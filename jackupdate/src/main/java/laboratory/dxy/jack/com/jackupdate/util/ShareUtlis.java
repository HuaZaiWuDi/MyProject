package laboratory.dxy.jack.com.jackupdate.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

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
     * 简单的分享图片
     **/
    public static void smpleShareImage(@NonNull Context context, @NonNull String path) {
        if (TextUtils.isEmpty(path)) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        L.d("图片地址：" + path);
        Uri parse = Uri.fromFile(new File(path));

        intent.putExtra(Intent.EXTRA_STREAM, parse);
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
    public static void smpleShareImage(@NonNull Context context, @NonNull Bitmap bitmap) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_STREAM, ImageTools.saveBitmap(bitmap, "shrae_img"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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


}
