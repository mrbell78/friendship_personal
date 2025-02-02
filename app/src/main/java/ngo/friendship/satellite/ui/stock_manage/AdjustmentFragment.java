package ngo.friendship.satellite.ui.stock_manage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import dagger.hilt.android.AndroidEntryPoint;
import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.constants.StockAdjustmentRequestStatus;
import ngo.friendship.satellite.databinding.FragmentProductAdjustmentBinding;

import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.model.AdjustmentMedicineInfo;
import ngo.friendship.satellite.model.StockAdjustmentInfo;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.viewmodels.OfflineViewModel;
import ngo.friendship.satellite.views.AppToast;
import ngo.friendship.satellite.views.DialogView;

@AndroidEntryPoint
public class AdjustmentFragment extends Fragment {

    FragmentProductAdjustmentBinding binding;
    private final int EXIT_ON_OK = 1;
    Activity ctx;
    String ACTIVE_TAB = "";
    int activeColor = 0, deActiveColor = 0;
    ArrayList<StockAdjustmentInfo> stockAdjustmentInfoList;
    OfflineViewModel mainViewModel;

    public AdjustmentFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AdjustmentFragment newInstance() {
        AdjustmentFragment fragment = new AdjustmentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ctx = getActivity();
        activeColor = ContextCompat.getColor(getActivity(), R.color.app_color);
        deActiveColor = ContextCompat.getColor(getActivity(), R.color.black);

        binding = FragmentProductAdjustmentBinding.inflate(getLayoutInflater(), container, false);
        binding.toolbar.tvProductTitle.setText(getActivity().getResources().getString(R.string.product_stock_adjust));
        binding.toolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctx.onBackPressed();
            }
        });
