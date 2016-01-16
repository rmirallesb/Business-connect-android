package org.rmiralles.app.fragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import org.rmiralles.app.R;

public class TabsListener implements ActionBar.TabListener {

    private Fragment fragment;

    public TabsListener(Fragment fragment)
    {
        this.fragment = fragment;
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.container, fragment);
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }
}
