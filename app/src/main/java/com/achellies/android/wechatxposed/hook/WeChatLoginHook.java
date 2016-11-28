package com.achellies.android.wechatxposed.hook;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by achellies on 16/11/18.
 */

/**
 * 微信登录的Hook, 到未登录时自动跳转到用户名和密码输入界面，并进行自动输入和自动点击登录按钮，进行登录
 */
class WeChatLoginHook extends BaseHook {

    @Override
    public void hook(ClassLoader classLoader) throws Throwable {
//        findAndHookMethod(WeChatSettings.LOGIN_HISTORY_UI_ACTIVITY, classLoader, "onResume", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//
//                Activity activity = (Activity)param.thisObject;
//
//                Intent intent = new Intent();
//                intent.setClassName(activity.getPackageName(), WeChatSettings.LOGIN_ACTIVITY);
//                intent.putExtra("login_type", 1);
//                activity.startActivity(intent);
//            }
//        });
//
//        findAndHookMethod(WeChatSettings.MOBILE_INPUT_ACTIVITY, classLoader, "onResume", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//
//                if (param.thisObject instanceof Activity) {
//                    Activity activity = (Activity) (param.thisObject);
//                    ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();
//
////                    Button btnLogin = findButton(contentView, "使用其他方式登录");
////                    if (btnLogin != null && btnLogin.getVisibility() == View.VISIBLE) {
////                        btnLogin.performClick();
////                    }
//                    Intent intent = new Intent();
//                    intent.setClassName(activity.getPackageName(), WeChatSettings.LOGIN_ACTIVITY);
//                    intent.putExtra("login_type", 1);
//                    activity.startActivity(intent);
//                }
//            }
//        });

        findAndHookMethod(WeChatSettings.LOGIN_ACTIVITY, classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                if (param.thisObject instanceof Activity) {
                    Activity activity = (Activity) (param.thisObject);
                    final ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();
                    Handler handler = new Handler(activity.getMainLooper());

                    final String userName = activity.getIntent().getStringExtra("x_userName");
                    final String password = activity.getIntent().getStringExtra("x_password");

                    activity.getIntent().putExtra("x_userName", "");
                    activity.getIntent().putExtra("x_password", "");

                    // 找到用户名、密码，然后输入
                    List<EditText> editTexts = findEditText(contentView);
                    if (editTexts.size() == 2) {
                        final EditText etUserName = editTexts.get(0);
                        final EditText etPassword = editTexts.get(1);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                etUserName.setText(userName);
                            }
                        });
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                etPassword.setText(password);
                            }
                        });
                    } else if (editTexts.size() == 1) {
                        final EditText etPassword = editTexts.get(0);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                etPassword.setText(password);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                // 找到登录按钮，然后单击登录
                                Button btnLogin = findButton(contentView, "登录");
                                if (btnLogin != null) {
                                    btnLogin.performClick();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
