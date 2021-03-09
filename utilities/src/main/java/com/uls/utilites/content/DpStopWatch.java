package com.uls.utilites.content;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2020-08-10
 */
public class DpStopWatch {

    private StopWatchMark mHead;
    private StopWatchMark mTail;
    private StopWatchMark min;
    private StopWatchMark max;
    private long total;
    private int size;
    private String fTag;
    private int maxLabelLen = 0;
    private int layer = 0;
    private IMarkPrinter mIMarkPrinter = new IMarkPrinter() {
        @Override
        public void print(StopWatchMark marked, String defaultLog) {
            System.out.println(defaultLog);
        }
    };

    public void watchMarkForeach(Consumer<StopWatchMark> markConsumer) {
        if (markConsumer == null) {
            return;
        }
        StopWatchMark cursor = mHead;
        while (cursor != null) {
            markConsumer.accept(cursor);
            cursor = cursor.next;
        }
    }

    public DpStopWatch(String tag) {
        this(tag, null);
    }

    public DpStopWatch() {
        mTail = mHead = new EmptyMark("");
    }

    public void setTag(String fTag) {
        this.fTag = fTag;
    }

    public long getFirstStopMark() {
        return mHead == null ? 0 : mHead.stopMark;
    }

    public long getLastStopMark() {
        return mTail == null ? 0 : mTail.stopMark;
    }

    public DpStopWatch(String tag, IMarkPrinter markPrinter) {
        fTag = tag;
        if (markPrinter != null) {
            this.mIMarkPrinter = markPrinter;
        }
    }

    public void stepIn() {
        synchronized (this) {
            layer++;
        }
    }

    public void stepOut() {
        synchronized (this) {
            layer--;
            layer = Math.max(0, layer);
        }
    }

    public void stopWatch(String label) {
        stopWatch(label, null);
    }

    /*public long tailComsumed() {
        return mTail == null ? -1 : mTail.stopWatchInteval;
    }

    public void markEnd() {
        if (mTail != null && mTail.stopWatchInteval == 0) {
            mTail.stopWatchInteval = System.currentTimeMillis() - mTail.stopMark;
        }
    }*/

    public void stopWatch(String label, String lastTip) {
        synchronized (this) {
            if (size >= 150) {
                reset();
            }
            StopWatchMark mark = new StopWatchMark(label);
            mark.layer = layer;
            mark.stopWatchInteval = mark.stopMark - mTail.stopMark;
            mark.tip = lastTip;


            if (min == null || min.stopWatchInteval > mark.stopWatchInteval) {
                min = mark;
            }
            if (max == null || max.stopWatchInteval < mark.stopWatchInteval) {
                max = mark;
            }

            mTail.next = mark;
            mTail = mTail.next;
            total += mark.stopWatchInteval;
            size++;

            if (label != null && maxLabelLen < label.length()) {
                maxLabelLen = label.length();
            }
        }
    }

    public void reset() {
        size = 0;
        total = 0;
        mHead = mTail = min = max = null;
    }

    public long getTotoal() {
        return total;
    }

    static class EmptyMark extends StopWatchMark {

        EmptyMark(String label) {
            super(label);
        }
    }

    public static class StopWatchMark {
        String tip;
        StopWatchMark next;
        private long stopMark;
        private long stopMarkNano;
        private long stopWatchInteval;
        private String label;
        private int layer = 0;
        private List<StopWatchMark> inners;


        StopWatchMark(String label) {
            this.label = label;
            this.stopMarkNano = System.nanoTime();
            this.stopMark = TimeUnit.NANOSECONDS.toMillis(stopMarkNano);
        }

        void markInner(StopWatchMark mark) {
            if (mark != null) {
                if (inners == null) {
                    inners = new ArrayList<>();
                }
                mark.layer = layer + 1;
                inners.add(mark);
            }
        }

        public long getStopMark() {
            return stopMark;
        }

        public long getStopWatchInteval() {
            return stopWatchInteval;
        }

        public String getLabel() {
            return label;
        }
    }

