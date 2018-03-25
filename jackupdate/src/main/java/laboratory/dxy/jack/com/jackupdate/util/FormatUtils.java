package laboratory.dxy.jack.com.jackupdate.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 项目名称：TextureViewDome
 * 类描述：
 * 创建人：Jack
 * 创建时间：2017/7/18
 */
public class FormatUtils {

    public FormatUtils() {
        throw new RuntimeException("cannot be instantiated");
    }


    public static String setFormatDate(long value, String format) {

        return new SimpleDateFormat(format, Locale.getDefault()).format(value);
    }

    public static String setFormatNum(long value, String format) {

        return new DecimalFormat(format).format(value);
    }


    public static long getDateMillis(String dateString, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        long millionSeconds = 0;
        try {
            millionSeconds = sdf.parse(dateString).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }// 毫秒

        return millionSeconds;
    }


    public static Date setParseDate(String value, String format) {

        try {
            return new SimpleDateFormat(format, Locale.getDefault()).parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Number setParseNumber(String value, String format) {
        try {
            return new DecimalFormat(format).parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