//        mainViewModel = new ViewModelProvider(this).get(OfflineViewModel.class);
//        mainViewModel.getStockAdjustmentInfoList().observe(getActivity(), new Observer<List<StockAdjustmentInfo>>() {
//            @Override
//            public void onChanged(List<StockAdjustmentInfo> stockAdjustmentInfos) {
//                showData();
//
//            }
//        });

        showData();

        binding.ivProductAdjust.setColorFilter(activeColor);
        binding.tvProductAdjust.setTextColor(activeColor);
        FloatingActionButton fab = binding.getRoot().findViewById(R.id.fabStockAdjustment);
        //binding.fabStockAdjustment.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 50, 50)));

        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
        });
        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
        });

        binding.layoutSync.llAllSync.setOnClickListener(v -> {
            getStatus();

        });


        ACTIVE_TAB = Constants.PRODUCT_ADJUST_ADJUST_TAB;
        binding.llProductAdjust.setOnClickListener(view -> {

            binding.ivProductAdjust.setColorFilter(activeColor);
            binding.tvProductAdjust.setTextColor(activeColor);

            binding.ivProductReturn.setColorFilter(deActiveColor);
            binding.tvProductReturn.setTextColor(deActiveColor);
            binding.ivProductDamages.setColorFilter(deActiveColor);
            binding.tvProductDamages.setTextColor(deActiveColor);

            ACTIVE_TAB = Constants.PRODUCT_ADJUST_ADJUST_TAB;
        });
        binding.llAdjustReturn.setOnClickListener(view -> {
            binding.ivProductReturn.setColorFilter(activeColor);
            binding.tvProductReturn.setTextColor(activeColor);

            binding.ivProductAdjust.setColorFilter(deActiveColor);
            binding.tvProductAdjust.setTextColor(deActiveColor);

            binding.ivProductDamages.setColorFilter(deActiveColor);
            binding.tvProductDamages.setTextColor(deActiveColor);

            ACTIVE_TAB = Constants.PRODUCT_ADJUST_RETURN_TAB;

        });

        binding.llAdjustDamages.setOnClickListener(view -> {
            binding.ivProductDamages.setColorFilter(activeColor);
            binding.tvProductDamages.setTextColor(activeColor);

            binding.ivProductReturn.setColorFilter(deActiveColor);
            binding.tvProductReturn.setTextColor(deActiveColor);

            binding.ivProductAdjust.setColorFilter(deActiveColor);
            binding.tvProductAdjust.setTextColor(deActiveColor);


            ACTIVE_TAB = Constants.PRODUCT_ADJUST_DAMAGES_TAB;

        });

        if (App.getContext().getDB().isInCompleteAdjustmentRequestExist()) {
            binding.fabStockAdjustment.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.ash_gray));
            binding.fabStockAdjustment.setClickable(false);

        }else{
            binding.fabStockAdjustment.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.app_color));
            binding.fabStockAdjustment.setClickable(true);
        }
        binding.fabStockAdjustment.setOnClickListener(view -> {
            if (App.getContext().getDB().isInCompleteAdjustmentRequestExist()) {
                String msg = getResources().getString(R.string.incomplete_adjustment_request_exist_prompt);
                msg = msg.replace("ADJUSTMENT_NUMBER", App.getContext().getDB().getAdjustmentRequestNumber());
                AppToast.showToastWarnaing(getActivity(), "" + msg);
            } else {
                if (ACTIVE_TAB.equals(Constants.PRODUCT_ADJUST_ADJUST_TAB)) {
//                Intent i = new Intent(ctx, ProductRiseActivity.class);
//                i.putExtra(ActivityDataKey.PARENT_NAME,""+getResources().getString(R.string.product_adjustment).replace("\n"," "));
//                i.putExtra(ActivityDataKey.ACTIVITY,""+Constants.PRODUCT_ADJUST_ADJUST_TAB);
//                startActivity(i);
                    Intent i = new Intent(getActivity(), AdjustmentRequestActivity.class);
                    startActivity(i);

                } else if (ACTIVE_TAB.equals(Constants.PRODUCT_ADJUST_DAMAGES_TAB)) {
                    Intent i = new Intent(ctx, StockRequisitionRiseActivity.class);
                    i.putExtra(ActivityDataKey.PARENT_NAME, "" + getResources().getString(R.string.product_damages).replace("\n", " "));
                    i.putExtra(ActivityDataKey.ACTIVITY, "" + Constants.PRODUCT_ADJUST_DAMAGES_TAB);
                    startActivity(i);
                } else if (ACTIVE_TAB.equals(Constants.PRODUCT_ADJUST_RETURN_TAB)) {
                    Intent i = new Intent(ctx, StockRequisitionRiseActivity.class);
                    i.putExtra(ActivityDataKey.PARENT_NAME, "" + getResources().getString(R.string.product_return).replace("\n", " "));
                    i.putExtra(ActivityDataKey.ACTIVITY, "" + Constants.PRODUCT_ADJUST_RETURN_TAB);
                    startActivity(i);
                }
            }
        });
        return binding.getRoot();
    }

    private void showData() {
        if (App.getContext().getDB().isInCompleteAdjustmentRequestExist()) {
            binding.fabStockAdjustment.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.ash_gray));
            binding.fabStockAdjustment.setClickable(false);
        }else{
            binding.fabStockAdjustment.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.app_color));
            binding.fabStockAdjustment.setClickable(true);
        }
        stockAdjustmentInfoList = App.getContext().getDB().getStockAdjustmentInfoList();
        if (stockAdjustmentInfoList.size() > 0) {
            binding.dataNotFound.serviceNotFound.setVisibility(View.GONE);
        } else {
            binding.dataNotFound.serviceNotFound.setVisibility(View.VISIBLE);
        }
        binding.llRequisitionRowContainer.removeAllViews();
        for (int i = 0; i < stockAdjustmentInfoList.size(); i++) {
            View view = View.inflate(ctx, R.layout.requisition_list_row, null);
            binding.llRequisitionRowContainer.addView(view);

            StockAdjustmentInfo requisitionInfo = stockAdjustmentInfoList.get(i);
            TextView tv_requisition_request_status = view.findViewById(R.id.tv_requisition_request_status);
            tv_requisition_request_status.setText("" + requisitionInfo.getState());
            TextView tvDate = view.findViewById(R.id.tv_date);
            try {
                long dd = Long.parseLong(requisitionInfo.getRequisitionDate());
                String dates = Utility.getDateFromMillisecond(dd, Constants.DATE_FORMAT_DD_MM_YYYY);
                tvDate.setText(dates);
            } catch (Exception e) {
                e.printStackTrace();
                tvDate.setText(requisitionInfo.getRequisitionDate());
            }

            TextView reqNo = view.findViewById(R.id.tv_req_no);
            reqNo.setText("" + requisitionInfo.getRequestNumber());

            TextView tvRequisitionPrice = view.findViewById(R.id.tv_requisition_price);
            tvRequisitionPrice.setVisibility(View.VISIBLE);
            // tvRequisitionPrice.setText(TextUtility.format("%.2f", requisitionInfo.getRequestNumber()));

//            TextView tvReceivePrice = view.findViewById(R.id.tv_receive_price);
            int totalQty = 0;
            for (AdjustmentMedicineInfo medicineInfo : requisitionInfo.getMedicineList()) {
                totalQty += medicineInfo.getInputQty();
            }

            tvRequisitionPrice.setText("" + totalQty);
            LinearLayout llProductStock = view.findViewById(R.id.llProductStock);
            view.setTag(i);
            llProductStock.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), AdjustmentConfirmActivity.class);
                intent.putExtra(ActivityDataKey.DATA, requisitionInfo);
                intent.putExtra(ActivityDataKey.PARENT_NAME, this.getClass().getName());
