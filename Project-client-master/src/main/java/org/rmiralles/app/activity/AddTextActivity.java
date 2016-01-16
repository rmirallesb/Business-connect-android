package org.rmiralles.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import org.rmiralles.app.Preferences;
import org.rmiralles.app.R;
import org.rmiralles.app.fragment.FullListFragment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class AddTextActivity extends Activity implements View.OnClickListener {
    private EditText etText;
    private Button btSendText;
    private LatLng position;
    private LocationManager locManager;
    private SharedPreferences sharedPreferences;
    private String longitude;
    private String latitude;

    public MediaPlayer mp;
    private boolean boolSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        mp = MediaPlayer.create(this, R.raw.click);

        sharedPreferences = getSharedPreferences("org.rmiralles.preferences", MODE_PRIVATE);

        position = getIntent().getParcelableExtra("position");

        etText = (EditText) findViewById(R.id.etText);

        btSendText = (Button) findViewById(R.id.btSendText);
        btSendText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btSendText){
            saveData();
        }
    }

    private void saveData(){
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Location location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null){
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }
        if (location == null){
            latitude = "37,3860517"; //Ubicación aleatoria, Google Mountain view.
            longitude = "-122,0838511";
        }

        String id = String.valueOf(sharedPreferences.getInt("id", -1));
        String business_title = (sharedPreferences.getString("business", ""));
        String text = etText.getText().toString();

        new Connect().execute(text,business_title,latitude,longitude,id);
    }

    public class Connect extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... data) {
            try {
                final String URL = "http://192.168.1.50:8080/addtext?text=" + data[0] + "&business_title=" + data[1] + "&lat=" + data[2] + "&lon=" + data[3] + "&id=" + data[4];
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                boolean newText = Boolean.parseBoolean(restTemplate.getForObject(URL, String.class));
                return newText;
            }catch (Exception e){
                Log.e("", e.getMessage(), e);
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean newText){
            if(newText) {
                Toast.makeText(getApplicationContext(), "Texto creado", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "El texto no ha sido creado", Toast.LENGTH_LONG).show();
            }
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
