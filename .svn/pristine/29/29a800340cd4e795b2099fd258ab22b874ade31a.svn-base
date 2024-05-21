package ngo.friendship.satellite.ui.beneficiary

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.adapter.BeneficiaryPageListAdapter
import ngo.friendship.satellite.adapter.PaginatedAdapterData
import ngo.friendship.satellite.asynctask.MHealthTask
import ngo.friendship.satellite.asynctask.Task
import ngo.friendship.satellite.asynctask.TaskKey
import ngo.friendship.satellite.constants.ActivityDataKey
import ngo.friendship.satellite.databinding.CommonServiceActivityBinding
import ngo.friendship.satellite.interfaces.OnCompleteListener
import ngo.friendship.satellite.jsonoperation.JSONParser
import ngo.friendship.satellite.model.Beneficiary
import ngo.friendship.satellite.model.SavedInterviewInfo
import ngo.friendship.satellite.ui.InterviewActivity
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.viewmodels.OfflineViewModel
import ngo.friendship.satellite.views.AppToast

/**
 * @author Md.Yeasin Ali
 * @created 01th Oct 2022
 */

@AndroidEntryPoint
class BeneficiaryListFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: CommonServiceActivityBinding
    private var BENEFICIARY_TYPE = ""
    private var HH_HOUSEHOLD = ""
    var beneficiaryList: ArrayList<Beneficiary> = ArrayList()
    private lateinit var benefViewModel: BeneficiaryViewModel
    private lateinit var adapter: BeneficiaryPageListAdapter

    //  var maternalInfoBeneficiaryList: ArrayList<MaternalInfo> = ArrayList()
