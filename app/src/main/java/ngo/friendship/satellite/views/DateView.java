package ngo.friendship.satellite.views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.model.LocalMonthInfo;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.utility.ModelProvider;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;

// TODO: Auto-generated Javadoc

/**
 * The Class DateView.
 */
public class DateView extends  QuestionView{

	/** The date picker. */
	private DatePicker datePicker;

	/** The default date. */
	private String defaultDate=null;

	/** The tv duration. */
	private TextView tvDuration;

	/** The month list. */
	ArrayList<LocalMonthInfo> monthList;

	/**
	 * Instantiates a new date view.
	 *
	 * @param context the context
	 * @param question the question
	 * @param monthList the month list
	 */
	public DateView(Context context, Question question, ArrayList<LocalMonthInfo> monthList) {
		super(context, question, monthList);	
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
		monthList = (ArrayList<LocalMonthInfo>)this.dataSet;

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		tvDuration = new TextView(context);
		tvDuration.setLayoutParams(params);
		tvDuration.setTextSize(18);
		tvDuration.setGravity(Gravity.CENTER);


		datePicker = new DatePicker(context,null,android.R.style.Widget_DatePicker);
		datePicker.setId(R.id.question_date_view);
		datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				showDuration(year,monthOfYear,dayOfMonth);
			}
		});

		if(Build.VERSION.SDK_INT> Build.VERSION_CODES.GINGERBREAD_MR1)
			datePicker.setCalendarViewShown(false);

		
		// set validation
		if(question.getValidationList()!=null && question.getValidationList().size()==1&& question.getValidationList().get(0).getBaseDate()!=null && question.getValidationList().get(0).getValue().trim() !=null )
		{
			Calendar  fromCalender =null;
			Calendar toCalender=null;
			
			String fromDate =question.getValidationList().get(0).getBaseDate().trim();
			String toDate =question.getValidationList().get(0).getValue().trim();

			if(Utility.isValidDate(fromDate, Constants.DATE_FORMAT_YYYY_MM_DD))
			{
				try {
					fromCalender =Calendar.getInstance();
					fromCalender.setTimeInMillis(Utility.getMillisecondFromDate(fromDate,Constants.DATE_FORMAT_YYYY_MM_DD));
					datePicker.setMinDate(fromCalender.getTimeInMillis()-10000);
				} catch (ParseException e) { 

				}
				
			}else if(fromDate.contains("<CURRENT_DATE>")){
				int day=0;
			    fromCalender =Calendar.getInstance();
				
				if(fromDate.length()>"<CURRENT_DATE>".length()){
					
					try {
						if(fromDate.contains("+"))
							day=Integer.parseInt(fromDate.substring(fromDate.indexOf("+")+1));
						else if(fromDate.contains("-"))
							day=day-Integer.parseInt(fromDate.substring(fromDate.indexOf("-")+1));

					} catch (Exception e) {
                      Log.e("ERROR ", e.getMessage());
					}
				}
				fromCalender.add(Calendar.DATE, day);
				datePicker.setMinDate(fromCalender.getTimeInMillis()-10000);
			}
			
			if(toDate!=null && toDate.length()>0){
				

				if(Utility.isValidDate(toDate, Constants.DATE_FORMAT_YYYY_MM_DD))
				{
					try {
						toCalender =Calendar.getInstance();
						toCalender.setTimeInMillis(Utility.getMillisecondFromDate(toDate,Constants.DATE_FORMAT_YYYY_MM_DD));
						datePicker.setMaxDate(toCalender.getTimeInMillis()+10000);
					} catch (ParseException e) { 
					   
					}
					
				}
				else if(toDate.contains("<CURRENT_DATE>")){
				  	int toDay=0;
				    toCalender =Calendar.getInstance();
					if(toDate.length()>"<CURRENT_DATE>".length()){
						
						try {
							
							if(toDate.contains("+"))
								toDay=Integer.parseInt(toDate.substring(toDate.indexOf("+")+1));
							else if(toDate.contains("-"))
								toDay=toDay-Integer.parseInt(toDate.substring(toDate.indexOf("-")+1));
							
						} catch (Exception e) {

						}
					}
					toCalender.add(Calendar.DATE, toDay);
					datePicker.setMaxDate(toCalender.getTimeInMillis()+10000);
				}
			    else if(fromCalender!=null ){
					
					try {
						
						int	d=Integer.parseInt(defaultDate.substring(defaultDate.indexOf("+")+1));
						toCalender= Calendar.getInstance();
						toCalender.add(Calendar.DATE,d);
						datePicker.setMaxDate(toCalender.getTimeInMillis()+10000);
					} catch (Exception e) {

					}
				}	
			}
			
			

			/// Some second has to be deducted from current time. 
			/// Because Calendar.getInstance().getTimeInMillis() call gets some millisecond higher than the current time we assumed
		}
		
		
		
		
		//// Set user input
		
		if(question.getUserInput() != null && question.getUserInput().size()==1)
		{
			String userInputdate = question.getUserInput().get(0);
			if(userInputdate!=null && !userInputdate.equalsIgnoreCase("")) 
			{ 
				setInputData(userInputdate);
			}
		}
		else if(question.getDefaultValue() != null && question.getDefaultValue().size()==1) /// Set default value
		{
			defaultDate = question.getDefaultValue().get(0).trim();

			if(defaultDate!=null && defaultDate.length()>0)
			{
				if(Utility.isValidDate(defaultDate, Constants.DATE_FORMAT_YYYY_MM_DD))
				{
					setInputData(defaultDate);
				}else if(defaultDate.trim().contains("<CURRENT_DATE>")){
					int day=0;
					Calendar  ca =Calendar.getInstance();
					
					if(defaultDate.trim().length()>"<CURRENT_DATE>".length()){
						
						try {
							if(defaultDate.trim().contains("+"))
								day=Integer.parseInt(defaultDate.substring(defaultDate.indexOf("+")+1));
							else if(defaultDate.trim().contains("-"))
								day=day-Integer.parseInt(defaultDate.substring(defaultDate.indexOf("-")+1));

						} catch (Exception e) {

						}
					}
					ca.add(Calendar.DATE, day);
					setInputData(Utility.getDateFromMillisecond(ca.getTimeInMillis(), Constants.DATE_FORMAT_YYYY_MM_DD));
				} 

			}
		}

		datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);	
		datePicker.setEnabled(!question.isReadonly());
		
		LinearLayout llViewContainer = new LinearLayout(context);
		llViewContainer.setLayoutParams(params);
		llViewContainer.setOrientation(LinearLayout.VERTICAL);
		llViewContainer.addView(datePicker);
		llViewContainer.addView(tvDuration);

		if(monthList.size() > 0)
		{
			Button btnLocal = new Button(context);
			btnLocal.setLayoutParams(params);
			btnLocal.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					displayLocalDatePickerDialog();
				}
			});
			btnLocal.setText(R.string.select_local_date);
			llViewContainer.addView(btnLocal);
		}
        
		ll_body.addView(llViewContainer);	
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputData()
	 */
	@Override
	public String getInputData() {
		if(datePicker==null)
			return null;

		int dd= datePicker.getDayOfMonth();
		int mm=datePicker.getMonth();
		int yy=datePicker.getYear();
		if(dd>0&&yy>0&&mm>=0)
		{
			return TextUtility.format("%d-%02d-%02d", yy,(mm+1),dd);//yy+"-"+(mm+1)+"-"+dd;
		}
		return null;
	}

	/** The current month index. */
	int currentMonthIndex = 0;
	/**
	 * Display workout confirmation alert dialog.
	 */
	Handler handler = new Handler();

	/** The year down runnable. */
	Runnable yearDownRunnable = new Runnable() {

		@Override
		public void run() {
			decreaseYear(etYear);
			handler.postDelayed(yearDownRunnable, 100);

		}
	};

	/** The year up runnable. */
	Runnable yearUpRunnable = new Runnable() {

		@Override
		public void run() {
			increaseYear(etYear);
			handler.postDelayed(yearUpRunnable, 100);

		}
	};

	/** The month down runnable. */
	Runnable monthDownRunnable = new Runnable() {

		@Override
		public void run() {
			decreaseMonth(etYear, etMonth, etDay);
			handler.postDelayed(monthDownRunnable, 100);

		}
	};

	/** The month up runnable. */
	Runnable monthUpRunnable = new Runnable() {

		@Override
		public void run() {
			increaseMonth(etYear, etMonth, etDay);
			handler.postDelayed(monthUpRunnable, 100);

		}
	};

	/** The day down runnable. */
	Runnable dayDownRunnable = new Runnable() {

		@Override
		public void run() {
			decreaseDay(etYear, etMonth, etDay);
			handler.postDelayed(dayDownRunnable, 100);

		}
	};

	/** The day up runnable. */
	Runnable dayUpRunnable = new Runnable() {

		@Override
		public void run() {
			increaseDay(etYear, etMonth, etDay);
			handler.postDelayed(dayUpRunnable, 100);

		}
	};

	/** The et day. */
	EditText etDay;

	EditText etMonth;

	EditText etYear;

	/**
	 * Display local date picker dialog.
	 */
	private void displayLocalDatePickerDialog()
	{


		final Dialog dialog = new Dialog(context, R.style.CustomDialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		View view = View.inflate(context, R.layout.local_date_picker, null);
		dialog.setContentView(view);

		etDay = view.findViewById(R.id.et_day);
		etMonth = view.findViewById(R.id.et_month);
		etYear = view.findViewById(R.id.et_year);
		setDateToLocalDatePicker(etDay, etMonth, etYear);

		View v = view.findViewById(R.id.btn_ok);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				convertLocalDateToEnglish(etDay, etYear, currentMonthIndex);
				dialog.dismiss();
			}
		});

		v = view.findViewById(R.id.btn_close);
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();

			}
		});

		/** Year Down Button */
		v = view.findViewById(R.id.btn_year_minus);

		if(question.isReadonly())
			v.setEnabled(false);
		
		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				if(arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					decreaseYear(etYear);
					handler.postDelayed(yearDownRunnable, 1000);
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					handler.removeCallbacks(yearDownRunnable);
				}
				return false;
			}
		});

		/** Year Up Button */
		v = view.findViewById(R.id.btn_year_plus);
		if(question.isReadonly())
			v.setEnabled(false);
		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				if(arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					increaseYear(etYear);
					handler.postDelayed(yearUpRunnable, 1000);
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					handler.removeCallbacks(yearUpRunnable);
				}
				return false;
			}
		});

		/** Month Down Button */
		v = view.findViewById(R.id.btn_month_minus);
		if(question.isReadonly())
			v.setEnabled(false);
		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				if(arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					decreaseMonth(etYear, etMonth, etDay);
					handler.postDelayed(monthDownRunnable, 1000);
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					handler.removeCallbacks(monthDownRunnable);
				}
				return false;
			}
		});

		/** Month up Button */
		v = view.findViewById(R.id.btn_month_plus);
		if(question.isReadonly())
			v.setEnabled(false);
		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				if(arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					increaseMonth(etYear, etMonth, etDay);
					handler.postDelayed(monthUpRunnable, 1000);
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					handler.removeCallbacks(monthUpRunnable);
				}
				return false;
			}
		});

		/** Day Down Button */
		v = view.findViewById(R.id.btn_day_minus);
		if(question.isReadonly())
			v.setEnabled(false);
		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				if(arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					decreaseDay(etYear, etMonth, etDay);
					handler.postDelayed(dayDownRunnable, 1000);
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					handler.removeCallbacks(dayDownRunnable);
				}
				return false;
			}
		});

		/** Day up Button */
		v = view.findViewById(R.id.btn_day_plus);
		if(question.isReadonly())
			v.setEnabled(false);
		v.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				if(arg1.getAction() == MotionEvent.ACTION_DOWN)
				{
					increaseDay(etYear, etMonth, etDay);
					handler.postDelayed(dayUpRunnable, 1000);
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					handler.removeCallbacks(dayUpRunnable);
				}
				return false;
			}
		});

		dialog.setCancelable(false);
		dialog.show();
		//		workoutConfirmAlert = dialog;
		//
		//		DisplayMetrics metrics = new DisplayMetrics();
		//		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		//		/** Set the size of alert dialog */
		//		dialog.getWindow().setLayout((int)(metrics.widthPixels*.70), WindowManager.LayoutParams.WRAP_CONTENT);


	}

	/**
	 * Convert local date to english.
	 *
	 * @param etDay the et day
	 * @param etYear the et year
	 * @param monthIndex the month index
	 */
	private void convertLocalDateToEnglish(EditText etDay, EditText etYear, int monthIndex)
	{
		int localDay = Integer.parseInt(etDay.getText().toString());
		int localYear = Integer.parseInt(etYear.getText().toString());
		int engMonthIndex = monthList.get(monthIndex).getStartMonthIndex();
		int engDay = monthList.get(monthIndex).getStartDay() + localDay - 1;
		int engYear = 0;
		if(engDay > ModelProvider.getNumberDayInMonth(monthList.get(monthIndex).getStartMonthIndex()))
		{
			engDay = engDay - ModelProvider.getNumberDayInMonth(monthList.get(monthIndex).getStartMonthIndex());
			engMonthIndex = monthList.get(monthIndex).getEndMonthIndex();
		}

		if(engMonthIndex < 3 || (engMonthIndex == 3 && engDay < 14))
		{
			engYear = localYear + 594;
		}
		else
			engYear = localYear + 593;


		//Log.e("daa", engDay+"/"+(engMonthIndex+1)+"/"+engYear);
		setInputData(engYear+"-"+(engMonthIndex+1)+"-"+engDay);

	}

	/**
	 * Increase year.
	 *
	 * @param etYear the et year
	 */
	private void increaseYear(EditText etYear)
	{
		int year = Integer.parseInt(etYear.getText().toString());
		etYear.setText(""+(year+1));
	}

	/**
	 * Decrease year.
	 *
	 * @param etYear the et year
	 */
	private void decreaseYear(EditText etYear)
	{
		int year = Integer.parseInt(etYear.getText().toString());
		if(year>1)
		{
			etYear.setText(""+(year-1));
		}
	}

	/**
	 * Increase month.
	 *
	 * @param etYear the et year
	 * @param etMonth the et month
	 * @param etDay the et day
	 */
	private void increaseMonth(EditText etYear, EditText etMonth, EditText etDay)
	{
		if(currentMonthIndex == (monthList.size()-1))
		{
			currentMonthIndex = 0;
			increaseYear(etYear);
		}
		else
		{
			currentMonthIndex = currentMonthIndex + 1;
		}
		etMonth.setText(monthList.get(currentMonthIndex).getName());

		int day = Integer.parseInt(etDay.getText().toString());
		if(day > monthList.get(currentMonthIndex).getNumberOfDays())
		{
			etDay.setText(""+monthList.get(currentMonthIndex).getNumberOfDays());
		}

	}

	/**
	 * Decrease month.
	 *
	 * @param etYear the et year
	 * @param etMonth the et month
	 * @param etDay the et day
	 */
	private void decreaseMonth(EditText etYear, EditText etMonth, EditText etDay)
	{
		if(currentMonthIndex == 0)
		{
			currentMonthIndex = monthList.size()-1;
			decreaseYear(etYear);
		}
		else
		{
			currentMonthIndex = currentMonthIndex -1;
		}
		etMonth.setText(monthList.get(currentMonthIndex).getName());

		int day = Integer.parseInt(etDay.getText().toString());
		if(day > monthList.get(currentMonthIndex).getNumberOfDays())
		{
			etDay.setText(""+monthList.get(currentMonthIndex).getNumberOfDays());
		}
	}

	/**
	 * Increase day.
	 *
	 * @param etYear the et year
	 * @param etMonth the et month
	 * @param etDay the et day
	 */
	private void increaseDay(EditText etYear, EditText etMonth, EditText etDay)
	{
		int day = Integer.parseInt(etDay.getText().toString());
		if(day == monthList.get(currentMonthIndex).getNumberOfDays())
		{

			increaseMonth(etYear, etMonth, etDay);
			etDay.setText("1");

		}
		else
			etDay.setText(""+(day + 1));
	}

	/**
	 * Decrease day.
	 *
	 * @param etYear the et year
	 * @param etMonth the et month
	 * @param etDay the et day
	 */
	private void decreaseDay(EditText etYear, EditText etMonth, EditText etDay)
	{
		int day = Integer.parseInt(etDay.getText().toString());

		if(day == 1)
		{
			decreaseMonth(etYear, etMonth, etDay);
			etDay.setText(""+monthList.get(currentMonthIndex).getNumberOfDays());
		}
		else
		{
			etDay.setText(""+(day-1));
		}
	}

	/**
	 * Sets the date to local date picker.
	 *
	 * @param etDay the et day
	 * @param etMonth the et month
	 * @param etYear the et year
	 */
	private void setDateToLocalDatePicker(EditText etDay, EditText etMonth, EditText etYear)
	{
		if(this.datePicker.getMonth() < 3 ||(this.datePicker.getMonth() == 3 && this.datePicker.getDayOfMonth() < 14))
			etYear.setText(""+(this.datePicker.getYear() - 594));
		else
			etYear.setText(""+(this.datePicker.getYear() - 593));

		int day = datePicker.getDayOfMonth();
		int month = datePicker.getMonth();
		Log.e("Month", ""+month);

		String monthName = "";
		int localDate = 1;
		int i=0;
		/**
		 * Calculate the month and day
		 */
		for(LocalMonthInfo monthInfo : this.monthList)
		{
			if(month == monthInfo.getStartMonthIndex() && day >= monthInfo.getStartDay())
			{
				monthName = monthInfo.getName();
				localDate = day - monthInfo.getStartDay() + 1;
				currentMonthIndex = i;
			}
			else if(month == monthInfo.getEndMonthIndex() && day <= monthInfo.getEndDay())
			{
				monthName = monthInfo.getName();
				int numberOfStartMonthDay = ModelProvider.getNumberDayInMonth(monthInfo.getStartMonthIndex());
				localDate = numberOfStartMonthDay - monthInfo.getStartDay() + day + 1;
				currentMonthIndex = i;
			}
			i++;
		}
		etDay.setText(""+localDate);
		etMonth.setText(monthName);
	}

	/**
	 * Show Age or Remaining days for next follow up .
	 *
	 * @param yy Year
	 * @param mm Month (Start from 0)
	 * @param dd Day
	 */
	private void showDuration(int yy, int mm, int dd)
	{
		if(question.getName().equals("B_DOB"))
		{
			try {
				String ageStr = Utility.getAge(yy+"-"+(mm+1)+"-"+dd);
				if(ageStr != null)
				{
					tvDuration.setTextColor(Color.BLACK);
					tvDuration.setText(getResources().getText(R.string.age)+" "+ageStr);
				}
				else
				{
					tvDuration.setTextColor(Color.RED);
					tvDuration.setText(getResources().getString(R.string.invalid_age));
				}
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		else if(question.getName().contains("NEXT_FOLLOWUP_DATE"))
		{
			Calendar fromCalender = Calendar.getInstance();

			Calendar toCalender = Calendar.getInstance();
			toCalender.set(Calendar.YEAR, yy);
			toCalender.set(Calendar.MONTH, mm);
			toCalender.set(Calendar.DAY_OF_MONTH, dd);

			tvDuration.setText(getResources().getText(R.string.followup_pre)+" "+Utility.getNumberOfDaysBetweenDate(fromCalender.getTimeInMillis(), toCalender.getTimeInMillis())+" "+getResources().getText(R.string.followup_post));
		}
		else{


		}

	}

	
	@Override
	public void setInputData(String data) {
	    
		try{
			long mili=Long.parseLong(data);
			data=Utility.getDateFromMillisecond(mili, Constants.DATE_FORMAT_YYYY_MM_DD);
		}catch(Exception ex){
			
		}
		
		Log.e("Date setInputData", data);
		if(datePicker==null) return ;
		int dd=-1;
		int yy=-1;
		int mm=-1;
		try {
			String[] strings;
			if(data.contains("-")){
				strings=data.split("-");
				dd=Integer.parseInt(strings[2]);
				mm=Integer.parseInt(strings[1]);
				yy=Integer.parseInt(strings[0]);
			}else if(data.contains("/")){
				strings=data.split("/");
			dd=Integer.parseInt(strings[0]);
			mm=Integer.parseInt(strings[1]);
			yy=Integer.parseInt(strings[2]);
			}
			mm = mm - 1;

		}
		catch(Exception exception)
		{

		}

		if(dd>0&&yy>0&&mm>=0)
		{
			Log.e("JUBA DATE ","Y "+yy+" M"+ mm+ " D "+dd);
			datePicker.updateDate(yy, mm, dd);
			showDuration(yy, mm, dd);

		}
	}

	public ArrayList<String> getInputDataList() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isValid(boolean isSingleForm) {
        return getInputData() != null;
    }
	
	public View getInputView() {
		return datePicker;
	}

	@Override
	public void replaceBody(Object data) {
		// TODO Auto-generated method stub
		
	}
}
