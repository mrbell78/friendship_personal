package ngo.friendship.satellite.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.databinding.ActivityLauncherBinding
import ngo.friendship.satellite.interfaces.OnDialogButtonClick
import ngo.friendship.satellite.ui.login.LoginPinActivity
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.views.AppToast
import ngo.friendship.satellite.views.DialogView

class LauncherActivity : BaseActivity() {
    private lateinit var binding: ActivityLauncherBinding
    var isSuccessful = false
    var redirectPageValue = 0
    var isSplashTextShowValue: String? = null
    var permissions = arrayOf(
        "android.permission.CAMERA",
        "android.permission.RECORD_AUDIO",
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
        "android.permission.READ_PHONE_STATE",
        "android.permission.CALL_PHONE"
    )

    //            "android.permission.WRITE_EXTERNAL_STORAGE",
    //                    "android.permission.READ_EXTERNAL_STORAGE",
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_launcher)
        setContentView(binding.root)


//        if(App.getContext().isExtSDCardPresent()){
//            App.loadApplicationData(this);
//            String imgPath=App.getContext().getCommonDir(this)+ File.separator+ App.getContext().getUserInfo().getLoginImagePathMobileDir();
//            if(FileOperaion.isExist(imgPath))
//            {
//                imageView.setImageBitmap(FileOperaion.decodeImageFile(imgPath, 100));
//            }
//        }


    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST -> compareUserPermission(permissions, grantResults)
        }
    }
    private fun compareUserPermission(permissions: Array<String>, grantResults: IntArray) {

        var isGrantAll = true
        for (i in permissions.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isGrantAll = false

            }
        }
        if (isGrantAll) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    try {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                        val uri = Uri.fromParts("package", this.packageName, null)
                        intent.data = uri
                       startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    checkSDCard()
                }
            } else {
                checkSDCard()
            }
        } else {
            dialogData();
//            if (!hasPermissions(this@LauncherActivity, *permissions)) {
//                ActivityCompat.requestPermissions(this, permissions, REQUEST)
//            }
//            else{
//              //  dialogData();
//            }

        }

    }
    override fun onResume() {
        super.onResume()
        App.getContext().readAppSettings(this)
        //        if (App.getContext().getAppSettings() == null) {
//            App.getContext().readAppSettings(this);
//        }
        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this)
        }
        animation()
    }
    private fun checkSDCardAndPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!hasPermissions(this, *permissions)) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST)
            } else {
                checkSDCard()
            }
        } else {
            checkSDCard()
        }
    }
    private fun checkSDCard() {
        try {
            Log.e("SD CARD", "CALLED")
            if (App.getContext().isExtSDCardPresent) {
                isSuccessful = App.loadApplicationData(this@LauncherActivity)
                App.getContext().copyAlgorithImageFromDrawable(this)

                //            if (!isSuccessful) {
//                AppToast.showToast(LauncherActivity.this, Integer.valueOf(R.string.sdcard_not_present));
//                LauncherActivity.this.finish();
//            } else if (App.getContext().getUserInfo().isActive()) {
//                LauncherActivity.this.startActivity(new Intent(LauncherActivity.this, LoginPinActivity.class));
//                LauncherActivity.this.finish();
//            } else {
//                LauncherActivity.this.startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
//                LauncherActivity.this.finish();
//            }
                if (!isSuccessful) {
                    AppToast.showToast(
                        this@LauncherActivity,
                        Integer.valueOf(R.string.sdcard_not_present)
                    )
                    finish()
                } else {
                    isSplashTextShowValue = App.getContext().appSettings.isSplashTextShow
                    val data = App.getContext().userInfo
                    val active = App.getContext().userInfo.isActive
                    if (App.getContext().userInfo.isActive && isSplashTextShowValue == "YES") {
                        redirectPageValue = 1
                        val intent = Intent(this@LauncherActivity, CautionActivity::class.java)
                        intent.putExtra("valueToRedirect", redirectPageValue)
                        startActivity(intent)
                        finish()
                    }
                    if (!App.getContext().userInfo.isActive && isSplashTextShowValue == "YES") {
                        redirectPageValue = 1
                        val intent = Intent(this@LauncherActivity, CautionActivity::class.java)
                        intent.putExtra("valueToRedirect", redirectPageValue)
                        startActivity(intent)
                        finish()
                    }
                    if (!App.getContext().userInfo.isActive && isSplashTextShowValue == "NO") {
                        this@LauncherActivity.startActivity(
                            Intent(
                                this@LauncherActivity,
                                LoginActivity::class.java
                            )
                        )
                        finish()
                    }
                    if (App.getContext().userInfo.isActive && isSplashTextShowValue == "NO") {
                        this@LauncherActivity.startActivity(
                            Intent(
                                this@LauncherActivity,
                                LoginPinActivity::class.java
                            )
                        )
                        finish()
                    }
                }
            } else {
                AppToast.showToast(this, Integer.valueOf(R.string.sdcard_not_present))
                finish()
            }
        } catch (e: Exception) {
           dialogData()
            e.printStackTrace()
        }
    }
    fun animation() {
        try {
            Log.e("ANIMATION", "CALLED")
            val animation = AnimationUtils.loadAnimation(this@LauncherActivity, R.anim.scaleup)
            binding.ivFcmPic.animation = animation
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    Log.e("ANIMATION", "STARTED")
                }

                override fun onAnimationEnd(animation: Animation) {
                    Log.e("ANIMATION", "ENDED")
                    checkSDCardAndPermission()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    fun dialogData() {
        val buttonMap = HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_ok
        //        buttonMap.put(2, R.string.btn_settings);
        val exitDialog = DialogView(
            this,
            R.string.dialog_title,
            R.string.permission_string_data,
            R.drawable.information,
            buttonMap
        )
        exitDialog.setOnDialogButtonClick(object : OnDialogButtonClick {
            override fun onDialogButtonClick(view: View?) {
                when (view!!.id) {
                    1 -> finish()
                }
            }
        })
        exitDialog.show()
    }
    public override fun onStart() {
        super.onStart()
        try {
            App.getContext().onStartActivity(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    public override fun onStop() {
        super.onStop()
        App.getContext().onStartActivity(this)
    }
    companion object {
        private const val REQUEST = 112
        private fun hasPermissions(context: Activity?, vararg permissions: String): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }
    }
}