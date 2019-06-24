package com.life.equipmentlife.paid.view.activities.login.loginview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.equipment.list.EquipmentListActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.life.equipmentlife.common.constants.Constants.EMAIL_PATTERN;
import static com.life.equipmentlife.model.session.SessionManager.HAS_LOGGED_IN_FIRST_TIME_ALREADY;
import static com.life.equipmentlife.model.session.SessionManager.KEY_EMAIL;
import static com.life.equipmentlife.model.session.SessionManager.KEY_NAME;
import static com.life.equipmentlife.model.session.SessionManager.KEY_PASSWORD;
import static com.life.equipmentlife.paid.view.activities.login.LoginActivity.EMAIL_SAVED;
import static com.life.equipmentlife.paid.view.activities.login.LoginActivity.FLAG_CHANGED;

public class LoginViewImpl extends BaseObservableViewMvc implements LoginView, ProfileDataChangeUidKeyListener {
    private static final String TAG = LoginViewImpl.class.getSimpleName();

    private Button buttonSendEmailForgotPassword;

    private EditText editTextForgotPasswordEmail;

    private ProgressBar progressBarForgotPasswordDialog;

    private Button btnCancelForgotPassword;

    private Button btnShowForgotPasswordDialog;

    private FrameLayout btnEquipmentLogin;

    private EditText inputEmailEquipmentLogin;

    private EditText inputPasswordEquipmentLogin;

    private TextView signInButtonText;

    private ProgressBar progressBar;

    private View revealView;

    private OnFinishActivityListener mOnFinishActivityListener;

    // Session Manager Class
    private SessionManager mSession;

    private Profile profileOwner;

    private ProfileDao mProfileDao;

    private FirebaseAuth mAuth;

    private boolean isLandscapeMode;

    private LayoutInflater mInflater;

    private int mConfigChangeFlag;

    private String mEmail;

    private AlertDialog dialogPasswordReset;

    public LoginViewImpl(LayoutInflater inflater,
                         ViewGroup parent,
                         FirebaseAuth auth,
                         SessionManager session,
                         ProfileDao profileDao,
                         OnFinishActivityListener onFinishActivityListener) {

        mInflater = inflater;

        setRootView(inflater.inflate(R.layout.activity_login, parent, false));

        mProfileDao = profileDao;

        mAuth = auth;

        mOnFinishActivityListener = onFinishActivityListener;

        // Session Manager
        mSession = session;

        initComponents();

        // Boolean to verify whether is land mode or not
        isLandscapeMode = getContext().getResources().getBoolean(R.bool.isLand);

        //if SharedPreferences contains username and name or password then directly redirect to Home activity
        if((mSession.pref.contains(KEY_EMAIL)) && (mSession.pref.contains(KEY_PASSWORD) || mSession.pref.contains(KEY_NAME))){
            Log.i(TAG,"LoginViewImpl() inside constructor - inside ELSE: email - " + mSession.pref.contains(KEY_EMAIL)
            + " password: - " + mSession.pref.contains(KEY_PASSWORD));
            getContext().startActivity(new Intent(getContext(), EquipmentListActivity.class));

            mOnFinishActivityListener.onFinishActivity();

        }

        takeOutHintFromNotEmptyFields();

        btnEquipmentLogin.setOnClickListener(view -> loginUserProfile());
        btnShowForgotPasswordDialog.setOnClickListener(view -> showDialogResetPasswordProfile());

    }

    /**
     * Method that links the email and configChangeFlag parameter from activity to this class to be
     * used on the savedInstanceState
     *
     * @param configChangeFlag Flag for verification the orientation screen if is landscape or portrait
     * @param email The email to be kept on edittext when the orientation changes
     */
    @Override
    public void bindEmailReset(int configChangeFlag, String email) {
        Log.i(TAG, "bindLayoutInflater() inside method - configChangeFlag: " + configChangeFlag);
        Log.i(TAG, "bindLayoutInflater() inside method - email: " + email);

        mConfigChangeFlag = configChangeFlag;
        mEmail = email;
    }

    /**
     * Method to show the dialog when the orientation changes from activity
     *
     * @param configChangeFlag Flag for verification the orientation screen if is landscape or portrait
     * @param email The email to be kept on edittext when the orientation changes
     */
    @Override
    public void showDialogForgotPassword(int configChangeFlag, String email) {
        Log.i(TAG, "showDialogForgotPassword() inside method - configChangeFlag: " + configChangeFlag);
        Log.i(TAG, "showDialogForgotPassword() inside method - email: " + email);
        mConfigChangeFlag = configChangeFlag;
        mEmail = email;
        showDialogResetPasswordProfile();
    }

