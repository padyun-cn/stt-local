package com.mon.ui.floating;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import java.util.Objects;

/**
 * Created by daiepngfei on 2021-01-11
 */
public class SystemWindowManager {

    public static boolean getRequiredPermmision(Context context) {
        Objects.requireNonNull(context);
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    /**
     * @param context
     */
    public static void goRequestOverlayPermisson(Context context) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
        }
        if (intent != null) {
            if (context instanceof Service) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }

    public static class Current {

        public static String printShortString(Context context) {
            StringBuilder sb = new StringBuilder("Window#Current# {");
            sb.append("isLand: ");
            sb.append(isLandscape(context));
            sb.append("; ");
            sb.append("rotation: ");
            final int rotation = getOrientation(context);
            switch (rotation) {
                case Surface.ROTATION_0:
                    //竖屏
                    sb.append("0");
                    break;
                case Surface.ROTATION_90:
                    //顺时针旋转90度
                    sb.append("90");
                    break;
                case Surface.ROTATION_180:
                    //顺时针旋转180度
                    sb.append("180");
                    break;
                case Surface.ROTATION_270:
                    //顺时针旋转270度
                    sb.append("270");
                    break;
                default:
                    break;
            }
            sb.append("; ");
            sb.append("width: ").append(context.getResources().getDisplayMetrics().widthPixels);
            sb.append(", ");
            sb.append("height: ").append(context.getResources().getDisplayMetrics().heightPixels);
            sb.append("}");
            return sb.toString();
        }

        public static int getOrientation(Context context) {
            final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            Log.d("OverlayManager", "onSreenOrientationConfigChange getRotation = " + rotation);
            return rotation;
        }

        public static boolean isLandscape(Context context) {
            final int rotation = getOrientation(context);
            /*final boolean isDefaultDisplay_PORTRAIT = context.getResources().getDisplayMetrics().widthPixels < context.getResources().getDisplayMetrics().heightPixels;
            printShortString(context);
            return isDefaultDisplay_PORTRAIT ?
                    rotation == Surface.ROTATION_90 | rotation == Surface.ROTATION_270 :
                    rotation == Surface.ROTATION_0 | rotation == Surface.ROTATION_180 ;*/

            final int scrW = context.getResources().getDisplayMetrics().widthPixels;
            final int scrH = context.getResources().getDisplayMetrics().heightPixels;
            // w < h && （90 ｜ 270）
            final boolean isDefaultPortrait_Land = (rotation == Surface.ROTATION_90 | rotation == Surface.ROTATION_270) && scrW < scrH;

            // w > h && （0 ｜ 180）
            final boolean isDefaultLand_Land = (rotation == Surface.ROTATION_0 | rotation == Surface.ROTATION_180) && scrW > scrH;


            // return isDefaultPortrait_Land || isDefaultLand_Land;
            return scrW > scrH;
        }

    }
}
