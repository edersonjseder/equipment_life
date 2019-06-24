package com.life.equipmentlife.paid.view.activities.equipment.registration;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.OnOpenSettingsListener;
import com.life.equipmentlife.common.listener.OnSetupSharedElementTransitionListener;
import com.life.equipmentlife.common.listener.PickerOptionListener;
import com.life.equipmentlife.common.transition.MorphTransition;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.equipment.registration.registrationview.EquipmentRegistrationView;
import com.life.equipmentlife.paid.view.activities.imagepicker.ImagePickerActivity;
import com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview.ImagePickerViewImpl;

public class EquipmentRegistrationActivity extends BaseActivity implements PickerOptionListener,
        OnSetupSharedElementTransitionListener, OnOpenSettingsListener, OnFinishActivityListener {

    private static final int REQUEST_IMAGE = 100;

    private SessionManager mSession;

    private ContentResolver contentResolver;

    private EquipmentDao equipmentDao;

    private EquipmentRegistrationView mRegistrationViewMvc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSession = getCompositionRoot().getSessionManager();

        contentResolver = getContentResolver();

        equipmentDao = getCompositionRoot().getEquipmentDao();

        mRegistrationViewMvc =
                getCompositionRoot()
                        .getViewFactory()
                        .getEquipmentRegistrationView(null, mSession, contentResolver,
                                this, this, equipmentDao,
                                this, this, this);

        ImagePickerActivity.clearCache(this);

        setContentView(mRegistrationViewMvc.getRootView());

    }

    /**
     * Method that add transition effects for entering Equipment registration class
     *
     * @param sharedEnter
     * @param sharedReturn
     */
    @Override
    public void setupSharedElementTransitions(MorphTransition sharedEnter, MorphTransition sharedReturn) {
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    public void dismiss() {
        setResult(Activity.RESULT_CANCELED);
        finishAfterTransition();
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
/**
        // setting aspect ratio
        intent.putExtra(ImagePickerViewImpl.INTENT_LOCK_ASPECT_RATIO, true);**/
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
        mRegistrationViewMvc.onActivityResult(requestCode, REQUEST_IMAGE, resultCode, data);
    }

    @Override
    public void onFinishActivity() {
        finish();
    }

}
