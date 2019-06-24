package com.life.equipmentlife.paid.view.activities.signup.socialcheck;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpSocialCheck extends BaseActivity implements FirebaseAuth.AuthStateListener,
        ProfileDataChangeUidKeyListener {
    private static final String TAG = SignUpSocialCheck.class.getSimpleName();

    @BindView(R.id.ll_overbox_check_social_login_loading)
    protected LinearLayout llOverBoxCheckSocialLoginLoading;

    private FirebaseAuth.AuthStateListener authListener;

    private FirebaseAuth mAuth;

    private FirebaseUser mUser;

    private SessionManager mSession;

    private ScreensNavigator mScreensNavigator;

    private ProfileDao implProfileDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging_in_loading_screen);

        ButterKnife.bind(this);

        mAuth = getFirebaseAuth();

        mUser = getFirebaseUser();

        mSession = getCompositionRoot().getSessionManager();

        implProfileDao = getCompositionRoot().getProfileDao();

        mScreensNavigator = getCompositionRoot().getScreensNavigator();

        authListener = this;

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }

    }

    /**
     * Method that verifies if the FireBase user is logged on
     *
     * @param firebaseAuth
     */
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.i(TAG,"onAuthStateChanged() inside method - mUser: " + mUser);

        llOverBoxCheckSocialLoginLoading.setVisibility(View.VISIBLE);

        if (mUser != null) {
            Log.i(TAG,"onAuthStateChanged() inside IF");

            mSession.setLoggedOut(false);

            implProfileDao.fetchProfileById(mUser.getUid(), this);

        }

    }

    @Override
    public void onDataUidKeyLoaded(Profile profile) {
        Log.i(TAG,"onDataUidKeyLoaded() inside method - profile: " + profile);

        llOverBoxCheckSocialLoginLoading.setVisibility(View.GONE);

        if (profile != null) {
            Log.i(TAG,"onDataUidKeyLoaded() inside IF");

            mSession.setProfileOnPrefs(profile);

            mScreensNavigator.goToEquipmentListActivity(profile);

            finish();

        }

    }

}
