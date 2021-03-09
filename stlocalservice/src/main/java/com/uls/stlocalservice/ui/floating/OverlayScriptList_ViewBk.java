package com.uls.stlocalservice.ui.floating;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mon.ui.buildup.OverlayViewSimpleList;
import com.mon.ui.floating.Overlay;
import com.mon.ui.floating.OverlayAlert;
import com.mon.ui.floating.SystemWindowManager;
import com.mon.ui.list.compat.adapterForViews.View_BaseAdapter;
import com.mon.ui.list.compat.adapterForViews.View_BaseHolder;
import com.mon.ui.list.compat.adapterForViews.View_IBaseModel;
import com.padyun.manifest.ApiStatusCodes;
import com.padyun.scripttools.biz.ui.content.Constants;
import com.padyun.scripttools.biz.ui.data.StRespBaseObj;
import com.padyun.scripttools.biz.ui.dialogs.upload.DgLiteProgress;
import com.padyun.scripttools.biz.ui.fragment.FmCustomScriptHome;
import com.padyun.scripttools.biz.ui.fragment.FmScriptEditor;
import com.padyun.scripttools.compat.data.CoScript;
import com.uls.utilites.content.CoreWorkers;
import com.padyun.scripttools.content.data.CustomScriptIOManager;
import com.padyun.scripttools.content.data.CustomScriptStorage;
import com.padyun.scripttools.content.data.FPathScript;
import com.padyun.scripttools.content.network.ScriptTestProxyService;
import com.padyun.scripttools.module.runtime.ScriptManager;
import com.padyun.scripttools.module.runtime.StContext;
import com.padyun.scripttools.module.runtime.StIntentActions;
import com.padyun.scripttools.module.runtime.test.TestProxy;
import com.padyun.scripttools.services.biz.UploadScriptService;
import com.padyun.scripttoolscore.content.network.SockResponseUtils;
import com.padyun.scripttoolscore.models.HttpParam;
import com.uls.stlocalservice.R;
import com.uls.stlocalservice.data.vhmodels.MdOverlayScriptTitle;
import com.uls.stlocalservice.ui.activity.EditorListActivity;
import com.uls.stlocalservice.ui.activity.EditorListLandActivity;
import com.uls.stlocalservice.ui.holders.HdOverlayCustomScriptTitle;
import com.uls.utilites.content.ToastUtils;
import com.uls.utilites.io.Files;
import com.uls.utilites.ui.DensityUtils;
import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.LogUtil;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMovementListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by daiepngfei on 2021-01-18
 */
@SuppressWarnings("ALL")
public class OverlayScriptList_ViewBk extends OverlayViewSimpleList {

    private boolean isProcessing = false;
    private TestProxy testProxy = null;
    private Consumer<String> runningScriptNameChangeListener = null;
    private ServiceConnection uploadServiceConnection = null;
    private UploadScriptService mUploadScriptService = null;
    private DgLiteProgress mDgUploadProgress = null;
    private FmCustomScriptHome.CustomScriptHomeProxy mCustomScriptHomeProxy = null;
    private Overlay overlay;

    public void setOverlay(Overlay overlay) {
        this.overlay = overlay;
    }

    public OverlayScriptList_ViewBk(@NonNull Activity context) {
        super(context);
    }

