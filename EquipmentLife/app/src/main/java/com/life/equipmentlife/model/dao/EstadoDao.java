package com.life.equipmentlife.model.dao;

import com.life.equipmentlife.common.listener.EstadoChangeListener;
import com.life.equipmentlife.model.pojo.Estado;

import java.util.List;

public interface EstadoDao {

    List<Estado> getEstadoList(EstadoChangeListener estadoListener);

}
