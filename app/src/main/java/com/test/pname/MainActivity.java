package com.test.pname;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private TextView tv;
    private MyHandler myHandler = new MyHandler(MainActivity.this);

    private static class MyHandler extends Handler {
        WeakReference<MainActivity> weakReference;

        public MyHandler(MainActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference == null)
                return;
            MainActivity ac = weakReference.get();
            if (ac == null)
                return;
            switch (msg.what) {
                case 1:
                    ac.closeCurApp();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.mytv);

        Log.d(TAG, ": called AppLovinSdk.initializeSdk");
        AppLovinSdk.initializeSdk(MainActivity.this, new applovinInitializationListener());
    }

    class applovinInitializationListener implements AppLovinSdk.SdkInitializationListener {

        @Override
        public void onSdkInitialized(AppLovinSdkConfiguration appLovinSdkConfiguration) {
            String applicationID = MainActivity.this.getApplication().getPackageName();
            String appName = getApplicationName();
            tv.setText(
                    "ApplovinKey = " + getApplovinKey() + "\n\n"
                            + "applicationID = " + applicationID + "\n"
                            + "appNam = " + appName + " \n"
                            + "targetUrl = " + getTargetUrl() + " \n"
                            + "result appLovinSdkConfiguration = " + appLovinSdkConfiguration.getConsentDialogState());
            Log.d(TAG, "onSdkInitialized() called with: appLovinSdkConfiguration = [" + appLovinSdkConfiguration + "]");

            int code = appLovinSdkConfiguration.getConsentDialogState()
                    == AppLovinSdkConfiguration.ConsentDialogState.UNKNOWN ? 0 : 1;
            dealWithResultBack(code);
        }
    }

    /**
     * 处理初始化返回的结果
     *
     * @param code 0：失败 1：成功
     */
    private void dealWithResultBack(int code) {
        String targetUrl = getTargetUrl();
        if (!TextUtils.isEmpty(targetUrl)) {
            targetUrl = targetUrl + "?success=" + code;
            Log.d(TAG, "dealWithResultBack: targetUrl == " + targetUrl);
            openUrl(targetUrl);
        } else {
            Log.d(TAG, "dealWithResultBack: the target url is empty!");
        }
    }

    private void openUrl(String targetUrl) {
        Log.d(TAG, "openUrl() called with: targetUrl = [" + targetUrl + "]");
//        try {
//            Uri uri = Uri.parse(targetUrl);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            MainActivity.this.startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d(TAG, "openUrl: error == " + e.getMessage());
//        }
//
//        myHandler.sendEmptyMessageDelayed(1, 1000);

        String url = targetUrl;
        OkHttpClient okHttpClient = new OkHttpClient();
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .connectTimeout(20, TimeUnit.SECONDS)//设置连接超时时间
//                .readTimeout(20, TimeUnit.SECONDS)//设置读取超时时间
//                .build();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: error == " + e.getMessage());
                myHandler.sendEmptyMessageDelayed(1, 1000);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
                myHandler.sendEmptyMessageDelayed(1, 1000);
            }
        });
    }

    private void closeCurApp() {
        Log.d(TAG, "closeCurApp: call killProcess cur pid;");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    public String getTargetUrl() {
        ApplicationInfo appInfo = null;
        String targeturl = null;
        try {
            appInfo = MainActivity.this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            targeturl = appInfo.metaData.getString("target.url");
            Log.d(TAG, " targeturl == " + targeturl);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return targeturl;
    }

    public String getApplovinKey() {
        ApplicationInfo appInfo = null;
        String applovinKey = null;
        try {
            appInfo = MainActivity.this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            applovinKey = appInfo.metaData.getString("applovin.sdk.key");
            Log.d(TAG, " applovinKey == " + applovinKey);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return applovinKey;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
    }
}
