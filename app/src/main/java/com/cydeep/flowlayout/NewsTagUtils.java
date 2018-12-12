package com.cydeep.flowlayout;

import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by chenyu on 17/4/15.
 */

public class NewsTagUtils {


    public static void setSelectTag(TextView textView){
        textView.setBackgroundResource(R.drawable.tag_select);
        textView.setTextColor(0xffffffff);
    }
    public static void setUnSelectTag(TextView textView){
        textView.setBackgroundResource(R.drawable.round_rect_gray);
        textView.setTextColor(0xff645e66);
    }

    public static void setGraySelectTag(TextView textView){
        textView.setBackgroundResource(R.drawable.tag_uncheck);
        textView.setTextColor(0xffdbdcde);
    }
}
