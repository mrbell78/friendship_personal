package ngo.friendship.satellite.ui.stock_manage;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import dagger.hilt.android.AndroidEntryPoint;
import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.constants.RequisitionStatus;
import ngo.friendship.satellite.constants.StockAdjustmentRequestStatus;
import ngo.friendship.satellite.databinding.ActivityStockRaiseConfirmBinding;
import ngo.friendship.satellite.databinding.ProductsConfirmItemsAdjustListBinding;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AdjustmentMedicineInfo;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.RequisitionInfo;
import ngo.friendship.satellite.model.RequisitionMedicineInfo;
import ngo.friendship.satellite.ui.beneficiary_filter.CommonBeneficiaryActivity;
import ngo.friendship.satellite.ui.beneficiary_filter.CommonServiceViewModel;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.AppToast;
import ngo.friendship.satellite.views.DialogView;

@AndroidEntryPoint
public class StockRequisitionConfirmActivity extends BaseActivity implements View.OnClickListener, CommonBeneficiaryActivity.OnDialogBeneficiaryButtonClickListener {


    ActivityStockRaiseConfirmBinding binding;
    ArrayList<MedicineInfo> medicineList = new ArrayList<>();
    ArrayList<AdjustmentMedicineInfo> adjustMedicineList;
    private RequisitionInfo medReqInfo;
    String activityPath = "", parentName = "";
    ProgressDialog dlog;

    String benName = "";
    String ACTIVITY = "";

    CommonBeneficiaryActivity dialogFragment;
    CommonServiceViewModel commonServiceViewModel;

