package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.Set;

import io.transcend.webview.TrackingConsentDetails;
import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TranscendWebView  wv= (TranscendWebView) findViewById(R.id.webView);
        try {
            TranscendAPI.getConsent(getApplicationContext(), new TranscendListener.ConsentListener() {
                @Override
                public void onConsentReceived(TrackingConsentDetails trackingConsentDetails) {
                    System.out.println(trackingConsentDetails.getPurposes().get("Analytics"));
                }

            });
        } catch (Exception e) {
            System.out.println("Found error on getConsent()");
        }

        try {
            TranscendAPI.getRegimes(getApplicationContext(), new TranscendListener.RegimesListener() {
                @Override
                public void onRegimesReceived(Set<String> regimes) {
                   if(regimes.contains("us")){
                       wv.setVisibility(View.VISIBLE);
                   }
                }
            });
        } catch (Exception e) {
            System.out.println("Found error on getRegimes()");
        }
    }
}
