package ngo.friendship.satellite.ui.beneficiary.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.constants.ActivityDataKey
import ngo.friendship.satellite.databinding.ActivityBeneficiariesDetailsBinding
import ngo.friendship.satellite.jsonoperation.JSONParser
import ngo.friendship.satellite.model.Beneficiary
import ngo.friendship.satellite.model.QuestionnaireInfo
import ngo.friendship.satellite.ui.InterviewActivity
import ngo.friendship.satellite.utility.FileOperaion
import ngo.friendship.satellite.views.MdiTextView
import org.json.JSONObject

@AndroidEntryPoint
class BeneficiaryProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityBeneficiariesDetailsBinding;
    var beneficiary: Beneficiary = Beneficiary();
    var questionnaireBenefReg: QuestionnaireInfo? = null
    var questionnaireBenefMig: QuestionnaireInfo? = null
    var questionnaireBenefDeath: QuestionnaireInfo? = null
    var benefProfileString = ""
    var benefProfileFromInterviewString = ""
    var benefCode: String = ""
    var benefName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeneficiariesDetailsBinding.inflate(
            layoutInflater
        )
        beneficiary = Beneficiary();
        setContentView(binding.root)
        enableBackButton()
        title = getString(R.string.benef_profile)
        var gson = Gson()
        if (intent.getStringExtra("BENEF_PROFILE") != null && intent.getStringExtra("BENEF_PROFILE") != "") {

            benefProfileString = intent.getStringExtra("BENEF_PROFILE").toString()
            beneficiary = gson.fromJson(benefProfileString, Beneficiary::class.java)
            binding.model = beneficiary

            if (beneficiary.userId == App.getContext().getUserInfo().userId) {
                binding.tvServiceType.setText("Self : " + beneficiary.fcmName + " (${beneficiary.address})");
            } else {
                binding.tvServiceType.setText("FCM : " + beneficiary.fcmName + " (${beneficiary.address})")
            }
        } else if (intent.getStringExtra("BENEF_PROFILE_FROM_INTERVIEW_LIST") != null && intent.getStringExtra(
                "BENEF_PROFILE_FROM_INTERVIEW_LIST"
            ) != ""
        ) {
            benefProfileFromInterviewString =
                intent.getStringExtra("BENEF_PROFILE_FROM_INTERVIEW_LIST").toString()

            Log.e("dd", "dd")

            var data = JSONObject(benefProfileFromInterviewString)
            //var interview:InterviewInfoSyncUnsync = gson.fromJson(benefProfileString, InterviewInfoSyncUnsync::class.java)

            //beneficiary = App.getContext().db.getBeneficiaryById(JSONParser.getString(data,"beneficiaryCode"));
            try {
                 benefCode = JSONParser.getString(data, "beneficiaryCode")
                 benefName = JSONParser.getString(data, "benefName")
                var benefAddress: String = JSONParser.getString(data, "benefAddress")
                var beneficiarGender: String = JSONParser.getString(data, "beneficiarGender")
                var age: String = JSONParser.getString(data, "age")
                var userId: Long = JSONParser.getLong(data, "userId")
                beneficiary.benefCodeFull = benefCode
                beneficiary.benefCode = benefCode
                beneficiary.userId = userId
                beneficiary.benefName = benefName
                beneficiary.address = benefAddress
                beneficiary.gender = beneficiarGender
                beneficiary.age = age
            } catch (e: Exception) {
                e.printStackTrace()
            }


//                if (beneficiary.age.length > 0) {
//                    if (beneficiary.age.contains("y")) {
//                        beneficiary.age = age
//                    } else {
//                        beneficiary.age = age + "y"
//                    }
//                }

            if (beneficiary.userId == App.getContext().getUserInfo().userId) {

                try {
                    var beneficiarys = App.getContext().db.getSingleBenef(benefCode)
                    if (beneficiarys != null) {
                        beneficiary = beneficiarys;
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


            try {
                if (benefCode.length >= 5) {
                    val benefCodeShorts = benefCode.takeLast(5)
                    beneficiary.benefCodeShort = benefCodeShorts
                    beneficiary.benefCode = benefCodeShorts
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            benefProfileString = gson.toJson(beneficiary)



            binding.model = beneficiary
            try {
                var name: String =
                    App.getContext().db.getUserName(beneficiary.benefCodeFull.substring(0, 9))
                if (name.length > 0) {
                    binding.tvServiceType.setText("FCM : " + name + " (${beneficiary.address})")
                } else {
                    var name: String = App.getContext().db.getUserName(beneficiary.userId)
                    binding.tvServiceType.setText("Self : " + name + " (${beneficiary.address})")
                }

//            if (beneficiary.benefCodeFull.contains(App.getContext().getUserInfo().userCode)) {
//                binding.tvServiceType.setText("FCM : " +App.getContext().db.getUserName(beneficiary.benefCode.substring(0, 9)) + " (${beneficiary.address})")
//            } else {
//
//            }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            questionnaireBenefReg = App.getContext().db.getQuestionnaire("BENEFICIARY_REGISTRATION")
            questionnaireBenefMig = App.getContext().db.getQuestionnaire("BENEFICIARY_MIGRATION")
            questionnaireBenefDeath = App.getContext().db.getQuestionnaire("DEATH_REGISTRATION")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.tvBenefName.setText("" + beneficiary.benefName)
        binding.tvBeneficiaryCode.setText("" + beneficiary.benefCode)



        if (beneficiary.getGender().equals("F", ignoreCase = true)) {
            binding.mobileNo.setText("Female (" + beneficiary.getAge() + ")")
        } else if (beneficiary.getGender().equals("M", ignoreCase = true)) {
            binding.mobileNo.setText("Male (" + beneficiary.getAge() + ")")
        } else if (beneficiary.getGender().equals("O", ignoreCase = true)) {
            binding.mobileNo.setText("Others (" + beneficiary.getAge() + ")")
        }

        if (FileOperaion.isExist(beneficiary.getBenefImagePath())) {
            try {
                binding.ivBeneficiary.setImageBitmap(
                    FileOperaion.decodeImageFile(
                        beneficiary.getBenefImagePath(),
                        60
                    )
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else {
            try {
                if (beneficiary.getGender().equals("F", ignoreCase = true)) {
                    binding.ivBeneficiary.setImageResource(R.drawable.ic_default_woman)
                } else if (beneficiary.getGender().equals("M", ignoreCase = true)) {
                    binding.ivBeneficiary.setImageResource(R.drawable.ic_default_man)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        isTabActiveDeactive(
            binding.llTabServiceList,
            binding.tvTabSyncdServiceList,
            binding.mdiTabServiceList,
            binding.llTabPersonalInfo,
            binding.tvTabSyncdPersonalInfo,
            binding.mdiTabPersonalInfo
        )
        loadFragment(PreviousServiceFragment.newInstance(benefProfileString))
        binding.llTabServiceList.setOnClickListener({

            loadFragment(PreviousServiceFragment.newInstance(benefProfileString))

            isTabActiveDeactive(
                binding.llTabServiceList,
                binding.tvTabSyncdServiceList,
                binding.mdiTabServiceList,
                binding.llTabPersonalInfo,
                binding.tvTabSyncdPersonalInfo,
                binding.mdiTabPersonalInfo
            )
        })

        binding.llTabPersonalInfo.setOnClickListener({
            loadFragment(PersonalInfoFragment.newInstance(benefProfileString))
            isTabActiveDeactive(
                binding.llTabPersonalInfo,
                binding.tvTabSyncdPersonalInfo,
                binding.mdiTabPersonalInfo,
                binding.llTabServiceList,
                binding.tvTabSyncdServiceList,
                binding.mdiTabServiceList

            )
        })
    }

    private fun isTabActiveDeactive(
        llTabactvieName: LinearLayout,
        tvTabActiveName: TextView,
        mdiActive: MdiTextView,
        llTabDeactvieName1: LinearLayout,
        tvTabDeactvieName1: TextView,
        mdiDeactive1: MdiTextView
    ) {

        llTabactvieName.setBackground(
            ContextCompat.getDrawable(
                this, R.drawable.border_rounded_corner
            )
        )
        tvTabActiveName.setTextColor(ContextCompat.getColor(this, R.color.white))
        mdiActive.setTextColor(ContextCompat.getColor(this, R.color.white))

        llTabDeactvieName1.setBackground(
            ContextCompat.getDrawable(
                this, R.drawable.border_rounded_corner_white
            )
        )

        tvTabDeactvieName1.setTextColor(ContextCompat.getColor(this, R.color.black))
        mdiDeactive1.setTextColor(ContextCompat.getColor(this, R.color.black))

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_beneficiary, menu)
        try {
            val actionUpdateBeneficiary = menu.findItem(R.id.action_update_beneficiary)
            if (beneficiary.userId == App.getContext().getUserInfo().userId) {
                actionUpdateBeneficiary.isVisible = true
            } else {
                actionUpdateBeneficiary.isVisible = false
            }


//            actionMigration.setTitle("" + (questionnaireBenefMig?.questionnaireTitle ?: ""))
//            actionDeathRegistration.setTitle(
//                "" + (questionnaireBenefDeath?.questionnaireTitle ?: "")
//            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_update_beneficiary -> {
                updateBenef()

                return true
            }

//            R.id.action_death_registration -> {
//                DialogView.showDeviceIDDialog(this)
//                return true
//            }
//
//            R.id.action_migration -> {
//                DialogView.showDeviceIDDialog(this)
//                return true
//            }

            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    private fun updateBenef() {
        try {
            var jsonStr: String? = ""
            val questionnaireInfo = App.getContext().db.getQuestionnaire("BENEFICIARY_REGISTRATION")
            if (questionnaireInfo != null) {
                val beneficiary = App.getContext().db.getBeneficiaryInfo(
                    beneficiary.getBenefCodeFull(),
                    questionnaireInfo.questionnaireName
                )
                jsonStr = questionnaireInfo.getQuestionnaireJSON(this@BeneficiaryProfileActivity)
                jsonStr = JSONParser.preapreRegirtrationQuestionear(jsonStr, beneficiary)
            }
            val entryPrams: ArrayList<String>? = null
//        entryPrams.add(householdNumberStr)
            val intent = Intent(this@BeneficiaryProfileActivity, InterviewActivity::class.java)
            intent.putExtra(ActivityDataKey.QUESTIONNAIRE_JSON, jsonStr)
            intent.putExtra(ActivityDataKey.BENEFICIARY_CODE, beneficiary.benefCodeFull)
            intent.putExtra(ActivityDataKey.BENEFICIARY_ID, beneficiary.benefId)
            intent.putExtra(ActivityDataKey.INTERVIEW_TYPE, ActivityDataKey.UPDATE_BENEF)
            intent.putExtra(ActivityDataKey.PARAMS, entryPrams)
            intent.putExtra("EDIT_HOUSEHOLD", false)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}