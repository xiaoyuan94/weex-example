package com.xxyuan.weexandroid.fragment;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.xxyuan.weexandroid.R;
import com.xxyuan.weexandroid.weex.Constants;


public class WXFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = Constants.EXAMPLE_URL;
        String name = getIntent().getStringExtra("name");
        if (!TextUtils.isEmpty(name)) {
            setTitle(name);
            url = Constants.LOCAL_JS_DIR + name;
        }

        WeexFragment weexFragment = WeexFragment.newInstance(url);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.content_fragment, weexFragment);
        transaction.commit();
    }
}
