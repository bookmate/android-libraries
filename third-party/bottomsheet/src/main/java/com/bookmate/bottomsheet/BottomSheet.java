package com.bookmate.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * Created by Defuera
 * based on https://github.com/soarcn/BottomSheet
 */
public class BottomSheet extends Dialog implements DialogInterface {

    private ListView list;
    private GridView grid;
    private Builder builder;
    private View container;

    public BottomSheet(Context context) {
        super(context, R.style.BottomSheet);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());

        widthMatchParent();
    }

    private void init(final Context context) {
        final com.bookmate.bottomsheet.ClosableSlidingLayout closableSlidingLayout = (ClosableSlidingLayout) View.inflate(context, R.layout.bottom_sheet_dialog, null);
        setContentView(closableSlidingLayout);

        initClosableSlidingLayout(closableSlidingLayout);
        setMinHeight();

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                list.startLayoutAnimation();

            }
        });

        closableSlidingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        if (builder.gridMode) {
            list.setVisibility(View.GONE);
            grid.setVisibility(View.VISIBLE);
            grid.setAdapter(builder.adapter);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (builder.clickListener != null) {
                        builder.clickListener.onClick(BottomSheet.this, position);
                    }
                    dismiss();
                }
            });
        } else {
            grid.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
            list.setAdapter(builder.adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (builder.clickListener != null) {
                        builder.clickListener.onClick(BottomSheet.this, position);
                    }
                    dismiss();
                }
            });
        }

        if (!builder.dim)
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        if (builder.dismissListener != null) {
            setOnDismissListener(builder.dismissListener);
        }

        if (builder.backgroundDrawableRes != 0)
            container.setBackgroundResource(builder.backgroundDrawableRes);

        initFooter();
    }

    private void setMinHeight() {
        if (builder.minHeight != 0)
            container.setMinimumHeight(builder.minHeight);
    }

    private void initClosableSlidingLayout(ClosableSlidingLayout closableSlidingLayout) {
        int[] location = new int[2];
        closableSlidingLayout.getLocationOnScreen(location);

        initTitle(closableSlidingLayout);

        list = (ListView) closableSlidingLayout.findViewById(R.id.list);
        grid = (GridView) closableSlidingLayout.findViewById(R.id.grid_view);
        container = findViewById(R.id.container);
        closableSlidingLayout.mTarget = builder.gridMode ? grid : list;

        closableSlidingLayout.setCollapsible(false);

        closableSlidingLayout.setSlideListener(new ClosableSlidingLayout.SlideListener() {
            @Override
            public void onClosed() {
                BottomSheet.this.dismiss();
            }

            @Override
            public void onOpened() {
            }
        });
    }

    private void initTitle(ClosableSlidingLayout closableSlidingLayout) {
        final TextView title = (TextView) closableSlidingLayout.findViewById(R.id.bottom_sheet_title);
        if (builder.title != null) {
            title.setVisibility(View.VISIBLE);
            title.setText(builder.title);
        }
    }

    private void initFooter() {
        if (builder.footerView == null)
            return;
        ((ViewGroup) findViewById(R.id.footer_container)).addView(builder.footerView);
        builder.footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.footerClickListener.onClick(v);
                dismiss();
            }
        });
    }

    private void widthMatchParent() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;

        if (getContext().getResources().getBoolean(R.bool.isPhone)) {
            TypedArray a = getContext().obtainStyledAttributes(new int[]{android.R.attr.layout_width});
            try {
                params.width = a.getLayoutDimension(0, ViewGroup.LayoutParams.MATCH_PARENT);
            } finally {
                a.recycle();
            }
        }

        getWindow().setAttributes(params);
    }

    /**
     * Constructor using a context for this builder and the BottomSheet it creates.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class Builder {

        private final Context context;
        private CharSequence title;
        private OnClickListener clickListener;
        private OnDismissListener dismissListener;
        private BaseAdapter adapter;
        private View footerView;
        private int minHeight;
        private View.OnClickListener footerClickListener;
        public boolean gridMode;
        private boolean dim;
        private int backgroundDrawableRes;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder sheet(BaseAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder gridSheet(BaseAdapter adapter) {
            this.adapter = adapter;
            this.gridMode = true;
            return this;
        }

        public Builder title(@StringRes int titleRes) {
            title = context.getText(titleRes);
            return this;
        }

        public Builder title(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder itemClickListener(@NonNull OnClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public BottomSheet show() {
            BottomSheet dialog = build();
            dialog.show();
            return dialog;
        }

        @SuppressLint("Override")
        public BottomSheet build() {
            BottomSheet dialog = new BottomSheet(context);
            dialog.builder = this;
            return dialog;
        }

        public Builder setOnDismissListener(@NonNull OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder footer(View footerVeiw, final View.OnClickListener footerClickListener) {
            this.footerView = footerVeiw;
            this.footerClickListener = footerClickListener;
            return this;
        }

        public Builder setMinHeight(int minHeight) {
            this.minHeight = minHeight;
            return this;
        }

        public Builder dim(boolean dim) {
            this.dim = dim;
            return this;
        }

        public Builder background(@DrawableRes int backgroundDrawableRes) {
            this.backgroundDrawableRes = backgroundDrawableRes;
            return this;
        }
    }
}