//                intent.putExtra(ActivityDataKey.ACTIVITY, Constants.PRODUCT_REQUISITION_STOCK_DETAILS);
                startActivity(intent);


            });

            //view.setOnClickListener(getActivity());
        }


    }

    private void showDataRetrieveConfirmationPrompt(String msg) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_yes);
        buttonMap.put(2, R.string.btn_no);
        DialogView dialog = new DialogView(getActivity(), R.string.dialog_title, msg, R.drawable.information, buttonMap);
        dialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {
                switch (view.getId()) {
                    case 1:
                        //  retrieveRequisitionList();
                        break;
                }

            }
        });
        dialog.show();
    }


    private void showOneButtonDialog(String msg, final int imageId, int messageColor, final int type) {

        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView dialog = new DialogView(getActivity(), R.string.dialog_title, msg, imageId, buttonMap);
        dialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {
                if (type == EXIT_ON_OK) {
                    getActivity().finish();
                }

            }
        });
        dialog.show();
    }

    private void getStatus() {
        JSONObject data = new JSONObject();
        JSONObject param = new JSONObject();
        try {
            param.put("REQUEST_NUMBER", stockAdjustmentInfoList.get(0).getRequestNumber());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        sendRequest(RequestType.STOCK_INVENTORY, RequestName.STOCK_ADJUST_REQ_STATUS, Constants.MODULE_DATA_GET, data, param, R.string.retrieving_data, R.string.please_wait);
    }

    private void sendRequest(final String requestType, final String requestName, final String moduleName, JSONObject data, JSONObject param, int dilogTitle, int diloagMsg) {
        RequestData request = new RequestData(requestType, requestName, moduleName);
        request.setParam1(param);
        request.setData(data);
        CommiunicationTask commiunicationTask = new CommiunicationTask(getActivity(), request, dilogTitle, diloagMsg);
        commiunicationTask.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Message msg) {

                if (requestName.equals(RequestName.STOCK_ADJUST_REQ_STATUS)) {
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                        App.showMessageDisplayDialog(getActivity(), getResources().getString(R.string.network_error), R.drawable.error, Color.RED);

                    } else {
                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (response.getResponseCode().equalsIgnoreCase("00")) {
                            App.showMessageDisplayDialog(getActivity(), response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
                        } else {
                            StockAdjustmentInfo stockAdjustmentInfo = new StockAdjustmentInfo();
                            if (stockAdjustmentInfoList.size() > 0) {
                                try {
                                    if (stockAdjustmentInfoList.get(0) == null)
                                        // Set Status and Request number


                                     stockAdjustmentInfo.setRequestNumber(response.getParamJson().getString(Column.REQUEST_NUMBER));
                                     stockAdjustmentInfo.setState(response.getParamJson().getString("STATUS"));
                                     stockAdjustmentInfo.setRequestNumber(response.getParamJson().getString(Column.REQUEST_NUMBER));

                                    /*
                                     * parse the adjusted medicine list  JSON
                                     */
                                    if (response.getData() != null) {
                                        JSONObject jDataObj = new JSONObject(response.getData());
                                        if (jDataObj.has("medicineList")) {
                                            stockAdjustmentInfo.setMedicineList(App.getContext().getDB().getMedicineList(jDataObj.getJSONArray("medicineList")));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                try {
                                    if (stockAdjustmentInfo.getState().equalsIgnoreCase(StockAdjustmentRequestStatus.APPROVED.toString())) {
                                        for (AdjustmentMedicineInfo medInfo : stockAdjustmentInfo.getMedicineList()) {
                                            if (App.getContext().getDB().isExist("medicine_stock", "MEDICINE_ID=" + medInfo.getMedicineId() + " AND  USER_ID=" + App.getContext().getUserInfo().getUserId())) {
                                                App.getContext().getDB().updateStockTable(medInfo.getMedicineId(), App.getContext().getUserInfo().getUserId(), medInfo.getAdjustQuantity(), medInfo.getUpdatedOn());
                                            } else {
                                                App.getContext().getDB().updateStockTable(medInfo.getMedicineId(), App.getContext().getUserInfo().getUserId(), medInfo.getAdjustQuantity(), medInfo.getUpdatedOn());
                                            }
                                        }
                                        App.getContext().getDB().removeAdjustmentRequest();

                                    } else if (stockAdjustmentInfo.getState().equalsIgnoreCase(StockAdjustmentRequestStatus.REJECTED.toString())) {
                                        App.getContext().getDB().removeAdjustmentRequest();
                                    } else if (!App.getContext().getDB().isInCompleteAdjustmentRequestExist()) {
                                        App.getContext().getDB().saveStockAdjustmentRequest(stockAdjustmentInfo.getMedicineList(), App.getContext().getUserInfo().getUserId(), stockAdjustmentInfo.getRequestNumber(), stockAdjustmentInfo.getState());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            showData();
                        }
                    }

                }
            }
        });


        commiunicationTask.execute();
    }


}