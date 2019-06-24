package com.life.equipmentlife.viewmodel;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FireBaseQueryLiveData extends LiveData<DataSnapshot> {

    private static final String TAG = FireBaseQueryLiveData.class.getSimpleName();

    private final Query query;
    private final MyValueEventListener listener = new MyValueEventListener();

    public FireBaseQueryLiveData(Query query) {
        this.query = query;
    }

    public FireBaseQueryLiveData(DatabaseReference ref) {
        this.query = ref;
    }

    @Override
    protected void onActive() {
        super.onActive();
        query.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        query.removeEventListener(listener);
    }

    private class MyValueEventListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.i(TAG,"onDataChange() inside method - dataSnapshot: " + dataSnapshot);
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to query " + query, databaseError.toException());
            setValue(null);
        }

    }
}
