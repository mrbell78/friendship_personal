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
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import androidx.appcompat.widget.SearchView;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.adapter.PatientRegisterListAdapter;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.PatientRegisterReportLayoutBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.DialogView;


/**
 * <p>Display Patient registration report</p>
 *
 * @author Md.Yesain Ali
 */
public class PatientRegisterReportActivity extends BaseActivity implements OnClickListener, OnCompleteListener {

    PatientRegisterReportLayoutBinding binding;
    final Calendar limitedDate = Calendar.getInstance();
    final Calendar CALENDAR_FROM = Calendar.getInstance();
    private Calendar CALENDAR_TO = Calendar.getInstance();
    String fromDateString = "";
    String toDateString = "";
    SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd-MMM-yyyy");
    SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    String activityPath;
    long minDate = 0;
    PatientRegisterListAdapter adapter;
    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = PatientRegisterReportLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ctx = this;
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
        JSONArray reportList = new JSONArray();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvReportRegisterList.setHasFixedSize(false);
        binding.rvReportRegisterList.setLayoutManager(linearLayoutManager);
        adapter = new PatientRegisterListAdapter(PatientRegisterReportActivity.this, reportList);
        binding.rvReportRegisterList.setAdapter(adapter);


        binding.etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                long afterTwoMonthsinMilli = cal.getTimeInMillis();
                // Log.d("Time", "onCreate: ...from date "+afterTwoMonthsinMilli);

                DatePickerDialog dpd = new DatePickerDialog(PatientRegisterReportActivity.this,
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
                DatePickerDialog dpd = new DatePickerDialog(PatientRegisterReportActivity.this,
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
        if (getIntent().getExtras() != null) {
            activityPath = getIntent().getExtras().getString(ActivityDataKey.ACTIVITY_PATH);
            setTitle("" + activityPath);
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.PATIENT_REGISTER, Constants.MODULE_DATA_GET);
        CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);

        task.setCompleteListener(this);
        try {
             startDate = YYYY_MM_DD.format(CALENDAR_FROM.getTime());
             endDate = YYYY_MM_DD.format(CALENDAR_TO.getTime());
            JSONObject paramData = new JSONObject();
            paramData.put("FROM_DATE", "" + startDate);
            paramData.put("TO_DATE", "" + endDate);
            request.setParam1(paramData);
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (SystemUtility.isConnectedToInternet(this)) {

            binding.btnSearch.setOnClickListener(new OnClickListener() {
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


//		binding.layoutSync.llAllSync.setOnClickListener(v -> {
//			if (SystemUtility.isConnectedToInternet(this)) {
//				RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.INTERVIEW_STATS, Constants.MODULE_DATA_GET);
//				CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
//				task.setCompleteListener(this);
//				task.execute();
//			} else {
//				SystemUtility.openInternetSettingsActivity(this);
//			}
//		});


    }


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LanguageContextWrapper.wrap(context, AppPreference.getString(context, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)));
    }

    /**
     * Display alert dialog with one button.
     *
     * @param msg          will be displayed in the message section of the dialog
     * @param imageId      is the image drawable id which will be displayed at the dialog's title
     * @param messageColor is the color id of the dialog's message
     * @author Kayum Hossan
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                this.finish();
                break;
            default:
                break;
        }

    }

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
                    JSONArray reportList = new JSONArray();
                    try {
                        JSONObject reqData = new JSONObject(response.getData());
                        reportList = JSONParser.getJsonArray(reqData, "REPORT");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (reportList == null || reportList.length() == 0) {
                        binding.dataNotFound.serviceNotFound.setVisibility(View.VISIBLE);

                        return;
                    } else {
                        binding.dataNotFound.serviceNotFound.setVisibility(View.GONE);
                    }
                    String selectedDate = "";
                    for (int i = 0; i < reportList.length(); i++) {
                        try {
                            JSONObject singObj = reportList.getJSONObject(i);

                            if (!selectedDate.equals(JSONParser.getString(singObj, "SERVICE_DATE"))) {
                                selectedDate = JSONParser.getString(singObj, "SERVICE_DATE");
                                singObj.put("IS_NEW_DATE", "true");
                            } else {
                                singObj.put("IS_NEW_DATE", "false");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    adapter = new PatientRegisterListAdapter(PatientRegisterReportActivity.this, reportList);
                    binding.rvReportRegisterList.setAdapter(adapter);
                }
            }

        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(ngo.friendship.satellite.R.menu.common_menu_search, menu);
        MenuItem item = menu.findItem(ngo.friendship.satellite.R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);

                return true;
            }
        });

        return true;
    }
}
