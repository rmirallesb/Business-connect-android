package org.rmiralles.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import org.rmiralles.app.Preferences;
import org.rmiralles.app.R;
import org.rmiralles.app.fragment.FilterListFragment;
import org.rmiralles.app.fragment.FullListFragment;
import org.rmiralles.app.fragment.TabsListener;

public class MainActivity extends Activity {

    private SharedPreferences sharedPreferences;
    public MediaPlayer mp;
    private boolean boolSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp = MediaPlayer.create(this, R.raw.click);

        sharedPreferences = getSharedPreferences("org.rmiralles.preferences", MODE_PRIVATE);

        loadTabs();
        getActionBar().setTitle("");
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#b56dff")));
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

    private void loadTabs() {

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab1 = actionBar.newTab().setText(R.string.title_wall);
        ActionBar.Tab tab2 = actionBar.newTab().setText(R.string.title_filterlist_fragment);

        Fragment tabFragment1 = new FullListFragment();
        Fragment tabFragment2 = new FilterListFragment();

        tab1.setTabListener(new TabsListener(tabFragment1));
        tab2.setTabListener(new TabsListener(tabFragment2));

        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}