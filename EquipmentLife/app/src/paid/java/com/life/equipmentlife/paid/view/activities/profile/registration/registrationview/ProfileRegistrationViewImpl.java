package com.life.equipmentlife.paid.view.activities.profile.registration.registrationview;

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
import com.life.equipmentlife.common.listener.OnOpenSettingsListener;
import com.life.equipmentlife.common.listener.PickerOptionListener;
import com.life.equipmentlife.common.utils.CpfValidation;
import com.life.equipmentlife.common.utils.MaskWatcher;
import com.life.equipmentlife.common.utils.UploadImageFireBase;
import com.life.equipmentlife.common.utils.ZipCodeSearch;
import com.life.equipmentlife.model.dao.AddressDataDao;
import com.life.equipmentlife.model.dao.EstadoDao;
import com.life.equipmentlife.model.daoimpl.ProfileDaoImpl;
import com.life.equipmentlife.model.executors.ThreadExecutorInsertProfile;
import com.life.equipmentlife.model.pojo.AddressData;
import com.life.equipmentlife.model.pojo.Estado;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.imagepicker.ImagePickerActivity;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.life.equipmentlife.common.constants.Constants.CPF_PATTERN;
import static com.life.equipmentlife.common.constants.Constants.EMAIL_PATTERN;
import static com.life.equipmentlife.model.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static com.life.equipmentlife.model.session.SessionManager.FROM_GOOGLE_SIGNUP;

