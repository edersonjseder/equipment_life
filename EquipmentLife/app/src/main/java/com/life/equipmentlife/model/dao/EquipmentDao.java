package com.life.equipmentlife.model.dao;

import android.support.v4.widget.SwipeRefreshLayout;

import com.life.equipmentlife.common.listener.EquipmentDataChangeSerialListener;
import com.life.equipmentlife.common.listener.EquipmentsRefreshDataChangeListener;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.common.listener.EquipmentsDataChangeListener;

import java.util.List;

public interface EquipmentDao {

    List<Equipment> fetchAllEquipments(EquipmentsDataChangeListener mEquipmentsDataChangeListener);

    List<Equipment> fetchAllRefreshedEquipments(EquipmentsRefreshDataChangeListener mEquipmentsRefreshDataChangeListener,
                                                SwipeRefreshLayout mSwipeRefreshLayoutMain);

    Equipment fetchEquipmentBySerial(String serialNumber, String uidKey, EquipmentDataChangeSerialListener listener);

    List<Equipment> fetchEquipmentByStatus(String status, String sessionUidKey, EquipmentsDataChangeListener mEquipmentsDataChangeListener);

    void insertEquipment(Equipment equipment);

    void updateEquipment(Equipment equipment);

    void deleteEquipment(Equipment equipment);

}
