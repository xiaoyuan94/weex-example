package com.xxyuan.weexandroid.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.common.WXRenderStrategy;
import com.taobao.weex.utils.WXFileUtils;
import com.xxyuan.weexandroid.R;
import com.xxyuan.weexandroid.weex.Constants;
import com.xxyuan.weexandroid.weex.module.User;

import java.util.HashMap;
import java.util.Map;



public class WeexFragment extends Fragment implements IWXRenderListener {


    private String mBundleUrl;
    private FrameLayout mContainer;
    private WXSDKInstance mWXSDKInstance;
    private Context context;

    public WeexFragment() {
    }

    public static WeexFragment newInstance(String bundleUrl) {
        WeexFragment fragment = new WeexFragment();
        Bundle args = new Bundle();
        args.putString(WXSDKInstance.BUNDLE_URL, bundleUrl);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        View view = View.inflate(context, R.layout.fragment_weex, null);
        mContainer = (FrameLayout) view.findViewById(R.id.fragment_container);

        mBundleUrl = getArguments() != null ? getArguments().getString(WXSDKInstance.BUNDLE_URL) : null;
        mWXSDKInstance = new WXSDKInstance(getActivity());
        mWXSDKInstance.registerRenderListener(this);
        HashMap<String, Object> options = new HashMap<>();
        options.put(WXSDKInstance.BUNDLE_URL, mBundleUrl);
        if (mBundleUrl.startsWith(Constants.LOCAL_FILE_SCHEMA)) {
            String jsName = mBundleUrl.replace(Constants.LOCAL_FILE_SCHEMA, "");
            mWXSDKInstance.render("FragmentWeex", WXFileUtils.loadAsset(jsName, context), null, null, WXRenderStrategy.APPEND_ASYNC);
        } else {
            mWXSDKInstance.renderByUrl("FragmentWeex", mBundleUrl, options, null, WXRenderStrategy.APPEND_ASYNC);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContainer.getParent() != null) {
            ((ViewGroup) mContainer.getParent()).removeView(mContainer);
        }
        return mContainer;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWXSDKInstance != null) {
            User user = new User("duqian2010@gmail.com", 28);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("data", "on resume");
            params.put("user", user);
            mWXSDKInstance.fireGlobalEventCallback("eventB", params);

            mWXSDKInstance.onActivityResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mWXSDKInstance != null) {
            mWXSDKInstance.onActivityStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mWXSDKInstance != null) {
                mWXSDKInstance.onActivityDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {
        mContainer.addView(view);
    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {

    }
}
