package ngo.friendship.satellite.views;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;

public class MenuButton extends LinearLayout{

	int imageId, buttonTextId;
	String imagePath, buttonText;
	Context context;
	private TextView tv ;
	private ImageView iv;
    
	public  MenuButton(Context ctx) {
		super(ctx);
		this.context = ctx;
		initLayout();
	}
	
	public MenuButton(Context ctx, AttributeSet attrs)
	{
		super(ctx, attrs);
		this.context = ctx;
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppButton);
		Drawable imageDrawable = a.getDrawable(R.styleable.AppButton_imageId);
		String buttonText = a.getString(R.styleable.AppButton_textId);
		boolean imageToRight = a.getBoolean(R.styleable.AppButton_imageToRight, false);
		initButton(imageDrawable, buttonText, imageToRight);
		a.recycle();
	}
	
	
	public MenuButton(Context ctx, int imageId, int buttonTextId, boolean imageToRight)
	{
		super(ctx);

		this.imageId = imageId;
		this.buttonTextId = buttonTextId;
		this.context = ctx;

		initButton(imageId, buttonTextId ,imageToRight);
	}


	public MenuButton(Context ctx, String imagePath, String buttonText, boolean imageToRight)
	{
		super(ctx);
		this.imagePath = imagePath;
		this.buttonText = buttonText;
		this.context = ctx;
		initButton(imagePath, buttonText, imageToRight);
	}

	public MenuButton(Context ctx, int imageId, String buttonText, boolean imageToRight)
	{
		super(ctx);
		this.imageId = imageId;
		this.buttonText = buttonText;
		this.context = ctx;
		initButton(imageId, buttonText, imageToRight);
	}
	
	

	private void initLayout(){
		tv = new TextView(context);
	    iv = new ImageView(context);
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(param);
		this.setOrientation(LinearLayout.VERTICAL);
	}
	
	private void initButton(Object image, Object text, boolean imageToRight)
	{
		initLayout();
		LayoutParams param;
		
		LinearLayout contantLayout=new LinearLayout(context);
		
		contantLayout.setOrientation(LinearLayout.HORIZONTAL);
		param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		
		contantLayout.setLayoutParams(param);
		contantLayout.setPadding(10,10,10,10);
		
		contantLayout.setGravity(Gravity.CENTER_VERTICAL);
		
		contantLayout.setBackgroundResource(R.drawable.bg_list_item);
		
		if(imageToRight)
		{
			if(text instanceof Integer)
				contantLayout.addView(getTextView(text));
			else
				contantLayout.addView(getTextView(text));
		}
		
		
		if( App.getContext().getAppSettings().getIsImageShow()!=null && App.getContext().getAppSettings().getIsImageShow().equals("YES")  ){
			if(image instanceof Integer)
			{
				contantLayout.addView(getImageView(image));
			}
			if(image instanceof Drawable)
			{
				contantLayout.addView(getImageView(image));
			}
			else
			{
				contantLayout.addView(getImageView(image));
			}
		}
		
		if(!imageToRight)
		{
			if(text instanceof Integer)
				contantLayout.addView(getTextView(text));
			else
				contantLayout.addView(getTextView(text));
		}
		
		
	
		this.addView(contantLayout);
		
		LinearLayout lineBreakLayout=new LinearLayout(context);
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
		param = new LayoutParams(LayoutParams.MATCH_PARENT,height);
		lineBreakLayout.setLayoutParams(param);
		lineBreakLayout.setBackgroundColor(Color.parseColor("#4e4e4e"));
		this.addView(lineBreakLayout);
		
		
	}
	
	public void setText(Object text){
		if(text instanceof Integer)
			tv.setText((Integer)text);
		else
			tv.setText((String)text);
	}
	
	public void setTextColor(Object textColor){
		if(textColor instanceof Integer)
			tv.setTextColor((Integer)textColor);
	}
	
	private ImageView getImageView(Object img)
	{
		LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		if(img instanceof Drawable){
			iv.setImageDrawable((Drawable)img);
		}
		else if(img instanceof Integer){
			iv.setImageResource((Integer)img);
		}
		else if(img instanceof String){
			iv.setImageBitmap(BitmapFactory.decodeFile((String)img));
		}
		
		
		iv.setLayoutParams(param);
		return iv;
	}
	
	public void setImage(Object img){
		if (iv==null) return ;
		if(img instanceof Drawable){
			iv.setImageDrawable((Drawable)img);
		}
		else if(img instanceof Integer){
			iv.setImageResource((Integer)img);
		}
		else if(img instanceof String){
			iv.setImageBitmap(BitmapFactory.decodeFile((String)img));
		}
	}



	private TextView getTextView(Object resId)
	{
	
		LayoutParams param = new LayoutParams( LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(param);
		
		if(resId instanceof Integer){
			tv.setText((Integer)resId);	
		}else if (resId instanceof String){
			tv.setText((String)resId);	
		}
		tv.setTextColor(Color.BLACK);
		
		tv.setTypeface(null, Typeface.BOLD);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_large));
		
		return tv;
	}
    
}