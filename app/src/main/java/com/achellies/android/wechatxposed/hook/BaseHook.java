package com.achellies.android.wechatxposed.hook;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by achellies on 16/11/18.
 */

public abstract class BaseHook {

    public abstract void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable;
}
