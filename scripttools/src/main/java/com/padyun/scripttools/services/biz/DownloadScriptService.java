package com.padyun.scripttools.services.biz;


import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import com.padyun.scripttools.biz.ui.content.Constants;
import com.padyun.scripttools.biz.ui.data.StRespBaseObj;
import com.padyun.scripttools.compat.data.CoScript;
import com.padyun.scripttools.content.data.CustomScriptIOManager;
import com.padyun.scripttools.content.data.CustomScriptStorage;
import com.padyun.scripttools.module.runtime.ScriptManager;
import com.padyun.scripttools.module.runtime.StContext;
import com.padyun.scripttoolscore.models.HttpParam;
import com.uls.utilites.content.SProgressor;
import com.uls.utilites.exceptions.CoreCancellationException;
import com.uls.utilites.io.Files;
import com.uls.utilites.io.UnzipTool;
import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.CancellationSignal;
import androidx.core.util.Consumer;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by daiepngfei on 2020-06-01
 */
public class DownloadScriptService extends Service {
    private static final String TAG = DownloadScriptService.class.getSimpleName();
    private DownloadBinder mBinder = new DownloadBinder();

    public static final int STATE_NONE = 0;
    public static final int STATE_WAITING = 1;
    public static final int STATE_DOWNLOADING = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_CANCELED = 4;
    public static final int STATE_INTERRUPTED = 6;
    public static final int STATE_COMPLETE = 5;

    public static final int DOWN_STATE_IDLE = 1;
    public static final int DOWN_STATE_FETCHING = 2;
    public static final int DOWN_STATE_DOWNLOADING = 3;
    public static final int DOWN_STATE_UNZIPPING = 4;
    public static final int DOWN_STATE_OVER = 5;

    public static final int ERROR_UNKNOWN = -100;
    public static final int ERROR_ADDRESS_FAILED = -1;
    public static final int ERROR_DOWNLOADING = -2;
    public static final int ERROR_JSON = -3;
    public static final int ERROR_IO = -4;

    public static final int TYPE_RAW = 0;
    public static final int TYPE_P = 1;

    public static final int ACTION_STATE_NONE = 0;
    public static final int ACTION_STATE_FAILED = 1;
    public static final int ACTION_STATE_DONE = 2;

    public static final String KEY_STATE = "state";
    public static final String KEY_ERROR_CODE = "error_code";
    public static final String KEY_DOWN_STATE = "down_state";
    public static final String KEY_TYPE = "type";
    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_GAMEID = "gameId";
    public static final String KEY_TASKID = "taskId";
    public static final String KEY_ACTION_STATE = "action_state";

    @VolatileFieldSetOnUIThread
    private volatile boolean isCancelAll = false;
    @VolatileFieldSetOnUIThread
    private volatile boolean isUnbinded = false;
    @VolatileFieldSetOnUIThread
    private volatile Thread mDownloadThread = null;

    private volatile DownloadInfo mCurrentDownloadInfo = null;
    private volatile Call mCurrentDownloadCall = null;

    private BlockingQueue<DownloadInfo> mDownloadInfos = new LinkedBlockingQueue<>();
    private Set<OnCachingRemoteScriptListener> onCachingRemoteScriptListeners = new HashSet<>();

