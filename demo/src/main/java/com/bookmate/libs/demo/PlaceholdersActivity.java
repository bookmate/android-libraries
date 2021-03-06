/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   20.10.15
 */
package com.bookmate.libs.demo;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;

import com.bookmate.libs.placeholders.LoaderStateView;
import com.bookmate.libs.placeholders.StateView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_placeholders)
public class PlaceholdersActivity extends Activity {
    @ViewById
    LoaderStateView loaderView;

    @ViewById
    FrameLayout container;

    @ViewById
    StateView stateView;

    @AfterViews
    void ready() {
        stateView.showState("t");
        stateView.setOnClickListener(new View.OnClickListener() {
            public int state;

            @Override
            public void onClick(View v) {
//                stateView.showState(++state);
            }
        });
//        EmptyView.setNetworkErrorLogic(new EmptyView.NetworkErrorLogic() {
//            @Override
//            public boolean isServerError(Exception exception) {
//                return exception instanceof RuntimeException;
//            }
//        });
//        loaderView = new LoaderView(this);
//        container.addView(loaderView);

//        loaderView.setOnRefreshClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                load();
//            }
//        });
    }

    @Click
    void load() {
//        loaderView.showLoading();
    }

    @Click
    void noData() {
//        loaderView.showNoData();
    }

    @Click
    void networkError() {
//        loaderView.showNetworkError(new Exception());
    }

    @Click
    void serverError() {
//        loaderView.showNetworkError(new RuntimeException());
    }
}
