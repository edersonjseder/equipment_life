package com.life.equipmentlife.paid.view.activities.signup.signupfacebook.signupfacebookview;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.life.equipmentlife.common.bases.ObservableViewMvc;

public interface SignUpFacebookView extends ObservableViewMvc {

    void activityResult(int requestCode, int resultCode, @Nullable Intent data);

    void bindFireBaseAuth(FirebaseAuth auth);

}
