package ngo.friendship.satellite.ui.reports;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.Last30DaysSalesLayoutBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.IndividualSalesInfo;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.views.DialogView;


/**
 * <p> Display FCM's last 30 days medicines sales report</p>
 * <b>Create Date : 27th May 2014</b><br/>
 * <b>Last Update : 16th February 2015</b><br/><br/>
 *
 * @author Kayum Hossan
 * @author Mohammed Jubayer
 */
public class Last30DaysSalesReportActivity extends AppCompatActivity implements OnCompleteListener {


    ProgressDialog dlog;
    String activityPath;
    Last30DaysSalesLayoutBinding binding;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = Last30DaysSalesLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
//            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
//            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
//        });
//        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
//            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
//            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
//        });
        binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
        binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
//        if (getIntent().getExtras() != null) {
//            activityPath = getIntent().getExtras().getString(ActivityDataKey.ACTIVITY_PATH);
//
//        }
        setTitle("" + getResources().getString(R.string.last_30_days_sale));


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.layoutSync.llAllSync.setOnClickListener(v -> {
            if (SystemUtility.isConnectedToInternet(this)) {
                RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.LAST_30_DAYS_SALES_SUMMARY, Constants.MODULE_DATA_GET);
                CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
                task.setCompleteListener(this);
                task.execute();
            } else {
                SystemUtility.openInternetSettingsActivity(this);
            }

        });

        if (SystemUtility.isConnectedToInternet(this)) {
            RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.LAST_30_DAYS_SALES_SUMMARY, Constants.MODULE_DATA_GET);
            CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
            task.setCompleteListener(this);
            task.execute();
        } else {
            SystemUtility.openInternetSettingsActivity(this);
        }
//        MHealthTask tsk = new MHealthTask(this, Task.LAST_30DAYS_SALES_LIST, R.string.retrieving_data, R.string.please_wait);
//        tsk.setCompleteListener(this);
//        tsk.execute();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (App.getContext().getAppSettings() == null)
            App.getContext().readAppSettings(this);

//        Utility.setActivityPath(this, R.string.last_30_days_sale);
        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this);
        }
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
     * @param last30DaysSalesList The date wise sales list
     */
    private void showData(ArrayList<IndividualSalesInfo> last30DaysSalesList) {
        binding.llReportRowContainer.removeAllViews();
        if (last30DaysSalesList == null || last30DaysSalesList.size() == 0) {
           // showOneButtonDialog(getResources().getString(R.string.data_not_available), R.drawable.information, Color.BLACK);
            binding.dataNotFound.serviceNotFound.setVisibility(View.VISIBLE);
            return;
        }else {
            binding.dataNotFound.serviceNotFound.setVisibility(View.GONE);
        }

        double totalMonthlySale = 0;
        for (IndividualSalesInfo salesInfo : last30DaysSalesList) {
            View v = View.inflate(this, R.layout.last_30_days_sales_row, null);
            binding.llReportRowContainer.addView(v);

            TextView tvDate = v.findViewById(R.id.tv_date);
            tvDate.setText(salesInfo.getDate());

            TextView tvAmount = v.findViewById(R.id.tv_total_price);
            tvAmount.setText(TextUtility.format("%.2f", salesInfo.getSaleAmount()));
            totalMonthlySale += salesInfo.getSaleAmount();

        }
        TextView tvTotalPrice = findViewById(R.id.tv_total_medicine_price);
        tvTotalPrice.setText(TextUtility.format("%s %.1f", getResources().getString(R.string.total_sale), totalMonthlySale));
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
                    MHealthTask tsk = new MHealthTask(this, Task.LAST_30DAYS_SALES_LIST, R.string.retrieving_data, R.string.please_wait);
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
                showData((ArrayList<IndividualSalesInfo>) msg.getData().getSerializable(TaskKey.DATA0));
            }
        }
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
