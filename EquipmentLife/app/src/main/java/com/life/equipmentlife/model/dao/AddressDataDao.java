package com.life.equipmentlife.model.dao;

import com.life.equipmentlife.common.listener.AddressDataChangeCepListener;
import com.life.equipmentlife.model.pojo.AddressData;

public interface AddressDataDao {

    AddressData fetchAddressDataByCep(String cep, AddressDataChangeCepListener dataChangeCepListener);

}
