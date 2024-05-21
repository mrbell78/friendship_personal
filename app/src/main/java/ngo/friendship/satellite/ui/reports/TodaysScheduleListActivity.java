package ngo.friendship.satellite.ui.reports;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

import org.json.JSONArray;
import org.json.JSONException;

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
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.TodaysTimeScheduleLayoutBinding;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.FileOperaion;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.AppToast;
import ngo.friendship.satellite.views.DialogView;

// TODO: Auto-generated Javadoc

/**
 * The Class TodaysScheduleListActivity.
 */
public class TodaysScheduleListActivity extends AppCompatActivity implements OnCompleteListener {


    String activityPath;
    ArrayList<UserScheduleInfo> scheduleList;
    ArrayList<UserScheduleInfo> attendedScheduleList;
    TodaysTimeScheduleLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = TodaysTimeScheduleLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
        });
        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
        });

        setTitle("" +R.string.todays_schedule);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.layoutSync.llAllSync.setOnClickListener(v -> {
            if (SystemUtility.isConnectedToInternet(this)) {
                retrieveSchedules();
            } else {
                SystemUtility.openInternetSettingsActivity(this);
            }
        });

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (App.getContext().getAppSettings() == null)
            App.getContext().readAppSettings(this);

        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this);
        }

        retriveAndShow("");
    }

    private void retriveAndShow(String hhNumber) {

        MHealthTask tsk = new MHealthTask(this, Task.RETRIEVE_TIME_SCHEDULE, R.string.retrieving_data, R.string.please_wait);
        tsk.setParam(hhNumber);
        tsk.setCompleteListener(this);
        tsk.execute();
    }


    private void showData(ArrayList<UserScheduleInfo> sList) {
        scheduleList = sList;
        binding.llScheduleRowContainer.removeAllViews();
        if (scheduleList == null || scheduleList.size() == 0) {
            showOneButtonDialog(getResources().getString(R.string.data_not_available), R.drawable.information, Color.BLACK);
            return;
        }

        for (UserScheduleInfo schedInfo : scheduleList) {

            View view;
            if (schedInfo.getBeneficiaryCode() != null) {
                view = View.inflate(this, R.layout.todays_schedule_benef_row, null);

                if (schedInfo.getSystemChangedDate() > 0) {
                    view.setBackgroundColor(Color.YELLOW);
                }


                ImageView ivBenefImage = view.findViewById(R.id.iv_beneficiary);

                if (FileOperaion.isExist(schedInfo.getBeneficiaryImagePath())) {
                    ivBenefImage.setImageBitmap(FileOperaion.decodeImageFile(schedInfo.getBeneficiaryImagePath(), 50));
                } else {
                    ivBenefImage.setImageResource(R.drawable.benef_dummy);
                }

                TextView tvName = view.findViewById(R.id.tv_beneficiary_name);
                tvName.setText(schedInfo.getBeneficiaryName());

                TextView tvCode = view.findViewById(R.id.tv_beneficiary_code);
                tvCode.setText(schedInfo.getBeneficiaryCode());

                TextView tvSchedTitle = view.findViewById(R.id.tv_sched_title);
                tvSchedTitle.setText(schedInfo.getDescription());

                TextView tvSchedDate = view.findViewById(R.id.tv_sched_date);
                final CheckBox cbAttended = view.findViewById(R.id.cb_benef_schedule);


                String schedate = "";
                if (schedInfo.getAttendedDate() > 0) {
                    schedate = Utility.getDateFromMillisecond(schedInfo.getScheduleDate(), Constants.dd_MMM) + ", " + Utility.getDateFromMillisecond(schedInfo.getAttendedDate(), Constants.dd_MMM);
                    tvSchedDate.setText(schedate);
                    cbAttended.setChecked(true);
                    cbAttended.setEnabled(false);
                } else {

                    schedate = Utility.getDateFromMillisecond(schedInfo.getScheduleDate(), Constants.dd_MMM);
                    tvSchedDate.setText(schedate);
                    cbAttended.setChecked(false);
                    cbAttended.setEnabled(false);

                    if (schedInfo.getInterviewId() > 0) {
                        LinearLayout ll = view.findViewById(R.id.ll_follow_up);

                        ll.setTag(schedInfo);
                        ll.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                UserScheduleInfo scheduleInfo = (UserScheduleInfo) v.getTag();
                                long parentInterviewId = scheduleInfo.getInterviewId();
//								Intent intent = new Intent(TodaysScheduleListActivity.this, PatientFollowupQuestionnaireListActivity.class);
//								intent.putExtra(ActivityDataKey.ACTIVITY_PATH, activityPath+">"+scheduleInfo.getBeneficiaryName());
//								intent.putExtra(ActivityDataKey.PARENT_INTERVIEW_ID, parentInterviewId);
//								intent.putExtra(ActivityDataKey.BENEFICIARY_CODE, App.getContext().getUserInfo().getUserCode()+scheduleInfo.getBeneficiaryCode());
//								intent.putExtra(ActivityDataKey.BENEFICIARY_ID, scheduleInfo.getBeneficiaryId());
//								startActivity(intent);


                            }
                        });
                    }
                }

                cbAttended.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (cbAttended.isChecked()) {
                            cbAttended.setChecked(false);
                        } else {
                            cbAttended.setChecked(true);
                        }

                    }
                });


            } else {
                view = View.inflate(this, R.layout.todays_schedule_benef_row, null);

                view.findViewById(R.id.ll_beneficiary_info_container).setVisibility(View.GONE);
                ImageView ivBenefImage = view.findViewById(R.id.iv_beneficiary);
                ivBenefImage.setImageResource(R.drawable.event);

                TextView tvTitle = view.findViewById(R.id.tv_beneficiary_name);
                tvTitle.setText(schedInfo.getScheduleType());

                TextView tvSchedDate = view.findViewById(R.id.tv_sched_date);


                final CheckBox cbAttended = view.findViewById(R.id.cb_benef_schedule);
                if (schedInfo.getAttendedDate() > 0) {
                    String schedate = Utility.getDateFromMillisecond(schedInfo.getScheduleDate(), Constants.dd_MMM) + ", " + Utility.getDateFromMillisecond(schedInfo.getAttendedDate(), Constants.dd_MMM);
                    tvSchedDate.setText(schedate);


                    cbAttended.setChecked(true);
                    cbAttended.setEnabled(false);

                    cbAttended.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (cbAttended.isChecked()) {
                                cbAttended.setChecked(false);
                            } else {
                                cbAttended.setChecked(true);
                            }

                        }
                    });
                } else {
                    String schedate = Utility.getDateFromMillisecond(schedInfo.getScheduleDate(), Constants.dd_MMM);
                    tvSchedDate.setText(schedate);
                    cbAttended.setChecked(false);
                    cbAttended.setEnabled(true);
                }

            }
            binding.llScheduleRowContainer.addView(view);
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
        exitDialog.show();
    }

    /**
     * Read data from view.
     */
    private void readDataFromView() {
        attendedScheduleList = new ArrayList<UserScheduleInfo>();
        for (int i = 0; i < binding.llScheduleRowContainer.getChildCount(); i++) {
            if (scheduleList.get(i).getBeneficiaryCode() == null || scheduleList.get(i).getBeneficiaryCode().equalsIgnoreCase("")) {
                if (scheduleList.get(i).getAttendedDate() <= 0) {
                    View view = binding.llScheduleRowContainer.getChildAt(i);
                    CheckBox cb = view.findViewById(R.id.cb_benef_schedule);
                    if (cb.isChecked()) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);

                        scheduleList.get(i).setAttendedDate(cal.getTimeInMillis());
                        attendedScheduleList.add(scheduleList.get(i));
                    }
                }
            }
        }
    }


    private void retrieveSchedules() {
        try {

            RequestData request = new RequestData(RequestType.USER_GATE, RequestName.USER_SCHEDULE, Constants.MODULE_DATA_GET);
            HashMap<String, String> refTable = new HashMap<String, String>();
            refTable.put(KEY.VERSION_NO_USER_SCHEDULE, DBTable.USER_SCHEDULE);
            request.setParam1(Utility.getTableRef(refTable, TodaysScheduleListActivity.this));

            CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
            task.setCompleteListener(this);
            task.execute();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
    }


    long needReq = 0;

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
                    retriveAndShow("");
                } else {
                    if (response.getResponseName().equals(RequestName.UPDATE_SCHEDULE)) {
                        showOneButtonDialog(getResources().getString(R.string.data_upload_successfull), R.drawable.information, Color.BLACK);
                        MHealthTask tsk = new MHealthTask(TodaysScheduleListActivity.this, Task.RETRIEVE_TIME_SCHEDULE, R.string.saving_data, R.string.please_wait);
                        tsk.setParam(attendedScheduleList);
                        tsk.setCompleteListener(this);
                        tsk.execute();
                    } else if (response.getResponseName().equals(RequestName.USER_SCHEDULE)) {
                        needReq = JSONParser.getLongNullAllow(response.getParamJson(), KEY.NEED_SAME_REQ);
                        MHealthTask tsk = new MHealthTask(TodaysScheduleListActivity.this, Task.RETRIEVE_TIME_SCHEDULE, R.string.saving_data, R.string.please_wait);
                        tsk.setParam(response);
                        tsk.setCompleteListener(this);
                        tsk.execute();
                    }

                }
            }

        } else if (msg.getData().getString(TaskKey.NAME).equals(TaskKey.MHEALTH_TASK)) {
            if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                App.showMessageDisplayDialog(this, getResources().getString(R.string.saving_error), R.drawable.error, Color.RED);
            } else {
                if (needReq == 1) {
                    needReq = 0;
                    retrieveSchedules();
                } else {
                    showData((ArrayList<UserScheduleInfo>) msg.getData().getSerializable(TaskKey.DATA0));
                }
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menu_search_upload, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Override onQueryTextSubmit method which is call when submit query is searched
            @Override
            public boolean onQueryTextSubmit(String query) {
                // If the list contains the search query than filter the adapter
                // using the filter method with the query as its argument
                if (!query.equalsIgnoreCase("") && query.length() >= 3) {

                    Toast.makeText(TodaysScheduleListActivity.this, "click", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            // This method is overridden to filter the adapter according
            // to a search query when the user is typing search
            @Override
            public boolean onQueryTextChange(String newText) {
//				adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_upload) {
            if (SystemUtility.isConnectedToInternet(this)) {
                readDataFromView();
                if (attendedScheduleList.size() > 0) {
                    try {
                        RequestData request = new RequestData(RequestType.FCM_SCHEDULE, RequestName.UPDATE_SCHEDULE, Constants.MODULE_BUNCH_PUSH);
                        JSONArray jArr = new JSONArray();
                        for (UserScheduleInfo scheduleInfo : attendedScheduleList) {
                            jArr.put(scheduleInfo.toJson());
                        }
                        request.getData().put("schedList", jArr);

                        CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
                        task.setCompleteListener(this);
                        task.execute();
                    } catch (MhealthException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    AppToast.showToast(this, R.string.no_selected_schedule);
                }
            } else {
                SystemUtility.openInternetSettingsActivity(this);
            }

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