    /**
     * Method to save state when orientation changes from activity
     *
     * @param bundle
     */
    @Override
    public void saveInstanceState(Bundle bundle) {
        Log.i(TAG, "saveInstanceState() inside method - mEmail: " + mEmail);

        if (editTextForgotPasswordEmail != null) {

            mEmail = editTextForgotPasswordEmail.getText().toString();

            Log.i(TAG, "saveInstanceState() inside method - mEmail: " + mEmail);

            mConfigChangeFlag = 1;

            bundle.putInt(FLAG_CHANGED, mConfigChangeFlag);
            bundle.putString(EMAIL_SAVED, mEmail);

        }

    }

    /**
     * Method that dismiss the alert dialog
     */
    @Override
    public void pause() {

        if (dialogPasswordReset != null) {
            dialogPasswordReset.dismiss();
            dialogPasswordReset = null;
        }

    }

    /**
     * Method closes the dialog when cancel button is pressed
     */
    private void onCloseDialog() {

        cleanFields();

        dialogPasswordReset.dismiss();
        dialogPasswordReset = null;

    }

    /**
     * Method that shows the password dialog when the 'Forgot your password' button is pressed
     */
    private void showDialogResetPasswordProfile() {
        Log.i(TAG, "showDialogResetPasswordProfile() inside method");

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View view = mInflater.inflate(R.layout.forgot_password_dialog, null);

        buttonSendEmailForgotPassword = view.findViewById(R.id.button_send_email_forgot_password);
        editTextForgotPasswordEmail = view.findViewById(R.id.editText_forgot_password_email);
        progressBarForgotPasswordDialog = view.findViewById(R.id.progressBar_forgot_password_dialog);
        btnCancelForgotPassword = view.findViewById(R.id.btn_close_forgot_password_dialog);

        if (mConfigChangeFlag == 1) {

            mConfigChangeFlag = 0;
            editTextForgotPasswordEmail.setText(mEmail);

        }

        btnCancelForgotPassword.setOnClickListener(v -> onCloseDialog());
        buttonSendEmailForgotPassword.setOnClickListener(v -> resetPasswordProfile());

        mBuilder.setView(view);
        dialogPasswordReset = mBuilder.create();
        dialogPasswordReset.setCanceledOnTouchOutside(false);
        dialogPasswordReset.show();

    }

    /**
     * Method initiates the screen components
     */
    private void initComponents() {

        btnShowForgotPasswordDialog = findViewById(R.id.button_forgot_password);

        btnEquipmentLogin = findViewById(R.id.btn_equipment_login);

        inputEmailEquipmentLogin = findViewById(R.id.input_email_equipment_login);

        inputPasswordEquipmentLogin = findViewById(R.id.input_password_equipment_login);

        signInButtonText = findViewById(R.id.signInButtonText);

        progressBar = findViewById(R.id.progressBar);

        revealView = findViewById(R.id.revealView);

    }

    /**
     * Method to clean the fields filled in forgot password dialog
     */
    private void cleanFields() {

        mEmail = null;

        editTextForgotPasswordEmail = null;

    }

