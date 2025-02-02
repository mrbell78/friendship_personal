package ngo.friendship.satellite.ui.reports;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.BenefRegistrationReportLayoutBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.BeneficiaryRegistrationState;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.views.DialogView;


/**
 * <p>Display Beneficiary registration report</p>
 * <b>Create Date : 29th May 2014</b><br/>
 * <b>Last Update : 16th December 2015</b>
 * <b>Last Update : 28th August 2022</b>
 * @author Kayum Hossan
 * @author Mohammed Jubayer
 * @author Md.Yesain Ali
 */
public class BeneficiaryRegistrationReportActivity extends BaseActivity implements OnClickListener, OnCompleteListener{

BenefRegistrationReportLayoutBinding binding;
	String activityPath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.loadApplicationData(this);
		binding = BenefRegistrationReportLayoutBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		//binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
//			binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
//			binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
//		});
//		binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
//			binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
//			binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
//		});

		if (getIntent().getExtras() != null) {
			activityPath = getIntent().getExtras().getString(ActivityDataKey.ACTIVITY_PATH);
			setTitle("" + activityPath);
		}


		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		if (SystemUtility.isConnectedToInternet(this)) {
				RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.REGISTRATION_STATS, Constants.MODULE_DATA_GET);
				CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
				task.setCompleteListener(this);
				task.execute();
			} else {
				SystemUtility.openInternetSettingsActivity(this);
			}
//		MHealthTask tsk=new MHealthTask(this,Task.REGISTRATION_STATS, R.string.retrieving_data, R.string.please_wait);
//		tsk.setCompleteListener(this);
//		tsk.execute();
	}


	@Override
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(LanguageContextWrapper.wrap(context, AppPreference.getString(context, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)));
	}
	
	/**
	 * Display alert dialog with one button.
	 *
	 * @param msg will be displayed in the message section of the dialog
	 * @param imageId is the image drawable id which will be displayed at the dialog's title
	 * @param messageColor is the color id of the dialog's message
	 * 
	 * @author Kayum Hossan
	 */
	private void showOneButtonDialog(String msg, final int imageId, int messageColor){
		
		HashMap<Integer ,Object> buttonMap =new HashMap<Integer, Object>(); 
		buttonMap.put(1, R.string.btn_close);
		DialogView exitDialog= new DialogView(this, R.string.dialog_title,msg,messageColor, imageId, buttonMap);
		exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {
			
			@Override
			public void onDialogButtonClick(View view) {
				// TODO Auto-generated method stub
				
			}
		});
		exitDialog.show();
	}
	
	/**
	 * Show report .
	 *
	 * @param benefRegistrationReport the benef registration report
	 * @author Kayum Hossan
	 */
	private void showData(ArrayList<BeneficiaryRegistrationState> benefRegistrationReport)
	{
		binding.llReportRowContainer.removeAllViews();
		if(benefRegistrationReport == null || benefRegistrationReport.size() == 0)
		{
			//showOneButtonDialog(getResources().getString(R.string.data_not_available), R.drawable.information, Color.BLACK);
binding.dataNotFound.serviceNotFound.setVisibility(View.VISIBLE);
			return;
		}else{
			binding.dataNotFound.serviceNotFound.setVisibility(View.GONE);
		}
		
		int totalReg = 0;
		for(BeneficiaryRegistrationState regInfo : benefRegistrationReport)
		{
			View v = View.inflate(this, R.layout.benef_registration_report_row, null);

			
			TextView tvNameOfMonth = v.findViewById(R.id.tv_name_of_month);
			tvNameOfMonth.setText(regInfo.getMonth());
			
			TextView tvNumOfReg = v.findViewById(R.id.tv_no_of_reg);
			tvNumOfReg.setText(""+regInfo.getQuantity());
			
			totalReg += regInfo.getQuantity();
			binding.llReportRowContainer.addView(v);
		}
		TextView tvTotalNumReg = findViewById(R.id.tv_num_of_reg);
		tvTotalNumReg.setText(TextUtility.format("%s %d",getResources().getString(R.string.total_number_of_reg), totalReg));
	}
	

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_close:
			this.finish();
			break;

		case R.id.btn_retrieve_data:
			if(SystemUtility.isConnectedToInternet(this))
			{
				RequestData request =new RequestData(RequestType.FCM_REPORT_SERVICE ,RequestName.REGISTRATION_STATS ,Constants.MODULE_DATA_GET);
				CommiunicationTask task=new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
				task.setCompleteListener(this);
				task.execute();
			}
			else
				SystemUtility.openInternetSettingsActivity(this);
			break;
		default:
			break;
		}
		
	}

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
					MHealthTask tsk=new MHealthTask(this,Task.REGISTRATION_STATS, R.string.retrieving_data, R.string.please_wait);
					tsk.setParam(response);
					tsk.setCompleteListener(this);
					tsk.execute();
				}
			}
			
		}else if(msg.getData().getString(TaskKey.NAME).equals(TaskKey.MHEALTH_TASK)){
			if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
				String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
				App.showMessageDisplayDialog(this, getResources().getString(R.string.saving_error), R.drawable.error, Color.RED);
			} else {
				showData((ArrayList<BeneficiaryRegistrationState>) msg.getData().getSerializable(TaskKey.DATA0));
			}
		}
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.common_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
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

}
