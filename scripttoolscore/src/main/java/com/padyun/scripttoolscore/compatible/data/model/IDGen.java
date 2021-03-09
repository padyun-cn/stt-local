package com.padyun.scripttoolscore.compatible.data.model;

import androidx.annotation.NonNull;

import com.uls.utilites.un.Useless;

import java.util.Random;

/**
 * Created by daiepngfei on 1/30/19
 */
public class IDGen {
    public static String genTmsStrRandom(String prifix){
        return genTmsStrRandom(prifix, null);
    }
    public static String genTmsStrRandom(String prifix, String suffix){
        return Useless.nonNullStr(prifix) + getTmsStrRandomContent() + Useless.nonNullStr(suffix);
    }

    @NonNull
    private static String getTmsStrRandomContent() {
        return System.currentTimeMillis() + "_" + new Random().nextInt(1000);
    }

    @NonNull
    public static long getTmsRandomLong() {
        return System.currentTimeMillis() * 1000 + new Random().nextInt(1000);
    }
}
