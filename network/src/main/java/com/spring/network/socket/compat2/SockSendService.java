package com.spring.network.socket.compat2;

import android.app.Activity;

import com.uls.utilites.content.MarkableThread;
import com.uls.utilites.un.Useless;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import androidx.fragment.app.Fragment;

/**
 * Created by daiepngfei on 11/4/19
 */
public class SockSendService {

    private static final ProxyProvider<Activity> sActSenders;
    private static final ProxyProvider<Fragment> sFragSenders;

    static {
        sActSenders = new ProxyProvider<>();
        sFragSenders = new ProxyProvider<>();
    }

    public static Proxy applyProxy(Activity activity){
        return sActSenders.applyProxy(activity);
    }

    public static Proxy applyProxy(Fragment fragment){
        return sFragSenders.applyProxy(fragment);
    }

    public static void disapplyProxy(Activity activity){
        sActSenders.disapplyProxy(activity);
    }

    public static void disapplyProxy(Fragment fragment){
        sFragSenders.disapplyProxy(fragment);
    }

    /**
     *
     * @param <T>
     */
    private static class ProxyProvider<T> {
        private final WeakHashMap<T, Proxy> sProviders;

        ProxyProvider(){
            sProviders = new WeakHashMap<>();
        }

        /**
         *
         * @param t
         * @return
         */
        Proxy applyProxy(T t){
            synchronized (this) {
                if(t == null){
                    return null;
                }
                if (sProviders.get(t) == null) {
                    sProviders.put(t, new Proxy());
                }
                return sProviders.get(t);
            }
        }

        /**
         *
         * @param t
         */
        void disapplyProxy(T t){
            synchronized (this) {
                if(t == null){
                    return;
                }
                Proxy proxy = sProviders.remove(t);
                if (proxy != null) {
                    proxy.destroy();
                }
            }
        }

    }

    /**
     *
     */
    private static class Proxy {
        private List<OTRRConnection> sSenders;
        private Proxy() {
            sSenders = new ArrayList<>();
        }

        /**
         *
         * @param ip
         * @param port
         * @param data
         */
        public void send(String ip, int port, byte[] data) {
            send(ip, port, data, null);
        }

        /**
         *
         * @param ip
         * @param port
         * @param data
         * @param callback
         */
        public void send(String ip, int port, byte[] data, ISockResponse callback) {
            send(ip, port, data, "", callback);
        }

        /**
         *
         * @param ip
         * @param port
         * @param data
         * @param tag
         * @param callback
         */
        public void send(String ip, int port, byte[] data, String tag, ISockResponse callback) {
            synchronized (this) {
                //  params checking
                if (Useless.isEmpty(ip) || data == null || data.length == 0) {
                    return;
                }
                // initializeActSendsMap();
                final OTRRConnection sender = new OTRRConnection(ip, port, data, tag, callback);
                sender.setOnStateChangedListener(state -> {
                    if (state == SockConnState.TERMINATED) {
                        removeSender(sender);
                    }
                });
                sSenders.add(sender);
                sender.send();
            }
        }

        /**
         *
         * @param sender
         */
        private void removeSender(OTRRConnection sender) {
            synchronized (this) {
                Useless.remove(sSenders, sender);
            }
        }

        /**
         *
         */
        void destroy(){
            synchronized (this){
                Useless.foreach(sSenders, MarkableThread::setMarkedStopped);
                sSenders.clear();
            }
        }
    }

}
