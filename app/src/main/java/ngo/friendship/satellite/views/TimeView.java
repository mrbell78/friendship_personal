package ngo.friendship.satellite.views;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.util.ArrayList;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;

public class TimeView extends  QuestionView {

	private  TimePicker timePicker;
	private String defaultTime=null;

	public TimeView(Context context, Question question) {
		super(context, question);	
		init();
		addCaptionField();
		addHintField();
		addInputField();
	}

	protected void addInputField() {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		timePicker=new TimePicker(context);
		timePicker.setId(R.id.question_time_view);
		timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		if(question.getTimeFormat()!=null && question.getTimeFormat().equals("24")){
			timePicker.setIs24HourView(true);
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
			String userInputdate =  question.getDefaultValue().get(0);
			if(userInputdate!=null && !userInputdate.equalsIgnoreCase("")) 
			{ 
				setInputData(userInputdate);
			}
		}


		timePicker.setEnabled(!question.isReadonly());
		
		LinearLayout llViewContainer = new LinearLayout(context);
		llViewContainer.setLayoutParams(params);
		llViewContainer.setOrientation(LinearLayout.VERTICAL);
		llViewContainer.addView(timePicker);

		ll_body.addView(llViewContainer);	
	}

	@Override
	public String getInputData() {
		if(timePicker==null)
			return null;

		int hh= timePicker.getCurrentHour();
		int mm=timePicker.getCurrentMinute();
		String time= TextUtility.format("%02d:%02d",hh,mm);

		if(Utility.isValidTime(time))
		{
			return time;
		}
		return null;
	}

	int currentMonthIndex = 0;

	Handler handler = new Handler();

	@Override
	public void setInputData(String data) {
		if(data!=null && Utility.isValidTime(data)){
			timePicker.setCurrentHour(Utility.getHour(data));
			timePicker.setCurrentMinute(Utility.getMinute(data));
		}

	}

	public ArrayList<String> getInputDataList() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isValid(boolean isSingleForm) {

		if (question.isRequired()) {
			if(getInputData()==null || getInputData().equals(""))
			{
				if (question.isRequired()) {
					if (!isSingleForm){
						AppToast.showToast(context, R.string.input_required);
					}

					return false;
				} else {
					return true;
				}
			}
			
			if(question.getValidationList()!=null && question.getValidationList().size()==1)
			{ 
				boolean fromValid=true;
				boolean toValid=true;

				String fromTime =question.getValidationList().get(0).getBaseDate().trim();
				String toTime   =question.getValidationList().get(0).getValue().trim();
				
				long fromHH,fromMM,toHH,toMM; 
				
				if(Utility.isValidTime(fromTime))
				{
					fromHH=Utility.getHour(fromTime);
					fromMM=Utility.getMinute(fromTime);
					if(timePicker.getCurrentHour()<fromHH || timePicker.getCurrentMinute()<fromMM ){
						fromValid=false;
					}
				}

				if(Utility.isValidTime(toTime))
				{
					toHH=Utility.getHour(toTime);
					toMM=Utility.getMinute(toTime);
					if(timePicker.getCurrentHour()>toHH || timePicker.getCurrentMinute()>toMM ){
						fromValid=false;
					}
				}
				
				if(fromValid&&toValid)
				{
					return true;

				}else
				{
					if(question.getValidationMsg() != null && !question.getValidationMsg().trim().equalsIgnoreCase(""))
						AppToast.showToast(context, question.getValidationMsg());
					else
						AppToast.showToast(context, R.string.input_required);
					
					return false;
				}
			}
			else 
			{ return true;}
			
		}else{
			return true;
		}

	}

	@Override
	public View getInputView() {
		// TODO Auto-generated method stub
		return timePicker;
	}

	@Override
	public void replaceBody(Object data) {
		// TODO Auto-generated method stub
		
	}
}
