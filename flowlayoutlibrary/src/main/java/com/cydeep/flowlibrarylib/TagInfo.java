package com.cydeep.flowlibrarylib;

import android.graphics.Rect;

public class TagInfo {
    public static final int TYPE_TAG_USER = 0;//可以移动的标签
    public static final int TYPE_TAG_SERVICE = 1;//不能移动的标签，一般是默认放在最前面的标签
    public String tagId;
    public String tagName;
    public Rect rect = new Rect();
    public int childPosition;
    public int dataPosition = -1;
    public int type;
}
