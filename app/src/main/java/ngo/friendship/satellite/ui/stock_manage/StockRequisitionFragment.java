package ngo.friendship.satellite.ui.stock_manage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.FragmentProductStockBinding;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.RequisitionInfo;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.viewmodels.OfflineViewModel;
import ngo.friendship.satellite.views.AppToast;
import ngo.friendship.satellite.views.DialogView;

@AndroidEntryPoint
public class StockRequisitionFragment extends Fragment implements View.OnClickListener {

    FragmentProductStockBinding binding;
    ArrayList<RequisitionInfo> requisitionList = new ArrayList<>();
    ArrayList<RequisitionInfo> requisitionListFilter=new ArrayList<>();
    Activity ctx;
    //    RequisitionInfo reqInfo;
    long requisitionId = -1;
    boolean isIniteadtedClick = false;
    boolean isApprovedClicked = false;
    boolean isWaitingToApproveClick = false;
    boolean isCanceledClick = false;
    boolean isCompletedClick = false;

    OfflineViewModel viewModel;

    public StockRequisitionFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static StockRequisitionFragment newInstance() {
        StockRequisitionFragment fragment = new StockRequisitionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductStockBinding.inflate(getLayoutInflater(), container, false);
        binding.toolbar.tvProductTitle.setText("Stock Requisition");
        binding.layoutSync.llPenddingSync.setVisibility(View.GONE);
        binding.layoutSync.viewLine.setVisibility(View.GONE);
        binding.layoutSync.llAllSync.setVisibility(View.GONE);
        binding.layoutSync.viewLine2.setVisibility(View.GONE);


        binding.toolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ctx.onBackPressed();
            }
        });

        binding.layoutSync.llGetData.setOnClickListener(v -> {
            if (SystemUtility.isConnectedToInternet(getActivity())) {
                showDataRetrieveConfirmationPrompt(getResources().getString(R.string.retrieve_data_confirmation));
            } else {
                SystemUtility.openInternetSettingsActivity(getActivity());
            }

        });
        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
            binding.layoutSync.llGetData.setVisibility(View.VISIBLE);
            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
        });
        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
            binding.layoutSync.llGetData.setVisibility(View.GONE);
            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
        });


        ctx = getActivity();
        binding.toolbar.actionCart.setVisibility(View.GONE);
        binding.toolbar.llCard.setVisibility(View.GONE);

        binding.llIniteadtedItem.setOnClickListener(this);
        binding.llApprovedItem.setOnClickListener(this);
//        binding.llWaitingToApprove.setOnClickListener(this);
        binding.llCanceled.setOnClickListener(this);
        binding.llCompleted.setOnClickListener(this);
        viewModel = new ViewModelProvider(this).get(OfflineViewModel.class);
        // reqInfo = App.getContext().getDB().getRequisitionMedicineInfo(requisitionId);
        binding.viewStock.setOnClickListener(view -> startActivity(new Intent(ctx, CurrentStockActivity.class)));
        binding.fabProductStock.setOnClickListener(view -> {
            Intent productRiseActivity = new Intent(ctx, StockRequisitionRiseActivity.class);
            productRiseActivity.putExtra(ActivityDataKey.PARENT_NAME, "" + getResources().getString(R.string.create_requisition));
            startActivity(productRiseActivity);
        });
       // retrieveRequisitionList(false);
        getLocalRequsition();
        //getRequisitionByNetwork();
        return binding.getRoot();
    }

