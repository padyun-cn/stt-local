package com.uls.stlocalservice.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;

import com.mon.ui.floating.SimpleOverlayManagerService;
import com.mon.ui.floating.SystemWindowManager;
import com.mon.ui.list.SimpleLineItemDecoration;
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.mon.ui.list.compat.fragment.FormedVHBaseFragment;
import com.padyun.core.dialogs.CoreDgV2Alert;
import com.padyun.scripttools.module.runtime.StContext;
import com.uls.stlocalservice.R;
import com.uls.stlocalservice.content.StlConfig;
import com.uls.stlocalservice.data.vhmodels.MdGame;
import com.uls.stlocalservice.service.LtFairyClientService;
import com.uls.stlocalservice.ui.floating.ScriptOverlays;
import com.uls.stlocalservice.ui.holders.HdGame;
import com.uls.utilites.android.AndroidUtils;
import com.uls.utilites.content.CAsync;
import com.uls.utilites.content.ToastUtils;
import com.uls.utilites.ui.DensityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by daiepngfei on 2020-11-11
 */
public class FmGameList extends FormedVHBaseFragment implements LtFairyClientService.OnStartingListner {
    private SimpleOverlayManagerService overlayManager;

    @Override
    public BaseRecyclerHolder generateVHByType(@NonNull View itemView, int viewType) {
        return new HdGame(itemView);
        // return super.generateVHByType(itemView, viewType);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (overlayManager != null) {
            overlayManager.closeAll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onCustomInit(View swp, RecyclerView rv, RecyclerView.LayoutManager mag, BaseV2RecyclerAdapter adp) {
        super.onCustomInit(swp, rv, mag, adp);
        if (getContext() != null) {
            final int margin = DensityUtils.dip2px(getContext(), 77);
            SimpleLineItemDecoration decoration = new SimpleLineItemDecoration(adp, Color.parseColor("#ededed"), 1);
            decoration.setMarginLeft(margin);
            rv.addItemDecoration(decoration);
        }
    }

    @Override
    protected void onLoadingData(int nextPageIndex, int pageSize, boolean isRefresh) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        showLoading();
        CAsync.excute((CAsync.ITaskRunnable<List<? extends IBaseRecyclerModel>>) () -> {
            List<MdGame> data = new ArrayList<>();
            List<ResolveInfo> infoList = AndroidUtils.queryInstalledUserAppPackages(activity);
            for (ResolveInfo t : infoList) {
                String apkPath = t.activityInfo.applicationInfo.publicSourceDir;
                if (apkPath == null || apkPath.length() == 0) {
                    apkPath = t.activityInfo.applicationInfo.sourceDir;
                }
                if (apkPath == null || apkPath.length() == 0) {
                    continue;
                }
                if (getActivity().getPackageName().equals(t.activityInfo.applicationInfo.packageName)) {
                    continue;
                }
                MdGame model = MdGame.fromPackageInfo(activity.getPackageManager(), t);
                if (model != null) {
                    // Log.d("tttteeesssstttt", "pkgname = " + model.getPackageName());
                    data.add(model);
                }
            }

            Collections.sort(data, (o1, o2) -> (int) (o2.getApkFile().length() - o1.getApkFile().length()));
            return data;
        }, data -> {
            update(data, true);
            dismissLoading();
        }, e -> dismissLoading());

    }

