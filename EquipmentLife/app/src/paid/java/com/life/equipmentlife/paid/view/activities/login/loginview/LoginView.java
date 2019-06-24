package com.life.equipmentlife.paid.view.activities.login.loginview;

import android.os.Bundle;

import com.life.equipmentlife.common.bases.ObservableViewMvc;

public interface LoginView extends ObservableViewMvc {

    void bindEmailReset(int configChangeFlag, String email);

    void showDialogForgotPassword(int configChangeFlag, String email);

    void saveInstanceState(Bundle bundle);

    void pause();

}
