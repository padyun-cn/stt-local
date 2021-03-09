package com.uls.stlocalservice.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mon.ui.list.compat.fragment.IFragmentGenerator;
import com.padyun.manifest.AppAddressBook;
import com.padyun.scripttools.biz.ui.activity.AcOverlayCaptureStreamPlay;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.content.SmpClickerDetector;
import com.padyun.scripttools.biz.ui.fragment.FmScriptTestingLogHorizontal;
import com.padyun.scripttools.biz.ui.views.CvBlurGuideView;
import com.padyun.scripttools.biz.ui.views.CvColorDropperDragLayout;
import com.padyun.scripttools.biz.ui.views.CvCropOffsetAndSlideLayout;
import com.padyun.scripttools.biz.ui.views.CvPixelView;
import com.padyun.scripttools.biz.ui.views.StrokeDrawerView;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.compat.data.CropImgGroupParceble;
import com.padyun.scripttools.compat.data.CropImgParceble;
import com.padyun.scripttools.content.data.SEImageInfoFileWriter;
import com.padyun.scripttools.module.runtime.AssetsManager;
import com.padyun.scripttools.module.runtime.StContext;
import com.padyun.scripttools.module.runtime.test.TestProxy;
import com.padyun.scripttoolscore.compatible.data.model.ImageInfo;
import com.padyun.scripttoolscore.compatible.data.model.SEColor;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uls.stlocalservice.R;
import com.uls.stlocalservice.ui.floating.ScriptOverlays;
import com.uls.utilites.common.IAccept;
import com.uls.utilites.content.CAsync;
import com.uls.utilites.content.CoreWorkers;
import com.uls.utilites.content.ToastUtils;
import com.uls.utilites.ui.DensityUtils;
import com.uls.utilites.ui.RectTools;
import com.uls.utilites.ui.Viewor;
import com.uls.utilites.un.ObserverRunnable;
import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.LogUtil;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

@SuppressWarnings("all")
public class OverlayCropActivity extends AcOverlayCaptureStreamPlay implements IFragmentGenerator {

    public static final String KEY_CROPINFO = "crop_info";
    public static final String KEY_IMG = "crop_img";
    private CropImageView mCropImageView;
    private ArrayList<Parcelable> mEntities = new ArrayList<>();
    private ArrayList<RectF> mContourRects = new ArrayList<>();
    private View mVBluePointerLayout, mVBlueHelpButton, mVFunc;
    private CvPixelView mCvPixelView;
    private View mPixcelColorPanelContent, mPixcelPanelLayout;
    private View mEyeDropper;
    private CvColorDropperDragLayout mColorDropperDragLayout;
    private CvCropOffsetAndSlideLayout mCropOprateView;
    private List<CropAction> mCropActions = new ArrayList<>();
    private int mBitmapWidth, mBitmapHeight;
    private boolean isZooming = false;


    /*@Override
    public boolean autoStartOnStart() {
        return false;
    }*/

    public boolean isZooming() {
        return isZooming;
    }

    public void setZooming(boolean zooming) {
        this.isZooming = zooming;
    }

    /*@org.jetbrains.annotations.Nullable
    @Override
    protected GSConfig onGsConfig() {
        return GSConfig.Builder.withTypeAndMode(StContext.config().getCodecType(), GSConfig.VideoCodecMode.ASYNC)
                .ip(ScriptTestConfig.getServerIp())
                .asip(ScriptTestConfig.getAsIp())
                .verify(ScriptTestConfig.getDefaultVerify())
                .configureModule(ScriptTestConfig.genDefaultConfigModule())
                .audioConfigIndex(ScriptTestConfig.getAudionConfigType())
                .build();
    }*/

    private enum Mode {
        GAME, CROP_FROM_FILE, CROP, DATA_REGION, DATA_CROP, COLOR, JUST_CROP_CLICK, JUST_CROP_OFFSET, JUST_CROP_SLIDE, DATA_COLOR, DATA_NONEXIST
    }

    private enum CropAction {
        CLICK, OFFSET, SLIDE, FLAG, NEXT_ACTION, FINISH, IDEL, OFFSET_ONLY;
    }

