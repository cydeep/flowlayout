package com.cydeep.flowlibrarylib.listener;

import com.cydeep.flowlibrarylib.TagInfo;

public interface OnTagClickListener {
    void onTagClick(TagInfo tagInfo);

    void onTagDelete(TagInfo tagInfo);
}
