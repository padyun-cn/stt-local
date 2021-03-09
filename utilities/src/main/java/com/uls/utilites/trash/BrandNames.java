package com.uls.utilites.trash;

import android.os.Build;

import androidx.core.util.Consumer;

/**
 * Created by daiepngfei on 2020-06-09
 */
public enum BrandNames {
    HUAWEI("huawei"),
    MEIZU("meizu"),
    XIAOMI("xiaomi"),
    OPPO("oppo"),
    VIVO("vivo"),
    SAMSUNG("samsung"),
    SMARTISAN("smartisan"),
    LG("lg"),
    LETV("letv"),
    ZTE("zte"),
    YULONG("yulong"),
    LENOVO("lenovo"),
    SONY("sony"),
    UNKOWN("");
    private String name;

    BrandNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean isBrandOf(BrandNames brandName) {
        return Build.MANUFACTURER.toLowerCase().contains(brandName.name);
    }

    public static void onBrandName(Consumer<BrandNames> brandNameConsumer) {
        if (brandNameConsumer != null) {
            final String m = Build.MANUFACTURER.toLowerCase();
            for (BrandNames name : BrandNames.values()) {
                if (m.contains(name.name)) {
                    brandNameConsumer.accept(name);
                    break;
                }
            }

        }
    }
}
