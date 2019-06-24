package com.life.equipmentlife.common.listener;

import com.life.equipmentlife.model.pojo.Equipment;

import java.util.List;

public interface EquipmentsDataChangeListener {

    void onDataLoaded(List<Equipment> equipments);

}
