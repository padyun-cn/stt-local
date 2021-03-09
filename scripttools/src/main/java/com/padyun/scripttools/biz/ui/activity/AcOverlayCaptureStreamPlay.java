package com.padyun.scripttools.biz.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.mon.ui.activities.AcBaseCompat;
import com.padyun.scripttools.R;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.test.ScriptTestConfig;
import com.padyun.scripttools.content.data.FPathScript;
import com.padyun.scripttoolscore.compatible.data.model.SEScript;
import com.padyun.scripttoolscore.compatible.plugin.LtLog;
import com.uls.utilites.content.AppPermissionManager;
import com.uls.utilites.content.CoreWorkers;
import com.uls.utilites.io.Files;
import com.uls.utilites.un.Useless;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;


/**
 * Created by lt on 2/21/19
 */
@SuppressWarnings("ALL")
public abstract class AcOverlayCaptureStreamPlay extends /*AcStreamPlay*/ AcBaseCompat {

    private static final int SELECT_PHOTO_CODE = 1;
    public static final String KEY_SCRIPT = "script";
    public static final String KEY_CAPTURE_ENABLE = "capture_enable";
    public static final String KEY_DONE_ENABLE = "done_enable";
    public static final String KEY_ALBUM_ENABLE = "album_enable";
    public static final String KEY_GAMING_ENABLE = "gaming_enable";
    public static final String KEY_IMGFILE = "img_file";
    public static final String KEY_SAVE_CAPTURE_DATA = "key_save_capture_data";
    private static final int MSG_UPDATEIMG = 1;
    private static final int MSG_SCREENSHOERROR = 2;
    private static final int MSG_ORIENTATION = 3;
    private static final int MSG_HIDE_SURFACE = 4;
    private ImageView mImageView;
    private String mImgFile, mImgFileKeeper, mSavedPath, mImgTempCacheFilePath;
    private String mDisposeId;
    private Bitmap mBitmap;
    private Handler mHandler;

    private FrameLayout mImageOverlayContainer;
    private FrameLayout mImageRemainingContainer;
    private FrameLayout mImageOverlayOverallContainer;
    protected ConstraintLayout mOverlayContainer;

    private boolean mIsGameStarted = false;
    private boolean mIsStoreValueGameStarted = false;


    private boolean mIsSaveCaptureData = false;
    private boolean mIsIntentSaveCaptureData = false;

    private AppPermissionManager mAppPermissionManager = new AppPermissionManager(this, this::doInitWithPermissionRequest, () -> this.init(EInitFrom.DEFAULT));

    @Override
    protected void onCreateContent(@NotNull FrameLayout viewById, int content_container) {
        super.onCreateContent(viewById, content_container);
        LayoutInflater.from(this).inflate(R.layout.overlay_edit_scripttools, viewById, true);
        mDisposeId = mImgFileKeeper = mImgFile = getIntent().getStringExtra(KEY_IMGFILE);
        mIsIntentSaveCaptureData = getIntent().getBooleanExtra(KEY_SAVE_CAPTURE_DATA, true);
        mHandler = new Handler(/*this*/);
        initViews();
    }

    public static void capture(Activity from, int requestCode, SEScript script, String destFile) {
        capture(from, requestCode, script, destFile, null);
    }

    public static void capture(Activity from, int requestCode, SEScript script, String destFile, Bundle extras) {
        launch(from, AcOverlayCaptureStreamPlay.class, requestCode, script, destFile, true, extras);
    }

    protected static void launch(Activity from, Class<? extends AcOverlayCaptureStreamPlay> to, int requestCode, SEScript script, String destFile, boolean saveCaptureData, Bundle extras) {
        Intent intent = new Intent(from, to);
        intent.putExtra(KEY_IMGFILE, destFile);
        intent.putExtra(KEY_SCRIPT, script);
        intent.putExtra(KEY_SAVE_CAPTURE_DATA, saveCaptureData);
        if (extras != null) intent.putExtras(extras);
        from.startActivityForResult(intent, requestCode);
    }


