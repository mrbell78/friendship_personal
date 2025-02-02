package ngo.friendship.satellite.ui.stock_manage

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.asynctask.CommiunicationTask
import ngo.friendship.satellite.asynctask.MHealthTask
import ngo.friendship.satellite.asynctask.Task
import ngo.friendship.satellite.asynctask.TaskKey
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.communication.RequestData
import ngo.friendship.satellite.communication.ResponseData
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.constants.RequestName
import ngo.friendship.satellite.constants.RequestType
import ngo.friendship.satellite.databinding.ActivityCurrentStockBinding
import ngo.friendship.satellite.interfaces.OnDialogButtonClick
import ngo.friendship.satellite.model.MedicineInfo
import ngo.friendship.satellite.utility.SystemUtility
import ngo.friendship.satellite.utility.TextUtility
import ngo.friendship.satellite.utility.Utility
import ngo.friendship.satellite.views.DialogView

class CurrentStockActivity : BaseActivity(), View.OnClickListener {
    var activityPath: String? = null
    var medicineList: ArrayList<MedicineInfo> = ArrayList()
    var medicineFilterList = ArrayList<MedicineInfo>()
    private lateinit var binding: ActivityCurrentStockBinding
    var llRowContainer: LinearLayout? = null
    var popupmenu: PopupMenu? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.loadApplicationData(this)
        binding = ActivityCurrentStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableBackButton()
        title = resources.getString(R.string.title_current_stock)
        binding.layoutSync.llPenddingSync.visibility = View.GONE
        binding.layoutSync.viewLine.visibility = View.GONE
        binding.layoutSync.llAllSync.visibility = View.GONE
        binding.layoutSync.viewLine2.visibility = View.GONE
        binding.layoutSync.syncLayoutOpen.setOnClickListener { v: View? ->
            binding.layoutSync.cvSyncLayout.visibility = View.VISIBLE
            binding.layoutSync.syncLayoutOpen.visibility = View.GONE
            binding.layoutSync.syncLayoutOpen.visibility = View.GONE
            binding.layoutSync.tvGettData.setText(getString(R.string.update_stock_from_server))
        }
        binding.layoutSync.ivCloseSync.setOnClickListener { v: View? ->
            binding.layoutSync.cvSyncLayout.visibility = View.GONE
            binding.layoutSync.syncLayoutOpen.visibility = View.VISIBLE
        }
        binding.layoutSync.llGetData.setOnClickListener { v: View? ->
            if (SystemUtility.isConnectedToInternet(this)) {
                showDataRetrieveConfirmationPrompt(resources.getString(R.string.retrieve_data_confirmation_stock))
            } else {
                SystemUtility.openInternetSettingsActivity(this)
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //  callSearch(query);
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                medicineFilterList.clear()
                if (medicineList.size > 0) {
                    for (item in medicineList) {
                        if (item.toString().lowercase().contains(newText.lowercase())) {
                            medicineFilterList.add(item)
                        }
                    }
                }

                showData(medicineFilterList)
                return true
            }

        })
        binding.mFilter.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                PopMenuDisplay()
            }

            fun PopMenuDisplay() {
                popupmenu = PopupMenu(this@CurrentStockActivity, binding.mFilter)
                popupmenu!!.menuInflater.inflate(R.menu.popup, popupmenu!!.menu)
                popupmenu!!.setOnMenuItemClickListener { item ->
                    Toast.makeText(this@CurrentStockActivity, item.title, Toast.LENGTH_LONG).show()
                    when (item.itemId) {
                        R.id.medicine -> {}
                    }
                    true
                }
                popupmenu!!.show()
            }
        })

