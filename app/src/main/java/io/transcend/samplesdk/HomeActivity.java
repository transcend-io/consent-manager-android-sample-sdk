package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


import android.os.Handler;
import android.os.Looper;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendConstants;
import io.transcend.webview.TranscendWebView;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        TranscendWebView wv = (TranscendWebView) findViewById(R.id.webView);
        WebView ewv= (WebView) findViewById(R.id.eshopit);
        ewv.getSettings().setJavaScriptEnabled(true);
        ewv.getSettings().setDomStorageEnabled(true);
        ewv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view,url);
                ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_CONSENT_KEY), System.out::println);
                ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_TCF_KEY), System.out::println);
            }
        });

        wv.setOnCloseListener(() -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    TranscendAPI.getConsent(getApplicationContext(), trackingConsentDetails -> {
                        System.out.println("In onCloseListener::" + trackingConsentDetails.isConfirmed());
                    });
                }
                catch (Exception ex){
                    System.out.println("Exception");
                }
                ewv.setVisibility(View.VISIBLE);
                ewv.loadUrl("https://staging2.theathletic.com/live-blogs/transfer-news-live-updates-latest/kERVX8vl0Soa/");
            });
        });

        try {
            TranscendAPI.getConsent(getApplicationContext(), trackingConsentDetails -> {
                System.out.println("isConfirmed:: " + trackingConsentDetails.isConfirmed());
                System.out.println(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(TranscendConstants.TRANSCEND_CONSENT_DATA, "lol"));
                try {
                    TranscendAPI.getRegimes(getApplicationContext(), regimes -> {
                        System.out.println("regimes size:: " + regimes.size());
                        if (regimes.contains("gdpr") && !trackingConsentDetails.isConfirmed()) {
                            System.out.println("WebView Alive");
                            wv.setVisibility(View.VISIBLE);
                        }
                        else {
                            ewv.setVisibility(View.VISIBLE);
                            ewv.loadUrl("https://staging2.theathletic.com/live-blogs/transfer-news-live-updates-latest/kERVX8vl0Soa/");
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Found error on getRegimes()");
                }
                System.out.println(trackingConsentDetails.getPurposes().get("Analytics"));

            });
        } catch (Exception e) {
            System.out.println("Found error on getConsent()");
        }
    }
    // Execute JavaScript to update localStorage
    //        try {
    //            String tcString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(IABConstants.IAB_TCF_TC_STRING, "");
    //            TranscendAPI.setConsent(ewv, trackingConsentDetails,tcString, (TranscendListener.ConsentStatusUpdateListener) (success, errorDetails) -> {
    //                if (success) {
    //                    ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_CONSENT_KEY), System.out::println);
    //                    ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_TCF_KEY), System.out::println);
    //                }
    //            });
    //        }
    //        catch (Exception ex){
    //            System.out.println("lol"+ ex);
    //
    //        }
    //          Approach 2: Create webView dynamically with onCloseListener
    //                       TranscendWebView wv = new TranscendWebView(HomeActivity.this,"https://transcend-cdn.com/cm/a3b53de6-5a46-427a-8fa4-077e4c015f93/airgap.js", new TranscendListener.OnCloseListener() {
    //                           @Override
    //                           public void onClose() {
    //                                System.out.println("Closed");
    //                           }
    //                       });
    //                       setContentView(wv);
}
