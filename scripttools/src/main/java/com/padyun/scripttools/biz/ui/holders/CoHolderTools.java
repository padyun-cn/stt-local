package com.padyun.scripttools.biz.ui.holders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mon.ui.list.compat.adapter.BaseV2RecyclerAdapter;
import com.uls.utilites.un.Useless;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.activity.AcCropColorEditor;
import com.padyun.scripttools.biz.ui.content.SECons;
import com.padyun.scripttools.biz.ui.dialogs.DgV2Common;
import com.padyun.scripttools.biz.ui.dialogs.edit.DgV3ONActionOffset;
import com.padyun.scripttools.biz.ui.dialogs.edit.DgV3ONActionRegionClick;
import com.padyun.scripttools.biz.ui.dialogs.edit.DgV3ONActionTouchMove;
import com.padyun.scripttools.biz.ui.dialogs.edit.DgV3ONImgEditCrop;
import com.padyun.scripttools.biz.ui.fragment.FmScriptEditor;
import com.padyun.scripttools.compat.data.AbsCoConditon;
import com.padyun.scripttools.compat.data.AbsCoImage;
import com.padyun.scripttools.compat.data.CoClick;
import com.padyun.scripttools.compat.data.CoOffset;
import com.padyun.scripttools.compat.data.CoSlide;
import com.padyun.scripttools.compat.data.CoTap;
import com.padyun.scripttools.compat.data.CropImgGroupParceble;
import com.padyun.scripttools.compat.data.CoFinish;
import com.padyun.scripttools.content.la.ISEActivityAttatchable;
import com.padyun.scripttools.content.la.SESimpleActivityLifeAttacher;

import java.util.ArrayList;


/**
 * Created by daiepngfei on 8/20/19
 */
public class CoHolderTools {

    private static final int[] sCounterImageResIds = {
            R.drawable.ic_cond_loop_times_setting_infi,
            R.drawable.ic_cond_loop_times_setting_count,
    };

    public static void handleItemDisableState(View itemView, ImageView flag, AbsCoConditon data) {
        itemView.setBackgroundColor(data.isDisabled() ? itemView.getResources().getColor(R.color.color_editor_item_disable) : Color.WHITE);
        if (flag != null) {
            flag.setVisibility(data.isDisabled() ? View.VISIBLE : View.GONE);
            flag.setImageResource(R.drawable.ic_script_item_disabled);
        }
    }

    public static void handleInsertModeState(View itemView, BaseV2RecyclerAdapter adapter, AbsCoConditon data, int position) {
        if (itemView == null || data == null) {
            return;
        }
        View overlay = itemView.findViewById(R.id.overlay);
        View overlayInsertingPanel = itemView.findViewById(R.id.overlayInserting);
        View overlayInsertingMask = itemView.findViewById(R.id.overlayClickMask);
        View buttonLeft = itemView.findViewById(R.id.buttonLeft);
        View buttonRight = itemView.findViewById(R.id.buttonRight);
        overlayInsertingPanel.setOnClickListener(v -> {
            // do nothing
        });
        buttonLeft.setOnClickListener(v -> {
            Message msg = Message.obtain();
            msg.what = FmScriptEditor.FB_INSERT;
            msg.arg1 = position;
            adapter.feedback(msg);
        });
        buttonRight.setOnClickListener(v -> {
            Message msg = Message.obtain();
            msg.what = FmScriptEditor.FB_INSERT;
            msg.arg1 = position + 1;
            adapter.feedback(msg);
        });
        overlayInsertingMask.setOnClickListener(v -> {
            Useless.foreach(adapter.getData(), t -> {
                if (t instanceof AbsCoConditon) {
                    AbsCoConditon conditon = (AbsCoConditon) t;
                    if (conditon == data) {
                        conditon.getUiFlags().setInsertStateInserting();
                    } else {
                        conditon.getUiFlags().setInsertStateStandby();
                    }
                }
            });
            adapter.notifyDataSetChanged();
        });
        switch (data.getUiFlags().getInsertingState()) {
            case INSERTING:
                overlay.setVisibility(View.VISIBLE);
                overlayInsertingMask.setVisibility(View.INVISIBLE);
                overlayInsertingPanel.setVisibility(View.VISIBLE);
                break;
            case STANDBY:
                overlay.setVisibility(View.VISIBLE);
                overlayInsertingMask.setVisibility(View.VISIBLE);
                overlayInsertingPanel.setVisibility(View.INVISIBLE);
                break;
            case OFF:
                overlay.setVisibility(View.GONE);
                break;
            default:
        }
    }

