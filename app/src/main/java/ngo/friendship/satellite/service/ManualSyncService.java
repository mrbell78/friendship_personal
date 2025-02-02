package ngo.friendship.satellite.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.SendMedicineReceivedListTask;
import ngo.friendship.satellite.asynctask.SendMedicineSellListTask;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.asynctask.UploadInterviewTask;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.constants.RequisitionStatus;
import ngo.friendship.satellite.constants.StockAdjustmentRequestStatus;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDataSentListener;
import ngo.friendship.satellite.interfaces.OnInterviewUploadListener;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.AppVersionHistory;
import ngo.friendship.satellite.model.Household;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.MedicineSellInfo;
import ngo.friendship.satellite.model.MyData;
import ngo.friendship.satellite.model.NotificationItem;
import ngo.friendship.satellite.model.RequisitionInfo;
import ngo.friendship.satellite.model.SavedInterviewInfo;
import ngo.friendship.satellite.model.StockAdjustmentInfo;
import ngo.friendship.satellite.ui.NotificationShowActivity;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.FileOperaion;
import ngo.friendship.satellite.utility.ModelHandler;
import ngo.friendship.satellite.utility.ModelProvider;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.Utility;


public class ManualSyncService extends Service {
    private static BootReceiver panicReceiver;

    public static long MAX_TRY = 1;
    public static StringBuilder sbMessageToBePrint;
    public static ArrayList<NotificationItem> notificationItemList;
    private String manualSync = "";

    @Override
    public void onCreate() {
        super.onCreate();
        sbMessageToBePrint = new StringBuilder();
        notificationItemList = new ArrayList<>();
        registerScreenReceiver();
    }

