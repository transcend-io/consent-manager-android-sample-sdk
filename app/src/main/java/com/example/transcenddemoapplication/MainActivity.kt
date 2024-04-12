package com.example.transcenddemoapplication

import android.content.Context
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.example.transcenddemoapplication.ui.theme.TranscendDemoApplicationTheme
import io.transcend.webview.TranscendConstants
import io.transcend.webview.TranscendListener
import io.transcend.webview.TranscendWebView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var canShowConsentBanner = mutableStateOf(false);
        // No hard rule to create a wrapper
        // We have created a wrapper just to keep all the TranscendAPI logic in one place
        // Can be designed as required
        TranscendApiWrapper.init(applicationContext) { initialized ->
            TranscendApiWrapper.getConsent(applicationContext){ trackingConsentDetails ->
                // if user has already given consent isConfirmed is set to true
                // so can avoid displaying the popup once again
                if(!trackingConsentDetails.isConfirmed) {
                    canShowConsentBanner.value = true
                }
            }
        }


        setContent {
            TranscendDemoApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Blue) {
                    BookingWebView()
                    if(canShowConsentBanner.value){
                        TrComposable(applicationContext)
                    }
                }
            }
        }
    }
}

// Ideally this wouldn't be the scenario
// But just lading this webView to get a idea
@Composable
fun BookingWebView() {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            loadUrl("https://transcend.io/")
        }
    })
}

@Composable
fun TrComposable(applicationContext: Context) {
    // Initialize TranscendWebView
    AndroidView(factory = { context ->
        // Always use TranscendWebView for UI purpose
        TranscendWebView(
            context,
            "https://transcend-cdn.com/cm/90ab36ce-3da0-481c-ba8d-98eb820d716f/airgap.js",
            TranscendListener.OnCloseListener {
                Log.d("tr:onclose","Closed")
                Log.d("tr:onclose","User Consent:: "+PreferenceManager.getDefaultSharedPreferences(applicationContext).getString(TranscendConstants.TRANSCEND_CONSENT_DATA,"couldn't find consent Data"))
            }
        )
    })
}
