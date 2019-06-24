package com.life.equipmentlife.common.listener;

import com.life.equipmentlife.model.pojo.Estado;

import java.util.List;

public interface EstadoChangeListener {

    void onEstadoLoaded(List<Estado> estados);

}
