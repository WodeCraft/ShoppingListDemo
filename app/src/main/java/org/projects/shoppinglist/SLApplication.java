package org.projects.shoppinglist;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class SLApplication extends Application {

    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Log.d("firebase", "Persistence enabled");
        } else {
            Log.d("firebase", "Persistence not enabled.");
        }
    }

}
