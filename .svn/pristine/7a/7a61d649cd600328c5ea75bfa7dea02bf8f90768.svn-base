package ngo.friendship.satellite.ui.reports;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.databinding.HealthCareReportLayoutBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.model.HealthCareReportInfo;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.views.DialogView;

/**
 * <p> Display health care report</p>
 * <b>Create Date : 29th May 2014</b><br/>
 * <b>Last Update : 16th December 2015</b><br/><br/>
 *
 * @author Kayum Hossan
 * @author Mohammed Jubayer
 */
public class HealthCareReportActivity extends BaseActivity implements OnCompleteListener {


    String activityPath;
    HealthCareReportLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = HealthCareReportLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
//            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
//            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
//        });
//        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
//            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
//            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
//        });

//        if (getIntent().getExtras() != null) {
//            activityPath = getIntent().getExtras().getString(ActivityDataKey.ACTIVITY_PATH);
//            setTitle("" + activityPath);
//        }
        setTitle("" + getResources().getString(R.string.beneficiary_health_care_report));


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        binding.layoutSync.llAllSync.setOnClickListener(v -> {
//            if (SystemUtility.isConnectedToInternet(this)) {
//                RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.INTERVIEW_STATS, Constants.MODULE_DATA_GET);
//                CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
//                task.setCompleteListener(this);
//                task.execute();
//            } else {
//                SystemUtility.openInternetSettingsActivity(this);
//            }
//        });

        MHealthTask tsk = new MHealthTask(this, Task.HEALTH_CARER_EPORT, R.string.retrieving_data, R.string.please_wait);
        tsk.setCompleteListener(this);
        tsk.execute();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Display alert dialog with one button.
     *
     * @param msg          will be displayed in the message section of the dialog
     * @param imageId      is the image drawable id which will be displayed at the dialog's title
     * @param messageColor is the color id of the dialog's message
     */
    private void showOneButtonDialog(String msg, final int imageId, int messageColor) {

        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView exitDialog = new DialogView(this, R.string.dialog_title, msg, messageColor, imageId, buttonMap);
        exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {
                // TODO Auto-generated method stub

            }
        });
        exitDialog.show();

    }

    /**
     * Show report .
     *
     * @param healthCareReport the health care report
     */
    private void showData(ArrayList<HealthCareReportInfo> healthCareReport) {
        binding.llReportRowContainer.removeAllViews();
        if (healthCareReport == null || healthCareReport.size() == 0) {
          //  showOneButtonDialog(getResources().getString(R.string.data_not_available), R.drawable.information, Color.BLACK);
            binding.itemNotFound.serviceNotFound.setVisibility(View.VISIBLE);
            return;
        }else{
            binding.itemNotFound.serviceNotFound.setVisibility(View.GONE);
        }

        int totalMonthlySale = 0;
        for (HealthCareReportInfo careInfo : healthCareReport) {
            View v = View.inflate(this, R.layout.health_care_report_row, null);
            binding.llReportRowContainer.addView(v);

            TextView tvCareType = v.findViewById(R.id.tv_care_type);
            tvCareType.setText(careInfo.getHealthCareTitle());

            TextView tvLastMonthQty = v.findViewById(R.id.tv_last_month_qty);
            //tvLastMonthQty.setText("" + careInfo.getLastMonthQuantity());
            tvLastMonthQty.setVisibility(View.GONE);

            TextView tvCurrentMonthQty = v.findViewById(R.id.tv_current_month_qty);
            tvCurrentMonthQty.setText("" + careInfo.getCurrentMonthQuantity());


            totalMonthlySale += careInfo.getCurrentMonthQuantity();

        }
        TextView tvTotalPrice = findViewById(R.id.tv_total_medicine_price);
        tvTotalPrice.setText(TextUtility.format("%s %d", getResources().getString(R.string.total_care_qty_currnt_month), totalMonthlySale));
    }


    @Override
    public void onComplete(Message msg) {
        if (msg.getData().getString(TaskKey.NAME).equals(TaskKey.COMMUNICATION_TASK)) {
            if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                App.showMessageDisplayDialog(this, getResources().getString(R.string.network_error), R.drawable.error, Color.RED);
            } else {
                ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                if (response.getResponseCode().equalsIgnoreCase("00")) {
                    App.showMessageDisplayDialog(this, response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
                } else {
                    MHealthTask tsk = new MHealthTask(this, Task.HEALTH_CARER_EPORT, R.string.retrieving_data, R.string.please_wait);
                    tsk.setParam(response);
                    tsk.setCompleteListener(this);
                    tsk.execute();
                }
            }

        } else if (msg.getData().getString(TaskKey.NAME).equals(TaskKey.MHEALTH_TASK)) {
            if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                App.showMessageDisplayDialog(this, getResources().getString(R.string.saving_error), R.drawable.error, Color.RED);
            } else {
                showData((ArrayList<HealthCareReportInfo>) msg.getData().getSerializable(TaskKey.DATA0));
            }
        }
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

}
