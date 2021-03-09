package com.padyun.scripttools.biz.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.core.dialogs.CoreDgV2Alert;
import com.padyun.manifest.ApiStatusCodes;
import com.padyun.scripttools.biz.ui.content.Constants.ApiPath.Phrase;
import com.padyun.scripttools.biz.ui.data.MdFavoredUserTask;
import com.padyun.scripttools.biz.ui.data.StRespBaseObj;
import com.padyun.scripttools.biz.ui.dialogs.upload.DgLiteProgress;
import com.padyun.scripttools.biz.ui.holders.HdFavoredUserTaskTitle;
import com.padyun.scripttools.common.StCommonCallback;
import com.padyun.scripttools.module.runtime.ScriptManager;
import com.padyun.scripttools.module.runtime.StContext;
import com.padyun.scripttools.module.runtime.StIntentActions;
import com.padyun.scripttools.module.runtime.StManifest;
import com.padyun.scripttools.services.biz.DownloadScriptService;
import com.padyun.scripttoolscore.models.HttpParam;
import com.uls.utilites.content.ToastUtils;
import com.uls.utilites.ui.DensityUtils;
import com.uls.utilites.un.Useless;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.padyun.scripttools.services.biz.DownloadScriptService.DOWN_STATE_DOWNLOADING;
import static com.padyun.scripttools.services.biz.DownloadScriptService.DOWN_STATE_IDLE;
import static com.padyun.scripttools.services.biz.DownloadScriptService.DOWN_STATE_UNZIPPING;
import static com.padyun.scripttools.services.biz.DownloadScriptService.ERROR_UNKNOWN;
import static com.padyun.scripttools.services.biz.DownloadScriptService.KEY_DOWN_STATE;
import static com.padyun.scripttools.services.biz.DownloadScriptService.KEY_ERROR_CODE;
import static com.padyun.scripttools.services.biz.DownloadScriptService.KEY_PROGRESS;
import static com.padyun.scripttools.services.biz.DownloadScriptService.KEY_STATE;
import static com.padyun.scripttools.services.biz.DownloadScriptService.KEY_TYPE;
import static com.padyun.scripttools.services.biz.DownloadScriptService.STATE_COMPLETE;
import static com.padyun.scripttools.services.biz.DownloadScriptService.STATE_DOWNLOADING;
import static com.padyun.scripttools.services.biz.DownloadScriptService.STATE_ERROR;
import static com.padyun.scripttools.services.biz.DownloadScriptService.STATE_NONE;
import static com.padyun.scripttools.services.biz.DownloadScriptService.STATE_WAITING;
import static com.padyun.scripttools.services.biz.DownloadScriptService.TYPE_RAW;

/**
 * Created by daiepngfei on 2020-05-14
 */
@SuppressWarnings("ConstantConditions")
public class FmCustomScriptFavoredList extends FmStSimpleList implements DownloadScriptService.OnCachingRemoteScriptListener {

    public static final String TAG = "FmCustomScriptFavoredList#";
    private final boolean editable;
    private DownloadScriptService mDownloadScriptService = null;
    private ServiceConnection mDownloadServiceConnection = null;
    private FmCustomScriptHome.CustomScriptHomeProxy mCustomScriptHomeProxy = null;

    public FmCustomScriptFavoredList(boolean editable) {
        this.editable = editable;
    }

    public FmCustomScriptFavoredList() {
        this(true);
    }

