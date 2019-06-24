package com.life.equipmentlife.paid.view.activities.signup.signupfacebook.signupfacebookview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.life.equipmentlife.common.constants.Constants.DATA;
import static com.life.equipmentlife.common.constants.Constants.PROFILE_ID;
import static com.life.equipmentlife.common.constants.Constants.URL;
import static com.life.equipmentlife.common.constants.Constants.USER_EMAIL;
import static com.life.equipmentlife.common.constants.Constants.USER_NAME;
import static com.life.equipmentlife.common.constants.Constants.USER_PICTURE;
import static com.life.equipmentlife.model.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static com.life.equipmentlife.model.session.SessionManager.HAS_LOGGED_IN_FIRST_TIME_ALREADY;
import static com.life.equipmentlife.model.session.SessionManager.KEY_EMAIL;
import static com.life.equipmentlife.model.session.SessionManager.KEY_NAME;

public class SignUpFacebookViewImpl extends BaseObservableViewMvc implements SignUpFacebookView,
        FacebookCallback<LoginResult>, OnCompleteListener<AuthResult>,
        GraphRequest.GraphJSONObjectCallback, ProfileDataChangeUidKeyListener {
    private static final String TAG = SignUpFacebookViewImpl.class.getSimpleName();

    /** Facebook sign in **/
    @BindView(R.id.btn_facebook_sign_in)
    protected LoginButton btnFacebookSignIn;

    @BindView(R.id.ll_overbox_facebook_login_loading)
    protected LinearLayout llOverboxFacebookLoginLoading;

    private CallbackManager callbackManager;

    private SessionManager mSession;

    private FirebaseAuth mAuth;

    private Profile mProfile;

    private String fireBaseUidKey;

    private LoginResult mLoginResult;

    private FragmentActivity mActivity;

    private ScreensNavigator mScreensNavigator;

    private ProfileDao mImplProfileDao;

    public SignUpFacebookViewImpl(LayoutInflater inflater,
                                  ViewGroup parent,
                                  SessionManager session,
                                  ScreensNavigator screensNavigator,
                                  FragmentActivity activity,
                                  ProfileDao implProfileDao) {

        setRootView(inflater.inflate(R.layout.activity_sign_up_facebook, parent, false));

        ButterKnife.bind(this, getRootView());

        mSession = session;

        mActivity = activity;

        mScreensNavigator = screensNavigator;

        mImplProfileDao = implProfileDao;

        callbackManager = CallbackManager.Factory.create();

        btnFacebookSignIn.setReadPermissions(Arrays.asList("public_profile", "user_photos", "email"));

        btnFacebookSignIn.registerCallback(callbackManager, this);

        if (AccessToken.getCurrentAccessToken() != null) {

            mProfile = new Profile();

            String keyId = session.pref.getString(PROFILE_ID, "");
            String name = session.pref.getString(KEY_NAME, "");
            String email = session.pref.getString(KEY_EMAIL, "");

            mProfile.setId(keyId);
            mProfile.setName(name);
            mProfile.setEmail(email);

            mScreensNavigator.goToProfileRegistration(mProfile.getId(), mProfile);

        }

    }

    /**
     * Method called from onActivityResult on activity class to execute the Facebook callbackManager
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void activityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This method receives Firebase credentials data from activity
     *
     * @param auth
     */
    @Override
    public void bindFireBaseAuth(FirebaseAuth auth) {
        mAuth = auth;
    }

    /**
     * The onSuccess method called from callbackManager to handle the Facebook login result
     *
     * @param loginResult
     */
    @Override
    public void onSuccess(LoginResult loginResult) {

        handleFacebookAccessToken(loginResult);

    }

    /**
     * Method handles the result of login made by Facebook API
     *
     * @param loginResult
     */
    private void handleFacebookAccessToken(LoginResult loginResult) {
        Log.d(TAG, "handleFacebookAccessToken:" + loginResult.getAccessToken());

        mLoginResult = loginResult;

        showProgressbarLoading();

        AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());

        mAuth.signInWithCredential(credential)
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

        if (task.isSuccessful()) {

            fireBaseUidKey = task.getResult().getUser().getUid();

            Log.d(TAG, "onComplete() inside method - fireBaseUidKey:" + fireBaseUidKey);

            checkIfUserAlreadyHasDataInserted(task.getResult().getUser().getUid());

        } else{

            Toast.makeText(getContext(), "Authentication error",
                    Toast.LENGTH_LONG).show();

            hideProgressbarLoading();

        }

    }

    /**
     * Method checks on Firebase if the user is already registered
     *
     * @param uid the Firebase uid key
     */
    private void checkIfUserAlreadyHasDataInserted(String uid) {
        Log.d(TAG, "checkIfUserAlreadyHasDataInserted() inside method - fireBaseUidKey:" + uid);

        mImplProfileDao.fetchProfileById(uid, this);

    }

    /**
     * Method that receives the Profile object in Firebase query result
     *
     * @param profile
     */
    @Override
    public void onDataUidKeyLoaded(Profile profile) {
        Log.d(TAG, "onDataUidKeyLoaded() inside method - profile:" + profile);

        mSession.setFirstTimeFacebookLogin();

        if (profile != null) {
            Log.d(TAG, "onDataUidKeyLoaded() inside IF");

            mSession.setProfileOnPrefs(profile);

            mScreensNavigator.goToEquipmentListActivity(profile);

            hideProgressbarLoading();

        } else {Log.d(TAG, "onDataUidKeyLoaded() inside ELSE");

            GraphRequest request = GraphRequest.newMeRequest(mLoginResult.getAccessToken(), this);

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,email,name,picture.width(200)");
            request.setParameters(parameters);
            request.executeAsync();

        }

    }

    /**
     * Method called from callback interface after checking if the user info exists or not in database
     *
     * @param object
     * @param response
     */
    @Override
    public void onCompleted(JSONObject object, GraphResponse response) {
        getData(object);
    }

    /**
     * Gets the user data from JSON object to be added in the Profile object if the user is logging with Facebook
     * for the first time
     *
     * @param object
     */
    private void getData(JSONObject object) {
        Log.d(TAG, "getData() inside method");

        mProfile = new Profile();

        try {

            mSession.editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);
            mSession.editor.putBoolean(FROM_FACEBOOK_SIGNUP, true);
            mSession.editor.commit();

            mProfile.setId(fireBaseUidKey);
            mProfile.setName(object.getString(USER_NAME));
            mProfile.setEmail(object.getString(USER_EMAIL));
            mProfile.setPicture(object.getJSONObject(USER_PICTURE).getJSONObject(DATA).getString(URL));

            Log.d(TAG, "getData() inside method - mProfile.getId(): " + mProfile.getId());
            Log.d(TAG, "getData() inside method - mProfile.getEmail(): " + mProfile.getEmail());
            Log.d(TAG, "getData() inside method - mProfile.getName(): " + mProfile.getName());
            Log.d(TAG, "getData() inside method - mProfile.getPicture(): " + mProfile.getPicture());

            mScreensNavigator.goToProfileRegistration(mProfile.getId(), mProfile);

            hideProgressbarLoading();

        } catch (JSONException e) {
            e.printStackTrace();
            hideProgressbarLoading();
        }

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }

    public void showProgressbarLoading() {
        llOverboxFacebookLoginLoading.setVisibility(View.VISIBLE);
    }

    public void hideProgressbarLoading() {
        llOverboxFacebookLoginLoading.setVisibility(View.GONE);
    }

}