    public void download(String gameId, String taskId, String downloadPath, String userToken, String unzipDirRaw,
                         String unzipDirP, String unzipDirSce, OnCachingRemoteScriptListener l) {
        if (isCancelAll || isUnbinded) {
            return;
        }
        final DownloadInfo downloadInfo = new DownloadInfo(taskId, gameId, userToken, downloadPath, unzipDirRaw, unzipDirP, unzipDirSce);
        synchronized (this) {
            if (downloadInfo.equals(mCurrentDownloadInfo)) {
                addOnCachingRemoteScriptListeners(l);
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(KEY_GAMEID, gameId);
            intent.putExtra(KEY_TASKID, taskId);
            if (mDownloadInfos.contains(downloadInfo)) {
                intent.putExtra(KEY_STATE, STATE_WAITING);
                addOnCachingRemoteScriptListeners(l);
                notifyCachingState(intent);
                return;
            }
            addOnCachingRemoteScriptListeners(l);
            intent.putExtra(KEY_STATE, STATE_NONE);
            mDownloadInfos.add(downloadInfo);
            intent.putExtra(KEY_STATE, STATE_WAITING);
            notifyCachingState(intent);
        }
    }

    private void notifyCachingState(Intent data) {
        if (data != null) {
            synchronized (this) {
                for (OnCachingRemoteScriptListener l : onCachingRemoteScriptListeners) {
                    if (l != null) {
                        l.onCachingRemoteScript(new Intent(data));
                    }
                }
            }
        }
    }

    public void addOnCachingRemoteScriptListeners(OnCachingRemoteScriptListener l) {
        if (l != null) {
            synchronized (this) {
                onCachingRemoteScriptListeners.add(l);
            }
        }
    }

    public void removeOnCachingRemoteScriptListeners(OnCachingRemoteScriptListener l) {
        if (l != null) {
            synchronized (this) {
                onCachingRemoteScriptListeners.remove(l);
            }
        }
    }

    public interface OnCachingRemoteScriptListener {
        void onCachingRemoteScript(Intent data);
    }

    public static class DownloadInfo implements Parcelable {

        private String taskId;
        private String gameId;
        private String userToken;
        private String downloadDir;
        private String unzipDirRaws, unzipDirProccessed, unzipDirScript;

        private transient CancellationSignal cancellationSignal = new CancellationSignal();
        private Intent mRunningInfo = new Intent();

        public DownloadInfo(String taskId, String gameId, String userToken, String downloadPath, String unzipDir, String unzipDirProccessed, String unzipDirScript) {
            this.taskId = taskId;
            this.gameId = gameId;
            this.userToken = userToken;
            this.downloadDir = downloadPath;
            this.unzipDirRaws = unzipDir;
            this.unzipDirProccessed = unzipDirProccessed;
            this.unzipDirScript = unzipDirScript;
            mRunningInfo.putExtra(KEY_GAMEID, gameId);
            mRunningInfo.putExtra(KEY_TASKID, taskId);
        }

        @Override
        public boolean equals(Object downloadInfo) {
            return downloadInfo instanceof DownloadInfo
                    && Useless.equals(((DownloadInfo) downloadInfo).taskId, taskId)
                    && Useless.equals(((DownloadInfo) downloadInfo).gameId, gameId);
        }

        CancellationSignal getCancellationSignal() {
            return cancellationSignal;
        }

        void setCanceled() {
            cancellationSignal.cancel();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.taskId);
            dest.writeString(this.gameId);
            dest.writeString(this.userToken);
            dest.writeString(this.downloadDir);
            dest.writeString(this.unzipDirRaws);
        }

        protected DownloadInfo(Parcel in) {
            this.taskId = in.readString();
            this.gameId = in.readString();
            this.userToken = in.readString();
            this.downloadDir = in.readString();
            this.unzipDirRaws = in.readString();
        }

        public static final Creator<DownloadInfo> CREATOR = new Creator<DownloadInfo>() {
            @Override
            public DownloadInfo createFromParcel(Parcel source) {
                return new DownloadInfo(source);
            }

            @Override
            public DownloadInfo[] newArray(int size) {
                return new DownloadInfo[size];
            }
        };

        public boolean isCanceled() {
            return cancellationSignal.isCanceled();
        }

        public Intent getRunningInfo() {
            return mRunningInfo;
        }
    }

    /**
     *
     */
    public void cancelCurrent() {
        synchronized (this) {
            if (mCurrentDownloadInfo != null) {
                mCurrentDownloadInfo.setCanceled();
            }
        }
    }

    @VolatileFieldSetOnUIThread
    public void cancelAll() {
        synchronized (this) {
            if (mCurrentDownloadCall != null) {
                mCurrentDownloadCall.cancel();
            }
            isCancelAll = true;
            if (mDownloadThread != null) {
                mDownloadThread.interrupt();
            }
            stopSelf();
        }
    }


