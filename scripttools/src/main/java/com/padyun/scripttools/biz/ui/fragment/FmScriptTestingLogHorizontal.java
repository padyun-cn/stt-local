package com.padyun.scripttools.biz.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mon.ui.buildup.CvCoreSingleChildDragableLayout;
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.content.UScriptPlayer;
import com.padyun.scripttools.biz.ui.holders.logs.CoHolderLogSp;
import com.padyun.scripttools.compat.data.AbsCoConditon;
import com.padyun.scripttools.compat.data.AbsCoImage;
import com.padyun.scripttools.compat.data.CoScript;
import com.padyun.scripttools.compat.data.CropImgGroupParceble;
import com.padyun.scripttools.compat.data.logs.CoLogSpItem;
import com.padyun.scripttools.content.data.CustomScriptIOManager;
import com.padyun.scripttoolscore.compatible.plugin.RuntimeInfo;
import com.uls.utilites.content.ToastUtils;
import com.uls.utilites.un.Useless;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * Created by daiepngfei on 2020-01-15
 */
public class FmScriptTestingLogHorizontal extends Fragment implements Handler.Callback, BaseV2RecyclerAdapter.VHComponent, UScriptPlayer.ILogCatcher, UScriptPlayer.OnStateChangedListener, UScriptPlayer.IScriptLoader {
    public static final String TAG = "FmScriptTestingLogHorizontal#";
    private View rootView;
    private View handleUp, handleDown;
    private ImageView btnPlay, btnStop;
    private SwipeRecyclerView recyclerView;
    private Handler handler = new Handler(this);
    private UScriptPlayer uScriptController;
    private String scriptPath;
    private BaseV2RecyclerAdapter adapter;
    private ArrayList<Parcelable> newEntities;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fr_script_log_horizontal, container, false);
            handleUp = rootView.findViewById(R.id.btnHandleUp);
            handleDown = rootView.findViewById(R.id.btnHandleDown);
            final CvCoreSingleChildDragableLayout handleUpContainer = rootView.findViewById(R.id.handleUpContainer);
            final View handleDownContainer = rootView.findViewById(R.id.handleDownContainer);
            handleDown.setOnClickListener(v -> {
                handleDownContainer.setVisibility(View.GONE);
                handleUpContainer.setVisibility(View.VISIBLE);
            });
            handleUp.setOnClickListener(v -> {
                handleUpContainer.setVisibility(View.GONE);
                handleDownContainer.setVisibility(View.VISIBLE);
            });

            btnPlay = rootView.findViewById(R.id.btnStart);
            btnPlay.setOnClickListener(v -> {
                if (uScriptController != null) {
                    uScriptController.start();
                }
            });
            btnStop = rootView.findViewById(R.id.btnStop);
            btnStop.setOnClickListener(v -> {
                if (uScriptController != null) {
                    uScriptController.stop();
                }
            });
            recyclerView = rootView.findViewById(R.id.listLog);
            final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(manager);
        }
        onInit();
        return rootView;
    }

    public void config(ArrayList<Parcelable> newEntities, String path) {
        this.newEntities = newEntities;
        this.scriptPath = path;
    }

    private void onInit() {
        //noinspection ConstantConditions
        adapter = new BaseV2RecyclerAdapter(getActivity(), this, handler);
        adapter.setDataLimit(500);
        recyclerView.setAdapter(adapter);
        uScriptController = new UScriptPlayer(scriptPath, this, this);
        uScriptController.setScriptLoader(this);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        stop();
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public BaseRecyclerHolder generateVHByType(@NonNull View itemView, int viewType) {
        if (viewType == R.layout.item_log_sp) {
            return new CoHolderLogSp(itemView);
        }
        return null;
    }

    @Override
    public void onScriptControlStateChanged(UScriptPlayer.State tstate) {
        handler.post(() -> {
            switch (tstate) {
                case IDLE:
                    if(adapter != null){
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                    }
                    btnPlay.setImageResource(R.drawable.ic_button_script_stream_log_play);
                    btnPlay.setOnClickListener(v -> {
                        if (uScriptController != null) {
                            uScriptController.start();
                        }
                    });
                    break;
                case PAUSED:
                    btnPlay.setImageResource(R.drawable.ic_button_script_stream_log_play);
                    btnPlay.setOnClickListener(v -> {
                        if (uScriptController != null) {
                            uScriptController.resume();
                        }
                    });
                    break;
                case RESUMED:
                case STARTED:
                    btnPlay.setImageResource(R.drawable.ic_button_script_stream_log_pause);
                    btnPlay.setOnClickListener(v -> {
                        if (uScriptController != null) {
                            uScriptController.pause(false);
                        }
                    });
                    break;
                case UNPAUSED:
                case UNRESUMED:
                case UNSTOPPED:
                case UNSTARTED:
                    ToastUtils.show(getActivity(), "网络错误，请稍候再试。");
                    break;
                default:
            }

            switch (tstate) {
                case PAUSING:
                case STARTING:
                case RESUMING:
                    btnPlay.setEnabled(false);
                    btnStop.setEnabled(false);
                    break;
                default:
                    btnPlay.setEnabled(true);
                    btnStop.setEnabled(true);

            }
        });
    }

    @Override
    public void onContinueCachingLogs(RuntimeInfo log, AbsCoConditon conditon, boolean touched) {
        if (conditon instanceof AbsCoImage) {
            handler.post(() -> {
                adapter.addItem(new CoLogSpItem((AbsCoImage<?>) conditon, touched));
                adapter.notifyItemInserted(adapter.getItemCount() - 1);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            });
        }
    }

    @Override
    public CoScript onLoadingCoScript(String path) {
        if (Useless.isEmpty(path)) {
            return null;
        }
        CoScript script = CustomScriptIOManager.getCoScriptFromFileSync(path);
        if (script != null) {
            List<AbsCoConditon> conditions = new ArrayList<>();
            Useless.foreach(newEntities, t -> {
                if (t instanceof CropImgGroupParceble) {
                    final CropImgGroupParceble cropImgGroupParceble = (CropImgGroupParceble) t;
                    final AbsCoConditon condition = CropImgGroupParceble.conAbsCondition(cropImgGroupParceble);
                    if(condition != null){
                        conditions.add(condition);
                    }
                }
                /*if (t instanceof CropImgParceble) {
                    final CropImgParceble cropImgParceble = (CropImgParceble) t;
                    switch (cropImgParceble.getMode()) {
                        case CropImgParceble.CLICK:
                            conditions.add(new CoClick(cropImgParceble.getImage()));
                            break;
                        case CropImgParceble.OFFSET:
                            CoOffset offset = new CoOffset(cropImgParceble.getImage());
                            offset.setOffset(cropImgParceble.getOffsetX(), cropImgParceble.getOffsetY());
                            conditions.add(offset);
                            break;
                        case CropImgParceble.SLIDE:
                            CoSlide slide = new CoSlide(cropImgParceble.getImage());
                            slide.setSlide(cropImgParceble.getSlideStart(), cropImgParceble.getSlideEnd());
                            conditions.add(slide);
                            break;
                        case CropImgParceble.FINISH:
                            CoFinish finish = new CoFinish(cropImgParceble.getImage());
                            conditions.add(finish);
                            break;
                        default:
                    }
                }*/
            });

            script.addContions(conditions);
        }
        return script;
    }

    public void stop() {
        if(uScriptController != null){
            uScriptController.stop();
        }
    }
}
