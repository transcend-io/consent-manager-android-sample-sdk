package com.transcend.samplesdk;

import androidx.appcompat.app.AppCompatActivity;
import com.transcend.webview.TranscendWebView;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private TranscendWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iconImageView = (ImageView) findViewById(R.id.iconImageView);
        iconImageView.startAnimation(getZoomOutAnimation());
        webView = (TranscendWebView) findViewById(R.id.webView);
        webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
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
