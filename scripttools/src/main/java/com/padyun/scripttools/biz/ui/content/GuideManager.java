package com.padyun.scripttools.biz.ui.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.mon.ui.app.AppDelegate;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.views.CvBlurGuideView;
import com.uls.utilites.un.Useless;

import androidx.annotation.NonNull;


/**
 * Created by daiepngfei on 9/11/19
 */
public class GuideManager {

    public static boolean hasShowed(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        return sp.getBoolean("showed", false);
    }

    public static void setShowed(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putBoolean("showed", true).apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("guide_ui", Context.MODE_PRIVATE);
    }

    public static void showGuideWithViewIfPossible(Context context, View anchorView, CvBlurGuideView guideView, final View.OnClickListener l) {
        if (hasShowed(context)/* || anchorView.getVisibility() == View.VISIBLE*/) return;
        final int scale = 3;
        final ViewTreeObserver observer = anchorView.getViewTreeObserver();
        anchorView.setVisibility(View.VISIBLE);
        final ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            private int rWidth;
            private int count;

            @Override
            public boolean onPreDraw() {
                final int width = anchorView.getMeasuredWidth();
                System.out.println("onPreDraw --- " + width);
                if (width > 0) {
                    if (rWidth != width) {
                        rWidth = width;
                        count = 0;
                    } else {
                        count++;
                        if (count >= 2) {
                            final Rect rect = computeRect(width, anchorView, scale);
                            final ViewTreeObserver.OnPreDrawListener opl = this;
                            anchorView.getViewTreeObserver().removeOnPreDrawListener(opl);
                            guideView.show(rect, v -> {
                                guideView.dismiss();
                                if (l != null) l.onClick(v);
                            });
                        }
                    }
                }
                return true;
            }
        };
        observer.addOnPreDrawListener(onPreDrawListener);
        System.out.println("onPreDraw --2-- " + anchorView.getMeasuredWidth());
    }

    @NonNull
    public static Rect computeRect(int width, View anchorView, int scale) {
        final int size = Math.max(width, anchorView.getMeasuredHeight());
        final int left = anchorView.getLeft() + width / 2 - size / 2;
        final int top = anchorView.getTop() + anchorView.getMeasuredHeight() / 2 - size / 2;
        return new Rect(left - scale, top - scale, left + size + scale, top + size + scale);
    }

    public static void clearAiAssitantGuidesRecord() {
        SharedPreferences sp = AppDelegate.app()
                .getSharedPreferences("AI_ASSISTANT_BHV", Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    private static class ConsTypeNames {
        private static final String GROUP_CROP = "crop";
        private static final String GROUP_CROP_V2_CLICK = "cropv2click";
        private static final String GROUP_CROP_V2_FINISH = "cropv2finish";
        private static final String GROUP_CROP_V2_SLIDE = "cropv2slide";
        private static final String GROUP_CROP_V2_FLAG = "cropv2flag";
        private static final String GROUP_CROP_V2_OFFSET = "cropv2offset";
        private static final String EDIT_BACK = "edit_back";
        private static final String EDIT_ENTER = "edit_enter";
        private static final String AI_TASK_LIST = "ai_task_list";
        private static final String AI_STREAM_TASK_LIST = "ai_stream_task_list";
    }

    public enum ImageGuideType {
        AI_TASK_LIST(ConsTypeNames.AI_TASK_LIST, R.drawable.bg_page_guide_task_list),
        AI_TASK_LIST2(ConsTypeNames.AI_TASK_LIST, R.drawable.bg_page_guide_task_list_script_button),
        AI_STREAM_TASK_LIST(ConsTypeNames.AI_STREAM_TASK_LIST, R.drawable.bg_page_guide_stream_add_task_float_button),
        AI_STREAM_TASK_LIST2(ConsTypeNames.AI_STREAM_TASK_LIST, R.drawable.bg_page_guide_stream_add_task),
        EDIT_ENTER(ConsTypeNames.EDIT_ENTER, R.drawable.bg_page_guide_script_tool_buttons),
        EDIT_BACK_FROM_STREAM_1(ConsTypeNames.EDIT_BACK, R.drawable.bg_page_guide_script_edit_item_options),
        EDIT_BACK_FROM_STREAM_2(ConsTypeNames.EDIT_BACK, R.drawable.bg_page_guide_script_edit_item_options2),

        /*CROP_ACTION(ConsTypeNames.GROUP_CROP, R.drawable.bg_page_guide_float_button),
        CROP_OPTION_CLICK(ConsTypeNames.GROUP_CROP, R.drawable.bg_page_guide_crop_popup_buttons),
        CROP_OPTION_FINISH(ConsTypeNames.GROUP_CROP, R.drawable.bg_page_guide_crop_popup_buttons_finish),
        CROP_OPTION_DETAIL_OFFSET(ConsTypeNames.GROUP_CROP, R.drawable.bg_page_guide_crop_offset),
        CROP_OPTION_DETAIL_SLIDE(ConsTypeNames.GROUP_CROP, R.drawable.bg_page_guide_crop_slide),*/

        CROP_OPTION_V2_CLICK(ConsTypeNames.GROUP_CROP_V2_FLAG, R.drawable.bg_guide_script_tools_help_click);
        /*CROP_OPTION_V2_CLICK(ConsTypeNames.GROUP_CROP_V2_CLICK, R.drawable.bg_page_guide_crop_popup_buttons),
        CROP_OPTION_V2_FINISH(ConsTypeNames.GROUP_CROP_V2_FINISH, R.drawable.bg_page_guide_crop_popup_buttons_finish),
        CROP_OPTION_DETAIL_V2_OFFSET(ConsTypeNames.GROUP_CROP_V2_OFFSET, R.drawable.bg_page_guide_crop_offset),
        CROP_OPTION_DETAIL_V2_SLIDE(ConsTypeNames.GROUP_CROP_V2_SLIDE, R.drawable.bg_page_guide_crop_slide);*/

        private int resId;
        private String alias;

        ImageGuideType(String name, int resId) {
            this.resId = resId;
            this.alias = name;
        }

        public String getAlias() {
            return alias;
        }

        public int getResId() {
            return resId;
        }
    }

    public static class ConsDimens {
        public static final int ACTION_IMAGE_WIDTH = 110;
        public static final int ACTION_IMAGE_HEIGHT = 60;
        public static final int ACTION_IMAGE_BOTTOM_MARGIN_H = 80;
        public static final int ACTION_IMAGE_BOTTOM_MARGIN_V = 65;
    }

    /**
     * @return
     */
    public static boolean hasShowed(Context ctx, ImageGuideType type) {
        SharedPreferences sp = ctx.getSharedPreferences("AI_ASSISTANT_BHV", Context.MODE_PRIVATE);
        return sp.getBoolean("guide_image_ever_showed_" + type.getAlias(), false);
    }

    /**
     * @param ctx
     * @param type
     * @param show
     */
    public static void setShowed(Context ctx, ImageGuideType type, boolean show) {
        SharedPreferences sp = ctx.getSharedPreferences("AI_ASSISTANT_BHV", Context.MODE_PRIVATE);
        sp.edit().putBoolean("guide_image_ever_showed_" + type.getAlias(), show).apply();
    }

    /**
     * @param ctx
     * @param bgImageView
     * @param actionImageView
     * @param types
     */
    public static void simplelyShowImageGuides(Context ctx, ImageView bgImageView, ImageView actionImageView, ImageGuideType... types) {
        if (Useless.nulls(ctx, bgImageView, actionImageView) || Useless.isEmpty(types)) {
            return;
        }

        if (types[0] != null && !hasShowed(ctx, types[0])) {
            bgImageView.setVisibility(View.VISIBLE);
            actionImageView.setVisibility(View.VISIBLE);
            simpleShowImageGuide(ctx, bgImageView, actionImageView, 0, types);
        }
    }

    /**
     * @param ctx
     * @param bgImageView
     * @param actionImageView
     * @param index
     * @param types
     */
    private static void simpleShowImageGuide(Context ctx, ImageView bgImageView, ImageView actionImageView, int index, ImageGuideType[] types) {

        if (index >= types.length) {
            bgImageView.setVisibility(View.GONE);
            actionImageView.setVisibility(View.GONE);
            return;
        }

        final ImageGuideType type = types[index];
        final int nextIndex = index + 1;
        if (type == null) {
            simpleShowImageGuide(ctx, bgImageView, actionImageView, nextIndex, types);
            return;
        }

        bgImageView.setImageResource(type.getResId());
        final boolean typeFinish = index == types.length - 1;
        actionImageView.setImageResource(typeFinish ? R.drawable.ic_v3_script_guide_button_finish
                : R.drawable.ic_v3_script_guide_button_next);
        actionImageView.setOnClickListener(v -> {
            if (typeFinish) {
                setShowed(ctx, type, true);
            }
            simpleShowImageGuide(ctx, bgImageView, actionImageView, nextIndex, types);
        });
    }

}
