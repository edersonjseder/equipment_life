package com.life.equipmentlife.paid.view.activities.equipment.details.detailsview;

import com.life.equipmentlife.common.bases.ObservableViewMvc;
import com.life.equipmentlife.model.pojo.Equipment;

public interface EquipmentDetailsView extends ObservableViewMvc {

    void bindEquipment(Equipment equipment);

    void showProgressIndication();

    void hideProgressIndication();

}
