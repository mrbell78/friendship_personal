package ngo.friendship.satellite.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import ngo.friendship.satellite.App
import ngo.friendship.satellite.MainActivity
import ngo.friendship.satellite.R
import ngo.friendship.satellite.asynctask.CommiunicationTask
import ngo.friendship.satellite.asynctask.TaskKey
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.communication.RequestData
import ngo.friendship.satellite.communication.ResponseData
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.constants.KEY
import ngo.friendship.satellite.constants.RequestName
import ngo.friendship.satellite.constants.RequestType
import ngo.friendship.satellite.databinding.ChangePasswordLayoutBinding
import ngo.friendship.satellite.interfaces.OnCompleteListener
import ngo.friendship.satellite.interfaces.OnDialogButtonClick
import ngo.friendship.satellite.utility.AppPreference
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.utility.Utility
import ngo.friendship.satellite.views.DialogView
import org.json.JSONException

class ChangePasswordActivity : BaseActivity(), View.OnClickListener, OnCompleteListener {
    private var binding: ChangePasswordLayoutBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.loadApplicationData(this)
        binding = ChangePasswordLayoutBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        title = resources.getString(R.string.change_password)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        binding!!.btnSendData.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }



    /**
     * Show a alert dialog with single button to display message.
     *
     * @param titleId     the string ID of dialog title
     * @param msg         will be displayed as the dialog message
     * @param imageId     the drawable id of image which will be set in dialog title
     * @param textColorId the text color id
     * @param et          the et
     */
    private fun showOneButtonDialog(
        titleId: Int,
        msg: String,
        imageId: Int,
        textColorId: Int,
        et: EditText?
    ) {
        val buttonMap = HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_close
        val exitDialog = DialogView(this, titleId, msg, textColorId, imageId, buttonMap)
        exitDialog.setOnDialogButtonClick(object : OnDialogButtonClick {
            override fun onDialogButtonClick(view: View?) {
                et?.requestFocus()
                if (imageId == R.drawable.information) {
                    App.getContext().readUserInfo(this@ChangePasswordActivity)
                    MainActivity.PASSWORD_CHANGED = true
                    finish()
                }
            }
        })
        exitDialog.show()
    }

    /**
     * Check provided password are valid.
     *
     * @return **true** If user entered valid data. **false** otherwise.
     */
    private fun checkDataValidity(): Boolean {
        var dataStr: String? = null
        val etOldPassword = findViewById<EditText>(R.id.et_old_password)
        dataStr = etOldPassword.text.toString()

        /*
         * Check if password field is empty
         */if (dataStr.equals("", ignoreCase = true)) {
            showOneButtonDialog(
                R.string.dialog_title,
                resources.getString(R.string.empty_password),
                R.drawable.error,
                Color.RED,
                etOldPassword
            )
            return false
        }

        /*
         * Check if given password length is less than 4
         */if (dataStr.length < 4) {
            showOneButtonDialog(
                R.string.dialog_title,
                resources.getString(R.string.invalid_password_length),
                R.drawable.error,
                Color.RED,
                etOldPassword
            )
            return false
        }

        /*
         * Check if given password is matched with current password
         */if (dataStr != App.getContext().userInfo.password) {
            showOneButtonDialog(
                R.string.dialog_title,
                resources.getString(R.string.worng_password),
                R.drawable.error,
                Color.RED,
                etOldPassword
            )
            return false
        }
        val etNewPassword = findViewById<EditText>(R.id.et_new_password)
        dataStr = etNewPassword.text.toString()

        /*
         * Check if new password field is empty
         */if (dataStr.equals("", ignoreCase = true)) {
            showOneButtonDialog(
                R.string.dialog_title,
                resources.getString(R.string.empty_password),
                R.drawable.error,
                Color.RED,
                etNewPassword
            )
            return false
        }

        /*
         * Check if new password length is less than 4
         */if (dataStr.length < 4) {
            showOneButtonDialog(
                R.string.dialog_title,
                resources.getString(R.string.invalid_password_length),
                R.drawable.error,
                Color.RED,
                etNewPassword
            )
            return false
        }
        val etReNewPassword = findViewById<EditText>(R.id.et_confirm_new_password)
        dataStr = etReNewPassword.text.toString()

        /*
         * Check if confirmation password field is empty
         */if (dataStr.equals("", ignoreCase = true)) {
            showOneButtonDialog(
                R.string.dialog_title,
                resources.getString(R.string.empty_password),
                R.drawable.error,
                Color.RED,
                etReNewPassword
            )
            return false
        }

        /*
         * Check if confirmation password length is less than 4
         */if (dataStr.length < 4) {
            showOneButtonDialog(
                R.string.dialog_title,
                resources.getString(R.string.invalid_password_length),
                R.drawable.error,
                Color.RED,
                etReNewPassword
            )
            return false
        }

        /*
         * Check if confirmation password matched with new password
         */if (dataStr != etNewPassword.text.toString()) {
            showOneButtonDialog(
                R.string.dialog_title,
                resources.getString(R.string.password_not_matched),
                R.drawable.error,
                Color.RED,
                etReNewPassword
            )
            return false
        }
        return true
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_send_data -> if (checkDataValidity()) {
                if (SystemUtility.isConnectedToInternet(this)) {
                    try {
                        val etNewPassword = findViewById<EditText>(R.id.et_new_password)
                        val password = etNewPassword.text.toString().trim { it <= ' ' }
                        val request = RequestData(
                            RequestType.USER_GATE,
                            RequestName.PW_CHANGE,
                            Constants.MODULE_DATA_GET
                        )
                        request.data.put("PASS_MD5", Utility.md5(password))
                        request.data.put(
                            "PASS_ENCRYPTED",
                            Utility.generateEncryptedPassword(password)
                        )
                        val task = CommiunicationTask(
                            this,
                            request,
                            R.string.retrieving_data,
                            R.string.please_wait
                        )
                        task.setCompleteListener(this)
                        task.execute()
                    } catch (e: JSONException) {
                        // TODO Auto-generated catch block
                        e.printStackTrace()
                    }
                } else {
                    SystemUtility.openInternetSettingsActivity(this)
                }
            }

            else -> {}
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    override fun onBackPressed() {}
    override fun onComplete(msg: Message) {
        if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
            val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
            App.showMessageDisplayDialog(
                this,
                resources.getString(R.string.network_error),
                R.drawable.error,
                Color.RED
            )
        } else {
            val response = msg.data.getSerializable(TaskKey.DATA0) as ResponseData?
            if (response!!.responseCode.equals("00", ignoreCase = true)) {
                App.showMessageDisplayDialog(
                    this,
                    response.errorCode + "-" + response.errorDesc,
                    R.drawable.error,
                    Color.RED
                )
            } else {
                val etNewPassword = findViewById<EditText>(R.id.et_new_password)
                AppPreference.putString(this, KEY.PASSWORD, etNewPassword.text.toString())
                showOneButtonDialog(
                    R.string.dialog_title,
                    resources.getString(R.string.password_change_successful),
                    R.drawable.information,
                    Color.BLACK,
                    null
                )
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        App.getContext().onStartActivity(this)
    }

    public override fun onStop() {
        super.onStop()
        App.getContext().onStartActivity(this)
    }
}