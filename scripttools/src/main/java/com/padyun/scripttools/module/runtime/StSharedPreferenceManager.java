package com.padyun.scripttools.module.runtime;

import android.content.Context;
import android.content.SharedPreferences;

import com.uls.utilites.un.Useless;

/**
 * Created by daiepngfei on 2020-06-23
 */
public class StSharedPreferenceManager {
    private StContext stContext;
    private SpBehavior behavior;

    private static final String SPNAME = StSharedPreferenceManager.class.getSimpleName();

    private static String getSpName(String userId, String type) {
        return Useless.sUnion("_", SPNAME, userId, type);
    }

    StSharedPreferenceManager(StContext stContext) {
        this.stContext = stContext;
    }

    public SpBehavior getStBehavior(Context context, String userId) {
        return context == null || Useless.isEmpty(userId) ? null : new SpBehavior(context, userId);
    }

    public class SpBehavior {
        private SharedPreferences sp;

        private static final String LAST_SUBSCRIPTION_CODE = "lastSubscriptionCode";
        public SpBehavior(Context context, String userId) {
            sp = context.getSharedPreferences(getSpName(userId, SpBehavior.class.getSimpleName()), Context.MODE_PRIVATE);
        }

        public boolean equalsLastSubscriptionCode(String currentCode) {
            final String lastCode = sp.getString(LAST_SUBSCRIPTION_CODE, "");
            return !lastCode.isEmpty() && lastCode.equals(currentCode);
        }

        public void setLastSubscriptionCode(String subscriptionCode){
            sp.edit().putString(LAST_SUBSCRIPTION_CODE, subscriptionCode).apply();
        }
    }
}