    final Runnable mOnReleaseRunnable = () -> {
        final int[] eddl = new int[2];
        mEyeDropper.getLocationOnScreen(eddl);
        final int[] pdl = new int[2];
        mPixcelColorPanelContent.getLocationOnScreen(pdl);
        if (new Rect(pdl[0], pdl[1], pdl[0] + mPixcelColorPanelContent.getMeasuredWidth(), pdl[1] + mPixcelColorPanelContent.getMeasuredHeight())
                .intersect(eddl[0], eddl[1], eddl[0] + mEyeDropper.getMeasuredWidth(), eddl[1] + mEyeDropper.getMeasuredHeight())) {
            final ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mPixcelColorPanelContent.getLayoutParams();
            params.rightToRight = params.rightToRight == ConstraintLayout.LayoutParams.PARENT_ID ? ConstraintLayout.LayoutParams.UNSET : ConstraintLayout.LayoutParams.PARENT_ID;
            params.leftToLeft = params.leftToLeft == ConstraintLayout.LayoutParams.PARENT_ID ? ConstraintLayout.LayoutParams.UNSET : ConstraintLayout.LayoutParams.PARENT_ID;
            mPixcelColorPanelContent.setLayoutParams(params);
            mPixcelColorPanelContent.requestLayout();
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mSurfaceView.setVisibility(View.VISIBLE);
        mOverlayContainer.setClickable(true);
        mCropActions.add(CropAction.IDEL);
        onPrepareToStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private Runnable mDisposedOnTouchCroppedRunnable = null;
    private Runnable mDisposedOnCropDoneRunnable = null;

    @Override
    public void onResume(){
        super.onResume();
        showGuide();
        ScriptOverlays.Agent.onEditing1(this);
    }

    private void onPrepareToStart() {
        Intent intent = getIntent();
        final int reqMode = intent.getIntExtra(SECons.Ints.KEY_CROP_REQ_MODE, SECons.Ints.VALUE_CROP_REQ_MODE_NORMAL);
        switch (reqMode) {
            case SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_CROP:
                switchEditMode(Mode.DATA_CROP);
                break;
            case SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_REGION:
                switchEditMode(Mode.DATA_REGION);
                break;
            case SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_COLOR:
                switchEditMode(Mode.DATA_COLOR);
                break;
            case SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_CROP_OFFSET:
                switchEditMode(Mode.JUST_CROP_OFFSET);
                break;
            case SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_CROP_SLIDE:
                switchEditMode(Mode.JUST_CROP_SLIDE);
                break;
            case SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_CROP_CLICK:
                switchEditMode(Mode.JUST_CROP_CLICK);
                break;
            case SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_NONEXIST:
                switchEditMode(Mode.DATA_NONEXIST);
                break;
            case SECons.Ints.VALUE_CROP_REQ_MODE_CROP_FROM_FILE:
                switchEditMode(Mode.CROP_FROM_FILE);
                break;
            default:
                switchEditMode(Mode.GAME);
        }
    }

    private ProgressDialog mProgressDialog;
    private ImageView mGuideImage, mGuideActionImage;

    final static class GuideClickListener {
        private static final RectF ORI_GUIDE_IMAGE_ACTION_BUTTON_RECT = new RectF(838F, 867F, 838F + 244F, 867F + 128F);
        private static final float ORI_GUIDE_IMAGE_WIDTH = 1920F;
        private static final float ORI_GUIDE_IMAGE_HEIGHT = 1080F;
        private boolean isUsed = false;
        GestureDetector gestureDetector;

        public GuideClickListener(Context context, View tar, Runnable onClick) {
            gestureDetector = new GestureDetector(context,
                    new GuideImageActionGestureListener(ORI_GUIDE_IMAGE_ACTION_BUTTON_RECT, ORI_GUIDE_IMAGE_WIDTH, ORI_GUIDE_IMAGE_HEIGHT,
                            tar, () -> {
                        if (onClick != null) {
                            onClick.run();
                        }
                        isUsed = true;
                    }));
        }

        boolean isUsed() {
            return isUsed;
        }

        void onTouchEvent(MotionEvent motionEvent) {
            this.gestureDetector.onTouchEvent(motionEvent);
        }
    }

    final static class GuideImageActionGestureListener extends GestureDetector.SimpleOnGestureListener {
        private RectF oriTarRect;
        private float oriWidth, oriHeight;
        private View tarScaleView;
        private Runnable onClick;

        GuideImageActionGestureListener(RectF oriTarRect, float oriWidth, float oriHeight, View tarScaleView, Runnable onClick) {
            this.oriTarRect = oriTarRect;
            this.oriHeight = oriHeight;
            this.oriWidth = oriWidth;
            this.tarScaleView = tarScaleView;
            this.onClick = onClick;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return RectTools.isContainsMotionPointInScaledView(e, tarScaleView, oriTarRect, oriWidth, oriHeight);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            final boolean triggled = RectTools.isContainsMotionPointInScaledView(e, tarScaleView, oriTarRect, oriWidth, oriHeight);
            if (triggled && onClick != null) {
                onClick.run();
            }
            return triggled;
        }
    }

    private View.OnTouchListener getSpcificOriGuideImageToucher(Runnable... actions) {

        if (actions == null || actions.length == 0) {
            return null;
        }

        final GuideClickListener[] guideClickListeners = new GuideClickListener[actions.length];

        for (int i = 0; i < actions.length; i++) {
            final Runnable action = actions[i];
            guideClickListeners[i] = new GuideClickListener(this, mGuideImage, action);
        }

        return new SimpleClickQueueGuideImagesToucher(guideClickListeners);
    }

    final static class SimpleClickQueueGuideImagesToucher implements View.OnTouchListener {
        private final GuideClickListener[] gestureDetectors;
        private int index;

        SimpleClickQueueGuideImagesToucher(GuideClickListener... gestureDetector) {
            this.gestureDetectors = gestureDetector;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (gestureDetectors == null || index >= gestureDetectors.length) {
                return false;
            }
            gestureDetectors[index].onTouchEvent(event);
            if (gestureDetectors[index].isUsed()) {
                index++;
            }
            return true;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected View[] onCreateOverallOverlay(FrameLayout container) {

        View view = LayoutInflater.from(this).inflate(R.layout.include_overlay_stream_control_panel, container, false);
        final View cropButton = view.findViewById(R.id.button_crop);
        final View.OnClickListener onClickListener = v -> screenshot(() -> switchEditMode(Mode.CROP));
        cropButton.setOnClickListener(onClickListener);
        view.findViewById(R.id.button_color).setOnClickListener(v -> screenshot(() -> switchEditMode(Mode.COLOR)));
        final View backSaveButton = view.findViewById(R.id.button_back_save);
        backSaveButton.setOnClickListener(v -> finish());
        view.setVisibility(View.INVISIBLE);
        mVFunc = view;

        View viewDragLayout = LayoutInflater.from(this).inflate(R.layout.include_stream_drag_floating_button, container, false);
        mVBluePointerLayout = viewDragLayout;
        final View buttonPointer = viewDragLayout.findViewById(R.id.button_floating);
        /*view.setVisibility(view.getVisibility() != View.VISIBLE ? View.VISIBLE : View.INVISIBLE)*/
        buttonPointer.setOnClickListener(onClickListener);


        ImageView guideImage = new ImageView(this);
        guideImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        guideImage.setBackgroundColor(Color.BLACK);
        guideImage.setScaleType(ImageView.ScaleType.FIT_XY);
        mGuideImage = guideImage;
        ImageView guideActionImage = new ImageView(this);
        final int actionButtonWidth = DensityUtils.dip2px(this, 110);
        final int actionButtonHeight = DensityUtils.dip2px(this, 60);
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(actionButtonWidth, actionButtonHeight);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layoutParams.bottomMargin = DensityUtils.dip2px(this, isOrientationPortrait() ? 65 : 80);
        guideActionImage.setLayoutParams(layoutParams);
        guideActionImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mGuideActionImage = guideActionImage;
        mGuideImage.setVisibility(View.GONE);
        mGuideActionImage.setVisibility(View.GONE);

        View viewHelpDragLayout = LayoutInflater.from(this).inflate(R.layout.include_stream_drag_floating_button_help, container, false);
        final View buttonHelpPointer = viewHelpDragLayout.findViewById(R.id.button_floating);
        /*view.setVisibility(view.getVisibility() != View.VISIBLE ? View.VISIBLE : View.INVISIBLE)*/
        mVBlueHelpButton = buttonHelpPointer;
        buttonHelpPointer.setOnClickListener(v -> {
            if (mCurMode == Mode.CROP) {
                guideImage.setVisibility(View.VISIBLE);
                final CropAction headCropAction = getCurrentHeadCropState();

                // set image resouce with res id
                switch (headCropAction) {
                    case CLICK:
                        guideImage.setImageResource(R.drawable.bg_guide_script_tools_help_click);
                        break;
                    case OFFSET:
                        guideImage.setImageResource(R.drawable.bg_guide_script_tools_help_offset);
                        break;
                    case FLAG:
                        guideImage.setImageResource(R.drawable.bg_guide_script_tools_help_flag_1);
                        break;
                    case SLIDE:
                        guideImage.setImageResource(R.drawable.bg_guide_script_tools_help_slide);
                        break;
                    case FINISH:
                        guideImage.setImageResource(R.drawable.bg_guide_script_tools_help_finish);
                        break;
                    default:
                }

                // set on click with action runnable
                final Runnable finalRunable = () -> {
                    guideImage.setVisibility(View.GONE);
                    showCropBubbleOptions();
                };
                Runnable[] actions;
                if (headCropAction != CropAction.FLAG) {
                    actions = new Runnable[]{finalRunable};
                } else {
                    actions = new Runnable[]{
                            () -> guideImage.setImageResource(R.drawable.bg_guide_script_tools_help_flag_2),
                            () -> guideImage.setImageResource(R.drawable.bg_guide_script_tools_help_flag_3),
                            finalRunable
                    };
                }

                guideImage.setOnTouchListener(getSpcificOriGuideImageToucher(actions));

                // dismiss crop buble options with the guide image showing
                dismissCropBubbleOptions();
            }
        });

        // all guide view
        return new View[]{/*view,*/ viewDragLayout/*, blueGuide, cropGuide, tryGuide, doneGuide, backGuide*/, viewHelpDragLayout, guideImage, guideActionImage};
    }

    @NonNull
    private CvBlurGuideView getGenerCvBlurGuideView() {
        CvBlurGuideView blueGuide = new CvBlurGuideView(this);
        blueGuide.setCornerRadius(DensityUtils.dip2px(this, 3));
        blueGuide.setDashWidth(DensityUtils.dip2px(this, 2));
        blueGuide.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        return blueGuide;
    }

    private View mDoneButton, mCropControlPanel;

    @Override
    protected View[] onCreateImageRemainingOverlay(FrameLayout container) {
        View view = LayoutInflater.from(this).inflate(R.layout.include_crop_confirm_panel, container, false);
        final View doneButton = view.findViewById(R.id.button_crop_done);
        mCropControlPanel = view;
        mDoneButton = doneButton;
        doneButton.setOnClickListener(this::onButtonConfirmCrop);
        view.findViewById(R.id.button_crop_cancel).setOnClickListener(v -> onButtonCancelCrop());
        return new View[]{/*view*/};
    }

    /**
     *
     */
    private void onButtonCancelCrop() {
        if (mCurMode == Mode.CROP || mCurMode == Mode.COLOR) {
            switchEditMode(Mode.GAME);
        } else {
            setResult(Activity.RESULT_CANCELED, new Intent());
            doFinish();
        }
    }

    /**
     * @param v
     */
    private void onButtonConfirmCrop(View v) {
        v.setEnabled(false);
        if (mCurMode == Mode.CROP) {
            // final String path = ScriptCons.Tools.genScreenFilePath();
            showProgress();
            StContext.assets().storeRawOnWorkers(getCurBitmap(), file -> {
                if (file != null) {
                    settleWithView(v, file.getAbsolutePath(), () -> v.setEnabled(true));
                } else {
                    dismissProgress();
                    v.setEnabled(true);
                    ToastUtils.show(OverlayCropActivity.this, R.string.string_toast_capture_failed);
                }
            });
            /*final String path = StContext.assets().newRaw().getAbsolutePath();
            justSaveCurrentScreen(path, () -> settleWithView(v, path, null), () -> v.setEnabled(true));*/
        } else if (mCurMode == Mode.COLOR) {
            /*final String path = StContext.assets().newRaw().getAbsolutePath();
            justSaveCurrentScreen(path, () -> {
                mEntities.add(getSeColor(path));
                switchEditMode(Mode.GAME);
                setFinishWithResult();
            }, () -> v.setEnabled(true));*/
        } else if (mCurMode == Mode.DATA_CROP) {
            SEImage oriImage = getIntent().getParcelableExtra(SECons.Ints.KEY_CROP_IMG);
            SEImage image = newEmptySEImage(oriImage.getImageOriginal());
            if (image != null) {
                showProgress();
                CoreWorkers.on(() -> {
                    try {
                        File f = StContext.assets().process(new File(image.getImageOriginal()),
                                image.getImageX(), image.getImageY(), image.getImageH(), image.getImageH());
                        if (f != null) {
                            image.setCropFileName(f.getName());
                            image.setImageCropPath(f.getAbsolutePath());
                            /* MODIFIED BY DAIPENGFEI on 2021-02-20. */
                            return SEImageInfoFileWriter.writeToFile(image);
                             //return StContext.network().applyTest().updateImageSync(image);
                            /* MODIFIED END. */
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }).then(r -> {
                    dismissProgress();
                    v.setEnabled(true);
                    if (r) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(SECons.Ints.KEY_CROP_IMG, image);
                        intent.putExtras(bundle);
                        setResult(Activity.RESULT_OK, intent);
                        doFinish();
                    }
                });
                /*ImageCropUploader.saveAndUpload(ip(), getAsIp(), image, image.getBounds(), image.getImageCropPath(), r -> {

                });*/
            }
            v.setEnabled(true);

        } else if (mCurMode == Mode.DATA_REGION) {
            Intent intent = new Intent();
            Rect cropInfo = new Rect();
            cropInfo.left = (int) mCropImageView.getLeftPoint();
            cropInfo.top = (int) mCropImageView.getTopPoint();
            cropInfo.right = (int) mCropImageView.getRightPoint();
            cropInfo.bottom = (int) mCropImageView.getBottomPoint();
            intent.putExtra(SECons.Ints.KEY_CROP_REGION_RECT, cropInfo);
            setResult(Activity.RESULT_OK, intent);
            doFinish();
        } else if (mCurMode == Mode.DATA_COLOR) {
            /*final String path = StContext.assets().newRaw().getAbsolutePath();
            justSaveCurrentScreen(path, () -> {
                SEColor seColor = getSeColor(path);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable(SECons.Ints.KEY_COLOR_ENTITY, seColor);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                doFinish();
            }, () -> v.setEnabled(true));*/
        } else {
            v.setEnabled(true);
        }
    }

    /**
     *
     * @param v
     * @param path
     * @param anyway
     */
    private void settleWithView(View v, String path, Runnable anyway) {
        LinkedHashMap<CropAction, SEImage> seActionImageMap = new LinkedHashMap<>();
        List<Rect> rectFs = mCropImageView.getAllCropRects();

        for (int i = 0; i < mCropActions.size() && i < rectFs.size(); i++) {
            SEImage image = newEmptySEImageWithSpecifiedRect(path, rectFs.get(i));
            if (image == null) continue;
            seActionImageMap.put(mCropActions.get(i), image);
        }

        if (seActionImageMap.size() > 0) {
            showProgress();
            final File rawFile = new File(path);
            final AssetsManager assets = StContext.assets();
            final TestProxy testProxy = StContext.network().applyTest();
            CoreWorkers
                    .on(() -> {
                        for (SEImage img : seActionImageMap.values()) {
                            final File file = assets.process(rawFile, img.getImageX(), img.getImageY(), img.getImageW(), img.getImageH());
                            if (file == null) {
                                return false;
                            }
                            img.setImageCropPath(file.getAbsolutePath());
                            img.setCropFileName(file.getName());
                            if(!SEImageInfoFileWriter.writeToFile(img)){
                                return false;
                            }
                            // TODO: 需要调试的代码
                            /*if (!testProxy.updateImageSync(img)) {
                                return false;
                            }*/
                        }
                        return true;
                    })
                    .then(r -> {
                        if (r) {
                            dataWithCropImageParceble(seActionImageMap, null);
                            onDisposedCropSave();
                        }
                        if (v != null) {
                            v.setEnabled(true);
                        }
                        dismissProgress();

                        if (anyway != null) {
                            anyway.run();
                        }
                    });


        } else {
            if (v != null) {
                v.setEnabled(true);
            }
        }
    }

    private void dismissProgress() {
        mProgressDialog.dismiss();
    }

    private void onDisposedCropSave() {
        if (mDisposedOnCropDoneRunnable != null) {
            mDisposedOnCropDoneRunnable.run();
            mDisposedOnCropDoneRunnable = null;
        }
    }

    @NonNull
    private SEColor getSeColor(String path) {
        Point p = mCvPixelView.getPixel();
        final int r = 100;
        p.x = calcSpecifiedImageWithHorizontal(p.x);
        p.y = calcSpecifiedImageWithVertical(p.y);
        Rect rect = new Rect(Math.max(0, p.x - r), Math.max(0, p.y - r), Math.min(p.x + r, getSpecifiedImageWidth()), Math.min(p.y + r, getSpecifiedImageHeight()));
        return new SEColor(mCvPixelView.getPickedColor(), path, rect);
    }

    private SEImage newEmptySEImage(String path) {
        ImageInfo cropInfo = new ImageInfo();
        cropInfo.x = (int) mCropImageView.getLeftPoint();
        cropInfo.y = (int) mCropImageView.getTopPoint();
        cropInfo.w = (int) mCropImageView.getRightPoint() - cropInfo.x;
        cropInfo.h = (int) mCropImageView.getBottomPoint() - cropInfo.y;

        /*final String cropSavedPath = StContext.assets().newProccessed().getAbsolutePath();
        if (Useless.isEmpty(path)) return null;*/
        SEImage image = new SEImage();
        image.setDescName("");
        /*image.setCropFileName(Files.name(cropSavedPath));
        image.setImageCropPath(cropSavedPath);*/
        image.setImageOriginal(path);
        image.setImageInfo(cropInfo);
        return image;
    }

    private SEImage newEmptySEImageWithSpecifiedRect(String path, Rect r) {
        if (r == null || path == null) return null;
        ImageInfo cropInfo = new ImageInfo();
        cropInfo.x = r.left;
        cropInfo.y = r.top;
        cropInfo.w = r.width();
        cropInfo.h = r.height();

        /*final String cropSavedPath = StContext.assets().newProccessed().getAbsolutePath();
        if (Useless.isEmpty(path)) return null;*/
        SEImage image = new SEImage();
        image.setDescName("");
        /*image.setCropFileName(Files.name(cropSavedPath));
        image.setImageCropPath(cropSavedPath);*/
        image.setImageOriginal(path);
        image.setImageInfo(cropInfo);
        return image;
    }

    private StrokeDrawerView mCropTapOverlay;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected View[] onCreateImageOverlay(FrameLayout container) {
        setImageViewVisibility(View.INVISIBLE);

        // toCreate crop view
        final CropImageView cropImageView = new CropImageView(this);
        final int match_parent = FrameLayout.LayoutParams.MATCH_PARENT;
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(match_parent, match_parent);
        params.gravity = Gravity.CENTER;
        cropImageView.setLayoutParams(params);
        cropImageView.setScaleType(CropImageView.ScaleType.FIT_CENTER);
        cropImageView.setMultiTouchEnabled(false);
        cropImageView.setAutoZoomEnabled(true);
        cropImageView.setOnCropWindowChangedListener(this::onCropWindowChange);
        final Rect rect = getIntentCropRect();
        if (isIntentMinSizeWithCropRectLimit() && rect != null && !rect.isEmpty()) {
            cropImageView.setMinCropResultSize(rect.width(), rect.height());
        } else cropImageView.setMinCropResultSize(15, 15);
        cropImageView.setMaxZoom(2);
        cropImageView.setOnSetCropOverlayReleasedListener(r1 -> {
            if (!isZooming()) {
                showCropBubbleOptions();
            }
        });
        cropImageView.setOnSetCropOverlayMovedListener(r1 -> {
            if (!isZooming()) {
                dismissCropBubbleOptions();
            }
        });
        mCropImageView = cropImageView;


        FrameLayout screenLoadAnimationLayout = new FrameLayout(this);
        final FrameLayout.LayoutParams screenLoadAnimationParams = new FrameLayout.LayoutParams(match_parent, match_parent);
        screenLoadAnimationLayout.setLayoutParams(screenLoadAnimationParams);
        screenLoadAnimationLayout.setClickable(true);
        screenLoadAnimationLayout.setBackgroundColor(Color.parseColor("#ccffffff"));
        ImageView imageView = new ImageView(this);
        final FrameLayout.LayoutParams imageViewParams = new FrameLayout.LayoutParams(match_parent, match_parent);
        final int margin = DensityUtils.dip2px(this, 20);
        imageViewParams.topMargin = margin;
        imageViewParams.leftMargin = margin;
        imageViewParams.rightMargin = margin;
        imageViewParams.bottomMargin = margin;
        imageView.setLayoutParams(imageViewParams);

        screenLoadAnimationLayout.addView(imageView);
        screenLoadAnimationLayout.setVisibility(View.INVISIBLE);


        // color
        CvColorDropperDragLayout pickerLayout = new CvColorDropperDragLayout(this);
        mColorDropperDragLayout = pickerLayout;
        mColorDropperDragLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mColorDropperDragLayout.setEnableDrag(false);

//        pickerLayout.setBackgroundColor(Color.parseColor("#7f7a8b9c"));
        mColorDropperDragLayout.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                pickerLayout.setCenterOffset((int) event.getX(), (int) event.getY());
                mCvPixelView.setPivot((int) event.getX(), (int) event.getY());
            }
            return false;
        });
        mColorDropperDragLayout.setOnPositionChanged((x, y, w, h) -> {
            // mOnReleaseRunnable.run();
            // pickerLayout.setCenterOffset((int) x, (int) y, false);
            // mCvPixelView.setPivot((int) x, (int) y);
        });
        mColorDropperDragLayout.setOutMargin(0, 0);
        mEyeDropper = pickerLayout.findViewById(R.id.cnm_picker);
        // color dropper
        View colorPanel = LayoutInflater.from(this).inflate(R.layout.overlays_screen_color_picker_dropper, container, false);
        ConstraintLayout.LayoutParams colorParams = (ConstraintLayout.LayoutParams) container.getLayoutParams();
        colorParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        colorParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        mPixcelPanelLayout = colorPanel;
        mPixcelColorPanelContent = colorPanel.findViewById(R.id.dropper_panel);
        mCvPixelView = colorPanel.findViewById(R.id.pixel_view);
        mCvPixelView.handleMoving(colorPanel.findViewById(R.id.button_dropper_up), CvPixelView.Direction.UP, 10, mOnReleaseRunnable, pickerLayout::setCenterOffset);
        mCvPixelView.handleMoving(colorPanel.findViewById(R.id.button_dropper_down), CvPixelView.Direction.DOWN, 10, mOnReleaseRunnable, pickerLayout::setCenterOffset);
        mCvPixelView.handleMoving(colorPanel.findViewById(R.id.button_dropper_left), CvPixelView.Direction.LEFT, 10, mOnReleaseRunnable, pickerLayout::setCenterOffset);
        mCvPixelView.handleMoving(colorPanel.findViewById(R.id.button_dropper_right), CvPixelView.Direction.RIGHT, 10, mOnReleaseRunnable, pickerLayout::setCenterOffset);
        // touch view
        genTouchView();

        //
        CvCropOffsetAndSlideLayout regionTouchInterceptLayout = new CvCropOffsetAndSlideLayout(this);
        mCropOprateView = regionTouchInterceptLayout;
        mCropOprateView.setVisibility(View.GONE);

        return new View[]{cropImageView, pickerLayout, colorPanel, mCropTapOverlay, regionTouchInterceptLayout, screenLoadAnimationLayout};
    }

    //private boolean touchViewEnable;
    private RectF mCurTouchedRect;
    private SmpClickerDetector mCropTapOverlayClickTouchDetector;

    @SuppressLint("ClickableViewAccessibility")
    private void genTouchView() {
        final int match_parent = FrameLayout.LayoutParams.MATCH_PARENT;
        mCropTapOverlay = new StrokeDrawerView(this);
        mCropTapOverlay.setShowFrames(false);
        // mCropTapOverlay.setBackgroundColor(Color.parseColor("#7fab7c53"));
        mCropTapOverlayClickTouchDetector = new SmpClickerDetector(mCropTapOverlay);
        mCropTapOverlayClickTouchDetector.setOnClickListener(v -> {
            mCropTapOverlayClickTouchDetector.setEnable(false);
            RectF f = null;
            double a = 0;
            final float downX = mCropTapOverlayClickTouchDetector.getDownX();
            final float downY = mCropTapOverlayClickTouchDetector.getDownY();
            for (RectF rectF : mContourRects) {
                if (!rectF.contains(downX, downY)) continue;
                final int area = (int) (rectF.width() * rectF.height());
                // min limit area
                if (area < DensityUtils.dip2px(OverlayCropActivity.this, 1600) || area > 0.9f * mCropTapOverlay.getMeasuredWidth() * mCropTapOverlay.getMeasuredHeight()) {
                    continue;
                }
                if (a == 0 || a > area) {
                    a = area;
                    f = rectF;
                }
            }

            if (f == null) {
                final int ra = DensityUtils.dip2px(OverlayCropActivity.this, 25);
                final int dr = ra * 2;
                final float l = Math.min(mCropTapOverlay.getMeasuredWidth() - dr, Math.max(0, downX - ra));
                final float t = Math.min(mCropTapOverlay.getMeasuredHeight() - dr, Math.max(0, downY - ra));
                f = new RectF(l, t, l + dr, t + dr);
            }

            resetTouchViewAccessability(f);
            setCroptRect(mCropTapOverlay.getRecoveryARect(f), "onTouch::584");
            onDisposedTouchCropped();
            switchCropState(CropAction.CLICK);
        });
        mCropTapOverlay.setLayoutParams(new FrameLayout.LayoutParams(match_parent, match_parent));
    }

    private void onDisposedTouchCropped() {
        if (mDisposedOnTouchCroppedRunnable != null) {
            mDisposedOnTouchCroppedRunnable.run();
            mDisposedOnTouchCroppedRunnable = null;
        }
    }

    private void onCropWindowChange() {
//        int left = (int) mCropImageView.getLeftPoint();
//        int top = (int) mCropImageView.getTopPoint();
//        int width = (int) mCropImageView.getRightPoint() - left;
//        int height = (int) mCropImageView.getBottomPoint() - top;
//        String info = String.format(Locale.CHINA, "已选择 x:%d,y:%d,w:%d,h:%d", left, top, width, height);
//        mCropInfo.setText(info);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentMode", mCurMode.ordinal());
        outState.putInt("currentCropState", getCurrentCropState().ordinal());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            final int curModeOrdinal = savedInstanceState.getInt("currentMode", mCurMode.ordinal());
            if (curModeOrdinal != mCurMode.ordinal()) {
                for (Mode m : Mode.values()) {
                    if (m.ordinal() == curModeOrdinal) {
                        mCurMode = m;
                        break;
                    }
                }
            }
            final int curCropStateOrdinal = savedInstanceState.getInt("currentCropState", getCurrentCropState().ordinal());
            if (curCropStateOrdinal != getCurrentCropState().ordinal()) {
                for (CropAction c : CropAction.values()) {
                    if (c.ordinal() == curCropStateOrdinal) {
                        setCurrentCropAction(c);
                        break;
                    }
                }
            }
        }
        switchEditMode(mCurMode);
        switchCropState(getCurrentCropState());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onScreenLoad(Bitmap bm, EInitFrom remote) {
        if (remote == EInitFrom.DEFAULT) {
            Rect rect = getIntentCropRect();
            if (rect != null) {
                setCroptRect(rect, "onScreenLoad:647");
            }
        } else {
            setCroptRect(new Rect(), "onScreenLoad:650");
        }
        onRemoteScreenBitmapLoaded(bm);
    }

    private void setCroptRect(Rect rect, String Tag) {
        LogUtil.d("nimada#setCropRectTag:", "Tag: " + Useless.nonNullStr(Tag) + ", rect:: " + (rect == null ? "null" : rect.toShortString()));
        mCropImageView.setCropRect(rect);
    }

    private void onRemoteScreenBitmapLoaded(Bitmap bm) {
        if (bm != null) {
            setScreenBitmapSize(bm);
            findContours(mContourRects, bm, mCropTapOverlay.getMeasuredWidth(), mCropTapOverlay.getMeasuredHeight());
            setCroptRect(null, "onRemoteScreenBitmapLoaded:664");
            mCropImageView.setImageBitmap(bm);
            initPixelViewWhenBitmapLoaded(bm);
            ToastUtils.show(this, R.string.string_toast_capture_success);
        }
    }

    private void setScreenBitmapSize(Bitmap bm) {
        mBitmapWidth = bm.getWidth();
        mBitmapHeight = bm.getHeight();
    }

    @SuppressLint("SetTextI18n")
    private void initPixelViewWhenBitmapLoaded(Bitmap bm) {
        if (getImageView().getMeasuredWidth() == 0 || getImageView().getMeasuredHeight() == 0) {
            return;
        }
        Bitmap bm2 = Bitmap.createScaledBitmap(bm, getImageView().getMeasuredWidth(), getImageView().getMeasuredHeight(), false);
        mCvPixelView.setBitmap(bm2);
        mCvPixelView.setOnColorPickedListener(color -> {
            ((TextView) findViewById(R.id.txt_color)).setText("R:" + Color.green(color) + "  G:" + Color.green(color) + "  B:" + Color.blue(color));
            findViewById(R.id.view_color_show).setBackgroundColor(color);
            Point pixel = mCvPixelView.getPixel();
            final ImageView bgImg = findViewById(R.id.pixel_view_bgimg);
            final int w = bgImg.getMeasuredWidth();
            final int h = bgImg.getMeasuredHeight();
            final int startX = Math.min(Math.max(0, pixel.x - w / 2), bm2.getWidth() - w);
            final int startY = Math.min(Math.max(0, pixel.y - h / 2), bm2.getHeight() - h);
            bgImg.setImageBitmap(Bitmap.createBitmap(bm2, startX, startY, w, h));
        });
    }

    private Rect getIntentCropRect() {
        return getIntent().getParcelableExtra("crop_rect");
    }

    private boolean isIntentMinSizeWithCropRectLimit() {
        return getIntent().getBooleanExtra("minCropSizeLimitToRect", false);
    }

    @Override
    protected void onScreenRotate(Bitmap bm) {
        mCropImageView.setImageBitmap(bm);
    }

    @Override
    public void finish() {
        super.finish();
        ScriptOverlays.Agent.onEditorList(getApplicationContext());
    }

    public void setFinishWithResult() {
        Intent intent = getResultIntent();
        setResult(RESULT_CANCELED, intent);
        if (!Useless.isEmpty(mEntities)) {
            intent.putExtra("items", mEntities);
            setResult(RESULT_OK, intent);
            StContext.postOffice().post(AppAddressBook.EDITOR_LISTS, getResultIntent().getExtras(), true);
            mEntities.clear();
        }
    }

    private Intent mResultIntent = new Intent();

    public Intent getResultIntent() {
        return mResultIntent;
    }

    /**
     * @param rects
     * @param bitmap
     * @param tarWidth
     * @param tarHeight
     */
    public void findContours(List<RectF> rects, Bitmap bitmap, int tarWidth, int tarHeight) {
        // load
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);
        // grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        // threshold
        Mat thresholdMat = new Mat();
        Core.MinMaxLocResult result = Core.minMaxLoc(gray);
        final int threshValInt = (int) ((result.maxVal - result.minVal) * 0.64 + result.minVal);
        Imgproc.adaptiveThreshold(gray, thresholdMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 51, 5);
        //Imgproc.threshold(gray, thresholdMat, threshValInt, result.maxVal, Imgproc.THRESH_BINARY_INV);
        // find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresholdMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        mCropTapOverlay.setApstioWH(image.width(), image.height());
        for (int i = 0; i < contours.size(); i++) {
            org.opencv.core.Rect rect = Imgproc.boundingRect(contours.get(i));
            runOnUiThread(() -> mCropTapOverlay.addRect(rect));
            if (rect != null) {
                final int width = image.width();
                final int height = image.height();
                final float x = rect.x * 1.0f / width * tarWidth;
                final float y = rect.y * 1.0f / height * tarHeight;
                final float r = (rect.x + rect.width) * 1.0f / width * tarWidth;
                final float b = (rect.y + rect.height) * 1.0f / height * tarHeight;
                rects.add(new RectF(x, y, r, b));
            }
        }
        runOnUiThread(() -> mCropTapOverlay.invalidate());
    }

    private Mode mCurMode = Mode.GAME;

    private void switchCropState(@NonNull CropAction state) {
        if (/*mCurMode != Mode.CROP || mCurMode != Mode.JUST_CROP_OFFSET ||*/ getCurrentCropState() == state) {
            return;
        }
        setCurrentCropAction(state);
        updateOptionsBubblesDrawable();
        switch (state) {
            case CLICK:
                mCropOprateView.setVisibility(View.GONE);
                setZooming(true);
                mCropImageView.setAutoZoomEnabled(true, () -> {
                    mCropImageView.setShowCropOverlay(true);
                    showCropBubbleOptions();
                    setZooming(false);
                });
                break;
            case OFFSET:
                showCropOprateView(() -> {
                    mCropOprateView.switchMode(CvCropOffsetAndSlideLayout.MODE_OFFSET);
                    RectF rectF = mCropImageView.getCropWindowRect();
                    Rect r = new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                    if (!r.isEmpty()) {
                        mCropOprateView.setCropRect(r.left, r.top, r.right, r.bottom);
                        offsetRectLimitedInCropImage(r);
                        mCropOprateView.setOffsetRect(r.left, r.top, r.right, r.bottom);
                    }
                });
                break;
            case OFFSET_ONLY:
                /*mCropImageView.setShowCropOverlay(false);
                Intent in = getIntent();
                Rect offsetRect = in.getParcelableExtra(SECons.Ints.KEY_CROP_REGION_RECT);
                Rect cropRect = in.getParcelableExtra(SECons.Ints.KEY_CROP_CROP_RECT);
                mCropOprateView.switchMode(CvCropOffsetAndSlideLayout.MODE_OFFSET);
                unboxSpcifiedImageRect(offsetRect);
                unboxSpcifiedImageRect(cropRect);
                mCropOprateView.setVisibility(View.VISIBLE);
                mCropOprateView.setImageBounds(mCropImageView.getImageBitmapDrawingRect());
                mCropOprateView.setCropRect(cropRect.left, cropRect.top, cropRect.right, cropRect.bottom);
                mCropOprateView.setOffsetRect(offsetRect.left, offsetRect.top, offsetRect.right, offsetRect.bottom);
                mCropOprateView.setButtonsClickListeners(v -> saveScreenAndCropWithClickView(v, image -> dataWithCropImageParceble(image, this::finish)), v -> finish());*/
                break;
            case SLIDE:
                showCropOprateView(() -> {
                    mCropOprateView.switchMode(CvCropOffsetAndSlideLayout.MODE_SLIDE);
                    initDefaultSlideRect();
                });
                break;
            case FLAG:
                mCropImageView.folkNewCropRect();
                mCropActions.add(CropAction.CLICK);
                updateOptionsBubblesDrawable();
                break;
            default:
        }
    }

    private void initDefaultSlideRect() {
        final RectF rectF = mCropImageView.getCropWindowRect();
        final Rect r = new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        if (!r.isEmpty()) {
            mCropOprateView.setCropRect(r.left, r.top, r.right, r.bottom);
            mCropOprateView.setSlideStartRect(r.left, r.top, r.right, r.bottom);
            offsetRectLimitedInCropImage(r);
            mCropOprateView.setSlideEndRect(r.left, r.top, r.right, r.bottom);
        }
    }

    /**
     * @param v
     * @param onSuccess
     */
    private void saveScreenAndCropWithClickView(View v, Consumer<SEImage> onSuccess) {
        // checkout mode
        if (mCurMode != Mode.CROP) {
            return;
        }
        // saveSync curren screen bitmap into file
        //final String path = StContext.assets().newRaw().getAbsolutePath();



        /*justSaveCurrentScreen(path, () -> {
            SEImage image = newEmptySEImage(path);
            if (image == null) {
                return;
            }
            // saveSync crop image bitmap into file
            showProgress();
            ImageCropUploader.saveAndUpload(ip(), getAsIp(), image, image.getBounds(), image.getImageCropPath(), r -> {
                if (!r) {
                    return;
                }
                if (onSuccess == null) {
                    return;
                }
                onSuccess.accept(image);
                dismissProgress();
                onDisposedCropSave();
                v.setEnabled(true);
            });
        }, () -> v.setEnabled(true));*/
    }

    private void showProgress() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.show();
    }

