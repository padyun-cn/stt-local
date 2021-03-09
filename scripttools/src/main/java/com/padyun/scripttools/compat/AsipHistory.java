package com.padyun.scripttools.compat;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.uls.utilites.un.Useless;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daiepngfei on 9/12/19
 */
public class AsipHistory {

    private static final String ASIP_HIS_KEY = "ASIP_HIS_KEY";

    public static List<HistItem> getHistoryList(Context context) {
        final HisList hisList = getHisList(context);
        if (hisList != null && hisList.items != null) {
            return hisList.items;
        }
        return null;
    }

    private static HisList getHisList(Context context) {
        SharedPreferences sp = context.getSharedPreferences(ASIP_HIS_KEY, Context.MODE_PRIVATE);
        final String jsonSrc = sp.getString("history", "{}");
        return new Gson().fromJson(jsonSrc, HisList.class);
    }

    public static void addHistory(Context context, String sip, String asip) {
        HisList hisList = getHisList(context);
        if(hisList == null){
            hisList = new HisList();
        }
        List<HistItem> items = hisList.items;
        if (Useless.isEmpty(items)) {
            items = new ArrayList<>();
            hisList.items = items;
        }
        for (HistItem item : items) {
            if (item != null && Useless.equals(item.serverIp, sip) && Useless.equals(item.asIp, asip)) {
                return;
            }
        }
        HistItem item = new HistItem();
        item.asIp = asip;
        item.serverIp = sip;
        items.add(item);
        SharedPreferences sp = context.getSharedPreferences(ASIP_HIS_KEY, Context.MODE_PRIVATE);
        sp.edit().putString("history", new Gson().toJson(hisList)).apply();
    }


    private static final class HisList {
        List<HistItem> items;
    }

    public static class HistItem {
        public String serverIp;
        public String asIp;
    }

}
