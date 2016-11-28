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

    public String mBuildBoard = "tuna";
    public String mBuildDevice = "maguro";
    public String mBuildDisplay = "cm_maguro-userdebug 4.4.4 KTU84Q fb57653563 test-keys";
    public String mBuildFingerprint = "google/yakju/maguro:4.3/JWR66Y/776638:user/release-keys";
    public String mBuildHost = "cyanogenmod";
    public String mBuildManufacturer = "samsung";
    public String mBuildModel = "Galaxy Nexus";
    public String mBuildProduct = "yakju";
    public String mBuildTags = "test-keys";
    public String mBuildType = "userdebug";
    public String mBuildUser = "jenkins";

//    public String mBuildBoard = "unknown";
//    public String mBuildDevice = "vbox86p";
//    public String mBuildDisplay = "vbox86p-userdebug 4.3 JLS36G eng.buildbot.20150216.213157 test-keys";
//    public String mBuildFingerprint = "generic/vbox86p/vbox86p:4.3/JLS36G/eng.buildbot.20150216.213157:userdebug/test-keys";
//    public String mBuildHost = "buildbot.soft.genymobile.com";
//    public String mBuildManufacturer = "Genymotion";
//    public String mBuildModel = "Google Nexus 4 - 4.3 - API 18 - 768x1280_1";
//    public String mBuildProduct = "vbox86p";
//    public String mBuildTags = "test-keys";
//    public String mBuildType = "userdebug";
//    public String mBuildUser = "buildbot";

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
