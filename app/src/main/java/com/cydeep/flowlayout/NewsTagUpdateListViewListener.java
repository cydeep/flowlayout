package com.cydeep.flowlayout;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.cydeep.flowlayout.base.OnUpdateListUIListener;
import com.cydeep.flowlayout.base.ViewHolder;
import com.cydeep.flowlibrarylib.FlowLayout;
import com.cydeep.flowlibrarylib.TagInfo;
import com.cydeep.flowlibrarylib.listener.OnTagClickListener;
import com.cydeep.flowlibrarylib.listener.OnTagSelectListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyu on 16/6/6.
 */
public class NewsTagUpdateListViewListener extends OnUpdateListUIListener {

    private SparseArray<ArrayList<TagInfo>> sparseArray;

    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }

    private OnTagClickListener onTagClickListener;

    @Override
    public void onUpdateUI(final Context context, final ViewHolder holder, final int position) {
        final ArrayList<TagInfo> tagInfos = sparseArray.get(position);
        if (tagInfos != null) {
            final FlowLayout newsTag = (FlowLayout) holder.getConvertView();
            newsTag.setTags(tagInfos);
            newsTag.setOnTagClickListener(new OnTagClickListener() {
                @Override
                public void onTagClick(TagInfo tagInfo) {
                    if (onTagClickListener != null) {
                        onTagClickListener.onTagClick(tagInfo);
                    }
                }

                @Override
                public void onTagDelete(TagInfo tagInfo) {

                }
            });
//            newsTag.setOnTagSelectListener(new OnTagSelectListener() {
//                @Override
//                public void onTagSelect(boolean isSelect, TextView view) {
//                    if (isSelect) {
//                        NewsTagUtils.setSelectTag(view);
//                    } else {
//                        NewsTagUtils.setUnSelectTag(view);
//
//                    }
//                }
//
//                @Override
//                public void onSetEditDefaultColor(TextView view) {
//
//                }
//            });
        }
    }

    @Override
    public int getCount() {
        return sparseArray.size();
    }

    @Override
    public void setData(List list) {

    }

    public void setData(SparseArray<ArrayList<TagInfo>> sparseArray) {
        this.sparseArray = sparseArray;
    }

    @Override
    public void setCount(int count) {
        mCount = count;
    }

    @Override
    public View initLayout(Context context, int position) {
        return new FlowLayout(context);
    }
}
