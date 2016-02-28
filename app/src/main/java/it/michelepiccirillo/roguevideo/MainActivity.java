package it.michelepiccirillo.roguevideo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getDataString();

        this.progressDialog = ProgressDialog.show(this, "", "Extracting video...");

        this.webView = new WebView(this);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setLoadsImagesAutomatically(false);
        this.webView.loadUrl(url);
        this.webView.addJavascriptInterface(new Object() {

            @SuppressWarnings("unused")
            @JavascriptInterface
            public void showVideo(String html) {
                finish();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(html));
                startActivity(browserIntent);
            }
        }, "ROGUE");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:(function() { " +
                        "window.alert = function () {}; " +
                        "window.open = function () {}; " +
                        "$('#videooverlay').click(); " +
                        "$('.vjs-big-play-button').click(); " +
                        "var videoSrc = $('video').attr('src');" +
                        "$('video').remove();" +
                        "window.ROGUE.showVideo(videoSrc);" +
                        "})()");
                super.onPageFinished(view, url);
            }
        });

    }

    @Override
    protected void onDestroy() {
        if(this.webView != null) {
            this.webView.loadUrl("javascript:document.open();document.close()");
            this.webView.destroy();
            this.webView = null;
        }

        if(this.progressDialog != null) {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        }

        super.onDestroy();
    }
}