//    private void getRequisitionByNetwork() {
//        if (SystemUtility.isConnectedToInternet(ctx)) {
//            requisitionId = App.getContext().getDB().getIncompletedMedicineRequisitionId();
//            if (requisitionId > 0) {
//                reqInfo = App.getContext().getDB().getRequisitionMedicineInfo(requisitionId);
//                checkRequisitionStatus(reqInfo.getRequisitionNo() + "");
//            } else {
//                // showTwoButtonErrorDialog(R.string.dialog_title, R.string.requisition_request_not_found, R.drawable.warning);
//            }
//        } else {
//            getLocalRequsition();
//        }
//    }

    public void getLocalRequsition() {
        App.showProgressBarMessage(getActivity(), "Please wait ...", "");
        viewModel.getAllRequisition().observe(getActivity(), requisitionInfos -> {
            App.hideProgressBarMessage();
            requisitionList = (ArrayList<RequisitionInfo>) requisitionInfos;
            showData(requisitionList);
        });
    }

    private void getFilterWisedata(String state) {
        requisitionListFilter = new ArrayList<>();
        for (RequisitionInfo item : requisitionList) {
            if (item.getRequisitionStatus().equals(state)) {
                requisitionListFilter.add(item);
            }
        }
        showData(requisitionListFilter);
    }

    private int getItemCount(String state) {
        List<RequisitionInfo> localRequisitionList = new ArrayList<>();
        if (requisitionList.size() > 0) {
            for (RequisitionInfo item : requisitionList) {
                if (state.equalsIgnoreCase(item.getRequisitionStatus())) {
                    localRequisitionList.add(item);
                }
            }
        }
        return localRequisitionList.size();
    }

    private void showData(ArrayList<RequisitionInfo> requisitionLists) {
        binding.tvInitiatedItemCount.setText("" + getItemCount("Initiated"));
        binding.tvApprovedItemCount.setText("" + getItemCount("Accepted"));
        binding.tvCanceledCount.setText("" + getItemCount("Rejected"));
        binding.tvCompletedCount.setText("" + getItemCount("Completed"));
        if (requisitionLists.size() > 0) {
            binding.noData.serviceNotFound.setVisibility(View.INVISIBLE);
        } else {
            binding.noData.serviceNotFound.setVisibility(View.VISIBLE);
        }
        binding.llRequisitionRowContainer.removeAllViews();
        if (requisitionLists == null || requisitionLists.size() == 0) {
//            AppToast.showToast(this, R.string.data_not_available);
            return;
        }

        for (int i = 0; i < requisitionLists.size(); i++) {
            View view = View.inflate(ctx, R.layout.requisition_list_row, null);
            binding.llRequisitionRowContainer.addView(view);
            RequisitionInfo requisitionInfo = requisitionLists.get(i);
            TextView tvDate = view.findViewById(R.id.tv_date);
            tvDate.setText(requisitionInfo.getRequisitionDate());

            TextView reqNo = view.findViewById(R.id.tv_req_no);
            reqNo.setText(requisitionInfo.getRequisitionNo() + "");
            TextView tvRequisitionRequestStatus = view.findViewById(R.id.tv_requisition_request_status);
            LinearLayout llProductStock = view.findViewById(R.id.llProductStock);
            llProductStock.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), StockRequisitionConfirmActivity.class);
                intent.putExtra(ActivityDataKey.DATA, requisitionInfo);
                intent.putExtra(ActivityDataKey.PARENT_NAME, this.getClass().getName());
                intent.putExtra(ActivityDataKey.ACTIVITY, Constants.PRODUCT_STOCK_REQUISITION_DETAILS);
                startActivity(intent);
            });
            if (("accepted").equalsIgnoreCase(requisitionInfo.getRequisitionStatus())) {
                tvRequisitionRequestStatus.setText("Waiting For Receive");
                tvRequisitionRequestStatus.setTextColor(getResources().getColor(R.color.white));
                tvRequisitionRequestStatus.setBackgroundColor(getResources().getColor(R.color.app_color));
                tvRequisitionRequestStatus.setBackgroundResource(R.drawable.border_corner_rounded_drawer);
                // tvRequisitionRequestStatus.setGravity(Gravity.CENTER_HORIZONTAL);
                tvRequisitionRequestStatus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tvRequisitionRequestStatus.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), StockRequisitionConfirmActivity.class);
                    intent.putExtra(ActivityDataKey.DATA, requisitionInfo);
                    intent.putExtra(ActivityDataKey.PARENT_NAME, this.getClass().getName());
                    intent.putExtra(ActivityDataKey.ACTIVITY, Constants.PRODUCT_STOCK_RECEIVE);
                    startActivity(intent);
                });

            } else {
                tvRequisitionRequestStatus.setText("" + requisitionInfo.getRequisitionStatus());
            }
            TextView tvRequisitionPrice = view.findViewById(R.id.tv_requisition_price);
            tvRequisitionPrice.setText(TextUtility.format("%.2f", requisitionInfo.getRequisitionMedicinePrice()));
            TextView tvReceivePrice = view.findViewById(R.id.tv_receive_price);
            tvReceivePrice.setText(TextUtility.format("%.2f", requisitionInfo.getReceiveMedicinePrice()));
            view.setTag(i);
            // view.setOnClickListener(ctx);

        }
    }

    private void showDataRetrieveConfirmationPrompt(String msg) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_yes);
        buttonMap.put(2, R.string.btn_no);
        DialogView dialog = new DialogView(ctx, R.string.dialog_title, msg, R.drawable.information, buttonMap);
        dialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {
                switch (view.getId()) {
                    case 1:
                        retrieveRequisitionList(true);

                        break;
                }

            }
        });
        dialog.show();
    }

    private void retrieveRequisitionList(boolean isGetRequest) {
        if (SystemUtility.isConnectedToInternet(getActivity())) {
            RequestData request = new RequestData(RequestType.STOCK_INVENTORY, RequestName.LAST7_REQUISITION, Constants.MODULE_DATA_GET);

            CommiunicationTask commiunicationTask = new CommiunicationTask(ctx, request, getResources().getString(R.string.retrieving_data), getResources().getString(R.string.please_wait));
            commiunicationTask.setCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(Message msg) {
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        showOneButtonDialog(getResources().getString(R.string.network_error) + "\n" + msg.getData().getString(TaskKey.ERROR_MSG), R.drawable.error, Color.RED);
                    } else {
                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);

                        if (response.getResponseCode().equalsIgnoreCase("00")) {
                            if (isGetRequest) {
                                showOneButtonDialog(response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
                            } else {
                                requisitionList = App.getContext().getDB().getLast7Requisition();
                                requisitionListFilter = App.getContext().getDB().getLast7Requisition();
                            }

                        } else {
                            try {
                                ArrayList<RequisitionInfo> requisitionLists = JSONParser.pasreRequisitionListJSON(response.getData());
                                App.getContext().getDB().saveRequisitionList(requisitionLists, App.getContext().getUserInfo().getUserId(),response.getParamJson());
                                requisitionList = App.getContext().getDB().getLast7Requisition();
                                requisitionListFilter = App.getContext().getDB().getLast7Requisition();
                            } catch (MhealthException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        showData(requisitionListFilter);
                    }

                }
            });
            commiunicationTask.execute();
        } else {
            requisitionList = App.getContext().getDB().getLast7Requisition();
            requisitionListFilter = App.getContext().getDB().getLast7Requisition();
            AppToast.showToastWarnaing(getActivity(), getActivity().getResources().getString(R.string.connect_to_internet_prompt));
        }
    }

