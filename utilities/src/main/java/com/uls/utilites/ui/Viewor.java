package com.uls.utilites.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.uls.utilites.un.Useless;


/**
 * Created by daiepngfei on 2/17/19
 */
public class Viewor {

    public static void gone(View... views){
        setVisibility(View.GONE,views);
    }

    public static void visible(View... views){
        setVisibility(View.VISIBLE,views);
    }

    public static void toggleGone(View... views){
        if(views == null) return;
        for (View v : views) {
            if(v != null){
                v.setVisibility(v.getVisibility() != View.GONE ? View.GONE : View.VISIBLE);
            }
        }
    }

    public static void invisible(View... views){
        setVisibility(View.INVISIBLE,views);
    }

    public static void setText(View textView, String text){
        if(textView instanceof TextView){
            ((TextView)textView).setText(text);
        }
    }

    private static void setVisibility(int visibility, View... views){
        Useless.foreach(views, t -> t.setVisibility(visibility));
    }

    public static int getStatusBarHeight(Activity act){
        Rect rectangle = new Rect();
        Window window = act.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        return titleBarHeight;

    }

    public static int getStatusBarHeight2(Activity act) {
        int result = 0;
        int resourceId = act.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = act.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
