package com.life.equipmentlife.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.life.equipmentlife.R;
import com.life.equipmentlife.model.networking.retrofit.RetrofitCompositionRoot;

import java.util.List;

public class FirebaseHandler extends Application {

    private RetrofitCompositionRoot mCompositionCep;

    @Override
    public void onCreate() {
        super.onCreate();

        mCompositionCep = new RetrofitCompositionRoot(this);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setProjectId(getString(R.string.project_id))
                .setApplicationId(String.valueOf(R.string.application_id))
                .setDatabaseUrl(getString(R.string.database_link))
                .setApiKey(getResources().getString(R.string.api_key)).build();

        FirebaseApp firebaseApp = null;

        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(this);

        if(firebaseApps!=null && !firebaseApps.isEmpty()){

            for(FirebaseApp app : firebaseApps){

                if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                    firebaseApp = app;
            }

        } else {

            firebaseApp = FirebaseApp.initializeApp(this, options);

        }

    }

    public RetrofitCompositionRoot getCompositionCep() {
        return mCompositionCep;
    }

}
