package br.com.sumusanalitics.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import br.com.mybinuvem.app.R;

import static br.com.sumusanalitics.app.ConfigActivity.NUM_SERIE;

public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Context context;
    SharedPreferences preferences;
    private CheckBox checkbox_store;
    String linkConexao="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        checkbox_store = (CheckBox) findViewById(R.id.checkbox_store);

        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);


        String numSerieArmazenado = preferences.getString(NUM_SERIE, "");

        if (numSerieArmazenado.isEmpty()){
            finish();
        }


        String loginStored = preferences.getString("loginStored", "");
        String senhaStored = preferences.getString("senhaStored", "");
        String logoEmpresa =preferences.getString("logoEmpresa", "");
        boolean savelogin = preferences.getBoolean("saveLogin", false);

        linkConexao = preferences.getString(ConfigActivity.LINK_CONEXAO,"");



        checkbox_store.setChecked(savelogin);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {

                    try {
                        attemptLogin();
                    }catch (Exception ex){
                        Toast.makeText(context,"Falha no login", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    attemptLogin();
                }catch (Exception ex){
                    Toast.makeText(context,"Falha no login", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mEmailView.setText(loginStored);
        mPasswordView.setText(senhaStored);


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        //http://52.11.74.228/img/analise-de-resultados-inicio.jpg

        final View mainLayout = findViewById(R.id.mainLayout);


/*
        if(!logoEmpresa.isEmpty()){

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    mainLayout.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.d("HIYA", "onBitmapFailed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Log.d("HIYA", "onPrepareLoad");
                }
            };

            Picasso.with(this)
                    .load(logoEmpresa)
                    .into(target);

        }*/


        //mEmailView.setText("analytics");
        //mPasswordView.setText("sumus");

    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }




        if(linkConexao.isEmpty()){

            new AlertDialog.Builder(context)
                    .setTitle("Atenção!")
                    .setMessage("Você não está conectado ao Sumus Analitics. Para ter acesso é necessário" +
                            " configurar o APP fornecendo um número de série válido.")
                    .setPositiveButton("Configurar APP", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, ConfigActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .create()
                    .show();

            return;
        }


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mAuthTask = new UserLoginTask(email, password, "sumus");
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_settings){
            Intent intent = new Intent(this, ConfigActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mUser;
        private final String mPassword;
        private final String mEmpresa;

        ProgressDialog progress;

        UserLoginTask(String email, String password, String empresa) {
            mUser = email;
            mPassword = password;
            mEmpresa = empresa;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(context, "Aguarde..", "Efetuando login");
        }

        @Override
        protected String doInBackground(Void... params) {


            StringBuilder Slink = new StringBuilder();

            if(!linkConexao.trim().startsWith("http"))
                Slink.append("http://");

            Slink.append(linkConexao.trim());

            String pastaApp = getString(R.string.pastaApp);

            Slink.append(pastaApp);


            //userLogin = BRHROCM
            //userPassword = 123456

            HashMap<String, String> dados = new HashMap<>();
            dados.put("userLogin", mUser);
            dados.put("userPassword", mPassword);
            dados.put("page", "Init");

            String retorno = HttpConnection.post(Slink.toString(), dados);

            if(retorno ==null){
                return null;
            }

            if(retorno.trim().toUpperCase().equals("ERRO")){
                return "ERRO";
            }else {
                return "OK";
            }
        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            //showProgress(false);
            progress.dismiss();

            if (success != null && !success.isEmpty() && success.trim().toUpperCase().equals("OK")) {


                if(checkbox_store.isChecked()) {

                    preferences.edit().putString("loginStored", mUser).commit();
                    preferences.edit().putString("senhaStored", mPassword).commit();


                }else{
                    preferences.edit().remove("loginStored").commit();
                    preferences.edit().remove("senhaStored").commit();
                }

                preferences.edit().putBoolean("saveLogin", checkbox_store.isChecked()).commit();

                Intent intent = new Intent(context, PrincipalActivity.class);

                intent.putExtra("login", mUser);
                intent.putExtra("senha", mPassword);
                intent.putExtra("link", linkConexao);

                startActivity(intent);

            } else {
                Log.d("onPostExecute", "Dados de acesso inválidos" );
                new AlertDialog.Builder(context)
                        .setTitle("Falha no acesso")
                        .setMessage("Dados de acesso inválidos")
                        .setPositiveButton("ok", null)
                        .create()
                        .show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
            progress.dismiss();
        }
    }

}
