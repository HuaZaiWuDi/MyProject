package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.scanner.ScannerFragment;
import com.embednet.wdluo.bleplatformsdkdemo.util.L;

import java.util.ArrayList;
import java.util.List;

import laboratory.dxy.jack.com.jackupdate.ui.recyclerview.CommonAdapter;
import laboratory.dxy.jack.com.jackupdate.ui.recyclerview.DividerItemDecoration;
import laboratory.dxy.jack.com.jackupdate.ui.recyclerview.ViewHolder;

public class ControlActivity extends BaseAvtivity {


    String TGA = ControlActivity.class.getSimpleName();

    List<Item> items = new ArrayList<>();

    CommonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        setTitleText("控制界面");
        setBack();


//        img_switch = findViewById(R.id.img_switch);
        setRecycler();

    }


    /**
     * * byte[2]=表示开机，
     * byte[3]=输出功率中0x00表示无充电（0x01表示在充电）
     * byte[4]=剩余电量75%，
     * byte[5]=负载阻值165/100=1.65欧姆，
     * byte[6]=广播关闭，
     * byte[7]=设备类型为0x80，
     * byte[8]=设备固件版本号0X01，
     */
    private void setRecycler() {
        final int[] imgs = {R.mipmap.img_icon_switch_on, R.mipmap.icon_battery, R.mipmap.icon_resistance, R.mipmap.icon_broad, R.mipmap.icon_app, R.mipmap.update};
        String[] titles = {"开关机状态", "电池状态", "负载阻值", "广播状态", "设备类型", "固件版本"};
        items.clear();
        for (int i = 0; i < imgs.length; i++) {
            Item item = new Item();
            item.icon = imgs[i];
            item.title = titles[i];
            item.isOpen = false;
            item.text = "关闭";
            items.add(item);
        }


        RecyclerView mRecyclerView = findViewById(R.id.mRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST, R.color.white));

        adapter = new CommonAdapter<Item>(this, R.layout.item_recycyler, items) {
            @Override
            public void convert(final ViewHolder holder, final Item item, final int position) {
                holder.setText(R.id.title, item.title);
                holder.setText(R.id.text, item.title);
                holder.setText(R.id.text, item.text);

                holder.setSwitch(R.id.switchgear, item.isOpen);
                holder.setImageResource(R.id.icon, item.icon);

                holder.setAlpha(R.id.item_view, item.isOpen ? 1 : 0.5f);

                Switch aSwitch = holder.getView(R.id.switchgear);
                aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        L.d("状态：" + b);
                        item.isOpen = b;
                        item.text = item.isOpen ? "开启" : "关闭";
                        holder.setText(R.id.text, item.text);
                        holder.setAlpha(R.id.item_view, item.isOpen ? 1 : 0.5f);
                    }
                });

            }
        };

        mRecyclerView.setAdapter(adapter);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public class Item {
        String title;
        String text;
        int icon;
        boolean isOpen;


    }

}
