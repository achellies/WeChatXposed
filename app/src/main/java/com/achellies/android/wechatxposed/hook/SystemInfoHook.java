package com.achellies.android.wechatxposed.hook;

import android.content.ContentResolver;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.achellies.android.wechatxposed.SettingsHelper;

import java.lang.reflect.Method;
import java.net.NetworkInterface;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by achellies on 16/11/16.
 */

public class SystemInfoHook extends BaseHook {
    private double mLatitude = 39.9912249d; //经度
    private double mLongitude = 116.3020556d; //纬度

    private String mIMEI = "achellies";
    private String mAndroidId = "achellies";
    private String mSimSerialNumber = "achellies";

    private String mMacAddress = "achellies";

    private SettingsHelper mSharedPreferences;

    private void fetchHookInfo() {
        if (mSharedPreferences == null) {
            mSharedPreferences = new SettingsHelper(Main.WEICHAT_XPOSED_PACKAGE_NAME);
        }
        if (mSharedPreferences != null) {
            mSharedPreferences.reload();
            mLatitude = Double.parseDouble(mSharedPreferences.getString("latitude", "0"));
            mLongitude = Double.parseDouble(mSharedPreferences.getString("longitude", "0"));
            mIMEI = mSharedPreferences.getString("imei", "");
            mAndroidId = mSharedPreferences.getString("androidId", "");
            mSimSerialNumber = mSharedPreferences.getString("simSerialNumber", "");
            mMacAddress = mSharedPreferences.getString("mac", "");
        }
    }

    @Override
    public void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // Device Id
        Utils.hook_method(TelephonyManager.class.getCanonicalName(), lpparam.classLoader, "getDeviceId", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                fetchHookInfo();
                param.setResult(mIMEI);
            }
        });


        // Sim Serial Number
        Utils.hook_method(TelephonyManager.class.getCanonicalName(), lpparam.classLoader, "getSimSerialNumber", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                fetchHookInfo();
                param.setResult(mSimSerialNumber);
            }
        });


        // Android Id
        Utils.hook_method(/*Settings.Secure.class.getCanonicalName()*/"android.provider.Settings$Secure", lpparam.classLoader, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                fetchHookInfo();
                if (param.args.length == 2 && (param.args[1] instanceof String)) {
                    if (Settings.Secure.ANDROID_ID.contentEquals((String) param.args[1])) {
                        param.setResult(mAndroidId);
                    } else {
                        super.afterHookedMethod(param);
                    }
                } else {
                    super.afterHookedMethod(param);
                }
            }
        });

        hookMacAddress(lpparam);
        hookLocation(lpparam);
    }

    private void hookMacAddress(XC_LoadPackage.LoadPackageParam lpparam) {
        Utils.hook_method(WifiInfo.class.getCanonicalName(), lpparam.classLoader, "getMacAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                fetchHookInfo();
                param.setResult(mMacAddress);
            }
        });

        Utils.hook_method(NetworkInterface.class.getCanonicalName(), lpparam.classLoader, "getHardwareAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                fetchHookInfo();
                param.setResult(Utils.hexStringToByteArray(mMacAddress.replace(":", "")));
            }
        });
    }

    private void hookLocation(XC_LoadPackage.LoadPackageParam lpparam) {
        //定位
        Utils.hook_method(TelephonyManager.class.getCanonicalName(), lpparam.classLoader, "getCellLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                fetchHookInfo();
                param.setResult(null);
            }
        });

        Utils.hook_method(WifiManager.class.getCanonicalName(), lpparam.classLoader, "getScanResults", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param)
                    throws Throwable {
                fetchHookInfo();
                param.setResult(null);
            }
        });

        Utils.hook_method(TelephonyManager.class.getCanonicalName(), lpparam.classLoader, "getNeighboringCellInfo", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param)
                    throws Throwable {
                fetchHookInfo();
                param.setResult(null);
            }
        });

        Utils.hook_methods(LocationManager.class.getCanonicalName(), "getLastKnownLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                fetchHookInfo();
                Location l = new Location(LocationManager.PASSIVE_PROVIDER);
                double lo = mLatitude;
                double la = mLongitude;
                l.setLatitude(la);
                l.setLongitude(lo);
                param.setResult(l);
            }
        });

        Utils.hook_methods(LocationManager.class.getCanonicalName(), "getGpsStatus", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                fetchHookInfo();
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

                try {
                    if (m != null) {
                        m.invoke(gss, svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
                        param.setResult(gss);
                    }
                } catch (Exception e) {
                    XposedBridge.log(e);
                }
            }
        });

        Utils.hook_methods(LocationManager.class.getCanonicalName(), "requestLocationUpdates", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                fetchHookInfo();

                param.setResult(null);

                if (param.args.length == 4 && (param.args[0] instanceof String) && (param.args[3] instanceof LocationListener)) {
                    LocationListener ll = (LocationListener) param.args[3];
                    Class<?> clazz = LocationListener.class;
                    Method m = null;
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.getName().equals("onLocationChanged")) {
                            m = method;
                            break;
                        }
                    }

                    try {
                        if (m != null) {
                            Object[] args = new Object[1];
                            Location l = new Location(LocationManager.PASSIVE_PROVIDER);
                            double lo = mLatitude;
                            double la = mLongitude;
                            l.setLatitude(la);
                            l.setLongitude(lo);
                            args[0] = l;
                            m.invoke(ll, args);
                        }
                    } catch (Exception e) {
                        XposedBridge.log(e);
                    }
                }
            }
        });
    }
}
