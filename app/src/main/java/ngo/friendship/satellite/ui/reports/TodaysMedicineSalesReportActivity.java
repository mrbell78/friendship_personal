package ngo.friendship.satellite.ui.reports;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.TodaysSalesLayoutBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.views.DialogView;


// TODO: Auto-generated Javadoc

/**
 * Display FCM's today's medicines sales report
 * <br>Created Date: 25th May 2014
 * <br>Last Update: 25th May 2014.
 *
 * @author Kayum Hossan
 */
public class TodaysMedicineSalesReportActivity extends AppCompatActivity implements OnCompleteListener {

    LinearLayout llMedicineContainer;
    ArrayList<MedicineInfo> medicineList;
    String activityPath;
    TodaysSalesLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = TodaysSalesLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
        binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
//		binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
//			binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
//			binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
//		});
//		binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
//			binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
//			binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
//		});

        if (getIntent().getExtras() != null) {
            activityPath = getIntent().getExtras().getString(ActivityDataKey.ACTIVITY_PATH);
            setTitle("" + activityPath);
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        RequestData request = new RequestData(RequestType.FCM_REPORT_SERVICE, RequestName.TODAYS_SALES, Constants.MODULE_DATA_GET);
        CommiunicationTask task = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
        task.setCompleteListener(this);
        task.execute();
//		binding.layoutSync.llAllSync.setOnClickListener(v -> {
//			if(SystemUtility.isConnectedToInternet(this))
//			{
//				RequestData request =new RequestData(RequestType.FCM_REPORT_SERVICE ,RequestName.TODAYS_SALES,Constants.MODULE_DATA_GET );
//				CommiunicationTask task=new CommiunicationTask(this, request,R.string.retrieving_data, R.string.please_wait);
//				task.setCompleteListener(this);
//				task.execute();
//			}
//			else{
//				SystemUtility.openInternetSettingsActivity(this);
//			}
//		});

//		MHealthTask tsk=new MHealthTask(this,Task.TODAYS_MEDICINE_SALES_REPOR, R.string.retrieving_data, R.string.please_wait);
//		tsk.setCompleteListener(this);
//		tsk.execute();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        if (App.getContext().getAppSettings() == null)
            App.getContext().readAppSettings(this);


        //activityPath=Utility.setActivityPath(this,R.string.today_sale);

        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this);
        }


    }


    private void showData() {
        double totalPrice = 0;
        binding.llMedicineRowContainer.removeAllViews();

        if (medicineList == null || medicineList.size() == 0) {
            binding.tvTotalMedicinePrice.setText(TextUtility.format("%s %.1f", getResources().getString(R.string.total_sale), totalPrice));
            binding.itemNotFound.serviceNotFound.setVisibility(View.VISIBLE);
//			showOneButtonDialog(getResources().getString(R.string.data_not_available), R.drawable.information, Color.BLACK);
            return;
        } else {
            binding.itemNotFound.serviceNotFound.setVisibility(View.GONE);
        }


        for (MedicineInfo medInfo : medicineList) {
            View v = View.inflate(this, R.layout.todays_sales_row, null);

            TextView tvMedName = v.findViewById(R.id.tv_medicine_name);
            tvMedName.setText(medInfo.getBrandName());

            TextView tvMedQty = v.findViewById(R.id.tv_medicine_quantity);
            tvMedQty.setText("" + medInfo.getSoldQuantity());

            TextView tvMedPrice = v.findViewById(R.id.tv_medicine_price);
            tvMedPrice.setText(TextUtility.format("%.2f", medInfo.getTotalPrice()));
            tvMedPrice.setVisibility(View.GONE);
            binding.llMedicineRowContainer.addView(v);
            totalPrice += Double.parseDouble(medInfo.getSoldQuantity()+"");
        }


        binding.tvTotalMedicinePrice.setText(TextUtility.format("%s %.1f", getResources().getString(R.string.total_sale), totalPrice));
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
                    MHealthTask tsk = new MHealthTask(this, Task.TODAYS_MEDICINE_SALES_REPOR, R.string.retrieving_data, R.string.please_wait);
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
                medicineList = (ArrayList<MedicineInfo>) msg.getData().getSerializable(TaskKey.DATA0);
                showData();
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
