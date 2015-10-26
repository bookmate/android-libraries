package com.bookmate.libs.placeholders;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class LoadingView extends LinearLayout {

    ProgressBar spinner;
    TextView loadingText;

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        @StringRes final int captionLoadingResId = a.getResourceId(R.styleable.LoadingView_captionLoading, R.string.loading);
        a.recycle();

        inflate(getContext(), R.layout.view_loading, this);
        spinner = (ProgressBar) findViewById(R.id.loading_view_spinner);
        loadingText = (TextView) findViewById(R.id.loading_view_text);
        loadingText.setText(captionLoadingResId);

        setLoadingMode(Mode.SPINNER);
    }

    @SuppressWarnings("SameParameterValue")
    public void setLoadingMode(Mode mode) {
        switch (mode) {
            case SPINNER:
                spinner.setVisibility(VISIBLE);
                loadingText.setVisibility(GONE);
                break;
            case TEXT:
                spinner.setVisibility(GONE);
                loadingText.setVisibility(VISIBLE);
                break;
        }
    }

    public void setLoadingTextColor(int textColor) {
        loadingText.setTextColor(textColor);
    }

    enum Mode {
        SPINNER, TEXT
    }
}