    /**
     * Start sync.
     */
    private void startSync() {
        sbMessageToBePrint = new StringBuilder();
        String type = "Sync Start";
        String message = "Sync started at " + Utility.getDateTimeFromMillisecond(Calendar.getInstance().getTimeInMillis(), Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
        showNotification(type, message, R.drawable.ic_satellite_logo_vertical, true, false);
        addNotification(message, type);
        if (App.loadApplicationData(this)) {

            if (SystemUtility.isConnectedToInternet(this)) {

                checkRequisitionStatus();
                // generateNotificationForDoctorFeedback();
                getMyData();
            } else {
                type = "Sync Stop";
                message = "No internet connection";
                showNotification(type, message, R.drawable.ic_satellite_logo_vertical, false, false);
                addNotification(message, type);
                endSync();
            }
        } else {
            type = "Sync Stop";
            message = "Can't load application data";
            showNotification(type, message, R.drawable.ic_satellite_logo_vertical, false, false);
            addNotification(message, type);
            endSync();
        }
    }

    private void endSync() {
        manualSync = "";

        String type = "Sync End";
        String message = "Sync ended at " + Utility.getDateTimeFromMillisecond(Calendar.getInstance().getTimeInMillis(), Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
        showNotification(type, message, R.drawable.ic_satellite_logo_vertical, false, false);
        addNotification(message, type);
        App.getContext().setAutoSyncServiceRunning(false);
        ManualSyncService.this.stopSelf();
        sbMessageToBePrint.setLength(0);
        sbMessageToBePrint = new StringBuilder();


    }


    public void writeLogAAA() {

        final String type = "Write Log";
        final int icon = R.drawable.sync;
        String msg = "Write log from system log";
        showNotification(type, msg, icon, true, false);
        addNotification(msg, type);

        String filePath = App.getContext().getLogDir(this) + "/mHealthLog.txt";
        String data = FileOperaion.readLogs();
        FileOperaion.writeData(data, filePath);

    }


    int checkRequisitionStatusCount = 0;

    //  Check last requisition status.
    private void checkRequisitionStatus() {
        final String type = "Requisition Status";
        final int icon = R.drawable.uploading;

        checkRequisitionStatusCount++;
        final String reqNo = App.getContext().getDB().getReqNumber(RequisitionStatus.Initiated.toString());
        if (reqNo != null && reqNo.trim().length() > 0) {
            String msg = "Start check last requisition status:" + reqNo;
            showNotification(type, msg, icon, true, false);
            addNotification(checkRequisitionStatusCount + ". " + msg, type);


            RequestData request = new RequestData(RequestType.STOCK_INVENTORY, RequestName.MEDICINE_REQUISITION_STATUS, Constants.MODULE_AUTO_SYNC);
            try {
                request.getParam1().put(Column.REQ_NO, reqNo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CommiunicationTask commiunicationTask = new CommiunicationTask(this, request);
            commiunicationTask.setCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(Message msg) {
                    boolean isError = false;
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        String errorMessage = msg.getData().getString(TaskKey.ERROR_MSG);
                        addNotification(errorMessage, type);
                        isError = true;
                    } else {
                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (response.getResponseCode().equalsIgnoreCase("00")) {
                            String errorMessage = response.getErrorCode() + " " + response.getErrorDesc();
                            addNotification(errorMessage, type);
                            isError = true;
                        } else {
                            Double tpd = JSONParser.getDouble(response.getDataJson(), "TOTAL_PRICE_DISPATCH");
                            String reqStatus = JSONParser.getString(response.getDataJson(), Column.REQ_STATUS);
                            JSONArray requisitionDetail = JSONParser.getJsonArray(response.getDataJson(), "medicine_requisition_detail");
                            App.getContext().getDB().updateRequisition(reqNo, reqStatus, tpd, requisitionDetail);
                            addNotification("Successfully update requisition status-" + reqNo + ":" + reqStatus, type);
                            isError = false;
                        }
                    }

                    if (isError && checkRequisitionStatusCount < MAX_TRY) {
                        checkRequisitionStatus();
                    } else {
                        uploadHouseHoldBasicData();
                    }
                }
            });
            commiunicationTask.execute();
        } else {
            uploadHouseHoldBasicData();
        }

    }

    long uploadHouseHoldBasicData = 0;

    private void uploadHouseHoldBasicData() {
        uploadHouseHoldBasicData++;
        final ArrayList<Household> households = App.getContext().getDB().getHouseholdBasicDataListUnsend();
        if (households != null && households.size() > 0) {
            final String type = "Household Basic Data";
            String msg = "Start uploading household basic data:" + households.size();
            showNotification(type, msg, R.drawable.uploading, true, false);
            addNotification(uploadHouseHoldBasicData + ". " + msg, type);

            RequestData request = new RequestData(RequestType.TRANSACTION, RequestName.HOUSEHOLD_DATA_ENTRY, Constants.MODULE_AUTO_SYNC);
            JSONArray jHouseholdListArr = new JSONArray();
            try {
                for (Household householdInfo : households) {
                    if (householdInfo.getSent() == 1) continue;
                    jHouseholdListArr.put(householdInfo.toJson());
                }
                request.getData().put("householdList", jHouseholdListArr);
            } catch (Exception e) {
                e.printStackTrace();
            }

            CommiunicationTask commiunicationTask = new CommiunicationTask(this, request);
            commiunicationTask.setCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(Message msg) {
                    boolean isError = false;
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        String errorMessage = msg.getData().getString(TaskKey.ERROR_MSG);
                        addNotification(errorMessage, type);
                        isError = true;
                    } else {


                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (response.getResponseCode().equalsIgnoreCase("00")) {
                            String errorMessage = response.getErrorCode() + " " + response.getErrorDesc();
                            addNotification(errorMessage, type);
                            isError = true;

                        } else {
                            App.getContext().getDB().updateHouseholdBasicDataSent();
                            addNotification("Successfully Upload household basic data", type);
                            isError = false;
                        }


                    }

                    if (isError && uploadHouseHoldBasicData < MAX_TRY) {
                        uploadHouseHoldBasicData();
                    } else {
                        uploadInterview();
                    }
                }
            });
            commiunicationTask.execute();
        } else {
            uploadInterview();
        }
    }


    private long uploadInterviewCount = 0;

    private void uploadInterview() {
        uploadInterviewCount++;
        ArrayList<SavedInterviewInfo> interviewList = App.getContext().getDB().getInterviewList("N", App.getContext().getAppSettings().getLanguage(), App.getContext().getUserInfo().getUserCode());
        if (interviewList.size() > 0) {
            final String type = "Seved Interview";
            String msg = " Start uploading saved interview :" + interviewList.size();
            showNotification(type, msg, R.drawable.uploading, true, false);
            addNotification(uploadInterviewCount + ". " + msg, type);

            UploadInterviewTask uit = new UploadInterviewTask(this, interviewList, Constants.MODULE_AUTO_SYNC);
            uit.setOnInterviewUploadListener(new OnInterviewUploadListener() {

                @Override
                public void onInterviewUploadFinished(String message) {
                    addNotification(message, type);
                    if (message.contains("color='red'") && uploadInterviewCount < MAX_TRY) {
                        uploadInterview();
                    } else {
                        uploadRequisition();
                    }
                }
            });
            uit.execute();
        } else {
            uploadRequisition();
        }
    }


    private long uploadRequisition = 0;

    /**
     * Upload unsent requisition.
     */
    private void uploadRequisition() {
        uploadRequisition++;


        final RequisitionInfo requInfo = App.getContext().getDB().getUnSentRequisition();
        if (requInfo != null) {
            final String type = "Saved requisition";
            String msg = "Start uploading requisition";
            showNotification(type, msg, R.drawable.uploading, true, false);
            addNotification(uploadRequisition + ". " + msg, type);

            ArrayList<MedicineInfo> medList = App.getContext().getDB().getRequsitionData(requInfo.getRequisitionId());

            RequestData request = new RequestData(RequestType.STOCK_INVENTORY, RequestName.MEDICINE_REQUISITION, Constants.MODULE_AUTO_SYNC);
            request.setData(JSONCreateor.createMedicineRequisitionJSON(medList));

            CommiunicationTask commiunicationTask = new CommiunicationTask(this, request);
            commiunicationTask.setCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(Message msg) {
                    boolean isError = false;
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        String errorMessage = msg.getData().getString(TaskKey.ERROR_MSG);
                        addNotification(errorMessage, type);
                        isError = true;
                    } else {
                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (response.getResponseCode().equalsIgnoreCase("00")) {
                            if (response.getErrorCode().equals("0308")) {
                                App.getContext().getDB().removeRequisitionRequest(requInfo.getRequisitionId());
                            }
                            String errorMessage = response.getErrorCode() + " " + response.getErrorDesc();
                            addNotification(errorMessage, type);
                            isError = true;
                        } else {
                            Long reqNo = JSONParser.getLong(response.getDataJson(), Column.REQ_NO);
                            App.getContext().getDB().updateRequisitionMasterTable(requInfo.getRequisitionId(), reqNo, RequisitionStatus.Accepted.toString(), -1);
                            addNotification("Successfully Upload requisition:" + reqNo, type);
                            isError = false;
                        }

                    }

                    if (isError && uploadRequisition < MAX_TRY) {
                        uploadRequisition();
                    } else {
                        uploadStockAdjustmentRequest();
                    }
                }
            });
            commiunicationTask.execute();
        } else {
            uploadStockAdjustmentRequest();
        }
    }

    private long uploadStockAdjustmentRequestCount = 0;

    /**
     * Upload stock adjustment request.
     */
    private void uploadStockAdjustmentRequest() {
        uploadStockAdjustmentRequestCount++;
        // Get adjustment data from database
        final StockAdjustmentInfo stockAdjustmentInfo = App.getContext().getDB().getStockAdjustmentInfo();
        if (stockAdjustmentInfo != null && stockAdjustmentInfo.getRequestNumber() != null) {
            final String type = "Stock Adjustment Request";
            String msg = "Start uploading stock adjustment request";
            showNotification(type, msg, R.drawable.uploading, true, false);
            addNotification(uploadStockAdjustmentRequestCount + ". " + msg, type);

            RequestData request = new RequestData(RequestType.STOCK_INVENTORY, RequestName.FCM_STOCK_ADJUST, Constants.MODULE_AUTO_SYNC);
            request.setData(JSONCreateor.createMedicineAdjutmentRequestJSON(stockAdjustmentInfo.getMedicineList()));
            CommiunicationTask commiunicationTask = new CommiunicationTask(this, request);
            commiunicationTask.setCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(Message msg) {
                    boolean isError = false;
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        String errorMessage = msg.getData().getString(TaskKey.ERROR_MSG);
                        addNotification(errorMessage, type);
                        isError = true;
                    } else {
                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (response.getResponseCode().equalsIgnoreCase("00")) {
                            String errorMessage = response.getErrorCode() + " " + response.getErrorDesc();
                            addNotification(errorMessage, type);
                            isError = true;
                        } else {
                            String reqNo = JSONParser.getString(response.getDataJson(), Column.REQUEST_NUMBER);
                            App.getContext().getDB().updateStockAdjustment(0, stockAdjustmentInfo.getAdjustmentId(), reqNo, StockAdjustmentRequestStatus.OPEN, null);
                            addNotification("Successfully upload stock adjustment request: " + reqNo, type);
                        }
                    }
                    if (isError && uploadStockAdjustmentRequestCount < MAX_TRY) {
                        uploadStockAdjustmentRequest();
                    } else {
                        uploadDirectMedicineSell();
                    }
                }
            });
            commiunicationTask.execute();

        } else {
            uploadDirectMedicineSell();
        }
    }

    private long uploadDirectMedicineSellCount = 0;

    /**
     * Upload unsent direct medicine sale.
     */
    private void uploadDirectMedicineSell() {
        ArrayList<MedicineSellInfo> medicineSellList = App.getContext().getDB().getMedicineConsumptionList();

        //Log.e("In Service", medicineSellList.size()+" " );
        if (medicineSellList.size() > 0) {
            final String type = "Direct Medicine Sale";
            String msg = "Start uploading direct medicine sale:" + medicineSellList.size();
            showNotification(type, msg, R.drawable.uploading, true, false);
            addNotification(uploadDirectMedicineSellCount + ". " + msg, type);

            SendMedicineSellListTask smslt = new SendMedicineSellListTask(this, medicineSellList, Constants.MODULE_AUTO_SYNC);
            smslt.execute();
            smslt.setOnDataSentListener(new OnDataSentListener() {

                @Override
                public void onDataSendingFinished(ResponseData response) {
                    if (response == null && uploadDirectMedicineSellCount < MAX_TRY) {
                        addNotification("Error occured in uploading direct medicine sale", type);
                        uploadDirectMedicineSell();

                    } else {
                        addNotification("Successfully upload direct medicine sale", type);
                        uploadUnsentMedicineReceived();
                    }
                }
            });
        } else {
            uploadUnsentMedicineReceived();
        }

    }

    private long uploadUnsentMedicineReceived = 0;

    /**
     * Send Unsent medicine receive request.
     */
    private void uploadUnsentMedicineReceived() {
        uploadUnsentMedicineReceived++;
        ArrayList<RequisitionInfo> unsentReceiveList = App.getContext().getDB().getUnsentMedicneReceivedList();
        if (unsentReceiveList.size() > 0) {
            final String type = "Medicine Receive";
            String msg = "Start uploading medicine receive:" + unsentReceiveList.size();
            showNotification(type, msg, R.drawable.uploading, true, false);
            addNotification(uploadUnsentMedicineReceived + ". " + msg, type);


            SendMedicineReceivedListTask smrlt = new SendMedicineReceivedListTask(this, unsentReceiveList, Constants.MODULE_AUTO_SYNC);
            smrlt.setOnDataSentListener(new OnDataSentListener() {

                @Override
                public void onDataSendingFinished(ResponseData webResponseInfo) {
                    if (webResponseInfo == null && uploadUnsentMedicineReceived < MAX_TRY) {
                        addNotification("Error occured in uploading medicine receive", type);
                        uploadUnsentMedicineReceived();

                    } else {
                        addNotification("Successfully upload medicine receive", type);
                        uploadAppVersionHistory();
                    }
                }
            });
            smrlt.execute();
        } else {
            uploadAppVersionHistory();
        }
    }

    /**
     * Upload unsent event schedule list.
     */
    //	private void uploadUnsentEventScheduleList()
    //	{
    //		ArrayList<UserScheduleInfo> schedList = MHealthApp.getContext().getDBManager().getUnsentEventScheduleList(MHealthApp.getContext().getAppSettings().getLanguage());
    //
    //		if(schedList.size() > 0)
    //		{
    //			SendAttandedScheduleTask smsdst = new SendAttandedScheduleTask(schedList, this);
    //			smsdst.setOnDataSentListener(new OnDataSentListener() {
    //
    //				@Override
    //				public void onDataSendingFinished(Response webResponseInfo) {
    //					if(webResponseInfo == null || webResponseInfo.getResponseCode().equalsIgnoreCase("00"))
    //						isError = true;
    //					// uploadAppVersionHistory();
    //				}
    //			});
    //			smsdst.execute();
    //		}
    //		else
    //		{
    //			//	uploadAppVersionHistory();
    //		}
    //	}

    /**
     * Upload unsent app version history
     */

    private long uploadAppVersionHistoryCount = 0;

    private void uploadAppVersionHistory() {
        uploadAppVersionHistoryCount++;
        Log.e("uploadAppVersionHistory", "Start upload AppVersionHistory");
        final AppVersionHistory appVersionHistory = App.getContext().getDB().getVersionHistory(Utility.getAppVersionCode(this), App.getContext().getAppSettings().getLanguage());
        if (appVersionHistory != null && !appVersionHistory.getInstallFlag().equalsIgnoreCase(AppVersionHistory.FLAG_RECEIVED) && appVersionHistory.getSendFlag() == 0) {
            final String type = "App Version History";
            String msg = "Start uploading app version history";
            showNotification(type, msg, R.drawable.uploading, true, false);
            addNotification(uploadAppVersionHistoryCount + ". " + msg, type);


            RequestData request = new RequestData(RequestType.USER_GATE, RequestName.UPDATE_APP_VERSION_HISTORY, Constants.MODULE_AUTO_SYNC);
            try {
                request.setData(JSONCreateor.createAppVersionHistoryJson(appVersionHistory));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            CommiunicationTask commiunicationTask = new CommiunicationTask(this, request);
            commiunicationTask.setCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(Message msg) {
                    boolean isError = false;
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        String errorMessage = msg.getData().getString(TaskKey.ERROR_MSG);
                        addNotification(errorMessage, type);
                        isError = true;
                    } else {

                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (response.getResponseCode().equalsIgnoreCase("00")) {
                            String errorMessage = response.getErrorCode() + " " + response.getErrorDesc();
                            addNotification(errorMessage, type);
                            isError = true;
                        } else {
                            Log.e("uploadAppVersionHistory", "Successfully update");
                            App.getContext().getDB().updateAppVersionHistorySendFlag(appVersionHistory.getVersionId());
                            String succesMessage = "Application : " + appVersionHistory.getVersionNumber() + " " + appVersionHistory.getInstallFlag() + " " + Utility.getDateFromMillisecond(appVersionHistory.getOpenDate(), Constants.DATE_FORMAT_DD_MM_YY);
                            addNotification(succesMessage, type);
                            isError = false;
                        }
                    }

                    if (isError && uploadAppVersionHistoryCount < MAX_TRY) {
                        uploadAppVersionHistory();
                    } else {
                        uploadInterviewSentStatus();
                    }
                }


            });
            commiunicationTask.execute();

        } else {
            uploadInterviewSentStatus();
        }
    }


    private long interviewSentStatusCount = 0;

    private void uploadInterviewSentStatus() {

        String limit = "15";
        try {
            limit = Utility.getFcmConfigurationValue(this, "Auto_Sync", "interview.send.status.sharing.limit", "15");
        } catch (Exception e) {
            e.printStackTrace();
        }


        String lastSentdate = AppPreference.getString(this, KEY.LAST_SERVICE_STATUS_SENT_DATE, "");
        final String currentdate = Utility.getDateFromMillisecond(Calendar.getInstance().getTimeInMillis(), Constants.DATE_FORMAT_YYYY_MM_DD);
        interviewSentStatusCount++;
        if (isFource || !currentdate.equals(lastSentdate)) {
            Log.e("uploadServiceSentStatus", "Start Upload Interview Sent Status");

            final JSONObject data = new JSONObject();
            try {
                data.put("STATUS", App.getContext().getDB().getServicelastSentStatus(currentdate, limit));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String type = "Interview Sent Status";
            String msg = "Start Upload Interview Sent Status";
            showNotification(type, msg, R.drawable.uploading, true, false);
            addNotification(interviewSentStatusCount + ". " + msg, type);
            RequestData request = new RequestData(RequestType.USER_GATE, RequestName.INTERVIEW_SENT_STATUS, Constants.MODULE_AUTO_SYNC);
            request.setData(data);
            CommiunicationTask commiunicationTask = new CommiunicationTask(this, request);
            commiunicationTask.setCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(Message msg) {
                    boolean isError = false;
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        String errorMessage = msg.getData().getString(TaskKey.ERROR_MSG);
                        addNotification(errorMessage, type);
                        isError = true;
                    } else {

                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (response.getResponseCode().equalsIgnoreCase("00")) {
                            String errorMessage = response.getErrorCode() + " " + response.getErrorDesc();
                            addNotification(errorMessage, type);
                            isError = true;
                        } else {
                            Log.e("uploadServiceSentStatus", "Sent successfully");
                            AppPreference.putString(ManualSyncService.this, KEY.LAST_SERVICE_STATUS_SENT_DATE, currentdate);
                            isError = false;
                        }
                    }
                    if (isError && uploadAppVersionHistoryCount < MAX_TRY) {
                        uploadAppVersionHistory();
                    } else {
                        downloadMydata();
                    }
                }


            });
            commiunicationTask.execute();

        } else {
            downloadMydata();
        }
    }


    /**
     * The ccs date changed.
     */
    //	boolean ccsDateChanged = false;

    private long downloadMydataCount = 0;

    private void downloadMydata() {
        downloadMydataCount++;
        final String type = "My Data";
        String msg = "Start downloading my data";
        showNotification(type, msg, R.drawable.downloading, true, false);
        addNotification(downloadMydataCount + ". " + msg, type);


        RequestData request = new RequestData(RequestType.USER_GATE, RequestName.MY_DATA_SYNC, Constants.MODULE_AUTO_SYNC);
        request.setParam1(Utility.getTableRef(null, this));
        CommiunicationTask commiunicationTask = new CommiunicationTask(this, request);
        commiunicationTask.setCompleteListener(new OnCompleteListener() {

            @Override
            public void onComplete(Message msg) {
                boolean isError = false;
                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                    String errorMessage = msg.getData().getString(TaskKey.ERROR_MSG);
                    addNotification(errorMessage, type);
                    isError = true;
                } else {
                    ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                    if (response.getResponseCode().equalsIgnoreCase("00")) {
                        String errorMessage = response.getErrorCode() + " " + response.getErrorDesc();
                        addNotification(errorMessage, type);
                        isError = true;
                    } else {
                        try {
                            MyData myData = ModelProvider.getMyData(ManualSyncService.this, new MyData(), response);
                            ModelHandler.getInstance(ManualSyncService.this).handleMydata(myData);
                            String errorMessage = "My data successfully Save";
                            addNotification(errorMessage, type);
                            if (JSONParser.getLongNullAllow(response.getParamJson(), "NEED_SAME_REQ") == 1) {
                                downloadMydata();
                                return;
                            }
                            isError = false;
                        } catch (MhealthException e) {
                            String errorMessage = e.getMessage();
                            addNotification(errorMessage, type);
                            isError = true;
                        }
                    }
                }

                if (isError && downloadMydataCount < MAX_TRY) {
                    downloadMydata();
                } else {
                    downloadAppVersionHistory();
                }
            }
        });
        commiunicationTask.execute();
    }


    long downloadAppVersionHistory = 0;

    /**
     * Download undone schedule list.
     */
    private void downloadAppVersionHistory() {
        downloadAppVersionHistory++;
        final String type = "App Version";
        String msg = "Start checking updated app version";
        showNotification(type, msg, R.drawable.downloading, true, false);
        addNotification(downloadAppVersionHistory + ". " + msg, type);

        RequestData request = new RequestData(RequestType.USER_GATE, RequestName.APP_VERSION_HISTORY, Constants.MODULE_AUTO_SYNC);
        HashMap<String, String> refTable = new HashMap<String, String>();
        refTable.put(KEY.APP_VERSION_NUMBER, " ");
        request.setParam1(Utility.getTableRef(refTable, this));
        CommiunicationTask commiunicationTask = new CommiunicationTask(this, request);
        commiunicationTask.setCompleteListener(new OnCompleteListener() {

            @Override
            public void onComplete(Message msg) {
                boolean isError = false;
                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                    String errorMessage = msg.getData().getString(TaskKey.ERROR_MSG);
                    addNotification(errorMessage, type);
                    isError = true;
                } else {
                    ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                    if (response.getResponseCode().equalsIgnoreCase("00")) {
                        String errorMessage = response.getErrorCode() + " " + response.getErrorDesc();
                        addNotification(errorMessage, type);
                        isError = true;
                    } else {
                        Log.e("responseData", response.toString());

                        if (response.getDataJson().has("APP_VERSION_HISTORY")) {
                            try {
                                MyData myData = ModelProvider.getMyData(ManualSyncService.this, new MyData(), response);
                                myData.getAppVersionHistory().setInstallFlag(AppVersionHistory.FLAG_RECEIVED);
                                App.getContext().getDB().saveAppVersionHistoryOnReceived(myData.getAppVersionHistory());
                                Log.e("AppVersionHistory save", myData.getAppVersionHistory().toString());

                                String message = myData.getAppVersionHistory().getVersionName() + " " + Utility.getDateFromMillisecond(myData.getAppVersionHistory().getReleaseDate(), Constants.DATE_FORMAT_DD_MM_YY);
                                addNotification(message, type);
                                isError = false;

                            } catch (MhealthException e) {
                                e.printStackTrace();
                                String errorMessage = e.getMessage();
                                addNotification(errorMessage, type);
                                isError = true;
                            }

                        } else {
                            String errorMessage = "No new version available.";
                            addNotification(errorMessage, type);
                            isError = false;
                        }
                    }
                }

                if (isError && downloadAppVersionHistory < MAX_TRY) {
                    downloadAppVersionHistory();
                } else {
                    endSync();
                }
            }
        });
        commiunicationTask.execute();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(panicReceiver);
        panicReceiver = null;

        App.getContext().setAutoSyncServiceRunning(false);
