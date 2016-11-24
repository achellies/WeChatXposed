package com.achellies.android.wechatxposed.hook;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by achellies on 16/11/23.
 */
class WeChatWebViewHook extends BaseHook {
    @Override
    public void hook(ClassLoader classLoader) throws Throwable {
        findAndHookMethod(WeChatSettings.WEBVIEW_ACTIVITY, classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                final Activity activity = (Activity) param.thisObject;
                final ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();


                View webView = findView(contentView, "com.tencent.smtt.webkit.WebView");
                if (webView != null) {
                    int[] location = new int[2];
                    webView.getLocationOnScreen(location);

                    simulateScroll(location[0], location[1], location[0] + webView.getWidth() / 2, location[1] + webView.getHeight() / 2);
                }
            }
        });
    }
}
