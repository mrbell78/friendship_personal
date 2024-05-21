package com.logic27.components;



import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StopWatch extends Component{


	Context context;

	private TextView tvTimer;
	private TextView tvTitle;

	private Button btnStop,btnStrart,btnReset;
	private Handler mHandler = new Handler();
	private long startTime;
	private long stopTime;
	private long elapsedTime;
	private final int REFRESH_RATE = 10;
	private String hours,minutes,seconds,milliseconds;
	private long secs,mins,hrs,msecs;
	private boolean stopped = false;
	private String title;


	public  StopWatch(Context ctx) {
		super(ctx);
		this.context = ctx;
		initLayout();
	}

	private void initLayout()
	{
		mHandler= new Handler();
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		this.setOrientation(LinearLayout.VERTICAL);
		this.setLayoutParams(param);
		this.setBackgroundColor(Color.DKGRAY);

		LayoutParams paramBody = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		paramBody.setMargins(3, 3,3, 3);
		LinearLayout  body =new LinearLayout(context);
		body.setLayoutParams(paramBody);
		body.setBackgroundColor(Color.WHITE);
		body.setOrientation(LinearLayout.VERTICAL);
		this.addView(body);
		tvTitle=new TextView(context);
		tvTitle.setLayoutParams(param);
		tvTitle.setText(getTitle());
		tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		tvTitle.setPadding(20, 20, 20, 20);
		tvTitle.setTypeface(null, Typeface.BOLD);
		tvTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


		tvTimer=new TextView(context);
		tvTimer.setLayoutParams(param);
		tvTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
		tvTimer.setPadding(20, 20, 20, 20);
		tvTimer.setText("00:00:00:00");
		tvTimer.setTypeface(null, Typeface.BOLD);
		tvTimer.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);



		LinearLayout llButtons=new LinearLayout(context);
		llButtons.setOrientation(LinearLayout.HORIZONTAL);
		llButtons.setLayoutParams(param);
		llButtons.setPadding(10,10,10,10);


		LayoutParams paramButton = new LayoutParams(0, LayoutParams.WRAP_CONTENT,1f);

		btnStrart=new Button(context);
		btnStrart.setLayoutParams(paramButton);
		btnStrart.setText("START");
		btnStrart.setTypeface(null, Typeface.BOLD);
		btnStrart.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		btnStrart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view ) {
				startClick(view);
			}
		});


		btnStop=new Button(context);
		btnStop.setLayoutParams(paramButton);
		btnStop.setText("STOP");
		btnStop.setEnabled(false);
		btnStop.setTypeface(null, Typeface.BOLD);
		btnStop.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		btnStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view ) {
				stopClick(view);
			}
		});


		btnReset=new Button(context);
		btnReset.setLayoutParams(paramButton);
		btnReset.setText("RESET");
		btnReset.setEnabled(false);
		btnReset.setTypeface(null, Typeface.BOLD);
		btnReset.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		btnReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view ) {
				resetClick(view);
			}
		});

		llButtons.addView(btnStrart);
		llButtons.addView(btnStop);
		llButtons.addView(btnReset);

		body.addView(tvTitle);
		body.addView(tvTimer);
		body.addView(llButtons);


	}



	public void startClick (View view){
		showStopButton();
		if(stopped){
			startTime = System.currentTimeMillis() - elapsedTime; 
		}
		else{
			startTime = System.currentTimeMillis();
		}
		mHandler.removeCallbacks(startTimer);
		mHandler.postDelayed(startTimer, 0);
	}

	public void stopClick (View view){
		hideStopButton();
		stopTimer();
	}

	private void stopTimer(){
		mHandler.removeCallbacks(startTimer);
		stopped = true;
	}

	public void resetClick (View view){
		stopped = false;
		tvTimer.setText("00:00:00:00");
	}

	private Runnable startTimer = new Runnable() {
		public void run() {
			elapsedTime = System.currentTimeMillis() - startTime;
			updateTimer(elapsedTime);
			mHandler.postDelayed(this,REFRESH_RATE);
		}
	};

	private void updateTimer(float time){

		secs = (long)(time/1000);
		mins = (long)((time/1000)/60);
		hrs = (long)(((time/1000)/60)/60);

		/* Convert the seconds to String 
		 * and format to ensure it has
		 * a leading zero when required
		 */
		secs = secs % 60;
		seconds=String.valueOf(secs);
		if(secs == 0){
			seconds = "00";
		}
		if(secs <10 && secs > 0){
			seconds = "0"+seconds;
		}

		/* Convert the minutes to String and format the String */

		mins = mins % 60;
		minutes=String.valueOf(mins);
		if(mins == 0){
			minutes = "00";
		}
		if(mins <10 && mins > 0){
			minutes = "0"+minutes;
		}

		/* Convert the hours to String and format the String */

		hours=String.valueOf(hrs);
		if(hrs == 0){
			hours = "00";
		}
		if(hrs <10 && hrs > 0){
			hours = "0"+hours;
		}

		/* Although we are not using milliseconds on the timer in this example
		 * I included the code in the event that you wanted to include it on your own
		 */


		milliseconds = String.valueOf((long)time);
		if(milliseconds.length()==2){
			milliseconds = "0"+milliseconds;
		}
		if(milliseconds.length()<=1){
			milliseconds = "000";
		}

		milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-1);

		/* Setting the timer text to the elapsed time */
		tvTimer.setText(hours + ":" + minutes + ":" + seconds+":"+milliseconds);
	}

	private void showStopButton(){
		btnStrart.setEnabled(false);
		btnReset.setEnabled(false);
		btnStop.setEnabled(true);
	}

	private void hideStopButton(){
		btnStrart.setEnabled(true);
		btnReset.setEnabled(true);
		btnStop.setEnabled(false);
	}


	@Override
	public ArrayList<String> getValues() {
		ArrayList<String> arrayList=null;
		if(!tvTimer.getText().toString().trim().equals("00:00:00:00"))
		{
		  arrayList=new  ArrayList<String>();
          arrayList.add(tvTimer.getText().toString());
		
		}
		stopTimer();
		return arrayList;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title=title;
		if(tvTitle!=null){
			tvTitle.setText(title);
		}
	}

	@Override
	public void setValues(ArrayList<String> values) {
		// TODO Auto-generated method stub

	}



}

