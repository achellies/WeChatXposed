package com.achellies.android.wechatxposed.hook;

import android.content.ContentResolver;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by achellies on 16/11/16.
 */
class SystemInfoHook extends BaseHook {

    @Override
    public void hook(ClassLoader classLoader) throws Throwable {


//        if ("ro.product.board".contentEquals(property)) {
//        } else if ("ro.product.device".contentEquals(property)) {
//        } else if ("ro.build.display.id".contentEquals(property)) {
//        } else if ("ro.build.host".contentEquals(property)) {
//        } else if ("ro.product.manufacturer".contentEquals(property)) {
//        } else if ("ro.product.model".contentEquals(property)) {
//        } else if ("ro.product.name".contentEquals(property)) {
//        } else if ("ro.build.tags".contentEquals(property)) {
//        } else if ("ro.build.type".contentEquals(property)) {
//        } else if ("ro.build.user".contentEquals(property)) {
//        }
//        stringBuilder.append("] BOARD:[" + Build.BOARD);
//        stringBuilder.append("] DEVICE:[" + Build.DEVICE);
//        stringBuilder.append("] DISPLAY:[" + Build.DISPLAY);
//        stringBuilder.append("] FINGERPRINT:[" + Build.FINGERPRINT);
//        stringBuilder.append("] HOST:[" + Build.HOST);
//        stringBuilder.append("] MANUFACTURER:[" + Build.MANUFACTURER);
//        stringBuilder.append("] MODEL:[" + Build.MODEL);
//        stringBuilder.append("] PRODUCT:[" + Build.PRODUCT);
//        stringBuilder.append("] TAGS:[" + Build.TAGS);
//        stringBuilder.append("] TYPE:[" + Build.TYPE);
//        stringBuilder.append("] USER:[" + Build.USER + "]");

        XposedHelpers.findAndHookMethod("android.os.SystemProperties", classLoader, "get", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setStaticObjectField(Build.class, "BOARD", SystemSettings.getInstance().mBuildBoard);
                XposedHelpers.setStaticObjectField(Build.class, "DEVICE", SystemSettings.getInstance().mBuildDevice);
                XposedHelpers.setStaticObjectField(Build.class, "DISPLAY", SystemSettings.getInstance().mBuildDisplay);
                XposedHelpers.setStaticObjectField(Build.class, "FINGERPRINT", SystemSettings.getInstance().mBuildFingerprint);
                XposedHelpers.setStaticObjectField(Build.class, "HOST", SystemSettings.getInstance().mBuildHost);
                XposedHelpers.setStaticObjectField(Build.class, "MANUFACTURER", SystemSettings.getInstance().mBuildManufacturer);
                XposedHelpers.setStaticObjectField(Build.class, "MODEL", SystemSettings.getInstance().mBuildModel);
                XposedHelpers.setStaticObjectField(Build.class, "PRODUCT", SystemSettings.getInstance().mBuildProduct);
                XposedHelpers.setStaticObjectField(Build.class, "TAGS", SystemSettings.getInstance().mBuildTags);
                XposedHelpers.setStaticObjectField(Build.class, "TYPE", SystemSettings.getInstance().mBuildType);
                XposedHelpers.setStaticObjectField(Build.class, "USER", SystemSettings.getInstance().mBuildUser);

                super.beforeHookedMethod(param);
            }
        });

        // Device Id
        Utils.hook_method(TelephonyManager.class.getCanonicalName(), classLoader, "getDeviceId", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(SystemSettings.getInstance().mIMEI);
            }
        });


        // Sim Serial Number
        Utils.hook_method(TelephonyManager.class.getCanonicalName(), classLoader, "getSimSerialNumber", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(SystemSettings.getInstance().mSimSerialNumber);
            }
        });


        // Android Id
        Utils.hook_method(/*Settings.Secure.class.getCanonicalName()*/"android.provider.Settings$Secure", classLoader, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args.length == 2 && (param.args[1] instanceof String)) {
                    if (Settings.Secure.ANDROID_ID.contentEquals((String) param.args[1])) {
                        param.setResult(SystemSettings.getInstance().mAndroidId);
                    } else {
                        super.afterHookedMethod(param);
                    }
                } else {
                    super.afterHookedMethod(param);
                }
            }
        });

        hookMacAddress(classLoader);
        hookLocation(classLoader);
    }

    private void hookMacAddress(ClassLoader classLoader) {
        Utils.hook_method(WifiInfo.class.getCanonicalName(), classLoader, "getMacAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(SystemSettings.getInstance().mMacAddress);
            }
        });

        Utils.hook_method(NetworkInterface.class.getCanonicalName(), classLoader, "getHardwareAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(Utils.hexStringToByteArray(SystemSettings.getInstance().mMacAddress.replace(":", "")));
            }
        });
    }

    private void hookLocation(ClassLoader classLoader) {

        Utils.hook_method(WifiManager.class.getCanonicalName(), classLoader, "getScanResults", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param)
                    throws Throwable {
                param.setResult(null);
            }
        });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                "getCellLocation", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                });

        XposedHelpers.findAndHookMethod("android.telephony.PhoneStateListener", classLoader,
                "onCellLocationChanged", CellLocation.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                });

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getPhoneCount", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(1);
                        }
                    });
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getNeighboringCellInfo", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(new ArrayList<>());
                        }
                    });
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getAllCellInfo", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(null);
                        }
                    });
            XposedHelpers.findAndHookMethod("android.telephony.PhoneStateListener", classLoader,
                    "onCellInfoChanged", List.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(null);
                        }
                    });
        }

        Utils.hook_methods(LocationManager.class.getCanonicalName(), "getLastKnownLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(SystemSettings.getInstance().mLatitude);
                l.setLongitude(SystemSettings.getInstance().mLongitude);

                l.setAccuracy(100f);
                l.setTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                }
                param.setResult(l);
            }
        });

        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "getTypeName", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult("WIFI");
                    }
                });
        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "isConnectedOrConnecting", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }
                });

        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "isConnected", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }
                });

        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", classLoader,
                "isAvailable", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }
                });

        XposedHelpers.findAndHookMethod("android.telephony.CellInfo", classLoader,
                "isRegistered", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(true);
                    }
                });

        XposedBridge.hookAllMethods(LocationManager.class, "getProviders", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("gps");
                param.setResult(arrayList);
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getBestProvider", Criteria.class, Boolean.TYPE, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("gps");
            }
        });

        XposedHelpers.findAndHookMethod("android.location.LocationManager", classLoader,
                "getGpsStatus", GpsStatus.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        GpsStatus gss = (GpsStatus) param.getResult();
                        if (gss == null)
                            return;

                        Class<?> clazz = GpsStatus.class;
                        Method m = null;
                        for (Method method : clazz.getDeclaredMethods()) {
                            if (method.getName().equals("setStatus")) {
                                if (method.getParameterTypes().length > 1) {
                                    m = method;
                                    break;
                                }
                            }
                        }
                        if (m == null)
                            return;

                        //access the private setStatus function of GpsStatus
                        m.setAccessible(true);

                        //make the apps belive GPS works fine now
                        int svCount = 5;
                        int[] prns = {1, 2, 3, 4, 5};
                        float[] snrs = {0, 0, 0, 0, 0};
                        float[] elevations = {0, 0, 0, 0, 0};
                        float[] azimuths = {0, 0, 0, 0, 0};
                        int ephemerisMask = 0x1f;
                        int almanacMask = 0x1f;

                        //5 satellites are fixed
                        int usedInFixMask = 0x1f;

                        XposedHelpers.callMethod(gss, "setStatus", svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                        param.args[0] = gss;
                        param.setResult(gss);
                        try {
                            m.invoke(gss, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                            param.setResult(gss);
                        } catch (Exception e) {
                            XposedBridge.log(e);
                        }
                    }
                });

        for (Method method : LocationManager.class.getDeclaredMethods()) {
            if (method.getName().equals("requestLocationUpdates")
                    && !Modifier.isAbstract(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())) {
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args.length >= 4 && (param.args[3] instanceof LocationListener)) {

                            LocationListener ll = (LocationListener) param.args[3];

                            Class<?> clazz = LocationListener.class;
                            Method m = null;
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.getName().equals("onLocationChanged") && !Modifier.isAbstract(method.getModifiers())) {
                                    m = method;
                                    break;
                                }
                            }

                            Location l = new Location(LocationManager.GPS_PROVIDER);
                            l.setLatitude(SystemSettings.getInstance().mLatitude);
                            l.setLongitude(SystemSettings.getInstance().mLongitude);
                            l.setAccuracy(10.00f);
                            l.setTime(System.currentTimeMillis());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                            }
                            XposedHelpers.callMethod(ll, "onLocationChanged", l);
                            try {
                                if (m != null) {
                                    m.invoke(ll, l);
                                }
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                        }
                    }
                });
            }

            if (method.getName().equals("requestSingleUpdate ")
                    && !Modifier.isAbstract(method.getModifiers())
                    && Modifier.isPublic(method.getModifiers())) {
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args.length >= 3 && (param.args[1] instanceof LocationListener)) {

                            LocationListener ll = (LocationListener) param.args[3];

                            Class<?> clazz = LocationListener.class;
                            Method m = null;
                            for (Method method : clazz.getDeclaredMethods()) {
                                if (method.getName().equals("onLocationChanged") && !Modifier.isAbstract(method.getModifiers())) {
                                    m = method;
                                    break;
                                }
                            }

                            try {
                                if (m != null) {
                                    Location l = new Location(LocationManager.GPS_PROVIDER);
                                    l.setLatitude(SystemSettings.getInstance().mLatitude);
                                    l.setLongitude(SystemSettings.getInstance().mLongitude);
                                    l.setAccuracy(100f);
                                    l.setTime(System.currentTimeMillis());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                                    }
                                    m.invoke(ll, l);
                                }
                            } catch (Exception e) {
                                XposedBridge.log(e);
                            }
                        }
                    }
                });
            }
        }
    }
}
