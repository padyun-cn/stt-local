package com.padyun.scripttools.content.trash;


import com.uls.utilites.un.Useless;

import java.util.ArrayList;
import java.util.List;

import androidx.core.util.Consumer;


/**
 * Created by daiepngfei on 1/29/19
 */
public class ClzCaster {

    public static <T> boolean cast(Object t, Class<T> cls, Consumer<T> consumer){
        return cast(t, cls, consumer, null);
    }

    public static <T> boolean cast(Object t, Class<T> cls, Consumer<T> consumer, Runnable reject){
        if(Useless.nulls(t, cls) || !cls.isInstance(t)) return false;
        final T ot = cls.cast(t);
        if(consumer != null) consumer.accept(ot);
        else if(reject != null) reject.run();
        return true;
    }

    public static <T, V> boolean cast2(Object t, Class<T> clst, Object v,  Class<V> clsv, Consumer2<T, V> consumer){
        if(Useless.nulls(t, clst, v, clsv) || !clst.isInstance(t) || !clsv.isInstance(v)) return false;
        final T ot = clst.cast(t);
        final V ov = clsv.cast(v);
        if(consumer != null) consumer.accept(ot, ov);
        return true;
    }

    public static <O, T> boolean castList(List<O> t, Class<T> cls, Consumer<ArrayList<T>> consumer){
        if(Useless.nulls(t, cls)) return false;
        ArrayList<T> list = new ArrayList<>();
        Useless.foreach(t, o -> {
            if(cls.isInstance(o)) list.add(cls.cast(o));
        });
        if(consumer != null) consumer.accept(list);
        return true;
    }

    public static void cast(Object t, Class[] classes, Consumer<Integer> consumer){
        if(classes == null) return;
        for(int i = 0; i < classes.length; i++) {
            if(classes[i] == null || !classes[i].isInstance(t)) continue;
            if(consumer != null) consumer.accept(i);
            break;
        }
    }

}
