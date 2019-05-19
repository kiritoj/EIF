package com.example.mifans.eif.CustomizeView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class MyEditView extends android.support.v7.widget.AppCompatEditText implements TextWatcher,View.OnFocusChangeListener {
    private boolean isfous;//是否获得焦点
    private Drawable drawable;//右侧图标的引用
    public MyEditView(Context context) {
        super(context);

    }

    public MyEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MyEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init(){
        //获取右边的drawable
        drawable = getCompoundDrawables()[2];
        if (drawable==null){
            return;
        }
        drawable.setBounds(0,0,40,40);
        //初始化把清除按钮隐藏
        setCompoundDrawables(getCompoundDrawables()[0],null,null,null);
        //焦点改变的监听
        setOnFocusChangeListener(this);
        addTextChangedListener(this);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //当手指抬起即认为点击了
        if(event.getAction()==MotionEvent.ACTION_UP){

            int x = (int) event.getX();//点击位置距离editview左上角x的距离
            int y = (int) event.getY();
            //判断有没有点击到右侧clean的图标
            if (istouchclean(x,y)){
                //清空输入
                setText("");
            };
        }
        return super.onTouchEvent(event);
    }
    public boolean istouchclean(int x,int y){
        int left = getWidth()-getTotalPaddingRight();//控件的宽度-图标左边沿的距离到控件右边沿的距离
        int right = getRight()-getPaddingRight();
        //x,y是以控件左上角为原点，不是父容器，所以这里top不能用getTop
        int top = getPaddingTop();
        int bottom = top+drawable.getIntrinsicHeight();
        boolean istouch = x > left && x < right && y > top && y < bottom;
        return istouch;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isfous){
            if (s.length()>0) {
                setCompoundDrawables(getCompoundDrawables()[0], null, drawable, null);

            }else{
                setCompoundDrawables(getCompoundDrawables()[0], null, null, null);
            }
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    //当焦点改变时，相应地改变drawleft的显示与隐藏，失去焦点是自然要隐藏起来
    public void onFocusChange(View v, boolean hasFocus) {

        isfous = hasFocus;
        if (hasFocus){


            //获得焦点且输入框内字符长度>0才显示出来
            if (getText().length()>0) {
                setCompoundDrawables(getCompoundDrawables()[0], null, drawable, null);
            }
        }else{
            setCompoundDrawables(getCompoundDrawables()[0],null,null,null);

        }
    }
    public void shark(){
        //左右平移，震动的效果
        ObjectAnimator.ofFloat(this,"translationX",-20,20,0).setDuration(200).start();

    }

}
