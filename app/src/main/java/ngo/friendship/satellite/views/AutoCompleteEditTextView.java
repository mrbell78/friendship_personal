package ngo.friendship.satellite.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.QUESTION_TYPE;
import ngo.friendship.satellite.constants.VALIDATION_TYPE;
import ngo.friendship.satellite.model.InputValidation;
import ngo.friendship.satellite.model.Question;

public class AutoCompleteEditTextView extends QuestionView implements View.OnFocusChangeListener{


	private MyAutoCompleteTextView inputView;
	private int myThreshold;
	public AutoCompleteEditTextView(Context context, Question question,
			ArrayList<String> dataSet) {
		super(context, question, dataSet);
		init();
		addCaptionField();
		addHintField();
		addInputField();


		// TODO Auto-generated constructor stub
	}

	@Override
	protected void addInputField() {


		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 60, 0, 0);
		inputView = new MyAutoCompleteTextView(context);
		inputView.setId(R.id.question_text_view);
		inputView.setLayoutParams(params);
		this.inputView.setPadding(5, 5, 5, 5);
		this.inputView.setTextSize(16);
		this.inputView.setTextColor(Color.BLACK);
		this.inputView.setTextSize(16);
		this.inputView.setOnFocusChangeListener(this);


		if (question.getType().equalsIgnoreCase(QUESTION_TYPE.STRING))
		{
			this.inputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		}
		else if (question.getType().equalsIgnoreCase(QUESTION_TYPE.NUMBER))
		{
			this.inputView.setInputType(InputType.TYPE_CLASS_NUMBER);
		}

		//// Set pre-saved data
		String userInput="";
		if(question.getUserInput() != null && question.getUserInput().size()>0)
		{
			for(String st:question.getUserInput()){
				userInput=userInput+"##"+st;
			}
			userInput=userInput.replaceFirst("##", "");
			setInputData(userInput);
		}
		else if(question.getDefaultValue() != null && question.getDefaultValue().size()>0)
		{
			userInput=question.getDefaultValue().get(0);
			setInputData(userInput);
		}

		ArrayList<String> dataSet = (ArrayList<String>) this.dataSet;

		//// Set AutoComplete Adapter
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,  R.layout.spinner_item, dataSet);
		inputView.setAdapter(adapter);

		ll_body.addView(this.inputView);


		inputView.addTextChangedListener(new  TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
//				if(!cbWriteEnable.isChecked())
//				{
//					InputMethodManager imp = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//					imp.hideSoftInputFromWindow(inputView.getWindowToken(), 0);
//				}

			}
		});

		inputView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				inputView.showDropDown();

			}
		});
		inputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

		if(question.isReadonly()){
			inputView.setEnabled(false);

		}else{
			inputView.requestFocus();
		}


	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#getInputData()
	 */
	@Override
	public String getInputData() {
		// TODO Auto-generated method stub
		Log.e("Name:", inputView.getText().toString());
		return inputView.getText().toString().trim();
	}

	/* (non-Javadoc)
	 * @see org.friendship.mhealth.views.QuestionView#setInputData(java.lang.String)
	 */
	@Override
	public void setInputData(String data) {
		inputView.setText(data);

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
	 * @see org.friendship.mhealth.views.QuestionView#isValid()
	 */
	@Override
	public boolean isValid(boolean isSingleForm) {
		String inputValue = "";
		int inputLength = 0;
		inputValue = this.inputView.getText().toString().trim();
		//	Log.e("User Input", inputValue);

		if (inputValue == null || inputValue.equalsIgnoreCase("")) {
			if (question.isRequired()) {
				if (!isSingleForm){
					AppToast.showToast(context, R.string.input_required);
				}

				return false;
			} else {
				return true;

			}

		} else {
			boolean isValid = true;
			inputLength = inputValue.length();
			for (InputValidation inputValidation : question.getValidationList()) {
				if (inputValidation.getValidationType().equalsIgnoreCase(
						VALIDATION_TYPE.REGEX)) {
					String expression = inputValidation.getValue().trim();
					CharSequence inputStr = this.inputView.getText().toString()
							.trim();
					Pattern pattern = Pattern.compile(expression,
							Pattern.CASE_INSENSITIVE);
					Matcher matcher = pattern.matcher(inputStr);
					if (matcher.matches()) {
						isValid = true;
						Log.e("mHealth", "REGEX TRUE");
					} else {
						isValid = false;
						Log.e("mHealth", "REGEX FALSE");
						break;
					}
				} else if (inputValidation.getValidationType()
						.equalsIgnoreCase(VALIDATION_TYPE.MIN_LENGTH)) {
					int minLen = -1;
					minLen = Integer.parseInt(inputValidation.getValue());
					if (inputLength >= minLen) {
						isValid = true;
						Log.e("mHealth", "MIN_LENGTH TRUE");
					} else {
						isValid = false;
						Log.e("mHealth", "MIN_LENGTH FALSE");
						break;
					}

				} else if (inputValidation.getValidationType()
						.equalsIgnoreCase(VALIDATION_TYPE.MIN_VALUE)) {
					int minInputVal = -10;
					int minRequireVal = -1;
					minInputVal = Integer.parseInt(inputValue);
					minRequireVal = Integer
							.parseInt(inputValidation.getValue());
					if (minInputVal >= minRequireVal) {
						isValid = true;
						Log.e("mHealth", "MIN_VALUE TRUE");
					} else {
						isValid = false;
						Log.e("mHealth", "MIN_VALUE false");
						break;
					}

				} else if (inputValidation.getValidationType()
						.equalsIgnoreCase(VALIDATION_TYPE.MAX_LENGTH)) {
					int maxLen = -1;
					maxLen = Integer.parseInt(inputValidation.getValue());
					if (inputLength <= maxLen) {
						isValid = true;
						Log.e("mHealth", "MAX_LENGTH TRUE");
					} else {
						isValid = false;
						Log.e("mHealth", "MAX_LENGTH FALSE");
						break;
					}

				} else if (inputValidation.getValidationType()
						.equalsIgnoreCase(VALIDATION_TYPE.MAX_VALUE)) {

					int maxInputVal = -10;
					int maxRequireVal = -100;
					maxInputVal = Integer.parseInt(inputValue);
					maxRequireVal = Integer
							.parseInt(inputValidation.getValue());
					if (maxInputVal <= maxRequireVal) {
						isValid = true;
						Log.e("mHealth", "MAX_VALUE TRUE");
					} else {
						isValid = false;
						Log.e("mHealth", "MAX_VALUE FALSE");
						break;
					}

				}

			}
			if(isValid)
			{
				return true;

			}else
			{
				if(!question.getValidationMsg().trim().equalsIgnoreCase(""))
					AppToast.showToast(context, question.getValidationMsg());
				else
					AppToast.showToast(context, R.string.input_required);
				return false;
			}
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			 ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}

	@Override
	public View getInputView() {return inputView;}

	@Override
	public void replaceBody(Object data) {
		// TODO Auto-generated method stub

	}

}
