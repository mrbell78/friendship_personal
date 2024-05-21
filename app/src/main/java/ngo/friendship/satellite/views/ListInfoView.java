package ngo.friendship.satellite.views;

/**
 * 
 * @author Kayum Hossan
 * Description: Display prescription 
 * Created Date: 2nd March 2014
 * Last Update: 31st March 2014
 * 
 * */

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionOption;
import ngo.friendship.satellite.utility.Utility;

// TODO: Auto-generated Javadoc

/**
 * The Class PrescriptionListView.
 */
public class ListInfoView extends QuestionView {

	/** The ll. */
	LinearLayout llListRowContainer;
	
	/**
	 * Instantiates a new check group view.
	 *
	 * @param context the context
	 * @param question the question
	 */
	public ListInfoView(Context context, Question question) {
		super(context, question);
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
		View view = View.inflate(context, R.layout.list_layout, null);
		ll_body.addView(view);
	
		
		String listType = "";
		int listTypeNumber=0;
		
  
		llListRowContainer = view.findViewById(R.id.ll_list_row_container);
		llListRowContainer.setId(R.id.question_list_view);
		int i = 0;
		
		for (QuestionOption questionOption : question.getOptionList())
		{   
		    view = View.inflate(context, R.layout.list_layout_row, null);
		    TextView txtViewText= view.findViewById(R.id.txtViewTxt);
		    txtViewText.setText(questionOption.getCaption());
		    
		    
		    
		    
		    TextView txtViewBullet= view.findViewById(R.id.txtViewbullet);
		 

		    
		    if( question.getDefaultValue() !=null &&  question.getDefaultValue().size()==1 ){
			
				if(question.getDefaultValue().get(0).equals("abc")) {
				   listTypeNumber++;
				    txtViewBullet.setText(Utility.getSmallLatter(listTypeNumber));
				}else if(question.getDefaultValue().get(0).equals("123")){
					listTypeNumber++;
					txtViewBullet.setText(listTypeNumber+".");
				}
				else if(question.getDefaultValue().get(0).equals("iii")){
					listTypeNumber++;
					
					txtViewBullet.setText(Utility.getRoman(listTypeNumber)+".");
				}
				else if(question.getDefaultValue().get(0).equals("none")){
					listTypeNumber++;
					txtViewBullet.setText("");
					txtViewBullet.setVisibility(View.GONE);
				}
				else {
					txtViewBullet.setText(question.getDefaultValue().get(0));
				}
				
			}
		    
		    
			if(i%2 == 0){
				view.setBackgroundColor(Color.rgb(225, 225, 225));
			}
			
			

			for(int j=0;question.getUserInput()!=null && j<question.getUserInput().size();j++)
			{
				if((i+1)==Utility.parseInt(question.getUserInput().get(j)))
				{
					view.setBackgroundColor(context.getResources().getColor(R.color.hilight));
					break;
				}
			}
			
			llListRowContainer.addView(view);
			i++;
		}
		
		try{
			InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
			inputManager.hideSoftInputFromWindow(llListRowContainer.getWindowToken(),0);
		}catch(Exception exception){
			exception.printStackTrace();
		}	

	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputData()
	 */
	@Override
	public String getInputData() {
		return llListRowContainer.getChildCount()+"";
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#isValid()
	 */
	@Override
	public boolean isValid(boolean isSingleForm) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputDataList()
	 */
	@Override
	public ArrayList<String> getInputDataList() {
		return getValue();
	}
	
	
	
	
	private ArrayList<String> getValue()
	{
		ArrayList<String> values =new ArrayList<String>();
		values.add(llListRowContainer.getChildCount()+"");
        return values;

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
