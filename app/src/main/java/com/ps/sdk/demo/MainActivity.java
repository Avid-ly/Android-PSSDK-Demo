package com.ps.sdk.demo;


import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aly.sdk.ALYAnalysis;
import com.ps.sdk.PSSDK;

import com.ps.sdk.PrivacyCollectionStatus;
import com.ps.sdk.PrivacyShareStatus;
import com.ps.sdk.callback.PrivacySendCallBack;
import com.ps.sdk.entity.PrivacyAuthorizationResult;
import com.ps.sdk.privacy.PrivacyManager;
import com.ps.sdk.tools.error.PrivacyAuthorizationException;


public class MainActivity extends AppCompatActivity implements PrivacySendCallBack {
    public static final String TAG = "pssdk-demo";
    private TextView mTvContent, mTvToken;
    static final String sPdtId = "your productid";
    static final String sGamerId = "your gameid";
    static final String sChannelId = "your channelId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent = findViewById(R.id.tv_content);
        mTvToken = findViewById(R.id.tv_token);
        PSSDK.setDebugable(true);
        Pssdk();
    }

    private void Pssdk() {
        Log.d(TAG, "pid: " + sPdtId + " gid : " + sGamerId);
        PSSDK.requestPrivacyAuthorization(this, sPdtId, sGamerId,
                new PSSDK.RequestPrivacyAuthorizationCallBack() {
                    @Override
                    public void onRequestSuccess(PrivacyAuthorizationResult result) {
                        //authorizationStatus  0 未请求过授权  1 请求过授权
                        //collectionStatus     0 未知         1 不同意收集  2 同意收集
                        //shareStatus          0 未知         1 不同意分享  2 同意分享
                        Log.i(TAG, "onRequestSuccess: " + result.toString());
                        if (result.getCollectionStatus() == PrivacyCollectionStatus.PrivacyCollectionStatusDenied) {
                            ALYAnalysis.disableAccessPrivacyInformation();
                        }
                        initAlySDK();
                        mTvContent.setText(result.toString());
                    }

                    @Override
                    public void onRequestFail(PrivacyAuthorizationException e) {
                        Log.i(TAG, "onRequestFail: " + e.getErrorMessage());
                        mTvContent.setText(e.getErrorCode() + "\n" + e.getErrorMessage());
                    }
                });
    }

    private void initAlySDK() {
        ALYAnalysis.init(this, sPdtId, sChannelId);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvToken.setText("token :" + ALYAnalysis.getUserId() + "\nptdid:" + sPdtId);
                    }
                });

            }
        }, 1 * 1000);
    }

    private void toast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, content, Toast.LENGTH_SHORT).show();
                Log.i(TAG, content);
                mTvContent.setText(content);
            }
        });
    }

    private void fullScreen() {
        //隐藏标题栏
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//隐藏状态栏
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onSuccess() {
        toast("updateUserPrivacyData onSuccess ");
    }

    @Override
    public void onFail(String errorMsg) {
        toast("updateUserPrivacyData onFail " + errorMsg);
    }
}
