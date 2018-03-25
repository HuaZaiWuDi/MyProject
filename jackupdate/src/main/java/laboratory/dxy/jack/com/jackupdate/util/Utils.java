package laboratory.dxy.jack.com.jackupdate.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.Hashtable;
import java.util.List;

import laboratory.dxy.jack.com.jackupdate.ui.RxToast;

/**
 * 项目名称：TextureViewDome
 * 类描述：
 * 创建人：oden
 * 创建时间：2017/7/18
 */
public class Utils {


    public Utils() {
        throw new RuntimeException("cannot be instantiated");
    }

    /**
     * 得到Bulid.gradil的版本名称
     *
     * @param context 上下文
     * @return String
     */
    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionName;
        }
        return "";
    }

    /**
     * 得到Bulid.gradil的版本号
     *
     * @param context 上下文
     * @return int
     */
    public static int getVersionCode(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 获取设备信息:手机型号 Build.DEVICE,系统版本:Build.VERSION.RELEASE,SDK版本:Build.VERSION.SDK_INT
     *
     * @return Object[] 设备信息
     */
    public static Object[] getDeviceInfo() {
        Object[] device = new Object[]{Build.DEVICE, Build.VERSION.RELEASE, Build.VERSION.SDK_INT};

        return device;
    }


    /**
     * 判断App是否处于后台
     *
     * @param context 上下文
     * @return int
     */
    public static boolean isAppOnForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {

            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 得到app名字
     *
     * @param context 上下文
     * @return String
     */
    public static String getAppName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        }
        return "";
    }

    /**
     * 得到app图标
     *
     * @param context 上下文
     * @return Drawable
     */
    public static Drawable getAppIcon(Context context) {
        try {
            return context.getPackageManager().getApplicationIcon(context.getPackageName());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static int dip2px(int dip, Context context) {
        return (int) (dip * getDensity(context) + 0.5f);
    }

    public static float getDensity(Context context) {
        return getDisplayMetrics(context).density;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static String getManifestString(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long firstTime;


    public static boolean doubleClickToFinish(Context mcontext) {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) //如果两次按键时间间隔大于2秒，则不退出
        {
            firstTime = secondTime;//更新firstTime
            RxToast.showToast(mcontext, "再按一次退出程序", false);
            return false;
        } else  //两次按键小于2秒时，退出应用
        {
            return true;
        }
    }



    //屏幕主题变暗
    public void setBackgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = (activity).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }


    //获取资源中的assets文件
    private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();

    public static Typeface get(Context c, String assetPath) {
        synchronized (cache) {
            if (!cache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), assetPath);
                    cache.put(assetPath, t);
                } catch (Exception e) {
                    Log.e("", "Could not get typeface '" + assetPath + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(assetPath);
        }
    }


    //sdcard是否可读写
    public static boolean IsCanUseSdCard() {
        try {
            return Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //sim卡是否可读
    public static boolean isCanUseSim(Context context) {
        try {
            TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
