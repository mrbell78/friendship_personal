package ngo.friendship.satellite.ui.reports;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.adapter.BasemHealthAdapter;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.DownloadFileTask;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.ActivityMyReportBinding;
import ngo.friendship.satellite.databinding.ReportCategoryItemRowBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.interfaces.OnDownloadFileCompleteListener;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppVersionHistory;
import ngo.friendship.satellite.model.Report;
import ngo.friendship.satellite.service.Alarm;
import ngo.friendship.satellite.utility.ItemDecorationAlbumColums;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.DialogView;
public class ReportListActivity extends BaseActivity implements OnCompleteListener {
    ActivityMyReportBinding binding;
    private ArrayList<Report> reportList = new ArrayList<>();
    private Report currentReport;
    Report singleReferelCenter;
    BasemHealthAdapter<Report, ReportCategoryItemRowBinding> mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(getResources().getString(R.string.all_report));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
            binding.layoutSync.llPenddingSync.setVisibility(View.GONE);
            binding.layoutSync.llAllSync.setVisibility(View.GONE);
            binding.layoutSync.viewLine.setVisibility(View.GONE);
            binding.layoutSync.viewLine2.setVisibility(View.GONE);
        });
        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
        });
        binding.layoutSync.llGetData.setOnClickListener(v -> {
            showConfirmationDialog(getResources().getString(R.string.retrieve_data_confirmation_previous_data), R.drawable.information);
        });

        singleReferelCenter = new Report();
        currentReport = new Report();
        currentReport.setRepId(0);
        currentReport.setType(Report.MENU);
        try {
            reportList = App.getContext().getDB().getReportAssts(null, currentReport.getRepId());
        }catch (Exception e){
            e.printStackTrace();
        }

        getItemAdapter();
//        try {
//            singleReferelCenter = reportList.get(0);
//
//            binding.tvSingleReferralReport.setText(singleReferelCenter.getRepCaption());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        binding.llTodaysSale.setOnClickListener(v -> {
            openActivity(TodaysMedicineSalesReportActivity.class, getResources().getString(R.string.today_sale));
        });

//        binding.llLast30DaysSale.setOnClickListener(v -> {
//            openActivity(Last30DaysSalesReportActivity.class, getResources().getString(R.string.last_30_days_sale));
//        });
        binding.llLast30DaysReceiveSale.setOnClickListener(v -> {
            openActivity(RcvSalesReportActivity.class, getResources().getString(R.string.last_30_days_receive_and_sales));
        });
        binding.llBeneficiaryHealthCareReport.setOnClickListener(v -> {
            openActivity(HealthCareReportActivity.class, getResources().getString(R.string.beneficiary_health_care_report));
        });
        binding.llBeneficiaryRegistrationReport.setOnClickListener(v -> {
            openActivity(BeneficiaryRegistrationReportActivity.class, getResources().getString(R.string.beneficiary_registration_report));
        });

        binding.llPatentRegisterReport.setOnClickListener(v -> {
            openActivity(PatientRegisterReportActivity.class, getResources().getString(R.string.patient_register));
        });
