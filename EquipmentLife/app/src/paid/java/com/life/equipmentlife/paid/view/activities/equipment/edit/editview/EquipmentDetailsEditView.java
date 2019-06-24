package com.life.equipmentlife.paid.view.activities.equipment.edit.editview;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.life.equipmentlife.common.bases.ObservableViewMvc;
import com.life.equipmentlife.model.pojo.Equipment;

public interface EquipmentDetailsEditView extends ObservableViewMvc {

    void bindEquipment(Equipment equipment);

    void onActivityResult(int requestCode, int requestImage, int resultCode, @Nullable Intent data);

}
