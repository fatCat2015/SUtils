package com.cat.sutils.wheel;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.cat.sutils.R;

import java.util.List;

public class WheelView extends RecyclerView implements View.OnClickListener {

    private int mStartColor;
    private int mEndColor;
    private int mVisibleItemCount;
    private float mScaleValue;
    private int stepDegree;
    private float mTextSize;
    private float mCenterLineWidth;
    private int mCenterLineColor;

    private ArgbEvaluator mArgbEvaluator;

    private WheelLayoutManager mWheelLayoutManager;

    private WheelAdapter mWheelAdapter;

    public WheelView(@NonNull Context context) {
        super(context);
        obtainAttrs(context,null);
        init();
    }

    public WheelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        obtainAttrs(context,attrs);
        init();
    }

    public WheelView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        obtainAttrs(context,attrs);
        init();
    }



    private void init(){
        setLayoutManager(mWheelLayoutManager=new WheelLayoutManager(mVisibleItemCount));
        mWheelLayoutManager.setItemTransformer(mDefaultItemTransformer);
        mArgbEvaluator=new ArgbEvaluator();
        setAdapter(mWheelAdapter=new WheelAdapter(getContext(),this));
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void obtainAttrs(Context context, AttributeSet attrs){
        TypedArray typedArray =context.obtainStyledAttributes(attrs,R.styleable.WheelView);
        mStartColor=typedArray.getColor(R.styleable.WheelView_startColor,0xff333333);
        mEndColor=typedArray.getColor(R.styleable.WheelView_endColor,0xff999999);
        mVisibleItemCount=typedArray.getInt(R.styleable.WheelView_visibleItemCount,5);
        if (mVisibleItemCount%2==0) {
            mVisibleItemCount+=1;
        }
        mScaleValue=typedArray.getFloat(R.styleable.WheelView_scaleValue,0.8F);
        stepDegree=typedArray.getInt(R.styleable.WheelView_stepDegree,20);
        mTextSize=typedArray.getDimension(R.styleable.WheelView_textSize,getContext().getResources().getDisplayMetrics().density*16);
        mCenterLineColor=typedArray.getColor(R.styleable.WheelView_centerLineColor,0xfff1f1f1);
        mCenterLineWidth=typedArray.getDimension(R.styleable.WheelView_centerLineWidth,getContext().getResources().getDisplayMetrics().density*0.8F);
        typedArray.recycle();
    }


    /**
     * 设置显示的数据
     * @param items
     * @param selectedPosition  默认选中位置
     * @param <T>
     */
    public <T> void setData(List<T> items,int selectedPosition){
        mWheelLayoutManager.setSelectedPosition(selectedPosition);
        mWheelAdapter.update(items);
    }

    public <T> void setData(List<T> items){
        mWheelLayoutManager.setSelectedPosition(0);
        mWheelAdapter.update(items);
    }


    /**
     * 设置选中的位置 无过渡动画
     * @param position
     */
    public void setPosition(int position){
        scrollToPosition(position);
    }

    /**
     * 获取选中的item
     * @param <T>
     * @return
     */
    public <T> T getSelectedItem(){
        return (T) mWheelAdapter.getItem(mWheelLayoutManager.getSelectedPosition());
    }

    /**
     * 选中改变监听
     * @param onSelectedChangListener
     */
    public void setOnSelectedChangListener(OnSelectedChangListener onSelectedChangListener){
        mWheelLayoutManager.setOnSelectedChangListener(onSelectedChangListener);
    }


    @Override
    public void onClick(View v) {
        int positionOfClickedItem=mWheelLayoutManager.getPosition(v);
        smoothScrollToPosition(positionOfClickedItem);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        setMeasuredDimension(getMeasuredWidth(), getPaddingBottom()+getPaddingTop()+mVisibleItemCount*getItemHeight());
    }


    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        drawCenterLine(c);
    }

    private void drawCenterLine(Canvas c){
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(mCenterLineColor);
        paint.setStrokeWidth(mCenterLineWidth);
        int startX=getPaddingLeft();
        int startY=(getHeight()-getItemHeight())/2;
        int endX=getWidth()-getPaddingRight();
        int endY=startY;
        c.drawLine(startX,startY,endX,endY,paint);
        endY=startY+=getItemHeight();
        c.drawLine(startX,startY,endX,endY,paint);
    }


    private int getItemHeight(){
        return (int) getContext().getResources().getDimension(R.dimen.wheel_view_item_height);
    }

    private final ItemTransformer mDefaultItemTransformer = (textView, position) -> {
        textView.setRotationX(-position*stepDegree);
        float scale= 1- Math.abs(position)*(1-mScaleValue)/(mVisibleItemCount/2);
        textView.setScaleX(scale);
        if(stepDegree==0){
            textView.setScaleY(scale);
        }
        int textColor=(int)mArgbEvaluator.evaluate(Math.abs(position)/(mVisibleItemCount/2), mStartColor,mEndColor);
        textView.setTextColor(textColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mTextSize);
    };



    public interface ItemTransformer{
        void transformItem(TextView childView, float position);
    }


}
