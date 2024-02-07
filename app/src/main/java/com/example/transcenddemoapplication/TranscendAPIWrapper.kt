package com.example.transcenddemoapplication

import android.content.Context
import android.util.Log
import io.transcend.webview.TranscendAPI
import io.transcend.webview.TranscendListener
import io.transcend.webview.models.TrackingConsentDetails

object TranscendApiWrapper {
    // Initialize TranscendAPI when the object is initialized
    // Pass Application context
    private var isInitialized = false;
    fun init(context: Context, callback: (Boolean) -> Unit) {
        if(!isInitialized) {
            TranscendAPI.init(
                context,
                "https://transcend-cdn.com/cm/90ab36ce-3da0-481c-ba8d-98eb820d716f/airgap.js",
                TranscendListener.ViewListener {
                    // Implement your logic here when the TranscendWebView is ready
                    Log.i("TAPI:INIT", "Successful");
                    callback(true);
                    isInitialized = true
                })
        }
        else{
            callback(true);
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
