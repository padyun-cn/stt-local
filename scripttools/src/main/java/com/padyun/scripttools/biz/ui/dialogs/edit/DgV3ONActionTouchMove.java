package com.padyun.scripttools.biz.ui.dialogs.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.activity.AcCropColorEditor;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.dialogs.AbsDgV2ONBase;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.content.trash.ClzCaster;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionSlide;
import com.uls.utilites.content.sp.SpTextWatcher;
import com.uls.utilites.un.Useless;

import androidx.annotation.NonNull;


/**
 * Created by daiepngfei on 6/21/19
 */
public class DgV3ONActionTouchMove extends AbsDgV2ONBase<SEActionSlide> {
    private static final int REQ_SLIDE = 12354;
    private SEActionSlide mData;
    private Callback<SEActionSlide> mCallback;
    private Activity mActivity;
    private Rect bounds;
    public DgV3ONActionTouchMove(@NonNull Activity context) {
        super(context);
    }

    public void init(Activity act, Rect bounds, SEActionSlide tap, Callback<SEActionSlide> callback) {
        mActivity = act;
        this.bounds = bounds;
        mData = tap == null ? new SEActionSlide() : tap;
        mCallback = callback;
        onInitView();
    }

    @Override
    protected void onInitView() {
        hideTitlebar(true);
        final ImageView emptyImg = findViewById(R.id.img_empty);
        emptyImg.setImageResource(R.drawable.ic_empty_color_set);
        final TextView floatingButtonText = findViewById(R.id.buttonText);
        floatingButtonText.setText("新增区域颜色");
        final FloatingActionButton button = findViewById(R.id.floatbutton);
        //button.setImageResource(R.drawable.ic_floating_img_capture);。
        final View closeButton = findViewById(R.id.buttonClose);
        closeButton.setOnClickListener(view -> dismiss());
        showColorEditLayout(mData, Result.UPDATE, this::dismiss);
    }

    @Override
    protected boolean isDetailMode() {
        return false;
    }

    @Override
    public boolean isTouchOutside() {
        return false;
    }

    private void updateCropImage() {
        final SEActionSlide tap = mData;
        if (tap == null) return;
        final ImageView imgTouchStart = findViewById(R.id.img_touch_start);
        final ImageView imgTouchEnd = findViewById(R.id.img_touch_end);
        CoBitmapWorker.consume8888Croped(tap.getOriginalPath(), tap.getStartBounds(), imgTouchStart::setImageBitmap);
        CoBitmapWorker.consume8888Croped(tap.getOriginalPath(), tap.getEndBounds(), imgTouchEnd::setImageBitmap);
    }

    @SuppressLint("SetTextI18n")
    public void showColorEditLayout(SEActionSlide ic, Result r, Runnable onConfirm) {
        final SEActionSlide tap = ic.seClone();
        showContentView(true);
        mData = tap;

        final EditText editName = findViewById(R.id.editText);
        editName.setText(tap.getName());
        editName.setSelection(Useless.nonNullStr(tap.getName()).length());
        editName.addTextChangedListener(new SpTextWatcher(tap::setName));

        updateCropImage();

        final View buttonSet = findViewById(R.id.button_set);
        buttonSet.setOnClickListener(v -> {
            /*V3SlideActivity.startSettingForResult(mActivity, REQ_COLOR_CHECK_RIGION, destFile,
                    tap.getStartBounds(), tap.getEndBounds(), tap.getStart_pointf(), tap.getEnd_pointf())*/
            Intent intent = new Intent(mActivity, AcCropColorEditor.class);
            Bundle bundle = new Bundle();
            bundle.putInt(SECons.Ints.KEY_CROP_REQ_MODE, SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_CROP_SLIDE);
            bundle.putString(SECons.Ints.KEY_ORIGIN_PATH, tap.getOriginalPath());
            bundle.putParcelable(SECons.Ints.KEY_CROP_CROP_RECT, bounds);
            bundle.putParcelable(SECons.Ints.KEY_CROP_SLIDE_START_RECT, tap.getStartBounds());
            bundle.putParcelable(SECons.Ints.KEY_CROP_SLIDE_END_RECT, tap.getEndBounds());
            intent.putExtras(bundle);
            attacherStartActivityForResult(intent, SECons.Ints.REQC_CO_CON_EDIT_CROP_SLIDE);
        });

        /*final View imgTouchStart = findViewById(R.id.img_touch_start);*/
        /*imgTouchStart.setOnClickListener(view -> {
            String destFile = CUtils.isEmpty(tap.getOriginalPath()) ? ScriptCons.Tools.genScreenFilePath() : tap.getOriginalPath();
            CropV2Activity.captureAndCropForResult(getActivity(), REQ_COLOR_CHECK_RIGION, getScript(), destFile, tap.getStartBounds());
        });*/

        /*final View imgTouchend = findViewById(R.id.img_touch_end);*/
        /*imgTouchend.setOnClickListener(view -> {
            String destFile = CUtils.isEmpty(tap.getOriginalPath()) ? ScriptCons.Tools.genScreenFilePath() : tap.getOriginalPath();
            CropV2Activity.captureAndCropForResult(getActivity(), REQ_COLOR_CHECK_RIGION_2, getScript(), destFile, tap.getEndBounds());
        });*/

        final EditText timeoutEdit = findViewById(R.id.edit_timeout);
        timeoutEdit.setText(String.valueOf(tap.getDelay()));
        timeoutEdit.addTextChangedListener(new SpTextWatcher(s -> tap.setDelay(Useless.stringToInt(s.toString()))));

        final View buttonFinish = findViewById(R.id.button_finish);
        buttonFinish.setOnClickListener(view -> {
            mCallback.onCallback(DgV3ONActionTouchMove.this, r, "", tap);
            if (onConfirm != null) onConfirm.run();
        });
    }

    @Override
    protected View getContentView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_dg_v2_action_scroll, null);
    }

    @Override
    protected View.OnClickListener getFloatingButtonClickListener() {
        return view -> showColorEditLayout(new SEActionSlide(), Result.CREATE, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        if(requestCode == SECons.Ints.REQC_CO_CON_EDIT_CROP_SLIDE && resultCode == Activity.RESULT_OK) {
            final SEActionSlide slide = mData;
            ClzCaster.cast(data.getParcelableExtra(SECons.Ints.KEY_CROP_SLIDE_START_RECT), Rect.class, slide::setStartBounds);
            ClzCaster.cast(data.getParcelableExtra(SECons.Ints.KEY_CROP_SLIDE_END_RECT), Rect.class, slide::setEndBounds);
            /*ClzCaster.objectCasting(data.getParcelableExtra(SlideSettingActivity.KEY_RECT_START_POINTF), PointF.class, slide::setStart_pointf);
            ClzCaster.objectCasting(data.getParcelableExtra(SlideSettingActivity.KEY_RECT_END_POINTF), PointF.class, slide::setEnd_pointf);*/
            updateCropImage();
        }
    }

    @Override
    public BaseRecyclerHolder generateVHByType(@NonNull View itemView, int viewType) {
        return null;
    }
}
