package com.qcj.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.qcj.common.R;
import com.qcj.common.interf.Indicator;

import java.util.List;

/**
 * 实现viewpagertab的指示器
 * Created by qiuchunjia on 2016/3/21.
 */
public class ViewPagerIndicator extends LinearLayout implements Indicator {

    private static final String TAG = "ViewPagerIndicator";
    /**
     * 绘制三角形的画笔
     */
    private Paint mPaint;
    /**
     * path构成一个三角形
     */
    private Path mPath;
    /**
     * 三角形的宽度
     */
    private int mTriangleWidth;
    /**
     * 三角形的高度
     */
    private int mTriangleHeight;

    /**
     * 三角形的宽度为单个Tab的1/6
     */
    private static final float RADIO_TRIANGEL = 1.0f / 6;
    /**
     * 三角形的最大宽度
     */
    private final int DIMENSION_TRIANGEL_WIDTH = (int) (getScreenWidth() / 3 * RADIO_TRIANGEL);

    /**
     * 初始时，三角形指示器的偏移量
     */
    private int mInitTranslationX;
    /**
     * 手指滑动时的偏移量
     */
    private float mTranslationX;

    /**
     * 默认的Tab数量
     */
    private static final int COUNT_DEFAULT_TAB = 4;
    /**
     * tab数量
     */
    private int mTabVisibleCount = COUNT_DEFAULT_TAB;

    /**
     * tab上的内容
     */
    private List<String> mTabTitles;
    /**
     * 与之绑定的ViewPager
     */
    public ViewPager mViewPager;

    /**
     * 标题正常时的颜色
     */
    private int color_text_normal = 0x77FFFFFF;
    /**
     * 标题选中时的颜色
     */
    private int color_text_highlightcolor = 0xFFFFFFFF;
    /**
     * 普通字体大小
     */
    private int size_text_normal = 28;
    /**
     * 选择后的字体大小
     */
    private int size_text_choose = 32;
    /*
    三角形的颜色
    * */
    private int triangleColor = 0xffffffff;

    public ViewPagerIndicator(Context context) {
        super(context);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context, attrs);
    }

    private void initData(Context context, AttributeSet attrs) {
        Log.e(TAG, "initData()");
// 获得自定义属性，tab的数量
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.viewpagerindicator, 0, 0);
        if (a != null) {
            mTabVisibleCount = a.getInt(R.styleable.viewpagerindicator_visiable_item_count, COUNT_DEFAULT_TAB);
            color_text_highlightcolor = a.getColor(R.styleable.viewpagerindicator_color_text_highlightcolor,
                    color_text_highlightcolor);
            color_text_normal = a.getColor(R.styleable.viewpagerindicator_color_text_normal,
                    color_text_normal);
            size_text_choose = (int) a.getDimension(R.styleable.viewpagerindicator_size_text_choose,
                    size_text_choose);
            size_text_normal = (int) a.getDimension(R.styleable.viewpagerindicator_size_text_normal,
                    size_text_normal);
            triangleColor = a.getColor(R.styleable.viewpagerindicator_indicatorColor,
                    triangleColor);
            a.recycle();
        }
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(triangleColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.e(TAG, "onFinishInflate()");
        int cCount = getChildCount();
        if (cCount == 0) {
            return;
        }
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            params.height = getHeight();
            params.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(params);
        }
        setItemClickTouch();
    }

    /**
     * 添加选项的点击事件
     */
    private void setItemClickTouch() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            final int finalI = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewPager != null) {
                        mViewPager.setCurrentItem(finalI);
                    }
                }
            });
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGEL);// 1/6 of
        mTriangleWidth = Math.min(DIMENSION_TRIANGEL_WIDTH, mTriangleWidth);
        // 初始化三角形
        initTriangle();
        // 初始时的偏移量
        mInitTranslationX = getWidth() / mTabVisibleCount / 2 - mTriangleWidth
                / 2;

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制指示器
        canvas.save();
        Log.e(TAG, "dispatchDraw getHeight()=" + getHeight());
        canvas.translate(mInitTranslationX + mTranslationX, getHeight());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    /**
     * 初始化三角形指示器
     */
    private void initTriangle() {
        mPath = new Path();
        mTriangleHeight = (int) (mTriangleWidth / 2 / Math.sqrt(2));
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }

    /**
     * 添加viewpager的引用
     *
     * @param viewPager
     * @param pos       需要设置的位置
     */
    public void setViewPager(ViewPager viewPager, final int pos) {
        if (viewPager == null) {
            try {
                throw new Exception("viewpager 不能为空");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        this.mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
                scrollMain(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(position);
                }
                resetTextView();
                setClickTextView(position);
                fixTheBug(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
        //设置当前页
        mViewPager.setCurrentItem(pos);
        setClickTextView(pos);

    }

    /**
     * 实现本类的核心代码
     *
     * @param position
     * @param offset
     */
    private void scrollMain(int position, float offset) {
        mTranslationX = getWidth() / mTabVisibleCount * (position + offset);//指示的偏移量
        int width = getScreenWidth() / mTabVisibleCount;  //指示器的宽度
        if (offset > 0 && position >= mTabVisibleCount - 2 && getChildCount() > mTabVisibleCount) {
            if (mTabVisibleCount > 1) {
                //当滑动到最后一个的时候不滚动
                if (position == getChildCount() - 2) {
                    //不做处理
                } else {
                    scrollTo((int) ((position - (mTabVisibleCount - 2)) * width + offset * width), 0);
                }
            } else if (mTabVisibleCount == 1) {
                //当可见的为一的时候
                scrollTo(position * width + (int) (width * offset), 0);

            }

        }
        invalidate();  //重绘
    }

    /**
     * //tab多了之后,滑动到最后那个,不用滑动返回,依次点击左上第一个,偶尔会出现:第一个tab不显示,或显示一半的情况
     *
     * @param position
     */
    private void fixTheBug(int position) {
        if ((position <= mTabVisibleCount - 2)) {
            if (getScrollX() > 0) {
                scrollTo(0, 0);
                Log.e(TAG, "fixthebug==postion=" + position);
                invalidate();
            }
        }
    }

    /**
     * 设置可见的tab的数量
     *
     * @param count
     */
    public void setVisibleTabCount(int count) {
        this.mTabVisibleCount = count;
    }

    /**
     * 设置tab的标题内容 可选，可以自己在布局文件中写死
     *
     * @param datas
     */
    public void setTabItemTitles(List<String> datas) {
        // 如果传入的list有值，则移除布局文件中设置的view
        if (datas != null && datas.size() > 0) {
            this.removeAllViews();
            this.mTabTitles = datas;

            for (String title : mTabTitles) {
                // 添加view
                addView(generateTextView(title));
            }
            // 设置item的click事件
            setItemClickTouch();
        }

    }

    /**
     * 根据标题生成我们的TextView
     *
     * @param text
     * @return
     */
    private TextView generateTextView(String text) {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mTabVisibleCount;
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(color_text_normal);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, size_text_normal);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 设置点击后的textview
     *
     * @param pos
     */
    private void setClickTextView(int pos) {
        View view = getChildAt(pos);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(color_text_highlightcolor);
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, size_text_choose);
        }
    }

    /**
     * 重置textview
     */
    private void resetTextView() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color_text_normal);
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, size_text_normal);
            }
        }
    }

    /**
     * 对外的ViewPager的回调接口
     */
    public interface PageChangeListener {
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }

    // 对外的ViewPager的回调接口
    private PageChangeListener onPageChangeListener;

    // 对外的ViewPager的回调接口的设置

    public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
        this.onPageChangeListener = pageChangeListener;
    }


    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

}
