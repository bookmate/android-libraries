/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   26.10.15
 */
package com.bookmate.libs.placeholders;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

class LoaderViewUiThreadHelper {
    private final LoaderView loaderView;
    private final Handler handler = new Handler(Looper.getMainLooper());

    LoaderViewUiThreadHelper(LoaderView loaderView) {
        this.loaderView = loaderView;
    }

    private void showLoading() {
        loaderView.setVisibility(View.VISIBLE);
        loaderView.emptyView.setVisibility(View.GONE);
        loaderView.loadingView.setVisibility(View.VISIBLE);
    }

    private void showNoData() {
        loaderView.setVisibility(View.VISIBLE);
        loaderView.emptyView.showNoData();
        loaderView.loadingView.setVisibility(View.GONE);
    }

    private void showNetworkError(@NonNull Exception exception) {
        loaderView.setVisibility(View.VISIBLE);
        loaderView.loadingView.setVisibility(View.GONE);
        loaderView.emptyView.showNetworkError(exception);
    }

    private void hide(boolean animate) {
        if (animate) {
            loaderView.fadeAnimator.animateVisibility(loaderView, false);
        } else
            loaderView.setVisibility(View.GONE);
    }

    public void showLoadingOnUiThread() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread())
            showLoading();
        else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showLoading();
                }
            });
    }

    public void showNoDataOnUiThread() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread())
            showNoData();
        else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showNoData();
                }
            });
    }

    public void showNetworkErrorOnUiThread(final Exception exception) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread())
            showNetworkError(exception);
        else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showNetworkError(exception);
                }
            });
    }

    public void showToastOnUiThread(@StringRes final int messageResId) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread())
            Toast.makeText(loaderView.getContext(), messageResId, Toast.LENGTH_SHORT).show();
        else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(loaderView.getContext(), messageResId, Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void hideOnUiThread(final boolean animate) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread())
            hide(animate);
        else
            handler.post(new Runnable() {
                @Override
                public void run() {
                    hide(animate);
                }
            });
    }
}
