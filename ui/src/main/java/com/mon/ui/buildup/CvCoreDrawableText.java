package com.mon.ui.buildup;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mon.ui.R;
import com.uls.utilites.ui.DensityUtils;
import com.uls.utilites.un.Useless;

import androidx.annotation.Nullable;


/**
 * Created by daiepngfei on 1/16/18
 */

public class CvCoreDrawableText extends FrameLayout implements ICoreDot {
    private TextView text;
    private ImageView image;
    public static final int HIT = 0;
    public static final int HTI = 1;
    public static final int VIT = 2;
    public static final int VTI = 3;
    public static final int COR_IMG_GRAVITY_LT = 0;
    public static final int COR_IMG_GRAVITY_RT = 1;
    public static final int COR_IMG_GRAVITY_LB = 2;
    public static final int COR_IMG_GRAVITY_RB = 3;
    private int corner_image_gravity = COR_IMG_GRAVITY_LT;
    private int corner_image_src = -1;
    private int corner_image_width = 0;
    private int corner_image_height = 0;
    private boolean corner_image_show = false;
    private ImageView cornerImage;
    private final int fLayoutMode;
    private boolean dotVisibile;
    private Paint mDotPaint;
    private RectF mDotRect = new RectF();
    private static final ImageView.ScaleType[] sScaleTypeArray = {
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };
    private int dotColor;
    private LinearLayout mRootLinear;

    public CvCoreDrawableText(Context context, int fLayoutMode, int imageWidth, int imageHeight, int margin) {
        super(context);
        initRootLinear(context);
        this.fLayoutMode = fLayoutMode;
        mRootLinear.setOrientation(isHorizontal() ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        mRootLinear.setGravity(Gravity.CENTER);

        image = new ImageView(context);
        text = new TextView(context);
        text.setTextSize(12);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setMaxEms(5);
        text.setLines(1);
        text.setTextColor(Color.BLACK);

        // set margins
        final LayoutParams imageParams = new LayoutParams(imageWidth, imageHeight);
        final LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.gravity = isHorizontal() ? Gravity.CENTER_VERTICAL : Gravity.CENTER_HORIZONTAL;
        resetImageLayoutMargin(margin, imageParams, textParams);
        text.setLayoutParams(textParams);
        image.setLayoutParams(imageParams);

        // opengl_render_init corner iamge
        cornerImage = new ImageView(context);
        final LayoutParams params = new LayoutParams(corner_image_width, corner_image_height);
        cornerImage.setLayoutParams(params);

        initialize();

    }

    public CvCoreDrawableText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initRootLinear(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CvCoreDrawableText);
        fLayoutMode = a.getInteger(R.styleable.CvCoreDrawableText_core_layout_mode, HIT);
        mRootLinear.setOrientation(isHorizontal() ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
        final boolean defaultGravity = a.getBoolean(R.styleable.CvCoreDrawableText_core_default_center_gravity, true);
        if (defaultGravity) mRootLinear.setGravity(Gravity.CENTER);
        image = new ImageView(context);
        image.setImageResource(a.getResourceId(R.styleable.CvCoreDrawableText_core_img_src, 0));
        text = new TextView(context);
        final boolean bold = a.getBoolean(R.styleable.CvCoreDrawableText_core_text_bold, false);
        if (bold) setTextStyleBold(true);
        text.setTextSize(a.getInteger(R.styleable.CvCoreDrawableText_core_drawable_text_size, 12));
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setMaxEms(a.getInteger(R.styleable.CvCoreDrawableText_core_drawable_text_max_ems, 5));
        final int ems = a.getInteger(R.styleable.CvCoreDrawableText_core_drawable_text_ems, -1);
        if(ems > 0) {
            text.setEms(ems);
        }
        final int textGravity = a.getInteger(R.styleable.CvCoreDrawableText_core_text_gravity, 0);
        if(textGravity > 0) {
            text.setGravity(textGravity == 1 ? Gravity.CENTER : Gravity.RIGHT);
        }
        text.setLines(1);
        text.setTextColor(a.getColor(R.styleable.CvCoreDrawableText_core_drawable_text_color, Color.BLACK));
        text.setText(a.getString(R.styleable.CvCoreDrawableText_core_drawable_text));
        final int index = a.getInt(R.styleable.CvCoreDrawableText_core_img_scale_type, -1);
        if (index >= 0) {
            image.setScaleType(sScaleTypeArray[index]);
        }
        text.setIncludeFontPadding(false);
        // set margins
        final int margin = (int) a.getDimension(R.styleable.CvCoreDrawableText_core_drawable_margin, DensityUtils.dip2px(context, 12));
        final int imageWidth = (int) a.getDimension(R.styleable.CvCoreDrawableText_core_img_width, 0.f);
        final int imageHeight = (int) a.getDimension(R.styleable.CvCoreDrawableText_core_img_height, 0.f);
        final LayoutParams imageParams = new LayoutParams(imageWidth, imageHeight);
        final LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textParams.gravity = isHorizontal() ? Gravity.CENTER_VERTICAL : Gravity.CENTER_HORIZONTAL;
        resetImageLayoutMargin(margin, imageParams, textParams);

        // opengl_render_init corner iamge
        cornerImage = new ImageView(context);
        corner_image_width = (int) a.getDimension(R.styleable.CvCoreDrawableText_core_corner_img_width, 0);
        corner_image_height = (int) a.getDimension(R.styleable.CvCoreDrawableText_core_corner_img_height, 0);
        final LayoutParams params = new LayoutParams(corner_image_width, corner_image_height);
        cornerImage.setLayoutParams(params);

        corner_image_show = a.getBoolean(R.styleable.CvCoreDrawableText_core_corner_img_show, false);
        if (corner_image_show) {
            corner_image_src = a.getResourceId(R.styleable.CvCoreDrawableText_core_corner_img_src, -1);
            corner_image_gravity = a.getInteger(R.styleable.CvCoreDrawableText_core_corner_img_gravity, 0);
            if(cornerImage == null) {
                showCornerImageResId(corner_image_src, corner_image_gravity);
            }
        }
        a.recycle();
        initialize();
    }

    protected void setCornerGravity(int index) {
        if(cornerImage == null || cornerImage.getLayoutParams() == null) return;
        LayoutParams params = (LayoutParams) cornerImage.getLayoutParams();
        switch (index) {
            case COR_IMG_GRAVITY_LT:
                params.gravity = Gravity.START | Gravity.TOP;
                break;
            case COR_IMG_GRAVITY_RT:
                params.gravity = Gravity.END | Gravity.TOP;
                break;
            case COR_IMG_GRAVITY_LB:
                params.gravity = Gravity.START | Gravity.BOTTOM;
                break;
            case COR_IMG_GRAVITY_RB:
                params.gravity = Gravity.END | Gravity.BOTTOM;
                break;
            default:
        }
    }

    public void showCornerImage(String url, int gravity){
        if(Useless.isEmpty(url) || !corner_image_show || cornerImage == null) return;
        cornerImage.setVisibility(View.VISIBLE);
        setCornerGravity(gravity);
        Glide.with(getContext()).load(url).into(cornerImage);
    }

    public void hideCornerImage(){
        if(cornerImage != null) cornerImage.setVisibility(View.INVISIBLE);
    }

    public void showCornerImageResId(int resid, int gravity){
        if(resid <= 0 || !corner_image_show || cornerImage == null) return;
        setCornerGravity(gravity);
        cornerImage.setImageResource(resid);
    }

    protected void initRootLinear(Context context) {
        if (mRootLinear == null) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            addView(linearLayout);
            mRootLinear = linearLayout;
        }
    }

