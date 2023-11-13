# consent-manager-android-sample-sdk

This repository showcases a sample Android application that consumes Transcend's WebView library.

## Local setup required for this repo
- Android Studio
- Gradle8.0
- jbr17.0.8

## Build and Install Application
1) `./gradlew androidDependencies`
2) `./gradlew assemble`
3) `./gradlew build`
4) `adb -d install ./app/build/outputs/apk/release/app-release-unsigned.apk` or click play button on your android studio.

## Usage of WebView Library in Sample Application:
### Step 1:  Add Dependencies
- Add the following dependencies on you app modules build.gradle file and install the following android dependencies:

```groovy
// STEP 1: Add required dependencies
implementation 'io.transcend.webview:webview:1.0.0-SNAPSHOT'
implementation 'androidx.webkit:webkit:1.7.0'
```

### Step 3:  Add Dependencies
- Add the following repository on dependecyResolutionManagement in settings.gradle file

```groovy
// STEP 2: Add following repository
maven {
            url= "https://s01.oss.sonatype.org/content/repositories/snapshots/"
}
```

### Step 3: Use the custom Transcend WebView
- Application developers using our custom WebView have the flexibility to employ this view in various contexts based on their application's logic. They can integrate this view during their application's startup, within the main activity, or in response to a button click event. To use Transcend’s webView the following changes are required:

```xml
<!-- on activity_main.xml -->
<io.transcend.webview.TranscendWebView
        android:id="@+id/webView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="visible"/>
```

```java
// Add the following import
import io.transcend.webview.TranscendWebView;

public class MainActivity extends AppCompatActivity {
	private TranscendWebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
				//Step 3: Get the webViewbyId and use loadUrl() to show up TCF-UI
				// Note: on close of TCF-UI the visibilty of this view is set to GONE
        webView = (TranscendWebView) findViewById(R.id.webView);
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
				
    }
}
```
