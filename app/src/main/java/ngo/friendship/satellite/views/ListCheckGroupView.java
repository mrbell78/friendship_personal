package ngo.friendship.satellite.views;

/**
 * @author Yeasin Ali
 * Description: Display List Check Group View
 * Created Date: 9th Sep 2023
 */

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionAnswer;
import ngo.friendship.satellite.model.QuestionOption;

// TODO: Auto-generated Javadoc

/**
 * The Class ListCheckGroupView.
 */
public class ListCheckGroupView extends QuestionView {

    /**
     * The ll row container.
     */
    private LinearLayout llRowContainer;

    /**
     * The medicine list.
     */
    ArrayList<QuestionOption> optionList;

    /**
     * The med array list.
     */
    HashMap<String, QuestionAnswer> questionAnswerMap;
    /**
     * Instantiates a new prescription list view.
     *
     * @param context the context
     * @param question the question
     * @param dataSet the data set
     */

    private int radioButtonSize;
    Activity activityG;

    public ListCheckGroupView(Context context, Question question, HashMap<String, QuestionAnswer> questionAnswerMap, Activity activity) {

        super(context, question);
        activityG = activity;
        this.questionAnswerMap = questionAnswerMap;
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
        try {
            optionList =new ArrayList<>();
            final ArrayList<QuestionOption> optionListData = question.getOptionList();
            final ArrayList<String> userInputList = question.getUserInput();
            if (userInputList !=null && userInputList.size()>0){
                for(String op: userInputList){
                    for(QuestionOption qp: optionListData){
                        if(qp.getValue().equalsIgnoreCase(op)){
                            qp.setSelected(true);
                              optionList.add(qp);
//                              break;
//                            return;
                        }

                    }
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }

        View view = View.inflate(context, R.layout.check_group_list_layout, null);
        ll_body.addView(view);

        llRowContainer = view.findViewById(R.id.question_option_view);


        View v = view.findViewById(R.id.tv_add);
        v.setVisibility(View.VISIBLE);
        v.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
                DialogView exitDialog = new DialogView((Activity) context, R.string.select_diseases, buttonMap);
                exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {

                    @Override
                    public void onDialogButtonClick(View view) {

                        optionList = (ArrayList<QuestionOption>) view.getTag();
                        showOptionListRenderList(optionList);

                    }
                });
                Collections.sort(question.getOptionList(), new Comparator<QuestionOption>() {
                    @Override
                    public int compare(QuestionOption o1, QuestionOption o2) {
                        return o1.getCaption().compareTo(o2.getCaption());
                    }
                });
                exitDialog.showCheckGroupListDialog(context, question, question.getOptionList());
            }
        });

        showOptionListRenderList(optionList);

    }

    private void showOptionListRenderList(final ArrayList<QuestionOption> getOptionList) {

        llRowContainer.removeAllViews();
        if (getOptionList != null) {
            for (int i = 0; i < getOptionList.size(); i++) {
                QuestionOption questionOption = getOptionList.get(i);
                View rowView = View.inflate(context, R.layout.check_group_list_row, null);
                TextView tvName = rowView.findViewById(R.id.tv_option_name);
                tvName.setText("" + questionOption.getCaption());
                llRowContainer.addView(rowView);

//            if (i % 2 == 1) {
//                View v = rowView.findViewById(R.id.ll_parent_container);
//                v.setBackgroundColor(Color.rgb(225, 225, 225));
//            }

            }
        }


    }


    /* (non-Javadoc)
     * @see org.friendship.mhealth.views.QuestionView#setInputData(java.lang.String)
     */
    @Override
    public void setInputData(String data) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.friendship.mhealth.views.QuestionView#isValid()
     */
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

    /* (non-Javadoc)
     * @see org.friendship.mhealth.views.QuestionView#getInputData()
     */
    @Override
    public String getInputData() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.friendship.mhealth.views.QuestionView#getInputDataList()
     */
    public ArrayList<String> getInputDataList() {
        return getValue();
    }

    @Override
    public View getInputView() {
        // TODO Auto-generated method stub
        return llRowContainer;
    }

    @Override
    public void replaceBody(Object data) {
        // TODO Auto-generated method stub

    }

    private ArrayList<String> getValue() {
        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < optionList.size(); i++) {
            QuestionOption    medicineLists = optionList.get(i);
            values.add(medicineLists.getValue());
        }

        if (values.size() > 0) {
            return values;
        }
        return null;

    }

}
