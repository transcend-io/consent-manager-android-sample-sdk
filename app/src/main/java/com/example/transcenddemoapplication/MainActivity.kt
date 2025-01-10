package com.example.transcenddemoapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.transcenddemoapplication.ui.theme.TranscendDemoApplicationTheme
import io.transcend.webview.TranscendWebView

class MainActivity : ComponentActivity() {
    private var isApiInstanceReady = mutableStateOf(false);
    private var isConsentConfirmed =  mutableStateOf(false);
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No hard rule to create a wrapper
        // We have created a wrapper just to keep all the TranscendAPI logic in one place
        // Can be designed as required
        TranscendApiWrapper.init(applicationContext) { initialized ->
            if (initialized) {
                // Success handler
                isApiInstanceReady.value = true

                TranscendApiWrapper.getConsent(applicationContext) { trackingConsentDetails ->
                    // if user has already given consent isConfirmed is set to true
                    // so can avoid displaying the popup once again
                     isConsentConfirmed.value = trackingConsentDetails.isConfirmed
                    println("trackingConsentDetails received:" + trackingConsentDetails.purposes)
                }
            } else {
                // Failure Handler
                println("API init failed")
            }
        }


        setContent {
            TranscendDemoApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    if(isApiInstanceReady.value) {
                        ShowButtons(applicationContext, isConsentConfirmed.value)
                    }
                }
            }
        }
    }
}


@Composable
fun consentBanner(showConsentBanner: Boolean, isConsentConfirmed: Boolean, onClose: (Boolean) -> Unit) {
    // you can use isConsentConfirmed to skip showing banner
    if(showConsentBanner) {
        // Use AndroidView to embed TranscendWebView in Compose
        AndroidView(factory = { context ->
            // Initialize the TranscendWebView
            TranscendWebView(
                context,
                TranscendApiWrapper.config
            ) { success, errorDetails, consentDetails ->
                if (success) {
                    println("On close success handler received the following purposes: ${consentDetails.purposes}")
                } else {
                    println("Found the following error on close: $errorDetails")
                }
                onClose(true)
            }.apply {
                loadUrl()
            }
        })
    }
}

@Composable
fun ShowButtons(applicationContext: Context, isConsentConfirmed: Boolean) {
    var showConsentBanner by remember { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .padding(16.dp)
            .height(48.dp)
    ) {
        // Button to show Transcend UI
        Button(
            onClick = { showConsentBanner = true },
        ) {
            Text("Show Consent Banner")
        }
        // Button to log consent
        Button(
            onClick = {
                TranscendApiWrapper.getConsent(applicationContext) { trackingConsentDetails ->
                    println("trackingConsentDetails received:" + trackingConsentDetails.purposes)
                }
            },
        ) {
            Text("log My Consent")
        }
    }

    consentBanner(showConsentBanner, isConsentConfirmed) { isClosed ->
        showConsentBanner = !isClosed
    }
}
