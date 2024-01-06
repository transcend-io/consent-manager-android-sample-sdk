package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;


import android.os.Handler;
import android.os.Looper;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.transcend.webview.IABConstants;
import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendConstants;
import io.transcend.webview.TranscendWebView;

// Mock application page that a user may see after "logging in"
// This is also a Webview that loads a website with airgap.js running.
// This is to demonstrate that consent state is properly synced
// between the mobile app and the web app.
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("HomeActivity - onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        WebView ewv = (WebView) findViewById(R.id.appWebView);
        ewv.getSettings().setJavaScriptEnabled(true);
        ewv.getSettings().setDomStorageEnabled(true);
        ewv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_CONSENT_KEY), System.out::println);
                ewv.evaluateJavascript(String.format("localStorage.getItem('%s')", TranscendConstants.STORAGE_TCF_KEY), System.out::println);
            }
        });
        ewv.setVisibility(View.VISIBLE);
        ewv.loadUrl("https://staging2.theathletic.com/live-blogs/transfer-news-live-updates-latest/kERVX8vl0Soa/");
    }
}
