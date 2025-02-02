package ngo.friendship.satellite.views;

import android.app.Activity;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.INPUT_TYPE;
import ngo.friendship.satellite.constants.QUESTION_TYPE;
import ngo.friendship.satellite.constants.VALIDATION_TYPE;
import ngo.friendship.satellite.model.InputValidation;
import ngo.friendship.satellite.model.Question;


public class EditTextView extends QuestionView implements OnFocusChangeListener {


    private EditText inputView;

    /**
     * Instantiates a new edits the text view.
     *
     * @param context the context
     * @param question the question
     */
    private Context context;

    public EditTextView(Context context, Question question) {
        super(context, question);
        this.context = context;
        init();
        addCaptionField();
        addHintField();
        addInputField();
    }

    protected void addInputField() {




        this.inputView = new EditText(context);
        this.inputView.setPadding(16, 8, 8, 16);
        this.inputView.setTextSize(16);
        this.inputView.setOnFocusChangeListener(this);
        this.inputView.setBackground(context.getDrawable(R.drawable.editext_border));

        if (question.getType().equalsIgnoreCase(QUESTION_TYPE.STRING)) {

//            String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
//            InputFilter characterFilter = new CharacterInputFilter(allowedCharacters);
//            inputView.setFilters(new InputFilter[]{characterFilter});

            if (question.isMultiLine()) {
                this.inputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                this.inputView.setMinLines(5);
                this.inputView.setGravity(Gravity.START);
            } else {
                this.inputView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            }
        } else if (question.getType().equalsIgnoreCase(QUESTION_TYPE.NUMBER)) {
            this.inputView.setInputType(InputType.TYPE_CLASS_NUMBER);
            this.inputView.setId(R.id.question_number_view);
            if (question.getNumType().equalsIgnoreCase(INPUT_TYPE.INTEGER)) {
                this.inputView.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (question.getNumType().equalsIgnoreCase(INPUT_TYPE.DECIMAL)) {
                this.inputView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
        }


        String userInput = "";
        if (question.getUserInput() != null) {
            for (String st : question.getUserInput()) {
                userInput = userInput + "##" + st;
            }
            userInput = userInput.replaceFirst("##", "");
            setInputData(userInput);
        } else if (question.getDefaultValue() != null && question.getDefaultValue().size() > 0) {
            userInput = question.getDefaultValue().get(0);
            setInputData(userInput);
        }


        if (question.isReadonly()) {
            inputView.setEnabled(false);

        } else {
//            inputView.requestFocus();
        }

        ll_body.addView(this.inputView);


    }

    public String getInputData() {
        String st = this.inputView.getText().toString().trim();
        return st.replaceAll("[\r\n]+", " ");
    }

    public boolean isValid(boolean isSingleForm) {

        String inputValue = "";
        int inputLength = 0;
        inputValue = getInputData();
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
                        Log.e("mHealth", expression + " REGEX TRUE " + inputStr);
                    } else {
                        isValid = false;
                        Log.e("mHealth", expression + " REGEX FALSE" + inputStr);
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

                } else if (inputValidation.getValidationType().equalsIgnoreCase(VALIDATION_TYPE.MIN_VALUE)) {


                    try {
                        if (question.getNumType().equalsIgnoreCase(INPUT_TYPE.INTEGER)) {
                            int minInputVal = Integer.parseInt(inputValue);
                            int minRequireVal = Integer.parseInt(inputValidation.getValue());

                            if (minInputVal >= minRequireVal) {
                                isValid = true;
                                Log.e("mHealth", "MIN_VALUE TRUE");
                            } else {
                                isValid = false;
                                break;
                            }
                        } else if (question.getNumType().equalsIgnoreCase(INPUT_TYPE.DECIMAL)) {
                            double minInputVal = Double.parseDouble(inputValue);
                            double minRequireVal = Double.parseDouble(inputValidation.getValue());

                            if (minInputVal >= minRequireVal) {
                                isValid = true;
                                Log.e("mHealth", "MIN_VALUE TRUE");
                            } else {
                                isValid = false;
                                break;
                            }
                        }


                    } catch (NumberFormatException e) {
                        isValid = false;
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

                    try {
                        if (question.getNumType().equalsIgnoreCase(INPUT_TYPE.INTEGER)) {
                            int maxInputVal = Integer.parseInt(inputValue);
                            int maxRequireVal = Integer.parseInt(inputValidation.getValue());
                            if (maxInputVal <= maxRequireVal) {
                                isValid = true;
                                Log.e("mHealth", "MAX_VALUE TRUE");
                            } else {
                                isValid = false;
                                Log.e("mHealth", "MAX_VALUE FALSE");
                                break;
                            }
                        } else if (question.getNumType().equalsIgnoreCase(INPUT_TYPE.DECIMAL)) {
                            double maxInputVal = Double.parseDouble(inputValue);
                            double maxRequireVal = Double.parseDouble(inputValidation.getValue());
                            if (maxInputVal <= maxRequireVal) {
                                isValid = true;
                                Log.e("mHealth", "MAX_VALUE TRUE");
                            } else {
                                isValid = false;
                                Log.e("mHealth", "MAX_VALUE FALSE");
                                break;
                            }
                        }


                    } catch (NumberFormatException e) {
                        isValid = false;
                    }

                }

            }
            if (isValid) {
                Log.e("mHealth", "isValid TRUE");
                return true;

            } else {
                if (question.getValidationMsg() != null && !question.getValidationMsg().trim().equalsIgnoreCase(""))
                    AppToast.showToast(context, question.getValidationMsg());
                else
                    AppToast.showToast(context, R.string.input_required);
                return false;
            }


        }
    }

    public void setInputData(String data) {

        if (question.getType().equalsIgnoreCase(QUESTION_TYPE.NUMBER) && question.getNumType().equalsIgnoreCase(INPUT_TYPE.INTEGER)) {
            try {
                inputView.setText((long) Double.parseDouble(data) + "");
            } catch (Exception e) {
                inputView.setText(data);
            }
        } else if (question.getType().equalsIgnoreCase(QUESTION_TYPE.NUMBER) && question.getNumType().equalsIgnoreCase(INPUT_TYPE.DECIMAL)) {
            try {
                inputView.setText(Double.parseDouble(data) + "");
            } catch (Exception e) {
                inputView.setText(data);
            }
        } else {
            inputView.setText(data);
        }
    }

    public ArrayList<String> getInputDataList() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public View getInputView() {
        // TODO Auto-generated method stub
        return inputView;
    }

    @Override
    public void replaceBody(Object data) {
        // TODO Auto-generated method stub

    }
}
