package com.padyun.scripttools.biz.ui.dialogs.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mon.ui.buildup.IntegerNumberInput;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.activity.AcCropColorEditor;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.content.imgtool.SEImgConvertor;
import com.padyun.scripttools.biz.ui.dialogs.DgV2SimpleWheel;
import com.padyun.scripttools.biz.ui.dialogs.DgV2TipsBase;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.content.data.SEImageInfoFileWriter;
import com.uls.utilites.content.CoreWorkers;
import com.padyun.scripttools.content.trash.ClzCaster;
import com.padyun.scripttools.module.runtime.StContext;
import com.padyun.scripttoolscore.compatible.data.model.ImageInfo;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemImage;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;
import com.uls.utilites.content.sp.SpSeekBarChangeListener;
import com.uls.utilites.content.sp.SpTextWatcher;
import com.uls.utilites.un.Useless;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * Created by daiepngfei on 6/21/19
 */
public class DgV3ONImgEditCrop extends DgV2TipsBase {

    private boolean mIsOringinalPath;
    private static final String[] IMG_FLAG_LABELS = {"原图", "HLS", "二值化"};
    private static final int[] IMG_FLAGS = {ImageInfo.FLAG_COLOR, ImageInfo.FLAG_HLS, ImageInfo.FLAG_THRESHOLD};
    private static final String[] IMG_FLAGS_TIPS = {"使用原图进行对比", "适用于在游戏中有颜色或亮度变化的图片", "适用于在游戏中为透明背景的图片"};
    private static final String[] IMG_STATE_LABELS = {"存在", "不存在"};
    private static final String[] IMG_SEARCHING_REGION_LEBALS = {"默认范围", "指定范围"};
    private static final String[] IMG_SEARCHING_REGION_TIPS = {"默认范围内查找图片", "设置某个范围中查找当前图片"};
    private static final int[] IMG_STATES = {SEItemImage.STATE_EXIST, SEItemImage.STATE_WITHOUT};
    private SEItemImage mCurImage;
    private SEImgConvertor mSEImgConvertor = new SEImgConvertor();
    private Callback<SEItemImage> mCallback;
    private Activity activity;

