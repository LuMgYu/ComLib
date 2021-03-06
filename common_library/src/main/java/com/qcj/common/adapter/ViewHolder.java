package com.qcj.common.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qcj.common.util.ImageUtil;

/*
 author  qiuchunjia
* 万能的viewhoder 并且封装了部分常用的使用方法
* */
public class ViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;
    private Context mContext;
    private int mLayoutId;

    /*
    * 设置private 不要设置为public  统一采用静态方法get来实现
    * */
    private ViewHolder(Context context, ViewGroup parent, int layoutId,
                       int position) {
        mContext = context;
        mLayoutId = layoutId;
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        mConvertView.setTag(this);
    }

    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }

    public int getPosition() {
        return mPosition;
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        if (tv != null) {
            tv.setText(text);
        }
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageResource(resId);
        }
        return this;
    }

    public ViewHolder setImageUrlByCommonSdv(int viewId, String imageUrl) {
        SimpleDraweeView view = getView(viewId);
        if (view != null) {
            ImageUtil.loadCommonImg(view, imageUrl);
        }
        return this;
    }
    public ViewHolder setImageUrlByloadCircleSdv(int viewId, String imageUrl) {
        SimpleDraweeView view = getView(viewId);
        if (view != null) {
            ImageUtil.loadCircleImg(view, imageUrl);
        }
        return this;
    }

    public ViewHolder setImageUrlByloadCommonSdv(int viewId, String imageUrl) {
        SimpleDraweeView view = getView(viewId);
        if (view != null) {
            ImageUtil.loadCommonImg(view, imageUrl);
        }
        return this;
    }
    public ViewHolder setImageUrlByloadCircleSdv(int viewId, String imageUrl, RoundingParams params) {
        SimpleDraweeView view = getView(viewId);
        if (view != null) {
            ImageUtil.loadRoundParamsImg(view, imageUrl,params);
        }
        return this;
    }
    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
        return this;
    }

    public ViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageDrawable(drawable);
        }
        return this;
    }

    public ViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        if (view != null) {
            view.setBackgroundColor(color);
        }
        return this;
    }

    public ViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        if (view != null) {
            view.setBackgroundResource(backgroundRes);
        }
        return this;
    }

    public ViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setTextColor(textColor);
        }
        return this;
    }

    public ViewHolder setTextColorRes(int viewId, int textColorRes) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setTextColor(mContext.getResources().getColor(textColorRes));
        }
        return this;
    }

    @SuppressLint("NewApi")
    public ViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            View view = getView(viewId);
            if (view != null) {
                view.setAlpha(value);
            }
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            View view = getView(viewId);
            if (view != null) {
                view.startAnimation(alpha);
            }
        }
        return this;
    }

    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public ViewHolder linkify(int viewId) {
        TextView view = getView(viewId);
        if (view != null) {
            Linkify.addLinks(view, Linkify.ALL);
        }
        return this;
    }

    public ViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            if (view != null) {
                view.setTypeface(typeface);
                view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }

        }
        return this;
    }

    public ViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = getView(viewId);
        if (view != null) {
            view.setProgress(progress);
        }
        return this;
    }

    public ViewHolder setProgress(int viewId, int progress, int max) {
        ProgressBar view = getView(viewId);
        if (view != null) {
            view.setMax(max);
            view.setProgress(progress);
        }
        return this;
    }

    public ViewHolder setMax(int viewId, int max) {
        ProgressBar view = getView(viewId);
        if (view != null) {
            view.setMax(max);
        }
        return this;
    }

    public ViewHolder setRating(int viewId, float rating) {
        RatingBar view = getView(viewId);
        if (view != null) {
            view.setRating(rating);
        }
        return this;
    }

    public ViewHolder setRating(int viewId, float rating, int max) {
        RatingBar view = getView(viewId);
        if (view != null) {
            view.setMax(max);
            view.setRating(rating);
        }
        return this;
    }

    public ViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        if (view != null) {
            view.setTag(tag);
        }
        return this;
    }

    public ViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        if (view != null) {
            view.setTag(key, tag);

        }
        return this;
    }

    public ViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = (Checkable) getView(viewId);
        if (view != null) {
            view.setChecked(checked);
        }
        return this;
    }

    /**
     * 关于事件的
     */
    public ViewHolder setOnClickListener(int viewId,
                                         View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
        return this;
    }

    public ViewHolder setOnTouchListener(int viewId,
                                         View.OnTouchListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnTouchListener(listener);
        }
        return this;
    }

    public ViewHolder setOnLongClickListener(int viewId,
                                             View.OnLongClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnLongClickListener(listener);
        }
        return this;
    }


}
