package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import io.transcend.webview.IABConstants;
import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendConstants;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;
import io.transcend.webview.models.ConsentStatus;
import io.transcend.webview.models.TrackingConsentDetails;
import io.transcend.webview.models.TranscendConfig;
import io.transcend.webview.models.TranscendCoreConfig;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
        String url = "https://transcend-cdn.com/cm-test/c7561f1c-7ec9-498c-a401-7219e3b36a8c/airgap.js";
        // Any additional domains you'd like to sync consent data to
        List<String> domainUrls = new ArrayList<>(Arrays.asList("https://example.com/"));
        // User token to sync Data
        String token = "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJlbmNyeXB0ZWRJZGVudGlmaWVyIjoiK3dJWXk2SkdmcGxaUUZMWS9ETnQrTUNRS0dISENWckYiLCJpYXQiOjE3MDY5MTA2ODd9.d4zZoMPtriAPwC0HvJ6BqkOGdG_qcPjmRYNNkN_MfLvZDob1OzQcFUbfKFtFZKix";
        // Specify any default airgap attributes
        Map<String, String> agAttributes = new HashMap<String, String>() {{
            // here
        }};
        // Create config Object
        TranscendConfig config = new TranscendConfig.ConfigBuilder(url).domainUrls(domainUrls).defaultAttributes(agAttributes).destroyOnClose(false).autoShowUI(false).mobileAppId("com.transcend.android").build();
        LinearLayout layout = (LinearLayout) findViewById(R.id.contentView);
        TranscendWebView transcendWebView = (TranscendWebView) findViewById(R.id.transcendWebView);
        // Set config for element defined on layout
        transcendWebView.setConfig(config);
        transcendWebView.setOnCloseListener((success, errorDetails, consentDetails) -> {
            if (success) {
                System.out.println("In onCloseListener::" + consentDetails.isConfirmed());
                System.out.println("User Purposes::" + consentDetails.getPurposes());
                getSdkConsentStatus();
                layout.setVisibility(View.VISIBLE);
            } else {
                System.out.println("OnCloseListener failed with the following error: " + errorDetails);
                layout.setVisibility(View.VISIBLE);
            }
        });
        transcendWebView.loadUrl();

        // Init API instance by passing config
        TranscendAPI.init(getApplicationContext(), config, (success, errorDetails) -> {
            if (success) {
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
            } else {
                System.out.println("OnViewReady failed with the following error: " + errorDetails);
                layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchRegimesAndHandleUI(TranscendWebView transcendWebView, TranscendCoreConfig config, TrackingConsentDetails trackingConsentDetails) {
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

    private void getSdkConsentStatus() {
        try {
            TranscendAPI.getSdkConsentStatus(getApplicationContext(), "appsflyer-android", consentStatus -> System.out.println(consentStatus.toString()));
        } catch (Exception e) {
            System.out.println("Found error on getServiceConsentStatus()");
        }
    }
}
