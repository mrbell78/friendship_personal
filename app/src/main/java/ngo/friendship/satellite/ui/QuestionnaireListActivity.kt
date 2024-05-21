package ngo.friendship.satellite.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.adapter.BasemHealthAdapter
import ngo.friendship.satellite.asynctask.CommiunicationTask
import ngo.friendship.satellite.asynctask.MHealthTask
import ngo.friendship.satellite.asynctask.Task
import ngo.friendship.satellite.asynctask.TaskKey
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.communication.RequestData
import ngo.friendship.satellite.communication.ResponseData
import ngo.friendship.satellite.constants.ActivityDataKey
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.constants.DBTable
import ngo.friendship.satellite.constants.KEY
import ngo.friendship.satellite.constants.RequestName
import ngo.friendship.satellite.constants.RequestType
import ngo.friendship.satellite.databinding.ActivityQuestionnaireListBinding
import ngo.friendship.satellite.databinding.ServiceQuestionnaireItemGridRowBinding
import ngo.friendship.satellite.databinding.ServiceQuestionnaireItemListRowBinding
import ngo.friendship.satellite.error.MhealthException
import ngo.friendship.satellite.interfaces.OnCompleteListener
import ngo.friendship.satellite.interfaces.OnDialogButtonClick
import ngo.friendship.satellite.jsonoperation.JSONParser
import ngo.friendship.satellite.model.Beneficiary
import ngo.friendship.satellite.model.Household
import ngo.friendship.satellite.model.QuestionnaireInfo
import ngo.friendship.satellite.model.QuestionnaireList
import ngo.friendship.satellite.utility.AppPreference
import ngo.friendship.satellite.utility.ItemDecorationAlbumColums
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.utility.Utility
import ngo.friendship.satellite.views.AppToast
import ngo.friendship.satellite.views.DialogView
import java.util.Random

class QuestionnaireListActivity : BaseActivity() {
    var categoryId = 0
    var categoryName: String? = null
    var categoryCaption: String? = null
    lateinit var questionnaireList: ArrayList<QuestionnaireInfo>
    var activityPath: String? = null
    lateinit var entryPrams: ArrayList<String>;
    lateinit var beneficiaryList: ArrayList<Beneficiary>;
    lateinit var binding: ActivityQuestionnaireListBinding;
    lateinit var mAdapterList: BasemHealthAdapter<QuestionnaireInfo, ServiceQuestionnaireItemListRowBinding>
    lateinit var mAdapterGrid: BasemHealthAdapter<QuestionnaireInfo, ServiceQuestionnaireItemGridRowBinding>
    lateinit var colors_500: IntArray
    var colorGenerator = Random()
    var beneficiary: Beneficiary = Beneficiary();
    var serviceViewType = 2
    lateinit var actionGridView: MenuItem
    lateinit var actionListView: MenuItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionnaireListBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        App.loadApplicationData(this)

        binding.layoutSync.llPenddingSync.visibility = View.GONE
        binding.layoutSync.viewLine.visibility = View.GONE
        binding.layoutSync.llAllSync.visibility = View.GONE
        binding.layoutSync.viewLine2.visibility = View.GONE

        binding.layoutSync.llGetData.setOnClickListener { v ->
            if (SystemUtility.isConnectedToInternet(this)) {
                showDataRetrieveConfirmationPrompt(resources.getString(R.string.retrieve_data_confirmation))
            } else {
                SystemUtility.openInternetSettingsActivity(this)
            }
        }

        binding.layoutSync.syncLayoutOpen.setOnClickListener { v ->
            binding.layoutSync.llGetData.visibility = View.VISIBLE
            binding.layoutSync.cvSyncLayout.visibility = View.VISIBLE
            binding.layoutSync.syncLayoutOpen.visibility = View.GONE
        }


        binding.layoutSync.ivCloseSync.setOnClickListener { v ->
            binding.layoutSync.llGetData.visibility = View.GONE
            binding.layoutSync.cvSyncLayout.visibility = View.GONE
            binding.layoutSync.syncLayoutOpen.visibility = View.VISIBLE
        }

