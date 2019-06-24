package com.life.equipmentlife.paid.view.activities.start.startview;

import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.common.bases.ObservableViewMvc;
import com.life.equipmentlife.model.pojo.Profile;

public interface StartScreenView extends ObservableViewMvc {

    void bindCredentialsData(int configChangeFlag, String email, String password, String confirmPassword, FirebaseAuth auth);

    void saveInstanceState(Bundle bundle);

    void showDialogCredentialsRegistration(int configChangeFlag, String email, String password, String confirmPassword);

    void pause();

}
