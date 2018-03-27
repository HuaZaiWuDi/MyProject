package com.embednet.wdluo.bleplatformsdkdemo.ble;

import com.embednet.wdluo.bleplatformsdkdemo.ble.listener.BleCallBack;
import com.embednet.wdluo.bleplatformsdkdemo.ble.listener.BleChartChangeCallBack;

import java.util.Date;

import laboratory.dxy.jack.com.jackupdate.util.ByteUtil;

/**
 * 项目名称：BLEPlatformSDKDemo
 * 类描述：
 * 创建人：Jack
 * 创建时间：2018/2/11
 */
public class BleAPI {
    private static BleAPI bleAPI = null;
    private static BleTools bleTools = null;

    public static BleAPI getInstance() {

        bleTools = BleTools.getInstance();

        if (bleAPI == null) {
            bleAPI = new BleAPI();
        }
        return bleAPI;
    }


    /***
     * 查询设备状态   返回：0x08,0x0a,0x00,0x01,0xff,75,165,0x00，0x80,0x01
     *
     *表示开机，输出功率中，0x00表示无充电（0x01表示在充电），剩余电量75%，负载阻值165/100=1.65欧姆,广播关闭，设备类型为0x80,设备固件版本号0X01
     *
     * byte[2]=表示开机，
     * byte[3]=输出功率中0x00表示无充电（0x01表示在充电）
     * byte[4]=剩余电量75%，
     * byte[5]=负载阻值165/100=1.65欧姆，
     * byte[6]=广播关闭，
     * byte[7]=设备类型为0x80，
     * byte[8]=设备固件版本号0X01，
     *
     */

    public void getDeviceStatus(final BleCallBack bleCallBack) {
        final byte[] bytes = new byte[2];
        bytes[0] = 0x01;
        bytes[1] = 0x02;
        bleTools.writeBle(bytes, new BleChartChangeCallBack() {
            @Override
            public void callBack(byte[] data) {
                if (bleCallBack != null)
                    bleCallBack.isSuccess(data);
//                MyApplication.cache.put("DeviceStatus", );
            }
        });
    }

    /**
     * 查询设备版本号：0x09,0x07,'Q', '1',   '-',  'V',  '1'
     * <p>
     * <p>
     * 0x07,0x07,'Q', '1',   ':',  'V',  '1'表示
     * 设备类型为Q1,固件版本号为V1
     */
    public void getDeviceVersion(final BleCallBack bleCallBack) {
        final byte[] bytes = new byte[2];
        bytes[0] = 0x02;
        bytes[1] = 0x02;
        bleTools.writeBle(bytes, new BleChartChangeCallBack() {
            @Override
            public void callBack(byte[] data) {
                if (bleCallBack != null)
                    bleCallBack.isSuccess(data);
            }
        });
    }


    /**
     * 设置设备时间
     * 0x03,0x06,1520873700(4字节UNIX时间戳)
     * <p>
     * 1520873700位UNIX时间戳，该命令表示设置设备时间为
     * 2018.3.13.12.55.05
     */
    public void setDeviceTime(final BleCallBack bleCallBack) {
        int time = (int) new Date().getTime();
        byte[] times = ByteUtil.intToBytesG4(time);
        final byte[] bytes = new byte[6];
        bytes[0] = 0x03;
        bytes[1] = 0x06;
        bytes[2] = times[0];
        bytes[3] = times[1];
        bytes[4] = times[2];
        bytes[5] = times[3];
        bleTools.writeBle(bytes, new BleChartChangeCallBack() {
            @Override
            public void callBack(byte[] data) {
                if (bleCallBack != null)
                    bleCallBack.isSuccess(data);
            }
        });
    }

    /**
     * 切换设备开关机
     * 0x04,0x03,0x00/0x01
     * 0x04,0x03,0x00表示设置设备为开机，0x04,0x03,0x01表示关机
     */
    public void setSwitchgear(boolean isOn, final BleCallBack bleCallBack) {
        final byte[] bytes = new byte[3];
        bytes[0] = 0x04;
        bytes[1] = 0x03;
        bytes[2] = (byte) (isOn ? 0x00 : 0x01);
        bleTools.writeBle(bytes, new BleChartChangeCallBack() {
            @Override
            public void callBack(byte[] data) {
                if (bleCallBack != null)
                    bleCallBack.isSuccess(data);
            }
        });
    }

    /**
     * 设置设备输出功率
     * 0x05,0x03,0x00/0x01/0x02
     * 0x04,0x03,0x00表示设置设备输出功率低，x04,0x03,0x01
     * 表示设置设备输出功率中，x04,0x03,0x02表示设置设备输出功率高
     */
    public void setInputPower(int power, final BleCallBack bleCallBack) {
        final byte[] bytes = new byte[3];
        bytes[0] = 0x05;
        bytes[1] = 0x03;
        bytes[2] = (byte) power;
        bleTools.writeBle(bytes, new BleChartChangeCallBack() {
            @Override
            public void callBack(byte[] data) {
                if (bleCallBack != null)
                    bleCallBack.isSuccess(data);
            }
        });
    }

    /**
     * 查询当天工作数据
     * 0x06,0x02
     * <p>
     * 返回：
     * 0x0a,0x14,0x00,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX(1点到8点之间数据)
     * 0x0a,0x14,0x01,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX(9点到16点之间数据)
     * 0x0a,0x14,0x02,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX,0xXXXX(17点到24点之间数据)
     */
    public void getWorkData(final BleCallBack bleCallBack) {
        final byte[] bytes = new byte[2];
        bytes[0] = 0x06;
        bytes[1] = 0x02;
        bleTools.writeBle(bytes, new BleChartChangeCallBack() {
            @Override
            public void callBack(byte[] data) {
                if (bleCallBack != null)
                    bleCallBack.isSuccess(data);
            }
        });
    }

    /**
     * 查询历史60天工作数据
     * 0x07,0x02
     * <p>
     * 返回：
     * 最近180天的历史记录，历史记录数字直接发送，连续发送36包数据，每包20字节，每天记录数据为4字节，
     * 比如2018年3月10号工作了1000次，那么4字节数据为：0x14(2018),0x3a(3月10号)，0x03,0xe8(0x03*256+0xe8=1000)
     */
    public void getHistroyData(final BleCallBack bleCallBack) {
        final byte[] bytes = new byte[2];
        bytes[0] = 0x07;
        bytes[1] = 0x02;
        bleTools.writeBle(bytes, new BleChartChangeCallBack() {
            @Override
            public void callBack(byte[] data) {
                if (bleCallBack != null)
                    bleCallBack.isSuccess(data);
            }
        });
    }

    /**
     * 设置设备广播开关
     * 0x0c,0x03,0x00/0x01
     * 0x00 表示关闭广播，0x01表示一直广播
     */
    public void switchBorad(boolean isOpen, final BleCallBack bleCallBack) {
        final byte[] bytes = new byte[3];
        bytes[0] = 0x07;
        bytes[1] = 0x03;
        bytes[1] = (byte) (isOpen ? 0x00 : 0x01);
        bleTools.writeBle(bytes, new BleChartChangeCallBack() {
            @Override
            public void callBack(byte[] data) {
                if (bleCallBack != null)
                    bleCallBack.isSuccess(data);
            }
        });
    }

}
