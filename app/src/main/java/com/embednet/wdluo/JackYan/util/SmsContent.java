package com.embednet.wdluo.JackYan.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * sms主要结构：
 * <p>
 * _id：短信序号，如100
 * <p>
 * thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
 * <p>
 * address：发件人地址，即手机号，如+86138138000
 * <p>
 * person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
 * <p>
 * date：日期，long型，如1346988516，可以对日期显示格式进行设置
 * <p>
 * protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信
 * <p>
 * read：是否阅读0未读，1已读
 * <p>
 * status：短信状态-1接收，0complete,64pending,128failed
 * <p>
 * type：短信类型1是接收到的，2是已发出
 * <p>
 * body：短信具体内容
 * <p>
 * service_center：短信服务中心号码编号，如+8613800755500
 */
public class SmsContent extends ContentObserver {

    public static final String SMS_URI_INBOX = "content://sms/inbox";
    private Activity activity = null;
    private String smsContent = "";
    private EditText verifyText = null;
    private String phone;

    public SmsContent(Activity activity, Handler handler, EditText verifyText, String phone) {
        super(handler);
        this.activity = activity;
        this.verifyText = verifyText;
        this.phone = phone;
    }

    public String getSmsContent() {
        return smsContent;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        ContentResolver cr = activity.getContentResolver();


        String[] projection = new String[]{"body"};//"_id", "address", "person",, "date", "type
        String where = " address = " + phone + " AND date >  "
                + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(Uri.parse(SMS_URI_INBOX), projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));
            L.d("短信信息：name:" + name + ",number: " + number + ",body: " + body);
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(body.toString());
            smsContent = m.replaceAll("").trim().toString();
            verifyText.setText(smsContent);
        }

//        Cursor cursor = null;// 光标
//        // 读取收件箱中指定号码的短信
//        cursor = activity.managedQuery(Uri.parse(SMS_URI_INBOX), new String[]{"_id", "address", "body", "read"}, "address=? and read=?",
//                new String[]{phone, "0"}, "date desc");
//        if (cursor != null) {// 如果短信为未读模式
//            cursor.moveToFirst();
//            if (cursor.moveToFirst()) {
//
//                String smsbody = cursor.getString(cursor.getColumnIndex("body"));
//                L.d("短信信息3：" + "smsbody=======================" + smsbody);
//
//            }
//        }
    }
}