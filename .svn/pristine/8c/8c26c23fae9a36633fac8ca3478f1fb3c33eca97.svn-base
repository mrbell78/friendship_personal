package ngo.friendship.satellite.ui.reports;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.Last30DaysRcvSaleLayoutBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.MedicineRcvSaleInfo;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.DialogView;


/**
 * <p> Display Paramedic medicines receive/sales report</p>
 * <b>Create Date : 1st Jan 2024</b><br/>
 *
 * @author md Yeasin Ali
 */
public class RcvSalesReportActivity extends AppCompatActivity implements OnCompleteListener {

    Last30DaysRcvSaleLayoutBinding binding;
    String fromDateString = "";
    String toDateString = "";
    SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd-MMM-yyyy");
    SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    String activityPath;
    long minDate = 0;
    final Calendar limitedDate = Calendar.getInstance();
    final Calendar CALENDAR_FROM = Calendar.getInstance();
    private Calendar CALENDAR_TO = Calendar.getInstance();
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = Last30DaysRcvSaleLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null) {
            activityPath = getIntent().getExtras().getString(ActivityDataKey.ACTIVITY_PATH);
            setTitle("" + activityPath);
        }
        ctx = this;

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        try {
            long reportSelectionDateLimit = 0;
            reportSelectionDateLimit = Utility.parseLong(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "Data_Limit", "report.selection.date.limit"));
            limitedDate.add(Calendar.DATE, (int) reportSelectionDateLimit * -1);
            minDate = limitedDate.getTimeInMillis();
        } catch (Exception e) {
        }
        String startDate = DD_MMM_YYYY.format(CALENDAR_FROM.getTime());
        String endDate = DD_MMM_YYYY.format(CALENDAR_TO.getTime());
        binding.etFromDate.setText(startDate);
        binding.etToDate.setText(endDate);
        binding.etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                long afterTwoMonthsinMilli = cal.getTimeInMillis();
                // Log.d("Time", "onCreate: ...from date "+afterTwoMonthsinMilli);

                DatePickerDialog dpd = new DatePickerDialog(RcvSalesReportActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                CALENDAR_FROM.set(year, monthOfYear, dayOfMonth, 0,
                                        0);
                                Log.e("DATETILL : ", "" + CALENDAR_FROM.getTime());
                                fromDateString = DD_MMM_YYYY.format(CALENDAR_FROM
                                        .getTime());
                                binding.etFromDate.setText("" + fromDateString);

                                long afterTwoMonthsinMilli = CALENDAR_FROM.getTimeInMillis();
                                Log.d("Time", "onCreate: ...from date " + afterTwoMonthsinMilli);

                            }
                        }, CALENDAR_FROM.get(Calendar.YEAR), CALENDAR_FROM
                        .get(Calendar.MONTH), CALENDAR_FROM
                        .get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                dpd.getDatePicker().setMinDate(minDate);
                dpd.show();

            }
        });
        binding.etToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(RcvSalesReportActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                Log.e("DATETILL : ", dayOfMonth + " "
                                        + monthOfYear + " " + year);
                                CALENDAR_TO.set(year, monthOfYear, dayOfMonth, 0, 0);
                                Log.e("DATETILL : ", "" + CALENDAR_TO.getTime());

                                toDateString = DD_MMM_YYYY.format(CALENDAR_TO.getTime());
                                binding.etToDate.setText("" + toDateString);
                                long afterTwoMonthsinMilli = CALENDAR_TO.getTimeInMillis();
                                Log.d("Time", "onCreate: ...from date " + afterTwoMonthsinMilli);
                            }
                        }, CALENDAR_TO.get(Calendar.YEAR), CALENDAR_TO
                        .get(Calendar.MONTH), CALENDAR_TO
                        .get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                dpd.getDatePicker().setMinDate(minDate);
                dpd.show();

            }
        });

        if (SystemUtility.isConnectedToInternet(this)) {

            RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.LAST_30_DAYS_RECEIVE_SALES, Constants.MODULE_DATA_GET);
            CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
            task.setCompleteListener(this);
            binding.btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String startDate = YYYY_MM_DD.format(CALENDAR_FROM.getTime());
                        String endDate = YYYY_MM_DD.format(CALENDAR_TO.getTime());
                        JSONObject paramData = new JSONObject();
                        paramData.put("FROM_DATE", "" + startDate);
                        paramData.put("TO_DATE", "" + endDate);
                        request.setParam1(paramData);
                        task.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            SystemUtility.openInternetSettingsActivity(this);
        }

