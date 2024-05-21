package com.logic27.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;


public class AudioPlayer extends Component implements MediaPlayer.OnCompletionListener {


    Context context;

    //private GifImageView ivGif;
    private TextView tvTitle;
    MediaPlayer mMediaPlayer = new MediaPlayer();;

    private ImageButton btnReverse, btnPlay, btnForward;
    private String hours, minutes, seconds;
    private long secs, mins, hrs;
    private int seekForwardTime = 10000; // 10 seconds
    private int seekBackwardTime = 10000; // 10 seconds
    private boolean stopped = false;
    private String title;
    private long startTime;
    private long lastStartTime;
    boolean isPlay = false;
    private boolean isRepeat = false;

    public AudioPlayer(Context ctx) {
        super(ctx);
        this.context = ctx;
        startTime = 0;
        lastStartTime = 0;
        initLayout();
    }

    private void initLayout() {

        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        this.setOrientation(LinearLayout.VERTICAL);
        this.setLayoutParams(param);
        this.setBackgroundColor(Color.DKGRAY);

        LayoutParams paramBody = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        paramBody.setMargins(3, 3, 3, 3);
        LinearLayout body = new LinearLayout(context);
        body.setLayoutParams(paramBody);
        body.setBackgroundColor(Color.WHITE);
        body.setOrientation(LinearLayout.VERTICAL);
        this.addView(body);
        tvTitle = new TextView(context);
        tvTitle.setLayoutParams(param);
        tvTitle.setText(getTitle());
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvTitle.setPadding(20, 20, 20, 20);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);


        LayoutParams paramGif = new LayoutParams(LayoutParams.WRAP_CONTENT, 150);

//        ivGif = new GifImageView(context);
//        ivGif.setLayoutParams(paramGif); // OR
////		imageView.getLayoutParams().width = 100;
//        ivGif.setPadding(20, 20, 20, 20);
//        ivGif.setBackgroundResource(R.drawable.audio);
        //ivGif.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        //printTime(startTime);
        //	tvTimer.setText("Yeasin");

        LinearLayout llButtons = new LinearLayout(context);
        llButtons.setOrientation(LinearLayout.HORIZONTAL);
        llButtons.setLayoutParams(param);
        llButtons.setPadding(10, 10, 10, 10);


        LayoutParams paramButton = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);

        btnPlay = new ImageButton(context);
        btnPlay.setLayoutParams(paramButton);
        btnPlay.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_play));
        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startClick(view);
                try {
                    isPlay = true;
                    playSound();

//					if(mMediaPlayer.isPlaying()){
//						if(mMediaPlayer!=null){
//							mMediaPlayer.pause();
//							// Changing button image to play button
//							btnPlay.setImageResource(R.drawable.ic_baseline_pause);
//						}
//					}else{
//						// Resume song
//						if(mMediaPlayer!=null){
//							mMediaPlayer.start();
//
//						}
//					}

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });



        btnReverse = new ImageButton(context);
        btnReverse.setLayoutParams(paramButton);
        btnReverse.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_reverse));
        btnReverse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // get current song position
                int currentPosition = mMediaPlayer.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mMediaPlayer.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mMediaPlayer.seekTo(0);
                }
               // stopClick(view);
            }
        });


        btnForward = new ImageButton(context);
        btnForward.setLayoutParams(paramButton);
       // btnForward.setEnabled(false);
        btnForward.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_forward));
        btnForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = mMediaPlayer.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mMediaPlayer.getDuration()){
                    // forward song
                    mMediaPlayer.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    mMediaPlayer.seekTo(mMediaPlayer.getDuration());
                }
            }
        });

        llButtons.addView(btnReverse);
        llButtons.addView(btnPlay);
        llButtons.addView(btnForward);

        body.addView(tvTitle);
        //body.addView(ivGif);
        body.addView(llButtons);


    }


    public void startClick(View view) {
        if (startTime > 0) {
            showStopButton();
        }
    }

    public void stopClick(View view) {
        hideStopButton();
    }

    public void resetClick(View view) {
        startTime = lastStartTime;
        //printTime(startTime);
    }


    private void showStopButton() {
//		btnReverse.setEnabled(false);
//		btnPlay.setEnabled(false);
//		btnForward.setEnabled(true);
    }

    private void hideStopButton() {
//		btnReverse.setEnabled(true);
//		btnPlay.setEnabled(true);
//		btnForward.setEnabled(false);
    }

    @Override
    public ArrayList<String> getValues() {
        ArrayList<String> arrayList = new ArrayList<String>();
        //arrayList.add(tvTimer.getText().toString().trim());
        mMediaPlayer.stop();
        btnPlay.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_play));
        return arrayList;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    @Override
    public void setValues(ArrayList<String> values) {
        startTime = parseLong(values.get(0));
        lastStartTime = startTime;
        //	printTime(startTime);
    }


    public void playSound() throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        File file = new File(App.getContext().getAlgorithmAssetDir(context), "friendship.mp3");

        // Play song
      //  mMediaPlayer.is

        if (mMediaPlayer.isPlaying()) {
            if (mMediaPlayer != null) {
                mMediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.ic_baseline_play);
            }
        } else {
            btnPlay.setImageResource(R.drawable.ic_baseline_pause);
            // Resume song
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(file.getPath());
                mMediaPlayer.prepare();
                mMediaPlayer.start();

            }
        }
    }

    @Override
    protected void finalize() throws Throwable {

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        super.finalize();

    }


    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}

