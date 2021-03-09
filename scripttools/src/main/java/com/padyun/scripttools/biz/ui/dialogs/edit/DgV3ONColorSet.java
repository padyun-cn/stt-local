package com.padyun.scripttools.biz.ui.dialogs.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mon.ui.list.compat.adapter.BaseRecyclerHolder;
import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.mon.ui.list.compat.adapter.IBaseRecyclerModel;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.activity.AcCropColorEditor;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.content.imgtool.SEColorPickUtils;
import com.padyun.scripttools.biz.ui.dialogs.AbsDgV2ONBase;
import com.padyun.scripttools.biz.ui.dialogs.DgV2SimpleWheel;
import com.padyun.scripttools.content.trash.ClzCaster;
import com.padyun.scripttoolscore.compatible.data.model.SEColor;
import com.padyun.scripttoolscore.compatible.data.model.coord.SECoordFixed;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItem;
import com.padyun.scripttoolscore.compatible.data.model.item.SEItemColor;
import com.padyun.scripttoolscore.compatible.data.model.range.SERangeSize;
import com.uls.utilites.content.sp.SpTextWatcher;
import com.uls.utilites.ui.DensityUtils;
import com.uls.utilites.ui.Viewor;
import com.uls.utilites.un.Useless;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by daiepngfei on 6/21/19
 */
public class DgV3ONColorSet extends AbsDgV2ONBase {
    /*public static final String KEY_RANGE = "range";
    public static final String KEY_COLOR_PICK = "color_pick";*/
    private static final int REQ_COLOR_PICK = 1234;
    private static final int REQ_COLOR_CHECK_RIGION = 5123;
    private Activity mActivity;

    private static final ArrayList<Integer> STATES = new ArrayList<>();
    private static final String[] STATES_DESC = new String[]{"无变化", "有变化", "多于", "少于"};

    static {
        STATES.add(SEItem.STATE_UNCHANGE);
        STATES.add(SEItem.STATE_CHANGE);
        STATES.add(SEItem.STATE_MORE);
        STATES.add(SEItem.STATE_LESS);
    }

    public DgV3ONColorSet(@NonNull Activity context) {
        super(context);
        this.mActivity = context;
    }

    public void init(Activity act, SEItemColor color, Callback callback) {
        if (color != null) mCurItemColor = color.seClone(true);
        onInitView();
    }

    @Override
    protected void onInitView() {
        final ImageView emptyImg = findViewById(R.id.img_empty);
        emptyImg.setImageResource(R.drawable.ic_empty_color_set);
        final TextView floatingButtonText = findViewById(R.id.buttonText);
        floatingButtonText.setText("新增区域颜色");
        final FloatingActionButton button = findViewById(R.id.floatbutton);
        button.setImageResource(R.drawable.ic_floating_color_set);
        final View closeButton = findViewById(R.id.buttonClose);
        /*final boolean editMode = mCurItemColor != null;
        closeButton.setOnClickListener(view -> {
            if (!editMode && mCurItemColor != null) {
                dismissContentView();
            } else dismiss();
        });
        if (editMode)*/
        showColorEditLayout(mCurItemColor, Result.UPDATE, this::dismiss);
    }

    @Override
    protected boolean isDetailMode() {
        return mCurItemColor != null;
    }

    @Override
    protected String getCustomTips() {
        return "选择一个设置好的区域颜色，或添加一个新的区域颜色。";
    }

    @Override
    protected String getCustomDetailTips() {
        return "在截图中选择一个区域并取色，当在此区域找到颜色时，确定在当前场景画面中。";
    }

    public void dismissContentView() {
        mCurItemColor = null;
        showContentView(false);
    }

    @Override
    public boolean isTouchOutside() {
        return false;
    }

