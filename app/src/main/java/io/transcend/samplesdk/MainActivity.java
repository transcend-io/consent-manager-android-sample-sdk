package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import io.transcend.webview.IABConstants;
import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendConstants;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;
import io.transcend.webview.models.TrackingConsentDetails;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

// MainActivity is the login page
public class MainActivity extends AppCompatActivity {
    private TranscendWebView webView;
    private boolean transcendInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpBookingWebView();
        setUpTranscendWebView();
    }

    private void setUpTranscendWebView() {
        // Note: Belongs to Managed Consent Database demo Org
        String url = "https://transcend-cdn.com/cm/90ab36ce-3da0-481c-ba8d-98eb820d716f/airgap.js";
        // Any additional domains you'd like to sync consent data to
        TranscendWebView transcendWebView = (TranscendWebView) findViewById(R.id.transcendWebView);

        transcendWebView.setOnCloseListener(() -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    TranscendAPI.getConsent(getApplicationContext(), trackingConsentDetails -> {
                        System.out.println("In onCloseListener::" + trackingConsentDetails.isConfirmed());
                    });
                }
                catch (Exception ex){
                    System.out.println("Exception");
                }
            });
        });

        TranscendAPI.init(
            getApplicationContext(),
            url,
            new TranscendListener.ViewListener() {
                @Override
                public void onViewReady() {
                    try {
                        System.out.println("Transcend Ready!!!!!!!");
                        TranscendAPI.getConsent(getApplicationContext(), trackingConsentDetails -> {
                            System.out.println("isConfirmed: " + trackingConsentDetails.isConfirmed());
                            System.out.println("SharedPreferences: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(TranscendConstants.TRANSCEND_CONSENT_DATA, "lol"));
                            System.out.println("GDPR_APPLIES from SharedPreferences: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(IABConstants.IAB_TCF_GDPR_APPLIES, 100));
                            getRegimeHandlerLogic(trackingConsentDetails, transcendWebView);
                        });

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        );
    }

    private void setUpBookingWebView(){
        WebView webView = (WebView) findViewById(R.id.WebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("https://www.booking.com/");
    }

    private void getRegimeHandlerLogic(TrackingConsentDetails trackingConsentDetails,
                                  TranscendWebView transcendWebView){
        try {
            TranscendAPI.getRegimes(getApplicationContext(), regimes -> {
                System.out.println("regimes: " + regimes.toString());
                // Custom logic
                if (true) {
                    System.out.println("Requesting user consent...");
                    transcendWebView.setVisibility(View.VISIBLE);
                } else {
                    transcendWebView.hideConsentManager();
                }
            });
        } catch (Exception e) {
            System.out.println("Found error on getRegimes()");
        }
    }
}
