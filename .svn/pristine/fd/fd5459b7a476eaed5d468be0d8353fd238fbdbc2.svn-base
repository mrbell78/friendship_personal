package com.logic27.components;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

public class ComponentFectory {
	public static Component  getComponent(String componentName,Context  context){
		if (componentName.equals(Component.STOPWATCH)) return new StopWatch(context) ;
		if (componentName.equals(Component.COUNTDOWN_TIMER)) return new CountdownTimer(context) ;
		if (componentName.equals(Component.AUDIO_PLAYER)) return new AudioPlayer(context) ;
		if (componentName.equals(Component.VIDEO_PLAYER)) return new VideoComponent(context) ;
		if (componentName.equals(Component.IMAGE_VIEW)) return new TouchImageViewComponent(context) ;
		else return null;
	}


	public static Component getComponentByExpression(String expression,Context context){

		String componentName="NONE";
		String componentTitle="";
		String componentPosition="BOTTOM";
		String componentKey="COMPONENT";
		String captions="";
		ArrayList<String> defaultInput=new ArrayList<String>();
		HashMap<String,String> captionMap=new HashMap<String, String>();
		String[] parts= expression.split(":");
		for (String part:parts) {
			part=part.trim();
			int lengths = part.split("=").length;
			if(part.startsWith("NAME") && lengths==2){
				componentName=part.split("=")[1];
			}else if(part.startsWith("TITLE") && part.split("=").length==2){
				componentTitle=part.split("=")[1];
			}else if(part.startsWith("POSITION") && part.split("=").length==2){
				componentPosition=part.split("=")[1];
			}else if(part.startsWith("KEY") && part.split("=").length==2){
				componentKey=part.split("=")[1];
			}else if(part.startsWith("DEFAULT_INPUT") && part.split("=").length==2){
				defaultInput.add(part.split("=")[1]);
			}
			else if(part.startsWith("CAPTION") && part.split("=").length==2){

				captions=part.split("=")[1];
				if(captions!=null){
					String [] capParts=captions.split("\\.");
					for (String capPart : capParts) {
						String[] capKeyValue= capPart.split("-");
						if(capKeyValue.length==2){
							captionMap.put(capKeyValue[0], capKeyValue[1]);
						}
					}
				}

			}
		}

		Component component=getComponent(componentName, context);
		if(component!=null){
			component.setName(componentName);
			component.setKey(componentKey);
			component.setPosition(componentPosition);
			component.setTitle(componentTitle);
			component.setCaptionMap(captionMap);
			component.setValues(defaultInput);
			component.resetCaptions(component);
			return component;
		}
		return null;
	}
}
