package com.padyun.scripttoolscore.compatible.data.model;

import android.os.Bundle;

/**
 * Created by daiepngfei on 1/21/19
 */
public interface SpanEvent {
   int ADD_SCENE                          = 0x0000;
   int ADD_CONDITION                      = 0x0001;
   int ADD_EVENT                          = 0x0002;
   int CONDITION_MENU                     = 0x0003;
   int CONDITION_MENU_NEW_IMG             = 0x0004;
   int CONDITION_MENU_EDIT_IMG            = 0x0005;
   int CONDITION_MENU_NEW_COLOR           = 0x0006;
   int CONDITION_MENU_EDIT_COLOR          = 0x0007;
   int CONDITION_MENU_NEW_JUDGE           = 0x0008;
   int CONDITION_MENU_EDIT_JUDGE          = 0x0009;
   int CONDITION_MENU_NEW_COUNTER         = 0x000A;
   int CONDITION_MENU_EDIT_COUNTER        = 0x000B;
   int CONDITION_MENU_NEW_TIMER           = 0x000C;
   int CONDITION_MENU_EDIT_TIMER          = 0x000D;
   int CONDITION_MENU_NEW_ALARM           = 0x000E;
   int CONDITION_MENU_EDIT_ALARM          = 0x000F;
   int CONDITION_MENU_NEW_USER            = 0x0010;
   int CONDITION_MENU_EDIT_USER           = 0x0011;
   int CONDITION_AND_OR                   = 0x0017;
   int CONDITION_BOOL                     = 0x0018;
   int EVENT_MENU                         = 0x0019;
   int EVENT_MENU_NEW_JUDGE               = 0x001A;
   int EVENT_MENU_JUDGE_LIST              = 0x001B;
   int EVENT_EDIT_JUDGE                   = 0x001C;
   int EVENT_MENU_NEW_COUNTER             = 0x001D;
   int EVENT_MENU_COUNTER_LIST            = 0x001E;
   int EVENT_EDIT_COUNTER                 = 0x001F;
   int EVENT_MENU_NEW_TIMER               = 0x0020;
   int EVENT_MENU_TIMER_LIST              = 0x0021;
   int EVENT_EDIT_TIMER                   = 0x0022;
   int EVENT_NEW_TAP_IMAGE                = 0x0023;
   int EVENT_EDIT_TAP_IMAGE_DIALOG        = 0x0024;
   int EVENT_EDIT_TAP_IMAGE_DETAIL        = 0x0025;
   int EVENT_NEW_SLIDE                    = 0x0026;
   int EVENT_EDIT_SLIDE                   = 0x0027;
   int EVENT_NEW_TAP                      = 0x0028;
   int EVENT_EDIT_TAP                     = 0x0029;
   int EVENT_EDIT_FINISH                  = 0x002A;
   int EVENT_NEW_INPUT                    = 0x002B;
   int EVENT_EDIT_INPUT                   = 0x002C;
   int OP_SAVE_CROP_SCREEN_IMG            = 0x0070;
   int OP_UI_ITEM_CONDITION               = 0x0071; // 条件上下移动-删除
   int OP_UI_ITEM_EVENT                   = 0x0072; // 事件上下移动-删除
   int OP_UI_ITEM_CONDITION_EDIT          = 0x0073; // 条件上下移动-删除
   int OP_UI_ITEM_EVENT_EDIT              = 0x0074; // 事件上下移动-删除
   int OP_UI_ITEM_SCENE                   = 0x0075; // 条件上下移动-删除
   int OP_UI_ITEM_DEL_REQUEST             = 0x0076; // 条件上下移动-删除
   int OTHER_REFRESH_UI                   = 0x0100; // 条件上下移动-删除
   int OTHER_NEW_NAME                     = 0x0101; // 条件上下移动-删除

    void onSpanEvent(int event, Bundle data, Callback callback);

   interface Callback {
      void callback(Bundle bundle);
   }
}
