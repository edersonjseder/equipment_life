package com.life.equipmentlife.common.listener;

import com.life.equipmentlife.model.pojo.Equipment;

public interface OnEquipmentItemSelectedListener {

    // called when user selects a Equipment from the list
    void onEquipmentItemSelected(Equipment equipment);

}
