package com.thegalaxysoftware.musica.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Nammu {

    private static final String TAG = Nammu.class.getSimpleName();
    private static final String KEY_PREV_PERMISSIONS = "previous_permissions";
    private static final String KEY_IGNORED_PERMISSIONS = "ignored_permissions";
    private static Context context;
    private static SharedPreferences sharedPreferences;
    private static ArrayList<PermissionRequest> permissionRequests = new ArrayList<PermissionRequest>();

    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences("pl.tajchert.runtimepermissionhelper", Context.MODE_PRIVATE);
        Nammu.context = context;
    }

    public static boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean hasPermission(Activity activity, String permission) {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean hasPermission(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permissions) {
        return activity.shouldShowRequestPermissionRationale(permissions);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void askForPermission(Activity activity, String permission, PermissionCallback permissionCallback) {
        askForPermission(activity, new String[]{permission}, permissionCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void askForPermission(Activity activity, String[] permissions, PermissionCallback permissionCallback) {
        if (permissionCallback == null) {
            return;
        }
        if (hasPermission(activity, permissions)) {
            permissionCallback.permissionGranted();
            return;
        }

        PermissionRequest permissionRequest = new PermissionRequest(new ArrayList<String>(Arrays.asList(permissions)), permissionCallback);
        permissionRequests.add(permissionRequest);
        activity.requestPermissions(permissions, permissionRequest.getRequestCode());

    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        PermissionRequest requestResult = new PermissionRequest(requestCode);

        if(permissionRequests.contains(requestResult))
        {
            PermissionRequest permissionRequest = permissionRequests.get(permissionRequests.indexOf(requestResult));
            if(verifyPermissions(grantResults))
            {
                permissionRequest.getPermissionCallback().permissionGranted();
            }
            else
            {
                permissionRequest.getPermissionCallback().permissionRefused();
            }
            permissionRequests.remove(requestResult);
        }

        refreshMonitoredList();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static ArrayList<String> getGrantedPermissions()
    {
        if(context == null)
        {
           throw new RuntimeException("Must call init() earlier");
        }

        ArrayList<String> permissions = new ArrayList<String>();
        ArrayList<String> permissionsGranted = new ArrayList<String>();

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissions.add(Manifest.permission.WRITE_CALENDAR);
        permissions.add(Manifest.permission.READ_CALENDAR);

        permissions.add(Manifest.permission.CAMERA);

        permissions.add(Manifest.permission.WRITE_CONTACTS);
        permissions.add(Manifest.permission.READ_CONTACTS);
        permissions.add(Manifest.permission.GET_ACCOUNTS);

        permissions.add(Manifest.permission.RECORD_AUDIO);

        permissions.add(Manifest.permission.CALL_PHONE);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.ADD_VOICEMAIL);
        permissions.add(Manifest.permission.USE_SIP);
        permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);

        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.RECEIVE_WAP_PUSH);
        permissions.add(Manifest.permission.RECEIVE_MMS);

        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        for(String permission : permissions)
        {
            if(context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            {
                permissionsGranted.add(permission);
            }
        }

        return permissionsGranted;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void refreshMonitoredList() {
        ArrayList<String> permissions = getGrantedPermissions();
        Set<String> set = new HashSet<String>();
        for (String perm : permissions) {
            set.add(perm);
        }
        sharedPreferences.edit().putStringSet(KEY_PREV_PERMISSIONS, set).apply();
    }

    public static ArrayList<String> getPreviousPermissions() {
        ArrayList<String> prevPermissions = new ArrayList<String>();
        prevPermissions.addAll(sharedPreferences.getStringSet(KEY_PREV_PERMISSIONS, new HashSet<String>()));
        return prevPermissions;
    }

    public static ArrayList<String> getIgnoredPermissions() {
        ArrayList<String> ignoredPermissions = new ArrayList<String>();
        ignoredPermissions.addAll(sharedPreferences.getStringSet(KEY_IGNORED_PERMISSIONS, new HashSet<String>()));
        return ignoredPermissions;
    }

    public static boolean isIgnoredPermission(String permission) {
        if (permission == null) {
            return false;
        }
        return getIgnoredPermissions().contains(permission);
    }

    public static void ignorePermission(String permission) {
        if (!isIgnoredPermission(permission)) {
            ArrayList<String> ignoredPermissions = getIgnoredPermissions();
            ignoredPermissions.add(permission);
            Set<String> set = new HashSet<String>();
            set.addAll(ignoredPermissions);
            sharedPreferences.edit().putStringSet(KEY_IGNORED_PERMISSIONS, set).apply();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void permissionCompare(PermissionListener permissionListener) {
        if (context == null) {
            throw new RuntimeException("Before comparing permissions you need to call Nammu.init(context)");

        }
        ArrayList<String> previouslyGranted = getPreviousPermissions();
        ArrayList<String> currentPermissions = getGrantedPermissions();
        ArrayList<String> ignoredPermissions = getIgnoredPermissions();
        for (String permission : ignoredPermissions) {
            if (previouslyGranted != null && !previouslyGranted.isEmpty()) {
                if (previouslyGranted.contains(permission)) {
                    previouslyGranted.remove(permission);
                }
            }

            if (currentPermissions != null && !currentPermissions.isEmpty()) {
                if (currentPermissions.contains(permission)) {
                    currentPermissions.remove(permission);
                }
            }
        }
        for (String permission : currentPermissions) {
            if (previouslyGranted.contains(permission)) {
                //All is fine, was granted and still is
                previouslyGranted.remove(permission);
            } else {
                //We didn't have it last time
                if (permissionListener != null) {
                    permissionListener.permissionsChanged(permission);
                    permissionListener.permissionsGranted(permission);
                }
            }
        }
        if (previouslyGranted != null && !previouslyGranted.isEmpty()) {
            //Something was granted and removed
            for (String permission : previouslyGranted) {
                if (permissionListener != null) {
                    permissionListener.permissionsChanged(permission);
                    permissionListener.permissionsRemoved(permission);
                }
            }
        }
        refreshMonitoredList();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermission(String permissionName) {
        if (context == null) {
            throw new RuntimeException("Before comparing permissions you need to call Nammu.init(context)");
        }
        return PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(permissionName);
    }

}
