package com.example.helloworld;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
