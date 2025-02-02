package ngo.friendship.satellite.ui.login

import android.app.Activity
import android.app.AlarmManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import ngo.friendship.satellite.App
import ngo.friendship.satellite.LanguageContextWrapper
import ngo.friendship.satellite.MainActivity
import ngo.friendship.satellite.R
import ngo.friendship.satellite.asynctask.CommiunicationTask
import ngo.friendship.satellite.asynctask.DownloadFileTask
import ngo.friendship.satellite.asynctask.TaskKey
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.communication.RequestData
import ngo.friendship.satellite.communication.ResponseData
import ngo.friendship.satellite.constants.ActivityDataKey
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.constants.KEY
import ngo.friendship.satellite.constants.RequestName
import ngo.friendship.satellite.constants.RequestType
import ngo.friendship.satellite.databinding.LoginPinLayoutBinding
import ngo.friendship.satellite.interfaces.OnDialogButtonClick
import ngo.friendship.satellite.interfaces.OnDownloadFileCompleteListener
import ngo.friendship.satellite.jsonoperation.JSONParser
import ngo.friendship.satellite.model.AppSettings
import ngo.friendship.satellite.model.AppVersionHistory
import ngo.friendship.satellite.ui.LauncherActivity
import ngo.friendship.satellite.ui.LoginActivity
import ngo.friendship.satellite.ui.WebViewActivity
import ngo.friendship.satellite.utility.AppPreference
import ngo.friendship.satellite.utility.Base64
import ngo.friendship.satellite.utility.FileOperaion
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.utility.Utility
import ngo.friendship.satellite.views.DialogView
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.Calendar

class LoginPinActivity : BaseActivity(), View.OnClickListener {
    var inputUserCode: String? = null
    var inputPass: String? = null
    var dlog: ProgressDialog? = null
    var appVersionHistory: AppVersionHistory? = null
    private val prevUserCode = ""
    var time = 0
    var notificaitonObj = JSONObject()
    lateinit var binding: LoginPinLayoutBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginPinLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        title = ""
        App.loadApplicationData(this)
        // StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
        val b = intent.extras