    @Override
    protected RecyclerView.ItemDecoration getDecor() {
        final int margin_out = DensityUtils.dip2px(getContext(), 1);
        final Paint paint = new Paint();
        paint.setColor(Color.parseColor("#D8D8D8"));
        paint.setAntiAlias(true);
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                final int pos = parent.getChildAdapterPosition(view);
                if (pos > 0) {
                    outRect.top = margin_out;
                }
            }

            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    c.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom(), paint);
                }
            }
        };
    }




    @Override
    public BaseRecyclerHolder generateVHByType(@NonNull View itemView, int viewType) {
        return null;
    }

    /**
     *
     */
    private static class ColorSetHolder extends BaseRecyclerHolder<ColorSetItem> {
        private TextView label;
        private View buttonEdit;
        private DgV3ONColorSet set;
        private View root;

        ColorSetHolder(View itemView, DgV3ONColorSet set) {
            super(itemView);
            this.root = itemView;
            this.set = set;
        }

        @Override
        public void init(View root) {
            label = root.findViewById(R.id.label);
            buttonEdit = root.findViewById(R.id.img_edit);
        }

        @Override
        protected void set(@NonNull Activity act, @NonNull BaseV2RecyclerAdapter adapter, @NonNull ColorSetItem data, int position) {
            label.setText(data.text);
            buttonEdit.setOnClickListener(view -> set.showColorEditLayout(data.item, Result.UPDATE, () -> {
                set.dismissContentView();
                adapter.notifyDataSetChanged();
            }));
            root.setOnClickListener(view -> set.doCallback(Result.SELECTED, data.item));
        }

    }

    private static class ColorSetItem implements IBaseRecyclerModel {
        private SEItemColor item;
        private String text;

        ColorSetItem(String text, SEItemColor color) {
            this.text = text;
            this.item = color;
        }

        @Override
        public int getTypeItemLayoutId() {
            return R.layout.item_dg_item_text_edit;
        }
    }

    private SEItemColor mCurItemColor;

    @SuppressLint("SetTextI18n")
    public void showColorEditLayout(SEItemColor ic, Result r, Runnable onConfirm) {
        SEItemColor itemColor = ic.seClone(true);
        mCurItemColor = itemColor;
        showContentView(true);
        /*--------C1--------*/
        // show name
        final EditText text = findViewById(R.id.edit_name);
        text.setText(Useless.nonNullStr(itemColor.getName()));
        text.setSelection(text.getText().toString().length());
        // preview image
        // final ImageView img = findViewById(R.id.img);
        // coord
        final TextView textCoord = findViewById(R.id.text_coord);
        final SECoordFixed coord = Useless.objectCasting(SECoordFixed.class, itemColor.getCoord());
        final SERangeSize range = Useless.objectCasting( SERangeSize.class, itemColor.getRange());
        updateCoordAndRange(itemColor.getOriginal(), coord, range);

        textCoord.setText("坐标：" + (coord == null || range == null ? "" : (coord.getX() + "," + coord.getY() + "," + range.getW() + "," + range.getH())));
        // pick crop image range
        final View buttonRange = findViewById(R.id.text_button_range);
        // buttonRange.setOnClickListener(view -> doCallback(Result.ACTION, KEY_RANGE, itemColor));
        buttonRange.setOnClickListener(view -> {
            //CropColorEditActivity.captureAndCropForResult(mActivity, REQ_COLOR_CHECK_RIGION, getScript(), cropPath, itemColor.getBounds())
            Intent intent = new Intent(mActivity, AcCropColorEditor.class);
            Bundle bundle = new Bundle();
            bundle.putInt(SECons.Ints.KEY_CROP_REQ_MODE, SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_REGION);
            bundle.putString(SECons.Ints.KEY_ORIGIN_PATH, itemColor.getOriginal());
            bundle.putParcelable(SECons.Ints.KEY_CROP_REGION_RECT, itemColor.getBounds());
            intent.putExtras(bundle);
            mActivity.startActivityForResult(intent, SECons.Ints.REQC_CO_CON_EDIT_REGION);
        });

        /*--------C2--------*/
        updateTextColor(itemColor.getColor());
        final TextView buttonColorPicker = findViewById(R.id.text_button_color_pick);
        // buttonColorPicker.setOnClickListener(view -> doCallback(Result.ACTION, KEY_COLOR_PICK, itemColor));
        buttonColorPicker.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, AcCropColorEditor.class);
            Bundle bundle = new Bundle();
            bundle.putInt(SECons.Ints.KEY_CROP_REQ_MODE, SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_COLOR);
            bundle.putString(SECons.Ints.KEY_ORIGIN_PATH, itemColor.getOriginal());
            bundle.putParcelable(SECons.Ints.KEY_COLOR_PIXCEL, itemColor.getPixel());
            intent.putExtras(bundle);
            mActivity.startActivityForResult(intent, SECons.Ints.REQC_CO_CON_EDIT_COLOR);
        });

        /*--------C3 相似度 --------*/
        final EditText editColorSim = findViewById(R.id.text_sim_rate);
        editColorSim.setText(((int) (itemColor.getSim() * 100)) + "");
        /*--------C5 变化百分比（有无变化） --------*/
        final EditText editColorChange = findViewById(R.id.text_color_change_rate);
        final int colorNum = itemColor.getColorNumber();
        final int percentOfChange = colorNum == 0 ? 0 : (int) (itemColor.getCondtionCount() / colorNum * 1.0f * 100);
        editColorChange.setText(percentOfChange + "");
        editColorChange.addTextChangedListener(new SpTextWatcher(t -> {
            final int num = Useless.stringToInt(t.toString(), 0);
            itemColor.setColorNumberFloating((int) (num / 100f * itemColor.getColorNumber()));
           /* switch (itemColor.getState()) {
                case SEItem.STATE_UNCHANGE:
                case SEItem.STATE_CHANGE:
                    itemColor.setColorNumberFloating((int) (num / 100f * itemColor.getColorNumber()));
                    break;
                case SEItem.STATE_MORE:
                case SEItem.STATE_LESS:
                    itemColor.setColorNumberThreshold(num);
                    break;
                default:
            }*/
            updateCurNumsTextView();
        }));
        updateCurNumsTextView();
        /*--------C5 变化数量（大于小于） --------*/
        final EditText editColorChangeNum = findViewById(R.id.text_color_change_num);
        editColorChangeNum.setText(String.valueOf(itemColor.getColorNumberThreshold()));
        editColorChangeNum.addTextChangedListener(new SpTextWatcher(t -> {
            final int num = Useless.stringToInt(t.toString(), 0);
            itemColor.setColorNumberThreshold(num);
            updateCurNumsTextView();
        }));
        setState(itemColor.getState());
        // 吊起选择器
        final View colorChangeSelectorButton = findViewById(R.id.view_color_change_selector);
        colorChangeSelectorButton.setOnClickListener(view -> showColorChangeSelector());
        final TextView textView = findViewById(R.id.text_color_change_button);
        textView.setText(STATES_DESC[STATES.indexOf(itemColor.getState())]);

        // 设置超时
        final EditText editTimeout = findViewById(R.id.text_time_edit);
        editTimeout.setText(String.valueOf(itemColor.getTimeout()));
        editTimeout.addTextChangedListener(new SpTextWatcher(charSequence -> itemColor.setTimeout(Useless.stringToInt(charSequence.toString(), itemColor.getTimeout()))));

        // finish - button
        final View finishButton = findViewById(R.id.button_finish);
        finishButton.setOnClickListener(view -> {
            itemColor.setName(text.getText().toString());
            itemColor.setSim(Useless.stringToInt(editColorSim.getText().toString()) / 100f);
            doCallback(r, itemColor);
            if (onConfirm != null) onConfirm.run();
        });
    }

    /**
     *
     */
    private void showColorChangeSelector() {
        final DgV2SimpleWheel dg = new DgV2SimpleWheel(getContext());
        final SEItemColor color = mCurItemColor;
        if (color != null) {
            dg.init(Arrays.asList(STATES_DESC), STATES.indexOf(color.getState()), (text1, position) -> {
                final TextView textView = findViewById(R.id.text_color_change_button);
                textView.setText(text1);
                setState(STATES.get(position));
                dg.dismiss();
            }).show();
        }
    }

    /**
     * @param state
     */
    private void setState(int state) {
        final View layoutColorChangeNum = findViewById(R.id.layout_color_change_num);
        final View layoutColorChangeRate = findViewById(R.id.layout_color_change_rate);
        final boolean showRate = state == SEItem.STATE_UNCHANGE || state == SEItem.STATE_CHANGE;
        layoutColorChangeRate.setVisibility(showRate ? View.VISIBLE : View.GONE);
        layoutColorChangeNum.setVisibility(showRate ? View.GONE : View.VISIBLE);
        final SEItemColor itemColor = mCurItemColor;
        if (itemColor != null) itemColor.setState(state);
    }


    @SuppressLint("SetTextI18n")
    private void updateCoordAndRange(String imgFile, SECoordFixed coord, SERangeSize range) {

        if (!Useless.nulls(coord, range)) {
            final TextView textCoord = findViewById(R.id.text_coord);
            textCoord.setText("坐标：" + (coord == null || range == null ? "" : (coord.getX() + "," + coord.getY() + "," + range.getW() + "," + range.getH())));
            // preview image
            if (!Useless.isEmpty(imgFile)) {
                final ImageView img = findViewById(R.id.img);
                final SEItemColor colorItem = mCurItemColor;
//                SEBitmapUtils.consume8888Croped(colorItem.getOriginal(), colorItem.getBounds(), img::setImageBitmap);
//                SEBitmapUtils.consume64(colorItem.getOriginal(), img::setImageBitmap);
                SEColorPickUtils.getColorCountWithItemColor(colorItem, img::setImageBitmap, integer -> {
                    final int sum = integer == null ? 0 : integer;
                    final int floatingNum = colorItem.getColorNumberFloating();
                    final int newFloatingNum = (int) (floatingNum * 1.0f / Math.max(1, colorItem.getColorNumber()) * sum);
                    colorItem.setColorNumberFloating(newFloatingNum);
                    colorItem.setColorNumber(sum);
                    updateCurNumsTextView();
                });
            }
        }
    }

    private void updateCurNumsTextView() {
        final SEItemColor colorItem = mCurItemColor;
        if (colorItem != null) {
            Viewor.setText(findViewById(R.id.label_color_pixel_add_count), "允许颜色增加个数：" + colorItem.getColorNumberCurrentWithState() + "像素");
            Viewor.setText(findViewById(R.id.label_color_pixel_count), "当前颜色个数：" + colorItem.getColorNumber() + "像素");
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateTextColor(int color) {
        // set color view's color
        final View colorView = findViewById(R.id.view_color_value);
        colorView.setBackgroundColor(color);
        final TextView colorValueDesc = findViewById(R.id.color_value_desc);
        // colorValueDesc.setText(CUtils.colorToRGBString(itemColor.getColor()));
        colorValueDesc.setText(
                "色值：" +
                        "R:" + Color.red(color) + " " +
                        "G:" + Color.green(color) + " " +
                        "B:" + Color.blue(color) + " "
        );

        final SEItemColor colorItem = mCurItemColor;
        SEColorPickUtils.getColorCountWithItemColor(colorItem, integer -> {
            final int sum = integer == null ? 0 : integer;
            final int floatingNum = colorItem.getColorNumberFloating();
            final int newFloatingNum = (int) (floatingNum * 1.0f / Math.max(1, colorItem.getColorNumber()) * sum);
            colorItem.setColorNumberFloating(newFloatingNum);
            colorItem.setColorNumber(sum);
            updateCurNumsTextView();
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != Activity.RESULT_OK) return;
        final SEItemColor itemColor = mCurItemColor;
        switch (requestCode) {
            case SECons.Ints.REQC_CO_CON_EDIT_COLOR:
                ClzCaster.cast(data.getParcelableExtra(SECons.Ints.KEY_COLOR_ENTITY), SEColor.class, t -> {
                    final int color = t.getColor();
                    final String file = t.getPath();
                    if (itemColor != null) {
                        itemColor.setPixel(new Point(t.getRect().centerX(), t.getRect().centerY()));
                        itemColor.setColor(color);
                        itemColor.setOriginal(file);
                    }
                    updateTextColor(color);
                });

                break;
            case SECons.Ints.REQC_CO_CON_EDIT_REGION:
                ClzCaster.cast(data.getParcelableExtra(SECons.Ints.KEY_CROP_REGION_RECT), Rect.class, t -> {
                    SECoordFixed coordFixed = new SECoordFixed();
                    coordFixed.setX(t.left);
                    coordFixed.setY(t.top);
                    SERangeSize range = new SERangeSize();
                    range.setW(t.width());
                    range.setH(t.height());
                    if (itemColor != null) {
                        itemColor.setCoord(coordFixed);
                        itemColor.setRange(range);
                        updateCoordAndRange(itemColor.getOriginal(), coordFixed, range);
                    }
                });
                break;
            default:
        }
    }

    @Override
    protected View getContentView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.view_dg_color_set_detail, null);
    }

    @Override
    protected View.OnClickListener getFloatingButtonClickListener() {
        return view -> showColorEditLayout(new SEItemColor(), Result.CREATE, null);
    }
}
