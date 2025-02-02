package ngo.friendship.satellite.ui.earlier_services


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import ngo.friendship.satellite.App
import ngo.friendship.satellite.MainActivity
import ngo.friendship.satellite.R
import ngo.friendship.satellite.adapter.BasemHealthAdapter
import ngo.friendship.satellite.asynctask.MHealthTask
import ngo.friendship.satellite.asynctask.Task
import ngo.friendship.satellite.asynctask.TaskKey
import ngo.friendship.satellite.constants.ActivityDataKey
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.constants.KEY
import ngo.friendship.satellite.databinding.CommonServicesItemRowBinding
import ngo.friendship.satellite.databinding.FragmentPendingSyncBinding
import ngo.friendship.satellite.error.MhealthException
import ngo.friendship.satellite.interfaces.FragmentCommunicator
import ngo.friendship.satellite.interfaces.OnDialogButtonClick
import ngo.friendship.satellite.jsonoperation.JSONParser
import ngo.friendship.satellite.model.InterviewInfoSyncUnsync
import ngo.friendship.satellite.model.Question
import ngo.friendship.satellite.model.QuestionList
import ngo.friendship.satellite.ui.InterviewActivity
import ngo.friendship.satellite.ui.beneficiary.profile.BeneficiaryProfileActivity
import ngo.friendship.satellite.utility.AppPreference
import ngo.friendship.satellite.utility.FileOperaion
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.utility.Utility
import ngo.friendship.satellite.views.DialogView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * @author Md.Yeasin Ali
 * @created 01th Oct 2022
 */
