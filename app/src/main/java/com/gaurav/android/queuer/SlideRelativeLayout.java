package com.gaurav.android.queuer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


/* This is custom Relative Layout so that the getters and setters can be implemented for obtaining the fractional percentage of view.
*  Without this the dimension in xml animator gets hard coded and view are not perfectly animated for different size devices.
 *  This Relative Layout is used as the root layout for the fragments 1 and 2 on which the animation is be performed.
 *  Relative layout is chosen because root fragment layout were Relative layout
 *  */
public class SlideRelativeLayout extends RelativeLayout {
    // constructers
    public SlideRelativeLayout(Context context) {
        super(context);
    }

    public SlideRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setXFraction(float fraction) {
        float translationX = getWidth() * fraction;
        setTranslationX(translationX);
    }

    public float getXFraction() {
        if (getWidth() == 0) {
            return 0;
        }
        return getTranslationX() / getWidth();
    }
}