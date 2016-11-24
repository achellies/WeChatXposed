package com.achellies.android.wechatxposed.hook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by achellies on 16/11/18.
 */

public class Main implements IXposedHookLoadPackage {
    public static final String WEICHAT_XPOSED_PACKAGE_NAME = "com.achellies.android.wechatxposed";
    public static final String XPOSED_INSTALLED = "de.robv.android.xposed.installer";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!(lpparam.packageName.contentEquals(WeChatSettings.WECHAT_PACKAGE_NAME) || lpparam.packageName.contentEquals(WEICHAT_XPOSED_PACKAGE_NAME))) {
            return;
        }

        new NoTraceHook().hook(lpparam.classLoader);
        new SystemInfoHook().hook(lpparam.classLoader);
        new StartActivityHook().hook(lpparam.classLoader);

        if (lpparam.packageName.contentEquals(WeChatSettings.WECHAT_PACKAGE_NAME)) {
            new WeChatRouterHook().hook(lpparam.classLoader);
        }
    }
}
