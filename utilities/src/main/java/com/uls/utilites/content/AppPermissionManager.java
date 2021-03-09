package com.uls.utilites.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by daiepngfei on 1/29/19
 */
public class AppPermissionManager {

    public static final int PERMISSION_REQUEST_CODE = 11010;
    private static final int PERMISSION_SETTING_REQUEST_CODE = 11011;
    public static class Permission {
        private String permission;
        private boolean ignored;

        Permission(String p) {
            this(p, false);
        }

        Permission(String p, boolean ig) {
            permission = p;
            ignored = ig;
        }

        public String getPermission() {
            return permission;
        }

        public boolean isIgnored() {
            return ignored;
        }
    }


    public static class Permissions {
        public static final Permission[] ALT = new Permission[]
                {
                        new Permission(android.Manifest.permission.READ_PHONE_STATE, true),
                        new Permission(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        new Permission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                };
    }

    private Activity act;
    private Runnable resultCallback;
    private Runnable runCallback;
    public AppPermissionManager(Activity activity, Runnable onResult, Runnable run){
        this.act = activity;
        this.resultCallback = onResult;
        this.runCallback = run;
    }


    public boolean requestPerminssions() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            if(runCallback != null) runCallback.run();
            return false;
        }
        final List<String> permissionsList = new ArrayList<>();
        final Permission[] permissions = Permissions.ALT;
        final int grant = PackageManager.PERMISSION_GRANTED;
        for (Permission permission : permissions) {
            final String permissionName = permission.getPermission();
            if (ContextCompat.checkSelfPermission(act, permissionName) != grant) {
                permissionsList.add(permissionName);
            }
        }
        final boolean notEmpty = permissionsList.size() != 0;
        if (notEmpty) {
            ActivityCompat.requestPermissions(act, permissionsList.toArray(new String[permissionsList.size()]), PERMISSION_REQUEST_CODE);
        }
        return notEmpty;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_SETTING_REQUEST_CODE) {
            if (resultCallback != null) resultCallback.run();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean denied = false;
            for (int i : grantResults) {
                if (i == PackageManager.PERMISSION_DENIED) {
                    denied = true;
                    break;
                }
            }

            boolean start = true;
            if (denied) {
                SharedPreferences sp = act.getPreferences(Context.MODE_PRIVATE);
                final boolean permissionDenied = sp.getBoolean("PERMISSION_" + permissions[0], false);
                if (!permissionDenied) {
                    start = false;
                    requestPerminssions();
                    sp.edit().putBoolean("PERMISSION_" + permissions[0], true).apply();
                } else {
                    boolean needToast = false;
                    final Permission[] thePermissions = Permissions.ALT;
                    final int grant = PackageManager.PERMISSION_GRANTED;
                    for (Permission permission : thePermissions) {
                        final String permissionName = permission.getPermission();
                        if (ContextCompat.checkSelfPermission(act, permissionName) != grant && !permission.isIgnored()) {
                            needToast = true;
                            break;
                        }
                    }
                    if (needToast) {
                        start = false;
                        // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
                        new AlertDialog.Builder(act)
                                .setTitle("提示")
                                .setMessage("尊敬的用户，应用需要通过权限确认，否则将无法正常运行。")
                                .setPositiveButton("去设置", (dialog, which) -> {
                                    Intent intent = new Intent()
                                            .setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
                                            .setData(Uri.fromParts("package", act.getPackageName(), null));
                                    act.startActivityForResult(intent, PERMISSION_SETTING_REQUEST_CODE);
                                })
                                .setNegativeButton("取消", (dialog, which) -> {

                                }).show();
                    }
                }
            }
            if (start && runCallback != null) runCallback.run();
        }
    }
}
