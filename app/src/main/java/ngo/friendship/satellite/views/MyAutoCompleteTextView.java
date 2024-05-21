package ngo.friendship.satellite.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

// TODO: Auto-generated Javadoc

/**
 * The Class MyAutoCompleteTextView.
 */
public class MyAutoCompleteTextView extends AutoCompleteTextView {

    /** The my threshold. */
    private int myThreshold;

    /**
     * Instantiates a new my auto complete text view.
     *
     * @param context the context
     */
    public MyAutoCompleteTextView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new my auto complete text view.
     *
     * @param context the context
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public MyAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Instantiates a new my auto complete text view.
     *
     * @param context the context
     * @param attrs the attrs
     */
    public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* (non-Javadoc)
     * @see android.widget.AutoCompleteTextView#setThreshold(int)
     */
    @Override
    public void setThreshold(int threshold) {
        if (threshold < 0) {
            threshold = 0;
        }
        myThreshold = threshold;
        
    }

    /* (non-Javadoc)
     * @see android.widget.AutoCompleteTextView#enoughToFilter()
     */
    @Override
    public boolean enoughToFilter() {
        return getText().length() >= myThreshold;
    }

    /* (non-Javadoc)
     * @see android.widget.AutoCompleteTextView#getThreshold()
     */
    @Override
    public int getThreshold() {
        return myThreshold;
    }

}
