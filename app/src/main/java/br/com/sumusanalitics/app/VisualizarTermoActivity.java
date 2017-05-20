package br.com.sumusanalitics.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import br.com.mybinuvem.app.R;

public class VisualizarTermoActivity extends AppCompatActivity {


    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_termo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        webview = (WebView) findViewById(R.id.webview_termo);
        webview.loadUrl("file:///android_asset/termo_adesao.html");

    }

}
