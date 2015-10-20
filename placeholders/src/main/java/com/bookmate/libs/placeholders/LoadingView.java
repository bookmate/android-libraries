package com.bookmate.libs.placeholders;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class LoadingView extends LinearLayout {

    ProgressBar spinner;
    TextView loadingText;


    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.view_loading, this);
//        spinner = findViewById(R.id.s)
    }

    public LoadingView(Context context) {
        super(context);
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
