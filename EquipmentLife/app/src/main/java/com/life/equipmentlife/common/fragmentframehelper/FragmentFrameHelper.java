package com.life.equipmentlife.common.fragmentframehelper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentFrameHelper {

    private final FragmentFrameWrapper mFragmentFrameWrapper;
    private final FragmentManager mFragmentManager;

    public FragmentFrameHelper(FragmentFrameWrapper fragmentFrameWrapper, FragmentManager fragmentManager) {
        mFragmentFrameWrapper = fragmentFrameWrapper;
        mFragmentManager = fragmentManager;
    }

    public void replaceFragmentAndClearBackstack(Fragment newFragment) {
        replaceFragment(newFragment, false, true);
    }

    private void replaceFragment(Fragment newFragment, boolean addToBackStack, boolean clearBackStack) {
        if (clearBackStack) {
            if (mFragmentManager.isStateSaved()) {
                // If the state is saved we can't clear the back stack. Simply not doing this, but
                // still replacing fragment is a bad idea. Therefore we abort the entire operation.
                return;
            }
            // Remove all entries from back stack
            mFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction ft = mFragmentManager.beginTransaction();

        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        // Change to a new fragment
        ft.replace(getFragmentFrameId(), newFragment, null);

        if (mFragmentManager.isStateSaved()) {
            // We acknowledge the possibility of losing this transaction if the app undergoes
            // save&restore flow after it is committed.
            ft.commitAllowingStateLoss();
        } else {
            ft.commit();
        }
    }

    private int getFragmentFrameId() {
        return mFragmentFrameWrapper.getFragmentFrame().getId();
    }

}
