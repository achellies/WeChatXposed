package com.achellies.android.wechatxposed.hook;

/**
 * Created by achellies on 16/11/22.
 */
class SystemSettings {
    private static SystemSettings sInstance;

    public boolean mOutputLog = false;
    public double mLatitude = 36.668454d; //纬度
    public double mLongitude = 117.024297d; //经度
    public String mIMEI = "865863023908325";
    public String mAndroidId = "76BBKT227G6";
    public String mSimSerialNumber = "865863023908324";
    public String mMacAddress = "24:4C:07:BA:D7:73";

    private SettingsHelper mSharedPreferences;

    public synchronized static SystemSettings getInstance() {
        if (sInstance == null) {
            sInstance = new SystemSettings();
        }

        sInstance.fetchHookInfo();
        return sInstance;
    }

    private SystemSettings() {
        super();
    }

    private void fetchHookInfo() {
        if (mSharedPreferences == null) {
            mSharedPreferences = new SettingsHelper(Main.WEICHAT_XPOSED_PACKAGE_NAME);
        }
        if (mSharedPreferences != null) {
            mSharedPreferences.reload();
            mOutputLog = mSharedPreferences.getBoolean("output_log", false);
            mLatitude = Double.parseDouble(mSharedPreferences.getString("latitude", "0"));
            mLongitude = Double.parseDouble(mSharedPreferences.getString("longitude", "0"));
            mIMEI = mSharedPreferences.getString("imei", "");
            mAndroidId = mSharedPreferences.getString("androidId", "");
            mSimSerialNumber = mSharedPreferences.getString("simSerialNumber", "");
            mMacAddress = mSharedPreferences.getString("mac", "");
        }
    }
}
