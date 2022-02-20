package com.air.wuyunliuqi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.air.wuyunliuqi.R;
import com.air.wuyunliuqi.Utils;
import com.air.wuyunliuqi.model.PielItem;

import java.util.List;

public class WheelView extends RelativeLayout {
    private int mBackgroundColor;
    private int mTextColor;
    private int mTopTextSize;
    private int mSecondaryTextSize;
    private int mBorderColor;
    private int mTopTextPadding;
    private int mEdgeWidth;
    private Drawable mCenterImage;

    private PielView pielView;

    public WheelView(Context context) {
        super(context);
        init(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * @param ctx
     * @param attrs
     */
    private void init(Context ctx, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.WheelView);
            mBackgroundColor = typedArray.getColor(R.styleable.WheelView_lkwBackgroundColor, Color.WHITE);
            mTopTextSize = typedArray.getDimensionPixelSize(R.styleable.WheelView_lkwTopTextSize, (int) Utils.INSTANCE.convertDpToPixel(10f, getContext()));
            mSecondaryTextSize = typedArray.getDimensionPixelSize(R.styleable.WheelView_lkwSecondaryTextSize, (int) Utils.INSTANCE.convertDpToPixel(8f, getContext()));
            mTextColor = typedArray.getColor(R.styleable.WheelView_lkwTopTextColor, Color.BLACK);
            mTopTextPadding = typedArray.getDimensionPixelSize(R.styleable.WheelView_lkwTopTextPadding, (int) Utils.INSTANCE.convertDpToPixel(10f, getContext())) + (int) Utils.INSTANCE.convertDpToPixel(10f, getContext());
            mCenterImage = typedArray.getDrawable(R.styleable.WheelView_lkwCenterImage);
            mEdgeWidth = typedArray.getInt(R.styleable.WheelView_lkwEdgeWidth, 1);
            mBorderColor = typedArray.getColor(R.styleable.WheelView_lkwEdgeColor, Color.BLACK);
            typedArray.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.view_wheel, this, false);

        pielView = frameLayout.findViewById(R.id.pieView);

        pielView.setPieBackgroundColor(mBackgroundColor);
        pielView.setTopTextPadding(mTopTextPadding);
        pielView.setTopTextSize(mTopTextSize);
        pielView.setSecondaryTextSizeSize(mSecondaryTextSize);
        pielView.setPieCenterImage(mCenterImage);
        pielView.setBorderColor(mBorderColor);
        pielView.setBorderWidth(mEdgeWidth);


        if (mTextColor != 0) {
            pielView.setPieTextColor(mTextColor);
        }


        addView(frameLayout);
    }


    public boolean isTouchEnabled() {
        return pielView.isTouchEnabled();
    }

    public void setTouchEnabled(boolean touchEnabled) {
        pielView.setTouchEnabled(touchEnabled);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //This is to control that the touch events triggered are only going to the PieView
        for (int i = 0; i < getChildCount(); i++) {
            if (isPielView(getChildAt(i))) {
                return super.dispatchTouchEvent(ev);
            }
        }
        return false;
    }

    private boolean isPielView(View view) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < getChildCount(); i++) {
                if (isPielView(((ViewGroup) view).getChildAt(i))) {
                    return true;
                }
            }
        }
        return view instanceof PielView;
    }

    public void setWheelBackgroundColor(int color) {
        pielView.setPieBackgroundColor(color);
    }

    public void setWheelCenterImage(Drawable drawable) {
        pielView.setPieCenterImage(drawable);
    }

    public void setBorderColor(int color) {
        pielView.setBorderColor(color);
    }

    public void setWheelTextColor(int color) {
        pielView.setPieTextColor(color);
    }

    /**
     * @param data
     */
    public void setData(List<PielItem> data) {
        pielView.setData(data);
    }

    /**
     * @param numberOfRound
     */
    public void setRound(int numberOfRound) {
        pielView.setRound(numberOfRound);
    }

    /**
     * @param fixedNumber
     */
    public void setPredeterminedNumber(int fixedNumber) {
        pielView.setPredeterminedNumber(fixedNumber);
    }
}
