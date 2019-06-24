package com.life.equipmentlife.paid.view.activities.start;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.start.startview.StartScreenView;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

import static com.life.equipmentlife.model.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static com.life.equipmentlife.model.session.SessionManager.FROM_GOOGLE_SIGNUP;

public class StartScreenActivity extends BaseActivity  {
    private static final String TAG = StartScreenActivity.class.getSimpleName();

    public static final String CONFIG_FLAG_CHANGED = "configChangeFlag";
    public static final String EMAIL_SAVED = "emailSaved";
    public static final String PASSWORD_SAVED = "passwordSaved";
    public static final String CONFIRM_PASSWORD_SAVED = "confirmPasswordSaved";

    private FirebaseAuth mAuth;

    private SessionManager session;

    private ScreensNavigator mScreensNavigator;

    private StartScreenView mScreenView;

    private String mEmail, mPassword, mConfirmPassword;

    private int configChangeFlag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate() inside method");

        session = getCompositionRoot().getSessionManager();

        mAuth = getFirebaseAuth();

        boolean google = session.pref.getBoolean(FROM_GOOGLE_SIGNUP, false);
        boolean facebook = session.pref.getBoolean(FROM_FACEBOOK_SIGNUP, false);

        mScreensNavigator = getCompositionRoot().getScreensNavigator();

        mScreenView = getCompositionRoot().getViewFactory()
                .getStartScreenView(null, mScreensNavigator, this);

        /**
         * Verify if the user has logged in for the first time on the app
         */
        if (session.verifyIfUserHasLoggedInAlreadyFirstTime()) {

            if (google) { // Verify if the login was from Google

                if (session.isLoggedOut()) { // Verify if the user has logged out from the app

                    // Go to Google login screen
                    mScreensNavigator.goToGoogleLogin();

                } else {

                    // Go to the social check activity to check if the user
                    // is registered already on the app by social media
                    mScreensNavigator.goToSignUpSocialCheck();

                }

                finish();

            } else if (facebook) { // Verify if the login was from Facebook

                if (session.isLoggedOut()) { // Verify if the user has logged out from the app

                    // Go to the Facebook login screen
                    mScreensNavigator.goToFacebookLogin();

                } else {

                    // Go to the social check activity to check if the user
                    // is registered already on the app by social media
                    mScreensNavigator.goToSignUpSocialCheck();

                }

                finish();

            } else {

                // Otherwise go to the login screen
                mScreensNavigator.goToLoginScreen();

                finish();

            }

        }

        if (savedInstanceState != null) {

            configChangeFlag = savedInstanceState.getInt(CONFIG_FLAG_CHANGED);
            mEmail = savedInstanceState.getString(EMAIL_SAVED);
            mPassword = savedInstanceState.getString(PASSWORD_SAVED);
            mConfirmPassword = savedInstanceState.getString(CONFIRM_PASSWORD_SAVED);

        }

        if((configChangeFlag == 1) &&
                (mEmail != null) &&
                (mPassword != null) &&
                (mConfirmPassword != null)){

            mScreenView.showDialogCredentialsRegistration(configChangeFlag, mEmail, mPassword, mConfirmPassword);

        }

        setContentView(mScreenView.getRootView());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mScreenView.bindCredentialsData(configChangeFlag, mEmail, mPassword, mConfirmPassword, mAuth);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mScreenView.pause();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mScreenView.saveInstanceState(outState);
    }

}
