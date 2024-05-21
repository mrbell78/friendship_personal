package ngo.friendship.satellite.views;

/**
 * @author Kayum Hossan
 * Description: Sound recorder view
 * Created Date: 10th March 2014
 * Last update: 10Th March 2014
 * */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.model.Question;

// TODO: Auto-generated Javadoc

/**
 * The Class AudioRecorderView.
 */
public class AudioRecorderView extends QuestionView {


	/** The temp audio file path. */
	String tempAudioFilePath = null;
	
	/** The audio file path. */
	String audioFilePath = null;

	/** The m player. */
	MediaPlayer mPlayer = null;
	
	/** The btn play. */
	Button btnPlay;
	
	/** The btn record voice. */
	Button btnRecordVoice;

	/** The current resource id. */
	int currentResourceId;

	/** The activity. */
	Activity activity;

	/** Handler to toggle recorder button image while recording. */
	Handler handler = new Handler();
	
	/** The runnable. */
	Runnable runnable = new Runnable() {

		@Override
		public void run() {

			if(currentResourceId == R.drawable.btn_mic_normal)
			{
				setButtonBackground(R.drawable.btn_mic_hover);
			}
			else
			{
				setButtonBackground(R.drawable.btn_mic_normal);
			}
			handler.postDelayed(runnable, 500);
		}
	};
	
	/**
	 * Sets the button background.
	 *
	 * @param id the new button background
	 */
	private void setButtonBackground(int id)
	{
		btnRecordVoice.setBackgroundResource(id);
		currentResourceId = id;
	}

	/**
	 * Instantiates a new audio recorder view.
	 *
	 * @param context the context
	 * @param question the question
	 */
	public AudioRecorderView(Context context, Question question) {
		super(context, question);

		activity = (Activity) context;
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

		LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);


		//// Record Button
		btnRecordVoice = new Button(context);
		btnRecordVoice.setPadding(5, 5, 5, 5);
		btnRecordVoice.setLayoutParams(layoutParams);
		btnRecordVoice.setText(R.string.btn_record_voice);
		btnRecordVoice.setTextSize(22);
		btnRecordVoice.setTextColor(Color.WHITE);
		btnRecordVoice.setTypeface(Typeface.DEFAULT_BOLD);		

		setButtonBackground(R.drawable.btn_mic_normal);


		/**
		 *  Initialize touch listener in recorder button to detect button down and up
		 */
		btnRecordVoice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					if(audioFilePath != null && new File(audioFilePath).exists())
						new File(audioFilePath).delete();
					
					setButtonBackground(R.drawable.btn_mic_hover);
					startRecording();
					
					handler.postDelayed(runnable, 500);
					
					btnRecordVoice.setText(R.string.btn_recording);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					stopRecording();
					setButtonBackground(R.drawable.btn_mic_normal);
					handler.removeCallbacks(runnable);
					btnRecordVoice.setText(R.string.btn_record_voice);
				}
				return false;
			}
		});

		
		//// Play button
		layoutParams.setMargins(0, 5, 0, 0);
		btnPlay = new Button(context);
		btnPlay.setPadding(5, 5, 5, 5);
		btnPlay.setLayoutParams(layoutParams);
		btnPlay.setTextSize(22);
		btnPlay.setTextColor(Color.WHITE);
		btnPlay.setTypeface(Typeface.DEFAULT_BOLD);	
		btnPlay.setText(R.string.btn_play);
		btnPlay.setBackgroundResource(R.drawable.btn_play);

		btnPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(mPlayer != null && mPlayer.isPlaying())
					stopAudio();
				else
					playAudio();
			}
		});


		ll_body.addView(btnRecordVoice);
		ll_body.addView(btnPlay);

		setInputData(question.getUserInput().get(0));

	}

	/** The m recorder. */
	MediaRecorder mRecorder;
	
	/**
	 * Start the recorder to record audio.
	 */
	private void startRecording() {


		String path = App.getContext().getVoiceFileDir(activity);
		long timeInMilli = Calendar.getInstance().getTimeInMillis();
		String audioFileName = Constants.AUDIO_PATH+App.getContext().getUserInfo().getUserCode()+"_"+timeInMilli + ".mp3";

		File audioFile = new File(path, audioFileName);
		audioFilePath = audioFile.getPath();

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(audioFilePath);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("Audio Recorder", "prepare() failed");
		}

		mRecorder.start();
	}

	/**
	 * Stop the recorder to stop recording.
	 */
	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
	}

	/**
	 * Play the recorded audio.
	 */
	private void playAudio()
	{
		if(audioFilePath != null)
		{
			stopAudio();


			mPlayer = new MediaPlayer();
			

			FileInputStream fis;
			try {
				fis = new FileInputStream(audioFilePath);
				mPlayer.setDataSource(fis.getFD());
				mPlayer.prepare();
				mPlayer.start();

				btnPlay.setText(R.string.btn_stop);
				btnPlay.setBackgroundResource(R.drawable.btn_stop);


				mPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {
						btnPlay.setText(R.string.btn_play);
						btnPlay.setBackgroundResource(R.drawable.btn_play);

					}
				});
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stop playing the recorded audio.
	 */
	private void stopAudio()
	{
		btnPlay.setText(R.string.btn_play);
		btnPlay.setBackgroundResource(R.drawable.btn_play);

		if(mPlayer != null)
		{
			mPlayer.stop();
			mPlayer = null;
		}
	}

	/**
	 * Gets the temp path.
	 *
	 * @return the temp path
	 */
	public String getTempPath()
	{
		return tempAudioFilePath;
	}
	
	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputData()
	 */
	@Override
	public String getInputData() {
		// TODO Auto-generated method stub
		return audioFilePath;
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#isValid()
	 */
	@Override
	public boolean isValid(boolean isSingleForm) {
		if(audioFilePath != null && new File(audioFilePath).exists())
			return true;
		else
		{
			if (!isSingleForm){
				AppToast.showToast(context, R.string.input_required);
			}

			return false;
		}

	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputDataList()
	 */
	@Override
	public ArrayList<String> getInputDataList() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#setInputData(java.lang.String)
	 */
	@Override
	public void setInputData(String data) {
		if(data != null)
		{
			if(audioFilePath != null && !audioFilePath.equalsIgnoreCase(data))
				new File(audioFilePath).delete();

			audioFilePath = data;
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
