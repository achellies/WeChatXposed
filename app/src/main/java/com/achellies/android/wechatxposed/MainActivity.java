package com.achellies.android.wechatxposed;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.achellies.android.wechatxposed.hook.WeChatSettings;
import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SettingsHelper mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = new SettingsHelper(this, "com.achellies.android.wechatxposed");

        setContentView(R.layout.activity_main);

        Button btnRefresh = (Button) findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchSysInfo();
                refreshUI();

                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(WeChatSettings.WECHAT_PACKAGE_NAME);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        fetchSysInfo();
    }

    void fetchSysInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = HttpRequest.get("https://raw.githubusercontent.com/achellies/WeChatAutomator/master/assets/sys_info.json").body();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    mSharedPreferences.setString("latitude", Double.toString(jsonObject.getDouble("latitude")));
                    mSharedPreferences.setString("longitude", Double.toString(jsonObject.getDouble("longitude")));
                    mSharedPreferences.setString("imei", jsonObject.getString("imei"));
                    mSharedPreferences.setString("androidId", jsonObject.getString("androidId"));
                    mSharedPreferences.setString("simSerialNumber", jsonObject.getString("simSerialNumber"));
                    mSharedPreferences.setString("mac", jsonObject.getString("mac"));
                } catch (JSONException ignore) {
                }
            }
        }).start();
    }

    void refreshUI() {
        TextView tvIMEI = (TextView) findViewById(R.id.tv_imei);

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (tm != null) {
            tvIMEI.setText(tm.getDeviceId());
        }

        TextView tvMac = (TextView) findViewById(R.id.tv_mac);

        tvMac.setText(getMACAddress("eth0"));

        //获取地理位置管理器
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //获取Location
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                TextView tvLocation = (TextView) findViewById(R.id.tv_location);
                String locationStr = "纬度：" + location.getLatitude() + ",经度：" + location.getLongitude();
                tvLocation.setText(locationStr);
            }
            //监视地理位置变化
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        TextView tvLocation = (TextView) findViewById(R.id.tv_location);
                        String locationStr = "纬度：" + location.getLatitude() + ",经度：" + location.getLongitude();
                        tvLocation.setText(locationStr);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

    }

    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        }
        return "";

    }
}
