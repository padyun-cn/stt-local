package com.padyun.scripttools.biz.ui.content;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.padyun.scripttools.compat.data.AbsCoConditon;
import com.padyun.scripttools.compat.data.CoScript;
import com.uls.utilites.content.CoreWorkers;
import com.padyun.scripttools.content.data.CustomScriptIOManager;
import com.padyun.scripttools.content.network.ITestLogCatcher;
import com.padyun.scripttools.module.runtime.StContext;
import com.padyun.scripttools.module.runtime.test.TestProxy;
import com.padyun.scripttoolscore.compatible.data.model.SEScript;
import com.padyun.scripttoolscore.compatible.plugin.RuntimeInfo;
import com.padyun.scripttoolscore.content.network.SockResponseUtils;

import java.util.HashMap;

import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 2020-01-15
 */
public class UScriptPlayer implements ITestLogCatcher {
    private final ILogCatcher logCatcher;
    private Handler handler = new Handler(Looper.getMainLooper());

    public enum State {
        IDLE, STARTING, STARTED, UNSTARTED, PAUSING, PAUSED, RESUMING, STOPPING, RESUMED, UNRESUMED, UNSTOPPED, UNPAUSED
    }

    private OnStateChangedListener onStateChangedListener;
    private State tstate = State.IDLE;
    private TestProxy testProxy;
    private String currentScriptPath;

    private static IScriptLoader sDefaultLoader = CustomScriptIOManager::getCoScriptFromFileSync;
    private IScriptLoader scriptLoader = null;

    public interface IScriptLoader {
        CoScript onLoadingCoScript(String path);
    }

    public interface OnStateChangedListener {
        void onScriptControlStateChanged(State tstate);
    }

    public interface ILogCatcher {
        void onContinueCachingLogs(RuntimeInfo log, AbsCoConditon conditon, boolean touched);
    }

    public UScriptPlayer(String currentScriptPath, ILogCatcher logCatcher, OnStateChangedListener onStateChangedListener) {
        this.logCatcher = logCatcher;
        this.onStateChangedListener = onStateChangedListener;
        this.testProxy = StContext.network().applyTest();
        this.currentScriptPath = currentScriptPath;

    }

    public IScriptLoader getScriptLoader() {
        return scriptLoader;
    }

    public void setScriptLoader(IScriptLoader scriptLoader) {
        this.scriptLoader = scriptLoader;
    }

    public void start() {
        if (getTstate() == State.IDLE) {
            setCurrenState(State.STARTING);
            withOrWithoutLoadingScriptJson();
            testProxy.startRetrivingLogs(this, new SockResponseUtils.SimpleResponse());
        }

    }

    public void resume() {
        //if (!isCurrentState(State.RESUMING) && !isCurrentState(State.IDLE)) {
        if(isCurrentState(State.PAUSED)){
            setCurrenState(State.RESUMING);
            testProxy.sendCmdResume(new SockResponseUtils.SimpleOkResponse() {
                @Override
                public void onFail(int errno, String msg, Exception e) {
                    setCurrenState(State.UNRESUMED);
                    setCurrenState(State.IDLE);
                }

                @Override
                public void onResponseOk() {
                    setCurrenState(State.RESUMED);
                }
            });
            testProxy.startRetrivingLogs(this, new SockResponseUtils.SimpleResponse());
        }
    }

    public void pause() {
        pause(false);
    }

    public void pause(boolean stopLogs) {
        if (isCurrentState(State.STARTED, State.RESUMED)) {
            final State state = getTstate();
            setCurrenState(State.PAUSING);
            if (stopLogs) {
                testProxy.stopRetrivingLogs();
            }
            testProxy.sendCmdPause(new SockResponseUtils.SimpleOkResponse() {

                @Override
                public void onFail(int errno, String msg, Exception e) {
                    setCurrenState(State.UNPAUSED);
                    setCurrenState(state);
                }

                @Override
                public void onResponseOk() {
                    setCurrenState(State.PAUSED);
                }
            });
        }
    }

    public void stop() {
        if (isCurrentState(State.STARTED, State.RESUMED)) {
            final State state = getTstate();
            setCurrenState(State.STOPPING);
            testProxy.stopRetrivingLogs();
            testProxy.sendCmdStop(new SockResponseUtils.SimpleOkResponse() {

                @Override
                public void onFail(int errno, String msg, Exception e) {
                    handler.post(() -> {
                        setCurrenState(State.UNSTOPPED);
                        setCurrenState(state);
                    });
                }

                @Override
                public void onResponseOk() {
                    handler.post(() -> setCurrenState(State.IDLE));
                }
            });
        }
    }

    /**
     *
     */
    @SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
    private void withOrWithoutLoadingScriptJson() {
        CoreWorkers
                .on(new CoreWorkers.Work<CoScript>() {
                    @Override
                    public CoScript work() {
                        return getCoScript();
                    }
                })
                .then(new CoreWorkers.Then<CoScript>() {
                    @Override
                    public void then(CoScript r) {
                        onCoScriptLoaded(r);
                    }
                });
    }

    private void onCoScriptLoaded(CoScript r) {
        try {
            final SEScript seScript = r.buildToSEScript();
            if (seScript != null) {
                updateLogIdTables(r);
                testProxy.sendStartCmd(seScript.buildToJson(), seScript.getImage_list(), new SockResponseUtils.SimpleOkResponse() {
                    @Override
                    public void onFail(int errno, String msg, Exception e) {
                        Log.e("nimab", msg + "" + errno, e);
                        setCurrenState(State.UNSTARTED);
                        setCurrenState(State.IDLE);
                    }

                    @Override
                    public void onResponseOk() {
                        setCurrenState(State.STARTED);
                        Log.e("nimab", "wah");
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            setCurrenState(State.UNSTARTED);
        }
    }

    private HashMap<String, AbsCoConditon> conditionMap = new HashMap<>();

    private void updateLogIdTables(CoScript currentScript) {
        Useless.foreach(currentScript.getConditions(), t -> conditionMap.put(t.getCondition_id(), t));
    }

    /**
     *
     */
    private CoScript getCoScript() {
        final String path = currentScriptPath;
        if (getScriptLoader() != null) {
            return getScriptLoader().onLoadingCoScript(path);
        }
        return Useless.isEmpty(path) ? null : sDefaultLoader.onLoadingCoScript(path);
    }

    private void setCurrenState(State state) {
        tstate = state;
        if (onStateChangedListener != null) {
            onStateChangedListener.onScriptControlStateChanged(state);
        }
    }

    private boolean isCurrentState(State... states) {
        for (State state : states) {
            if(state == getTstate()){
                return true;
            }
        }
        return false;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("script_tstate", getTstate().ordinal());
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            switch (savedInstanceState.getInt("script_tstate")) {
                case 1:
                    setCurrenState(State.STARTED);
                    break;
                case 2:
                    setCurrenState(State.PAUSED);
                    break;
                default:
                    setCurrenState(State.IDLE);
            }
        }
    }

    private State getTstate() {
        return tstate;
    }


    @Override
    public void onContinueCachingLogs(RuntimeInfo info) {
        if (info == null || info.getEntity() == null) return;
        final RuntimeInfo.Entity entity = info.getEntity();
        if (entity != null) {
            final String conditionId = entity.getCondition_id();
            final AbsCoConditon condition = conditionMap.get(conditionId);
            if (condition != null) {
                logCatcher.onContinueCachingLogs(info, condition, entity.isIs_trigger());
            }
        }
    }
}