//		if(!App.getContext().isActivityRunning())
//		{
//			App.getContext().getDB().closeDB();
//			App.destroyApp();
//		}
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.e("onBind start", "");
        App.getContext().setAutoSyncServiceRunning(true);
        return mBinder;
    }


    public void showNotification(String title, String message, int imageId, boolean showProgress, boolean isAutoCancel) {
        Log.e("notification_data", "title -> " + title + "  message ->" + message + "  isAutoCancel ->" + isAutoCancel);
        sbMessageToBePrint.append("\n");
        sbMessageToBePrint.append(message);
        sbMessageToBePrint.append("\n");


        Intent intent = new Intent(this, NotificationShowActivity.class);
        intent.putExtra("NOTIF_MESSAGE", sbMessageToBePrint.toString());
        intent.putExtra("NOTIF_ID", 9999);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;
        String channelId = title;
        String channelName = title;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mChannel.setSound(null, null);
            mNotificationManager.createNotificationChannel(mChannel);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isFource) {
                    mBuilder =
                            new NotificationCompat.Builder(this, channelId)
                                    .setSmallIcon(imageId)
                                    .setContentTitle("[Satellite Care] " + title)
                                    .setContentText(message)
                                    .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                                    .setDefaults(0)
                                    .setAutoCancel(true);
                } else {
                    mBuilder =
                            new NotificationCompat.Builder(this, channelId)
                                    .setSmallIcon(imageId)
                                    .setContentTitle("[Satellite Care] is running...")
                                    .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE))
                                    .setDefaults(0)
                                    .setAutoCancel(true);
                }
            } else {

                if (isFource) {
                    mBuilder =
                            new NotificationCompat.Builder(this, channelId)
                                    .setSmallIcon(imageId)
                                    .setContentTitle("[Satellite Care] " + title)
                                    .setContentText(message)
                                    .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                                    .setDefaults(0)
                                    .setAutoCancel(false);
                } else {
                    mBuilder =
                            new NotificationCompat.Builder(this, channelId)
                                    .setSmallIcon(imageId)
                                    .setContentTitle("[Satellite Care] is running...")
                                    .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                                    .setDefaults(0)
                                    .setAutoCancel(true);
                }
            }


            //  }


            Notification notif = mBuilder.build();
            mBuilder.setProgress(100, 0, showProgress);
            if (!isFource) {
                notif.flags |= Notification.FLAG_FOREGROUND_SERVICE;
                startForeground(1, notif);
            } else {
                mNotificationManager.notify(9999, notif);
            }


        } else {
            mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(imageId)
                            .setContentTitle("[Satellite Care] " + title)
                            .setContentText(message)
                            .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                            .setDefaults(0)
                            .setAutoCancel(true);
            mBuilder.setProgress(100, 0, showProgress);
            Notification notif = mBuilder.build();

            if (isFource) {
                mNotificationManager.notify(9999, notif);
            } else {
                notif.flags |= Notification.FLAG_AUTO_CANCEL;
                mNotificationManager.cancel(9999);
            }
        }


        Log.e("sdfsdf", sbMessageToBePrint.toString());
        Log.e("*************", "-------------------------");
    }

    public void addNotification(String message, String type) {
        NotificationItem notifItem = new NotificationItem();
        notifItem.setTitle(message);
        notifItem.setNotificationType(type);
        Log.e("NOTIFICATION ADD", notifItem + "");
        notificationItemList.add(notifItem);
    }


    /**
     * This is the object that receives interactions from clients.  See RemoteService
     * for a more complete example.
     */
    private final IBinder mBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply,
                                     int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

    private boolean isFource = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand start", startId + "");
        try {
            if (intent.getExtras().containsKey(ActivityDataKey.IS_FORCE)) {
                isFource = intent.getExtras().getBoolean(ActivityDataKey.IS_FORCE);
                manualSync = intent.getExtras().getString(ActivityDataKey.MANUAL_SYNC, "");
                Log.e("Service onStartCommand", "" + isFource);
            }
        } catch (Exception ex) {

        }

        App.getContext().setAutoSyncServiceRunning(true);
        startSync();
        return super.onStartCommand(intent, flags, startId);
    }


    private void getMyData() {
        App.getContext().loadApplicationData(ManualSyncService.this);
        RequestData request = new RequestData(RequestType.USER_GATE, RequestName.MY_DATA, Constants.MODULE_DATA_GET);
        request.setParam1(Utility.getTableRef(null, ManualSyncService.this));
        CommiunicationTask commiunicationTask = new CommiunicationTask(this, request);
        commiunicationTask.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Message msg) {
                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                    String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                    addNotification(errorMsg, "Can't Fetch your Data");
                } else {
                    final ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                    if (response.getResponseCode().equalsIgnoreCase("00")) {
                        addNotification(response.getErrorCode() + "-" + response.getErrorDesc(), "Can't save your data.");
                        //App.showMessageDisplayDialog(this, response.getErrorCode()+"-"+response.getErrorDesc(), R.drawable.error, Color.RED);
                    } else {
                        try {
                            Log.e("responseMyData", response.getData().toString());
                            JSONArray jsonArrayForUpdate = null;
                            MyData myData = ModelProvider.getMyData(ManualSyncService.this, new MyData(), response);
                            ModelHandler.getInstance(ManualSyncService.this).handleMydata(myData);
                            //added by Ashraf at 18th August,2021 for Update PIDF.InterviewID=PIM.FCM_INTERVIEW_ID Mapping
//                            try{
//                                jsonArrayForUpdate=App.getContext().getDB().getInterviewDoctorFeedbackForUpdate();
//                                if (jsonArrayForUpdate != null && jsonArrayForUpdate.length()>0) {
//                                    for (int d = 0; d < jsonArrayForUpdate.length(); d++) {
//                                        long docFollowupId = JSONParser.getLong(jsonArrayForUpdate.getJSONObject(d), Column.DOC_FOLLOWUP_ID);
//                                        long pimFcmInterviewId = JSONParser.getLong(jsonArrayForUpdate.getJSONObject(d), "PIM_FCM_INTERVIEW_ID");
//                                        App.getContext().getDB().updatePatientInterviewDoctorFeedbackBasedOnFCMInterviewId(docFollowupId, pimFcmInterviewId);
//
//                                    }
//                                }
//
//                            }catch (Exception e){z
//
//                            }
                            // end block
                            addNotification("Successfully saved your data.", "Successfully saved your data.");
                        } catch (Exception e) {
                            addNotification(e.getMessage(), "Can't save your data.");
                        }

                    }
                    //  generateNotificationForDoctorFeedback();
                }
            }
        });
        commiunicationTask.execute();
    }


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LanguageContextWrapper.wrap(context, AppPreference.getString(context, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)));
    }

    private void registerScreenReceiver() {
        panicReceiver = new BootReceiver();

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);

        registerReceiver(panicReceiver, filter);
    }


}
