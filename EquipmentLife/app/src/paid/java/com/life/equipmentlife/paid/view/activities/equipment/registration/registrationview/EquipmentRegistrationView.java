package com.life.equipmentlife.paid.view.activities.equipment.registration.registrationview;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.life.equipmentlife.common.bases.ObservableViewMvc;

public interface EquipmentRegistrationView extends ObservableViewMvc<EquipmentRegistrationView.OnNavigationUp> {

    interface OnNavigationUp {
        void onNavigateUpClicked();
    }

    void onActivityResult(int requestCode, int requestImage, int resultCode, @Nullable Intent data);

}
