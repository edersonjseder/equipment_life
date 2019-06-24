package com.life.equipmentlife.paid.view.activities.signup.signupfacebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.signup.signupfacebook.signupfacebookview.SignUpFacebookView;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

public class SignUpFacebookActivity extends BaseActivity {

    private static final String TAG = SignUpFacebookActivity.class.getSimpleName();

    // Session Manager Class
    private SessionManager session;

    private ScreensNavigator mScreensNavigator;

    private FirebaseAuth mAuth;

    private SignUpFacebookView mFacebookView;

    private ProfileDao implProfileDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() inside method");

        session = getCompositionRoot().getSessionManager();

        mAuth = getFirebaseAuth();

        mScreensNavigator = getCompositionRoot().getScreensNavigator();

        implProfileDao = getCompositionRoot().getProfileDao();

        mFacebookView =
                getCompositionRoot().getViewFactory().getSignUpFacebookView(null, session,
                        mScreensNavigator,this, implProfileDao);

        setContentView(mFacebookView.getRootView());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFacebookView.bindFireBaseAuth(mAuth);
    }

    /**
     * Method called when facebook login is clicked by user
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookView.activityResult(requestCode, resultCode, data);
    }

}
