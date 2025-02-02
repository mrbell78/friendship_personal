package ngo.friendship.satellite;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import ngo.friendship.satellite.adapter.BeneficiaryPageListAdapter;
import ngo.friendship.satellite.adapter.PaginatedAdapterData;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.DownloadFileTask;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.ActivityMainBinding;
import ngo.friendship.satellite.databinding.LocationWiseBenefDialogBinding;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.interfaces.OnDownloadFileCompleteListener;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppVersionHistory;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.LocationEvent;
import ngo.friendship.satellite.model.LocationModel;
import ngo.friendship.satellite.model.QuestionnaireInfo;
import ngo.friendship.satellite.model.SatelliteSessionChwarModel;
import ngo.friendship.satellite.model.SatelliteSessionModel;
import ngo.friendship.satellite.model.UserInfo;
import ngo.friendship.satellite.service.Alarm;
import ngo.friendship.satellite.service.BootReceiver;
import ngo.friendship.satellite.service.ManualSyncService;
import ngo.friendship.satellite.ui.ChangePasswordActivity;
import ngo.friendship.satellite.ui.InterviewActivity;
import ngo.friendship.satellite.ui.WebViewActivity;
import ngo.friendship.satellite.ui.beneficiary.BeneficiaryActivity;
import ngo.friendship.satellite.ui.earlier_services.EarlierServicesActivity;
import ngo.friendship.satellite.ui.stock_manage.ProductsHomeActivity;
import ngo.friendship.satellite.ui.reports.ReportListActivity;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.Base64;
import ngo.friendship.satellite.utility.BusProvider;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.viewmodels.OfflineViewModel;
import ngo.friendship.satellite.views.AppToast;
import ngo.friendship.satellite.views.DialogView;

@AndroidEntryPoint
public class MainActivity extends BaseActivity implements OnCompleteListener {

    private ActivityMainBinding binding;
    BootReceiver bootReceiver;
    UserInfo userInfo = new UserInfo();
    public static boolean PASSWORD_CHANGED = false;

    ArrayList<Beneficiary> beneficiaryList = new ArrayList<>();
    ArrayList<Beneficiary> filterBeneficiaryList = new ArrayList<>();
    String filterTextLocation = "";
    //    @Inject
//    OfflineViewModel offlineViewModel;
    OfflineViewModel mainViewModel;
    BeneficiaryPageListAdapter adapter;
    int pageSize = 10;
    private int currentPage = 0;
    ArrayList<String> entryPrams;
    ArrayList<LocationModel> selectedLocationList = new ArrayList<>();
    ArrayList<LocationModel> previousSelectedLocation = new ArrayList<>();
    ArrayList<LocationModel> selectedRadioList = new ArrayList<>();
    long selectedRadioValue = 0;
    long satSessionId = 0;


    ArrayList<UserInfo> selectedFcmList = new ArrayList<>();

    // Get the current date
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    Calendar sessionDate = Calendar.getInstance();
    String sessionDdateformate = "";
    String pendingSyncPage = "";

    ArrayList<SatelliteSessionModel> previosuSatelliteInfoList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        String usrSLNo = AppPreference.getString(MainActivity.this, KEY.USR_SL_NO, "");


        userInfo = App.getContext().getUserInfo();
        mainViewModel = new ViewModelProvider(this).get(OfflineViewModel.class);

        initDataLoad();
        initSidebarLoad();
        allClickListenerLoad();
        // call autoSync Scheduler
        loadAutoSyncScheduler();
//        scheduleDataSave();
        allClickListenerLoad();
        loadFcmSelectedData();
//        scheduleAlarm();
       // MyWorkScheduler.scheduleWork();


