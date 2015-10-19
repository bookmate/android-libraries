package com.bookmate.libs.placeholders;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.bookmate.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by defuera on 29/12/14.
 */
@EViewGroup(R.layout.view_loader_checkbox)
public class LoaderCheckbox extends FrameLayout {

    @SuppressWarnings("UnusedDeclaration")
    private static final String LOG_TAG = LoaderCheckbox.class.getSimpleName();

    @ViewById
    CheckBox checkbox;

    @ViewById
    ProgressBar progressBar;

    public LoaderCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @AfterViews
    void ready() {
        setLoading(false);
    }

    void setLoading(boolean loading) {
        checkbox.setVisibility(loading ? INVISIBLE : VISIBLE);
        progressBar.setVisibility(loading ? VISIBLE : INVISIBLE);
    }

    public void setChecked(boolean checked) {
        progressBar.setVisibility(INVISIBLE);
        checkbox.setVisibility(VISIBLE);
        checkbox.setChecked(checked);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isChecked() {
        return checkbox.isChecked();
    }

    public void setCheckboxButton(int checkboxDrawableRes) {
        checkbox.setButtonDrawable(checkboxDrawableRes);
    }

//    public boolean isVisible() {
//        return getVisibility() == VISIBLE;
//    }
}
