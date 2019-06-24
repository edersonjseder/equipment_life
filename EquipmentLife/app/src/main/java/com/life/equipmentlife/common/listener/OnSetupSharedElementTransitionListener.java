package com.life.equipmentlife.common.listener;

import com.life.equipmentlife.common.transition.MorphTransition;

public interface OnSetupSharedElementTransitionListener {
    void setupSharedElementTransitions(MorphTransition sharedEnter, MorphTransition sharedReturn);
    void dismiss();
}
