package com.padyun.scripttools.services.biz;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;

import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.content.Constants;
import com.padyun.scripttools.biz.ui.data.StRespBaseObj;
import com.padyun.scripttools.compat.data.CoScript;
import com.padyun.scripttools.content.data.CustomScriptIOManager;
import com.padyun.scripttools.content.data.CustomScriptStorage;
import com.padyun.scripttools.content.data.FPathScript;
import com.padyun.scripttools.module.runtime.StContext;
import com.padyun.scripttoolscore.models.HttpParam;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.uls.utilites.content.CNet;
import com.uls.utilites.content.SProgressor;
import com.uls.utilites.exceptions.CoreCancellationException;
import com.uls.utilites.exceptions.RunOnUIThreadException;
import com.uls.utilites.io.Files;
import com.uls.utilites.io.ZipTool;
import com.uls.utilites.trash.BrandNames;
import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.Md5Util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.util.Consumer;
import okhttp3.Response;

/**
 * Created by daiepngfei on 2020-05-19
 */
@SuppressWarnings("unused")
public class UploadScriptService extends Service implements CNet.IState, Handler.Callback {

    @SuppressWarnings("unused")
    public static final String KEY_DATA = "DATA";

    public static final int UP_STATE_NONE = 0;
    public static final int UP_STATE_READY = 1;
    public static final int UP_STATE_LOADED = 2;
    public static final int UP_STATE_ZIPPING = 3;
    public static final int UP_STATE_UPLOADING = 4;
    public static final int UP_STATE_PUBLISHING = 5;
    public static final int UP_STATE_ERROR = -2;
    public static final int UP_STATE_CANCELLED = -1;
    public static final int UP_STATE_INTERRUPTION = -3;
    public static final int UP_STATE_COMPLETE = 6;

    public static final int STATE_NONE = 0;
    public static final int STATE_WAITTING = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_CANCELED = 3;
    public static final int STATE_INTERRUPTED = 4;
    public static final int STATE_REFUSED = 6;
    public static final int STATE_ERROR = 5;

    public static final int TYPE_NONE = 0;
    public static final int TYPE_RAW = 1;
    public static final int TYPE_PROCCESSED = 2;

    public static final int ACTION_READY = 1;
    public static final int ACTION_FAILED = -1;
    public static final int ACTION_DONE = 2;

    public static final int ERROR_UPLOADING = 1;
    public static final int ERROR_LOADING = 2;
    public static final int ERROR_ZIPPING = 3;
    public static final int ERROR_PUBLISHING = 4;
    public static final int ERROR_UNKNOWN = 0;

    public static final String KEY_STATE = "state";
    public static final String KEY_STATE_ERROR = "state_error";
    public static final String KEY_PUBLISHED_ID = "publishedID";
    public static final String KEY_UP_TYPE = "up_type";
    public static final String KEY_UP_PROGRESS = "progress";
    public static final String KEY_UP_STATE = "uploading_state";
    public static final String KEY_KEY = "key";
    public static final String KEY_ZIP_INFO = "zipping_info";
    public static final String KEY_MSG = "msg";
    public static final String KEY_UP_ACTION_STATE = "action_state";
    public static final String KEY_REMOTE_COMMANDS = "remote_commands";

    public static final String REMOTE_COMMANDS_STOP_CURRENT = "remote_commands_stop_current";
    public static final String REMOTE_COMMANDS_STOP_ALL = "remote_commands_stop_all";

    private Thread mUpWorkingThread = new Thread(() -> cloudWorking(this::cloudWorkingUpload, this::onUpFinsished));

    private LinkedBlockingQueue<Uploader> mUploadProxyQueue = new LinkedBlockingQueue<>();

    // making sure that this field can just be assigned by Upload-thread
    private volatile Uploader currentActivedUploader = null;
    // making sure that this field can just be assigned by UI thread
    private volatile boolean isCanncelAll = false;
    private Set<OnUploadingStateChangedListener> onUploadingStateChangedListeners = new HashSet<>();
    private ScriptUploadBinder mCloudBinder = new ScriptUploadBinder();
    private volatile boolean isUnbinded;

    /**
     * @param l
     */
    public void addUploadingStateChangeListener(OnUploadingStateChangedListener l) {
        if (l != null) {
            onUploadingStateChangedListeners.add(l);
        }
    }

    /**
     * @param l
     */
    public void removeUploadingStateChangeListener(OnUploadingStateChangedListener l) {
        if (l != null) {
            onUploadingStateChangedListeners.remove(l);
        }
    }

    /**
     * @param scriptFilePath
     * @param gameId
     * @param userToken
     */
    public void upload(String scriptFilePath, String gameId, String userToken) {
        upload(scriptFilePath, gameId, userToken, null);
    }

    /**
     * @param scriptFilePath
     * @param gameId
     * @param userToken
     * @param l
     */
    public void upload(String scriptFilePath, String gameId, String userToken, OnUploadingStateChangedListener l) {
        synchronized (this) {
            // error with invalid parameters
            if (!Useless.noEmptyStr(scriptFilePath, gameId, userToken)) {
                if (l != null) {
                    Intent intent = new Intent();
                    onKeySet(intent, scriptFilePath);
                    onUploadStateChanged(intent, UP_STATE_ERROR);
                    onStateError(intent, ERROR_UNKNOWN);
                    l.onUpStateChanged(intent);
                    removeUploadingStateChangeListener(l);
                }
                return;
            }

            // create a new uploader with parameters
            addUploadingStateChangeListener(l);
            final Uploader uploader = new Uploader(userToken, gameId, scriptFilePath);
            final Intent data = new Intent();
            data.putExtra(KEY_KEY, uploader.getKey());
            data.putExtra(KEY_STATE, STATE_NONE);
            // waiting in queue
            if (uploader.equals(currentActivedUploader)) { // is running on the working thread
                data.putExtra(KEY_STATE, STATE_RUNNING);
            } else { // is
                if (!mUploadProxyQueue.contains(uploader)) {
                    mUploadProxyQueue.offer(uploader);
                }
                data.putExtra(KEY_STATE, STATE_WAITTING);
            }
        }
    }

