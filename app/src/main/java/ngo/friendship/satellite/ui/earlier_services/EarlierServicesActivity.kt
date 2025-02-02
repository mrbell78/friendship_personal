package ngo.friendship.satellite.ui.earlier_services

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.asynctask.*
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.communication.RequestData
import ngo.friendship.satellite.communication.ResponseData
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.constants.RequestName
import ngo.friendship.satellite.constants.RequestType
import ngo.friendship.satellite.databinding.ActivityEarlierServicesBinding
import ngo.friendship.satellite.error.MhealthException
import ngo.friendship.satellite.interfaces.OnCompleteListener
import ngo.friendship.satellite.interfaces.OnInterviewUploadListener
import ngo.friendship.satellite.model.InterviewInfoSyncUnsync
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.views.AppToast
import ngo.friendship.satellite.views.MdiTextView
import org.json.JSONArray
import org.json.JSONException

/**
 * @author Md.Yeasin Ali
 * @created 01th Oct 2022
 */
@AndroidEntryPoint
class EarlierServicesActivity : BaseActivity(), OnInterviewUploadListener, OnCompleteListener {
    var interviewList = ArrayList<InterviewInfoSyncUnsync>()
    private lateinit var binding: ActivityEarlierServicesBinding;
    private lateinit var viewModel: EarlierServiceViewModel
    var dlog: ProgressDialog? = null
    var pageType: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEarlierServicesBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        viewModel = run {
            ViewModelProvider(this).get(EarlierServiceViewModel::class.java)
        }

        title = getString(R.string.earlier_services)
        enableBackButton()
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            pageType = bundle.getString("PAGE_TYPE").toString()
        }


        tabCount();

        if (pageType == "SERVER_TO_SENT") {
            isTabActiveDeactive(
                binding.llTabSyncd,
                binding.tvSyncdSyncd,
                binding.mdiSyncd,
                binding.llTabPending,
                binding.tvTabPending,
                binding.mdiTabPending
            )

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PendingSyncServiceFragment.newInstance(false))
                .commit()
        } else {
            isTabActiveDeactive(
                binding.llTabPending,
                binding.tvTabPending,
                binding.mdiTabPending,
                binding.llTabSyncd,
                binding.tvSyncdSyncd,
                binding.mdiSyncd
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PendingSyncServiceFragment.newInstance(true))
                .commit()

        }
        binding.llTabPending.updatePadding(top = 5, bottom = 5);
        binding.llTabSyncd.updatePadding(top = 5, bottom = 5);

        binding.llTabPending.setOnClickListener {

            isTabActiveDeactive(
                binding.llTabPending,
                binding.tvTabPending,
                binding.mdiTabPending,
                binding.llTabSyncd,
                binding.tvSyncdSyncd,
                binding.mdiSyncd
            )
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PendingSyncServiceFragment.newInstance(true))
                .commit()
        }
        binding.llTabSyncd.setOnClickListener {

            isTabActiveDeactive(
                binding.llTabSyncd,
                binding.tvSyncdSyncd,
                binding.mdiSyncd,
                binding.llTabPending,
                binding.tvTabPending,
                binding.mdiTabPending
            )

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PendingSyncServiceFragment.newInstance(false))
                .commit()
        }
    }

    private fun isTabActiveDeactive(
        llTabactvieName: LinearLayout,
        tvTabActiveName: TextView,
        mdiActive: MdiTextView,
        llTabDeactvieName: LinearLayout,
        tvTabDeactvieName: TextView,
        mdiDeactive: MdiTextView
    ) {

        llTabactvieName.setBackground(
            ContextCompat.getDrawable(
                this,
                R.drawable.border_rounded_corner
            )
        )
        tvTabActiveName.setTextColor(ContextCompat.getColor(this, R.color.white))
        mdiActive.setTextColor(ContextCompat.getColor(this, R.color.white))

        llTabDeactvieName.setBackground(
            ContextCompat.getDrawable(
                this,
                R.drawable.border_rounded_corner_white
            )
        )

        tvTabDeactvieName.setTextColor(ContextCompat.getColor(this, R.color.black))
        mdiDeactive.setTextColor(ContextCompat.getColor(this, R.color.black))
    }


    fun actionMenuHiddenShow() {
        invalidateOptionsMenu();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.common_menu_search_sync, menu)
        val item = menu?.findItem(R.id.action_search);
        val itemSycn = menu?.findItem(R.id.action_sync);
        if (item != null) {
            item.setVisible(true)
        }
        if (itemSycn != null) {
            if (interviewList.size > 0) {
                itemSycn.setVisible(true)
                if (item != null) {
                    item.setVisible(false)
                }
            } else {
                itemSycn.setVisible(false)
            }

        }

//        if (hiddenSearchMenu){
//            if (item != null) {
//                item.setVisible(false)
//            }
//        }
        val searchView = item?.actionView as SearchView
        // search queryTextChange Listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (!p0.equals("")) {
