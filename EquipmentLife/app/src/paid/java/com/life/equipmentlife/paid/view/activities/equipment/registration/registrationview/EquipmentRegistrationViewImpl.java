package com.life.equipmentlife.paid.view.activities.equipment.registration.registrationview;

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
import android.text.TextWatcher;
import android.transition.ArcMotion;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.life.equipmentlife.common.listener.EquipmentDataChangeSerialListener;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.OnOpenSettingsListener;
import com.life.equipmentlife.common.listener.OnSetupSharedElementTransitionListener;
import com.life.equipmentlife.common.listener.PickerOptionListener;
import com.life.equipmentlife.common.transition.MorphTransition;
import com.life.equipmentlife.common.utils.UploadImageFireBase;
import com.life.equipmentlife.common.utils.Utils;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.daoimpl.EquipmentDaoImpl;
import com.life.equipmentlife.model.executors.ThreadExecutorInsertEquipment;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.imagepicker.ImagePickerActivity;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EquipmentRegistrationViewImpl extends BaseObservableViewMvc<EquipmentRegistrationView.OnNavigationUp>
        implements EquipmentRegistrationView, EquipmentDataChangeSerialListener, MultiplePermissionsListener {

    private static final String TAG = EquipmentRegistrationViewImpl.class.getSimpleName();

    @Nullable
    @BindView(R.id.ll_equipment_registration_content)
    protected LinearLayout llEquipmentRegistrationContent;

    @Nullable
    @BindView(R.id.ll_equipment_serial_search)
    protected LinearLayout llEquipmentSerialSearch;

    @Nullable
    @BindView(R.id.imageView_equipment_registration_picture)
    protected ImageView imageViewEquipmentRegistrationPicture;

    @BindView(R.id.et_equipment_regist_brand)
    protected EditText editViewEquipmentBrand;

    @BindView(R.id.et_equipment_regist_model)
    protected EditText editViewEquipmentModel;

    @BindView(R.id.et_equipment_registration_serial_number)
    protected EditText editViewEquipmentSerialNumber;

    @BindView(R.id.tv_equipment_owner_registration)
    protected TextView textViewEquipmentOwner;

    @BindView(R.id.tv_equipment_registration_date_text)
    protected TextView textViewEquipmentRegistrationDate;

    @BindView(R.id.radioGroupEquipmentRegistrationStatus)
    protected RadioGroup radioGroupEquipmentStatus;

    @BindView(R.id.rb_equipment_registration_status_owned)
    protected RadioButton rbEquipmentRegistrationStatusOwned;

    @BindView(R.id.et_equipment_regist_short_description)
    protected EditText editTextEquipmentShortDescription;

    @BindView(R.id.iv_equipment_registration_save)
    protected ImageButton imageButtonEquipmentRegistSave;

    @BindView(R.id.progressBar_equipment_registration_upload_picture)
    protected ProgressBar progressBarEquipmentRegistPictureUpload;

    @BindView(R.id.layout_equipment_registration_activity)
    protected ViewGroup container;

    @BindView(R.id.image_camera_picture_upload)
    protected ImageView imageCameraPictureUpload;

    @BindView(R.id.btn_equipment_serial_search)
    protected FrameLayout btnEquipmentSerialSearch;

    @BindView(R.id.btnSerialSearchButtonImage)
    protected ImageView btnSerialSearchButtonImage;

    @BindView(R.id.et_equipment_registration_serial_number_search)
    protected EditText editTextEquipmentRegistrationSerialNumber;

    @BindView(R.id.progressBar_equipment_serial_search)
    protected ProgressBar progressBarEquipmentSerialSearch;

    @BindView(R.id.serialSearchText)
    protected TextView serialSearchText;

    @BindView(R.id.ll_serial_search_message)
    protected LinearLayout llSerialSearchMessage;

    @BindView(R.id.ll_serial_search_message_registered)
    protected LinearLayout llSerialSearchMessageRegistered;

    private ImageView imageViewEquipmentRegistrationPictureToUpload;

    //Uri to store the image uri
    private Uri filePath;

    //Firebase
    private FirebaseStorage mStorage;

    private StorageReference mStorageReference;

    private ContentResolver mContentResolver;

    private String ownerName;

    private String serialNumber;

    private boolean isLandscapeMode;

    private String registered;

    private String uid;

    private EquipmentDao mEquipmentDao;

    private EquipmentDaoImpl implInsert;

    private ThreadExecutorInsertEquipment executorInsert;

    private OnSetupSharedElementTransitionListener mOnSetupSharedElementTransitionListener;

    private FragmentActivity mActivity;

    private UploadImageFireBase imageFireBase;

    private PickerOptionListener mOptionListener;

    private OnOpenSettingsListener mSettingsListener;

    private OnFinishActivityListener mOnFinishActivityListener;

    /**
     * Class constructor used by the activity where it is linked to
     *
     * @param inflater
     * @param parent
     * @param session
     * @param contentResolver
     * @param sharedElementTransitionListener
     * @param equipmentDao
     */
    public EquipmentRegistrationViewImpl(LayoutInflater inflater, ViewGroup parent,
                                         SessionManager session,
                                         ContentResolver contentResolver,
                                         OnFinishActivityListener onFinishActivityListener,
                                         OnSetupSharedElementTransitionListener sharedElementTransitionListener,
                                         EquipmentDao equipmentDao, FragmentActivity activity,
                                         PickerOptionListener listener, OnOpenSettingsListener settingsListener) {

        setRootView(inflater.inflate(R.layout.activity_equipment_registration, parent, false));

        ButterKnife.bind(this, getRootView());

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mContentResolver = contentResolver;

        mActivity = activity;

        mOptionListener = listener;

        mOnFinishActivityListener = onFinishActivityListener;

        mSettingsListener = settingsListener;

        mEquipmentDao = equipmentDao;

        implInsert = new EquipmentDaoImpl();

        mOnSetupSharedElementTransitionListener = sharedElementTransitionListener;

        Date registeredDate = new Date();
        registered = Utils.getFormattedDate(registeredDate);

        ownerName = session.getProfileOnPrefs().getName();
        Log.i(TAG, "onCreate() inside method - ownerName: " + ownerName);

        uid = session.getProfileOnPrefs().getId();

        // Boolean to verify whether is tablet mode or not
        isLandscapeMode = getContext().getResources().getBoolean(R.bool.isLand);

        if (isLandscapeMode) {
            imageViewEquipmentRegistrationPictureToUpload = findViewById(R.id.imageView_equipment_registration_picture_land);
        }

        setupSharedElementTransitions();

        takeOutHintFromNotEmptyFields();

        textViewEquipmentOwner.setText(ownerName);
        textViewEquipmentRegistrationDate.setText(registered);

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new equipment data into the underlying database.
     */
    @OnClick(R.id.iv_equipment_registration_save)
    public void onSaveButtonClicked() {
        Log.i(TAG, "onSaveButtonClicked() inside method");

        if (!validate()) {
            return;
        }

        final Equipment equipment = new Equipment();

        populateEntity(equipment);

        // If user wants to save equipment data with picture upload
        // then enter in this IF
        if (filePath != null) {

            imageFireBase = new UploadImageFireBase(filePath, uid, getContext(), mStorageReference,
                    mContentResolver, progressBarEquipmentRegistPictureUpload, implInsert, equipment,
                    mOnFinishActivityListener);

            imageFireBase.uploadImageAndSaveData(1);

        } else {

            equipment.setPicture("");

            executorInsert = new ThreadExecutorInsertEquipment(implInsert, equipment);

            executorInsert.insertDataThread();

            showSuccessfulMessage();

            mOnFinishActivityListener.onFinishActivity();

        }

    }

    /**
     * Populates the equipment pojo to be saved on database
     *
     * @param equipment
     */
    private void populateEntity(Equipment equipment) {
        Log.v(TAG, "Inside method populateEntity()");

        String status = getEquipmentStatusFromViews();

        if (status.equals(getContext().getResources().getString(R.string.equipment_sold))) {

            equipment.setStatusCurrentOwner(status);
            equipment.setStatusPreviousOwner(getContext().getResources().getString(R.string.equipment_owned));

            equipment.setProfileCurrentOwnerId(uid);
            equipment.setProfilePreviousOwnerId(uid);

            equipment.setCurrentOwner(textViewEquipmentOwner.getText().toString());
            equipment.setPreviousOwner(textViewEquipmentOwner.getText().toString());

        } else if (status.equals(getContext().getResources().getString(R.string.equipment_stolen))) {

            equipment.setStatusCurrentOwner(status);
            equipment.setStatusPreviousOwner(getContext().getResources().getString(R.string.equipment_owned));

            equipment.setProfilePreviousOwnerId(uid);
            equipment.setProfileCurrentOwnerId(uid);

            equipment.setPreviousOwner(textViewEquipmentOwner.getText().toString());
            equipment.setCurrentOwner(textViewEquipmentOwner.getText().toString());

        } else {

            equipment.setStatusCurrentOwner(status);
            equipment.setStatusPreviousOwner(status);

            equipment.setProfileCurrentOwnerId(uid);
            equipment.setProfilePreviousOwnerId(uid);

            equipment.setCurrentOwner(textViewEquipmentOwner.getText().toString());
            equipment.setPreviousOwner(textViewEquipmentOwner.getText().toString());
        }

        equipment.setBrand(editViewEquipmentBrand.getText().toString());
        equipment.setModel(editViewEquipmentModel.getText().toString());
        equipment.setSerialNumber(editViewEquipmentSerialNumber.getText().toString());
        equipment.setRegistrationDate(registered);
        equipment.setShortDescription(editTextEquipmentShortDescription.getText().toString());

    }

    /**
     * Method that load options for user choose what place to get his pictures
     */
    @OnClick(R.id.image_camera_picture_upload)
    public void onEquipmentImageClick() {
        Log.d(TAG, "onEquipmentImageClick() inside method");


        Dexter.withActivity(mActivity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(this).check();

    }

    /**
     * method that grant permission for the user to choose which place he is getting the pictures
     * to upload
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
            Log.i(TAG, "activityResult() inside method - mStorageReference: " + mStorageReference );

            if (resultCode == Activity.RESULT_OK) {

                filePath = data.getParcelableExtra(getContext().getResources().getString(R.string.path_uri));
                // Boolean to verify whether is tablet mode or not
                boolean isLandscapeMode = getContext().getResources().getBoolean(R.bool.isLand);

                if (isLandscapeMode) {

                    Glide.with(getContext().getApplicationContext())
                            .load(filePath.toString())
                            .into(imageViewEquipmentRegistrationPictureToUpload);

                } else {

                    Glide.with(getContext().getApplicationContext())
                            .load(filePath.toString())
                            .into(imageViewEquipmentRegistrationPicture);

                }

            }

        }

    }

    /**
     * getEquipmentStatusFromViews is called whenever the selected status needs to be retrieved
     */
    public String getEquipmentStatusFromViews() {

        String status = "";

        int checkedId = radioGroupEquipmentStatus.getCheckedRadioButtonId();

        switch (checkedId) {
            case R.id.rb_equipment_registration_status_owned:
                status = getContext().getResources().getString(R.string.equipment_owned);
                break;
            case R.id.rb_equipment_registration_status_sold:
                status = getContext().getResources().getString(R.string.equipment_sold);
        }

        return status;

    }

    public void setupSharedElementTransitions() {

        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.fast_out_slow_in);


        MorphTransition sharedEnter = new MorphTransition(ContextCompat.getColor(getContext(), R.color.dialog_background_color),
                ContextCompat.getColor(getContext(), R.color.dialog_background_color), 100, getContext().getResources().getDimensionPixelSize(R.dimen.dialog_corners), true);
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphTransition sharedReturn = new MorphTransition(ContextCompat.getColor(getContext(), R.color.dialog_background_color),
                ContextCompat.getColor(getContext(), R.color.fab_background_color), getContext().getResources().getDimensionPixelSize(R.dimen.dialog_corners), 100,  false);
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (container != null) {
            sharedEnter.addTarget(container);
            sharedReturn.addTarget(container);
        }

        mOnSetupSharedElementTransitionListener.setupSharedElementTransitions(sharedEnter, sharedReturn);

    }

    /**
     * Searches the equipment by serial before filling the info to verify if the equipment
     * is okay, so that the user doesn't register a stolen equipment
     */
    @OnClick(R.id.btn_equipment_serial_search)
    protected void searchEquipmentBySerial() {
        Log.i(TAG,"searchEquipmentBySerial() inside method");

        if (!validateSerial()) {
            return;
        }

        animateButtonWidth();
        fadeOutTextAndSetProgressDialog();

        serialNumber = editTextEquipmentRegistrationSerialNumber.getText().toString().trim();

        mEquipmentDao.fetchEquipmentBySerial(serialNumber, "", this);

    }

    /**
     * method that returns the equipment info searched and verifies if it's okay to register (Not stolen)
     * or not
     * @param equipment
     */
    @Override
    public void onDataSerialLoaded(Equipment equipment) {
        Log.i(TAG,"onDataSerialLoaded() inside method - equipment.getSerialNumber(): " + equipment.getSerialNumber());

        if (equipment.getSerialNumber() != null) {

            if (equipment.getSerialNumber().equals(serialNumber)) {

                nextActionRegisteredAlready();

            } else {

                nextAction();

            }

        } else {

            editViewEquipmentSerialNumber.setText(editTextEquipmentRegistrationSerialNumber.getText().toString());
            editViewEquipmentSerialNumber.setEnabled(false);
            llEquipmentSerialSearch.setVisibility(View.GONE);
            imageButtonEquipmentRegistSave.setVisibility(View.VISIBLE);
            llEquipmentRegistrationContent.setVisibility(View.VISIBLE);

        }

    }

    // Animation button to search email
    private void animateButtonWidth() {

        ValueAnimator anim = ValueAnimator.ofInt(btnEquipmentSerialSearch.getMeasuredWidth(), getSearchWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnEquipmentSerialSearch.getLayoutParams();
                layoutParams.width = value;
                btnEquipmentSerialSearch.requestLayout();
            }
        });

        anim.setDuration(350);
        anim.start();

    }

    // Method to fade out the icon and show the progress bar on button
    private void fadeOutTextAndSetProgressDialog() {
        btnSerialSearchButtonImage.animate().alpha(0f).setDuration(350).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showProgressDialog();
            }
        }).start();
    }

    // Show the progress button when searching email
    private void showProgressDialog() {
        progressBarEquipmentSerialSearch.getIndeterminateDrawable().setColorFilter(getContext().getResources().getColor(R.color.transparent), PorterDuff.Mode.SRC_IN);
        progressBarEquipmentSerialSearch.setVisibility(View.VISIBLE);
        btnSerialSearchButtonImage.setVisibility(View.GONE);
    }

    // Method tha activates the animation of showing stolen message if the serial is found
    private void nextAction() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                fadeOutProgressDialogAndSetText();

            }
        }, 3000);

    }

    // Method to fade out progress bar from button
    private void fadeOutProgressDialogAndSetText() {
        btnSerialSearchButtonImage.animate().alpha(1f).setDuration(350).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                fadeOutProgressDialog();
            }
        });
    }

    // Method to make progress bar disappear
    private void fadeOutProgressDialog() {

        progressBarEquipmentSerialSearch.setVisibility(View.GONE);
        //growButton();
        showTextMessage();
        getNormalButton();

    }

    /**
     * Animation to make progress bar disappear from button
     */
    // Method tha activates the animation of showing warning message if the equipment is already registered
    private void nextActionRegisteredAlready() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                fadingOutProgressDialog();

            }
        }, 3000);

    }

    // Method to fade out progress bar from button
    private void fadingOutProgressDialog() {
        btnSerialSearchButtonImage.animate().alpha(1f).setDuration(350).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                fadeOutProgressBar();
            }
        });
    }

    // Method to make progress bar disappear
    private void fadeOutProgressBar() {

        progressBarEquipmentSerialSearch.setVisibility(View.GONE);
        showTextMessageRegistered();
        getNormalButton();

    }

    /**
     * Shows the warning message if there is an equipment registered set to stolen status
     */
    private void showTextMessage() {
        llSerialSearchMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the warning message if there is an equipment already registered with informed serial
     */
    private void showTextMessageRegistered() {
        llSerialSearchMessageRegistered.setVisibility(View.VISIBLE);
    }

    // Method that set the button to the initial form
    private void getNormalButton() {

        ValueAnimator anim = ValueAnimator.ofInt(btnEquipmentSerialSearch.getMeasuredWidth(), getSearchWidth());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = btnEquipmentSerialSearch.getLayoutParams();
                layoutParams.width = value;
                serialSearchText.setVisibility(View.GONE);
                btnSerialSearchButtonImage.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.email_search_icon));
                btnSerialSearchButtonImage.setVisibility(View.VISIBLE);
                btnEquipmentSerialSearch.requestLayout();
                btnEquipmentSerialSearch.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.btn_email_search_equipment_edit));

            }
        });

        anim.setDuration(550);
        anim.start();

    }

    // Method to get the button size
    private int getSearchWidth() {
        return (int) getContext().getResources().getDimension(R.dimen.search_width);
    }

    // Focused on the fields and take out the hint when not empty
    private void takeOutHintFromNotEmptyFields() {

        /** Search equipment serial number **/
        if (editTextEquipmentRegistrationSerialNumber.getText().toString().isEmpty()) {

            editTextEquipmentRegistrationSerialNumber.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_serial));

        } else {

            editTextEquipmentRegistrationSerialNumber.setHint("");

        }

        // Using this listener so that the serial emptied adds hint
        editTextEquipmentRegistrationSerialNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (llSerialSearchMessage.isShown() ||
                        llSerialSearchMessageRegistered.isShown()) {

                    llSerialSearchMessage.setVisibility(View.GONE);
                    llSerialSearchMessageRegistered.setVisibility(View.GONE);

                }

                if (editTextEquipmentRegistrationSerialNumber.getText().toString().isEmpty()) {

                    editTextEquipmentRegistrationSerialNumber.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_serial));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Equipment brand **/
        if (editViewEquipmentBrand.getText().toString().isEmpty()) {

            editViewEquipmentBrand.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_brand));

        } else {

            editViewEquipmentBrand.setHint("");

        }

        // Using this listener so that the brand emptied adds hint
        editViewEquipmentBrand.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewEquipmentBrand.getText().toString().isEmpty()) {

                    editViewEquipmentBrand.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_brand));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Equipment model **/
        if (editViewEquipmentModel.getText().toString().isEmpty()) {

            editViewEquipmentModel.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_model));

        } else {

            editViewEquipmentModel.setHint("");

        }

        // Using this listener so that the model emptied adds hint
        editViewEquipmentModel.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewEquipmentModel.getText().toString().isEmpty()) {

                    editViewEquipmentModel.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_model));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Equipment serial number **/
        if (editViewEquipmentSerialNumber.getText().toString().isEmpty()) {

            editViewEquipmentSerialNumber.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_serial));

        } else {

            editViewEquipmentSerialNumber.setHint("");

        }

        // Using this listener so that the serial number emptied adds hint
        editViewEquipmentSerialNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewEquipmentSerialNumber.getText().toString().isEmpty()) {

                    editViewEquipmentSerialNumber.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_serial));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        /** Equipment short description **/
        if (editTextEquipmentShortDescription.getText().toString().isEmpty()) {

            editTextEquipmentShortDescription.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_short_description));

        } else {

            editTextEquipmentShortDescription.setHint("");

        }

        // Using this listener so that the short description emptied adds hint
        editTextEquipmentShortDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextEquipmentShortDescription.getText().toString().isEmpty()) {

                    editTextEquipmentShortDescription.setHint(getContext().getResources().getString(R.string.hint_equipment_registration_short_description));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * Validates if the fields are not empty
     * @return
     */
    private boolean validate() {
        boolean valid = true;

        String model = editViewEquipmentModel.getText().toString();

        String brand = editViewEquipmentBrand.getText().toString();

        if (model.isEmpty()) {

            editViewEquipmentModel.setError(getContext().getResources().getString(R.string.equipment_model_validator_required));
            valid = false;

        } else if (brand.isEmpty()) {

            editViewEquipmentBrand.setError(getContext().getResources().getString(R.string.equipment_brand_validator_required));
            valid = false;

        } else {
            editViewEquipmentModel.setError(null);
        }

        return valid;

    }

    /**
     * Verifies if the serial field is empty
     * @return
     */
    private boolean validateSerial() {
        Log.i(TAG,"validateSerial() inside method");
        boolean valid = true;

        String serialNumber = editTextEquipmentRegistrationSerialNumber.getText().toString();

        if (serialNumber.isEmpty()) {

            editTextEquipmentRegistrationSerialNumber.setError(getContext().getResources().getString(R.string.equipment_serial_search_validator_required));
            valid = false;

        } else {
            editTextEquipmentRegistrationSerialNumber.setError(null);
        }

        return valid;

    }

    private void showSuccessfulMessage() {
        Toast.makeText(getContext(), getContext().getResources().getString(R.string.message_save_successful), Toast.LENGTH_LONG).show();
    }

}
