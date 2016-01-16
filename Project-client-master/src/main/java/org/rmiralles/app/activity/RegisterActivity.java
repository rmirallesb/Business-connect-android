package org.rmiralles.app.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.rmiralles.app.R;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText etUsername, etPassword, etBusiness;
    private Button btAccept, btCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etBusiness = (EditText) findViewById(R.id.etBusiness);

        btAccept = (Button) findViewById(R.id.btAccept);
        btCancel = (Button) findViewById(R.id.btCancel);

        btAccept.setOnClickListener(this);
        btCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btAccept:
                register();
                break;
            case R.id.btCancel:
                cancel();
                break;
        }
    }

    public void cancel(){
        finish();
    }

    public void register(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String business = etBusiness.getText().toString();
        if (username.equalsIgnoreCase("") || business.equalsIgnoreCase("") || password.equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(), "No puedes dejarlo en blanco", Toast.LENGTH_LONG).show();
            return;
        }

        new Connect().execute(username, password, business);
    }

    public class Connect extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... data) {
            try {
                final String URL = "http://192.168.1.50:8080/register?username=" + data[0] + "&password=" + data[1] + "&business_title=" + data[2];
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                boolean register = restTemplate.getForObject(URL, Boolean.class);
                return register;
            }catch(Exception e){
                Log.e("", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Boolean register){
            if (register){
                Toast.makeText(getApplicationContext(), "Usuario creado", Toast.LENGTH_LONG).show();
                finish();
            } else{
                Toast.makeText(getApplicationContext(), "El usuario no se ha podido crear", Toast.LENGTH_LONG).show();
            }
        }
    }
}
