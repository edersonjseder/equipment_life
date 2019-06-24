package com.life.equipmentlife.paid.view.activities.imagepicker.imagepickerview;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import com.life.equipmentlife.common.bases.ObservableViewMvc;

public interface ImagePickerView extends ObservableViewMvc {

    void cropImage(Uri sourceUri, ContentResolver contentResolver);

    void handleUCropResult(Intent data);

    void setResultOk(Uri imagePath);

    void setResultCancelled();

    Uri getCacheImagePath(String fileName);

}
