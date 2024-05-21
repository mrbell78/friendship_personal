package ngo.friendship.satellite.views;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionOption;

// TODO: Auto-generated Javadoc

/**
 * The Class RadioGroupView.
 */
public class RadioGroupView extends QuestionView {

    private RadioGroup radioGroup = null;
    private int checkedId = -1;
    private String checkedValue = "";


    /** Radio button value reset */


    /**
     * Instantiates a new radio group view.
     *
     * @param context  the context
     * @param question the question
     */
    public RadioGroupView(Context context, Question question) {
        super(context, question);
        init();
        addCaptionField();
        addHintField();
        addInputField();
    }

    protected void addInputField() {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        radioGroup = new RadioGroup(context);
        radioGroup.setId(R.id.question_select_view);
        radioGroup.setLayoutParams(params);
        int i = 0;

        /// Kayum
        int checkedIndex = -1;
        final String[] val = {null};
        if (question.getUserInput() != null && question.getUserInput().size() > 0) {
            val[0] = question.getUserInput().get(0);
        } else if (question.getDefaultValue() != null && question.getDefaultValue().size() > 0) {
            String defaultVal = question.getDefaultValue().get(0);

            try {
                if (defaultVal != null && !defaultVal.equalsIgnoreCase("")) {
                    checkedIndex = Integer.parseInt(defaultVal.replace("option", ""));
                }
            } catch (Exception e) {
                checkedIndex = -1;
            }
            ////////////
        }

        radioResetButton = new ImageButton(context);
        radioResetButton.setId(R.id.radio_reset_button);
        radioResetButton.setImageResource(R.drawable.ic_reset);
        radioResetButton.setBackgroundResource(R.color.white);
        //radioResetButton.setVisibility(View.GONE);
        LayoutParams button_params = new LayoutParams(0, 40,0.5f);
        // button_params.setMargins(0, 5, 0, 0);
        //button_params.gravity.
        radioResetButton.setLayoutParams(button_params);
        this.ll_header.addView(radioResetButton);



        for (QuestionOption questionOption : question.getOptionList()) {
            RadioButton radioButton = new RadioButton(context);

            radioButton.setId(i);
            radioButton.setTextSize(16);
            radioButton.setText(questionOption.getCaption());
            radioButton.setLayoutParams(params);

            if (i % 2 == 0)
                radioButton.setBackgroundColor(Color.rgb(225, 225, 225));
//			radioButton.setPadding(10, 5, 0, 5);

            // add Image Button



            //// Kayum
            if (val[0] != null) {
               // this.radioResetButton.setVisibility(VISIBLE);
                if (val[0].equalsIgnoreCase(questionOption.getValue()))
                    radioButton.setChecked(true);
            } else {
                if (checkedIndex >= 0) {
                    if (questionOption.getId() == checkedIndex)
                        radioButton.setChecked(true);



                }


            }
            ///////


            radioGroup.addView(radioButton);
            i++;

            if (question.isReadonly())
                radioButton.setClickable(false);
        }
        radioGroup.setEnabled(!question.isReadonly());
        this.ll_body.addView(radioGroup);

        this.radioResetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.clearCheck();

            }
        });
    }

    public String getInputData() {
        String value = getValue();
        if (this.getValue() == null) {

            return null;
        } else {
            return value;
        }
    }

    public boolean isValid(boolean isSingleForm) {

        if (question.isRequired()) {
            if (this.getValue() == null) {
                if (question.getValidationMsg() != null && !question.getValidationMsg().trim().equalsIgnoreCase("")) {
                    AppToast.showToast(context, question.getValidationMsg());
                } else {
                    if (!isSingleForm) {
                        AppToast.showToast(context, R.string.input_required);
                    }

                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;

        }

    }

    private String getValue() {
        int id = radioGroup.getCheckedRadioButtonId();
        if (id < 0) return null;

        return question.getOptionList().get(id).getValue();
    }

    public ArrayList<String> getInputDataList() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setInputData(String data) {

    }

    @Override
    public View getInputView() {
        // TODO Auto-generated method stub
        return radioGroup;
    }

    @Override
    public void replaceBody(Object data) {
        // TODO Auto-generated method stub

    }
}
