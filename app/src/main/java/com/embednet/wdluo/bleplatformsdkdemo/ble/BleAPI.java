package com.embednet.wdluo.bleplatformsdkdemo.ble;

import java.util.Calendar;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：Jack
 * 创建时间：2018/2/11
 */
public class BleAPI {


    //切换开关
    public static byte[] switchOpen(boolean isOpen, boolean isRead) {
        if (isRead)
            return new byte[]{0x01, 0x02};
        else
            return new byte[]{0x01, 0x03, (byte) (isOpen ? 0x00 : 0x01)};
    }


    //输出大小   0x00大,0x01中，0x02 小
    public static byte[] outPut(int value) {
        if (value < 0) {
            return new byte[]{0x02, 0x02};
        } else
            return new byte[]{0x02, 0x03, (byte) (value == 0 ? 0x00 : value == 1 ? 0x01 : 0x02)};
    }

    //版本号
    public static byte[] readVersion() {
        return new byte[]{0x03, 0x02};
    }

    //设备名
    public static byte[] readName(String name) {
        if (name == null)
            return new byte[]{0x04, 0x02};
        else {

            int len = 2 + name.getBytes().length;
            byte[] bytes = new byte[len];
            bytes[0] = 0x04;
            bytes[1] = (byte) len;
            System.arraycopy(name.getBytes(), 0, bytes, 2, name.getBytes().length);
            return bytes;
        }
    }

    //电池电量及状态
    public static byte[] battery() {
        return new byte[]{0x07, 0x02};
    }

    //负载阻值
    public static byte[] loadValue() {
        return new byte[]{0x08, 0x02};
    }


    //开始DFU升级
    public static byte[] startDFU() {
        return new byte[]{0x09, 0x02};
    }

    public static byte[] synParms() {
        Calendar c = Calendar.getInstance();
        byte year = (byte) (c.get(Calendar.YEAR) - 2000);
        byte month = (byte) (c.get(Calendar.MONTH) + 1);
        byte day = (byte) c.get(Calendar.DAY_OF_MONTH);
        byte hour = (byte) c.get(Calendar.HOUR_OF_DAY);
        byte min = (byte) c.get(Calendar.MINUTE);
        byte sec = (byte) c.get(Calendar.SECOND);

        byte[] bytes = new byte[8];
        bytes[0] = 0x0a;
        bytes[1] = 0x08;
        bytes[2] = year;
        bytes[3] = month;
        bytes[4] = day;
        bytes[5] = hour;
        bytes[6] = min;
        bytes[7] = sec;
        return bytes;
    }
}
