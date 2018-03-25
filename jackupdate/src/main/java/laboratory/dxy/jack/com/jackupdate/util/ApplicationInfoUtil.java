package laboratory.dxy.jack.com.jackupdate.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用信息获取类
 *
 * @author 野虎
 * @时间 2016年2月23日下午3:47:13
 */
public class ApplicationInfoUtil {
    public static final int DEFAULT = 0; // 默认 所有应用  
    public static final int SYSTEM_APP = DEFAULT + 1; // 系统应用  
    public static final int NONSYSTEM_APP = DEFAULT + 2; // 非系统应用  

    /**
     * 根据包名获取相应的应用信息
     *
     * @param context
     * @param packageName
     * @return 返回包名所对应的应用程序的名称。
     */
    public static String getProgramNameByPackageName(Context context,
                                                     String packageName) {
        PackageManager pm = context.getPackageManager();
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 获取手机所有应用信息
     *
     * @param allApplist
     * @param context
     */
    public static void getAllProgramInfo(List<AppInfo> allApplist,
                                         Context context) {
        getAllProgramInfo(allApplist, context, DEFAULT);
    }

    /**
     * 获取手机所有应用信息
     *
     * @param applist
     * @param context
     * @param type    标识符 是否区分系统和非系统应用
     */
    public static void getAllProgramInfo(List<AppInfo> applist,
                                         Context context, int type) {
        ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); // 用来存储获取的应用信息数据  
        List<PackageInfo> packages = context.getPackageManager()
                .getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo tmpInfo = new AppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
                    context.getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            tmpInfo.versionCode = packageInfo.versionCode;
            tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(context
                    .getPackageManager());
            switch (type) {
                case NONSYSTEM_APP:
                    if (!isSystemAPP(packageInfo)) {
                        applist.add(tmpInfo);
                    }
                    break;
                case SYSTEM_APP:
                    if (isSystemAPP(packageInfo)) {
                        applist.add(tmpInfo);
                    }
                    break;
                default:
                    applist.add(tmpInfo);
                    break;
            }

        }
    }

    /**
     * 获取所有系统应用信息
     *
     * @param context
     * @return
     */
    public static List<AppInfo> getAllSystemProgramInfo(Context context) {
        List<AppInfo> systemAppList = new ArrayList<AppInfo>();
        getAllProgramInfo(systemAppList, context, SYSTEM_APP);
        return systemAppList;
    }

    /**
     * 获取所有非系统应用信息
     *
     * @param context
     * @return
     */
    public static List<AppInfo> getAllNonsystemProgramInfo(Context context) {
        List<AppInfo> nonsystemAppList = new ArrayList<AppInfo>();
        getAllProgramInfo(nonsystemAppList, context, NONSYSTEM_APP);
        return nonsystemAppList;
    }

    /**
     * 判断是否是系统应用
     *
     * @param packageInfo
     * @return
     */
    public static Boolean isSystemAPP(PackageInfo packageInfo) {
        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用  
            return false;
        } else { // 系统应用  
            return true;
        }
    }


    /**
     * @author 野虎
     *         2016年2月23日上午11:53:52
     */
    public static class AppInfo {
        public String appName; // 应用名
        public String packageName; // 包名
        public String versionName; // 版本名
        public int versionCode = 0; // 版本号
        public Drawable appIcon = null; // 应用图标

        @Override
        public String toString() {
            return appName + " , " + packageName + " ," + versionName + " ," + versionCode;
        }

    }
}