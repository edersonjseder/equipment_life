package com.life.equipmentlife.model.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;
import com.life.equipmentlife.viewmodel.FireBaseQueryLiveData;

import static com.life.equipmentlife.common.constants.Constants.PROFILES;

public class ProfileSearchUidKeyRepository implements Runnable {
    private static final String TAG = ProfileSearchUidKeyRepository.class.getSimpleName();

    private DatabaseReference databaseReference;
    private MutableLiveData<Profile> data;
    private FireBaseQueryLiveData liveData;
    private ControllerCompositionRoot mCompositionRoot;
    private Profile profile;
    private Handler mHandler;
    private String uidKey;

    public ProfileSearchUidKeyRepository(DatabaseReference reference, ControllerCompositionRoot compositionRoot) {

        databaseReference = reference.child(PROFILES);
        mCompositionRoot = compositionRoot;
        profile = new Profile();
        mHandler = new Handler();

    }

    /**
     * Method fetches the profile by uikey stored in Firebase database
     *
     * @return the live data containing the profile object
     */
    public LiveData<Profile> fetchProfileByUidKey(String uid) {

        data = new MutableLiveData<>();

        uidKey = uid;

        Query query = databaseReference.orderByKey().equalTo(uid);

        liveData = new FireBaseQueryLiveData(query);

        mHandler.postDelayed(this, 3000);

        return data;

    }

    /**
     * Method that executes the observable call
     */
    @Override
    public void run() {

        liveData.observe(mCompositionRoot.getActivity(), dataSnapshot -> {

            Log.i(TAG, "run() inside method - dataSnapshot: " + dataSnapshot + " uid: " + uidKey);

            if (dataSnapshot != null) {
                Log.i(TAG, "run() inside method - first IF");

                if (dataSnapshot.hasChildren()) {
                    Log.i(TAG, "run() inside method - second IF");

                    handleReturn(dataSnapshot);

                } else {

                    data.setValue(null);

                }

            }

        });

    }

    /**
     * Handles the datasnapshot task containing the equipments list from Firebase
     *
     * @param dataSnapshot
     */
    private void handleReturn(DataSnapshot dataSnapshot) {
        Log.i(TAG, "handleReturn() inside method");

        for(DataSnapshot item : dataSnapshot.getChildren()) {

            if (item.getKey().equals(uidKey)) {
                Log.i(TAG, "handleReturn() inside method - inside for loop");

                profile = item.getValue(Profile.class);
                profile.setId(item.getKey());

                data.setValue(profile);

                break;

            }

        }

    }

}