//        binding.llReferralCenter.setOnClickListener(v -> {
//            Gson gson = new Gson();
//            Intent intent = new Intent(ReportListActivity.this, ReportviewActivity.class);
//            intent.putExtra("REPORT_DATA_PASS", gson.toJson(singleReferelCenter).toString());
//            startActivity(intent);
//            // openActivity(UpcomingTimeScheduleActivity.class, getResources().getString(R.string.upcoming_schedule));
//            // openActivity(TodaysMedicineSalesReportActivity.class, getResources().getString(R.string.today_sale));
//        });
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
        } else if (id == R.id.action_mobile) {
            showConfirmationDialog(getResources().getString(R.string.retrieve_data_confirmation_previous_data), R.drawable.information);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConfirmationDialog(String msg, int imageId) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_yes);
        buttonMap.put(2, R.string.btn_no);
        DialogView dialog = new DialogView(this, R.string.dialog_title, msg, R.drawable.warning, buttonMap);
        dialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {
                switch (view.getId()) {
                    case 1:
                        getMyData();
                        break;
                }

            }
        });
        dialog.show();
    }

    private void getMyData() {
        ReportListActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        App.getContext().loadApplicationData(ReportListActivity.this);
        RequestData request = new RequestData(RequestType.USER_GATE, RequestName.MY_DATA_WITH_SERVICE, Constants.MODULE_DATA_GET);
        request.setParam1(Utility.getTableRef(null, ReportListActivity.this));
        CommiunicationTask commiunicationTask = new CommiunicationTask(ReportListActivity.this, request, R.string.retrieving_data, R.string.please_wait);
        commiunicationTask.setCompleteListener(ReportListActivity.this);
        commiunicationTask.execute();
    }


    private void getItemAdapter() {
        mAdapter = new BasemHealthAdapter<>() {
            @Override
            public void onItemLongClick(@NonNull View view, Report model, int position) {
                Toast.makeText(ReportListActivity.this, "Click-Data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(@NonNull View view, Report report, int position, ReportCategoryItemRowBinding dataBinding) {


            }

            @Override
            public void onBindData(Report model, int position, ReportCategoryItemRowBinding dataBinding) {
                dataBinding.tvReportName.setText("" + model.getRepCaption());
                dataBinding.llReporItem.setOnClickListener(v -> {
                    Gson gson = new Gson();
                    Intent intent = new Intent(ReportListActivity.this, ReportviewActivity.class);
                    intent.putExtra("REPORT_DATA_PASS", gson.toJson(model).toString());
                    startActivity(intent);
                });

            }

            @Override
            public int getLayoutResId() {
                return R.layout.report_category_item_row;
            }
        };

        mAdapter.addItems(reportList);
        binding.rvReports.setHasFixedSize(true);
        binding.rvReports.setItemAnimator(new DefaultItemAnimator());
        binding.rvReports.setAdapter(mAdapter);
        binding.rvReports.setNestedScrollingEnabled(false);
        binding.rvReports.addItemDecoration(new ItemDecorationAlbumColums(ReportListActivity.this, LinearLayoutManager.HORIZONTAL));
        binding.rvReports.addItemDecoration(new ItemDecorationAlbumColums(ReportListActivity.this, LinearLayoutManager.VERTICAL));

    }

    private void openActivity(Class<?> cls, String activityPath) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(ActivityDataKey.ACTIVITY_PATH, activityPath);
        startActivity(intent);
    }

    @Override
    public void onComplete(Message msg) {
        if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
            String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
            App.showMessageDisplayDialog(this, getResources().getString(R.string.network_error), R.drawable.error, Color.RED);
        } else {
            final ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);

            if (response.getResponseCode().equalsIgnoreCase("00")) {
                App.showMessageDisplayDialog(this, response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
            } else {
                MHealthTask tsk = new MHealthTask(ReportListActivity.this, Task.MYDATA, R.string.saving_data, R.string.please_wait);
                tsk.setParam(response);
                tsk.setCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(Message msg) {

                        // showCompleteDownloadDataDilog();
                        if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                            String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                            App.showMessageDisplayDialog(ReportListActivity.this, getResources().getString(R.string.saving_error), R.drawable.error, Color.RED);
                        } else {
                            if (JSONParser.getLongNullAllow(response.getParamJson(), KEY.NEED_SAME_REQ) == 1) {
                                getMyData();
                            } else {
                                App.showMessageDisplayDialog(ReportListActivity.this, getResources().getString(R.string.retrieve_successfull), R.drawable.information, Color.BLACK);
                                Alarm.startAutosyncService(ReportListActivity.this);
                                Alarm.startNotificationService(ReportListActivity.this);
                            }
                        }
                    }
                });
                tsk.execute();
            }
        }

    }

    private void showInstallConfirmationPrompt(final AppVersionHistory appVersionHistory) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_yes);
        buttonMap.put(2, R.string.btn_no);
        DialogView exitDialog = new DialogView(this, R.string.dialog_title, appVersionHistory.getUpdateDesc(), R.drawable.information, buttonMap);
        exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {
                switch (view.getId()) {
                    case 1:
                        if (appVersionHistory.getInstallFlag() != null && appVersionHistory.getInstallFlag().equals(AppVersionHistory.FLAG_OPENED) && appVersionHistory.getAppPathLocal() != null && new File(appVersionHistory.getAppPathLocal()).exists()) {
//						File apk = new File(appVersionHistory.getAppPathLocal());
//						Intent i = new Intent();
//						i.setAction(Intent.ACTION_VIEW);
//						i.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
//						startActivity(i);
//						LoginPinActivity.this.finish();
                            File apk = new File(appVersionHistory.getAppPathLocal());
                            Intent i = new Intent();
                            i.setAction(Intent.ACTION_VIEW);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Uri apkURI = FileProvider.getUriForFile(
                                        getApplicationContext(),
                                        getApplicationContext().getPackageName() + ".provider", apk);
                                i = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                i.setData(apkURI);
                                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                // i.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");

                            } else {
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                Uri apkURI = FileProvider.getUriForFile(
                                        getApplicationContext(),
                                        getApplicationContext().getPackageName() + ".provider", apk);
                                i.setDataAndType(apkURI, "application/vnd.android.package-archive");
                                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            }
                            startActivity(i);
                            ReportListActivity.this.finish();
                        } else {
                            if (SystemUtility.isConnectedToInternet(ReportListActivity.this)) {

                                //String outpath = App.getContext().getCommonDir(LoginPinActivity.this);

                                String outpath = App.getContext().getApkDir(ReportListActivity.this);
                                try {
                                    File apk = new File(appVersionHistory.getAppPathLocal());
                                    if (apk.exists()) {
                                        apk.delete();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                DownloadFileTask downloadFileTask = new DownloadFileTask(ReportListActivity.this, appVersionHistory.getAppPath(), outpath);
                                downloadFileTask.setOnDownloadFileCompleteListener(new OnDownloadFileCompleteListener() {
                                    @Override
                                    public void OnDownloadFileCompleteListener(String filePath) {
//							         if (filePath==null ) return ;
//									appVersionHistory.setInstallFlag(AppVersionHistory.FLAG_OPENED);
//									appVersionHistory.setAppPathLocal(filePath);
//									appVersionHistory.setOpenDate(Calendar.getInstance().getTimeInMillis());
//									App.getContext().getDB().updateAppVersionHistory(appVersionHistory);
//
//									File apk = new File(filePath);
//									Intent i = new Intent();
//									i.setAction(Intent.ACTION_VIEW);
//									i.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
//									startActivity(i);
//									LoginPinActivity.this.finish();
                                        if (filePath == null) return;
                                        appVersionHistory.setInstallFlag(AppVersionHistory.FLAG_OPENED);
                                        appVersionHistory.setAppPathLocal(filePath);
                                        appVersionHistory.setOpenDate(Calendar.getInstance().getTimeInMillis());
                                        App.getContext().getDB().updateAppVersionHistory(appVersionHistory);

                                        File apk = new File(filePath);
                                        Intent i = new Intent();
                                        i.setAction(Intent.ACTION_VIEW);
                                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                                            i.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
                                        } else {
                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            Uri apkURI = FileProvider.getUriForFile(
                                                    getApplicationContext(),
                                                    getApplicationContext().getPackageName() + ".provider", apk);
                                            i.setDataAndType(apkURI, "application/vnd.android.package-archive");
                                            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        }

                                        startActivity(i);

                                        // for next release remove database because of interview table has new data
                                        //added on 6th May,2021
//									removeLocalDatabase();
//									App.loadApplicationData(LoginPinActivity.this);
                                        // end

                                        ReportListActivity.this.finish();
                                    }
                                });
                                downloadFileTask.execute();
                            } else {
                                SystemUtility.openInternetSettingsActivity(ReportListActivity.this);
                            }
                        }
                        break;
                }
            }
        });
        exitDialog.showWebView();
    }
}