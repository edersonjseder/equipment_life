package com.life.equipmentlife.paid.view.activities.equipment.edit.editview;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.OnOpenSettingsListener;
import com.life.equipmentlife.common.listener.PickerOptionListener;
import com.life.equipmentlife.common.utils.UploadImageFireBase;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.executors.ThreadExecutorUpdateEquipment;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeEmailListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.common.utils.Utils;
import com.life.equipmentlife.paid.view.activities.imagepicker.ImagePickerActivity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.life.equipmentlife.common.constants.Constants.EMAIL_PATTERN;
import static com.life.equipmentlife.model.session.SessionManager.UID_KEY;
import static com.life.equipmentlife.paid.view.activities.equipment.details.detailsview.EquipmentDetailsViewImpl.HAS_EDITED_EQUIPMENT;

public class EquipmentDetailsEditViewImpl extends BaseObservableViewMvc implements EquipmentDetailsEditView,
        ProfileDataChangeEmailListener, ProfileDataChangeUidKeyListener, MultiplePermissionsListener {

    private static final String TAG = EquipmentDetailsEditViewImpl.class.getSimpleName();

    @BindView(R.id.et_equipment_edit_brand)
    protected EditText editTextEquipmentEditBrand;

    @BindView(R.id.et_equipment_edit_model)
    protected EditText editTextEquipmentEditModel;

    @BindView(R.id.et_equipment_edit_serial_number)
    protected EditText editTextEquipmentEditSerialNumber;

    @BindView(R.id.tv_equipment_edit_registration_date)
    protected TextView textViewEquipmentEditRegistrationDate;

    @BindView(R.id.tv_equipment_edit_owner)
    protected TextView textViewEquipmentEditOwner;

    @BindView(R.id.radioGroupEquipmentEditStatus)
    protected RadioGroup checkBoxEquipmentEditStatus;

    @BindView(R.id.et_equipment_edit_short_description)
    protected EditText editTextEquipmentEditShortDescription;

    @BindView(R.id.image_btn_equipment_details_edit_save)
    protected ImageButton imageButtonEquipmentDetailsEditSave;

    @BindView(R.id.progressBar_equipment_edit_upload_picture)
    protected ProgressBar progressBarEquipmentEditPictureUpload;

    @Nullable
    @BindView(R.id.imageView_equipment_edit_picture)
    protected ImageView imageViewEquipmentEditPicture;

    @BindView(R.id.image_camera_picture_upload)
    protected ImageView imageCameraPictureUpload;

    @BindView(R.id.ll_profile_email_sold_to)
    protected LinearLayout linearLayoutProfileCpfSoldTo;

    @BindView(R.id.ll_profile_name_sold_to)
    protected LinearLayout linearLayoutProfileNameSoldTo;

    @BindView(R.id.et_equipment_edit_owner_email)
    protected EditText editTextEquipmentEditOwnerEmail;

    @BindView(R.id.tv_equipment_edit_sold_to_owner_name)
    protected TextView textViewEquipmentEditSoldToOwnerName;

    @BindView(R.id.progressBar_equipment_edit_sold_to)
    protected ProgressBar progressBarEquipmentEditSoldTo;

    @BindView(R.id.btn_equipment_profile_email_search)
    protected FrameLayout btnEquipmentProfileEmailSearch;

    @BindView(R.id.btnEmailSearchButtonImage)
    protected ImageView btnEmailSearchButtonImage;

    @BindView(R.id.emailSearchText)
    protected TextView emailSearchText;

    @Nullable
    @BindView(R.id.imageView_equipment_edit_picture_land)
    protected ImageView imageViewEquipmentEditPictureToUpload;

    @BindView(R.id.ll_overbox_equipment_details_edit_loading)
    protected LinearLayout llOverboxEquipmentDetailsEditLoading;

    @BindView(R.id.pb_equipment_detail_edit_loading)
    protected ProgressBar pbEquipmentDetailsEditLoading;

    private Equipment mEquipment;

    // Constants for priority
    public static final String EQUIPMENT_OWNED = "Owned";
    public static final String EQUIPMENT_SOLD = "Sold";
    public static final String EQUIPMENT_STONED = "Stolen";

    //Uri to store the image uri
    private Uri filePath;

    //Firebase
    private FirebaseStorage storage;

    private StorageReference storageReference;

    private ContentResolver mContentResolver;

    private String urlEquipmentPicture;

    private Profile mProfile;

    private boolean isLandscapeMode;

    private String uid;

    // To put a flag for details edition
    private SessionManager mSession;

    private String statusSold;

    private EquipmentDao implUpdate;

    private ProfileDao implProfileDao;

    private FragmentActivity mActivity;

    private UploadImageFireBase imageFireBase;

    private PickerOptionListener mOptionListener;

    private OnOpenSettingsListener mSettingsListener;

    private ThreadExecutorUpdateEquipment executorUpdate;

    private OnFinishActivityListener mOnFinishActivityListener;

    public EquipmentDetailsEditViewImpl(LayoutInflater inflater, ViewGroup parent,
                                        SessionManager session, ProfileDao profileDao,
                                        EquipmentDao equipmentDao,
                                        ContentResolver contentResolver,
                                        FragmentActivity activity,
                                        PickerOptionListener listener, OnOpenSettingsListener settingsListener,
                                        OnFinishActivityListener onFinishActivityListener) {
        Log.i(TAG,"EquipmentDetailsEditViewImpl() inside constructor");

        setRootView(inflater.inflate(R.layout.activity_equipment_details_edit, parent, false));

        ButterKnife.bind(this, getRootView());

        mSession = session;

        mOnFinishActivityListener = onFinishActivityListener;

        mOptionListener = listener;

        mSettingsListener = settingsListener;

        implUpdate = equipmentDao;

        implProfileDao = profileDao;

        mActivity = activity;

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mContentResolver = contentResolver;

        mSession.setEditedEquipmentInfo(false);

        uid = session.getUidKey();

        // Boolean to verify whether is tablet mode or not
        isLandscapeMode = getContext().getResources().getBoolean(R.bool.isLand);

        verifyEquipmentStatus();

        takeOutHintFromNotEmptyFields();

    }

    /**
     * Method that verifies what equipment status will be chosen if user clicks on radio button
     * so that if the status chosen was 'sold' the email edit text will be shown so the user can
     * search and find who he'll sell his equipment to
     */
    private void verifyEquipmentStatus() {
        Log.i(TAG,"verifyEquipmentStatus() inside method");

        checkBoxEquipmentEditStatus.setOnCheckedChangeListener((group, checkedId) -> {
            Log.i(TAG,"onCheckedChanged() inside method");

            switch (checkedId) {

                case R.id.rb_equipment_edit_status_owned:
                    Log.i(TAG,"inside case: rb_equipment_edit_status_owned");
                    linearLayoutProfileCpfSoldTo.setVisibility(View.GONE);
                    linearLayoutProfileNameSoldTo.setVisibility(View.GONE);
                    statusSold = getContext().getResources().getString(R.string.equipment_owned);
                    cleanOwnerEmailEquipmentDetailsEdit();
                    break;

                case R.id.rb_equipment_edit_status_sold:
                    Log.i(TAG,"inside case: rb_equipment_edit_status_sold");
                    linearLayoutProfileCpfSoldTo.setVisibility(View.VISIBLE);
                    statusSold = getContext().getResources().getString(R.string.equipment_sold);
                    break;

                case R.id.rb_equipment_edit_status_stolen:
                    Log.i(TAG,"inside case: rb_equipment_edit_status_stolen");
                    linearLayoutProfileCpfSoldTo.setVisibility(View.GONE);
                    linearLayoutProfileNameSoldTo.setVisibility(View.GONE);
                    statusSold = getContext().getResources().getString(R.string.equipment_stolen);
                    cleanOwnerEmailEquipmentDetailsEdit();
                    break;

            }

        });

    }

    /**
     * Method that searches profile object by email parameter
     */
    @OnClick(R.id.btn_equipment_profile_email_search)
    protected void searchProfileByEmail() {

        if (!validateEmail()) {
            return;
        }

        animateButtonWidth();
        fadeOutTextAndSetProgressDialog();

        String email = editTextEquipmentEditOwnerEmail.getText().toString().trim();

        implProfileDao.fetchProfileByEmail(email, this);

    }

    /**
     * Method used by ProfileDao to provide the profile object search result for this class
     * using email as parameter
     *
     * @param profile
     */
    @Override
    public void onDataEmailLoaded(Profile profile) {

        if (profile != null) {

            mProfile = profile;

            textViewEquipmentEditSoldToOwnerName.setText(profile.getName());

            fadeOutProgressBar();

            linearLayoutProfileNameSoldTo.setVisibility(View.VISIBLE);

        } else {

            nextAction();

        }

    }

    /**
     * Method that binds equipment object from Activity to this class to be used to be shown
     * to the user through the screen components and then he can edit them.
     *
     * @param equipment
     */
    @Override
    public void bindEquipment(Equipment equipment) {

        showProgressIndication();

        mEquipment = equipment;

        setDetailsFields(mEquipment);

        imageButtonEquipmentDetailsEditSave.setOnClickListener(view -> onSaveEquipmentDetailsButtonClicked(mEquipment));

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveEquipmentDetailsButtonClicked(Equipment equipment) {
        Log.i(TAG,"onSaveEquipmentDetailsButtonClicked() inside method");

        populateEditedEntity(equipment);

        mSession.setEditedEquipmentInfo(true);

        // If user wants to save equipment data with picture upload
        // then enter in this IF
        if (filePath != null) {
            Log.i(TAG,"onSaveEquipmentDetailsButtonClicked() inside IF");

            imageFireBase = new UploadImageFireBase(filePath, uid, getContext(), storageReference,
                    mContentResolver, progressBarEquipmentEditPictureUpload, implUpdate, equipment, true);

            imageFireBase.uploadImageAndSaveData(1);

            mOnFinishActivityListener.onFinishActivity();

        } else {
            Log.i(TAG,"onSaveEquipmentDetailsButtonClicked() inside ELSE");

            executorUpdate = new ThreadExecutorUpdateEquipment(implUpdate, mEquipment);

            executorUpdate.updateDataThread();

            mOnFinishActivityListener.onFinishActivity();

        }

    }

    /**
     * Method that sets the Equipment information details in the screen components to be shown
     * to the user to be edited
     *
     * @param equipment
     */
    private void setDetailsFields(Equipment equipment) {

        hideProgressIndication();

        if (equipment.getStatusCurrentOwner().equals(getContext().getResources().getString(R.string.equipment_sold))) {

            String uid = equipment.getProfileCurrentOwnerId();

            implProfileDao.fetchProfileById(uid, this);

        }

        setEquipmentStatusInViews(equipment.getStatusCurrentOwner());
        editTextEquipmentEditBrand.setText(equipment.getBrand());
        editTextEquipmentEditModel.setText(equipment.getModel());
        editTextEquipmentEditSerialNumber.setText(equipment.getSerialNumber());
        textViewEquipmentEditRegistrationDate.setText(equipment.getRegistrationDate());
        textViewEquipmentEditOwner.setText(equipment.getCurrentOwner());
        editTextEquipmentEditShortDescription.setText(equipment.getShortDescription());

        if (isLandscapeMode) {

            if ((!equipment.getPicture().isEmpty()) || (equipment.getPicture() != "")){

                Uri imageUrl = Utils.buildImageUrl(equipment.getPicture());

                Glide.with(getContext().getApplicationContext())
                        .load(imageUrl)
                        .into(imageViewEquipmentEditPictureToUpload);

            }

        } else {

            if ((!equipment.getPicture().isEmpty()) || (equipment.getPicture() != "")){

                Uri imageUrl = Utils.buildImageUrl(equipment.getPicture());

                Glide.with(getContext().getApplicationContext())
                        .load(imageUrl)
                        .into(imageViewEquipmentEditPicture);

            }

        }

    }

    /**
     * Method used by ProfileDao to provide the profile object search result for this class
     * using uid key as parameter
     *
     * @param profile
     */
    @Override
    public void onDataUidKeyLoaded(Profile profile) {
        Log.i(TAG,"onDataUidKeyLoaded() inside method - profile: " + profile);

        hideProgressIndication();

        mProfile = profile;

        if (mProfile != null) {

            textViewEquipmentEditSoldToOwnerName.setText(profile.getName());
            linearLayoutProfileNameSoldTo.setVisibility(View.VISIBLE);

        } else {

            textViewEquipmentEditSoldToOwnerName.setText("");
            linearLayoutProfileNameSoldTo.setVisibility(View.GONE);

        }

    }

    /**
     * Method that populates the Equipment object with information from
     * screen components filled by user
     *
     * @param equipment
     */
    private void populateEditedEntity(Equipment equipment) {

        if (statusSold != null) {

            if (statusSold.equals(getContext().getResources().getString(R.string.equipment_sold))) {

                equipment.setProfileCurrentOwnerId(mProfile.getId());
                equipment.setCurrentOwner(mProfile.getName());
                equipment.setStatusCurrentOwner(getEquipmentStatusFromViews());
                equipment.setStatusPreviousOwner(getEquipmentStatusFromViews());

            } else if (statusSold.equals(getContext().getResources().getString(R.string.equipment_stolen))) {

                equipment.setProfileCurrentOwnerId("");
                equipment.setCurrentOwner("");
                equipment.setStatusCurrentOwner(getEquipmentStatusFromViews());

            } else {

                equipment.setCurrentOwner(textViewEquipmentEditOwner.getText().toString());
                equipment.setStatusCurrentOwner(getEquipmentStatusFromViews());

            }

        }

        equipment.setBrand(editTextEquipmentEditBrand.getText().toString());
        equipment.setModel(editTextEquipmentEditModel.getText().toString());
        equipment.setSerialNumber(editTextEquipmentEditSerialNumber.getText().toString());
        equipment.setShortDescription(editTextEquipmentEditShortDescription.getText().toString());

        if (urlEquipmentPicture != null) {

            equipment.setPicture(urlEquipmentPicture);
        }

    }

    /**
     * getEquipmentStatusFromViews is called whenever the selected status needs to be retrieved
     */
    public String getEquipmentStatusFromViews() {

        String status = "";

        int checkedId = checkBoxEquipmentEditStatus.getCheckedRadioButtonId();

        switch (checkedId) {
            case R.id.rb_equipment_edit_status_owned:
                status = getContext().getResources().getString(R.string.equipment_owned);
                break;
            case R.id.rb_equipment_edit_status_sold:
                status = getContext().getResources().getString(R.string.equipment_sold);
                break;
            case R.id.rb_equipment_edit_status_stolen:
                status = getContext().getResources().getString(R.string.equipment_stolen);
        }

        return status;

    }

    /**
     * setEquipmentStatusInViews is called when we receive a task from MainActivity
     *
     * @param status the priority value
     */
    public void setEquipmentStatusInViews(String status) {

        switch (status) {
            case EQUIPMENT_OWNED:
                checkBoxEquipmentEditStatus.check(R.id.rb_equipment_edit_status_owned);
                break;
            case EQUIPMENT_SOLD:
                checkBoxEquipmentEditStatus.check(R.id.rb_equipment_edit_status_sold);
                break;
            case EQUIPMENT_STONED:
                checkBoxEquipmentEditStatus.check(R.id.rb_equipment_edit_status_stolen);
        }

    }

    /**
     * Method that shows the user options for take picture from camera or choose an image
     * from gallery to be uploaded on firebase storage
     */
    @OnClick(R.id.image_camera_picture_upload)
    public void onEquipmentImageClick() {
        Log.d(TAG, "onEquipmentImageClick() inside method");

        Dexter.withActivity(mActivity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(this).check();

    }

    /**
     * Method that gives permission granted from user for choose an image either from phone gallery
     * or by taking picture on camera
     *
     * @param report
     */
    @Override
    public void onPermissionsChecked(MultiplePermissionsReport report) {

        if (report.areAllPermissionsGranted()) {
            showImagePickerOptions();
        }

        if (report.isAnyPermissionPermanentlyDenied()) {
            showSettingsDialog();
        }

    }

    /**
     * Method that sets the permission given by user
     *
     * @param permissions
     * @param token
     */
    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
        token.continuePermissionRequest();
    }

    /**
     * Method that shows which options the user chooses his images, from galery or from taking photos
     * on camera
     */
    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getContext(), mOptionListener);
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getString(R.string.dialog_permission_title));

        builder.setMessage(getString(R.string.dialog_permission_message));

        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {

            dialog.cancel();

            openSettings();

        });

        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());

        builder.show();

    }

    /**
     * Open phone gallery settings
     */
    private void openSettings() {
        mSettingsListener.openSettings();
    }

    /**
     * Method that sets the image chosen by user on the screen component to be uploaded
     *
     * @param requestCode
     * @param requestImage
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int requestImage, int resultCode, @Nullable Intent data) {

        if (requestCode == requestImage) {

            if (resultCode == Activity.RESULT_OK) {

                filePath = data.getParcelableExtra(getContext().getResources().getString(R.string.path_uri));

                urlEquipmentPicture = filePath.toString();

                // Boolean to verify whether is tablet mode or not
                boolean isLandscapeMode = getContext().getResources().getBoolean(R.bool.isLand);

                if (isLandscapeMode) {

                    Glide.with(getContext().getApplicationContext())
                            .load(filePath.toString())
                            .into(imageViewEquipmentEditPictureToUpload);

                } else {

                    Glide.with(getContext().getApplicationContext())
                            .load(filePath.toString())
                            .into(imageViewEquipmentEditPicture);

                }

            }

        }

    }

    /**
     * Validates the email box if it is empty or if the email is valid
     *
     * @return the boolean value, true if everything is ok and false if the conditions are not ok
     */
    private boolean validateEmail() {
        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(EMAIL_PATTERN);

        int errorColor = ContextCompat.getColor(getContext().getApplicationContext(), R.color.white_text_color);

        String errorString = getContext().getResources().getString(R.string.login_profile_validator_email_not_match);

        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorString);

        spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorString.length(), 0);

        String email = editTextEquipmentEditOwnerEmail.getText().toString().trim();

        Matcher match = emailPtrn.matcher(email);

        if (!match.matches()) {
            editTextEquipmentEditOwnerEmail.setError(spannableStringBuilder);
            cleanOwnerEmailEquipmentDetailsEdit();
            valid = false;

        } else if (email.isEmpty()) {

            editTextEquipmentEditOwnerEmail.setError(getContext().getResources().getString(R.string.login_profile_validator_email_required));
            valid = false;

        }

        return valid;

    }

    private void cleanOwnerEmailEquipmentDetailsEdit() {

        if (!editTextEquipmentEditOwnerEmail.getText().toString().equals("")) {
            editTextEquipmentEditOwnerEmail.setText("");
        }

    }

    // Animation button to search email
    private void animateButtonWidth() {

        ValueAnimator anim = ValueAnimator.ofInt(btnEquipmentProfileEmailSearch.getMeasuredWidth(), getSearchWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnEquipmentProfileEmailSearch.getLayoutParams();
                layoutParams.width = value;
                btnEquipmentProfileEmailSearch.requestLayout();
            }
        });

        anim.setDuration(350);
        anim.start();

    }

    // Method to get the button size
    private int getSearchWidth() {
        return (int) getContext().getResources().getDimension(R.dimen.search_width);
    }

    // Method to fade out the icon and show the progress bar on button
    private void fadeOutTextAndSetProgressDialog() {
        btnEmailSearchButtonImage.animate().alpha(0f).setDuration(350).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showProgressDialog();
            }
        }).start();
    }

    // Show the progress button when searching email
    private void showProgressDialog() {
        progressBarEquipmentEditSoldTo.getIndeterminateDrawable().setColorFilter(getContext().getResources().getColor(R.color.transparent), PorterDuff.Mode.SRC_IN);
        progressBarEquipmentEditSoldTo.setVisibility(View.VISIBLE);
        btnEmailSearchButtonImage.setVisibility(View.GONE);
    }

    // Method tha activates the animation of showing error message if the email is not found
    private void nextAction() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                fadeOutProgressDialogAndSetText();

            }
        }, 3000);

    }

    // Method to fade out the progress bar from button
    private void fadeOutProgressBar() {
        btnEmailSearchButtonImage.animate().alpha(1f).setDuration(350).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                progressBarEquipmentEditSoldTo.setVisibility(View.GONE);
                btnEmailSearchButtonImage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.email_search_icon));
                btnEmailSearchButtonImage.setVisibility(View.VISIBLE);
            }
        });
    }

    // Method to fade out progress bar from button and set the error message if email is not found
    private void fadeOutProgressDialogAndSetText() {
        btnEmailSearchButtonImage.animate().alpha(1f).setDuration(350).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                fadeOutProgressDialog();
            }
        });
    }

    // Method to make progress bar disappear
    private void fadeOutProgressDialog() {

        progressBarEquipmentEditSoldTo.setVisibility(View.GONE);
        growButton();

        delayedStartNormalButton();

    }

    // Method to grow the button and show the error message if an error occurs when searching email
    // and it's not found
    private void growButton() {

        ValueAnimator anim = ValueAnimator.ofInt(btnEquipmentProfileEmailSearch.getMeasuredWidth(), getMessageWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnEquipmentProfileEmailSearch.getLayoutParams();
                layoutParams.width = value;
                btnEquipmentProfileEmailSearch.requestLayout();
                btnEquipmentProfileEmailSearch.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_sign_in_error));
                emailSearchText.setText(getContext().getResources().getString(R.string.profile_detail_email_label_not_found));
                emailSearchText.setVisibility(View.VISIBLE);
                emailSearchText.setTextColor(getContext().getResources().getColor(R.color.white_text_color));
            }
        });

        anim.setDuration(350);
        anim.start();
    }

    // Method to delay the growth of the button to the state of normal before the error message was shown
    private void delayedStartNormalButton() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                getNormalButton();

            }
        }, 2000);
    }

    // Method that set the button to the initial form
    private void getNormalButton() {

        ValueAnimator anim = ValueAnimator.ofInt(btnEquipmentProfileEmailSearch.getMeasuredWidth(), getSearchWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnEquipmentProfileEmailSearch.getLayoutParams();
                layoutParams.width = value;
                emailSearchText.setVisibility(View.GONE);
                btnEmailSearchButtonImage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.email_search_icon));
                btnEmailSearchButtonImage.setVisibility(View.VISIBLE);
                btnEquipmentProfileEmailSearch.requestLayout();
                btnEquipmentProfileEmailSearch.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_email_search_equipment_edit));

            }
        });

        anim.setDuration(550);
        anim.start();

    }

    // get the size of the message error button
    private int getMessageWidth() {
        return (int) getContext().getResources().getDimension(R.dimen.message_width);
    }

    // Focused on the fields and take out the hint when not empty
    private void takeOutHintFromNotEmptyFields() {

        /** Equipment brand **/
        if (editTextEquipmentEditBrand.getText().toString().isEmpty()) {

            editTextEquipmentEditBrand.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_brand));

        } else {

            editTextEquipmentEditBrand.setHint("");

        }

        // Using this listener so that the brand emptied adds hint
        editTextEquipmentEditBrand.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextEquipmentEditBrand.getText().toString().isEmpty()) {

                    editTextEquipmentEditBrand.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_brand));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Equipment model **/
        if (editTextEquipmentEditModel.getText().toString().isEmpty()) {

            editTextEquipmentEditModel.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_model));

        } else {

            editTextEquipmentEditModel.setHint("");

        }

        // Using this listener so that the model emptied adds hint
        editTextEquipmentEditModel.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextEquipmentEditModel.getText().toString().isEmpty()) {

                    editTextEquipmentEditModel.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_model));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Equipment serial number **/
        if (editTextEquipmentEditSerialNumber.getText().toString().isEmpty()) {

            editTextEquipmentEditSerialNumber.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_serial));

        } else {

            editTextEquipmentEditSerialNumber.setHint("");

        }

        // Using this listener so that the serial number emptied adds hint
        editTextEquipmentEditSerialNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextEquipmentEditSerialNumber.getText().toString().isEmpty()) {

                    editTextEquipmentEditSerialNumber.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_serial));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        /** Equipment short description **/
        if (editTextEquipmentEditShortDescription.getText().toString().isEmpty()) {

            editTextEquipmentEditShortDescription.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_short_description));

        } else {

            editTextEquipmentEditShortDescription.setHint("");

        }

        // Using this listener so that the short description emptied adds hint
        editTextEquipmentEditShortDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextEquipmentEditShortDescription.getText().toString().isEmpty()) {

                    editTextEquipmentEditShortDescription.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_short_description));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void showProgressIndication() {
        llOverboxEquipmentDetailsEditLoading.setVisibility(View.VISIBLE);
        pbEquipmentDetailsEditLoading.setVisibility(View.VISIBLE);
    }

    public void hideProgressIndication() {
        llOverboxEquipmentDetailsEditLoading.setVisibility(View.GONE);
        pbEquipmentDetailsEditLoading.setVisibility(View.GONE);
    }

}