    /**
     * Method that executes the password reset on FireBase
     */
    private void resetPasswordProfile() {

        String email = editTextForgotPasswordEmail.getText().toString().trim();

        if (!validateResetEmailToSend()) {
            return;
        }

        progressBarForgotPasswordDialog.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(getContext(),
                                    getContext().getResources().getString(R.string.reset_password_email_sent_successfully),
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(getContext(),
                                    getContext().getResources().getString(R.string.reset_password_email_sent_failure),
                                    Toast.LENGTH_SHORT).show();

                        }

                        progressBarForgotPasswordDialog.setVisibility(View.GONE);

                    }
                });

    }

    /**
     * Method that makes the login
     */
    private void loginUserProfile() {

        if (!validate()) {
            return;
        }

        animateButtonWidth();
        fadeOutTextAndSetProgressDialog();

        String email = inputEmailEquipmentLogin.getText().toString().trim();
        String password = inputPasswordEquipmentLogin.getText().toString();

        mSession.editor.putBoolean(HAS_LOGGED_IN_FIRST_TIME_ALREADY, true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                    @Override
                    public void onSuccess(AuthResult authResult) {

                        getProfile(authResult.getUser().getUid());

                    }

                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {

                        nextActionError();

                    }

                });

    }

    /**
     * Gets the profile from database by uid
     *
     * @param uid
     */
    private void getProfile(final String uid) {
        mProfileDao.fetchProfileById(uid, this);
    }

    /**
     * Method that returns the profile object fetched from database
     *
     * @param profile
     */
    @Override
    public void onDataUidKeyLoaded(Profile profile) {

        profileOwner = profile;

        setProfile(profile);

    }

    /**
     * Sets the profile data on session
     *
     * @param profile
     */
    private void setProfile(Profile profile) {

        mSession.setProfileOnPrefs(profile);

        nextAction();

    }

    /**
     * Validates the credentials from screen if they're valid or empty
     *
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(EMAIL_PATTERN);

        String email = inputEmailEquipmentLogin.getText().toString().trim();
        String password = inputPasswordEquipmentLogin.getText().toString();

        Matcher match = emailPtrn.matcher(email);

        if (email.isEmpty()) {

            inputEmailEquipmentLogin.setError(getContext().getResources().getString(R.string.login_profile_validator_email_required_error));
            valid = false;

        } else if (!match.matches()) {

            inputEmailEquipmentLogin.setError(getContext().getResources().getString(R.string.login_profile_validator_email_not_match));
            valid = false;

        } else if (password.isEmpty()) {
            inputPasswordEquipmentLogin.setError(getContext().getResources().getString(R.string.login_profile_validator_password_required_error));
            valid = false;

        } else if (password.length() < 4 || password.length() > 10) {
            inputPasswordEquipmentLogin.setError(getContext().getResources().getString(R.string.login_profile_validator_password_limit_error));
            valid = false;

        } else {
            inputPasswordEquipmentLogin.setError(null);
        }

        return valid;
    }

    /**
     * Validates the email field on reset dialog if it's empty or valid
     *
     * @return
     */
    private boolean validateResetEmailToSend() {

        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(EMAIL_PATTERN);

        String email = editTextForgotPasswordEmail.getText().toString().trim();

        Matcher match = emailPtrn.matcher(email);

        if (email.isEmpty()) {

            editTextForgotPasswordEmail.setError(getContext().getResources().getString(R.string.login_profile_validator_email_required_error));
            valid = false;

        } else if (!match.matches()) {

            editTextForgotPasswordEmail.setError(getContext().getResources().getString(R.string.login_profile_validator_email_not_match));
            valid = false;

        } else {
            editTextForgotPasswordEmail.setError(null);
        }

        return valid;

    }

    // Animation button to login
    private void animateButtonWidth() {

        ValueAnimator anim = ValueAnimator.ofInt(btnEquipmentLogin.getMeasuredWidth(), getFinalWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnEquipmentLogin.getLayoutParams();
                layoutParams.width = value;
                btnEquipmentLogin.requestLayout();
            }
        });

        anim.setDuration(350);
        anim.start();

    }

    // Method to get the button size
    private int getFinalWidth() {
        return (int) getContext().getResources().getDimension(R.dimen.get_width);
    }

    private void fadeOutTextAndSetProgressDialog() {
        signInButtonText.animate().alpha(0f).setDuration(550).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showProgressDialog();
            }
        }).start();
    }

    // Show the progress button when logging in
    private void showProgressDialog() {
        progressBar.getIndeterminateDrawable().setColorFilter(getContext().getResources().getColor(R.color.transparent), PorterDuff.Mode.SRC_IN);
        progressBar.setVisibility(View.VISIBLE);
    }

    // Method tha activates the animation of showing the reveal button when the authentication
    // is success
    private void nextAction() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                revealButton();
                fadeOutProgressDialog();
                delayedStartNextActivity();

            }
        }, 4000);

    }

    // Animation to open the screen from the button
    private void revealButton() {
        btnEquipmentLogin.setElevation(0f);
        revealView.setVisibility(View.VISIBLE);

        int x = revealView.getWidth();
        int y = revealView.getHeight();

        int startX = (int) (getFinalWidth() / 2 + btnEquipmentLogin.getX());
        int startY = (int) (getFinalWidth() / 2 + btnEquipmentLogin.getY());

        float radius = Math.max(x, y) * 1.2f;

        Animator reveal = ViewAnimationUtils.createCircularReveal(revealView, startX,startY, getFinalWidth(), radius);

        reveal.setDuration(2000);
        reveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        reveal.start();
    }

    // Method to fade out the progress bar from button
    private void fadeOutProgressDialog() {
        progressBar.animate().alpha(0f).setDuration(2000).start();
    }

    // Method to access the other activity from the animation after some milliseconds
    private void delayedStartNextActivity() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Context context = getContext();
                Class destinationClass = EquipmentListActivity.class;

                Intent intentToStartListActivity = new Intent(context, destinationClass);
                intentToStartListActivity.putExtra(Intent.EXTRA_TEXT, profileOwner);
                getContext().startActivity(intentToStartListActivity);
                mOnFinishActivityListener.onFinishActivity();

            }

        }, 100);

    }

    // Error message button when incorrect login - Initial
    private void nextActionError() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                revealButtonError();

            }
        }, 4000);

    }

    // Method tha activates the animation of showing error message if the login is not success
    private void revealButtonError() {

        fadeOutProgressDialogAndSetText();

    }

    // Method to fade out progress bar from button and set the error message if login is not success
    private void fadeOutProgressDialogAndSetText() {
        signInButtonText.animate().alpha(1f).setDuration(650).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                takeOutProgressDialog();
            }
        });
    }

    // Method to make progress bar disappear
    private void takeOutProgressDialog() {

        progressBar.setVisibility(View.GONE);
        growErrorButton();
        delayedStartNormalButton();

    }

    // Method to grow the button and show the error message if an error occurs when logging in
    // fails
    private void growErrorButton() {

        ValueAnimator anim = ValueAnimator.ofInt(btnEquipmentLogin.getMeasuredWidth(), getErrorWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnEquipmentLogin.getLayoutParams();
                layoutParams.width = value;
                btnEquipmentLogin.requestLayout();
                btnEquipmentLogin.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_sign_in_error));
                signInButtonText.setText(getContext().getResources().getString(R.string.login_alert_dialog_error_login_message));
                signInButtonText.setTextColor(ContextCompat.getColor(getContext(), R.color.white_text_color));
            }
        });

        anim.setDuration(700);
        anim.start();
    }

    // Method to delay the growth of the button to the state of normal before the error message was shown
    private void delayedStartNormalButton() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getNormalButton();

            }
        }, 3000);
    }

    // Method that set the button to the initial form
    private void getNormalButton() {

        ValueAnimator anim = ValueAnimator.ofInt(btnEquipmentLogin.getMeasuredWidth(), getInitialWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnEquipmentLogin.getLayoutParams();
                layoutParams.width = value;
                btnEquipmentLogin.requestLayout();
                btnEquipmentLogin.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_sign_in));

                signInButtonText.setText(getContext().getResources().getString(R.string.text_login));
                signInButtonText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            }
        });

        anim.setDuration(450);
        anim.start();

    }

    // get the size of the message error button
    private int getErrorWidth() {

        int widthMode = 0;

        if (isLandscapeMode) {
            widthMode = (int) getContext().getResources().getDimension(R.dimen.error_width);
        } else {
            widthMode = (int) getContext().getResources().getDimension(R.dimen.error_width);
        }

        return widthMode;
    }

    private int getInitialWidth() {

        int widthMode = 0;

        if (isLandscapeMode) {
            widthMode = (int) getContext().getResources().getDimension(R.dimen.initial_width);
        } else {
            widthMode = (int) getContext().getResources().getDimension(R.dimen.initial_width);
        }

        return widthMode;
    }
    // Error message button when incorrect login - Final

    // Focused on the fields and take out the hint when not empty
    private void takeOutHintFromNotEmptyFields() {

        /** Profile email **/
        if (inputEmailEquipmentLogin.getText().toString().isEmpty()) {

            inputEmailEquipmentLogin.setHint(getContext().getResources().getString(R.string.hint_login_email));

        } else {

            inputEmailEquipmentLogin.setHint("");

        }

        // Using this listener so that the brand emptied adds hint
        inputEmailEquipmentLogin.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (inputEmailEquipmentLogin.getText().toString().isEmpty()) {

                    inputEmailEquipmentLogin.setHint(getContext().getResources().getString(R.string.hint_login_email));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile password **/
        if (inputPasswordEquipmentLogin.getText().toString().isEmpty()) {

            inputPasswordEquipmentLogin.setHint(getContext().getResources().getString(R.string.hint_login_password));

        } else {

            inputPasswordEquipmentLogin.setHint("");

        }

        // Using this listener so that the model emptied adds hint
        inputPasswordEquipmentLogin.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (inputPasswordEquipmentLogin.getText().toString().isEmpty()) {

                    inputPasswordEquipmentLogin.setHint(getContext().getResources().getString(R.string.hint_login_password));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
