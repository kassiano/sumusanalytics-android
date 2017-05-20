package br.com.sumusanalitics.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import br.com.mybinuvem.app.R;
import br.com.sumusanalitics.app.helper.HttpRequest;
import br.com.sumusanalitics.app.helper.WebHttpRequest;
import br.com.sumusanalitics.app.model.RetornoConfig;

import static android.view.View.GONE;

public class ConfigActivity extends AppCompatActivity {


    public final static String LINK_CONEXAO= "linkConexao";
    public final static String NUM_SERIE = "numeroSerie";


    EditText num_serie;
    SharedPreferences preferences;
    Context context;
    Button btn_conectar, btn_desconectar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        context = this;

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String numSerieArmazenado = preferences.getString(NUM_SERIE, "");
        String linkConexaoArmazenado = preferences.getString(LINK_CONEXAO, "");


        if(!numSerieArmazenado.isEmpty())
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        num_serie = (EditText) findViewById(R.id.numero_serie);
        btn_conectar =(Button) findViewById(R.id.btn_conectar);
        btn_desconectar=(Button) findViewById(R.id.btn_desconectar);

        num_serie.setText(numSerieArmazenado);


        if(linkConexaoArmazenado !=null && !linkConexaoArmazenado.isEmpty()){
            btn_desconectar.setVisibility(View.VISIBLE);
            btn_conectar.setVisibility(View.GONE);
        }

    }


/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {

        Log.d("onBackPressed", keyCode +"");
        Log.d("onBackPressed", (keyCode == KeyEvent.KEYCODE_BACK) +"");

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            String numSerieArmazenado = preferences.getString(NUM_SERIE, "");

            Log.d("onBackPressed", "LINK " + numSerieArmazenado);

            if (numSerieArmazenado.isEmpty()){
                return false;
            }
        }
        return true;
    }
*/


    public void conectar(View view) {

        final String numero_serie = num_serie.getText().toString();

        new AsyncTask<Void, Void, String>(){

            ProgressDialog progressBar;

            Boolean running = false;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                running = true;

                progressBar = new ProgressDialog(context);
                progressBar.setTitle(null);
                progressBar.setMessage("Conectando..");
                progressBar.setButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Use either finish() or return() to either close the activity or just the dialog
                        running = false;
                    }
                });
                progressBar.show();

            }

            @Override
            protected String doInBackground(Void... params) {

                try {

                    StringBuilder link = new StringBuilder("http://services.sumusti.com.br/app/analytics/customer/");
                    link.append(numero_serie);

                    HttpRequest http = new WebHttpRequest();
                    String retorno = http.getJson(link.toString());

                    return retorno;

                }catch (Exception ex){

                    Log.d("doInBackground", ex.getMessage());
                    return "{\"success\":true,\"data\":\"ERRO\"}";
                }

                //return "ativacao.sumusti.com.br:8081";
            }

            @Override
            protected void onPostExecute(String linkRetorno) {
                super.onPostExecute(linkRetorno);

                progressBar.dismiss();

                if (!running){
                    Log.d("onPostExecute","Ação cancelada");
                    return; //Ação cancelada
                }


                 if(linkRetorno != null && !linkRetorno.isEmpty()
                        && !linkRetorno.trim().contains("ERRO")){


                    linkRetorno = linkRetorno.replace("\n" , "");

                    RetornoConfig retornoConfig = new Gson().fromJson(linkRetorno, RetornoConfig.class);

                    Log.d("onPostExecute","Ação não cancelada");

                    Log.d("onPostExecute",retornoConfig.getLink());

                    preferences.edit().putString(NUM_SERIE, numero_serie).apply();
                    preferences.edit().putString(LINK_CONEXAO, retornoConfig.getLink()).apply();
                    preferences.edit().putBoolean("primeiroAcesso", false).apply();


                    new AlertDialog.Builder(context)
                            .setTitle("Sucesso!")
                            .setMessage("Número de série válido. Conexão realizada com êxito.")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .create()
                            .show();


                    btn_conectar.setVisibility(GONE);
                    btn_desconectar.setVisibility(View.VISIBLE);

                }else{
                    new AlertDialog.Builder(context)
                            .setTitle("Número inválido!")
                            .setMessage("Número de série incorreto. Favor inserir um número de série válido.")
                            .setPositiveButton("cancelar", null)
                            .create()
                            .show();
                }

            }

        }.execute();

    }

    public void desconectar(View view) {

        DialogInterface.OnClickListener desconectarListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                efetuarDesconexao();
            }
        };


        new AlertDialog.Builder(context)
                .setTitle("Atenção!")
                .setMessage("Ao desconectar-se não será mais possível visualizar seus dados. Para voltar " +
                        "a ter acesso será necessário reconfigurar o APP fornecendo novamente um número de série válido.")
                .setPositiveButton("Continuar", desconectarListener)
                .setNegativeButton("Cancelar", null)
                .create()
                .show();

    }


    public void efetuarDesconexao(){
        new AsyncTask<Void, Void, String>(){

            ProgressDialog progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressBar = ProgressDialog.show(context, null, "Desconectando..", true,
                        true);

            }

            @Override
            protected String doInBackground(Void... params) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String linkRetorno) {
                super.onPostExecute(linkRetorno);

                progressBar.dismiss();

                preferences.edit().putString(LINK_CONEXAO, "").apply();
                preferences.edit().putString(NUM_SERIE, "").apply();
                num_serie.setText("");

                new AlertDialog.Builder(context)
                        .setTitle("Desconectado!")
                        .setMessage("O Sumus Analytics não está mais conectado a sua base de dados.")
                        .setPositiveButton("OK", null)
                        .create()
                        .show();


                btn_conectar.setVisibility(View.VISIBLE);
                btn_desconectar.setVisibility(GONE);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            }

            @Override
            protected void onCancelled() {
                super.onCancelled();

                new AlertDialog.Builder(context)
                        .setTitle("Ação cancelada!")
                        .setMessage("Você ainda está conectado.")
                        .setPositiveButton("ok", null)
                        .create()
                        .show();

            }
        }.execute();

    }

}
