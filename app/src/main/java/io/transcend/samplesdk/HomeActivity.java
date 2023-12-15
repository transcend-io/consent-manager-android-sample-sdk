package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

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
                    // Approach 2: Create webview dynamically with onCloseListener
//                       TranscendWebView wv = new TranscendWebView(HomeActivity.this,"https://transcend-cdn.com/cm/a3b53de6-5a46-427a-8fa4-077e4c015f93/airgap.js", new TranscendListener.OnCloseListener() {
//                           @Override
//                           public void onClose() {
//                                System.out.println("Closed");
//                           }
//                       });
//                       setContentView(wv);

                   }
                }
            });
        } catch (Exception e) {
            System.out.println("Found error on getRegimes()");
        }
    }
}
