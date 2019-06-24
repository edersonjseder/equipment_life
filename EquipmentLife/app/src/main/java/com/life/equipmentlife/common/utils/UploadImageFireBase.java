package com.life.equipmentlife.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.life.equipmentlife.R;
import com.life.equipmentlife.common.listener.OnFinishActivityListener;
import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.dao.ProfileDao;
import com.life.equipmentlife.model.daoimpl.EquipmentDaoImpl;
import com.life.equipmentlife.model.daoimpl.ProfileDaoImpl;
import com.life.equipmentlife.model.executors.ThreadExecutorInsertEquipment;
import com.life.equipmentlife.model.executors.ThreadExecutorInsertProfile;
import com.life.equipmentlife.model.executors.ThreadExecutorUpdateEquipment;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.pojo.Profile;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.view.screensnavigator.ScreensNavigator;

public class UploadImageFireBase {

    private static final String TAG = UploadImageFireBase.class.getSimpleName();

    private String mUrlUploadedPicture;

    private Uri mFilePath;

    private String uid;

    private ContentResolver mContentResolver;

    private StorageReference mStorageReference, sRef;

    private Context mContext;

    private Equipment mEquipment;

    private Profile mProfile;

    private ScreensNavigator mScreensNavigator;

    private EquipmentDaoImpl mEquipmentImplInsert;

    private EquipmentDao mEquipmentDao;

    private ProfileDaoImpl mProfileImplInsert;

    private ProfileDao mProfileDao;

    private boolean isEditProfile;

    private boolean isEditEquipment;

    private SessionManager mSession;

    private ThreadExecutorInsertEquipment mExecutorEquipmentInsert;

    private ThreadExecutorUpdateEquipment mExecutorEquipmentUpdate;

    private ThreadExecutorInsertProfile mExecutorProfileInsert;

    private ProgressBar mProgressBarPictureUpload;

    private OnFinishActivityListener mOnFinishActivityListener;

    /**
     * Constructor called from equipment registration
     *
     * @param filePath
     * @param idKey
     * @param context
     * @param storageReference
     * @param contentResolver
     * @param progressBarPictureUpload
     * @param implInsert
     * @param equipment
     */
    public UploadImageFireBase(Uri filePath, String idKey, Context context,
                               StorageReference storageReference, ContentResolver contentResolver,
                               ProgressBar progressBarPictureUpload,
                               EquipmentDaoImpl implInsert, Equipment equipment,
                               OnFinishActivityListener onFinishActivityListener) {
        Log.v(TAG, "Inside constructor UploadImageFireBase()");

        uid = idKey;
        mFilePath = filePath;
        mContext = context;
        mEquipmentImplInsert = implInsert;
        mEquipment = equipment;
        mOnFinishActivityListener = onFinishActivityListener;
        mStorageReference = storageReference;
        mContentResolver = contentResolver;
        mProgressBarPictureUpload = progressBarPictureUpload;

    }

    /**
     * Constructor called from equipment details edit
     *
     * @param filePath
     * @param idKey
     * @param context
     * @param storageReference
     * @param contentResolver
     * @param progressBarPictureUpload
     * @param equipmentDao
     * @param equipment
     * @param editEquipment
     */
    public UploadImageFireBase(Uri filePath, String idKey, Context context,
                               StorageReference storageReference, ContentResolver contentResolver,
                               ProgressBar progressBarPictureUpload,
                               EquipmentDao equipmentDao, Equipment equipment, boolean editEquipment) {
        Log.v(TAG, "Inside constructor UploadImageFireBase()");

        uid = idKey;
        mFilePath = filePath;
        mContext = context;
        mEquipmentDao = equipmentDao;
        mEquipment = equipment;
        isEditEquipment = editEquipment;
        mStorageReference = storageReference;
        mContentResolver = contentResolver;
        mProgressBarPictureUpload = progressBarPictureUpload;

    }

    /**
     * Constructor called from profile registration
     *
     * @param filePath
     * @param context
     * @param storageReference
     * @param contentResolver
     * @param progressBarPictureUpload
     * @param screensNavigator
     * @param implInsert
     * @param profile
     * @param session
     */
    public UploadImageFireBase(Uri filePath, Context context,
                               StorageReference storageReference, ContentResolver contentResolver,
                               ProgressBar progressBarPictureUpload, ScreensNavigator screensNavigator,
                               ProfileDaoImpl implInsert, Profile profile, SessionManager session) {
        Log.v(TAG, "Inside constructor UploadImageFireBase()");

        mFilePath = filePath;
        mContext = context;
        mProfileImplInsert = implInsert;
        mProfile = profile;
        mSession = session;
        mScreensNavigator = screensNavigator;
        mStorageReference = storageReference;
        mContentResolver = contentResolver;
        mProgressBarPictureUpload = progressBarPictureUpload;

    }

