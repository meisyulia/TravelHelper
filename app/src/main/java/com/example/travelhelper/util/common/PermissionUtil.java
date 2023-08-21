package com.example.travelhelper.util.common;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {
    private final static String TAG = "PermissionUtil";

    // 检查某个权限。返回true表示已启用该权限，返回false表示未启用该权限
    public static boolean checkPermission(Activity act, String permission, int requestCode) {
        Log.d(TAG, "checkPermission: "+permission);
        boolean result = true;
        // 只对Android6.0及以上系统进行校验
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查当前App是否开启了名称为permission的权限
            int check = ContextCompat.checkSelfPermission(act, permission);
            if (check != PackageManager.PERMISSION_GRANTED) {
                // 未开启该权限，则请求系统弹窗，好让用户选择是否立即开启权限
                ActivityCompat.requestPermissions(act, new String[]{permission}, requestCode);
                result = false;
            }
        }
        return result;
    }

    // 检查多个权限。返回true表示已完全启用权限，返回false表示未完全启用权限
    public static boolean checkMultiPermission(Activity act, String[] permissions, int requestCode) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = PackageManager.PERMISSION_GRANTED;
            // 通过权限数组检查是否都开启了这些权限
            for (String permission : permissions) {
                check = ContextCompat.checkSelfPermission(act, permission);
                if (check != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
            if (check != PackageManager.PERMISSION_GRANTED) {
                // 未开启该权限，则请求系统弹窗，好让用户选择是否立即开启权限
                ActivityCompat.requestPermissions(act, permissions, requestCode);
                result = false;
            }
        }
        return result;
    }

    // 检查是否允许修改系统设置
    public static boolean checkWriteSettings(Activity act, int requestCode) {
        Log.d(TAG, "checkWriteSettings:");
        boolean result = true;
        // 只对Android6.0及Android7.0系统进行校验
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // 检查当前App是否允许修改系统设置
            if(!Settings.System.canWrite(act)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + act.getPackageName()));
                act.startActivityForResult(intent, requestCode);
                Toast.makeText(act, "需要允许设置权限才能调节亮度噢", Toast.LENGTH_SHORT).show();
                result = false;
            }
        }
        return result;
    }

    public static void goActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        ctx.startActivity(intent);
    }

    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    public static void checkPermissions(Activity act,String... permissions) {
        try{
            if (Build.VERSION.SDK_INT >= 23 && act.getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(act,permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = act.getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
                        method.invoke(act, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private static List<String> findDeniedPermissions(Activity act,String[] permissions) {
        try{
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && act.getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(act,perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(act,perm)) {
                        /*if(!needCheckBackLocation
                                && BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }*/
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }

    private static int checkMySelfPermission(Activity act,String perm) {
        try {
            Method method = act.getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(act, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private static boolean shouldShowMyRequestPermissionRationale(Activity act,String perm) {
        try {
            Method method = act.getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(act, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try{
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
        return true;
    }

}
