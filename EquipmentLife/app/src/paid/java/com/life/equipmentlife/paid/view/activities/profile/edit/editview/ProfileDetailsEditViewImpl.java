package com.life.equipmentlife.paid.view.activities.profile.edit.editview;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import com.life.equipmentlife.common.listener.AddressDataChangeCepListener;
import com.life.equipmentlife.common.listener.EstadoChangeListener;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.OnOpenSettingsListener;
import com.life.equipmentlife.common.listener.PickerOptionListener;
import com.life.equipmentlife.common.utils.MaskWatcher;
import com.life.equipmentlife.common.utils.UploadImageFireBase;
import com.life.equipmentlife.common.utils.Utils;
import com.life.equipmentlife.common.utils.ZipCodeSearch;
import com.life.equipmentlife.model.dao.AddressDataDao;
import com.life.equipmentlife.model.dao.EstadoDao;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.pojo.AddressData;
import com.life.equipmentlife.model.pojo.Estado;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.imagepicker.ImagePickerActivity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileDetailsEditViewImpl extends BaseObservableViewMvc implements ProfileDetailsEditView, MultiplePermissionsListener,
        AddressDataChangeCepListener, EstadoChangeListener {

    private static final String TAG = ProfileDetailsEditViewImpl.class.getSimpleName();

    @BindView(R.id.layout_owner_passwords)
    protected LinearLayout linearLayoutOwnerPasswords;

    @BindView(R.id.et_profile_name)
    protected EditText editViewProfileEditName;

    @BindView(R.id.et_profile_cpf)
    protected EditText editViewProfileEditCpf;

    @BindView(R.id.et_profile_phone)
    protected EditText editViewProfileEditTelephone;

    @BindView(R.id.et_profile_email)
    protected EditText editTextProfileEditEmail;

    @BindView(R.id.et_profile_cep)
    protected EditText editTextProfileEditCEP;

    @BindView(R.id.et_profile_address)
    protected EditText editTextProfileEditAddress;

    @BindView(R.id.et_profile_number)
    protected EditText editTextProfileEditAddressNumber;

    @BindView(R.id.et_profile_city)
    protected EditText editViewProfileEditCity;

    @BindView(R.id.et_profile_state)
    protected EditText editTextProfileEditState;

    @BindView(R.id.et_profile_uf)
    protected EditText editTextProfileEditUF;

    @BindView(R.id.et_profile_password)
    protected EditText editTextProfileEditPassword;

    @BindView(R.id.et_profile_password_confirm)
    protected EditText editTextProfileEditPasswordConfirm;

    @BindView(R.id.image_btn_profile_details_edit_save)
    protected ImageButton imageButtonProfileDetailsEditSave;

    @BindView(R.id.img_profile_edit)
    protected ImageView imgProfileEdit;

    @BindView(R.id.img_plus_profile_edit)
    protected ImageView imgPlusProfileEdit;

    @BindView(R.id.progressBar_profile_updating)
    protected ProgressBar progressBarProfileUpdating;

    //Uri to store the image uri
    private Uri filePath;

    //Firebase
    private FirebaseStorage mStorage;

    private StorageReference mStorageReference;

    private ContentResolver mContentResolver;

    private ProfileDao implProfileDao;

    private ZipCodeSearch zipCodeSearch;

    private boolean isLandscapeMode;

    private List<Estado> mEstados;

    // To put a flag for details edition
    private SessionManager mSession;

    private boolean isLoginFromGoogle;

    private boolean isLoginFromFacebook;

    private UploadImageFireBase uploadImageFireBase;

    private OnFinishActivityListener mOnFinishActivityListener;

    private FragmentActivity mActivity;

    private PickerOptionListener mOptionListener;

    private OnOpenSettingsListener mSettingsListener;

    public ProfileDetailsEditViewImpl(LayoutInflater inflater, ViewGroup parent,
                                      SessionManager session, ProfileDao profileDao,
                                      OnFinishActivityListener onFinishActivityListener,
                                      FragmentActivity activity, PickerOptionListener listener,
                                      OnOpenSettingsListener settingsListener,
                                      ContentResolver contentResolver,
                                      AddressDataDao addressDao, EstadoDao estadoDao) {

        setRootView(inflater.inflate(R.layout.activity_profile_details_edit, parent, false));

        ButterKnife.bind(this, getRootView());

        mActivity = activity;

        mOptionListener = listener;

        mSettingsListener = settingsListener;

        mSession = session;

        implProfileDao = profileDao;

        zipCodeSearch = new ZipCodeSearch(addressDao, this, progressBarProfileUpdating);

        mOnFinishActivityListener = onFinishActivityListener;

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mContentResolver = contentResolver;

        mSession.setEditedProfileInfo(false);

        // Boolean to verify whether is tablet mode or not
        isLandscapeMode = getContext().getResources().getBoolean(R.bool.isLand);

        // Check if the login was from Google
        isLoginFromGoogle = session.loginFromGoogle();

        // Check if the login was from Facebook
        isLoginFromFacebook = session.loginFromFacebook();

        if (isLoginFromGoogle || isLoginFromFacebook) {
            hidePasswordLayout();
        }

        takeOutHintFromNotEmptyFields();

        getEstadosList(estadoDao);

    }

    /**
     * It binds the profile got from activity class to be used in this class
     *
     * @param profile
     */
    @Override
    public void bindProfile(Profile profile) {

        initFieldsWithMasks();

        setDetailsEditFields(profile);

        searchCepFromTextComponent();

        imageButtonProfileDetailsEditSave.setOnClickListener(view -> onSaveImageButtonClicked(profile));

    }

    /**
     * It generates CPF, Telephone and CEP masks on edit text screen components
     */
    private void initFieldsWithMasks() {

        editViewProfileEditCpf.addTextChangedListener(MaskWatcher.buildCpf());
        editViewProfileEditTelephone.addTextChangedListener(new MaskWatcher("(##) #####-####"));
        editTextProfileEditCEP.addTextChangedListener(new MaskWatcher("#####-###"));

    }

    /**
     * It gets the Estado data list from API
     *
     * @param estadoDao
     */
    private void getEstadosList(EstadoDao estadoDao) {
        estadoDao.getEstadoList(this);
    }

    /**
     * The estado list object got from API as result from REST query made
     *
     * @param estados
     */
    @Override
    public void onEstadoLoaded(List<Estado> estados) {
        Log.i(TAG, "onEstadoLoaded() inside method - Estados: " + estados);

        mEstados = estados;

    }

    /**
     * It searches address data by CEP from API
     */
    private void searchCepFromTextComponent() {
        Log.i(TAG, "searchCepFromTextComponent() inside method");

        editTextProfileEditCEP.addTextChangedListener(zipCodeSearch);

    }

    /**
     * The address data object got from API as result from REST query made by CEP
     *
     * @param addressData
     */
    @Override
    public void onDataCepLoaded(AddressData addressData) {
        Log.i(TAG, "onDataCepLoaded() inside method - AddressData: " + addressData);

        String city = null;

        progressBarProfileUpdating.setVisibility(View.GONE);

        if (addressData != null) {
            Log.i(TAG, "onDataCepLoaded() inside IF");

            for (Estado estado : mEstados) {

                if (estado.getSigla().equals(addressData.getUf())) {

                    city = estado.getNome();

                }

            }

            editTextProfileEditAddressNumber.setFocusable(true);
            editTextProfileEditAddress.setText(addressData.getLogradouro());
            editViewProfileEditCity.setText(addressData.getLocalidade());
            editTextProfileEditState.setText(city);
            editTextProfileEditUF.setText(addressData.getUf());

        }

    }

    /**
     * Loads the default profile from drawable if no picture is loaded from Firebase storage
     */
    @Override
    public void loadProfileDefault() {

        if (isLandscapeMode) {

            Glide.with(getContext().getApplicationContext())
                    .load(R.drawable.baseline_account_circle_black_48)
                    .into(imgProfileEdit);

            imgProfileEdit.setColorFilter(ContextCompat.getColor(getContext(), R.color.menu_transparent));

        } else {

            Glide.with(getContext().getApplicationContext())
                    .load(R.drawable.baseline_account_circle_black_48)
                    .into(imgProfileEdit);

            imgProfileEdit.setColorFilter(ContextCompat.getColor(getContext(), R.color.menu_transparent));

        }

    }

    /**
     * Method to add image profile to be uploaded on firebase storage
     */
    @OnClick({R.id.img_profile_edit, R.id.img_plus_profile_edit})
    public void onProfileImageClick() {
        Log.d(TAG, "onProfileImageClick() inside method");

        Dexter.withActivity(mActivity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(this).check();

    }

    /**
     * Ask user's permission to access his gallery or use his camera to
     * upload his chosen picture for profile data
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
     * Shows options for the user whether the picture is chosen from gallery or by taking photo
     * from camera
     */
    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getContext(), mOptionListener);
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

                Glide.with(getContext().getApplicationContext()).load(filePath.toString()).into(imgProfileEdit);

                imgProfileEdit.setColorFilter(ContextCompat.getColor(getContext(), R.color.menu_transparent));

            }

        }

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
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveImageButtonClicked(Profile profile) {
        Log.i(TAG,"onSaveEquipmentDetailsButtonClicked() inside method - profile: " + profile);

        if (isLoginFromGoogle || isLoginFromFacebook) {

            if (!validateInfoFromSocial()) {
                return;
            }

        } else {

            if (!validate()) {
                return;
            }
        }

        populateEditedEntity(profile);

        mSession.setEditedProfileInfo(true);
        mSession.setProfileOnPrefs(profile);

        /**
         * If the user changed his profile photo then goes in the uploadImageFireBase class
         */
        if (filePath != null) {
            Log.i(TAG,"onSaveEquipmentDetailsButtonClicked() inside IF - filePath: " + filePath);

            uploadImageFireBase =
                    new UploadImageFireBase(filePath, getContext(), mStorageReference,
                            mContentResolver, progressBarProfileUpdating, implProfileDao, profile,true);

            uploadImageFireBase.uploadImageAndSaveData(0);

            mOnFinishActivityListener.onFinishActivity();

        } else { // otherwise it updates the profile info without update his profile image
            Log.i(TAG,"onSaveEquipmentDetailsButtonClicked() inside ELSE - filePath: " + filePath);
            Log.i(TAG,"onSaveEquipmentDetailsButtonClicked() inside ELSE - profile.getAddress(): " + profile.getAddress());

            implProfileDao.updateProfile(profile);

            mOnFinishActivityListener.onFinishActivity();

        }

    }

    /**
     * Method tha populates the profile onbject when user changes any information so that it can be
     * updated in database
     *
     * @param profile
     */
    private void populateEditedEntity(Profile profile) {
        Log.i(TAG,"populateEditedEntity() inside method - profile: " + profile);

        profile.setName(editViewProfileEditName.getText().toString());
        profile.setCpf(editViewProfileEditCpf.getText().toString());
        profile.setTelephone(editViewProfileEditTelephone.getText().toString());
        profile.setEmail(editTextProfileEditEmail.getText().toString());
        profile.setCep(editTextProfileEditCEP.getText().toString());
        profile.setAddress(editTextProfileEditAddress.getText().toString());
        profile.setAddressNumber(editTextProfileEditAddressNumber.getText().toString());
        profile.setCity(editViewProfileEditCity.getText().toString());
        profile.setState(editTextProfileEditState.getText().toString());
        profile.setUf(editTextProfileEditUF.getText().toString());

        profile.setPassword(editTextProfileEditPassword.getText().toString());

    }

    /**
     *
     * Method that sets the edit text components when user enter the details edit activity
     *
     * @param profile
     */
    private void setDetailsEditFields(Profile profile) {

        if (isLoginFromGoogle || isLoginFromFacebook) {

            editViewProfileEditName.setEnabled(false);
            editTextProfileEditEmail.setEnabled(false);
            imgPlusProfileEdit.setVisibility(View.GONE);
            imgPlusProfileEdit.setEnabled(false);
            imgProfileEdit.setEnabled(false);

        }

        editViewProfileEditName.setText(profile.getName());
        editViewProfileEditCpf.setText(profile.getCpf());
        editViewProfileEditTelephone.setText(profile.getTelephone());
        editTextProfileEditEmail.setText(profile.getEmail());
        editTextProfileEditCEP.setText(profile.getCep());
        editTextProfileEditAddress.setText(profile.getAddress());
        editTextProfileEditAddressNumber.setText(profile.getAddressNumber());
        editViewProfileEditCity.setText(profile.getCity());
        editTextProfileEditState.setText(profile.getState());
        editTextProfileEditUF.setText(profile.getUf());

        editTextProfileEditPassword.setText(profile.getPassword());
        editTextProfileEditPasswordConfirm.setText(profile.getPassword());

        if (profile.getPicture() != null) {

            if ((!profile.getPicture().isEmpty()) || (profile.getPicture() != "")){

                if (isLoginFromGoogle || isLoginFromFacebook) {

                    Glide.with(getContext().getApplicationContext())
                            .load(profile.getPicture())
                            .into(imgProfileEdit);

                } else {

                    Uri imageUrl = Utils.buildImageUrl(profile.getPicture());

                    Glide.with(getContext().getApplicationContext())
                            .load(imageUrl)
                            .into(imgProfileEdit);

                }

            }

        }

    }

    /**
     * It hides password layout from screen components of users logged in from social media
     */
    private void hidePasswordLayout() {

        linearLayoutOwnerPasswords.setVisibility(View.GONE);

    }

    /**
     * It validates the required fields to check if they're empty or invalid and then returns a boolean value
     *
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        String name = editViewProfileEditName.getText().toString();

        String cpf = editViewProfileEditCpf.getText().toString();

        String phone = editViewProfileEditTelephone.getText().toString();

        String email = editTextProfileEditEmail.getText().toString();

        String cep = editTextProfileEditCEP.getText().toString();

        String password = editTextProfileEditPassword.getText().toString();

        String confirmPassword = editTextProfileEditPasswordConfirm.getText().toString();

        Matcher match = emailPtrn.matcher(email);

        if (name.isEmpty()) {

            editViewProfileEditName.setError(getContext().getResources().getString(R.string.registration_profile_validator_name_required));
            valid = false;

        } else if (cpf.isEmpty()) {

            editViewProfileEditCpf.setError(getContext().getResources().getString(R.string.registration_profile_validator_cpf_required));
            valid = false;

        } else if (phone.isEmpty()) {

            editViewProfileEditTelephone.setError(getContext().getResources().getString(R.string.registration_profile_validator_phone_required));
            valid = false;

        } else if (cep.isEmpty()) {

            editTextProfileEditCEP.setError(getContext().getResources().getString(R.string.registration_profile_validator_cep_required));
            valid = false;

        } else if (email.isEmpty()) {

            editTextProfileEditEmail.setError(getContext().getResources().getString(R.string.login_profile_validator_email_required));
            valid = false;

        } else if (password.isEmpty() || password.length() < 4 || password.length() > 10) {

            editTextProfileEditPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;

        } else if (!password.equals(confirmPassword)) {

            editTextProfileEditPassword.setError("Passwords must be matched");
            valid = false;

        } else if (!match.matches()) {

            editTextProfileEditEmail.setError(getContext().getResources().getString(R.string.login_profile_validator_email_not_match));
            valid = false;

        } else {

            editTextProfileEditPassword.setError(null);
        }

        return valid;
    }

    /**
     * It validates the screen components of users logged in from social media
     *
     * @return
     */
    public boolean validateInfoFromSocial() {
        boolean valid = true;


        String cpf = editViewProfileEditCpf.getText().toString();
        String phone = editViewProfileEditTelephone.getText().toString();

        if (cpf.isEmpty()) {

            editViewProfileEditCpf.setError(getContext().getResources().getString(R.string.registration_profile_validator_cpf_required));
            valid = false;

        } else if (phone.isEmpty()) {

            editViewProfileEditTelephone.setError(getContext().getResources().getString(R.string.registration_profile_validator_phone_required));
            valid = false;

        } else {

            editViewProfileEditTelephone.setError(null);
        }

        return valid;
    }

    // Focused on the fields and take out the hint when not empty
    private void takeOutHintFromNotEmptyFields() {

        /** Profile name **/
        if (editViewProfileEditName.getText().toString().isEmpty()) {

            editViewProfileEditName.setHint(getContext().getResources().getString(R.string.hint_profile_edit_name));

        } else {

            editViewProfileEditName.setHint("");

        }

        // Using this listener so that the name emptied adds hint
        editViewProfileEditName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewProfileEditName.getText().toString().isEmpty()) {

                    editViewProfileEditName.setHint(getContext().getResources().getString(R.string.hint_profile_edit_name));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile CPF **/
        if (editViewProfileEditCpf.getText().toString().isEmpty()) {

            editViewProfileEditCpf.setHint(getContext().getResources().getString(R.string.hint_profile_edit_cpf));

        } else {

            editViewProfileEditCpf.setHint("");

        }

        // Using this listener so that the CPF emptied adds hint
        editViewProfileEditCpf.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewProfileEditCpf.getText().toString().isEmpty()) {

                    editViewProfileEditCpf.setHint(getContext().getResources().getString(R.string.hint_profile_edit_cpf));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile Telephone **/
        if (editViewProfileEditTelephone.getText().toString().isEmpty()) {

            editViewProfileEditTelephone.setHint(getContext().getResources().getString(R.string.hint_profile_edit_telephone));

        } else {

            editViewProfileEditTelephone.setHint("");

        }

        // Using this listener so that the telephone emptied adds hint
        editViewProfileEditTelephone.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewProfileEditTelephone.getText().toString().isEmpty()) {

                    editViewProfileEditTelephone.setHint(getContext().getResources().getString(R.string.hint_profile_edit_telephone));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile CEP **/
        if (editTextProfileEditCEP.getText().toString().isEmpty()) {

            editTextProfileEditCEP.setHint(getContext().getResources().getString(R.string.hint_profile_edit_cep));

        } else {

            editTextProfileEditCEP.setHint("");

        }

        // Using this listener so that the cep emptied adds hint
        editTextProfileEditCEP.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileEditCEP.getText().toString().isEmpty()) {

                    editTextProfileEditCEP.setHint(getContext().getResources().getString(R.string.hint_profile_edit_cep));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile address **/
        if (editTextProfileEditAddress.getText().toString().isEmpty()) {

            editTextProfileEditAddress.setHint(getContext().getResources().getString(R.string.hint_profile_edit_address));

        } else {

            editTextProfileEditAddress.setHint("");

        }

        // Using this listener so that the address emptied adds hint
        editTextProfileEditAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileEditAddress.getText().toString().isEmpty()) {

                    editTextProfileEditAddress.setHint(getContext().getResources().getString(R.string.hint_profile_edit_address));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile address number **/
        if (editTextProfileEditAddressNumber.getText().toString().isEmpty()) {

            editTextProfileEditAddressNumber.setHint(getContext().getResources().getString(R.string.hint_profile_edit_number));

        } else {

            editTextProfileEditAddressNumber.setHint("");

        }

        // Using this listener so that the address number emptied adds hint
        editTextProfileEditAddressNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileEditAddressNumber.getText().toString().isEmpty()) {

                    editTextProfileEditAddressNumber.setHint(getContext().getResources().getString(R.string.hint_profile_edit_number));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile city **/
        if (editViewProfileEditCity.getText().toString().isEmpty()) {

            editViewProfileEditCity.setHint(getContext().getResources().getString(R.string.hint_profile_edit_city));

        } else {

            editViewProfileEditCity.setHint("");

        }

        // Using this listener so that the city emptied adds hint
        editViewProfileEditCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewProfileEditCity.getText().toString().isEmpty()) {

                    editViewProfileEditCity.setHint(getContext().getResources().getString(R.string.hint_profile_edit_city));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile state **/
        if (editTextProfileEditState.getText().toString().isEmpty()) {

            editTextProfileEditState.setHint(getContext().getResources().getString(R.string.hint_profile_edit_state));

        } else {

            editTextProfileEditState.setHint("");

        }

        // Using this listener so that the state emptied adds hint
        editTextProfileEditState.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileEditState.getText().toString().isEmpty()) {

                    editTextProfileEditState.setHint(getContext().getResources().getString(R.string.hint_profile_edit_state));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile state UF **/
        if (editTextProfileEditUF.getText().toString().isEmpty()) {

            editTextProfileEditUF.setHint(getContext().getResources().getString(R.string.hint_profile_edit_uf));

        } else {

            editTextProfileEditUF.setHint("");

        }

        // Using this listener so that the state UF emptied adds hint
        editTextProfileEditUF.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileEditUF.getText().toString().isEmpty()) {

                    editTextProfileEditUF.setHint(getContext().getResources().getString(R.string.hint_profile_edit_uf));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile Email **/
        if (editTextProfileEditEmail.getText().toString().isEmpty()) {

            editTextProfileEditEmail.setHint(getContext().getResources().getString(R.string.hint_profile_edit_email));

        } else {

            editTextProfileEditEmail.setHint("");

        }

        // Using this listener so that the email emptied adds hint
        editTextProfileEditEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileEditEmail.getText().toString().isEmpty()) {

                    editTextProfileEditEmail.setHint(getContext().getResources().getString(R.string.hint_profile_edit_email));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile Password **/
        if (editTextProfileEditPassword.getText().toString().isEmpty()) {

            editTextProfileEditPassword.setHint(getContext().getResources().getString(R.string.hint_profile_edit_password));

        } else {

            editTextProfileEditPassword.setHint("");

        }

        // Using this listener so that the password emptied adds hint
        editTextProfileEditPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileEditPassword.getText().toString().isEmpty()) {

                    editTextProfileEditPassword.setHint(getContext().getResources().getString(R.string.hint_profile_edit_password));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile Confirm password **/
        if (editTextProfileEditPasswordConfirm.getText().toString().isEmpty()) {

            editTextProfileEditPasswordConfirm.setHint(getContext().getResources().getString(R.string.hint_profile_edit_password_confirm));

        } else {

            editTextProfileEditPasswordConfirm.setHint("");

        }

        // Using this listener so that the password confirmation emptied adds hint
        editTextProfileEditPasswordConfirm.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileEditPasswordConfirm.getText().toString().isEmpty()) {

                    editTextProfileEditPasswordConfirm.setHint(getContext().getResources().getString(R.string.hint_profile_edit_password_confirm));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
