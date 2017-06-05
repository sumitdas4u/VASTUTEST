package com.utsavmobileapp.utsavapp.service;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.utsavmobileapp.utsavapp.R;

public class pdf extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        WebView browser = (WebView) findViewById(R.id.webview);
        assert browser != null;
        browser.loadUrl("http://utsavapp.in/pdf.html");
    }
}