    private Callback mOnLoadCallack = new Callback() {

        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            // todo onfailed
            runOnUiThread(() -> {
                Toast.makeText(getActivity(), "网络错误，请稍候重试", Toast.LENGTH_SHORT).show();
                dismissLoading();
            });

        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            StRespBaseObj objo = null;
            try {
                objo = StRespBaseObj.parse(response);
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(call, new IOException(e));
                return;
            }
            final StRespBaseObj obj = objo;
            runOnUiThread(() -> {
                if (obj != null) {
                    if (obj.getCode() == 0) {
                        final Type typeOf = new TypeToken<ArrayList<MdFavoredUserTask>>() {
                        }.getType();
                        final ArrayList<MdFavoredUserTask> list = obj.dataAsList(typeOf);
                        if (list != null && list.size() != 0) {
                            for (MdFavoredUserTask task : list) {
                                if (task == null) continue;
                            }
                            Collections.sort(list);
                            listRefresh(new ArrayList<>(list));
                        } else {
                            //ToastUtils.show(getContext(), "还未有订阅内容");
                        }
                    } else {
                        ToastUtils.show(getContext(), Useless.nonNullStr(obj.getMsg()));
                    }
                } else {
                    ToastUtils.show(getContext(), "网络忙，请稍候重试");
                }
                dismissLoading();

            });

        }
    };

    @Override
    protected void onInitSwipeReclcerView(SwipeRecyclerView swipeRecyclerView, BaseV2RecyclerAdapter mListAdapter) {
        swipeRecyclerView.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
            if (editable) {
                if (getActivity() == null) return;
                IBaseRecyclerModel model = getData().get(position);
                if (model instanceof MdFavoredUserTask) {
                    final MdFavoredUserTask task = (MdFavoredUserTask) model;
                    if (task.isCreator()) {
                        SwipeMenuItem menuItem = new SwipeMenuItem(getActivity());
                        final int width = DensityUtils.dip2px(getActivity(), 60f);
                        final int height = ViewGroup.LayoutParams.MATCH_PARENT;
                        menuItem.setBackgroundColor(Color.parseColor("#7baB32")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).setText("分享");
                        SwipeMenuItem menuItem2 = new SwipeMenuItem(getActivity());
                        menuItem2.setBackgroundColor(Color.parseColor("#389CFF")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).setText("下载");
                        // menuItem.setBackgroundColor(Color.parseColor("#7baB32")).setWidth(width).setHeight(height).setTextSize(10).setTextColor(Color.WHITE).setText("取消订阅");
                        rightMenu.addMenuItem(menuItem);
                        rightMenu.addMenuItem(menuItem2);
                    }
                }
            }
        });

        swipeRecyclerView.setOnItemMenuClickListener((menuBridge, adapterPosition) -> {
            if (!editable) {
                return;
            }
            menuBridge.closeMenu();
            IBaseRecyclerModel model = getData().get(adapterPosition);
            if (model instanceof MdFavoredUserTask) {
                final MdFavoredUserTask task = (MdFavoredUserTask) model;
                final int pos = menuBridge.getPosition();
                if (pos == 0) {
                    showLoading();
                    StContext.network().http().post(Phrase.POST_SHARE_CODE, new StCommonCallback<String>(String.class) {

                        @Override
                        public void onStResponseFailure(int code, String msg, Exception e) {
                            dismissLoading();
                            ToastUtils.show(getActivity(), msg == null ? "网络错误，请稍候重试" : msg);
                        }

                        @Override
                        public void onParsedResponse(String s) {
                            dismissLoading();
                            try {
                                JSONObject object = new JSONObject(s);
                                final String code = object.getString("code");
                                Intent intent = new Intent();
                                intent.putExtra("code", code);
                                intent.putExtra("task", object.getString("task_name"));
                                intent.putExtra("game", object.getString("game_name"));
                                StContext.getInstance().sendStBroadcast(StIntentActions.SHARE_TASK_CODE, intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onStResponseFailure(-1, "解析出错，请稍候重试", e);
                            }
                        }

                    }, new HttpParam("taskid", task.getId()));
                } else if (pos == 1) {
                    withDownloadService(() -> tryToDownloadWithServericeReady(task));
                }
            }
        });
    }

    private void tryToDownloadWithServericeReady(MdFavoredUserTask task) {
        if (getActivity() == null) {
            return;
        }
        final StManifest manifest = StContext.getInstance().getManifest();
        final String currentWorkingDir = StContext.getInstance().getCurrentWorkingDir();
        final String dirRaws = StContext.assets().currentDirRaw().getAbsolutePath();
        final String dirP = StContext.assets().currentDirProcessed().getAbsolutePath();
        final String dirScript = StContext.script().currentScriptDir();
        final File f = new File(dirScript, task.getTask_name() + ScriptManager.stFileExt());
        if (f.exists()) {
            CoreDgV2Alert.toCreate(getActivity()).setTitle("温馨提示")
                    .setMessage("检测同名的本地文件，是否选择覆盖安装？")
                    .setPositiveButton("覆盖", (dialog, which) -> {
                        dialog.dismiss();
                        doStartingDownload(task, manifest, currentWorkingDir, dirRaws, dirP, dirScript);
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        } else {
            doStartingDownload(task, manifest, currentWorkingDir, dirRaws, dirP, dirScript);
        }
    }

    private void doStartingDownload(MdFavoredUserTask task, StManifest manifest, String currentWorkingDir, String dirRaws, String dirP, String dirScript) {
        mDownloadScriptService.download(
                manifest.getGameId(), task.getId(), // game & task id
                currentWorkingDir + "/download/" + task.getTask_name(), // download dir
                manifest.getUserToken(), // user token
                dirRaws, dirP, dirScript, // unzipping dirs
                FmCustomScriptFavoredList.this // listener
        );
        getProgressDialog().setTitle("温馨提示").setMessage("开始准备下载").show();
    }

    /**
     * @param proxy
     */
    public void setCustomScriptHomeProxy(FmCustomScriptHome.CustomScriptHomeProxy proxy) {
        mCustomScriptHomeProxy = proxy;
    }

    @Override
    public void onStart() {
        super.onStart();
        withDownloadService(() -> mDownloadScriptService.addOnCachingRemoteScriptListeners(this));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDownloadScriptService != null) {
            mDownloadScriptService.removeOnCachingRemoteScriptListeners(FmCustomScriptFavoredList.this);
        }
    }

    /**
     * @param runnable
     */
    private void withDownloadService(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (mDownloadScriptService == null || mDownloadServiceConnection == null) {
            if (mDownloadServiceConnection == null) {
                mDownloadServiceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        if (service instanceof DownloadScriptService.DownloadBinder) {
                            final DownloadScriptService.DownloadBinder binder = (DownloadScriptService.DownloadBinder) service;
                            mDownloadScriptService = binder.getDownloadScriptService();
                            runnable.run();
                        }
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        mDownloadServiceConnection = null;
                    }
                };
            }
            final Activity activity = getActivity();
            if (activity != null) {
                final Intent intent = new Intent(activity, DownloadScriptService.class);
                activity.startService(intent);
                activity.bindService(intent, mDownloadServiceConnection, Context.BIND_AUTO_CREATE);
            }
        } else {
            runnable.run();
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg == null || !(msg.obj instanceof MdFavoredUserTask)) {
            return false;
        }
        final MdFavoredUserTask task = (MdFavoredUserTask) msg.obj;
        final HttpParam[] params = new HttpParam[]{
                new HttpParam("device_id", StContext.manifest().getDeviceId()),
                new HttpParam("game_id", StContext.manifest().getGameId()),
                new HttpParam("task_id", task.getId())
        };
        switch (msg.what) {
            case FmCustomScriptTitleList.MSG_POST_ADD:
                showLoading();
                StContext.network().http().post(Phrase.POST_TASK_STATUS, new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(() -> {
                            dismissLoading();
                            ToastUtils.show(getActivity(), "网络错误，请稍候重试");
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        StRespBaseObj obj = null;
                        try {
                            obj = StRespBaseObj.parse(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (obj == null) {
                            showSimpleTipsDialog("网络错误，请稍候重试");
                            return;
                        }

                        final int code = obj.getCode();
                        final String msg = obj.getMsg();
                        runOnUiThread(() -> {
                            dismissLoading();
                            switch (code) {
                                case 0:
                                case ApiStatusCodes.TASK_RUNNING_TYPE_USER:
                                    addUserTask(task, params);
                                    break;
                                case ApiStatusCodes.TASK_RUNNING_TYPE_SYSTEM:
                                    CoreDgV2Alert.toCreate(getActivity()).setTitle("温馨提示").setMessage(msg)
                                            .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                                            .setPositiveButton("确定", (dialog, which) -> {
                                                dialog.dismiss();
                                                clearTaskToAddTask(task, params);
                                            })
                                            .show();
                                    break;
                                default:
                                    showSimpleTipsDialog(msg);
                            }
                        });
                    }
                }, params);
                break;
            case FmCustomScriptTitleList.MSG_POST_DEL:
                showLoading();
                StContext.network().http().post(Phrase.POST_REMOVE_TASK, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        runOnUiThread(() -> {
                            dismissLoading();
                            ToastUtils.show(getActivity(), TIPS_NET_ERROR);
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        StRespBaseObj obj = null;
                        try {
                            obj = StRespBaseObj.parse(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (obj == null) {
                            ToastUtils.show(getActivity(), TIPS_NET_ERROR);
                            return;
                        }

                        final int code = obj.getCode();
                        final String msg = obj.getMsg();
                        runOnUiThread(() -> {
                            dismissLoading();
                            if (code != 0) {
                                ToastUtils.show(getActivity(), msg);
                            } else {
                                /*task.setTask_status(0);
                                notifyDataSetChanged();*/
                                reload();
                            }
                        });

                    }
                }, params);
                break;
            default:

        }

        return super.handleMessage(msg);
    }

    private static final String TIPS_NET_ERROR = "网络错误，请稍候重试";

    private void clearTaskToAddTask(MdFavoredUserTask task, HttpParam[] params) {
        showLoading();
        StContext.network().http().post(Phrase.POST_CLEAR_TASK, new Callback() {
            @Override
            public void onFailure(@NotNull Call call1, @NotNull IOException e) {
                runOnUiThread(() -> {
                    dismissLoading();
                    ToastUtils.show(getActivity(), TIPS_NET_ERROR);
                });
            }

            @Override
            public void onResponse(@NotNull Call call1, @NotNull Response response1) throws IOException {
                StRespBaseObj obj = null;
                try {
                    obj = StRespBaseObj.parse(response1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (obj == null) {
                    showSimpleTipsDialog(TIPS_NET_ERROR);
                    return;
                }
                final int code = obj.getCode();
                final String msg = obj.getMsg();
                runOnUiThread(() -> {
                    dismissLoading();
                    if (code == 0) {
                        addUserTask(task, params);
                    } else {
                        showSimpleTipsDialog(msg);
                    }
                });
            }
        }, params);
    }

    private void addUserTask(MdFavoredUserTask task, HttpParam[] params) {
        showLoading();
        StContext.network().http().post(Phrase.POST_SUBMIT_TASK, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    dismissLoading();
                    showSimpleTipsDialog("网络错误，请稍候重试");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                dismissLoading();
                String msg = null;
                try {
                    StRespBaseObj baseObj = StRespBaseObj.parse(response);
                    msg = baseObj.getMsg();
                    runOnUiThread(() -> {
                        if (baseObj.getCode() == 0) {
                            ToastUtils.show(getActivity(), "任务添加成功");
                            task.setTask_status(1);
                            notifyDataSetChanged();
                            Intent intent = new Intent();
                            intent.putExtra("flag", FmCustomScriptHome.class.getSimpleName());
                            StContext.getInstance().sendStBroadcast(StIntentActions.CLEAR_REMOTE_TASKS, intent);
                            reload();
                            mCustomScriptHomeProxy.markNewFavoredTaskUpdated();
                        } else {
                            ToastUtils.show(getActivity(), baseObj.getMsg());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.show(getActivity(), msg == null ? "网络错误，请稍候重试" : msg);
                }
            }
        }, params);
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    protected void onLoadListFirstTime() {
        //load();
    }

    public void reload() {
        load();
    }

    private void load() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("gameid", manifest().getGameId());
        parameters.put("device_id", manifest().getDeviceId());
        showLoading();
        httpGet(Phrase.GET_USERS_TASK_LIST, parameters, mOnLoadCallack);
    }

    @Override
    public BaseRecyclerHolder generateVHByType(@NonNull View itemView, int viewType) {
        return new HdFavoredUserTaskTitle(itemView);
    }


    private DgLiteProgress mProgressDialog = null;

    @NonNull
    private DgLiteProgress getProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new DgLiteProgress(getActivity());
            mProgressDialog.setNegativeButton("取消下载", (dialog, which) -> {
                withDownloadService(() -> mDownloadScriptService.cancelCurrent());
                dialog.dismiss();
            }).setPositiveButton("最小化", (dialog, which) -> dialog.dismiss());
        }
        return mProgressDialog;
    }

    @Override
    public void onCachingRemoteScript(Intent data) {

        final List<IBaseRecyclerModel> models = getData();
        if (models == null || models.size() == 0) {
            return;
        }

        final String key = data.getStringExtra(DownloadScriptService.KEY_TASKID);
        MdFavoredUserTask task = null;
        int index = 0;
        for (int i = 0; i < models.size(); i++) {
            final IBaseRecyclerModel model = models.get(i);
            if (!(model instanceof MdFavoredUserTask)) {
                continue;
            }
            final MdFavoredUserTask tempTask = (MdFavoredUserTask) model;
            if (Useless.equals(tempTask.getId(), key)) {
                task = tempTask;
                index = i;
                break;
            }
        }

        if (task == null) {
            return;
        }

        final int fIndex = index;
        final MdFavoredUserTask fTask = task;

        handleDownloadingState(data, fTask, fIndex, false);
    }

    private void handleDownloadingState(Intent data, MdFavoredUserTask task, int fIndex, boolean forceShowDialog) {
        getSwipeRecyclerView().setSwipeItemMenuEnabled(fIndex, false);
        runOnUiThread(() -> {
            final int state = data.getIntExtra(KEY_STATE, STATE_NONE);
            final DgLiteProgress dgLiteProgress = getProgressDialog();
            switch (state) {
                case STATE_NONE:
                    getSwipeRecyclerView().setSwipeItemMenuEnabled(fIndex, true);
                    // todo: kongxian
                    System.out.println();
                    break;
                case STATE_WAITING:
                    if (dgLiteProgress.isShowing()) {
                        dgLiteProgress.dismiss();
                    }
                    if (forceShowDialog) {
                        showSimpleTipsDialog("下载等待中");
                    }
                    break;
                case STATE_DOWNLOADING:
                    final float downloadingProgress = data.getFloatExtra(KEY_PROGRESS, 0F);
                    final boolean typeRaw = data.getIntExtra(KEY_TYPE, TYPE_RAW) == TYPE_RAW;
                    dismissSimpleTipsDialog();
                    if (forceShowDialog) {
                        dgLiteProgress.show();
                    }
                    if (dgLiteProgress.isShowing()) {

                        final int downState = data.getIntExtra(KEY_DOWN_STATE, DOWN_STATE_IDLE);
                        switch (downState) {
                            /*case DOWN_STATE_IDLE:
                                break;
                            case DOWN_STATE_FETCHING:
                                break;*/
                            case DOWN_STATE_DOWNLOADING:
                            case DOWN_STATE_UNZIPPING:
                                dgLiteProgress.setTitle("温馨提示");
                                dgLiteProgress.setProgressTitle("正在" + (downState == DOWN_STATE_UNZIPPING ? "解压" : "下载") + (typeRaw ? "资源文件" : "数据文件") + ",请勿关闭客户端！");
                                dgLiteProgress.setProgressSubTitle((typeRaw ? "(2/2)" : "(1/2)"));
                                dgLiteProgress.setProgress(downloadingProgress);
                                break;
                            default:
                        }

                    }
                    break;
                case STATE_COMPLETE:
                    getProgressDialog().dismiss();
                    showSimpleTipsDialog("下载完成，请切换到本地任务进行编辑。");
                    if (mCustomScriptHomeProxy != null) {
                        mCustomScriptHomeProxy.markNewFavoredTaskUpdated();
                    }
                    getSwipeRecyclerView().setSwipeItemMenuEnabled(fIndex, true);
                    break;
                case STATE_ERROR:
                    getProgressDialog().dismiss();
                    final Intent d = data;
                    final int errorCode = d.getIntExtra(KEY_ERROR_CODE, ERROR_UNKNOWN);
                    showSimpleTipsDialog("下载失败: code " + errorCode);
                    getSwipeRecyclerView().setSwipeItemMenuEnabled(fIndex, true);
                    break;
                default:
                    getSwipeRecyclerView().setSwipeItemMenuEnabled(fIndex, true);
            }
            getListAdapter().notifyItemChanged(fIndex);
        });
    }
}
