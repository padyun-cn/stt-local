package com.padyun.scripttools.biz.ui.dialogs.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.activity.AcCropColorEditor;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.dialogs.AbsDgV2ONBase;
import com.padyun.scripttools.common.utils.CoBitmapWorker;
import com.padyun.scripttools.content.trash.ClzCaster;
import com.padyun.scripttoolscore.compatible.data.model.actions.SEActionImage;
import com.uls.utilites.content.sp.SpTextWatcher;
import com.uls.utilites.un.Useless;

import androidx.annotation.NonNull;


/**
 * Created by daiepngfei on 6/21/19
 */
public class DgV3ONActionOffset extends AbsDgV2ONBase<SEActionImage> {
    /*public static final String KEY_RANGE = "range";
    public static final String KEY_COLOR_PICK = "color_pick";*/
    private SEActionImage mData;
    private Activity mActivity;
    private Callback<SEActionImage> mCallback;
    public DgV3ONActionOffset(@NonNull Activity context) {
        super(context);
    }

    public void init(Activity act,  SEActionImage tap, Callback<SEActionImage> callback) {
        mData = tap == null ? new SEActionImage() : tap;
        mActivity = act;
        mCallback = callback;
        hideTitlebar(true);
        showColorEditLayout(mData, Result.UPDATE, this::dismiss);
    }

    @Override
    protected void onInitView() {

    }

    @Override
    protected boolean isDetailMode() {
        return false;
    }

    @Override
    public boolean isTouchOutside() {
        return false;
    }

    @SuppressLint("SetTextI18n")
    public void showColorEditLayout(SEActionImage ic, Result r, Runnable onConfirm) {
        final SEActionImage tap = ic.seClone();
        showContentView(true);
        mData = tap;
        final EditText editName = findViewById(R.id.editText);
        editName.setText(tap.getName());
        editName.setSelection(Useless.nonNullStr(tap.getName()).length());
        editName.addTextChangedListener(new SpTextWatcher(tap::setName));

        updateOffsetImg();
        /*final View buttonCancel = findViewById(R.id.button_offset_cancel);
        buttonCancel.setOnClickListener(view -> {
            tap.setShift_x(0);
            tap.setShift_y(0);
            updateOffsetImg();
        });*/

        final TextView buttonOffsetEdit = findViewById(R.id.button_edit_offset);
        buttonOffsetEdit.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, AcCropColorEditor.class);
            Bundle bundle = new Bundle();
            bundle.putInt(SECons.Ints.KEY_CROP_REQ_MODE, SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_CROP_OFFSET);
            bundle.putString(SECons.Ints.KEY_ORIGIN_PATH, tap.getImageOriginal());
            bundle.putParcelable(SECons.Ints.KEY_CROP_REGION_RECT, tap.getOffsetBounds());
            bundle.putParcelable(SECons.Ints.KEY_CROP_CROP_RECT, tap.getCropBounds());
            intent.putExtras(bundle);
            attacherStartActivityForResult(intent, SECons.Ints.REQC_CO_CON_EDIT_REGION);
        });

        final EditText timeoutEdit = findViewById(R.id.edit_timeout);
        timeoutEdit.setText(String.valueOf(tap.getDelay()));
        timeoutEdit.addTextChangedListener(new SpTextWatcher(s -> tap.setDelay(Useless.stringToInt(s))));

        final View buttonFinish = findViewById(R.id.button_finish);
        buttonFinish.setOnClickListener(view -> {
            if(mCallback != null) mCallback.onCallback(this, r, "", tap);
            if (onConfirm != null) onConfirm.run();
        });
    }

    private void updateOffsetImg() {
        final SEActionImage tap = mData;
        if (tap == null) return;
        final ImageView imgOffset = findViewById(R.id.img_offset);
        final boolean isOffset = tap.getShift_x() != 0 || tap.getShift_y() != 0;
        final Rect cropRect = tap.getCropBounds();
        if (cropRect != null && isOffset) {
            CoBitmapWorker.consume8888Croped(tap.getImageOriginal(), tap.getOffsetBounds(), imgOffset::setImageBitmap);
        } else {
            imgOffset.setImageResource(android.R.color.transparent);
        }

        final View buttonCancel = findViewById(R.id.button_offset_cancel);
        buttonCancel.setVisibility(isOffset ? View.VISIBLE : View.GONE);
    }

    @Override
    protected View getContentView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_dg_v3_action_offset, null);
    }

    @Override
    protected View.OnClickListener getFloatingButtonClickListener() {
        return view -> showColorEditLayout(new SEActionImage(), Result.CREATE, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || !isShowing()) return;
        final SEActionImage image = mData;
        switch (requestCode) {
            case SECons.Ints.REQC_CO_CON_EDIT_REGION:
                ClzCaster.cast(data.getParcelableExtra(SECons.Ints.KEY_EDIT_NEW_OFFSET), Point.class, t -> {
                    if (image != null) {
                        image.setShift_x(t.x);
                        image.setShift_y(t.y);
                    }
                    updateOffsetImg();
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
