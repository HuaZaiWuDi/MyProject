package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.ble.BleAPI;
import com.embednet.wdluo.bleplatformsdkdemo.ble.BleTools;
import com.embednet.wdluo.bleplatformsdkdemo.scanner.ScannerFragment;
import com.embednet.wdluo.bleplatformsdkdemo.util.Utils;

import laboratory.dxy.jack.com.jackupdate.util.AnimUtils;

import static com.embednet.wdluo.bleplatformsdkdemo.MyApplication.bleManager;

public class ControlActivity extends BaseAvtivity {


    String TGA = ControlActivity.class.getSimpleName();

    ImageView img_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        setTitleText("控制界面");
        setBack();


        img_switch = findViewById(R.id.img_switch);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add("搜索");

        /**
         * setShowAsAction参数说明  MenuItem接口的一些常量
         * SHOW_AS_ACTION_ALWAYS  总是显示这个项目作为一个操作栏按钮。
         * SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW  此产品的动作视图折叠成一个正常的菜单项。
         * SHOW_AS_ACTION_IF_ROOM  显示此项目作为一个操作栏的按钮,如果系统有空间。
         * SHOW_AS_ACTION_NEVER   从不显示该项目作为一个操作栏按钮。
         * SHOW_AS_ACTION_WITH_TEXT 当这个项目是在操作栏中,始终以一个文本标签显示它,即使它也有指定一个图标。
         */

        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);//主要是这句话

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final ScannerFragment dialog = ScannerFragment.getInstance(ControlActivity.this, null, false); // Device that is advertising directly does not have the GENERAL_DISCOVERABLE nor LIMITED_DISCOVERABLE flag set.
                dialog.show(getSupportFragmentManager(), "scan_fragment");
                return false;
            }
        });//添加监听事件

        return super.onCreateOptionsMenu(menu);

    }


    private boolean isOpen = true;

    public void doSwitch(View v) {

        AnimUtils.doHeartBeat(v, 300);
//        BleTools.getInstance().writeBle(BleAPI.switchOpen(isOpen,false));


//        Utils.navigateWithRippleCompat(this, null, v, isOpen ? R.color.button_material_dark : R.color.red);
        //做开关
        img_switch.setImageResource(isOpen ? R.mipmap.img_icon_switch_off : R.mipmap.img_icon_switch_on);
        isOpen = !isOpen;


    }

    @Override
    protected void onDestroy() {
//        bleManager.disconnectAllDevice();
        super.onDestroy();
    }
}