    public void cancel(String key) {
        synchronized (this) {
            if (key == null) {
                return;
            }
            final Uploader currentUploader = currentActivedUploader;
            if (currentUploader != null && key.equals(currentUploader.getKey())) {
                cancelCurrent();
                return;
            }
            Uploader target = null;
            for (Uploader uploader : mUploadProxyQueue) {
                if (uploader == null) {
                    continue;
                }
                if (key.equals(uploader.getKey())) {
                    target = uploader;
                    break;
                }
            }
            if (target != null) {
                mUploadProxyQueue.remove(target);
            }
        }
    }

    /**
     * @param scriptFilePath
     * @param queryCallback
     */
    public void queryState(String scriptFilePath, OnUploadingStateChangedListener queryCallback) {

        if (scriptFilePath == null || scriptFilePath.isEmpty() || queryCallback == null) {
            return;
        }

        final Uploader currentUploader = currentActivedUploader;
        if (currentUploader != null && scriptFilePath.equals(currentUploader.getKey())) {
            queryCallback.onUpStateChanged(currentUploader.getRunningInfo());
            return;
        }

        Intent data = new Intent();
        data.putExtra(KEY_KEY, scriptFilePath);
        data.putExtra(KEY_STATE, STATE_NONE);
        for (Uploader uploader : mUploadProxyQueue) {
            if (uploader == null) {
                continue;
            }
            if (scriptFilePath.equals(uploader.getKey())) {
                data.putExtra(KEY_STATE, STATE_WAITTING);
                break;
            }
        }

        queryCallback.onUpStateChanged(data);
    }

    private void onUpFinsished() {
        mUpWorkingThread = null;
        if (isUnbinded) {
            stopSelf();
        }
    }

    /**
     * @param data
     */
    private void notifyUploadStateChanged(Intent data) {
        if (data != null) {
            final Intent intent = new Intent(data);
            for (OnUploadingStateChangedListener l : onUploadingStateChangedListeners) {
                l.onUpStateChanged(intent);
            }
        }
    }

    @Override
    public void onNetStateChanged(CNet.State state) {
        // do nothing
    }


    @SuppressWarnings("unused")
    @UiThread
    public void setCancelAll() {
        synchronized (this) {
            if (mUpWorkingThread != null) {
                mUpWorkingThread.interrupt();
            }
            isCanncelAll = true;
            if (currentActivedUploader != null) {
                currentActivedUploader.setCanceled();
            }
        }
    }

