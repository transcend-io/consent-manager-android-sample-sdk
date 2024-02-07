# consent-manager-android-sample-sdk

This repository showcases a sample Android application that consumes Transcend's WebView library in JetPackCompose.

## Local setup required for sampleSDK

- Android Studio
- Gradle8.0
- jbr17.0.8

## TranscendApiWrapper - API level

- `Note: all the calls expect application global Context : *applicationContext*`
- This wrapper class entails all the logic related to API’s that our library provides

### Init

  `fun init(context: Context, callback: (Boolean) -> Unit)`

- This main aim of this function is to call `TranscendAPI.init` which creates a webView instance running in **backgorund**.
- Note: `TranscendAPI.init` takes `ViewListener` as a parameter which is called when we are ready to accept api requests.

### getRegimes

  `fun init(context: Context, callback: (Boolean) -> Unit)`

- This main aim of this function is to call `TranscendAPI.init` which creates a webView instance running in **backgorund**.
- Note: `TranscendAPI.init` takes `ViewListener` as a parameter which is called when we are ready to accept api requests.
- Fetch set of all applicable regimes for the current user based on the regional data. Regimes can be configured at

[Transcend](https://app.transcend.io/consent-manager/regional-experiences)

### getConsent

  `fun init(context: Context, callback: (Boolean) -> Unit)`

- This main aim of this function is to call `TranscendAPI.init` which creates a webView instance running in **backgorund**.
- Note: `TranscendAPI.init` takes `ViewListener` as a parameter which is called when we are ready to accept api requests.
- Returns the consent choices made by the user in the form of `TrackingConsentDetails`
- `TrackingConsentDetails` has all the required infromation but out of this one important property is  *`isConfirmed` - which is set to true if user has alrady slected consent chocies and the consent data is not expired.*

## TranscendWebView - UI level

- NOTE(Important) If you desire to show the consent banner on your app, you should be creating `TranscendWebView` post TranscendWrapper has successfully finised init of the API instance. *`refer MainActivity.kt`*
- The constructor of `TranscendWebView` expects the following parameter:
    - context: *context of the current instance, which could be an Activity, Fragment, Service, or any other Android component*
    - url: *the arigap url associated with your organization*
    - OnCloseListener: *can be used to add custom logic when user has closed the UI banner*
