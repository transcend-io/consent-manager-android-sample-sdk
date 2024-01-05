package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import io.transcend.webview.IABConstants;
import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendConstants;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;
import io.transcend.webview.models.TrackingConsentDetails;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        WebView ewv= (WebView) findViewById(R.id.eshopit_2);
        ewv.getSettings().setJavaScriptEnabled(true);
        ewv.getSettings().setDomStorageEnabled(true);

        ewv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("TranscendAPI", "onPageStarted: " + url);
                String consent = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(TranscendConstants.TRANSCEND_CONSENT_DATA, null);
                String tcString= PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(IABConstants.IAB_TCF_TC_STRING, null);
                Gson gson = new Gson();

                TranscendAPI.setConsent(view,gson.fromJson(consent, TrackingConsentDetails.class),tcString,(TranscendListener.ConsentStatusUpdateListener) (success, errorDetails) -> {
                    if(success){
                        System.out.println("Successfully synced data");
                    }else{
                        System.out.println("Unsuccessful data sync");
                    }
                });
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view,url);
                ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_CONSENT_KEY), System.out::println);
                ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_TCF_KEY), System.out::println);
            }
        });

        ewv.loadUrl("https://eshopit.co/");


    }
}
