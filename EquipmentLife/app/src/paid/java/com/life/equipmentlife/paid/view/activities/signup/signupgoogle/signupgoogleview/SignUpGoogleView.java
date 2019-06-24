package com.life.equipmentlife.paid.view.activities.signup.signupgoogle.signupgoogleview;

import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.common.bases.ObservableViewMvc;

public interface SignUpGoogleView extends ObservableViewMvc {

    void onHandleGoogleResult(Intent data);

    void bindFireBaseAuth(FirebaseAuth auth);

    void showProgressbarLoading();

    void hideProgressbarLoading();

}
