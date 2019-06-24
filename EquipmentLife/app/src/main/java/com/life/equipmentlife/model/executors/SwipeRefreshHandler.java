package com.life.equipmentlife.model.executors;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;

import com.life.equipmentlife.common.listener.RefreshScreenListener;

public class SwipeRefreshHandler implements Runnable {

    private final Handler mHandler;
    private SwipeRefreshLayout mSwipeRefreshLayoutMain;
    private RefreshScreenListener mRefreshScreenListener;

    public SwipeRefreshHandler(Handler handler, SwipeRefreshLayout swipeRefreshLayoutMain, RefreshScreenListener listener) {

        mHandler = handler;
        mSwipeRefreshLayoutMain = swipeRefreshLayoutMain;
        mRefreshScreenListener = listener;

    }

    public void executeHandler() {
        mHandler.postDelayed(this, 2000);
    }

    @Override
    public void run() {

        mRefreshScreenListener.onRefreshScreen(mSwipeRefreshLayoutMain);

    }

}
