package com.life.equipmentlife.paid.view.activities.equipment.list;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.FrameLayout;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.listener.OnGoingToEquipmentRegistrationListener;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseInitialActivity;
import com.life.equipmentlife.paid.view.activities.equipment.registration.EquipmentRegistrationActivity;
import com.life.equipmentlife.paid.view.logout.LogoutApplication;
import com.life.equipmentlife.paid.view.navdrawer.NavDrawerView;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

import static com.life.equipmentlife.common.constants.Constants.PROFILE;
import static com.life.equipmentlife.model.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static com.life.equipmentlife.model.session.SessionManager.FROM_GOOGLE_SIGNUP;

public class EquipmentListActivity extends BaseInitialActivity implements OnGoingToEquipmentRegistrationListener,
        NavDrawerView.OnSelectDrawerMenuItem {

    // Constant for logging
    private static final String TAG = EquipmentListActivity.class.getSimpleName();

    private int RC_LOGIN = 100;

    // Flag to check if login was from Google
    private boolean isLoginFromGoogle;

    // Flag to check if login was from facebook
    private boolean isLoginFromFacebook;

    private Profile profileOwner;

    private SessionManager session;

    private NavDrawerView mViewMvc;

    private ScreensNavigator mScreensNavigator;

    private LogoutApplication logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = getCompositionRoot().getSessionManager();

        mScreensNavigator = getCompositionRoot().getScreensNavigator();

        logout = getCompositionRoot().getLogoutApplication();

        // Check if the login was from Google
        isLoginFromGoogle = session.loginFromGoogle();

        // Check if the login was from Facebook
        isLoginFromFacebook = session.loginFromFacebook();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Log.i(TAG,"onCreate() inside method - inside if");

            profileOwner = (Profile) bundle.getSerializable(PROFILE);

            Log.i(TAG,"onCreate() inside method - inside if - profileOwner.picture: " + profileOwner.getPicture());
            Log.i(TAG,"onCreate() inside method - inside if - profileOwner.getAddress(): " + profileOwner.getAddress());


        } else {
            Log.i(TAG,"onCreate() inside method - inside else");

            profileOwner = session.getProfileOnPrefs();

            Log.i(TAG,"onCreate() inside ELSE - profile: " + profileOwner);
            Log.i(TAG,"onCreate() inside ELSE - profile.picture: " + profileOwner.getPicture());

        }

        mViewMvc =
                getCompositionRoot().getViewFactory()
                                    .getNavDrawerView(null, profileOwner);

        setContentView(mViewMvc.getRootView());

        if (savedInstanceState == null) {
            mScreensNavigator.goToListFragment();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewMvc.registerListener(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewMvc.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Opens the drawer menu
     */
    @Override
    public void openDrawer() {
        mViewMvc.openDrawer();
    }

    /**
     * Closes the drawer menu
     */
    @Override
    public void closeDrawer() {
        mViewMvc.closeDrawer();
    }

    /**
     * Verifies if the drawer is opened
     *
     * @return the boolean value
     */
    @Override
    public boolean isDrawerOpen() {
        return mViewMvc.isDrawerOpen();
    }

    /**
     * gets the frame to put the fragment list on
     *
     * @return the boolean value
     */
    @Override
    public FrameLayout getFragmentFrame() {
        return mViewMvc.getFragmentFrame();
    }

    /**
     * Method takes to the Equipment registration screen
     *
     * @param fabButtonAddNewEquipment the button component to apply animation
     */
    @Override
    public void goToEquipmentRegistration(FloatingActionButton fabButtonAddNewEquipment) {

        Class destinationClass = EquipmentRegistrationActivity.class;

        Intent intentToStartDetailActivity = new Intent(this, destinationClass);

        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation(this, fabButtonAddNewEquipment, getString(R.string.transition_enter_registration));

        startActivityForResult(intentToStartDetailActivity, RC_LOGIN, options.toBundle());

    }

    /**
     * Method goes to the profile details screen when the item is clicked from drawer menu
     *
     * @param profile
     */
    @Override
    public void goToProfileDetails(Profile profile) {
        Log.i(TAG, "goToProfileDetails() inside method");
        mScreensNavigator.goToProfileDetails(profile);
    }

    /**
     * Logs out the app when logOut menu is clicked
     */
    @Override
    public void logoutProfile() {
        Log.i(TAG, "logoutProfile() inside method");

        // Check if the login was from Google or Facebook
        if (isLoginFromGoogle) {

            logout.loggingOutGoogle();

        } else if (isLoginFromFacebook) {

            logout.loggingOutFacebook();

        } else {

            logout.logoutApp();

        }

    }

}