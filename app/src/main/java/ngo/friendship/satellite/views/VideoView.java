package ngo.friendship.satellite.views;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import ngo.friendship.satellite.model.Question;

// TODO: Auto-generated Javadoc

/**
 * The Class VideoView.
 */
public class VideoView extends QuestionView {

	/**
	 * Instantiates a new video view.
	 *
	 * @param context the context
	 * @param question the question
	 */
	public VideoView(Context context, Question question) {
		super(context, question);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected void addInputField() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getInputData() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isValid(boolean isSingleForm) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<String> getInputDataList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInputData(String data) {
		// TODO Auto-generated method stub
		
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