        String pendingSyncPage = "";
        try {
            pendingSyncPage = getIntent().getStringExtra(ActivityDataKey.PENDING_SYNC_PAGE);
            if (pendingSyncPage != null && pendingSyncPage.length() > 0) {
                Intent earlierService = new Intent(MainActivity.this, EarlierServicesActivity.class);
                earlierService.putExtra("PAGE_TYPE", "SERVER_TO_SENT");
                earlierService.putExtra("PAGE_TITLE", "Total Service");
                startActivity(earlierService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void loadFcmSelectedData() {
        String selectedLocation = AppPreference.getString(MainActivity.this, ActivityDataKey.SELECTED_LOCATION_NAME, "");
        String selectedFcm = AppPreference.getString(MainActivity.this, ActivityDataKey.SELECTED_FCM_STRING, "");

        // Set selected location name in the text view
        assert selectedLocation != null;
        if (!selectedLocation.isEmpty()) {
            binding.tvAreaName.setText(selectedLocation);
        } else {
            binding.tvAreaName.setText(R.string.select_location_area);
        }

        // Set selected FCM string in the text view
        assert selectedFcm != null;
        if (!selectedFcm.isEmpty()) {
            binding.tvSelectedFcmNames.setText(selectedFcm);
        } else {
            // binding.tvSelectedFcmNames.setText(R.string.select_fcm);
        }

        try {
            // Get the selected FCM list from preferences
            String selectedFcmList = AppPreference.getString(MainActivity.this, ActivityDataKey.SELECTED_FCM, "");
            // Convert the selected FCM list from JSON to ArrayList of UserInfo objects
            Type modelListType = new TypeToken<ArrayList<UserInfo>>() {
            }.getType();
            this.selectedFcmList = new Gson().fromJson(selectedFcmList, modelListType);
            binding.tvFcmCoverageCount.setText(String.format("%02d", this.selectedFcmList.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSidebarLoad() {
        try {
            // Set the user-related information in the UI.
            String userName = userInfo.getUserName();
            binding.navHeader.tvFcmNameWithCode.setText("" + Character.toUpperCase(userName.charAt(0)) + userName.substring(1));
            binding.navHeader.tvParamedicLocation.setText(userInfo.getLocationName());
            binding.navHeader.tvFcmCode.setText(userInfo.getUserCode());
            // binding.navHeader.tvParamedicLocation.setText(getResources().getString(R.string.location) + " \n" + userInfo.getLocationName());
            binding.navHeader.tvVersionName.setText("" + Utility.getVersionName(MainActivity.this));
            binding.navHeader.tvDeviceId.setText("" + Utility.getIMEInumber(MainActivity.this));
            // Load and display the user's profile picture if available.
            if (userInfo.getProfilePicInString() != null && !userInfo.getProfilePicInString().equals("") && userInfo.getProfilePicInString().length() > 7) {
                InputStream stream;
                try {
                    stream = new ByteArrayInputStream(Base64.decode(userInfo.getProfilePicInString()));
                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                    binding.navHeader.ivFcmPic.setImageBitmap(bitmap);
                    binding.ivHeadFcmPic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Navigation-related listeners
        binding.navHeader.llHelp.setOnClickListener(v -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(ActivityDataKey.WEB_URL, "file:///android_asset/html/about.html");
            intent.putExtra(ActivityDataKey.TYPE, "HELP");
            startActivity(intent);
        });
        //  file:///android_asset/mHealth-Help/bn/index.html
        binding.navHeader.llAllService.setOnClickListener(v -> {
            Intent earlierService = new Intent(MainActivity.this, EarlierServicesActivity.class);
            earlierService.putExtra("PAGE_TYPE", "SERVER_TO_SENTSS");
            earlierService.putExtra("PAGE_TITLE", "Total Service");
            startActivity(earlierService);
        });

        binding.navHeader.llFooter.setOnClickListener(v -> {
            if (SystemUtility.isConnectedToInternet(MainActivity.this)) {
                try {
                    RequestData request = new RequestData(RequestType.USER_GATE, RequestName.APP_VERSION_HISTORY, Constants.MODULE_DATA_GET);
                    HashMap<String, String> refTable = new HashMap<String, String>();
                    refTable.put(KEY.APP_VERSION_NUMBER, " ");
                    request.setParam1(Utility.getTableRef(refTable, this));
                    request.getParam1().put("TYPE", "FORCE");
                    CommiunicationTask commiunicationTask = new CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait);
                    commiunicationTask.setCompleteListener(new OnCompleteListener() {

                        @Override
                        public void onComplete(Message msg) {
                            if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                                String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                                App.showMessageDisplayDialog(MainActivity.this, getResources().getString(R.string.network_error), R.drawable.error, Color.RED);
                            } else {
                                ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                                if (response.getResponseCode().equalsIgnoreCase("00")) {
                                    App.showMessageDisplayDialog(MainActivity.this, response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
                                } else {
                                    try {
                                        AppVersionHistory avh = null;
                                        if (response.getResponseCode().equalsIgnoreCase("01") && response.getDataJson().has("APP_VERSION_HISTORY")) {
                                            avh = JSONParser.parseAppVersionHistoryJSON(response.getDataJson().getJSONObject("APP_VERSION_HISTORY"));
                                        }
                                        if (avh != null) {
                                            App.getContext().getDB().saveAppVersionHistoryOnReceived(avh);
                                            Log.e("AppVersionHistory save", avh.toString());
                                            avh.setInstallFlag(AppVersionHistory.FLAG_RECEIVED);
                                            showInstallConfirmationPrompt(avh);
                                        } else {
                                            App.showMessageDisplayDialog(MainActivity.this, getResources().getString(R.string.no_app_update_available), R.drawable.information, Color.BLACK);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        }
                    });
                    commiunicationTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                SystemUtility.openInternetSettingsActivity(MainActivity.this);
            }
        });
/** Go to stock management module **/
        binding.navHeader.llMedicines.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductsHomeActivity.class);
            startActivity(intent);
        });
/** ......................................................................  **/
        binding.navHeader.llAllRepor.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportListActivity.class);
            startActivity(intent);
        });

        binding.navHeader.btnLogout.setOnClickListener(v -> {
            MainActivity.this.finish();
        });

        SpannableString content = new SpannableString("Change Password");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        binding.navHeader.tvChangePassword.setText(content);
        binding.navHeader.tvChangePassword.setOnClickListener(v -> {
            if (!App.getContext().isDemo()) {
                startActivity(new Intent(this, ChangePasswordActivity.class));
            } else {
                HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
                buttonMap.put(1, R.string.btn_close);
                DialogView dialog = new DialogView(this, R.string.dialog_title, R.string.not_aplicable_for_demo, R.drawable.warning, buttonMap);
                dialog.show();
            }
        });
    }

    private void loadAutoSyncScheduler() {
        bootReceiver = new BootReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_DEFAULT);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(bootReceiver, filter);
        broadcastIntent();
    }

//    private void scheduleDataSave() {
//        bootReceiver = new BootReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        filter.addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED);
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_DEFAULT);
//        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//        filter.addAction(Intent.ACTION_POWER_CONNECTED);
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        registerReceiver(bootReceiver, filter);
//        broadcastIntent();
//    }

    private void allClickListenerLoad() {
        // Sync-related listeners
        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
        });

        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
        });
        binding.layoutSync.llAllSync.setOnClickListener(v -> {
            Intent intnt = new Intent(this, ManualSyncService.class);
            intnt.putExtra(ActivityDataKey.IS_FORCE, true);
            intnt.putExtra(ActivityDataKey.MANUAL_SYNC, ActivityDataKey.MANUAL_SYNC);
            this.startService(intnt);
        });

        binding.layoutSync.llGetData.setOnClickListener(v -> {
            showConfirmationDialog(getResources().getString(R.string.retrieve_data_confirmation), R.drawable.information, DATA_GET);
        });
        binding.layoutSync.llPenddingSync.setOnClickListener(v -> {
            Intent earlierService = new Intent(MainActivity.this, EarlierServicesActivity.class);
            earlierService.putExtra("PAGE_TYPE", "SERVER_TO_SENTSS");
            earlierService.putExtra("PAGE_TITLE", "Total Service");
            startActivity(earlierService);
        });


        //get Unsync count
        binding.layoutSync.tvUnSyncCount.setText("" + getResources().getString(R.string.unsync_n_15) + " (" + getUnsyncCount() + ")");
        //get Sync Count
        binding.layoutSync.tvSyncCount.setText("" + getResources().getString(R.string.sync));

        // Other listeners
        binding.benefFilter.setOnClickListener(v -> {
            showCustomDialogSelectdBenfFcm();
            // startActivity(new Intent(MainActivity.this,DoctrosFeedbackDetails.class));
        });

        binding.llProfileHead.setOnClickListener(view -> binding.drawerLayout.openDrawer(Gravity.RIGHT));
        binding.llTotalService.setOnClickListener(v -> {
            Intent earlierService = new Intent(MainActivity.this, EarlierServicesActivity.class);
            earlierService.putExtra("PAGE_TYPE", "SERVER_TO_SENT");
            earlierService.putExtra("PAGE_TITLE", "Total Service");
            startActivity(earlierService);
        });

        binding.llDownload.btnGetMyData.setOnClickListener(v -> {
            if (SystemUtility.isConnectedToInternet(this)) {
                showConfirmationDialog(getResources().getString(R.string.retrieve_data_confirmation), R.drawable.information, DATA_GET);
            } else {
                SystemUtility.openInternetSettingsActivity(this);
            }
        });

        binding.llDownload.btnSkipNow.setOnClickListener(v -> {
            AppPreference.putString(this, KEY.IS_USER_LOGINNED, "SKIPPED");
            binding.llDownload.llAutoSync.setVisibility(View.GONE);
            App.getContext().setAutoSyncServiceRunning(false);
            binding.drawerLayout.setVisibility(View.VISIBLE);
            HashMap<Integer, Object> btnMap = new HashMap<Integer, Object>();
            btnMap.put(1, R.string.btn_ok);
            DialogView alertDialog = new DialogView(MainActivity.this, R.string.dialog_title, R.string.sync_skip_message, R.drawable.information, btnMap);
            alertDialog.setOnDialogButtonClick(view -> {
                switch (view.getId()) {
                    case 1:
                        //hide dialog
                        break;
                    default:
                        break;
                }
            });
            alertDialog.show();
        });

        binding.llmHealthBenef.setOnClickListener(v -> {
            Intent benefActivity = new Intent(MainActivity.this, BeneficiaryActivity.class);
            benefActivity.putExtra(ActivityDataKey.INTENT_DATA_PASS, Constants.BENEFICIARY_LIST_mHEALTH);
            startActivity(benefActivity);
        });
        binding.tvMyBenefCount.setOnClickListener(v -> {
            Intent benefActivity = new Intent(MainActivity.this, BeneficiaryActivity.class);
            benefActivity.putExtra(ActivityDataKey.INTENT_DATA_PASS, Constants.BENEFICIARY_LIST_PARAMEDIC);
            startActivity(benefActivity);
        });
        binding.btnBenefSearch.setOnClickListener(v -> {
            String benefSearch = binding.etBenefSearch.getText().toString();
            // Perform search operation with benefSearch
        });

        SpannableString newBenReg = new SpannableString("(+) New Registration");
        newBenReg.setSpan(new UnderlineSpan(), 0, newBenReg.length(), 0);
        // binding.tvNewRegistration.setText(newBenReg);

        binding.btnBenefSearch.setOnClickListener(v -> {
            String defaultBenefCode = Utility.getFcmConfigurationValue(this, "default_beneficiary", "default.beneficiary.code", "00000");
            String language = App.getContext().getAppSettings().getLanguage();
            QuestionnaireInfo questionnaireInfo = App.getContext().getDB().getQuestionnaireCallByName("BENEFICIARY_REGISTRATION", language);
            boolean isGuest = Boolean.parseBoolean(Utility.getFcmConfigurationValue(this, "default_beneficiary", "is.default.beneficiary.guest", "true"));
            startInterview(questionnaireInfo, null, isGuest);
        });


        binding.etBenefSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // new FilterTask().execute(s.toString());
                try {
                    adapter.filter(s.toString());
                    //filterBeneficiaryListData(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private Long getUnsyncCount() {
        Long count = App.getContext().getDB().getInterviewListCount(
                "N",
                App.getContext().getAppSettings().getLanguage(),
                App.getContext().getUserInfo().getUserCode()
        );

        return count;
    }

    /**
     * Initializes the data load for the activity.
     */
    private void initDataLoad() {
        Date date = new Date();  // Get the current date.
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM, yyyy");  // Format for the month and year.
        String currentDate = simpleDateFormat.format(date).toLowerCase();
        binding.tvReportingMonthYear.setText("" + Character.toUpperCase(currentDate.charAt(0)) + currentDate.substring(1));  // Set the formatted date in the text view.
        long monthInterviewCount = App.getContext().getDB().getCurrentMonthInterviewCount(App.getContext().getAppSettings().getLanguage());
        long todayInterviewCount = App.getContext().getDB().getCurrentTodayInterviewCount(App.getContext().getAppSettings().getLanguage());
        long sessionCount = App.getContext().getDB().getMonthlySessionCount(App.getContext().getAppSettings().getLanguage());
        binding.tvMonthlyService.setText("" + String.format("%02d", monthInterviewCount));
        binding.tvTodayServiceCount.setText("" + String.format("%02d", todayInterviewCount));
        binding.tvSessionCount.setText("" + String.format("%02d", sessionCount));

        try {
            String userName = userInfo.getUserName();
            binding.tvFcmName.setText("" + Character.toUpperCase(userName.charAt(0)) + userName.substring(1));
        } catch (Exception e) {
            e.printStackTrace();
        }


        MHealthTask tsk = new MHealthTask(MainActivity.this, Task.RETERIEVE_BENEFICIARY_LIST, R.string.load_beneficiaries, R.string.please_wait);
        tsk.setParam("");
        tsk.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Message msg) {
                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                    String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                    AppToast.showToast(MainActivity.this, errorMsg);
                }
                beneficiaryList = (ArrayList<Beneficiary>) msg.getData().getSerializable(TaskKey.DATA0);
                try {
                    ArrayList<Beneficiary> mHealthBenefList = App.getContext().getDB().getBeneficiaryList(Constants.BENEFICIARY_LIST_mHEALTH, userInfo.getUserId());
                    binding.tvmHealthCount.setText("" + String.format("%02d", mHealthBenefList.size()));

                    ArrayList<Beneficiary> paramedicbenfList = App.getContext().getDB().getBeneficiaryList(Constants.BENEFICIARY_LIST_PARAMEDIC, userInfo.getUserId());
                    binding.tvMyBenefCount.setText("" + String.format("%02d", paramedicbenfList.size()));
                    getbenefListAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        tsk.execute();

    }

    public void loadSessionData() {
        long lastDate = AppPreference.getLong(MainActivity.this, ActivityDataKey.LAST_LOAD_DATE, 0);
        if (lastDate > 0) {
            SatelliteSessionModel previosuSatelliteInfsso = App.getContext().getDB().getSessionDateWise(lastDate, App.getContext().getUserInfo().getUserId());
            if (previosuSatelliteInfsso != null && previosuSatelliteInfsso.getSatSessionDate() > 0) {
                sessionDate.setTimeInMillis(previosuSatelliteInfsso.getSatSessionDate());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                sessionDdateformate = dateFormat.format(sessionDate.getTime());
                binding.todaysSessionDate.setText("" + sessionDdateformate);
                String locationName = "";
                LocationModel locationData = App.getContext().getDB().getLocationById(previosuSatelliteInfsso.getSatSessionLocationId());
                previousSelectedLocation.add(locationData);
                locationName = locationData.getLocationName();

                binding.sessionLocation.setText("" + locationName);
                binding.todaysSessionDate.setText("" + sessionDdateformate);
            } else {
                binding.todaysSessionDate.setText("" + sessionDdateformate);

            }
        }

    }

    private void getbenefListAdapter() {
        adapter = new BeneficiaryPageListAdapter(MainActivity.this);
        adapter.setDefaultRecyclerView(this, R.id.rvBenefList);
        adapter.setStartPage(1); //set first page of data. default value is 1.
        adapter.setPageSize(10); //set page data size. default value is 10.
        adapter.setOnPaginationListener(new PaginatedAdapterData.OnPaginationListener() {
            @Override
            public void onCurrentPage(int page) {
                //Toast.makeText(MainActivity.this, "Page " + page + " loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNextPage(int page) {
                getNewItems(page);
            }

            @Override
            public void onFinish() {
                // Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvBenefList.setAdapter(adapter);
        getNewItems(adapter.getStartPage());
        binding.rvBenefList.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        //manager.scrollToPositionWithOffset(0, beneficiaryList.size());
        binding.rvBenefList.setLayoutManager(manager);
        binding.rvBenefList.setItemAnimator(new DefaultItemAnimator());
        Collections.reverse(beneficiaryList);
        adapter.setItems(beneficiaryList);

    }

    private void startInterview(QuestionnaireInfo questionnaireInfo, String benefCode, boolean isGust) {
        entryPrams = new ArrayList<String>();
        Intent intent = new Intent(this, InterviewActivity.class);
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_JSON, questionnaireInfo.getQuestionnaireJSON(this));
        intent.putExtra(ActivityDataKey.ACTIVITY_PATH, "");
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_TITLE, questionnaireInfo.getQuestionnaireTitle());
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_ID, questionnaireInfo.getId());
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_NAME, questionnaireInfo.getQuestionnaireName());
        intent.putExtra(ActivityDataKey.BENEFICIARY_CODE, benefCode);
//        intent.putExtra(ActivityDataKey.BENEFICIARY_ID, beneficiary.getBenefId());
        intent.putExtra(ActivityDataKey.PARAMS, entryPrams);
        intent.putExtra(ActivityDataKey.IS_GUST, isGust);
        intent.putExtra(ActivityDataKey.SINGLE_PG_FROM_VIEW, questionnaireInfo.getIsSinglePgFormView());
        intent.putExtra(ActivityDataKey.INTERVIEW_TYPE, ActivityDataKey.NEW);
        intent.putExtra("EDIT_HOUSEHOLD", true);
        startActivity(intent);
    }


    public void onGetDate(ArrayList<Beneficiary> users) {
        adapter.submitItems(users);
    }

    private void getNewItems(final int page) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<Beneficiary> users = new ArrayList<>();
                int start = page * pageSize - pageSize;
                int end = page * pageSize;
                for (int i = start; i < end; i++) {
                    if (i < beneficiaryList.size()) {
                        users.add(beneficiaryList.get(i));
                    }
                }
                onGetDate(users);
            }
        }, 500);
    }

    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("ngo.friendship.satellite.AN_INTENT");
        intent.setComponent(new ComponentName(getPackageName(), ".service.BootReceiver"));
        getApplicationContext().sendBroadcast(intent);
    }

    private void showCompleteDownloadDataDilog() {
        /* force user to Sync data from server */
        int syncStatus = App.getContext().getDB().getMandatoryDataDownloadedStatus(this);
        if (syncStatus < 100 && AppPreference.getString(this, KEY.IS_USER_LOGINNED, "").equals("YES")) {
            binding.drawerLayout.setVisibility(View.GONE);
            binding.llDownload.llAutoSync.setVisibility(View.VISIBLE);
            binding.llDownload.sbSyncStatus.setProgress(syncStatus);
            binding.llDownload.tvSyncStatus.setText(syncStatus + "%");
        } else {
            binding.drawerLayout.setVisibility(View.VISIBLE);
            binding.llDownload.llAutoSync.setVisibility(View.GONE);
        }
    }

    public static final int DATA_GET = 1;
    public static final int EXIT = 2;
    private void showConfirmationDialog(String msg, int imageId, final int type) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_yes);
        buttonMap.put(2, R.string.btn_no);
        DialogView dialog = new DialogView(this, R.string.dialog_title, msg, R.drawable.warning, buttonMap);
        dialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {
                switch (view.getId()) {
                    case 1:
                        if (type == DATA_GET) {
                            getMyData();
                        } else if (type == EXIT) {
                            MainActivity.this.finish();
                        }
                        break;
                }

            }
        });
        dialog.show();
    }

    private void getMyData() {
        MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        App.getContext().loadApplicationData(MainActivity.this);
        RequestData request = new RequestData(RequestType.USER_GATE, RequestName.MY_DATA, Constants.MODULE_DATA_GET);
        request.setParam1(Utility.getTableRef(null, MainActivity.this));
        CommiunicationTask commiunicationTask = new CommiunicationTask(MainActivity.this, request, R.string.retrieving_data, R.string.please_wait);
        commiunicationTask.setCompleteListener(MainActivity.this);
        commiunicationTask.execute();
    }

