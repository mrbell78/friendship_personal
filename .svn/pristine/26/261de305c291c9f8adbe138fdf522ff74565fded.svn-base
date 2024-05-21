package ngo.friendship.satellite.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import ngo.friendship.satellite.R;

public class MyLinearLayout extends LinearLayout {

    private Context context;
    private int maxHeightDp;

    public MyLinearLayout(Context context) {
        super(context);
        this.context = context;
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyLinearLayout, 0, 0);
        try {
            maxHeightDp = a.getInteger(R.styleable.MyLinearLayout_maxHeightDp, 0);
        } finally {
            a.recycle();
        }
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeightPx = dpToPx(maxHeightDp);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeightPx, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMaxHeightDp(int maxHeightDp) {
        this.maxHeightDp = maxHeightDp;
        invalidate();
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