//    private void medicineReceive() {
//
//        RequestData request = new RequestData(RequestType.STOCK_INVENTORY, RequestName.MEDICINE_RECEIVE, Constants.MODULE_BUNCH_PUSH);
//        final long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
//        try {
////            JSONObject jParamObj = new JSONObject();
////            jParamObj.put(Column.REQ_NO, Long.toString(medReqInfo.getRequisitionNo()));
//           // request.setParam1(jParamObj);
//           //request.setData(JSONCreateor.createMedicineReceivcedJSON(medReqInfo.getMedicineList(), currentTimeInMillis));
//
//
//            CommiunicationTask commiunicationTask = new CommiunicationTask(getActivity(), request, getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));
//            commiunicationTask.setCompleteListener(new OnCompleteListener() {
//                @Override
//                public void onComplete(Message msg) {
//                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
////                        App.getContext().getDB().saveMedicineReceived(medReqInfo.getMedicineList(), App.getContext().getUserInfo().getUserId(), medReqInfo.getRequisitionNo(), "N", -1, currentTimeInMillis);
////                        App.getContext().getDB().updateRequisitionMasterTable(medReqInfo.getRequisitionId(), medReqInfo.getRequisitionNo(), RequisitionStatus.Completed.toString(), currentTimeInMillis);
//                        showOneButtonDialog(getResources().getString(R.string.medicine_receive_data_sending_failed) + "\n" + getResources().getString(R.string.network_error) + "\n" + msg.getData().getString(TaskKey.ERROR_MSG), R.drawable.error, Color.RED);
//
//                    } else {
//                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
//                        if (response.getResponseCode().equalsIgnoreCase("01")) {
//                            ArrayList<MedicineInfo> medList;
//                            try {
//                                medList = JSONParser.parseMedicineReceivedJSON(response.getData());
//                                App.getContext().getDB().saveMedicineReceived(medList, App.getContext().getUserInfo().getUserId(), medReqInfo.getRequisitionNo(), "Y", -1, currentTimeInMillis);
//                                App.getContext().getDB().updateRequisitionMasterTable(medReqInfo.getRequisitionId(), medReqInfo.getRequisitionNo(), RequisitionStatus.Completed.toString(), currentTimeInMillis);
//                            } catch (MhealthException e) {
//                                e.printStackTrace();
//                            }
//                            showOneButtonDialog(getResources().getString(R.string.medicine_received_successfully), R.drawable.information, Color.BLACK);
//
//                        } else {
////                            App.getContext().getDB().saveMedicineReceived(medReqInfo.getMedicineList(), App.getContext().getUserInfo().getUserId(), medReqInfo.getRequisitionNo(), "N", -1, currentTimeInMillis);
////                            App.getContext().getDB().updateRequisitionMasterTable(medReqInfo.getRequisitionId(), medReqInfo.getRequisitionNo(), RequisitionStatus.Completed.toString(), currentTimeInMillis);
//                            showOneButtonDialog(getResources().getString(R.string.medicine_receive_data_sending_failed) + "\n" + response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
//                        }
//                    }
//
//                }
//            });
//            commiunicationTask.execute();
//
//        } catch (Exception ex) {
//        }
//
//    }

    private void showOneButtonDialog(String msg, final int imageId, int messageColor) {

        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView dialog = new DialogView(ctx, R.string.dialog_title, msg, messageColor, imageId, buttonMap);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null) {
            int rowIndex = (Integer) v.getTag();
//            Intent intent = new Intent(ctx, RequisitionDetailActivity.class);
//            intent.putExtra(ActivityDataKey.REQUISITION_ID, requisitionList.get(rowIndex).getRequisitionId());
//            intent.putExtra(ActivityDataKey.REQUISITION_NO, requisitionList.get(rowIndex).getRequisitionNo());
////            intent.putExtra(ActivityDataKey.ACTIVITY_PATH, activityPath);
//            startActivity(intent);
            return;
        }

        switch (v.getId()) {
            case R.id.btn_close:
                ctx.finish();
                break;
            case R.id.btn_retrieve_data:
                if (SystemUtility.isConnectedToInternet(ctx)) {
                    showDataRetrieveConfirmationPrompt(getResources().getString(R.string.retrieve_data_confirmation));

                } else
                    SystemUtility.openInternetSettingsActivity(ctx);
                break;
            case R.id.initeated_items:
                if (v.getTag() != null) {
//                    int rowIndex = (Integer) v.getTag();
//                    Intent intent = new Intent(ctx, RequisitionDetailActivity.class);
//                    intent.putExtra(ActivityDataKey.REQUISITION_ID, requisitionList.get(rowIndex).getRequisitionId());
//                    intent.putExtra(ActivityDataKey.REQUISITION_NO, requisitionList.get(rowIndex).getRequisitionNo());
//                    // intent.putExtra(ActivityDataKey.ACTIVITY_PATH, activityPath);
//                    startActivity(intent);
                    break;
                }
            case R.id.llIniteadtedItem:
                if (!isIniteadtedClick) {
                    isIniteadtedClick = true;
                    tabActiveDeactive(
                            binding.tvInitiatedItemCount,
                            binding.tvApprovedItemCount,
                            binding.tvCanceledCount,
                            binding.tvCompletedCount,
                            true);
                    getFilterWisedata("Initiated");

                    //   binding.tvInitiatedItemCount.setText(""+requisitionListFilter.size());
                } else {
                    isIniteadtedClick = false;
                    tabActiveDeactive(
                            binding.tvInitiatedItemCount,
                            binding.tvApprovedItemCount,
                            binding.tvCanceledCount,
                            binding.tvCompletedCount,
                            false);
                    getFilterWisedata("");
                    //  getRequisitionByNetwork();
                    //retrieveRequisitionList(false);
                    getLocalRequsition();
                }
                break;

            case R.id.llApprovedItem:
                if (!isApprovedClicked) {
                    isApprovedClicked = true;

                    tabActiveDeactive(
                            binding.tvApprovedItemCount,
                            binding.tvInitiatedItemCount,
                            binding.tvCanceledCount,
                            binding.tvCompletedCount,
                            true);
                    getFilterWisedata("Accepted");

                } else {
                    isApprovedClicked = false;
                    tabActiveDeactive(
                            binding.tvApprovedItemCount,
                            binding.tvInitiatedItemCount,
                            binding.tvCanceledCount,
                            binding.tvCompletedCount,
                            false
                    );
                    //retrieveRequisitionList(false);
                    getLocalRequsition();
                    //getRequisitionByNetwork();
                }
                break;

            case R.id.llCanceled:
                if (!isCanceledClick) {
                    isCanceledClick = true;

                    tabActiveDeactive(
                            binding.tvCanceledCount,
                            binding.tvApprovedItemCount,
                            binding.tvInitiatedItemCount,
                            binding.tvCompletedCount,
                            true);
                    getFilterWisedata("Rejected");
                } else {
                    isCanceledClick = false;

                    tabActiveDeactive(
                            binding.tvCanceledCount,
                            binding.tvApprovedItemCount,
                            binding.tvInitiatedItemCount,
                            binding.tvCompletedCount,
                            false
                    );
                    getFilterWisedata("");
                    //  retrieveRequisitionList(false);
                    getLocalRequsition();
                }
                break;

            case R.id.llCompleted:
                if (!isCompletedClick) {
                    isCompletedClick = true;

                    tabActiveDeactive(
                            binding.tvCompletedCount,
                            binding.tvCanceledCount,
                            binding.tvApprovedItemCount,
                            binding.tvInitiatedItemCount,

                            true);
                    getFilterWisedata("Completed");
                } else {
                    isCompletedClick = false;

                    tabActiveDeactive(
                            binding.tvCompletedCount,
                            binding.tvCanceledCount,
                            binding.tvApprovedItemCount,
                            binding.tvInitiatedItemCount,

                            false
                    );
                    getFilterWisedata("");
                    //   retrieveRequisitionList(false);
                    getLocalRequsition();
                    // getRequisitionByNetwork();
                }
                break;

            default:
                //retrieveRequisitionList(false);
                getLocalRequsition();
                break;
        }

    }

    public void tabActiveDeactive(
            TextView activeRoundCount,
            TextView deactivateRoundCount2,
            TextView deactivateRoundCount3,
            TextView deactivateRoundCount4,
            boolean activeStatus
    ) {

        if (activeStatus) {
            activeRoundCount.setBackground(getResources().getDrawable(R.drawable.active_border_rounded));
            activeRoundCount.setTextColor(getResources().getColor(R.color.white));
        } else {
            activeRoundCount.setBackground(getResources().getDrawable(R.drawable.border_rounded_text_view));
            activeRoundCount.setTextColor(getResources().getColor(R.color.app_color));
        }


        deactivateRoundCount2.setBackground(getResources().getDrawable(R.drawable.border_rounded_text_view));
        deactivateRoundCount2.setTextColor(getResources().getColor(R.color.app_color));

        deactivateRoundCount3.setBackground(getResources().getDrawable(R.drawable.border_rounded_ash));
        deactivateRoundCount3.setTextColor(getResources().getColor(R.color.app_color));

        deactivateRoundCount4.setBackground(getResources().getDrawable(R.drawable.border_rounded_green));
        deactivateRoundCount4.setTextColor(getResources().getColor(R.color.app_color));


    }


}