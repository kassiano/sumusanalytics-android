package br.com.sumusanalitics.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import br.com.mybinuvem.app.R;

public class TermosDeUsoActivity extends AppCompatActivity {

    private Context context;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos_de_uso);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);


        //preferences.edit().putBoolean("termosDeUsoAceito", false).apply();


    }


    @Override
    protected void onResume() {
        super.onResume();

        boolean termosDeUsoAceito = preferences.getBoolean("termosDeUsoAceito", false);

        if(termosDeUsoAceito){
            finish();
        }
    }

    public void lerTermoAdesao(View view) {

        startActivity(new Intent(context, VisualizarTermoActivity.class));
    }

    public void concordar(View view) {

        preferences.edit().putBoolean("termosDeUsoAceito", true).apply();

        new AsyncTask<Void, Void,Void>(){

            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = ProgressDialog.show(context,"Aguarde ...", null );
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

                Intent intent =new Intent(context, ConfigActivity.class);
                startActivity(intent);

                //Toast.makeText(context, "Termo de adesão aceito com sucesso.", Toast.LENGTH_SHORT)
                //        .show();

            }
        }.execute();
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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
