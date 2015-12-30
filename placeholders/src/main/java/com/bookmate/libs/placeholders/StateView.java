/**
 * Copyright (c) 2015 Bookmate.
 * All Rights Reserved.
 * <p>
 * Author: Dmitry Gordeev <netimen@dreamindustries.co>
 * Date:   28.12.15
 */
package com.bookmate.libs.placeholders;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bookmate.libs.base.Utils;

public class StateView extends TextView {

    private final Params params;
    private int state;

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        params = loadAttributes(context, attrs);
        showState(0);
    }

    public void showState(int state) {
        setVisibility(VISIBLE);
        setState(state);
    }

    public int getState() {
        return state;
    }

    protected void setState(int state) {
        if (state >= params.statesCount())
            throw new IllegalArgumentException("incorrect state " + state + ". States count " + params.statesCount());

        this.state = state;
        setText(getContext().getResources().getText(state < params.captions.length ? params.captions[state] : 0, ""));
        showIcon(state < params.icons.length ? params.icons[state] : 0);
    }

    protected void showIcon(int iconId) {
        setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0);
    }

    protected static Params loadAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateView);
        int[] captions = Utils.loadResourceIds(a, "string", R.styleable.StateView_statesCaptions, context.getResources().getResourceEntryName(R.attr.statesCaptions));
        int[] icons = Utils.loadResourceIds(a, "drawable", R.styleable.StateView_statesIcons, context.getResources().getResourceEntryName(R.attr.statesIcons));
        a.recycle();

        return new Params(captions, icons);
    }

    protected static class Params {
        @StringRes
        public final int[] captions;

        @StringRes
        public final int[] icons;

        public Params(int[] captions, int[] icons) {
            this.captions = captions == null ? new int[0] : captions;
            this.icons = icons == null ? new int[0] : icons;
        }

        int statesCount() {
            return Math.max(captions.length, icons.length);
        }
    }
}
