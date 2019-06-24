package com.life.equipmentlife.paid.view.activities.equipment.list.listitemview;

import com.life.equipmentlife.common.bases.ObservableViewMvc;
import com.life.equipmentlife.model.pojo.Equipment;

public interface EquipmentListItemView extends ObservableViewMvc {

    void bindEquipment(Equipment equipment);
}
