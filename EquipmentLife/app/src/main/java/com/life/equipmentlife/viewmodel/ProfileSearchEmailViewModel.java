package com.life.equipmentlife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.repository.ProfileSearchEmailRepository;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;

public class ProfileSearchEmailViewModel extends AndroidViewModel {
    private static final String TAG = ProfileSearchEmailViewModel.class.getSimpleName();

    // This data will be fetched asynchronously
    private LiveData<Profile> profileMutableLiveData;

    private ProfileSearchEmailRepository mProfileSearchEmailRepository;

    public ProfileSearchEmailViewModel(Application application, ControllerCompositionRoot compositionRoot) {
        super(application);

        mProfileSearchEmailRepository = compositionRoot.getProfileSearchEmailRepository();

    }

    @NonNull
    public LiveData<Profile> searchProfileByEmailLiveData(String email) {
        Log.i(TAG, "searchProfileByEmailLiveData() inside method - email: " + email);

        profileMutableLiveData = mProfileSearchEmailRepository.fetchProfileByEmail(email);

        return profileMutableLiveData;

    }

}
