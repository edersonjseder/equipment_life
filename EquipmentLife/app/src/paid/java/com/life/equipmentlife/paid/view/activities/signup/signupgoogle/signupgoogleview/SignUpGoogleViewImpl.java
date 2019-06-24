package com.life.equipmentlife.paid.view.activities.signup.signupgoogle.signupgoogleview;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.OnSignInGoogle;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpGoogleViewImpl extends BaseObservableViewMvc implements SignUpGoogleView,
        GoogleApiClient.OnConnectionFailedListener, OnCompleteListener<AuthResult>, ProfileDataChangeUidKeyListener {
    private static final String TAG = SignUpGoogleViewImpl.class.getSimpleName();

    @BindView(R.id.btn_google_sign_up)
    protected FrameLayout btnGoogleSignUp;

    @BindView(R.id.ll_overbox_google_login_loading)
    protected LinearLayout llOverboxGoogleLoginLoading;

    private GoogleApiClient googleApiClient;

    private FirebaseAuth mAuth;

    private GoogleSignInAccount mAccount;

    private SessionManager mSession;

    private Profile mProfile;

    private ProfileDao mImplProfileDao;

    private FragmentActivity mActivity;

    private ScreensNavigator mScreensNavigator;

    private OnSignInGoogle mOnSignInGoogle;

    private String uidKey;

    public SignUpGoogleViewImpl(LayoutInflater inflater,
                                  ViewGroup parent,
                                  SessionManager session,
                                  ScreensNavigator screensNavigator,
                                  FragmentActivity activity,
                                  OnSignInGoogle onSignInGoogle,
                                  ProfileDao implProfileDao) {

        setRootView(inflater.inflate(R.layout.activity_sign_up_google, parent, false));

        ButterKnife.bind(this, getRootView());

        mSession = session;

        mActivity = activity;

        mOnSignInGoogle = onSignInGoogle;

        mScreensNavigator = screensNavigator;

        mImplProfileDao = implProfileDao;

        // Google sign in
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.server_client_id))
                        .requestEmail()
                        .build();

        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(mActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        btnGoogleSignUp.setOnClickListener(view -> signInGoogle());

    }

    /**
     * This method receives Firebase data from activity
     *
     * @param auth
     */
    @Override
    public void bindFireBaseAuth(FirebaseAuth auth) {
        Log.i(TAG, "bindFireBaseAuth() inside method - mAuth: " + mAuth);
        mAuth = auth;
        Log.i(TAG, "bindFireBaseAuth() inside method - mAuth after: " + mAuth);
    }

    @Override
    public void showProgressbarLoading() {
        llOverboxGoogleLoginLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressbarLoading() {
        llOverboxGoogleLoginLoading.setVisibility(View.GONE);
    }

    /**
     * Method that starts the intent for signing in with Google after clicking the Google
     * button on screen
     */
    protected void signInGoogle() {
        Log.i(TAG, "signInGoogle() inside method");

        mOnSignInGoogle.signInGoogle(googleApiClient);

    }

    /**
     * It handles the Google authentication result coming from user credentials filled
     *
     * @param data
     */
    @Override
    public void onHandleGoogleResult(Intent data) {
        Log.i(TAG, "handleResultFromActivity() inside method - data: " + data);

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);

            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);

            Toast.makeText(getContext(),
                    getContext().getResources().getString(R.string.signup_profile_error) + e.getMessage(),
                    Toast.LENGTH_LONG).show();

            // ...
        }

    }

    /**
     * Creates user in Firebase through Google credentials and then it will be taken
     * to Profile registration to be completed with personal information
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.i(TAG, "firebaseAuthWithGoogle() inside method - account: " + account);
        Log.i(TAG, "firebaseAuthWithGoogle() inside method - token: " + account.getIdToken());
        Log.i(TAG, "firebaseAuthWithGoogle() inside method - ID: " + account.getId());
        mAccount = account;

        showProgressbarLoading();

        AuthCredential credentials = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credentials)
                .addOnCompleteListener(mActivity, this);

    }

    /**
     * Method onComplete called from Firebase onCompleteListener interface to execute the task
     * handle and manage the Firebase sign in
     *
     * @param task
     */
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d(TAG, "onComplete() inside method");

        try {
            //check if successful
            if (!task.isSuccessful()) {
                Log.d(TAG, "onComplete() inside IF");

                hideProgressbarLoading();

                Toast.makeText(getContext(),
                        getContext().getResources().getString(R.string.signup_profile_error) + task.getException(),
                        Toast.LENGTH_LONG).show();

            } else {

                checkIfUserAlreadyHasDataInserted(task.getResult().getUser().getUid());

            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Method checks on Firebase if the user is already registered
     *
     * @param uid the Firebase uid key
     */
    private void checkIfUserAlreadyHasDataInserted(String uid) {

        uidKey = uid;

        mImplProfileDao.fetchProfileById(uid, this);

    }

    /**
     * Method that receives the Profile object in Firebase query result
     *
     * @param profile
     */
    @Override
    public void onDataUidKeyLoaded(Profile profile) {
        Log.i(TAG,"onDataUidKeyLoaded() inside method");

        mSession.setFirstTimeGoogleLogin();

        if (profile != null) {
            Log.i(TAG,"onDataUidKeyLoaded() inside IF");

            mSession.setProfileOnPrefs(profile);

            mScreensNavigator.goToEquipmentListActivity(profile);

            hideProgressbarLoading();

        } else {
            Log.d(TAG, "onDataUidKeyLoaded() inside ELSE");

            mProfile = new Profile();
            String picture;

            String name = mAccount.getDisplayName();
            String familyName = mAccount.getFamilyName();
            String email = mAccount.getEmail();

            if (mAccount.getPhotoUrl() != null) {
                picture = mAccount.getPhotoUrl().toString();
            } else {
                picture = "";
            }

            Log.i(TAG, "onDataUidKeyLoaded() inside method - picture: " + picture);

            // Populate part of profile object
            mProfile.setName(name + " " + familyName);
            mProfile.setEmail(email);
            mProfile.setPicture(picture);

            Log.i(TAG, "onDataUidKeyLoaded() inside method - uidKey: " + uidKey);

            mScreensNavigator.goToProfileRegistration(uidKey, mProfile);

            hideProgressbarLoading();

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
