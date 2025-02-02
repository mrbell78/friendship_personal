package ngo.friendship.satellite.ui.reports;

import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.databinding.ActivityReportviewBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.Report;
import ngo.friendship.satellite.utility.Utility;



public class ReportviewActivity extends AppCompatActivity implements OnCompleteListener {

    public class MedicineAdapterModel{
        long MEDICINE_ID;
        String BRAND_NAME;

        public MedicineAdapterModel(long MEDICINE_ID, String BRAND_NAME) {
            this.MEDICINE_ID = MEDICINE_ID;
            this.BRAND_NAME = BRAND_NAME;
        }
    }
    ActivityReportviewBinding binding;

    final Calendar limitedDate = Calendar.getInstance();
    final Calendar CALENDAR_FROM = Calendar.getInstance();
    private Calendar CALENDAR_TO = Calendar.getInstance();
    String fromDateString = "";
    String toDateString = "";
    SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd-MMM-yyyy");
    private Report currentReport;
    private Stack stack = new Stack();
    long minDate = 0;
    ArrayList<MedicineInfo> medicineList = new ArrayList<>();
    ArrayList<MedicineAdapterModel> adapterModels = new ArrayList<>();
    ArrayList<String> medicineNameList = new ArrayList<>();
    long singleMedId=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        try {
            long reportSelectionDateLimit = 0;
            reportSelectionDateLimit = Utility.parseLong(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "Data_Limit", "report.selection.date.limit"));
            limitedDate.add(Calendar.DATE, (int) reportSelectionDateLimit * -1);
            minDate = limitedDate.getTimeInMillis();
        } catch (Exception e) {
            // TODO: handle exception
        }
        retriveMedicine();

        try {
            if (getIntent().getExtras().containsKey("REPORT_DATA_PASS")) {
                String reportDataPass = getIntent().getExtras().getString("REPORT_DATA_PASS");

                Gson data = new Gson();
                Report currentReports = data.fromJson(reportDataPass, Report.class);
                currentReport = App.getContext().getDB().getSingleReportAssts(currentReports.getRepId());
                if(currentReports.getRepName().equalsIgnoreCase("MEDICINE_TRACKING_REPORT")){
                   // binding.cvHeader.setVisibility(GONE);
                    binding.spinner.setVisibility(View.VISIBLE);
                }
                openReport(currentReport);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        Report singleReport = reports.get(singleReport.get);
//        singleReport.setCalendarFromAndTo(CALENDAR_FROM, CALENDAR_TO);
//        singleReport.resetInputPram();


        binding.btnSearch.setOnClickListener(v -> {
            currentReport.setData(null);
            currentReport.setData(null);
            openReport(currentReport);
        });
        binding.etFromDate.setText(DD_MMM_YYYY.format(CALENDAR_FROM.getTime()));
        binding.etToDate.setText(DD_MMM_YYYY.format(CALENDAR_TO.getTime()));
        binding.etFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                long afterTwoMonthsinMilli = cal.getTimeInMillis();
                // Log.d("Time", "onCreate: ...from date "+afterTwoMonthsinMilli);

                DatePickerDialog dpd = new DatePickerDialog(ReportviewActivity.this,
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
                DatePickerDialog dpd = new DatePickerDialog(ReportviewActivity.this,
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

        //showReport(singleReport);
    }

    private void retriveMedicine() {
        MHealthTask tsk = new MHealthTask(this, Task.RETRIEVE_CURRENT_STOCK_MEDICINE_LIST, R.string.retrieving_data, R.string.please_wait);
        tsk.setCompleteListener(msg -> {
            if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                App.showMessageDisplayDialog(ReportviewActivity.this, getResources().getString(R.string.retrive_error), R.drawable.error, Color.RED);
            } else {
                medicineList = (ArrayList<MedicineInfo>) msg.getData().getSerializable(TaskKey.DATA0);
                for (MedicineInfo medicineInfo:medicineList){
                    medicineInfo.setSelected(false);
                    adapterModels.add(new MedicineAdapterModel(medicineInfo.getMedId(),medicineInfo.getBrandName()));
                    medicineNameList.add(medicineInfo.getBrandName());

                }
                showSpiner(adapterModels,medicineNameList);
            }
        });
        tsk.execute();
    }
    private void showSpiner(ArrayList<MedicineAdapterModel> medicineList,ArrayList<String> medicineNameList) {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,medicineNameList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                Log.d("spinerItem", "onItemSelected: .....selected item is "+selectedItem);



                for(MedicineAdapterModel adpInfo:medicineList){
                    if(adpInfo.BRAND_NAME.trim().equals(selectedItem.trim())){
                        Log.d("medicineId", "onItemSelected: ...the id is "+adpInfo.MEDICINE_ID);
                        singleMedId =adpInfo.MEDICINE_ID;
                    }
                }

               // singleMedName =selectedItem;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        binding.btnSearchEqt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                currentReport.setData(null);
//                currentReport.setData(null);
//                openReport(currentReport);
//            }
//        });

//        binding.btn_search_eqt.setOnClickListener(v -> {
//            currentReport.setData(null);
//            currentReport.setData(null);
//            openReport(currentReport);
//        });
    }
    private void showReport(final Report report) {
        if (report.getData() != null && report.getRepParams() != null) {
            Log.e("REPORT DATA", report.getData().toString());
            try {
                showReportHeader(report);
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            binding.reportContainer.removeAllViews();
            if (report.getData().length() > 0) {
                binding.llDataNotFound.serviceNotFound.setVisibility(GONE);
                for (int pos = 0; pos < report.getData().length(); pos++) {
                    try {
                        JSONObject data = report.getData().getJSONObject(pos);
                        String repIdKey = null;
                        if (data.has("REP_ID_KEY_NAME"))
                            repIdKey = data.getString("REP_ID_KEY_NAME");
                        TableRow tr = new TableRow(this);
                        if (repIdKey != null && repIdKey.contains("##")) {
                            tr.setTag(repIdKey);
                            tr.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String repId = ((String) v.getTag()).split("##")[0];
                                    String repKey = ((String) v.getTag()).split("##")[1];
                                    String repName = ((String) v.getTag()).split("##")[2];
                                    ArrayList<Report> reports = App.getContext().getDB().getReportAssts(Utility.parseLong(repId), null);
                                    if (reports != null && reports.size() == 1) {
                                        stack.push(currentReport);
                                        Report rep = reports.get(0);
                                        rep.setRepCaption(repName);
                                        rep.setCalendarFromAndTo(report.getCalendarFrom(), report.getCalendarTo());
                                        rep.setMEDICINE_ID(singleMedId);
                                        rep.resetInputPram();
                                        rep.getInputParams().put("REP_KEY", repKey);
                                        openReport(rep);
                                    }
                                }
                            });
                        }
                        for (int index = 0; index < report.getRepParams().length(); index++) {
                            TextView tv = new TextView(this);
                            if (index == 0 && repIdKey != null && repIdKey.contains("##")) {
                                tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                tv.setTextColor(Color.BLUE);
                            }
                            JSONObject column = report.getRepParams().getJSONObject(index);
                            String key = column.getString(KEY.KEY);
                            int weight = Utility.parseInt(column.getString(KEY.WEIGHT));

                            tv.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight * 1.0f));
                            tv.setBackgroundResource(R.drawable.cell_shape);
                            tv.setPadding(5, 5, 5, 5);
                            tv.setText(data.get(key) + "");
                            tr.addView(tv);
                        }
                        binding.reportContainer.addView(tr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else {
                binding.llDataNotFound.serviceNotFound.setVisibility(View.VISIBLE);
            }
        }
    }
    private void showReportHeader(Report report) throws JSONException {
        binding.reportHeader.removeAllViews();
        setTitle("" + report.getRepCaption());
        TableRow tr = new TableRow(this);
        //	tr.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

        for (int index = 0; index < report.getRepParams().length(); index++) {
            TextView tv = new TextView(this);
            JSONObject column = report.getRepParams().getJSONObject(index);
            String title = column.getString(KEY.TITLE);
            int weight = Utility.parseInt(column.getString(KEY.WEIGHT));

            tv.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight * 1.0f));
            tv.setBackgroundResource(R.drawable.cell_shape_header);
            tv.setPadding(5, 5, 5, 5);
            tv.setText(title);
            tr.addView(tv);
        }
        binding.reportHeader.addView(tr);
    }
    public void openReport(Report report) {
        report.setCalendarFromAndTo(CALENDAR_FROM, CALENDAR_TO);
       report.setMEDICINE_ID(singleMedId);
        report.setUser_id(App.getContext().getUserInfo().getUserId());
        report.resetInputPram();
        MHealthTask tsk = new MHealthTask(this, Task.RETRIEVE_REPORT, R.string.retrieving_data, R.string.please_wait);
        tsk.setParam(report);
        tsk.setCompleteListener(this);
        tsk.execute();
    }
    @Override
    public void onComplete(Message msg) {
        if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
            String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
            App.showMessageDisplayDialog(this, getResources().getString(R.string.retrive_error), R.drawable.error, Color.RED);
        } else {
            Report currentReport = (Report) msg.getData().getSerializable(TaskKey.DATA0);
            showReport(currentReport);
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
            if (stack.isEmpty()) {
                onBackPressed();
            } else {
                openReport((Report) stack.pop());
            }
            //
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void calDate() {

    }
}