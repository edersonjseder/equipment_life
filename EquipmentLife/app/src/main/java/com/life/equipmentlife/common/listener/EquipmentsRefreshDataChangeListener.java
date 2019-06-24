package com.life.equipmentlife.common.listener;

import android.support.v4.widget.SwipeRefreshLayout;

import com.life.equipmentlife.model.pojo.Equipment;

import java.util.List;

public interface EquipmentsRefreshDataChangeListener {

    void onDataRefreshLoaded(List<Equipment> equipments, SwipeRefreshLayout mSwipeRefreshLayoutMain);

}
