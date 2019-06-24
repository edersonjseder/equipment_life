package com.life.equipmentlife.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.life.equipmentlife.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static final String STORAGE_PATH_UPLOADS = "equipment_pictures/";

    public static final String STORAGE_PATH_UPLOADS_PROFILE = "profile_pictures/";

    private static final String TAG = Utils.class.getSimpleName();

    // Class not instantiable
    private Utils(){}

    public static Uri buildImageUrl(String imageUrl) {

        Log.v(TAG, "Built Image URI " + imageUrl);

        Uri builtUri = Uri.parse(imageUrl);

        return builtUri;
    }

    public static String getFileExtension(Uri uri, ContentResolver contentResolver) {

        ContentResolver cR = contentResolver;

        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    public static String getPlainText(String text) {

        String[] split;
        String result = "";

        try {

            split = text.split("%2F|\\?");

            System.out.println(split);

            result = split[1];

        } catch (Exception ex) {
            ex.getMessage();
        }

        return result;
    }

    public static String getFormattedDate(Date dateRegistered) {

        String date = "";

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt_BR"));

            date = sdf.format(dateRegistered);

            System.out.println(date);

        } catch (Exception ex) {

        }

        return date;
    }

    public static String getFirstNameOnly(String profileName) {

        String [] name = profileName.split(" ");

        return name[0];

    }

    public static void closeKeyboard(Context context, View view) {

        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

}
