package ngo.friendship.satellite.ui.my_service

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.os.Message
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.adapter.BasemHealthAdapter
import ngo.friendship.satellite.asynctask.CommiunicationTask
import ngo.friendship.satellite.asynctask.MHealthTask
import ngo.friendship.satellite.asynctask.Task
import ngo.friendship.satellite.asynctask.TaskKey
import ngo.friendship.satellite.communication.RequestData
import ngo.friendship.satellite.communication.ResponseData
import ngo.friendship.satellite.constants.*
import ngo.friendship.satellite.databinding.CommonServiceActivityBinding
import ngo.friendship.satellite.databinding.CommonServicesItemRowBinding
import ngo.friendship.satellite.databinding.FollowUpDialogBinding
import ngo.friendship.satellite.databinding.FollowUpDialogItemBinding
import ngo.friendship.satellite.error.MhealthException
import ngo.friendship.satellite.interfaces.OnCompleteListener
import ngo.friendship.satellite.model.QuestionnaireInfo
import ngo.friendship.satellite.model.UserScheduleInfo
import ngo.friendship.satellite.ui.InterviewActivity
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.utility.Utility
import ngo.friendship.satellite.views.AppToast
import ngo.friendship.satellite.views.DialogView
import org.json.JSONArray
import org.json.JSONException

/**
 * @author Md.Yeasin Ali
 * @created 01th Oct 2022
 */
class FollowUpListFragment : Fragment(), View.OnClickListener, OnCompleteListener {

    private lateinit var binding: CommonServiceActivityBinding
    var scheduleList = java.util.ArrayList<UserScheduleInfo>()
    var attendedScheduleList = java.util.ArrayList<UserScheduleInfo>()

