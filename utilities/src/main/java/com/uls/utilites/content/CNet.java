package com.uls.utilites.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;

import com.uls.utilites.un.Useless;

import java.util.HashSet;
import java.util.Objects;

/**
 * Created by daiepngfei on 2020-05-19
 */
public class CNet {
    private boolean isInited = false;
    private NetworkCapabilities networkCapabilities;
    private State state = new State();
    private HashSet<IState> hooks = new HashSet<>();

    public interface IState {
        void onNetStateChanged(State state);
    }

    public static class State {
        private boolean isWifiOn, isDataOn, isOnline;

        private State() {
        }

        public boolean isWifiOn() {
            return isWifiOn;
        }

        public boolean isDataOn() {
            return isDataOn;
        }

        public boolean isOnline() {
            return isOnline;
        }

        public boolean isWifiOnline() {
            return isOnline && isWifiOn;
        }
    }

    public static CNet ins() {
        return F.sInstance;
    }

    private static class F {
        private static final CNet sInstance = new CNet();
    }

    public static void init(Context context) {
        if (context != null) {
            ins().initSelf(context.getApplicationContext());
        }
    }

    public static boolean isCurrentlyWifiOnline(Context context) {
        ins().updateCNetState(context);
        return ins().state.isWifiOnline();
    }

    public static boolean isOnline() {
        return ins().state.isOnline;
    }

    public static void hook(Context context, IState wifiHook) {
        if (wifiHook != null) {
            ins().hooks.add(wifiHook);
            ins().updateCNetState(context);
            wifiHook.onNetStateChanged(ins().state);
        }
    }

    public static void unhook(IState wifiHook) {
        if (wifiHook != null) {
            ins().hooks.remove(wifiHook);
        }
    }

    private void initSelf(Context context) {
        if (!isInited) {
            isInited = true;
            final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Objects.requireNonNull(manager);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final NetworkRequest networkRequest = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();
                manager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onAvailable(Network network) {
                        setConnectedInfoChanged(context);
                    }

                    @Override
                    public void onUnavailable() {
                        setConnectedInfoChanged(context);
                    }

                    @Override
                    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                        setConnectedInfoChanged(context);
                    }
                });
            } else {
                IntentFilter filter = new IntentFilter();
                //noinspection deprecation
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                context.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        setConnectedInfoChanged(context);
                    }
                }, filter);
            }
        }
    }

    private void setConnectedInfoChanged(Context context) {
        updateCNetState(context);
        notifyCallbacks();
    }

    private void notifyCallbacks() {
        if (hooks.size() > 0) {
            for (IState consumer : hooks) {
                if (consumer != null) {
                    consumer.onNetStateChanged(state);
                }
            }
        }
    }

    private void updateCNetState(Context context) {

        if (context == null) {
            return;
        }

        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Objects.requireNonNull(manager);

        state.isDataOn = false;
        state.isWifiOn = false;
        state.isOnline = false;

        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            return;
        }

        if (info.isConnected()) {
            state.isOnline = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final NetworkCapabilities capabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
                if (capabilities != null) {
                    state.isWifiOn = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    state.isDataOn = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
                    return;
                }
            }
        }

        Useless.foreach(manager.getAllNetworkInfo(), networkInfo -> {
            if (networkInfo.isConnected()) {
                final int type = networkInfo.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    state.isWifiOn = true;
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    state.isDataOn = true;
                }
            }
        });
    }
}
