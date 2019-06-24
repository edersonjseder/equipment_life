package com.life.equipmentlife.common.listener;

import android.widget.ImageButton;

import com.life.equipmentlife.model.pojo.Profile;

public interface OnEnterProfileEditListener {

    void goToProfileEdit(Profile profile, ImageButton btnProfileDetailsEdit);

}