    /**
     * @param zoomEnd
     */
    private void showCropOprateView(Runnable zoomEnd) {
        dismissCropBubbleOptions();
        mCropImageView.setShowCropOverlay(false);
        mCropImageView.setAutoZoomEnabled(false, () -> {
            // set buttons lisetners
            // mCropOprateView.setButtonsClickListeners(v -> saveScreenAndCropWithClickView(v, this::dataWithCropImageParceble), v -> switchCropState(CropAction.CLICK));
            mCropOprateView.setButtonsClickListeners(
                    v -> {
                        if (mCurMode == Mode.DATA_NONEXIST) {
                            settleWithView(v, getIntent().getStringExtra(SECons.Ints.KEY_ORIGIN_PATH), this::finish);
                        } else {
                            StContext.assets().storeRawOnWorkers(getCurBitmap(), file -> {
                                if (file != null) {
                                    settleWithView(v, file.getAbsolutePath(), () -> v.setEnabled(true));
                                } else {
                                    dismissProgress();
                                    v.setEnabled(true);
                                    ToastUtils.show(OverlayCropActivity.this, "截图存储失败，请稍候重试！");
                                }
                            });
                            //final String path = StContext.assets().newRaw().getAbsolutePath();
                            //justSaveCurrentScreen(path, () -> settleWithView(v, path, null), () -> v.setEnabled(true));
                        }
                    },
                    v -> switchCropState(CropAction.CLICK)
            );
            mCropOprateView.setVisibility(View.VISIBLE);
            mCropOprateView.setImageBounds(mCropImageView.getImageBitmapDrawingRect());
            if (zoomEnd != null) {
                zoomEnd.run();
            }
            mCropOprateView.invalidate();
//            mVBlueHelpButton.setVisibility(View.VISIBLE);
        });
    }

