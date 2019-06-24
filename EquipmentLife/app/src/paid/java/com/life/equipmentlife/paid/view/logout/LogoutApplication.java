package com.life.equipmentlife.paid.view.logout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.life.equipmentlife.R;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

public class LogoutApplication implements ResultCallback<Status>, GraphRequest.Callback {

    // Constant for logging
    private static final String TAG = LogoutApplication.class.getSimpleName();

    /** Google API **/
    private GoogleApiClient mGoogleApiClient;

    private GraphRequest graphRequest;

    private SessionManager mSession;

    private Context mContext;

    private ScreensNavigator mScreensNavigator;

    public LogoutApplication(ScreensNavigator screensNavigator, SessionManager session, Context context) {

        mSession = session;

        mContext = context;

        mScreensNavigator = screensNavigator;

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();

    }

    /**
     * Method that logs out of the application
     */
    public void logoutApp() {

        mSession.logoutUser();

        mScreensNavigator.goToLoginScreen();

    }

    /**
     * Logging out from Facebook
     */
    public void loggingOutFacebook() {
        Log.i(TAG, "loggingOutFacebook() inside method");

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        graphRequest =
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        mContext.getResources().getString(R.string.graph_path),
                        null,
                        HttpMethod.DELETE,
                        this);

        graphRequest.executeAsync();

    }

    /**
     * Callback method to log out the Facebook session and go to the initial Facebook login
     *
     * @param response
     */
    @Override
    public void onCompleted(GraphResponse response) {
        Log.i(TAG, "onCompleted() inside method");

        LoginManager.getInstance().logOut();

        mSession.logoutUserFacebook();

        mScreensNavigator.goToFacebookLogin();

    }

    /**
     * Logging out from Google
     */
    public void loggingOutGoogle() {
        Log.i(TAG, "loggingOutGoogle() inside method");

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(this);
    }

    /**
     * Result callback method to log out the Google session and go to the initial Google login
     *
     * @param status
     */
    @Override
    public void onResult(@NonNull Status status) {

        mSession.logoutUserGoogle();

        mScreensNavigator.goToGoogleLogin();

    }

}
