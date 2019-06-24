package com.life.equipmentlife.paid.view.screensnavigator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;

import com.life.equipmentlife.common.fragmentframehelper.FragmentFrameHelper;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.paid.view.activities.equipment.details.EquipmentDetailsActivity;
import com.life.equipmentlife.paid.view.activities.equipment.list.EquipmentListActivity;
import com.life.equipmentlife.paid.view.activities.signup.signupgoogle.SignUpGoogleActivity;
import com.life.equipmentlife.paid.view.activities.signup.socialcheck.SignUpSocialCheck;
import com.life.equipmentlife.paid.view.activities.start.StartScreenActivity;
import com.life.equipmentlife.paid.view.activities.login.LoginActivity;
import com.life.equipmentlife.paid.view.activities.profile.details.ProfileDetailsActivity;
import com.life.equipmentlife.paid.view.activities.profile.registration.ProfileRegistrationActivity;
import com.life.equipmentlife.paid.view.activities.signup.signupfacebook.SignUpFacebookActivity;
import com.life.equipmentlife.paid.view.fragments.EquipmentListFragment;

import static com.life.equipmentlife.common.constants.Constants.PROFILE;
import static com.life.equipmentlife.common.constants.Constants.UID;
import static com.life.equipmentlife.model.session.SessionManager.UID_KEY;

public class ScreensNavigator {

    // Constant for logging
    private static final String TAG = ScreensNavigator.class.getSimpleName();

    private Context mContext;
    private FragmentFrameHelper mFragmentFrameHelper;

    /**
     * Constructor to be used on the equipment list and the rest of the activities
     *
     * @param context needed to start activity
     * @param fragmentFrameHelper needed to start the fragment
     */
    public ScreensNavigator(Context context, FragmentFrameHelper fragmentFrameHelper) {
        mContext = context;
        mFragmentFrameHelper = fragmentFrameHelper;
    }

    /**
     * Method takes to the Login Screen
     */
    public void goToLoginScreen() {

        Intent intent = new Intent(mContext, LoginActivity.class);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(intent);

    }

    /**
     * Method takes to the Sign Up Google Activity
     */
    public void goToSignUpSocialCheck() {

        Intent intent = new Intent(mContext, SignUpSocialCheck.class);

        // Closing all the Activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(intent);

    }

    /**
     * Method takes to the Profile registration
     *
     * @param uid to put in the User id field
     * @param profile to take this object with email and password filled to be completed
     */
    public void goToProfileRegistration(String uid, Profile profile) {

        Class destinationClass = ProfileRegistrationActivity.class;

        Bundle bundle = new Bundle();
        bundle.putString(UID, uid);
        bundle.putSerializable(Intent.EXTRA_TEXT, profile);

        Intent intentToStartProfileRegistrationActivity = new Intent(mContext, destinationClass);

        intentToStartProfileRegistrationActivity.putExtras(bundle);

        mContext.startActivity(intentToStartProfileRegistrationActivity);

    }

    /**
     * Method takes to the Equipment details information
     *
     * @param equipment when user selects this object on the list
     * @param optionsCompat to be used on the Activity to execute the animation
     */
    public void goToEquipmentDetails(Equipment equipment, ActivityOptionsCompat optionsCompat) {
        Log.i(TAG, "goToEquipmentDetails() inside method - equipment: " + equipment);

        Class destinationClass = EquipmentDetailsActivity.class;

        Intent intentToStartDetailActivity = new Intent(mContext, destinationClass);

        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, equipment);

        mContext.startActivity(intentToStartDetailActivity, optionsCompat.toBundle());

    }

    /**
     * Method takes to the profile details screen when user clicks on
     * profile menu item
     *
     * @param profileOwner the object to be shown on the details screen
     */
    public void goToProfileDetails(Profile profileOwner) {

        Class destinationClass = ProfileDetailsActivity.class;

        Intent intentToStartDetailActivity = new Intent(mContext, destinationClass);

        Bundle bundle = new Bundle();

        bundle.putSerializable(PROFILE, profileOwner);

        intentToStartDetailActivity.putExtras(bundle);

        mContext.startActivity(intentToStartDetailActivity);

    }

    /**
     * Method takes to the activity start screen
     */
    public void goToStartScreen() {

        Class destinationClass = StartScreenActivity.class;

        Intent intentToStartScreenActivity = new Intent(mContext, destinationClass);

        mContext.startActivity(intentToStartScreenActivity);

    }

    /**
     * Method takes to the Facebook login screen
     */
    public void goToFacebookLogin() {

        Class destinationClass = SignUpFacebookActivity.class;

        Intent intentToSignUpFacebookActivity = new Intent(mContext, destinationClass);

        mContext.startActivity(intentToSignUpFacebookActivity);

    }

    /**
     * Method takes to the Google login activity
     */
    public void goToGoogleLogin() {

        Class destinationClass = SignUpGoogleActivity.class;

        Intent intentToSignUpGoogleActivity = new Intent(mContext, destinationClass);

        mContext.startActivity(intentToSignUpGoogleActivity);

    }

    /**
     * Method takes to the list fragment
     *
     */
    public void goToListFragment() {
        mFragmentFrameHelper.replaceFragmentAndClearBackstack(EquipmentListFragment.getInstance());
    }

    /**
     * Method that takes user to the equipment list initial screen
     */
    public void goToEquipmentListActivity(Profile profile) {

        Class destinationClass = EquipmentListActivity.class;

        Intent intentToEquipmentListActivity = new Intent(mContext, destinationClass);

        Bundle bundle = new Bundle();

        bundle.putString(UID_KEY, profile.getId());

        bundle.putSerializable(PROFILE, profile);

        intentToEquipmentListActivity.putExtras(bundle);

        mContext.startActivity(intentToEquipmentListActivity);

    }

}