    @SuppressLint("SetTextI18n")
    public static void handleTimeoutEditLayout(TextView integerNumberInput, AbsCoConditon data) {
        integerNumberInput.setText("延迟" + data.getCo_timeout() + "毫秒");
    }

    /**
     * @param textView
     * @param imageView
     */
    @SuppressLint("SetTextI18n")
    public static void handleCounterEditLayout(TextView textView, ImageView imageView, AbsCoConditon data) {
        textView.setText(data.getCo_brain_count() == 0 ? "" : String.valueOf(data.getCo_brain_count()));
        imageView.setImageResource(data.getCo_brain_count() == 0 ? sCounterImageResIds[0] : sCounterImageResIds[1]);
    }

    /**
     * @param act
     * @param data
     */
    static void editItemImage(Activity act, ISEActivityAttatchable attatchable, AbsCoImage data, Runnable dismiss) {
        DgV3ONImgEditCrop dgCrop = new DgV3ONImgEditCrop(act);
        dgCrop.setAttatchable(attatchable);
        dgCrop.init(act, data.getMainSEItem(), (dg, r, key, _data) -> {
            data.setIdentifyImage(_data.getImage_detail());
            data.setMainSEItem(_data);
            dg.dismiss();
            if (dismiss != null) {
                dismiss.run();
            }
        });
        dgCrop.show();
    }

    /**
     * @param act
     * @param data
     */
    static void editItemNonExistImage(Activity act, ISEActivityAttatchable attatchable, AbsCoImage data, Runnable dismiss) {
        DgV3ONImgEditCrop dgCrop = new DgV3ONImgEditCrop(act);
        dgCrop.setAttatchable(attatchable);
        dgCrop.init(act, data.getNonExistExtraItemImage(), (dg, r, key, _data) -> {
            data.setNonExsitExtra(_data.getImage_detail());
            data.setNonExistExtraItemImage(_data);
            dg.dismiss();
            if (dismiss != null) {
                dismiss.run();
            }
        });
        dgCrop.show();
    }