    public DgV3ONImgEditCrop(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getTipsContentLayout() {
        return R.layout.view_dg_img_set_detail;
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        findViewById(R.id.title_bar).setVisibility(View.GONE);
        findViewById(R.id.layout_content_container).setBackgroundResource(R.drawable.bg_dg_shadow_card_fff8f7f7);
//        ((TextView) findViewById(R.id.tips)).setVisibility(View.GONE);
        //.setText("注意：如果需要改变一张截图而不改变所有改截图，请重命名并选择保存-另存为");
    }

    public void init(Activity act, SEItemImage image, Callback<SEItemImage> callback) {
        this.activity = act;
        this.mCallback = callback;
        showEditLayout(image, Result.UPDATE, this::dismiss);
    }


    public Activity getActivity() {
        return activity;
    }

    protected Callback getCallback() {
        return mCallback;
    }


    private void doCallback(Result r, String key, SEItemImage data) {
        if (mCallback != null) {
            mCallback.onCallback(this, r, key, data);
        }
    }

    private void updateCoordAndRange() {
        final SEItemImage image = mCurImage;
        mSEImgConvertor.config(image.getImageOriginal(), image.getCropBounds(), true);
        switch (image.getImageFlag()) {
            case ImageInfo.FLAG_COLOR:
                mSEImgConvertor.cColor();
                break;
            case ImageInfo.FLAG_HLS:
                mSEImgConvertor.cHLS();
                break;
            case ImageInfo.FLAG_THRESHOLD:
                mSEImgConvertor.cThreshold(image.getImageThreshold(), image.getImageMaxval(), image.getImageType());
                break;
            default:
        }
    }


    private void showEditLayout(SEItemImage ic, Result r, Runnable onConfirm) {
        final SEItemImage image = ic.seClone(true);
        mCurImage = image;

        // profile
        /*final ImageView imgTest = findViewById(R.id.imgTest);
        Glide.with(getActivity()).load(image.getImageCropPath()).into(imgTest);*/
        final ImageView img = findViewById(R.id.img);
        img.setOnClickListener(v -> startCropOnly(SECons.Ints.REQC_CO_CON_EDIT_CROP, image.getCropBounds()));

        mSEImgConvertor.setOnConversion((c, bitmap) -> img.setImageBitmap(bitmap));
        updateCoordAndRange();
        final EditText imgName = findViewById(R.id.imgName);
        imgName.setText(image.getImageName());
        imgName.setSelection(Useless.nonNullStr(image.getImageName()).length());
        imgName.addTextChangedListener(new SpTextWatcher(image::setImageName));

        // sim
        final EditText simEditor = findViewById(R.id.editSim);
        simEditor.setText(String.valueOf(image.getImageSim()));
        simEditor.addTextChangedListener(new SpTextWatcher(charSequence -> image.setImageSim(Useless.stringToInt(charSequence, image.getImageSim()))));

        // type
        updateTypeLayout(image);

        // state
        final TextView stateButton = findViewById(R.id.buttonState);
        Useless.consumeIndexOf(IMG_STATES, image.getState(), t -> stateButton.setText(IMG_STATE_LABELS[t]));
        stateButton.setOnClickListener(view -> {
            /*final DgV2SimpleWheel dg = new DgV2SimpleWheel(getContext());
            dg.onComponentInit(CMessyUtils.asList(IMG_STATE_LABELS), Math.max(0, CMessyUtils.indexOf(IMG_STATES, image.getState())), (text, position) -> {
                stateButton.setText(text);
                image.setState(IMG_STATES[position]);
                dg.dismiss();
            });
            dg.show();*/
        });

        // time
        final EditText timeEditor = findViewById(R.id.editTime);
        timeEditor.setText(String.valueOf(image.getTimeout()));
        timeEditor.addTextChangedListener(new SpTextWatcher(s -> image.setTimeout(Useless.stringToInt(s, image.getTimeout()))));

        // crop searching cropRegion
        final TextView cropButton = findViewById(R.id.button_range_style_select);
        final boolean hasRangeSet = image.getSearchRangeRect() != null;
        updateRegionSetLayout(image, hasRangeSet);
        cropButton.setOnClickListener(v -> {
            final DgV2SimpleWheel wheel = new DgV2SimpleWheel(getActivity());
            wheel.init(Useless.asList(IMG_SEARCHING_REGION_LEBALS), image.getSearchRangeRect() != null ? 1 : 0, (text, position) -> {
                if (position == 0) {
                    image.resetCoordAndRange();
                }
                updateRegionSetLayout(image, position == 1);
            });
            wheel.show();
        });


        // finish - button
        final View finishButton = findViewById(R.id.button_finish);
        finishButton.setOnClickListener(view -> {
            final SEImage ori = ic.getImage_detail();
            final SEImage n = image.getImage_detail();
            final boolean boundsChanged = !ori.getBounds().equals(n.getBounds());
            final boolean imageFlagChanged = n.getImageFlag() != ori.getImageFlag();
            final boolean threshChanged = !imageFlagChanged && n.getImageFlag() == ImageInfo.FLAG_THRESHOLD && n.getImageThreshold() != ori.getImageThreshold();
            final String key = "changed:" + (boundsChanged ? "bounds|" : "") + (imageFlagChanged ? "imageFlag|" : "") + "END";
            if (boundsChanged || imageFlagChanged || threshChanged) {
                final String path = image.getImageCropPath();
                // remember to clear bitmap cache
                CoBitmapWorker.lruInvalidate(image.getImageCropPath(), null);
                CoBitmapWorker.lruInvalidate(image.getImageOriginal(), image.getCropBounds());
                if (this.onDoneClickListener == null || !this.onDoneClickListener.onDoneClick(r, key, image, path)) {
                    callbackWithSaving(r, key, image, path);
                }
            } else doCallback(r, "unchanged", image);
        });
    }

    private OnDoneClickListener onDoneClickListener;

    public void setOnDoneClickListener(OnDoneClickListener onDoneClickListener) {
        this.onDoneClickListener = onDoneClickListener;
    }

    public interface OnDoneClickListener {
        boolean onDoneClick(Result r, String key, SEItemImage image, String path);
    }

    private void startCropOnly(int reqCode, Rect rect) {
        Intent intent = new Intent(getActivity(), AcCropColorEditor.class);
        Bundle bundle = new Bundle();
        bundle.putInt(SECons.Ints.KEY_CROP_REQ_MODE, SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_CROP_CLICK);
        bundle.putString(SECons.Ints.KEY_ORIGIN_PATH, mCurImage.getImageOriginal());
        bundle.putParcelable(SECons.Ints.KEY_CROP_CROP_RECT, rect);
        intent.putExtras(bundle);
        attacherStartActivityForResult(intent, reqCode);
    }

    private void updateTypeLayout(SEItemImage image) {
        final TextView imgFlagSetButton = findViewById(R.id.buttonImgType);
        final TextView typeTips = findViewById(R.id.text_tips_img_type);
        final AppCompatSeekBar seekBar = findViewById(R.id.seek_threashold);
        final IntegerNumberInput numberInput = findViewById(R.id.edit_number_threashold);
        seekBar.setOnSeekBarChangeListener(new SpSeekBarChangeListener(t -> {
            image.setImageThreshold(t);
            numberInput.setText(String.valueOf(t));
            updateCoordAndRange();
        }));
        numberInput.addTextChangedListener(new SpTextWatcher(s -> {
            final int v = Useless.limitInRange(Useless.stringToInt(s), 0, 255);
            image.setImageThreshold(v);
            seekBar.setProgress(v);
            updateCoordAndRange();
            numberInput.setSelection(Useless.nonNullStr(numberInput.getText()).toString().length());
        }));
        Useless.consumeIndexOf(IMG_FLAGS, image.getImageFlag(), t -> updateImgTypeUI(image, imgFlagSetButton, typeTips, numberInput, seekBar, t));
        imgFlagSetButton.setOnClickListener(view -> {
            final DgV2SimpleWheel dg = new DgV2SimpleWheel(getContext());
            dg.init(Useless.asList(IMG_FLAG_LABELS), Math.max(0, Useless.indexOf(IMG_FLAGS, image.getImageFlag())), (text, position) -> {
                updateImgTypeUI(image, imgFlagSetButton, typeTips, numberInput, seekBar, position);
                updateCoordAndRange();
                dg.dismiss();
            });
            dg.show();
        });
    }

    public void callbackWithSaving(Result result, final String key, SEItemImage image, final String path) {
        showFinishLoading();
        CoreWorkers
                .on(() -> {
                    final File f = StContext.assets().process(mSEImgConvertor.getConvertedMat());
                    if (f != null) {
                        final SEImage seImage = image.getImage_detail();
                        seImage.setImageCropPath(f.getAbsolutePath());
                        seImage.setCropFileName(f.getName());
                        /* MODIFIED BY DAIPENGFEI on 2021-02-20. */
                        return SEImageInfoFileWriter.writeToFile(seImage);
                        // return StContext.network().applyTest().updateImageSync(seImage);
                        /* MODIFIED END. */
                    }
                    return false;
                })
                .then(r -> {
                    // dismiss onLoadingCoScript
                    dimissFinishLoading();
                    // failed
                    if (!r) {
                        Toast.makeText(getActivity(), "同步保存失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // reset crop cropPath
                    //image.setImageCropPath(path);

                    // do callback
                    doCallback(result, key, image);
                });
    }

    private void showFinishLoading() {
        setMasked(true);
        findViewById(R.id.button_finish_avi).setVisibility(View.VISIBLE);
    }

    private void dimissFinishLoading() {
        setMasked(false);
        findViewById(R.id.button_finish_avi).setVisibility(View.GONE);
    }

    private void updateRegionSetLayout(SEItemImage image, boolean show) {
        findViewById(R.id.layout_specified_region).setVisibility(show ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.button_range_style_select)).setText(IMG_SEARCHING_REGION_LEBALS[show ? 1 : 0]);
        ((TextView) findViewById(R.id.tipsOfCropRectRigion)).setText(IMG_SEARCHING_REGION_TIPS[show ? 1 : 0]);
        findViewById(R.id.button_range_set).setOnClickListener(v1 -> {
            if (Useless.isEmpty(image.getImageCropPath())) {
                Toast.makeText(getActivity(), "尚未进行截图", Toast.LENGTH_SHORT).show();
                return;
            }
            startCropOnly(SECons.Ints.REQC_CO_CON_EDIT_REGION, mCurImage.getSearchRangeRect());
        });
        final ImageView img = findViewById(R.id.img_specified_region);
        if (image.getSearchRangeRect() != null) {
            CoBitmapWorker.consume8888Croped(image.getImageOriginal(), image.getSearchRangeRect(), img::setImageBitmap);
        } else {
            img.setImageBitmap(null);
        }
    }

    private void updateImgTypeUI(SEItemImage image, TextView imgFlagSetButton, TextView typeTips, IntegerNumberInput numberInput, AppCompatSeekBar seekBar, int position) {
        if (position > -1 && position < IMG_FLAGS.length) {
            imgFlagSetButton.setText(IMG_FLAG_LABELS[position]);
            typeTips.setText(IMG_FLAGS_TIPS[position]);
            image.setImageFlag(IMG_FLAGS[position]);
            if (position == IMG_FLAGS.length - 1) {
                findViewById(R.id.seekbar_layout).setVisibility(View.VISIBLE);
                numberInput.setText(String.valueOf(Useless.limitInRange(image.getImageThreshold(), 0, 255)));
            } else {
                findViewById(R.id.seekbar_layout).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        final SEItemImage image = mCurImage;
        switch (requestCode) {
            case SECons.Ints.REQC_CO_CON_EDIT_CROP:
                ClzCaster.cast(data.getParcelableExtra(SECons.Ints.KEY_CROP_CROP_RECT), Rect.class, t -> {
                    if (image != null && t != null && !t.equals(mCurImage.getCropBounds())) {
                        image.setImageX(t.left);
                        image.setImageY(t.top);
                        image.setImageW(t.width());
                        image.setImageH(t.height());
                        updateCoordAndRange();
                        updateTypeLayout(image);
                        updateRegionSetLayout(image, image.getSearchRangeRect() != null);
                        /*setMasked(true);
                        SETCropSaver.saveAndUpload(image.getSEImage(), image.getCropBounds(), image.getImageCropPath(), r -> {
                            if (r) {

                            }
                            setMasked(false);
                        });*/

                    }
                });
                break;
            case SECons.Ints.REQC_CO_CON_EDIT_REGION:
                ClzCaster.cast(data.getParcelableExtra(SECons.Ints.KEY_CROP_CROP_RECT), Rect.class, t -> {
                    SECoordFixed coordFixed = new SECoordFixed();
                    coordFixed.setX(t.left);
                    coordFixed.setY(t.top);
                    SERangeSize range = new SERangeSize();
                    range.setW(t.width());
                    range.setH(t.height());
                    if (image != null) {
                        image.setCoord(coordFixed);
                        image.setRange(range);
                        if (image.getSearchRangeRect() != null) {
                            CoBitmapWorker.consume8888Croped(image.getImageOriginal(), image.getSearchRangeRect(), t1 -> ((ImageView) findViewById(R.id.img_specified_region)).setImageBitmap(t1));
                        }
                    }
                });
                break;
            default:
        }
    }

}
