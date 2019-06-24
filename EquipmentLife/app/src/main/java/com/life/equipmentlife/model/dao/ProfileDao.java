package com.life.equipmentlife.model.dao;

import com.life.equipmentlife.common.listener.ProfileDataChangeEmailListener;
import com.life.equipmentlife.common.listener.ProfileDataChangeUidKeyListener;
import com.life.equipmentlife.model.pojo.Profile;

public interface ProfileDao {

    void insertProfile(Profile profile, String uidKey);

    void updateProfile(Profile profile);

    Profile fetchProfileById(String uid, ProfileDataChangeUidKeyListener dataChangeUidKeyListener);

    Profile fetchProfileByEmail(String email, ProfileDataChangeEmailListener dataChangeEmailListener);

}