    RequisitionInfo requisitionInfo;
    RequisitionInfo  medReqInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = ActivityStockRaiseConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.layout2.setVisibility(View.GONE);
        binding.llNoteSection.setVisibility(View.GONE);
        requisitionInfo = new RequisitionInfo();
        try {
            String requisitonDate = Utility.getDateFromMillisecond(Calendar.getInstance().getTimeInMillis(), Constants.DATE_FORMAT_DD_MMM_YYYY);
            binding.tvDate.setText("Date : " + requisitonDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        commonServiceViewModel = new ViewModelProvider(this).get(CommonServiceViewModel.class);
        adjustMedicineList = new ArrayList<>();
        commonServiceViewModel.getSelected().observe(this, beneficiary -> binding.beneficiarySpinner.setText(beneficiary.getBenefName()));
        binding.btnUploadData.setOnClickListener(this);
        // String colors[] = {"walk in customer", " Mst. Sokhina", "Amina Khatun", "Jamina jannat", "Shumi", "Morjina"};
        binding.beneficiarySpinner.setText(getResources().getString(R.string.benef_name));

        binding.beneficiaryList.setOnClickListener(view -> {
            dialogFragment = CommonBeneficiaryActivity.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            dialogFragment.setOnDialogButtonClickListener(StockRequisitionConfirmActivity.this);
            dialogFragment.show(fragmentManager, "custom_dialog_fragment");
        });

// Application of the Array to the Spinner
//        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors);
//        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
//        binding.beneficiarySpinner.setAdapter(spinnerArrayAdapter);


        Bundle b = getIntent().getExtras();
        parentName = b.getString(ActivityDataKey.PARENT_NAME);
        ACTIVITY = b.getString(ActivityDataKey.ACTIVITY);
        binding.toolbar.tvProductTitle.setText(getResources().getString(R.string.requisition_confirm));
        binding.toolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        if( parent_name.equals(StockAdjustmentRequestActivity.class.getName()) ) {
//            adjustMedicineList = (ArrayList<AdjustmentMedicineInfo>) b.getSerializable(ActivityDataKey.DATA);
//            showAdjustmentMedicineList(adjustMedicineList);
//
//        }
        if (ACTIVITY.equals("" + Constants.PRODUCT_ADJUST_ADJUST_TAB)) {
            medicineList = (ArrayList<MedicineInfo>) b.getSerializable(ActivityDataKey.DATA);
            showAdjustmentMedicineList(medicineList);
        } else if (ACTIVITY.equals("" + Constants.PRODUCT_STOCK_REQUISITION)) {
            medicineList = (ArrayList<MedicineInfo>) b.getSerializable(ActivityDataKey.DATA);
            binding.tvProductPrice.setVisibility(View.INVISIBLE);
            binding.tvPriceTotal.setVisibility(View.INVISIBLE);
            showAdjustmentMedicineList(medicineList);
        } else if (ACTIVITY.equals("" + Constants.PRODUCT_STOCK_REQUISITION_DETAILS)) {
            requisitionInfo = (RequisitionInfo) b.getSerializable(ActivityDataKey.DATA);
            RequisitionInfo reqInfo = App.getContext().getDB().getRequisitionMedicineInfo(requisitionInfo.getRequisitionId());
            showRequistionDetails(reqInfo);
            binding.toolbar.tvProductTitle.setText(R.string.order_details);
            binding.btnUploadData.setVisibility(View.GONE);
        }else if (ACTIVITY.equals("" + Constants.PRODUCT_STOCK_RECEIVE)) {
            binding.toolbar.tvProductTitle.setText(""+getString(R.string.receive));
            binding.tvProducReceivedQuantity.setVisibility(View.VISIBLE);
            binding.tvProductPrice.setVisibility(View.GONE);

            binding.tvRequsitionHeaderQuantityOnly.setVisibility(View.GONE);
            binding.tvRequestQuantity.setVisibility(View.VISIBLE);
            binding.llTotalReceivedQuantity.setVisibility(View.VISIBLE);
            binding.tvPriceTotal.setVisibility(View.GONE);
            requisitionInfo = (RequisitionInfo) b.getSerializable(ActivityDataKey.DATA);
            try {
                binding.tvOrderState.setText(""+R.string.order+ requisitionInfo.getRequisitionNo() + " ( " + requisitionInfo.getRequisitionStatus() + " )");
                binding.tvDate.setText(""+getResources().getString(R.string.date) + requisitionInfo.getRequisitionDate());
            } catch (Exception e) {
                e.printStackTrace();
            }

              medReqInfos = App.getContext().getDB().getIncompleteRequisition();
            for (MedicineInfo medicineInfo : medReqInfos.getMedicineList()) {
                medicineInfo.setQtyReceived(medicineInfo.getQtyDispatch());
            }
            medReqInfo=medReqInfos;
            showMedicineList(medReqInfo.getMedicineList());
        }
//        else if( parent_name.equals(MedicineReceiveActivity.class.getName()) ) {
//            medReqInfo = (RequisitionInfo) b.getSerializable(ActivityDataKey.DATA);
//            btnOk.setText(R.string.btn_complete);
//            showReceiveMedeicine(medReqInfo);
//        }
//        else if( parent_name.equals(MedicineRequisitionActivity.class.getName()) ){
//            medicineList = (ArrayList<ngo.friendship.satellite.model.MedicineInfo>) b.getSerializable(ActivityDataKey.DATA);
//            showMedicineList(medicineList);
//        }


//        removeItem = findViewById(R.id.removeItem);


        // Click event for single list row
//        removeItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                medicineList.clear();
//                llRowContainer.removeView(view);
//                llRowContainer.notify();
//                Toast.makeText(StockRaiseConfirmActivity.this, "Item Remove", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        medicineList = null;
    }


    /**
     * Display medicine list.
     *
     * @param medicineList The medicine list containing all medicine with all medicine information
     */
    private void showMedicineList(ArrayList<MedicineInfo> medicineList) {

        double totalMedicinePrice = 0;
        int totalMedicineQty = 0;
        int i = 0;
        for (MedicineInfo medicineInfo : medicineList) {
            ProductsConfirmItemsAdjustListBinding dialogView = ProductsConfirmItemsAdjustListBinding.inflate(getLayoutInflater());
           // View view = View.inflate(this, R.layout.products_confirm_items_adjust_list, null);
            binding.llMedicineRowContainer.addView(dialogView.getRoot());

            dialogView.tvMedicineName.setText(medicineInfo.toString());


            if (Utility.parseInt(medicineInfo.getRequiredQuantity()) > 0) {
                dialogView.tvQty.setText("" + medicineInfo.getRequiredQuantity());
                totalMedicineQty += Utility.parseInt("" + medicineInfo.getRequiredQuantity());
                /*
                 *  Show individual medicine total price
                 */
                totalMedicinePrice += Utility.parseInt(medicineInfo.getRequiredQuantity()) * medicineInfo.getUnitPurchasePrice();

                String medPriceStr = TextUtility.format("%.2f", Utility.parseInt(medicineInfo.getRequiredQuantity()) * medicineInfo.getUnitPurchasePrice());
                if(ACTIVITY.equals("" + Constants.PRODUCT_STOCK_RECEIVE)){
                    dialogView.tvPrice.setVisibility(View.GONE);
                    dialogView.llReceiveQuantity.setVisibility(View.VISIBLE);
                    dialogView.etReceiveQuantity.setText(""+medicineInfo.getQtyDispatch());
                    dialogView.etReceiveQuantity.setTag(i);
                    dialogView.etReceiveQuantity.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            Long myReceivedQuantity = Utility.parseLong(charSequence.toString());
                            if(myReceivedQuantity<=medicineInfo.getQtyDispatch()){

                                dialogView.etReceiveQuantity.setBackground(ContextCompat.getDrawable(StockRequisitionConfirmActivity.this,R.drawable.border_rounded_corner_white));

                                String totalReceivedQuantity = TextUtility.format("%s %.2f", "", getTotalMedicineReceivedQuantity());
                                binding.tvTotalQuantity.setText(totalReceivedQuantity);
                            }else{

                                dialogView.etReceiveQuantity.setBackground(ContextCompat.getDrawable(StockRequisitionConfirmActivity.this,R.drawable.border_rounded_corner_red));
                                dialogView.etReceiveQuantity.setText(""+ medicineInfo.getRequiredQuantity());
                                AppToast.showToast(StockRequisitionConfirmActivity.this,"Can not exceed  the system quantity limit");

                            }
                        }
                        @Override
                        public void afterTextChanged(Editable editable) {

//                            int sellQty = Utility.parseInt(editable.toString().trim());
//                            int rowIndex = (Integer) dialogView.etReceiveQuantity.getTag();
//                            int rcvQuantity = Integer.parseInt(medicineList.get(rowIndex).getRequiredQuantity());
//                            JSONArray configArry = new JSONArray(AppPreference.getString(StockRequisitionConfirmActivity.this, KEY.FCM_CONFIGURATION, "[]"));
//                            String val = JSONParser.getFcmConfigValue(configArry, "INVENTORY", "allow.0.inventory.product.consumption");
//                            Log.e("valData", val);

                            Long myReceivedQuantity = Utility.parseLong(editable.toString());
                            Log.d(TAG, "afterTextChanged: ...inside after change ........the quantity is "+myReceivedQuantity);
                            Long myRequisitionQuantity = Long.parseLong(medicineInfo.getRequiredQuantity());
                            if(editable!=null & myRequisitionQuantity>myReceivedQuantity){
                                medicineInfo.setQtyReceived(myReceivedQuantity);
                            }else{
                                medicineInfo.setQtyReceived(myRequisitionQuantity);
                            }
                        }
                    });

                  //  medicineInfo.setQtyReceived(20);
                }
                dialogView.tvPrice.setText(""+medPriceStr);

            }

            i++;

        }

