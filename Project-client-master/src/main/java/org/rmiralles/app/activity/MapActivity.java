package org.rmiralles.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import org.rmiralles.app.Preferences;
import org.rmiralles.app.R;
import org.rmiralles.app.base.Text;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleMap.OnMarkerClickListener, GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private SharedPreferences sharedPreferences;
    private GoogleMap mMap;
    private Marker marker;
    private String type;

    public MediaPlayer mp;
    private boolean boolSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();

        mp = MediaPlayer.create(this, R.raw.click);

        sharedPreferences = getSharedPreferences("org.rmiralles.preferences", MODE_PRIVATE);

        type = getIntent().getStringExtra("type");

        if (!type.equalsIgnoreCase("newtext")){
            View fragment = findViewById(R.id.mapText);
            fragment.getLayoutParams().height = getWindowManager().getDefaultDisplay().getHeight();
        }

        if (type.equalsIgnoreCase("explore")){
            new Connect().execute();
        } else if(type.equalsIgnoreCase("onetext")){
            addOneMarker(new LatLng(Double.parseDouble(getIntent().getStringExtra("latitude")), Double.parseDouble(getIntent().getStringExtra("longitude"))), getIntent().getStringExtra("text"));
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (type.equalsIgnoreCase("newtext")){
                    addOneMarker(latLng, "-1");
                }
            }
        });
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!type.equalsIgnoreCase("onetext")){
            cameraToMyLocation();
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
                editor.putInt("login", -1);
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

    private void setUpMapIfNeeded(){
        if (mMap == null){
            MapsInitializer.initialize(this);

            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapText)).getMap();
            mMap.setMyLocationEnabled(true);
        }
    }

    private void cameraToMyLocation(){
        Location location = mMap.getMyLocation();
        CameraUpdate camera;
        if (location != null) {
            camera = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            mMap.moveCamera(camera);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f), 2000, null);
        } else {
            camera = CameraUpdateFactory.newLatLng(new LatLng(37, -122));
            mMap.moveCamera(camera);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.0f), 2000, null);
        }
    }

    private void cameraMarkerLocation(LatLng latLng){
        CameraUpdate camera = CameraUpdateFactory.newLatLng(latLng);
        mMap.moveCamera(camera);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f), 2000, null);
    }

    private void addMarkers(List<Text> texts){
        if (texts.size() > 0){
            for (Text text : texts){
                addOneMarker(new LatLng(Double.parseDouble(text.getLatitude()), Double.parseDouble(text.getLongitude())), String.valueOf(text.getId()));
            }
        }
    }

    private void addOneMarker(LatLng latLng, String id){
        if (type.equalsIgnoreCase("newtext")){
            if (marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker((new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));
        } else if((type.equalsIgnoreCase("onetext")) || (type.equalsIgnoreCase("explore"))){
            marker = mMap.addMarker(new MarkerOptions().position(latLng));
            marker.setSnippet(id);
            cameraMarkerLocation(latLng);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        if (type.equalsIgnoreCase("newtext")){
            return false;
        } else if ((type.equalsIgnoreCase("onetext")) || (type.equalsIgnoreCase("explore"))){
            Intent intent = new Intent (MapActivity.this, TextActivity.class);
            intent.putExtra("text", marker.getSnippet());
            startActivity(intent);
            finish();
        }
        return false;
    }

    private boolean addMarkerToMyLocation(){
        Location location = mMap.getMyLocation();
        if (location == null){
            return false;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        addOneMarker(latLng, "-1");

        return true;
    }

    public class Connect extends AsyncTask<Void, Void, List> {

        @Override
        protected List<Text> doInBackground(Void... voids) {
            try {
                final String URL = "http://192.168.1.50:8080/gettexts?username=" + sharedPreferences.getString("username", "") + "&password=" + sharedPreferences.getString("password", "");
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Text[] texts = restTemplate.getForObject(URL, Text[].class);
                List<Text> textList = new ArrayList<Text>();
                for (Text text : texts) {
                    textList.add(text);
                }
                return textList;
            }catch (Exception e){
                Log.e("", e.getMessage(), e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(List textList){
            addMarkers(textList);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