    private void doInitWithPermissionRequest() {
        mAppPermissionManager.requestPerminssions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAppPermissionManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAppPermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    protected FrameLayout getImageOverlayContainer() {
        return mImageOverlayContainer;
    }

    private Bitmap adjustPhotoRotation(Bitmap map, final int orientationDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(orientationDegree);
        try {
            Bitmap newMap = Bitmap.createBitmap(map, 0, 0, map.getWidth(), map.getHeight(), matrix, true);
            return newMap;
        } catch (OutOfMemoryError ex) {
        }

        return null;

    }

    @Override
    protected void onStart() {
        super.onStart();
        doInitWithPermissionRequest();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initViews() {
        mImageView = findViewById(R.id.image);
        mOverlayContainer = findViewById(R.id.imageContainer);
        mImageOverlayContainer = findViewById(R.id.imageOverlayContainer);
        mImageRemainingContainer = findViewById(R.id.imageRemainingContainer);
        mImageRemainingContainer.setVisibility(View.GONE);
        mImageOverlayOverallContainer = findViewById(R.id.overlayOverAllContainer);
        mImageOverlayContainer.removeAllViews();
        Useless.foreach(onCreateImageOverlay(mImageOverlayContainer), t -> mImageOverlayContainer.addView(t));
        Useless.foreach(onCreateImageRemainingOverlay(mImageRemainingContainer), t -> mImageRemainingContainer.addView(t));
        Useless.foreach(onCreateOverallOverlay(mImageOverlayOverallContainer), t -> mImageOverlayOverallContainer.addView(t));
    }

    protected FrameLayout getImageRemainingContainer() {
        return mImageRemainingContainer;
    }

    protected View[] onCreateImageOverlay(FrameLayout container) {
        return null;
    }

    protected View[] onCreateImageRemainingOverlay(FrameLayout container) {
        return null;
    }

    protected View[] onCreateOverallOverlay(FrameLayout container) {
        return null;
    }

    protected void onScreenLoad(Bitmap bm, EInitFrom remote) {

    }

    protected void onScreenRotate(Bitmap bm) {

    }

    protected void onScreenSaved(Bitmap bm, String path, Boolean t, boolean b) {
    }

    @NonNull
    protected Intent onConfrimResult(Intent intent, Bitmap bitmap, String imgFile, boolean save) {
        return intent;
    }

    private void rotateBimap() {
        Bitmap map = adjustPhotoRotation(mBitmap, 270);
        if (map != null) {
            mBitmap = map;
            LtLog.i("width:" + map.getWidth() + " height:" + map.getHeight());
            setImageBitmap(map);
            onScreenRotate(map);
            if (map.getWidth() > map.getHeight()) {
                switchLandScape();
            } else {
                switchPortrait();
            }
        }
    }

    protected void setImageViewVisibility(int visibility) {
        mImageView.setVisibility(visibility);
    }

    protected ImageView getImageView() {
        return mImageView;
    }

    private void setImageBitmap(Bitmap map) {
        if (mImageView != null && mImageView.getVisibility() == View.VISIBLE) {
            mImageView.setImageBitmap(map);
        }
    }

    /*protected void doSaveScreen(boolean confirmResult, Runnable successSave) {
        showLoading();
        if (mBitmap != null && mImgFile != null) {
            final boolean fileExist = Files.exists(mImgFile);
            if (!mIsIntentSaveCaptureData || !mIsSaveCaptureData) {
                mSavedPath = mImgFile;
                if (successSave != null) successSave.run();
                onScreenSaved(mBitmap, mSavedPath, true, false);
                if (confirmResult) doConfirmResult(true, false);
                dismissLoading();
            } else {
                saveAsync(confirmResult, successSave, fileExist);
            }
        } else {
            dismissLoading();
        }
    }*/

    protected void dismissLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loadingLayout).setVisibility(View.INVISIBLE);
            }
        });

    }

    protected void showLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loadingLayout).setVisibility(View.VISIBLE);
            }
        });
    }

    /*private void saveAsync(boolean confirmResult, Runnable successSave, boolean newPath) {
        CoreWorkers.exec(() -> {
            mSavedPath = newPath ? StContext.assets().newRaw().getAbsolutePath() : mImgFile;
            return CoBitmapWorker.saveSync(mBitmap, mSavedPath);
        }, t -> {
            dismissLoading();
            if (successSave != null) successSave.run();
            if (t) onScreenSaved(mBitmap, mSavedPath, t, !newPath && t);
            if (confirmResult) doConfirmResult(t, !newPath && t);
        });
    }*/

    @Deprecated
    protected void justSaveCurrentScreen(String path, Runnable successSave, Runnable anyway){
        if(mBitmap != null) {
            showLoading();
            CoreWorkers.exec(() -> {
                return CoBitmapWorker.saveSync(mBitmap, path);
            }, t -> {
                dismissLoading();
                Files.delete(FPathScript.tmpFile("tmp.png"));
                if (successSave != null && t) successSave.run();
                else if(anyway != null) anyway.run();
            });
        }
    }

    private void doConfirmResult(boolean success, boolean save) {
        if (success) {
            Intent intent = onConfrimResult(new Intent(), mBitmap, mSavedPath, save);
            if (!onFinishWithResult(intent)) {
                setResult(RESULT_OK, intent);
                finish();
            }
        } else {
            Toast.makeText(AcOverlayCaptureStreamPlay.this, "保存原图失败， 请重新截取图片", Toast.LENGTH_SHORT).show();
        }
    }

    protected String getSavedPath() {
        return mSavedPath;
    }

    protected Bitmap getCurBitmap() {
        return mBitmap;
    }

    protected boolean onFinishWithResult(Intent intent) {
        return false;
    }


    public enum EInitFrom {
        LOCAL, REMOTE, DEFAULT
    }

    private void init(EInitFrom from) {

        if (mBitmap == null && Files.exists(mImgFile)) {
            mBitmap = BitmapFactory.decodeFile(mImgFile);
        }

        if (mBitmap == null && Files.exists(mImgTempCacheFilePath)) {
            mBitmap = BitmapFactory.decodeFile(mImgTempCacheFilePath);
        }

        if (mBitmap != null) {
            if (mBitmap.getWidth() > mBitmap.getHeight() && getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                // bm = mBitmap ;
                switchLandScape();
            } else if (mBitmap.getWidth() < mBitmap.getHeight() && getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                // bm = mBitmap ;
                switchPortrait();
            }
            setImageBitmap(mBitmap);
            onScreenLoad(mBitmap, from);
        }
    }


    protected void screenshot(Runnable success) {
        //Toast.makeText(this, "正在截图.....", Toast.LENGTH_SHORT).show();
        showLoading();
        Files.delete(FPathScript.tmpFile("tmp.png"));
        /*screenShot(new ICCallback<Bitmap>() {

            @Override
            public void onSuccess(Bitmap map) {
                if (map != null) {
                    //mHandler.sendEmptyMessage(MSG_UPDATEIMG);
                    LogUtil.d("Ac#Over#LayCaptureSteramPlay", "1");
                    onBackgroundScreenshotSuccess(map);
                    LogUtil.d("Ac#Over#LayCaptureSteramPlay", "2");
                    final File f = FPathScript.tmpFile("tmp.png.downloading");
                    LogUtil.d("Ac#Over#LayCaptureSteramPlay", "3");
                    if(f.exists()){
                        f.delete();
                    }
                    LogUtil.d("Ac#Over#LayCaptureSteramPlay", "4");
                    CoBitmapWorker.saveSync(map, f.getAbsolutePath());
                    LogUtil.d("Ac#Over#LayCaptureSteramPlay", "5");
                    f.renameTo(FPathScript.tmpFile("tmp.png"));
                    LogUtil.d("Ac#Over#LayCaptureSteramPlay", "6");

                    final Bitmap m = map;
                    runOnUiThread(() -> {
//                     stopGame();
                        //dismissStream();
                        LogUtil.d("Ac#Over#LayCaptureSteramPlay", "7");
                        if(success != null){
                            success.run();
                        }
                        setLoadedBitmap(f, m);
                    });

                } else {
                    mHandler.sendEmptyMessage(MSG_SCREENSHOERROR);
                }
                dismissLoading();
            }

            @Override
            public void onSendFail(int errno, String msg, Exception e) {
                LogUtil.d("Ac#Over#LayCaptureSteramPlay", "100");
                runOnUiThread(() -> {
                    LogUtil.d("Ac#Over#LayCaptureSteramPlay", "1001");
                    ToastUtils.show(AcOverlayCaptureStreamPlay.this, "截图失败，请稍候重试");
                    dismissLoading();
                });
            }
        });*/
    }

    protected void setLoadedBitmap(File f, Bitmap m) {
        mBitmap = m;
        mImgFile = mImgFileKeeper;
        mImgTempCacheFilePath = f.getAbsolutePath();
        mIsSaveCaptureData = true;
        // dismissStream();
        init(EInitFrom.REMOTE);
    }

    protected void onBackgroundScreenshotSuccess(Bitmap map) {
    }

    protected void clearBitmapBackToPortrait() {
        mBitmap = null;
        if (mImageView != null) mImageView.setImageResource(android.R.color.transparent);
        switchPortrait();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //if (mBitmap != null) outState.putParcelable("bitmap", mBitmap);
        outState.putBoolean("isSavecaptureData", mIsSaveCaptureData);
        outState.putBoolean("isGameRunning", mIsGameStarted);
        outState.putString("imgTempCacheFilePath", mImgTempCacheFilePath);
        outState.putString("disposedId", mDisposeId);
        outState.putString("imgfile", mImgFile);
        outState.putString("imgfileKeeper", mImgFileKeeper);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        doOnResotreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreateRestoreInstance(Bundle restoreInstanceState) {
        super.onCreateRestoreInstance(restoreInstanceState);
        doOnResotreInstanceState(restoreInstanceState);
    }

    private void doOnResotreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            /*Bitmap map = savedInstanceState.getParcelable("bitmap");
            if (map != null) mBitmap = map;*/
            mIsSaveCaptureData = savedInstanceState.getBoolean("isSavecaptureData", false);
            mIsStoreValueGameStarted = savedInstanceState.getBoolean("isGameRunning", false);
            mDisposeId = savedInstanceState.getString("disposedId");
            mImgFile = savedInstanceState.getString("imgfile");
            mImgTempCacheFilePath = savedInstanceState.getString("imgTempCacheFilePath");
            mImgFileKeeper = savedInstanceState.getString("imgfileKeeper");
            if (mIsStoreValueGameStarted && !mIsGameStarted) {
                //startGame(() -> mIsStoreValueGameStarted = false);
            }
        }
    }

    public void onResume() {
        super.onResume();
//        if(mBitmap == null) {
//            screenshot();
//        }
    }



    protected String getIp(){
        return ScriptTestConfig.getServerIp();
    }

    protected String getAsIp(){
        return ScriptTestConfig.getAsIp();
    }


    protected int getSpecifiedImageWidth() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ScriptTestConfig.Image.SPECIFED_WIDTH_LAND : ScriptTestConfig.Image.SPECIFED_WIDTH_PORT;
    }

    protected int getSpecifiedImageHeight() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ScriptTestConfig.Image.SPECIFED_HEIGHT_LAND : ScriptTestConfig.Image.SPECIFED_HEIGHT_PORT;
    }


    protected void fixSpcifiedImageRect(Rect rect) {
        if (rect != null)
            rect.set(calcSpecifiedImageWithHorizontal(rect.left), calcSpecifiedImageWithVertical(rect.top),
                    calcSpecifiedImageWithHorizontal(rect.right), calcSpecifiedImageWithVertical(rect.bottom));
    }

    protected void unboxSpcifiedImageRect(Rect rect) {
        if (rect != null)
            rect.set(unboxSpecifiedImageWithHorizontal(rect.left), unboxSpecifiedImageWithVertical(rect.top),
                    unboxSpecifiedImageWithHorizontal(rect.right), unboxSpecifiedImageWithVertical(rect.bottom));
    }

    protected int calcSpecifiedImageWithHorizontal(int x) {
        final int w = getSpecifiedImageWidth();
        return (int) Math.min(w, Math.max(0, x * 1.0f / getImageView().getMeasuredWidth() * w));
    }

    protected int unboxSpecifiedImageWithHorizontal(int x) {
        final int w = getSpecifiedImageWidth();
        return (int) Math.min(w, Math.max(0, x * 1.0f / w * getImageView().getMeasuredWidth()));
    }

    protected int calcSpecifiedImageWithVertical(int y) {
        final int h = getSpecifiedImageHeight();
        return (int) Math.min(h, Math.max(0, y * 1.0f / getImageView().getMeasuredHeight() * h));
    }

    protected int unboxSpecifiedImageWithVertical(int y) {
        final int h = getSpecifiedImageHeight();
        return (int) Math.min(h, Math.max(0, y * 1.0f / h * getImageView().getMeasuredHeight()));
    }
}
