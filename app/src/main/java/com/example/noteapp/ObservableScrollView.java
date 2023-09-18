package com.example.noteapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

public class ObservableScrollView extends ScrollView
{
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 150;
    private float startY = 0;
    private float startX = 0;

    private Context mContext;
    private int mMaxYOverscrollDistance;

    public ObservableScrollView(Context context)
    {
        super(context);
        mContext = context;
        initBounceScrollView();
    }

    public ObservableScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        initBounceScrollView();
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        initBounceScrollView();
    }

    private void initBounceScrollView()
    {
        //get the density of the screen and do some maths with it on the max overscroll distance
        //variable so that you get similar behaviors no matter what the screen size

        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
    {
        //This is where the magic happens, we have replaced the incoming maxOverScrollY with our own custom variable mMaxYOverscrollDistance;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getY() - startY;
                startY = ev.getY();

                // Đảm bảo cuộn trong khoảng giới hạn
                if (getScrollY() + deltaY < 0) {
                    deltaY = -getScrollY();
                }

                // Đảm bảo cuộn trong khoảng giới hạn
                if (getScrollY() + getHeight() + deltaY > getChildAt(0).getHeight()) {
                    deltaY = getChildAt(0).getHeight() - getScrollY() - getHeight();
                }

                // Cuộn ScrollView
                scrollBy(0, (int) deltaY);
                break;
            default:
                break;
        }

        return super.onTouchEvent(ev);
    }
}
