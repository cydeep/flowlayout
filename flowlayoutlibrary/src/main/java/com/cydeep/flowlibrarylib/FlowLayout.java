package com.cydeep.flowlibrarylib;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.cydeep.flowlibrarylib.listener.OnTagClickListener;
import com.cydeep.flowlibrarylib.listener.OnTagSelectListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chenyu on 17/4/5.
 */
public class FlowLayout extends ViewGroup {
    private final float textSize;
    private final int tagHeight;
    private final int deleteIconMargin;
    private final int childViewPadding;
    private final int defaultViewBackground;
    private final int selectViewBackground;
    private final int fixViewEditingBackground;
    private final int fixViewEditingTextColor;
    private final int selectTextColor;
    private int textViewSpacing;
    private int verticalSpacing;
    private int defaultTextColor;
    private DragHandler mDragAndDropHandler;
    private AnimatorSet lastAnimatorSet;
    public int deleteIconWidth;
    private boolean isMeasureSuccess;
    private OnTagClickListener onTagClickListener;
    private OnTagSelectListener onTagSelectListener;
    private List<TextView> recommentLists = new ArrayList<>();
    private String tagId = "";

    public TextView getSelectButton() {
        return selectButton;
    }

    private TextView selectButton;

    public SparseArray<ArrayList<TagInfo>> getRowSparseArray() {
        return rowSparseArray;
    }

    private SparseArray<ArrayList<TagInfo>> rowSparseArray;

    public ArrayList<TagInfo> getTagInfos() {
        return tagInfos;
    }

