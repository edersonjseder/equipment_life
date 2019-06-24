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

import static com.life.equipmentlife.common.constants.Constants.USER_EMAIL;
import static com.life.equipmentlife.common.constants.Constants.PROFILES;

public class ProfileSearchEmailRepository implements Runnable {
    private static final String TAG = ProfileSearchEmailRepository.class.getSimpleName();

    private DatabaseReference databaseReference;
    private MutableLiveData<Profile> data;
    private FireBaseQueryLiveData liveData;
    private ControllerCompositionRoot mCompositionRoot;
    private Profile profile;
    private Handler mHandler;
    private String mEmail;

    public ProfileSearchEmailRepository(DatabaseReference reference, ControllerCompositionRoot compositionRoot) {

        databaseReference = reference.child(PROFILES);
        mCompositionRoot = compositionRoot;
        profile = new Profile();
        mHandler = new Handler();

    }

    /**
     * Method fetches the profile by email stored in Firebase database
     *
     * @return the live data containing the profile object
     */
    public LiveData<Profile> fetchProfileByEmail(String email) {

        mEmail = email;

        data = new MutableLiveData<>();

        Query query = databaseReference.orderByChild(USER_EMAIL).equalTo(email);

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

            Log.i(TAG, "onChanged() inside method - dataSnapshot: " + dataSnapshot);

            if (dataSnapshot.exists()) {
                Log.i(TAG, "onChanged() inside method - first IF");

                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    Log.i(TAG, "onChanged() inside method - second IF");

                    handleReturn(dataSnapshot);

                }

            } else {

                data.setValue(null);

            }

        });

    }

    /**
     * Handles the datasnapshot task containing the profile object from Firebase
     *
     * @param dataSnapshot
     */
    private void handleReturn(DataSnapshot dataSnapshot) {

        for(DataSnapshot item : dataSnapshot.getChildren()) {

            if (item.child(USER_EMAIL).getValue().equals(mEmail)) {

                profile = item.getValue(Profile.class);
                profile.setId(item.getKey());

                data.setValue(profile);

                break;

            }

        }

    }

}
