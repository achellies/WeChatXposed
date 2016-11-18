package com.achellies.android.wechatxposed.hook;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by achellies on 16/11/18.
 */

public class StartActivityHook extends BaseHook {
    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //hook 启动 activity android.app.Activity#startActivityForResult
        findAndHookMethod("android.app.Activity", lpparam.classLoader, "startActivityForResult", Intent.class, int.class, Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent it = (Intent) param.args[0];
                        Bundle bund = (Bundle) param.args[2];
                        String tag = "startXActivity";
                        getIntentAndBundle(it, bund, tag);
                    }
                }
        );
    }

    //处理 intent
    public void getIntentAndBundle(Intent it, Bundle bund, String tag) {
        HashMap<String, Object> intentMap = new HashMap<>();


        //取得 action
        if (it.getAction() != null) {
            intentMap.put("action", it.getAction());
        }

        //取得 data
        if (it.getDataString() != null) {
            intentMap.put("data", it.getDataString());
        }

        //取得 type
        if (it.getType() != null) {
            intentMap.put("type", it.getType());
        }

        //取得 Component
        if (it.getComponent() != null) {
            ComponentName cp = it.getComponent();
            intentMap.put("pkgName", cp.getPackageName());
        }
        int count = 0;
        if (it.getExtras() != null) {
            try {
                Bundle intentBundle = it.getExtras();
                if (intentBundle != null) {
                    Set<String> keySet = intentBundle.keySet();

                    for (String key : keySet) {
                        count++;
                        Object thisObject = intentBundle.get(key);
                        String thisClass = thisObject.getClass().getName();

                        if (thisObject instanceof ArrayList) {
                            ArrayList thisArrayList = (ArrayList) thisObject;
                            HashMap<String, String> valueMap = new HashMap<>();
                            for (Object thisArrayListObject : thisArrayList) {
                                valueMap.put("value", thisArrayListObject.toString());
                            }
                            intentMap.put("arrayList", valueMap);
                        } else {
                            if (thisObject instanceof Long && thisObject.toString().length() == 13) {
                                intentMap.put("longTime", key);
                            } else {
                                HashMap<String, String> valueMap = new HashMap<>();
                                valueMap.put(key, thisObject.toString());
                                intentMap.put(thisClass + count, valueMap);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
