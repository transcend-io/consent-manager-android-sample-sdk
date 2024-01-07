package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.gson.Gson;

import io.transcend.webview.IABConstants;
import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendConstants;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;
import io.transcend.webview.models.TrackingConsentDetails;

public class ManageConsentPreferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("HomeActivity - onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_consent_preferences);
        setUpButtons();
    }


    private void setUpButtons() {
        Button button = (Button) findViewById(R.id.changeConsent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranscendWebView transcendWebView = (TranscendWebView) findViewById(R.id.transcendWebView);
                transcendWebView.reload();
                transcendWebView.setVisibility(View.VISIBLE);
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
                        System.out.println("SharedPreferences: " + androidx.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(TranscendConstants.TRANSCEND_CONSENT_DATA, "lol"));
                        System.out.println("GDPR_APPLIES from SharedPreferences: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(IABConstants.IAB_TCF_GDPR_APPLIES, 100));
                    }
                });
            } catch (Exception e) {
                System.out.println("getConsent error" + e);
            }
        }
}
