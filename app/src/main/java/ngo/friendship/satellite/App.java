package ngo.friendship.satellite;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import dagger.hilt.android.HiltAndroidApp;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.UserInfo;
import ngo.friendship.satellite.storage.SatelliteCareDatabaseManager;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.FileOperaion;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.DialogView;
import ngo.friendship.satellite.views.QuestionView;

@HiltAndroidApp
public class App extends Application {

    private boolean demo = false;
    private static App instence;
    private SatelliteCareDatabaseManager dbManager;
    private QuestionManager questionManager;
    private QuestionView currentQuestionView;
    private JSONObject questionAnswerJson;
    private Question currentQuestion;
    private static Dialog progressDialog;

    private UserInfo userInfo;

    private final String USER_GATE_API = "/usergate";

    private final String FCM_SCHDULE_API = "/fcm_schedule";

    private final String TRANSACTION_API = "/transactionapi";

    private final String STOCK_INVENTORY_API = "/stock_inventory";

    private final String FCM_REPORT_SERVICE_API = "/report_service";

    private boolean isAutoSyncServiceRunning = false;
    private boolean isDataSaveHistoryServiceRunning = false;

    public boolean isDataSaveHistoryServiceRunning() {
        return isDataSaveHistoryServiceRunning;
    }

    public void setDataSaveHistoryServiceRunning(boolean dataSaveHistoryServiceRunning) {
        isDataSaveHistoryServiceRunning = dataSaveHistoryServiceRunning;
    }

    private boolean isNotificationServiceRunning = false;

    public static long LAST_SUCCESS_REQUEST_TIME = -1;
    // RED OR GREEN
    public static int STATUS_COLOR = Color.RED;

    private boolean isActivityRunning = false;

    private Activity currentActivity = null;

    public Set<String> appListData = new HashSet<String>();

    private static ProgressDialog pgDialog;

    public Activity getCurrentActivity() {
        return currentActivity;
    }


    public void setAutoSyncServiceRunning(boolean isServiceRunning) {
        this.isAutoSyncServiceRunning = isServiceRunning;
    }

    public boolean isAutoSyncServiceRunning() {
        return isAutoSyncServiceRunning;
    }

    public void setNotificationServiceRunning(boolean isServiceRunning) {
        this.isNotificationServiceRunning = isServiceRunning;
    }

    public boolean isNotificationServiceRunning() {
        return isNotificationServiceRunning;
    }

