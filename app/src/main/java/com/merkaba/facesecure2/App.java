package com.merkaba.facesecure2;

import android.app.Application;
import android.content.ContextWrapper;
import android.widget.ProgressBar;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.pixplicity.easyprefs.library.Prefs;
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        // init chaquopy
        Python.start(new AndroidPlatform(this));
    }
}
