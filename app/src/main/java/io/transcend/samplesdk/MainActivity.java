package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;

import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;
import io.transcend.webview.models.ConsentStatus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private TranscendWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iconImageView = (ImageView) findViewById(R.id.iconImageView);
        iconImageView.startAnimation(getZoomOutAnimation());
//      Note: Belongs to Managed Consent Database demo Org
        String url = "https://transcend-cdn.com/cm/63b35d96-a6db-436f-a1cf-ea93ae4be24e/airgap.js";
        webView = TranscendAPI.init(getApplicationContext(), url, new TranscendListener.ViewListener(){
            @Override
            public void onViewReady() {
                try {
                    TranscendAPI.getSdkConsentStatus(getApplicationContext(), "datadog-ios", new TranscendListener.ConsentStatusListener() {
                        @Override
                        public void onStatusReceived(ConsentStatus consentStatus) {
                            System.out.println(consentStatus.toString());
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Found error on getServiceConsentStatus()");
                }
            }
        });


        Button button = (Button) findViewById(R.id.myButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the SecondActivity
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    public ScaleAnimation getZoomOutAnimation(){
        ScaleAnimation zoomOutAnimation = new ScaleAnimation(
                .7f,
                1f,
                .7f,
                1f,
                Animation.RELATIVE_TO_SELF,
                Animation.RELATIVE_TO_SELF
        );
        zoomOutAnimation.setDuration(3000);
        return zoomOutAnimation;

    }
}