//        MHealthTask tsk = new MHealthTask(this, Task.LAST_30_DAYS_RECEIVE_SALES, R.string.retrieving_data, R.string.please_wait);
//        tsk.setCompleteListener(this);
//        tsk.execute();
        binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
        binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
//        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
//            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
//            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
//        });
//        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
//            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
//            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
//        });

        binding.layoutSync.llAllSync.setOnClickListener(v -> {
            if (SystemUtility.isConnectedToInternet(this)) {

                RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.LAST_30_DAYS_RECEIVE_SALES, Constants.MODULE_DATA_GET);
                CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
                task.setCompleteListener(this);
                task.execute();
            } else {
                SystemUtility.openInternetSettingsActivity(this);
            }
        });

    }


    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (App.getContext().getAppSettings() == null)
            App.getContext().readAppSettings(this);

//        Utility.setActivityPath(this, R.string.last_30_days_receive_and_sales);
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
     * @param last30DaysRcvSalesList the last30 days rcv sales list
     */
    private void showData(ArrayList<MedicineRcvSaleInfo> last30DaysRcvSalesList) {
        binding.llReportRowContainer.removeAllViews();
        if (last30DaysRcvSalesList == null || last30DaysRcvSalesList.size() == 0) {
           // showOneButtonDialog(getResources().getString(R.string.data_not_available), R.drawable.information, Color.BLACK);
            binding.dataNotFound.serviceNotFound.setVisibility(View.VISIBLE);
            return;
        }else{
            binding.dataNotFound.serviceNotFound.setVisibility(View.GONE);
        }

        double totalMonthlySale = 0;
        double totalMonthlyRecive = 0;
        for (MedicineRcvSaleInfo salesInfo : last30DaysRcvSalesList) {
            View v = View.inflate(this, R.layout.last_30_days_rcv_sale_row, null);
            binding.llReportRowContainer.addView(v);

            TextView tvMedicineName = v.findViewById(R.id.tv_medicine_name);
            tvMedicineName.setText(salesInfo.getMedicineName());

            TextView tvMedicineRcv = v.findViewById(R.id.tv_rcv_medicine_quantity);
            tvMedicineRcv.setText(salesInfo.getReceiveQuantity());

            TextView tvMedicineSale = v.findViewById(R.id.tv_sale_medicine_quantity);
            tvMedicineSale.setText(salesInfo.getSaleQuantity());

            TextView tvMedicineSalePrice = v.findViewById(R.id.tv_medicine_price);
            tvMedicineSalePrice.setText(salesInfo.getTotalSalePrice());
            tvMedicineSalePrice.setVisibility(View.GONE);
            totalMonthlySale += Utility.parseDouble(salesInfo.getSaleQuantity());
            totalMonthlyRecive += Utility.parseDouble(salesInfo.getReceiveQuantity());



        }
        TextView tvTotalPrice = findViewById(R.id.tv_total_medicine_price);
        TextView tvTotalRecive = findViewById(R.id.tv_total_medicine_recive);
        tvTotalPrice.setText(TextUtility.format("%s %.1f", getResources().getString(R.string.total_sale), totalMonthlySale));
        tvTotalRecive.setText(TextUtility.format("%s %.1f", getResources().getString(R.string.total_recive), totalMonthlyRecive));
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
                    MHealthTask tsk = new MHealthTask(this, Task.LAST_30_DAYS_RECEIVE_SALES, R.string.retrieving_data, R.string.please_wait);
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
                showData((ArrayList<MedicineRcvSaleInfo>) msg.getData().getSerializable(TaskKey.DATA0));
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
