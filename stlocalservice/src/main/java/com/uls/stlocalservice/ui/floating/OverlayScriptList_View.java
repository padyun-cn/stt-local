package com.uls.stlocalservice.ui.floating;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mon.ui.buildup.OverlayViewSimpleList;
import com.mon.ui.floating.Overlay;
import com.mon.ui.floating.OverlayAlert;
import com.mon.ui.floating.SystemWindowManager;
import com.mon.ui.list.compat.adapterForViews.View_BaseAdapter;
import com.mon.ui.list.compat.adapterForViews.View_BaseHolder;
import com.mon.ui.list.compat.adapterForViews.View_IBaseModel;
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
import com.padyun.scripttools.module.runtime.test.TestProxy;
import com.padyun.scripttools.services.biz.UploadScriptService;
import com.padyun.scripttoolscore.content.network.SockResponseUtils;
import com.uls.stlocalservice.R;
import com.uls.stlocalservice.data.vhmodels.MdOverlayScriptTitle;
import com.uls.stlocalservice.ui.activity.EditorListActivity;
import com.uls.stlocalservice.ui.activity.EditorListLandActivity;
import com.uls.stlocalservice.ui.holders.HdOverlayCustomScriptTitle;
import com.uls.utilites.common.ICCallback;
import com.uls.utilites.content.ToastUtils;
import com.uls.utilites.io.Files;
import com.uls.utilites.ui.DensityUtils;
import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.LogUtil;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMovementListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by daiepngfei on 2021-01-18
 */
@SuppressWarnings("ALL")
public class OverlayScriptList_View extends OverlayViewSimpleList {

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

    public OverlayScriptList_View(@NonNull Activity context) {
        super(context);
    }