    @SuppressWarnings("unused")
    @UiThread
    public void cancelCurrent() {
        synchronized (this) {
            if (currentActivedUploader != null) {
                currentActivedUploader.setCanceled();
                onStateChanged(currentActivedUploader.getRunningInfo(), STATE_CANCELED);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CNet.hook(getApplicationContext(), this);
        doStart();
    }


    private void doStart() {
        mUpWorkingThread.setName("UploadScriptService");
        mUpWorkingThread.start();
        //addUploadingStateChangeListener(new ForegroundUploadNotificationLisetener());
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        isUnbinded = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mUploadProxyQueue.isEmpty() && currentActivedUploader == null) {
            isUnbinded = true;
            stopWorkers();
            stopSelf();
        }
        return true;
    }

    private void stopWorkers() {
        if(mUpWorkingThread != null && mUpWorkingThread.isAlive()) {
            mUpWorkingThread.interrupt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CNet.unhook(this);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mCloudBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            final String commmand = intent.getStringExtra(KEY_REMOTE_COMMANDS);
            if (commmand != null && !commmand.isEmpty()) {
                if (commmand.equals(REMOTE_COMMANDS_STOP_CURRENT)) {
                    cancelCurrent();
                } else if (commmand.equals(REMOTE_COMMANDS_STOP_ALL)) {
                    setCancelAll();
                }
            }
        }

        return START_NOT_STICKY;
    }

    /**
     * @param work
     */
    private void cloudWorking(ICloudWork work, Runnable onLoopFinishing) {
        while (!isCanncelAll && !isUnbinded) {
            try {
                work.work();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (CoreCancellationException e) {
                e.printStackTrace();
            }
        }

        if (onLoopFinishing != null) {
            onLoopFinishing.run();
        }
    }

    /**
     *
     */
    public interface ICloudWork {
        void work() throws InterruptedException, CoreCancellationException;
    }

    /**
     * @throws InterruptedException
     */
    private void cloudWorkingUpload() throws InterruptedException, CoreCancellationException {
        Uploader uploader = null;
        try {
            /* --- FETCHING & LOADING --- */
            // notify state
            uploader = doFetching();
            keepCheckingRunState();

            setCurrentUploaderProxy(uploader);

            // loading
            keepCheckingRunState();
            doLoading(uploader);

            /* ---UP_STATE_ZIPPING --- */
            // compress(zip)
            keepCheckingRunState();
            doZippingProcessed(uploader);

            keepCheckingRunState();
            doZippingRaws(uploader);

            /* ---UP_STATE_UPLOADING--- */
            final Object waiter = new Object();
            onUploadStateChanged(uploader.getRunningInfo(), UP_STATE_UPLOADING);
            // uploading
            keepCheckingRunState();
            doUploadingProccessed(uploader, waiter);

            keepCheckingRunState();
            doUploadingRaws(uploader, waiter);

            // publishing
            keepCheckingRunState();
            doPublishing(uploader);

            // save script
            uploader.getRunningInfo().putExtra(KEY_UP_STATE, UP_STATE_NONE);
            onUploadStateChanged(uploader.getRunningInfo(), UP_STATE_COMPLETE);

        } catch (ZipFailedException e) {
            e.printStackTrace();
            if (uploader != null) {
                onStateError(uploader.getRunningInfo(), ERROR_ZIPPING);
            }
        } catch (LoadingException e) {
            e.printStackTrace();
            if (uploader != null) {
                Intent intent = uploader.getRunningInfo();
                if (intent != null) {
                    intent.putExtra(KEY_MSG, e.getMsg());
                    onStateError(intent, ERROR_LOADING);
                }
            }
        } catch (PublishingException e) {
            e.printStackTrace();
            if (uploader != null) {
                onStateError(uploader.getRunningInfo(), ERROR_PUBLISHING);
            }
        } catch (UploadTokenException | UploadException e) {
            e.printStackTrace();
            if (uploader != null) {
                onStateError(uploader.getRunningInfo(), ERROR_UPLOADING);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (uploader != null) {
                if (uploader.isCanceled() || isCanncelAll) {
                    onStateChanged(uploader.getRunningInfo(), STATE_CANCELED);
                } else {
                    onStateChanged(uploader.getRunningInfo(), STATE_INTERRUPTED);
                }
                // onUploadStateChanged(uploader.getRunningInfo(), UP_STATE_INTERRUPTION);
            }
            throw e;
        } catch (CoreCancellationException e) {
            e.printStackTrace();
            //onUploadStateChanged(uploader.getRunningInfo(), UP_STATE_CANCELLED);
            if (uploader != null) {
                onStateChanged(uploader.getRunningInfo(), STATE_CANCELED);
            }
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            if (uploader != null) {
                onStateError(uploader.getRunningInfo(), ERROR_UNKNOWN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            setCurrentUploaderProxy(null);
        }
    }

    /**
     * @param uploader
     */
    private void setCurrentUploaderProxy(Uploader uploader) {
        synchronized (this) {
            currentActivedUploader = uploader;
        }
    }

    /**
     * @throws InterruptedException
     */
    private void keepCheckingRunState() throws InterruptedException, CoreCancellationException {
        Useless.assertThreadInterruption();
        if (isCanncelAll) throw new CoreCancellationException();
    }

    /**
     * @param uploader
     */
    private void doPublishing(Uploader uploader) throws PublishingException {
        final Intent data = uploader.getRunningInfo();
        onUploadStateChanged(data, UP_STATE_PUBLISHING);
        try {
            uploader.publish();
        } catch (Exception e) {
            onActionState(data, ACTION_FAILED);
            throw e;
        }
        onActionState(data, ACTION_DONE);
        data.putExtra(KEY_PUBLISHED_ID, uploader.getPublishedId());
    }

    /**
     * @param uploader
     * @param waiter
     *
     * @throws UploadException
     * @throws UploadTokenException
     * @throws InterruptedException
     */
    private void doUploadingRaws(Uploader uploader, Object waiter) throws UploadException, UploadTokenException,
            InterruptedException, CoreCancellationException {
        final Intent data = uploader.getRunningInfo();
        onTypeSwitched(data, TYPE_RAW, true);
        synchronized (waiter) {
            try {
                uploader.setUploading(true);
                uploader.uploadRaw(
                        (key, percent) -> onProgress(data, (float) percent),
                        (key, info, response) -> {
                            synchronized (waiter) {
                                uploader.setResponseInfoRaw(info);
                                uploader.setUploading(false);
                                waiter.notifyAll();
                            }
                        },
                        () -> isCanncelAll || uploader.isCanceled()
                );
            } catch (Exception e) {
                uploader.setUploading(false);
                waiter.notifyAll();
                throw e;
            }
        }
        synchronized (waiter) {
            while (uploader.isUploading()) {
                waiter.wait();
            }
        }

        final ResponseInfo info = uploader.getResponseInfoRaw();
        handleResponseInfo(data, info);
    }

    /**
     * @param uploader
     * @param waiter
     *
     * @throws UploadException
     * @throws UploadTokenException
     * @throws InterruptedException
     */
    private void doUploadingProccessed(Uploader uploader, Object waiter) throws UploadException, UploadTokenException, InterruptedException, CoreCancellationException {
        final Intent data = uploader.getRunningInfo();
        onTypeSwitched(data, TYPE_PROCCESSED, true);
        synchronized (waiter) {
            uploader.setUploading(true);
            try {
                uploader.uploadProcessed(
                        (key, percent) -> onProgress(data, (float) percent), // the 'up-progress-handler'
                        (key, info, response) -> {
                            synchronized (waiter) {
                                uploader.setResponseInfoProcessed(info);
                                uploader.setUploading(false);
                                waiter.notifyAll();
                            }
                        },
                        () -> isCanncelAll || uploader.isCanceled()
                );
            } catch (Exception e) {
                uploader.setUploading(false);
                waiter.notifyAll();
                throw e;
            }
        }
        // waiting for upload finished
        synchronized (waiter) {
            while (uploader.isUploading()) {
                waiter.wait();
            }
        }


        // on action state changed
        final ResponseInfo info = uploader.getResponseInfoProcessed();
        handleResponseInfo(data, info);
    }

    /**
     * @param data
     * @param info
     *
     * @throws UploadException
     */
    private void handleResponseInfo(Intent data, ResponseInfo info) throws UploadException, CoreCancellationException {
        if (info.isOK() || info.statusCode == 614/*已经存在*/) {
            onActionState(data, ACTION_DONE);
        } else {
            onActionState(data, ACTION_FAILED);
            if (info.isCancelled()) {
                throw new CoreCancellationException();
            } else {
                throw new UploadException();
            }
        }
    }

    /**
     * @param uploader
     *
     * @throws InterruptedException
     * @throws ZipFailedException
     */
    private void doZippingRaws(Uploader uploader) throws InterruptedException, ZipFailedException, IOException, CoreCancellationException {
        // zip raw files
        final Intent data = uploader.getRunningInfo();
        onTypeSwitched(data, TYPE_RAW, true);

        data.putExtra(KEY_ZIP_INFO, "总大小： " + Useless.formatFileSizeBytes(uploader.getRawTotalSize()) +
                ", 共" + uploader.getRawFilesLength() + "个文件 ");

        if (!uploader.zipRaw(raw -> onProgress(data, raw))) {
            // notify state 'zipping RAW files failed'
            onActionState(data, ACTION_FAILED);
            throw new ZipFailedException();
        }
        onActionState(data, ACTION_DONE);
    }

    /**
     * @param uploader
     *
     * @throws InterruptedException
     * @throws ZipFailedException
     */
    private void doZippingProcessed(Uploader uploader) throws InterruptedException, ZipFailedException, CoreCancellationException {
        // zip processed files
        final Intent data = uploader.getRunningInfo();
        onTypeSwitched(data, TYPE_PROCCESSED, true);
        onUploadStateChanged(data, UP_STATE_ZIPPING);
        data.putExtra(KEY_ZIP_INFO, "总大小： " + Useless.formatFileSizeBytes(uploader.getProcessedTotoalSize()) +
                ", 共" + uploader.getProcessedFilesLength() + "个文件 ");
        if (!uploader.zipProcessed(processed -> onProgress(data, processed))) {
            // notify state 'zipping PROCESSED files failed'
            onActionState(data, ACTION_FAILED);
            throw new ZipFailedException();
        }
        // notify state
        onActionState(data, ACTION_DONE);
    }

    /**
     * @param uploader
     *
     * @return
     */
    private void doLoading(Uploader uploader) throws LoadingException {
        uploader.load();
        onUploadStateChanged(uploader.getRunningInfo(), UP_STATE_LOADED);
    }

    @NotNull
    private Uploader doFetching() throws Exception {
        final Uploader uploader = mUploadProxyQueue.take();
        Intent data = new Intent();
        uploader.setRunningInfo(data);
        onKeySet(data, uploader.getKey());
        onStateChanged(data, STATE_RUNNING);
        onUploadStateChanged(data, UP_STATE_READY);
        return uploader;
    }

    /**
     *
     */
    public interface OnUploadingStateChangedListener {
        void onUpStateChanged(Intent data);
    }


    /**
     * @param data
     * @param state
     */
    public void onStateChanged(Intent data, int state) {
        data.putExtra(KEY_STATE, state);
        onUpdateState(data);
    }

    /**
     * @param data
     */
    public void onStateError(Intent data, int error) {
        data.putExtra(KEY_STATE, STATE_ERROR);
        data.putExtra(KEY_STATE_ERROR, error);
        onUpdateState(data);
    }

    /**
     * @param data
     * @param state
     */
    public void onUploadStateChanged(Intent data, int state) {
        data.putExtra(KEY_UP_STATE, state);
        onUpdateState(data);
    }

    /**
     * @param data
     * @param type
     */
    public void onTypeSwitched(Intent data, int type, boolean resetProgressAndActionState) {
        data.putExtra(KEY_UP_TYPE, type);
        if (resetProgressAndActionState) {
            data.putExtra(KEY_UP_ACTION_STATE, ACTION_READY);
            data.putExtra(KEY_UP_PROGRESS, 0F);
        }
        onUpdateState(data);
    }

    /**
     * @param data
     * @param actionState
     */
    public void onActionState(Intent data, int actionState) {
        data.putExtra(KEY_UP_ACTION_STATE, actionState);
        onUpdateState(data);
    }

    /**
     * @param data
     * @param key
     */
    public void onKeySet(Intent data, String key) {
        data.putExtra(KEY_KEY, key);
    }

    /**
     * @param data
     * @param progress
     */
    public void onProgress(Intent data, float progress) {
        data.putExtra(KEY_UP_PROGRESS, progress);
        onUpdateState(data);
    }


    /**
     * @param data
     */
    private void onUpdateState(Intent data) {
        notifyUploadStateChanged(data);

    }


    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    /**
     *
     */
    public final class ScriptUploadBinder extends Binder {

        /**
         * @return
         */
        @SuppressWarnings("unused")
        public UploadScriptService getUploaderService() {
            return UploadScriptService.this;
        }
    }


    @SuppressWarnings("all")
    private final class ForegroundUploadNotificationLisetener implements OnUploadingStateChangedListener {

        private RemoteViews mRemoteViews;
        private Notification notification;
        private NotificationCompat.Builder cBuilder;

        private void startForgroundNotificationV2() {
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final String channelId = "uploadingservice";
                NotificationChannel chan = new NotificationChannel(channelId, "uploading", NotificationManager.IMPORTANCE_DEFAULT);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert manager != null;
                manager.createNotificationChannel(chan);
                builder = new NotificationCompat.Builder(UploadScriptService.this, channelId);
            } else {
                builder = new NotificationCompat.Builder(UploadScriptService.this);
            }

            cBuilder = builder;
            builder.setOngoing(true);
            builder.setSmallIcon(R.drawable.ic_upload_notification_drawable_small);
            builder.setProgress(10000, 2000, false);
            builder.setContentTitle("hello, 你好");
            builder.setContentText("好你妈!");
            startForeground(1, notification);


        }

        /**
         *
         */
        private void startForgroundNotification() {
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final String channelId = "uploadingservice";
                NotificationChannel chan = new NotificationChannel(channelId, "uploading", NotificationManager.IMPORTANCE_DEFAULT);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert manager != null;
                manager.createNotificationChannel(chan);
                builder = new NotificationCompat.Builder(UploadScriptService.this, channelId);
            } else {
                builder = new NotificationCompat.Builder(UploadScriptService.this);
            }

            builder.setOngoing(true);
            builder.setSmallIcon(R.drawable.ic_upload_notification_drawable_small);
            final RemoteViews remoteView = new RemoteViews(getPackageName(), BrandNames.isBrandOf(BrandNames.SMARTISAN) ?
                    R.layout.notification_progress_fxxking_smartison : R.layout.notification_progress);

            builder.setCustomBigContentView(remoteView);
            mRemoteViews = remoteView;
            mRemoteViews.setTextViewText(R.id.progressTitle, "正在压缩，请勿后台关闭云派应用");
            mRemoteViews.setTextViewText(R.id.button1, "取消当前任务");
            mRemoteViews.setTextViewText(R.id.button2, "取消全部任务");
            mRemoteViews.setViewVisibility(R.id.buttonLayout, View.VISIBLE);
            mRemoteViews.setViewVisibility(R.id.button1, View.VISIBLE);
            mRemoteViews.setViewVisibility(R.id.button2, View.VISIBLE);

            Intent intent1 = new Intent(getApplicationContext(), UploadScriptService.class);
            intent1.putExtra(KEY_REMOTE_COMMANDS, REMOTE_COMMANDS_STOP_CURRENT);
            PendingIntent pendingIntent1 = PendingIntent.getService(getApplicationContext(), 0, intent1, 0);
            mRemoteViews.setOnClickPendingIntent(R.id.button1, pendingIntent1);

            Intent intent2 = new Intent(getApplicationContext(), UploadScriptService.class);
            intent2.putExtra(KEY_REMOTE_COMMANDS, REMOTE_COMMANDS_STOP_ALL);
            PendingIntent pendingIntent2 = PendingIntent.getService(getApplicationContext(), 0, intent2, 0);
            mRemoteViews.setOnClickPendingIntent(R.id.button2, pendingIntent2);

            notification = builder.build();

            startForeground(1, notification);
        }

        private void stopForegroundNotification() {
            stopForeground(true);
        }

        @Override
        public void onUpStateChanged(Intent data) {
            onUpDataChangedV2(data);
        }

        private void onUpDataChangedV2(Intent data) {
            final int state = data.getIntExtra(KEY_STATE, STATE_NONE);
            final int upState = data.getIntExtra(KEY_UP_STATE, UP_STATE_NONE);
            final int type = data.getIntExtra(KEY_UP_TYPE, TYPE_NONE);
            final float progress = data.getFloatExtra(KEY_UP_PROGRESS, 0F);
            final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            switch (state) {
                case STATE_RUNNING:
                    if (upState == UP_STATE_READY) {
                        startForgroundNotificationV2();
                    } else if (upState == UP_STATE_NONE) {
                        stopForegroundNotification();
                    } else if (upState == UP_STATE_ZIPPING) {
                        //mRemoteViews.setTextViewText(R.id.progressTitle, "正在压缩，请勿后台关闭云派应用");
                        //mRemoteViews.setTextViewText(R.id.progressSubTitle, type == TYPE_PROCCESSED ? "数据文件包" : "资源文件包");
                        cBuilder.setProgress(10000, (int) (10000 * progress), false);
                        //mRemoteViews.setTextViewText(R.id.progressInfo, ((int) (progress * 100) + "%"));

                        notificationManagerCompat.notify(1, cBuilder.build());
                    } /*else if (upState == UP_STATE_UPLOADING) {
                        mRemoteViews.setTextViewText(R.id.progressTitle, "正在上传，请勿后台关闭云派应用");
                        mRemoteViews.setTextViewText(R.id.progressSubTitle, type == TYPE_PROCCESSED ? "数据文件包" : "资源文件包");
                        mRemoteViews.setProgressBar(R.id.progressBar, 10000, (int) (10000 * progress), false);
                        mRemoteViews.setTextViewText(R.id.progressInfo, ((int) (progress * 100) + "%"));
                        notificationManagerCompat.notify(1, notification);
                    } else if (upState == UP_STATE_ERROR) {
                        mRemoteViews.setTextViewText(R.id.progressSubTitle, "上传出错，请稍候重试");
                        notificationManagerCompat.notify(1, notification);
                    }*/ else if (upState == UP_STATE_COMPLETE) {
                        stopForegroundNotification();
                    }
                    break;
                case STATE_ERROR:
                    mRemoteViews.setTextViewText(R.id.progressSubTitle, "上传出错，请稍候重试");
                    notificationManagerCompat.notify(1, notification);
                    break;
                case STATE_CANCELED:
                    stopForegroundNotification();
                    break;
                case STATE_INTERRUPTED:
                    stopForegroundNotification();
                    break;
                default:
            }
        }

        private void onUpDataChangedV1(Intent data) {
            final int state = data.getIntExtra(KEY_STATE, STATE_NONE);
            final int upState = data.getIntExtra(KEY_UP_STATE, UP_STATE_NONE);
            final int type = data.getIntExtra(KEY_UP_TYPE, TYPE_NONE);
            final float progress = data.getFloatExtra(KEY_UP_PROGRESS, 0F);
            final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
            switch (state) {
                case STATE_RUNNING:
                    if (upState == UP_STATE_READY) {
                        startForgroundNotification();
                        mRemoteViews.setTextViewText(R.id.progressTitle, "准备上传中");
                        notificationManagerCompat.notify(1, notification);
                    } else if (upState == UP_STATE_NONE) {
                        stopForegroundNotification();
                    } else if (upState == UP_STATE_ZIPPING) {
                        mRemoteViews.setTextViewText(R.id.progressTitle, "正在压缩，请勿后台关闭云派应用");
                        mRemoteViews.setTextViewText(R.id.progressSubTitle, type == TYPE_PROCCESSED ? "数据文件包" : "资源文件包");
                        mRemoteViews.setProgressBar(R.id.progressBar, 10000, (int) (10000 * progress), false);
                        mRemoteViews.setTextViewText(R.id.progressInfo, ((int) (progress * 100) + "%"));

                        notificationManagerCompat.notify(1, notification);
                    } else if (upState == UP_STATE_UPLOADING) {
                        mRemoteViews.setTextViewText(R.id.progressTitle, "正在上传，请勿后台关闭云派应用");
                        mRemoteViews.setTextViewText(R.id.progressSubTitle, type == TYPE_PROCCESSED ? "数据文件包" : "资源文件包");
                        mRemoteViews.setProgressBar(R.id.progressBar, 10000, (int) (10000 * progress), false);
                        mRemoteViews.setTextViewText(R.id.progressInfo, ((int) (progress * 100) + "%"));
                        notificationManagerCompat.notify(1, notification);
                    } else if (upState == UP_STATE_ERROR) {
                        mRemoteViews.setTextViewText(R.id.progressSubTitle, "上传出错，请稍候重试");
                        notificationManagerCompat.notify(1, notification);
                    } else if (upState == UP_STATE_COMPLETE) {
                        stopForegroundNotification();
                    }
                    break;
                case STATE_ERROR:
                    mRemoteViews.setTextViewText(R.id.progressSubTitle, "上传出错，请稍候重试");
                    notificationManagerCompat.notify(1, notification);
                    break;
                case STATE_CANCELED:
                    stopForegroundNotification();
                    break;
                case STATE_INTERRUPTED:
                    stopForegroundNotification();
                    break;
                default:
            }
        }
    }
}

/**
 *
 */
@SuppressWarnings("WeakerAccess")
final class Uploader {
    private Intent runningInfo;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String scriptFilePath, userToken, gameId;
    private long rawTotalSize = 0;
    private long processedTotoalSize = 0;
    private CoScript script;
    private String ID = null;
    private File[] rawFiles, processedFiles;
    private String rawZipPath, processedZipPath;
    private File rawZip, processedZip;
    private volatile CancellationSignal cancellationSignal = new CancellationSignal();
    private volatile boolean isUploading;
    private ResponseInfo responseInfoRaw, responseInfoProcessed;
    private UploadTokenEntity processedEntity, rawEntity;

    ResponseInfo getResponseInfoRaw() {
        return responseInfoRaw;
    }

    @SuppressWarnings("unused")
    public void setResponseInfoRaw(ResponseInfo responseInfoRaw) {
        this.responseInfoRaw = responseInfoRaw;
    }

    ResponseInfo getResponseInfoProcessed() {
        return responseInfoProcessed;
    }

    void setResponseInfoProcessed(ResponseInfo responseInfoProcessed) {
        this.responseInfoProcessed = responseInfoProcessed;
    }

    void setUploading(boolean uploading) {
        this.isUploading = uploading;
    }

    boolean isCanceled() {
        return cancellationSignal.isCanceled();
    }

    void setCanceled() {
        cancellationSignal.cancel();
    }

    boolean isUploading() {
        return isUploading;
    }

    public String getKey() {
        return scriptFilePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Uploader) {
            Uploader objUploader = (Uploader) obj;
            return objUploader.getKey().equals(getKey());
        }
        return false;
    }

    Uploader(String userToken, String gameId, String scriptPath) {
        assert userToken != null;
        assert gameId != null;
        assert scriptPath != null;
        this.userToken = userToken;
        this.gameId = gameId;
        this.scriptFilePath = scriptPath;
    }

    void load() throws LoadingException {
        if (ID == null) {
            ID = Md5Util.file(new File(scriptFilePath));
            if (Useless.isEmpty(ID)) {
                throw new LoadingException(LoadingException.ERR_EMPTY_FILE, "文件已损坏或者丢失，请删除后重新创建或下载");
            }

            script = CustomScriptIOManager.getCoScriptFromFileSync(scriptFilePath);
            if (script == null || Useless.isEmpty(script.getConditions())) {
                throw new LoadingException(LoadingException.ERR_EMPTY_CONDS, "当前可执行内容为空，请编辑后再重新上传");
            }

            if (!Useless.isEmpty(ID) && script != null) {
                final Set<File> raws = new HashSet<>();
                final Set<File> processeds = new HashSet<>();
                final File scriptFile = new File(scriptFilePath);
                processeds.add(scriptFile);
                processedTotoalSize += scriptFile.length();
                final StringBuilder rawSbBuilder = new StringBuilder();
                Useless.foreach(script.getAllSEImageDeDuplicated(), image -> {
                    File raw = new File(image.getImageOriginal());
                    if (raw.exists() && raw.length() > 0) {
                        raws.add(raw);
                        rawSbBuilder.append(raw.getName());
                        rawTotalSize += raw.length();
                    }
                    File f = new File(image.getImageCropPath());
                    if (f.exists() && f.length() > 0) {
                        processeds.add(f);
                        processedTotoalSize += f.length();
                    }
                });

                if (this.rawFiles == null) {
                    this.rawFiles = new File[raws.size()];
                    raws.toArray(this.rawFiles);
                }

                if (this.processedFiles == null) {
                    this.processedFiles = new File[processeds.size()];
                    processeds.toArray(this.processedFiles);
                }

                rawZipPath = FPathScript.tmpFilePath(ID + "-" + Md5Util.string(rawSbBuilder.toString()) + "-RAW");
                processedZipPath = FPathScript.tmpFilePath(ID + "-PROCESSED");

            }
        }

    }

    public long getRawTotalSize() {
        return rawTotalSize;
    }

    public long getProcessedTotoalSize() {
        return processedTotoalSize;
    }

    public int getProcessedFilesLength() {
        return processedFiles == null ? 0 : processedFiles.length;
    }

    public int getRawFilesLength() {
        return rawFiles == null ? 0 : rawFiles.length;
    }


    /**
     * @param processedProgressor
     *
     * @return
     *
     * @throws InterruptedException
     */
    boolean zipProcessed(Consumer<Float> processedProgressor) throws InterruptedException, CoreCancellationException {
        final String tag = "总大小： " + Useless.formatFileSizeBytes(getProcessedTotoalSize()) + ", 共" + processedFiles.length + "个文件： ";
        try {
            if (zip(processedZipPath, processedFiles, getProcessedTotoalSize(), tag, processedProgressor)) {
                processedZip = new File(processedZipPath);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @return
     *
     * @throws InterruptedException
     */
    boolean zipRaw(Consumer<Float> rawProgressor) throws InterruptedException, IOException, CoreCancellationException {
        final File zipFile = new File(rawZipPath);
        final String tag = "总大小： " + Useless.formatFileSizeBytes(getRawTotalSize()) + ", 共" + rawFiles.length + "个文件： ";
        if (zip(rawZipPath, rawFiles, getRawTotalSize(), tag, rawProgressor)) {
            rawZip = zipFile;
            return true;
        }
        return false;
    }

    /**
     * @param zipPath
     * @param maxProgress
     * @param rawProgressor
     *
     * @return
     *
     * @throws InterruptedException
     */
    private boolean zip(final String zipPath, File[] files, long maxProgress, String progressTag, Consumer<Float> rawProgressor) throws InterruptedException, IOException, CoreCancellationException {
        try {
            return !Useless.isEmpty(ID) && ZipTool.zip(files, zipPath, cancellationSignal,
                    new SProgressor(maxProgress, 50, progressTag, rawProgressor));
        } catch (RunOnUIThreadException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param upProgressHandler
     * @param upCompletionHandler
     * @param signal
     *
     * @throws UploadException
     * @throws UploadTokenException
     */
    void uploadRaw(UpProgressHandler upProgressHandler, UpCompletionHandler upCompletionHandler, UpCancellationSignal signal) throws UploadException, UploadTokenException {
        final UploadTokenEntity entity = getUploadToken();
        isUploading = true;
        if (entity == null) {
            isUploading = false;
            throw new UploadTokenException(null);
        }
        rawEntity = entity;
        final String upKay = entity.getUploadScriptResourcePath() + entity.getFilename() + ".zip";
        doUploadingWork(rawZip, upKay, entity.getToken(), upProgressHandler, upCompletionHandler, signal);
    }

    /**
     * @param upProgressHandler
     * @param upCompletionHandler
     * @param signal
     *
     * @throws UploadException
     * @throws UploadTokenException
     */
    void uploadProcessed(UpProgressHandler upProgressHandler, UpCompletionHandler upCompletionHandler, UpCancellationSignal signal) throws UploadException, UploadTokenException {
        isUploading = true;
        final UploadTokenEntity entity = getUploadToken();
        if (entity == null) {
            isUploading = false;
            throw new UploadTokenException(null);
        }
        processedEntity = entity;
        final String upKay = entity.getUpalodScriptDataPath() + entity.getFilename() + ".zip";
        doUploadingWork(processedZip, upKay, entity.getToken(), upProgressHandler, upCompletionHandler, signal);
    }


    /**
     * @param f
     * @param upProgressHandler
     * @param upCompletionHandler
     * @param signal
     */
    private void doUploadingWork(File f, String upKey, String token, UpProgressHandler upProgressHandler,
                                 UpCompletionHandler upCompletionHandler, UpCancellationSignal signal) throws UploadException {
        final UploadManager uploadManager;
        try {
            uploadManager = new UploadManager(new Configuration.Builder()
                    .recorder(new FileRecorder(f.getParent())/*, (key, file) -> key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse()*/)
                    .build()
            );
            uploadManager.put(f, upKey, token, upCompletionHandler, new UploadOptions(null, null, true, upProgressHandler, signal));
        } catch (IOException e) {
            e.printStackTrace();
            throw new UploadException();
        }
    }

    /**
     * @return
     */
    private UploadTokenEntity getUploadToken() throws UploadTokenException {
        UploadTokenEntity token = null;
        try {
            final Response response = StContext.network()
                    .http().getSync(Constants.ApiPath.Phrase.QINIU_TOKEN, new HttpParam("gameid", gameId));
            final StRespBaseObj uploadTokenEntityYpRespJsonWpObj = StRespBaseObj.parse(response);
            if (uploadTokenEntityYpRespJsonWpObj != null) {
                token = uploadTokenEntityYpRespJsonWpObj.dataAsObject(UploadTokenEntity.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new UploadTokenException(e);
        }
        return token;
    }

    public static class PublishedInfo {
        String id;

        public PublishedInfo(String id) {
            this.id = id;
        }
    }

    /**
     * @throws PublishingException
     */
    void publish() throws PublishingException {
        String errorMsg = null;
        int errorCode = -1;
        if (responseInfoProcessed == null || responseInfoRaw == null) {
            //noinspection ConstantConditions
            throw new PublishingException(null, errorCode, errorMsg);
        }

        try {
            final Response response = StContext.network()
                    .http().postSync(Constants.ApiPath.Phrase.CREATE_ADD_TASK,
                            new HttpParam("version_name", String.valueOf(script.getPublishingCode())),
                            new HttpParam("package", processedEntity.getScriptDataHttpAddress() + ".zip"),
                            new HttpParam("pic", rawEntity.getScriptResourcesHttpAddress() + ".zip"),
                            new HttpParam("name", String.valueOf(Files.nameSfx(scriptFilePath))),
                            new HttpParam("game_id", gameId),
                            new HttpParam("taskid", script.getPublishedID())
                    );

            final StRespBaseObj obj = StRespBaseObj.parse(response);
            if (obj != null) {
                final PublishedInfo uploadTokenEntity = obj.dataAsObject(PublishedInfo.class);
                if (uploadTokenEntity != null) {
                    script.setPublishedID(uploadTokenEntity.id);
                    getRunningInfo().putExtra(UploadScriptService.KEY_PUBLISHED_ID, uploadTokenEntity.id);
                    CustomScriptStorage.saveSync(scriptFilePath, script);
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new PublishingException(null, errorCode, errorMsg);

        }
        throw new PublishingException(null, errorCode, errorMsg);
    }

    public Intent getRunningInfo() {
        return runningInfo;
    }

    public void setRunningInfo(Intent runningInfo) {
        this.runningInfo = runningInfo;
    }

    public String getPublishedId() {
        return script.getPublishedID();
    }
}

@SuppressWarnings({"unused", "WeakerAccess"})
final class UploadTokenEntity {

    /**
     * token : C5HCJbg99qgAV2bErswS6sfHTjHRBCbAyQMRf4lP:U7t948yIQkuGkwTJ-aZPHggOxo4=:eyJzY29wZSI6InlwYWktaGVscGVyIiwiZGVhZGxpbmUiOjE1OTAzNzc1NjZ9
     * filename : f8ba7779114badb5c8ad5c5bcab9e594-1590373966 picpath : user_task/pic/MTE=/ packpath
     * : user_task/script/MTE=/
     */
    private String domain;
    private String token;
    private String filename;
    private String picpath; // 资源大图
    private String packpath; // 脚本小图

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }


    String getScriptDataHttpAddress() {
        return domain + "/" + packpath + filename;
    }

    String getScriptResourcesHttpAddress() {
        return domain + "/" + picpath + filename;
    }

    String getUpalodScriptDataPath() {
        return packpath;
    }

    String getUploadScriptResourcePath() {
        return picpath;
    }

    String getUploadFileName() {
        return filename;
    }

    public void setPackpath(String packpath) {
        this.packpath = packpath;
    }
}


final class ZipFailedException extends Exception {
}

@SuppressWarnings({"WeakerAccess", "unused", "UnusedAssignment"})
final class LoadingException extends Exception {
    final static int ERR_EMPTY_FILE = -1;
    final static int ERR_EMPTY_CONDS = -2;
    final static int ERR_EMPTY_NONE = -100;
    private int err = ERR_EMPTY_NONE;
    private String msg = "加载失败，请稍候重试";

    public int getErr() {
        return err;
    }

    public String getMsg() {
        return msg;
    }

    public LoadingException(int err, String msg) {
        this.err = err;
        this.msg = msg;
    }
}

final class UploadException extends Exception {
}

@SuppressWarnings("WeakerAccess")
final class UploadTokenException extends Exception {
    public UploadTokenException(Throwable e) {
        if (e != null) {
            addSuppressed(e);
        }
    }
}

@SuppressWarnings("unused")
final class PublishingException extends Exception {
    private String msg;
    private int code;

    PublishingException(Throwable e, int code, String msg) {
        this.code = code;
        this.msg = msg;
        if (e != null) {
            addSuppressed(e);
        }
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}