    public OverlayScriptList_ViewBk(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCustomScriptHomeProxy(FmCustomScriptHome.CustomScriptHomeProxy proxy) {
        this.mCustomScriptHomeProxy = proxy;
    }

    @Override
    protected void onLoadListFirstTime() {
        testProxy = ScriptTestProxyService.apply(StContext.manifest().getDeviceIp(),
                StContext.manifest().getDeviceAsIp());
    }


    @Override
    protected ConfigOption getCustomConfigOption() {
        ConfigOption configuration = genDefaultConfigOption();
        configuration.setAutoFirstLoad(false);
        return configuration;
    }

    @Override
    public View_BaseHolder generateVHByType(@NonNull View itemView, int viewType) {
        return new HdOverlayCustomScriptTitle(itemView);
    }

    private void load() {

        final String userIdOrMobile = StContext.manifest().getUserId();
        final String gameId = StContext.manifest().getGameId();
        final File[] scriptFiles = CustomScriptIOManager.getCustomScriptFilesWithGameId(userIdOrMobile, gameId);
        // Toast.makeText(getContext(), "" + userIdOrMobile + " | " + gameId + " | " + FPathScript.scriptDataDir(), Toast.LENGTH_LONG).show();
        if (scriptFiles != null && scriptFiles.length > 0) {
            showLoading();
            new CoreWorkers<List<View_IBaseModel>>(
                    () -> {
                        final ArrayList<View_IBaseModel> list = new ArrayList<>();
                        Useless.foreach(scriptFiles, file -> {
                            File f = file;
                            /*compat code*/
                            String dPath = f.getAbsolutePath();
                            if (".sce".equals(Files.ext(dPath))) {
                                // dPath = Files_.replaceExt(dPath, "ai")
                                dPath = dPath.replace(".sce", ".ai");
                                if (Files.exists(f)) {
                                    CoScript script = null;
                                    try {
                                        script = CustomScriptIOManager.parseToCoScript(Files.readWholeFileString(f));
                                        if (CustomScriptStorage.directSave(dPath, script)) {
                                            Files.delete(f);
                                            f = new File(dPath);
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            /*compat code*/
                            CoScript coScript = CustomScriptIOManager.getCoScriptFromFileSync(f);
                            if (coScript != null && coScript.getId() != null) {
                                if (!Useless.isEmpty(coScript.getPublishedID())) {
                                    StContext.script().registerOrUpdateScriptDesc(coScript.getPublishedID(),
                                            ScriptManager.SFileDesc(dPath, coScript.getPublishingCode()));
                                }
                                list.add(new MdOverlayScriptTitle(f, coScript));
                            }
                        });
                        return list;

                    },
                    r -> {
                        dismissLoading();
                        listRefresh(r);
                        checkUploadingScript();
                        checkRunningScript();
                    })
                    .start();
        }
    }


    private void checkUploadingScript() {
        withBindedUploadService(() -> foreachData(MdOverlayScriptTitle.class,
                it -> mUploadScriptService.queryState(it.getPath(), mOnUploadListener)));
    }

    private void checkRunningScript() {

//        StContext.network().applyTest().getCurrentRunningScript(new ICCallback<String>() {
//
//            @Override
//            public void onSuccess(String t) {
//                LogUtil.d(OverlayScriptList_View.class.getSimpleName(), "current running script is : $t");
//                try {
//                    final JSONObject json = new JSONObject(t);
//                    final String id = json.getString("id");
//                    final int state = json.getInt("state");
//                    Useless.runOnUiThread(() -> {
//                        foreachData(MdOverlayScriptTitle.class, it -> {
//                            if (it.setStateWithId(id, state)) {
//                                runningScriptNameChangeListener.accept(it.getName());
//                            }
//                        });
//                        refreshList();
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onSendFail(int errno, String msg, Exception e) {
//                LogUtil.w(OverlayScriptList_View.class.getSimpleName(), "error : $msg", e);
//            }
//        });
    }

    public static final int MSG_POST_ADD = 12345;
    public static final int MSG_POST_DEL = 54321;

    @Override
    public boolean handleMessage(Message msg) {
        if (isProcessing) {
            // ToastUtils.show(activity, "正在执行请求，请稍候")
            return false;
        }
        if (msg != null) {
            switch (msg.what) {
                case MSG_POST_ADD:
                    isProcessing = true;
                    final MdOverlayScriptTitle title = (MdOverlayScriptTitle) msg.obj;
                    withRemoteTasksClear(title, () -> addSocketTask(title));
                    break;
                case MSG_POST_DEL:
                    final MdOverlayScriptTitle title2 = (MdOverlayScriptTitle) msg.obj;
                    removeTask(title2);
                    break;

            }
        }
        return super.handleMessage(msg);
    }

    private void removeTask(MdOverlayScriptTitle title) {
        isProcessing = true;
        showLoading();
        testProxy.sendCmdStop(
                new SockResponseUtils.SimpleOkResponse() {

                    @Override
                    public void onFail(int errno, String msg, Exception e) {
                        super.onFail(errno, msg, e);
                        isProcessing = false;
                        dismissLoading();
                        ToastUtils.show(getContext(), "网络错误，请稍候重试");
                    }

                    @Override
                    public void onResponseOk() {
                        super.onResponseOk();
                        dismissLoading();
                        isProcessing = false;
                        runningScriptNameChangeListener.accept("");
                        foreachData(MdOverlayScriptTitle.class, it -> it.setStateWithId(title.getScript().getId(),
                                MdOverlayScriptTitle.STATE_IDLE));
                        refreshList();
                    }
                });
    }

    private void withRemoteTasksClear(MdOverlayScriptTitle title, Runnable then) {
        showLoading();
        StContext.network().http().post(Constants.ApiPath.Phrase.POST_TASK_STATUS,
                new Callback() {


                    @Override
                    public void onFailure(Call call, IOException e) {
                        onCallbackError("网络错误，请稍候重试");
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        StRespBaseObj _obj = null;
                        try {
                            _obj = StRespBaseObj.parse(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (_obj == null) {
                            onCallbackError("网络错误，请稍候重试");
                            return;
                        }
                        final StRespBaseObj obj = _obj;
                        if (obj.getCode() == 0) {
                            dismissLoading();
                            then.run();
                        } else {
                            Useless.runOnUiThread(() -> {
                                dismissLoading();
                                if (obj.getCode() == ApiStatusCodes.TASK_RUNNING_TYPE_USER ||
                                        obj.getCode() == ApiStatusCodes.TASK_RUNNING_TYPE_SYSTEM) {
                                    showJZDialog(obj);
                                } else {
                                    ToastUtils.show(getContext(), obj.getMsg());
                                }
                                isProcessing = false;
                            });
                        }
                    }

                    private void showJZDialog(StRespBaseObj obj) {
                        OverlayAlert.build(overlay.getOverlayContext())
                                .setTitle("温馨提示")
                                .setMessage(obj.getMsg())
                                .setNegativeButton("取消", (d, p) -> {
                                    isProcessing = false;
                                    dismissLoading();
                                    d.dismiss();
                                })
                                .setPositiveButton("确定", (d, p) ->
                                {
                                    dismissLoading();
                                    d.dismiss();
                                    doClearTasks();
                                })
                                .show();
                    }

                    private void doClearTasks() {
                        showLoading();
                        StContext.network().http().post(Constants.ApiPath.Phrase.POST_CLEAR_TASK,
                                new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        onCallbackError("网络错误，请稍候重试");
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        StRespBaseObj obj = null;
                                        try {
                                            obj = StRespBaseObj.parse(response);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (obj == null) {
                                            onCallbackError("网络错误，请稍候重试");
                                            return;
                                        }
                                        if (obj.getCode() == 0) {
                                            Useless.runOnUiThread(() -> {
                                                dismissLoading();
                                                mCustomScriptHomeProxy.markTitleListUpdated();
                                                final Intent intent = new Intent();
                                                intent.putExtra("flag", FmCustomScriptHome.class.getSimpleName());
                                                StContext.getInstance().sendStBroadcast(StIntentActions.CLEAR_REMOTE_TASKS,
                                                        intent);
                                                then.run();
                                            });
                                        } else {
                                            onCallbackError(obj.getMsg());
                                        }
                                    }
                                },
                                new HttpParam("device_id", StContext.manifest().getDeviceId()),
                                new HttpParam("game_id", StContext.manifest().getGameId()),
                                new HttpParam("task_id", title.getScript().getPublishedID())
                        );
                    }

                    private void onCallbackError(String msg) {
                        Useless.runOnUiThread(() -> {
                            dismissLoading();
                            isProcessing = false;
                            ToastUtils.show(getContext(), msg);
                        });
                    }
                }
                ,
                new HttpParam("device_id", StContext.manifest().getDeviceId()),
                new HttpParam("game_id", StContext.manifest().getGameId()),
                new HttpParam("task_id", title.getScript().getPublishedID())
        );
    }

    private void addSocketTask(MdOverlayScriptTitle title) {
        showLoading();
        CustomScriptIOManager.getCoScriptFromFileAsync(title.getPath(), it -> {
            testProxy.sendStartCmd(it.buildToSEScript().buildToJson(), it.getAllSEImageDeDuplicated(),
                    new SockResponseUtils.SimpleOkResponse() {

                        @Override
                        public void onFail(int errono, String msg, Exception e) {
                            isProcessing = false;
                            dismissLoading();
                            ToastUtils.show(getContext(), "网络错误，请稍候重试");
                        }

                        @Override
                        public void onResponseOk() {
                            isProcessing = false;
                            dismissLoading();
                            runningScriptNameChangeListener.accept(title.getName());
                            foreachData(MdOverlayScriptTitle.class, it -> it.setStateWithId(title.getScript().getId(),
                                    MdOverlayScriptTitle.STATE_RUNNING));
                            refreshList();
                        }

                    });
        });
    }


    @Override
    public View onCreateEmptyView(LayoutInflater inflater, ViewGroup container) {
        View buttonLayout = inflater.inflate(R.layout.view_empty_new_custom_tasks, container, false);
        View button = buttonLayout.findViewById(R.id.buttonNewCustomTask);
        button.setOnClickListener(v -> createNewScriptWithNewName());
        return buttonLayout;
    }

    public void createNewScriptWithNewName() {
        final OverlayNewScriptDialog dg = new OverlayNewScriptDialog(overlay.getOverlayContext(),
                (dialogInterface, name, sip, asip) -> {
                    dialogInterface.dismiss();
                    createNewScript(name, dialogInterface::dismiss);
                });
        dg.show();
    }

    private void createNewScript(String name, Runnable runnable) {
        File f = FPathScript.scriptDataFile(name);
        boolean result = false;
        try {
            result = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) {
            CoScript script = CoScript.newScript();
            CustomScriptStorage.saveAsync(f.getAbsolutePath(), script, it -> {
                if (it) {
                    loadScriptWithFile(f.getAbsolutePath());
                    listAppend(new MdOverlayScriptTitle(f, script));
                } else {
                    ToastUtils.show(getContext(), "创建新脚本失败");
                }
                runnable.run();
            });
        } else {
            runnable.run();
        }
    }

    private void loadScriptWithFile(String f) {
        Class c = SystemWindowManager.Current.isLandscape(getContext()) ? EditorListLandActivity.class : EditorListActivity.class;
        Intent intent = new Intent(getContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(FmScriptEditor.FILE_PATH, f);
        getContext().startActivity(intent);
        ScriptOverlays.Agent.onEditorList(getContext());
    }

    @Override
    public void onInitSwipeReclcerView(SwipeRecyclerView recyclerView, View_BaseAdapter adapter) {
        adapter.setOnItemClickListener((a, item, b) -> {
            MdOverlayScriptTitle m = (MdOverlayScriptTitle) item;
            if (m.getEnableEditing()) {
                /*withRemoteTasksClear(m, () -> */
                loadScriptWithFile(m.getPath())/*)*/;
            } else {
                withBindedUploadService(() -> {
                    mUploadScriptService.queryState(m.getPath(), it -> {
                        handleUIWithUploadState(m, it, true);
                    });
                });
            }
        });

        recyclerView.setSwipeMenuCreator(
                (a, rightMenu, position) -> {
                    if (/*editable &&*/ position < getDataSize()) {
                        int width = DensityUtils.dip2px(getContext(), 60f);
                        int height = ViewGroup.LayoutParams.MATCH_PARENT;
                        SwipeMenuItem uploadItem = new SwipeMenuItem(getContext());
                        uploadItem.setWidth(width)
                                .setHeight(height)
                                .setTextSize(10)
                                .setTextColor(Color.WHITE)
                                .setBackgroundColor(Color.parseColor("#389CFF"))
                                .setText("上传云端");
                        SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                        deleteItem
                                .setWidth(width)
                                .setHeight(height)
                                .setTextSize(10)
                                .setTextColor(Color.WHITE)
                                .setBackgroundColor(Color.parseColor("#FF3B32"))
                                .setText("删除");
                        SwipeMenuItem renameItem = new SwipeMenuItem(getContext());
                        renameItem
                                .setBackgroundColor(Color.parseColor("#7baB32"))
                                .setWidth(width)
                                .setHeight(height)
                                .setTextSize(10)
                                .setTextColor(Color.WHITE)
                                .setText("重命名");
                        rightMenu.addMenuItem(uploadItem);
                        rightMenu.addMenuItem(renameItem);
                        rightMenu.addMenuItem(deleteItem);
                    }
                });


        recyclerView.setOnItemMovementListener(new OnItemMovementListener() {
            @Override
            public int onDragFlags(RecyclerView p0, RecyclerView.ViewHolder p1) {
                return 0;
            }

            @Override
            public int onSwipeFlags(RecyclerView p0, RecyclerView.ViewHolder p1) {
                LogUtil.d("linshi", "linshi");
                return ItemTouchHelper.LEFT;
            }
        });
        recyclerView.setOnItemMenuClickListener(
                (menuBridge, adapterPosition) -> {
                    menuBridge.closeMenu();
                    int pos = menuBridge.getPosition();
                    Object m = adapter.get(adapterPosition);
                    if (/*editable &&*/ m instanceof MdOverlayScriptTitle) {
                        switch (pos) {
                            case 0:
                                String taskId = ((MdOverlayScriptTitle) m).getScript().getPublishedID();
                                if (taskId != null && taskId.length() > 0) {
                                    showLoading();
                                    StContext.network().http().get(
                                            Constants.ApiPath.Phrase.GET_CHECK_TASK_STATUS,
                                            new Callback() {
                                                @Override
                                                public void onFailure(Call call, IOException e) {
                                                    Useless.runOnUiThread(() -> {
                                                        dismissLoading();
                                                        ToastUtils.show(getContext(), "网络错误，请稍候重试");
                                                    });
                                                }

                                                @Override
                                                public void onResponse(Call call, Response response) {
                                                    StRespBaseObj _obj = null;
                                                    try {
                                                        _obj = StRespBaseObj.parse(response);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    final StRespBaseObj obj = _obj;
                                                    Useless.runOnUiThread(() -> {
                                                        dismissLoading();
                                                        if (obj == null) {
                                                            ToastUtils.show(getContext(), "网络错误，请稍候重试");
                                                        } else {
                                                            if (obj.getCode() != 0) {
                                                                showSimpleTipsDialog(obj.getMsg());
                                                            } else {
                                                                showUploadTips((MdOverlayScriptTitle) m);
                                                            }
                                                        }
                                                    });
                                                }
                                            },
                                            new HttpParam("taskid", taskId)
                                    );
                                } else {
                                    showUploadTips((MdOverlayScriptTitle) m);
                                }
                                break;
                            case 1:
                                OverlayNewScriptDialog dg = new OverlayNewScriptDialog(overlay.getOverlayContext(), (a, name, b, c) -> {
                                    a.dismiss();
                                    File f = new File(((MdOverlayScriptTitle) m).getPath());
                                    if (f.exists()) {
                                        File newF = FPathScript.newScriptFile(Files.dir(((MdOverlayScriptTitle) m).getPath()).getAbsolutePath(), name);
                                        if (f.renameTo(newF)) {
                                            ((MdOverlayScriptTitle) m).setFile(newF);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                                );
                                dg.show();
                                break;
                            case 2:
                                OverlayAlert.build(overlay.getOverlayContext())
                                        .setTitle("提示")
                                        .setMessage("确定删除 '" + ((MdOverlayScriptTitle) m).getName() + "' 任务么？ ")
                                        .setPositiveButton("确定",
                                                (dialog, which) -> {
                                                    dialog.dismiss();
                                                    adapter.removeItemAtPosition(adapterPosition);
                                                    Files.delete(((MdOverlayScriptTitle) m).getPath());
                                                    if (adapter.isEmpty()) {
                                                        listRefresh(new ArrayList<>());
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                })
                                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                                        .show();
                                break;
                            default:
                        }
                    }
                });
    }

    /**
     * /** do
     */
    private void withBindedUploadService(Runnable withing) {
        if (uploadServiceConnection == null || mUploadScriptService == null) {
            Intent uploadServiceIntent = new Intent(getContext(), UploadScriptService.class);
            getContext().startService(uploadServiceIntent);
            uploadServiceConnection = new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    uploadServiceConnection = null;
                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mUploadScriptService = ((UploadScriptService.ScriptUploadBinder) service).getUploaderService();
                    mUploadScriptService.addUploadingStateChangeListener(mOnUploadListener);
                    withing.run();
                }
            };
            getContext().bindService(uploadServiceIntent, uploadServiceConnection, Service.BIND_AUTO_CREATE);
        } else {
            withing.run();
        }
    }

    /**
     *
     */
    private final UploadScriptService.OnUploadingStateChangedListener mOnUploadListener =
            data -> {

                // httpGet & check the uploading-key
                String key = data.getStringExtra(UploadScriptService.KEY_KEY);
                if (key == null || key.isEmpty()) {
                    return;
                }

                // query the item with same key(file path)
                MdOverlayScriptTitle item = null;
                for (int i = 0; i < getDataSize(); i++) {
                    final Object obj = getData().get(i);
                    if (obj instanceof MdOverlayScriptTitle) {
                        if (key.equals(((MdOverlayScriptTitle) obj).getPath())) {
                            item = (MdOverlayScriptTitle) obj;
                            break;
                        }
                    }
                }
                if (item == null) {
                    return;
                }

                MdOverlayScriptTitle fItem = item;

                Useless.runOnUiThread(() -> handleUIWithUploadState(fItem, data, false));
            };


    private void showUploadingStateTips(String s) {
        OverlayAlert.build(overlay.getOverlayContext())
                .setTitle("温馨提示")
                .setExclusiveNaturalButton("知道了", (d, w) -> {
                    d.dismiss();
                })
                .setMessage(s).show();
    }

    /**
     *
     */
    private void handleUIWithUploadState(MdOverlayScriptTitle item, Intent data, boolean forceShowDialog) {
        item.setEnableEditing(true);
        item.setSubTitle(null);
        // httpGet running state
        final int index = getData().indexOf(item);
        enableItemSwipe(index);
        final int state = data.getIntExtra(UploadScriptService.KEY_STATE, UploadScriptService.STATE_NONE);

        switch (state) {
            case UploadScriptService.STATE_NONE:
                item.setEnableEditing(true);
                item.setSubTitle(null);
                break;
            case UploadScriptService.STATE_WAITTING:
                disableItemSwipe(index);
                item.setEnableEditing(false);
                item.setSubTitle("等待中");
                break;
            case UploadScriptService.STATE_RUNNING:
                disableItemSwipe(index);
                item.setEnableEditing(false);
                item.setSubTitle("正在上传中，请稍后");
                final int actionState = data.getIntExtra(UploadScriptService.KEY_UP_ACTION_STATE, UploadScriptService.ACTION_READY);
                final int upState = data.getIntExtra(UploadScriptService.KEY_UP_STATE, UploadScriptService.UP_STATE_READY);
                switch (upState) {
                    case UploadScriptService.UP_STATE_ZIPPING:
                        item.setSubTitle("正在压缩，请稍后");
                        final DgLiteProgress dg = getUploadProgressBar();
                        if (forceShowDialog) {
                            dg.show();
                        }
                        if (dg.isShowing()) {
                            //dg.setProgress(0.5f)
                            dg.setProgress(data.getFloatExtra(UploadScriptService.KEY_UP_PROGRESS, 0F));
                            final String zippingInfo = Useless.nonNullStr(data.getStringExtra(UploadScriptService.KEY_ZIP_INFO));
                            final boolean isTP = data.getIntExtra(UploadScriptService.KEY_UP_TYPE, UploadScriptService.TYPE_PROCCESSED) == UploadScriptService.TYPE_PROCCESSED;
                            dg.setProgressSubTitle(zippingInfo + (isTP ? "(1/2)" : "(2/2)"));
                        }
                        break;
                    case UploadScriptService.UP_STATE_UPLOADING:
                        item.setSubTitle("正在上传，请稍候");
                        final DgLiteProgress dg2 = getUploadProgressBar();
                        if (forceShowDialog) {
                            dg2.show();
                        }
                        if (dg2.isShowing()) {
                            //dg.setProgress(0.5f)
                            final boolean isTP = data.getIntExtra(UploadScriptService.KEY_UP_TYPE, UploadScriptService.TYPE_PROCCESSED) == UploadScriptService.TYPE_PROCCESSED;
                            dg2.setProgress(data.getFloatExtra(UploadScriptService.KEY_UP_PROGRESS, 0F));
                            dg2.setProgressTitle("正在上传" + (isTP ? "脚本，请勿关闭客户端！" : "可编辑的本地数据\n截图较大，可能耗时过长，请耐心等待" + ""));
                            dg2.setProgressSubTitle((isTP ? "(1/2)" : "(2/2)"));
                        }
                        break;
                    case UploadScriptService.UP_STATE_PUBLISHING:
                        item.setSubTitle("正在发布中，请稍后");
                        break;
                    case UploadScriptService.UP_STATE_COMPLETE:
                        enableItemSwipe(index);
                        item.setSubTitle(null);
                        if (actionState == UploadScriptService.ACTION_DONE) {
                            getUploadProgressBar().dismiss();
                            showUploadingStateTips("上传成功，审核通过后将发送通知至【消息中心】，并开通分享任务给好友权限！");
                            if (data.getStringArrayExtra(UploadScriptService.KEY_PUBLISHED_ID) != null) {
                                item.getScript().setPublishedID(data.getStringExtra(UploadScriptService.KEY_PUBLISHED_ID));
                            }
                            if (mCustomScriptHomeProxy != null) {
                                mCustomScriptHomeProxy.markTitleListUpdated();
                            }
                        }
                        break;
                    case UploadScriptService.UP_STATE_ERROR:
                        enableItemSwipe(index);
                        item.setSubTitle("上传出现错误");
                        break;
                    default:
                }
                break;
            case UploadScriptService.STATE_REFUSED:
                final String msg = data.getStringExtra(UploadScriptService.KEY_MSG);
                showUploadingStateTips(msg);
                break;
            case UploadScriptService.STATE_CANCELED:
                showUploadingStateTips("本次上传已经取消");
                enableItemSwipe(index);
                break;
            case UploadScriptService.STATE_ERROR:
                getUploadProgressBar().dismiss();
                final String msg2 = data.getStringExtra(UploadScriptService.KEY_MSG);
                showUploadingStateTips((Useless.isEmpty(msg2)) ? "发布失败，请稍候重试。" : msg2);
                enableItemSwipe(index);
                break;
            default:
                enableItemSwipe(index);
        }

        getListAdapter().notifyItemChanged(getData().indexOf(item));
    }

    private void disableItemSwipe(int index) {
        if (index >= 0) {
            getSwipeRecyclerView().setSwipeItemMenuEnabled(index, false);
        }
    }

    private void enableItemSwipe(int index) {
        if (index >= 0) {
            getSwipeRecyclerView().setSwipeItemMenuEnabled(index, true);
        }
    }

    private void showUploadTips(MdOverlayScriptTitle m) {
        OverlayAlert.build(overlay.getOverlayContext()).setTitle("上传说明")
                .setMessage("上传任务到云端分为两个部分：\n\n"
                        + "1.上传不可编辑的任务，作为审核用，上传完成后任务可添加，但不可编辑。\n\n"
                        + "2.上传所有可编辑的文件，上传完成后更换手机可以下载到新手机。\n\n"
                        + "注：可编辑文件为游戏截图，截图可能较大，建议在WIFI环境下上传。")
                .setPositiveButton("继续",
                        (dialog, w) -> {

                            dialog.dismiss();
                            doCompress(m);
                        })
                .setNegativeButton("取消",
                        (dialog, w) -> {
                            dialog.dismiss();
                        })
                .show();
    }


    private void doCompress(MdOverlayScriptTitle m) {

        // progress dialog
        final DgLiteProgress dgUploadProgress = getUploadProgressBar();
        dgUploadProgress.setTitle("温馨提示");
        dgUploadProgress.setProgressTitle("正在压缩，请耐心等待");
        dgUploadProgress.setProgressSubTitle("(1/2)");
        dgUploadProgress.setProgress(0F);
        dgUploadProgress.setPositiveButton("最小化", (dialog, w) -> {
            dialog.dismiss();
        });
        dgUploadProgress.setNegativeButton("取消操作", (dialog, w) -> {
            withBindedUploadService(() -> {
                mUploadScriptService.cancel(m.getPath());
                dialog.dismiss();
            });
        });
        dgUploadProgress.show();

        // start service
        withBindedUploadService(() -> {
            mUploadScriptService.upload(m.getPath(),
                    StContext.manifest().getGameId(), StContext.manifest().getUserToken());
        });
    }

    private DgLiteProgress getUploadProgressBar() {
        if (mDgUploadProgress == null) {
            mDgUploadProgress = new DgLiteProgress(getContext());
        }
        return mDgUploadProgress;
    }


    void onDestroy() {
        if (uploadServiceConnection != null) {
            mUploadScriptService.removeUploadingStateChangeListener(mOnUploadListener);
            getContext().unbindService(uploadServiceConnection);
        }
    }

    public void refresh() {
        load();
    }


    public void setRunningScriptNameChangeListener(Consumer<String> nameChangeListener) {
        this.runningScriptNameChangeListener = nameChangeListener;
    }

    @Override
    protected void showLoading() {
        System.out.println("xiao xiao5 ");
        if (overlay != null) {
            overlay.showLoading();
        } else {
            super.showLoading();
        }
    }

    @Override
    protected void dismissLoading() {
        if (overlay != null) {
            System.out.println("xiao xiao6 ");
            overlay.dismissLoading();
        } else {
            super.dismissLoading();
        }
    }
}