    public static final int MSG_START_GAME = 101;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == Activity.RESULT_OK) {
            final Activity activity = getActivity();
            if (activity != null && mLastStartItem != null) {
                startOverlayWithlastClickItem();
            }
        }
    }

    private void startOverlayWithlastClickItem() {
        final Activity act = getActivity();
        if (act == null) {
            return;
        }
        if (mLastStartItem != null) {
            StContext.manifest()
                    .setUseId(StlConfig.UID)
                    .setDeviceIp(StlConfig.SIP)
                    .setGameId(mLastStartItem.getPackageName());
            /*YpFairyService.setStarterClass(YpTaskFairyImpl.class);
            act.startService(new Intent(act.getApplicationContext(), YpFairyService.class));*/
            startFairyService(act);
        }
    }

    private Handler mProcessHandler = new Handler();


    /*private void checkSu() {
        showLoading();
        CoreWorkers
                .on(() -> {
                    Process p;
                    final String tag = "checkSu";
                    try {
                        // Preform su to get root privledges
                        p = Runtime.getRuntime().exec("su");

                        // Attempt to write a file to a root-only
                        DataOutputStream os = new DataOutputStream(p.getOutputStream());
                        os.writeBytes("echo Do I have root? \n");
                        //os.writeBytes("echo \"Do I have root?\" >/sdcard/temporary.txt\n");

                        // Close the terminal
                        os.writeBytes("exit\n");
                        os.flush();
                        p.waitFor();
                        final int exit = p.exitValue();
                        Log.e(tag, "exit value is " + exit);
                        return exit == 0;
                    } catch (IOException e) {
                        // TODO Code to run in input/output exception
                        e.printStackTrace();
                        Log.e(tag, "io", e);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e(tag, "interrrupted", e);
                    }
                    return false;
                })
                .then(result -> {
                    dismissLoading();
                    final Activity act = getActivity();
                    if (act == null) {
                        return;
                    }
                    if (result) {
                        act.startService(new Intent(act, LtFairyClientService.class));
                        act.startActivity(act.getPackageManager().getLaunchIntentForPackage(mLastStartItem.getPackageName()));
                        ScriptOverlays.Agent.onGameNormal(act);
                    } else {
                        ToastUtils.show(act, R.string.string_toast_error_root_failed);
                    }
                });
    }
*/
    private LtFairyClientService service;

    private void startFairyService(final Activity act) {
        showLoading();
        //checkSu();
        if (service == null) {
            act.startService(new Intent(act, LtFairyClientService.class));
            act.bindService(new Intent(act, LtFairyClientService.class), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder binder) {
                    service = ((LtFairyClientService.FairyBinder) binder).getService();
                    service.start(FmGameList.this);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            }, Context.BIND_AUTO_CREATE);
        } else {
            service.start(FmGameList.this);
        }
        /*checkSu(
                () -> {
                    act.startService(new Intent(act, LtFairyClientService.class));

                },
                () -> {
                    ToastUtils.show(act, R.string.string_toast_error_root_failed);
                    dismissLoading();
                }
        );*/

    }


    private MdGame mLastStartItem;
    private static final int REQ = 123;

    @Override
    protected boolean onItemMessage(Message msg) {
        final Activity activity = getActivity();
        if (activity == null) {
            return super.onItemMessage(msg);
        }
        if (msg.what == MSG_START_GAME) {
            performFloatingWindowPermissionCheck(activity, () -> {
                mLastStartItem = (MdGame) msg.obj;
                startOverlayWithlastClickItem();
            });
        }
        return super.onItemMessage(msg);
    }

    /*private void startGameWithItem(Message msg, Activity activity) {

        *//*if (!SystemWindowManager.getRequiredPermmision(getContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName()));
                startActivityForResult(intent, REQ);
            } else {
                Toast.makeText(getContext(), R.string.string_toast_tips_overlay_permmission, Toast.LENGTH_LONG).show();
            }
        } else {
            startOverlayWithlastClickItem();
        }*//*
    }*/

    private void performFloatingWindowPermissionCheck(Activity activity, Runnable onPermmitted) {
        if (SystemWindowManager.getRequiredPermmision(activity)) {
            onPermmitted.run();
            return;
        }
        CoreDgV2Alert.toCreate(activity)
                .setTitle(R.string.string_dialog_title_tip)
                .setMessage(R.string.string_dialog_overlay_permission)
                .setPositiveButton(R.string.string_dialog_overlay_permission_gosetting, (dialog, which) -> {
                    SystemWindowManager.goRequestOverlayPermisson(activity);
                    onPermmitted.run();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.string_dialog_overlay_permission_set, (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public void onStartingStateChanged(int state) {
        final Activity act = getActivity();
        if (act == null) {
            return;
        }
        mProcessHandler.post(() -> {
            if (state >= LtFairyClientService.STATE_SUED) {
                act.startActivity(act.getPackageManager().getLaunchIntentForPackage(mLastStartItem.getPackageName()));
                ScriptOverlays.Agent.onGameNormal(act);
                dismissLoading();
            } else if (state < LtFairyClientService.STATE_IDLE) {
                if (state == LtFairyClientService.STATE_ROOT_FAILED) {
                    ToastUtils.showToast(act, R.string.string_toast_error_root_failed);
                } else {
                    ToastUtils.showToast(act, R.string.string_toast_error_start_failed);
                }
                dismissLoading();
            }

        });
    }
}
