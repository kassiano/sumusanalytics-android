package br.com.sumusanalitics.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import static br.com.sumusanalitics.app.ConfigActivity.NUM_SERIE;

/**
 * Created by kassianoresende on 19/04/17.
 */

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String numSerieArmazenado = preferences.getString(NUM_SERIE, "");
        boolean termosDeUsoAceito = preferences.getBoolean("termosDeUsoAceito", false);

        Intent intent;

        if(termosDeUsoAceito){

            if(numSerieArmazenado.isEmpty()){
                intent = new Intent(this, ConfigActivity.class);
            }else{
                intent = new Intent(this, LoginActivity.class);
            }

        }else{

            intent = new Intent(this, TermosDeUsoActivity.class);
        }


        startActivity(intent);

    }
}


