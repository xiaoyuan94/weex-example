package com.xxyuan.weexandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.taobao.weex.RenderContainer;
import com.taobao.weex.WXSDKInstance;
import com.xxyuan.weexandroid.R;
import com.xxyuan.weexandroid.common.CommonUtils;
import com.xxyuan.weexandroid.weex.Constants;
import com.xxyuan.weexandroid.weex.DQWeexListener;
import com.xxyuan.weexandroid.weex.WeexDialogParams;
import com.xxyuan.weexandroid.weex.WeexInstanceManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeexDialogActivity extends AppCompatActivity implements DQWeexListener {


    //private Map<String, ViewGroup> weexContainerMap = new HashMap<>();

    @BindView(R.id.container_weex)
    FrameLayout weexContainer;
    @BindView(R.id.tv_info)
    TextView tv_info;
    @BindView(R.id.btn_change_view)
    Button btnChange;
    private Context context;
    private WeexInstanceManager weexInstanceManager;

    private int screenWidth = 0;
    private int screenHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_instance);
        ButterKnife.bind(this);
        initData();
        //注册广播，用于动态添加weex页面
        registerWeexReceiver();
        //测试渲染两个服务端js
        testAddWeexDialog();
    }

    private void testAddWeexDialog() {
        WeexDialogParams params = new WeexDialogParams(Constants.LOCAL_SERVER_JS, "", 0.1, 0.1, 0.5, 0.3, 1);
        params.md5 = Constants.LOCAL_SERVER_JS_MD5;
        createWeexDialog(params);
    }

    @OnClick(R.id.tv_info)
    public void openWeexPage() {
        testAddWeexDialog();
    }

    private float changeSize = 0f;

    @OnClick(R.id.btn_change_view)
    public void changeView() {
        WeexDialogParams params = new WeexDialogParams(Constants.LOCAL_SERVER_JS, Constants.LOCAL_SERVER_JS_MD5, 0.1, 0.1, 0.5 + changeSize, 0.3, 1);
        resizeWeexDialog(params);
        changeSize += 0.02;
    }

    private void initData() {
        context = this;
        weexInstanceManager = new WeexInstanceManager(context, weexContainer);
        weexInstanceManager.setListener(this);
        screenWidth = CommonUtils.getScreenWidth(context);
        screenHeight = CommonUtils.getScreenHeight(context);
        tv_info.setText(R.string.weex_dialog_tips);
        btnChange.setText(R.string.btn_txt_render_again);
    }

    /**
     * 监听WeexDialogModule发出的广播
     */
    private void registerWeexReceiver() {
        context.registerReceiver(receiver, new IntentFilter(Constants.BC_ACTION_WEEX_DIALOG));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constants.BC_ACTION_WEEX_DIALOG.equals(action)) {
                String cmd = intent.getStringExtra(Constants.KEY_CMD);
                WeexDialogParams params = (WeexDialogParams) intent.getSerializableExtra(Constants.KEY_PARAM);
                Log.d("dq", "received cmd" + cmd + ",param=" + params);
                if (Constants.CMD_WEEX_NEW_DIALOG.equals(cmd)) {
                    createWeexDialog(params);
                } else if (Constants.CMD_WEEX_RESIZE_DIALOG.equals(cmd)) {
                    resizeWeexDialog(params);
                } else if (Constants.CMD_WEEX_CLOSE_DIALOG.equals(cmd)) {
                    closeWeexDialog(params);
                } else if (Constants.CMD_WEEX_SHOW_DIALOG.equals(cmd)) {
                    showOrHideWeexDialog(params, true);
                } else if (Constants.CMD_WEEX_HIDE_DIALOG.equals(cmd)) {
                    showOrHideWeexDialog(params, false);
                }
            }
        }
    };

    /**
     * 显示或者隐藏窗口
     *
     * @param params 所需参数
     * @param isShow 是否显示
     */
    private void showOrHideWeexDialog(WeexDialogParams params, boolean isShow) {
        if (params != null && !TextUtils.isEmpty(params.url)) {
            ViewGroup viewGroup = weexInstanceManager.getContainerMap().get(params.url);
            if (viewGroup == null) {
                return;
            }
            if (isShow) {
                viewGroup.setVisibility(View.VISIBLE);
            } else {
                viewGroup.setVisibility(View.GONE);
            }
        }
    }

    private void closeWeexDialog(WeexDialogParams params) {
        if (params != null && !TextUtils.isEmpty(params.url)) {
            String url = params.url;
            showOrHideWeexDialog(params, false);
            weexInstanceManager.removeInstance(url);
            weexInstanceManager.removeContainer(url);
        }
    }

    /**
     * 调整窗口大小
     *
     * @param params
     */
    private void resizeWeexDialog(WeexDialogParams params) {
        if (params != null) {
            int left = (int) (screenWidth * params.x);
            int top = (int) (screenHeight * params.y);
            int width = (int) (screenWidth * params.width);
            int height = (int) (screenHeight * params.height);
            ViewGroup container = weexInstanceManager.getContainerMap().get(params.url);
            //Log.d("dq", "resize WeexDialog left=" + left + ",top=" + top + ",width=" + width + ",heigth=" + height);
            if (container != null) {
                container.setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) container.getLayoutParams();
                layoutParams.leftMargin = left;
                layoutParams.topMargin = top;
                layoutParams.width = width;
                layoutParams.height = height;
                container.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * 创建Weex窗口
     *
     * @param params
     */
    private void createWeexDialog(WeexDialogParams params) {
        if (params != null) {
            String url = params.url;
            if (TextUtils.isEmpty(url)) {
                return;
            }
            weexInstanceManager.removeContainer(url);

            int left = (int) (screenWidth * params.x);
            int top = (int) (screenHeight * params.y);
            int width = (int) (screenWidth * params.width);
            int height = (int) (screenHeight * params.height);
            ViewGroup container = createWeexContainer(left, top, width, height);
            weexContainer.addView(container);
            weexInstanceManager.generateWeexPage(container, params);
        }
    }

    /**
     * 动态添加布局:FrameLayout
     *
     * @return weex容器
     */
    private RenderContainer createWeexContainer(int left, int top, int width, int height) {
        RenderContainer container = new RenderContainer(context);
        //FrameLayout container = new FrameLayout(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = left;
        layoutParams.topMargin = top;
        layoutParams.width = width;
        layoutParams.height = height;  //去掉这个高度则自适应weex设定的高度，但会闪烁
        container.setLayoutParams(layoutParams);
        return container;
    }

    @Override
    public void onResume() {
        super.onResume();
        weexInstanceManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            weexInstanceManager.onPause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        weexInstanceManager.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        weexInstanceManager.onDestroy();
        if (null != receiver) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {

    }
}