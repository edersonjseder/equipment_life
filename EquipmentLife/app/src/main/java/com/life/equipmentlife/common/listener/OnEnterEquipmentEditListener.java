package com.life.equipmentlife.common.listener;

import android.widget.ImageButton;

import com.life.equipmentlife.model.pojo.Equipment;

public interface OnEnterEquipmentEditListener {

    void goToEquipmentEdit(Equipment equipment, ImageButton btnEquipmentDetailsEdit);

}
