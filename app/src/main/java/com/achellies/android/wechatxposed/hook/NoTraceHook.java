package com.achellies.android.wechatxposed.hook;

import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by achellies on 16/11/18.
 */
class NoTraceHook extends BaseHook {
    @Override
    public void hook(ClassLoader classLoader) throws Throwable {
        findAndHookMethod("android.app.ApplicationPackageManager", classLoader, "getInstalledApplications", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List<ApplicationInfo> applicationList = (List) param.getResult();
                List<ApplicationInfo> resultapplicationList = new ArrayList<>();
                for (ApplicationInfo applicationInfo : applicationList) {
                    String packageName = applicationInfo.packageName;
                    if (isTarget(packageName)) {
                    } else {
                        resultapplicationList.add(applicationInfo);
                    }
                }
                param.setResult(resultapplicationList);
            }
        });

        findAndHookMethod("android.app.ApplicationPackageManager", classLoader, "getInstalledPackages", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List<PackageInfo> packageInfoList = (List) param.getResult();
                List<PackageInfo> resultpackageInfoList = new ArrayList<>();

                for (PackageInfo packageInfo : packageInfoList) {
                    String packageName = packageInfo.packageName;
                    if (isTarget(packageName)) {
                    } else {
                        resultpackageInfoList.add(packageInfo);
                    }
                }
                param.setResult(resultpackageInfoList);
            }
        });

//        findAndHookMethod("android.app.ApplicationPackageManager", classLoader, "getPackageInfo", String.class, int.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                String packageName = (String) param.args[0];
//                if (isTarget(packageName)) {
//                    param.args[0] = WeChatSettings.WECHAT_PACKAGE_NAME;
//                }
//            }
//        });
//
//        findAndHookMethod("android.app.ApplicationPackageManager", classLoader, "getApplicationInfo", String.class, int.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                String packageName = (String) param.args[0];
//                if (isTarget(packageName)) {
//                    param.args[0] = WeChatSettings.WECHAT_PACKAGE_NAME;
//                }
//            }
//        });

        findAndHookMethod("android.app.ActivityManager", classLoader, "getRunningServices", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List<ActivityManager.RunningServiceInfo> serviceInfoList = (List) param.getResult();
                List<ActivityManager.RunningServiceInfo> resultList = new ArrayList<>();

                for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceInfoList) {
                    String serviceName = runningServiceInfo.process;
                    if (isTarget(serviceName)) {
                    } else {
                        resultList.add(runningServiceInfo);
                    }
                }
                param.setResult(resultList);
            }
        });

        findAndHookMethod("android.app.ActivityManager", classLoader, "getRunningTasks", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                List<ActivityManager.RunningTaskInfo> serviceInfoList = (List) param.getResult();
                List<ActivityManager.RunningTaskInfo> resultList = new ArrayList<>();

                for (ActivityManager.RunningTaskInfo runningTaskInfo : serviceInfoList) {
                    String taskName = runningTaskInfo.baseActivity.flattenToString();
                    if (isTarget(taskName)) {
                    } else {
                        resultList.add(runningTaskInfo);
                    }
                }
                param.setResult(resultList);
            }
        });

        findAndHookMethod("android.app.ActivityManager", classLoader, "getRunningAppProcesses", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = (List) param.getResult();
                List<ActivityManager.RunningAppProcessInfo> resultList = new ArrayList<>();

                if (runningAppProcessInfos != null) {
                    for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {
                        String processName = runningAppProcessInfo.processName;
                        if (isTarget(processName)) {
                        } else {
                            resultList.add(runningAppProcessInfo);
                        }
                    }
                }
                param.setResult(resultList);
            }
        });
    }

    private boolean isTarget(String name) {
        return name.contentEquals(Main.WEICHAT_XPOSED_PACKAGE_NAME) || name.contentEquals(Main.XPOSED_INSTALLED) || name.contains("achellies") || name.contains("xposed");
    }
}