    public static void showProgressBar(Activity _context) {
        progressDialog = new Dialog(_context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.full_screen_dialog);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressBar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void showProgressBarMessage(Activity _context, String title, String des) {
        progressDialog = new Dialog(_context);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.full_screen_dialog_with_text);
        TextView tvTitle = (TextView) progressDialog.findViewById(R.id.tv_title);
        TextView tvDes = (TextView) progressDialog.findViewById(R.id.tv_des);
        tvTitle.setText("" + title);
        tvDes.setText("" + des);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void hideProgressBarMessage() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void showProgressDialog(Activity _context) {
        hideProgressDialog();
        pgDialog = new ProgressDialog(_context);
        pgDialog.setMessage("Please, wait...");
        pgDialog.setCancelable(false);
        pgDialog.show();
    }

    public static void hideProgressDialog() {
        if (pgDialog != null && (pgDialog.isShowing())) {
            pgDialog.dismiss();
        }
    }

    public boolean isActivityRunning() {
        return isActivityRunning;
    }

    public boolean isApplicationInForeground(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        if (processInfos != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.processName.equals(ctx.getPackageName())) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String d : processInfo.pkgList) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String getApiPath(String requestType) {
        if (requestType.equals(RequestType.USER_GATE))
            return USER_GATE_API;
        else if (requestType.equals(RequestType.FCM_REPORT_SERVICE))
            return FCM_REPORT_SERVICE_API;
        else if (requestType.equals(RequestType.FCM_SCHEDULE))
            return FCM_SCHDULE_API;
        else if (requestType.equals(RequestType.STOCK_INVENTORY))
            return STOCK_INVENTORY_API;
        else if (requestType.equals(RequestType.TRANSACTION))
            return TRANSACTION_API;
        else return "";
    }

    /**
     * Gets the user gate api.
     *
     * @return the user gate api
     */
    public String getUserGateAPI() {
        return USER_GATE_API;
    }

    /**
     * Gets the FCM schedule api.
     *
     * @return the FCM schedule api
     */
    public String getFCMScheduleAPI() {
        return FCM_SCHDULE_API;
    }

    /**
     * Gets the transection api.
     *
     * @return the transection api
     */
    public String getTransectionAPI() {
        return TRANSACTION_API;
    }

    public String getStockInventoryAPI() {
        return STOCK_INVENTORY_API;
    }

    /**
     * Gets the FCM report service api.
     *
     * @return the FCM report service api
     */
    public String getFCMReportServiceAPI() {
        return FCM_REPORT_SERVICE_API;
    }

    /**
     * The app settings.
     */
    private AppSettings appSettings;

    /**
     * Gets the app settings.
     *
     * @return application settings object
     */
    public AppSettings getAppSettings() {
        return appSettings;
    }


    public void setQuestionAnswerJson(JSONObject questionAnswerJson) {
        this.questionAnswerJson = questionAnswerJson;
    }

    public JSONObject getQuestionAnswerJson() {
        return questionAnswerJson;
    }

    //public final String GATE_WAY_PATH="/mHealth_gateway";


    /**
     * Gets the API base path.
     *
     * @return mHealth API root path
     */
    public String getGateWayBasePath() {
        if (appSettings != null
                && !appSettings.getHostAddress().equalsIgnoreCase(""))
            return appSettings.getHostAddress() + "/api";
        else
            return null;
    }

    /**
     * Gets the alternative api base path.
     *
     * @return the alternative api base path
     */
    public String getAlternativeGateWayBasePath() {
        if (appSettings != null
                && !appSettings.getAlternateHostAddress().equalsIgnoreCase(""))
            return appSettings.getAlternateHostAddress() + "/api";
        else
            return null;
    }

    /**
     * Read application setting from shared preference.
     *
     * @param ctx is the application context
     */
    public void readAppSettings(Context ctx) {
        appSettings = new AppSettings();
        AppPreference.remove(ctx, KEY.MHEALTH_SERVER);
        appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, ""));
        if (appSettings.getHostAddress().equalsIgnoreCase("")) {
            if (isDemo()) {
                if (AppPreference.LIVE.equals(AppPreference.getAppMode(ctx))) {
                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "https://mhealth-demo.apps.friendship.ngo/mHealthEnt_gateway");
                } else if (AppPreference.TESTING.equals(AppPreference.getAppMode(ctx))) {
                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "https://mhealth-demo.apps.friendship.ngo/mHealthEnt_gateway");
                }

            } else {
                if (AppPreference.LIVE.equals(AppPreference.getAppMode(ctx))) {

                    /* for Devs */
                    //AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://163.53.150.205:8080/mHealthEnt_gateway");
                    //AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://192.168.16.205:8080/mHealthEnt_gateway");
                    // AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://192.168.8.26:8080/satelliteCare_gateway");
                    //AppPreference.putString(ctx, KEY.MHEALTH_SERVER,"https://devs.apps.friendship.ngo:8443/mHealthEnt_gateway/");
                    // AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://192.168.8.39:8080/satelliteCare_gateway");
//                      AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://192.168.8.39:8080/satelliteCare_gateway");
//                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://192.168.8.39:8080/satelliteCare_gateway");
//                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER,"https://devs.apps.friendship.ngo:8443/mHealthEnt_gateway/");


//                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "https://sc.apps.friendship.ngo/satelliteCare_gateway");
                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://devs.apps.friendship.ngo:8080/satelliteCare_gateway");
//                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER,"https://devs.apps.friendship.ngo:8443/satelliteCare_gateway/");

//                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://192.168.8.39:8080/satelliteCare_gateway");
//                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://devs.apps.friendship.ngo:8080/SatelliteCare_gateway");
//                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER,"https://devs.apps.friendship.ngo:8443/SatelliteCare_gateway/");

//                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://192.168.1.130:8080/satelliteCare_gateway");
//                   AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://devs.apps.friendship.ngo:8080/satelliteCare_gateway");
                    //AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://demo.mhealth.apps.friendship.ngo:8080/mHealthEnt_gateway" );
                    //AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "https://mhealth-demo.apps.friendship.ngo/mHealthEnt_gateway");


                } else if (AppPreference.TESTING.equals(AppPreference.getAppMode(ctx))) {
                    AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "https://p2.apps.friendship.ngo:8443/mHealthEnt_gateway");
                }
            }

            //appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "http://acf-mhealth.friendship.ngo/mHealthEnt_gateway"));
