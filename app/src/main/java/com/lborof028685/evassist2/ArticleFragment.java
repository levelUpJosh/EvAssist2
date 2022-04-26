package com.lborof028685.evassist2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;


public class ArticleFragment extends AppCompatActivity {

    public WebView myWebView;
    private ProgressBar spinner;
    boolean ShowOrHideWebViewInitial = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_article);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        //get uri
        Intent intent = getIntent();
        String articleLink = intent.getStringExtra("articleLink");

        myWebView = (WebView) findViewById(R.id.webview);
        spinner = (ProgressBar)findViewById(R.id.progressBar);
        myWebView.setWebViewClient(new CustomWeb());

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);


        // TODO: change to a layout with back button etc

        Context context = getApplicationContext();
        CharSequence text = articleLink;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


        myWebView.loadUrl(articleLink);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Splash screen for webview to show its loading
    private class CustomWeb extends WebViewClient {
        @Override
        public void onPageStarted(WebView webview, String url, Bitmap favicon) {
            if (ShowOrHideWebViewInitial) {
                webview.setVisibility(webview.INVISIBLE);
            }

        }

        @Override
        public void onPageFinished(WebView webview, String url) {
            ShowOrHideWebViewInitial =false;
            spinner.setVisibility(View.GONE);
            webview.setVisibility(webview.VISIBLE);
            super.onPageFinished(webview,url);
        }
    }
}