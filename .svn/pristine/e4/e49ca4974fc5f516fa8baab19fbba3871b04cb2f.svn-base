package ngo.friendship.satellite.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.constants.Column
import ngo.friendship.satellite.constants.KEY
import ngo.friendship.satellite.databinding.ActivityCautionBinding
import ngo.friendship.satellite.model.TextRef
import ngo.friendship.satellite.ui.login.LoginPinActivity
import ngo.friendship.satellite.utility.AppPreference
import ngo.friendship.satellite.views.AppToast

class CautionActivity : BaseActivity(), View.OnClickListener {
    lateinit var cautionTextRefList: ArrayList<TextRef>
    var splashText: String? = null
    private lateinit var binding: ActivityCautionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_caution)
        setContentView(binding.root)
        val mimeType = "text/html"
        val encoding = "UTF-8"
        try {
            cautionTextRefList = App.getContext().db.getTextRef("SPLASH2_TEXT")
        }catch (e:Exception){e.printStackTrace()}
        val html = StringBuilder()
        if (cautionTextRefList.size > 0) {
            splashText = cautionTextRefList.get(0).textCaption
            html.append(splashText + "")
        } else {
            html.append("")
        }
        binding.container.loadDataWithBaseURL("", html.toString(), mimeType, encoding, "")
       binding.btnNext.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnNext -> {
                val intent = intent
                val redirectPageValue = intent.getIntExtra("valueToRedirect", 0)
                if (redirectPageValue == 0) {
                    AppToast.showToast( this@CautionActivity, Integer.valueOf(R.string.sdcard_not_present))
                    finish()
                } else if (redirectPageValue == 1) {
                    val userCode = AppPreference.getString(this@CautionActivity, Column.USER_LOGIN_ID, "")
                    val userPassword = AppPreference.getString(this@CautionActivity, KEY.PASSWORD, "")
                    val state = AppPreference.getLong(this@CautionActivity, Column.STATE, -1)
                    val orgId = AppPreference.getLong(this@CautionActivity, Column.ORG_ID, -1)
                    val userInfo =  App.getContext().db.getUserInfo(userCode, userPassword, orgId, state)
                    if (userInfo!=null){
                        this@CautionActivity.startActivity(Intent(this@CautionActivity,LoginPinActivity::class.java))
                        finish()
                    }else{
                        this@CautionActivity.startActivity(Intent(this@CautionActivity,LoginActivity::class.java))
                        finish()
                    }
                } else {
                    this@CautionActivity.startActivity(Intent(this@CautionActivity,LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}