    /**
     * @param image
     *//*
    private void dataWithCropImageParceble(SEImage image) {
        CropImgParceble cropImgParceble;
        switch (getCurrentCropState()) {
            case CLICK:
                cropImgParceble = new CropImgParceble(CropImgParceble.CLICK);
                break;
            case FINISH:
                cropImgParceble = new CropImgParceble(CropImgParceble.FINISH);
                break;
            case OFFSET:
                cropImgParceble = new CropImgParceble(CropImgParceble.OFFSET);
                Point p = mCropOprateView.getOffsets();
                cropImgParceble.setOffsetX(getResultWithWidthAspectRatio(p.x));
                cropImgParceble.setOffsetY(getResultWithHeightAspectRatio(p.y));
                break;
            case SLIDE:
                cropImgParceble = new CropImgParceble(CropImgParceble.SLIDE);
                Pair<Rect, Rect> pair = mCropOprateView.getImageSlideRects();
                if (pair != null) {
                    cropImgParceble.setSlideStart(getResultRectWithAspectRation(pair.first));
                    cropImgParceble.setSlideEnd(getResultRectWithAspectRation(pair.second));
                }
                break;
            case FLAG:
                cropImgParceble = new CropImgParceble(CropImgParceble.FLAG);
                cropImgParceble.setImage(image);
                break;
            default:
                cropImgParceble = null;
        }

        if (cropImgParceble != null) {
            cropImgParceble.setImage(image);
            mEntities.add(cropImgParceble);
            backToStreamFromCropMode();
            setFinishWithResult();
//            if (onSuccess != null) {
//                onSuccess.run();
//            }
        }
    }*/

