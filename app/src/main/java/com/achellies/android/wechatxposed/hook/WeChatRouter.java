package com.achellies.android.wechatxposed.hook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringDef;

/**
 * Created by achellies on 16/11/24.
 */

public class WeChatRouter {
    /**
     * X_ACTION 定义
     */
    @StringDef({X_ACTION_LOGIN, X_ACTION_LOGOUT, X_ACTION_STAR_CONTACT, X_ACTION_START_WEBVIEW})
    public @interface X_Action {
    }

    public static final String X_ACTION_KEY = "x_action";

    public static final String X_ACTION_PARAM = "x_param";

    /**
     * 登录的action
     */
    public static final String X_ACTION_LOGIN = "x_action_login";

    /**
     * 退出登录的action
     */
    public static final String X_ACTION_LOGOUT = "x_action_logout";

    /**
     * 关注某个公共账号
     */
    public static final String X_ACTION_STAR_CONTACT = "x_action_star_contact";

    /**
     * 启动微信的浏览器阅读一篇文章
     */
    public static final String X_ACTION_START_WEBVIEW = "x_action_webview";

    /**
     * 进入附近的人页面
     */
    public static final String X_ACTION_NEARBY = "x_action_nearby";

    private static WeChatRouter sInstance = null;

    public final SettingsHelper mSharedPreferences;

    public static WeChatRouter getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WeChatRouter(context);
        }

        return sInstance;
    }

    private WeChatRouter(Context context) {
        super();
        mSharedPreferences = new SettingsHelper(context, "com.achellies.android.wechatxposed");
    }

    public void startLoginActivity(final Activity activity, final String userName, final String password) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = activity.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(WeChatSettings.WECHAT_PACKAGE_NAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(X_ACTION_KEY, X_ACTION_LOGIN);
                intent.putExtra("x_userName", userName);
                intent.putExtra("x_password", password);
                activity.startActivity(intent);
            }
        });
    }

    public void startLogoutActivity(final Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = activity.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(WeChatSettings.WECHAT_PACKAGE_NAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(X_ACTION_KEY, X_ACTION_LOGOUT);
                activity.startActivity(intent);
            }
        });
    }

    public void startStarContactActivity(final Activity activity, final String contactUserId) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = activity.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(WeChatSettings.WECHAT_PACKAGE_NAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(X_ACTION_KEY, X_ACTION_STAR_CONTACT);
                intent.putExtra("x_Contact_User", contactUserId);
                activity.startActivity(intent);
            }
        });
    }

    public void startWebViewActivity(final Activity activity, final String url) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                PackageManager pm = activity.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(WeChatSettings.WECHAT_PACKAGE_NAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(X_ACTION_KEY, X_ACTION_START_WEBVIEW);
                intent.putExtra("x_url", url);
                activity.startActivity(intent);
            }
        });
    }

    public void startNearByActivity(final Activity activity, final double longitude, final double latitude) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                mSharedPreferences.setString("latitude", Double.toString(latitude));
                mSharedPreferences.setString("longitude", Double.toString(longitude));

                PackageManager pm = activity.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(WeChatSettings.WECHAT_PACKAGE_NAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(X_ACTION_KEY, X_ACTION_NEARBY);
                activity.startActivity(intent);
            }
        });
    }
}