class PendingSyncServiceFragment : Fragment(), View.OnClickListener,
    CompoundButton.OnCheckedChangeListener, FragmentCommunicator {
    private lateinit var binding: FragmentPendingSyncBinding
    private lateinit var viewModel: EarlierServiceViewModel
    private var IS_SAVED = false
    var activityPath: String? = null
    var interviews: ArrayList<InterviewInfoSyncUnsync> = ArrayList()
    var mAdapter: BasemHealthAdapter<InterviewInfoSyncUnsync, CommonServicesItemRowBinding>? = null
    var communicator: FragmentCommunicator? = null
    var ctx: Context? = null

    val limitedDate = Calendar.getInstance()
    val CALENDAR_FROM = Calendar.getInstance()
    private val CALENDAR_TO = Calendar.getInstance()
    var DD_MMM_YYYY = SimpleDateFormat("dd-MMM-yyyy")

    var fromDateString = ""
    var toDateString = ""
    var minDate: Long = 0

    val DATA_GET = 1
    val EXIT = 2
    var limitMonth = -1;
    private val PAGE_SIZE = 50 // Number of items to load per page
    private var currentPage = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPendingSyncBinding.inflate(inflater, container, false)
        App.loadApplicationData(activity)
        viewModel = ViewModelProvider(this).get(EarlierServiceViewModel::class.java)

        if (arguments != null) {
            IS_SAVED = requireArguments().getBoolean("IS_SAVED", false)
        }

        if (IS_SAVED) {
            binding.llSelectAll.visibility = View.VISIBLE
        } else {
            binding.llSelectAll.visibility = View.GONE
        }

        try {
            val confiData = AppPreference.getString(requireActivity(), KEY.FCM_CONFIGURATION, "[]")
            val configArry = JSONArray(confiData)
            limitMonth = Integer.parseInt(JSONParser.getFcmConfigValue(  configArry, "SYNCED_DATA_LIST", "date.range.show" ))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        itemAdapter
        CALENDAR_FROM.add(Calendar.MONTH, limitMonth)
        retrieveInterview("", "0", "50")

        viewModel.selected.observe(requireActivity()) { item ->
            var filterInterviewList: ArrayList<InterviewInfoSyncUnsync> = ArrayList()
            if (item.length > 0) {
                for (single in interviews) {
                    if (
                        single.benefName.lowercase()  .contains(item.lowercase(), ignoreCase = true) ||
                        single.benefCode.lowercase().contains(item.lowercase(), ignoreCase = true)
                    ) {
                        filterInterviewList.add(single)
                    }
                }


                mAdapter?.addItems(filterInterviewList)

            } else {
                mAdapter?.addItems(interviews)
            }
            mAdapter!!.notifyDataSetChanged()
        }


//        Report singleReport = reports.get(singleReport.get);
//        singleReport.setCalendarFromAndTo(CALENDAR_FROM, CALENDAR_TO);
//        singleReport.resetInputPram();


        if (!IS_SAVED) {
            binding.llDateSearch.visibility = View.VISIBLE
            binding.etFromDate.setText(DD_MMM_YYYY.format(CALENDAR_FROM.getTime()))
            binding.etToDate.setText(DD_MMM_YYYY.format(CALENDAR_TO.getTime()))

            binding.etFromDate.setOnClickListener {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DATE, 1)
                val afterTwoMonthsinMilli = cal.timeInMillis
                // Log.d("Time", "onCreate: ...from date "+afterTwoMonthsinMilli);
                val dpd = DatePickerDialog(
                    requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        CALENDAR_FROM.set(year, monthOfYear, dayOfMonth, 0, 0)
                        Log.e("DATETILL : ", "" + CALENDAR_FROM.getTime())
                        fromDateString = DD_MMM_YYYY.format(CALENDAR_FROM.getTime())
                        binding.etFromDate.setText("" + fromDateString)
                        val afterTwoMonthsinMilli: Long = CALENDAR_FROM.getTimeInMillis()
                        Log.d("Time", "onCreate: ...from date $afterTwoMonthsinMilli")
                    }, CALENDAR_FROM.get(Calendar.YEAR), CALENDAR_FROM
                        .get(Calendar.MONTH), CALENDAR_FROM
                        .get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.maxDate = Calendar.getInstance().timeInMillis
                dpd.datePicker.minDate = minDate
                dpd.show()


            }
            binding.etToDate.setOnClickListener {
                val dpd = DatePickerDialog(
                    requireActivity(),
                    { view, year, monthOfYear, dayOfMonth ->
                        Log.e(
                            "DATETILL : ", dayOfMonth.toString() + " "
                                    + monthOfYear + " " + year
                        )
                        CALENDAR_TO.set(year, monthOfYear, dayOfMonth, 0, 0)
                        Log.e("DATETILL : ", "" + CALENDAR_TO.getTime())
                        toDateString = DD_MMM_YYYY.format(CALENDAR_TO.getTime())
                        binding.etToDate.setText("" + toDateString)
                        val afterTwoMonthsinMilli: Long = CALENDAR_TO.getTimeInMillis()
                        Log.d("Time", "onCreate: ...from date $afterTwoMonthsinMilli")
                    }, CALENDAR_TO.get(Calendar.YEAR), CALENDAR_TO
                        .get(Calendar.MONTH), CALENDAR_TO
                        .get(Calendar.DAY_OF_MONTH)
                )
                dpd.datePicker.maxDate = Calendar.getInstance().timeInMillis
                dpd.datePicker.minDate = minDate
                dpd.show()
            }
            getSyncData("", "0", "5000", CALENDAR_FROM.timeInMillis, CALENDAR_TO.timeInMillis);
            binding.btnSearch.setOnClickListener { v ->
                interviews.clear()
                getSyncData("", "0", "5000", CALENDAR_FROM.timeInMillis, CALENDAR_TO.timeInMillis);


            }
        } else {
            binding.llDateSearch.visibility = View.GONE
        }




        binding.cvCheckAll.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                for (interviewInfo in interviews) {
                    interviewInfo.isSelected = true
                }
            } else {
                for (interviewInfo in interviews) {
                    interviewInfo.isSelected = false
                }
            }
            (ctx as EarlierServicesActivity?)!!.showInterview(selectedInterviewList)
            mAdapter!!.notifyDataSetChanged()
        }
        return binding.root
    }

    private fun retrieveInterview(data: String, offset: String, limit: String) {
        val tsk = MHealthTask(
            activity,
            Task.RETRIEVE_SAVED_INTERVIEW_LIST,
            R.string.retrieving_data,
            R.string.please_wait
        )
        val tasl = if (IS_SAVED) "N" else "Y"

        if (IS_SAVED) {
            tsk.setParam("N", data, "0", "500", "-1", "-1")
            tsk.setCompleteListener { msg ->
                if (msg.data.getString(TaskKey.NAME) == TaskKey.MHEALTH_TASK) {
                    if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                        val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                        App.showMessageDisplayDialog(
                            activity,
                            resources.getString(R.string.saving_error),
                            R.drawable.error,
                            Color.RED
                        )
                    } else {
                        interviews =
                            msg.data.getSerializable(TaskKey.DATA0) as ArrayList<InterviewInfoSyncUnsync>
                        // tsk.setParam("NR", data)
                        tsk.setParam("NR", data, "0", "500", "-1", "-1")
                        tsk.setCompleteListener { msg ->
                            if (msg.data.getString(TaskKey.NAME) == TaskKey.MHEALTH_TASK) {
                                if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                                    val errorMsg =
                                        msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                                    App.showMessageDisplayDialog(
                                        activity,
                                        resources.getString(R.string.saving_error),
                                        R.drawable.error,
                                        Color.RED
                                    )
                                } else {
                                    interviews.addAll(msg.data.getSerializable(TaskKey.DATA0) as ArrayList<InterviewInfoSyncUnsync>)
                                    if (interviews.size > 0) {
                                        binding.serviceItemNotFound.serviceNotFound.visibility =
                                            View.GONE
                                        binding.cvCheckAll.visibility = View.VISIBLE
                                    } else {
                                        binding.serviceItemNotFound.serviceNotFound.visibility =
                                            View.VISIBLE
                                    }
                                    if (!IS_SAVED) {
                                        binding.llSelectAll.visibility = View.GONE
                                    }

                                    if (IS_SAVED && interviews.size > 0) {
                                        binding.llSelectAll.visibility = View.VISIBLE
                                    } else {
                                        binding.llSelectAll.visibility = View.GONE
                                    }

                                    mAdapter!!.addItems(interviews)
                                    (ctx as EarlierServicesActivity?)!!.showInterview(
                                        selectedInterviewList
                                    )
                                    Log.e("Inviews", "" + interviews.size)
                                }
                            }
                        }
                        tsk.execute()


//                    if (interviews.size > 0) {
//                        binding.serviceItemNotFound.serviceNotFound.visibility = View.GONE
//                        binding.cvCheckAll.visibility = View.VISIBLE
//                    } else {
//                        binding.serviceItemNotFound.serviceNotFound.visibility = View.VISIBLE
//                    }
//                    if (!IS_SAVED) {
//                        binding.llSelectAll.visibility = View.GONE
//                    }
//
//                    if (IS_SAVED && interviews.size > 0) {
//                        binding.llSelectAll.visibility = View.VISIBLE
//                    } else {
//                        binding.llSelectAll.visibility = View.GONE
//                    }

//                    mAdapter!!.addItems(interviews)
//                    (ctx as EarlierServicesActivity?)!!.showInterview(selectedInterviewList)
//                    Log.e("Inviews", "" + interviews.size)
                    }
                }
            }
            tsk.execute()
        } else {
            tsk.setParam("Y", data, offset, limit, "-1", "-1")

            tsk.setCompleteListener { msg ->
                if (msg.data.getString(TaskKey.NAME) == TaskKey.MHEALTH_TASK) {
                    if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                        val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                        App.showMessageDisplayDialog(
                            activity,
                            resources.getString(R.string.saving_error),
                            R.drawable.error,
                            Color.RED
                        )
                    } else {
                        interviews =
                            msg.data.getSerializable(TaskKey.DATA0) as ArrayList<InterviewInfoSyncUnsync>

                        if (interviews.size > 0) {
                            binding.serviceItemNotFound.serviceNotFound.visibility = View.GONE
                            binding.cvCheckAll.visibility = View.VISIBLE
                        } else {
                            binding.serviceItemNotFound.serviceNotFound.visibility = View.VISIBLE
                        }
                        if (!IS_SAVED) {
                            binding.llSelectAll.visibility = View.GONE
                        }
                        if (IS_SAVED && interviews.size > 0) {
                            binding.llSelectAll.visibility = View.VISIBLE
                        } else {
                            binding.llSelectAll.visibility = View.GONE
                        }

                        mAdapter!!.addItems(interviews)
                        (ctx as EarlierServicesActivity?)!!.showInterview(selectedInterviewList)
                        Log.e("Inviews", "inside m executor" + interviews.size)
                    }
                }
            }
            tsk.execute()
        }
    }

    private fun getSyncData(
        data: String,
        offset: String,
        limit: String,
        fromDate: Long,
        toDate: Long
    ) {
        val lng = App.getContext().appSettings.language
        val fcmCode = App.getContext().userInfo.userCode
        interviews.addAll(
            App.getContext().getDB().getInterviewListSyncUnsync(
                "Y",
                lng,
                fcmCode,
                Constants.INTERVIEW_ALL,
                Integer.parseInt(offset),
                Integer.parseInt(limit),
                fromDate,
                toDate
            )
        )
        Log.d("Inviews", "getSyncData: ..........interview size " + interviews.size)
        mAdapter!!.addItems(interviews)
        mAdapter!!.notifyDataSetChanged()
        if (requireActivity() is EarlierServicesActivity) {
            if (!IS_SAVED) {
                (requireActivity() as EarlierServicesActivity).tabCountSearch(interviews.size)
            }

        }


    }


    private val itemAdapter: Unit
        private get() {
            mAdapter = object :
                BasemHealthAdapter<InterviewInfoSyncUnsync, CommonServicesItemRowBinding>() {
                override fun onItemLongClick(
                    view: View,
                    model: InterviewInfoSyncUnsync,
                    position: Int
                ) {
                }

                override fun onItemClick(
                    view: View,
                    savedInterviewInfo: InterviewInfoSyncUnsync,
                    position: Int,
                    dataBinding: CommonServicesItemRowBinding
                ) {
                    //   openQuestionnair(position);
                }

                @SuppressLint("SetTextI18n")
                override fun onBindData(
                    model: InterviewInfoSyncUnsync,
                    position: Int,
                    dataBinding: CommonServicesItemRowBinding
                ) {


                    dataBinding.llFollowUp.visibility = View.GONE
                    dataBinding.mdiLabelIcon.visibility = View.GONE
                    dataBinding.tvAgeGender.visibility = View.GONE
                    dataBinding.btnProfile.visibility = View.VISIBLE
                    dataBinding.tvBenefNameOthers.visibility = View.VISIBLE
                    dataBinding.btnProfile.setOnClickListener {
                        val profileString = Gson().toJson(model).toString()
                        val fcmProfile = Intent(ctx, BeneficiaryProfileActivity::class.java)
                        fcmProfile.putExtra("BENEF_PROFILE_FROM_INTERVIEW_LIST", profileString)
                        ctx!!.startActivity(fcmProfile)
                    }

                    if (model.docFollowupId > 0) {
                        dataBinding.mdiLabelIcon.visibility = View.VISIBLE
                        dataBinding.mdiLabelIcon.setText(Html.fromHtml("&#xF04D9"))
                    }
                    try {
                        val benefCode = model.beneficiaryCode!!.takeLast(5)
                        dataBinding.tvBeneficiaryCode.setText("" + benefCode)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    dataBinding.tvVisited.visibility = View.VISIBLE
                    try {
                        val fromDateDate: Date = Date(model.createDate)
                        val outputDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                        var fromDateFormat = outputDateFormat.format(fromDateDate)
                        dataBinding.tvVisited.text =
                            "Time : " + fromDateFormat + " " + model.interviewTime
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dataBinding.tvVisited.text = "Time :  " + model.interviewTime
                    }



                    if (!model.beneficiarGender.isNullOrEmpty()) {
                        var genderName: String = "";
                        if (model.beneficiarGender.equals("M", ignoreCase = true)) {
                            genderName = "Male"
                        } else if (model.beneficiarGender.equals("F", ignoreCase = true)) {
                            genderName = "Female"

                        } else if (model.beneficiarGender.equals("O", ignoreCase = true)) {
                            genderName = "Others"
                        }
                        if (!model.age.isNullOrEmpty()) {
                            try {
                                if (model.age.contains('y')) {
                                    dataBinding.tvBenefNameOthers.text =
                                        "  $genderName, " + "${model.age} "
                                } else {
                                    dataBinding.tvBenefNameOthers.text =
                                        "  $genderName, " + "${model.age}y "
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }


                        //dataBinding.tvBenefNameOthers.setText("("+model.beneficiarGender+")")
                    } else {
                        dataBinding.tvBenefNameOthers.setText("")
                    }
                    if (FileOperaion.isExist(model.benefImagePath)) {
                        try {
                            dataBinding.ivBeneficiary.setImageBitmap(
                                FileOperaion.decodeImageFile(
                                    model.benefImagePath,
                                    60
                                )
                            )
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            if (model.beneficiarGender.equals("F", ignoreCase = true)) {
                                dataBinding.ivBeneficiary.setImageResource(R.drawable.ic_default_woman)
                            } else if (model.beneficiarGender.equals("M", ignoreCase = true)) {
                                dataBinding.ivBeneficiary.setImageResource(R.drawable.ic_default_man)
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    val value = JSONParser.getFcmConfigValue(
                        App.getContext().appSettings.fcmConfigrationJsonArray,
                        "INTERVIEW_SERVICES",
                        "time.limit.interview.edit.in.minute"
                    )

                    // Log.d(TAG, "onBindData: ...........the config value is ${value}")
                    val timeConstrain: Int = value.toInt();
                    if (IS_SAVED) {
                        dataBinding.llEditTime.setVisibility(View.GONE)
//                            val hour:String = String.format("%.2f", getTimeDifferenec(model).toDouble());
//                            dataBinding.tvTimeRemaining.setText("${hour} Hour Elapsed")
                    } else {
                        if (getTimeDifferenec(model).toDouble() <= timeConstrain / 60) {
                            dataBinding.llEditTime.setVisibility(View.VISIBLE)
                            val hour: String =
                                String.format("%.2f", getTimeDifferenec(model).toDouble());
                            dataBinding.tvTimeRemaining.setText("${hour} Hour Elapsed")

                        }
                    }




                    if (IS_SAVED) {
                        Log.d(
                            "sentDatas",
                            "onBindData: .......sent stats " + App.getContext().getDB()
                                .getInterViewServerStatus(interviews.get(position).interviewId)
                        )
                        if (App.getContext().getDB()
                                .getInterViewServerStatus(interviews.get(position).interviewId)
                                .equals("NR")
                        ) {
                            dataBinding.mdiDelete.visibility = View.GONE
                        } else {
                            dataBinding.mdiDelete.visibility = View.VISIBLE
                            dataBinding.mdiDelete.setOnClickListener { v: View? ->
                                showConfirmationDialog(
                                    getString(R.string.are_you_sure_are_you_want_to_delete_this_service),
                                    DATA_GET,
                                    model,
                                    position
                                );
                            }
                        }
                    }

                    dataBinding.clServiceBody.setOnClickListener { v: View? ->
                        if (IS_SAVED && App.getContext().getDB()
                                .getInterViewServerStatus(interviews.get(position).interviewId) != null && App.getContext()
                                .getDB()
                                .getInterViewServerStatus(interviews.get(position).interviewId)
                                .equals("N")
                        ) {
                            editInterView(model, "local")
                        } else {
                            if (getTimeDifferenec(model).toDouble() <= timeConstrain / 60) {
//                                dataBinding.llEditTime.setVisibility(View.VISIBLE)
//                                dataBinding.tvTimeRemaining.setText("${getTimeDifferenec(model).toInt()}elapsed")
                                editInterView(model, "remote")
                            } else {
                                dataBinding.llEditTime.setVisibility(View.GONE)
                            }
                        }
                    }
                    if (model.isNewDate()) {
                        dataBinding.tvDate.setVisibility(View.VISIBLE)
                        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                        val outputDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                        try {
                            val dateS = inputDateFormat.parse(model.recordDate)
                            val formattedDate = outputDateFormat.format(dateS)
                            dataBinding.tvDate.text = "" + formattedDate
                        } catch (e: Exception) {
                            dataBinding.tvDate.text = "" + model.recordDate
                            println("Error parsing or formatting date: ${e.message}")
                        }

                    } else {
                        dataBinding.tvDate.setVisibility(View.GONE)
                        dataBinding.tvDate.setText("")
                    }
                    if (IS_SAVED) {
                        dataBinding.cvCheck.visibility = View.VISIBLE
                    } else {
                        dataBinding.cvCheck.visibility = View.GONE
                    }
                    dataBinding.tvBenefName.text = "" + model.benefName
//                        Utility.setGenter(
//                            model.beneficiarGender,
//                            model.beneficiarAge,
//                            dataBinding.tvAgeGender,
//                            ctx
//                        )
//
//                        dataBinding.tvBeneficiaryCode.text =
//                            "" + model.beneficiaryCode.substring(model.beneficiaryCode.length - 5)
                    dataBinding.tvServiceType.visibility = View.VISIBLE
                    dataBinding.tvServiceType.setTypeface(
                        dataBinding.tvServiceType.getTypeface(),
                        Typeface.BOLD_ITALIC
                    )
                    dataBinding.tvServiceType.text = "" + model.questionnarieTitle
//                        dataBinding.tvDateTime.text = "" + model.time + ", " + "" + model.date
                    if (model.isSelected) {
                        dataBinding.cvCheck.isChecked = true
                    } else {
                        dataBinding.cvCheck.isChecked = false
                    }
                    dataBinding.cvCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            model.isSelected = true
                        } else {
                            model.isSelected = false
                        }
                        (ctx as EarlierServicesActivity?)!!.showInterview(selectedInterviewList)
                    }
                    //                }
                    //  dataBinding.clServiceBody.setOnClickListener { v: View? -> }
//                        Utility.setGenter(
//                            model.beneficiarGender,
//                            model.beneficiarAge,
//                            dataBinding.tvAgeGender,
//                            activity
//                        )
                    Utility.setDefaultImage(
                        model.beneficiarGender,
                        model.benefImagePath,
                        dataBinding.ivBeneficiary
                    )
//                dataBinding.tvQuestionnaireIcon.setText("");
//                dataBinding.tvQuestionnaireTitle.setText("" + model.getQuestionnaireTitle());
                }

                override val layoutResId: Int
                    get() = R.layout.common_services_item_row
            }
            val ATTRS = intArrayOf(android.R.attr.listDivider)
            val a = requireActivity().obtainStyledAttributes(ATTRS)
            val divider = a.getDrawable(0)
            val insetDivider = InsetDrawable(divider, 8, 16, 8, 16)
            a.recycle()
            val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
            itemDecoration.setDrawable(insetDivider)
            binding.rvService.addItemDecoration(itemDecoration)
            binding.rvService.setHasFixedSize(true)
            val mLayoutManager = LinearLayoutManager(activity)
            binding.rvService.layoutManager = mLayoutManager
            binding.rvService.itemAnimator = DefaultItemAnimator()
            binding.rvService.adapter = mAdapter


            // Scroll listener for RecyclerView to detect when to load more data
            binding.rvService.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    Log.d(
                        "pagination",
                        "onScrolled: total item count is " + totalItemCount + "last visible item is " + lastVisibleItem
                    );
                    if (totalItemCount <= (lastVisibleItem + 1)) {
                        var offset: Int = currentPage * PAGE_SIZE
                        // getSyncData("",offset.toString(),"50",-1,-1)
                        currentPage++
                    }
                }
            })
        }

    private fun showConfirmationDialog(
        msg: String,
        type: Int,
        info: InterviewInfoSyncUnsync,
        position: Int
    ) {
        val buttonMap = java.util.HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_yes
        buttonMap[2] = R.string.btn_no
        val dialog = DialogView(activity, R.string.dialog_title, msg, R.drawable.warning, buttonMap)
        dialog.setOnDialogButtonClick(object : OnDialogButtonClick {
            override fun onDialogButtonClick(view: View?) {

                when (view!!.id) {
                    1 -> if (type == MainActivity.DATA_GET) {

                        deleteInterView(info, position);
                    } else if (type == MainActivity.EXIT) {
                        activity?.finish()
                    }
                }
            }
        })
        dialog.show()
    }

    private fun getTimeDifferenec(info: InterviewInfoSyncUnsync): String {

        val dateFormat = SimpleDateFormat("HH", Locale.getDefault())
        val currentTimeMillis: Long = System.currentTimeMillis()
        val timeDifferenceMillis: Long = currentTimeMillis - info.createDate


        val doubleTimedifference = timeDifferenceMillis.toDouble()
        val seconds: Double = doubleTimedifference / 1000.00
        val minutes: Double = seconds / 60.00
        val hours: Double = String.format("%.3f", minutes).toDouble() / 60.00


        return hours.toString();

//        if(hour.toInt()>=24){
//            return false
//        }else return true

    }


    val selectedInterviewList: ArrayList<InterviewInfoSyncUnsync>
        get() {
            val selectedInterviewList = ArrayList<InterviewInfoSyncUnsync>()
            for (info in interviews) {
                if (info.isSelected) {
                    selectedInterviewList.add(info)
                }
            }
            Log.e("SELECTED INTERVIEW ", selectedInterviewList.toString())
            return selectedInterviewList
        }

    fun editInterView(info: InterviewInfoSyncUnsync, server: String) {

        var jsonStr: String? = ""
        val questionnaireInfo = App.getContext().db.getQuestionnaire(info.questionnaireId)
        if (questionnaireInfo != null) {
            jsonStr = questionnaireInfo.getQuestionnaireJSON(activity)
            jsonStr = JSONParser.preapreQuestionearWithAns(jsonStr, info.questionAnswerJson)
            val entryParams = ArrayList<String>()
            entryParams.add(info.householdNumber)
            val intent = Intent(activity, InterviewActivity::class.java)
            intent.putExtra(ActivityDataKey.QUESTIONNAIRE_JSON, jsonStr)
            intent.putExtra(ActivityDataKey.BENEFICIARY_CODE, info.beneficiaryCode)
            intent.putExtra(ActivityDataKey.BENEFICIARY_ID, info.beneficiaryId)
            intent.putExtra(ActivityDataKey.PARENT_INTERVIEW_ID, info.parentInterviewId)
            intent.putExtra(ActivityDataKey.PARAMS, entryParams)
            intent.putExtra(ActivityDataKey.INTERVIEW_TYPE, ActivityDataKey.UPDATE)
            intent.putExtra(ActivityDataKey.TRANS_REF, info.transRef)
            intent.putExtra(ActivityDataKey.PENDING_SYNC_PAGE, "PendingSyncServiceFragment")
            intent.putExtra("EDIT", true)
            intent.putExtra("server", server)
            startActivity(intent)
        }
    }


    fun deleteInterView(info: InterviewInfoSyncUnsync, position: Int) {

        // val ansMap: Map<String, String> = HashMap()
        var ansMap: HashMap<String, String> = HashMap<String, String>()
        var jsonStr: String? = ""
        val questionnaireInfo = App.getContext().db.getQuestionnaire(info.questionnaireId)
        if (questionnaireInfo != null) {
            jsonStr = questionnaireInfo.getQuestionnaireJSON(activity)
            jsonStr = JSONParser.preapreQuestionearWithAns(jsonStr, info.questionAnswerJson)
            val entryParams = ArrayList<String>()
            entryParams.add(info.householdNumber)
        }
        var currentQuestion: Question? = null
        var qList: QuestionList? = null
        var currentQuestionList: QuestionList? = null
        try {
            qList = JSONParser.parseQuestionListJSON(jsonStr)
        } catch (e: MhealthException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        if (qList != null) {
            val jObj = JSONObject(jsonStr)
            val jDataObj: JSONObject = jObj.getJSONObject("QUESTIONNAIRE_DATA")
            val jQuetionnaireObj = jDataObj.getJSONObject(KEY.QUESTIONNAIRE)
            val keyList = jQuetionnaireObj.keys()
            var medicinedata: String = ""
            val ansList: JSONArray = JSONObject(info.questionAnswerJson).getJSONArray("questions")
            val sb = StringBuilder()
            sb.append("[")
            for (i in 0 until ansList.length()) {
//                ansMap.put(
//                    "medicine" + ansList.getJSONObject(i).getString("qkey"),
//                    ansList.getJSONObject(i).getString("answer")
//                )
                //  Medicine
                if (ansList.getJSONObject(i).getString("qkey").equals(
                        "${
                            App.getContext().getDB().getQuestionnaireIDByQNameAndQDetailsName(
                                info.questionnaireId,
                                "Medicine"
                            )
                        }"
                    )
                ) {
                    val medicineJson: String = ansList.getJSONObject(i).getString("answer")

                    medicinedata = medicineJson.replace("|", ",")
                    sb.append(medicinedata)

                    //  Log.d("jsonData", "deleteInterView: ..."+sb)
                }

            }
            sb.append("]")
            val mList: JSONArray = JSONArray(sb.toString())
            for (i in 0 until mList.length()) {
                val jsonDosesObject: JSONObject = mList.getJSONObject(i)
                val medicineId = JSONParser.getString(jsonDosesObject, "MED_ID")
                val medicineQuantity = JSONParser.getString(jsonDosesObject, "MED_QTY")

            }
            App.getContext().getDB().deleteService(
                info.interviewId,
                info.userId,
                mList,
                Calendar.getInstance().timeInMillis.toInt()
            )
            mAdapter?.removeItem(position)
            interviews.removeAt(position)
            if (IS_SAVED && interviews.size > 0) {
                binding.llSelectAll.visibility = View.VISIBLE
            } else {
                binding.llSelectAll.visibility = View.GONE
            }
            mAdapter!!.notifyDataSetChanged()
            (ctx as EarlierServicesActivity?)!!.tabCount()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onResume() {
        super.onResume()
        if (App.getContext().appSettings == null) {
            App.getContext().readAppSettings(activity)
        }
        if (!SystemUtility.isAutoTimeEnabled(activity)) {
            SystemUtility.openDateTimeSettingsActivity(activity)
        }
        //        if(IS_SAVED){
//            activityPath= Utility.setActivityPath(getActivity(),R.string.saved_interview);
//        }else{
//            activityPath=Utility.setActivityPath(getActivity(),R.string.sent_interview);
//        }
    }

    override fun onClick(v: View) {}
    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {}
    override fun passData(name: String?) {
        Toast.makeText(ctx, "" + name, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(isSaved: Boolean): PendingSyncServiceFragment {
            val homePage = PendingSyncServiceFragment()
            val args = Bundle()
            args.putBoolean("IS_SAVED", isSaved)
            homePage.arguments = args
            return homePage
        }
    }
}