//            appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "https://mhealth-demo.apps.friendship.ngo/mHealthEnt_gateway"));
            //with https
            //appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "https://mhealth-demo.apps.friendship.ngo/mHealthEnt_gateway"));

//             appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "https://sc.apps.friendship.ngo/satelliteCare_gateway"));

//            appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "https://sc.apps.friendship.ngo/satelliteCare_gateway"));
//            appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "https://sc.apps.friendship.ngo/satelliteCare_gateway"));
            //appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "http://p2.friendship.ngo/mHealthEnt_gateway"));
            //appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "https://mhealth.apps.friendship.ngo/mHealthEnt_gateway"));
            //without https
//            appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "http://192.168.8.39:8080/satelliteCare_gateway"));
//            appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "https://sc.apps.friendship.ngo/satelliteCare_gateway"));
//            AppPreference.putString(ctx, KEY.MHEALTH_SERVER, "http://192.168.8.39:8080/satelliteCare_gateway");
              appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "http://devs.apps.friendship.ngo:8080/satelliteCare_gateway"));
//              appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "http://192.168.1.130:8080/satelliteCare_gateway"));
//            appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "http://192.168.1.130:8080/satelliteCare_gateway"));
            //with https
            //appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "https://devs.apps.friendship.ngo:8443/mHealthEnt_gateway"));
