# flowlayout
简介：
类似网易新闻，今日头条频道标签排序删除等处理功能  

怎样配置：
在您gradle中

allprojects {

        repositories {
        
            maven { url 'https://jitpack.io' }
        
        }
    
}
    
dependencies {

     implementation 'com.github.cydeep:flowlayout:v1.0.0'   
     
}  
效果图

![image](https://github.com/cydeep/flowlayout/blob/master/app/src/main/res/drawable/impression.gif)


怎样使用：

在布局文件中

<com.cydeep.flowlibrarylib.FlowLayout
        android:id="@+id/newsTag"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        
        android:layout_marginLeft="@dimen/dp_15"
        
        android:layout_marginRight="@dimen/dp_5"
        
        android:layout_marginTop="@dimen/dp_23"
        
        app:fixViewEditingBackground = "@drawable/tag_select_one"    //固定标签背景
        
        app:fixViewTextColor="@color/colorAccent"                    //固定标签文字颜色
        
        app:defaultViewBackground = "@drawable/tag_uncheck_one"      //默认标签背景
        
        app:defaultTextColor="@color/colorPrimaryDark"               //默认标签文字颜色
        
        app:selectViewBackground = "@drawable/round_rect_red"        //当前所在标签背景
        
        app:selectTextColor="#ffF8982D"                              //当前所在标签文字颜色
        
        app:tagHeight = "40"                                         //单个标签高度
        
        app:deleteIconMargin = "20"                                  //删除按钮距离标签上和右的距离，有默认值
        
        app:horizontalSpacingSize = "50"                             //水平方向标签与标签之间的间距
        
        app:verticalSpacingSize = "10"                               //竖直方向标签与标签之间的间距
        
        app:childViewPadding = "50"                                  //标签内容与标签边界的距离
        
        app:deleteIconWidth = "60"                                   //删除图标宽高大小
        
        app:flowLayoutTextSize="30" />                               //标签内文字大小
        
        //以上大小都已做适配，并有默认值

flowLayout.setSelectTagId(currentId);//设置您当前所在栏目，若为-1时，默认第一个栏目是当前选中的栏目

flowLayout.setOnTagClickListener(new OnTagClickListener() {//设置点击事件

            @Override
            public void onTagClick(TagInfo tagInfo) {//标签点击事件   
            

            }

            @Override
            public void onTagDelete(TagInfo tagInfo) {//删除按钮点击事件
            

            }
        });
        
 标签类型：
 
 public class TagInfo {
 
    public static final int TYPE_TAG_USER = 0;//可以移动的标签
    
    public static final int TYPE_TAG_SERVICE = 1;//为固定标签，不能移动的那种
    
    public String tagId;
    
    public String tagName;
    
    Rect rect = new Rect();       //标签在布局中的位置信息
    
    public int childPosition;    //移动前标签的位置
    
    public int dataPosition = -1;//移动后标签的位置
    
    public int type;
}
