package com.life.equipmentlife.paid.view.activities.base;

import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.life.equipmentlife.application.FirebaseHandler;
import com.life.equipmentlife.model.database.DatabaseRef;
import com.life.equipmentlife.model.session.SessionManager;
import com.life.equipmentlife.paid.controller.ControllerCompositionRoot;

public class BaseFragment extends Fragment {

    private ControllerCompositionRoot mControllerCompositionRoot;
    private DatabaseRef databaseRef;
    private SessionManager session;

    public ControllerCompositionRoot getCompositionRoot() {

        databaseRef = new DatabaseRef();
        session = new SessionManager(getContext());

        if (mControllerCompositionRoot == null) {

            mControllerCompositionRoot =
                    new ControllerCompositionRoot(
                            ((FirebaseHandler) requireActivity().getApplication()).getCompositionCep(),
                            databaseRef, session, requireActivity());

        }

        return mControllerCompositionRoot;
    }

    public FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    public FirebaseUser getFirebaseUser() {
        return getFirebaseAuth().getCurrentUser();
    }

}
