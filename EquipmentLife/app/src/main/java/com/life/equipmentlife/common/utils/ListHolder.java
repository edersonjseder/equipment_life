package com.life.equipmentlife.common.utils;

import com.life.equipmentlife.model.pojo.Equipment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListHolder implements Serializable {

    private List<Equipment> equipmentList;

    public ListHolder() {

        equipmentList = new ArrayList<>();

    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

}
