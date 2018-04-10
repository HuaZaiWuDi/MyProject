package com.embednet.wdluo.bleplatformsdkdemo.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.embednet.wdluo.bleplatformsdkdemo.R;
import com.embednet.wdluo.bleplatformsdkdemo.ui.SweetDialog;


public class AboutActivity extends BaseAvtivity {

    private List<CardItem> data = new ArrayList<>();
    String[] title;
    String[] text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
//        StatusBarUtils.from(this).setTransparentStatusbar(true).process();
        setTitleText(R.string.help);
        setBack();
    }

    private void initRxCard() {
        RxCardStackView mRxCardStackView = findViewById(R.id.mRxCardStackView);
        initData();
        HelpCardAdapter adapter = new HelpCardAdapter(this);
        adapter.setData(data);
        mRxCardStackView.setAdapter(adapter);
        mRxCardStackView.setItemExpendListener(new RxCardStackView.ItemExpendListener() {
            @Override
            public void onItemExpend(boolean expend) {

    }


    public void APP(View v) {
        new SweetDialog(this)
                .setDaration(2000)
                .setTitleText("已经是最新版本了！")
                .show();

    }

    private void initData() {
        data.clear();
        for (int i = 0; i < title.length; i++) {
            data.add(new CardItem(ChartUtils.nextColor(), title[i], text[i]));
        }
    }

}
