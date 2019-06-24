package com.life.equipmentlife.model.executors;

import com.life.equipmentlife.model.daoimpl.EquipmentDaoImpl;
import com.life.equipmentlife.model.pojo.Equipment;

public class ThreadExecutorInsertEquipment implements Runnable {

    private EquipmentDaoImpl mEquipmentDaoImpl;

    private Equipment mEquipment;

    public ThreadExecutorInsertEquipment(EquipmentDaoImpl implInsert, Equipment equipment) {
        mEquipmentDaoImpl = implInsert;
        mEquipment = equipment;
    }

    public void insertDataThread() {

        AppExecutors.getsInstance().getDiskIO().execute(this);

    }

    @Override
    public void run() {

        mEquipmentDaoImpl.insertEquipment(mEquipment);

    }

}
