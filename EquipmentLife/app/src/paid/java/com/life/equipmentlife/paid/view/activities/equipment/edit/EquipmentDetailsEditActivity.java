package com.life.equipmentlife.paid.view.activities.equipment.edit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.common.listener.OnOpenSettingsListener;
import com.life.equipmentlife.common.listener.PickerOptionListener;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.equipment.edit.editview.EquipmentDetailsEditView;
import com.life.equipmentlife.paid.view.activities.imagepicker.ImagePickerActivity;
import com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview.ImagePickerViewImpl;

public class EquipmentDetailsEditActivity extends BaseActivity implements
        PickerOptionListener, OnOpenSettingsListener, OnFinishActivityListener {
    private static final String TAG = EquipmentDetailsEditActivity.class.getSimpleName();

    private static final int REQUEST_IMAGE = 100;

    private SessionManager session;

    private Equipment mEquipment;

    private ProfileDao profileDao;

    private EquipmentDao equipmentDao;

    private EquipmentDetailsEditView mDetailsEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate() inside method");

        session = getCompositionRoot().getSessionManager();

        profileDao = getCompositionRoot().getProfileDao();

        equipmentDao = getCompositionRoot().getEquipmentDao();

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                mEquipment = (Equipment) intentThatStartedThisActivity.getSerializableExtra(Intent.EXTRA_TEXT);

            }
        }

        mDetailsEditView
                = getCompositionRoot().getViewFactory()
                .getEquipmentDetailsEditView(null, profileDao, equipmentDao, session, getContentResolver(),
                        this, this, this, this);

        ImagePickerActivity.clearCache(this);

        setContentView(mDetailsEditView.getRootView());

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDetailsEditView.bindEquipment(mEquipment);
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

        intent.putExtra(ImagePickerViewImpl.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerViewImpl.INTENT_ASPECT_RATIO_Y, 1);

        startActivityForResult(intent, REQUEST_IMAGE);

    }

    /**
     * Method that sets the image chosen by user on the screen component to be uploaded
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mDetailsEditView.onActivityResult(requestCode, REQUEST_IMAGE, resultCode, data);
    }

    @Override
    public void onFinishActivity() {
        finish();
    }

}
