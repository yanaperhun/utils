package com.zinier.entel.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zinier.entel.R;

/**
 * Created by janaperhun on 19.04.17.
 */

public class ItemGraphLayout extends FrameLayout {

    public ItemGraphLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ItemGraphLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ItemGraphLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(inflater.inflate(R.layout.layout_graph_item, null));

        TypedArray a = getContext().obtainStyledAttributes(attributeSet, R.styleable.ItemGraphLayout);
        String title = a.getString(R.styleable.ItemGraphLayout_itemGraphTitle);
        String value = a.getString(R.styleable.ItemGraphLayout_itemGraphValue);

        ((TextView) findViewById(R.id.tvTitle)).setText(title);
        ((TextView) findViewById(R.id.tvValue)).setText(value);

        a.recycle();

    }

    public void setValue(double value) {
        ((TextView) findViewById(R.id.tvValue)).setText(
                value % 1 == 0 ? String.valueOf(((int) value)) : String.valueOf(value));
    }
}