    private void resetImageLayoutMargin(int margin, LayoutParams imageParams, LayoutParams textParams) {
        if (isOrderIT()) {
            if (fLayoutMode == HIT) textParams.leftMargin = margin;
            else if (fLayoutMode == VIT) textParams.topMargin = margin;
        } else {
            if (fLayoutMode == HTI) imageParams.leftMargin = margin;
            else if (fLayoutMode == VTI) imageParams.topMargin = margin;
        }
        text.setLayoutParams(textParams);
        image.setLayoutParams(imageParams);
        image.requestLayout();
        text.requestLayout();
    }

    public void setTextStyleBold(boolean bold) {
        this.text.setTypeface(Typeface.defaultFromStyle(bold ? Typeface.BOLD : Typeface.NORMAL));
        this.text.postInvalidate();
    }

    public void setDrawableResource(int res) {
        this.image.setImageResource(res);
        this.image.postInvalidate();
    }

    public void setText(CharSequence text) {
        this.text.setText(text);
    }

    public void setText(int text) {
        this.text.setText(text);
    }

    private void initialize() {
        mDotPaint = new Paint();
        mDotPaint.setAntiAlias(true);
        mDotPaint.setColor(getResources().getColor(R.color.color_core_default_color_dot));
        final boolean it = isOrderIT();
        mRootLinear.addView(it ? image : text);
        mRootLinear.addView(it ? text : image);
        if(cornerImage != null) addView(cornerImage);
    }

    public void setImageUrl(String url, int placeholder) {
        Glide.with(getContext()).load(url).placeholder(placeholder).into(image);
    }

    public void setImageResouces(int res){
        image.setImageResource(res);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (dotVisibile) {
            final int size = DensityUtils.dip2px(getContext(), getResources().getInteger(R.integer.dimen_core_default_dot_size)) * 2;
            final int margin = -(size / 4);
            int r, t;
            // get top-right point
            if (fLayoutMode == HIT || fLayoutMode == VTI) {
                r = text.getRight();
                t = text.getTop();
            } else {
                r = image.getRight();
                t = image.getTop();
            }
            this.mDotPaint.setColor(dotColor);
            mDotRect.set(r + margin, t - margin, r + margin + size, t - margin - size);
            canvas.drawOval(mDotRect, mDotPaint);
        }
    }

    public void setTextColor(int color) {
        text.setTextColor(color);
    }

    @SuppressWarnings("unused")
    public void setTextMaxEms(int ems) {
        text.setMaxEms(ems);
    }

    @SuppressWarnings("unused")
    public void setTextVisibility(int visibility) {
        text.setVisibility(visibility);
    }

    public void setImageVisibility(int visibility) {
        image.setVisibility(visibility);
    }

    private boolean isHorizontal() {
        return fLayoutMode == HIT || fLayoutMode == HTI;
    }

    private boolean isOrderIT() {
        return fLayoutMode == HIT || fLayoutMode == VIT;
    }

    public void setDotVisibile(boolean dotVisibile) {
        this.dotVisibile = dotVisibile;
        invalidate();
    }

    public void setDotColorRes(int res) {
        this.dotColor = getResources().getColor(res);
    }

    @Override
    public void showDot(boolean show) {
        setDotColorRes(Color.RED);
        setDotVisibile(show);
    }
}
