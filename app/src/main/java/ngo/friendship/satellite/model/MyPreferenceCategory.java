package ngo.friendship.satellite.model;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * The Class MyPreferenceCategory.
 */
public class MyPreferenceCategory extends PreferenceCategory  implements Serializable{
    
    /**
     * Instantiates a new my preference category.
     *
     * @param context the context
     */
    public MyPreferenceCategory(Context context) {
        super(context);
    }

    /**
     * Instantiates a new my preference category.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Instantiates a new my preference category.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public MyPreferenceCategory(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    /* (non-Javadoc)
     * @see android.preference.Preference#onBindView(android.view.View)
     */
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = view.findViewById(android.R.id.title);
        titleView.setTextColor(Color.BLACK);
    }
}