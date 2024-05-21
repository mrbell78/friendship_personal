package ngo.friendship.satellite.views;

import android.app.Dialog;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.Calendar;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.EnglishMonthInfo;
import ngo.friendship.satellite.model.LocalMonthInfo;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.utility.ModelProvider;
import ngo.friendship.satellite.utility.TextUtility;

// TODO: Auto-generated Javadoc

/**
 * The Class LMPDateView.
 */
public class LMPDateView extends  QuestionView{
	
	/** The date picker. */
	private DatePicker datePicker;
	
	/** The default date. */
	private String defaultDate=null;
	
	/** The tv duration. */
	private TextView tvDuration;
	
	/** The month list. */
	ArrayList<LocalMonthInfo> monthList;
	
	/**
	 * Instantiates a new LMP date view.
	 *
	 * @param context the context
	 * @param question the question
	 * @param monthList the month list
	 */
	public LMPDateView(Context context, Question question, ArrayList<LocalMonthInfo> monthList) {
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


		datePicker = new DatePicker(context);
		datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new OnDateChangedListener() {

			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				showTentativeEDDDate(year,monthOfYear,dayOfMonth);
			}
		});

		if(Build.VERSION.SDK_INT> Build.VERSION_CODES.GINGERBREAD_MR1)
			datePicker.setCalendarViewShown(false);

		if(question.isReadonly())
			datePicker.setClickable(false);

		//// Set user input
		String dateStr = getInputData();
		if(question.getUserInput() != null && question.getUserInput().size()>0)
		{
			String userInputdate = question.getUserInput().get(0);
			if(userInputdate!=null && !userInputdate.equalsIgnoreCase("")) 
			{  
				//			strings=defaultDate.split("/");

				dateStr = userInputdate;
			}
		}
		else if(question.getDefaultValue() != null && question.getDefaultValue().size()>0) /// Set default value
		{
			defaultDate = question.getDefaultValue().get(0);
			if(defaultDate!=null && !defaultDate.equalsIgnoreCase(""))
			{

				dateStr = defaultDate;
			}
		}

		setInputData(dateStr);

		datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);

		//		if(question.getUserName().equals("B_DOB"))
		//			datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
//		if(question.getUserName().equals("NEXT_FOLLOWUP_DATE"))
//		{
//			datePicker.setMinDate(Calendar.getInstance().getTimeInMillis()-10000);
//			/// Some second has to be deducted from current time. 
//			///Because Calendar.getInstance().getTimeInMillis() call gets some millisecond higher than the current time we assumed
//		}

		
		LinearLayout llViewContainer = new LinearLayout(context);
		llViewContainer.setLayoutParams(params);
		llViewContainer.setOrientation(LinearLayout.VERTICAL);
		llViewContainer.addView(datePicker);
		

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
		llViewContainer.addView(tvDuration);

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
			return TextUtility.format("%d-%02d-%02d", yy,(mm+1),dd);
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
	
	/** The et month. */
	EditText etMonth;
	
	/** The et year. */
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


		Log.e("daa", engDay+"/"+(engMonthIndex+1)+"/"+engYear);
		setInputData(engDay+"/"+(engMonthIndex+1)+"/"+engYear);

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
				int numberOfStartMonthDay =ModelProvider.getNumberDayInMonth(monthInfo.getStartMonthIndex());
				localDate = numberOfStartMonthDay - monthInfo.getStartDay() + day + 1;
				currentMonthIndex = i;
			}
			i++;
		}
		etDay.setText(""+localDate);
		etMonth.setText(monthName);
	}

	/**
	 * Show expected delivery date from starting date of last menstrual cycle. The date is 280 days from starting date of last menstrual cycle
	 * @param yy The year of starting date of last menstrual cycle
	 * @param mm The month of starting date of last menstrual cycle
	 * @param dd The day of starting date of last menstrual cycle
	 */
	private void showTentativeEDDDate(int yy, int mm, int dd)
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, yy);
		cal.set(Calendar.MONTH, mm);
		cal.set(Calendar.DAY_OF_MONTH, dd);
		
		cal.add(Calendar.DAY_OF_YEAR, 280);
		ArrayList<EnglishMonthInfo> monthList = ModelProvider.getMonthList();
		tvDuration.setText(context.getString(R.string.expected_delivery_date)+": "+cal.get(Calendar.DAY_OF_MONTH)+" "+monthList.get(cal.get(Calendar.MONTH)).getName()+" "+cal.get(Calendar.YEAR));

	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#setInputData(java.lang.String)
	 */
	@Override
	public void setInputData(String data) {
		//		defaultDate=data;
		if(datePicker==null) return ;
		int dd=-1;
		int yy=-1;
		int mm=-1;
		try {
			String[] strings=data.split("-");
			dd=Integer.parseInt(strings[2]);
			mm=Integer.parseInt(strings[1]);
			yy=Integer.parseInt(strings[0]);

			mm = mm - 1;

		}
		catch(Exception exception)
		{

		}

		if(dd>0&&yy>0&&mm>=0)
		{
			//Log.e("JUBA DATE ","Y "+yy+" M"+ mm+ " D "+dd);
			datePicker.updateDate(yy, mm, dd);
			showTentativeEDDDate(yy, mm, dd);

		}
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputDataList()
	 */
	@Override
	public ArrayList<String> getInputDataList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#isValid()
	 */
	@Override
	public boolean isValid(boolean isSingleForm) {
		if(getInputData()==null)
		{
			return false;
		}

		if(question.getName().equals("B_DOB"))
		{
			Calendar calender = Calendar.getInstance();
			long currentDate = calender.getTimeInMillis();

			calender.set(Calendar.YEAR, datePicker.getYear());
			calender.set(Calendar.MONTH, datePicker.getMonth());
			calender.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
			calender.set(Calendar.HOUR_OF_DAY, 0);
			calender.set(Calendar.MINUTE, 0);

			long dob = calender.getTimeInMillis();

			if(dob > currentDate)
			{
				AppToast.showToast(context, R.string.incalid_date);
				return false;
			}

		}
		return true;
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
