package com.life.equipmentlife.model.daoimpl;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.util.Log;

import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.database.DatabaseRef;
import com.life.equipmentlife.common.listener.ProfileDataChangeEmailListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;
import com.life.equipmentlife.viewmodel.CepSearchViewModel;
import com.life.equipmentlife.viewmodel.CepSearchViewModelFactory;
import com.life.equipmentlife.viewmodel.ProfileSearchEmailViewModel;
import com.life.equipmentlife.viewmodel.ProfileSearchEmailViewModelFactory;
import com.life.equipmentlife.viewmodel.ProfileSearchUidKeyViewModel;
import com.life.equipmentlife.viewmodel.ProfileSearchUidKeyViewModelFactory;

import static com.life.equipmentlife.common.constants.Constants.PROFILES;

public class ProfileDaoImpl implements ProfileDao {

    private static final String TAG = ProfileDaoImpl.class.getSimpleName();

    private DatabaseRef reference;
    private Profile mProfile;

    private ProfileDataChangeUidKeyListener mListener;

    private ProfileDataChangeEmailListener mEmailListener;

    private ControllerCompositionRoot mCompositionRoot;

    private ProfileSearchUidKeyViewModel mProfileSearchUidKeyViewModel;
    private ProfileSearchUidKeyViewModelFactory viewModelFactory;

    private ProfileSearchEmailViewModel mProfileSearchEmailViewModel;
    private ProfileSearchEmailViewModelFactory viewModelEmailFactory;

    /**
     * Constructor for insert, update and delete
     */
    public ProfileDaoImpl() {
        reference = getDatabaseRef();
    }

    /**
     * Constructor with the viewmodel for searching
     *
     * @param application
     * @param compositionRoot
     */
    public ProfileDaoImpl(Application application,
                          ControllerCompositionRoot compositionRoot) {

        reference = getDatabaseRef();

        mCompositionRoot = compositionRoot;

        /** Search by uidKey **/
        viewModelFactory =
                new ProfileSearchUidKeyViewModelFactory(application, compositionRoot);

        mProfileSearchUidKeyViewModel =
                ViewModelProviders.of(compositionRoot.getActivity(), viewModelFactory)
                        .get(ProfileSearchUidKeyViewModel.class);

        /** Search by email **/
        viewModelEmailFactory =
                new ProfileSearchEmailViewModelFactory(application, compositionRoot);

        mProfileSearchEmailViewModel =
                ViewModelProviders.of(compositionRoot.getActivity(), viewModelEmailFactory)
                        .get(ProfileSearchEmailViewModel.class);

    }

    /**
     * Fetch profile by id from FireBase
     *
     * @param uid the id which will be used as parameter to the search
     * @param dataChangeUidKeyListener
     * @return
     */
    @Override
    public Profile fetchProfileById(String uid, ProfileDataChangeUidKeyListener dataChangeUidKeyListener) {
        Log.i(TAG, "fetchProfileById() inside method + uid: " + uid);

        mListener = dataChangeUidKeyListener;

        mProfileSearchUidKeyViewModel
                .searchProfileByUidKeyLiveData(uid).observe(mCompositionRoot.getActivity(), profile -> {
                Log.i(TAG, "observe() inside method + profile: " + profile);

                mProfile = profile;
                mListener.onDataUidKeyLoaded(profile);

        });

        return mProfile;

    }

    /**
     * Fetch profile by email from FireBase
     *
     * @param email the email which will be used as parameter to the search
     * @param dataChangeEmailListener
     * @return
     */
    @Override
    public Profile fetchProfileByEmail(String email, ProfileDataChangeEmailListener dataChangeEmailListener) {
        Log.i(TAG, "fetchProfileByEmail() inside method - email: " + email);

        mEmailListener = dataChangeEmailListener;

        mProfileSearchEmailViewModel
                .searchProfileByEmailLiveData(email).observe(mCompositionRoot.getActivity(), profile -> {

            Log.i(TAG, "observe() inside method + profile: " + profile);

            mProfile = profile;
            mEmailListener.onDataEmailLoaded(profile);

        });

        return mProfile;

    }


    /**
     * Insert new profile data on Firebase
     *
     * @param profile the object to be inserted
     * @param uidKey the key to be inserted inside profile before it will be inserted
     */
    @Override
    public void insertProfile(Profile profile, String uidKey) {
        Log.i(TAG, "insertProfile() inside method + profile: " + profile + " - " + uidKey);

        // Insert profile node on Firebase
        reference.getReference().getRef().child(PROFILES).child(uidKey).setValue(profile);

    }

    /**
     * Update the profile data on Firebase
     *
     * @param profile the object to be updated
     */
    @Override
    public void updateProfile(Profile profile) {
        Log.i(TAG, "updateProfile() inside method + profile: " + profile);

        String id = profile.getId();

        // I set the id to empty so it doesn't be added on firebase under the child id of profile
        profile.setId(null);

        reference.getReference().getRef().child(PROFILES).child(id).setValue(profile);

        // Put back the reference to be used later
        profile.setId(id);

    }

    private DatabaseRef getDatabaseRef() {

        if (reference == null) {
            reference = new DatabaseRef();
        }

        return reference;

    }

}