        /*
         *  Show all medicine total price
         */medReqInfos.setMedicineList(medicineList);

        String totalPriceString = TextUtility.format("%.2f",totalMedicinePrice);

        if (ACTIVITY.equals("" + Constants.PRODUCT_STOCK_RECEIVE)){
            binding.tvTotalQuantity.setText(""+totalMedicinePrice);
            String totalReceivedQuantity = TextUtility.format("%s %.2f", "", getTotalMedicineReceivedQuantity());
            binding.tvTotalQuantity.setText(totalReceivedQuantity);
            binding.tvQtyTotal.setText(""+totalMedicineQty);
        }else{
            binding.tvQtyTotal.setText(""+totalMedicineQty);
            binding.tvPriceTotal.setText(""+totalMedicinePrice);


        }



    }

    private double getTotalMedicineReceivedQuantity() {
        double totalquantity = 0;
        for (int i = 0; i < binding.llMedicineRowContainer.getChildCount(); i++) {

            View view = binding.llMedicineRowContainer.getChildAt(i);
            EditText etQty = view.findViewById(R.id.etReceiveQuantity);
            String qtyStr = etQty.getText().toString().trim();
            totalquantity +=  Utility.parseLong(qtyStr);


        }
        return totalquantity;

    }

    private void showAdjustmentMedicineList(ArrayList<MedicineInfo> medicineList) {
        double totalMedicinePrice = 0;
        int totalMedicineQty = 0;
        for (MedicineInfo medicineInfo : medicineList) {
            AdjustmentMedicineInfo adjustmentMedicineInfo = new AdjustmentMedicineInfo();
            adjustmentMedicineInfo.setMedicineId(medicineInfo.getMedId());
            adjustmentMedicineInfo.setMedicineName(medicineInfo.getGenericName());
            adjustmentMedicineInfo.setAdjustQuantity(Integer.parseInt(medicineInfo.getRequiredQuantity()));
            adjustmentMedicineInfo.setUpdatedOn(medicineInfo.getUpdateTime());
            adjustMedicineList.add(adjustmentMedicineInfo);
            ProductsConfirmItemsAdjustListBinding dialogView = ProductsConfirmItemsAdjustListBinding.inflate(getLayoutInflater());
            //View view = View.inflate(this, R.layout.products_confirm_items_adjust_list, null);
            binding.llMedicineRowContainer.addView(dialogView.getRoot());
            dialogView.tvMedicineName.setText(medicineInfo.toString());

            if (Utility.parseInt(medicineInfo.getSoldQuantity()) > 0) {
                totalMedicineQty += Utility.parseInt(medicineInfo.getSoldQuantity());
                //  dialogView.etQuantity.setText("" + medicineInfo.getSoldQuantity());

                /*
                 *  Show individual medicine total price
                 */
                totalMedicinePrice += Utility.parseDouble(medicineInfo.getSoldQuantity()) * medicineInfo.getUnitSalesPrice();
                String medPriceStr = TextUtility.format(" %.2f", Utility.parseDouble(medicineInfo.getSoldQuantity()) * medicineInfo.getUnitSalesPrice());
                dialogView.tvQty.setText("" + medicineInfo.getSoldQuantity());
                dialogView.tvPrice.setText(medPriceStr);
                dialogView.tvPrice.setVisibility(View.INVISIBLE);
            }

        }
        binding.tvQtyTotal.setText("" + totalMedicineQty);
        //binding.tvPriceTotal.setText(String.format("%.2f",totalMedicinePrice));
        binding.tvPriceTotal.setVisibility(View.INVISIBLE);
//        binding.removeItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                ViewGroup parent = (ViewGroup) llRowContainer.getParent();
//                if (parent != null) {
//                    parent.removeView(llRowContainer);
//                }
//            }
//        });

        /*
         *  Show all medicine total price
         */

//        TextView tvTotalMedicinePrice = findViewById(R.id.tv_total_medicine_price);
//        String totalPriceString = TextUtility.format("%s %.2f", getResources().getString(R.string.total_medicine_price), totalMedicinePrice);
//        binding.tv.setText(totalPriceString);
    }



    private void showRequistionDetails(RequisitionInfo medReqInfo) {

        binding.tvOrderState.setText(R.string.order + medReqInfo.getRequisitionNo() + " ( " + medReqInfo.getRequisitionStatus() + " )");
        try {
            long dates = Long.parseLong(medReqInfo.getRequisitionDate());
            String requisitonDate = Utility.getDateFromMillisecond(dates, Constants.DATE_FORMAT_DD_MMM_YYYY);
            binding.tvDate.setText(R.string.date + requisitonDate);
        } catch (Exception e) {
            e.printStackTrace();
            binding.tvDate.setText(R.string.date + medReqInfo.getRequisitionDate());
        }

        double totalMedicinePrice = 0;
        int totalMedicineQty = 0;
        for (RequisitionMedicineInfo medicineInfo : medReqInfo.getRequisitionMedicineList()) {
            ProductsConfirmItemsAdjustListBinding dialogView = ProductsConfirmItemsAdjustListBinding.inflate(getLayoutInflater());
            //View view = View.inflate(this, R.layout.products_confirm_items_adjust_list, null);
            binding.llMedicineRowContainer.addView(dialogView.getRoot());
            dialogView.tvMedicineName.setText(medicineInfo.toString());
            if (Utility.parseInt("" + medicineInfo.getRequisitionQuantity()) > 0) {

                if (medReqInfo.getRequisitionStatus().equalsIgnoreCase("completed")){
                    dialogView.tvQty.setText("" + medicineInfo.getReceiveQuantity());
                    totalMedicineQty += Utility.parseInt("" + medicineInfo.getReceiveQuantity());
                    totalMedicinePrice += Utility.parseDouble("" + medicineInfo.getReceivePrice());
                }else{
                    dialogView.tvQty.setText("" + medicineInfo.getRequisitionQuantity());
                    totalMedicineQty += Utility.parseInt("" + medicineInfo.getRequisitionQuantity());
                    totalMedicinePrice += Utility.parseDouble("" + medicineInfo.getRequisitionPrice());
                }

                dialogView.tvPrice.setText(String.format("%.2f",totalMedicinePrice));
                dialogView.tvPrice.setVisibility(View.GONE);
            }


        }
        binding.tvQtyTotal.setText("" + totalMedicineQty);
        binding.tvPriceTotal.setText(String.format("%.2f",totalMedicinePrice));
        binding.tvPriceTotal.setVisibility(View.INVISIBLE);

        //  findViewById(R.id.tv_total_medicine_price).setVisibility(View.GONE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                this.finish();
                break;
            case R.id.btn_upload_data:
                if (SystemUtility.isConnectedToInternet(this)) {
                    if (ACTIVITY.equals(Constants.PRODUCT_STOCK_REQUISITION)) {
                        uploadRequisition();


                    } else if (ACTIVITY.equals(Constants.PRODUCT_STOCK_RECEIVE)) {
                        medicineReceive();
                    }
                } else {
                    SystemUtility.openInternetSettingsActivity(this);
                }
                break;
            default:
                break;
        }

    }


    private void uploadRequisition() {
        RequestData request = new RequestData(RequestType.STOCK_INVENTORY, RequestName.MEDICINE_REQUISITION, Constants.MODULE_BUNCH_PUSH);
        request.setData(JSONCreateor.createMedicineRequisitionJSON(medicineList));

        CommiunicationTask commiunicationTask = new CommiunicationTask(this, request, getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));
        commiunicationTask.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Message msg) {
                long itemId = App.getContext().getDB().saveMedicineRequisition(medicineList, App.getContext().getUserInfo().getUserId());
                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                    showOneButtonDialog(getResources().getString(R.string.requisition_data_sending_failed) + "\n" + getResources().getString(R.string.network_error) + "\n" + msg.getData().getString(TaskKey.ERROR_MSG), R.drawable.error, Color.RED);
                } else {
                    ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                    if (response.getResponseCode().equalsIgnoreCase("00")) {

                        if (response.getErrorCode().equals("0308")) {
                            App.getContext().getDB().removeRequisitionRequest(itemId);
                            String msgaa = getResources().getString(R.string.incomplete_requisition_request_server_exist_prompt);
                            try {
                                msgaa = msgaa.replace("REQUISITION_NUMBER", response.getParamJson().getString(Column.REQ_NO));
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            showOneButtonDialog(msgaa, R.drawable.information, Color.BLACK);
                        } else {
                            showOneButtonDialog(getResources().getString(R.string.requisition_data_sending_failed) + "\n" + response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
                        }

                    } else {
                        try {
                            long reqNo = response.getDataJson().getLong(Column.REQ_NO);
                            //Save requisition number return from server into database
                            if (reqNo > 0) {
                                App.getContext().getDB().updateRequisitionMasterTable(itemId, reqNo, RequisitionStatus.Initiated.toString(), -1);
                                showOneButtonDialog(getResources().getString(R.string.medicine_requisition_successfully) + "\n" + getResources().getString(R.string.requisition_number) + ":" + reqNo, R.drawable.information, Color.BLACK);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        });
        commiunicationTask.execute();
    }


    /**
     * The adjustment master id.
     */
    long adjustmentMasterId;
    private void medicineAdjustment() {
        try {
            RequestData request = new RequestData(RequestType.STOCK_INVENTORY, RequestName.FCM_STOCK_ADJUST, Constants.MODULE_DATA_GET);
            request.setData(JSONCreateor.createMedicineAdjutmentRequestJSON(adjustMedicineList));

            CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
            task.setCompleteListener(msg -> {
                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                    adjustmentMasterId = App.getContext().getDB().saveStockAdjustmentRequest(adjustMedicineList, App.getContext().getUserInfo().getUserId(), null, StockAdjustmentRequestStatus.OPEN.toString());
                    showOneButtonDialog(getResources().getString(R.string.adjustment_data_sending_failed) + "\n" + getResources().getString(R.string.network_error) + "\n" + msg.getData().getString(TaskKey.ERROR_MSG), R.drawable.error, Color.RED);
                } else {
                    ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                    if (response.getResponseCode().equalsIgnoreCase("00")) {
                        if (response.getErrorCode().equals("0308")) {
                            App.getContext().getDB().removeAdjustmentRequest();
                            String msgaa = getResources().getString(R.string.incomplete_adjustment_request_server_exist_prompt_again);
                            try {
                                msgaa = msgaa.replace("ADJUSTMENT_NUMBER", response.getParamJson().getString(Column.REQUEST_NUMBER));
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            showOneButtonDialog(msgaa, R.drawable.information, Color.BLACK);
                        } else {
                            adjustmentMasterId = App.getContext().getDB().saveStockAdjustmentRequest(adjustMedicineList, App.getContext().getUserInfo().getUserId(), null, StockAdjustmentRequestStatus.OPEN.toString());
                            showOneButtonDialog(getResources().getString(R.string.adjustment_data_sending_failed) + "\n" + response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
                        }
                    } else {
                        try {
                            String reqNo = response.getDataJson().getString(Column.REQUEST_NUMBER);
                            adjustmentMasterId = App.getContext().getDB().saveStockAdjustmentRequest(adjustMedicineList, App.getContext().getUserInfo().getUserId(), reqNo, StockAdjustmentRequestStatus.OPEN.toString());
                            showOneButtonDialog(getResources().getString(R.string.medicine_adjustment_successfully) + "\n" + getResources().getString(R.string.adjustment_request_number) + reqNo, R.drawable.information, Color.BLACK);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            });
            task.execute();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void medicineReceive() {

        RequestData request = new RequestData(RequestType.STOCK_INVENTORY, RequestName.MEDICINE_RECEIVE, Constants.MODULE_BUNCH_PUSH);
        final long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        try {
            JSONObject jParamObj = new JSONObject();
            jParamObj.put(Column.REQ_NO, Long.toString(medReqInfo.getRequisitionNo()));
            request.setParam1(jParamObj);
            request.setData(JSONCreateor.createMedicineReceivcedJSON(medReqInfo.getMedicineList(), currentTimeInMillis));


            CommiunicationTask commiunicationTask = new CommiunicationTask(this, request, getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));
            commiunicationTask.setCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(Message msg) {
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        App.getContext().getDB().saveMedicineReceived(medReqInfo.getMedicineList(), App.getContext().getUserInfo().getUserId(), medReqInfo.getRequisitionNo(), "N", -1, currentTimeInMillis);
                        App.getContext().getDB().updateRequisitionMasterTable(medReqInfo.getRequisitionId(), medReqInfo.getRequisitionNo(), RequisitionStatus.Completed.toString(), currentTimeInMillis);
                        showOneButtonDialog(getResources().getString(R.string.medicine_receive_data_sending_failed) + "\n" + getResources().getString(R.string.network_error) + "\n" + msg.getData().getString(TaskKey.ERROR_MSG), R.drawable.error, Color.RED);

                    } else {
                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (response.getResponseCode().equalsIgnoreCase("01")) {
                            ArrayList<MedicineInfo> medList;
                            try {
                                medList = JSONParser.parseMedicineReceivedJSON(response.getData());
                                App.getContext().getDB().saveMedicineReceived(medList, App.getContext().getUserInfo().getUserId(), medReqInfo.getRequisitionNo(), "Y", -1, currentTimeInMillis);
                                App.getContext().getDB().updateRequisitionMasterTable(medReqInfo.getRequisitionId(), medReqInfo.getRequisitionNo(), RequisitionStatus.Completed.toString(), currentTimeInMillis);
                            } catch (MhealthException e) {
                                e.printStackTrace();
                            }
                            showOneButtonDialog(getResources().getString(R.string.medicine_received_successfully), R.drawable.information, Color.BLACK);

                        } else {
                            App.getContext().getDB().saveMedicineReceived(medReqInfo.getMedicineList(), App.getContext().getUserInfo().getUserId(), medReqInfo.getRequisitionNo(), "N", -1, currentTimeInMillis);
                            App.getContext().getDB().updateRequisitionMasterTable(medReqInfo.getRequisitionId(), medReqInfo.getRequisitionNo(), RequisitionStatus.Completed.toString(), currentTimeInMillis);
                            showOneButtonDialog(getResources().getString(R.string.medicine_receive_data_sending_failed) + "\n" + response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
                        }
                    }

                }
            });
            commiunicationTask.execute();

        } catch (Exception ex) {
        }

    }
    private long consumpId = -1;

    private void medicineSell() {

        consumpId = App.getContext().getDB().cosumeMedicineToSell(medicineList, App.getContext().getUserInfo().getUserId());

        RequestData request = new RequestData(RequestType.TRANSACTION, RequestName.DIRECT_MEDICINE_SELL, Constants.MODULE_DATA_GET);
        try {
            request.setData(JSONCreateor.createMedicineSellJSON(medicineList));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        CommiunicationTask commiunicationTask = new CommiunicationTask(this, request, getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));
        commiunicationTask.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Message msg) {
                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                    showOneButtonDialog(getResources().getString(R.string.medicine_sell_data_sending_failed) + "\n" + getResources().getString(R.string.network_error), R.drawable.error, Color.RED);
                } else {
                    ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                    if (response.getResponseCode().equalsIgnoreCase("00")) {
                        showOneButtonDialog(getResources().getString(R.string.medicine_sell_data_sending_failed) + "\n" + response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
                    } else {
                        App.getContext().getDB().updateMedicineConsumptionStatus(-1, consumpId, "Y");
                        showOneButtonDialog(getResources().getString(R.string.medicine_sell_data_sending_seccessfull), R.drawable.information, Color.BLACK);
                    }
                }

            }
        });
        commiunicationTask.execute();
    }
    private void showOneButtonDialog(String msg, final int imageId, int messageColor) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView dialog = new DialogView(this, R.string.dialog_title, msg, messageColor, imageId, buttonMap);
        dialog.setOnDialogButtonClick(new OnDialogButtonClick() {
            @Override
            public void onDialogButtonClick(View view) {
//                if( parent_name.equals(StockAdjustmentRequestActivity.class.getName()) ) {
//                    StockAdjustmentRequestActivity.context.finish();
//                }
                if (parentName.equals(ProductsHomeActivity.class.getName())) {

                    finish();
                }
//                else if( parent_name.equals(MedicineReceiveActivity.class.getName()) ) {
//                    MedicineReceiveActivity.context.finish();
//                }
//                else if( parent_name.equals(MedicineRequisitionActivity.class.getName()) ){
//                    MedicineRequisitionActivity.context.finish();
//                }
                //StockRequisitionConfirmActivity.this.finish();

                    Intent intent = new Intent(StockRequisitionConfirmActivity.this, ProductsHomeActivity.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
            }
        });
        dialog.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onStart() {
        super.onStart();
        App.getContext().onStartActivity(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        App.getContext().onStartActivity(this);
    }


    @Override
    public void onBeneficiaryButtonClick(Beneficiary beneficiary) {
        AppToast.showToast(StockRequisitionConfirmActivity.this, "" + beneficiary.getBenefName());
        benName = beneficiary.getBenefName();
        binding.beneficiarySpinner.setText(benName);
        dialogFragment.dismiss();

    }
}