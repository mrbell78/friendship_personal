package com.logic27.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;

import ngo.friendship.satellite.R;

public class VideoComponent extends Component{


    Context context;

    private PlayerView playerView;
    private TextView tvTitle;
  //  PhotoView data;

    private ImageButton btnZoomIn, btnZoomOut;
    private boolean stopped = false;
    private String title;
    private long startTime;
    private long lastStartTime;
    boolean isPlay = false;
    private boolean isRepeat = false;
    ExoPlayer  simpleExoPlayer;
    public VideoComponent(Context ctx) {
        super(ctx);
        this.context = ctx;
        startTime = 0;
        lastStartTime = 0;
        initLayout();
    }

    private void initLayout() {
        simpleExoPlayer = new ExoPlayer.Builder(context).build();
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


        LayoutParams paramGif = new LayoutParams(LayoutParams.MATCH_PARENT, 400);

        playerView = new PlayerView(context);
        playerView.setLayoutParams(paramGif); // OR
//		imageView.getLayoutParams().width = 100;
        playerView.setPadding(20, 20, 20, 20);
        playerView.setPlayer(simpleExoPlayer);
        MediaItem mediaItem = MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        simpleExoPlayer.addMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(true);
        playerView.setUseController(true);
        playerView.setShowNextButton(false);
        playerView.setShowPreviousButton(false);
        playerView.setKeepScreenOn(true);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        ViewGroup.LayoutParams params = playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(params);

        body.addView(tvTitle);
        body.addView(playerView);


    }




    @Override
    public ArrayList<String> getValues() {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.stop();
            simpleExoPlayer.seekTo(0);
            simpleExoPlayer.pause();


        }

        //arrayList.add(tvTimer.getText().toString().trim());
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
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.stop();
            simpleExoPlayer.seekTo(0);
            simpleExoPlayer.pause();


        }
        //	printTime(startTime);
    }




}

