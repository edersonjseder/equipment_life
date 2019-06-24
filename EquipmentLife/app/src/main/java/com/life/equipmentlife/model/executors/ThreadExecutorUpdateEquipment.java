package com.life.equipmentlife.model.executors;

import com.life.equipmentlife.model.dao.EquipmentDao;
import com.life.equipmentlife.model.pojo.Equipment;

public class ThreadExecutorUpdateEquipment implements Runnable {

    private EquipmentDao mEquipmentDao;

    private Equipment mEquipment;

    public ThreadExecutorUpdateEquipment(EquipmentDao implInsert, Equipment equipment) {
        mEquipmentDao = implInsert;
        mEquipment = equipment;
    }

    public void updateDataThread() {

        AppExecutors.getsInstance().getDiskIO().execute(this);

    }

    @Override
    public void run() {

        mEquipmentDao.updateEquipment(mEquipment);

    }

}
