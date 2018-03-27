package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.embednet.wdluo.bleplatformsdkdemo.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends BaseAvtivity {
    private GridView gview;
    private List<Map<String, Object>> itemList;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = {R.drawable.update, R.mipmap.img_control, R.mipmap.icon_history_data};
    private String[] iconName = {"更新固件", "控制中心", "历史数据"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setTitleText("功能界面");
        setBack();


        gview = (GridView) findViewById(R.id.gridView);
        // ensure that Bluetooth exists
        if (!ensureBLEExists()) {
            finish();
        }
        //获取数据
        itemList = getItemList();
        //新建适配器
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        sim_adapter = new SimpleAdapter(this, itemList, R.layout.item, from, to);
        //配置适配器
        gview.setAdapter(sim_adapter);
        //设置点击回调
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        /* Create an Intent that will start the Main WordPress Activity. */
                        Intent mainIntent = new Intent(MainActivity.this,
                                UpdateDFUActivity.class);
                        startActivity(mainIntent);
                        break;
                    case 1:
                        /* Create an Intent that will start the Main WordPress Activity. */
                        startActivity(new Intent(MainActivity.this,
                                ControlActivity.class));
                        break;
                    case 2:
                        /* Create an Intent that will start the Main WordPress Activity. */
                        startActivity(new Intent(MainActivity.this,
                                HistoryDataActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }


    public List<Map<String, Object>> getItemList() {
        List<Map<String, Object>> itemListTemp = new ArrayList<>();
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            itemListTemp.add(map);
        }
        return itemListTemp;
    }

    private boolean ensureBLEExists() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.no_ble, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