    @Override
    public String toString() {
        return "DTimer=> {total:" + total + "; max/min:" + max + "/" + min +
                "} =#= {size:" + size + "; average: " + (total * 1.0D / size) + "}";
    }

    public void defaultPrintAllMarks() {
        final String sfTag = fTag == null ? "未命名timer" : fTag;
        final String printTag = "defaultPrintAllMarks[" + sfTag + "]";
        System.out.println(printTag + "::");
        System.out.println(printTag + ">>>>>>>>>>>>>>>>::" + (fTag == null ? "未命名timer" : fTag) + "::>>>>>>>>>>>>>>>>");
        System.out.println(printTag + ":: thread#" + Thread.currentThread().getName() + "| this#" + this.hashCode());
        StopWatchMark cursor = mHead;
        if (cursor == null) {
            System.err.println(printTag + "::" + (fTag == null ? "null异常cusor是空timer" : fTag));
            return;
        }
        while (true) {
            StopWatchMark next = cursor.next;
            if (next == null) {
                if (mIMarkPrinter != null) {
                    mIMarkPrinter.print(cursor, toString());
                }
                break;
            }
            if (mIMarkPrinter != null) {
                StringBuilder sb = new StringBuilder(cursor.label == null ? "" : cursor.label);
                final int max = maxLabelLen - sb.length();
                sb.append("_");
                for (int i = 0; i < max; i++) {
                    sb.append("$");
                }

                StringBuilder sb1 = new StringBuilder("");
                if (cursor.layer > 0) {
                    for (int i = 0; i < cursor.layer; i++) {
                        sb1.append("____");
                    }
                }
                mIMarkPrinter.print(cursor, printTag + "(label:" + (sb.toString()) + ")>>"
                        + sb1.toString()
                        + "stopWatch-at: " + cursor.stopMark
                        + "/"
                        + "stopWatchInteval: " + cursor.stopWatchInteval
                        + "; "
                        + (cursor.tip == null ? "" : "tip - : " + cursor.tip));

            }
            cursor = next;
        }
        if (mIMarkPrinter != null) {
            System.out.println(printTag + "|------------------------------------------------------------------------------|");
            //System.out.println(printTag + "|                                                                              |");
            mIMarkPrinter.print(cursor, printTag + "| count=" + size + "; fun-total=" + total
                    + ((mTail != null && mHead != null) ? "; summon= " + (mTail.stopMark - mHead.stopMark) : ""));
            if (min != null) {
                //System.out.println(printTag + "|                                                                              |");
                mIMarkPrinter.print(cursor, printTag + "| fastest[" + getMarkLabel(min) + "]: " + min.stopWatchInteval);
            }
            if (max != null) {
                //System.out.println(printTag + "|                                                                              |");
                mIMarkPrinter.print(cursor, printTag + "| slowest[" + getMarkLabel(max) + "]: " + max.stopWatchInteval);
            }
            //System.out.println(printTag + "|                                                                              |");
            System.out.println(printTag + "|------------------------------------------------------------------------------|");
        }

        System.out.println(printTag + "<<<<<<<<<<<<<<<<::" + fTag + "::<<<<<<<<<<<<<<<<\n");
        System.out.println(printTag + "");
    }

    private String getMarkLabel(StopWatchMark max) {
        return max == null || max.label == null ? "?" : max.label;
    }

    public interface IMarkPrinter {
        void print(StopWatchMark marked, String defaultLog);
    }

    public static class ThreadLocalManager {
        private static ThreadLocal<DpStopWatch> timerThreadLocal = new ThreadLocal<>();

        public static void newTimer(String tag) {
            currentTimer(true).setTag(tag);
        }

        public static DpStopWatch currentTimer() {
            return currentTimer(false);
        }


        private static DpStopWatch currentTimer(boolean clear) {
            if (clear) {
                timerThreadLocal.remove();
            }
            DpStopWatch dTimer = timerThreadLocal.get();
            if (dTimer == null) {
                dTimer = new DpStopWatch();
                timerThreadLocal.set(dTimer);
            }
            return dTimer;
        }

        public static void dumpCurrent() {
            currentTimer(false).defaultPrintAllMarks();
        }
    }


}