    /**
     * @param onSuccess
     */
    private void dataWithCropImageParceble(Map<CropAction, SEImage> seImageActionMap, Runnable onSuccess) {
        // crop image
        CropImgGroupParceble cropImgGroupParceble = new CropImgGroupParceble();
        Set<CropAction> actions = seImageActionMap.keySet();
        for (CropAction a : actions) {
            SEImage image = seImageActionMap.get(a);
            CropImgParceble cropImgParceble;
            switch (a) {
                case CLICK:
                    cropImgParceble = new CropImgParceble(CropImgParceble.CLICK);
                    break;
                case FINISH:
                    cropImgParceble = new CropImgParceble(CropImgParceble.FINISH);
                    break;
                case OFFSET:
                    cropImgParceble = new CropImgParceble(CropImgParceble.OFFSET);
                    Point p = mCropOprateView.getOffsets();
                    cropImgParceble.setOffsetX(getResultWithWidthAspectRatio(p.x));
                    cropImgParceble.setOffsetY(getResultWithHeightAspectRatio(p.y));
                    break;
                case SLIDE:
                    cropImgParceble = new CropImgParceble(CropImgParceble.SLIDE);
                    Pair<Rect, Rect> pair = mCropOprateView.getImageSlideRects();
                    if (pair != null) {
                        cropImgParceble.setSlideStart(getResultRectWithAspectRation(pair.first));
                        cropImgParceble.setSlideEnd(getResultRectWithAspectRation(pair.second));
                    }
                    break;
                case FLAG:
                    cropImgParceble = new CropImgParceble(CropImgParceble.FLAG);
                    cropImgParceble.setImage(image);
                    break;
                default:
                    cropImgParceble = null;
            }
            if (cropImgParceble != null) {
                cropImgParceble.setImage(image);
                cropImgGroupParceble.add(cropImgParceble);
            }
        }

        if (!cropImgGroupParceble.isEmpty()) {
            mEntities.add(cropImgGroupParceble);
            backToStreamFromCropMode();
            setFinishWithResult();
            if (onSuccess != null) {
                onSuccess.run();
            }
        }


    }

    private Rect getResultRectWithAspectRation(Rect r) {
        if (r == null || r.isEmpty()) return r;
        Rect re = new Rect(getResultWithWidthAspectRatio(r.left), getResultWithHeightAspectRatio(r.top),
                getResultWithWidthAspectRatio(r.right), getResultWithHeightAspectRatio(r.bottom));
        return re;
    }

    private Rect mapResultRectWithAspectRation(Rect r) {
        if (r == null || r.isEmpty()) return r;
        Rect re = new Rect(mapResultWithWidthAspectRatio(r.left), mapResultWithHeightAspectRatio(r.top),
                mapResultWithWidthAspectRatio(r.right), mapResultWithHeightAspectRatio(r.bottom));
        return re;
    }

    private int getResultWithWidthAspectRatio(int src) {
        RectF rectF = mCropOprateView.getImageBounds();
        final int w = rectF == null || rectF.isEmpty() ? 1 : (int) rectF.width();
        return (int) (Math.max(1, mBitmapWidth) * 1.0F / w * src);
    }

    private int mapResultWithWidthAspectRatio(int src) {
        RectF rectF = mCropOprateView.getImageBounds();
        final int w = rectF == null || rectF.isEmpty() ? 1 : (int) rectF.width();
        final int result = (int) (w * 1.0F / Math.max(1, mBitmapWidth) * src);
        return result;
    }

    private int getResultWithHeightAspectRatio(int src) {
        RectF rectF = mCropOprateView.getImageBounds();
        final int w = rectF == null || rectF.isEmpty() ? 1 : (int) rectF.height();
        return (int) (Math.max(1, mBitmapHeight) * 1.0F / w * src);
    }

    private int mapResultWithHeightAspectRatio(int src) {
        RectF rectF = mCropOprateView.getImageBounds();
        final int h = rectF == null || rectF.isEmpty() ? 1 : (int) rectF.height();
        final int result = (int) (h * 1.0F / Math.max(1, mBitmapHeight) * src);
        return result;
    }

    private void backToStreamFromCropMode() {
        mCropOprateView.setVisibility(View.GONE);
        mCropImageView.resetCropRect();

        mPopupOptionNonExist.setVisibility(View.VISIBLE);
        mPopupOptionFinish.setVisibility(View.VISIBLE);
        mCropActions.clear();
        setCurrentCropAction(CropAction.IDEL);

        dismissCropBubbleOptions();
        switchEditMode(Mode.GAME);
    }

    private void offsetRectLimitedInCropImage(Rect r) {
        RectF f = mCropImageView.getImageBitmapDrawingRect();
        int offset = DensityUtils.dip2px(this, 10);
        final int offsetLimitX = Math.min(offset, Math.abs((int) ((f.width() - r.width()) / 2)));
        final int offsetLimitY = Math.min(offset, Math.abs((int) ((f.height() - r.height()) / 2)));
        offset = Math.min(offsetLimitX, offsetLimitY);
        final int offsetX = r.right <= f.right - offset ? offset : -offset;
        final int offsetY = r.bottom <= f.bottom - offset ? offset : -offset;
        r.offset(offsetX, offsetY);
    }


