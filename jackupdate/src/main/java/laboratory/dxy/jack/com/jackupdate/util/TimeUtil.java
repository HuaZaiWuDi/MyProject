package laboratory.dxy.jack.com.jackupdate.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 项目名称：DXYBle_GM
 * 类描述：
 * 创建人：Jack
 * 创建时间：2017/6/23
 */
public class TimeUtil {


    public TimeUtil() {
        throw new RuntimeException("cannot be instantiated");
    }

    // 获取当前时间所在周的开始日期
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        Date time = c.getTime();
        time.getDay();
        return c.getTime();
    }

    // 获取当前时间所在周的结束日期
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.getTime();
    }

    // 获取指定周的每一天
    public static Date getDayEveryOfWeek(Date date, int day) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + day); // Sunday
        return c.getTime();
    }

    // 获取指定月的每一天
    public static Date getEveryDayOfMonth(Date date, int day) {

        Calendar c = new GregorianCalendar();
        c.setTime(date);
        int daysCountOfMonth = c.getActualMaximum(Calendar.DATE);//获取指定年份中指定月份有几天
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, day);//设置为1号,当前日期既为本月第一天
        return c.getTime();
    }

}
