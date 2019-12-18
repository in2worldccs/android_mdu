package com.ibrahem.ccs.helper;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.ibrahem.ccs.R;


/**
 * Created by ibrahem on 10/20/2018.
 */
public class PermissionHelper {



    // to handel permissiom Code

    public static final int CODE_PERMISSION_READ_PHONE_STATE = 11;
    public static final int CODE_PERMISSION_CAMERA =22;
    public static final int CODE_PERMISSION_RECORD_AUDIO = 33;
    public static final int CODE_PERMISSION_READ_SMS = 44;
    public static final int CODE_PERMISSION_READ_CALL_LOG = 55;
    public final static int CODE_OVERLAY_PERMISSION = 66;
    public final static int CODE_UsageStats_PERMISSION = 77;


    // to handel permissiom v6
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_READ_SMS = Manifest.permission.READ_SMS;
    public static final String PERMISSION_READ_CALL_LOG = Manifest.permission.READ_CALL_LOG;

    public static final String[] PERMISSIONS_LIST = {
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CAMERA,
            PERMISSION_RECORD_AUDIO,
            PERMISSION_READ_CALL_LOG
    };
    public static final String[] PERMISSIONS_LIST_AUDIO = {
            PERMISSION_RECORD_AUDIO
    };

    public static boolean checkAllPermission(Activity context, String[] permissions, final int requestCode) {
        boolean allPermissionGranted = true;
        for (int i = 0; i < permissions.length; i++) {
            if (isPermissionGranted(context, permissions[i])) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, permissions[i])) {
                    // Toast.makeText(context, "I need this permission:"+permissions[i], Toast.LENGTH_SHORT).show();
                }
                allPermissionGranted = false;
            }
        }
        if (!allPermissionGranted) {
            ActivityCompat.requestPermissions(context, permissions, requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkPermission(final Activity context, final String permission, final int requestCode, String tite, String body) {
        if (isPermissionGranted(context, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(tite);
                alertDialog.setMessage(body);
                alertDialog.setPositiveButton(context.getResources().getString(R.string.allow), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(context, "I need this permission", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
                        dialog.dismiss();
                    }
                });

                alertDialog.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert11 = alertDialog.create();
                alert11.show();
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPermissionGranted(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkDrawOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context))
            return false;
        else return true;
    }
    public static void drawOverlayPermission(Context context, int REQUEST_CODE) {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            /** request permission via start activity for result */
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isAccessUsageStats(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (ValidationHelper.validObject(appOpsManager)) return false;
            int mode = 0;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            if (mode != AppOpsManager.MODE_ALLOWED)
                return true;
             else return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(((Activity)context).getLocalClassName(), " isAccessUsageStats: "+e.getMessage());
            return false;

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void accessUsageStats(Context context)  {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (ValidationHelper.validObject(appOpsManager)) return;
            int mode = 0;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            if (mode != AppOpsManager.MODE_ALLOWED) {
                Log.d(((Activity)context).getLocalClassName(), "accessUsageStats: !!!!!!USAGE STATS");

                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                ((Activity)context).startActivityForResult(intent,CODE_UsageStats_PERMISSION);
            }

        } catch (PackageManager.NameNotFoundException e) {

            Log.d(((Activity)context).getLocalClassName(), " accessUsageStats: "+e.getMessage());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean checkUsageStatsAndOverlayPermission(Context context){
        return PermissionHelper.checkDrawOverlayPermission(context) || PermissionHelper.isAccessUsageStats(context);
    }

}
