package ngo.friendship.satellite.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by SHAHADAT on 12/8/2016.
 */

public class MdiTextView extends AppCompatTextView {

    public MdiTextView(Context context) {
        super(context);
        init(context);
    }

    public MdiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MdiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        Typeface roboto = Typeface.createFromAsset(context.getAssets(), "fonts/materialdesignicons-webfont.ttf");
        this.setTypeface(roboto);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


}
