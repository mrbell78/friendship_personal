package ngo.friendship.satellite.views;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import ngo.friendship.satellite.model.Question;

// TODO: Auto-generated Javadoc

/**
 * The Class BarCodeView.
 */
public class BarCodeView extends QuestionView {

	/**
	 * Instantiates a new bar code view.
	 *
	 * @param context the context
	 * @param question the question
	 */
	public BarCodeView(Context context, Question question) {
		super(context, question);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#addInputField()
	 */
	@Override
	protected void addInputField() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputData()
	 */
	@Override
	public String getInputData() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#isValid()
	 */
	@Override
	public boolean isValid(boolean isSingleForm) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputDataList()
	 */
	@Override
	public ArrayList<String> getInputDataList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#setInputData(java.lang.String)
	 */
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
