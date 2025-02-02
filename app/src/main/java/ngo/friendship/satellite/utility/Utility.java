package ngo.friendship.satellite.utility;

import static ngo.friendship.satellite.communication.APICommunication.makeWebRequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mikhaellopez.circularimageview.CircularImageView;

//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.conn.ConnectTimeoutException;
//import org.apache.http.conn.HttpHostConnectException;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.protocol.HTTP;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//import de.congrace.exp4j.ExpressionBuilder;
//import de.congrace.exp4j.UnknownFunctionException;
//import de.congrace.exp4j.UnparsableExpressionException;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
//import ngo.friendship.mhealth.activities.CustomNotificationShowActivity;
//import ngo.friendship.mhealth.communication.APICommunication;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.NotificationItem;
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedback;
import ngo.friendship.satellite.model.QuestionAnswer;
import ngo.friendship.satellite.model.maternal.MaternalService;
import ngo.friendship.satellite.views.AppToast;


public class Utility {

    public static String md5(String data) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data.getBytes("UTF-8"));

            // converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(TextUtility.format("%02x", b & 0xff));
            }

            digest = sb.toString();

        } catch (UnsupportedEncodingException ex) {
            // Logger.getLogger(StringReplace.class.getUserName()).log(Level.SEVERE,
            // null, ex);
        } catch (NoSuchAlgorithmException ex) {
            // Logger.getLogger(StringReplace.class.getUserName()).log(Level.SEVERE,
            // null, ex);
        }
        return digest;
    }

    public static void displayMessage(View view, Context context, String toastString) {
        try {
            Snackbar.make(view, toastString, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    /**
     * Read the device IMEI and return.
     *
     * @param ctx The application context
     * @return The Device's IMEI or <strong>null</strong> if the device doesn't
     * have any IMEI
     */
//	public static String getIMEInumber(Context ctx) {
//		String identifier = null;
//		TelephonyManager tm = (TelephonyManager) ctx
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		if (tm != null)
//			identifier = tm.getDeviceId();
//		if (identifier == null || identifier.length() == 0)
//			identifier = Secure.getString(ctx.getContentResolver(),Secure.ANDROID_ID);
//
//		return identifier;
//	}
    public static String getIMEInumber(Context ctx) {
        String identifier = null;
        TelephonyManager tm = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
        try {
            identifier = tm.getDeviceId();
        } catch (Exception e) {
            if (identifier == null || identifier.length() == 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    identifier = android.provider.Settings.System.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
                } else {
                    identifier = DeviceUniqueIDUtils.getMACAddress("wlan0");
                }
        }
        DeviceUniqueIDUtils.getIPAddress(true); // IPv4
        DeviceUniqueIDUtils.getIPAddress(false); // IPv6
        return identifier;
    }

    public static String getOldIMEInumber(Context ctx) {
        String imeiInfo = "";
        String oldDeviceId = AppPreference.getString(ctx, KEY.OLD_DEVICE_ID, "");
        try {
            imeiInfo = oldDeviceId + "+" + Utility.getDeviceSuperInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imeiInfo;

    }


    /**
     * Get Application version name.
     *
     * @param ctx The application context
     * @return The application version name
     */
    public static String getVersionName(Context ctx) {
        String versionName = "unknown";
        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            versionName = "v" + pinfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionName;
    }


    public static String getVersionCode(Context ctx) {
        String versionName = "";
        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            versionName = "" + pinfo.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * Set screen brightness .
     *
     * @param context The application context
     * @param value   The brightness value. Min: 1, Max: 255
     */
    public static void setScreenBrightness(Context context, float value) {
        float brightnessValue = value / 255;
        int brightnessMode = 1;
        try {
            brightnessMode = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }

        WindowManager.LayoutParams layoutParams = ((Activity) context)
                .getWindow().getAttributes();
        layoutParams.screenBrightness = brightnessValue; // set 50% brightness
        ((Activity) context).getWindow().setAttributes(layoutParams);
    }

    /**
     * Set the screen off timeout time .
     *
     * @param ctx     The application context
     * @param duraion The time in millisecond
     */
    public static void setScreenOffTimeoutTime(Context ctx, int duraion) {
        Settings.System.putInt(ctx.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, duraion);
    }

    /**
     * Read device current screen off timeout time.
     *
     * @param ctx The application context
     * @return The time in millisecond
     */
    public static int getCurrentScreenTimeoutTime(Context ctx) {
        return Settings.System.getInt(ctx.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, -1);
    }

    /**
     * Unlock device screen.
     *
     * @param activity The application context in Activity
     */
    public static void unlockScreen(Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    public static long getMillisecondFromDate(String dateStr,
                                              SimpleDateFormat dateFormat) throws ParseException {
        if (dateStr == null)
            return 0;
        Log.e("getMillisecondFromDate", dateStr);

        Date date = dateFormat.parse(dateStr);
        long milliseconds = date.getTime();
        return milliseconds;
    }

    /**
     * Get the formated date <strong>(yyyy-MM-dd)</strong> from millisecond.
     *
     * @param millisecond The date in millisecond
     * @param dateFormat  the date format
     * @return The Formated Date <strong>(yyyy-MM-dd)</strong>
     */
    public static String getDateFromMillisecond(long millisecond,
                                                SimpleDateFormat dateFormat) {

        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        String dateStr = dateFormat.format(calendar.getTime());

        return dateStr;
    }

    public static String getDateTimeFromMillisecond(long millisecond,
                                                    SimpleDateFormat dateFormat) {

        // Create a calendar object that will convert the date and time value in
        // milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);
        String dateStr = dateFormat.format(calendar.getTime());
        return dateStr;
    }


    /**
     * Send <strong>message</strong> to <strong>phoneNumber</strong> as SMS.
     *
     * @param ctx         The application context
     * @param phoneNumber The phone number to which the message will be sent
     * @param message     The message which will be sent.
     */
    public static void sendSMS(final Context ctx, String phoneNumber,
                               String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, 0,
                new Intent(DELIVERED), 0);

        // ---when the SMS has been sent---
        ctx.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        AppToast.showToastSuccess(ctx, "SMS sent");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        AppToast.showToastWarnaing(ctx, "Generic failure");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        AppToast.showToastWarnaing(ctx, "No service");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        AppToast.showToastWarnaing(ctx, "Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        AppToast.showToastWarnaing(ctx, "Radio off");
                        break;
                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        ctx.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        AppToast.showToastSuccess(ctx, "SMS delivered");
                        break;
                    case Activity.RESULT_CANCELED:
                        AppToast.showToastWarnaing(ctx, "SMS not delivered");
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    /**
     * Get current age from date of birth.
     *
     * @param dob The date of birth in <strong>yyyy-MM-dd</strong> format
     * @return Current age.<br>
     * <i>Exmple: 2m 3d (If age < 1). 2y 3m (If age < 10) else 20y</i>
     * @throws ParseException the parse exception
     */
    public static String getAge(String dob) throws ParseException {

        Calendar calender = Calendar.getInstance();

        long currentTime = calender.getTimeInMillis();

        calender.setTime(Constants.DATE_FORMAT_YYYY_MM_DD.parse(dob));

        long dobInMillis = calender.getTimeInMillis();

        long ageInMillis = currentTime - dobInMillis;

        if (ageInMillis < 0) {
            return null;
        }

        long day = ageInMillis / 1000 / 60 / 60 / 24;

        day = day + 1; // add 1 day in age calculation

        long year = day / 365;
        long remainingDay = day % 365;
        long month = remainingDay / 30;

        remainingDay = remainingDay % 30;

        StringBuilder sb = new StringBuilder();
        if (year < 1) {
            if (month > 0) {
                sb.append(month);
                sb.append("m ");
            }

            if (remainingDay > 0) {
                sb.append(remainingDay);
                sb.append("d");
            }
        } else if (year < 10) {
            if (year > 0) {
                sb.append(year);
                sb.append("y ");
            }
            if (month > 0) {
                sb.append(month);
                sb.append("m ");
            }
        } else {
            sb.append(year);
            sb.append("y");
        }

        return sb.toString();
    }

    /**
     * Gets the age in year.
     *
     * @param dob the dob
     * @return the age in year
     * @throws ParseException the parse exception
     */


    public static long getAgeInYear(String dob) throws ParseException {


        Calendar calender = Calendar.getInstance();

        long currentTime = calender.getTimeInMillis();

        calender.setTime(Constants.DATE_FORMAT_YYYY_MM_DD.parse(dob));

        long dobInMillis = calender.getTimeInMillis();

        long ageInMillis = currentTime - dobInMillis;

        if (ageInMillis < 0) {
            return 0;
        }

        long day = ageInMillis / 1000 / 60 / 60 / 24;

        day = day + 1; // add 1 day in age calculation
        return day / 365;
    }


    public static long getAgeInMonth(String dob) throws ParseException {


        Calendar calender = Calendar.getInstance();

        long currentTime = calender.getTimeInMillis();

        calender.setTime(Constants.DATE_FORMAT_YYYY_MM_DD.parse(dob));

        long dobInMillis = calender.getTimeInMillis();

        long ageInMillis = currentTime - dobInMillis;

        if (ageInMillis < 0) {
            return 0;
        }

        long day = ageInMillis / 1000 / 60 / 60 / 24;

        day = day + 1; // add 1 day in age calculation

        long month = day / 30;
        //        if(day%30>0){
        //        	month++;
        //        }
        return month;
    }

    /**
     * Gets the age in day.
     *
     * @param dob the dob
     * @return the age in day
     * @throws ParseException the parse exception
     */
    public static long getAgeInDay(String dob) throws ParseException {


        Calendar calender = Calendar.getInstance();

        long currentTime = calender.getTimeInMillis();

        calender.setTime(Constants.DATE_FORMAT_YYYY_MM_DD.parse(dob));

        long dobInMillis = calender.getTimeInMillis();

        long ageInMillis = currentTime - dobInMillis;

        if (ageInMillis <= 0) {
            return 0;
        }

        long day = ageInMillis / 1000 / 60 / 60 / 24;

        day = day + 1; // add 1 day in age calculation

        // long year = day/365;
        // long remainingDay = day%365;
        // long month = remainingDay/30;
        //
        // remainingDay = remainingDay%30;

        return day;
    }

    public static long getAgeInHour(String dob) throws ParseException {


        Calendar calender = Calendar.getInstance();

        long currentTime = calender.getTimeInMillis();

        calender.setTime(Constants.DATE_FORMAT_YYYY_MM_DD.parse(dob));

        long dobInMillis = calender.getTimeInMillis();

        long ageInMillis = currentTime - dobInMillis;

        if (ageInMillis <= 0) {
            return 0;
        }

        long day = ageInMillis / 1000 / 60 / 60;


        return day;
    }

    /**
     * Get the number of days between two date.
     * <p>
     * <p>
     * The date upto which the day will be calculated
     *
     * @return The number of days between <strong>fromDate</strong> and
     * <strong>toDate</strong>
     */
    public static long getNumberOfDaysBetweenDate(long fromDateInMillis, long toDateInMillis) {
        long houre = getNumberOfHoureBetweenDate(fromDateInMillis, toDateInMillis);
        if (houre <= 0) return 0;
        else return houre / 24;
    }

    public static long getNumberOfHoureBetweenDate(long fromDateInMillis, long toDateInMillis) {

        long durationTimeInMillis = toDateInMillis - fromDateInMillis;
        if (durationTimeInMillis <= 0) {
            return 0;
        }
        return durationTimeInMillis / (1000 * 60 * 60);
    }

    public static long getNumberOfMinuteBetweenDate(long fromDateInMillis, long toDateInMillis) {

        long durationTimeInMillis = toDateInMillis - fromDateInMillis;
        if (durationTimeInMillis <= 0) {
            return 0;
        }
        return durationTimeInMillis / (1000 * 60 * 60*60);
    }

    /**
     * Gets the remaining days.
     *
     * @param fromDate the from date
     * @param toDate   the to date
     * @return the remaining days
     */
    public static String getRemainingDays(Calendar fromDate, Calendar toDate) {


        long remainingDaysInMillis = toDate.getTimeInMillis() - fromDate.getTimeInMillis();
        boolean isNagitave = remainingDaysInMillis < 0;
        long totalDay = Math.abs(remainingDaysInMillis) / 1000 / 60 / 60 / 24;
        long year = totalDay / 365;
        long remainingDay = totalDay % 365;
        long month = remainingDay / 30;

        remainingDay = remainingDay % 30;

        StringBuilder sb = new StringBuilder();
        if (isNagitave) {
            sb.append("-");
        }

        if (year < 1) {
            if (month > 0) {
                sb.append(month);
                sb.append("m ");
            }
            sb.append(remainingDay);
            sb.append("d");
        } else if (year < 10) {
            sb.append(year);
            sb.append("y ");
            if (month > 0) {
                sb.append(month);
                sb.append("m ");
            }
        } else {
            sb.append(year);
            sb.append("y");
        }

        Log.e("getRemainingDays", toDate.getTimeInMillis() + "-" + fromDate.getTimeInMillis() + "=" + (toDate.getTimeInMillis() - fromDate.getTimeInMillis()) + "=" + sb.toString());

        return sb.toString();
    }

    /**
     * Encrypt password and return.
     *
     * @param originalPass The original password which will be encrypted
     * @return The encrypted password
     */
    public static String generateEncryptedPassword(String originalPass) {
        ArrayList<Character> encryptPass = new ArrayList<Character>();
        final char encryptionLetter[] = {'!', 'f', 'E', '%', 'h', '&', '*',
                '$', '.', 'l', 'D', 'b', '^', 'x', 'V', 'D', 'P', '{', 'Z',
                'w', 'J', '!', '~', ']', 'C', 'c', 'M', '>', 'T', 'u', '?',
                '<', 'I', '?', '@'};

        String reversedPass = new StringBuilder(originalPass).reverse()
                .toString();
        char passArr[] = reversedPass.toCharArray();
        for (int i = 0; i < passArr.length; i++) {
            encryptPass.add(passArr[i]);
        }

        Random rnd = new Random();

        for (int i = 0; i < 28; i++) {
            int randNumber = Math.abs(rnd.nextInt() % encryptionLetter.length);
            char randChar = encryptionLetter[randNumber];

            int rendIndex = Math.abs(rnd.nextInt() % encryptPass.size());
            encryptPass.add(rendIndex, randChar);
        }
        StringBuilder sb = new StringBuilder();
        for (char ch : encryptPass)
            sb.append(ch);
        return sb.toString();
    }

    /**
     * Creates the error web response.
     *
     * @param ctx       the ctx
     * @param messageId the message id
     * @param e         the e
     * @return the web response info
     */
    public static ResponseData createErrorWebResponse(Context ctx,
                                                      int messageId, Exception e) {
        ResponseData webResponseDataInfo = new ResponseData();
        webResponseDataInfo.setResponseCode("00");
        webResponseDataInfo.setErrorCode("");
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.getResources().getString(messageId));
        sb.append("\n");
        sb.append(e.getMessage());

        webResponseDataInfo.setErrorDesc(sb.toString());

        return webResponseDataInfo;
    }


    /**
     * Download beneficiary image.
     *
     * @param ctx The application context
     * @param ^   beneficiary List The beneficiary list which image will be downloaded
     */

    public static void downloadBeneficiaryImage(Context ctx,
                                                ArrayList<Beneficiary> beneficiaryList) {

        try {
            if (App.getContext().getAppSettings().getCloneInfo() != null && App.getContext().getAppSettings().getCloneInfo().has("INCLUDE_FILE_DOWNLOAD") && App.getContext().getAppSettings().getCloneInfo().getLong("INCLUDE_FILE_DOWNLOAD") == 0) {
                return;
            }
        } catch (JSONException e1) {

        }


        if (beneficiaryList == null)
            return;

        for (Beneficiary beneficiary : beneficiaryList) {
            if (beneficiary.getBenefImagePath() != null
                    && !beneficiary.getBenefImagePath().equalsIgnoreCase("")) {
                File file = new File(beneficiary.getBenefImagePath());
                /** Check if image exist */
                if (!file.exists()) {
                    String fileName = file.getName();
                    fileName = "name://" + fileName;

                    JSONObject jdataObj = new JSONObject();
                    try {
                        /** Create data JSON */
                        jdataObj.put("BENEF_IMAGE_PATH", fileName);

                        /** Create request json */
                        String requestJson = JSONCreateor.createRequestJson(
                                ctx, RequestType.USER_GATE,
                                RequestName.GET_FILE, Constants.MODULE_DATA_GET, null, jdataObj, null);

                        // Log.e("File Req", requestJson);

                        /** Create multipart data */
                        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder
                                .create();
                        Charset cs = Charset.forName("UTF-8");
                        entityBuilder.setCharset(cs);
                        entityBuilder.addBinaryBody("data",
                                requestJson.getBytes(HTTP.UTF_8));

                        boolean isAlternativeApi = false;
                        String apiBase = App.getContext()
                                .getGateWayBasePath();
                        if (apiBase == null) {
                            apiBase = App.getContext()
                                    .getAlternativeGateWayBasePath();
                            isAlternativeApi = true;
                        }

                        ResponseData webResponseInfo = makeWebRequest(ctx,
                                entityBuilder, apiBase);

                        if (!isAlternativeApi
                                && (webResponseInfo == null
                                || webResponseInfo
                                .getWebResponseStatusCode() != 200 || webResponseInfo
                                .getResponseCode().equalsIgnoreCase(
                                        "00"))) {
                            apiBase = App.getContext()
                                    .getAlternativeGateWayBasePath();
                            if (apiBase != null) {
                                webResponseInfo = makeWebRequest(ctx,
                                        entityBuilder, apiBase);
                            }
                        }

                        if (webResponseInfo != null
                                && webResponseInfo.getWebResponseStatusCode() == 200
                                && webResponseInfo.getResponseCode()
                                .equalsIgnoreCase("01")) {
                            JSONObject jDataObj = new JSONObject(
                                    webResponseInfo.getData());

                            // Log.e("File Res", webResponseInfo.getData());

                            /** Retrieve image binary data from response */
                            String fileData = jDataObj.getString("FILE_DATA");

                            /** Write to file */
                            FileOperaion.writeImageToFileNew(beneficiary.getBenefImagePath(), fileData);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static void downloadCommonFile(Context ctx, String downloadUrl, String targetDir) {
        File outputFile = new File(targetDir);
        try {
            URL url = new URL(downloadUrl);//Create Download URl
            HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
            c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
            c.connect();//connect the URL Connection
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            byte[] buffer = new byte[1024];//Set buffer type
            int len1 = 0;//init length
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);//Write new file
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    private static Response makeWebRequest(Context ctx,
//                                           MultipartEntityBuilder entityBuilder, String baseUrl) {
//        Response webResponseInfo = new Response();
//        try {
//
//            webResponseInfo = APICommunication.uploadMultipartData(ctx, baseUrl
//                    + App.getContext().getUserGateAPI(), entityBuilder);
//
//        } catch (ClientProtocolException e) {
//
//            webResponseInfo = Utility.createErrorWebResponse(ctx,
//                    R.string.network_error, e);
//            e.printStackTrace();
//        } catch (MhealthException e) {
//            webResponseInfo = Utility.createErrorWebResponse(ctx,
//                    R.string.error_json_parse_exception, e);
//            e.printStackTrace();
//        } catch (HttpHostConnectException e) {
//            webResponseInfo = Utility.createErrorWebResponse(ctx,
//                    R.string.exception_host_connection, e);
//            e.printStackTrace();
//        } catch (ConnectTimeoutException e) {
//            webResponseInfo = Utility.createErrorWebResponse(ctx,
//                    R.string.exception_time_out, e);
//            e.printStackTrace();
//        } catch (ConnectException e) {
//            webResponseInfo = Utility.createErrorWebResponse(ctx,
//                    R.string.network_error, e);
//            e.printStackTrace();
//        } catch (Exception e) {
//            webResponseInfo = Utility.createErrorWebResponse(ctx,
//                    R.string.error_exception, e);
//            e.printStackTrace();
//        }
//
//        return webResponseInfo;
//    }


    public static List<QuestionAnswer> sortQuestionAnswerList(List<QuestionAnswer> qaArrList) {
        Collections.sort(qaArrList, new Comparator<QuestionAnswer>() {
            @Override
            public int compare(QuestionAnswer lhs, QuestionAnswer rhs) {
                // TODO Auto-generated method stub
                return lhs.getOrder() - rhs.getOrder();
            }
        });
        return qaArrList;
    }

    public static ArrayList<MaternalService> sortMaternalService(HashMap<String, MaternalService> MaternalServiceMap) {
        ArrayList<MaternalService> maternalServices = new ArrayList<MaternalService>(MaternalServiceMap.values());
        Collections.sort(maternalServices, new Comparator<MaternalService>() {
            @Override
            public int compare(MaternalService lhs, MaternalService rhs) {
                return (int) rhs.getMaternalServiceId() - (int) lhs.getMaternalServiceId();
            }
        });
        return maternalServices;
    }

    public static boolean haveAnyVisibleQuestion(HashMap<String, QuestionAnswer> questionAnswerList) {
        Iterator<String> keys = questionAnswerList.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            QuestionAnswer questionAnswer = questionAnswerList.get(key);
            if (!questionAnswer.isHidden()) {
                return true;
            }
        }
        return false;
    }


    public static String getRoman(int number) {

        String riman[] = {"M", "XM", "CM", "D", "XD", "CD", "C", "XC", "L",
                "XL", "X", "IX", "V", "IV", "I"};
        int arab[] = {1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9,
                5, 4, 1};
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (number > 0 || arab.length == (i - 1)) {
            while ((number - arab[i]) >= 0) {
                number -= arab[i];
                result.append(riman[i]);
            }
            i++;
        }
        return result.toString();
    }

    public static String getSmallLatter(int number) {

        int rem = number % 26;
        int mod = number / 26;
        String res = "";
        for (int x = 0; x < mod; x++) {
            res = res + (char) (97) + ".";
        }
        if (rem == 0)
            return res;
        return res + (char) (rem + 96) + ".";

    }

    public static String getAppCreateDate(Context context) {

        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            return SimpleDateFormat.getInstance().format(
                    new Date(time));

        } catch (Exception e) {
        }
        return null;
    }

    public static long getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 99999999;
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long parseLong(String data) {
        try {
            return Long.parseLong(data);
        } catch (Exception ex) {
            return 0;
        }

    }

    public static int parseInt(String data) {
        try {
            return Integer.parseInt(data);
        } catch (Exception ex) {
            return 0;
        }

    }

    public static double parseDouble(String data) {
        try {
            return Double.parseDouble(data);
        } catch (Exception ex) {
            return 0.00;
        }

    }

    public static boolean isLeapYear(int year) {

        return (year % 400 == 0) || ((year % 4 == 0) && (year % 100 != 0));
    }


    public static ArrayList<String> getAnswer(HashMap<String, QuestionAnswer> questionAnswerMap, String questionName) {
        if (questionAnswerMap != null) {
            for (String key : questionAnswerMap.keySet()) {
                QuestionAnswer questionAnswer = questionAnswerMap.get(key);
                if (questionAnswer != null && questionAnswer.getQuestionName().equals(questionName)) {
                    return questionAnswer.getAnswerList();
                }
            }
        }
        return null;
    }


    public static boolean isValidDate(String inDate, SimpleDateFormat dateFormat) {
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static boolean isValidTime(String hhMM) {
        try {
            String[] st = hhMM.trim().split(":");
            if (st.length == 2) {
                int hh = Integer.parseInt(st[0]);
                int mm = Integer.parseInt(st[1]);
                if (hh >= 0 && hh <= 23 && mm >= 0 && mm <= 59 && (hh + mm) > 0) {
                    return true;
                }
            }
        } catch (Exception pe) {
            return false;
        }
        return false;
    }

    public static int getHour(String hhMM) {
        return Integer.parseInt(hhMM.trim().split(":")[0]);
    }

    public static int getMinute(String hhMM) {
        return Integer.parseInt(hhMM.trim().split(":")[1]);
    }

    public static String setActivityPath(Activity activity, Object title) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        String lastPart = "";
        String activityPath = "";
        if (title instanceof Integer)
            lastPart = activity.getResources().getString((Integer) title);
        else if (title instanceof String)
            lastPart = (String) title;
        Bundle bundle = activity.getIntent().getExtras();
        if (bundle != null) {
            activityPath = activity.getIntent().getExtras().getString(ActivityDataKey.ACTIVITY_PATH);
        }

        if (activityPath == null || activityPath.trim().equalsIgnoreCase(""))
            activityPath = lastPart;
        else
            activityPath = activityPath + ">" + lastPart;

        TextView tv = activity.findViewById(R.id.tv_path);
        tv.setText(activityPath);

        try {
            LinearLayout statusBar = activity.findViewById(R.id.ll_header_separator_line);
            if (statusBar != null && App.STATUS_COLOR != -1) {
                statusBar.setBackgroundColor(App.STATUS_COLOR);
            }
        } catch (Exception exception) {
        }

        try {
            if (App.getContext().getAppSettings().getCloneInfo() != null) {
                activity.findViewById(R.id.iv_clone).setVisibility(View.VISIBLE);
            } else {
                activity.findViewById(R.id.iv_clone).setVisibility(View.GONE);
            }

        } catch (Exception ex) {

        }
        return activityPath;
    }


    public static ArrayList<Beneficiary> getBeneficiaryList(String householdNumber, String selectionCriteriaStr) {
        ArrayList<Beneficiary> beneficiaryList = null;
        boolean withOutfamilyHead = false;
        String gender = null;
        String maritalStatus = null;
        Integer maternalStatus = null;
        String ageType = null;
        int isguestBenefAllowed = -1;
        int start = -1;
        int end = -1;
        try {
            JSONObject jObject = new JSONObject(selectionCriteriaStr);
            if (jObject.has("benefSelectionCriteria")) {
                JSONObject selectionCriteria = jObject.getJSONObject("benefSelectionCriteria");
                if (selectionCriteria.has("ageRange")) {
                    JSONObject ageRange = selectionCriteria.getJSONObject("ageRange");
                    if (ageRange.has("start"))
                        start = ageRange.getInt("start");
                    if (ageRange.has("end"))
                        end = ageRange.getInt("end");
                    if (ageRange.has("ageType")) ;
                    ageType = ageRange.getString("ageType");
                }
                if (selectionCriteria.has("gender"))
                    gender = selectionCriteria.getString("gender");
                if (selectionCriteria.has("guestBenefAllowed"))
                    isguestBenefAllowed = selectionCriteria.getInt("guestBenefAllowed");

                if (selectionCriteria.has("maritalStatus"))
                    maritalStatus = selectionCriteria.getString("maritalStatus");
                if (selectionCriteria.has("maternalStatus"))
                    maternalStatus = selectionCriteria.getInt("maternalStatus");
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return App.getContext().getDB().getHouseholdMemberList(householdNumber, withOutfamilyHead, gender, maritalStatus, maternalStatus, ageType, start, end, isguestBenefAllowed);
    }

    public static long getNumberOfWeeksBetweenDate(long fromDateInMillis, long toDateInMillis) {
        Log.e("numberOfWeek", "FORM DATE " + fromDateInMillis + " TO DATE " + toDateInMillis);
        long day = getNumberOfDaysBetweenDate(fromDateInMillis, toDateInMillis);
        if (day <= 0) return 0;
        else return day / 7;
    }

    public static String getTableNameByVersionNoKey(String versionNoKey) {
        if (versionNoKey.equals(KEY.VERSION_NO_BENEFICIARY))
            return DBTable.BENEFICIARY;
        else if (versionNoKey.equals(KEY.VERSION_NO_CATEGORIES))
            return DBTable.QUESTIONNAIRE_CATEGORY;
        else if (versionNoKey.equals(KEY.VERSION_NO_QUESTIONNAIRE))
            return DBTable.QUESTIONNAIRE;
        else if (versionNoKey.equals(KEY.VERSION_NO_REFERRAL_CENTER))
            return DBTable.REFERRAL_CENTER;
        else if (versionNoKey.equals(KEY.VERSION_NO_USER_SCHEDULE))
            return (DBTable.USER_SCHEDULE);
        else if (versionNoKey.equals(KEY.VERSION_NO_USER_INFO))
            return DBTable.USER_INFO;
        else if (versionNoKey.equals(KEY.VERSION_NO_MEDICINE))
            return DBTable.MEDICINE;
        else if (versionNoKey.equals(KEY.VERSION_NO_DIAGNOSIS_INFO))
            return DBTable.DIAGNOSIS_INFO;
        else if (versionNoKey.equals(KEY.VERSION_NO_GROWTH_MEASUREMENT_PARAM))
            return DBTable.GROWTH_MEASUREMENT_PARAM;
        else if (versionNoKey.equals(KEY.VERSION_NO_IMMUNIZATION_INFO))
            return DBTable.IMMUNIZATION_INFO;
        else if (versionNoKey.equals(KEY.VERSION_NO_IMMUNIZATION_SERVICE))
            return DBTable.IMMUNIZATION_SERVICE;
        else if (versionNoKey.equals(KEY.VERSION_NO_IMMUNIZATION_FOLLOWUP))
            return DBTable.IMMUNIZATION_FOLLOWUP;
        else if (versionNoKey.equals(KEY.VERSION_NO_IMMUNIZABLE_BENEFICIARY))
            return DBTable.IMMUNIZABLE_BENEFICIARY;
        else if (versionNoKey.equals(KEY.VERSION_NO_IMMUNIZATION_TARGET))
            return DBTable.IMMUNIZATION_TARGET;
        else if (versionNoKey.equals(KEY.VERSION_NO_CCS_STATUS))
            return DBTable.CCS_STATUS;
        else if (versionNoKey.equals(KEY.VERSION_NO_CCS_ELIGIBLE))
            return DBTable.CCS_ELIGIBLE;
        else if (versionNoKey.equals(KEY.VERSION_NO_COURTYARD_MEETING_TARGET))
            return DBTable.COURTYARD_MEETING_TARGET;
        else if (versionNoKey.equals(KEY.VERSION_NO_TEXT_REF))
            return DBTable.TEXT_REF;
        else if (versionNoKey.equals(KEY.VERSION_NO_EVENT_INFO))
            return DBTable.EVENT_INFO;
        else if (versionNoKey.equals(KEY.TRANS_REF_MATERNAL_INFO))
            return DBTable.MATERNAL_INFO;
        else if (versionNoKey.equals(KEY.VERSION_NO_MATERNAL_CARE_INFO))
            return DBTable.MATERNAL_CARE_INFO;
        else if (versionNoKey.equals(KEY.VERSION_NO_HOUSEHOLD))
            return DBTable.HOUSEHOLD;
        else if (versionNoKey.equals(KEY.VERSION_NO_CCS_TREATMENT))
            return DBTable.CCS_TEREATMENT;
        else if (versionNoKey.equals(KEY.VERSION_NO_TOPIC_INFO))
            return DBTable.TOPIC_INFO;
        else if (versionNoKey.equals(KEY.VERSION_NO_COURTYARD_MEETING_TOPIC_MONTH))
            return DBTable.COURTYARD_MEETING_TOPIC_MONTH;
        else if (versionNoKey.equals(KEY.VERSION_NO_QUESTIONNAIRE_BROAD_CATEGORY))
            return DBTable.QUESTIONNAIRE_BROAD_CATEGORY;
        else if (versionNoKey.equals(KEY.VERSION_NO_QUESTIONNAIRE_SERVICE_CATEGORY))
            return DBTable.QUESTIONNAIRE_SERVICE_CATEGORY;
        else if (versionNoKey.equals(KEY.VERSION_NO_REPORT_ASST))
            return DBTable.REPORT_ASST;
        else if (versionNoKey.equals(KEY.VERSION_NO_REFERRAL_CENTER_CATEGORY))
            return DBTable.REFERRAL_CENTER_CATEGORY;
        else if (versionNoKey.equals(KEY.TRANS_REF_PATIENT_INTERVIEW_DOCTOR_FEEDBACK))
            return DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK;
        else if (versionNoKey.equals(KEY.VERSION_NO_SESSION))
            return DBTable.SATELLITE_SESSION;
        else return null;

    }

    public static boolean isNumaric(String data) {
        try {
            Double.parseDouble(data);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static double evaluate(String expression, HashMap<String, QuestionAnswer> questionAnswerMap) {
        String qtyStr = expression.trim();
        if (qtyStr.indexOf("<") == 0 && qtyStr.indexOf(">") == (qtyStr.length() - 1)) {
            String qtyExpression = qtyStr.substring(1, qtyStr.length() - 1);
            Matcher m = Pattern.compile("q\\d+").matcher(qtyExpression);
            while (m.find()) {
                QuestionAnswer answer = questionAnswerMap.get(m.group());
                if (answer != null && answer.getAnswerList() != null && answer.getAnswerList().size() == 1) {
                    qtyExpression = qtyExpression.replace(m.group(), answer.getAnswerList().get(0));
                }
            }

            try {
                return new ExpressionBuilder(qtyExpression).build().calculate();
            } catch (UnknownFunctionException e) {
                e.printStackTrace();
            } catch (UnparsableExpressionException e) {
                e.printStackTrace();
            }

        }
        return 0.0;
    }


    public static boolean isIMEIPermitted(String imei) {
        String testImeis = "35809604027190,"
                + "358401055111023,"
                + "358113061814074,"
                + "358303062038308,"
                + "359038056407213,"
                + "359034066426665,"
                + "353264062892893,"
                + "353106061056089,"
                + "359034066425014,"
                + "358913057943609,"
                + "355811068098079,"
                + "352467071269158"
                + "359034066427325,"
                + "863657038464726,"
                + "353264062892893,"
                + "358913057983609,"
                + "359884050617954,"
                + "357337062071555,"
                + "359884050213051,"
                + "358401053850721,"
                + "359034066499134,"
                + "352175077271322,"
                + "356414071209053,"
                + "869597034591540,"
                + "358401054090673,";
        return testImeis.contains(imei);
    }


    public static boolean isJson(String text) {
        try {
            JSONObject object = new JSONObject(text);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document document = builder.parse(is);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static ArrayList<String> split(String text, String regularExpression) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (text.length() > 0) {
            String[] str = text.split(regularExpression);
            for (String string : str) {
                arrayList.add(string);
            }
        }
        return arrayList;

    }

    public static Long getValue(Map<String, Long> map, String key) {
        if (map.get(key) != null) {
            return map.get(key);
        } else {
            return 0L;
        }
    }

    public static JSONObject getTableRef(HashMap<String, String> refTable, Context context) {
        JSONObject params = new JSONObject();
        Map<String, Long> versionList = App.getContext().getDB().getVersionList();
        try {

            if (refTable != null && refTable.size() > 0) {
                Iterator it = refTable.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if (pair.getKey().toString().equals("TRANS_REF")) {
                        params.put(pair.getKey().toString(), App.getContext().getDB().getMax(pair.getValue().toString(), Column.TRANS_REF));
                    } else if (pair.getKey().toString().startsWith(KEY.APP_VERSION_NUMBER)) {
                        params.put(KEY.APP_VERSION_NUMBER, context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
                    } else if (pair.getKey().toString().equals(KEY.LAST_UPDATE_TIME_MEDICINE_STOCK)) {
                        params.put(KEY.LAST_UPDATE_TIME_MEDICINE_STOCK, App.getContext().getDB().getMax(DBTable.MEDICINE_STOCK, Column.LAST_UPDATE_ON));
                    } else {
                        params.put(pair.getKey().toString(), getValue(versionList, pair.getValue().toString()));
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }
            } else {

                params.put(KEY.VERSION_NO_BENEFICIARY, getValue(versionList, DBTable.BENEFICIARY));
                params.put(KEY.VERSION_NO_CATEGORIES, getValue(versionList, DBTable.QUESTIONNAIRE_CATEGORY));
                params.put(KEY.VERSION_NO_MEDICINE, getValue(versionList, DBTable.MEDICINE));
                params.put(KEY.VERSION_NO_QUESTIONNAIRE, getValue(versionList, DBTable.QUESTIONNAIRE));
                params.put(KEY.VERSION_NO_REFERRAL_CENTER, getValue(versionList, DBTable.REFERRAL_CENTER));
                params.put(KEY.VERSION_NO_REFERRAL_CENTER_CATEGORY, getValue(versionList, DBTable.REFERRAL_CENTER_CATEGORY));
                params.put(KEY.VERSION_NO_USER_INFO, getValue(versionList, DBTable.USER_INFO));
                params.put(KEY.VERSION_NO_DIAGNOSIS_INFO, getValue(versionList, DBTable.DIAGNOSIS_INFO));
                params.put(KEY.VERSION_NO_GROWTH_MEASUREMENT_PARAM, getValue(versionList, DBTable.GROWTH_MEASUREMENT_PARAM));
                params.put(KEY.VERSION_NO_IMMUNIZABLE_BENEFICIARY, getValue(versionList, DBTable.IMMUNIZABLE_BENEFICIARY));
                params.put(KEY.VERSION_NO_IMMUNIZATION_INFO, getValue(versionList, DBTable.IMMUNIZATION_INFO));
                params.put(KEY.VERSION_NO_IMMUNIZATION_TARGET, getValue(versionList, DBTable.IMMUNIZATION_TARGET));
                params.put(KEY.VERSION_NO_EVENT_INFO, getValue(versionList, DBTable.EVENT_INFO));
                params.put(KEY.VERSION_NO_TEXT_REF, getValue(versionList, DBTable.TEXT_REF));
                params.put(KEY.VERSION_NO_HOUSEHOLD, getValue(versionList, DBTable.HOUSEHOLD));

                params.put(KEY.VERSION_NO_TOPIC_INFO, getValue(versionList, DBTable.TOPIC_INFO));
                params.put(KEY.VERSION_NO_COURTYARD_MEETING_TOPIC_MONTH, getValue(versionList, DBTable.COURTYARD_MEETING_TOPIC_MONTH));
                params.put(KEY.VERSION_NO_COURTYARD_MEETING_TARGET, getValue(versionList, DBTable.COURTYARD_MEETING_TARGET));

                params.put(KEY.VERSION_NO_MATERNAL_CARE_INFO, getValue(versionList, DBTable.MATERNAL_CARE_INFO));
                params.put(KEY.VERSION_NO_USER_SCHEDULE, getValue(versionList, DBTable.USER_SCHEDULE));
                params.put(KEY.VERSION_NO_IMMUNIZATION_FOLLOWUP, getValue(versionList, DBTable.IMMUNIZATION_FOLLOWUP));
                params.put(KEY.VERSION_NO_IMMUNIZATION_SERVICE, getValue(versionList, DBTable.IMMUNIZATION_SERVICE));

                params.put(KEY.VERSION_NO_CCS_STATUS, getValue(versionList, DBTable.CCS_STATUS));
                params.put(KEY.VERSION_NO_CCS_ELIGIBLE, getValue(versionList, DBTable.CCS_ELIGIBLE));
                params.put(KEY.VERSION_NO_CCS_TREATMENT, getValue(versionList, DBTable.CCS_TEREATMENT));


                params.put(KEY.VERSION_NO_REPORT_ASST, getValue(versionList, DBTable.REPORT_ASST));
                params.put(KEY.VERSION_NO_QUESTIONNAIRE_BROAD_CATEGORY, getValue(versionList, DBTable.QUESTIONNAIRE_BROAD_CATEGORY));
                params.put(KEY.VERSION_NO_QUESTIONNAIRE_SERVICE_CATEGORY, getValue(versionList, DBTable.QUESTIONNAIRE_SERVICE_CATEGORY));


                params.put(KEY.TRANS_REF_INTERVIEW_MASTER, App.getContext().getDB().getMax(DBTable.PATIENT_INTERVIEW_MASTER, Column.TRANS_REF));
                params.put(KEY.TRANS_REF_MATERNAL_ABORTION, App.getContext().getDB().getMax(DBTable.MATERNAL_ABORTION, Column.TRANS_REF));
                params.put(KEY.TRANS_REF_MATERNAL_BABY_INFO, App.getContext().getDB().getMax(DBTable.MATERNAL_BABY_INFO, Column.TRANS_REF));
                params.put(KEY.TRANS_REF_MATERNAL_DELIVERY, App.getContext().getDB().getMax(DBTable.MATERNAL_DELIVERY, Column.TRANS_REF));
                params.put(KEY.TRANS_REF_MATERNAL_INFO, App.getContext().getDB().getMax(DBTable.MATERNAL_INFO, Column.TRANS_REF));
                params.put(KEY.TRANS_REF_MATERNAL_SERVICE, App.getContext().getDB().getMax(DBTable.MATERNAL_SERVICE, Column.TRANS_REF));
                params.put(KEY.TRANS_REF_COURTYARD_MEETING, App.getContext().getDB().getMax(DBTable.COURTYARD_MEETING, Column.TRANS_REF));
                params.put(KEY.TRANS_REF_DEATH_REGISTRATION, App.getContext().getDB().getMax(DBTable.DEATH_REGISTRATION, Column.TRANS_REF));

                params.put(KEY.VERSION_NO_SUGGESTION_TEXT, App.getContext().getDB().getMax(DBTable.SUGGESTION_TEXT, Column.VERSION_NO));
                params.put(KEY.LAST_UPDATE_TIME_MEDICINE_STOCK, App.getContext().getDB().getMax(DBTable.MEDICINE_STOCK, Column.LAST_UPDATE_ON));

                params.put(KEY.TRANS_REF_PATIENT_INTERVIEW_DOCTOR_FEEDBACK, App.getContext().getDB().getMax(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, Column.TRANS_REF));

                params.put(KEY.VERSION_NO_FCM_CONFIGURATIONS, Utility.parseLong(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "FCM_CONFIGURATIONS", KEY.VERSION_NO_FCM_CONFIGURATIONS)));
                params.put(KEY.VERSION_NO_NOTIFICATION, App.getContext().getDB().getMax(DBTable.NOTIFICATION_MASTER, Column.VERSION_NO));
                params.put(KEY.VERSION_NO_MEDICINE_REQUISITION, App.getContext().getDB().getMax(DBTable.MEDICINE_REQUISITION_MASTER, Column.VERSION_NO));
                params.put(KEY.VERSION_NO_MEDICINE_RECEIVE, App.getContext().getDB().getMax(DBTable.MEDICINE_RECEIVE_MASTER, Column.VERSION_NO));
                params.put(KEY.VERSION_NO_MEDICINE_ADJUSTMENT, App.getContext().getDB().getMax(DBTable.MEDICINE_ADJUSTMENT_MASTER, Column.VERSION_NO));
                params.put(KEY.VERSION_NO_MEDICINE_CONSUMPTION, App.getContext().getDB().getMax(DBTable.MEDICINE_CONSUMPTION_MASTER, Column.VERSION_NO));
                params.put(KEY.VERSION_NO_SESSION, App.getContext().getDB().getMax(DBTable.SATELLITE_SESSION, Column.VERSION_NO));


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return params;
    }

    public synchronized static void imageOrientation(String imagePath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            final Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);


            ExifInterface ei = new ExifInterface(imagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int rotationAngle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true);

            FileOutputStream out = new FileOutputStream(imagePath);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized String getFormatedData(String caption, String value, String formatType) {
        if (formatType.equals("JSON")) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("CAPTION", caption);
                jsonObject.put("VALUE", value);
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (formatType.equals("XML")) {
            return "<OPTION><CAPTION>" + caption + "</CAPTION><VALUE>" + value + "</VALUE></OPTION>";
        } else if (formatType.equals("CSV")) {
            return value;
        }
        return "";
    }

    public static synchronized JSONArray toJsonArray(Cursor cursor) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        if (cursor.moveToFirst()) {
            do {
                int totalColumn = cursor.getColumnCount();
                JSONObject jsonObject = new JSONObject();
                for (int columnIndex = 0; columnIndex < totalColumn; columnIndex++) {
                    jsonObject.put(cursor.getColumnName(columnIndex), cursor.getString(columnIndex));
                }
                jsonArray.put(jsonObject);

            } while (cursor.moveToNext());
        }
        return jsonArray;
    }

    @SuppressLint("LongLogTag")
    public static boolean isActivityOpen(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            Log.e("Current Open Activity info ", cn.getPackageName() + " " + cn.getClassName());
            if (cn.getPackageName().equalsIgnoreCase(context.getPackageName())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String formatTextBylanguage(String original, String local) {
        if ("en".equals(App.getContext().getAppSettings().getLanguage())) {
            return original;
        }
        if (local != null && !local.equalsIgnoreCase("")) {
            return local;
        } else {
            return original;
        }
    }

    public static void checkUserActive(Context context, ResponseData responseData) {
        try {
            if (responseData != null && responseData.getErrorCode() != null && responseData.getErrorCode().equals("0003")) {
                AppPreference.putLong(context, Column.STATE, 0);
                App.getContext().getUserInfo().setState(0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public static String getFcmConfigurationValue(Context context, String name, String key, String defultValue) {


        try {
            String confiData = AppPreference.getString(context, KEY.FCM_CONFIGURATION, "[]");
            JSONArray configArry = new JSONArray(confiData);
            return JSONParser.getFcmConfigValue(configArry, name, key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return defultValue;

    }

	/*public static String getUniqueId(Context context){
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        try {
            if ( androidId != null ) {
                return UUID.nameUUIDFromBytes(androidId.getBytes("utf8")) + "";
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();

                return (deviceId != null) ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) + "" : UUID.randomUUID() + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }*/


    public static String getAdminPassword(Context context) {
        //String deviceIMEI = Utility.getIMEInumber(context);
        // int lastNumber= Integer.parseInt(deviceIMEI.charAt(deviceIMEI.length()-1)+"");


        Calendar calender = Calendar.getInstance();
        int dayOfWeek = calender.get(Calendar.DAY_OF_WEEK) + 1;
        if (dayOfWeek == 8) {
            dayOfWeek = 1;
        }

        int dayOfMonth = calender.get(Calendar.DAY_OF_MONTH);

        int x = 0;

        try {
            //x=Integer.parseInt(deviceIMEI.substring(deviceIMEI.length()-lastNumber));
        } catch (Exception e) {

        }


        //return  (x+ dayOfWeek + dayOfMonth)+"";
        return (dayOfWeek + dayOfMonth) + "";

    }

    public static String humanReadablePrescription(String prescriptionJson) {


        String prescription = "";

        MedicineInfo medicineInfo = new MedicineInfo();
        try {
            JSONObject mJson = new JSONObject(prescriptionJson);
            if (mJson.has("MED_ID")) {
                medicineInfo.setMedId(Integer.parseInt(mJson.getString("MED_ID")));
            }
            if (mJson.has("MED_TYPE")) {
                medicineInfo.setMedicineType(mJson.getString("MED_TYPE"));
            }
            if (mJson.has("GEN_NAME")) {
                medicineInfo.setGenericName(mJson.getString("GEN_NAME"));
            }
            if (mJson.has("MED_NAME")) {
                medicineInfo.setBrandName(mJson.getString("MED_NAME"));
            }

            if (mJson.has("MED_QTY")) {
                medicineInfo.setRequiredQuantity(mJson.getString("MED_QTY"));
            }

            if (mJson.has("SALE_QTY")) {
                medicineInfo.setSoldQuantity(mJson.getString("SALE_QTY"));
            }

//			medicineInfo.setMedId(Integer.parseInt(mJson.getString("MED_ID")));
//			medicineInfo.setMedicineType(mJson.getString("MED_TYPE"));
//			medicineInfo.setGenericName(mJson.getString("GEN_NAME"));
//			medicineInfo.setBrandName(mJson.getString("MED_NAME"));
//			medicineInfo.setRequiredQuantity(mJson.getString("MED_QTY"));
//			medicineInfo.setSoldQuantity(mJson.getString("SALE_QTY"));

            if (mJson.has("MED_DURATION"))
                medicineInfo.setDoseDuration(mJson.getString("MED_DURATION"));
            if (mJson.has("MTR")) medicineInfo.setMedicineTakingRule(mJson.getString("MTR"));
            if (mJson.has("MTR_LBL"))
                medicineInfo.setMedicineTakingRuleLabel(mJson.getString("MTR_LBL"));
            if (mJson.has("MTR_SF"))
                medicineInfo.setSmsFormatmedicineTakingRule(mJson.getString("MTR_SF"));
            if (mJson.has("AFM")) medicineInfo.setAdviceForMedicine(mJson.getString("AFM"));
            if (mJson.has("AFM_SF"))
                medicineInfo.setSmsFormatadviceForMedicine(mJson.getString("AFM_SF"));
            if (mJson.has("SF")) medicineInfo.setSmsFormat(mJson.getString("SF"));
            prescription = medicineInfo.getMedicineType() + " " + medicineInfo.getBrandName();

            if (!TextUtility.getEmptyStringFromNull(medicineInfo.getMedicineTakingRule()).isEmpty()) {
                prescription += " (" + medicineInfo.getMedicineTakingRule() + ")";
            }
            return prescription;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void adjustFCMInterviewIdInfeedback() {
        JSONArray jsonArrayForUpdate = null;
        try {
            jsonArrayForUpdate = App.getContext().getDB().getInterviewDoctorFeedbackForUpdate();
            if (jsonArrayForUpdate != null && jsonArrayForUpdate.length() > 0) {
                for (int d = 0; d < jsonArrayForUpdate.length(); d++) {
                    long docFollowupId = JSONParser.getLong(jsonArrayForUpdate.getJSONObject(d), Column.DOC_FOLLOWUP_ID);
                    long pimFcmInterviewId = JSONParser.getLong(jsonArrayForUpdate.getJSONObject(d), "PIM_FCM_INTERVIEW_ID");
                    App.getContext().getDB().updatePatientInterviewDoctorFeedbackBasedOnFCMInterviewId(docFollowupId, pimFcmInterviewId);

                }
            }

        } catch (Exception e) {

        }
    }

    public static JsonObject getQuestionAnswerJsonForFeedbackUpdate(PatientInterviewDoctorFeedback doctorFeedback) {
        try {
            String qData = App.getContext().getDB().getTextCaption("QUESTION_ANSWER_JSON", "DR_FEEDBACK_JSON");

            JsonParser parser = new JsonParser();
            JsonElement elem = parser.parse(qData);
            JsonArray elemArr = elem.getAsJsonArray();
            JsonObject jsonObject = (JsonObject) elemArr.get(0);
            return jsonObject;


        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            return null;

        }
    }

    public static JsonObject modifyQuestionAnswerJson(JsonObject jsonObject, String targetQues, String firstTierFld, String firstTierVal,
                                                      String secondTier, String secondTierFld, String secondTierVal) {
        try {
            JsonObject questionnaireObj = (JsonObject) ((JsonObject) jsonObject.get("QUESTIONNAIRE_DATA")).get("questionnaire");
            JsonObject targetObj = (JsonObject) questionnaireObj.get(targetQues);
            if (targetQues != null) {
                if (firstTierFld != null && firstTierVal != null) {
                    targetObj.add(firstTierFld, new JsonPrimitive(firstTierVal));
                }
                if (secondTier != null && secondTierFld != null && secondTierVal != null) {
                    JsonArray arr = (JsonArray) targetObj.get(secondTier);
                    JsonObject obj = (JsonObject) arr.get(0);
                    obj.add(secondTierFld, new JsonPrimitive(secondTierVal));
                }
            }
            return jsonObject;

        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
            return null;
        }


    }


    public static void showNotification(Context ctx, JSONObject notificationObject) {
        String title = "";
        String content = "";
        int notificationId = new Random().nextInt(9999);

        title = JSONParser.getString(notificationObject, "TITLE");
        content = JSONParser.getString(notificationObject, "CONTENT");


        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + ctx.getPackageName() + "/" + R.raw.mhealthringtonenotification);


//        Intent intent = new Intent(ctx, CustomNotificationShowActivity.class);
//        intent.putExtra("DATA_PASS", notificationObject.toString());
//
//        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationCompat.Builder mBuilder;
//        String channelId = "";
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            NotificationChannel mChannel = new NotificationChannel(
//                    channelId, channelId, importance);
//
//            if (mNotificationManager != null)
//                mNotificationManager.createNotificationChannel(mChannel);
//            mBuilder =
//                    new NotificationCompat.Builder(ctx, channelId)
//                            .setSmallIcon(R.drawable.bell)
//                            .setContentTitle("[mHealth] " + title)
//                            .setContentText(content)
//                            .setContentIntent(PendingIntent.getActivity(ctx, notificationId, intent, 0))
//                            .setAutoCancel(false)
//                            .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
//            //mNotificationManager.createNotificationChannel(mChannel);
//        } else {
//            mBuilder =
//
//                    new NotificationCompat.Builder(ctx)
//                            .setSmallIcon(R.drawable.bell)
//                            .setContentTitle("[mHealth] " + title)
//                            .setContentText(content)
//                            .setContentIntent(PendingIntent.getActivity(ctx, notificationId, intent, 0))
//                            .setAutoCancel(false)
//                            .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
//        }
//        mBuilder.setProgress(0, 0, false);
//        MediaPlayer mp = MediaPlayer.create(ctx, R.raw.mhealthringtonenotification);
//        mp.start();
//        //		mNotificationManager.cancel(9999);
//        mNotificationManager.notify(notificationId, mBuilder.build());
    }


    public static void showNotificationForDoctorFeedback(Context ctx, JSONObject notificationObject, String feedbackMsg, int notificationId, String title, String message, int imageId, boolean showProgress, boolean isAutoCancel, ArrayList<NotificationItem> notificationItems) {

//        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + ctx.getPackageName() + "/" + R.raw.mhealthringtonenotification);
//
//
//        Intent intent = new Intent(ctx, CustomNotificationShowActivity.class);
//        intent.putExtra("NOTIF_ITEM_LIST", notificationItems);
//        intent.putExtra("NOTIF_MESSAGE", feedbackMsg);
//        intent.putExtra("NOTIF_ID", notificationId);
//        intent.putExtra("NOTIF_OBJECT", notificationObject.toString());
//        //intent.putExtra("NOTIF_ITEM_LIST",notificationItems);
//
//
//        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationCompat.Builder mBuilder;
//        String channelId = notificationId + "";
//        String channelName = notificationId + "";
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            NotificationChannel mChannel = new NotificationChannel(
//                    channelId, channelName, importance);
//
//            if (mNotificationManager != null)
//                mNotificationManager.createNotificationChannel(mChannel);
//            mBuilder =
//                    new NotificationCompat.Builder(ctx, channelId)
//                            .setSmallIcon(R.drawable.bell)
//                            .setContentTitle("[mHealth] " + title)
//                            .setContentText(message)
//                            .setContentIntent(PendingIntent.getActivity(ctx, notificationId, intent, 0))
//                            .setAutoCancel(isAutoCancel)
//                            .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
//
//            //mNotificationManager.createNotificationChannel(mChannel);
//        } else {
//            mBuilder =
//                    new NotificationCompat.Builder(ctx)
//                            .setSmallIcon(R.drawable.bell)
//                            .setContentTitle("[mHealth] " + title)
//                            .setContentText(message)
//                            .setContentIntent(PendingIntent.getActivity(ctx, notificationId, intent, 0))
//                            .setAutoCancel(isAutoCancel)
//                            .setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
//        }
//        mBuilder.setProgress(0, 0, showProgress);
//        MediaPlayer mp = MediaPlayer.create(ctx, R.raw.mhealthringtonenotification);
//        mp.start();
//        //		mNotificationManager.cancel(9999);
//        mNotificationManager.notify(notificationId, mBuilder.build());
    }


    public static String getDeviceSuperInfo() {
        String deviceInfo = "";
        try {
            deviceInfo += "" + Build.BRAND + "(" + Build.MODEL + "-" + Build.VERSION.SDK_INT + ")";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceInfo;
    }

    public static byte[] generateKey(String password) throws Exception {
        byte[] keyStart = password.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    public static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }
    public static void setDefaultImage(String gender, String imagePath, ImageView ivHolderName) {
        int imageResourse = R.drawable.ic_satelite;
        int imageResourseBac = R.drawable.border_rounded_corner_male;
        if (FileOperaion.isExist(imagePath)) {
            ivHolderName.setImageBitmap(
                    FileOperaion.decodeImageFile(imagePath, 60)
            );
        } else {
            if (gender != null) {
                if (gender.trim().equalsIgnoreCase("f")) {
                    imageResourse = R.drawable.ic_default_woman;
                    imageResourseBac = R.drawable.border_rounded_corner_female;
                } else if (gender.trim().toLowerCase().equalsIgnoreCase("m")) {
                    imageResourse = R.drawable.ic_default_man;
                    imageResourseBac = R.drawable.border_rounded_corner_male;
                } else if (gender.trim().toLowerCase().equalsIgnoreCase("o")) {
                    imageResourse = R.drawable.ic_other_gender;
                    imageResourseBac = R.drawable.border_rounded_corner_male;
                }
            }
            ivHolderName.setImageResource(imageResourse);
            ivHolderName.setBackgroundResource(imageResourseBac);
        }


    }

    public static void setDefaultImage(String gender, String imagePath, CircularImageView ivHolderName) {
        int imageResourse = R.drawable.ic_satelite;
        int imageResourseBac = R.drawable.border_rounded_corner_male;
        if (FileOperaion.isExist(imagePath)) {
            ivHolderName.setImageBitmap(
                    FileOperaion.decodeImageFile(imagePath, 60)
            );
        } else {
            if (gender != null) {
                if (gender.trim().equalsIgnoreCase("f")) {
                    imageResourse = R.drawable.ic_default_woman;
                    imageResourseBac = R.drawable.border_rounded_corner_female;
                } else if (gender.trim().toLowerCase().equalsIgnoreCase("m")) {
                    imageResourse = R.drawable.ic_default_man;
                    imageResourseBac = R.drawable.border_rounded_corner_male;
                } else if (gender.trim().toLowerCase().equalsIgnoreCase("o")) {
                    imageResourse = R.drawable.ic_other_gender;
                    imageResourseBac = R.drawable.border_rounded_corner_male;
                }
            }
            ivHolderName.setImageResource(imageResourse);
//            ivHolderName.setBackgroundResource(imageResourseBac);
        }
    }
    public static void setGenter(String gender, String cloneText, TextView tvGender, Context ctx) {
        if (gender.toLowerCase(Locale.ROOT).equalsIgnoreCase("f")) {
            tvGender.setText("Female");
        } else if (gender.toLowerCase(Locale.ROOT).equalsIgnoreCase("m")) {
            tvGender.setText("Male");
        } else if (gender.toLowerCase(Locale.ROOT).equalsIgnoreCase("0")) {
            tvGender.setText("Others");
        } else {
            tvGender.setText(ctx.getResources().getString(R.string.data_not_found));
        }

        if (!cloneText.equalsIgnoreCase("")) {
            if (gender.toLowerCase(Locale.ROOT).equalsIgnoreCase("f")) {
                tvGender.setText("Female " + "(" + cloneText + ")");
            } else if (gender.toLowerCase(Locale.ROOT).equalsIgnoreCase("m")) {
                tvGender.setText("Male " + " (" + cloneText + ") ");
            } else if (gender.toLowerCase(Locale.ROOT).equalsIgnoreCase("0")) {
                tvGender.setText("Others " + " (" + cloneText + ") ");
            }
        } else {
            tvGender.setText(ctx.getResources().getString(R.string.data_not_found));
        }

    }

    public static void questionAnsToWebContent(){

    }


}