//                    fragmentCommunicator.passData("Yeasin")
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.equals("")) {
                    if (query != null) {
                        viewModel.selectedItem(query)
                    }
                } else {
                    viewModel.selectedItem("")
                }

                return true
            }
        })


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        } else if (id == R.id.action_sync) {

            uploadUnSyncData();

        }
        return super.onOptionsItemSelected(item)
    }



    public fun showInterview(interivew: ArrayList<InterviewInfoSyncUnsync>) {

        interviewList = interivew;
        actionMenuHiddenShow()
    }

    override fun onInterviewUploadFinished(message: String?) {
        dlog?.dismiss()
        tabCount();
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, PendingSyncServiceFragment.newInstance(true)).commit()
        }catch (e:Exception){e.printStackTrace()}

    }

    private fun uploadUnSyncData() {
        if (interviewList.size == 0) {
            AppToast.showToast(this, R.string.no_selected_interview)
            return
        }
        val householdList = App.getContext().db.householdBasicDataListUnsend
        if (householdList.size > 0) {
            if (SystemUtility.isConnectedToInternet(this)) {
                try {
                    val request = RequestData(
                        RequestType.TRANSACTION,
                        RequestName.HOUSEHOLD_DATA_ENTRY,
                        Constants.MODULE_BUNCH_PUSH
                    )
                    val jHouseholdListArr = JSONArray()
                    for (householdInfo in householdList) {
                        if (householdInfo.sent == 1L) continue
                        jHouseholdListArr.put(householdInfo.toJson())
                    }
                    request.getData().put("householdList", jHouseholdListArr)
                    val commiunicationTask = CommiunicationTask(
                        this@EarlierServicesActivity,
                        request,
                        R.string.uploading_data,
                        R.string.please_wait
                    )
                    commiunicationTask.setCompleteListener { msg ->
                        val response: ResponseData? =
                            msg.data.getSerializable(TaskKey.DATA0) as ResponseData?
                        val tsk = MHealthTask(
                            this@EarlierServicesActivity,
                            Task.RETRIEVE_HOUSEHOLD_BASICDATA_INFORMATION_LIST,
                            R.string.retrieving_data,
                            R.string.please_wait
                        )
                        tsk.setParam(response)
                        tsk.setCompleteListener { }
                        tsk.execute()
                        dlog = ProgressDialog.show(
                            this@EarlierServicesActivity,
                            resources.getString(R.string.uploading_data),
                            resources.getString(R.string.please_wait)
                        )
                        val uit = UploadInterviewTaskEarlierService(
                            this@EarlierServicesActivity,
                            interviewList,
                            Constants.MODULE_BUNCH_PUSH
                        )
                        uit.setOnInterviewUploadListener(this@EarlierServicesActivity)
                        uit.execute()
                    }
                    commiunicationTask.execute()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: MhealthException) {
                    e.printStackTrace()
                }
            } else {
                SystemUtility.openInternetSettingsActivity(this)
            }
        } else {
            dlog = ProgressDialog.show(
                this,
                resources.getString(R.string.uploading_data),
                resources.getString(R.string.please_wait)
            )
//            var interviewListService = ArrayList<InterviewInfoSyncUnsync>()
//            var interviewListDoctorFeedback = ArrayList<InterviewInfoSyncUnsync>()
//            for (interview: InterviewInfoSyncUnsync in interviewList) {
//                if (interview.docFollowupId > 0) {
//
//                   var  patientInterviewDoctorFeedbackList:ArrayList<PatientInterviewDoctorFeedback>
//                    interviewListDoctorFeedback.add(interview)
//                } else {
//                    interviewListService.add(interview)
//                }
//            }
            if (interviewList.size > 0) {
                val uit =
                    UploadInterviewTaskEarlierService(this, interviewList, Constants.MODULE_BUNCH_PUSH)
                uit.setOnInterviewUploadListener(this)
                uit.execute()
            }

//            if (interviewListDoctorFeedback.size > 0) {
//
//
//                //				new UploadInterviewTask(getSelectedInterviewList()).execute();
////                val uitDoctor = UploadDoctorFeedbackTask(this, interviewListDoctorFeedback)
////                uitDoctor.setOnInterviewUploadListener(this)
////                uitDoctor.execute()
//            }
        }


    }

    public fun tabCount() {
        var interviewListUnsendCount = App.getContext().db.getInterviewListCount(
            "N",
            App.getContext().appSettings.language,
            App.getContext().userInfo.userCode
        )
        interviewListUnsendCount = interviewListUnsendCount+App.getContext().db.getInterviewListCount(
            "NR",
            App.getContext().appSettings.language,
            App.getContext().userInfo.userCode
        )
//        val interviewListsendCount = App.getContext().db.getInterviewListCount(
//            "Y",
//            App.getContext().appSettings.language,
//            App.getContext().userInfo.userCode
//        )
        binding.tvSyncdSyncd.text =
            getString(R.string.syncd_data_list)
        binding.tvTabPending.text =
            getString(R.string.unsync_list) + " (${interviewListUnsendCount})"

    }

    public fun tabCountSearch( sent:Int) {
        binding.tvSyncdSyncd.text =
            getString(R.string.syncd_data_list) + " (${sent})"
    }
    override fun onComplete(msg: Message?) {
        var data = msg?.getData()?.getSerializable(TaskKey.DATA0);
        Log.e("datas", data.toString());
    }
}