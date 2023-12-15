package io.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;

import io.transcend.webview.TranscendAPI;
import io.transcend.webview.TranscendListener;
import io.transcend.webview.TranscendWebView;

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
        String url = "https://transcend-cdn.com/cm/a3b53de6-5a46-427a-8fa4-077e4c015f93/airgap.js";
        webView = TranscendAPI.init(getApplicationContext(), url, new TranscendListener.ViewListener(){
            @Override
            public void onViewReady() {
                try {
                    TranscendAPI.getRegimes(getApplicationContext(), new TranscendListener.RegimesListener() {
                        @Override
                        public void onRegimesReceived(Set<String> regimes) {
                            System.out.println(regimes.size());
                            System.out.println(regimes.contains("gdpr"));
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Found error on getRegimes()");
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
