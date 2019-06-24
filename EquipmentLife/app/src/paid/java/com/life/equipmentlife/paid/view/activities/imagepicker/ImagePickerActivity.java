package com.life.equipmentlife.paid.view.activities.imagepicker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.life.equipmentlife.R;
import com.life.equipmentlife.common.listener.OnActivityForResultImgPickerListener;
import com.life.equipmentlife.common.listener.PickerOptionListener;
import com.life.equipmentlife.paid.view.activities.base.BaseActivity;
import com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview.ImagePickerView;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import static com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview.ImagePickerViewImpl.REQUEST_GALLERY_IMAGE;
import static com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview.ImagePickerViewImpl.REQUEST_IMAGE_CAPTURE;
import static com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview.ImagePickerViewImpl.fileName;

public class ImagePickerActivity extends BaseActivity implements OnActivityForResultImgPickerListener {
    private static final String TAG = ImagePickerActivity.class.getSimpleName();

    private ImagePickerView mPickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPickerView = getCompositionRoot().getViewFactory()
                .getImagePickerView(null, getIntent(), this, this);

        setContentView(mPickerView.getRootView());

    }

    /**
     * Method shows the picker options for the user on screen
     *
     * @param context
     * @param listener
     */
    public static void showImagePickerOptions(Context context, PickerOptionListener listener) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.lbl_set_profile_photo));

        // add a list
        String[] animals = {context.getString(R.string.lbl_take_camera_picture), context.getString(R.string.lbl_choose_from_gallery)};
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    listener.onTakeCameraSelected();
                    break;
                case 1:
                    listener.onChooseGallerySelected();
                    break;
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void doActivityForResult(Intent intent, int resultCode) {
        startActivityForResult(intent, resultCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            case REQUEST_IMAGE_CAPTURE:

                if (resultCode == RESULT_OK) {

                    mPickerView.cropImage(mPickerView.getCacheImagePath(fileName), getContentResolver());

                } else {

                    mPickerView.setResultCancelled();

                }

                break;

            case REQUEST_GALLERY_IMAGE:

                if (resultCode == RESULT_OK) {

                    Uri imageUri = data.getData();

                    mPickerView.cropImage(imageUri, getContentResolver());

                } else {

                    mPickerView.setResultCancelled();

                }

                break;

            case UCrop.REQUEST_CROP:

                if (resultCode == RESULT_OK) {

                    mPickerView.handleUCropResult(data);

                } else {

                    mPickerView.setResultCancelled();

                }

                break;

            case UCrop.RESULT_ERROR:

                final Throwable cropError = UCrop.getError(data);

                Log.e(TAG, "Crop error: " + cropError);

                mPickerView.setResultCancelled();

                break;

            default:

                mPickerView.setResultCancelled();

        }

    }

    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
    public static void clearCache(Context context) {

        File path = new File(context.getExternalCacheDir(), "camera");

        if (path.exists() && path.isDirectory()) {

            for (File child : path.listFiles()) {

                child.delete();

            }

        }

    }

}
