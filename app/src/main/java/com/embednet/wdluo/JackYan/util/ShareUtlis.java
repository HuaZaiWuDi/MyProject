package com.embednet.wdluo.JackYan.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * 项目名称：TextureViewDome
 * 类描述：
 * 创建人：Jack
 * 创建时间：2017/7/25
 */
public class ShareUtlis {


    public ShareUtlis() {
         /* cannot be instantiated */
        throw new RuntimeException("cannot be instantiated");
    }


    /**
     * 简单的分享文本
     **/
    public static void smpleShareText(@NonNull Context context, @NonNull String text) {
        if (TextUtils.isEmpty(text)) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享"));
    }


    /**
     * 简单的分享文本文件
     **/
    public static void smpleShareTxtPath(@NonNull Context context, @NonNull String txtPath) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        Uri data = File2UriByN(context, new File(txtPath), intent);
        //pdf文件要被读取所以加入读取权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(data, "application/txt");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 简单的分享图片
     **/
    public static void smpleShareImage(@NonNull Context context, @NonNull String path) {
        if (TextUtils.isEmpty(path)) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        L.d("图片地址：" + path);

        Uri data = File2UriByN(context, new File(path), intent);


        intent.putExtra(Intent.EXTRA_STREAM, data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享"));
    }

    /**
     * 简单的分享图片
     **/
    public static void smpleShareImage(@NonNull Context context, @NonNull Uri uri) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享"));
    }


    /**
     * 简单的分享图片
     **/
    public static void smpleShareImage(@NonNull Context context, @NonNull Bitmap bitmap, String APPpackage) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(APPpackage))
            intent.setPackage(APPpackage);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");

        File imgFile = ImageTools.saveBitmap(bitmap, new Date().getTime() + "");
        Uri data = File2UriByN(context, imgFile, intent);

        intent.putExtra(Intent.EXTRA_STREAM, data);

        context.startActivity(Intent.createChooser(intent, "分享"));


    }

    /**
     * 简单的分享多图片
     **/
    public static void smpleShareImages(@NonNull Context context, @NonNull ArrayList<String> images) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putStringArrayListExtra(Intent.EXTRA_STREAM, images);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享"));
    }


    /**
     * 简单的评分分享
     **/
    public static void smpleEvaluate(@NonNull Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent2, "评价"));
    }


    private static Uri File2UriByN(Context context, File file, Intent intent) {
        Uri data = null;
        if (file != null) {
            // 判断版本大于等于7.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //添加临时授权
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authorities
                data = FileProvider.getUriForFile(context, context.getPackageName(), file);
            } else {
                data = Uri.fromFile(file);
            }
        }
        return data;
    }


}