//            appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "http://192.168.1.130:8080/satelliteCare_gateway"));
            // appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "http://192.168.16.205:8080/mHealthEnt_gateway"));
            //appSettings.setHostAddress(AppPreference.getString(ctx, KEY.MHEALTH_SERVER, "https://prescriptec.urdt.net/mHealthEnt_gateway"));
        }

        appSettings.setAlternateHostAddress(AppPreference.getString(ctx, KEY.ALTERNATIVE_SERVER, ""));
        appSettings.setLanguage(AppPreference.getString(ctx, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE));
        appSettings.setUseNetworkProvidedTime(AppPreference.getBoolean(ctx, KEY.USE_NETWORK_PROVIDED_TIME, true));
        appSettings.setGPSStartOnAppStart(AppPreference.getBoolean(ctx, KEY.GPS_START, false));


        appSettings.setCcsAutometicFCMFollowupInterval(AppPreference.getInt(ctx, KEY.CCS_AUTOMETIC_FCM_FOLLOWUP_INTERVAL_VALUE, 0));
        appSettings.setCcsNumberOfMaximumFCMFollowup(AppPreference.getInt(ctx, KEY.CCS_NUMBER_OF_MAXIMUM_FOLLOWUP_VALUE, 0));

        appSettings.setEpiMinAge(AppPreference.getInt(ctx, KEY.EPI_MIN_AGE, 0));
        appSettings.setEpiMaxAge(AppPreference.getInt(ctx, KEY.EPI_MAX_AGE, 0));

        appSettings.setTtMinAge(AppPreference.getInt(ctx, KEY.TT_MIN_AGE, 0));
        appSettings.setTtMaxAge(AppPreference.getInt(ctx, KEY.TT_MAX_AGE, 0));

        appSettings.setScheduleDayAfterToday(AppPreference.getInt(ctx, KEY.SCHEDULE_DAY_AFTER_TODAY, 0));
        appSettings.setScheduleDayBeforToday(AppPreference.getInt(ctx, KEY.SCHEDULE_DAY_BEFORE_TODAY, 0));

        appSettings.setFollowUpDayAfterToday(AppPreference.getInt(ctx, KEY.FOLLOWUP_DAY_AFTER_TODAY, 0));
        appSettings.setFollowUpDayBeforToday(AppPreference.getInt(ctx, KEY.FOLLOWUP_DAY_BEFORE_TODAY, 0));
        appSettings.setFollowUpType(AppPreference.getString(ctx, KEY.FOLLOWUP_TYPE, ""));

        appSettings.setIsImageShow(AppPreference.getString(ctx, KEY.IS_IMAGE_SHOW, "NO"));

        appSettings.setFcmConfigration(AppPreference.getString(ctx, KEY.FCM_CONFIGURATION, ""));

        appSettings.setImmunizationMissGapDate(AppPreference.getInt(ctx, KEY.IMMUNIZATION_MISSING_GAP_DATE, 0));
        //appSettings.setIsSplashTextShow(AppPreference.getString(ctx, KEY.IS_SPLASH_TEXT_SHOW, "NO"));

        try {
            String confiData = AppPreference.getString(ctx, KEY.FCM_CONFIGURATION, "");
            if (confiData == null || confiData.equals("")) {
                appSettings.setIsSplashTextShow(AppPreference.getString(ctx, KEY.IS_SPLASH_TEXT_SHOW, "NO"));
            } else {
                JSONArray configArry = new JSONArray(confiData);
                String tempAddress = JSONParser.getFcmConfigValue(configArry, "GATEWAY_SERVER_RELOCATION", "url");
                String temAddressFromDate = JSONParser.getFcmConfigValue(configArry, "GATEWAY_SERVER_RELOCATION", "effective_date");
                if (tempAddress != null && temAddressFromDate != null) {
                    long date = Utility.getMillisecondFromDate(temAddressFromDate, Constants.DATE_FORMAT_YYYY_MM_DD);
                    long currentTimeMili = Calendar.getInstance().getTimeInMillis();
                    if (date < currentTimeMili) {
                        if (!appSettings.getHostAddress().equals(tempAddress)) {
                            AppPreference.putString(ctx, KEY.MHEALTH_SERVER, tempAddress);
                            appSettings.setHostAddress(tempAddress);
                        }
                    }
                }

                String isSplashTextShowValue = JSONParser.getFcmConfigValue(configArry, "SPLASH2", "splash2_prompt");
                appSettings.setIsSplashTextShow(AppPreference.getString(ctx, KEY.IS_SPLASH_TEXT_SHOW, isSplashTextShowValue));
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            String cloneInfoStr = AppPreference.getString(ctx, KEY.CLONE_INFO, "");
            if (cloneInfoStr.length() > 0) {
                JSONObject clone = new JSONObject(cloneInfoStr);

                appSettings.setCloneInfo(clone);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public QuestionView getCurrentQuestionView() {
        return currentQuestionView;
    }

    /**
     * Sets the current question view.
     *
     * @param currentQuestionView the new current question view
     */
    public void setCurrentQuestionView(QuestionView currentQuestionView) {
        this.currentQuestionView = currentQuestionView;
    }

    /**
     * Gets the current question.
     *
     * @return the current question
     */
    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    /**
     * Sets the current question.
     *
     * @param currentQuestion the new current question
     */
    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    /**
     * Gets the question manager.
     *
     * @return the question manager
     */
    public QuestionManager getQuestionManager() {
        return questionManager;
    }

    /**
     * Sets the question manager.
     *
     * @param questionManager the new question manager
     */
    public void setQuestionManager(QuestionManager questionManager) {
        this.questionManager = questionManager;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
    }

    /**
     * Gets the context.
     *
     * @return single instance of this class
     */
    public synchronized static App getContext() {
        if (instence == null) {
            instence = new App();
        }
        return instence;
    }

    /**
     * Checks if is ext sd card present.
     *
     * @return Whether External SD card is present or not
     */
    public boolean isExtSDCardPresent() {

        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)
                && !Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    /**
     * Gets the app data dir.
     *
     * @return application data directory root path
     */
    public String getAppDataDir(Context context) {
        String appmode = AppPreference.getString(context, KEY.APPMODE, AppPreference.LIVE, AppPreference.COMMON);
        if (isExtSDCardPresent()) {
            //String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/.mhealth.android.data." + appmode;
            String filePath = Constants.MHEALTH_APP_FOLDER_PATH + appmode;
            File file = new File(filePath);
            if (!file.exists())
                file.mkdirs();
            return filePath;
        } else
            return null;
    }


    /**
     * Gets the questionnaire json dir.
     *
     * @return Directory path where the interview JSON is saved
     */
    public String getQuestionnaireJSONDir(Context context) {
        String dir = getAppDataDir(context);
        if (dir == null)
            return null;

        else {
            dir = dir + "/.questionnaire.json";
            File file = new File(dir);
            if (!file.exists())
                file.mkdirs();
            return dir;
        }
    }

    public String getLogDir(Context context) {
        String dir = getAppDataDir(context);
        if (dir == null)
            return null;

        else {
            dir = dir + "/.log.file";
            File file = new File(dir);
            if (!file.exists())
                file.mkdirs();

            return dir;
        }
    }

    /**
     * Gets the apk  dir.
     *
     * @return return apk dir
     */
    public String getCommonDir(Context context) {
        String dir = getAppDataDir(context);
        if (dir == null)
            return null;
        else {
            dir = dir + "/.common.res";
            File file = new File(dir);
            if (!file.exists())
                file.mkdirs();
            return dir;
        }
    }

    public String getApkDir(Context context) {
        return getCommonDir(context) + "/mHealth.apk";
    }


    public boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * Gets the beneficiary image dir.
     *
     * @return beneficiary image disrectory
     */
    public String getBeneficiaryImageDir(Context context) {
        String dir = getAppDataDir(context);
        if (dir == null)
            return null;

        else {

            dir = dir + "/.benef.img";
            File file = new File(dir);
            if (!file.exists())
                file.mkdirs();

            return dir;
        }
    }

    public String getAlgorithmAssetDir(Context context) {
        String dir = getAppDataDir(context);
        if (dir == null)
            return null;

        else {

            dir = dir + "/.algo.res";
            File file = new File(dir);
            if (!file.exists())
                file.mkdirs();
            return dir;
        }
    }

    public void copyAlgorithImageFromDrawable(Context context) {
        try {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.diabetics_screening_partner);
            File file = new File(App.getContext().getAlgorithmAssetDir(context), "DIABETES_SCREENING_partner.jpg");
            FileOutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Gets the voice file dir.
     *
     * @return the voice file dir
     */
    public String getVoiceFileDir(Context context) {
        String dir = getAppDataDir(context);
        if (dir == null)
            return null;

        else {

            dir = dir + "/.voice.file";
            File file = new File(dir);
            if (!file.exists())
                file.mkdirs();
            return dir;
        }
    }


    /**
     * Sets the DB manager.
     *
     * @param dbManager the new DB manager
     */
    public void setDBManager(SatelliteCareDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }


    public void setAppSettings(AppSettings appSettings) {
        this.appSettings = appSettings;
    }

    /**
     * Gets the DB manager.
     *
     * @return the DB manager
     */
    public synchronized SatelliteCareDatabaseManager getDB() {
        return dbManager;
    }

    /* Destroy app.
     */
    public static void destroyApp() {

        if (App.getContext().getDB() != null) {
            App.getContext().getDB().closeDB();
        }
        instence = null;
    }

    /**
     * Load application data.
     *
     * @param ctx is the application context
     * @return true if database initialization successful. false otherwise
     */
    public static boolean loadApplicationData(Context ctx) {
        if (getContext().getAppSettings() == null) {
            App.getContext().readAppSettings(ctx);
        }
        if (getContext().getDB() == null) {
            SatelliteCareDatabaseManager dbManager = new SatelliteCareDatabaseManager(ctx);
            if (dbManager.initializeDatabase()) {
                App.getContext().setDBManager(dbManager);
            }
        }

        if (getContext().getUserInfo() == null) {
            App.getContext().readUserInfo(ctx);
        }


        return getContext().getAppSettings() != null
                && getContext().getDB() != null
                && getContext().getUserInfo() != null;

    }


    public void readUserInfo(Context ctx) {
        String userCode = AppPreference.getString(ctx, Column.USER_LOGIN_ID, "");
        String userPassword = AppPreference.getString(ctx, KEY.PASSWORD, "");
        long state = AppPreference.getLong(ctx, Column.STATE, -1);
        long orgId = AppPreference.getLong(ctx, Column.ORG_ID, -1);
        UserInfo userInfo = getContext().getDB().getUserInfo(userCode, userPassword, orgId, state);
        App.getContext().setUserInfo(userInfo == null ? new UserInfo() : userInfo);
    }


    public static void showMessageDisplayDialog(Activity ctx, String msg, int imageId, int messageColor) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView dialog = new DialogView(ctx, R.string.dialog_title, msg, messageColor, imageId, buttonMap);
        dialog.show();
    }

    public void onStartActivity(Activity activity) {
        if (appListData.size() < 10) {
            appListData.add(activity.getLocalClassName());
        }

        this.currentActivity = activity;
        if (isActivityRunning) {
            this.isActivityRunning = isActivityRunning;
            this.currentActivity = activity;
        } else {
            if (currentActivity != null && currentActivity.getClass() == activity.getClass()) {
                this.isActivityRunning = isActivityRunning;
                this.currentActivity = null;
            }
        }

//        try {
//            String path = File.separator + App.getContext().getUserInfo().getHeaderSmallLogoPathDir();
//            String imgPath = App.getContext().getCommonDir(activity) + path;
//            if (FileOperaion.isExist(imgPath)) {
//                ((ImageView) activity.findViewById(R.id.iv_logo)).setImageBitmap(FileOperaion.decodeImageFile(imgPath, 100));
//            }
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }


        if (isDemo()) {
            setDemoBanner(activity);
        }
        setTraningBanner(activity);


    }

    //    public void onStopActivity(Activity activity) {
//        if (isActivityRunning) {
//            this.isActivityRunning = isActivityRunning;
//            this.currentActivity = activity;
//        } else {
//            if (currentActivity != null && currentActivity.getClass() == activity.getClass()) {
//                this.isActivityRunning = isActivityRunning;
//                this.currentActivity = null;
//            }
//        }
//    }
    public void setDemoBanner(Activity activity) {
        try {
            TextView tv = activity.findViewById(R.id.txt_traning);
            tv.setVisibility(View.VISIBLE);
            tv.setText("DEMO VERSION");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setTraningBanner(Activity activity) {
        if (AppPreference.getAppMode(activity).equals(AppPreference.TESTING)) {
            try {
                activity.findViewById(R.id.txt_traning).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadCommonResource(Context context) {
        if (!SystemUtility.isConnectedToInternet(context)) return;

        String url = userInfo.getHeaderSmallLogoPath();
        File file = new File(getCommonDir(context) + File.separator + userInfo.getHeaderSmallLogoPathDir());
        if (!file.exists() && url != null && !url.isEmpty()) {
            MHealthTask task = new MHealthTask(context, Task.DOWNLOAD_FILE);
            task.setParam(url.trim(), file.getAbsolutePath());
            task.execute();
        }

        url = userInfo.getLoginImagePathMobile();
        file = new File(getCommonDir(context) + File.separator + userInfo.getLoginImagePathMobileDir());
        if (!file.exists() && url != null && !url.isEmpty()) {
            MHealthTask task = new MHealthTask(context, Task.DOWNLOAD_FILE);
            task.setParam(url.trim(), file.getAbsolutePath());
            task.execute();
        }

        url = userInfo.getTitleLogoPathMobile();
        file = new File(getCommonDir(context) + File.separator + userInfo.getTitleLogoPathMobileDir());
        if (!file.exists() && url != null && !url.isEmpty()) {
            MHealthTask task = new MHealthTask(context, Task.DOWNLOAD_FILE);
            task.setParam(url.trim(), file.getAbsolutePath());
            task.execute();
        }
    }


    public boolean isDemo() {
        return demo;
    }
}

