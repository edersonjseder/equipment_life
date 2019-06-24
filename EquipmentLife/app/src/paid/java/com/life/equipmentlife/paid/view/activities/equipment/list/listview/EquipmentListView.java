package com.life.equipmentlife.paid.view.activities.equipment.list.listview;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;

import com.life.equipmentlife.common.bases.ObservableViewMvc;
import com.life.equipmentlife.model.pojo.Equipment;
import com.life.equipmentlife.model.pojo.Profile;

import java.util.List;

public interface EquipmentListView extends ObservableViewMvc {

    void bindEquipments(List<Equipment> equipments);

    void bindEquipmentsRefresh(List<Equipment> equipments, SwipeRefreshLayout mSwipeRefreshLayoutMain);

    void onSaveInstanceState(Bundle outState);

    void onViewStateRestored(Bundle outState);

}
