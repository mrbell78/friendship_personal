package ngo.friendship.satellite.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ngo.friendship.satellite.R;

/*
 * Author: Md. Shahadat Sarker
 * created: 15 Feb 2018
 * modified: 22 Feb 2018
 * */
public class AppButton extends LinearLayout {

	Context context;
	private TextView tv;
	private MdiTextView mdi_icon;
	boolean iconToRight;
	
	Object mdIcon, text, btn_class;

	/**
	 * Instantiates a new my button.
	 *
	 * @param ctx
	 *            the ctx
	 */
	public AppButton(Context ctx) {
		super(ctx);
		this.context = ctx;
		initLayout();
	}

	/**
	 * Instantiates a new my button.
	 *
	 * @param ctx
	 *            the ctx
	 * @param attrs
	 *            the attrs
	 */
	public AppButton(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		this.context = ctx;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardButton);
		mdIcon = a.getString(R.styleable.CardButton_mdIcon);
		text = a.getString(R.styleable.CardButton_text);
		iconToRight = a.getBoolean(R.styleable.CardButton_iconToRight, false);

		try {
			btn_class = a.getString(R.styleable.CardButton_btn_class);
		} catch (Exception ex){

		}

		initButton(mdIcon, text, iconToRight);
		a.recycle();
	}


	public AppButton(Context ctx, Object mdIcon, Object text) {
		super(ctx);

		this.mdIcon = mdIcon;
		this.text = text;
		this.context = ctx;

		initButton(mdIcon, text, this.iconToRight);
	}


	public AppButton(Context ctx, Object mdIcon, Object text, boolean iconToRight) {
		super(ctx);
		this.mdIcon = mdIcon;
		this.text = text;
		this.context = ctx;
		initButton(mdIcon, text, iconToRight);
	}


	/**
	 * Inits the layout.
	 */
	private void initLayout() {

		tv = new TextView(context);
		mdi_icon = (MdiTextView) View.inflate(context, R.layout.app_button_icon, null);

        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_large));
		tv.setDuplicateParentStateEnabled(true);

		
		/*mdi_icon.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getDimension(R.dimen.appbutton_icon_size)); // 21sp
		mdi_icon.setDuplicateParentStateEnabled(true);*/

		this.setBackgroundResource(R.drawable.app_button);


        if( btn_class == null ){
            tv.setTextColor(context.getResources().getColorStateList(R.color.app_button_pressed));
            mdi_icon.setTextColor(context.getResources().getColorStateList(R.color.app_button_pressed));
            this.setBackgroundResource(R.drawable.app_button);

        } else if( btn_class.toString().equalsIgnoreCase("ASH") ){
            tv.setTextColor(context.getResources().getColorStateList(R.color.app_button_pressed));
            mdi_icon.setTextColor(context.getResources().getColorStateList(R.color.app_button_pressed));
            this.setBackgroundResource(R.drawable.app_button_ash);

        }


		this.setOrientation(LinearLayout.HORIZONTAL);
		
		if( iconToRight ) {
			this.setGravity(Gravity.CENTER_VERTICAL | Gravity.END | Gravity.RIGHT);
		} else {
			this.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT | Gravity.START);
		}
		
	}

	private void initButton(Object mdIcon, Object text, boolean iconToRight) {
		initLayout();
		
		if( iconToRight ) {
			this.addView(getTextView(text));
			this.addView(getMdiTextView(mdIcon));
			
		} else {
			this.addView(getMdiTextView(mdIcon));
			this.addView(getTextView(text));
		}


	}

	public void setTextColor(Object textColor) {
		if (textColor instanceof Integer)
			tv.setTextColor((Integer) textColor);
	}

	public void setText(Object text) {
		if (text instanceof Integer)
			tv.setText((Integer) text);
		else
			tv.setText((String) text);
	}
	
	public void setIcon(Object resId) {
		if(resId instanceof Integer){
			mdi_icon.setText((Integer)resId);
		}else{
			mdi_icon.setText((String)resId);
		}
	}

	private MdiTextView getMdiTextView(Object resId)
	{
		LayoutParams param = new LayoutParams( LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		param.setMargins(1, 10, 1, 10);
		mdi_icon.setLayoutParams(param);
		
		if(resId instanceof Integer){
			mdi_icon.setText((Integer)resId);
		}else{
			mdi_icon.setText((String)resId);
		}
		
		
		return mdi_icon;
	}



	private TextView getTextView(Object resId)
	{
		LayoutParams param = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(param);
		
		if(resId instanceof Integer){
			tv.setText((Integer)resId);
		}else if (resId instanceof String){
			tv.setText((String)resId);
		}
		
		if( iconToRight ) {
			tv.setPadding(1, 1, 5, 1);
			
		} else {
			tv.setPadding(5, 1, 1, 1);
			
		}
		
		return tv;
	}

}
