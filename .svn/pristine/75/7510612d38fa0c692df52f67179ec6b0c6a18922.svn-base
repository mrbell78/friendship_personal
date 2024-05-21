package ngo.friendship.satellite.views;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import java.util.ArrayList;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionOption;

// TODO: Auto-generated Javadoc

/**
 * The Class CheckGroupView.
 */
public class CheckGroupView extends QuestionView implements OnCheckedChangeListener {

    /**
     * The ll.
     */
    LinearLayout ll;

    /**
     * Instantiates a new check group view.
     *
     * @param context  the context
     * @param question the question
     */
    public CheckGroupView(Context context, Question question) {
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

        int i = 0;
        ll = new LinearLayout(context);
        ll.setId(R.id.question_multi_select_view);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(params);
        ll.setOrientation(LinearLayout.VERTICAL);
        for (QuestionOption questionOption : question.getOptionList()) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setId(i);
            checkBox.setText(questionOption.getCaption());
            checkBox.setLayoutParams(params);
            if (i % 2 == 0)
                checkBox.setBackgroundColor(Color.rgb(225, 225, 225));

            //// Checked item based on user input or Default value
            if (question.getUserInput() != null && question.getUserInput().size() > 0) {
                for (int j = 0; j < question.getUserInput().size(); j++) {
                    if (questionOption.getValue().equalsIgnoreCase(question.getUserInput().get(j))) {
                        checkBox.setChecked(true);
                        break;
                    }
                }
            } else if (question.getDefaultValue() != null && question.getDefaultValue().size() > 0) {
                for (int j = 0; j < question.getDefaultValue().size(); j++) {
                    if (("option" + questionOption.getId()).equalsIgnoreCase(question.getDefaultValue().get(j))) {
                        checkBox.setChecked(true);
                        break;
                    }
                }
            }
            checkBox.setEnabled(!question.isReadonly());
            checkBox.setOnCheckedChangeListener(this);
            ll.addView(checkBox);
            i++;
        }


        this.ll_body.addView(ll);

    }

    public String getInputData() {
        return null;
    }

    public boolean isValid(boolean isSingleForm) {
        if (question.isRequired()) {
            ArrayList<String> values = null;
            values = getValue();
            if (values == null || values.size() < 1) {
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

    public ArrayList<String> getInputDataList() {
        return getValue();
    }

    private ArrayList<String> getValue() {
        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < ll.getChildCount(); i++) {
            View v = ll.getChildAt(i);
            if (v instanceof CheckBox && ((CheckBox) v).isChecked()) {
                int id = v.getId();
                values.add(question.getOptionList().get(id).getValue());
            }
        }

        if (values.size() > 0) {
            return values;
        }
        return null;

    }

    public void setInputData(String data) {

    }

    public void onCheckedChanged(CompoundButton cb, boolean isChecked) {


        if (question.getOptionList().get(cb.getId()).getValue().equalsIgnoreCase("none")) {

            for (int i = 0; i < ll.getChildCount(); i++) {
                View v = ll.getChildAt(i);
                if (v instanceof CheckBox) {
                    CheckBox checkBox = ((CheckBox) v);
                    if (!question.getOptionList().get(checkBox.getId()).getValue().equalsIgnoreCase("none")) {
                        if (isChecked) {
                            checkBox.setChecked(false);
                            checkBox.setEnabled(false);
                        } else {
                            checkBox.setEnabled(true);
                        }
                    }
                }

            }

        }

    }

    @Override
    public View getInputView() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void replaceBody(Object data) {

    }
}
