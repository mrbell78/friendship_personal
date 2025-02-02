package ngo.friendship.satellite.ui.stock_manage;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.constants.StockAdjustmentRequestStatus;
import ngo.friendship.satellite.databinding.ActivityAdjustConfirmBinding;
import ngo.friendship.satellite.databinding.ProductsConfirmItemsAdjustListBinding;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.model.AdjustmentMedicineInfo;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.RequisitionInfo;
import ngo.friendship.satellite.model.StockAdjustmentInfo;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.DialogView;

public class AdjustmentConfirmActivity extends BaseActivity implements View.OnClickListener {


    ArrayList<MedicineInfo> medicineList;
    ArrayList<AdjustmentMedicineInfo> adjustMedicineList;
    private RequisitionInfo medReqInfo;
    String activityPath = "", parent_name = "";
    ProgressDialog dlog;
    private TextView addMoreItem, removeItem;
    ActivityAdjustConfirmBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = ActivityAdjustConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(getString(R.string.request_to_adjustment));
        binding.toolbar.tvProductTitle.setText(R.string.adjustments_details);
        binding.toolbar.btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.btnUploadData.setOnClickListener(this);
        Bundle b = getIntent().getExtras();

        parent_name = b.getString(ActivityDataKey.PARENT_NAME);


        if (parent_name.equals(AdjustmentRequestActivity.class.getName())) {
            adjustMedicineList = (ArrayList<AdjustmentMedicineInfo>) b.getSerializable(ActivityDataKey.DATA);
            showAdjustmentMedicineList(adjustMedicineList);
            binding.llNoteSection.setVisibility(View.GONE);

        }

        if (parent_name.equals(AdjustmentFragment.class.getName())) {
            StockAdjustmentInfo adjustMedicineLists = (StockAdjustmentInfo) b.getSerializable(ActivityDataKey.DATA);
            showAdjustmentMedicineList(adjustMedicineLists.getMedicineList());
            binding.llNoteSection.setVisibility(View.GONE);
            binding.btnUploadData.setVisibility(View.GONE);
            binding.tvOrderState.setText("Adjustment :" + adjustMedicineLists.getRequestNumber() + " (" + adjustMedicineLists.getState() + ")");
            try {
                long dates = Long.parseLong(adjustMedicineLists.getRequisitionDate());
                String requisitonDate = Utility.getDateFromMillisecond(dates, Constants.DATE_FORMAT_DD_MM_YYYY);
                binding.tvDate.setText("Date : " + requisitonDate);
            } catch (Exception e) {
                e.printStackTrace();
                binding.tvDate.setText("Date : " + adjustMedicineLists.getRequisitionDate());
            }
        }
//        if (parent_name.equals(StockAdjustmentRequestActivity.class.getName())) {
//            medicineList = (ArrayList<MedicineInfo>) b.getSerializable(ActivityDataKey.DATA);
//            showDirectSaleMedicineList(medicineList);
////            btnOk.setText(R.string.sell_medicine);
////            btnOk.setIcon(R.string.mdi_basket_unfill);
//
//        }
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
//                Toast.makeText(AdjustConfirmActivity.this, "Item Remove", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        medicineList = null;
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if (App.getContext().getAppSettings() == null)
            App.getContext().readAppSettings(this);