//    private void getMyParamedicData() {
//        MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        App.getContext().loadApplicationData(MainActivity.this);
//        RequestData request = new RequestData(RequestType.USER_GATE, RequestName.MY_DATA_PARAMEDIC, Constants.MODULE_DATA_GET);
//        request.setParam1(Utility.getTableRef(null, MainActivity.this));
//        CommiunicationTask commiunicationTask = new CommiunicationTask(MainActivity.this, request, R.string.retrieving_data, R.string.please_wait);
//        commiunicationTask.setCompleteListener(MainActivity.this);
//        commiunicationTask.execute();
//    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Object event) {
        if (event instanceof LocationEvent) {
            filterTextLocation = "" + ((LocationEvent) event).getValue();
            mainViewModel.setMyString("" + filterTextLocation);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            BusProvider.getBus().register(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void onStop() {
        super.onStop();

//        App.getContext().onStartActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            BusProvider.getBus().unregister(this);
        } catch (Exception e) {
        }
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
                MHealthTask tsk = new MHealthTask(MainActivity.this, Task.MYDATA, R.string.saving_data, R.string.please_wait);
                tsk.setParam(response);
                tsk.setCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(Message msg) {
                        showCompleteDownloadDataDilog();
                        if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                            String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                            App.showMessageDisplayDialog(MainActivity.this, getResources().getString(R.string.saving_error), R.drawable.error, Color.RED);
                        } else {

                            if (JSONParser.getLongNullAllow(response.getParamJson(), KEY.NEED_SAME_REQ) == 1) {
                                getMyData();
                            } else {
                                initDataLoad();
                                App.showMessageDisplayDialog(MainActivity.this, getResources().getString(R.string.retrieve_successfull), R.drawable.information, Color.BLACK);
                                UserInfo userInfo = App.getContext().getUserInfo();
                                userInfo.setToken(AppPreference.getString(MainActivity.this, KEY.TOKEN, ""));
                                Alarm.startAutosyncService(MainActivity.this);
                                Alarm.startNotificationService(MainActivity.this);
                            }
                        }
                    }
                });
                tsk.execute();
            }
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        
        Alarm.startAutosyncService(MainActivity.this);
        Alarm.startNotificationService(MainActivity.this);
        //get Unsync count
        binding.layoutSync.tvUnSyncCount.setText("" + getResources().getString(R.string.unsync_n_15) + " (" + getUnsyncCount() + ")");
        //get Sync Count
        binding.layoutSync.tvSyncCount.setText("" + getResources().getString(R.string.sync));
        long monthInterviewCount = App.getContext().getDB().getCurrentMonthInterviewCount(App.getContext().getAppSettings().getLanguage());
        long todayInterviewCount = App.getContext().getDB().getCurrentTodayInterviewCount(App.getContext().getAppSettings().getLanguage());
        long sessionCount = App.getContext().getDB().getMonthlySessionCount(App.getContext().getAppSettings().getLanguage());
        binding.tvMonthlyService.setText("" + String.format("%02d", monthInterviewCount));
        binding.tvTodayServiceCount.setText("" + String.format("%02d", todayInterviewCount));
        binding.tvSessionCount.setText("" + String.format("%02d", sessionCount));
        loadSessionData();
        if (PASSWORD_CHANGED) {
            this.finish();
            return;
        }

        showCompleteDownloadDataDilog();
    }

    @Override
    public void onBackPressed() {
        showConfirmationDialog(getResources().getString(R.string.exit_confirmation), R.drawable.warning, EXIT);
    }

    public String StringFormate(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public void showCustomDialogSelectdBenfFcm() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LocationWiseBenefDialogBinding dialogBinding = LocationWiseBenefDialogBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();
        ArrayList<LocationModel> locationList = App.getContext().getDB().getLocationList();
        long orgId = AppPreference.getLong(MainActivity.this, Column.ORG_ID, -1);
        String loginId = AppPreference.getString(MainActivity.this, Column.USER_LOGIN_ID, "");
        dialogBinding.spLocationDialog.setItems(locationList);
        try {
            if (selectedLocationList.size() > 0) {
                dialogBinding.spLocationDialog.setSelection(selectedLocationList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String itemSelectedLocation = dialogBinding.spLocationDialog.getSelectedItemsString();

        ArrayList<UserInfo> userInfo = App.getContext().getDB().getAllUserInfo(orgId, loginId, 1, itemSelectedLocation);
        dialogBinding.spFcmDialog.setItems(userInfo);

        try {
            dialogBinding.spFcmDialog.setSelection(selectedFcmList, MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long lastDate = AppPreference.getLong(MainActivity.this, ActivityDataKey.LAST_LOAD_DATE, 0);
        if (lastDate > 0) {
            sessionDate.setTimeInMillis(lastDate);
        } else {
            long timeInMillis = System.currentTimeMillis();
            sessionDate.setTimeInMillis(timeInMillis);
        }

        if (sessionDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            // Format the date using the SimpleDateFormat
            sessionDdateformate = dateFormat.format(sessionDate.getTime());
            dialogBinding.sessionDate.setText("" + sessionDdateformate);
            SatelliteSessionModel previosuSatelliteInfsso = App.getContext().getDB().getSessionDateWise(sessionDate.getTimeInMillis(), App.getContext().getUserInfo().getUserId());
            if (previosuSatelliteInfsso != null && previosuSatelliteInfsso.getSatelliteSessionId() > 0) {
                satSessionId = previosuSatelliteInfsso.getSatelliteSessionId();
                LocationModel locationData = App.getContext().getDB().getLocationById(previosuSatelliteInfsso.getSatSessionLocationId());
                ArrayList<LocationModel> seletetedSateliteLocationDB = new ArrayList<>();
                seletetedSateliteLocationDB.add(locationData);
                getRadioSelectedLocation(dialogBinding, seletetedSateliteLocationDB, true);

                ArrayList<SatelliteSessionChwarModel> selectedSessionCharlist = App.getContext().getDB().getSatelliteSessionChar(previosuSatelliteInfsso.getSatelliteSessionId());
                ArrayList<LocationModel> previousSelectedLocationList = new ArrayList<>();
                StringBuilder fcmLocationString = new StringBuilder();
                for (SatelliteSessionChwarModel ssChar : selectedSessionCharlist) {
                    LocationModel selectedChwLocation = App.getContext().getDB().getLocationById(ssChar.getLOCATION_ID());
                    previousSelectedLocationList.add(selectedChwLocation);
                    fcmLocationString.append("'");
                    fcmLocationString.append(selectedChwLocation.getLocationId());
                    fcmLocationString.append("'");
                    fcmLocationString.append(",");
                }

                dialogBinding.spLocationDialog.setSelection(previousSelectedLocationList);


                ArrayList<UserInfo> selectedFcmListUnderLocation = App.getContext().getDB().getAllUserInfo(orgId, loginId, 1, StringFormate(fcmLocationString.toString()));
//                dialogBinding.spFcmDialog.setItems(selectedFcmListUnderLocation);

                ArrayList<UserInfo> selectedFcmListDB = new ArrayList<>();
                for (UserInfo user : selectedFcmListUnderLocation) {
                    for (SatelliteSessionChwarModel selectedSessionChw : selectedSessionCharlist) {
                        if (user.getUserId() == selectedSessionChw.getUSER_ID()) {
                            selectedFcmListDB.add(user);
                        }
                    }
                }

                dialogBinding.spFcmDialog.setSelection(selectedFcmListDB, MainActivity.this);
                // getRadioSelectedLocation(dialogBinding, seletetedSateliteLocationDB,radioDefaultValue);
                dialogBinding.btnLoadBenef.setText(getResources().getString(R.string.update_session_load_beneficiaries));
                dialogBinding.btnLoadBenef.setBackgroundTintList(dialogBinding.btnLoadBenef.getResources().getColorStateList(R.color.dark_yellow));
            } else {
                satSessionId = 0;
//                dialogBinding.spLocationDialot.setSelection(previousSelectedLocation);
//                String itemSelectedLocationLocal = dialogBinding.spLocationDialot.getSelectedItemsString();
//                ArrayList<UserInfo> userInfoLocal = App.getContext().getDB().getAllUserInfo(orgId, loginId, 1, itemSelectedLocationLocal);
//                dialogBinding.spFcmDialot.setItems(userInfoLocal);
//                try {
//                    dialogBinding.spFcmDialot.setSelection(selectedFcmList,MainActivity.this);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                dialogBinding.spFcmDialot.setItems(userInfoLocal);
                //  getRadioSelectedLocation(dialogBinding,previousSelectedLocation,false);
                try {
                    selectedFcmList.clear();
                    dialogBinding.spFcmDialog.setSelection(selectedFcmList, MainActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialogBinding.btnLoadBenef.setText(getResources().getString(R.string.create_beneficiaries_and_load));
                dialogBinding.btnLoadBenef.setBackgroundTintList(dialogBinding.btnLoadBenef.getResources().getColorStateList(R.color.colorPrimary));
            }
        }

        dialogBinding.btnSessionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int initialYear = sessionDate.get(Calendar.YEAR);
                int initialMonth = sessionDate.get(Calendar.MONTH);
                int initialDay = sessionDate.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog and show it
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Handle the selected date from the dialog
                                sessionDate.set(year, monthOfYear, dayOfMonth);
                                // Create a SimpleDateFormat instance with the desired format
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

                                // Format the date using the SimpleDateFormat
                                sessionDdateformate = dateFormat.format(sessionDate.getTime());
                                dialogBinding.sessionDate.setText("" + sessionDdateformate);

                                SatelliteSessionModel previosuSatelliteInfsso = App.getContext().getDB().getSessionDateWise(sessionDate.getTimeInMillis(), App.getContext().getUserInfo().getUserId());
                                if (previosuSatelliteInfsso != null && previosuSatelliteInfsso.getSatelliteSessionId() > 0) {
                                    satSessionId = previosuSatelliteInfsso.getSatelliteSessionId();

                                    ArrayList<LocationModel> seletetedSateliteLocationDB = new ArrayList<>();


                                    for (LocationModel lm : locationList) {
                                        if (lm.getLocationId() == previosuSatelliteInfsso.getSatSessionLocationId()) {
                                            seletetedSateliteLocationDB.add(lm);
                                        }
                                    }

                                    ArrayList<SatelliteSessionChwarModel> sessionCharlist = new ArrayList<>();
                                    sessionCharlist = App.getContext().getDB().getSatelliteSessionChar(previosuSatelliteInfsso.getSatelliteSessionId());
                                    ArrayList<LocationModel> previousSelectedLocationList = new ArrayList<>();

                                    StringBuilder fcmLocationString = new StringBuilder();
                                    for (SatelliteSessionChwarModel ssChar : sessionCharlist) {
                                        for (LocationModel lm : locationList) {
                                            if (lm.getLocationId() == ssChar.getLOCATION_ID()) {
                                                previousSelectedLocationList.add(lm);
                                                fcmLocationString.append("'");
                                                fcmLocationString.append(lm.getLocationId());
                                                fcmLocationString.append("'");
                                                fcmLocationString.append(",");
                                            }
                                        }
                                    }

                                    dialogBinding.spLocationDialog.setSelection(previousSelectedLocationList);
                                    ArrayList<UserInfo> userInfo = App.getContext().getDB().getAllUserInfo(orgId, loginId, 1, StringFormate(fcmLocationString.toString()));

                                    dialogBinding.spFcmDialog.setItems(userInfo);
                                    ArrayList<UserInfo> selectedFcmListDB = new ArrayList<>();
                                    for (UserInfo user : userInfo) {
                                        for (SatelliteSessionChwarModel sschar : sessionCharlist) {
                                            if (user.getUserId() == sschar.getUSER_ID()) {
                                                selectedFcmListDB.add(user);
                                            }
                                        }
                                    }
                                    dialogBinding.spFcmDialog.setSelection(selectedFcmListDB, MainActivity.this);
                                    getRadioSelectedLocation(dialogBinding, seletetedSateliteLocationDB, true);
                                    dialogBinding.btnLoadBenef.setText("Update Session & Load Beneficiaries");
                                    dialogBinding.btnLoadBenef.setBackgroundTintList(dialogBinding.btnLoadBenef.getResources().getColorStateList(R.color.dark_yellow));

                                } else {
                                    satSessionId = 0;
                                    if (previousSelectedLocation.size() > 0)
                                        previousSelectedLocation.clear();

                                    dialogBinding.spLocationDialog.setSelection(previousSelectedLocation);
//                                    String itemSelectedLocation = dialogBinding.spLocationDialot.getSelectedItemsString();
//                                    ArrayList<UserInfo> userInfo = App.getContext().getDB().getAllUserInfo(orgId, loginId, 1, itemSelectedLocation);
//                                    dialogBinding.spFcmDialot.setItems(userInfo);
//                                    try {
//                                        dialogBinding.spFcmDialot.setSelection(selectedFcmList,MainActivity.this);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    dialogBinding.spFcmDialot.setItems(userInfo);
                                    getRadioSelectedLocation(dialogBinding, previousSelectedLocation, false);

                                    try {
                                        selectedFcmList.clear();
                                        dialogBinding.spFcmDialog.setSelection(selectedFcmList, MainActivity.this);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    dialogBinding.btnLoadBenef.setText(getResources().getString(R.string.create_beneficiaries_and_load));
                                    dialogBinding.btnLoadBenef.setBackgroundTintList(dialogBinding.btnLoadBenef.getResources().getColorStateList(R.color.colorPrimary));
                                }
                            }
                        }, initialYear, initialMonth, initialDay);
                Calendar currentTime = Calendar.getInstance();
                currentTime.add(Calendar.DAY_OF_MONTH, 1);
                datePickerDialog.getDatePicker().setMaxDate(currentTime.getTimeInMillis());
                //datePickerDialog.datePicker.minDate = minDate

                datePickerDialog.show();


            }
        });
        Log.d("lname", "showCustomDialogSelectdBenfFcm: ........lstring" + dialogBinding.spLocationDialog.getSelectedItemsString());
        // App.getContext().getDB().getSatelliteSession()
        // getRadioSelectedLocation(dialogBinding,selectedLocationList,radioDefaultValue);
        mainViewModel.getMyStringLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String myString) {
                List<String> stringList = Arrays.asList(myString.split(","));
                previousSelectedLocation.clear();
                if (myString != null && !myString.isEmpty() && myString != "") {
                    for (String id : stringList) {
                        long locationid = Long.parseLong(id.trim().replace("'", ""));
                        LocationModel locationData = App.getContext().getDB().getLocationById(locationid);
                        previousSelectedLocation.add(locationData);
                    }
                }
                getRadioSelectedLocation(dialogBinding, previousSelectedLocation, false);
                ArrayList<UserInfo> userInfo = App.getContext().getDB().getAllUserInfo(orgId, loginId, 1, myString);
                dialogBinding.spFcmDialog.setItems(userInfo);
            }
        });
        dialogBinding.btnClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialogBinding.btnLoadBenef.setOnClickListener(v -> {

            String locationSelectedName = dialogBinding.spLocationDialog.buildSelectedItemString();
            String fcmName = dialogBinding.spFcmDialog.buildSelectedItemString();
            binding.tvAreaName.setText("" + locationSelectedName);
            binding.tvSelectedFcmNames.setText("" + fcmName);
            if (locationSelectedName.length() > 0) {
                if (dialogBinding.spFcmDialog.getSelectedItems().size() > 0) {
                    if (selectedRadioValue > 0) {
                        String selectedFcmListString = new Gson().toJson(dialogBinding.spFcmDialog.getSelectedItems());
                        String selectedLocationListString = new Gson().toJson(dialogBinding.spLocationDialog.getSelectedItems());
                        AppPreference.putString(MainActivity.this, ActivityDataKey.SELECTED_FCM, selectedFcmListString);
                        AppPreference.putString(MainActivity.this, ActivityDataKey.SELECTED_LOCATION_AREA, selectedLocationListString);
                        AppPreference.putString(MainActivity.this, ActivityDataKey.SELECTED_FCM_STRING, fcmName);
                        AppPreference.putLong(MainActivity.this, ActivityDataKey.LAST_LOAD_DATE, sessionDate.getTimeInMillis());
                        AppPreference.putString(MainActivity.this, ActivityDataKey.SELECTED_LOCATION_NAME, locationSelectedName);
                        MainActivity.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        showConfarmationDilogParamedic(dialog, getResources().getString(R.string.retrieve_data_confirmation), R.drawable.information, dialogBinding.spFcmDialog.getSelectedItemsString(), dialogBinding);
                    } else {
                        AppToast.showToastWarnaing(MainActivity.this, "Please select session location");
                    }

                } else {
                    AppToast.showToastWarnaing(MainActivity.this, "Please select FCM");
                    //dialog.dismiss();
                }

            } else {
                AppToast.showToastWarnaing(MainActivity.this, "Please select Location");
                //dialog.dismiss();
            }

        });
    }

    private void getRadioSelectedLocation(LocationWiseBenefDialogBinding dialogBinding, ArrayList<LocationModel> locationModelsList, Boolean selectedItem) {
        dialogBinding.container.removeAllViews();

        // Create a RadioGroup
        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(LinearLayout.VERTICAL);
        radioGroup.removeAllViews();
        if (locationModelsList != null && locationModelsList.size() > 0) {
            //dialogBinding.sessionLocation.setItems(selectedLocationList);

            for (LocationModel lm : locationModelsList) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(lm.getLocationName());
                radioButton.setId(View.generateViewId());
                radioButton.setChecked(selectedItem);// Generate a unique ID for each radio button
                radioGroup.addView(radioButton);


            }
            // Add RadioGroup to the layout
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialogBinding.container.addView(radioGroup, layoutParams);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton selectedRadioButton = radioGroup.findViewById(i);
                    if (selectedRadioButton != null) {
                        // Do something with the selected RadioButton

                        String selectedObjectName = selectedRadioButton.getText().toString();

                        for (LocationModel slm : locationModelsList) {
                            if (slm.getLocationName().equalsIgnoreCase(selectedObjectName)) {
                                selectedRadioValue = slm.getLocationId();
                                Log.d("radio", "onCheckedChanged: ..........selected object is" + selectedRadioValue);
                            }

                        }

                        // Handle the selected object based on the name or any other property
                    }

                }
            });


        }


    }


    private void showDatePickerDialog() {
    }

    private void showConfarmationDilogParamedic(AlertDialog dialogParamedic, String msg, int imageId, String itemsData, LocationWiseBenefDialogBinding dialogBinding) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_yes);
        buttonMap.put(2, R.string.btn_no);
        DialogView dialog = new DialogView(this, R.string.dialog_title, msg, R.drawable.warning, buttonMap);
        dialog.setMessage(getResources().getString(R.string.remove_fcm_data));
        dialog.setOnDialogButtonClick(new OnDialogButtonClick() {
            @Override
            public void onDialogButtonClick(View view) {
                switch (view.getId()) {
                    case 1:
                        RequestData request = new RequestData(RequestType.USER_GATE, RequestName.MY_DATA_PARAMEDIC, Constants.MODULE_DATA_GET);
                        try {
                            request.setData(new JSONObject().put("FCM_LIST", "" + itemsData));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        CommiunicationTask commiunicationTask = new CommiunicationTask(MainActivity.this, request, R.string.retrieving_data, R.string.please_wait);
                        commiunicationTask.setCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(Message msg) {
                                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                                    String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                                    App.showMessageDisplayDialog(MainActivity.this, getResources().getString(R.string.network_error), R.drawable.error, Color.RED);
                                } else {
                                    final ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                                    if (response.getResponseCode().equalsIgnoreCase("00")) {
                                        App.showMessageDisplayDialog(MainActivity.this, response.getErrorCode() + "-" + response.getErrorDesc(), R.drawable.error, Color.RED);
                                    } else {
                                        App.getContext().getDB().deleteAllBenef(App.getContext().getUserInfo().getUserId());
                                        MHealthTask tsk = new MHealthTask(MainActivity.this, Task.MYDATA, R.string.saving_data, R.string.please_wait);
                                        tsk.setParam(response);
                                        tsk.setCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(Message msg) {
                                                dialogParamedic.dismiss();
                                                saveSessionLocalDb(dialogBinding);
                                                initDataLoad();
                                                loadSessionData();
                                                uploadSession();

                                            }
                                        });
                                        tsk.execute();
//                                        JSONObject jDataObj = response.getDataJson();
//                                        MyData myData = new MyData();
//                                        if (jDataObj.has(DBTable.HOUSEHOLD)) {
//                                            try {
//                                                myData.setHouseholdList(JSONParser.parseHouseholdList(jDataObj.getJSONArray(DBTable.HOUSEHOLD)));
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                        if (jDataObj.has(DBTable.BENEFICIARY)) {
//                                            try {
//                                                myData.setBeneficiaryList(JSONParser.parseBeneficiaryListJSON(jDataObj.getJSONArray(DBTable.BENEFICIARY)));
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//
//                                        }
//                                        MHealthTask tsk = new MHealthTask(MainActivity.this, Task.MY_DATA_PARAMEDIC, R.string.saving_data, R.string.please_wait);
//                                        tsk.setParam(jDataObj);
//                                        tsk.setCompleteListener(new OnCompleteListener() {
//                                            @Override
//                                            public void onComplete(Message msg) {
//                                                AppToast.showToast(MainActivity.this, "sdfsdfsdfsdf");
//                                            }
//                                        });

//                                        MHealthTask tsk = new MHealthTask(MainActivity.this, Task.MYDATA, R.string.saving_data, R.string.please_wait);
//                                        tsk.setParam(response);
//                                        tsk.setCompleteListener(new OnCompleteListener() {
//                                            @Override
//                                            public void onComplete(Message msg) {
//                                                App.showMessageDisplayDialog(MainActivity.this, getResources().getString(R.string.retrieve_successfull), R.drawable.information, Color.BLACK);
//                                            }
//                                        });
                                        // ModelHandler.getInstance(MainActivity.this).handleMydata(myData);
                                    }
                                }
                            }
                        });
                        commiunicationTask.execute();
                        break;
                }

            }
        });
        dialog.show();
    }

    private void saveSessionLocalDb(LocationWiseBenefDialogBinding dialogBinding) {
        App.getContext().getDB().saveSatelliteSession(sessionDate.getTimeInMillis(), App.getContext().getUserInfo().getUserId(), selectedRadioValue, dialogBinding.spFcmDialog.getSelectedItems(), dialogBinding.spLocationDialog.getSelectedItems(), satSessionId);

    }

    private void uploadSession() {
        ArrayList<SatelliteSessionModel> satelliteSessionModelArrayList = App.getContext().getDB().getSessionListUnsent(App.getContext().getUserInfo().getUserId(), "N");
        if (satelliteSessionModelArrayList.size() > 0) {
            if (SystemUtility.isConnectedToInternet(this)) {
                try {
                    RequestData request = new RequestData(RequestType.USER_GATE, RequestName.SAVE_SATELLITE_SESSTION, Constants.MODULE_BUNCH_PUSH);

                    JSONArray jsonArray = listToJsonArray(satelliteSessionModelArrayList);
                    request.getData().put("satellite_sesstion_list", jsonArray);
                    CommiunicationTask commiunicationTask = new CommiunicationTask(
                            this,
                            request,
                            R.string.uploading_data,
                            R.string.please_wait
                    );
                    commiunicationTask.setCompleteListener(msg -> {
                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (response.getResponseCode().equalsIgnoreCase("01")) {
                            Log.d("rcode", "uploadSession: response code is " + response.getResponseCode());
                            for (SatelliteSessionModel ssm : satelliteSessionModelArrayList) {
                                App.getContext().getDB().updateDataSentStatusForSatSession(ssm.getSatelliteSessionId(), "Y");
                            }
                        }
                    });
                    commiunicationTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                SystemUtility.openInternetSettingsActivity(this);
            }
        } else {

        }
    }

    public static JSONArray listToJsonArray(List<SatelliteSessionModel> sessionList) {
        JSONArray jsonArray = new JSONArray();
        for (SatelliteSessionModel singleObject : sessionList) {
            jsonArray.put(singleObject.toJson());
        }
        return jsonArray;
    }

    private void filterBeneficiaryListData(String filterText) {
        ArrayList<Beneficiary> filteredList = new ArrayList<>();
        for (Beneficiary beneficiary : beneficiaryList) {
            if (beneficiary.getBenefName().toLowerCase().contains(filterText.toLowerCase())) {
                filteredList.add(beneficiary);
            }
        }
        adapter.submitItems(filteredList);
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
                            MainActivity.this.finish();
                        } else {
                            if (SystemUtility.isConnectedToInternet(MainActivity.this)) {

                                //String outpath = App.getContext().getCommonDir(LoginPinActivity.this);

                                String outpath = App.getContext().getApkDir(MainActivity.this);
                                try {
                                    File apk = new File(appVersionHistory.getAppPathLocal());
                                    if (apk.exists()) {
                                        apk.delete();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                DownloadFileTask downloadFileTask = new DownloadFileTask(MainActivity.this, appVersionHistory.getAppPath(), outpath);
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

                                        MainActivity.this.finish();
                                    }
                                });
                                downloadFileTask.execute();
                            } else {
                                SystemUtility.openInternetSettingsActivity(MainActivity.this);
                            }
                        }
                        break;
                }
            }
        });
        exitDialog.showWebView();
    }


}