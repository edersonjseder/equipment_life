package com.life.equipmentlife.paid.view.activities.signup.signupgoogle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.OnSignInGoogle;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.signup.signupgoogle.signupgoogleview.SignUpGoogleView;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

public class SignUpGoogleActivity extends BaseActivity implements OnFinishActivityListener, OnSignInGoogle {
    private static final String TAG = SignUpGoogleActivity.class.getSimpleName();

    private static final int REQ_CODE = 7;

    private FirebaseAuth mAuth;

    private SessionManager session;

    private ScreensNavigator mScreensNavigator;

    private SignUpGoogleView mGoogleView;

    private ProfileDao implProfileDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate() inside method - user: " + getFirebaseUser());

        session = getCompositionRoot().getSessionManager();

        mAuth = getFirebaseAuth();

        implProfileDao = getCompositionRoot().getProfileDao();

        mScreensNavigator = getCompositionRoot().getScreensNavigator();

        mGoogleView = getCompositionRoot().getViewFactory().getSignUpGoogleView(null, session,
                mScreensNavigator,this, this, implProfileDao);

        setContentView(mGoogleView.getRootView());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleView.bindFireBaseAuth(mAuth);
    }

    /**
     * Method that starts the intent for signing in with Google after clicking the Google
     * button on screen
     */
    @Override
    public void signInGoogle(GoogleApiClient googleApiClient) {
        Log.i(TAG, "signInGoogle() inside method");

        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);

    }

    /**
     * Method that deals with the result of Google auth api when user puts his credentials on screen
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "activityResult() inside method - requestCode == REQ_CODE: " + requestCode + " == " + REQ_CODE);

        if (requestCode == REQ_CODE) {

            mGoogleView.onHandleGoogleResult(data);

        }

    }

    @Override
    public void onFinishActivity() {
        finish();
    }

}