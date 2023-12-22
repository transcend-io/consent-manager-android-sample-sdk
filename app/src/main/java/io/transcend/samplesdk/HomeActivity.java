package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Set;

import io.transcend.webview.TranscendConstants;
import io.transcend.webview.models.TrackingConsentDetails;
import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        WebView ewv= (WebView) findViewById(R.id.eshopit);
        ewv.getSettings().setJavaScriptEnabled(true);
        ewv.getSettings().setDomStorageEnabled(true);

        ewv.loadUrl("https://staging2.theathletic.com/");
        ewv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Execute JavaScript to update localStorage
                try {
                    TranscendAPI.getConsent(getApplicationContext(), new TranscendListener.ConsentListener() {
                        @Override
                        public void onConsentReceived(TrackingConsentDetails trackingConsentDetails) {
                            System.out.println(trackingConsentDetails.getPurposes().get("Analytics"));
                            try {
                                TranscendAPI.setConsent(ewv, getApplicationContext(), trackingConsentDetails, new TranscendListener.ConsentStatusUpdateListener() {
                                    @Override
                                    public void onConsentStatusUpdate(boolean success, String errorDetails) {
                                        if (success) {
                                            ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_CONSENT_KEY), result -> {
                                                System.out.println(result);
                                            });
                                            ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_TCF_KEY), result -> {
                                                System.out.println(result);
                                            });
                                        }
                                    }
                                });
                            }
                            catch (Exception ex){
                                System.out.println("lol"+ ex);

                            }
                        }

                    });
                } catch (Exception e) {
                    System.out.println("Found error on getConsent()");
                }
            }
        });
        TranscendWebView  wv= (TranscendWebView) findViewById(R.id.webView);

        try {
            TranscendAPI.getRegimes(getApplicationContext(), new TranscendListener.RegimesListener() {
                @Override
                public void onRegimesReceived(Set<String> regimes) {
                    System.out.println(regimes.toArray()[0]);
                   if(regimes.contains("GDPR")){
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
