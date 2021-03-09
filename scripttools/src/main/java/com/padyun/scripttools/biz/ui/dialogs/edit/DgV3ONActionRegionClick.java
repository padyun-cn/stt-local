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

import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.activity.AcCropColorEditor;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.dialogs.AbsDgV2ONBase;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.content.trash.ClzCaster;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionTap;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;
import com.uls.utilites.content.sp.SpTextWatcher;
import com.uls.utilites.un.Useless;

import androidx.annotation.NonNull;


/**
 * Created by daiepngfei on 6/21/19
 */
public class DgV3ONActionRegionClick extends AbsDgV2ONBase<SEActionTap> {
    /*public static final String KEY_RANGE = "range";
    public static final String KEY_COLOR_PICK = "color_pick";*/
    private static final int REQ_COLOR_PICK = 123123;
    private static final int REQ_COLOR_CHECK_RIGION = 44123;
    private SEActionTap mData;
    private Activity mActivity;
    private Callback<SEActionTap> mCallback;

    public DgV3ONActionRegionClick(@NonNull Activity context) {
        super(context);
    }

    public void init(Activity act, SEActionTap tap, Callback<SEActionTap> callback) {
        mCallback = callback;
        mActivity = act;
        hideTitlebar(true);
        mData = tap == null ? new SEActionTap() : tap;
        onInitView();
        showColorEditLayout(mData, Result.UPDATE, this::dismiss);
    }

    @Override
    protected void onInitView() {
        /*hideTitlebar(true);
        final ImageView emptyImg = findViewById(R.id.img_empty);
        emptyImg.setImageResource(R.drawable.ic_empty_color_set);
        final TextView floatingButtonText = findViewById(R.id.buttonText);
        floatingButtonText.setText("新增区域颜色");
        final FloatingActionButton button = findViewById(R.id.floatbutton);
        //button.setImageResource(R.drawable.ic_floating_img_capture);。
        final View closeButton = findViewById(R.id.buttonClose);
        closeButton.setOnClickListener(view -> dismiss());
        showColorEditLayout(mData, Result.UPDATE, this::dismiss);*/
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
        final SEActionTap tap = mData;
        if (tap == null) return;
        final ImageView imgCrop = findViewById(R.id.img_crop);
        CoBitmapWorker.consume8888Croped(tap.getOriginalPath(), tap.getBounds(), imgCrop::setImageBitmap);
    }

    @SuppressLint("SetTextI18n")
    private void showColorEditLayout(SEActionTap ic, Result r, Runnable onConfirm) {
        final SEActionTap tap = ic.seClone();
        showContentView(true);
        mData = tap;

        final EditText editName = findViewById(R.id.editText);
        editName.setText(tap.getName());
        editName.setSelection(Useless.nonNullStr(tap.getName()).length());
        editName.addTextChangedListener(new SpTextWatcher(s -> tap.setName(s.toString())));
        updateCropImage();
        final View buttonOffsetEdit = findViewById(R.id.layout_button_edit_crop);
        buttonOffsetEdit.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, AcCropColorEditor.class);
            Bundle bundle = new Bundle();
            bundle.putInt(SECons.Ints.KEY_CROP_REQ_MODE, SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_REGION);
            bundle.putString(SECons.Ints.KEY_ORIGIN_PATH, tap.getOriginalPath());
            bundle.putParcelable(SECons.Ints.KEY_CROP_REGION_RECT, tap.getBounds());
            intent.putExtras(bundle);
            mActivity.startActivityForResult(intent, SECons.Ints.REQC_CO_CON_EDIT_REGION);
        });

        final EditText timeoutEdit = findViewById(R.id.edit_timeout);
        timeoutEdit.setText(String.valueOf(tap.getDelay()));
        timeoutEdit.addTextChangedListener(new SpTextWatcher(s -> tap.setDelay(Useless.stringToInt(s.toString()))));

        final View buttonFinish = findViewById(R.id.button_finish);
        buttonFinish.setOnClickListener(view -> {
            if(mCallback != null) mCallback.onCallback(this, r, "", tap);
            if (onConfirm != null) onConfirm.run();
        });
    }

    @Override
    protected View getContentView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_dg_v2_action_region_click, null);
    }

    @Override
    protected View.OnClickListener getFloatingButtonClickListener() {
        return view -> showColorEditLayout(new SEActionTap(), Result.CREATE, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        final SEActionTap tap = mData;
        switch (requestCode) {
            case SECons.Ints.REQC_CO_CON_EDIT_REGION:
                ClzCaster.cast(data.getParcelableExtra(SECons.Ints.KEY_CROP_REGION_RECT), Rect.class, t -> {
                    SECoordFixed coordFixed = new SECoordFixed();
                    coordFixed.setX(t.left);
                    coordFixed.setY(t.top);
                    SERangeSize range = new SERangeSize();
                    range.setW(t.width());
                    range.setH(t.height());
                    if (tap != null) {
                        tap.setCoord(coordFixed);
                        tap.setRange(range);
                        //tap.setOriginalPath();
                    }
                    updateCropImage();
                });
                break;
            default:
        }
    }

    @Override
    public BaseRecyclerHolder generateVHByType(@NonNull View itemView, int viewType) {
        return null;
    }
}
