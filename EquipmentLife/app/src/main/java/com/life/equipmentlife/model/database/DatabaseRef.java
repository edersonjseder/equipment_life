package com.life.equipmentlife.model.database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.life.equipmentlife.common.constants.Constants.DATABASE_NAME;

public class DatabaseRef {
    private static final String TAG = DatabaseRef.class.getSimpleName();

    private DatabaseReference reference;
    private FirebaseDatabase database;

    public DatabaseRef() {
        Log.i(TAG, "DatabaseRef() inside constructor");

        database = FirebaseDatabase.getInstance();
        reference = database.getReference(DATABASE_NAME);
        reference.keepSynced(true);

    }

    public DatabaseReference getReference() {
        Log.i(TAG, "getReference() inside method - reference: " + reference.getRef());
        return reference;
    }

}
