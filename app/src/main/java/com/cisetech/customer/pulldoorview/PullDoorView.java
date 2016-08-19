package com.cisetech.customer.pulldoorview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Author:Yqy
 * Date:2016-08-03
 * Desc:拉伸开门View
 * Company:cisetech
 */
public class PullDoorView extends FrameLayout{
    private Scroller mScroller;
    private BounceInterpolator mInterpolator;
    public PullDoorView(Context context) {
        this(context, null);
    }

    public PullDoorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullDoorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView();
    }

    /**
     * 初始化View
     */
    private void setUpView() {
        /**
         * 创建一个带BounceInterpolator插值器（结束时候前后跳动）
         * 的Scroller
         */
        mInterpolator=new BounceInterpolator();
        mScroller=new Scroller(getContext(),mInterpolator);
        /**
         * 创建一个ImageView放置父控件
         */
        ImageView imageView=new ImageView(getContext());
        FrameLayout.LayoutParams imgLp=new FrameLayout.LayoutParams(-1,-1);
        imageView.setImageResource(R.mipmap.ic_introduction);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView, imgLp);
        /**
         * 创建一个TextView,放置控件底部
         */
        FrameLayout.LayoutParams textLp=new FrameLayout.LayoutParams(-2,-2);
        textLp.gravity= Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;
        TextView textView=new TextView(getContext());
        textView.setText("向上滑动关闭此广告");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.5f);
        textView.setTextColor(Color.DKGRAY);
        textView.setLayoutParams(textLp);
        addView(textView);
        /**
         * 创建一个alpha动画
         */
        AlphaAnimation a=new AlphaAnimation(0,1f);
        a.setDuration(1000);
        a.setRepeatCount(Animation.INFINITE);
        a.setRepeatMode(Animation.REVERSE);
        textView.startAnimation(a);
    }
    private float lastX,lastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        /**
         * 计算距离差
         */
        int durationY= (int) (event.getY()-lastY);
        switch (action){
            case MotionEvent.ACTION_DOWN:
                /**
                 * 记录按下的位置
                 */
                lastX=event.getX();
                lastY=event.getY();
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                scrollTo(0, 0);//关闭
                return true;
            case MotionEvent.ACTION_MOVE:
                /**
                 * 只处理向上滑
                 */
                if(durationY<0){
                    scrollBy(0, -(int) durationY);
                }
                lastX=event.getX();
                lastY=event.getY();
                break;
            case MotionEvent.ACTION_UP:
                if(getScrollY()>=getHeight()/2){//当滑动到控件一半的时候自动上滑打开控件
                    mScroller.startScroll(0, getScrollY(),0,getHeight(),500);
                }else{//滑动小于一般，自动下滑回到原位
                    mScroller.startScroll(0, getScrollY(),0,-getScrollY(),500);
                }
                /**
                 * 记得去重新绘制一下
                 */
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }
    /**
     * 当控件不断滑动的时候会回调此方法
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){//判断是否在自动滑动中
            int currY = mScroller.getCurrY();
            scrollTo(0,currY);
            invalidate();
        }
    }
}