//    lateinit var mAdapter: BasemHealthAdapter<Beneficiary, CommonServicesItemRowBinding>
    val viewModel: OfflineViewModel by viewModels()
    var conunter = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            BENEFICIARY_TYPE = requireArguments().getString(ActivityDataKey.BENEFICIARY_TYPE, "")
            HH_HOUSEHOLD = requireArguments().getString(ActivityDataKey.HH_HOUSEHOLD, "")
        }
    }

    companion object {
        fun newInstance(BENEFICIARY_TYPE: String, HH_HOUSEHOLD: String?): BeneficiaryListFragment {
            val homePage = BeneficiaryListFragment()
            val args = Bundle()
            args.putString(ActivityDataKey.BENEFICIARY_TYPE, BENEFICIARY_TYPE)
            args.putString(ActivityDataKey.HH_HOUSEHOLD, HH_HOUSEHOLD)
            homePage.arguments = args
            return homePage
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CommonServiceActivityBinding.inflate(inflater, container, false)
        App.hideProgressBar()
        // itemAdapter

//        beneficiaryList = App.getContext().db.beneficiaryList("");
//        mAdapter.addItems(beneficiaryList)


        adapter = BeneficiaryPageListAdapter(context)


        binding.rvService.setHasFixedSize(true)
        binding.rvService.setLayoutManager(LinearLayoutManager(context))
        binding.rvService.setItemAnimator(DefaultItemAnimator())

        binding.rvService.setAdapter(adapter)

        adapter.submitItems(beneficiaryList)

//        adapter.setDefaultRecyclerView(activity, R.id.rv_service)

//
//        adapter.setStartPage(1) //set first page of data. default value is 1.
//
//        adapter.setPageSize(10) //set page data size. default value is 10.
//        adapter.addItems(beneficiaryList)

        //getters
//        adapter.getStartPage(); // return start page
//        adapter.getCurrentPage(); // return last page which loaded
//        adapter.getRecyclerView(); // return recycler view

//        adapter.setOnItemClickListener(new BeneficiaryPageListAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//
//            }
//        });
//        try {
//            adapter.setOnPaginationListener(object : PaginatedAdapterData.OnPaginationListener {
//                override fun onCurrentPage(page: Int) {
//                    //   Toast.makeText(activity, "Page $page loaded!", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onNextPage(page: Int) {
//                    getNewItems(page)
//                }
//
//                override fun onFinish() {
//                    //Toast.makeText(activity, "finish", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

//        activity?.let {
//            viewModel.getBeneficiaryList(BENEFICIARY_TYPE, HH_HOUSEHOLD).observe(it) {
//                beneficiaryList = it
//                mAdapter.addItems(it)
//                mAdapter.notifyDataSetChanged()
//                App.hideProgressDialog()
//                if (it.size > 0) {
//                    binding.serviceItemNotFound.serviceNotFound.visibility = View.GONE
//                } else {
//                    binding.serviceItemNotFound.serviceNotFound.visibility = View.VISIBLE
//                }
//            }
//        }
        var tsk = MHealthTask(
            activity,
            Task.RETERIEVE_BENEFICIARY_LIST,
            R.string.load_beneficiaries,
            R.string.please_wait
        );
        tsk.setParam(""+BENEFICIARY_TYPE);
        tsk.setCompleteListener(OnCompleteListener {
            if (it.getData().containsKey(TaskKey.ERROR_MSG)) {
                AppToast.showToast(activity, it.getData().getSerializable(TaskKey.ERROR_MSG))
            }
            beneficiaryList = it.getData().getSerializable(TaskKey.DATA0) as ArrayList<Beneficiary>
            adapter.submitItems(beneficiaryList)
            App.hideProgressDialog()
            if (beneficiaryList.size > 0) {
                binding.serviceItemNotFound.serviceNotFound.visibility = View.GONE
            } else {
                binding.serviceItemNotFound.serviceNotFound.visibility = View.VISIBLE
            }


        });
        tsk.execute()

        benefViewModel = activity?.run {
            ViewModelProvider(this).get(BeneficiaryViewModel::class.java)
        } ?: throw Exception("Invalid Activity")


//        activity?.let {
//            viewModel.getBeneficiaryList(BENEFICIARY_TYPE).observe(it) {
//                beneficiaryList = it
//
//                adapter.submitItems(beneficiaryList)
//                App.hideProgressDialog()
//                if (it.size > 0) {
//                    binding.serviceItemNotFound.serviceNotFound.visibility = View.GONE
//                } else {
//                    binding.serviceItemNotFound.serviceNotFound.visibility = View.VISIBLE
//                }
//            }
//        }

        benefViewModel.selected.observe(requireActivity()) { item ->
            val filterInterviewList: ArrayList<Beneficiary> = ArrayList()
            if (item.length > 0) {
                for (single in beneficiaryList) {
                    if (single.benefName.lowercase()
                            .contains(
                                item.lowercase(),
                                ignoreCase = true
                            ) || single.benefCode.lowercase()
                            .contains(item.lowercase(), ignoreCase = true)
                    ) {
                        filterInterviewList.add(single)
                    }
                }
                adapter.clear()
                adapter.submitItems(filterInterviewList)


            } else {
                adapter.clear()
                adapter.submitItems(beneficiaryList)
            }
        }
        return binding.root
    }

    private fun getNewItems(page: Int) {
        Handler().postDelayed({
            val users = java.util.ArrayList<Beneficiary>()
            val start: Int = page * conunter - conunter
            val end: Int = page * conunter
            for (i in start until end) {
                if (i < beneficiaryList.size) {
                    users.add(beneficiaryList[i])
                }
            }
            onGetDate(users)
        }, 500)
    }

    fun onGetDate(users: ArrayList<Beneficiary>) {
        adapter.submitItems(users)
    }

    fun editInterView(info: SavedInterviewInfo) {
        var jsonStr: String = ""
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
        arguments?.let {
            BENEFICIARY_TYPE = requireArguments().getString(ActivityDataKey.BENEFICIARY_TYPE, "")
            HH_HOUSEHOLD = requireArguments().getString(ActivityDataKey.HH_HOUSEHOLD, "")
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.getContext().appSettings == null) {
            App.getContext().readAppSettings(activity)
        }
        if (!SystemUtility.isAutoTimeEnabled(activity)) {
            SystemUtility.openDateTimeSettingsActivity(activity)
        }
    }


    override fun onClick(v: View) {}


}