    public OverlayScriptList_View(@NonNull Context context, @Nullable AttributeSet attrs) {
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
                    // -> do in background
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
                    // -> on post executed
                    r -> {
                        dismissLoading();
                        listRefresh(r);
                        checkRunningScript();
                    })
                    // -> execute
                    .start();
        }
    }

    private void checkRunningScript() {
        StContext.network().applyTest().getCurrentRunningScript(new ICCallback<String>() {

            @Override
            public void onSuccess(String t) {
                LogUtil.d(OverlayScriptList_View.class.getSimpleName(), "current running script is : $t");
                try {
                    final JSONObject json = new JSONObject(t);
                    final String id = json.getString("id");
                    final int state = json.getInt("state");
                    Useless.runOnUiThread(() -> {
                        foreachData(MdOverlayScriptTitle.class, it -> {
                            if (it.setStateWithId(id, state)) {
                                if (runningScriptNameChangeListener != null) {
                                    runningScriptNameChangeListener.accept(it.getName());
                                }
                            }
                        });
                        refreshList();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSendFail(int errno, String msg, Exception e) {
                LogUtil.w(OverlayScriptList_View.class.getSimpleName(), "error : $msg", e);
            }
        });
    }


    public static final int MSG_POST_ADD = 12345;
    public static final int MSG_POST_DEL = 54321;
    private MdOverlayScriptTitle toBeAdded;

    @Override
    public boolean handleMessage(Message msg) {
        if (isProcessing) {
            // ToastUtils.show(activity, "正在执行请求，请稍候")
            return false;
        }
        if (msg != null) {
            switch (msg.what) {
                case MSG_POST_ADD:
                    if(toBeAdded != null && toBeAdded.isStateRunningPre()){
                        toBeAdded.setStateWithId(toBeAdded.getScript().getId(), MdOverlayScriptTitle.STATE_IDLE);
                    }
                    final MdOverlayScriptTitle title = (MdOverlayScriptTitle) msg.obj;
                    toBeAdded = title;
                    toBeAdded.setStateWithId(title.getScript().getId(), MdOverlayScriptTitle.STATE_RUNNING_PRE);
                    refreshList();
                    // addSocketTask(title);
                    break;
                case MSG_POST_DEL:
                    final MdOverlayScriptTitle title2 = (MdOverlayScriptTitle) msg.obj;
                    if(title2.isStateRunningPre()){
                        title2.setStateWithId(title2.getScript().getId(), MdOverlayScriptTitle.STATE_IDLE);
                        toBeAdded = null;
                        refreshList();
                    } else if(title2.isStateRunning()) {
                        removeTask(title2);
                    }
                    break;

            }
        }
        return super.handleMessage(msg);
    }

    private void removeTask(MdOverlayScriptTitle title) {
        isProcessing = true;
        showLoading();
        StContext.network().applyTest().sendCmdStop(
                new SockResponseUtils.SimpleOkResponse() {

                    @Override
                    public void onFail(int errno, String msg, Exception e) {
                        super.onFail(errno, msg, e);
                        isProcessing = false;
                        dismissLoading();
                        ToastUtils.show(getContext(), R.string.string_toast_error_network);
                    }

                    @Override
                    public void onResponseOk() {
                        super.onResponseOk();
                        dismissLoading();
                        isProcessing = false;
                        if (runningScriptNameChangeListener != null) {
                            runningScriptNameChangeListener.accept("");
                        }
                        toBeAdded = null;
                        foreachData(MdOverlayScriptTitle.class, it -> it.setStateWithId(title.getScript().getId(),
                                MdOverlayScriptTitle.STATE_IDLE));
                        refreshList();
                    }
                });
    }

    private void addSocketTask(MdOverlayScriptTitle title) {
        isProcessing = true;
        showLoading();
        CustomScriptIOManager.getCoScriptFromFileAsync(title.getPath(), it -> {
            final String script = it.buildToSEScript().buildToJson();
            JSONObject startCmd = new JSONObject();
            try {
                startCmd.put("id", it.getId());
                startCmd.put("image_folder", StContext.assets().currentDirProcessed() + "/");
                startCmd.put("script", script);
            } catch (JSONException e) {
                e.printStackTrace();
                isProcessing = false;
                dismissLoading();
                ToastUtils.show(getContext(), R.string.string_toast_error_network);
                return;
            }
            System.out.println("startCmd: " + startCmd.optString("image_folder"));
            StContext.network().applyTest().sendStartCmd(startCmd.toString(), it.getAllSEImageDeDuplicated(),
                    new SockResponseUtils.SimpleOkResponse() {

                        @Override
                        public void onFail(int errono, String msg, Exception e) {
                            isProcessing = false;
                            dismissLoading();
                            ToastUtils.show(getContext(), R.string.string_toast_error_network);
                        }

                        @Override
                        public void onResponseOk() {
                            isProcessing = false;
                            dismissLoading();
                            if (runningScriptNameChangeListener != null) {
                                runningScriptNameChangeListener.accept(title.getName());
                            }
                            /*foreachData(MdOverlayScriptTitle.class, it -> it.setStateWithId(title.getScript().getId(),
                                    MdOverlayScriptTitle.STATE_RUNNING));*/
                            title.setStateWithId(title.getScript().getId(),
                                    MdOverlayScriptTitle.STATE_RUNNING);
                            toBeAdded = null;
                            refreshList();
                            ScriptOverlays.Agent.onGameNormal2(getContext());
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
                    ToastUtils.show(getContext(), R.string.string_toast_create_new_script_failed);
                }
                runnable.run();
            });
        } else {
            runnable.run();
        }
    }

    private void loadScriptWithFile(String f) {
        ScriptOverlays.Agent.onEditorList(getContext());
        StContext.manifest().setScriptFilePath(f);
        Class c = SystemWindowManager.Current.isLandscape(getContext()) ? EditorListLandActivity.class : EditorListActivity.class;
        Intent intent = new Intent(getContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(FmScriptEditor.FILE_PATH, f);
        getContext().startActivity(intent);
    }

    @Override
    public void onInitSwipeReclcerView(SwipeRecyclerView recyclerView, View_BaseAdapter adapter) {
        adapter.setOnItemClickListener((a, item, b) -> {
            MdOverlayScriptTitle m = (MdOverlayScriptTitle) item;
            loadScriptWithFile(m.getPath())/*)*/;
        });

        recyclerView.setSwipeMenuCreator(
                (a, rightMenu, position) -> {
                    if (/*editable &&*/ position < getDataSize()) {
                        int width = DensityUtils.dip2px(getContext(), 60f);
                        int height = ViewGroup.LayoutParams.MATCH_PARENT;
                        SwipeMenuItem uploadItem = new SwipeMenuItem(getContext());
                        SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
                        deleteItem
                                .setWidth(width)
                                .setHeight(height)
                                .setTextSize(10)
                                .setTextColor(Color.WHITE)
                                .setBackgroundColor(Color.parseColor("#FF3B32"))
                                .setText(R.string.string_delete);
                        SwipeMenuItem renameItem = new SwipeMenuItem(getContext());
                        renameItem
                                .setBackgroundColor(Color.parseColor("#7baB32"))
                                .setWidth(width)
                                .setHeight(height)
                                .setTextSize(10)
                                .setTextColor(Color.WHITE)
                                .setText(R.string.string_rename);
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
                            case 1:

                                OverlayAlert.build(overlay.getOverlayContext())
                                        .setTitle(R.string.string_dialog_title_tip)
                                        .setMessage(getResString(R.string.string_dialog_create_new_script_message, ((MdOverlayScriptTitle) m).getName()))
                                        .setPositiveButton(R.string.string_confirm,
                                                (dialog, which) -> {
                                                    dialog.dismiss();
                                                    adapter.removeItemAtPosition(adapterPosition);
                                                    Files.delete(((MdOverlayScriptTitle) m).getPath());
                                                    if (adapter.isEmpty()) {
                                                        listRefresh(new ArrayList<>());
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                })
                                        .setNegativeButton(R.string.string_cancel, (dialog, which) -> dialog.dismiss())
                                        .show();
                                break;
                            default:
                        }
                    }
                });
    }

    private String getResString(int id, String name) {
        return getContext().getResources().getString(id, name);
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

    public void refresh() {
        load();
    }


    public void setRunningScriptNameChangeListener(Consumer<String> nameChangeListener) {
        this.runningScriptNameChangeListener = nameChangeListener;
    }

    @Override
    protected void onInitBottomView(LayoutInflater inflater, FrameLayout topLayout) {
        View v = inflater.inflate(R.layout.button_floating_overlay_scripts_list_bottom, topLayout, true);
        v.findViewById(R.id.button_run).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toBeAdded != null){
                    addSocketTask(toBeAdded);
                } else {
                    ToastUtils.show(getContext(), "尚未添加任务");
                }
            }
        });
    }

    @Override
    protected void showLoading() {
        if (overlay != null) {
            overlay.showLoading();
        } else {
            super.showLoading();
        }
    }

    @Override
    protected void dismissLoading() {
        if (overlay != null) {
            overlay.dismissLoading();
        } else {
            super.dismissLoading();
        }
    }
}