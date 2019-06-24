package com.life.equipmentlife.paid.view.activities.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.login.loginview.LoginView;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

public class LoginActivity extends BaseActivity implements FirebaseAuth.AuthStateListener, OnFinishActivityListener,
        ProfileDataChangeUidKeyListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    public static final String FLAG_CHANGED = "configChangeFlag";
    public static final String EMAIL_SAVED = "emailSaved";

    private FirebaseAuth.AuthStateListener authListener;

    private FirebaseAuth mAuth;

    private FirebaseUser mUser;

    private SessionManager session;

    private ProfileDao profileDao;

    private LoginView mLoginViewMvc;

    private int configChangeFlag = 1;

    private String email;

    private String sessionUidKey;

    private ScreensNavigator mScreensNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate() inside method");

        mAuth = getFirebaseAuth();

        mUser = getFirebaseUser();

        session = getCompositionRoot().getSessionManager();

        profileDao = getCompositionRoot().getProfileDao();

        mScreensNavigator = getCompositionRoot().getScreensNavigator();

        authListener = this;

        mLoginViewMvc
                = getCompositionRoot().getViewFactory()
                                      .getLoginViewMvc(null, mAuth, session, profileDao, this);

        if (savedInstanceState != null) {
            Log.i(TAG, "onCreate() inside IF");

            configChangeFlag = savedInstanceState.getInt(FLAG_CHANGED);
            email = savedInstanceState.getString(EMAIL_SAVED);

        }

        if ((configChangeFlag == 1) && (email != null)) {
            Log.i(TAG, "onCreate() inside second IF");

            mLoginViewMvc.showDialogForgotPassword(configChangeFlag, email);

        }

        setContentView(mLoginViewMvc.getRootView());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume() inside method");
        mAuth.addAuthStateListener(authListener);
        mLoginViewMvc.bindEmailReset(configChangeFlag, email);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState() inside method");
        mLoginViewMvc.saveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLoginViewMvc.pause();

        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.i(TAG,"onAuthStateChanged() inside method");

        if (mUser != null) {
            Log.i(TAG,"onAuthStateChanged() inside IF");

            sessionUidKey = session.getProfileOnPrefs().getId();

            profileDao.fetchProfileById(sessionUidKey, this);

        }

    }

    @Override
    public void onDataUidKeyLoaded(Profile profile) {

        if (profile != null) {

            mScreensNavigator.goToEquipmentListActivity(profile);

            finish();

        } else {

            mScreensNavigator.goToEquipmentListActivity(session.getProfileOnPrefs());

            finish();

        }

    }

    @Override
    public void onFinishActivity() {
        finish();
    }

}