    @VolatileFieldSetOnUIThread
    public void stopCurrent() {
        if (!Useless.isOnUiThread()) {
            throw new IllegalThreadStateException();
        }
        if (mCurrentDownloadInfo != null) {
            mCurrentDownloadInfo.setCanceled();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void onTypeChanged(@NonNull Intent data, int type) {
        data.putExtra(KEY_TYPE, type);
        data.putExtra(KEY_ACTION_STATE, ACTION_STATE_NONE);
        data.putExtra(KEY_PROGRESS, 0F);
    }

    private void onActionStateChanged(@NonNull Intent data, int actionState) {
        data.putExtra(KEY_ACTION_STATE, actionState);
    }

    private void onStateChange(@NonNull Intent data, int state) {
        data.putExtra(KEY_STATE, state);
        notifyCachingState(data);
    }

    private void onDownStateChange(@NonNull Intent data, int state) {
        data.putExtra(KEY_DOWN_STATE, state);
    }

    private void onProgress(Intent data, float progress) {
        if (data != null) {
            data.putExtra(KEY_PROGRESS, progress);
            notifyCachingState(data);
        }
    }


    private void onDownloading() {

        while (!isCancelAll) {

            DownloadInfo downloader = null;
            try {

                downloader = mDownloadInfos.take();
                mCurrentDownloadInfo = downloader;

                final Intent data = downloader.mRunningInfo;
                onStateChange(data, STATE_DOWNLOADING);
                onDownStateChange(data, DOWN_STATE_FETCHING);
                notifyCachingState(data);

                // request download info
                keepCheckingCancellation();
                final Response response = StContext.network().http().getSync(Constants.ApiPath.Phrase.GET_RESOUCES_WITH_TASK_ID, new HttpParam("task_id", downloader.taskId));
                if (!response.isSuccessful() || response.body() == null) {
                    onActionStateChanged(data, ACTION_STATE_FAILED);
                    onStateError(data, ERROR_ADDRESS_FAILED);
                    continue;
                }

                //noinspection ConstantConditions 上面判断了 rz ide
                final StRespBaseObj obj = StRespBaseObj.parse(response);
                if (obj == null) {
                    onActionStateChanged(data, ACTION_STATE_FAILED);
                    onStateError(data, ERROR_ADDRESS_FAILED);
                    continue;
                }

                if (obj.getCode() > 0) {
                    onActionStateChanged(data, ACTION_STATE_FAILED);
                    onStateError(data, ERROR_ADDRESS_FAILED);
                    LogUtil.d(TAG, obj.getMsg());
                    continue;
                }

                final JSONObject dataObj = new JSONObject(obj.getDataString());
                final String downloadRawUrl = dataObj.getString("pic");
                final String downloadProccessedUrl = dataObj.getString("package");

                onActionStateChanged(data, ACTION_STATE_DONE);
                // download p-files
                keepCheckingCancellation();

                onTypeChanged(data, TYPE_P);
                onDownStateChange(data, DOWN_STATE_DOWNLOADING);
                final Uri proccessedUrl = Uri.parse(downloadProccessedUrl);
                final String processedName = proccessedUrl.getLastPathSegment();
                //final String processedName = Md5Util.string(downloadProccessedUrl);
                final File zipP = doHttpDownload(downloadProccessedUrl, downloader.downloadDir + "/processed/" + processedName, f -> onProgress(data, f));
                if (zipP == null || !zipP.exists()) {
                    onActionStateChanged(data, ACTION_STATE_FAILED);
                    onStateError(data, ERROR_DOWNLOADING);
                    throw new ScriptDownloadException();
                }
                onActionStateChanged(data, ACTION_STATE_DONE);

                // download raws
                keepCheckingCancellation();
                onTypeChanged(data, TYPE_RAW);
                final Uri rawUrl = Uri.parse(downloadRawUrl);
                final String rawName = rawUrl.getLastPathSegment();
                final File zipRaw = doHttpDownload(downloadRawUrl, downloader.downloadDir + "/raw/" + rawName, f -> onProgress(data, f));
                if (zipRaw == null || !zipRaw.exists()) {
                    onActionStateChanged(data, ACTION_STATE_FAILED);
                    onStateError(data, ERROR_DOWNLOADING);
                    throw new ScriptDownloadException();
                }
                onActionStateChanged(data, ACTION_STATE_DONE);

                // unzipping
                keepCheckingCancellation();

                onTypeChanged(data, TYPE_RAW);
                onDownStateChange(data, DOWN_STATE_UNZIPPING);
                UnzipTool.unzip(zipRaw, new File(downloader.unzipDirRaws), downloader.getCancellationSignal(), f -> onProgress(data, f), null);
                //noinspection ResultOfMethodCallIgnored
                zipRaw.delete();

                keepCheckingCancellation();
                onTypeChanged(data, TYPE_P);
                final String upzipBaseDir = downloader.unzipDirProccessed;
                final String unzipScript = downloader.unzipDirScript;
                final String taskId = downloader.taskId;
                UnzipTool.unzip(zipP, new File(upzipBaseDir), downloader.getCancellationSignal(),
                        f -> onProgress(data, f), new UnzipTool.UnzipCustomizer() {
                            @Override
                            public File filteredDirectoryPath(File defaultDir, String zipEntryName) {
                                return ScriptManager.isScriptFileExtension(zipEntryName) ? new File(unzipScript) : defaultDir;
                            }

                            @Override
                            public boolean onCustomWrite(File f, InputStream inputStream, CancellationSignal signal, SProgressor sProgressor) throws Exception {
                                Files.writeBytes(inputStream, f, false, signal, sProgressor);
                                CoScript coScript = CustomScriptIOManager.getCoScriptFromFileSync(f.getAbsolutePath());
                                if(coScript != null) {
                                    coScript.setPublishedID(taskId);
                                    CustomScriptStorage.saveSync(f.getAbsolutePath(), coScript);
                                }
                                return true;
                            }


                        });
                // noinspection ResultOfMethodCallIgnored
                // SceTools.compatUnzip(zipP, new File(StContext.getInstance().getCurrentWorkingDir() + "/temp-test-dir/"));

                zipP.delete();

                onStateChange(data, STATE_COMPLETE);
            } catch (InterruptedException e) {
                if (downloader != null) {
                    if (isCancelAll || downloader.isCanceled()) {
                        onStateChange(downloader.mRunningInfo, STATE_CANCELED);
                    } else {
                        onStateChange(downloader.mRunningInfo, STATE_INTERRUPTED);
                    }
                }
                e.printStackTrace();
                break;
            } catch (CoreCancellationException e) {
                if (downloader != null) {
                    onStateChange(downloader.mRunningInfo, STATE_CANCELED);
                }
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof IOException) {
                    onStateError(downloader.mRunningInfo, ERROR_IO);
                } else if (e instanceof JSONException) {
                    onStateError(downloader.mRunningInfo, ERROR_JSON);
                } else if (e instanceof ScriptDownloadException) {
                    onStateError(downloader.mRunningInfo, ERROR_DOWNLOADING);
                } else {
                    if (downloader != null) {
                        onStateError(downloader.mRunningInfo, ERROR_UNKNOWN);
                    }
                }
            } finally {
                mCurrentDownloadInfo = null;
            }

            synchronized (DownloadScriptService.this) {
                if (mDownloadInfos.isEmpty() && isUnbinded) {
                    stopSelf();
                    break;
                }
            }
        }
    }

    private void onStateError(Intent data, int errorCode) {
        data.putExtra(KEY_STATE, STATE_ERROR);
        data.putExtra(KEY_ERROR_CODE, errorCode);
        notifyCachingState(data);
    }

    private void keepCheckingCancellation() throws CoreCancellationException {
        if (isCancelAll || mCurrentDownloadInfo != null && mCurrentDownloadInfo.isCanceled()) {
            throw new CoreCancellationException();
        }
    }

    /**
     * @param url
     * @param destPath
     *
     * @return
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private File doHttpDownload(String url, String destPath, Consumer<Float> onProgress) throws IOException, InterruptedException, CoreCancellationException, ScriptDownloadException {
        final File destFile = new File(destPath);
        if (destFile.exists() && destFile.isFile()) {
            return destFile;
        }

        if (!destFile.getParentFile().exists()) {
            if (!destFile.getParentFile().mkdirs()) {
                throw new IOException();
            }
        }

        final File destFileDownloading = new File(destFile.getParent(), destFile.getName() + ".downloading");

        if (destFileDownloading.exists()) {
            if (!destFileDownloading.delete()) {
                throw new IOException("File is using");
            }
        }

        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            final Call call = client.newCall(new Request.Builder()
                    .url(url).get().build());

            mCurrentDownloadCall = call;
            final Response response = call.execute();
            if (!response.isSuccessful()) {
                return null;
            }
            final ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            final long len = body.contentLength();
            if (len <= 0) {
                return null;
            }
            final SProgressor sProgressor = new SProgressor(len, onProgress);

            inputStream = body.byteStream();
            fileOutputStream = new FileOutputStream(destFileDownloading);
            final byte[] buffer = new byte[8192];
            int nread;
            long sum = 0L;
            for (; ; ) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                nread = inputStream.read(buffer, 0, buffer.length);
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                if (call.isCanceled()) {
                    destFileDownloading.delete();
                    throw new CoreCancellationException();
                }
                if (nread <= 0) {
                    break;
                }
                sum += nread;
                fileOutputStream.write(buffer, 0, nread);
                sProgressor.updateProgressBy(nread);
            }

            if (sum == len && destFileDownloading.renameTo(destFile)) {
                return destFile;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ScriptDownloadException(e);
        } finally {

            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                //noinspection ThrowFromFinallyBlock
                throw ex;
            }

            if (fileOutputStream != null) {
                fileOutputStream.close();
            }

            mCurrentDownloadCall = null;
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        synchronized (this) {
            isUnbinded = true;
            if (mDownloadInfos.isEmpty() && mCurrentDownloadInfo == null) {
                stopSelf();
            }
            return super.onUnbind(intent);
        }
    }

    public class DownloadBinder extends Binder {
        public DownloadScriptService getDownloadScriptService() {
            return DownloadScriptService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mDownloadThread == null) {
            mDownloadThread = new Thread(this::onDownloading);
            mDownloadThread.start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

final class ScriptDownloadException extends Exception {
    public ScriptDownloadException() {
    }

    public ScriptDownloadException(String message) {
        super(message);
    }

    public ScriptDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptDownloadException(Throwable cause) {
        super(cause);
    }

}
