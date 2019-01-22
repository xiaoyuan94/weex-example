package com.xxyuan.weexandroid.activity;


import com.xxyuan.weexandroid.R;

public class WeexUIActivity extends BaseActivity {

    @Override
    protected void startRenderPage() {
        renderLocalWeexPage("weex/home.js");
        //renderNetWeexPage("http://192.168.0.68:8081/dist/home.js");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_weex_ui;
    }
}