        super.onResume();
//        activityPath= Utility.setActivityPath(this,"");

        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this);
        }


    }

    /**
     * Display medicine list.
     *
     * @param medicineList The medicine list containing all medicine with all medicine information
     */
    private void showMedicineList(ArrayList<ngo.friendship.satellite.model.MedicineInfo> medicineList) {
        double totalMedicinePrice = 0;
        for (ngo.friendship.satellite.model.MedicineInfo medicineInfo : medicineList) {
            View view = View.inflate(this, R.layout.products_confirm_items_adjust_list, null);
            binding.llMedicineRowContainer.addView(view);

            TextView tv = view.findViewById(R.id.tv_medicine_name);
            tv.setText(medicineInfo.toString());

            if (Utility.parseInt(medicineInfo.getRequiredQuantity()) > 0) {
                tv = view.findViewById(R.id.tv_medicine_quantity);
                tv.setText("" + medicineInfo.getRequiredQuantity());

                /*
                 *  Show individual medicine total price
                 */
                totalMedicinePrice += Utility.parseInt(medicineInfo.getRequiredQuantity()) * medicineInfo.getUnitPurchasePrice();

                tv = view.findViewById(R.id.tv_medicine_price);
                String medPriceStr = TextUtility.format("%s %.2f", getResources().getString(R.string.total_price), Utility.parseInt(medicineInfo.getRequiredQuantity()) * medicineInfo.getUnitPurchasePrice());
                tv.setText(medPriceStr);
            }
        }

        /*
         *  Show all medicine total price
         */
        TextView tvTotalMedicinePrice = findViewById(R.id.tv_total_medicine_price);
        String totalPriceString = TextUtility.format("%s %.2f", getResources().getString(R.string.total_medicine_price), totalMedicinePrice);
        tvTotalMedicinePrice.setText(totalPriceString);
    }

    private void showAdjustmentMedicineList(ArrayList<AdjustmentMedicineInfo> adjustMedList) {
        int i = 0;
        int totalCountItem = 0;
        for (AdjustmentMedicineInfo mi : adjustMedList) {

//            if (mi.getInputQty() > 0) {
//                i += 1;
//                ProductsConfirmItemsAdjustListBinding view = ProductsConfirmItemsAdjustListBinding.inflate(getLayoutInflater());
//                binding.llMedicineRowContainer.addView(view.getRoot());
//
//                //     TextView tv = view.findViewById(R.id.tv_medicine_name);
//                view.tvMedicineName.setText(i + ". " + mi.getMedicineName());
//                view.tvQty.setVisibility(View.INVISIBLE);
//                view.tvPrice.setText("" + mi.getInputQty());
//                totalCountItem += mi.getInputQty();
//                //   totalMedicinePrice += Utility.parseDouble("" + mi.getRequisitionPrice());
//                //view.findViewById(R.id.tv_medicine_price).setVisibility(View.GONE);
//
//            }

            i += 1;
            ProductsConfirmItemsAdjustListBinding view = ProductsConfirmItemsAdjustListBinding.inflate(getLayoutInflater());
            binding.llMedicineRowContainer.addView(view.getRoot());

            //     TextView tv = view.findViewById(R.id.tv_medicine_name);
            view.tvMedicineName.setText(i + ". " + mi.getMedicineName());
            view.tvQty.setVisibility(View.INVISIBLE);
            view.tvPrice.setText("" + mi.getInputQty());
            totalCountItem += mi.getInputQty();
        }
        binding.tvHeaderPrice.setVisibility(View.GONE);
        binding.tvQtyTotal.setVisibility(View.GONE);
        binding.tvPriceTotal.setText("" + totalCountItem);

        //   findViewById(R.id.tv_total_medicine_price).setVisibility(View.GONE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                this.finish();
                break;
            case R.id.btn_upload_data:
                if (SystemUtility.isConnectedToInternet(this)) {
//                    if( parent_name.equals(MedicineRequisitionActivity.class.getName()) ) {
//                    medicineRequisition();
//                }

                    if (parent_name.equals(AdjustmentRequestActivity.class.getName())) {
                        medicineAdjustment();
                    }
//                else if(parent_name.equals(StockAdjustmentRequestActivity.class.getName())) {
//                    medicineAdjustment();
//                }
//                else if(parent_name.equals(MedicineReceiveActivity.class.getName())) {
//                    medicineReceive();
//                }
                } else {
                    SystemUtility.openInternetSettingsActivity(this);
                }
                break;
            default:
                break;
        }

    }


