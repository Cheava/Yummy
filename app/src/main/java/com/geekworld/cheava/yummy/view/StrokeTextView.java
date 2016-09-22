package com.geekworld.cheava.yummy.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geekworld.cheava.yummy.R;

/**
 * The type Stroke text view.
 */
/*
* @class StrokeTextView
* @desc  描边文字
* @author wangzh
*/
public class StrokeTextView extends TextView {

    private TextView storekeText = null;        // 用于描边的TextView

    /**
     * Instantiates a new Stroke text view.
     *
     * @param context the context
     */
    public StrokeTextView(Context context) {
        super(context);
        storekeText = new TextView(context);
        init();
    }

    /**
     * Instantiates a new Stroke text view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        storekeText = new TextView(context, attrs);
        init();
    }

    /**
     * Instantiates a new Stroke text view.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public StrokeTextView(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
        storekeText = new TextView(context, attrs, defStyle);
        init();
    }

    /**
     * Init.
     */
    public void init() {
        TextPaint tp1 = storekeText.getPaint();
        tp1.setStrokeWidth(2);                          // 设置描边宽度
        tp1.setStyle(Style.STROKE);                     // 对文字只描边
        //storekeText.setTextColor(0xFFFFFFFF);            // 设置描边颜色
        storekeText.setTextColor(getResources().getColor(R.color.black_overlay) );            // 设置描边颜色
        storekeText.setGravity(getGravity());
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        storekeText.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = storekeText.getText();

        // 两个TextView上的内容必须一致
        if (tt == null || !tt.equals(this.getText())) {
            storekeText.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        storekeText.measure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        storekeText.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        storekeText.draw(canvas);
        super.onDraw(canvas);
    }

}