    protected void loadOriginFromFile(String filePath) {
        showLoading();
        /*final File tempFile = FPathScript.tmpFile("tmp.png");
        Files.delete(tempFile);*/
        final File tempFile = new File(filePath);
        CAsync.excute(
                () -> {
                    Bitmap map = CoBitmapWorker.load(filePath);
                    if (map != null) {
                        //mHandler.sendEmptyMessage(MSG_UPDATEIMG);
                        // LogUtil.d("Ac#Over#LayCaptureSteramPlay", "1");
                        onBackgroundScreenshotSuccess(map);
                        /*LogUtil.d("Ac#Over#LayCaptureSteramPlay", "2");
                        final File f = FPathScript.tmpFile("tmp.png.downloading");
                        LogUtil.d("Ac#Over#LayCaptureSteramPlay", "3");
                        if (f.exists()) {
                            f.delete();
                        }
                        LogUtil.d("Ac#Over#LayCaptureSteramPlay", "4");
                        CoBitmapWorker.saveSync(map, f.getAbsolutePath());
                        LogUtil.d("Ac#Over#LayCaptureSteramPlay", "5");
                        f.renameTo(tempFile);
                        LogUtil.d("Ac#Over#LayCaptureSteramPlay", "6");*/
                    }
                    return map;
                },
                new IAccept<Bitmap>() {
                    @Override
                    public void accept(Bitmap t) {
                        if (t == null) {
                            ToastUtils.show(OverlayCropActivity.this, R.string.string_toast_capture_failed);
                            dismissLoading();
                        } else {
                            final int ori = t.getWidth() > t.getHeight() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                            if (getSpecifiedScreenOrientation() != ori) {
                                setRequestedOrientation(ori);
                            }
                            new Handler().postDelayed(() -> {
                                setLoadedBitmap(tempFile, t);
                                switchEditMode(Mode.CROP);
                                dismissLoading();
                            }, 1000);

                        }
                    }
                }
        );

    }

    /**
     * @param mode
     */
    private void switchEditMode(@NonNull Mode mode) {
        mCurMode = mode;
        resetTouchViewAccessability(null);
        dismissCropBubbleOptions();

        if (mCropTapOverlay != null) Viewor.invisible(mCropTapOverlay);

        switch (mode) {
            case CROP:
                if (mCropTapOverlay != null) Viewor.visible(mCropTapOverlay);
                Viewor.visible(mOverlayContainer, mCropImageView);
                Viewor.invisible(mColorDropperDragLayout, mVBluePointerLayout, mVFunc, mPixcelPanelLayout, getImageView());
                /*touchViewEnable = true;*/
                mCropTapOverlayClickTouchDetector.setEnable(true);
                break;
            case CROP_FROM_FILE:
                Viewor.invisible(mColorDropperDragLayout, mVFunc, mOverlayContainer, mPixcelPanelLayout, mCropImageView, getImageView(), mCropOprateView);
                loadOriginFromFile(getIntent().getStringExtra(SECons.Ints.KEY_ORIGIN_PATH));
                break;
            case COLOR:
                Viewor.visible(mColorDropperDragLayout, mOverlayContainer, mPixcelPanelLayout, getImageView());
                Viewor.invisible(mVBluePointerLayout, mVFunc, mCropImageView, mCropOprateView);
                break;
            case GAME:
//                Viewor.visible(mVBluePointerLayout);
//                Viewor.invisible(mColorDropperDragLayout, mVFunc, mOverlayContainer, mPixcelPanelLayout, mCropImageView, getImageView(), mCropOprateView);
                startActivity(getPackageManager().getLaunchIntentForPackage(StContext.manifest().getGameId()));
                ScriptOverlays.Agent.onGameCapture(getApplicationContext());
                break;
            case JUST_CROP_CLICK:
                if (mCropTapOverlay != null) Viewor.visible(mCropTapOverlay);
                Viewor.visible(mOverlayContainer, mCropImageView);
                Viewor.invisible(mColorDropperDragLayout, mVBluePointerLayout, mVFunc, mPixcelPanelLayout, getImageView(), mCropOprateView);
                final String originRegionPath3 = getIntent().getStringExtra(SECons.Ints.KEY_ORIGIN_PATH);
                CoBitmapWorker.consume64(originRegionPath3, t -> {
                    final int ori = t.getWidth() > t.getHeight() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    if (getSpecifiedScreenOrientation() != ori) {
                        setRequestedOrientation(ori);
                    }
                    onSetCropBitmap(t);
                    /*if (getSpecifiedScreenOrientation() == ori) {
                        onSetCropBitmap(t);
                    } else {
                        setRequestedOrientation(ori);
                    }*/
                });
                break;
            case JUST_CROP_OFFSET:
            case JUST_CROP_SLIDE:
                Viewor.visible(mOverlayContainer, mCropImageView);
                Viewor.invisible(mColorDropperDragLayout, mVBluePointerLayout, mVFunc, mPixcelPanelLayout, getImageView(), mCropOprateView);
                final String originRegionPath2 = getIntent().getStringExtra(SECons.Ints.KEY_ORIGIN_PATH);
                CoBitmapWorker.consume64(originRegionPath2, t -> {
                    final int ori = t.getWidth() > t.getHeight() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    if (getSpecifiedScreenOrientation() != ori) {
                        setRequestedOrientation(ori);
                    }
                    onSetOffsetOrSlideBitmap(mode, t);
                });
                break;
            case DATA_NONEXIST:
                if (mCropTapOverlay != null) Viewor.visible(mCropTapOverlay);
                Viewor.visible(mOverlayContainer, mCropImageView);
                Viewor.invisible(mColorDropperDragLayout, mVBluePointerLayout, mVFunc, mPixcelPanelLayout, getImageView(), mCropOprateView);
                final String originRegionPath4 = getIntent().getStringExtra(SECons.Ints.KEY_ORIGIN_PATH);
                CoBitmapWorker.consume64(originRegionPath4, t -> {
                    final int ori = t.getWidth() > t.getHeight() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    if (getSpecifiedScreenOrientation() != ori) {
                        setRequestedOrientation(ori);
                    }
                    onSetNonexistBitmap(originRegionPath4, t);
                });
                break;
            case DATA_CROP:
                justSimpleCropLoad(img -> {
                    setCroptRect(img.getBounds(), "switchEditMode::1017");
                    mCropImageView.setShowCropOverlay(true);
                    mCropImageView.setAutoZoomEnabled(true);
                });
                break;
            case DATA_REGION:
                Viewor.visible(mOverlayContainer, mCropImageView);
                Viewor.invisible(mColorDropperDragLayout, mVBluePointerLayout, mVFunc, mPixcelPanelLayout, getImageView(), mCropOprateView);
                final String originRegionPath = getIntent().getStringExtra(SECons.Ints.KEY_ORIGIN_PATH);
                CoBitmapWorker.consume64(originRegionPath, t -> {
                    final int ori = t.getWidth() > t.getHeight() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    /*if (getSpecifiedScreenOrientation() == ori) {
                        mCropImageView.setImageBitmap(t);
                        Intent in = getIntent();
                        Rect rect = in.getParcelableExtra(SECons.Ints.KEY_CROP_REGION_RECT);
                        Rect min = in.getParcelableExtra(SECons.Ints.KEY_CROP_REGION_MIN_RECT);
                        if (min != null)
                            mCropImageView.setMinCropResultSize(min.width(), min.height());
                        if (rect != null) {
                            mCropImageView.setCropRect(rect);
                        }
                        mCropImageView.setShowCropOverlay(true);
                        mCropImageView.setAutoZoomEnabled(true);
                    } else {
                        setRequestedOrientation(ori);
                    }*/

                    if (getSpecifiedScreenOrientation() != ori) {
                        setRequestedOrientation(ori);
                    }

                    mCropImageView.setImageBitmap(t);
                    Intent in = getIntent();
                    Rect rect = in.getParcelableExtra(SECons.Ints.KEY_CROP_REGION_RECT);
                    Rect min = in.getParcelableExtra(SECons.Ints.KEY_CROP_REGION_MIN_RECT);
                    if (min != null)
                        mCropImageView.setMinCropResultSize(min.width(), min.height());
                    if (rect != null) {
                        setCroptRect(rect, "switchEditMode::1055");
                    }
                    mCropImageView.setShowCropOverlay(true);
                    mCropImageView.setAutoZoomEnabled(true);
                });
                break;
            case DATA_COLOR:
                Viewor.visible(mColorDropperDragLayout, mOverlayContainer, mPixcelPanelLayout, getImageView());
                Viewor.invisible(mVBluePointerLayout, mVFunc, mCropImageView, mCropOprateView);
                final String originPath = getIntent().getStringExtra(SECons.Ints.KEY_ORIGIN_PATH);
                CoBitmapWorker.consume64(originPath, t -> {
                    final int ori = t.getWidth() > t.getHeight() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    if (getSpecifiedScreenOrientation() == ori) {
                        getImageView().setImageBitmap(t);
                        new Handler().postDelayed(() -> {
                            initPixelViewWhenBitmapLoaded(t);
                            Point p = getIntent().getParcelableExtra(SECons.Ints.KEY_COLOR_PIXCEL);
                            if (p != null) {
                                p.x = unboxSpecifiedImageWithHorizontal(p.x);
                                p.y = unboxSpecifiedImageWithVertical(p.y);
                                mCvPixelView.setPivot(p.x, p.y);
                                mColorDropperDragLayout.setCenterOffset(p.x, p.y);
                            }
                        }, 1000);
                    } else {
                        setRequestedOrientation(ori);
                    }
                });
                break;
            default:
        }

        if (mode == Mode.GAME) {
            doSwitchFragmentWithTag(FmScriptTestingLogHorizontal.TAG);
        } else {
            hideFragment(FmScriptTestingLogHorizontal.TAG);
            if (logHorizontal != null) {
                logHorizontal.stop();
            }
        }
    }

    private void showGuide() {
        /*GuideManager.simplelyShowImageGuides(this, mGuideImage, mGuideActionImage,
                GuideManager.ImageGuideType.CROP_OPTION_V2_CLICK,
        );*/
    }

