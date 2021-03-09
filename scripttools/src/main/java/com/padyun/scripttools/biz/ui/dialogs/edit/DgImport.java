package com.padyun.scripttools.biz.ui.dialogs.edit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mon.ui.buildup.CvImageCheckbox;
import com.mon.ui.buildup.CvViewPager;
import com.padyun.scripttools.R;
import com.padyun.scripttools.biz.ui.dialogs.DgV2ScriptBase;
import com.padyun.scripttools.biz.ui.fragment.ViewImportDetailList;
import com.padyun.scripttools.biz.ui.fragment.ViewImportMenuList;
import com.padyun.scripttools.compat.data.AbsCoConditon;
import com.padyun.scripttools.compat.data.AbsCoImage;
import com.padyun.scripttools.compat.data.CoClick;
import com.padyun.scripttools.compat.data.CoFinish;
import com.padyun.scripttools.compat.data.CoOffset;
import com.padyun.scripttools.compat.data.CoSlide;
import com.padyun.scripttoolscore.compatible.data.model.SEImage;
import com.uls.utilites.content.ToastUtils;
import com.uls.utilites.io.Files;
import com.uls.utilites.un.Useless;
import com.uls.utilites.utils.Md5Util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by daiepngfei on 2019-12-30
 */
public class DgImport extends DgV2ScriptBase {

    private String uid, gameId;
    private Activity activity;

