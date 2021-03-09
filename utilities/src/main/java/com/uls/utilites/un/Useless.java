package com.uls.utilites.un;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.uls.utilites.exceptions.UiThreadOnlyException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 9/18/17
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Useless {

    private static final String TAG = "CNV#";
    private static final String EMPTY_STR = "";
    private static final long GB = 1024 * 1024 * 1024;
    private static final long MB = 1024 * 1024;
    private static final long KB = 1024;

    @NonNull
    public static String getCurrentThreadInfo() {
        Thread t = Thread.currentThread();
        return t.getName() + "(" + t.hashCode() + ") ";
    }


    public static void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    /**
     * @param src
     *
     * @return
     */
    public static byte[] getCopyedArray(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        byte[] tar = new byte[src.length];
        System.arraycopy(src, 0, tar, 0, tar.length);
        return tar;
    }

    /**
     * @param clz
     * @param t
     * @param <T>
     *
     * @return
     */
    public static <T> boolean isInstance(Class<T> clz, Object... t) {
        if (nulls(clz, t) || t.length == 0) return false;
        for (Object o : t) {
            if(!clz.isInstance(o)){
                return false;
            }
        }
        return true;
    }

    /**
     * @param s
     * @param clz
     *
     * @return
     */
    public static boolean isInstanceOfs(Object s, Class... clz) {
        if (clz == null) return false;
        for (Class cs : clz) {
            if (cs != null && cs.isInstance(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param list
     * @param t
     * @param <T>
     *
     * @return
     */
    public static <T> boolean remove(List<T> list, T t) {
        if (list == null || t == null) {
            return false;
        }
        T tar = null;
        for (int i = 0; i < list.size(); i++) {
            T o = list.get(i);
            if (o == t) {
                tar = o;
                break;
            }
            if (i == list.size() - 1) {
                return false;
            }
        }
        list.remove(tar);
        return true;
    }

    /**
     * @param reference
     * @param consumer
     * @param <T>
     *
     * @return
     */
    public static <T> boolean weakPerform(WeakReference<T> reference, Consumer<T> consumer) {
        if (reference != null) {
            final T t = reference.get();
            if (t != null) {
                consumer.accept(t);
                return true;
            }
        }
        return false;
    }

    /**
     * @param mColor
     *
     * @return
     */
    public static String colorToRGBString(int mColor) {
        return colorToRGBString(mColor, ", ");
    }

    /**
     * @param mColor
     * @param s
     *
     * @return
     */
    public static String colorToRGBString(int mColor, String s) {
        return String.valueOf(
                (mColor & (255 << 16)) >>> 16) +
                s +
                ((mColor & (255 << 8)) >>> 8) +
                s +
                ((mColor << 24) >>> 24);
    }


    /**
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T, K> void foreach(Map<T, K> list, Consumer<Pair<T, K>> consumer) {
        if (isEmpty(list) || consumer == null) return;
        for (T t : list.keySet()) {
            if(t == null || list.get(t) == null) continue;
            consumer.accept(new Pair<>(t, list.get(t)));
        }
    }

    /**
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T> void foreach(T[] list, Consumer<T> consumer) {
        if (isEmpty(list)) return;
        foreach(Arrays.asList(list), consumer);
    }

    /**
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T> void foreach(List<T> list, Consumer<T> consumer) {
        if (isEmpty(list) || consumer == null) return;
        for (T t : list) {
            if (t != null) {
                consumer.accept(t);
            }
        }
    }

    /**
     * @param list
     * @param filter
     * @param <T>
     */
    public static <T> T foreachQuering(List<T> list, ForeachFilter<T> filter) {
        if (isEmpty(list) || filter == null) {
            return null;
        }
        for (T t : list) {
            if (t == null) {
                continue;
            }
            T result = filter.filter(t);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * @param cls
     * @param list
     * @param consumer
     * @param <T>
     */
    public static <T, K> void forEachCasting(List<K> list, Class<T> cls, Consumer<T> consumer) {
        foreach(list, k -> couldCasting(cls, k, consumer));
    }

    /**
     * @param cls
     * @param target
     * @param then
     * @param <T>
     */
    public static <T> void couldCasting(Class<T> cls, Object target, Consumer<T> then) {
        if (cls.isInstance(target)) {
            T t = cls.cast(target);
            if (t != null) {
                then.accept(t);
            }
        }
    }

    /**
     * @param list
     */
    public static void clear(List<?> list) {
        if (!isEmpty(list)) list.clear();
    }

    /**
     * @param list
     * @param index
     * @param <T>
     *
     * @return
     */
    public static <T> T get(List<T> list, int index) {
        T t = null;
        if (list != null && index < list.size() && index > -1) {
            t = list.get(index);
        }
        return t;
    }

    /**
     * @param t
     * @param <T>
     *
     * @return
     */
    @SafeVarargs
    public static <T> ArrayList<T> asList(T... t) {
        final ArrayList<T> list = new ArrayList<>();
        if (!isEmpty(t)) foreach(t, list::add);
        return list;
    }

    /**
     * @param self
     * @param container
     *
     * @return
     */
    public static boolean bit_contains(int self, int container) {
        return (self & container) == self;
    }


    /**
     * 比较两个金额的大小
     *
     * @param moneyone
     * @param moneytwo
     *
     * @return
     */
    public static int ComparetoMoney(BigDecimal moneyone, BigDecimal moneytwo) {
        return moneyone.compareTo(moneytwo);
    }

    /**
     * 比较两个金额的大小
     *
     * @param moneyone
     * @param moneytwo
     *
     * @return
     */
    public static int ComparetoMoneys(String moneyone, String moneytwo) {
        BigDecimal money1 = new BigDecimal(moneyone);
        BigDecimal money2 = new BigDecimal(moneytwo);
        return money1.compareTo(money2);
    }

    /**
     * @param number
     *
     * @return
     */
    public static boolean isNumber(String number) {
        boolean isNumber = false;
        try {
            final int result = Integer.valueOf(number);
            System.out.println(result);
            isNumber = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isNumber;
    }

    /**
     * @param str
     *
     * @return
     */
    public static boolean isUriSchemeHttp(String str) {
        return startsWithOr(str, "http", "https");
    }

    /**
     * @param target
     *
     * @return
     */
    public static String sTrim(String target) {
        return nonNullStr(target).trim();
    }

    /**
     * @param pattern
     * @param strings
     *
     * @return
     */
    public static String sUnion(String pattern, List<String> strings) {
        if (isEmpty(strings)) return EMPTY_STR;
        return sUnion(pattern, strings.toArray(new String[strings.size()]));
    }

    /**
     * @param pattern
     * @param targets
     *
     * @return
     */
    public static String sUnion(String pattern, String... targets) {
        StringBuilder union = new StringBuilder();
        if (!isEmpty(targets) && !hasEmptyIn(pattern)) {
            for (String target : targets) {
                if (!hasEmptyIn(target)) {
                    union.append(target).append(pattern);
                }
            }
            if (union.length() > 0) union.delete(union.length() - 1, union.length());
        }
        return union.toString();
    }

    /**
     * @param target
     * @param pattern
     *
     * @return
     */
    public static String[] sSplits(String target, String pattern) {
        if (hasEmptyIn(target, pattern)) return null;
        return target.split(pattern);
    }

    /**
     * @param target
     * @param c
     *
     * @return
     */
    public static boolean in(int target, int[] c) {
        Integer[] integer = null;
        if (c != null) {
            integer = new Integer[c.length];
            for (int i = 0; i < c.length; i++) {
                integer[i] = c[i];
            }
        }
        return in(target, integer);
    }

    /**
     * @param target
     * @param patterns
     *
     * @return
     */
    public static boolean startsWithOr(String target, String... patterns) {
        if (!hasEmptyIn(target) && !hasEmptyIn(patterns)) {
            for (String p : patterns) {
                if (target.startsWith(p)) return true;
            }
        }
        return false;
    }

    /**
     * @param target
     * @param c
     *
     * @return
     */
    public static boolean in(Object target, Object[] c) {
        boolean in = false;
        if (target != null && c != null && c.length > 0) {
            for (Object i : c) {
                if (target.equals(i)) {
                    in = true;
                    break;
                }
            }
        }
        return in;
    }

    /**
     * @param obj
     * @param tar
     *
     * @return
     */
    public static boolean contains(String obj, String tar) {
        boolean result = false;
        if (!hasEmptyIn(obj, tar)) {
            result = obj.contains(tar);
        }
        return result;
    }

    /**
     * @param objs
     * @param tar
     *
     * @return
     */
    public static <T> boolean contains(T[] objs, T tar) {
        boolean result = false;
        if (!nulls(objs, tar)) {
            for (T t : objs) {
                if (t != null && t.equals(tar)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @param val
     *
     * @return
     */
    public static String urlEncode(String val) {
        try {
            val = URLEncoder.encode(val, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e(TAG, "", e);
        }
        return val;
    }

    /**
     * @param pattern
     * @param strings
     *
     * @return
     */
    @SuppressWarnings("StringConcatenationInLoop")
    public static String antiSplit(String pattern, String... strings) {
        String result = EMPTY_STR;
        if (!nulls(pattern, strings)) {
            for (String s : strings) {
                if (!hasEmptyIn(s)) {
                    result += s;
                    result += pattern;
                }
            }
            if (result.length() > 0) {
                result = result.substring(0, result.length() - pattern.length());
            }
        }
        return result;
    }

    /**
     * @param obj
     *
     * @return
     */
    public static boolean isBasicType(Object obj) {
        return obj != null && (Integer.class.isInstance(obj) || String.class.isInstance(obj) || Boolean.class.isInstance(obj) ||
                Float.class.isInstance(obj) || Double.class.isInstance(obj) || Character.class.isInstance(obj) ||
                Byte.class.isInstance(obj) || Short.class.isInstance(obj) || Long.class.isInstance(obj));
    }


    /**
     * @param strs
     *
     * @return
     */
    public static boolean noEmptyStr(String... strs) {
        if (strs != null && strs.length > 0) {
            for (String s : strs) {
                if (s == null || s.length() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void assertNoEmptyStr(String... strs) {
        boolean no = noEmptyStr(strs);
        if (!no) {
            throw new IllegalArgumentException("empty string parameter!");
        }
    }

    /**
     * @param strs
     *
     * @return
     *
     * @Depracated
     */
    @Deprecated
    public static boolean hasEmptyIn(String... strs) {
        boolean empty = strs == null || strs.length == 0;
        if (!empty) {
            for (String s : strs) {
                if (TextUtils.isEmpty(s)) {
                    empty = true;
                    break;
                }
            }
        }
        return empty;
    }


    /**
     * @param str
     *
     * @return
     */
    public static boolean isEmpty(Map str) {
        return str == null || str.size() == 0;
    }

    /**
     * @param str
     *
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * @param str
     * @param setvalue
     *
     * @return
     */
    public static String emptyOr(String str, String setvalue) {
        return isEmpty(str) ? setvalue : str;
    }


    /**
     * @param strs
     *
     * @return
     */
    public static boolean isEmpty(Collection strs) {
        return strs == null || strs.size() <= 0;
    }

    /**
     *
     * @param strs
     * @return
     */
    public static String cbEmpty(String strs) {
        return emptyOr(strs, "");
    }

    /**
     * @param objects
     *
     * @return
     */
    public static boolean uselessArgs(Object... objects) {
        boolean n = false;
        if (objects != null) {
            for (Object obj : objects) {
                if (
                        obj == null ||
                                (obj.getClass().isArray() && Array.getLength(obj) == 0) ||
                                (obj instanceof String && obj.toString().length() == 0)

                ) {
                    n = true;
                    break;
                }

            }
        }
        return n;
    }


    /**
     * @param strs
     *
     * @return
     */
    public static boolean nulls(Object... strs) {
        boolean empty = strs == null;
        if (!empty) {
            for (Object s : strs) {
                if (s == null) {
                    empty = true;
                    break;
                }
            }
        }
        return empty;
    }


    /**
     * @param arr
     * @param val
     *
     * @return
     */
    public static int indexOf(int[] arr, int val) {
        return consumeIndexOf(arr, val, null);
    }

    /**
     * @param arr
     * @param val
     * @param consumer
     *
     * @return
     */
    public static int consumeIndexOf(int[] arr, int val, Consumer<Integer> consumer) {
        int r = -1;
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != val) continue;
                if (consumer != null) consumer.accept(i);
                r = i;
                break;
            }
        }
        return r;
    }

    /**
     * @param weakObjs
     *
     * @return
     */
    @SafeVarargs
    public static <T> boolean nulls(WeakReference<T>... weakObjs) {
        boolean empty = weakObjs == null;
        if (!empty) {
            for (WeakReference<T> s : weakObjs) {
                if (s == null || s.get() == null) {
                    empty = true;
                    break;
                }
            }
        }
        return empty;
    }


    public static String emptyToStr(String content, String str) {
        if (isEmpty(content)) {
            return str;
        }
        return content;
    }



    /**
     * @param target
     * @param min
     * @param max
     *
     * @return
     */
    public static int limitInRange(int target, int min, int max) {
        if (min > max) throw new IllegalArgumentException("");
        return target < min ? min : target > max ? max : target;
    }

    /**
     * @param target
     * @param floor
     * @param ceil
     *
     * @return
     */
    public static boolean isInRange(int target, int floor, int ceil) {
        return floor <= ceil && target >= floor && target <= ceil;
    }

    /**
     * @param date
     * @param pattern
     *
     * @return
     */
    public static boolean date_IsTodayBefore(String date, String pattern) {
        return date_IsTodayAfterOrBefore(date, pattern, false);
    }

    /**
     * @param date
     * @param pattern
     *
     * @return
     */
    public static boolean date_IsTodayAfter(String date, String pattern) {
        return date_IsTodayAfterOrBefore(date, pattern, true);
    }

    /**
     * @param dateLeft
     * @param dateRight
     * @param pattern
     *
     * @return
     */
    public static boolean date_IsTodayBetween(String dateLeft, String dateRight, String pattern) {
        return date_IsTodayBefore(dateRight, pattern) && date_IsTodayAfter(dateLeft, pattern);
    }

    /**
     * @param date
     * @param pattern
     * @param after
     *
     * @return
     */
    private static boolean date_IsTodayAfterOrBefore(String date, String pattern, boolean after) {
        final long now = System.currentTimeMillis();
        final long dateTime = date_GetTimeWithDateFormat(date, pattern);
        return after ? now > dateTime : now < dateTime;
    }

    /**
     * @param date
     * @param pattern
     *
     * @return
     */
    public static long date_GetTimeWithDateFormat(String date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        try {
            Date dateObj = dateFormat.parse(date);
            return dateObj.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * @param pattern
     *
     * @return
     */
    @NonNull
    public static String date_GetDayOfToday(@NonNull String pattern) {
        final long now = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        return dateFormat.format(new Date(now));
    }

    /**
     * @param objs
     *
     * @return
     */
    public static <T> boolean isEmpty(T[] objs) {
        return objs == null || objs.length == 0;
    }

    /**
     * @param strs
     *
     * @return
     */
    public static boolean hasEmptyIn(CharSequence... strs) {
        boolean empty = strs == null;
        if (!empty) {
            for (CharSequence s : strs) {
                if (TextUtils.isEmpty(s)) {
                    empty = true;
                    break;
                }
            }
        }
        return empty;
    }

    /**
     * @param items
     * @param index
     *
     * @return
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean validIndex(List<?> items, int index) {
        return items != null && index >= 0 && index < items.size();
    }

    /**
     * @param items
     * @param source
     * @param target
     * @param <T>
     *
     * @return
     */
    public static <T> boolean replace(List<T> items, T source, T target) {
        if (isEmpty(items) || nulls(source, target) || !items.contains(source)) return false;
        final int index = items.indexOf(source);
        items.add(index, target);
        items.remove(index + 1);
        return true;
    }

    /**
     * @param items
     * @param sourceIndex
     * @param target
     * @param <T>
     *
     * @return
     */
    @SuppressWarnings("UnusedReturnValue")
    private static <T> boolean replace(List<T> items, int sourceIndex, T target) {
        if (isEmpty(items) || nulls(target)) return false;
        items.add(sourceIndex, target);
        items.remove(sourceIndex + 1);
        return true;
    }

    /**
     * @param items
     * @param source
     * @param target
     * @param <T>
     *
     * @return
     */
    @SuppressWarnings("UnusedReturnValue")
    public static <T> boolean swap(List<T> items, T source, T target) {
        if (isEmpty(items) || nulls(target, source) || !items.contains(source) || !items.contains(target))
            return false;
        final int sourceIndex = items.indexOf(source);
        final int targetIndex = items.indexOf(target);
        replace(items, sourceIndex, target);
        replace(items, targetIndex, source);
        return true;
    }

    /**
     * @param items
     * @param sourceIndex
     * @param targetIndex
     * @param <T>
     *
     * @return
     */
    public static <T> boolean swap(List<T> items, int sourceIndex, int targetIndex) {
        if (isEmpty(items) || !validIndex(items, sourceIndex) || !validIndex(items, targetIndex))
            return false;
        final T source = items.get(sourceIndex);
        final T target = items.get(targetIndex);
        replace(items, sourceIndex, target);
        replace(items, targetIndex, source);
        return true;
    }

    /**
     * Could be empty -> safety string returned
     *
     * @param string
     *         target string
     *
     * @return empty if string sameId null
     */
    public static String nonNullStr(String string) {
        return emptyOr(string, "");
    }

    /**
     * Could be empty -> safety string returned
     *
     * @param string
     *         target string
     *
     * @return empty if string sameId null
     */
    public static CharSequence nonNullStr(CharSequence string) {
        return string == null ? "" : string;
    }

    /**
     * Could be </br> like
     *
     * @param string
     *
     * @return
     */
    public static String cbBr(String string) {
        return nonNullStr(string).replaceAll("\\r\\n", "<br />").replaceAll("\\n", "<br />");
    }

    /**
     * @param base
     * @param target
     *
     * @return
     */
    public static boolean equals(String base, String target) {
        return target != null && nonNullStr(base).equals(target);
    }

    /**
     * @param base
     * @param target
     *
     * @return
     */
    public static boolean startWith(String base, String target) {
        return nonNullStr(base).startsWith(target);
    }

    /**
     * @param string
     * @param defaultValue
     *
     * @return
     */
    public static int intParse(String string, int defaultValue) {
        try {
            return Integer.valueOf(string);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Check if it sameId "Index out of Bounds"
     *
     * @param collection
     * @param indexs
     *
     * @return
     */
    public static boolean isIndexesValid(Collection collection, int... indexs) {
        if (collection != null && indexs != null) {
            for (int i : indexs) {
                if (i < 0 || i >= collection.size()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if it sameId "Index out of Bounds"
     *
     * @param indexs
     *
     * @return
     */
    public static boolean isIndexesValid(Object[] arr, int... indexs) {
        if (arr != null && indexs != null) {
            for (int i : indexs) {
                if (i < 0 || i >= arr.length) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param collection
     * @param index
     * @param <T>
     *
     * @return
     */
    public static <T> T sGet(List<T> collection, int index) {
        return sGet(collection, index, null);
    }

    /**
     * @param collection
     * @param index
     * @param defaultValue
     * @param <T>
     *
     * @return
     */
    public static <T> T sGet(List<T> collection, int index, T defaultValue) {
        if (collection == null || collection.isEmpty()) {
            return defaultValue;
        }
        index = Math.min(collection.size() - 1, index);
        index = Math.max(0, index);
        return collection.get(index);
    }


    /**
     * @param lstrs
     *
     * @return
     */
    public static boolean isLongNumberString(String... lstrs) {
        try {
            for (String s : lstrs) {
                //noinspection ResultOfMethodCallIgnored
                Long.valueOf(s);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "xlLong => ", e);
            return false;
        }
        return true;
    }


    /**
     * @param intStr
     *
     * @return
     */
    public static int stringToInt(CharSequence intStr) {
        return stringToInt(intStr, 0);
    }


    /**
     * @param intStr
     * @param defaultValue
     *
     * @return
     */
    public static int stringToInt(CharSequence intStr, int defaultValue) {
        return intStr == null ? defaultValue : stringToInt(intStr.toString(), defaultValue);
    }

    /**
     * @param intStr
     *
     * @return
     */
    public static int stringToInt(String intStr, int defaultValue) {

        try {
            //noinspection ResultOfMethodCallIgnored
            defaultValue = Integer.valueOf(intStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "stringToInt => ", e);
        }
        return defaultValue;
    }

    /**
     * @param intStr
     *
     * @return
     */
    public static long stringToLong(String intStr, long defaultValue) {
        try {
            //noinspection ResultOfMethodCallIgnored
            defaultValue = Long.valueOf(intStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "stringToInt => ", e);
        }
        return defaultValue;
    }

    /**
     * @param lstr
     *
     * @return
     */
    public static boolean isIntTypeString(String lstr) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.valueOf(lstr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "xlInt => ", e);
            return false;
        }
        return true;
    }

    /**
     * @param target
     * @param values
     *
     * @return
     */
    public static boolean hasEqualedIn(String target, String... values) {
        boolean result = false;
        if (!hasEmptyIn(target) && values != null) {
            for (String value : values) {
                if (target.equals(value)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @param target
     * @param values
     *
     * @return
     */
    public static boolean isAllEqualed(String target, String... values) {
        boolean result = false;
        if (!hasEmptyIn(target) && values != null) {
            for (int i = 0; i < values.length; i++) {
                if (!target.equals(values[i])) {
                    result = false;
                    break;
                } else if (i == values.length - 1) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * @param cls
     * @param obj
     * @param <T>
     *
     * @return
     */
    public static <T> T objectCasting(@NonNull Class<T> cls, @NonNull Object obj) {
        return cls.isInstance(obj) ? cls.cast(obj) : null;
    }

    /**
     * @param pattern
     * @param timeStamp
     *
     * @return
     */
    public static String simplelyFormattingDate(String pattern, String timeStamp) {
        return isLongNumberString(timeStamp) ? simplelyFormattingDate(pattern, Long.valueOf(timeStamp)) : EMPTY_STR;
    }

    /**
     * @param pattern
     * @param timeStamp
     *
     * @return
     */
    public static String simplelyFormattingDate(String pattern, long timeStamp) {
        String result = EMPTY_STR;
        try {
            result = new SimpleDateFormat(pattern, Locale.CHINA).format(new Date(timeStamp * 1000));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "simplelyFormattingDate => ", e);
        }
        return result;
    }

    /**
     * @param map
     * @param <K>
     * @param <V>
     *
     * @return
     */
    public static <K, V> List<V> asList(Map<K, V> map) {
        List<V> list = new ArrayList<>();
        if (map != null) {
            Set<K> keySet = map.keySet();
            for (K k : keySet) {
                list.add(map.get(k));
            }
        }
        return list;
    }


    /**
     * 获得超类的参数类型，取第一个参数类型
     *
     * @param <T>
     *         类型参数
     * @param clazz
     *         超类类型
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> Class<T> genericSuperType(final Class clazz) {
        return genericSuperType(clazz, 0);
    }

    /**
     * 根据索引获得超类的参数类型
     *
     * @param clazz
     *         超类类型
     * @param index
     *         索引
     */
    @SuppressWarnings("rawtypes")
    public static Class genericSuperType(final Class clazz, final int index) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class) params[index];
    }

    /**
     * @param value
     *
     * @return
     */
    public static String formatFileSizeBytes(long value) {
        return formatFileSizeBytes(value, null);
    }

    /**
     * @param value
     *
     * @return
     */
    public static String formatFileSizeBytes(String value) {
        if (value != null && value.length() == 0) {
            value = "0";
        }
        return formatFileSizeBytes(Long.parseLong(value), null);
    }

    /**
     * @param value
     *         use g$xx for gb or "" as default  "GB" <br /> use m$xx for mb or "" as default  "MB"
     *         <br /> use k$xx for kb or "" as default  "KB" <br /> use b$xx for  b or "" as default
     *         "Bytes"  <br /> use '|' to as a spliter
     * @param pattern
     *
     * @return
     */
    public static String formatFileSizeBytes(long value, String pattern) {
        String result = "Unknown";
        if (value >= 0) {
            String gb, mb, kb, b;
            gb = "GB";
            mb = "MB";
            kb = "KB";
            b = "Bytes";
            if (!hasEmptyIn(pattern)) {
                @SuppressWarnings("RegExpEmptyAlternationBranch") String[] patterns = pattern.trim().split("|");
                for (String p : patterns) {
                    String tp = p.trim();
                    if (tp.length() <= 2) {
                        continue;
                    }
                    if (tp.startsWith("g$")) {
                        gb = tp.substring(2);
                    } else if (tp.startsWith("m$")) {
                        mb = tp.substring(2);
                    } else if (tp.startsWith("k$")) {
                        kb = tp.substring(2);
                    } else if (tp.startsWith("b$")) {
                        b = tp.substring(2);
                    }
                }
            }
            final DecimalFormat format = new DecimalFormat("0.00");
            if (value > GB) {
                result = format.format(value * 1D / GB) + gb;
            } else if (value > MB) {
                result = format.format(value * 1D / MB) + mb;
            } else if (value > KB) {
                result = format.format(value * 1D / KB) + kb;
            } else {
                result = value + b;
            }
        }
        return result;
    }

    /**
     * @param target
     * @param tail
     *
     * @return
     */
    public static String formatFloat(float target, int tail) {
        int tailFor = 0;
        String t = String.valueOf(target);
        if (t.contains(".")) {
            final int index = t.indexOf(".");
            final int extra = t.length() - 1 - index;
            if (extra > tail) {
                t = t.substring(0, index + tail + 1);
            } else if (extra < tail) {
                tailFor = tail - extra;
            }
        } else {
            t = t + ".";
            tailFor = tail;
        }
        StringBuilder tailSb = new StringBuilder();
        for (int i = 0; i < tailFor; i++) {
            tailSb.append("0");
        }
        return t + tailSb.toString();
    }

    /**
     * @param o
     *
     * @return
     */
    public static String stringValueOfbasicType(Object o) {
        if (o != null && isBasicType(o)) {
            return o.toString();
        }
        return EMPTY_STR;
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal decimalAdd(double a, double b) {
        return decimalAdd(String.valueOf(a), String.valueOf(b));
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal decimalMinus(double a, double b) {
        return decimalMinus(String.valueOf(a), String.valueOf(b));
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal decimalMul(double a, double b) {
        return decimalMul(String.valueOf(a), String.valueOf(b));
    }

    /**
     * @param a
     * @param b
     * @param mode
     *
     * @return
     */
    public static BigDecimal decimalDivided(double a, double b, RoundingMode mode) {
        return decimalDivided(String.valueOf(a), String.valueOf(b), mode);
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal decimalAdd(float a, float b) {
        return decimalAdd(String.valueOf(a), String.valueOf(b));
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal decimalMinus(float a, float b) {
        return decimalMinus(String.valueOf(a), String.valueOf(b));
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal decimalMul(float a, float b) {
        return decimalMul(String.valueOf(a), String.valueOf(b));
    }

    /**
     * @param a
     * @param b
     * @param mode
     *
     * @return
     */
    public static BigDecimal decimalDivided(float a, float b, RoundingMode mode) {
        return decimalDivided(String.valueOf(a), String.valueOf(b), mode);
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal decimalAdd(String a, String b) {
        return new BigDecimal(a).add(new BigDecimal(b));
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal decimalMinus(String a, String b) {
        return new BigDecimal(a).subtract(new BigDecimal(b));
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    public static BigDecimal decimalMul(String a, String b) {
        return new BigDecimal(a).multiply(new BigDecimal(b));
    }

    /**
     * @param a
     * @param b
     * @param mode
     *
     * @return
     */
    public static BigDecimal decimalDivided(String a, String b, RoundingMode mode) {
        return new BigDecimal(a).divide(new BigDecimal(b), mode);
    }

    /**
     * @param a
     * @param b
     *
     * @return
     */
    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    public static BigDecimal decimalDivideds(String a, String b) {
        return new BigDecimal(a).divide(new BigDecimal(b));
    }

    /**
     * @param source
     * @param cls
     * @param <T>
     *
     * @return
     */
    public static <T> T JSONObjParse(String source, Class<T> cls) {
        if (nulls(source, cls)) return null;
        T t = null;
        if (!isEmpty(source) && cls != null && source.startsWith("{")) {
            t = new Gson().fromJson(source, cls);
        }
        return t;
    }

    /**
     * @param source
     * @param cls
     * @param <T>
     *
     * @return
     */
    public static <T> List<T> JSONArrayParse(String source, Class<T> cls) {
        List<T> list = new ArrayList<>();
        if (cls != null && !isEmpty(source) && source.startsWith("[")) {
            try {
                JSONArray jsonArray = new JSONArray(source);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.optJSONObject(i);
                    if (object == null) continue;
                    list.add(new Gson().fromJson(object.toString(), cls));
                }
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return list;
    }

    public static boolean isOnUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void assertOnUiThread(){
        if(!isOnUiThread()){
            throw new UiThreadOnlyException();
        }
    }

    public static void assertThreadInterruption() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    public interface ForeachFilter<T> {
        T filter(T t);
    }
}