    /**
     * Constructor called from profile detail edit
     *
     * @param filePath
     * @param context
     * @param storageReference
     * @param contentResolver
     * @param progressBarPictureUpload
     * @param profileDao
     * @param profile
     * @param editProfile
     */
    public UploadImageFireBase(Uri filePath, Context context,
                               StorageReference storageReference, ContentResolver contentResolver,
                               ProgressBar progressBarPictureUpload,
                               ProfileDao profileDao, Profile profile, boolean editProfile) {
        Log.v(TAG, "Inside constructor UploadImageFireBase()");

        mFilePath = filePath;
        mContext = context;
        mProfileDao = profileDao;
        mProfile = profile;
        isEditProfile = editProfile;
        mStorageReference = storageReference;
        mContentResolver = contentResolver;
        mProgressBarPictureUpload = progressBarPictureUpload;

    }

    /**
     * Method that uploads the image chosen by user to be saved on database and saves the
     * equipment or profile data depending on the parameters received
     */
    public void uploadImageAndSaveData(int flagUpload) {
        Log.v(TAG, "Inside method uploadImageAndSaveData()");

        if(mFilePath != null) {

            if (flagUpload == 1) {

                sRef = mStorageReference.child(Utils.STORAGE_PATH_UPLOADS +
                        uid + System.currentTimeMillis() +
                        "." + Utils.getFileExtension(mFilePath, mContentResolver));

            } else if (flagUpload == 0) {

                sRef = mStorageReference.child(Utils.STORAGE_PATH_UPLOADS_PROFILE +
                        mProfile.getId() + System.currentTimeMillis() +
                        "." + Utils.getFileExtension(mFilePath, mContentResolver));


            }

            UploadTask uploadTask = sRef.putFile(mFilePath);

            Task<Uri> urlTask = uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBarPictureUpload.setVisibility(View.VISIBLE);
                    mProgressBarPictureUpload.setProgress((int) progress);

                }

            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return sRef.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {

                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();

                        mProgressBarPictureUpload.setVisibility(View.INVISIBLE);

                        if (downloadUri != null) {
                            mUrlUploadedPicture = downloadUri.toString();
                        }

                    } else {

                        mUrlUploadedPicture = "";

                    }

                    /**
                     * If flagUpload is equal to 1 updates equipment picture
                     */
                    if (flagUpload == 1) {

                        /**
                         * If isEditEquipment equals to true updates the equipment edit
                         */
                        if (!isEditEquipment) {

                            mEquipment.setPicture(mUrlUploadedPicture);

                            executeInsertEquipment();

                        } else {

                            executeUpdateEquipment();

                        }

                    // If flagUpload is equal to 0 updates profile picture
                    } else if (flagUpload == 0) {
                        Log.v(TAG, "Inside method - else if - uploadImageAndSaveData() - key: " + mProfile.getId());
                        // If isEditProfile equals to true updates the profile edit
                        if (!isEditProfile) {
                            Log.v(TAG, "Inside method - IF - uploadImageAndSaveData() - uid: " + mProfile.getId());

                            mProfile.setPicture(mUrlUploadedPicture);

                            mSession.setProfileOnPrefs(mProfile);

                            executeInsertProfile();

                        } else {

                            mSession.setProfileOnPrefs(mProfile);

                            executeUpdateProfile();

                        }

                    }

                }

            });

        }

    }

    /**
     * Method inserts a new equipment
     */
    private void executeInsertEquipment() {

        mExecutorEquipmentInsert = new ThreadExecutorInsertEquipment(mEquipmentImplInsert, mEquipment);

        mExecutorEquipmentInsert.insertDataThread();

        showSuccessfulMessage();

        mOnFinishActivityListener.onFinishActivity();

    }

    /**
     * Method inserts a new profile
     */
    private void executeInsertProfile() {
        Log.v(TAG, "Inside method - executeInsertProfile()");

        mExecutorProfileInsert = new ThreadExecutorInsertProfile(mProfileImplInsert, mScreensNavigator, mProfile, mSession);

        showSuccessfulMessage();

        mExecutorProfileInsert.insertDataThread();

    }

    /**
     * Method updates the existing profile
     */
    private void executeUpdateProfile() {

        mProfileDao.updateProfile(mProfile);

        showSuccessfulMessage();

    }

    /**
     * Method updates the existing equipment
     */
    private void executeUpdateEquipment() {

        mExecutorEquipmentUpdate = new ThreadExecutorUpdateEquipment(mEquipmentDao, mEquipment);

        mExecutorEquipmentUpdate.updateDataThread();

        showSuccessfulMessage();

    }

    private void showSuccessfulMessage() {
        Toast.makeText(mContext, mContext.getResources().getString(R.string.message_save_successful), Toast.LENGTH_LONG).show();
    }

}
