package com.cydeep.flowlibrarylib.listener;

import android.widget.TextView;


public interface OnTagSelectListener {
    void onTagSelect(boolean isSelect, TextView view);

    void onSetEditDefaultColor(TextView view);
}