    private boolean isOrientationPortrait() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    private void onSetOffsetOrSlideBitmap(@NonNull Mode mode, Bitmap t) {
        mCropImageView.setImageBitmap(t);
        mCropImageView.setShowCropOverlay(false);
        mCropImageView.setAutoZoomEnabled(false);
        setScreenBitmapSize(t);
        //mCropOprateView.setBackgroundColor(Color.parseColor("#7fabcd12"));
        Viewor.invisible(mCropOprateView);
        ObserverRunnable.run(100, 1500, new ObserverRunnable.IObserverRunnable() {

            @Override
            public boolean when() {
                final int width = mCropImageView.getMeasuredWidth();
                final int height = mCropImageView.getMeasuredHeight();
                final int scrWidth = DensityUtils.getScreenW(OverlayCropActivity.this);
                final int scrHeight = DensityUtils.getScreenH(OverlayCropActivity.this);
                return width > 0 && height > 0 && (width == scrWidth || scrHeight == height);
            }

            @Override
            public void then() {
                Intent in = getIntent();
                Rect cropRect = in.getParcelableExtra(SECons.Ints.KEY_CROP_CROP_RECT);
                setCroptRect(cropRect, "switchEditMode::1141");
                mCropOprateView.setImageBounds(mCropImageView.getImageBitmapDrawingRect());
                mCropOprateView.setVisibility(View.VISIBLE);
                final int offsetLeft = (int) mCropImageView.getImageBitmapDrawingRect().left;
                final int offsetTop = (int) mCropImageView.getImageBitmapDrawingRect().top;
                Rect cropRectMapped = mapResultRectWithAspectRation(cropRect);
                cropRectMapped.offset(offsetLeft, offsetTop);
                mCropOprateView.setCropRect(cropRectMapped.left, cropRectMapped.top, cropRectMapped.right, cropRectMapped.bottom);
                if (mode == Mode.JUST_CROP_OFFSET) {
                    handleOffset(cropRect, offsetLeft, offsetTop);
                } else {
                    mCropOprateView.switchMode(CvCropOffsetAndSlideLayout.MODE_OFFSET);
                    handleSlide(cropRect, offsetLeft, offsetTop);
                }
            }

            private void handleSlide(Rect cropRect, int offsetLeft, int offsetTop) {
                if (cropRect != null) {
                    // slide mode
                    mCropOprateView.switchMode(CvCropOffsetAndSlideLayout.MODE_SLIDE);

                    // connect rect
                    Rect startRect = getIntent().getParcelableExtra(SECons.Ints.KEY_CROP_SLIDE_START_RECT);
                    Rect endRect = getIntent().getParcelableExtra(SECons.Ints.KEY_CROP_SLIDE_END_RECT);
                    if (startRect == null || startRect.isEmpty() || endRect == null || endRect.isEmpty()) {
                        initDefaultSlideRect();
                    } else {
                        Rect startRectMapped = mapResultRectWithAspectRation(startRect);
                        startRectMapped.offset(offsetLeft, offsetTop);
                        mCropOprateView.setSlideStartRect(startRectMapped.left, startRectMapped.top, startRectMapped.right, startRectMapped.bottom);

                        // end rect
                        Rect endRectMapped = mapResultRectWithAspectRation(endRect);
                        endRectMapped.offset(offsetLeft, offsetTop);
                        mCropOprateView.setSlideEndRect(endRectMapped.left, endRectMapped.top, endRectMapped.right, endRectMapped.bottom);
                    }
                    // invalide view
                    mCropOprateView.invalidate();

                    // set buttons listeners
                    mCropOprateView.setButtonsClickListeners(v -> {
                        Pair<Rect, Rect> p = mCropOprateView.getImageSlideRects();
                        Rect first = getResultRectWithAspectRation(p.first);
                        Rect second = getResultRectWithAspectRation(p.second);
                        Intent intent = new Intent();
                        intent.putExtra(SECons.Ints.KEY_CROP_SLIDE_START_RECT, first);
                        intent.putExtra(SECons.Ints.KEY_CROP_SLIDE_END_RECT, second);
                        setResult(RESULT_OK, intent);
                        doFinish();
                    }, v -> finish());
                }
            }

            private void handleOffset(Rect cropRect, int offsetLeft, int offsetTop) {
                Rect offsetRect = getIntent().getParcelableExtra(SECons.Ints.KEY_CROP_REGION_RECT);
                if (cropRect != null) {
                    mCropOprateView.switchMode(CvCropOffsetAndSlideLayout.MODE_OFFSET);
                    // offset rect
                    Rect offsetRectMapped = mapResultRectWithAspectRation(offsetRect);
                    offsetRectMapped.offset(offsetLeft, offsetTop);
                    mCropOprateView.setOffsetRect(offsetRectMapped.left, offsetRectMapped.top, offsetRectMapped.right, offsetRectMapped.bottom);

                    // invalide view
                    mCropOprateView.invalidate();

                    // buttons listners
                    mCropOprateView.setButtonsClickListeners(v -> {
                        Point p = mCropOprateView.getOffsets();
                        p.x = getResultWithWidthAspectRatio(p.x);
                        p.y = getResultWithHeightAspectRatio(p.y);
                        Intent intent = new Intent();
                        intent.putExtra(SECons.Ints.KEY_EDIT_NEW_OFFSET, p);
                        setResult(RESULT_OK, intent);
                        doFinish();
                    }, v -> finish());
                }
            }
        });
    }

    private void onSetNonexistBitmap(String path, Bitmap t) {
        mCropImageView.setAutoZoomEnabled(true);
        mCropImageView.setImageBitmap(t);
        mCropImageView.setShowCropOverlay(true);
        mCropOprateView.setVisibility(View.GONE);
        mCropImageView.setOnSetCropOverlayMovedListener(rect -> dismissCropBubbleOptions());
        mCropImageView.setOnSetCropOverlayReleasedListener(rect -> showCropBubbleOptions());
        setScreenBitmapSize(t);
        ObserverRunnable.run(100, 1500, new ObserverRunnable.IObserverRunnable() {

                    @Override
                    public boolean when() {
                        final int width = mCropImageView.getMeasuredWidth();
                        final int height = mCropImageView.getMeasuredHeight();
                        final int scrWidth = DensityUtils.getScreenW(OverlayCropActivity.this);
                        final int scrHeight = DensityUtils.getScreenH(OverlayCropActivity.this);
                        return width > 0 && height > 0 && (width == scrWidth || scrHeight == height);
                    }

                    @Override
                    public void then() {
                        if (isFinishing()) {
                            return;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed()) {
                            return;
                        }
                        Intent in = getIntent();
                        Rect cropRect = in.getParcelableExtra(SECons.Ints.KEY_CROP_CROP_RECT);
                        /*if (cropRect != null) {
                            LogUtil.d("nimada", "START = " + cropRect.top + " | " + cropRect.left);
                        }*/
                        setCroptRect(cropRect, "switchEditMode::1257");
                        /*final Rect settleRect = mCropImageView.getCropRect();
                        if (cropRect != null) {
                            LogUtil.d("nimada2", "START = " + settleRect.top + " | " + settleRect.left);
                        }*/
                        switchCropState(CropAction.FLAG);
                        showCropBubbleOptions();
                        mPopupOptionNonExist.setVisibility(View.GONE);
                        mPopupOptionFinish.setVisibility(View.GONE);
                        mPopupOptionCancel.setOnClickListener(v -> finish());
                        mPopupOptionConfirm.setOnClickListener(v -> settleWithView(v, path, () -> finish()));
                    }
                }
        );
    }

    private void onSetCropBitmap(Bitmap t) {
        mCropImageView.setAutoZoomEnabled(true);
        mCropImageView.setImageBitmap(t);
        mCropImageView.setShowCropOverlay(true);
        mCropOprateView.setVisibility(View.GONE);
        final View.OnClickListener l = v -> {
            Intent resultIntent = getResultIntent();
            Rect r = mCropImageView.getCropRect();
            r.set((int) mCropImageView.getLeftPoint(), (int) mCropImageView.getTopPoint(),
                    (int) mCropImageView.getRightPoint(), (int) mCropImageView.getBottomPoint());
            LogUtil.d("nimada3", "END = " + r.top + " | " + r.left);
            resultIntent.putExtra(SECons.Ints.KEY_CROP_CROP_RECT, r);
            setResult(RESULT_OK, resultIntent);
            doFinish();
        };
        mCropImageView.setOnSetCropOverlayMovedListener(rect -> dismissCropBubbleOptions());
        mCropImageView.setOnSetCropOverlayReleasedListener(rect -> showBubbleOptionsConfirmation(l, v -> finish()));
        setScreenBitmapSize(t);
        ObserverRunnable.run(100, 1500, new ObserverRunnable.IObserverRunnable() {

                    @Override
                    public boolean when() {
                        final int width = mCropImageView.getMeasuredWidth();
                        final int height = mCropImageView.getMeasuredHeight();
                        final int scrWidth = DensityUtils.getScreenW(OverlayCropActivity.this);
                        final int scrHeight = DensityUtils.getScreenH(OverlayCropActivity.this);
                        return width > 0 && height > 0 && (width == scrWidth || scrHeight == height);
                    }

                    @Override
                    public void then() {
                        if (isFinishing()) {
                            return;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed()) {
                            return;
                        }
                        Intent in = getIntent();
                        Rect cropRect = in.getParcelableExtra(SECons.Ints.KEY_CROP_CROP_RECT);
                        /*if (cropRect != null) {
                            LogUtil.d("nimada", "START = " + cropRect.top + " | " + cropRect.left);
                        }*/
                        setCroptRect(cropRect, "switchEditMode::1257");
                        /*final Rect settleRect = mCropImageView.getCropRect();
                        if (cropRect != null) {
                            LogUtil.d("nimada2", "START = " + settleRect.top + " | " + settleRect.left);
                        }*/
                        showBubbleOptionsConfirmation(l, v -> finish());
                    }
                }
        );
    }

    private void doFinish() {
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        showGuide();
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
    }

    /**
     * @param rect
     */
    private void resetTouchViewAccessability(RectF rect) {
        /*mCropTapOverlayClickTouchDetector.setEnable(false);*/
        mCropImageView.setShowCropOverlay(rect != null);
        mCurTouchedRect = rect;
        mCropImageView.setAutoZoomEnabled(!mCropTapOverlayClickTouchDetector.isEnable());
//        mCropImageView.setAutoZoomEnabled(false);
    }

    /**
     * @param load
     */
    private void justSimpleCropLoad(OnSimpleCropLoad load) {
        Viewor.visible(mOverlayContainer, mCropImageView);
        Viewor.invisible(mColorDropperDragLayout, mVBluePointerLayout, mVFunc, mPixcelPanelLayout, getImageView());
        Intent imgIntent = getIntent();
        SEImage img = imgIntent.getParcelableExtra(SECons.Ints.KEY_CROP_IMG);
        CoBitmapWorker.consume64(img.getImageOriginal(), t -> {
            final int ori = t.getWidth() > t.getHeight() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            if (getSpecifiedScreenOrientation() == ori) {
                mCropImageView.setImageBitmap(t);
                if (load != null) load.onLoaded(img);
            } else {
                setRequestedOrientation(ori);
            }
        });
    }

