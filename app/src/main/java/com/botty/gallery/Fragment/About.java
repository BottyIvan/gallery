package com.botty.gallery.Fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.botty.gallery.R;

/**
 * Created by BottyIvan on 09/01/15.
 */
public class About extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

    }
}
