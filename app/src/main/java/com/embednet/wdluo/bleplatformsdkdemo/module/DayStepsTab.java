package com.embednet.wdluo.bleplatformsdkdemo.module;

import java.util.Arrays;

/**
 * Created by MacBook on 18/3/19.
 */

public class DayStepsTab {

    public String date;

    public int[] data;

    public int Total;


    @Override
    public String toString() {
        return "DayStepsTab{" +
                "date='" + date + '\'' +
                ", data=" + Arrays.toString(data) +
                ", Total=" + Total +
                '}';
    }
}
