package com.cydeep.flowlayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.cydeep.flowlayout.base.BaseActivity;
import com.cydeep.flowlayout.base.ListViewAdapter;
import com.cydeep.flowlayout.base.TitleViews;
import com.cydeep.flowlibrarylib.FlowLayout;
import com.cydeep.flowlibrarylib.FlowLayoutUtils;
import com.cydeep.flowlibrarylib.TagInfo;
import com.cydeep.flowlibrarylib.ViewSizeUtil;
import com.cydeep.flowlibrarylib.listener.OnInterceptTouchEventListener;
import com.cydeep.flowlibrarylib.listener.OnTagClickListener;
import com.cydeep.flowlibrarylib.listener.OnTagSelectListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chenyu on 17/3/24.
 */

public class NewsTagsActivity extends BaseActivity {
    private List<TagInfo> recommondTagInfos = new ArrayList<>();
    private NewsTagUpdateListViewListener newsTagUpdateListViewListener = new NewsTagUpdateListViewListener();
    private FlowLayout flowLayout;
    private ArrayList<TagInfo> myTagInfos = new ArrayList<>();
    private ListViewAdapter listViewAdapter;
    private ListView recommendNewsTagListView;
    private String currentId;
    private boolean isEdit;
    private String lastId;
    private int childPosition;
    private ArrayList<String> pHomeNewsList;


    @Override
    protected void initTitle(TitleViews titleViews) {
        titleViews.right_container_right_image.setVisibility(View.VISIBLE);
        titleViews.right_container_right_image.setBackgroundResource(R.drawable.icon_nav_close_black);
        titleViews.right_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public static void StartNewsTagsActivity(Fragment fragment, int tagId, ArrayList<String> list, int RequestCode) {
        Intent intent = new Intent(fragment.getContext(), NewsTagsActivity.class);
        intent.putExtra("tagId", tagId);
        if (list != null) {
            intent.putExtra("tagList", list);
        }
        fragment.startActivityForResult(intent, RequestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        currentId = lastId = getIntent().getStringExtra("tagId");
        if (currentId == null) {
            currentId = lastId = "-1";
        }
        pHomeNewsList = getIntent().getStringArrayListExtra("tagList");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags_info);
//        View header = View.inflate(this, R.layout.header_listview_tags_info, null);

        recommendNewsTagListView = getView(R.id.news_tag_list_view);
//        recommendNewsTagListView.addHeaderView(header);
        listViewAdapter = new ListViewAdapter(this, newsTagUpdateListViewListener);

        flowLayout = getView(R.id.newsTag);
        flowLayout.setSelectTagId(currentId);

        flowLayout.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(TagInfo tagInfo) {
                System.out.println("-------------");
            }

            @Override
            public void onTagDelete(TagInfo tagInfo) {

            }
        });

        newsTagUpdateListViewListener.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(TagInfo tagInfo) {
                if (myTagInfos.size() >= 100) {
                    Toast.makeText(NewsTagsActivity.this, getString(R.string.tags_enough_tips, 100), Toast.LENGTH_SHORT).show();
                } else {
                    recommondTagInfos.remove(tagInfo);
                    setData();
                    flowLayout.addTag(tagInfo, isEdit);
                    listViewAdapter.notifyDataSetChanged();
//                    recommendNewsTagListView.setSelection(0);
                    TagInfo info = myTagInfos.get(myTagInfos.size() - 1);
                    currentId = info.tagId;
                    childPosition = info.childPosition;
                    setMyTagNum();
                }

            }

            @Override
            public void onTagDelete(TagInfo tagInfo) {

            }
        });
        updateMyTagUi();
        getRecommendTagInfos();
        recommendNewsTagListView.setAdapter(listViewAdapter);
    }

    private void updateMyTagUi() {
        String[] tagsDefault = getResources().getStringArray(R.array.tags_default);
        String[] tagsRecommend = getResources().getStringArray(R.array.tags_recommend);
        myTagInfos.addAll(addTags("fix", tagsDefault, TagInfo.TYPE_TAG_SERVICE));
        myTagInfos.addAll(addTags("default", tagsRecommend, TagInfo.TYPE_TAG_USER));
        flowLayout.setTags(myTagInfos);
        viewHolder
                .setVisible(R.id.tag_edit, true)
                .setOnClickListener(R.id.tag_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((TextView) v).getText().toString().equals(getResources().getString(R.string.edit))) {
                            if (flowLayout.getSelectButton() != null) {
                                NewsTagUtils.setUnSelectTag(flowLayout.getSelectButton());
                            }
                            isEdit = true;
                            setMyTagNum();
                            viewHolder.setVisible(R.id.tag_drag_tips, true);
                            viewHolder.setText(R.id.tag_drag_tips, getString(R.string.tag_drag_tips));
                            ((TextView) v).setText(R.string.done);
                            initTagDrag();
                        } else {
                            isEdit = false;
                            if (flowLayout.getSelectButton() != null) {
                                NewsTagUtils.setSelectTag(flowLayout.getSelectButton());
                            }
                            viewHolder.setText(R.id.tag_my_sort, R.string.my_assortment);
                            ((TextView) v).setText(R.string.edit);
                            viewHolder.setVisible(R.id.tag_drag_tips, false);
                            initTagDefault();
                        }
                    }
                });
    }

    public List<TagInfo> addTags(String tagId, String[] stringArray, int type) {
        List<TagInfo> list = new ArrayList<>();
        TagInfo tagInfo;
        String name;
        if (stringArray != null && stringArray.length > 0) {
            for (int i = 0; i < stringArray.length; i++) {
                name = stringArray[i];
                tagInfo = new TagInfo();
                tagInfo.type = type;
                tagInfo.tagName = name;
                tagInfo.tagId = tagId + i;
                list.add(tagInfo);
            }
        }
        return list;
    }

    public void hideDiaglog() {
        hideWaitDialog();
    }

    private void getRecommendTagInfos() {
        String[] country = getResources().getStringArray(R.array.country_name);
        recommondTagInfos.addAll(addTags("country", country, TagInfo.TYPE_TAG_USER));
        setData();
        listViewAdapter.notifyDataSetChanged();
    }


    public void setMyTagNum() {
        if (myTagInfos.size() != 0) {
            viewHolder.setText(R.id.tag_my_sort, getString(R.string.my_assortment) + "(" + myTagInfos.size() + "/100" + ")");
        } else {
            viewHolder.setText(R.id.tag_my_sort, getString(R.string.my_assortment));
        }
    }

    private void setData() {
        SparseArray<ArrayList<TagInfo>> sparseArray = getListSparseArray();
        newsTagUpdateListViewListener.setData(sparseArray);
    }

    private SparseArray<ArrayList<TagInfo>> getListSparseArray() {
        return FlowLayoutUtils.getRow(recommondTagInfos
                , ViewSizeUtil.getCustomDimen(NewsTagsActivity.this, 330f)
                , (int) (ViewSizeUtil.getCustomDimen(NewsTagsActivity.this, 16f))
                , (int) (ViewSizeUtil.getCustomDimen(NewsTagsActivity.this, 15f))
                , (int) (ViewSizeUtil.getCustomDimen(NewsTagsActivity.this, 26f))
        );
    }

    private void initTagDrag() {
        flowLayout.enableDragAndDrop();
        flowLayout.setIsEdit(true);
    }

    private void initTagDefault() {
        flowLayout.setDefault();
        flowLayout.setIsEdit(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
