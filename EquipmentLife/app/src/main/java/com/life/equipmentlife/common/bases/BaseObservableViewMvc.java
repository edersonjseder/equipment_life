package com.life.equipmentlife.common.bases;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BaseObservableViewMvc<ListenerType> extends BaseViewMvc
        implements ObservableViewMvc<ListenerType> {

    private Set<ListenerType> mListeners = new HashSet<>();

    @Override
    public void registerListener(ListenerType listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterListener(ListenerType listener) {
        mListeners.remove(listener);
    }

    public Set<ListenerType> getListeners() {

        return Collections.unmodifiableSet(mListeners);

    }
}
