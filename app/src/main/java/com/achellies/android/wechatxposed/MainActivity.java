package com.achellies.android.wechatxposed;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.achellies.android.wechatxposed.hook.WeChatRouter;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append("VERSION.RELEASE:[" + Build.VERSION.RELEASE);
            stringBuilder.append("] VERSION.CODENAME:[" + Build.VERSION.CODENAME);
            stringBuilder.append("] VERSION.INCREMENTAL:[" + Build.VERSION.INCREMENTAL);
            stringBuilder.append("] BOARD:[" + Build.BOARD);
            stringBuilder.append("] DEVICE:[" + Build.DEVICE);
            stringBuilder.append("] DISPLAY:[" + Build.DISPLAY);
            stringBuilder.append("] FINGERPRINT:[" + Build.FINGERPRINT);
            stringBuilder.append("] HOST:[" + Build.HOST);
            stringBuilder.append("] MANUFACTURER:[" + Build.MANUFACTURER);
            stringBuilder.append("] MODEL:[" + Build.MODEL);
            stringBuilder.append("] PRODUCT:[" + Build.PRODUCT);
            stringBuilder.append("] TAGS:[" + Build.TAGS);
            stringBuilder.append("] TYPE:[" + Build.TYPE);
            stringBuilder.append("] USER:[" + Build.USER + "]");
        } catch (Throwable th) {
        }

        Button btnSystemInfo = (Button) findViewById(R.id.btn_system_info);
        btnSystemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etLongitude = (EditText) findViewById(R.id.et_longitude);
                EditText etLatitude = (EditText) findViewById(R.id.et_latitude);
                EditText etImei = (EditText) findViewById(R.id.et_imei);
                EditText etMac = (EditText) findViewById(R.id.et_mac);
                EditText etAndroidId = (EditText) findViewById(R.id.et_androidId);
                EditText etSimSerialNumber = (EditText) findViewById(R.id.et_simSerialNumber);

                WeChatRouter.getInstance(MainActivity.this).mSharedPreferences.setString("latitude", etLatitude.getText().toString());
                WeChatRouter.getInstance(MainActivity.this).mSharedPreferences.setString("longitude", etLongitude.getText().toString());
                WeChatRouter.getInstance(MainActivity.this).mSharedPreferences.setString("imei", etImei.getText().toString());
                WeChatRouter.getInstance(MainActivity.this).mSharedPreferences.setString("androidId", etAndroidId.getText().toString());
                WeChatRouter.getInstance(MainActivity.this).mSharedPreferences.setString("simSerialNumber", etSimSerialNumber.getText().toString());
                WeChatRouter.getInstance(MainActivity.this).mSharedPreferences.setString("mac", etMac.getText().toString());
            }
        });

        Button btnNearBy = (Button) findViewById(R.id.btn_nearby);
        btnNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etLongitude = (EditText) findViewById(R.id.et_longitude);
                EditText etLatitude = (EditText) findViewById(R.id.et_latitude);
                try {
                    WeChatRouter.getInstance(MainActivity.this).startNearByActivity(MainActivity.this, Double.parseDouble(etLongitude.getText().toString()), Double.parseDouble(etLatitude.getText().toString()));
                } catch (Exception ignore) {
                }
            }
        });

        Button btnWeChatLogin = (Button) findViewById(R.id.btn_wechat_login);
        btnWeChatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etUserName = (EditText) findViewById(R.id.et_userName);
                EditText etPassword = (EditText) findViewById(R.id.et_password);
                String userName = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
                    WeChatRouter.getInstance(MainActivity.this).startLoginActivity(MainActivity.this, userName, password);
                }
            }
        });

        Button btnWeChatLogout = (Button) findViewById(R.id.btn_wechat_logout);
        btnWeChatLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeChatRouter.getInstance(MainActivity.this).startLogoutActivity(MainActivity.this);
            }
        });

        Button btnContactInfo = (Button) findViewById(R.id.btn_wechat_contact);
        btnContactInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.et_contact);
                String text = editText.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    WeChatRouter.getInstance(MainActivity.this).startStarContactActivity(MainActivity.this, text);
                }
            }
        });

        Button btnReadArticle = (Button) findViewById(R.id.btn_wechat_contact_news);
        btnReadArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etArticle = (EditText) findViewById(R.id.et_contact_article);
                String url = etArticle.getText().toString();
                if (!TextUtils.isEmpty(url)) {
                    WeChatRouter.getInstance(MainActivity.this).startWebViewActivity(MainActivity.this, url);
                }
            }
        });

        CheckBox cbLog = (CheckBox) findViewById(R.id.cb_log);
        cbLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.getId() == R.id.cb_log) {
                    WeChatRouter.getInstance(MainActivity.this).mSharedPreferences.setBoolean("output_log", isChecked);
                }
            }
        });

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        String xAction = intent.getStringExtra(WeChatRouter.X_ACTION_KEY);
        String jsonParam = intent.getStringExtra(WeChatRouter.X_ACTION_PARAM);
        if (!TextUtils.isEmpty(xAction) && !TextUtils.isEmpty(jsonParam)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonParam);
                if (WeChatRouter.X_ACTION_LOGIN.contentEquals(xAction)) {
                    String userName = jsonObject.getString("name");
                    String password = jsonObject.getString("password");
                    WeChatRouter.getInstance(this).startLoginActivity(this, userName, password);
                } else if (WeChatRouter.X_ACTION_STAR_CONTACT.contentEquals(xAction)) {
                    String id = jsonObject.getString("id");
                    WeChatRouter.getInstance(this).startStarContactActivity(this, id);
                } else if (WeChatRouter.X_ACTION_NEARBY.contentEquals(xAction)) {
                    double latitude = jsonObject.getDouble("latitude");
                    double longitude = jsonObject.getDouble("longitude");
                    WeChatRouter.getInstance(this).startNearByActivity(this, longitude, latitude);
                } else if (WeChatRouter.X_ACTION_START_WEBVIEW.contentEquals(xAction)) {
                    String url = jsonObject.getString("url");
                    WeChatRouter.getInstance(this).startWebViewActivity(this, url);
                }
            } catch (JSONException ignore) {
                ignore.printStackTrace();
            }
        } else if (!TextUtils.isEmpty(xAction)) {
            if (WeChatRouter.X_ACTION_LOGOUT.contentEquals(xAction)) {
                WeChatRouter.getInstance(this).startLogoutActivity(this);
            }
        }
    }
}
