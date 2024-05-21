package ngo.friendship.satellite.views;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ngo.friendship.satellite.R;

public class AppToast {


	public static void showToast(Context activity,Object text, Integer imgId ,Integer bacground ,int duration){
		LayoutInflater inflater=((Activity)activity).getLayoutInflater();
		View customToastroot =inflater.inflate(R.layout.mhealth_toast, null);
		
		LinearLayout linearLayout = customToastroot.findViewById(R.id.toast_background);
		if(bacground==null){
			linearLayout.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.mhtalth_toast_yellow));
		}else{
			linearLayout.setBackgroundDrawable(activity.getResources().getDrawable(bacground));
		}
		
		
		ImageView img = customToastroot.findViewById(R.id.toast_img);
		if(imgId==null){
			img.setImageResource(R.drawable.yellow);
		}else{
			img.setImageResource(imgId);	
		}
		
		
		
		TextView txtView = customToastroot.findViewById(R.id.toast_txt);
		if(text instanceof String)
			txtView.setText((String)text);
		else if(text instanceof Integer)
			txtView.setText((Integer)text);
		
		Toast customtoast=new Toast(activity);
		customtoast.setView(customToastroot);
		customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 250);
		customtoast.setDuration(duration);
		customtoast.show();
	}
	public static void showToast(Context activity,Object text,int duration){
		 showToast(activity,text, null,null , duration);
	}
    public static void showToast(Context activity,Object text){showToast(activity,text, null,null, Toast.LENGTH_SHORT); }
    public static void showToastSuccess(Context activity,Object text){showToast(activity,text, R.drawable.success , R.drawable.mhtalth_toast_blue, Toast.LENGTH_SHORT); }
    public static void showToastError(Context activity,Object text){showToast(activity,text,  R.drawable.error , R.drawable.mhtalth_toast_red, Toast.LENGTH_SHORT);}
    public static void showToastWarnaing(Context activity,Object text){showToast(activity,text,  R.drawable.warning , R.drawable.mhtalth_toast_yellow, Toast.LENGTH_SHORT);}


}