        questionnaireList = ArrayList();
        val b = intent.extras
        // Get category id
        App.getContext().readUserInfo(this@QuestionnaireListActivity)
        categoryId = b!!.getInt(ActivityDataKey.QUESTIONNAIRE_CATEGORY_ID)
        categoryName = b.getString(ActivityDataKey.QUESTIONNAIRE_CATEGORY_NAME)
        categoryCaption = b.getString(ActivityDataKey.QUESTIONNAIRE_CATEGORY_CAPTION)
        if (intent.getStringExtra("BENEF_PROFILE") != null && intent.getStringExtra("BENEF_PROFILE") !== "") {
            val benefProfile = intent.getStringExtra("BENEF_PROFILE")
            beneficiary = Gson().fromJson(benefProfile, Beneficiary::class.java)
            //title =                resources.getString(R.string.service_list) + " (" + beneficiary.getBenefName() + " -" + beneficiary.getBenefCode() + ")"
            title =     resources.getString(R.string.service_list) + " (" + beneficiary.getBenefCode()  +" - "+ beneficiary.getBenefName() + " - "+ beneficiary.getGender() +"/"+beneficiary.getAge()+")"
        } else {
            title = resources.getString(R.string.service_list)
        }
        entryPrams = ArrayList()
        //        setTitle("" + categoryCaption);
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        serviceViewType =
            AppPreference.getInt(this@QuestionnaireListActivity, KEY.SERVICE_VIEW_MODE, 2)
        if (serviceViewType == 1) {
            itemAdapterList
        } else if (serviceViewType == 2) {
            getItemAdapterGrid()
        }
    }

    private fun showDataRetrieveConfirmationPrompt(msg: String) {
        val buttonMap = java.util.HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_yes
        buttonMap[2] = R.string.btn_no
        val dialog = DialogView(
            this@QuestionnaireListActivity,
            R.string.dialog_title,
            msg,
            R.drawable.information,
            buttonMap
        )
        dialog.setOnDialogButtonClick(object : OnDialogButtonClick {
            override fun onDialogButtonClick(view: View?) {
//                when (view!!.id) {
//                    1 -> retrieveRequisitionList(true)
//                }
                retriveQuestionaires();
                //AppToast.showToastWarnaing(this@QuestionnaireListActivity,"get data method not implemented yet")
            }
        })
        dialog.show()
    }

    val itemAdapterList: Unit
        get() {
            mAdapterList = object :
                BasemHealthAdapter<QuestionnaireInfo, ServiceQuestionnaireItemListRowBinding>() {
                override fun onItemLongClick(view: View, model: QuestionnaireInfo, position: Int) {}
                override fun onItemClick(
                    view: View,
                    questionnaireInfo: QuestionnaireInfo,
                    position: Int,
                    dataBinding: ServiceQuestionnaireItemListRowBinding
                ) {
                    openQuestionnair(position)
                }

                override fun onBindData(
                    model: QuestionnaireInfo,
                    position: Int,
                    dataBinding: ServiceQuestionnaireItemListRowBinding
                ) {
                    try {
                        dataBinding.tvQuestionnaireIcon.setText(Html.fromHtml("" + model.getIcon()));

                        dataBinding.tvQuestionnaireTitle.text = "" + model.questionnaireTitle
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }

                override val layoutResId: Int
                    get() = R.layout.service_questionnaire_item_list_row
            }

//        try {
//            showQuestionnaire(getQuestionnaireList(categoryId, App.getContext().getAppSettings().getLanguage()));
            questionnaireList =
                App.getContext().db.getQuestionnaireList(-1, App.getContext().appSettings.language)
            mAdapterList.addItems(questionnaireList)

//        int[] ATTRS = new int[]{android.R.attr.listDivider};
//        TypedArray a = obtainStyledAttributes(ATTRS);
//        Drawable divider = a.getDrawable(0);
//        InsetDrawable insetDivider = new InsetDrawable(divider, 16, 0, 16, 0);
//        a.recycle();
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(insetDivider);
//        binding.rvQuestionnaire.addItemDecoration(itemDecoration);
            binding.rvQuestionnaire.addItemDecoration(
                ItemDecorationAlbumColums(
                    this@QuestionnaireListActivity,
                    LinearLayoutManager.HORIZONTAL
                )
            )
            binding.rvQuestionnaire.removeItemDecoration(
                ItemDecorationAlbumColums(
                    this@QuestionnaireListActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            binding.rvQuestionnaire.setHasFixedSize(true)
            val mLayoutManager = LinearLayoutManager(this)
            binding.rvQuestionnaire.layoutManager = mLayoutManager
            binding.rvQuestionnaire.itemAnimator = DefaultItemAnimator()
            binding.rvQuestionnaire.adapter = mAdapterList
            //        } catch (MhealthException e) {
//            e.printStackTrace();
//        }
        }

    private fun getItemAdapterGrid() {
        mAdapterGrid = object :
            BasemHealthAdapter<QuestionnaireInfo, ServiceQuestionnaireItemGridRowBinding>() {
            override fun onItemLongClick(view: View, model: QuestionnaireInfo, position: Int) {}
            override fun onItemClick(
                view: View,
                questionnaireInfo: QuestionnaireInfo,
                position: Int,
                dataBinding: ServiceQuestionnaireItemGridRowBinding
            ) {
                openQuestionnair(position)
            }

            override fun onBindData(
                model: QuestionnaireInfo,
                position: Int,
                dataBinding: ServiceQuestionnaireItemGridRowBinding
            ) {

                try {
                    dataBinding.tvQuestionnaireIcon.setText(Html.fromHtml("" + model.getIcon()));
                    //dataBinding.tvQuestionnaireIcon.setMdiIcon("" + model.getIcon());
                    // dataBinding.tvQuestionnaireIcon.setText(Html.fromHtml("" + model.getIcon()));

                    //dataBinding.tvQuestionnaireTitle.text = "" + model.questionnaireTitle
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                //  dataBinding.tvQuestionnaireIcon.setText(Html.fromHtml("" + model.getIcon()));
//                dataBinding.tvQuestionnaireIcon.setText("")
                dataBinding.tvQuestionnaireTitle.text = "" + model.questionnaireTitle
                if (position == 0) {
                    dataBinding.tvQuestionnaireIcon.setTextColor(resources.getColor(R.color.grid_color_one))

                } else if (position == 1) {
                    dataBinding.tvQuestionnaireIcon.setTextColor(resources.getColor(R.color.grid_color_two))

                }else if (position == 2) {
                    dataBinding.tvQuestionnaireIcon.setTextColor(resources.getColor(R.color.grid_color_three))

                }else if (position == 3) {
                    dataBinding.tvQuestionnaireIcon.setTextColor(resources.getColor(R.color.grid_color_one))
                }
                else if (position == 4) {
                    dataBinding.tvQuestionnaireIcon.setTextColor(resources.getColor(R.color.grid_color_two))
                }
                else if (position == 5) {
                    dataBinding.tvQuestionnaireIcon.setTextColor(resources.getColor(R.color.grid_color_three))
                }


            }

            override val layoutResId: Int
                get() = R.layout.service_questionnaire_item_grid_row
        }
//        try {
//            showQuestionnaire(getQuestionnaireList(categoryId, App.getContext().getAppSettings().getLanguage()));
        questionnaireList =
            App.getContext().db.getQuestionnaireList(-1, App.getContext().appSettings.language)
        mAdapterGrid.addItems(questionnaireList)

//        int[] ATTRS = new int[]{android.R.attr.listDivider};
//        TypedArray a = obtainStyledAttributes(ATTRS);
//        Drawable divider = a.getDrawable(0);
//        InsetDrawable insetDivider = new InsetDrawable(divider, 16, 0, 16, 0);
//        a.recycle();
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(insetDivider);
//        binding.rvQuestionnaire.addItemDecoration(itemDecoration);
        binding.rvQuestionnaire.removeItemDecoration(
            ItemDecorationAlbumColums(
                this@QuestionnaireListActivity,
                LinearLayoutManager.HORIZONTAL
            )
        )
        binding.rvQuestionnaire.setHasFixedSize(true)
        val mLayoutManager: LinearLayoutManager = GridLayoutManager(this, 2)
        binding.rvQuestionnaire.layoutManager = mLayoutManager
        binding.rvQuestionnaire.itemAnimator = DefaultItemAnimator()
        binding.rvQuestionnaire.adapter = mAdapterGrid
        binding.rvQuestionnaire.addItemDecoration(
            ItemDecorationAlbumColums(
                this@QuestionnaireListActivity,
                LinearLayoutManager.HORIZONTAL
            )
        )
        //        binding.rvQuestionnaire.addItemDecoration(new ItemDecorationAlbumColums(QuestionnaireListActivity.this, LinearLayoutManager.VERTICAL));
//        } catch (MhealthException e) {
//            e.printStackTrace();
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.question_menu, menu)
        actionGridView = menu.findItem(R.id.action_grid_view)
        actionListView = menu.findItem(R.id.action_list_view)
        if (serviceViewType == 1) {
            actionGridView.setVisible(false)
            actionListView.setVisible(true)
        } else if (serviceViewType == 2) {
            actionGridView.setVisible(true)
            actionListView.setVisible(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.action_list_view -> {
                actionGridView.isVisible = true
                actionListView.isVisible = false
                serviceViewType = 2
                getItemAdapterGrid()
                AppPreference.putInt(this, KEY.SERVICE_VIEW_MODE, 2)
                return true
            }

            R.id.action_grid_view -> {
                actionGridView.isVisible = false
                actionListView.isVisible = true
                serviceViewType = 1
                itemAdapterList
                AppPreference.putInt(this, KEY.SERVICE_VIEW_MODE, 1)
                return true
            }

            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(MhealthException::class)
    fun getQuestionnaireList(categoryId: Int, langCode: String?): QuestionnaireList {
        val questionnaireList = App.getContext().db.getQuestionnaireList(categoryId, langCode)
        val allQuestionnaire = QuestionnaireList()
        allQuestionnaire.errorCode = 1
        allQuestionnaire.allQuestionnaire = questionnaireList
        return allQuestionnaire
    }

    private fun openQuestionnair(p: Int) {
        if (questionnaireList[p].promptForBeneficiary == 0L) {
            val isGust = java.lang.Boolean.parseBoolean(
                Utility.getFcmConfigurationValue(
                    this,
                    "default_beneficiary",
                    "is.default.beneficiary.guest",
                    "true"
                )
            )
            val defultBenefCode = Utility.getFcmConfigurationValue(
                this,
                "default_beneficiary",
                "default.beneficiary.code",
                "00000"
            )
            startInterview(questionnaireList[p], defultBenefCode, isGust)
            val houseHoldNumber = beneficiary.benefCode.substring(0, 3)
            beneficiaryList = Utility.getBeneficiaryList(
                beneficiary.benefCodeFull,
                questionnaireList[p].getBenefSelectionCriteria()
            );
//            if (beneficiaryList.size>0){
//                startInterview(questionnaireList[p], defultBenefCode, isGust)
//            }else{
//                AppToast.showToastWarnaing(this,"Benef Criteria not metch ")
//            }
            startInterview(questionnaireList[p], defultBenefCode, isGust)
        } else {
            val defultBenefCode = Utility.getFcmConfigurationValue(
                this,
                "default_beneficiary",
                "default.beneficiary.code",
                "00000"
            )
            val houseHoldNumber = beneficiary.benefCode.substring(0, 3)
            entryPrams.add(houseHoldNumber)
            beneficiaryList = Utility.getBeneficiaryList(
                beneficiary.benefCodeFull,
                questionnaireList[p].getBenefSelectionCriteria()
            );


            if (beneficiaryList.size > 0) {
                startInterview(questionnaireList[p], defultBenefCode, false)
            } else {
                AppToast.showToastWarnaing(
                    this,
                    resources.getString(R.string.this_service) + " " + beneficiary.benefName + " " + resources.getString(
                        R.string.not_applicable
                    )
                );
            }
//                boolean isAutoSelfCreateHouseHold = Boolean.parseBoolean(Utility.getFcmConfigurationValue(this, "default_beneficiary", "auto.sequence.and.self.create.hh", "false"));
//                if (isAutoSelfCreateHouseHold) {
//                    entryPrams = new ArrayList<>();
//                    if (questionnaireList.getAllQuestionnaire().get(p).getQuestionnaireName().equals(QuestionnaireName.BENEFICIARY_REGISTRATION)) {
//                        String houseHoldNumber = TextUtility.format("%03d", App.getContext().getDB().getLastHouseHoldNumberWithIncrement());
//                        if (houseHoldNumber.length() > 3) {
//                            AppToast.showToastWarnaing(QuestionnaireListActivity.this, " Maximum beneficiary limit over !!");
//                        } else {
//                            entryPrams.add(houseHoldNumber);
//                            entryPrams.add(App.getContext().getUserInfo().getUserCode() + houseHoldNumber);
//                            startAutoHouseHoldWithBeneficiaryRegInterview(questionnaireList.getAllQuestionnaire().get(p), null, false);
//                        }
//                    } else {
//                        ArrayList<Household> householdArrayList = App.getContext().getDB().getHouseholdListAutoSelected();
//                        showHouseHoldNumberInputDialogWithSelfCreateHousehold(householdArrayList, "", p);
//                    }
//
//
//                } else {
//                    showHouseHoldNumberInputDialogFormHealth(p);
//                }
        }
    }

    private fun startInterview(
        questionnaireInfo: QuestionnaireInfo?,
        benefCode: String,
        isGust: Boolean
    ) {
        entryPrams.add(beneficiary.benefCodeFull)
        val intent = Intent(this, InterviewActivity::class.java)
        intent.putExtra(
            ActivityDataKey.QUESTIONNAIRE_JSON,
            questionnaireInfo!!.getQuestionnaireJSON(this)
        )
        intent.putExtra(
            ActivityDataKey.ACTIVITY_PATH,
            getIntent().extras!!.getString(ActivityDataKey.QUESTIONNAIRE_CATEGORY_CAPTION)
        )
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_TITLE, questionnaireInfo.questionnaireTitle)
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_ID, questionnaireInfo.id)
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_NAME, questionnaireInfo.questionnaireName)
        intent.putExtra(ActivityDataKey.BENEFICIARY_CODE, beneficiary.benefCodeFull)
        intent.putExtra(ActivityDataKey.BENEFICIARY_ID, beneficiary.benefId)
        intent.putExtra(ActivityDataKey.PARAMS, entryPrams)
        intent.putExtra(ActivityDataKey.IS_GUST, isGust)
        intent.putExtra(ActivityDataKey.INTERVIEW_TYPE, ActivityDataKey.NEW)
        intent.putExtra(ActivityDataKey.SINGLE_PG_FROM_VIEW, questionnaireInfo.isSinglePgFormView)
        intent.putExtra("EDIT_HOUSEHOLD", true)
        startActivity(intent)
    }

    private fun startAutoHouseHoldWithBeneficiaryRegInterview(
        questionnaireInfo: QuestionnaireInfo,
        benefCode: String,
        isGust: Boolean
    ) {
        val intent = Intent(this, InterviewActivity::class.java)
        intent.putExtra(
            ActivityDataKey.QUESTIONNAIRE_JSON,
            questionnaireInfo.getQuestionnaireJSON(this)
        )
        intent.putExtra(
            ActivityDataKey.ACTIVITY_PATH,
            getIntent().extras!!.getString(ActivityDataKey.QUESTIONNAIRE_CATEGORY_CAPTION)
        )
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_TITLE, questionnaireInfo.questionnaireTitle)
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_ID, questionnaireInfo.id)
        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_NAME, questionnaireInfo.questionnaireName)
        intent.putExtra(ActivityDataKey.BENEFICIARY_CODE, benefCode)
        intent.putExtra(ActivityDataKey.PARAMS, entryPrams)
        intent.putExtra(ActivityDataKey.IS_GUST, isGust)
        intent.putExtra(ActivityDataKey.INTERVIEW_TYPE, ActivityDataKey.NEW)
        intent.putExtra(ActivityDataKey.NEW_HOUSEHOLD_BENEFICIARY_REG, "HOUSEHOLD_BENEFICIARY_REG")
        intent.putExtra(ActivityDataKey.SINGLE_PG_FROM_VIEW, questionnaireInfo.isSinglePgFormView)
        startActivity(intent)
    }

    fun showHouseHoldNumberInputDialogWithSelfCreateHousehold(
        householdArrayList: ArrayList<Household?>?,
        questionType: String?,
        questionPosition: Int
    ) {
        val buttonMap = HashMap<Int, Any>()
        //        buttonMap.put(1, R.string.btn_enter);
        buttonMap[1] = R.string.btn_cancel
        val exitDialog = DialogView(this, buttonMap)
        entryPrams = ArrayList()
        exitDialog.showHouseholdNumberInputDialogWithSelfCreateHousehold(
            this@QuestionnaireListActivity,
            householdArrayList,
            questionType,
            questionPosition
        )
    }

    private fun retriveQuestionaires() {
        val request =
            RequestData(
                RequestType.USER_GATE,
                RequestName.QUESTIONNAIRES,
                Constants.MODULE_DATA_GET
            )
        val refTable = java.util.HashMap<String, String>()
        refTable[KEY.VERSION_NO_QUESTIONNAIRE] = DBTable.QUESTIONNAIRE
        request.setParam1(Utility.getTableRef(refTable, this@QuestionnaireListActivity))
        val commiunicationTask = CommiunicationTask(
            this@QuestionnaireListActivity,
            request,
            R.string.retrieving_data,
            R.string.please_wait
        )
        commiunicationTask.setCompleteListener { msg ->
            if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                App.showMessageDisplayDialog(
                    this@QuestionnaireListActivity,
                    resources.getString(R.string.network_error),
                    R.drawable.error,
                    Color.RED
                )
            } else {
                val response: ResponseData =
                    msg.data.getSerializable(TaskKey.DATA0) as ResponseData;
                if (response.responseCode.equals("00", ignoreCase = true)) {
                    App.showMessageDisplayDialog(
                        this@QuestionnaireListActivity,
                        response.errorCode + "-" + response.errorCode,
                        R.drawable.error,
                        Color.RED
                    )
                } else {
                    val tsk = MHealthTask(
                        this@QuestionnaireListActivity,
                        Task.RETRIEVE_QUESTIONNAIRE_LIST,
                        R.string.retrieving_data,
                        R.string.please_wait
                    )
                    tsk.setParam(categoryId, App.getContext().appSettings.language, response)
                    tsk.setCompleteListener(OnCompleteListener {
                        if (it.getData().containsKey(TaskKey.ERROR_MSG)) {
                            val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                            App.showMessageDisplayDialog(
                                this@QuestionnaireListActivity,
                                resources.getString(R.string.saving_error),
                                R.drawable.error,
                                Color.RED
                            )

                        } else {
                            JSONParser.getLongNullAllow(response.paramJson, KEY.NEED_SAME_REQ)
                            if (JSONParser.getLongNullAllow(
                                    response.paramJson,
                                    KEY.NEED_SAME_REQ
                                ) == 1L
                            ) {
                                retriveQuestionaires()
                            } else {
                                var data: QuestionnaireList =
                                    msg.data.getSerializable(TaskKey.DATA0) as QuestionnaireList
                                questionnaireList = data.allQuestionnaire
                                showSuccesfullDataRetrievalDialog(
                                    resources.getString(R.string.retrieve_successfull),
                                    R.drawable.information,
                                    Color.BLACK
                                )
                            }
                        }
                    })


                }
            }
        }
        commiunicationTask.execute()
    }

    private fun showSuccesfullDataRetrievalDialog(msg: String, imageId: Int, messageColor: Int) {
        val buttonMap = java.util.HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_close
        val dialog = DialogView(this, R.string.dialog_title, msg, messageColor, imageId, buttonMap)
        dialog.show()
    }


}