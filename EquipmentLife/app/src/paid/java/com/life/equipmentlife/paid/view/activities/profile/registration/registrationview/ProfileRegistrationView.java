package com.life.equipmentlife.paid.view.activities.profile.registration.registrationview;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.life.equipmentlife.common.bases.ObservableViewMvc;
import com.life.equipmentlife.model.pojo.Profile;

public interface ProfileRegistrationView extends ObservableViewMvc {

    void bindProfile(Profile profile, String idKey);

    void loadProfileDefault();

    void onActivityResult(int requestCode, int requestImage, int resultCode, @Nullable Intent data);

}
