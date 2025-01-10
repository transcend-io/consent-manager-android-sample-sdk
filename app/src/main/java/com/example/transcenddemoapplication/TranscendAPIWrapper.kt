package com.example.transcenddemoapplication

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import io.transcend.webview.IABConstants
import io.transcend.webview.TranscendAPI
import io.transcend.webview.TranscendConstants
import io.transcend.webview.TranscendListener
import io.transcend.webview.models.TrackingConsentDetails
import io.transcend.webview.models.TranscendConfig.ConfigBuilder


object TranscendApiWrapper {
    // Initialize TranscendAPI when the object is initialized
    // Pass Application context
    private var isInitCompleted = false
    private const val AG_URL = "https://transcend-cdn.com/cm/0016865d-822d-4574-8235-546152a5b53e/airgap.js"
    private val agAttributes: Map<String, String> = object : HashMap<String, String>() {
        init {
            // here
            put("data-regime", "default")
        }
    }
    val config = ConfigBuilder(AG_URL).defaultAttributes(agAttributes).destroyOnClose(false)
        .autoShowUI(false).mobileAppId("TextNow Android")
        .viewState("CompleteOptionsToggles").build()

    fun init(context: Context, callback: (Boolean) -> Unit) {
        if(!isInitCompleted) {
            TranscendAPI.init(context, config) { success, errorDetails ->
                if (success) {
                    try {
                        println("Transcend API Ready!!!!!!!")
                        TranscendAPI.getConsent(context) { trackingConsentDetails ->
                            println("isConfirmed: ${trackingConsentDetails.isConfirmed}")
                            val sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(context)
                            println(
                                "SharedPreferences: ${
                                    sharedPreferences.getString(
                                        TranscendConstants.TRANSCEND_CONSENT_DATA, "lol"
                                    )
                                }"
                            )
                            println(
                                "GDPR_APPLIES from SharedPreferences: ${
                                    sharedPreferences.getInt(
                                        IABConstants.IAB_TCF_GDPR_APPLIES, 100
                                    )
                                }"
                            )
                        }
                        callback(true)
                    } catch (e: Exception) {
                        println(e.message)
                        callback(false)
                    }
                }
                else {
                    println(errorDetails)
                    callback(false)
                }
            }
        } else {
            // already initiated
            callback(true)
        }
    }

    // Function to get regimes with callback
    // Pass Application context
    fun getRegimes(context: Context, callback: (Set<String>) -> Unit) {
        try {
            // Call getRegimes function from TranscendAPI
            TranscendAPI.getRegimes(context, TranscendListener.RegimesListener {
                    // Handle the received regimes and pass them to the callback
                    callback(it)
            })
        } catch (ex: Exception) {
            // Handle exception
            Log.e("TranscendApiWrapper", "Error fetching regimes", ex)
            // Call the callback with an empty set or throw exception based on your requirement
            callback(emptySet())
        }
    }

    // Function to get regimes with callback
    // Pass Application context
    fun getConsent(context: Context, callback: (TrackingConsentDetails) -> Unit) {
        try {
            // Call getConsent function from TranscendAPI with the provided context
            TranscendAPI.getConsent(context, TranscendListener.ConsentListener {
                callback(it)
            })
        } catch (ex: Exception) {
            // Handle exception
            Log.e("TranscendApiWrapper", "Error fetching consent", ex)
            // Call the callback with default consent details or throw exception based on your requirement
            callback(TrackingConsentDetails())
        }
    }

    // All other API's can be found on io.transcend.webview.TranscendAPI
    // for which you can add wrappers below if you wish to use this approach

}
