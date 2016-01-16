package org.rmiralles.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.rmiralles.app.Preferences;
import org.rmiralles.app.R;
import org.rmiralles.app.base.Text;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class TextActivity extends Activity implements View.OnClickListener {

    private Text text;
    private TextView tvBusinessTitle, tvText, tvDate, tvUser;
    private Button btViewMap;
    private String idText;
    private SharedPreferences sharedPreferences;

    public MediaPlayer mp;
    private boolean boolSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);

        mp = MediaPlayer.create(this, R.raw.click);

        sharedPreferences = getSharedPreferences("org.rmiralles.preferences", MODE_PRIVATE);

        tvBusinessTitle = (TextView) findViewById(R.id.tvBusinessTitle);
        tvText = (TextView) findViewById(R.id.tvText);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvUser = (TextView) findViewById(R.id.tvUser);

        btViewMap = (Button) findViewById(R.id.btViewMap);
        btViewMap.setOnClickListener(this);

        idText = getIntent().getStringExtra("text");

        new Connect().execute(idText);
    }

    private void showText(Text text){
        this.text=text;
        tvBusinessTitle.setText(text.getBusiness_title());
        tvText.setText(text.getText());
        tvDate.setText(String.valueOf(text.getDatetime()));
        tvUser.setText(text.getId_user().getUsername());
    }

    public class Connect extends AsyncTask<String, Void, Text> {

        @Override
        protected Text doInBackground(String... id) {
            try {
                final String URL = "http://192.168.1.50:8080/gettextdetail?id=" + id[0];
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Text text = restTemplate.getForObject(URL, Text.class);

                return text;
            }catch (Exception e){
                Log.e("", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Text text){
            showText(text);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btViewMap){
            Intent intent = new Intent().setClass(TextActivity.this, MapActivity.class);
            intent.putExtra("type", "onetext");
            intent.putExtra("text", String.valueOf(text.getId()));
            intent.putExtra("latitude", text.getLatitude());
            intent.putExtra("longitude", text.getLongitude());
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {

            case R.id.menu_new_text:
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                boolSound = sharedPreferences.getBoolean("soundOn", false);
                if (boolSound){
                    mp.start();
                }
                intent = new Intent(this, AddTextActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.menu_preferences:
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                boolSound = sharedPreferences.getBoolean("soundOn", false);
                if (boolSound){
                    mp.start();
                }

                intent = new Intent(this, Preferences.class);
                this.startActivity(intent);
                return true;
            case R.id.menu_logout:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", "");
                editor.putInt("id", -1);
                editor.commit();

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                boolSound = sharedPreferences.getBoolean("soundOn", false);
                if (boolSound){
                    mp.start();
                }

                intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
