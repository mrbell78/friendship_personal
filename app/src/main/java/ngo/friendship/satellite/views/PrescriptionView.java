package ngo.friendship.satellite.views;

/**
 * @author Kayum Hossan
 * Description: Display prescription
 * Created Date: 2nd March 2014
 * Last Update: 31st March 2014
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionAnswer;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;

// TODO: Auto-generated Javadoc

/**
 * The Class PrescriptionListView.
 */
public class PrescriptionView extends QuestionView {

    /**
     * The ll row container.
     */
    private LinearLayout llRowContainer;

    /**
     * The medicine list.
     */
    ArrayList<MedicineInfo> medicineList;

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

    private boolean inventoryCheck = true;
    private int radioButtonSize;
    Activity activityG;

    public PrescriptionView(Context context, Question question, HashMap<String, QuestionAnswer> questionAnswerMap, Activity activity) {

        super(context, question);
        activityG = activity;
        this.questionAnswerMap = questionAnswerMap;
        init();
        addCaptionField();
        addHintField();
        addInputField();
        inventoryCheck = true;
        try {

            JSONArray configArry = AppPreference.getFCMConfigration(context);
            if (JSONParser.getFcmConfigValue(configArry, "INVENTORY", "allow.0.inventory.product.consumption").equals("YES")) {
                inventoryCheck = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /* (non-Javadoc)
     * @see org.friendship.mhealth.views.QuestionView#addInputField()
     */
    @Override
    protected void addInputField() {
        final ArrayList<MedicineInfo> prescription = question.getPrescription();
        medicineList = App.getContext().getDB().getMedicineList();

        for (MedicineInfo mp : prescription) {
            for (MedicineInfo ml : medicineList) {
                if (mp.getMedId() == ml.getMedId()) {
                    mp.setUnitPurchasePrice(ml.getUnitPurchasePrice());
                    mp.setUnitSalesPrice(ml.getUnitSalesPrice());
                    mp.setStrength(ml.getStrength());
                    mp.setMeasureUnit(ml.getMeasureUnit());
                    mp.setManufId(ml.getManufId());
                    mp.setAvailableQuantity(ml.getAvailableQuantity());
                    mp.setBrandName(ml.getBrandName());
                    mp.setSelected(true);
                    medicineList.set(medicineList.indexOf(ml), mp);
                }
            }
        }

        View view = View.inflate(context, R.layout.prescription_list_layout, null);
        ll_body.addView(view);

        llRowContainer = view.findViewById(R.id.question_prescription_view);


        final TextView tvTotalMedicinePrice = view.findViewById(R.id.tv_total_medicine_price);
        final TextView tvTotalMedicineQty = view.findViewById(R.id.tv_total_medicine_qty);

        if (question.isAddMedicine()) {
            View v = view.findViewById(R.id.tv_add);
            v.setVisibility(View.VISIBLE);
            v.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
                    DialogView exitDialog = new DialogView((Activity) context, R.string.medicine_list_dialog_title, buttonMap);
                    exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {

                        @Override
                        public void onDialogButtonClick(View view) {
                            prescription.clear();
                            llRowContainer.removeAllViews();
                            for (MedicineInfo mdi : medicineList) {
                                if (mdi.isSelected()) {
                                    try {
                                        prescription.add(mdi);
                                        showPrescription(prescription, tvTotalMedicinePrice, tvTotalMedicineQty);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
//                            MedicineInfo mi = (MedicineInfo) view.getTag();
//                            mi.setSelected(true);
//                            prescription.add(mi);
//                            try {
//                                showPrescription(prescription, tvTotalMedicinePrice, tvTotalMedicineQty);
//                            } catch (JSONException e) {
//                                throw new RuntimeException(e);
//                            }
                        }
                    });
                    exitDialog.showMedicineListDialog(medicineList);

                }
            });

        }

        try {
            showPrescription(prescription, tvTotalMedicinePrice, tvTotalMedicineQty);
        } catch (JSONException e) {
          e.printStackTrace();
        }

    }

    private void showPrescription(final ArrayList<MedicineInfo> prescription,
                                  final TextView tvTotalMedicinePrice,
                                  final TextView tvTotalMedicineQty) throws JSONException {

        llRowContainer.removeAllViews();


        String takingTime = "";
        String isVisible = "";
        String dosSlist = "";

        try {
            String value = JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "INTERVIEW_SERVICES", "prescription.parameters");
            JSONArray dosesPrescription = new JSONArray(value);
            for (int i = 0; i < dosesPrescription.length(); i++) {
                JSONObject jsonDosesObject = dosesPrescription.getJSONObject(i);
                String key = JSONParser.getString(jsonDosesObject, "key");
                if (key.equals("medicine.dose")) {
                    dosSlist = JSONParser.getString(jsonDosesObject, "value");
                } else if (key.equals("taking.time")) {
                    takingTime = JSONParser.getString(jsonDosesObject, "value");
                } else if (key.equals("param.visibility")) {
                    isVisible = JSONParser.getString(jsonDosesObject, "value");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < prescription.size(); i++) {
            final MedicineInfo medInfo = prescription.get(i);
            if (!Utility.isNumaric(medInfo.getRequiredQuantity())) {
                double rq = Utility.evaluate(medInfo.getRequiredQuantity(), questionAnswerMap);
                medInfo.setRequiredQuantity((int) rq + "");
                medInfo.setSoldQuantity(medInfo.getRequiredQuantity());
            }

            if (!Utility.isNumaric(medInfo.getDoseDuration())) {
                double rq = Utility.evaluate(medInfo.getDoseDuration(), questionAnswerMap);
                medInfo.setDoseDuration((int) rq + "");
            }
            View rowView = View.inflate(context, R.layout.prescription_list_row, null);
            LinearLayout llAdditionalPortion = rowView.findViewById(R.id.llMoreOptions);

            if (isVisible.equals("true")) {
                List<String> systemDoses = new ArrayList<>();
                systemDoses = getDoes(dosSlist, medInfo.getMedicineType());

                if (getMeldicineTypeSystem(dosSlist, medInfo.getMedicineType())) {
                    if (systemDoses != null) {
                        llAdditionalPortion.setVisibility(View.VISIBLE);
                        RadioGroup radioGroup;
                        radioGroup = rowView.findViewById(R.id.radioGrp);

                        JSONArray takeTimeList = new JSONArray(takingTime);
                        for (int j = 0; j < takeTimeList.length(); j++) {

                            JSONObject jsonRadioobj = takeTimeList.getJSONObject(j);
                            String radioName = JSONParser.getString(jsonRadioobj, "value");
                            RadioButton radioButton = new RadioButton(context);
                            radioButton.setText(radioName);
                            radioButton.setId(j);// Set a unique ID for each radio button
                            radioGroup.addView(radioButton);
                            if (radioName.equalsIgnoreCase("" + medInfo.getTakingTime())) {
                                radioButton.setChecked(true);
                            }
                        }
                        if (medInfo.getTakingTime() == null) {
                            radioGroup.check(((RadioButton) radioGroup.getChildAt(takeTimeList.length() - 1)).getId());

                        }


                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        RadioButton radioButton = (RadioButton) rowView.findViewById(selectedId);
                        if (radioButton.isChecked()) {
                            String selectedValue = radioButton.getText().toString();
                            medInfo.setTakingTime(selectedValue);
                        }


                        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                                RadioButton radioButton = (RadioButton) findViewById(i);
                                String selectedValue = radioButton.getText().toString();
                                medInfo.setTakingTime(selectedValue);
                            }
                        });


//                        radioGroup.get

                        Spinner spinner = rowView.findViewById(R.id.spinnerDoses);


                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, systemDoses);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spinner.setAdapter(adapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String selectedItem = adapterView.getItemAtPosition(i).toString();
                                medInfo.setDoses(selectedItem);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        try {
                            spinner.setSelection(getSelectedDoses(systemDoses, medInfo.getDoses()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        llAdditionalPortion.setVisibility(View.GONE);
                    }
                }
            } else {
                llAdditionalPortion.setVisibility(View.GONE);
            }

            if (question.isAddMedicine()) {
                View close = rowView.findViewById(R.id.ll_close);
                close.setVisibility(View.VISIBLE);
                close.setTag(medInfo);
                close.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        MedicineInfo mi = (MedicineInfo) v.getTag();
                        mi.setSelected(false);
                        mi.setSoldQuantity("0");
                        prescription.remove(mi);
                        try {
                            showPrescription(prescription, tvTotalMedicinePrice, tvTotalMedicineQty);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }


            llRowContainer.addView(rowView);
            if (i % 2 == 1) {
                View v = rowView.findViewById(R.id.ll_parent_container);
                v.setBackgroundColor(Color.rgb(225, 225, 225));
            }


            final TextView tvMedName = rowView.findViewById(R.id.tv_medicine_name);
            tvMedName.setText(medInfo.toString() + " - " + getResources().getString(R.string.stock) + ": " + medInfo.getAvailableQuantity());


            if (medInfo.getMedId() <= 0) {
                rowView.findViewById(R.id.ll_medicine_name_container).setVisibility(View.GONE);
                rowView.findViewById(R.id.ll_medicine_qty_container).setVisibility(View.GONE);
                rowView.findViewById(R.id.ll_medicine_line).setVisibility(View.GONE);
            }

            EditText etDays = rowView.findViewById(R.id.etDaysQuantity);
            TextView tvQty = rowView.findViewById(R.id.tv_med_qty);
            TextView tvDayUse = rowView.findViewById(R.id.tv_med_day_use);
            final TextView tvPrice = rowView.findViewById(R.id.tv_med_price);
            final TextView tvMedId = rowView.findViewById(R.id.tv_med_id);
            final EditText etQty = rowView.findViewById(R.id.et_sold_med_qty);
            tvQty.setText(medInfo.getRequiredQuantity() + "");
            if (medInfo.getDays() != null) {
                etDays.setText("" + medInfo.getDays());
            }


            if (medInfo.getMedicineTakingRuleLabel() != null && medInfo.getMedicineTakingRuleLabel().trim().length() > 0) {
                tvDayUse.setText(medInfo.getMedicineTakingRuleLabel() + "");
            } else {
                tvDayUse.setVisibility(View.GONE);
            }


            tvPrice.setText(TextUtility.format("%.2f", medInfo.getTotalPrice()));
            tvMedId.setText("" + medInfo.getMedId());


            TextView tvAdvice = rowView.findViewById(R.id.tv_advice_for_medicine);
            if (medInfo.getAdviceForMedicine() != null && medInfo.getAdviceForMedicine().trim().length() > 0) {
                tvAdvice.setText("" + medInfo.getAdviceForMedicine());
            } else {
                tvAdvice.setVisibility(View.GONE);
            }


            LinearLayout llMedNameContainer = rowView.findViewById(R.id.ll_medicine_name_container);


            /// Combine selected item index and generic name (Separated by #) to build the tag string.
            //selected item index is used to detect which medicine is select in current Medicine menu list.
            //Generic name is used to get the data set from the Map

            tvMedName.setTag(medInfo);
//            int currentQty = medInfo.getAvailableQuantity()+Integer.parseInt(medInfo.getSoldQuantity());
//            medInfo.setAvailableQuantity(currentQty);

//



			/* This is mandatory to set the data into this view after add the text watcher. 
			This will trigger the textwatcher and watcher will calculate the total price of that medicine
			 * */
            etQty.setText("" + medInfo.getSoldQuantity());


            int qtyTmp = medInfo.getSoldQuantity() == null ? Utility.parseInt(medInfo.getRequiredQuantity()) : Utility.parseInt(medInfo.getSoldQuantity());


            if ((qtyTmp > medInfo.getAvailableQuantity()) && inventoryCheck) {
                etQty.setTextColor(Color.RED);
            } else {
                etQty.setTextColor(Color.BLACK);
            }

            etQty.addTextChangedListener(new TextWatcher() {
                //
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
                    int availableQty = Utility.parseInt("" + medInfo.getAvailableQuantity()) + Utility.parseInt(medInfo.getSoldQuantity());

                    String str = arg0.toString();
                    MedicineInfo medInfo = (MedicineInfo) tvMedName.getTag();
                    if (!str.equalsIgnoreCase("")) {
                        int newVal = Utility.parseInt(str);

                        //						int availableQty = tempMedList.get(spnName.getSelectedItemPosition()).getAvailableQuantity();
                        //int availableQty = medInfo.getAvailableQuantity();
                        if ((newVal > availableQty)) {
                            etQty.setTextColor(Color.RED);
                            App.showMessageDisplayDialog(activityG, getContext().getString(R.string.stock_quantity_not_availalbe), R.drawable.error, Color.RED);
                            if (Utility.parseInt(medInfo.getSoldQuantity()) > 0) {
                                etQty.setText("" + medInfo.getSoldQuantity());
                            } else {
                                etQty.setText("" + 0);
                            }

                        } else {
                            etQty.setTextColor(Color.BLACK);
                            // medInfo.setSoldQuantity(str);
                        }
                    }

                    // Update the total price field based on data from quantity field

                    double totalPrice = 0;
                    String qtyStr = etQty.getText().toString();
                    if (!qtyStr.equalsIgnoreCase("")) {
                        int qty = Utility.parseInt(qtyStr);
                        //						totalPrice = qty*tempMedList.get(spnName.getSelectedItemPosition()).getUnitSalesPrice();
                        totalPrice = qty * medInfo.getUnitSalesPrice();
                    }
//					tvPrice.setText(""+totalPrice);
                    tvPrice.setText(TextUtility.format("%.2f", totalPrice));
                    if (!qtyStr.equalsIgnoreCase("")) {
                        tvQty.setText("" + qtyStr);
                    } else {
                        tvQty.setText("0");
                    }

                    showTotalPrice(tvTotalMedicinePrice);
                    showTotalQty(tvTotalMedicineQty);

                }
            });

            etQty.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        EditText editTex = (EditText) v;
                        editTex.setSelection(editTex.length());
                    }

                }
            });
        }
    }

    private int getSelectedDoses(List<String> systemDoses, String selectdValue) {
        for (int n = 0; n < systemDoses.size(); n++) {
            String task = systemDoses.get(n);
            if (task.equalsIgnoreCase(selectdValue)) {
                return n;
            }
        }
        return 0;

    }

    private List<String> getDoes(String sDosList, String medicineInfoStock) {

        String medType = "";
        ArrayList<String> localDosesList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(sDosList);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonDosesObject = jsonArray.getJSONObject(i);
                medType = JSONParser.getString(jsonDosesObject, "medicine.type");
                if (medicineInfoStock.equals(medType)) {
                    //systemDoses.clear();
                    //Log.d(TAG, "getDoes: .....configuretion medtype-----------"+medType + "----- and stock med type is ,,,.."+medicineInfoStock);
                    String jsonString = JSONParser.getString(jsonDosesObject, "dose");
                    JSONArray doseList = new JSONArray(jsonString);
                    for (int j = 0; j < doseList.length(); j++) {
                        JSONObject dlistdata = doseList.getJSONObject(j);
                        String value = JSONParser.getString(dlistdata, "value");
                        localDosesList.add(value);
                    }
                    //Log.d(TAG, "getDoes: ...........total doselist is ........."+localDosesList);
                } else {
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return localDosesList;
    }

    private boolean getMeldicineTypeSystem(String sDosList, String medicineInfoStock) {
        String medType = "";
        boolean isMatched = false;
        try {
            JSONArray jsonArray = new JSONArray(sDosList);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonDosesObject = jsonArray.getJSONObject(i);
                medType = JSONParser.getString(jsonDosesObject, "medicine.type");

                if (medicineInfoStock.equals(medType)) {
                    //systemDoses.clear();
                    isMatched = true;
                    break;
                } else {
                    isMatched = false;
                }
            }
        } catch (JSONException e) {
           e.printStackTrace();
        }
        return isMatched;
    }

    private void showTotalQty(TextView tv) {

        int totalQty = 0;
        for (int i = 0; i < llRowContainer.getChildCount(); i++) {
            View rowView = llRowContainer.getChildAt(i);

            TextView tvPrice = rowView.findViewById(R.id.tv_med_qty);
            totalQty += Integer.parseInt(tvPrice.getText().toString().trim());
        }

        String label = getResources().getString(R.string.total_quantity);
        tv.setText(label + " " + totalQty);

    }

    /**
     * Show medicine total price into TextView tv .
     *
     * @param tv The view to which the price will be dispplayed
     */
    private void showTotalPrice(TextView tv) {
        double totalPrice = 0;
        for (int i = 0; i < llRowContainer.getChildCount(); i++) {
            View rowView = llRowContainer.getChildAt(i);

            TextView tvPrice = rowView.findViewById(R.id.tv_med_price);
            totalPrice += Double.parseDouble(tvPrice.getText().toString().trim());
        }


        tv.setText(TextUtility.format("%s %.2f", getResources().getString(R.string.total_medicine_price), totalPrice));
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
    @Override
    public boolean isValid(boolean isSingleForm) {

        if (!inventoryCheck) return true;

        for (int i = 0; i < llRowContainer.getChildCount(); i++) {


            View rowView = llRowContainer.getChildAt(i);

            long medId = 0;
            try {
                medId = Utility.parseInt(((TextView) rowView.findViewById(R.id.tv_med_id)).getText().toString().trim());
            } catch (Exception e) {
                // TODO: handle exception
            }
            if (medId <= 0) continue;
            EditText etQty = rowView.findViewById(R.id.et_sold_med_qty);
            String qty = etQty.getText().toString().trim();
            if (qty.equalsIgnoreCase("")) {
                AppToast.showToast(context, R.string.empty_sold_medicine);
                return false;
            }

            int soldQty = Utility.parseInt(qty);
            TextView tvMedName = rowView.findViewById(R.id.tv_medicine_name);

            //			String idTag[] = spnName.getTag().toString().split("#");
            MedicineInfo medicineInfo = (MedicineInfo) tvMedName.getTag();


            //Log.e("asdfasdfsdf", soldQty+"  "+tempMedList.get(itemIndex).getAvailableQuantity());
            if (soldQty > medicineInfo.getAvailableQuantity()) {
                AppToast.showToast(context, R.string.exceed_sold_medicine);
                return false;
            }


//            EditText etDays = rowView.findViewById(R.id.et_day);
//            String days = etQty.getText().toString().trim();
//            if (days.equalsIgnoreCase("")) {
//                etDays.setText("0");
//                AppToast.showToast(context, getContext().getString(R.string.days_cant_be_empty));
//                return false;
//            }


        }
        return true;
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
    @Override
    public Object getInputDataList() {

        ArrayList<String> inputData = new ArrayList<String>();
        for (int i = 0; i < llRowContainer.getChildCount(); i++) {
            View rowView = llRowContainer.getChildAt(i);

            EditText etQty = rowView.findViewById(R.id.et_sold_med_qty);

            TextView tvDayUse = rowView.findViewById(R.id.tv_med_day_use);
            TextView tvMedId = rowView.findViewById(R.id.tv_med_id);

            //			Spinner spnName = (Spinner) rowView.findViewById(R.id.spn_med_name);
            TextView tvMedName = rowView.findViewById(R.id.tv_medicine_name);
            TextView etDaysQuantity = rowView.findViewById(R.id.etDaysQuantity);

            //			String idTag[] = spnName.getTag().toString().split("#");
            MedicineInfo medicineInfo = (MedicineInfo) tvMedName.getTag();

            String soldQty = etQty.getText().toString().trim();
            if (soldQty.equalsIgnoreCase(""))
                soldQty = "0";


            String dayQty = etDaysQuantity.getText().toString().trim();
            if (dayQty.equalsIgnoreCase("")) {
                dayQty = "0";
            }

            TextView tvQty = rowView.findViewById(R.id.tv_med_qty);
            String requiredQty = tvQty.getText().toString();

            TextView tvPrice = rowView.findViewById(R.id.tv_med_price);
            String price = tvPrice.getText().toString();
            JSONObject mObject = new JSONObject();
            try {
                mObject.put("MED_ID", tvMedId.getText() + "");
                mObject.put("MED_TYPE", medicineInfo.getMedicineType());
                mObject.put("GEN_NAME", medicineInfo.getGenericName());
                mObject.put("MED_NAME", medicineInfo.getBrandName());
                mObject.put("MED_QTY", requiredQty);
                mObject.put("SALE_QTY", soldQty);
                mObject.put("MED_DURATION", medicineInfo.getDoseDuration());
                mObject.put("MTR", medicineInfo.getMedicineTakingRule());
                mObject.put("MTR_LBL", medicineInfo.getMedicineTakingRuleLabel());
                mObject.put("MTR_SF", medicineInfo.getSmsFormatmedicineTakingRule());
                mObject.put("AFM", medicineInfo.getAdviceForMedicine());
                mObject.put("AFM_SF", medicineInfo.getSmsFormatadviceForMedicine());
                mObject.put("SF", medicineInfo.getSmsFormat());
                mObject.put("TOTAL_PRICE", price);
                mObject.put("DAYS", dayQty + "");
                mObject.put("DOSES", medicineInfo.getDoses() + "");
                mObject.put("TAKINGTIME", medicineInfo.getTakingTime() + "");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            inputData.add(mObject.toString());

        }
        return inputData;
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
}
