package net.getquicker.hook;

import android.Manifest;
import android.os.Build;

import net.getquicker.BuildConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Permission Constants
 */
public class PermConst {

    public final static Map<String, List<String>> PACKAGE_PERMISSIONS;

    static {
        PACKAGE_PERMISSIONS = new HashMap<>();

        List<String> smsCodePermissions = new ArrayList<>();

        // Backup import or export
//        smsCodePermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        smsCodePermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        smsCodePermissions.add("android.permission.INJECT_EVENTS");

        String smsCodePackage = BuildConfig.APPLICATION_ID;
        PACKAGE_PERMISSIONS.put(smsCodePackage, smsCodePermissions);
    }

}
