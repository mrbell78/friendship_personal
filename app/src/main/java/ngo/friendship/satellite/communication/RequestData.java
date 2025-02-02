package ngo.friendship.satellite.communication;

import static android.content.ContentValues.TAG;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.error.ErrorCode;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.ModelProvider;
import ngo.friendship.satellite.utility.Utility;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class RequestData {
    private Context context;
    private String requestType;
    private String requestName;
    private String requestTime;
    private String moduleName;
    private String orgCode;
    private String userCode;
    private String pw;
    private String imei;
    private String old_imei;
    private Long orgId;
    private long dataLength;
    private JSONObject data;
    private String lang;
    private String requestAction = "";
    private JSONObject param1;
    private Map versionMap;
    public JSONObject MAIN_DATA;
    private long processStartTimeMillis;
    private List<File> files = new ArrayList<>();

    private RequestData() {
    }

    public RequestData(String requestType, String requestName, String moduleName) {
        this.requestType = requestType;
        this.requestName = requestName;
        this.moduleName = moduleName;
        this.param1 = new JSONObject();
        this.data = new JSONObject();
        this.orgId = App.getContext().getUserInfo().getOrgId();
        this.orgCode = App.getContext().getUserInfo().getOrgCode();
        this.userCode = App.getContext().getUserInfo().getUserCode();
        this.pw = App.getContext().getUserInfo().getPassword();
    }
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }


    public String getOldImei() {
        return old_imei;
    }

    public void setOldImei(String oldImei) {
        this.old_imei = oldImei;
    }

    public long getDataLength() {
        return dataLength;
    }

    public void setDataLength(long dataLength) {
        this.dataLength = dataLength;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getRequestAction() {
        return requestAction;
    }

    public void setRequestAction(String requestAction) {
        this.requestAction = requestAction;
    }

    public JSONObject getParam1() {
        return param1;
    }

    public void setParam1(JSONObject param1) {
        this.param1 = param1;
    }

    public void setProcessStartTimeMillis(long processStartTimeMillis) {
        this.processStartTimeMillis = processStartTimeMillis;
    }

    public long getProcessStartTimeMillis() {
        return processStartTimeMillis;
    }

    public void setContext(Context context) {
        this.context = context;
        this.imei = Utility.getIMEInumber(this.context);
        this.old_imei = Utility.getOldIMEInumber(this.context);
        this.moduleName = Utility.getFcmConfigurationValue(context, "MODULE", this.moduleName, "mHealth-FCM");
    }

    public Context getContext() {
        return context;
    }

    public Map getVersionMap() {
        return versionMap;
    }

    public void setVersionMap(Map versionMap) {
        this.versionMap = versionMap;
    }

    @Override
    public String toString() {
        return "Request{" + "requestType=" + requestType + ", requestName=" + requestName + ", requestTime=" + requestTime + ", userCode=" + userCode + ", pw=" + pw + ", imei=" + imei + ", old_imei=" + old_imei + ", dataLength=" + dataLength + ", data=" + data + ", lang=" + lang + ", requestAction=" + requestAction + ", param1=" + param1 + '}';
    }

    public String toJson() throws JSONException {
        Date date = new Date();


        JSONObject jObj = new JSONObject();
        jObj.put(Column.ORG_CODE, orgCode);
        jObj.put(KEY.USER_CODE, Utility.md5(userCode));
        jObj.put(KEY.PASSWORD, Utility.md5(pw));

        jObj.put(Column.ORG_ID, orgId);
        jObj.put(KEY.IMEI, imei);
        jObj.put(KEY.OLD_IMEI, old_imei);
        jObj.put("DEMO", App.getContext().isDemo());


        jObj.put(KEY.REQUEST_TYPE, requestType);
        jObj.put(KEY.REQUEST_NAME, requestName);
        jObj.put(KEY.MODULE_NAME, moduleName);
        jObj.put(KEY.REQUEST_TIME, Constants.ddIMMIyyyyHHmmss.format(date));
        jObj.put(KEY.REQUEST_ACTION, requestAction);
        if (data != null) {
            jObj.put(KEY.DATA_LENGTH, data.toString().replace("\\", "").length());
            jObj.put(KEY.DATA, data);
        } else {
            jObj.put(KEY.DATA, new JSONObject());
            jObj.put(KEY.DATA_LENGTH, 0);
        }
        jObj.put(KEY.LANG, App.getContext().getAppSettings().getLanguage());
        if (param1 != null) {
            jObj.put(KEY.PARAM1, param1);
        }else {
            jObj.put(KEY.PARAM1, new JSONObject());
        }
        jObj.put(KEY.LANG, App.getContext().getAppSettings().getLanguage());
        return jObj.toString();
    }

    public ResponseData send(String url,Context ctx) throws MhealthException {
        try {
            Log.e("REQUEST URL  : ", url);
            Log.e("REQUEST DATA : ", toJson());

            String data = null;
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            try {
                httppost.setHeader(KEY.app_version, ""+Utility.getVersionCode(ctx));
                String authValue = "Bearer " + AppPreference.getString(ctx, KEY.TOKEN,"");
                Log.e("authValue",""+authValue);
                httppost.setHeader(KEY.AUTHORIZATION, authValue);
            }catch (Exception e){
                e.printStackTrace();
            }
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.addBinaryBody("data", toJson().getBytes(HTTP.UTF_8));

            if (files != null && files.size() > 0) {
                for (File file : files) {
                    entityBuilder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
                }
            }

            httppost.setEntity(entityBuilder.build());
            HttpResponse httpResponse = httpclient.execute(httppost);
            HttpEntity resEntity = httpResponse.getEntity();

            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                App.LAST_SUCCESS_REQUEST_TIME = Calendar.getInstance().getTimeInMillis();
                App.STATUS_COLOR = context.getResources().getColor(R.color.online);

                inputStream = resEntity.getContent();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);

                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                inputStream.close();
                data = sb.toString();
                ResponseData response = ModelProvider.getResponseModel(new ResponseData(), data);
                httpclient.getConnectionManager().shutdown();
                if (App.getContext().isApplicationInForeground(ctx)){
//                    if (response.getErrorCode() != null) {
//                        if (response.getErrorCode().equals("0005")) {
//                            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
//                            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//                            String currentActivity = taskInfo.get(0).topActivity.getClassName();
//                            if (!currentActivity.equalsIgnoreCase("ngo.friendship.mhealth.activities.LoginActivity")) {
//                                Intent loginActivity = new Intent(ctx, LoginActivity.class);
//                                loginActivity.putExtra("INTENT_INVALITE_TOKEN", response.getErrorDesc());
//                                ctx.startActivity(loginActivity);
//                            }
//
//                        }
//                    }
                }

                return response;
            } else {
                App.STATUS_COLOR = context.getResources().getColor(R.color.offline);
                throw new MhealthException(ErrorCode.HTTP_STATUS, "STATUS CODE : " + httpResponse.getStatusLine().getStatusCode());
            }

        } catch (UnsupportedEncodingException e) {
            App.STATUS_COLOR = context.getResources().getColor(R.color.offline);
            throw new MhealthException(ErrorCode.UNSUPPORTED_ENCODING_EXCEPTION, "UNSUPPORTED ENCODING EXCEPTION");
        } catch (ClientProtocolException e) {
            App.STATUS_COLOR = context.getResources().getColor(R.color.offline);
            throw new MhealthException(ErrorCode.CLIENT_PROTOCOL_EXCEPTION, "CLIENT PROTOCOL EXCEPTION ");
        } catch (HttpHostConnectException e) {
            App.STATUS_COLOR = context.getResources().getColor(R.color.offline);
            throw new MhealthException(ErrorCode.HTTP_HOST_CONNECT_EXCEPTION, "HTTP HOST CONNECT EXCEPTION");
        } catch (ConnectTimeoutException e) {
            App.STATUS_COLOR = context.getResources().getColor(R.color.offline);
            throw new MhealthException(ErrorCode.CONNECT_TIMEOUT_EXCEPTION, "CONNECT TIMEOUT EXCEPTION");
        } catch (SocketTimeoutException e) {
            App.STATUS_COLOR = context.getResources().getColor(R.color.offline);
            throw new MhealthException(ErrorCode.SOCKET_TIMEOUT_EXCEPTION, "SOCKET TIMEOUT EXCEPTION");
        } catch (ConnectException e) {
            App.STATUS_COLOR = context.getResources().getColor(R.color.offline);
            throw new MhealthException(ErrorCode.CONNECT_EXCEPTION, "UNSUPPORTED ENCODING");
        } catch (IOException e) {
            App.STATUS_COLOR = context.getResources().getColor(R.color.offline);
            throw new MhealthException(ErrorCode.IO_EXCEPTION, "UNSUPPORTED_ENCODING");
        } catch (JSONException e) {
            App.STATUS_COLOR = context.getResources().getColor(R.color.offline);
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION");
        }


    }


    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleName() {
        return moduleName;
    }


    public void addFile(File file) {
        files.add(file);
    }

    public void clearFiles() {
        files.clear();
    }

    public List<File> getFiles() {
        return files;
    }
}
