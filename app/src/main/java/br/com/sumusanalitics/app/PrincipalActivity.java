package br.com.sumusanalitics.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.util.EncodingUtils;

import br.com.mybinuvem.app.R;


/*

    Alias : maxview
    senha : maxview
    path : /Users/kassianoresende/Documents/android/maxview_key/maxview_key.jks

 */

public class PrincipalActivity extends AppCompatActivity  {


    ProgressDialog progressBar;
    WebView webView;
    Context context;
    String LINKAPP;
    final String LOGOUT ="modulos_sair.aspx";

    boolean isLogginOut=false;

    boolean isLogin= true;
    String login="",senha="";


    boolean firstNavigation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        if(intent!=null){

            LINKAPP = intent.getStringExtra("link");
            login = intent.getStringExtra("login");
            senha = intent.getStringExtra("senha");

        }else{
            logOut();
        }


        webView =(WebView) findViewById(R.id.webView);

        context = this;


        webView.setWebViewClient(new AppWebViewClients());


        webView.getSettings().setJavaScriptEnabled(true);
       // webView.getSettings().setBuiltInZoomControls(true);
       // webView.getSettings().setSupportZoom(true);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);


        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }



        //userLogin = BRHROCM
        //userPassword = 123456

        String postData = String.format("userLogin=%s&userPassword=%s&page=Init", login, senha);

        Log.i("VERSAO",Build.VERSION.SDK_INT +"" );

        if(!LINKAPP.startsWith("http"))
            LINKAPP = "http://"+LINKAPP;


        String pastaApp = getString(R.string.pastaApp);
        LINKAPP += pastaApp;

        webView.postUrl(LINKAPP, EncodingUtils.getBytes(postData, "BASE64"));

        Log.d("PrincipalActivity", LINKAPP + postData);
        //webView.loadUrl(LINKAPP + postData);

    }



    @Override
    public void onBackPressed() {

        Log.d("onBackPressed", "pressionou voltar ...");

        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        //super.onBackPressed();
    }


    public class AppWebViewClients extends WebViewClient {


        boolean isShowing=false;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);


            if(!isShowing) {

                progressBar = new ProgressDialog(context);
                progressBar.setTitle("Aguarde");
                progressBar.setMessage("Carregando...");
                progressBar.setButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {


                        if(firstNavigation) {
                            finish();
                        }else{
                            webView.goBack();
                        }
                    }
                });
                progressBar.show();

                isShowing=true;
            }

        //    view.setVisibility(View.INVISIBLE);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            firstNavigation = false;

            if(view.getUrl().endsWith("login.aspx"))
            {
                logOut();

            }else{

             //   view.setVisibility(View.VISIBLE);

                if(isShowing) {
                    progressBar.dismiss();
                    isShowing= false;
                }

            }


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bem_vindo_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_sair){


            new AlertDialog.Builder(context)
                    .setTitle("Sair")
                    .setMessage("Tem certeza que deseja sair ?")
                    .setPositiveButton("sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logOut();
                        }
                    })
                    .setNegativeButton("Não", null)
                    .create()
                    .show();


        }

        return super.onOptionsItemSelected(item);
    }


    private void logOut(){

        new AsyncTask<Void, Void,Void>(){

            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(context,"Saindo ...", null );
            }

            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(2000);
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progress.dismiss();

                finish();

            }
        }.execute();
    }


}
