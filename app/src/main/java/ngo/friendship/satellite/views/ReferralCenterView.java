package ngo.friendship.satellite.views;

/*
 * Author: Kayum Hossan
 * Description: Display referral center list
 * Created Date: 9th March 2014
 * Last modified: 9th March 2014
 * */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.ReferralCenterInfo;

// TODO: Auto-generated Javadoc

/**
 * The Class ReferralCenterView.
 */
public class ReferralCenterView extends QuestionView {

    /**
     * The radio group.
     */
    private RadioGroup radioGroup = null;

    /**
     * Instantiates a new referral center view.
     *
     * @param context  the context
     * @param question the question
     */
    public ReferralCenterView(Context context, Question question) {
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

        radioGroup = new RadioGroup(context);
        radioGroup.setId(R.id.question_referral_center_view);
        int i = 0;

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
        /// Kayum
        int checkedIndex = -1;
        String val = null;
        if (question.getUserInput() != null && question.getUserInput().size() > 0) {
            val = question.getUserInput().get(0);
            Log.e("User Input", "user: " + val);
        } else if (question.getDefaultValue() != null && question.getDefaultValue().size() > 0) {
            val = question.getDefaultValue().get(0);
            Log.e("DEfault Val", "Default: " + val);
        }

//		Log.e("Referral Center Default Val", "sdfsdf"+question.getDefaultValue().get(0));

        for (final ReferralCenterInfo referralCenterInfo : question.getReferralCenterList()) {
            RadioButton radioButton = new RadioButton(context);

            radioButton.setId(i);
            radioButton.setText(referralCenterInfo.toString());
            radioButton.setTextSize(16);

            radioButton.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View arg0) {
//					int clickedItemIndex = arg0.getId();

                    StringBuilder sbShortInfo = new StringBuilder();
                    sbShortInfo.append(context.getResources().getString(R.string.referral_center_caption));
                    sbShortInfo.append(referralCenterInfo.getCaptionName());
                    sbShortInfo.append("\n");

                    sbShortInfo.append(context.getResources().getString(R.string.referral_center_number));
                    sbShortInfo.append(referralCenterInfo.getContactNumber());
                    sbShortInfo.append("\n");

                    sbShortInfo.append(context.getResources().getString(R.string.referral_center_distance));
                    sbShortInfo.append(referralCenterInfo.getDistance() + "KM");


                    StringBuilder sbDescription = new StringBuilder();
                    sbDescription.append(referralCenterInfo.getDescription());

                    displayRefCenterInfo(sbShortInfo.toString(), sbDescription.toString());
                    return true;
                }
            });


            //// Kayum
            if (val != null) {
                if (val.equalsIgnoreCase(Long.toString(referralCenterInfo.getID())))
                    radioButton.setChecked(true);
            } else {

                if (checkedIndex >= 0 && checkedIndex == i)
                    radioButton.setChecked(true);

            }
            ///////

            radioGroup.addView(radioButton);
            i++;

        }

        this.ll_body.addView(radioGroup);

        this.radioResetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                radioGroup.clearCheck();

            }
        });
    }

    /* (non-Javadoc)
     * @see org.friendship.mhealth.views.QuestionView#getInputData()
     */
    @Override
    public String getInputData() {
        String value = getValue();
        if (this.getValue() == null) {

            return null;
        } else {
            return value;
        }
    }

    /* (non-Javadoc)
     * @see org.friendship.mhealth.views.QuestionView#isValid()
     */
    @Override
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

    /**
     * Gets the value.
     *
     * @return the value
     */
    private String getValue() {
        int id = radioGroup.getCheckedRadioButtonId();
        if (id < 0) return null;

        return Long.toString(question.getReferralCenterList().get(id).getID());
    }

    /* (non-Javadoc)
     * @see org.friendship.mhealth.views.QuestionView#getInputDataList()
     */
    @Override
    public ArrayList<String> getInputDataList() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void setInputData(String data) {

    }


    /**
     * Display ref center info.
     *
     * @param shortInfo   the short info
     * @param description the description
     */
    private void displayRefCenterInfo(String shortInfo, String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = View.inflate(context, R.layout.referral_center_detail_info_dialog, null);
        builder.setView(view);

        TextView tvShortInfo = view.findViewById(R.id.tv_ref_center_short_info);
        tvShortInfo.setText(shortInfo);


        TextView tvDescription = view.findViewById(R.id.tv_ref_center_detail_info);
        tvDescription.setText(description);

        // Add action buttons
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
            }
        });

        builder.show();

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
