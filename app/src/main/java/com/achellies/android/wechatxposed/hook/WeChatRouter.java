package com.achellies.android.wechatxposed.hook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by achellies on 16/11/24.
 */

public class WeChatRouter {
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
                intent.putExtra(WeChatSettings.X_ACTION_KEY, WeChatSettings.X_ACTION_LOGIN);
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
                intent.putExtra(WeChatSettings.X_ACTION_KEY, WeChatSettings.X_ACTION_LOGOUT);
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
                intent.putExtra(WeChatSettings.X_ACTION_KEY, WeChatSettings.X_ACTION_STAR_CONTACT);
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
                intent.putExtra(WeChatSettings.X_ACTION_KEY, WeChatSettings.X_ACTION_START_WEBVIEW);
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
                intent.putExtra(WeChatSettings.X_ACTION_KEY, WeChatSettings.X_ACTION_NEARBY);
                activity.startActivity(intent);
            }
        });
    }
}
