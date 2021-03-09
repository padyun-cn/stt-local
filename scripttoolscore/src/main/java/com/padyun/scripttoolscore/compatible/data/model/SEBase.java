package com.padyun.scripttoolscore.compatible.data.model;

import android.os.Bundle;

import com.uls.utilites.un.Useless;

import java.io.Serializable;

/**
 * Created by daiepngfei on 1/23/19
 */
public class SEBase implements Serializable {
    public static final String KEY_SERIALIZABLE = "key_serializable";
    public static final String KEY_ID = "ID";
    public static final String KEY_TITLE = "title";
    public static final String KEY_NAME = "title";

    public static <T extends SEBase> Bundle simpleBundle( T t){
        return simpleBundle(new Bundle(), t);
    }
    public static <T extends SEBase> Bundle simpleBundle(Bundle bundle, T t){
        if(!Useless.nulls(bundle, t)) bundle.putSerializable(KEY_SERIALIZABLE, t);
        return bundle;
    }

    public static Serializable getSerializableWithBundle(Bundle bundle){
        return bundle == null ? null : bundle.getSerializable(KEY_SERIALIZABLE);
    }

    public static Bundle simpleTitleBundle(String title){
        Bundle b = new Bundle();
        b.putString(KEY_TITLE, title);
        return b;
    }

    public static Bundle simpleNameBundle(String title){
        Bundle b = new Bundle();
        b.putString(KEY_NAME, title);
        return b;
    }

    public static String getTitleWithBundle(Bundle b){
        return b == null ? null : b.getString(KEY_TITLE);
    }

    public static String getNameWithBundle(Bundle b){
        return b == null ? null : b.getString(KEY_NAME);
    }

    @SuppressWarnings("unchecked")
    public static <T extends SEBase> T getSerializableWithBundle(Bundle bundle, Class<T> cls){
        T t = null;
        if(!Useless.nulls(bundle, cls)) {
            Serializable serializable = bundle.getSerializable(KEY_SERIALIZABLE);
            if(cls.isInstance(serializable)) t = (T) serializable;
        }
        return t;
    }

    protected String wrapGapBrackets(String s){
        if(Useless.isEmpty(s)) return "";
        return "<" + s + ">";
    }
}
