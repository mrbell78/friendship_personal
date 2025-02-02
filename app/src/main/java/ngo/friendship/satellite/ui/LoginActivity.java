
package ngo.friendship.satellite.ui;

import static ngo.friendship.satellite.App.getContext;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.communication.ResponseData;

import androidx.navigation.ui.AppBarConfiguration;

import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.MainActivity;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.ActivityLoginBinding;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.UserInfo;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.FileOperaion;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.AppToast;
import ngo.friendship.satellite.views.DialogView;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoginBinding binding;
    private AppBarConfiguration appBarConfiguration;

    String[] entries_appbar_spinner = { "FR", "UG", "SK"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        App.loadApplicationData(this);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        AppPreference.putString(LoginActivity.this, Column.ORG_CODE, "FR");
        Spinner spinner= findViewById(R.id.spinner);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,entries_appbar_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //Toast.makeText(getApplicationContext(),entries_appbar_spinner[position] , Toast.LENGTH_LONG).show();
                AppPreference.putString(LoginActivity.this, Column.ORG_CODE, entries_appbar_spinner[position].trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        LanguageContextWrapper.wrap(this, AppPreference.getString(this, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE));

        String imgPath = getContext().getCommonDir(this) + File.separator + getContext().getUserInfo().getTitleLogoPathMobileDir();
        if (FileOperaion.isExist(imgPath)) {
            binding.ivMhealthLogo.setImageBitmap(FileOperaion.decodeImageFile(imgPath, 100));
        }


        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this);
        }

        binding.tvVersionName.setText(Utility.getVersionName(this));
        binding.btnLogin.setOnClickListener(this);


        binding.etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    tryLogin();
                }
                return false;
            }
        });



    }




    private void saveDataToLocalStorage(String userCode, String password, JSONObject data, boolean isNewUser) throws JSONException {

        if (isNewUser) {
            getContext().getDB().deleteDb();
            getContext().setDBManager(null);
            getContext().setAppSettings(null);
            AppPreference.remove(this, KEY.FCM_CONFIGURATION);
            App.destroyApp();
            getContext().loadApplicationData(this);
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(data.getLong(Column.USER_ID));
        userInfo.setUserName(data.getString(Column.USER_NAME));
        userInfo.setUserCode(data.getString(Column.USER_LOGIN_ID));
        userInfo.setLocationId(data.getLong(Column.LOCATION_ID));
        userInfo.setLocationCode(data.getString(Column.LOCATION_CODE));
        userInfo.setLocationName(JSONParser.getString(data,Column.LOCATION_NAME));
        userInfo.setPassword(password);
        userInfo.setState(1);
        userInfo.setOrgId(data.getLong(Column.ORG_ID));
        userInfo.setOrgCode(data.getString(Column.ORG_CODE));
        userInfo.setOrgName(JSONParser.getString(data, Column.ORG_NAME));
        userInfo.setOrgDesc(JSONParser.getString(data, Column.ORG_DESC));
        userInfo.setOrgAddress(JSONParser.getString(data, Column.ORG_ADDRESS));
        userInfo.setOrgCountry(JSONParser.getString(data, Column.ORG_COUNTRY));
        userInfo.setHeaderSmallLogoPath(JSONParser.getString(data, Column.HEADER_SMALL_LOGO_PATH));
        userInfo.setLoginImagePathMobile(JSONParser.getString(data, Column.LOGIN_IMAGE_PATH_MOBILE));
        userInfo.setTitleLogoPathMobile(JSONParser.getString(data, Column.TITLE_LOGO_PATH_MOBILE));
        userInfo.setAppTitleMobile(JSONParser.getString(data, Column.APP_TITLE_MOBILE));
        userInfo.setToken(JSONParser.getString(data, KEY.TOKEN));

        getContext().getDB().saveUserInfo(userInfo, null);

        AppPreference.putLong(this, Column.USER_ID, userInfo.getUserId());
        AppPreference.putString(this, KEY.PASSWORD, userInfo.getPassword());
        AppPreference.putString(this, KEY.OLD_DEVICE_ID, JSONParser.getString(data, "ASSIGNED_MOBILE_IMEI"));
        AppPreference.putString(this, Column.USER_LOGIN_ID, userCode);
        AppPreference.putLong(this, Column.STATE, userInfo.getState());
        AppPreference.putLong(this, Column.ORG_ID, userInfo.getOrgId());
        AppPreference.putString(this, Column.ORG_CODE, userInfo.getOrgCode());
        AppPreference.putString(this, Column.ORG_CODE, userInfo.getOrgCode());
//        if (data.has(KEY.USR_SL_NO)) {
//            AppPreference.putString(this, KEY.USR_SL_NO, JSONParser.getString(data, KEY.USR_SL_NO));
//        }
        if (data.has(KEY.TOKEN)) {
            AppPreference.putString(this, KEY.TOKEN, JSONParser.getString(data, KEY.TOKEN));
        }
        if (data.has(Column.USER_KEY)) {
            AppPreference.putString(this, Column.USER_KEY, data.getString(Column.USER_KEY));
        }
        getContext().setUserInfo(userInfo);
        AppPreference.putString(this, KEY.IS_USER_LOGINNED, "YES");


        JSONObject cloneInfo = null;
        if (data.has("CLONE_INFO")) {
            cloneInfo = data.getJSONObject("CLONE_INFO");
        }

        if (cloneInfo != null && cloneInfo.length() > 0) {
            AppPreference.putString(this, KEY.CLONE_INFO, cloneInfo.toString());
            getContext().getAppSettings().setCloneInfo(cloneInfo);
        } else {
            AppPreference.remove(this, KEY.CLONE_INFO);
            getContext().getAppSettings().setCloneInfo(null);
        }

        getContext().loadCommonResource(this);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        this.finish();
    }

    private boolean isDataValid(String orgCode, String userName, String password) {

        if (userName.equalsIgnoreCase("")) {
            showOneButtonDialog(R.string.dialog_title, R.string.empty_user_id, Color.RED, R.drawable.error);
            binding.etUserCode.requestFocus();
            return false;
        }

        if (password.equalsIgnoreCase("")) {
            showOneButtonDialog(R.string.dialog_title, R.string.empty_password, Color.RED, R.drawable.error);
            binding.etPassword.requestFocus();
            return false;
        }
        return true;
    }


    private void showOneButtonDialog(int titleId, Object msg, int colorId, int imageId) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView exitDialog = new DialogView(this, R.string.dialog_title, msg, colorId, imageId, buttonMap);
        exitDialog.show();
    }


    private void startServerAuthentication(final boolean isNewUser) {
        if (!SystemUtility.isConnectedToInternet(LoginActivity.this)) {
            SystemUtility.openInternetSettingsActivity(LoginActivity.this);
        } else {
            String orgCode =  AppPreference.getString(LoginActivity.this, Column.ORG_CODE,"");
            final String userCode = binding.etUserCode.getText().toString().trim();
            final String password = binding.etPassword.getText().toString().trim();
            RequestData request = new RequestData(RequestType.USER_GATE, RequestName.LOGIN, Constants.MODULE_DATA_GET);
           request.setOrgCode(orgCode);
            request.setUserCode(userCode);
            request.setPw(password);
            CommiunicationTask commiunicationTask = new CommiunicationTask(this, request, R.string.try_login, R.string.please_wait);
            commiunicationTask.setCompleteListener(new OnCompleteListener() {

                @Override
                public void onComplete(Message msg) {
                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                        String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                        App.showMessageDisplayDialog(LoginActivity.this, getResources().getString(R.string.network_error), R.drawable.error, Color.RED);
                    } else {
                        ResponseData responseData = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        if (responseData.getResponseCode().equalsIgnoreCase("00")) {
                            App.showMessageDisplayDialog(LoginActivity.this, responseData.getErrorCode() + "-" + responseData.getErrorDesc(), R.drawable.error, Color.RED);
                        } else {
                            try {
                                AppPreference.putString(LoginActivity.this, ActivityDataKey.SELECTED_FCM, "");
                                AppPreference.putString(LoginActivity.this, ActivityDataKey.SELECTED_LOCATION_AREA, "");
                                AppPreference.putString(LoginActivity.this, ActivityDataKey.SELECTED_FCM_STRING, "");
                                AppPreference.putString(LoginActivity.this, ActivityDataKey.SELECTED_LOCATION_NAME, "");
                                getContext().deleteDir(new File(getContext().getCommonDir(LoginActivity.this)));
                                saveDataToLocalStorage(userCode, password, responseData.getDataJson(), isNewUser);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
            commiunicationTask.execute();
        }
    }

    /**
     * Start the login process.
     */
    private void tryLogin() {
        if (getContext().getAppSettings() != null && (getContext().getGateWayBasePath() != null || getContext().getAlternativeGateWayBasePath() != null)) {
//            String orgCode = "FR";
            String userLoginId = binding.etUserCode.getText().toString().trim();
            String password =  binding.etPassword.getText().toString();
           String orgCode =  AppPreference.getString(LoginActivity.this, Column.ORG_CODE,"");

            if (isDataValid(orgCode, userLoginId, password)) {
                if (getContext().getUserInfo().isNewUser()) {
                    startServerAuthentication(true);
                } else if (getContext().getUserInfo().isDifferentUser(orgCode, userLoginId)) {
                    HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
                    buttonMap.put(1, R.string.btn_ok);
                    buttonMap.put(2, R.string.btn_close);
                    DialogView dialogView = new DialogView(this, R.string.dialog_title, R.string.different_user_code, R.color.warning, R.drawable.warning, buttonMap);
                    dialogView.setOnDialogButtonClick(new OnDialogButtonClick() {

                        @Override
                        public void onDialogButtonClick(View view) {
                            switch (view.getId()) {
                                case 1:
                                    startServerAuthentication(true);
                                    break;
                                case 2:
                                    binding.etUserCode.setText("");
                                    binding.etPassword.setText("");
                                    break;
                            }
                        }
                    });
                    dialogView.show();
                } else {
                    startServerAuthentication(false);
                }
            }
        } else {
            AppToast.showToast(this, R.string.empty_server_address_message);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                tryLogin();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);

        return true;



    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                DialogView.showAdminLoginDialog(this);
                return true;

            case R.id.menu_app_mode_switch:
                if (!App.getContext().isDemo()) {
                    changeAppMode(this);
                }
                return true;


            case R.id.menu_about:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(ActivityDataKey.WEB_URL, "file:///android_asset/html/about.html");
                intent.putExtra(ActivityDataKey.TYPE, "ABOUT");
                startActivity(intent);
                return true;
            case R.id.menu_my_device:
                DialogView.showDeviceIDDialog(this);
                return true;


            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeAppMode(final Activity context) {
        final String appMode = AppPreference.getAppMode(context);
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_ok);
        buttonMap.put(2, R.string.btn_close);
        DialogView dialogView = new DialogView(context, R.string.dialog_title, appMode.equals(AppPreference.LIVE) ? R.string.switch_app_mode_traning : R.string.switch_app_mode_live, R.color.warning, R.drawable.warning, buttonMap);
        dialogView.setOnDialogButtonClick(new OnDialogButtonClick() {
            @Override
            public void onDialogButtonClick(View view) {
                switch (view.getId()) {
                    case 1:
                        AppPreference.changeAppMode(context, appMode);
                        context.finish();
                        startActivity(new Intent(context, LauncherActivity.class));
                        break;
                }
            }
        });
        dialogView.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        getContext().onStartActivity(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().onStartActivity(this);
    }


}