public class ProfileRegistrationViewImpl extends BaseObservableViewMvc implements ProfileRegistrationView,
        MultiplePermissionsListener, AddressDataChangeCepListener, EstadoChangeListener {

    private static final String TAG = ProfileRegistrationViewImpl.class.getSimpleName();

    @BindView(R.id.et_owner_name)
    protected EditText editViewProfileName;

    @BindView(R.id.et_owner_cpf)
    protected EditText editViewProfileCpf;

    @BindView(R.id.et_owner_phone)
    protected EditText editViewProfileTelephone;

    @BindView(R.id.et_owner_cep)
    protected EditText editTextOwnerCEP;

    @BindView(R.id.et_owner_address)
    protected EditText editTextProfileAddress;

    @BindView(R.id.et_owner_number)
    protected EditText editTextOwnerNumber;

    @BindView(R.id.et_owner_email)
    protected EditText editTextProfileEmail;

    @BindView(R.id.et_owner_city)
    protected EditText editViewProfileCity;

    @BindView(R.id.et_owner_state)
    protected EditText editTextProfileState;

    @BindView(R.id.et_owner_uf)
    protected EditText editTextOwnerUF;

    @BindView(R.id.img_profile_registration)
    protected ImageView imgProfileRegistration;

    @BindView(R.id.img_plus_profile_registration)
    protected ImageView imgPlusProfileRegistration;

    @BindView(R.id.progressBar_profile_registration_updating)
    protected ProgressBar progressBarProfileRegistrationUpdating;

    @BindView(R.id.image_btn_profile_registration_save)
    protected ImageButton imageButtonProfileRegistrationSave;

    //Uri to store the image uri
    private Uri filePath;

    //Firebase
    private FirebaseStorage mStorage;

    private StorageReference mStorageReference;

    private ContentResolver mContentResolver;

    private Profile mProfile;

    private ThreadExecutorInsertProfile executorInsert;

    private ProfileDaoImpl implInsert;

    private ZipCodeSearch zipCodeSearch;

    private ScreensNavigator mScreensNavigator;

    private List<Estado> mEstados;

    // Session Manager Class
    private SessionManager mSession;

    // Flag to check if login was from Google
    private boolean isLoginFromGoogle;

    // Flag to check if login was from facebook
    private boolean isLoginFromFacebook;

    // uid key generated from Firebase user creation
    private String uidKey;

    private FragmentActivity mActivity;

    private PickerOptionListener mOptionListener;

    private OnOpenSettingsListener mSettingsListener;

    private UploadImageFireBase uploadImageFireBase;

    public ProfileRegistrationViewImpl(LayoutInflater inflater,
                                       ViewGroup parent,
                                       ScreensNavigator screensNavigator,
                                       SessionManager sessionManager,
                                       FragmentActivity activity, PickerOptionListener listener,
                                       OnOpenSettingsListener settingsListener,
                                       ContentResolver contentResolver,
                                       AddressDataDao addressDao, EstadoDao estadoDao) {

        setRootView(inflater.inflate(R.layout.activity_profile_registration, parent, false));

        ButterKnife.bind(this, getRootView());

        mActivity = activity;

        mOptionListener = listener;

        mSettingsListener = settingsListener;

        mSession = sessionManager;

        mScreensNavigator = screensNavigator;

        implInsert = new ProfileDaoImpl();

        zipCodeSearch = new ZipCodeSearch(addressDao, this, progressBarProfileRegistrationUpdating);

        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mContentResolver = contentResolver;

        // Check if the login was from Google
        isLoginFromGoogle = mSession.pref.getBoolean(FROM_GOOGLE_SIGNUP, false);

        // Check if the login was from Facebook
        isLoginFromFacebook = mSession.pref.getBoolean(FROM_FACEBOOK_SIGNUP, false);

        takeOutHintFromNotEmptyFields();

        getEstadosList(estadoDao);

        // Using anonymous View.OnClickListener overriding onClick method:
        // Replaced by lambda
        imageButtonProfileRegistrationSave.setOnClickListener(view -> onSaveButtonClicked());

    }

    /**
     * It generates CPF, Telephone and CEP masks on edit text screen components
     */
    private void initFieldsWithMasks() {

        editViewProfileCpf.addTextChangedListener(MaskWatcher.buildCpf());
        editViewProfileTelephone.addTextChangedListener(new MaskWatcher("(##) #####-####"));
        editTextOwnerCEP.addTextChangedListener(new MaskWatcher("#####-###"));

    }

    /**
     * It binds the profile and uidKey got from activity class to be used in this class
     *
     * @param profile
     * @param idKey
     */
    @Override
    public void bindProfile(Profile profile, String idKey) {
        Log.i(TAG, "bindProfile() inside method - profile: " + profile);
        Log.i(TAG, "bindProfile() inside method - uidKey: " + uidKey);

        mProfile = profile;

        uidKey = idKey;
        Log.i(TAG, "bindProfile() inside method - after uidKey = idKey; - uidKey: " + uidKey);

        initFieldsWithMasks();

        searchCepFromTextComponent();

        if (isLoginFromGoogle) {

            applyNameAndEmailFromSocialSignUp();

        }

        if (isLoginFromFacebook) {

            applyNameAndEmailFromSocialSignUp();

        }

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

        editTextOwnerCEP.addTextChangedListener(zipCodeSearch);

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

        progressBarProfileRegistrationUpdating.setVisibility(View.GONE);

        if (addressData != null) {
            Log.i(TAG, "onDataCepLoaded() inside IF");

            for (Estado estado : mEstados) {

                if (estado.getSigla().equals(addressData.getUf())) {

                    city = estado.getNome();

                }

            }

            editTextOwnerNumber.setFocusable(true);
            editTextProfileAddress.setText(addressData.getLogradouro());
            editViewProfileCity.setText(addressData.getLocalidade());
            editTextProfileState.setText(city);
            editTextOwnerUF.setText(addressData.getUf());

        }

    }

    /**
     * Loads the default profile from drawable if no picture is loaded from Firebase storage
     */
    @Override
    public void loadProfileDefault() {

        Glide.with(getContext().getApplicationContext())
                .load(R.drawable.baseline_account_circle_black_48)
                .into(imgProfileRegistration);

        imgProfileRegistration.setColorFilter(ContextCompat.getColor(getContext(), R.color.menu_transparent));

    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        Log.i(TAG, "onSaveButtonClicked() inside method");
        Log.i(TAG, "onSaveButtonClicked() inside method - uid: " + uidKey);
        Log.i(TAG, "onSaveButtonClicked() inside method - filePath: " + filePath);

        if (!validate()) {
            return;
        }

        populateProfileEntity(mProfile);

        if (filePath != null) {
            Log.i(TAG, "onSaveButtonClicked() inside IF");

            uploadImageFireBase =
                    new UploadImageFireBase(filePath, getContext(), mStorageReference,
                            mContentResolver, progressBarProfileRegistrationUpdating, mScreensNavigator,
                            implInsert, mProfile, mSession);

            uploadImageFireBase.uploadImageAndSaveData(0);

        } else {
            Log.i(TAG, "onSaveButtonClicked() inside ELSE");

            executorInsert = new ThreadExecutorInsertProfile(implInsert, mScreensNavigator, mProfile, mSession);

            executorInsert.insertDataThread();

        }

    }

    /**
     * Populates the profile pojo to be saved on database
     *
     * @param profile
     */
    private void populateProfileEntity(Profile profile) {
        Log.i(TAG, "populateProfileEntity() inside method + profile: " + profile);

        profile.setId(uidKey);
        profile.setName(editViewProfileName.getText().toString());
        profile.setCpf(editViewProfileCpf.getText().toString());
        profile.setTelephone(editViewProfileTelephone.getText().toString());
        profile.setEmail(editTextProfileEmail.getText().toString());
        profile.setCep(editTextOwnerCEP.getText().toString());
        profile.setAddress(editTextProfileAddress.getText().toString());
        profile.setAddressNumber(editTextOwnerNumber.getText().toString());
        profile.setCity(editViewProfileCity.getText().toString());
        profile.setState(editTextProfileState.getText().toString());
        profile.setUf(editTextOwnerUF.getText().toString());

    }

    /**
     * If the login is from social media then name, email and photo is disabled
     */
    private void applyNameAndEmailFromSocialSignUp() {

        editViewProfileName.setText(mProfile.getName());
        editViewProfileName.setEnabled(false);

        editTextProfileEmail.setText(mProfile.getEmail());
        editTextProfileEmail.setEnabled(false);

        Glide.with(getContext().getApplicationContext())
                .load(mProfile.getPicture())
                .into(imgProfileRegistration);

        imgProfileRegistration.setColorFilter(ContextCompat.getColor(getContext(), R.color.menu_transparent));

        imgProfileRegistration.setEnabled(false);

        imgPlusProfileRegistration.setVisibility(View.GONE);

    }

    /**
     * Method to add image profile to be uploaded on firebase storage
     */
    @OnClick({R.id.img_profile_registration, R.id.img_plus_profile_registration})
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
     * Shows options for the user whether the picture is chosen from gallery or by taking photo
     * from camera
     */
    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getContext(), mOptionListener);
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

                Glide.with(getContext().getApplicationContext()).load(filePath.toString()).into(imgProfileRegistration);

                imgProfileRegistration.setColorFilter(ContextCompat.getColor(getContext(), R.color.menu_transparent));

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
     * It validates the required fields to check if they're empty or invalid and then returns a boolean value
     *
     * @return
     */
    public boolean validate() {
        boolean valid = true;

        Pattern emailPtrn = Pattern.compile(EMAIL_PATTERN);

        Pattern cpfPtrn = Pattern.compile(CPF_PATTERN);

        String name = editViewProfileName.getText().toString();

        String cpf = editViewProfileCpf.getText().toString();

        String phone = editViewProfileTelephone.getText().toString();

        String email = editTextProfileEmail.getText().toString();

        String cep = editTextOwnerCEP.getText().toString();

        String address = editTextProfileAddress.getText().toString();

        String city = editViewProfileCity.getText().toString();

        String state = editTextProfileState.getText().toString();

        Matcher match = emailPtrn.matcher(email);

        Matcher cpfMatch = cpfPtrn.matcher(cpf);

        if (name.isEmpty()) {

            editViewProfileName.setError(getContext().getResources().getString(R.string.registration_profile_validator_name_required));
            valid = false;

        } else if (cpf.isEmpty()) {

            editViewProfileCpf.setError(getContext().getResources().getString(R.string.registration_profile_validator_cpf_required));
            valid = false;

        } else if (!cpfMatch.matches()) {
            editViewProfileCpf.setError(getContext().getResources().getString(R.string.login_profile_validator_cpf_not_match));
            valid = false;

        } else if (CpfValidation.isValid(cpf)) {
            editViewProfileCpf.setError(getContext().getResources().getString(R.string.login_profile_validator_cpf_not_match));
            valid = false;

        } else if (phone.isEmpty()) {

            editViewProfileTelephone.setError(getContext().getResources().getString(R.string.registration_profile_validator_phone_required));
            valid = false;

        } else if (email.isEmpty()) {

            editTextProfileEmail.setError(getContext().getResources().getString(R.string.login_profile_validator_email_required));
            valid = false;

        } else if (cep.isEmpty()) {

            editTextOwnerCEP.setError(getContext().getResources().getString(R.string.registration_profile_validator_cep_required));
            valid = false;

        } else if (address.isEmpty()) {

            editTextProfileAddress.setError(getContext().getResources().getString(R.string.registration_profile_validator_address_required));
            valid = false;

        } else if (city.isEmpty()) {

            editViewProfileCity.setError(getContext().getResources().getString(R.string.registration_profile_validator_city_required));
            valid = false;

        } else if (state.isEmpty()) {

            editTextProfileState.setError(getContext().getResources().getString(R.string.registration_profile_validator_state_required));
            valid = false;

        } else if (!match.matches()) {
            editTextProfileEmail.setError(getContext().getResources().getString(R.string.login_profile_validator_email_not_match));
            valid = false;

        } else {
            editTextProfileEmail.setError(null);
        }

        return valid;
    }

    // Focused on the fields and take out the hint when not empty
    private void takeOutHintFromNotEmptyFields() {

        /** Profile name **/
        if (editViewProfileName.getText().toString().isEmpty()) {

            editViewProfileName.setHint(getContext().getResources().getString(R.string.hint_profile_edit_name));

        } else {

            editViewProfileName.setHint("");

        }

        // Using this listener so that the name emptied adds hint
        editViewProfileName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewProfileName.getText().toString().isEmpty()) {

                    editViewProfileName.setHint(getContext().getResources().getString(R.string.hint_profile_edit_name));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile CPF **/
        if (editViewProfileCpf.getText().toString().isEmpty()) {

            editViewProfileCpf.setHint(getContext().getResources().getString(R.string.hint_profile_edit_cpf));

        } else {

            editViewProfileCpf.setHint("");

        }

        // Using this listener so that the CPF emptied adds hint
        editViewProfileCpf.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewProfileCpf.getText().toString().isEmpty()) {

                    editViewProfileCpf.setHint(getContext().getResources().getString(R.string.hint_profile_edit_cpf));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile Telephone **/
        if (editViewProfileTelephone.getText().toString().isEmpty()) {

            editViewProfileTelephone.setHint(getContext().getResources().getString(R.string.hint_profile_edit_telephone));

        } else {

            editViewProfileTelephone.setHint("");

        }

        // Using this listener so that the telephone emptied adds hint
        editViewProfileTelephone.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewProfileTelephone.getText().toString().isEmpty()) {

                    editViewProfileTelephone.setHint(getContext().getResources().getString(R.string.hint_profile_edit_telephone));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile CEP **/
        if (editTextOwnerCEP.getText().toString().isEmpty()) {

            editTextOwnerCEP.setHint(getContext().getResources().getString(R.string.hint_profile_edit_cep));

        } else {

            editTextOwnerCEP.setHint("");

        }

        // Using this listener so that the cep emptied adds hint
        editTextOwnerCEP.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextOwnerCEP.getText().toString().isEmpty()) {

                    editTextOwnerCEP.setHint(getContext().getResources().getString(R.string.hint_profile_edit_cep));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile address **/
        if (editTextProfileAddress.getText().toString().isEmpty()) {

            editTextProfileAddress.setHint(getContext().getResources().getString(R.string.hint_profile_edit_address));

        } else {

            editTextProfileAddress.setHint("");

        }

        // Using this listener so that the address emptied adds hint
        editTextProfileAddress.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileAddress.getText().toString().isEmpty()) {

                    editTextProfileAddress.setHint(getContext().getResources().getString(R.string.hint_profile_edit_address));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile address number **/
        if (editTextOwnerNumber.getText().toString().isEmpty()) {

            editTextOwnerNumber.setHint(getContext().getResources().getString(R.string.hint_profile_edit_number));

        } else {

            editTextOwnerNumber.setHint("");

        }

        // Using this listener so that the address number emptied adds hint
        editTextOwnerNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextOwnerNumber.getText().toString().isEmpty()) {

                    editTextOwnerNumber.setHint(getContext().getResources().getString(R.string.hint_profile_edit_number));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile city **/
        if (editViewProfileCity.getText().toString().isEmpty()) {

            editViewProfileCity.setHint(getContext().getResources().getString(R.string.hint_profile_edit_city));

        } else {

            editViewProfileCity.setHint("");

        }

        // Using this listener so that the city emptied adds hint
        editViewProfileCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editViewProfileCity.getText().toString().isEmpty()) {

                    editViewProfileCity.setHint(getContext().getResources().getString(R.string.hint_profile_edit_city));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile state **/
        if (editTextProfileState.getText().toString().isEmpty()) {

            editTextProfileState.setHint(getContext().getResources().getString(R.string.hint_profile_edit_state));

        } else {

            editTextProfileState.setHint("");

        }

        // Using this listener so that the state emptied adds hint
        editTextProfileState.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileState.getText().toString().isEmpty()) {

                    editTextProfileState.setHint(getContext().getResources().getString(R.string.hint_profile_edit_state));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile state UF **/
        if (editTextOwnerUF.getText().toString().isEmpty()) {

            editTextOwnerUF.setHint(getContext().getResources().getString(R.string.hint_profile_edit_uf));

        } else {

            editTextOwnerUF.setHint("");

        }

        // Using this listener so that the state UF emptied adds hint
        editTextOwnerUF.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextOwnerUF.getText().toString().isEmpty()) {

                    editTextOwnerUF.setHint(getContext().getResources().getString(R.string.hint_profile_edit_uf));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /** Profile Email **/
        if (editTextProfileEmail.getText().toString().isEmpty()) {

            editTextProfileEmail.setHint(getContext().getResources().getString(R.string.hint_profile_edit_email));

        } else {

            editTextProfileEmail.setHint("");

        }

        // Using this listener so that the email emptied adds hint
        editTextProfileEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG,"onTextChanged() inside method - CharSequence: " + s);

                if (editTextProfileEmail.getText().toString().isEmpty()) {

                    editTextProfileEmail.setHint(getContext().getResources().getString(R.string.hint_profile_edit_email));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