        val notifObject = b?.getString("NOTIF_OBJECT")
        if (notifObject != null) {
            notificaitonObj= JSONObject(notifObject)
            Log.e("SouceDes", notificaitonObj.toString())
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        // TODO Auto-generated method stub
        super.onResume()
        LanguageContextWrapper.wrap(
            this,
            AppPreference.getString(this, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)
        )
        val imgPath = App.getContext()
            .getCommonDir(this) + File.separator + App.getContext().userInfo.titleLogoPathMobileDir
        if (FileOperaion.isExist(imgPath)) {
            (findViewById<View>(R.id.mhealth_logo) as ImageView).setImageBitmap(
                FileOperaion.decodeImageFile(
                    imgPath,
                    100
                )
            )
        }
        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this)
        }
        binding.tvOne.setOnClickListener(this)
        binding.tvTwo.setOnClickListener(this)
        binding.tvThree.setOnClickListener(this)
        binding.tvFour.setOnClickListener(this)
        binding.tvFive.setOnClickListener(this)
        binding.tvSix.setOnClickListener(this)
        binding.tvSeven.setOnClickListener(this)
        binding.tvEight.setOnClickListener(this)
        binding.tvNine.setOnClickListener(this)
        binding.tvZero.setOnClickListener(this)
        binding.btnBackspace.setOnClickListener(this)
        binding.tvVersionName.text = Utility.getVersionName(this)
        readSavedDataAndShow()
        showVersionNotification()
        if (App.getContext().userInfo == null) {
            App.getContext().readUserInfo(this@LoginPinActivity)
        }

        //spcialPermissionCheckOnAlarmManager(LoginPinActivity.this);
    }

    private fun spcialPermissionCheckOnAlarmManager(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ctx.getSystemService(ALARM_SERVICE) as AlarmManager
            val hasPermission = alarmManager.canScheduleExactAlarms()
            if (!hasPermission) {
                try {
                    val intent = Intent()
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    val uri = Uri.fromParts("package", this.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showVersionNotification() {
        Log.e("LOGIN_ACTIVITY", "on resume call")
        val txtNoticicatuion = findViewById<TextView>(R.id.txt_notification)
        txtNoticicatuion.visibility = View.GONE
        appVersionHistory = App.getContext().db.getVersionHistory(
            Utility.getAppVersionCode(this),
            App.getContext().appSettings.language
        )
        if (appVersionHistory != null && appVersionHistory!!.versionNumber > Utility.getAppVersionCode(
                this
            )
        ) {
            txtNoticicatuion.visibility = View.VISIBLE
            txtNoticicatuion.tag = appVersionHistory
            txtNoticicatuion.text = appVersionHistory!!.updateNotification
            txtNoticicatuion.setOnClickListener { showInstallConfirmationPrompt(appVersionHistory!!) }
        } else if (appVersionHistory != null && appVersionHistory!!.versionNumber == Utility.getAppVersionCode(
                this
            ) && appVersionHistory!!.installFlag.equals(
                AppVersionHistory.FLAG_OPENED,
                ignoreCase = true
            )
        ) {
            appVersionHistory!!.installFlag = AppVersionHistory.FLAG_INSTALLED
            appVersionHistory!!.installDate = Calendar.getInstance().timeInMillis
            App.getContext().db.updateAppVersionHistory(appVersionHistory)
        }
    }

    override fun onPause() {
        super.onPause()
    }

    private fun readSavedDataAndShow() {
        val userInfo = App.getContext().userInfo
        if (userInfo.userId > 0) {
            binding.tvFcmCode.text = App.getContext().userInfo.userCode
            binding.tvFcmName.text = userInfo.userName
            //            binding.tvAppTitle.setText(userInfo.getAppTitleMobile());
            binding.tvCompanyName.text = userInfo.orgName
            if (userInfo.profilePicInString != null && userInfo.profilePicInString != "" && userInfo.profilePicInString.length > 7) {
                val stream: InputStream
                try {
                    stream = ByteArrayInputStream(Base64.decode(userInfo.profilePicInString))
                    val bitmap = BitmapFactory.decodeStream(stream)
                    binding.ivFcmPic.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Start the login process.
     */
    private fun tryLogin() {
        val password =
            binding.tvPin1.text.toString() + binding.tvPin2.text.toString() + binding.tvPin3.text.toString() + binding.tvPin4.text.toString()
        if (password == App.getContext().userInfo.password) {
            AppPreference.putString(this, KEY.IS_USER_LOGINNED, "YES")
            if (notificaitonObj.length() > 0) {
                try {
//                    Intent j = new Intent(this, MainActivity.class);
//                    j.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    startActivity(j);
//                    startActivity(new Intent(LoginPinActivity.this, NotificationShowActivity.class));
                } catch (e: Exception) {
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
                    // startActivity(new Intent(LoginPinActivity.this, MainDashboardActivity.class));
                    e.printStackTrace()
                }
            } else {

                startActivity(Intent(this@LoginPinActivity, MainActivity::class.java))
            }

            //finish();
            clearPin()
        } else {
            clearPin()
            val buttonMap = HashMap<Int, Any>()
            buttonMap[1] = R.string.btn_close
            val exitDialog = DialogView(
                this,
                R.string.dialog_title,
                R.string.login_failed,
                Color.RED,
                R.drawable.error,
                buttonMap
            )
            exitDialog.setOnDialogButtonClick(object : OnDialogButtonClick {
                override fun onDialogButtonClick(view: View?) {
                    clearPin()
                }
            })
            exitDialog.show()
        }
    }

    private fun clearPin() {
        binding.tvPin1.text = ""
        binding.tvPin2.text = ""
        binding.tvPin3.text = ""
        binding.tvPin4.text = ""
        binding.tvPin1.setBackgroundResource(R.drawable.circle_pin_normal)
        binding.tvPin2.setBackgroundResource(R.drawable.circle_pin_normal)
        binding.tvPin3.setBackgroundResource(R.drawable.circle_pin_normal)
        binding.tvPin4.setBackgroundResource(R.drawable.circle_pin_normal)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tv_one, R.id.tv_two, R.id.tv_three, R.id.tv_four, R.id.tv_five, R.id.tv_six, R.id.tv_seven, R.id.tv_eight, R.id.tv_nine, R.id.tv_zero -> {
                val txt = (view as TextView).text.toString()
                if (binding.tvPin1.text.toString() == "") {
                    binding.tvPin1.text = txt
                    binding.tvPin1.setBackgroundResource(R.drawable.circle_pin_fill)
                } else if (binding.tvPin2.text.toString() == "") {
                    binding.tvPin2.text = txt
                    binding.tvPin2.setBackgroundResource(R.drawable.circle_pin_fill)
                } else if (binding.tvPin3.text.toString() == "") {
                    binding.tvPin3.text = txt
                    binding.tvPin3.setBackgroundResource(R.drawable.circle_pin_fill)
                } else if (binding.tvPin4.text.toString() == "") {
                    binding.tvPin4.text = txt
                    binding.tvPin4.setBackgroundResource(R.drawable.circle_pin_fill)
                    val handler = Handler()
                    val r = Runnable { tryLogin() }
                    handler.postDelayed(r, 200) // 200 milliseconds
                }
            }

            R.id.btn_backspace -> if (binding.tvPin4.text.toString() != "") {
                binding.tvPin4.setBackgroundResource(R.drawable.circle_pin_normal)
                binding.tvPin4.text = ""
            } else if (binding.tvPin3.text.toString() != "") {
                binding.tvPin3.setBackgroundResource(R.drawable.circle_pin_normal)
                binding.tvPin3.text = ""
            } else if (binding.tvPin2.text.toString() != "") {
                binding.tvPin2.setBackgroundResource(R.drawable.circle_pin_normal)
                binding.tvPin2.text = ""
            } else if (binding.tvPin1.text.toString() != "") {
                binding.tvPin1.setBackgroundResource(R.drawable.circle_pin_normal)
                binding.tvPin1.text = ""
            }

            else -> {}
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // TODO Auto-generated method stub
        menuInflater.inflate(R.menu.menu_login_pin, menu)
        val menuItem = menu.findItem(R.id.menu_app_mode_switch)
        if (AppPreference.LIVE == AppPreference.getAppMode(this)) {
            menuItem.setTitle(R.string.switch_to_training)
        } else {
            menuItem.setTitle(R.string.switch_to_live)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                DialogView.showAdminLoginDialog(this)
                return true
            }

            R.id.menu_update -> {
                if (SystemUtility.isConnectedToInternet(this@LoginPinActivity)) {
                    try {
                        val request = RequestData(
                            RequestType.USER_GATE,
                            RequestName.APP_VERSION_HISTORY,
                            Constants.MODULE_DATA_GET
                        )
                        val refTable = HashMap<String, String>()
                        refTable[KEY.APP_VERSION_NUMBER] = " "
                        request.param1 = Utility.getTableRef(refTable, this)
                        request.param1.put("TYPE", "FORCE")
                        val commiunicationTask = CommiunicationTask(
                            this,
                            request,
                            R.string.retrieving_data,
                            R.string.please_wait
                        )
                        commiunicationTask.setCompleteListener { msg ->
                            if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                                val errorMsg =
                                    msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                                App.showMessageDisplayDialog(
                                    this@LoginPinActivity,
                                    resources.getString(R.string.network_error),
                                    R.drawable.error,
                                    Color.RED
                                )
                            } else {
                                val response =
                                    msg.data.getSerializable(TaskKey.DATA0) as ResponseData?
                                if (response!!.responseCode.equals("00", ignoreCase = true)) {
                                    App.showMessageDisplayDialog(
                                        this@LoginPinActivity,
                                        response.errorCode + "-" + response.errorDesc,
                                        R.drawable.error,
                                        Color.RED
                                    )
                                } else {
                                    try {
                                        var avh: AppVersionHistory? = null
                                        if (response.responseCode.equals(
                                                "01",
                                                ignoreCase = true
                                            ) && response.dataJson!!.has("APP_VERSION_HISTORY")
                                        ) {
                                            avh = JSONParser.parseAppVersionHistoryJSON(
                                                response.dataJson!!.getJSONObject("APP_VERSION_HISTORY")
                                            )
                                        }
                                        if (avh != null) {
                                            App.getContext().db.saveAppVersionHistoryOnReceived(avh)
                                            Log.e("AppVersionHistory save", avh.toString())
                                            avh.installFlag = AppVersionHistory.FLAG_RECEIVED
                                            showInstallConfirmationPrompt(avh)
                                        } else {
                                            App.showMessageDisplayDialog(
                                                this@LoginPinActivity,
                                                resources.getString(R.string.no_app_update_available),
                                                R.drawable.information,
                                                Color.BLACK
                                            )
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                        commiunicationTask.execute()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    SystemUtility.openInternetSettingsActivity(this@LoginPinActivity)
                }
                return true
            }

            R.id.menu_app_mode_switch -> {
                if (!App.getContext().isDemo) {
                    changeAppMode(this)
                } else {
                    val buttonMap = HashMap<Int, Any>()
                    buttonMap[1] = R.string.btn_close
                    val dialog = DialogView(
                        this,
                        R.string.dialog_title,
                        R.string.not_aplicable_for_demo,
                        R.drawable.warning,
                        buttonMap
                    )
                    dialog.show()
                }
                return true
            }

            R.id.menu_switch -> {
                startActivity(Intent(this, LoginActivity::class.java))
                return true
            }

            R.id.menu_about -> {
                startActivity(
                    Intent(
                        this,
                        WebViewActivity::class.java
                    ).putExtra(ActivityDataKey.WEB_URL, "file:///android_asset/html/about.html")
                        .putExtra(ActivityDataKey.TYPE, "ABOUT")
                )
                return true
            }

            R.id.menu_my_device -> {
                DialogView.showDeviceIDDialog(this)
                return true
            }

            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showInstallConfirmationPrompt(appVersionHistory: AppVersionHistory) {
        val buttonMap = HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_yes
        buttonMap[2] = R.string.btn_no
        val exitDialog = DialogView(
            this,
            R.string.dialog_title,
            appVersionHistory.updateDesc,
            R.drawable.information,
            buttonMap
        )
        exitDialog.setOnDialogButtonClick(object : OnDialogButtonClick {
            override fun onDialogButtonClick(view: View?) {
                when (view!!.id) {
                    1 -> if (appVersionHistory.installFlag != null && appVersionHistory.installFlag == AppVersionHistory.FLAG_OPENED && appVersionHistory.appPathLocal != null && File(
                            appVersionHistory.appPathLocal
                        ).exists()
                    ) {
//						File apk = new File(appVersionHistory.getAppPathLocal());
//						Intent i = new Intent();
//						i.setAction(Intent.ACTION_VIEW);
//						i.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
//						startActivity(i);
//						LoginPinActivity.this.finish();
                        val apk = File(appVersionHistory.appPathLocal)
                        var i = Intent()
                        i.action = Intent.ACTION_VIEW
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val apkURI = FileProvider.getUriForFile(
                                applicationContext,
                                applicationContext.packageName + ".provider", apk
                            )
                            i = Intent(Intent.ACTION_INSTALL_PACKAGE)
                            i.data = apkURI
                            i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                            // i.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
                        } else {
                            i.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            val apkURI = FileProvider.getUriForFile(
                                applicationContext,
                                applicationContext.packageName + ".provider", apk
                            )
                            i.setDataAndType(apkURI, "application/vnd.android.package-archive")
                            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        startActivity(i)
                        finish()
                    } else {
                        if (SystemUtility.isConnectedToInternet(this@LoginPinActivity)) {
                            //String outpath = App.getContext().getCommonDir(LoginPinActivity.this);
                            val outpath = App.getContext().getApkDir(this@LoginPinActivity)
                            val downloadFileTask = DownloadFileTask(
                                this@LoginPinActivity,
                                appVersionHistory.appPath,
                                outpath
                            )
                            downloadFileTask.setOnDownloadFileCompleteListener(
                                OnDownloadFileCompleteListener { filePath -> //							         if (filePath==null ) return ;
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
                                    if (filePath == null) return@OnDownloadFileCompleteListener
                                    appVersionHistory.installFlag = AppVersionHistory.FLAG_OPENED
                                    appVersionHistory.appPathLocal = filePath
                                    appVersionHistory.openDate = Calendar.getInstance().timeInMillis
                                    App.getContext().db.updateAppVersionHistory(appVersionHistory)
                                    val apk = File(filePath)
                                    val i = Intent()
                                    i.action = Intent.ACTION_VIEW
                                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                                        i.setDataAndType(
                                            Uri.fromFile(apk),
                                            "application/vnd.android.package-archive"
                                        )
                                    } else {
                                        i.flags =
                                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                        val apkURI = FileProvider.getUriForFile(
                                            applicationContext,
                                            applicationContext.packageName + ".provider", apk
                                        )
                                        i.setDataAndType(
                                            apkURI,
                                            "application/vnd.android.package-archive"
                                        )
                                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    startActivity(i)

                                    // for next release remove database because of interview table has new data
                                    //added on 6th May,2021
//									removeLocalDatabase();
//									App.loadApplicationData(LoginPinActivity.this);
                                    // end
                                    finish()
                                })
                            downloadFileTask.execute()
                        } else {
                            SystemUtility.openInternetSettingsActivity(this@LoginPinActivity)
                        }
                    }
                }
            }
        })
        exitDialog.showWebView()
    }

    fun changeAppMode(context: Activity) {
        val appMode = AppPreference.getAppMode(context)
        val buttonMap = HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_ok
        buttonMap[2] = R.string.btn_close
        val dialogView = DialogView(
            context,
            R.string.dialog_title,
            if (appMode == AppPreference.LIVE) R.string.switch_app_mode_traning else R.string.switch_app_mode_live,
            R.color.warning,
            R.drawable.warning,
            buttonMap
        )
        dialogView.setOnDialogButtonClick(object : OnDialogButtonClick {
            override fun onDialogButtonClick(view: View?) {
                when (view!!.id) {
                    1 -> {
                        AppPreference.changeAppMode(context, appMode)
                        context.finish()
                        startActivity(Intent(context, LauncherActivity::class.java))
                    }
                }
            }
        })
        dialogView.show()
    }

    public override fun onStart() {
        super.onStart()
        App.getContext().onStartActivity(this)
    }

    public override fun onStop() {
        super.onStop()
        App.getContext().onStartActivity(this)
    }

    private fun removeLocalDatabase() {
        AppPreference.putString(this@LoginPinActivity, ActivityDataKey.SELECTED_FCM, "");
        AppPreference.putString(this@LoginPinActivity, ActivityDataKey.SELECTED_LOCATION_AREA, "");
        AppPreference.putString(this@LoginPinActivity, ActivityDataKey.SELECTED_FCM_STRING, "");
        AppPreference.putString(this@LoginPinActivity, ActivityDataKey.SELECTED_LOCATION_NAME, "");
        App.getContext().db.deleteDb()
        App.getContext().deleteDir(File(App.getContext().getQuestionnaireJSONDir(this@LoginPinActivity)))
        App.getContext().deleteDir(File(App.getContext().getBeneficiaryImageDir(this@LoginPinActivity)))
        App.getContext().deleteDir(File(App.getContext().getVoiceFileDir(this@LoginPinActivity)))
        App.getContext().setDBManager(null)
        App.getContext().appSettings = null
        App.loadApplicationData(this@LoginPinActivity)
    }

    companion object {
        var needServerAuthentication = false
    }
}