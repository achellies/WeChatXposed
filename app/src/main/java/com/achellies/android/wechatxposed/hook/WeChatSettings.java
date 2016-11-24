package com.achellies.android.wechatxposed.hook;

import android.support.annotation.StringDef;

/**
 * Created by achellies on 16/11/18.
 */
class WeChatSettings {

    /**
     * X_ACTION 定义
     */
    @StringDef({X_ACTION_LOGIN, X_ACTION_LOGOUT, X_ACTION_STAR_CONTACT, X_ACTION_START_WEBVIEW})
    public @interface X_Action {
    }

    static final String X_ACTION_KEY = "x_action";


    /**
     * 登录的action
     */
    static final String X_ACTION_LOGIN = "x_action_login";

    /**
     * 退出登录的action
     */
    static final String X_ACTION_LOGOUT = "x_action_logout";

    /**
     * 关注某个公共账号
     */
    static final String X_ACTION_STAR_CONTACT = "x_action_star_contact";

    /**
     * 启动微信的浏览器阅读一篇文章
     */
    static final String X_ACTION_START_WEBVIEW = "x_action_webview";

    /**
     * 进入附近的人页面
     */
    static final String X_ACTION_NEARBY = "x_action_nearby";

    /**
     * 微信的包名
     */
    static final String WECHAT_PACKAGE_NAME = "com.tencent.mm";

    /**
     * 微信的Application
     */
    static final String WECHAT_APPLICATION = "com.tencent.tinker.loader.app.TinkerApplication";

    /**
     * 微信启动页面
     */
    static final String LAUNCHER_ACTIVITY = "com.tencent.mm.ui.LauncherUI";

    /**
     * 微信登录时手机号输入界面
     */
    static final String MOBILE_INPUT_ACTIVITY = "com.tencent.mm.ui.account.mobile.MobileInputUI";

    /**
     * 微信登录时登录历史界面
     */
    static final String LOGIN_HISTORY_UI_ACTIVITY = "com.tencent.mm.ui.account.LoginHistoryUI";

    /**
     * 微信使用账户和密码登录的界面
     */
    static final String LOGIN_ACTIVITY = "com.tencent.mm.ui.account.LoginUI";

    /**
     * 公共号的界面，这里可以选择"查看历史消息"， "进入公共账号"， '关注等等'
     */
    static final String CONTACT_INFO_UI_ACTIVITY = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";

    /**
     * 微信的WebView相关的Activity
     */
    static final String WEBVIEW_ACTIVITY = "com.tencent.mm.plugin.webview.ui.tools.WebViewUI";

    /**
     * 设置界面
     */
    static final String SETTING_ACTIVITY = "com.tencent.mm.plugin.setting.ui.setting.SettingsUI";

    /**
     * 附近的人界面
     */
    static final String NEARBY_ACTIVITY = "com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI";


    private WeChatSettings() {
        super();
    }
}
