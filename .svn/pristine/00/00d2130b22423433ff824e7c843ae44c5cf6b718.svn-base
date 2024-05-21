package ngo.friendship.satellite.ui.reports;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.FileOperaion;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.DialogView;

// TODO: Auto-generated Javadoc

/**
 * The Class UnDoneTimeScheduleListActivity.
 */
public class UnDoneTimeScheduleListActivity extends AppCompatActivity implements OnClickListener, OnCompleteListener{

	//ProgressDialog dlog;
	String activityPath;
	ArrayList<UserScheduleInfo> scheduleList;
	LinearLayout llRowContainer;

	EditText etHhNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.loadApplicationData(this);
		setContentView(R.layout.schedule_layout);
	
		View v = findViewById(R.id.btn_retrieve_data);
		v.setOnClickListener(this);
		
		llRowContainer= findViewById(R.id.ll_schedule_row_container);
		
		v = findViewById(R.id.btn_close);
		v.setOnClickListener(this);


		findViewById(R.id.ll_serach).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_search).setOnClickListener(this);
		etHhNumber=findViewById(R.id.et_hh_number);

		retriveAndShow("");

	}


	private void retriveAndShow( String data){

		if(data==null || data.trim().length()==0) etHhNumber.setText("");

		MHealthTask tsk=new MHealthTask(this, Task.RETRIEVE_SCHEDULE_LIST, R.string.retrieving_data, R.string.please_wait);
		if(data!=null && data.trim().length()>0) {
			tsk.setParam(-30,(String)data);
		}else{
			tsk.setParam(-30,"");
		}
		tsk.setCompleteListener(this);
		tsk.execute();
	}





	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if( App.getContext().getAppSettings()==null)
			App.getContext().readAppSettings(this);
		

		if(!SystemUtility.isAutoTimeEnabled(this)){
			SystemUtility.openDateTimeSettingsActivity(this);
		}
	
		
		activityPath=Utility.setActivityPath(this, R.string.undone_time_schedule);
	}
	
	

	

	/**
	 * Display alert dialog with one button.
	 *
	 * @param msg will be displayed in the message section of the dialog
	 * @param imageId is the image drawable id which will be displayed at the dialog's title
	 * @param messageColor is the color id of the dialog's message
	 */
	private void showOneButtonDialog(String msg, final int imageId, int messageColor){
		     HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
	        buttonMap.put(1, R.string.btn_close);
	        DialogView exitDialog = new DialogView(this, R.string.dialog_title, msg, messageColor, imageId, buttonMap);
	        exitDialog.show();
	}
	
	/**
	 * Display Schedule list.
	 *
	 * @param scheduleList The Schedule List
	 */
	private void showData(ArrayList<UserScheduleInfo> scheduleList)
	{
		llRowContainer.removeAllViews();
		if(scheduleList == null || scheduleList.size() == 0)
		{
			showOneButtonDialog(getResources().getString(R.string.data_not_available), R.drawable.information, Color.BLACK);
			return;
		}
		String dateStr = null;
		for(UserScheduleInfo schedInfo : scheduleList)
		{
			if(dateStr == null || !dateStr.equalsIgnoreCase(schedInfo.getScheduleDateStr()))
			{
				dateStr = schedInfo.getScheduleDateStr();
				
				/*
				 * Set date
				 */
				TextView tv = new TextView(this);
				LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				tv.setPadding(5, 5, 5, 5);
				tv.setLayoutParams(params);
				tv.setBackgroundColor(Color.RED);
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(18);
				tv.setText(schedInfo.getScheduleDateStr());
				llRowContainer.addView(tv);
			}
			View view;
			if(schedInfo.getBeneficiaryCode() != null)
			{
				view = View.inflate(this, R.layout.patient_followup_row, null);
				
				if(schedInfo.getSystemChangedDate() > 0)
				{
					view.setBackgroundColor(Color.YELLOW);
				}
				
				ImageView ivBenefImage = view.findViewById(R.id.iv_beneficiary);
				if(FileOperaion.isExist(schedInfo.getBeneficiaryImagePath())){
					ivBenefImage.setImageBitmap(FileOperaion.decodeImageFile(schedInfo.getBeneficiaryImagePath(), 50)); 
				}
				else{
					ivBenefImage.setImageResource(R.drawable.ic_default_man);
				}
					
				
				TextView tvName = view.findViewById(R.id.tv_beneficiary_name);
				tvName.setText(schedInfo.getBeneficiaryName());
				
				TextView tvCode = view.findViewById(R.id.tv_beneficiary_code);
				tvCode.setText(schedInfo.getBeneficiaryCode());
				
				TextView tvDesc = view.findViewById(R.id.tv_followup_desc);
				tvDesc.setText(schedInfo.getDescription());
			}
			else
			{
				view = View.inflate(this, R.layout.fcm_event_row, null);
				
				TextView tvTitle = view.findViewById(R.id.tv_schedule_desc);
				tvTitle.setText(schedInfo.getDescription());
				
				TextView tvType = view.findViewById(R.id.tv_schedule_type);
				tvType.setText(schedInfo.getScheduleType());
			}
			llRowContainer.addView(view);
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_close:
			this.finish();
			
			break;
			case R.id.btn_search:
				retriveAndShow(etHhNumber.getText().toString());
				break;
		case R.id.btn_retrieve_data:
			if(SystemUtility.isConnectedToInternet(this))
			{

				RequestData request =new RequestData(RequestType.FCM_REPORT_SERVICE ,RequestName.UNDONE_SCHEDULE,Constants.MODULE_DATA_GET );
				CommiunicationTask task=new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
				task.setCompleteListener(this);
				task.execute();
			}
			else
			{
				SystemUtility.openInternetSettingsActivity(this);
			}
			break;

		default:
			break;
		}
		
	}
	
	
	@Override
	public void onBackPressed() {}
	@Override
	public void onComplete(Message msg) {
		if(msg.getData().getString(TaskKey.NAME).equals(TaskKey.COMMUNICATION_TASK)){
			if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
				String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
				App.showMessageDisplayDialog(this, getResources().getString(R.string.network_error), R.drawable.error, Color.RED);
			} else {
				ResponseData response=(ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
				if(response.getResponseCode().equalsIgnoreCase("00")){
					App.showMessageDisplayDialog(this, response.getErrorCode()+"-"+response.getErrorDesc(), R.drawable.error, Color.RED);
				}else{

					etHhNumber.setText("");
					MHealthTask tsk=new MHealthTask(this, Task.REPORT_SCHEDULE_LIST, R.string.retrieving_data, R.string.please_wait);
					tsk.setParam(response,-30);
					tsk.setCompleteListener(this);
					tsk.execute();
				}
			}
			
		}else if(msg.getData().getString(TaskKey.NAME).equals(TaskKey.MHEALTH_TASK)){
			if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
				String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
				App.showMessageDisplayDialog(this, getResources().getString(R.string.saving_error), R.drawable.error, Color.RED);
			} else {
				showData((ArrayList<UserScheduleInfo>) msg.getData().getSerializable(TaskKey.DATA0));
			}
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		App.getContext().onStartActivity(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		App.getContext().onStartActivity(this);
	}



	@Override
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(LanguageContextWrapper.wrap(context, AppPreference.getString(context, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)));
	}
}
