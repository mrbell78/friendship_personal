package com.logic27.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.utility.imageview.PhotoView;

public class TouchImageViewComponent extends Component{


    Context context;

    private PhotoView touchImageView;
    private TextView tvTitle;
  //  PhotoView data;

    private ImageButton btnZoomIn, btnZoomOut;
    private boolean stopped = false;
    private String title;
    private long startTime;
    private long lastStartTime;
    boolean isPlay = false;
    private boolean isRepeat = false;

    public TouchImageViewComponent(Context ctx) {
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


        LayoutParams paramGif = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        touchImageView = new PhotoView(context);
        touchImageView.setLayoutParams(paramGif); // OR
        touchImageView.setPadding(20, 20, 20, 20);
        touchImageView.setImageResource(R.drawable.ic_notification);

        LinearLayout llButtons = new LinearLayout(context);
        llButtons.setOrientation(LinearLayout.HORIZONTAL);
        llButtons.setLayoutParams(param);
        llButtons.setPadding(10, 10, 10, 10);


        LayoutParams paramButton = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);

        btnZoomIn = new ImageButton(context);
        btnZoomIn.setLayoutParams(paramButton);
        btnZoomIn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_zoom_in));


        btnZoomIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    float x = touchImageView.getScaleX();
                    float y = touchImageView.getScaleY();
                    // set increased value of scale x and y to perform zoom in functionality
                    touchImageView.setScaleX((float) (x + 1));
                    touchImageView.setScaleY((float) (y + 1));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        btnZoomOut = new ImageButton(context);
        btnZoomOut.setLayoutParams(paramButton);
        btnZoomOut.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_zoom_out));
        btnZoomOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float x = touchImageView.getScaleX();
                    float y = touchImageView.getScaleY();
                    // set increased value of scale x and y to perform zoom in functionality
                    touchImageView.setScaleX((float) (x - 1));
                    touchImageView.setScaleY((float) (y - 1));

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
//        llButtons.addView(btnZoomIn);
//        llButtons.addView(btnZoomOut);

        body.addView(tvTitle);
        body.addView(touchImageView);
        body.addView(llButtons);

    }







    @Override
    public ArrayList<String> getValues() {
        ArrayList<String> arrayList = new ArrayList<String>();
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
    }




}

