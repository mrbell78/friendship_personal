package ngo.friendship.satellite.ui.beneficiary

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.adapter.BasemHealthAdapter
import ngo.friendship.satellite.asynctask.MHealthTask
import ngo.friendship.satellite.asynctask.Task
import ngo.friendship.satellite.asynctask.TaskKey
import ngo.friendship.satellite.constants.ActivityDataKey
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.databinding.CommonServiceActivityBinding
import ngo.friendship.satellite.databinding.CommonServicesItemRowBinding
import ngo.friendship.satellite.jsonoperation.JSONParser
import ngo.friendship.satellite.model.SavedInterviewInfo
import ngo.friendship.satellite.model.maternal.MaternalInfo
import ngo.friendship.satellite.ui.InterviewActivity
import ngo.friendship.satellite.ui.beneficiary.profile.BeneficiaryProfileActivity
import ngo.friendship.satellite.utility.FileOperaion
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.utility.Utility
import java.text.DecimalFormat

/**
 * @author Md.Yeasin Ali
 * @created 01th Oct 2022
 */
class maternalMotherListFragment : Fragment(), View.OnClickListener {
    private var _binding: CommonServiceActivityBinding? = null
    private val binding get() = _binding!!
    private var IS_SAVED = 0
    var maternalInfoBeneficiaryList: ArrayList<MaternalInfo> = ArrayList()
    lateinit var mAdapter: BasemHealthAdapter<MaternalInfo, CommonServicesItemRowBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            IS_SAVED = requireArguments().getInt("IS_SAVED", 0)
        }
    }

    companion object {
        fun newInstance(isSaved: Int): maternalMotherListFragment {
            val homePage = maternalMotherListFragment()
            val args = Bundle()
            args.putInt("IS_SAVED", isSaved)
            homePage.arguments = args
            return homePage
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CommonServiceActivityBinding.inflate(inflater, container, false)
        App.loadApplicationData(activity)
        itemAdapter
        return binding.root
    }


    //   openQuestionnair(position);
    private val itemAdapter: Unit
        private get() {
            mAdapter =
                object : BasemHealthAdapter<MaternalInfo, CommonServicesItemRowBinding>() {
                    override fun onItemLongClick(
                        view: View,
                        model: MaternalInfo,
                        position: Int
                    ) {
                    }

                    override fun onItemClick(
                        view: View,
                        beneficiary: MaternalInfo,
                        position: Int,
                        dataBinding: CommonServicesItemRowBinding
                    ) {
                        var gson: Gson = Gson()
                        var singleBenef: String = gson.toJson(beneficiary).toString()
                        val benefProfile = Intent(requireActivity(), BeneficiaryProfileActivity::class.java)
                        benefProfile.putExtra("BENEF_PROFILE", singleBenef)
                        startActivity(benefProfile);
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onBindData(
                        model: MaternalInfo,
                        position: Int,
                        dataBinding: CommonServicesItemRowBinding
                    ) {
                        dataBinding.tvBenefName.text = model.benefName
                        dataBinding.tvBeneficiaryCode.text = "" + model.benefCode
                        dataBinding.tvServiceType.text = "" + model.lastserviceName
                        dataBinding.flMaternal.visibility = View.VISIBLE
                        dataBinding.llEdd.visibility = View.VISIBLE
                        dataBinding.llFollowUp.visibility = View.VISIBLE
                        dataBinding.tvPregnant.visibility = View.VISIBLE
                        dataBinding.tvVisited.visibility = View.GONE
                        dataBinding.tvEdd.text = "" + model.edd
                        val nft = DecimalFormat("#00.###")
                        nft.isDecimalSeparatorAlwaysShown = false
                        val prgWeek: String = nft.format(
                           model.getPregnancyDurationInWeek()
                        ) + "W"
                        dataBinding.tvPregnant.text = prgWeek
                        dataBinding.cartBadge.text = ""+model.noOfRiskItem


                        if (model.getUserScheduleInfos().get(0)
                                .getScheduleDate() > 1
                        ) {
                            dataBinding.tvFollowUp.text = Utility.getDateFromMillisecond(
                                model.getUserScheduleInfos().get(0)
                                    .getScheduleDate(),
                                Constants.DATE_FORMAT_DD_MM_YY
                            )
                        }

                        if (model.gender.equals("F")) {
                            dataBinding.tvAgeGender.text = "Female (" + model.age + ")"
                        } else if (model.gender.equals("M")) {
                            dataBinding.tvAgeGender.text = "Male (" + model.age + ")"
                        }


                        if (FileOperaion.isExist(
                                model.benefImagePath
                            )
                        ) {
                            dataBinding.ivBeneficiary.setImageBitmap(
                                FileOperaion.decodeImageFile(
                                    model.benefImagePath,
                                    60
                                )
                            )
                        } else {
                            if (model.gender.equals("F")) {
                                dataBinding.ivBeneficiary.setImageResource(R.drawable.ic_default_woman)
                            } else if (model.gender.equals("M")) {
                                dataBinding.ivBeneficiary.setImageResource(R.drawable.ic_default_man)
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

    fun editInterView(info: SavedInterviewInfo) {
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
            intent.putExtra("EDIT", true)
            startActivity(intent)
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
        //        if(IS_SAVED){
//            activityPath= Utility.setActivityPath(getActivity(),R.string.saved_interview);
//        }else{
//            activityPath=Utility.setActivityPath(getActivity(),R.string.sent_interview);
//        }


        reterieveMaternalBeneficiaryList()


    }

    private fun reterieveMaternalBeneficiaryList() {

        val tsk = MHealthTask(
            activity,
            Task.RETERIEVE_MATERNAL_BENEFICIARY_LIST,
            R.string.retrieving_data,
            R.string.please_wait
        )
        tsk.setParam("")
        tsk.setCompleteListener { msg ->
            if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                App.showMessageDisplayDialog(
                    activity,
                    resources.getString(R.string.retrive_error),
                    R.drawable.error,
                    Color.RED
                )
            } else {
                maternalInfoBeneficiaryList =
                    msg.data.getSerializable(TaskKey.DATA0) as ArrayList<MaternalInfo>
            }
        }
        tsk.execute()
    }


    override fun onClick(v: View) {}


}