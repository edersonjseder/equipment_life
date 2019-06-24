package com.life.equipmentlife.model.executors;

import android.content.Context;

import com.life.equipmentlife.model.daoimpl.EquipmentDaoImpl;
import com.life.equipmentlife.model.pojo.Equipment;

import java.util.List;

public class ThreadExecutorDeleteEquipment implements Runnable {

    private EquipmentDaoImpl mEquipmentDaoImpl;

    private List<Equipment> mEquipments;

    private Context mContext;

    private int mPosition;

    public ThreadExecutorDeleteEquipment(EquipmentDaoImpl implDelete, Context context, List<Equipment> equipments, int position) {
        mEquipmentDaoImpl = implDelete;
        mEquipments = equipments;
        mContext = context;
        mPosition = position;
    }

    public void removeItemDataThread() {

        AppExecutors.getsInstance().getDiskIO().execute(this);

    }

    @Override
    public void run() {

        mEquipmentDaoImpl.deleteEquipment(mEquipments.get(mPosition));

    }

}
