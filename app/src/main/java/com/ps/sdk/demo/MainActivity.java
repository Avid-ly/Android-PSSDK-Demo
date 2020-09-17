package com.ps.sdk.demo;


import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aly.sdk.ALYAnalysis;
import com.ps.sdk.PSSDK;
import com.ps.sdk.callback.PrivacyDataCallBack;
import com.ps.sdk.callback.PrivacyInfoStatusCallBack;
import com.ps.sdk.callback.PrivacySendCallBack;
import com.ps.sdk.privacy.PrivacyManager;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "pssdk";
    private TextView mTvContent,mTvToken;


    static final String sPdtId = "600167";
    static final String sGamerId = "001";
    String privacyName;
    private Button btnShowDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvContent = findViewById(R.id.tv_content);
        mTvToken=findViewById(R.id.tv_token);
        btnShowDialog=findViewById(R.id.btn_showdialog);
        btnShowDialog.setVisibility(View.GONE);
        initAlySDK();
    }


    private void requestPrivacyData() {
        PSSDK.requestPrivacyData(this, sPdtId, sGamerId, new PrivacyDataCallBack() {
            @Override
            public void onSuccess(String privacy, boolean ignore, int type, boolean accepted) {
                privacyName=privacy;
                if (!TextUtils.isEmpty(privacy)){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnShowDialog.setVisibility(View.VISIBLE);
                        }
                    });
                }
                toast("onSuccess: " + privacy + " ignore: " + ignore + " type:" + type + " accepted :" + accepted);
            }

            @Override
            public void onFail(String s) {
                toast("onFail :" + s);
            }
        });

    }

    private void showPrivacyDialog() {
        PSSDK.showPrivacyDialog(MainActivity.this, sPdtId, privacyName, new PrivacyInfoStatusCallBack() {
            @Override
            public void onAccessPrivacyInfoAccepted() {
                toast("onAccessPrivacyInfoAccepted");
                PSSDK.updateAccessPrivacyInfoStatus(MainActivity.this,sPdtId,sGamerId, PrivacyManager.PrivacyInfoStatusEnum.PrivacyInfoStatusAccepted,privacyName,null);
            }

            @Override
            public void onAccessPrivacyInfoDefined() {
                toast("onAccessPrivacyInfoDefined");
                PSSDK.updateAccessPrivacyInfoStatus(MainActivity.this,sPdtId,sGamerId, PrivacyManager.PrivacyInfoStatusEnum.PrivacyInfoStatusDefined,privacyName,null);

            }

            @Override
            public void onAccessPrivacyInfoUnknown(String s) {
                toast("onAccessPrivacyInfoUnknown " + s);
                PSSDK.updateAccessPrivacyInfoStatus(MainActivity.this,sPdtId,sGamerId, PrivacyManager.PrivacyInfoStatusEnum.PrivacyInfoStatusUnkown, privacyName,null);

            }
        });
    }

    private void initAlySDK() {
        ALYAnalysis.init(this, sPdtId, "32401");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvToken.setText("token :"+ALYAnalysis.getUserId()+" ptdid:"+sPdtId);
                    }
                });

            }
        },1*1000);
    }



    public void getUserPrivacyData(View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestPrivacyData();
                    }
                });

            }
        }, 1 * 1000);
    }

    public void showDialog(View view) {
        showPrivacyDialog();
    }

    public void updateUserPrivacyData(View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PSSDK.updateAccessPrivacyInfoStatus(MainActivity.this, sPdtId, sGamerId, PrivacyManager.PrivacyInfoStatusEnum.PrivacyInfoStatusAccepted, "gdpr", new PrivacySendCallBack() {
                    @Override
                    public void onSuccess() {
                        toast("updateUserPrivacyData onSuccess ");
                    }

                    @Override
                    public void onFail(String s) {
                        toast("updateUserPrivacyData onFail " + s);
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
}