    /**
     * @param act
     * @param adapter
     * @param data
     */
    static void changeType(Activity act, ISEActivityAttatchable attatchable, BaseV2RecyclerAdapter adapter, AbsCoImage data, Runnable dismiss) {
        DgV2Common dgV2Common = DgV2Common.buildWith(act).setTitle("设置动作")
                .setTitleTips("设置执行的动作").setItems(Color.parseColor("#247EFB"), "点击图片", /*"点击固定位置",*/ "偏移点击位置", "模拟滑动", "设置不存在", "结束任务")
                .setOnItemClickListener((dg, adapter1, item, position1) -> {
                    AbsCoConditon condition = null;
                    Runnable runnable = null;
                    switch (position1) {
                        case 0:
                            if (!Useless.isInstance(CoClick.class, data) || Useless.isInstance(CoOffset.class, data)) {
                                condition = new CoClick(data.getIdentifyImage());
                            }
                            break;
                        /*case 1:
                            if (!CUtils.isInstance(CoTap.class)) {
                                final CoTap tap = new CoTap(data.getIdentifyImage());
                                runnable = TypeChangeRunnables.ofCoTap(act, attatchable, adapter, tap);
                                conditon = tap;
                            }
                            break;*/
                        case 1:
                            if (!Useless.isInstance(CoOffset.class, data)) {
                                final CoOffset offset = new CoOffset(data.getIdentifyImage());
                                runnable = TypeChangeRunnables.ofCoOffset(act, attatchable, adapter, offset);
                                condition = offset;
                            }
                            break;
                        case 2:
                            if (!Useless.isInstance(CoSlide.class, data)) {
                                CoSlide slide = new CoSlide(data.getIdentifyImage());
                                runnable = TypeChangeRunnables.ofCoSlide(act, attatchable, adapter, slide);
                                condition = slide;
                            }
                            break;
                        case 3:
                            if (Useless.isInstance(AbsCoImage.class, data)) {
                                /*data.setNonExsitExtra(data.getIdentifyImage());
                                data.setIdentifyImage(null);*/
                                /*CoFinish finish = new CoFinish(data.getIdentifyImage());
                                runnable = TypeChangeRunnables.ofCoFinish(act, attatchable, adapter, finish);
                                conditon = finish;*/
                                Intent intent = new Intent(act, AcCropColorEditor.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt(SECons.Ints.KEY_CROP_REQ_MODE, SECons.Ints.VALUE_CROP_REQ_MODE_SIMPLE_NONEXIST);
                                bundle.putString(SECons.Ints.KEY_ORIGIN_PATH, data.getIdentifyImage().getImageOriginal());
                                bundle.putParcelable(SECons.Ints.KEY_CROP_CROP_RECT, data.getIdentifyImage().getBounds());
                                intent.putExtras(bundle);
                                attatchable.attach(new SESimpleActivityLifeAttacher() {
                                    @Override
                                    public void onActivityResult(int requestCode, int resultCode, Intent intentData) {
                                        if (intentData != null && resultCode == Activity.RESULT_OK &&
                                                requestCode == SECons.Ints.REQC_CO_CON_EDIT_NONEXIST) {
                                            ArrayList<Parcelable> parcelables = intentData.getParcelableArrayListExtra("items");
                                            if (parcelables != null && parcelables.size() == 1) {
                                                if (parcelables.get(0) instanceof CropImgGroupParceble) {
                                                    CropImgGroupParceble parcebleGroup = (CropImgGroupParceble) parcelables.get(0);
                                                    AbsCoImage con = CropImgGroupParceble.conAbsCondition(parcebleGroup);
                                                    if (con != null) {
                                                        con.setNonExsitExtra(data.getIdentifyImage());
                                                        adapter.repalce(data, con);
                                                    }
                                                }
                                            }
                                        }
                                        attatchable.dettach(this);
                                    }
                                });
                                attatchable.attacherStartActivityForResult(intent, SECons.Ints.REQC_CO_CON_EDIT_NONEXIST);
                            }
                            break;
                        case 4:
                            if (!Useless.isInstance(CoFinish.class, data)) {
                                CoFinish finish = new CoFinish(data.getIdentifyImage());
                                runnable = TypeChangeRunnables.ofCoFinish(act, attatchable, adapter, finish);
                                condition = finish;
                            }
                            break;
                        default:
                    }

                    if (condition != null) {
                        //noinspection ConstantConditions
                        if (condition instanceof AbsCoImage && data instanceof AbsCoImage) {
                            ((AbsCoImage) condition).mergeBaseInfo(data);
                        }
                        adapter.repalce(data, condition);
                        //adapter.feedback(SECons.Ints.SCRIPT_NEED_SAVE);
                    }

                    dg.dismiss();
                    if (runnable != null) runnable.run();
                    if (dismiss != null) dismiss.run();
                }).create();
        dgV2Common.show();
    }

    /**
     * @param act
     * @param adapter
     * @param data
     */
    static void changeNonExsitInnerType(Activity act, ISEActivityAttatchable attatchable, BaseV2RecyclerAdapter adapter, AbsCoImage data, Runnable dismiss) {
        DgV2Common dgV2Common = DgV2Common.buildWith(act).setTitle("设置动作")
                .setTitleTips("设置执行的动作").setItems(Color.parseColor("#247EFB"), "点击图片", "偏移点击位置", "模拟滑动")
                .setOnItemClickListener((dg, adapter1, item, position1) -> {
                    AbsCoConditon condition = null;
                    Runnable runnable = null;
                    switch (position1) {
                        case 0:
                            if (!Useless.isInstance(CoClick.class, data) || Useless.isInstance(CoOffset.class, data)) {
                                condition = new CoClick(data.getIdentifyImage());
                            }
                            break;
                        case 1:
                            if (!Useless.isInstance(CoOffset.class, data)) {
                                final CoOffset offset = new CoOffset(data.getIdentifyImage());
                                runnable = TypeChangeRunnables.ofCoOffset(act, attatchable, adapter, offset);
                                condition = offset;
                            }
                            break;
                        case 2:
                            if (!Useless.isInstance(CoSlide.class, data)) {
                                CoSlide slide = new CoSlide(data.getIdentifyImage());
                                runnable = TypeChangeRunnables.ofCoSlide(act, attatchable, adapter, slide);
                                condition = slide;
                            }
                            break;
                        default:
                    }

                    if (condition != null) {
                        //noinspection ConstantConditions
                        if (condition instanceof AbsCoImage && data instanceof AbsCoImage) {
                            ((AbsCoImage) condition).setNonExsitExtra(data.getNonExsitExtraImage());
                            ((AbsCoImage) condition).mergeBaseInfo(data);
                        }
                        adapter.repalce(data, condition);
                    }

                    dg.dismiss();
                    if (runnable != null) runnable.run();
                    if (dismiss != null) dismiss.run();
                }).create();
        dgV2Common.show();
    }

    @SuppressWarnings("unused")
    private static class TypeChangeRunnables {
        /**
         * @param act
         * @param attatchable
         * @param adapter
         * @param tap
         *
         * @return
         */
        private static Runnable ofCoTap(Activity act, ISEActivityAttatchable attatchable, BaseV2RecyclerAdapter adapter, CoTap tap) {
            return () -> {
                DgV3ONActionRegionClick dgV3ONActionRegionClick = new DgV3ONActionRegionClick(act);
                dgV3ONActionRegionClick.setAttatchable(attatchable);
                dgV3ONActionRegionClick.init(act, tap.getMainSEAction(), (dg1, r, key, dat) -> {
                    tap.setMainSEAction(dat);
                    tap.setTapRect(dat.getBounds());
                    dg1.dismiss();
                    adapter.feedback(SECons.Ints.SCRIPT_NEED_SAVE);
                    adapter.notifyDataSetChanged();
                });
                dgV3ONActionRegionClick.show();
            };
        }

        /**
         * @param act
         * @param attatchable
         * @param adapter
         * @param offset
         *
         * @return
         */
        private static Runnable ofCoOffset(Activity act, ISEActivityAttatchable attatchable, BaseV2RecyclerAdapter adapter, CoOffset offset) {
            return () -> {
                DgV3ONActionOffset dgOffset = new DgV3ONActionOffset(act);
                dgOffset.setAttatchable(attatchable);
                dgOffset.init(act, offset.getMainSEAction(), (dg, r, key, dat) -> {
                    offset.setOffset(dat.getShift_x(), dat.getShift_y());
                    offset.setMainSEAction(dat);
                    adapter.feedback(SECons.Ints.SCRIPT_NEED_SAVE);
                    adapter.notifyDataSetChanged();
                    dg.dismiss();
                });
                dgOffset.show();
            };
        }

        /**
         * @param act
         * @param attatchable
         * @param adapter
         * @param slide
         *
         * @return
         */
        private static Runnable ofCoSlide(Activity act, ISEActivityAttatchable attatchable, BaseV2RecyclerAdapter adapter, CoSlide slide) {
            return () -> {
                final DgV3ONActionTouchMove clickDg = new DgV3ONActionTouchMove(act);
                clickDg.setAttatchable(attatchable);
                clickDg.init(act, slide.getIdentifyImage().getBounds(), slide.getMainSEAction(), (dgActionImg, r, key, data1) -> {
                            slide.setMainSEAction(data1);
                            adapter.feedback(SECons.Ints.SCRIPT_NEED_SAVE);
                            adapter.notifyDataSetChanged();
                        }
                );
                clickDg.show();
            };
        }

        /**
         * @param act
         * @param attatchable
         * @param adapter
         * @param slide
         *
         * @return
         */
        private static Runnable ofCoFinish(Activity act, ISEActivityAttatchable attatchable, BaseV2RecyclerAdapter adapter, CoFinish slide) {
            return () -> {
                /*final DgV3ONActionTouchMove clickDg = new DgV3ONActionTouchMove(act);
                clickDg.setAttatchable(attatchable);
                clickDg.onComponentInit(act, slide.getIdentifyImage().getBounds(), slide.getSEAction(), (dgActionImg, r, key, data1) -> {
                            slide.setSEAction(data1);
                            adapter.feedback(SECons.Ints.SCRIPT_NEED_SAVE);
                            adapter.notifyDataSetChanged();
                        }
                );
                clickDg.show();*/
            };
        }

    }

}
