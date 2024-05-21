package com.logic27.components;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CountdownTimer extends Component{


	Context context;

	private TextView tvTimer;
	private TextView tvTitle;

	private Button btnStop,btnStrart,btnReset;
	private String hours,minutes,seconds;
	private long secs,mins,hrs;
	private boolean stopped = false;
	private String title;
	private long startTime;
	private MyCountDownTimer countDownTimer;
	private long lastStartTime;

	public  CountdownTimer(Context ctx) {
		super(ctx);
		this.context = ctx;
		startTime=0;
		lastStartTime=0;
		countDownTimer=null;
		initLayout();
	}

	private void initLayout()
	{

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
		tvTimer.setTypeface(null, Typeface.BOLD);
		tvTimer.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
		printTime(startTime);


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
		btnStop.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		btnStop.setEnabled(false);
		btnStop.setTypeface(null, Typeface.BOLD);
		btnStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view ) {
				stopClick(view);
			}
		});


		btnReset=new Button(context);
		btnReset.setLayoutParams(paramButton);
		btnReset.setText("RESET");
		btnReset.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
		btnReset.setEnabled(false);
		btnReset.setTypeface(null, Typeface.BOLD);
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
		if(startTime>0){
			showStopButton();
			countDownTimer = new MyCountDownTimer(startTime, 1000);
			countDownTimer.start();
		}
	}

	public void stopClick (View view){
		hideStopButton();
		if(countDownTimer!=null){
			countDownTimer.cancel();
		}

	}

	public void resetClick (View view){
		startTime=lastStartTime;
		printTime(startTime);
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
		ArrayList<String> arrayList=new  ArrayList<String>();
		arrayList.add(tvTimer.getText().toString().trim());
		hideStopButton();
	    if(countDownTimer!=null){
			countDownTimer.cancel();
		}
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
		startTime=parseLong(values.get(0));
		lastStartTime=startTime;
		printTime(startTime);
	}


	public class MyCountDownTimer extends CountDownTimer {
		public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			startTime=0;
			printTime(startTime);
            try {
				playSound();
				startVibration();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		@Override
		public void onTick(long millisUntilFinished) {
			startTime=millisUntilFinished;
			printTime(startTime);
		}
	}
	
	private void startVibration()
	{
	 Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
	 long pattern[]={0,500,300,500,300,400};
	 vibrator.vibrate(pattern, -1);
	}
	MediaPlayer mMediaPlayer;
	public void playSound() throws IllegalArgumentException,  SecurityException, IllegalStateException,IOException 
	{
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	    mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setDataSource(context, soundUri);
		final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
			mMediaPlayer.setLooping(false);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		}
	}

	public String printTime(long millisUntilFinished){
		secs = millisUntilFinished/1000;
		mins = (millisUntilFinished/1000)/60;
		hrs = ((millisUntilFinished/1000)/60)/60;

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
        String str=hours + ":" + minutes + ":" + seconds;   
		tvTimer.setText(str);
		return str;
	}

    @Override
    protected void finalize() throws Throwable {
  
    	if( mMediaPlayer != null)
		{
    		mMediaPlayer.stop();
    		mMediaPlayer.release();
		}

    	if(countDownTimer!=null){
			countDownTimer.cancel();
		}
    	super.finalize();

    }


}

