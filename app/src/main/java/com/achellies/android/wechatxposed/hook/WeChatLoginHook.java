package com.achellies.android.wechatxposed.hook;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by achellies on 16/11/18.
 */

/**
 * 微信登录的Hook, 到未登录时自动跳转到用户名和密码输入界面，并进行自动输入和自动点击登录按钮，进行登录
 */
public class WeChatLoginHook extends BaseHook {

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        findAndHookMethod(WeChatSettings.MOBILE_INPUT_ACTIVITY, lpparam.classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (param.thisObject instanceof Activity) {
                    Activity activity = (Activity) (param.thisObject);
                    ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();

                    Button btnLogin = findButton(contentView, "使用其他方式登录");
                    if (btnLogin != null && btnLogin.getVisibility() == View.VISIBLE) {
                        btnLogin.performClick();
                    }
                }
            }
        });

        findAndHookMethod(WeChatSettings.LOGIN_ACTIVITY, lpparam.classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (param.thisObject instanceof Activity) {
                    Activity activity = (Activity) (param.thisObject);
                    ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();

                    // 找到用户名、密码，然后输入
                    List<EditText> editTexts = findEditText(contentView);
                    if (editTexts.size() == 2) {
                        EditText etUserName = editTexts.get(0);
                        EditText etPassword = editTexts.get(1);
                        etUserName.setText(WeChatSettings.getInstance().mUserName);
                        etPassword.setText(WeChatSettings.getInstance().mPassword);
                    }
                    // 找到登录按钮，然后单击登录
                    Button btnLogin = findButton(contentView, "登录");
                    if (btnLogin != null) {
                        btnLogin.performClick();
                    }
                }
            }
        });
    }

    List<EditText> findEditText(ViewGroup contentView) {
        List<EditText> editTexts = new ArrayList<EditText>();
        int count = contentView.getChildCount();
        if (count == 0) {
            return editTexts;
        }
        for (int index = 0; index < count; ++index) {
            View childView = contentView.getChildAt(index);
            if (childView instanceof ViewGroup) {
                editTexts.addAll(findEditText((ViewGroup) childView));
            } else if (childView instanceof EditText) {
                EditText et = (EditText) childView;
                editTexts.add(et);
            }
        }
        return editTexts;
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
