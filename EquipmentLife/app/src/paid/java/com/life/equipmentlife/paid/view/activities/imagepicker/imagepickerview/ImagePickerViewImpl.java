package com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.bases.BaseObservableViewMvc;
import com.life.equipmentlife.common.listener.OnActivityForResultImgPickerListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

import static android.support.v4.content.FileProvider.getUriForFile;
import static com.facebook.FacebookSdk.getCacheDir;

public class ImagePickerViewImpl extends BaseObservableViewMvc implements ImagePickerView {
    private static final String TAG = ImagePickerViewImpl.class.getSimpleName();

    public static final String INTENT_IMAGE_PICKER_OPTION = "image_picker_option";
    public static final String INTENT_ASPECT_RATIO_X = "aspect_ratio_x";
    public static final String INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y";
    public static final String INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio";
    public static final String INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality";
    public static final String INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height";
    public static final String INTENT_BITMAP_MAX_WIDTH = "max_width";
    public static final String INTENT_BITMAP_MAX_HEIGHT = "max_height";

    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_GALLERY_IMAGE = 1;

    private boolean lockAspectRatio = false, setBitmapMaxWidthHeight = false;

    private int ASPECT_RATIO_X = 16, ASPECT_RATIO_Y = 9, bitmapMaxWidth = 1000, bitmapMaxHeight = 1000;

    private int IMAGE_COMPRESSION = 80;

    public static String fileName;

    private FragmentActivity mActivity;

    private OnActivityForResultImgPickerListener mImagePicker;

    public ImagePickerViewImpl(LayoutInflater inflater, ViewGroup parent, Intent mIntent,
                               OnActivityForResultImgPickerListener imagePicker,
                               FragmentActivity activity) {

        setRootView(inflater.inflate(R.layout.activity_image_picker, parent, false));

        mActivity = activity;

        mImagePicker = imagePicker;

        Intent intent = mIntent;
        if (intent == null) {
            Toast.makeText(getContext().getApplicationContext(), getString(R.string.toast_image_intent_null), Toast.LENGTH_LONG).show();
            return;
        }

        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X);
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y);
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION);
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false);
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false);
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight);

        int requestCode = intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            takeCameraImage();

        } else {

            chooseImageFromGallery();

        }

    }

    /**
     * method that shows the intent of options for the user to choose pictures from gallery or other locations
     */
    private void chooseImageFromGallery() {

        Dexter.withActivity(mActivity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            mImagePicker.doActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE);

                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }

                }).check();

    }

    /**
     * method that takes picture from camera app on the phone
     */
    private void takeCameraImage() {

        Dexter.withActivity(mActivity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {

                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            fileName = System.currentTimeMillis() + ".jpg";

                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));

                            if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {

                                mImagePicker.doActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

                            }

                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                    }

                }).check();

    }

    /**
     * Method that crops the image by user on screen
     *
     * @param sourceUri
     * @param contentResolver
     */
    @Override
    public void cropImage(Uri sourceUri, ContentResolver contentResolver) {

        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(contentResolver, sourceUri)));

        UCrop.Options options = new UCrop.Options();

        options.setCompressionQuality(IMAGE_COMPRESSION);

        // applying UI theme
        options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.theme_primary));
        options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.theme_primary));
        options.setActiveWidgetColor(ContextCompat.getColor(getContext(), R.color.theme_primary));

        if (lockAspectRatio)
            options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);

        if (setBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);

        UCrop.of(sourceUri, destinationUri)
                .withOptions(options)
                .start(mActivity);

    }

    /**
     * method that manages the picture crop result
     * @param data
     */
    @Override
    public void handleUCropResult(Intent data) {

        if (data == null) {

            setResultCancelled();

            return;

        }

        final Uri resultUri = UCrop.getOutput(data);

        setResultOk(resultUri);

    }

    /**
     * Sets the picture crop result on the image component
     * @param imagePath
     */
    @Override
    public void setResultOk(Uri imagePath) {

        Intent intent = new Intent();

        intent.putExtra("path", imagePath);

        mActivity.setResult(Activity.RESULT_OK, intent);

        mActivity.finish();

    }

    /**
     * Cancel the result operation of cropping image
     */
    @Override
    public void setResultCancelled() {

        Intent intent = new Intent();

        mActivity.setResult(Activity.RESULT_CANCELED, intent);

        mActivity.finish();

    }

    /**
     * Gets the name of the picture chosen by user
     * @param resolver
     * @param uri
     * @return
     */
    private static String queryName(ContentResolver resolver, Uri uri) {

        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);

        assert returnCursor != null;

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

        returnCursor.moveToFirst();

        String name = returnCursor.getString(nameIndex);

        returnCursor.close();

        return name;

    }

    /**
     * Saves the image path on cache folder on the phone
     * @param fileName
     * @return
     */
    public Uri getCacheImagePath(String fileName) {

        File path = new File(getContext().getExternalCacheDir(), "camera");

        if (!path.exists()) path.mkdirs();
            File image = new File(path, fileName);

        return getUriForFile(getContext(), getContext().getPackageName() + ".provider", image);

    }

}
