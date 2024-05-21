package ngo.friendship.satellite.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.asynctask.CommiunicationTaskWithoutProgressBar;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.jsonoperation.JSONParser;

// TODO: Auto-generated Javadoc

/**
 * The Class AlarmReceiver.
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, NotificationService.class));
        } else {
            context.startService(new Intent(context, NotificationService.class));
        }

        try {
            uploadNotificationData(context);
        }catch (Exception e){}
    }


    private static void uploadNotificationData(Context ctx) {
        try {
            JSONArray unsendNotificationData = App.getContext().getDB().getAllUnsendNotification();
            if (unsendNotificationData.length() > 0) {
                RequestData request = new RequestData(RequestType.USER_GATE, RequestName.UPDATE_NOTIFICATION, Constants.MODULE_DATA_GET);
                try {

                    JSONObject jObj = new JSONObject();
                    jObj.put("NOTIFICATION_DATA", unsendNotificationData);
                    request.setData(jObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CommiunicationTaskWithoutProgressBar commiunicationTask = new CommiunicationTaskWithoutProgressBar(ctx, request);
                commiunicationTask.setCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(Message msg) {
                        if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                            String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                        } else {
                            ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                            if (response.getResponseCode().equalsIgnoreCase("00")) {
                                Log.e("Data", "data send notification");
                            } else {
                                for (int i = 0; i < unsendNotificationData.length(); i++) {
                                    try {
                                        long notificationId = JSONParser.getLong(unsendNotificationData.getJSONObject(i), "S_NOTIFICATION_ID");
                                        App.getContext().getDB().updateSendStatusNotification(notificationId, 1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }


                });
                commiunicationTask.execute();
            }
        } catch (Exception e) {
        }

    }





}
