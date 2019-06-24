package com.life.equipmentlife.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.repository.ProfileSearchUidKeyRepository;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;

public class ProfileSearchUidKeyViewModel extends AndroidViewModel {
    private static final String TAG = ProfileSearchUidKeyViewModel.class.getSimpleName();

    // This data will be fetched asynchronously
    private LiveData<Profile> profileMutableLiveData;

    private ProfileSearchUidKeyRepository mProfileSearchUidKeyRepository;

    public ProfileSearchUidKeyViewModel(Application application, ControllerCompositionRoot compositionRoot) {
        super(application);

        mProfileSearchUidKeyRepository = compositionRoot.getProfileSearchUidKeyRepository();

    }

    @NonNull
    public LiveData<Profile> searchProfileByUidKeyLiveData(String uid) {
        Log.i(TAG, "searchProfileByUidKeyLiveData() inside method - uid: " + uid);

        profileMutableLiveData = mProfileSearchUidKeyRepository.fetchProfileByUidKey(uid);

        return profileMutableLiveData;

    }

}
