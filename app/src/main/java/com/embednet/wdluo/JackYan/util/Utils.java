package com.embednet.wdluo.JackYan.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：oden
 * 创建时间：2018/1/10
 */
public class Utils {


    public static String getVersionName(Context context) {
        String versionName = "V0.0.1";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
//            Contents.versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
//            L.d("appVersionInfo:" + versionName + "code:" + Contents.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 带水波动画
     */
    @SuppressLint("NewApi")
    public static void navigateWithRippleCompat(final Activity activity,
                                                final View triggerView, @ColorRes int color) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat option = ActivityOptionsCompat.makeScaleUpAnimation(triggerView, 0, 0,
                    triggerView.getMeasuredWidth(), triggerView.getMeasuredHeight());
            return;
        }

        int[] location = new int[2];
        triggerView.getLocationInWindow(location);
        final int cx = location[0] + triggerView.getWidth() / 2;
        final int cy = location[1] + triggerView.getHeight() / 2;
        final ImageView view = new ImageView(activity);
        view.setImageResource(color);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        int w = decorView.getWidth();
        int h = decorView.getHeight();
        decorView.addView(view, w, h);
        int finalRadius = (int) Math.sqrt(w * w + h * h) + 1;
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setDuration(500);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                decorView.removeView(view);
            }
        });
        anim.start();
    }


    /**
     * @param triggerView 事件View
     *                    <p>
     *                    带水波动画的Activity跳转
     */

    @SuppressLint("NewApi")
    public static void navigateWithRippleCompat(final Activity activity, @Nullable final Intent intent,
                                                final View triggerView, @ColorRes int color) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (intent != null) {
                ActivityOptionsCompat option = ActivityOptionsCompat.makeScaleUpAnimation(triggerView, 0, 0,
                        triggerView.getMeasuredWidth(), triggerView.getMeasuredHeight());
                ActivityCompat.startActivity(activity, intent, option.toBundle());
            }
            return;
        }

        int[] location = new int[2];
        triggerView.getLocationInWindow(location);
        final int cx = location[0] + triggerView.getWidth() / 2;
        final int cy = location[1] + triggerView.getHeight() / 2;
        final ImageView view = new ImageView(activity);
        view.setImageResource(color);

        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        int w = decorView.getWidth();
        int h = decorView.getHeight();
        decorView.addView(view, w, h);
        int finalRadius = (int) Math.sqrt(w * w + h * h) + 1;
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.setDuration(300);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (intent != null) {
                    activity.startActivity(intent);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                decorView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(view);
                    }
                }, 500);
            }
        });
        anim.start();
    }

    public static String formatData(Date date, String format) {
        return new SimpleDateFormat(format, Locale.CHINA).format(date.getTime());
    }

    public static String formatData() {
        String DATE_FORMAT = "yyyyMMddHHmmss";
        return new SimpleDateFormat(DATE_FORMAT, Locale.CHINA).format(new Date().getTime());
    }


    /**
     * 发送邮箱，会跳转到手机邮箱软件
     */
    public static void sendEmail(Context context) {
        String[] strings = {"1092741288@qq.com"};
        String title = "测试标题";
        String content = "测试内容";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("massage/rfc822");
        // 设置邮件发收人
        intent.putExtra(Intent.EXTRA_EMAIL, strings);
        // 设置邮件标题
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        // 设置邮件内容
        intent.putExtra(Intent.EXTRA_TEXT, content);

        // 调用系统的邮件系统
        context.startActivity(Intent.createChooser(intent, "请选择邮件发送软件"));

    }

}