//    private void medicineRequisition() {
//        Request request =new Request(RequestType.STOCK_INVENTORY, RequestName.MEDICINE_REQUISITION, Constants.MODULE_BUNCH_PUSH);
//        request.setData(JSONCreateor.createMedicineRequisitionJSON(medicineList));
//
//        CommiunicationTask commiunicationTask= new CommiunicationTask(this, request,getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));
//        commiunicationTask.setCompleteListener(new OnCompleteListener() {
//            @Override
//            public void onComplete(Message msg) {
//                long itemId = App.getContext().getDB().saveMedicineRequisition(medicineList, App.getContext().getUserInfo().getUserId());
//                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
//                    showOneButtonDialog(getResources().getString(R.string.requisition_data_sending_failed)+"\n"+getResources().getString(R.string.network_error)+"\n"+msg.getData().getString(TaskKey.ERROR_MSG), R.drawable.error, Color.RED);
//                } else {
//                    Response response=(Response) msg.getData().getSerializable(TaskKey.DATA0);
//                    if(response.getResponseCode().equalsIgnoreCase("00")){
//
//                        if(response.getErrorCode().equals("0308")){
//                            App.getContext().getDB().removeRequisitionRequest(itemId);
//                            String msgaa=getResources().getString(R.string.incomplete_requisition_request_server_exist_prompt);
//                            try {
//                                msgaa=msgaa.replace("REQUISITION_NUMBER",response.getParamJson().getString(Column.REQ_NO));
//                            } catch (JSONException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
//                            showOneButtonDialog(msgaa, R.drawable.information, Color.BLACK);
//                        }else{
//                            showOneButtonDialog(getResources().getString(R.string.requisition_data_sending_failed)+"\n"+response.getErrorCode()+"-"+response.getErrorDesc(), R.drawable.error, Color.RED);
//                        }
//
//                    }else{
//                        try {
//                            long reqNo = response.getDataJson().getLong(Column.REQ_NO);
//                            //Save requisition number return from server into database
//                            if(reqNo > 0)
//                            {
//                                App.getContext().getDB().updateRequisitionMasterTable(itemId, reqNo, RequisitionStatus.Initiated.toString(), -1);
//                                showOneButtonDialog(getResources().getString(R.string.medicine_requisition_successfully)+"\n"+getResources().getString(R.string.requisition_number)+":"+reqNo, R.drawable.information, Color.BLACK);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//            }
//        });
//        commiunicationTask.execute();
//    }


    /**
     * The adjustment master id.
     */
    long adjustmentMasterId;

    private void medicineAdjustment() {
        try {
            RequestData request = new RequestData(RequestType.STOCK_INVENTORY, RequestName.FCM_STOCK_ADJUST, Constants.MODULE_DATA_GET);
            Log.d(TAG, "medicineAdjustment: ...request data is "+adjustMedicineList);
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
                           // App.getContext().getDB().removeAdjustmentRequest();
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


//    private void medicineReceive() {
//
//        Request request =new Request(RequestType.STOCK_INVENTORY, RequestName.MEDICINE_RECEIVE,Constants.MODULE_BUNCH_PUSH);
//        final long  currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
//        try{
//            JSONObject jParamObj = new JSONObject();
//            jParamObj.put(Column.REQ_NO, Long.toString(medReqInfo.getRequisitionNo()));
//            request.setParam1(jParamObj);
//            request.setData(JSONCreateor.createMedicineReceivcedJSON(medReqInfo.getMedicineList(), currentTimeInMillis));
//
//
//            CommiunicationTask commiunicationTask= new CommiunicationTask(this, request,getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));
//            commiunicationTask.setCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(Message msg) {
//                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
//                        App.getContext().getDB().saveMedicineReceived(medReqInfo.getMedicineList(),App.getContext().getUserInfo().getUserId(), medReqInfo.getRequisitionNo(),"N", -1,currentTimeInMillis);
//                        App.getContext().getDB().updateRequisitionMasterTable(medReqInfo.getRequisitionId(), medReqInfo.getRequisitionNo(), RequisitionStatus.Completed.toString(), currentTimeInMillis);
//                        showOneButtonDialog(getResources().getString(R.string.medicine_receive_data_sending_failed)+"\n"+getResources().getString(R.string.network_error)+"\n"+msg.getData().getString(TaskKey.ERROR_MSG), R.drawable.error, Color.RED);
//
//                    } else {
//                        Response response=(Response) msg.getData().getSerializable(TaskKey.DATA0);
//                        if(response.getResponseCode().equalsIgnoreCase("01")){
//                            ArrayList<MedicineInfo> medList;
//                            try {
//                                medList = JSONParser.parseMedicineReceivedJSON(response.getData());
//                                App.getContext().getDB().saveMedicineReceived(medList, App.getContext().getUserInfo().getUserId(), medReqInfo.getRequisitionNo(),"Y", -1,currentTimeInMillis);
//                                App.getContext().getDB().updateRequisitionMasterTable(medReqInfo.getRequisitionId(), medReqInfo.getRequisitionNo(), RequisitionStatus.Completed.toString(), currentTimeInMillis);
//                            } catch (MhealthException e) {
//                                e.printStackTrace();
//                            }
//                            showOneButtonDialog(getResources().getString(R.string.medicine_received_successfully), R.drawable.information, Color.BLACK);
//
//                        }else {
//                            App.getContext().getDB().saveMedicineReceived(medReqInfo.getMedicineList(), App.getContext().getUserInfo().getUserId(), medReqInfo.getRequisitionNo(),"N", -1,currentTimeInMillis);
//                            App.getContext().getDB().updateRequisitionMasterTable(medReqInfo.getRequisitionId(), medReqInfo.getRequisitionNo(), RequisitionStatus.Completed.toString(), currentTimeInMillis);
//                            showOneButtonDialog(getResources().getString(R.string.medicine_receive_data_sending_failed)+"\n"+response.getErrorCode()+"-"+response.getErrorDesc(), R.drawable.error, Color.RED);
//                        }
//                    }
//
//                }
//            });
//            commiunicationTask.execute();
//
//        }catch(Exception ex){}
//
//    }


    private long consumpId = -1;

//    private void medicineSell() {
//
//        consumpId = App.getContext().getDB().cosumeMedicineToSell(medicineList, App.getContext().getUserInfo().getUserId());
//
//        RequestData request = new RequestData(RequestType.TRANSACTION, RequestName.DIRECT_MEDICINE_SELL, Constants.MODULE_DATA_GET);
//        try {
//            request.setData(JSONCreateor.createMedicineSellJSON(medicineList));
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        CommiunicationTask commiunicationTask = new CommiunicationTask(this, request, getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));
//        commiunicationTask.setCompleteListener(new OnCompleteListener() {
//            @Override
//            public void onComplete(Message msg) {
//                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
//                    showOneButtonDialog(getResources().getString(R.string.medicine_sell_data_sending_failed) + "\n" + getResources().getString(R.string.network_error), R.drawable.error, Color.RED);
//                } else {
//                    ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
//                    if (response.getResponseCode().equalsIgnoreCase("00")) {
//                        showOneButtonDialog(getResources().getString(R.string.medicine_sell_data_sending_failed) + "\n" + response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
//                    } else {
//                        App.getContext().getDB().updateMedicineConsumptionStatus(-1, consumpId, "Y");
//                        showOneButtonDialog(getResources().getString(R.string.medicine_sell_data_sending_seccessfull), R.drawable.information, Color.BLACK);
//                    }
//                }
//
//            }
//        });
//        commiunicationTask.execute();
//    }


    private void showOneButtonDialog(String msg, final int imageId, int messageColor) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView dialog = new DialogView(this, R.string.dialog_title, msg, messageColor, imageId, buttonMap);
        dialog.setOnDialogButtonClick(new OnDialogButtonClick() {
            @Override
            public void onDialogButtonClick(View view) {
                Intent intent = new Intent(  AdjustmentConfirmActivity.this, ProductsHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(ActivityDataKey.ACTIVITY,"AdjustmentConfirmActivity");
                startActivity(intent);
                finish();
////                if( parent_name.equals(StockAdjustmentRequestActivity.class.getName()) ) {
////                    StockAdjustmentRequestActivity.context.finish();
////                }
//                if (parent_name.equals(ProductsHomeActivity.class.getName())) {
//                    finish();
//                }
////                else if( parent_name.equals(MedicineReceiveActivity.class.getName()) ) {
////                    MedicineReceiveActivity.context.finish();
////                }
////                else if( parent_name.equals(MedicineRequisitionActivity.class.getName()) ){
////                    MedicineRequisitionActivity.context.finish();
////                }
//                AdjustmentConfirmActivity.this.finish();
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
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LanguageContextWrapper.wrap(context, AppPreference.getString(context, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)));
    }


}