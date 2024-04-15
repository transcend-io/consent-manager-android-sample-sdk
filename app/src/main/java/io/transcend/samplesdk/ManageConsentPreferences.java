package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import io.transcend.webview.IABConstants;
import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendConstants;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;
import io.transcend.webview.models.TrackingConsentDetails;
import io.transcend.webview.models.TranscendCoreConfig;

public class ManageConsentPreferences extends AppCompatActivity {
    Context context;
    TranscendWebView transcendWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_consent_preferences);

        System.out.println("ManageConsentPreferences - onCreate");
        // Can either create a clone of previous defined config
        // if you need any changes
        // Example: here I want to change the destroy on close behavior to false
        TranscendCoreConfig config = TranscendAPI.config.clone();
        config.setDestroyOnClose(false);
        // Or could also directly use same config object created on MainActivity as follows
        // if no change needed
        // config = TranscendAPI.config;
        transcendWebView = (TranscendWebView) findViewById(R.id.transcendWebView);
        transcendWebView.setConfig(config);
        transcendWebView.loadUrl();
        setUpButtons();
        context = this;
    }


    private void setUpButtons() {
        Button button = (Button) findViewById(R.id.changeConsent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transcendWebView.setVisibility(View.VISIBLE);
                transcendWebView.showConsentManager(null);
            }
        });

        Button manageConsentButton = (Button) findViewById(R.id.logConsent);
        manageConsentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAndLogConsent();
            }
        });
    }

    private void getAndLogConsent() {
        try {
            TranscendAPI.getConsent(getApplicationContext(), new TranscendListener.ConsentListener() {
                @Override
                public void onConsentReceived(TrackingConsentDetails consentDetails) {
                    System.out.println("getConsent().isConfirmed(): " + consentDetails.isConfirmed());
                    System.out.println("getConsent().getPurposes():" + consentDetails.getPurposes());
                    System.out.println("SharedPreferences: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(TranscendConstants.TRANSCEND_CONSENT_DATA, "null"));
                    System.out.println("GDPR_APPLIES from SharedPreferences: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(IABConstants.IAB_TCF_GDPR_APPLIES, -1));
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