    private interface OnSimpleCropLoad {
        void onLoaded(SEImage image);

    }

    /**
     *
     */
    private PopupWindow mBubbleOptionsLeft, mBubbleOptionsConfirmation;

    private void showBubbleOptionsConfirmation(View.OnClickListener confirm, View.OnClickListener cancel) {

        if (mBubbleOptionsConfirmation == null) {
            final View root = LayoutInflater.from(this).inflate(R.layout.popup_bubble_options_confirmation, null);
            mBubbleOptionsConfirmation = new PopupWindow(root, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        final View buttonConfirmation = mBubbleOptionsConfirmation.getContentView().findViewById(R.id.confirm);
        final View buttonCancel = mBubbleOptionsConfirmation.getContentView().findViewById(R.id.cancel);
        buttonConfirmation.setOnClickListener(confirm);
        buttonCancel.setOnClickListener(cancel);
        showCropPopWinAtLocation(mBubbleOptionsConfirmation, 35, 80, 5);
    }

    /**
     *
     */
    private void showCropBubbleOptions() {
        if (mBubbleOptionsLeft == null) {
            mBubbleOptionsLeft = genDefaultCropBubblePopupWindow(R.layout.popup_crop_bubble_options_left);
        }

        if (mCurMode == Mode.CROP) {
            mVBlueHelpButton.setVisibility(View.VISIBLE);
        }

        final CropAction state = getCurrentCropState();
        if (state != CropAction.CLICK && state != CropAction.FINISH
                && state != CropAction.FLAG) {
            return;
        }

        final RectF rect = mCropImageView.getCropWindowRect();
        if (rect != null && !rect.isEmpty()) {
            /*final int r = mCropImageView.getMeasuredWidth();
            final int centerX = (int) rect.centerX();
            final int cropImageViewCenterX = (r >>> 1);
            showCropPopWinAtLocation(centerX > cropImageViewCenterX ? mBubbleOptionsRight : mBubbleOptionsLeft, 96, 166, 10);*/
            showCropPopWinAtLocation(/*centerX > cropImageViewCenterX ? mBubbleOptionsRight : */mBubbleOptionsLeft, 96, 166, 10);
            updateOptionsBubblesDrawable();
        }
    }

    private CropAction getCurrentCropState() {
        return mCropActions.get(mCropActions.size() - 1);
    }

    private CropAction getCurrentHeadCropState() {
        return mCropActions.size() > 0 ? mCropActions.get(0) : CropAction.IDEL;
    }

    private void setCurrentCropAction(CropAction c) {
        if (mCropActions.size() == 0) {
            mCropActions.add(c);
        } else {
            mCropActions.set(mCropActions.size() - 1, c);
        }
    }

    /**
     * @param popupWindow
     * @param winWidth
     * @param winHeight
     * @param margin
     */
    private void showCropPopWinAtLocation(PopupWindow popupWindow, int winWidth, int winHeight, int margin) {
        final RectF rect = mCropImageView.getCropWindowRect();
        if (rect != null && !rect.isEmpty()) {
            int x;
            int y;
            final int r = mCropImageView.getMeasuredWidth();
            final int b = mCropImageView.getMeasuredHeight();
            final int lv = 100;
            final Rect limitBounds = new Rect(lv, lv, r - lv, b - lv);
            final int popuWindowMargin = DensityUtils.dip2px(this, margin);
            final int popupWindowWidth = DensityUtils.dip2px(this, winWidth);
            final int popupWindowHeight = DensityUtils.dip2px(this, winHeight);
            final int centerX = (int) rect.centerX();
            final int cropImageViewCenterX = (r >>> 1);
            if (centerX > cropImageViewCenterX) {
                x = (int) (rect.left - popupWindowWidth - popuWindowMargin);
            } else {
                x = (int) rect.right + popuWindowMargin;
            }
            y = (int) Math.min(Math.max(limitBounds.top, rect.centerY() - (popupWindowHeight >>> 1)), limitBounds.bottom - popupWindowHeight);
            x = Math.min(Math.max(limitBounds.left, x), limitBounds.right - popupWindowWidth - popuWindowMargin);
            if (rect.intersects(x, y, x + popupWindowWidth, y + popupWindowHeight)) {
                x = (int) (rect.left + popuWindowMargin);
                y = (int) (rect.top + popuWindowMargin);
                // popupWindow = mBubbleOptionsLeft;
            }
            try {
                popupWindow.showAtLocation(mCropImageView, Gravity.NO_GRAVITY, x, y);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private View mPopupOptionCancel, mPopupOptionConfirm, mPopupOptionFinish, mPopupOptionNonExist;

    @NonNull
    private PopupWindow genDefaultCropBubblePopupWindow(int resId) {
        final View root = LayoutInflater.from(this).inflate(resId, null, false);
        PopupWindow window = new PopupWindow(root, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        final View buttonClick = root.findViewById(R.id.btnCropBubbleOptionClick);
        final View buttonOffset = root.findViewById(R.id.btnCropBubbleOptionOffset);
        final View buttonSlide = root.findViewById(R.id.btnCropBubbleOptionSlide);
        final View buttonNonExist = root.findViewById(R.id.btnCropBubbleOptionNonExist);
        final View buttonFinish = root.findViewById(R.id.btnCropBubbleOptionFinish);
        final View buttonClickConfirm = root.findViewById(R.id.btnCropBubbleOptionConfirm);
        final View buttonClickCancel = root.findViewById(R.id.btnCropBubbleOptionCancel);
        mPopupOptionCancel = buttonClickCancel;
        mPopupOptionNonExist = buttonNonExist;
        mPopupOptionFinish = buttonFinish;
        mPopupOptionConfirm = buttonClickConfirm;
        buttonClickConfirm.setOnClickListener(this::onButtonConfirmCrop);
        buttonClick.setOnClickListener(v -> switchCropState(CropAction.CLICK));
        buttonOffset.setOnClickListener(v -> switchCropState(CropAction.OFFSET));
        buttonSlide.setOnClickListener(v -> switchCropState(CropAction.SLIDE));
        buttonFinish.setOnClickListener(v -> switchCropState(CropAction.FINISH));
        buttonClickCancel.setOnClickListener(v -> {
            if (mCropActions.size() > 1) {
                mCropActions.remove(mCropActions.size() - 1);
                setCurrentCropAction(CropAction.CLICK);
                buttonNonExist.setVisibility(View.VISIBLE);
                buttonFinish.setVisibility(View.VISIBLE);
                mCropImageView.defolk();
            } else {
                backToStreamFromCropMode();
            }
        });
        buttonNonExist.setOnClickListener(v -> {
            switchCropState(CropAction.FLAG);
            mPopupOptionNonExist.setVisibility(View.GONE);
            mPopupOptionFinish.setVisibility(View.GONE);
        });


        return window;
    }


    /**
     *
     */
    private void updateOptionsBubblesDrawable() {
        if (mBubbleOptionsLeft != null && mBubbleOptionsLeft.isShowing()) {
            updateOptionsBubblesDrawable(mBubbleOptionsLeft.getContentView());
        }
    }

    /**
     * @param root
     */
    private void updateOptionsBubblesDrawable(View root) {
        final View buttonClick = root.findViewById(R.id.btnCropBubbleOptionClick);
        final View buttonOffset = root.findViewById(R.id.btnCropBubbleOptionOffset);
        final View buttonSlide = root.findViewById(R.id.btnCropBubbleOptionSlide);
        final View buttonFinish = root.findViewById(R.id.btnCropBubbleOptionFinish);
        final CropAction state = getCurrentCropState();
        View button = null;
        switch (state) {
            case CLICK:
                button = buttonClick;
                break;
            case OFFSET:
                button = buttonOffset;
                break;
            case SLIDE:
                button = buttonSlide;
                break;
            case FINISH:
                button = buttonFinish;
                break;
            default:
        }
        buttonClick.setBackgroundResource(R.drawable.bg_capsule_crop_bubble_option);
        buttonSlide.setBackgroundResource(R.drawable.bg_capsule_crop_bubble_option);
        buttonOffset.setBackgroundResource(R.drawable.bg_capsule_crop_bubble_option);
        buttonFinish.setBackgroundResource(R.drawable.bg_capsule_crop_bubble_option);
        if (button != null) {
            button.setBackgroundResource(R.drawable.bg_capsule_crop_bubble_option_highlight);
        }
    }

    private void dismissCropBubbleOptions() {
        if (mCurMode != Mode.CROP) {
            mVBlueHelpButton.setVisibility(View.GONE);
        }
        if (mBubbleOptionsLeft != null) {
            mBubbleOptionsLeft.dismiss();
        }
        if (mBubbleOptionsConfirmation != null) {
            mBubbleOptionsConfirmation.dismiss();
        }
    }

    private int getSpecifiedScreenOrientation() {
        int ori = getRequestedOrientation();
        if (ori == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
            ori = getResources().getDisplayMetrics().widthPixels > getResources().getDisplayMetrics().heightPixels ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        return ori;
    }

    @Override
    public int getFragmentContainerId(String tag) {
        if (FmScriptTestingLogHorizontal.TAG.equals(tag)) {
            return R.id.logFragmenmtContainer;
        }
        return 0;
    }

    private FmScriptTestingLogHorizontal logHorizontal;

    @Override
    public Fragment onCreateFragmentWithTag(String tag) {
        final Intent intent = getIntent();
        if (FmScriptTestingLogHorizontal.TAG.equals(tag) &&
                !Useless.hasEmptyIn(intent.getStringExtra("scriptPath"), intent.getStringExtra("ip"),
                        intent.getStringExtra("asip"))) {
            if (logHorizontal == null) {
                logHorizontal = new FmScriptTestingLogHorizontal();
            }
            logHorizontal.config(mEntities, intent.getStringExtra("scriptPath"));
            return logHorizontal;
        }
        return null;
    }

}