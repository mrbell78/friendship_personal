package com.logic27.components;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class Component extends LinearLayout {

	private HashMap<String, String>  captionMap;
	private String position="";
	private String key="";
	private String name="";


	public HashMap<String, String> getCaptionMap() {
		return captionMap;
	}
	public void setCaptionMap(HashMap<String, String> captionMap) {
		this.captionMap = captionMap;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public abstract String getTitle() ;
	public abstract void setTitle(String title);
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public static final String STOPWATCH="STOPWATCH";
	public static final String COUNTDOWN_TIMER="COUNTDOWN_TIMER";
	public static final String AUDIO_PLAYER="AUDIO_PLAYER";
	public static final String VIDEO_PLAYER="VIDEO_PLAYER";
	public static final String IMAGE_VIEW="IMAGE_VIEW";

	public Component(Context context) {
		super(context);
	}  
	public abstract ArrayList<String> getValues();
	public abstract void setValues(ArrayList<String> values);

	protected long parseLong(String data){
		try {
			return Long.parseLong(data);
		} catch (Exception e) {
			return 0; 
		}
	}

	public void resetCaptions(ViewGroup parent) {
		for (int i = parent.getChildCount() - 1; i >= 0; i--) {
			final View child = parent.getChildAt(i);
			if (child instanceof ViewGroup) {
				resetCaptions((ViewGroup) child);
			} else {
				if (child != null) {
					if (child instanceof Button ) {
						Button bt =(Button)child;
						String caption=bt.getText().toString().trim();
						if(captionMap!=null){
							if(captionMap.get(caption)!=null){
								bt.setText(captionMap.get(caption));
							}
						}

					} else if (child instanceof TextView ) {
						TextView bt =(TextView)child;
						String caption=bt.getText().toString().trim();
						if(captionMap!=null){
							if(captionMap.get(caption)!=null){
								bt.setText(captionMap.get(caption));
							}
						}
					}  
				}
			}
		}
	}
}