    public DgImport(@NonNull Activity context) {
        super(context);
        activity = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dg_script_import;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NotNull
    public DgImport init(@NotNull String uid, @NotNull String gameId, final String currentPath, Consumer<List<? extends AbsCoConditon>> consumer) {
        this.uid = uid;
        this.gameId = gameId;

        final CvViewPager pager = findViewById(R.id.pager);
        pager.setTouchDisabled(true);
        final ViewImportMenuList pageMenu = new ViewImportMenuList(activity, uid, gameId, currentPath);
        final ViewImportDetailList pageDetail = new ViewImportDetailList(activity, uid, gameId);
        final View[] pages = new View[]{pageMenu, pageDetail};
        final TextView tab2Title = findViewById(R.id.tab2Title);

        final View tab2BackButton = findViewById(R.id.btnBack);
        tab2BackButton.setOnClickListener(v -> setCurrentItem(pager, 0));
        final CvImageCheckbox tab2CheckBox = findViewById(R.id.cbSelectAll);
        tab2CheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> pageDetail.setCheckedAll(isChecked));

        pageMenu.setOnScriptItemClickListener((adapter, item, position) -> {
            setCurrentItem(pager, 1);
            pageDetail.load(item.getPath());
            tab2Title.setText(item.getName());
        });

        pageDetail.setCallback(msg -> {
            if (msg != null && msg.what == ViewImportDetailList.ON_CHECKED_CHANGED) {
                tab2CheckBox.setCheckWithoutNotifing(pageDetail.getCheckAllState());
                return true;
            }
            return false;
        });

        pageDetail.setOnItemSelectedListener(items -> {
            if (consumer != null && !Useless.isEmpty(items)) {
                consumer.accept(items);
                dismiss();
            } else {
                ToastUtils.show(activity, "选项为空");
            }
        });

        pager.setAdapter(new PagerAdapter() {

            @Override
            public int getCount() {
                return pages.length;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(pages[position]);
                return pages[position];
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(pages[position]);
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

        });
        return this;
    }

    private void showLoading() {
        findViewById(R.id.loadingView).setVisibility(View.VISIBLE);
    }

    private void dismissLoading() {
        findViewById(R.id.loadingView).setVisibility(View.INVISIBLE);
    }

    private void setCurrentItem(ViewPager pager, int index) {
        findViewById(R.id.tab2).setVisibility(index == 0 ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.tab1).setVisibility(index == 0 ? View.VISIBLE : View.INVISIBLE);
        pager.setCurrentItem(index, true);
    }

    @SuppressWarnings("RedundantClassCall")
    public static class ImportFactory {

        public static List<? extends AbsCoConditon> newCopiedConditions(List<? extends AbsCoConditon> conditons) {
            final List<AbsCoConditon> r = new ArrayList<>();
            Useless.foreach(conditons, t -> {
                if (CoFinish.class.isInstance(t)) {
                    r.add(newFinish((CoFinish) t));
                } else if (CoOffset.class.isInstance(t)) {
                    r.add(newOffset((CoOffset) t));
                } else if (CoClick.class.isInstance(t)) {
                    r.add(newClick((CoClick) t));
                } else if (CoSlide.class.isInstance(t)) {
                    r.add(newSlide((CoSlide) t));
                }
            });
            return r;
        }

        static CoFinish newFinish(CoFinish finish) {
            if (finish == null) return null;
            CoFinish r = new CoFinish(getSEImageWithCropFile(finish.getIdentifyImage()));
            copyCoConditionInfo(finish, r);
            return r;
        }

        static CoOffset newOffset(CoOffset offset) {
            if (offset == null) return null;
            CoOffset r = new CoOffset(getSEImageWithCropFile(offset.getIdentifyImage()));
            copyCoConditionInfo(offset, r);
            r.setNonExsitExtra(getNonExsitImageFromAbsCoImage(offset));
            r.setOffset(offset.offsetX(), offset.offsetY());
            return r;
        }

        static CoSlide newSlide(CoSlide slide) {
            if (slide == null) return null;
            CoSlide r = new CoSlide(getSEImageWithCropFile(slide.getIdentifyImage()));
            copyCoConditionInfo(slide, r);
            r.setNonExsitExtra(getNonExsitImageFromAbsCoImage(slide));
            r.setSlide(slide.getCo_start(), slide.getCo_end());
            return r;
        }

        static CoClick newClick(CoClick click) {
            if (click == null) return null;
            CoClick newClick = new CoClick(getSEImageWithCropFile(click.getIdentifyImage()));
            copyCoConditionInfo(click, newClick);
            newClick.setNonExsitExtra(getNonExsitImageFromAbsCoImage(click));
            newClick.setCo_range(click.getCo_range());
            return newClick;
        }

        private static SEImage getSEImageWithCropFile(SEImage image) {
            if (image == null) return null;
            SEImage nImage = image.seClone(true);
            nImage.newId();
            if (Files.exists(nImage.getImageCropPath())) {
                File f = new File(nImage.getImageCropPath());
                if(f.exists()){

                    //final String path = Files.replaceNameExcludeExt(f,  Md5Util.file(f));
                    final String path = f.getParent() + Md5Util.file(f) + Files.ext(f.getAbsolutePath());
                    if(path != null) {
                        final File destFile = new File(path);
                        if (!destFile.exists()) {
                            Files.copy(new File(nImage.getImageCropPath()), destFile);
                        }
                    }
                }
                return nImage;
            } else {
                return null;
            }

        }

        private static SEImage getNonExsitImageFromAbsCoImage(AbsCoImage image) {
            if (image == null || !image.hasNonExist()) {
                return null;
            }
            SEImage nImage = image.getNonExsitExtraImage().seClone(true);
            nImage.newId();
            if (Files.exists(nImage.getImageCropPath())) {
                File f = new File(nImage.getImageCropPath());
                if(f.exists()){
//                    final String path = Files.replaceNameExcludeExt(f,  Md5Util.file(f));
                    final String path = f.getParent() + Md5Util.file(f) + Files.ext(f.getAbsolutePath());
                    if(path != null) {
                        final File destFile = new File(path);
                        if (!destFile.exists()) {
                            Files.copy(new File(nImage.getImageCropPath()), destFile);
                        }
                    }
                }
                return nImage;
            } else {
                return null;
            }

        }

        private static void copyCoConditionInfo(AbsCoConditon from, AbsCoConditon to) {
            if (from != null && to != null) {
                to.setCo_timeout(from.getCo_timeout());
                to.setCo_brain_count(from.getCo_brain_count());
                to.setCo_isDefaultTimeout(from.isCo_isDefaultTimeout());
                if (from instanceof AbsCoImage && to instanceof AbsCoImage) {
                    ((AbsCoImage) to).getMainSEItem().setTimeout(((AbsCoImage) from).getMainSEItem().getTimeout());
                    ((AbsCoImage) to).getMainSEItem().setState(((AbsCoImage) from).getMainSEItem().getState());
                }
                to.setDisabled(from.isDisabled());
            }
        }
    }

}
