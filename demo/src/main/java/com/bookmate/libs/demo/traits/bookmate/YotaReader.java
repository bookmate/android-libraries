/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p/>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   19.08.15
 */
package com.bookmate.libs.demo.traits.bookmate;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EService;

@EService
public class YotaReader extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @AfterInject
    void afterInject() {

    }
}
