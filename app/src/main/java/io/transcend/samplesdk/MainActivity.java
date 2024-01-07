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
        setUpTranscendWebView();
        setUpButtons();
    }

    private void setUpButtons() {
        Button button = (Button) findViewById(R.id.Home);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the SecondActivity
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        Button manageConsentButton = (Button) findViewById(R.id.manageConsentPreferences);
        manageConsentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the SecondActivity
                Intent intent = new Intent(MainActivity.this, ManageConsentPreferences.class);
                startActivity(intent);
            }
        });
    }

    private void setUpTranscendWebView() {
        // Note: Belongs to Managed Consent Database demo Org
        String url = "https://transcend-cdn.com/cm-test/ee571c7f-030a-41b2-affa-70df8a47b57b/airgap.js";
        List<String> domainUrls = new ArrayList<>(Arrays.asList("https://staging2.theathletic.com/"));
        TranscendWebView transcendWebView = (TranscendWebView) findViewById(R.id.transcendWebView);
        LinearLayout layout = (LinearLayout)findViewById(R.id.contentView);

        transcendWebView.setOnCloseListener(() -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                layout.setVisibility(View.VISIBLE);
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
                domainUrls,
                new TranscendListener.ViewListener() {
                    @Override
                    public void onViewReady() {
                        try {
                            transcendInitialized = true;
                            System.out.println("Transcend Ready!!!!!!!");
                            TranscendAPI.getConsent(getApplicationContext(), trackingConsentDetails -> {
                                System.out.println("isConfirmed:: " + trackingConsentDetails.isConfirmed());
                                System.out.println("SharedPreferences: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(TranscendConstants.TRANSCEND_CONSENT_DATA, "lol"));
                                System.out.println("GDPR_APPLIES from SharedPreferences: " + PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(IABConstants.IAB_TCF_GDPR_APPLIES, 100));

                                try {
                                    TranscendAPI.getRegimes(getApplicationContext(), regimes -> {
                                        System.out.println("regimes size:: " + regimes.size());
                                        if (regimes.contains("gdpr") && !trackingConsentDetails.isConfirmed()) {
                                            System.out.println("Requesting user consent...");
                                            transcendWebView.setVisibility(View.VISIBLE);
                                        } else {
                                            LinearLayout contentView = findViewById(R.id.contentView);
                                            contentView.setVisibility(View.VISIBLE);
                                        }
                                    });
                                } catch (Exception e) {
                                    System.out.println("Found error on getRegimes()");
                                }
                                System.out.println(trackingConsentDetails.getPurposes().get("Analytics"));

                            });


                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
    }
}
