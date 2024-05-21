package ngo.friendship.satellite.ui.reports;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;

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
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.ScheduleLayoutBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.FileOperaion;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.views.DialogView;

// TODO: Auto-generated Javadoc

/**
 * The Class TomorrowsTimeScheduleActivity.
 */
public class TomorrowsTimeScheduleActivity extends AppCompatActivity implements OnCompleteListener {


    /**
     * The activity path.
     */
    String activityPath;

    /**
     * The schedule list.
     */
    ArrayList<UserScheduleInfo> scheduleList;
    ScheduleLayoutBinding binding;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = ScheduleLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(getResources().getString(R.string.tomorrow_time_schedule));

        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
        });
        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
        });

        if (getIntent().getExtras() != null) {
            activityPath = getIntent().getExtras().getString(ActivityDataKey.ACTIVITY_PATH);
            setTitle("" + activityPath);
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.layoutSync.llAllSync.setOnClickListener(v -> {
            if (SystemUtility.isConnectedToInternet(this)) {
                RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.UNDONE_SCHEDULE, Constants.MODULE_DATA_GET);
                CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
                task.setCompleteListener(this);
                task.execute();

            } else {
                SystemUtility.openInternetSettingsActivity(this);
            }
        });
        retriveAndShow("");

    }


    private void retriveAndShow(String data) {
        MHealthTask tsk = new MHealthTask(this, Task.RETRIEVE_SCHEDULE_LIST, R.string.retrieving_data, R.string.please_wait);
        if (data != null && data.trim().length() > 0) {
            tsk.setParam(1, (String) data);
        } else {
            tsk.setParam(1, "");
        }
        tsk.setCompleteListener(this);
        tsk.execute();
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

        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this);
        }

        //activityPath=Utility.setActivityPath(this, R.string.tomorrow_time_schedule);
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
     * Show data.
     *
     * @param scheduleList the schedule list
     */
    private void showData(ArrayList<UserScheduleInfo> scheduleList) {
        binding.llScheduleRowContainer.removeAllViews();
        if (scheduleList == null || scheduleList.size() == 0) {
            showOneButtonDialog(getResources().getString(R.string.data_not_available), R.drawable.information, Color.BLACK);
            return;
        }
        for (UserScheduleInfo schedInfo : scheduleList) {
            View view;
            if (schedInfo.getBeneficiaryCode() != null) {
                view = View.inflate(this, R.layout.patient_followup_row, null);

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

                TextView tvDesc = view.findViewById(R.id.tv_followup_desc);
                tvDesc.setText(schedInfo.getDescription());
            } else {
                view = View.inflate(this, R.layout.fcm_event_row, null);

                TextView tvTitle = view.findViewById(R.id.tv_schedule_desc);
                tvTitle.setText(schedInfo.getDescription());

                TextView tvType = view.findViewById(R.id.tv_schedule_type);
                tvType.setText(schedInfo.getScheduleType());
            }
            binding.llScheduleRowContainer.addView(view);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menu_search, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Override onQueryTextSubmit method which is call when submit query is searched
            @Override
            public boolean onQueryTextSubmit(String query) {
                // If the list contains the search query than filter the adapter
                // using the filter method with the query as its argument
                if (!query.equalsIgnoreCase("") && query.length() >= 3) {

                    retriveAndShow(query);
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
        }

        return super.onOptionsItemSelected(item);
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
                    MHealthTask tsk = new MHealthTask(this, Task.REPORT_SCHEDULE_LIST, R.string.retrieving_data, R.string.please_wait);
                    tsk.setParam(response, 1);
                    tsk.setCompleteListener(this);
                    tsk.execute();
                }
            }

        } else if (msg.getData().getString(TaskKey.NAME).equals(TaskKey.MHEALTH_TASK)) {
            if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                App.showMessageDisplayDialog(this, getResources().getString(R.string.saving_error), R.drawable.error, Color.RED);
            } else {
                showData((ArrayList<UserScheduleInfo>) msg.getData().getSerializable(TaskKey.DATA0));
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

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LanguageContextWrapper.wrap(context, AppPreference.getString(context, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)));
    }
}
