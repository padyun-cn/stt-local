package com.mon.ui.floating;

import android.content.res.Resources;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.mon.ui.R;

import org.jetbrains.annotations.NotNull;

/**
 * Created by daiepngfei on 2021-01-19
 */
public class OverlayAlert extends OverlayDialog {
    private Builder builder = null;
    public static Builder build(OverlayContext context) {
        return new OverlayAlert.Builder(context);
    }

    OverlayAlert(OverlayContext context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        setContentView(R.layout.overlay_dg_core_common_base);
    }

    enum WhichButton {
        POSITIVE, NEGATIVE, NATURAL, EXCLUSIVE_NATURAL
    }

    public static class Builder {
        private String title;
        private boolean divider;
        private String msg;
        private Pair<String, OnClickListener> positive;
        private Pair<String, OnClickListener> negative;
        private Pair<String, OnClickListener> natural;
        private Pair<String, OnClickListener> exNatural;
        private OverlayContext context;

        public Builder(OverlayContext context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            return setTitle(title, true);
        }

        public Builder setTitle(int title){
            return setTitle(title, true);
        }

        private Resources getResources() {
            return this.context.getContext().getResources();
        }

        @NotNull
        private String getString(int title) {
            return getResources().getString(title);
        }

        public Builder setTitle(int title, boolean divider) {
            return setTitle(getString(title), divider);
        }

        public Builder setTitle(String title, boolean divider) {
            this.title = title;
            this.divider = divider;
            return this;
        }

        public Builder setMessage(int message) {
            return setMessage(getString(message));
        }

        public Builder setMessage(String message) {
            this.msg = message;
            return this;
        }

        public Builder setPositiveButton(int label, OnClickListener l) {
            return setPositiveButton(getString(label), l);
        }

        public Builder setPositiveButton(String label, OnClickListener l) {
            positive = new Pair<>(label, l);
            return this;
        }

        public Builder setNegativeButton(int label, OnClickListener l) {
            return setNegativeButton(getString(label), l);
        }

        public Builder setNegativeButton(String label, OnClickListener l) {
            negative = new Pair<>(label, l);
            return this;
        }

        public Builder setNaturalButton(int label, OnClickListener l) {
            return setNaturalButton(getString(label), l);
        }

        public Builder setNaturalButton(String label, OnClickListener l) {
            natural = new Pair<>(label, l);
            return this;
        }

        public Builder setExclusiveNaturalButton(int label, OnClickListener l) {
            return setExclusiveNaturalButton(getString(label), l);
        }

        public Builder setExclusiveNaturalButton(String label, OnClickListener l) {
            exNatural = new Pair<>(label, l);
            return this;
        }

        OverlayAlert create() {
            OverlayAlert overlayAlert = new OverlayAlert(context);
            overlayAlert.builder = this;
            overlayAlert.create();
            return overlayAlert;
        }

        public void show() {
            create().show();
        }

    }

    @Override
    protected void onAttachToWindow() {
        if (builder != null) {

            setTitle(builder.title, builder.divider);
            setMessage(builder.msg);

            if(builder.positive != null) {
                setPositiveButton(builder.positive.first, builder.positive.second);
            }
            if(builder.natural != null) {
                setNaturalButton(builder.natural.first, builder.natural.second);
            }
            if(builder.negative != null) {
                setNegativeButton(builder.negative.first, builder.negative.second);
            }
            if(builder.exNatural != null) {
                setExclusiveNaturalButton(builder.exNatural.first, builder.exNatural.second);
            }

        }
    }

    OverlayAlert setTitle(String title) {
        return setTitle(title, true);
    }

    private OverlayAlert setTitle(String title, boolean divider) {
        findViewById(R.id.divider_line).setVisibility(divider ? View.VISIBLE : View.GONE);
        return setTextView(R.id.title, title);
    }

    private OverlayAlert setTextView(int id, String text) {
        TextView button = (TextView) findViewById(id);
        button.setText(text);
        button.setVisibility(View.VISIBLE);
        return this;
    }

    private void setMessage(String message) {
        setTextView(R.id.msg, message);
    }

    private void setPositiveButton(String label, OnClickListener l) {
        setButton(R.id.positive, true, WhichButton.POSITIVE, label, l);
    }

    private void setNegativeButton(String label, OnClickListener l) {
        setButton(R.id.negative, true, WhichButton.NEGATIVE, label, l);
    }

    private void setExclusiveNaturalButton(String label, OnClickListener l) {
        setButton(R.id.natural, false, WhichButton.EXCLUSIVE_NATURAL, label, l);
    }

    private void setNaturalButton(String label, OnClickListener l) {
        findViewById(R.id.inner_natural).setVisibility(View.VISIBLE);
        findViewById(R.id.divider_line_inner_natural).setVisibility(View.VISIBLE);
        setButton(R.id.inner_natural, true, WhichButton.NATURAL, label, l);
    }

    private void setButton(int id, boolean showGroup, WhichButton which, String label, OnClickListener l) {
        if (showGroup) showButtonGroup();
        else showExcluesiveNaturalButton();
        TextView button = (TextView) findViewById(id);
        button.setText(label);
        button.setOnClickListener(v -> {
            l.onClick(OverlayAlert.this, which.ordinal());
            onButtonClicked(which);
        });
    }

    protected void onButtonClicked(WhichButton which) {
    }


    private void showButtonGroup() {
        findViewById(R.id.button_group).setVisibility(View.VISIBLE);
        findViewById(R.id.natural).setVisibility(View.GONE);
    }

    private void showExcluesiveNaturalButton() {
        findViewById(R.id.natural).setVisibility(View.VISIBLE);
        findViewById(R.id.button_group).setVisibility(View.GONE);
    }


}