    private ArrayList<TagInfo> tagInfos;
    private boolean isSettingAnimation = false;

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.flowLayout, defStyle, 0);

        textViewSpacing = a.getDimensionPixelSize(R.styleable.flowLayout_horizontalSpacingSize, ViewSizeUtil.getCustomDimen(context, 15f));
        verticalSpacing = a.getDimensionPixelSize(R.styleable.flowLayout_verticalSpacingSize, ViewSizeUtil.getCustomDimen(context, 15f));
        tagHeight = a.getDimensionPixelSize(R.styleable.flowLayout_tagHeight, ViewSizeUtil.getCustomDimen(context, 28f));
        childViewPadding = a.getDimensionPixelSize(R.styleable.flowLayout_childViewPadding, ViewSizeUtil.getCustomDimen(context, 26f));
        textSize = a.getDimensionPixelSize(R.styleable.flowLayout_flowLayoutTextSize, ViewSizeUtil.getCustomDimen(context, 14f)) / ViewSizeUtil.getDensity(context);
        deleteIconWidth = a.getDimensionPixelSize(R.styleable.flowLayout_deleteIconWidth, ViewSizeUtil.getCustomDimen(context, 29f));
        deleteIconMargin = a.getDimensionPixelSize(R.styleable.flowLayout_deleteIconMargin, ViewSizeUtil.getCustomDimen(context, 10f));

        selectViewBackground = a.getResourceId(R.styleable.flowLayout_selectViewBackground, R.drawable.tag_select);
        selectTextColor = a.getColor(R.styleable.flowLayout_selectTextColor, 0xffffffff);


        defaultTextColor = a.getColor(R.styleable.flowLayout_defaultTextColor, 0xff645e66);
        defaultViewBackground = a.getResourceId(R.styleable.flowLayout_defaultViewBackground, R.drawable.round_rect_gray);

        fixViewEditingTextColor = a.getColor(R.styleable.flowLayout_fixViewTextColor, 0xffdbdcde);
        fixViewEditingBackground = a.getResourceId(R.styleable.flowLayout_fixViewEditingBackground, R.drawable.tag_uncheck);


        a.recycle();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    public void setDefault() {
        mDragAndDropHandler = null;
    }

    public List<ImageView> deleteIconImageViews = new ArrayList<>();

    public void setIsEdit(boolean isEdit) {
        if (isEdit) {
            for (int i = 0; i < getTagInfos().size(); i++) {
                addDeleteImageView(i);
            }
            for (int i = 0; i < recommentLists.size(); i++) {
                if (onTagSelectListener != null) {
                    recommentLists.get(i).setBackgroundResource(defaultViewBackground);
                    recommentLists.get(i).setTextColor(0xffdbdcde);
                }
                recommentLists.get(i).setOnClickListener(null);
            }

            requestLayout();
        } else {
            recommentLists.clear();
            initData();
        }
    }


    public void addDeleteImageView(int i) {
        ImageView imageView;
        imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(ViewSizeUtil.getCustomDimen(getContext(), 29f), ViewSizeUtil.getCustomDimen(getContext(), 29f)));
        imageView.setBackgroundResource(R.drawable.icon_delete);
        final int finalI = i;
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getChildAt(finalI);
                TagInfo tagInfo = (TagInfo) view.getTag();
                deleteTag(tagInfo);
            }
        });
        setOnLongClick(getChildAt(i), i);
        if (((TagInfo) getChildAt(i).getTag()).type == TagInfo.TYPE_TAG_SERVICE) {
            imageView.setVisibility(INVISIBLE);
        }
        addView(imageView);
        deleteIconImageViews.add(imageView);
    }

    public void deleteTag(TagInfo tagInfo) {
        tagInfos.remove(tagInfo);

        removeAllViews();
        isMeasureSuccess = false;
        addTags(tagInfos);
        deleteIconImageViews.clear();
        setIsEdit(true);
        if (onTagClickListener != null) {
            onTagClickListener.onTagDelete(tagInfo);
        }
    }

    public void initData() {
        removeAllViews();
        isMeasureSuccess = false;
        deleteIconImageViews.clear();
        setTags(tagInfos);
    }

    public void addTag(TagInfo tagInfo, boolean isEdit) {
        tagInfos.add(tagInfo);
        removeAllViews();
        isMeasureSuccess = false;
        addTags(tagInfos);
        deleteIconImageViews.clear();
        setIsEdit(isEdit);
    }

    /**
     * 当前选中的tagId
     * @param tagId
     */
    public void setSelectTagId(String tagId) {
        this.tagId = tagId;
    }


    public void setTags(ArrayList<TagInfo> tagInfos) {
        addTags(tagInfos);
        requestLayout();
    }


    public void setOnTagClickListener(OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }

    public void setOnTagSelectListener(OnTagSelectListener onTagSelectListener) {
        this.onTagSelectListener = onTagSelectListener;
    }

    public void addTags(ArrayList<TagInfo> tagInfos) {
        this.tagInfos = tagInfos;
        for (int i = 0; i < tagInfos.size(); i++) {
            addListViewTextView(tagInfos, i);
        }
        if (getChildCount() > tagInfos.size()) {
            removeViews(tagInfos.size(), getChildCount() - tagInfos.size());
        }
    }

    public void addTextView(List<TagInfo> tagInfos, int i) {
        TagInfo tagInfo;
        TextView button;
        tagInfo = tagInfos.get(i);
        tagInfo.childPosition = i;
        button = new TextView(getContext());
        button.setTextColor(defaultTextColor);
        button.setGravity(Gravity.CENTER);
        button.setTextSize(textSize);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, tagHeight);
        button.setBackgroundResource(R.drawable.round_rect_gray);
        addView(button, layoutParams);
        button.setText(tagInfo.tagName);
        button.setTag(tagInfo);
        button.setTag(tagInfo);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTagClickListener != null) {
                    onTagClickListener.onTagClick((TagInfo) v.getTag());
                }
            }
        });
    }

    public void addListViewTextView(List<TagInfo> tagInfos, int i) {
        TagInfo tagInfo;
        TextView button;
        tagInfo = tagInfos.get(i);
        tagInfo.childPosition = i;
        if (i < getChildCount()) {
            button = (TextView) getChildAt(i);
        } else {
            button = new TextView(getContext());
            if (tagInfo.type == TagInfo.TYPE_TAG_SERVICE) {
                recommentLists.add(button);
            }
            if (tagId.equals("-1") && i == 0 && deleteIconImageViews.size() == 0) {
                selectButton = button;
                setSelectTag(button);
            } else {
                if (tagInfo.tagId.equals(tagId) && deleteIconImageViews.size() == 0) {
                    selectButton = button;
                    setSelectTag(button);
                    setTextViewColor(button,selectViewBackground, selectTextColor);
                } else {
                    setTextViewColor(button,defaultViewBackground, defaultTextColor);
                }
            }
            button.setGravity(Gravity.CENTER);
            button.setTextSize(textSize);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, tagHeight);
            addView(button, layoutParams);
        }
        button.setText(tagInfo.tagName);
        button.setTag(tagInfo);
        button.setTag(tagInfo);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTagClickListener != null) {
                    onTagClickListener.onTagClick((TagInfo) v.getTag());
                }
            }
        });
    }

    public static void setSelectTag(TextView textView){
        textView.setBackgroundResource(R.drawable.tag_select);
        textView.setTextColor(0xffffffff);
    }
    public static void setUnSelectTag(TextView textView){
        textView.setBackgroundResource(R.drawable.round_rect_gray);
        textView.setTextColor(0xff645e66);
    }

    private void setTextViewColor(TextView textView,int backgroundRes,int color){
        textView.setBackgroundResource(backgroundRes);
        textView.setTextColor(color);
    }

    public void setOnLongClick(View button, int i) {
        if (mDragAndDropHandler != null) {
            final int finalI = i;
            TagInfo tagInfo = (TagInfo) getChildAt(finalI).getTag();
            if (tagInfo.type == TagInfo.TYPE_TAG_USER) {
                button.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        startDragging(finalI);
                        return true;
                    }
                });
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (!isMeasureSuccess && tagInfos != null) {
            final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(tagHeight, MeasureSpec.EXACTLY);
            final int imageMeasureSpec = MeasureSpec.makeMeasureSpec(deleteIconWidth, MeasureSpec.EXACTLY);

            rowSparseArray = FlowLayoutUtils.getTagRects(
                    tagInfos,
                    deleteIconMargin,
                    width - deleteIconMargin,
                    (int) (textSize * ViewSizeUtil.getDensity(getContext())),
                    tagHeight,
                    textViewSpacing,
                    verticalSpacing,
                    childViewPadding, new FlowLayoutUtils.onGetTagListener() {
                        @Override
                        public void onGetTag(int position, TagInfo tagInfo) {
                            getChildAt(position).measure(MeasureSpec.makeMeasureSpec(tagInfo.rect.width(), MeasureSpec.EXACTLY), childHeightMeasureSpec);
                            if (deleteIconImageViews.size() > 0) {
//                                tagInfo.rect.top += deleteIconMargin;
//                                tagInfo.rect.bottom += deleteIconMargin;
                                deleteIconImageViews.get(position).measure(imageMeasureSpec, imageMeasureSpec);
                            }
                        }
                    });
        }
        if (rowSparseArray != null && rowSparseArray.size() > 0) {
            List<TagInfo> tagInfos = rowSparseArray.get(rowSparseArray.size() - 1);
            if (tagInfos != null && tagInfos.size() > 0) {
                setMeasuredDimension(width, tagInfos.get(tagInfos.size() - 1).rect.bottom);
            } else {
                setMeasuredDimension(width, 0);
            }
        } else {
            setMeasuredDimension(width, 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isMeasureSuccess && tagInfos != null) {
            TagInfo tagInfo;
            for (int i = 0; i < tagInfos.size(); i++) {
                tagInfo = getTagInfos().get(i);
                if (deleteIconImageViews.size() > 0) {
                    deleteIconImageViews.get(i).layout(getDeleteIconL(tagInfo.rect.right), getDeleteIconT(tagInfo.rect.top), getDeleteIconR(tagInfo.rect.right), getDeleteIconB(tagInfo.rect.top));
                }
                getChildAt(i).layout(tagInfo.rect.left, tagInfo.rect.top, tagInfo.rect.right, tagInfo.rect.bottom);
            }
        }
    }

    public int getDeleteIconB(int top) {
        return top - deleteIconMargin + deleteIconWidth;
    }

    public int getDeleteIconR(int right) {
        return right + deleteIconMargin;
    }

    public int getDeleteIconT(int top) {
        return top - deleteIconMargin;
    }

    public int getDeleteIconL(int right) {
        return right + deleteIconMargin - deleteIconWidth;
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.dispatchDraw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDragAndDropHandler != null) {
            return mDragAndDropHandler.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void enableDragAndDrop() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            throw new UnsupportedOperationException("Drag and drop is only supported API levels 14 and up!");
        }
        mDragAndDropHandler = new DragHandler(this);
    }

    public void startDragging(final int position) {
        if (mDragAndDropHandler != null) {
            mDragAndDropHandler.startDragging(position);
        }
    }

    private void sortTag() {
        rowSparseArray = FlowLayoutUtils.getTagRects(tagInfos,deleteIconMargin, getMeasuredWidth(), (int) (textSize * ViewSizeUtil.getDensity(getContext()) + 0.5f), tagHeight, textViewSpacing, verticalSpacing, childViewPadding, null);
    }

    public void startAnimation(final TagInfo lastTagInfo) {
        if (isSettingAnimation) {
            return;
        }
        sortTag();
        TagInfo tagInfo;
        Rect rect;
        List<Animator> animationList = new ArrayList<>();
        for (int i = 0; i < tagInfos.size(); i++) {
            rect = new Rect();
            tagInfo = (TagInfo) getChildAt(i).getTag();
            getChildAt(i).getHitRect(rect);
//            if (deleteIconImageViews.size() > 0) {
//                tagInfo.rect.top += deleteIconMargin;
//                tagInfo.rect.bottom += deleteIconMargin;
//            }
            if (getChildAt(i).isShown()) {
                if (rect.left != tagInfo.rect.left) {
                    animationList.add(getObjectAnimator(tagInfo.rect.left, "x", getChildAt(i), 250));
                    if (deleteIconImageViews.size() > 0) {
                        animationList.add(getObjectAnimator(getDeleteIconL(tagInfo.rect.right), "x", deleteIconImageViews.get(i), 250));
                    }
                }
                if (rect.top != tagInfo.rect.top) {
                    animationList.add(getObjectAnimator(tagInfo.rect.top, "y", getChildAt(i), 250));
                    if (deleteIconImageViews.size() > 0) {
                        animationList.add(getObjectAnimator(getDeleteIconT(tagInfo.rect.top), "y", deleteIconImageViews.get(i), 250));
                    }
                }
            } else {
                animationList.add(getObjectAnimator(tagInfo.rect.left, "x", getChildAt(i), 0));
                animationList.add(getObjectAnimator(tagInfo.rect.top, "y", getChildAt(i), 0));
                if (deleteIconImageViews.size() > 0) {
                    animationList.add(getObjectAnimator(getDeleteIconL(tagInfo.rect.right), "x", deleteIconImageViews.get(i), 0));
                    animationList.add(getObjectAnimator(getDeleteIconT(tagInfo.rect.top), "y", deleteIconImageViews.get(i), 0));
                }
            }
        }
        lastAnimatorSet = new AnimatorSet();
        lastAnimatorSet.playTogether(animationList);
        lastAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isSettingAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isSettingAnimation = false;
                if (mDragAndDropHandler.getLastTagInfo() != null && lastTagInfo != mDragAndDropHandler.getLastTagInfo()) {
                    startAnimation(mDragAndDropHandler.getLastTagInfo());
                } else if (tagInfos.get(tagInfos.size() - 1).rect.bottom != getMeasuredHeight()) {
                    isMeasureSuccess = true;
                    requestLayout();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isSettingAnimation = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        lastAnimatorSet.start();
    }

    @NonNull
    public ObjectAnimator getObjectAnimator(int value, String property, View view, long duration) {
        ObjectAnimator x;
        x = ObjectAnimator.ofFloat(view, property, value);
        x.setDuration(duration);
        return x;
    }

}