//        View v = findViewById(R.id.btn_retrieve_data);
//        v.setOnClickListener(this);
//
//        v = findViewById(R.id.btn_close);
//        v.setOnClickListener(this);
        llRowContainer = findViewById(R.id.ll_medicine_row_container)
        retrieveCurrentStock()
    }

    /**
     * Show medicine stock.
     * Prepare medicine row and add to row container layout
     */
    @SuppressLint("SetTextI18n")
    private fun showData(medicineLists: ArrayList<MedicineInfo>?) {
        llRowContainer!!.removeAllViews()
        if (medicineLists == null) {
//            AppToast.showToast(this, R.string.data_not_available);
            return
        }
        for (medInfo in medicineLists) {
            val view = View.inflate(this, R.layout.medicine_requisition_row, null)
            llRowContainer!!.addView(view)
            val tv_name = view.findViewById<TextView>(R.id.tv_name)
            val tv_type = view.findViewById<TextView>(R.id.tv_type)
            val tv_info_left = view.findViewById<TextView>(R.id.tv_info_left)
            val tv_info_right = view.findViewById<TextView>(R.id.tv_info_right)
            view.findViewById<View>(R.id.et_quantity).visibility = View.GONE
            val tv_quantity = view.findViewById<TextView>(R.id.tv_quantity)
            tv_quantity.visibility = View.VISIBLE

            if (medInfo.currentStockQuantity <= medInfo.minimumStockQuantity) {
                tv_name.setTextColor(Color.RED)
                tv_type.setTextColor(Color.RED)
                tv_info_left.setTextColor(Color.RED)
                tv_info_right.setTextColor(Color.RED)
                tv_quantity.setTextColor(Color.RED)
            }
//            else if(medInfo.getCurrentStockQuantity() ==0){
//                tv_name.setTextColor(Color.RED);
//                tv_type.setTextColor(Color.RED);
//                tv_info_left.setTextColor(Color.RED);
//                tv_info_right.setTextColor(Color.RED);
//                tv_quantity.setTextColor(Color.RED)
//                       }
            tv_name.text = medInfo.toString()
            tv_type.text = medInfo.medicineType
            tv_info_left.text = TextUtility.format("%.2f", medInfo.unitPurchasePrice)
            tv_info_right.text = TextUtility.format("%.2f", medInfo.unitSalesPrice)
            tv_quantity.text = medInfo.toString()
            tv_quantity.text = "" + medInfo.currentStockQuantity
        }
    }

    /**
     * show user confirmation alert dialog to retrieve data from server.
     *
     * @param msg will be displayed in the dialog message section.
     * This is a question to ask user whether s/he really wants to retrieve data from server
     */
    private fun showDataRetrieveConfirmationPrompt(msg: String) {
        val buttonMap = HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_confirm
        buttonMap[2] = R.string.btn_cancel
        val exitDialog =
            DialogView(this, R.string.dialog_title, msg, R.drawable.information, buttonMap)
        exitDialog.setOnDialogButtonClick(object : OnDialogButtonClick {
            override fun onDialogButtonClick(view: View?) {
                when (view!!.id) {
                    1 -> retrieveMedicines()
                }
            }
        })
        exitDialog.show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_close -> finish()
            R.id.btn_retrieve_data -> showDataRetrieveConfirmationPrompt(resources.getString(R.string.retrieve_data_confirmation))
            else -> {}
        }
    }

    /**
     * Display alert dialog with one button.
     *
     * @param msg will be displayed in the message section of the dialog
     * @param imageId is the image drawable id which will be displayed at the dialog's title
     * @param messageColor is the color id of the dialog's message
     */
    private fun showOneButtonDialog(msg: String, imageId: Int, messageColor: Int) {
        val buttonMap = HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_close
        val exitDialog =
            DialogView(this, R.string.dialog_title, msg, messageColor, imageId, buttonMap)
        exitDialog.show()
    }


    private fun retrieveCurrentStock() {
        val tsk = MHealthTask(
            this,
            Task.RETRIEVE_CURRENT_STOCK_MEDICINE_LIST,
            R.string.retrieving_data,
            R.string.please_wait
        )
        tsk.setCompleteListener { msg ->
            if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                App.showMessageDisplayDialog(
                    this@CurrentStockActivity,
                    resources.getString(R.string.retrive_error),
                    R.drawable.error,
                    Color.RED
                )
            } else {
                medicineList = msg.data.getSerializable(TaskKey.DATA0) as ArrayList<MedicineInfo>
                showData(medicineList)
            }
        }
        tsk.execute()
    }

    private fun retrieveMedicines() {
        val request =
            RequestData(
                RequestType.USER_GATE,
                RequestName.MEDICINES_STOCK,
                Constants.MODULE_DATA_GET
            )
        request.param1 = Utility.getTableRef(null, this)
        val commiunicationTask =
            CommiunicationTask(this, request, R.string.retrieving_data, R.string.please_wait)
        commiunicationTask.setCompleteListener { msg ->
            if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                App.showMessageDisplayDialog(
                    this@CurrentStockActivity,
                    resources.getString(R.string.network_error),
                    R.drawable.error,
                    Color.RED
                )
            } else {
                val response = msg.data.getSerializable(TaskKey.DATA0) as ResponseData
                if (response.responseCode.equals("00", ignoreCase = true)) {
                    App.showMessageDisplayDialog(
                        this@CurrentStockActivity,
                        response.errorCode + "-" + response.errorDesc,
                        R.drawable.error,
                        Color.RED
                    )
                } else {
                    val tsk = MHealthTask(
                        this@CurrentStockActivity,
                        Task.MEDICINE_STOCK,
                        R.string.saving_data,
                        R.string.please_wait
                    )
                    tsk.setParam(response)
                    tsk.setCompleteListener { msg ->
                        if (msg.data.containsKey(TaskKey.ERROR_MSG)) {
                            val errorMsg = msg.data.getSerializable(TaskKey.ERROR_MSG) as String?
                            App.showMessageDisplayDialog(
                                this@CurrentStockActivity,
                                resources.getString(R.string.saving_error),
                                R.drawable.error,
                                Color.RED
                            )
                        } else {

                            medicineList =
                                msg.data.getSerializable(TaskKey.DATA0) as ArrayList<MedicineInfo>
                            App.showMessageDisplayDialog(
                                this@CurrentStockActivity,
                                resources.getString(R.string.retrieve_successfull),
                                R.drawable.information,
                                Color.BLACK
                            )
                            showData(medicineList)

//                            if (JSONParser.getLongNullAllow(
//                                    response.paramJson,
//                                    KEY.NEED_SAME_REQ
//                                ) == 1L
//                            ) {
//                                retrieveMedicines()
//                            } else {
//                                App.showMessageDisplayDialog(this@CurrentStockActivity, resources.getString(R.string.retrieve_successfull), R.drawable.information, Color.BLACK)
//                                retrieveCurrentStock()
//                            }
                        }
                    }
                    tsk.execute()
                }
            }
        }
        commiunicationTask.execute()
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