package com.achellies.android.wechatxposed.hook;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by achellies on 16/11/18.
 */

public class WeChatRouterHook extends BaseHook {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        findAndHookMethod(WeChatSettings.LAUNCHER_ACTIVITY, lpparam.classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (param.thisObject instanceof Activity) {
                    Activity activity = (Activity) (param.thisObject);
                    final ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();

                    Intent intent = activity.getIntent();

                    String x_action = intent.getStringExtra(WeChatSettings.X_ACTION_KEY);
                    if (WeChatSettings.X_ACTION_LOGIN.contentEquals(x_action)) {
                        contentView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Button btnLogin = findButton(contentView, "登录");
                                if (btnLogin != null && btnLogin.getVisibility() == View.VISIBLE) {
                                    btnLogin.performClick();
                                }
                            }
                        }, 1000);
                    }
                }
            }
        });

    }

    Button findButton(ViewGroup contentView, String text) {
        int count = contentView.getChildCount();
        if (count == 0) {
            return null;
        }
        for (int index = 0; index < count; ++index) {
            View childView = contentView.getChildAt(index);
            if (childView instanceof ViewGroup) {
                Button btn = findButton((ViewGroup) childView, text);
                if (btn != null) {
                    return btn;
                }
            } else if (childView instanceof Button) {
                Button btn = (Button) childView;
                if (btn.getText() != null && btn.getText().toString().contentEquals(text)) {
                    return btn;
                }
            }
        }
        return null;
    }
}
