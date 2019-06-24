package com.life.equipmentlife.paid.view.activities.profile.details.detailsview;

import com.life.equipmentlife.common.bases.ObservableViewMvc;
import com.life.equipmentlife.model.pojo.Profile;

public interface ProfileDetailsView extends ObservableViewMvc {

    void bindProfile(Profile profile);

}
