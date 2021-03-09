package com.uls.stlocalservice.ui.floating;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;

import com.mon.ui.floating.SimpleOverlayManagerService;
import com.mon.ui.floating.SystemWindowManager;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.fragment.FmScriptEditor;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.CompatScriptService;
import com.padyun.scripttools.content.data.FPathScript;
import com.padyun.scripttools.module.runtime.StContext;
import com.uls.stlocalservice.R;
import com.uls.stlocalservice.ui.activity.EditorListActivity;
import com.uls.stlocalservice.ui.activity.EditorListLandActivity;
import com.uls.stlocalservice.ui.activity.OverlayCropActivity;
import com.uls.stlocalservice.ui.activity.OverlayCropLandActivity;
import com.uls.utilites.content.CAsync;
import com.uls.utilites.content.CoreWorkers;
import com.uls.utilites.content.ToastUtils;
import com.uls.utilites.un.Useless;

import java.io.File;
import java.util.List;

/**
 * Created by daiepngfei on 2021-01-20
 */
@SuppressWarnings("ALL")
public class ScriptOverlays extends SimpleOverlayManagerService {

    private Handler handler = new Handler();

    private static final String INTENT_ACTION_MODE = "action_mode";
    private static final String BUNDLE_KEY_MODE = "mode";
    public static final int OVERLAY_CLEAR = 1;
    public static final int OVERLAY_GAME_NORMAL = 2;
    public static final int OVERLAY_GAME_NORMAL2 = 3;
    public static final int OVERLAY_US_EDITOR_LIST = 4;
    public static final int OVERLAY_GAME_CAPTURE = 5;
    public static final int OVERLAY_US_EDITING_1 = 6;
    public static final int OVERLAY_US_EDITING_2 = 7;
    private int lastMode;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null) {
                handleIntentActions(intent, action);
            }
        }
        return START_STICKY;
    }

    private void handleIntentActions(Intent intent, String action) {
        switch (action) {
            case INTENT_ACTION_MODE:
                final int mode = intent.getIntExtra(BUNDLE_KEY_MODE, 0);
                handleIntentAction_Mode(mode);
                break;
            default:
        }
    }

    private void handleIntentAction_Mode(int mode) {
        handler.post(() -> {
            switch (mode) {
                case OVERLAY_CLEAR:
                    setModeClear();
                    break;
                case OVERLAY_GAME_NORMAL:
                    setModeGameNormal();
                    break;
                case OVERLAY_GAME_NORMAL2:
                    setModeGameNormal2();
                    break;
                case OVERLAY_US_EDITOR_LIST:
                    setModeEditorList();
                    break;
                case OVERLAY_GAME_CAPTURE:
                    setModeGameCapture();
                    break;
                case OVERLAY_US_EDITING_1:
                    setModeUsEditing1();
                    break;
                case OVERLAY_US_EDITING_2:
                    setModeUsEditing2();
                    break;
                default:
            }
            lastMode = mode;
            System.out.println("current Mode is " + mode);
        });

    }

    public static class Agent {


        public static void clear(Context context) {
            changeMode(context, OVERLAY_CLEAR);
        }

        public static void onGameNormal(Context context) {
            changeMode(context, OVERLAY_GAME_NORMAL);
        }

        public static void onGameNormal2(Context context) {
            changeMode(context, OVERLAY_GAME_NORMAL2);
        }

        public static void onEditorList(Context context) {
            changeMode(context, OVERLAY_US_EDITOR_LIST);
        }

        public static void onGameCapture(Context context) {
            changeMode(context, OVERLAY_GAME_CAPTURE);
        }

        public static void onEditing1(Context context) {
            changeMode(context, OVERLAY_US_EDITING_1);
        }

        public static void onEditing2(Context context) {
            changeMode(context, OVERLAY_US_EDITING_2);
        }

        static void changeMode(Context context, int mode) {
            Intent intent = new Intent(context, ScriptOverlays.class);
            intent.setAction(INTENT_ACTION_MODE);
            intent.putExtra(BUNDLE_KEY_MODE, mode);
            context.startService(intent);
        }

    }

    private void setModeClear() {
        closeAll();
    }

    private void setModeGameNormal() {
        // 按钮
        final OverlayEdgeButton_Scripts scriptButton = OverlayEdgeButton_Scripts.getInstance();
        // 设置按钮点击后显示列表
        scriptButton.setOnClickListener(v -> pushOverlay(getApplicationContext(), OverlayScriptList.getInstance()));
        addOverlay(getApplicationContext(), scriptButton);
        // 首次添加后先隐藏
        scriptButton.dismissOverlay();

        // 列表
        final OverlayScriptList scriptList = OverlayScriptList.getInstance();
        pushOverlay(getApplicationContext(), scriptList);
        // 设置列表'主动'关闭后显示 按钮
        scriptList.setOnCloseClickLisener(v -> OverlayEdgeButton_Scripts.getInstance().showOverlay());
    }

    private void setModeGameNormal2() {
        OverlayScriptList.getInstance().finish();
        OverlayEdgeButton_Scripts.getInstance().showOverlay();
    }

    private void setModeEditorList() {
        closeAll();
    }

    private void setModeGameCapture() {
        final OverlayEdgeButton_Editor editor = OverlayEdgeButton_Editor.getInstance();
        addOverlay(getApplicationContext(), editor);

        final OverlayButtonBack back = OverlayButtonBack.getInstance();
        addOverlay(getApplicationContext(), back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScriptOverlays.Agent.onEditorList(getApplicationContext());
                Class c = SystemWindowManager.Current.isLandscape(getApplicationContext()) ? EditorListLandActivity.class : EditorListActivity.class;
                Intent intent = new Intent(getApplicationContext(), c);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(FmScriptEditor.FILE_PATH, StContext.manifest().getScriptFilePath());
                getApplicationContext().startActivity(intent);
            }
        });

        editor.setOnClickListener(v -> {
            editor.dismissOverlay();
            back.dismissOverlay();
//            showLoading();
            final File destFile = FPathScript.tmpFile("tmp.png");
            handleIntentAction_Mode(OVERLAY_CLEAR);
            back.post(() -> {
                goCapturing(destFile);
            }, 200);
        });
    }

    private void goCapturing(File destFile) {
        CoreWorkers
                .on(new CoreWorkers.Work<Bitmap>() {
                    @Override
                    public Bitmap work() {
                        Bitmap map = CompatScriptService.requestImageBitmap(StContext.manifest().getDeviceIp());
                        CoBitmapWorker.saveSync(map, destFile.getAbsolutePath());
                        return map;
                    }
                })
                .then(new CoreWorkers.Then<Bitmap>() {
                    @Override
                    public void then(Bitmap bitmap) {
                        if (bitmap != null) {
                            final Class c = SystemWindowManager.Current.isLandscape(getApplicationContext()) ?
                                    OverlayCropLandActivity.class : OverlayCropActivity.class;
                            Intent intent = new Intent(getApplicationContext(), c);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(SECons.Ints.KEY_CROP_REQ_MODE, SECons.Ints.VALUE_CROP_REQ_MODE_CROP_FROM_FILE);
                            // intent.putExtra(SECons.Ints.KEY_ORIGIN_PATH, "/storage/emulated/0/YPEditor/screenshot_20210121_134716.png");
                            intent.putExtra(SECons.Ints.KEY_ORIGIN_PATH, destFile.getAbsolutePath());
                            startActivity(intent);
                            // handleIntentAction_Mode(OVERLAY_US_EDITING_1);
                        } else {
                            ToastUtils.show(getApplicationContext(), R.string.string_toast_capture_failed);
                        }
                    }
                });
    }

    private void setModeUsEditing1() {
        closeAll();
    }

    private void setModeUsEditing2() {
    }

    @Override
    protected void onServiceConfigureChanged() {
        // To clear and reset with last mode
        handler.post(() -> {
            System.out.println("current Mode onServiceConfigureChanged : " + SystemWindowManager.Current.printShortString(getApplicationContext()));
            closeAll();
            handleIntentAction_Mode(lastMode);
            final String pkgName = getTopActivityPackageName(getApplicationContext());
            /*if(pkgName.equals(StContext.manifest().getGameId())) {
            }*/
        });
    }

    public String getTopActivityPackageName(Context context) {
        String topActivityPackage = null;
        String topActivityClass = null;
        ActivityManager activityManager = (ActivityManager) (context
                .getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager
                .getRunningTasks(1);
        if (runningTaskInfos != null) {
            ActivityManager.RunningTaskInfo taskInfo = runningTaskInfos.get(0);
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityPackage = f.getPackageName();
            topActivityClass = f.getClassName();
        }
        return topActivityPackage;
    }
}