    //    val viewModel: OfflineViewModel by viewModels()
    private var FOLLOW_UP_PAGE_NAME: String = ""
    lateinit var mAdapter: BasemHealthAdapter<UserScheduleInfo, CommonServicesItemRowBinding>
    lateinit var mAdapterFollowUp: BasemHealthAdapter<QuestionnaireInfo, FollowUpDialogItemBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            FOLLOW_UP_PAGE_NAME = requireArguments().getString("FOLLOW_UP_PAGE_NAME", "")
        }
    }

    companion object {
        fun newInstance(isSaved: String): FollowUpListFragment {
            val homePage = FollowUpListFragment()
            val args = Bundle()
            args.putString("FOLLOW_UP_PAGE_NAME", isSaved)
            homePage.arguments = args
            return homePage
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CommonServiceActivityBinding.inflate(inflater, container, false)
        App.loadApplicationData(activity)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (FOLLOW_UP_PAGE_NAME.equals(Constants.TODAY_FOLLOW_UP)) {
            retriveAndShow("0");
        } else if (FOLLOW_UP_PAGE_NAME.equals(Constants.COMPLETE_FOLLOW_UP)) {
            retriveAndShow("1")
        }
//        else if (FOLLOW_UP_PAGE_NAME.equals(Constants.ALL_FOLLOW_UP)) {
//
//        }
//        viewModel = ViewModelProvider(this).get(MHealthViewModel::class.java)
//        App.showProgressDialog(activity)
//        viewModel.retrievePatientFollowup("", IS_SAVED).observe(
//            viewLifecycleOwner
//        ) { list ->
//            if (list.size > 0) {
//                mAdapter.addItems(list)
//                binding.serviceItemNotFound.serviceNotFound.visibility = View.GONE
//            } else {
//                binding.serviceItemNotFound.serviceNotFound.visibility = View.VISIBLE
//            }
//            App.hideProgressDialog()
//
//        }

        itemAdapter
    }

    private fun retriveAndShow(schedStatus: String?) {
        val tsk = MHealthTask(
            activity,
            Task.RETRIEVE_TIME_SCHEDULE,
            R.string.retrieving_data,
            R.string.please_wait
        )
        tsk.setParam(schedStatus)
        tsk.setCompleteListener(this)
        tsk.execute()
    }


    private val itemAdapter: Unit
        private get() {
            mAdapter =
                object : BasemHealthAdapter<UserScheduleInfo, CommonServicesItemRowBinding>() {
                    override fun onItemLongClick(
                        view: View,
                        model: UserScheduleInfo,
                        position: Int
                    ) {
                    }

                    override fun onItemClick(
                        view: View,
                        userScheduleInfo: UserScheduleInfo,
                        position: Int,
                        dataBinding: CommonServicesItemRowBinding
                    ) {
                        //   openQuestionnair(position);
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onBindData(
                        model: UserScheduleInfo,
                        position: Int,
                        dataBinding: CommonServicesItemRowBinding
                    ) {
                        dataBinding.tvVisited.visibility = View.VISIBLE
                        dataBinding.tvFollowUpKey.setTypeface(null, Typeface.BOLD_ITALIC)

                        dataBinding.tvBeneficiaryCode.text = model.beneficiaryCode
                        dataBinding.tvBenefName.text = model.beneficiaryName
                        dataBinding.tvFollowUpKey.text = "Follow-up in : "
                        dataBinding.tvFollowUp.text = "" + model.remainingDays + " Day(s) "
                        dataBinding.tvServiceType.text = "" + model.description
                        model.lastServiceDate?.let {
                            dataBinding.tvVisited.visibility =View.VISIBLE
                            dataBinding.tvVisited.text = "Last Visited : " + model.lastServiceDate
                        } ?: run {
                            dataBinding.tvVisited.visibility =View.GONE
                        }





                        Utility.setDefaultImage(model.gender, model.beneficiaryImagePath,dataBinding.ivBeneficiary)



                        if (model.scheduleType.equals(
                                FollowupType.PATIENT_VISIT,
                                ignoreCase = true
                            ) || model.scheduleType.equals(FollowupType.CCS, ignoreCase = true)
                        ) {
                            dataBinding.btnUpload.visibility = View.GONE
                        } else {
                            dataBinding.tvAgeGender.visibility = View.GONE
                            dataBinding.tvBenefName.text = model.scheduleType
                            dataBinding.tvVisited.visibility =View.GONE
                            if (model.scheduleStatus>0){
                                dataBinding.tvFollowUpKey.text = "Date : "
                                dataBinding.tvFollowUpKey.text = "Follow-up : "
                            }else{
                                dataBinding.tvFollowUpKey.text = "Date-in : "
                            }

                            dataBinding.tvFollowUp.text = "" + model.remainingDays + " Day(s) "

                            if (model.eventId > 0) {
                                dataBinding.btnUpload.visibility = View.VISIBLE
                            } else {
                                dataBinding.btnUpload.visibility = View.GONE
                            }

                        }



                        dataBinding.btnUpload.setOnClickListener {
                            if (SystemUtility.isConnectedToInternet(activity)) {
                                try {
                                    val request = RequestData(
                                        RequestType.FCM_SCHEDULE,
                                        RequestName.UPDATE_SCHEDULE,
                                        Constants.MODULE_BUNCH_PUSH
                                    )
                                    val jArr = JSONArray()
                                    //   for (scheduleInfo in attendedScheduleList) {
                                    jArr.put(model.toJson())
                                    //}
                                    request.getData().put("user_schedule", jArr)
                                    val task = CommiunicationTask(
                                        activity,
                                        request,
                                        R.string.retrieving_data,
                                        R.string.please_wait
                                    )
                                    task.setCompleteListener(this@FollowUpListFragment)
                                    task.execute()
                                } catch (e: MhealthException) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace()
                                } catch (e: JSONException) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace()
                                }
                            } else {
                                SystemUtility.openInternetSettingsActivity(activity)
                            }
                        }
                        dataBinding.clServiceBody.setOnClickListener {
                            if (model.interviewId > 0) {
                                val questionnaireList: ArrayList<QuestionnaireInfo> =
                                    App.getContext().db.getFollowupQuestionnaireList(
                                        model.interviewId, App.getContext().appSettings.language
                                    )
                                if (questionnaireList.size > 0) {
                                    showCustomDialog(model, questionnaireList)
                                } else {
                                    AppToast.showToast(
                                        context,
                                        R.string.data_not_available
                                    )
                                }
                            }
                        }






                    }

                    override val layoutResId: Int
                        get() = R.layout.common_services_item_row
                }
            val ATTRS = intArrayOf(android.R.attr.listDivider)
            val a = context?.obtainStyledAttributes(ATTRS)
            val divider = a?.getDrawable(0)
            val insetDivider = InsetDrawable(divider, 8, 16, 8, 16)
            if (a != null) {
                a.recycle()
            }
            val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            itemDecoration.setDrawable(insetDivider)
            binding.rvService.addItemDecoration(itemDecoration)
            binding.rvService.setHasFixedSize(true)
            val mLayoutManager = LinearLayoutManager(activity)
            binding.rvService.layoutManager = mLayoutManager
            binding.rvService.itemAnimator = DefaultItemAnimator()
            binding.rvService.adapter = mAdapter
        }


    private fun showCustomDialog(
        userScheduleInfo: UserScheduleInfo,
        questionnaireList: ArrayList<QuestionnaireInfo>
    ) {
        val dialogBinding: FollowUpDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity), R.layout.follow_up_dialog, null, false
        )

        val customDialog = AlertDialog.Builder(activity, 0).create()

        customDialog.apply {
            setView(dialogBinding.root)
            setCancelable(false)
        }.show()




        mAdapterFollowUp =
            object : BasemHealthAdapter<QuestionnaireInfo, FollowUpDialogItemBinding>() {
                override fun onItemLongClick(
                    view: View,
                    model: QuestionnaireInfo,
                    position: Int
                ) {
                }

                override fun onItemClick(
                    view: View,
                    model: QuestionnaireInfo,
                    position: Int,
                    dataBinding: FollowUpDialogItemBinding
                ) {
                    //   openQuestionnair(position);
                }

                @SuppressLint("SetTextI18n")
                override fun onBindData(
                    questionnaireInfo: QuestionnaireInfo,
                    position: Int,
                    dataBinding: FollowUpDialogItemBinding
                ) {
//                    var maternalId: Long = -1
//                    val maternalCreateDate: Long = 0
//                    maternalId= questionnaireInfo.getMaternalServiceId()
                    dataBinding.tvName.setText(questionnaireInfo.questionnaireTitle)
                    dataBinding.mdiIcon.setText(Html.fromHtml("&#x" + questionnaireInfo.icon))

                    dataBinding.llFollowUp.setOnClickListener {
                        val intent = Intent(activity, InterviewActivity::class.java)
                        intent.putExtra(
                            ActivityDataKey.QUESTIONNAIRE_JSON,
                            questionnaireInfo.getQuestionnaireJSON(activity)
                        )
                        intent.putExtra(
                            ActivityDataKey.QUESTIONNAIRE_TITLE,
                            questionnaireInfo.getQuestionnaireTitle()
                        )
                        intent.putExtra(ActivityDataKey.QUESTIONNAIRE_ID, questionnaireInfo.getId())
                        intent.putExtra(
                            ActivityDataKey.QUESTIONNAIRE_NAME,
                            questionnaireInfo.getQuestionnaireName()
                        )
                        intent.putExtra(
                            ActivityDataKey.PARENT_INTERVIEW_ID,
                            userScheduleInfo.interviewId
                        )
                        intent.putExtra(
                            ActivityDataKey.BENEFICIARY_CODE,
                            App.getContext().getUserInfo()
                                .getUserCode() + userScheduleInfo.beneficiaryCode
                        )
                        intent.putExtra(ActivityDataKey.INTERVIEW_TYPE, ActivityDataKey.FOLLOW_UP)
                        intent.putExtra(
                            ActivityDataKey.BENEFICIARY_ID,
                            userScheduleInfo.beneficiaryId
                        )
//                        intent.putExtra(ActivityDataKey.MATERNAL_SERVICE_ID,maternalId)
                        startActivity(intent)
                        activity?.finish()
                    }

                }

                override val layoutResId: Int
                    get() = R.layout.follow_up_dialog_item
            }
        val ATTRS = intArrayOf(android.R.attr.listDivider)
        val a = context?.obtainStyledAttributes(ATTRS)
        val divider = a?.getDrawable(0)
        val insetDivider = InsetDrawable(divider, 8, 16, 8, 16)
        if (a != null) {
            a.recycle()
        }
        val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(insetDivider)
        dialogBinding.rvFollowUp.addItemDecoration(itemDecoration)
        dialogBinding.rvFollowUp.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity)
        dialogBinding.rvFollowUp.layoutManager = mLayoutManager
        dialogBinding.rvFollowUp.itemAnimator = DefaultItemAnimator()
        dialogBinding.rvFollowUp.adapter = mAdapterFollowUp

        mAdapterFollowUp.addItems(questionnaireList)

        dialogBinding.ivBack.setOnClickListener {
            customDialog.dismiss()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        if (App.getContext().appSettings == null) {
            App.getContext().readAppSettings(activity)
        }
        if (!SystemUtility.isAutoTimeEnabled(activity)) {
            SystemUtility.openDateTimeSettingsActivity(activity)
        }

//        retriveAndShow("")
        //        if(IS_SAVED){
//            activityPath= Utility.setActivityPath(getActivity(),R.string.saved_interview);
//        }else{
//            activityPath=Utility.setActivityPath(getActivity(),R.string.sent_interview);
//        }
        //  retrieveInterview("")
    }

    override fun onClick(v: View) {}
    override fun onComplete(msg: Message?) {
        if (msg!!.data.getString(TaskKey.NAME) == TaskKey.COMMUNICATION_TASK) {
            if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                App.showMessageDisplayDialog(
                    activity,
                    resources.getString(R.string.network_error),
                    R.drawable.error,
                    Color.RED
                )
            } else {
                val response: ResponseData = msg.data.getSerializable(TaskKey.DATA0) as ResponseData
                if (response.responseCode.equals("00", ignoreCase = true)) {
                    App.showMessageDisplayDialog(
                        activity,
                        response.errorCode + "-" + response.errorDesc,
                        R.drawable.error,
                        Color.RED
                    )
                } else {
                    retriveAndShow("0")
                }
            }
        } else if (msg.data.getString(TaskKey.NAME) == TaskKey.MHEALTH_TASK) {
            if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                App.showMessageDisplayDialog(
                    activity,
                    resources.getString(R.string.saving_error),
                    R.drawable.error,
                    Color.RED
                )
            } else {
                showData(msg.data.getSerializable(TaskKey.DATA0) as java.util.ArrayList<UserScheduleInfo>)
            }
        }
    }

    private fun showData(sList: java.util.ArrayList<UserScheduleInfo>) {
        mAdapter.addItems(sList)
        if (sList.size > 0) {
            binding.serviceItemNotFound.serviceNotFound.visibility = View.GONE
        } else {
            binding.serviceItemNotFound.serviceNotFound.visibility = View.VISIBLE
        }
        Log.e("Hello_World", "" + sList.size)

    }


    /**
     * Display alert dialog with one button.
     *
     * @param msg will be displayed in the message section of the dialog
     * @param imageId is the image drawable id which will be displayed at the dialog's title
     * @param messageColor is the color id of the dialog's message
     */
    private fun showOneButtonDialog(msg: String, imageId: Int, messageColor: Int) {
        val buttonMap = java.util.HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_close
        val exitDialog =
            DialogView(activity, R.string.dialog_title, msg, messageColor, imageId, buttonMap)
        exitDialog.show()
    }

}