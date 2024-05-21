package ngo.friendship.satellite.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TimePicker;

// TODO: Auto-generated Javadoc

/**
 * The Class TimePreference.
 */
public class TimePreference extends DialogPreference {
    
    /** The last hour. */
    private int lastHour=0;
    
    /** The last minute. */
    private int lastMinute=0;
    
    /** The is24 hour format. */
    private boolean is24HourFormat;
    
    /** The picker. */
    private TimePicker picker=null;
    
    /** The context. */
    Context context;
 //  private TextView timeDisplay;

    /**
  * Instantiates a new time preference.
  *
  * @param ctxt the ctxt
  */
 public TimePreference(Context ctxt) {
        this(ctxt, null);
        context = ctxt;
    }

    /**
     * Instantiates a new time preference.
     *
     * @param ctxt the ctxt
     * @param attrs the attrs
     */
    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, 0);
        context = ctxt;
    }

    /**
     * Instantiates a new time preference.
     *
     * @param ctxt the ctxt
     * @param attrs the attrs
     * @param defStyle the def style
     */
    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);
        context = ctxt;

        is24HourFormat = DateFormat.is24HourFormat(ctxt);
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    /* (non-Javadoc)
     * @see android.preference.Preference#toString()
     */
    @Override
    public String toString() {
        if(is24HourFormat) {
            return ((lastHour < 10) ? "0" : "")
                    + Integer.toString(lastHour)
                    + ":" + ((lastMinute < 10) ? "0" : "")
                    + Integer.toString(lastMinute);
        } else {
            int myHour = lastHour % 12;
            return ((myHour == 0) ? "12" : ((myHour < 10) ? "0" : "") + Integer.toString(myHour))
                    + ":" + ((lastMinute < 10) ? "0" : "") 
                    + Integer.toString(lastMinute) 
                    + ((lastHour >= 12) ? " PM" : " AM");
        }
    }

    /* (non-Javadoc)
     * @see android.preference.DialogPreference#onCreateDialogView()
     */
    @Override
    protected View onCreateDialogView() {
        picker=new TimePicker(getContext().getApplicationContext());
        return(picker);
    }

    /* (non-Javadoc)
     * @see android.preference.DialogPreference#onBindDialogView(android.view.View)
     */
    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setIs24HourView(false);
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    /* (non-Javadoc)
     * @see android.preference.Preference#onCreateView(android.view.ViewGroup)
     */
    @Override
    protected View onCreateView (ViewGroup parent) {
         View prefView = super.onCreateView(parent);
         LinearLayout layout = new LinearLayout(parent.getContext());
         LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT, 2);
         layout.addView(prefView, lp);

         return layout;
    }

    /* (non-Javadoc)
     * @see android.preference.DialogPreference#onDialogClosed(boolean)
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            picker.clearFocus();
            lastHour=picker.getCurrentHour();
            lastMinute=picker.getCurrentMinute();

            String time=String.valueOf(lastHour)+":"+String.valueOf(lastMinute);
            if (callChangeListener(time)) {
                persistString(time);
              //  Log.e("Pref Name", getKey()+time);
             //   timeDisplay.setText(toString());
            }
        }
    }

    /* (non-Javadoc)
     * @see android.preference.Preference#onGetDefaultValue(android.content.res.TypedArray, int)
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    /* (non-Javadoc)
     * @see android.preference.Preference#onSetInitialValue(boolean, java.lang.Object)
     */
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            if (defaultValue==null) {
                time="00:00";
            }
            else {
                time=defaultValue.toString();
            }
            if (shouldPersist()) {
                persistString(time);
            }
        }

        String[] timeParts=time.split(":");
        lastHour=Integer.parseInt(timeParts[0]);
        lastMinute=Integer.parseInt(timeParts[1]);
    }
}