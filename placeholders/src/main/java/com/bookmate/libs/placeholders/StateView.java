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
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bookmate.libs.base.Utils;

import java.util.Arrays;
import java.util.List;

public class StateView extends TextView {

    private final Params params;
    private String state;

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        params = loadAttributes(context, attrs);
    }

    public void showState(String state) {
        setVisibility(VISIBLE);
        setState(state);
    }

    public String getState() {
        return state;
    }

    protected void setState(String state) {
        this.state = state;
        final int stateIndex = params.states.indexOf(state);
        if (stateIndex < 0)
            throw new IllegalArgumentException("incorrect state: " + state + ". Available states: " + params.states);

        setState(stateIndex);
    }

    protected void setState(int stateIndex) {
        setText(getContext().getResources().getText(stateIndex < params.captions.length ? params.captions[stateIndex] : 0, ""));
        showIcon(stateIndex < params.icons.length ? params.icons[stateIndex] : 0);
    }

    protected void showIcon(int iconId) {
        setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0);
    }

    protected static Params loadAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateView);
        int[] captions = Utils.loadResourceIds(a, "string", R.styleable.StateView_statesCaptions, context.getResources().getResourceEntryName(R.attr.statesCaptions));
        int[] icons = Utils.loadResourceIds(a, "drawable", R.styleable.StateView_statesIcons, context.getResources().getResourceEntryName(R.attr.statesIcons));
        CharSequence[] states = a.getTextArray(R.styleable.StateView_states);
        a.recycle();

        return new Params(captions, icons, states);
    }

    protected static class Params {
        @StringRes
        public final int[] captions;

        @StringRes
        public final int[] icons;

        public final List<CharSequence> states;

        public Params(@Nullable int[] captions, @Nullable int[] icons, @Nullable CharSequence[] states) {
            if (states == null)
                throw new IllegalArgumentException("no states specified");

            this.states = Arrays.asList(states);
            this.captions = captions == null ? new int[0] : captions;
            this.icons = icons == null ? new int[0] : icons;

            if (states.length > Math.max(this.captions.length, this.icons.length))
                throw new IllegalArgumentException("no states or more states than icons and captions");
        }
    }
}
