package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import io.transcend.webview.IABConstants;
import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendConstants;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;
import io.transcend.webview.models.TrackingConsentDetails;
import io.transcend.webview.models.TranscendConfig;
import io.transcend.webview.models.TranscendCoreConfig;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// MainActivity is the login page
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpTranscendWebView();
        setUpButtons();
        System.out.println("========Build Details=========");
        System.out.println(Build.VERSION.RELEASE);
        System.out.println(Build.MODEL);
        System.out.println(Build.MANUFACTURER);
        System.out.println(Build.BRAND);
        System.out.println(Build.PRODUCT);
        System.out.println(Build.HARDWARE);
        System.out.println(Build.VERSION.SDK_INT);
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT].getName();
        System.out.println("Android OsName: "+ osName);
        System.out.println("========Build Details=========");

    }

    private void setUpButtons() {
        Button button = (Button) findViewById(R.id.Home);
        button.setOnClickListener(view -> {
            // Create an Intent to start the SecondActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        Button manageConsentButton = (Button) findViewById(R.id.manageConsentPreferences);
        manageConsentButton.setOnClickListener(view -> {
            // Create an Intent to start the SecondActivity
            Intent intent = new Intent(MainActivity.this, ManageConsentPreferences.class);
            startActivity(intent);
        });
    }

    private void setUpTranscendWebView() {
        // Note: Belongs to Managed Consent Database demo Org
        String url = "https://transcend-cdn.com/cm/63b35d96-a6db-436f-a1cf-ea93ae4be24e/airgap.js";
        // Any additional domains you'd like to sync consent data to
        List<String> domainUrls = new ArrayList<>(Arrays.asList("https://example.com/"));
        // User token to sync Data
        String token = "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJlbmNyeXB0ZWRJZGVudGlmaWVyIjoiK3dJWXk2SkdmcGxaUUZMWS9ETnQrTUNRS0dISENWckYiLCJpYXQiOjE3MDY5MTA2ODd9.d4zZoMPtriAPwC0HvJ6BqkOGdG_qcPjmRYNNkN_MfLvZDob1OzQcFUbfKFtFZKix";
        // Specify any default airgap attributes
        Map<String,String> agAttributes = new HashMap<String,String>(){{
            // here
        }};
        // Create config Object
        TranscendConfig config = new TranscendConfig.ConfigBuilder(url)
                .domainUrls(domainUrls)
                .defaultAttributes(agAttributes)
                .token(token)
                .destroyOnClose(false)
                .autoShowUI(true)
                .build();
        LinearLayout layout = (LinearLayout)findViewById(R.id.contentView);
        TranscendWebView transcendWebView = (TranscendWebView) findViewById(R.id.transcendWebView);
        // Set config for element defined on layout
        transcendWebView.setConfig(config);
        transcendWebView.setOnCloseListener(consentDetails -> {
            System.out.println("In onCloseListener::" + consentDetails.isConfirmed());
            System.out.println("User Purposes::"+ consentDetails.getPurposes());
            layout.setVisibility(View.VISIBLE);
        });
        transcendWebView.loadUrl();

        // Init API instance by passing config
        TranscendAPI.init(
                getApplicationContext(),
                config,
                new TranscendListener.ViewListener() {
                    @Override
                    public void onViewReady() {
                        try {
                            System.out.println("Transcend Ready!!!!!!!");
                            TranscendAPI.getConsent(getApplicationContext(), trackingConsentDetails -> {
                                System.out.println("isConfirmed: " + trackingConsentDetails.isConfirmed());
                                System.out.println("SharedPreferences: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(TranscendConstants.TRANSCEND_CONSENT_DATA, "lol"));
                                System.out.println("GDPR_APPLIES from SharedPreferences: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(IABConstants.IAB_TCF_GDPR_APPLIES, 100));
                                fetchRegimesAndHandleUI(transcendWebView, config, trackingConsentDetails);
                            });
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }

    private void fetchRegimesAndHandleUI(TranscendWebView transcendWebView, TranscendCoreConfig config, TrackingConsentDetails trackingConsentDetails){
        try {
            TranscendAPI.getRegimes(getApplicationContext(), regimes -> {
                System.out.println("regimes: " + regimes.toString());
                if (true) {
                    System.out.println("Requesting user consent...");
                    transcendWebView.setVisibility(View.VISIBLE);
                } else {
                    transcendWebView.hideConsentManager();
                    LinearLayout contentView = findViewById(R.id.contentView);
                    contentView.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            System.out.println("Found error on getRegimes()");
        }
    }
}
