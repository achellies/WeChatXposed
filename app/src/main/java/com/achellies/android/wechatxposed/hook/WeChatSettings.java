package com.achellies.android.wechatxposed.hook;

import android.support.annotation.StringDef;

/**
 * Created by achellies on 16/11/18.
 */

public class WeChatSettings {
    private static WeChatSettings sInstance;


    /**
     * X_ACTION 定义
     */
    @StringDef({X_ACTION_LOGIN})
    public @interface X_Action {
    }

    public static final String X_ACTION_KEY = "x_action";

    public static final String X_ACTION_LOGIN = "x_action_login";

    /**
     * 微信的包名
     */
    public static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";

    /**
     * 微信启动页面
     */
    public static final String LAUNCHER_ACTIVITY = "com.tencent.mm.ui.LauncherUI";

    /**
     * 微信登录时手机号输入界面
     */
    public static final String MOBILE_INPUT_ACTIVITY = "com.tencent.mm.ui.account.mobile.MobileInputUI";

    /**
     * 微信使用账户和密码登录的界面
     */
    public static final String LOGIN_ACTIVITY = "com.tencent.mm.ui.account.LoginUI";

    /**
     * 公共号的页面
     */

    public synchronized static WeChatSettings getInstance() {
        if (sInstance == null) {
            sInstance = new WeChatSettings();
        }
        return sInstance;
    }

    public String mUserName = "userName";
    public String mPassword = "password";

    private WeChatSettings() {
        super();
    }
}
