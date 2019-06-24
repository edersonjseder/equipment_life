package com.life.equipmentlife.paid.view.activities.start.startview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.OnCreateUserListener;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.life.equipmentlife.paid.view.activities.start.StartScreenActivity.CONFIG_FLAG_CHANGED;
import static com.life.equipmentlife.paid.view.activities.start.StartScreenActivity.CONFIRM_PASSWORD_SAVED;
import static com.life.equipmentlife.paid.view.activities.start.StartScreenActivity.EMAIL_SAVED;
import static com.life.equipmentlife.paid.view.activities.start.StartScreenActivity.PASSWORD_SAVED;

public class StartScreenViewImpl extends BaseObservableViewMvc implements StartScreenView, View.OnClickListener,
        OnCompleteListener<AuthResult> {
    private static final String TAG = StartScreenViewImpl.class.getSimpleName();

    @BindView(R.id.btn_initial_sign_in)
    protected FrameLayout btnInitialSignIn;

    @BindView(R.id.btn_initial_sign_up)
    protected FrameLayout btnInitialSignUp;

    @BindView(R.id.btn_sign_up_google)
    protected ImageButton btnSignUpGoogle;

    @BindView(R.id.btn_sign_up_facebook)
    protected ImageButton btnSignUpFacebook;

    protected EditText editTextProfileEmail;

    protected EditText editTextProfilePassword;

    protected EditText editTextProfileConfirmPassword;

    protected ProgressBar progressBarLoginProfileFireBase;

    protected Button buttonSaveSignUpCredentials;

    protected Button buttonCancelSignUpCredentials;

    private FragmentActivity mActivity;

    private ScreensNavigator mScreensNavigator;

    private AlertDialog dialogCredentialRegistration;

    private LayoutInflater mInflater;

    private FirebaseAuth mAuth;

    private String mEmail, mPassword, mConfirmPassword;

    private int mConfigChangeFlag = 1;

    public StartScreenViewImpl(LayoutInflater inflater,
                               ViewGroup parent,
                               ScreensNavigator screensNavigator,
                               FragmentActivity activity) {

        mInflater = inflater;

        setRootView(inflater.inflate(R.layout.activity_start_screen, parent, false));

        ButterKnife.bind(this, getRootView());

        mActivity = activity;

        mScreensNavigator = screensNavigator;

        btnInitialSignIn.setOnClickListener(this);
        btnInitialSignUp.setOnClickListener(this);
        btnSignUpGoogle.setOnClickListener(this);
        btnSignUpFacebook.setOnClickListener(this);

    }

    /**
     * This method receives credentials data from activity
     *
     * @param configChangeFlag
     * @param email
     * @param password
     * @param confirmPassword
     * @param auth
     */
    @Override
    public void bindCredentialsData(int configChangeFlag, String email,
                                    String password, String confirmPassword, FirebaseAuth auth) {

        mConfigChangeFlag = configChangeFlag;
        mAuth = auth;
        mEmail = email;
        mPassword = password;
        mConfirmPassword = confirmPassword;

    }

    /**
     * This method shows the credential registration on the screen from activity
     *
     * @param configChangeFlag
     * @param email
     * @param password
     * @param confirmPassword
     */
    @Override
    public void showDialogCredentialsRegistration(int configChangeFlag, String email,
                                                  String password, String confirmPassword) {
        mConfigChangeFlag = configChangeFlag;
        mEmail = email;
        mPassword = password;
        mConfirmPassword = confirmPassword;

        showDialogRegistrationCredentials();

    }

    @Override
    public void saveInstanceState(Bundle bundle) {

        if (editTextProfileEmail != null &&
                editTextProfilePassword != null &&
                editTextProfileConfirmPassword != null) {

            mEmail = editTextProfileEmail.getText().toString();
            mPassword = editTextProfilePassword.getText().toString();
            mConfirmPassword = editTextProfileConfirmPassword.getText().toString();

            mConfigChangeFlag = 1;

            bundle.putInt(CONFIG_FLAG_CHANGED, mConfigChangeFlag);
            bundle.putString(EMAIL_SAVED, mEmail);
            bundle.putString(PASSWORD_SAVED, mPassword);
            bundle.putString(CONFIRM_PASSWORD_SAVED, mConfirmPassword);

        }

    }

    /**
     * Method shows the credentials registration dialog
     */
    private void showDialogRegistrationCredentials() {
        Log.i(TAG, "showDialogRegistrationCredentials() inside method");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View view = mInflater.inflate(R.layout.sign_up_dialog, null);

        editTextProfileEmail = view.findViewById(R.id.editText_profile_email);
        editTextProfilePassword = view.findViewById(R.id.editText_profile_password);
        editTextProfileConfirmPassword = view.findViewById(R.id.editText_profile_confirm_password);
        buttonCancelSignUpCredentials = view.findViewById(R.id.button_cancel_sign_up_credentials);
        buttonSaveSignUpCredentials = view.findViewById(R.id.button_save_sign_up_credentials);
        progressBarLoginProfileFireBase = view.findViewById(R.id.progressBar_profile_login_firebase);

        if (mConfigChangeFlag == 1) {

            mConfigChangeFlag = 0;
            editTextProfileEmail.setText(mEmail);
            editTextProfilePassword.setText(mPassword);
            editTextProfileConfirmPassword.setText(mConfirmPassword);

        }

        buttonCancelSignUpCredentials.setOnClickListener(v -> onCloseDialog());
        buttonSaveSignUpCredentials.setOnClickListener(v -> onSaveCredentials());

        mBuilder.setView(view);
        dialogCredentialRegistration = mBuilder.create();
        dialogCredentialRegistration.setCanceledOnTouchOutside(false);
        dialogCredentialRegistration.show();

    }

    /**
     * Closes the dialog
     */
    private void onCloseDialog() {

        cleanFields();

        dialogCredentialRegistration.dismiss();
        dialogCredentialRegistration = null;

    }

    /**
     * Method to clean the fields filled in forgot password dialog
     */
    private void cleanFields() {

        mEmail = null;

        editTextProfileEmail = null;
        editTextProfilePassword = null;
        editTextProfileConfirmPassword = null;

    }

    /**
     * Method that takes to the Login screen
     */
    private void goToLoginScreen() {

        mScreensNavigator.goToLoginScreen();

    }

    public void onSaveCredentials() {

        if (!validate()) {
            return;
        }

        createProfileCredentials();

    }

    /**
     * Creates user in Firebase through registration of credentials and then it will be taken
     * to Profile registration to be completed with personal information
     */
    private void createProfileCredentials() {

        Profile profile = new Profile();

        profile.setEmail(editTextProfileEmail.getText().toString());
        profile.setPassword(editTextProfilePassword.getText().toString());

        createUserWithEmailAndPassword(profile);

    }

    /**
     * Method creates user credentials on Firebase authentication module
     *
     * @param profile
     */
    public void createUserWithEmailAndPassword(Profile profile) {
        Log.i(TAG, "createUserWithEmailAndPassword() inside method");

        mEmail = profile.getEmail();

        mPassword = profile.getPassword();

        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(mActivity, this);

    }

    /**
     * Method handles if the user credentials were created successfully or if threw an error
     *
     * @param task
     */
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d(TAG, "onComplete() inside method");

        try {

            //check if successful
            if (!task.isSuccessful()) {

                onCompleteFirebaseAuthFailed(getContext().getResources().getString(R.string.signup_profile_error) + task.getException());

            } else {

                Profile profile = new Profile();

                profile.setEmail(mEmail);
                profile.setPassword(mPassword);

                onCompleteFirebaseAuth(task.getResult().getUser().getUid(), profile);

            }

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * When the profile data is created this method goes to
     * the profile registration
     *
     * @param uid
     * @param profile
     */
    public void onCompleteFirebaseAuth(String uid, Profile profile) {

        mScreensNavigator.goToProfileRegistration(uid, profile);

        progressBarLoginProfileFireBase.setVisibility(View.GONE);

    }

    /**
     * In case of any fail this method shows an error message to the user
     *
     * @param errorMessage
     */
    public void onCompleteFirebaseAuthFailed(String errorMessage) {

        Toast.makeText(getContext(),
                errorMessage,
                Toast.LENGTH_LONG).show();

        progressBarLoginProfileFireBase.setVisibility(View.GONE);

    }

    /**
     * Method that takes the user to log in with Facebook API
     */
    private void goToSignUpFacebook() {

        mScreensNavigator.goToFacebookLogin();

    }

    /**
     * Method that takes the user to log in with Google API
     */
    private void goToSignUpGoogle() {

        mScreensNavigator.goToGoogleLogin();

    }

    @Override
    public void pause() {

        if (dialogCredentialRegistration != null) {
            dialogCredentialRegistration.dismiss();
            dialogCredentialRegistration = null;
        }

    }

    /**
     * This method executes the actions according
     * to the button click the user choose
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_initial_sign_in:
                goToLoginScreen();
                break;

            case R.id.btn_initial_sign_up:
                showDialogRegistrationCredentials();
                break;

            case R.id.btn_sign_up_google:
                goToSignUpGoogle();
                break;

            case R.id.btn_sign_up_facebook:
                goToSignUpFacebook();
                break;

        }

    }

    /**
     * Validate the fields filled by user on the screen
     *
     * @return the boolean value to check if attends the request
     */
    public boolean validate() {
        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        String email = editTextProfileEmail.getText().toString().trim();
        String password = editTextProfilePassword.getText().toString();
        String confirmPassword = editTextProfileConfirmPassword.getText().toString();

        Matcher match = emailPtrn.matcher(email);

        if (email.isEmpty()) {

            editTextProfileEmail.setError(getContext().getResources().getString(R.string.login_profile_validator_email_required_error));
            valid = false;

        } else if (!match.matches()) {

            editTextProfileEmail.setError(getContext().getResources().getString(R.string.login_profile_validator_email_not_match));
            valid = false;

        } else if (password.isEmpty()) {

            editTextProfilePassword.setError(getContext().getResources().getString(R.string.login_profile_validator_password_required_error));
            valid = false;

        } else if (password.length() < 4 || password.length() > 10) {

            editTextProfilePassword.setError(getContext().getResources().getString(R.string.login_profile_validator_password_limit_error));
            valid = false;

        } else if (!password.equals(confirmPassword)) {

            editTextProfileConfirmPassword.setError(getContext().getResources().getString(R.string.login_profile_validator_password_not_match));
            valid = false;

        } else {

            editTextProfileEmail.setError(null);

        }

        return valid;

    }

}