package ngo.friendship.satellite.views;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.ScheduleInfo;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;

// TODO: Auto-generated Javadoc

/**
 * View to show the immunization chart
 * <br>Created Date: 25th Sept 2014
 * <br>Last update: 25th Sept 2014.
 *
 * @author Kayum Hossan
 */
public class ScheduleChardView extends QuestionView implements OnClickListener{

	/**
	 * Instantiates a new immunization chart view.
	 *
	 * @param context the context
	 * @param question the question
	 */
	ArrayList<ScheduleInfo> scheduleInfos;
	public ScheduleChardView(Context context, Question question) {
		super(context, question);
		init();
		addCaptionField();
		addHintField();
		addInputField();
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#addInputField()
	 */
	@Override
	protected void addInputField() {

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);


		LinearLayout llViewContainer = new LinearLayout(context);
		llViewContainer.setId(R.id.question_card_view);
		llViewContainer.setLayoutParams(params);
		llViewContainer.setOrientation(LinearLayout.VERTICAL);

		int i=0;
		scheduleInfos =new ArrayList<ScheduleInfo>();
		if(question.getUserInput()!= null)
		{
			try {
				scheduleInfos= JSONParser.parseScheduleInfos( question.getUserInput().get(0));
			} catch (MhealthException e) {
			}
		}

		for(ScheduleInfo scheduleInfo :scheduleInfos)
		{
			/**
			 * Row Container
			 */
			LayoutParams mainRowParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
			mainRowParams.setMargins(0, 0, 0,20);
			LinearLayout llRow = new LinearLayout(context);
			llRow.setOrientation(LinearLayout.VERTICAL);
			llRow.setLayoutParams(mainRowParams);
			llRow.setPadding(5, 5, 5, 5);
			llViewContainer.addView(llRow);

			/**
			 * Text View to show Immunization dose title
			 */
			TextView tvImmuTitle = new TextView(context);
			tvImmuTitle.setLayoutParams(params);
			tvImmuTitle.setText(scheduleInfo.getScheduleDesc());
			tvImmuTitle.setTextSize(15);
			tvImmuTitle.setTextColor(Color.BLACK);
			llRow.addView(tvImmuTitle);

			/**
			 * Layout for hold the select date button and clear date button
			 */
			LinearLayout llButtonContainer = new LinearLayout(context);
			llButtonContainer.setLayoutParams(params);
			llButtonContainer.setOrientation(LinearLayout.HORIZONTAL);
			llRow.addView(llButtonContainer);

			/**
			 * Select Date Button
			 */
			LayoutParams dateButtunParam = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
			Button btnTakenDate = new Button(context);
			btnTakenDate.setLayoutParams(dateButtunParam);
			btnTakenDate.setTextSize(16);
			btnTakenDate.setTextColor(Color.BLACK);
			btnTakenDate.setId(i);
			btnTakenDate.setTag("BTN_DATE");
			btnTakenDate.setOnClickListener(this);
			if(question.isReadonly())btnTakenDate.setEnabled(false);
			llButtonContainer.addView(btnTakenDate);


			/**
			 * Clear date button
			 */
			Button btnDateClear = new Button(context);
			LayoutParams clearButtonParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			clearButtonParam.setMargins(10, 0,0, 0);
			btnDateClear.setLayoutParams(clearButtonParam);
			btnDateClear.setBackgroundResource(R.drawable.btn_clear);
			btnDateClear.setId(i);
			btnDateClear.setTag("BTN_CLEAR_DATE");
			btnDateClear.setOnClickListener(this);
			if(question.isReadonly())btnDateClear.setVisibility(View.GONE);
			llButtonContainer.addView(btnDateClear);


			/**
			 * Check if the immunization date is present
			 */
			if(scheduleInfo.getScheduleDate()>0 )
			{
				btnTakenDate.setText(Utility.getDateFromMillisecond(scheduleInfo.getScheduleDate(), Constants.DATE_FORMAT_DD_MM_YYYY) );
			}
			else
			{
				btnTakenDate.setText(R.string.enter_date);
			}


			if(i%2 == 0)
				llRow.setBackgroundColor(Color.rgb(225, 225, 225));
			i++;
		}
		ll_body.addView(llViewContainer);


	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputData()
	 */
	@Override
	public String getInputData() {

		return null;
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#setInputData(java.lang.String)
	 */
	@Override
	public void setInputData(String data) {

	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputDataList()
	 */
	@Override
	public Object getInputDataList() {
		ArrayList<String> inputList = new ArrayList<String>();
		for(ScheduleInfo scheduleInfo : scheduleInfos)
		{
			String dateStr = "" ;
			if(scheduleInfo.getScheduleDate()>0)
			{
				dateStr = scheduleInfo.getScheduleId()+"#"+scheduleInfo.getScheduleName()+"#"+scheduleInfo.getScheduleType()+"#"+ scheduleInfo.getScheduleDate()+"#"+scheduleInfo.getReferenceId()+"#"+scheduleInfo.getScheduleDesc();
				inputList.add(dateStr);
			}
		}
		return inputList;
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#isValid()
	 */
	@Override
	public boolean isValid(boolean isSingleForm) {

		if (question.isRequired()) {
			boolean valid = false;
			for(ScheduleInfo scheduleInfo : scheduleInfos)
			{
				if(scheduleInfo.getScheduleDate()>0)
				{
					valid = true;
					break;
				}
			}

			if(!valid)
			{
				if (!isSingleForm){
					AppToast.showToast(context, R.string.input_required);
				}

			}
			return valid;
		}else{
			return true;
		}

	}

	/** The m day. */
	int mYear,mMonth, mDay;

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(final View v) {
		String tag = (String) v.getTag();

		if(tag.equals("BTN_DATE"))
		{

			if(scheduleInfos.get(v.getId()).getScheduleDate() >0 )
			{

				String dateStr = Constants.DATE_FORMAT_DD_MM_YYYY.format(new Date(scheduleInfos.get(v.getId()).getScheduleDate()));
				String [] dateSplit = dateStr.split("-");
				mDay =  Integer.parseInt(dateSplit[0]);
				mMonth =   Integer.parseInt(dateSplit[1]);
				mYear = Integer.parseInt(dateSplit[2]);


			}
			else
			{
				final Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);
			}

			DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {

					Button button = (Button)v;
					String dateStr = TextUtility.format("%02d-%02d-%d", dayOfMonth, monthOfYear+1, year);
					button.setText(dateStr);


					try {
						scheduleInfos.get(v.getId()).setScheduleDate(Utility.getMillisecondFromDate(dateStr, Constants.DATE_FORMAT_DD_MM_YYYY));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}, mYear, mMonth, mDay);
			
            if("+".equals(scheduleInfos.get(v.getId()).getDateInputRule())){
            	dpd.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
            }else if("-".equals(scheduleInfos.get(v.getId()).getDateInputRule())){
            	dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
            } 
			dpd.show();
		}
		else if(tag.equals("BTN_CLEAR_DATE"))
		{
			HashMap<Integer ,Object> buttonMap =new HashMap<Integer, Object>(); 
			buttonMap.put(1, R.string.btn_yes);
			buttonMap.put(2, R.string.btn_no);
			DialogView exitDialog= new DialogView((Activity)context, R.string.dialog_title, R.string.remove_date_confermation, R.drawable.warning, buttonMap);
			exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {

				@Override
				public void onDialogButtonClick(View view) {
					switch (view.getId()) {
					case 1:
						LinearLayout llBtnContainer = (LinearLayout) v.getParent();
						Button btnDate = (Button) llBtnContainer.getChildAt(0);
						btnDate.setText(R.string.enter_date);
						scheduleInfos.get(v.getId()).setScheduleDate(0);
						break;

					default:
						break;
					}


				}
			});
			exitDialog.show();


		}
	}

	@Override
	public View getInputView() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void replaceBody(Object data) {
		// TODO Auto-generated method stub

	}
}
