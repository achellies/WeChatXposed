package com.achellies.android.wechatxposed.hook;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by achellies on 16/11/18.
 */
class WeChatRouterHook extends BaseHook {
    static final String TAG = "*****xxxxxx";
    static final long POST_DELAYED_TIME = 1000 * 3; // 低端机上启动速度比较慢

    Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (SystemSettings.getInstance().mOutputLog) {
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append(activity.getClass().getCanonicalName());
                stringBuilder.append("\n");
                stringBuilder.append(StartActivityHook.getIntentAndBundle(activity.getIntent()));

                Toast.makeText(activity, stringBuilder.toString(), Toast.LENGTH_LONG).show();

                Log.e(TAG, stringBuilder.toString());
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    @Override
    public void hook(final ClassLoader classLoader) throws Throwable {
        findAndHookMethod(WeChatSettings.WECHAT_APPLICATION, classLoader, "onCreate", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                final Application app = (Application) param.thisObject;

                app.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

                app.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new WeChatLoginHook().hook(app.getClassLoader());
                            new WeChatContactInfoHook().hook(app.getClassLoader());
                            new WeChatWebViewHook().hook(app.getClassLoader());
                            new WeChatSettingsHook().hook(app.getClassLoader());
                        } catch (Throwable ignore) {
                            ignore.printStackTrace();
                        }
                    }
                });
            }
        });

        findAndHookMethod(WeChatSettings.LAUNCHER_ACTIVITY, classLoader, "onResume", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        if (param.thisObject instanceof Activity) {
                            final Activity activity = (Activity) (param.thisObject);
                            final ViewGroup contentView = (ViewGroup) activity.getWindow().getDecorView();

                            final String x_action = activity.getIntent().getStringExtra(WeChatSettings.X_ACTION_KEY);
                            if (!TextUtils.isEmpty(x_action)) {
                                activity.getIntent().putExtra(WeChatSettings.X_ACTION_KEY, "");
                                contentView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (WeChatSettings.X_ACTION_LOGIN.contentEquals(x_action)) {
                                            String userName = activity.getIntent().getStringExtra("x_userName");
                                            String password = activity.getIntent().getStringExtra("x_password");

                                            Button btnLogin = findButton(contentView, "登录");
                                            if (btnLogin != null && btnLogin.getVisibility() == View.VISIBLE) {
//                                                btnLogin.performClick();
                                                Intent intent = new Intent();
                                                intent.setClassName(activity.getPackageName(), WeChatSettings.LOGIN_ACTIVITY);
                                                intent.putExtra("login_type", 1);
                                                intent.putExtra("x_userName", userName);
                                                intent.putExtra("x_password", password);
                                                activity.startActivity(intent);
                                            } else {
                                                Intent intent = new Intent();
                                                intent.setClassName(activity.getPackageName(), WeChatSettings.LOGIN_ACTIVITY);
                                                intent.putExtra("login_type", 1);
                                                intent.putExtra("x_userName", userName);
                                                intent.putExtra("x_password", password);
                                                activity.startActivity(intent);
                                            }
                                        } else if (WeChatSettings.X_ACTION_LOGOUT.contentEquals(x_action)) {
                                            Button btnLogin = findButton(contentView, "登录");
                                            if (btnLogin != null && btnLogin.getVisibility() == View.VISIBLE) {
                                                // 已经退出了登录
                                            } else {
                                                Intent intent = new Intent();
                                                intent.setClassName(activity.getPackageName(), WeChatSettings.SETTING_ACTIVITY);
                                                intent.putExtra("x_logout", 1);
                                                activity.startActivity(intent);
                                            }
                                        } else if (WeChatSettings.X_ACTION_STAR_CONTACT.contentEquals(x_action)) {
                                            final String contactUser = activity.getIntent().getStringExtra("x_Contact_User");

                                            Intent intent = new Intent();
                                            intent.setClassName(activity.getPackageName(), WeChatSettings.CONTACT_INFO_UI_ACTIVITY);

                                            intent.putExtra("Contact_User", contactUser);
                                            intent.putExtra("x_action_star", 1);
                                            activity.startActivity(intent);
                                        } else if (WeChatSettings.X_ACTION_START_WEBVIEW.contentEquals(x_action)) {
                                            final String url = activity.getIntent().getStringExtra("x_url");

                                            Intent intent = new Intent();
                                            intent.setClassName(activity.getPackageName(), WeChatSettings.WEBVIEW_ACTIVITY);

                                            intent.putExtra("shortUrl", url);
                                            intent.putExtra("rawUrl", url);
                                            activity.startActivity(intent);
                                        } else if (WeChatSettings.X_ACTION_NEARBY.contentEquals(x_action)) {
                                            Intent intent = new Intent();
                                            intent.setClassName(activity.getPackageName(), WeChatSettings.NEARBY_ACTIVITY);
                                            activity.startActivity(intent);
                                        }

                                    }
                                }, POST_DELAYED_TIME);
                            }
                        }
                    }
                }
        );
    }
}
