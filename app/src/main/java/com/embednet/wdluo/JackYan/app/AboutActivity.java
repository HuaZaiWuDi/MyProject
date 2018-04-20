package com.embednet.wdluo.JackYan.app;

import android.os.Bundle;

import com.embednet.wdluo.JackYan.R;
import com.embednet.wdluo.JackYan.adapter.HelpCardAdapter;
import com.embednet.wdluo.JackYan.module.CardItem;

import java.util.ArrayList;
import java.util.List;

import laboratory.dxy.jack.com.jackupdate.ui.cardstack.RxCardStackView;
import lecho.lib.hellocharts.util.ChartUtils;


public class AboutActivity extends BaseActivity {

    private List<CardItem> data = new ArrayList<>();
    String[] title;
    String[] text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        title = getResources().getStringArray(R.array.help_title);
        text = getResources().getStringArray(R.array.hlep_text);
//        StatusBarUtils.from(this).setTransparentStatusbar(true).process();
        setTitleText(R.string.help);
        setBack();
        initRxCard();
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
        });
    }

    private void initData() {
        data.clear();
        for (int i = 0; i < title.length; i++) {
            data.add(new CardItem(ChartUtils.nextColor(), title[i], text[i]));
        }
    }
}
