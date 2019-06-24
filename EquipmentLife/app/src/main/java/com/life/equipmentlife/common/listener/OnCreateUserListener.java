package com.life.equipmentlife.common.listener;

import com.life.equipmentlife.model.pojo.Profile;

public interface OnCreateUserListener {

    void createUserWithEmailAndPassword(Profile profile);

}
