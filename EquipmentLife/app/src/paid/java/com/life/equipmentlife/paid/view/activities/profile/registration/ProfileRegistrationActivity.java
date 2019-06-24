package com.life.equipmentlife.paid.view.activities.profile.registration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.listener.OnOpenSettingsListener;
import com.life.equipmentlife.common.listener.PickerOptionListener;
import com.life.equipmentlife.model.dao.AddressDataDao;
import com.life.equipmentlife.model.dao.EstadoDao;
import com.life.equipmentlife.model.pojo.Estado;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.imagepicker.ImagePickerActivity;
import com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview.ImagePickerViewImpl;
import com.life.equipmentlife.paid.view.activities.profile.registration.registrationview.ProfileRegistrationView;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

import java.util.List;

import static com.life.equipmentlife.common.constants.Constants.UID;

public class ProfileRegistrationActivity extends BaseActivity implements PickerOptionListener, OnOpenSettingsListener {

    public static final int REQUEST_IMAGE = 100;

    private Profile mProfile;

    private String uidKey;

    private AddressDataDao addressDao;

    private EstadoDao estadoDao;

    private SessionManager mSession;

    private ScreensNavigator mScreensNavigator;

    private ProfileRegistrationView mRegistrationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSession = getCompositionRoot().getSessionManager();

        addressDao = getCompositionRoot().getAddressDataDao();

        estadoDao = getCompositionRoot().getEstadoDao();

        mScreensNavigator = getCompositionRoot().getScreensNavigator();

        Bundle bundle = getIntent().getExtras();

        // Receives the login credentials from SignUpActivity
        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                uidKey = bundle.getString(UID);
                mProfile = (Profile) bundle.getSerializable(Intent.EXTRA_TEXT);

            }

        }

        mRegistrationView = getCompositionRoot().getViewFactory()
                .getProfileRegistrationView(null, mScreensNavigator, mSession,
                        this, this, this, getContentResolver(),
                        addressDao, estadoDao);

        mRegistrationView.loadProfileDefault();

        ImagePickerActivity.clearCache(this);

        setContentView(mRegistrationView.getRootView());

    }

    @Override
    protected void onStart() {
        super.onStart();
        mRegistrationView.bindProfile(mProfile, uidKey);
    }

    /**
     * method that takes picture from camera
     */
    @Override
    public void onTakeCameraSelected() {

        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerViewImpl.INTENT_IMAGE_PICKER_OPTION, ImagePickerViewImpl.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerViewImpl.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerViewImpl.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerViewImpl.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerViewImpl.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerViewImpl.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerViewImpl.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);

    }

    /**
     * Method that chose pictures from Gallery
     */
    @Override
    public void onChooseGallerySelected() {

        Intent intent = new Intent(this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerViewImpl.INTENT_IMAGE_PICKER_OPTION, ImagePickerViewImpl.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerViewImpl.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerViewImpl.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerViewImpl.INTENT_ASPECT_RATIO_Y, 1);

        startActivityForResult(intent, REQUEST_IMAGE);

    }

    /**
     * Method that open the phone option settings
     */
    @Override
    public void openSettings() {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

        Uri uri = Uri.fromParts(getResources().getString(R.string.package_settings), getPackageName(), null);

        intent.setData(uri);

        startActivityForResult(intent, 101);

    }

    /**
     * Method that sets the image chosen by user on the screen component to be uploaded
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mRegistrationView.onActivityResult(requestCode, REQUEST_IMAGE, resultCode, data);
    }

}
