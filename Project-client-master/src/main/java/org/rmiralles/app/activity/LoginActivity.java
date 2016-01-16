package org.rmiralles.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.rmiralles.app.R;
import org.rmiralles.app.base.User;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class LoginActivity extends Activity implements View.OnClickListener {

    private Button btAccept, btRegister;
    private EditText etUsername, etPassword;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("org.rmiralles.preferences", MODE_PRIVATE);

        btAccept = (Button) findViewById(R.id.btAccept);
        btRegister = (Button) findViewById(R.id.btRegister);
        btAccept.setOnClickListener(this);
        btRegister.setOnClickListener(this);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        checkUser();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btAccept:
                login();
                break;
            case R.id.btRegister:
                register();
        }
    }

    public void login(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        new Connect().execute(username, password);
    }

    public void register(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void checkUser(){
        int id = sharedPreferences.getInt("id", -1);
        String username = sharedPreferences.getString("username","");
        String business = sharedPreferences.getString("business","");
        if ((id != -1) && (!username.equalsIgnoreCase("")) && (!business.equalsIgnoreCase(""))){
            startMain();
        }
    }

    public void startMain(){
        finish();
        Intent intent = new Intent().setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public class Connect extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... data) {
            try {
                final String URL = "http://192.168.1.50:8080/login?username=" + data[0] + "&password=" + data[1];
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                User user = restTemplate.getForObject(URL, User.class);
                return user;
            } catch (Exception e) {
                Log.e("", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user == null || user.getUsername() == null){
                Toast.makeText(getApplicationContext(), "El usuario no existe", Toast.LENGTH_LONG).show();
            }else if (user.getUsername() != null){
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username",user.getUsername());
                editor.putString("password",user.getPassword());
                editor.putString("business",user.getBusiness_title());
                editor.putInt("id", user.getId());
                editor.commit();
                startMain();
            }
        }
    }
}