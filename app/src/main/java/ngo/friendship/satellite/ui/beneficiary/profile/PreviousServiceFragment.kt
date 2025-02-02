package ngo.friendship.satellite.ui.beneficiary.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.adapter.BasemHealthAdapter
import ngo.friendship.satellite.databinding.CommonServiceActivityBinding
import ngo.friendship.satellite.databinding.PreviousServiceFragmentInterviewDetailsBinding
import ngo.friendship.satellite.databinding.PreviousServiceFragmentRowBinding
import ngo.friendship.satellite.jsonoperation.JSONParser
import ngo.friendship.satellite.model.Beneficiary
import ngo.friendship.satellite.model.SavedInterviewInfo
import ngo.friendship.satellite.utility.Utility
import ngo.friendship.satellite.viewmodels.OfflineViewModel
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class PreviousServiceFragment : Fragment() {
    private lateinit var beneficiary: Beneficiary
    private lateinit var binding: CommonServiceActivityBinding;
    val viewModel: OfflineViewModel by viewModels()
    lateinit var mAdapter: BasemHealthAdapter<SavedInterviewInfo, PreviousServiceFragmentRowBinding>

    companion object {
        fun newInstance(data: String): PreviousServiceFragment {
            val fragment = PreviousServiceFragment()
            val args = Bundle()
            args.putString("key", data)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CommonServiceActivityBinding.inflate(
            layoutInflater
        )
        itemAdapter
        val data = arguments?.getString("key")
        if (data != null) {
            val gson = Gson()

            try {
                beneficiary = gson.fromJson(data, Beneficiary::class.java)
                val langCode = App.getContext().appSettings.language
                var interviewList: ArrayList<SavedInterviewInfo> = App.getContext().getDB() .getInterviewListByBenefCode(langCode, beneficiary.benefCodeFull);
                if (interviewList.size > 0) {
                    mAdapter.addItems(interviewList)
                    binding.serviceItemNotFound.serviceNotFound.visibility = View.GONE
                } else {
                    binding.serviceItemNotFound.serviceNotFound.visibility = View.VISIBLE
                }

//                activity?.let {
//                    viewModel.getInterviewListByBenefCode(beneficiary.benefCodeFull)
//                        .observe(it) { interviewList ->
//                            if (interviewList.size > 0) {
//                                mAdapter.addItems(interviewList)
//                                binding.serviceItemNotFound.serviceNotFound.visibility = View.GONE
//                            } else {
//                                binding.serviceItemNotFound.serviceNotFound.visibility = View.VISIBLE
//                            }
//
//                        }
//                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return binding.root
    }

    private val itemAdapter: Unit
        private get() {
            mAdapter =
                object :
                    BasemHealthAdapter<SavedInterviewInfo, PreviousServiceFragmentRowBinding>() {
                    override fun onItemLongClick(
                        view: View,
                        model: SavedInterviewInfo,
                        position: Int
                    ) {
                    }

                    override fun onItemClick(
                        view: View,
                        userScheduleInfo: SavedInterviewInfo,
                        position: Int,
                        dataBinding: PreviousServiceFragmentRowBinding
                    ) {
                        showCustomDialog(userScheduleInfo)
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onBindData(
                        model: SavedInterviewInfo,
                        position: Int,
                        dataBinding: PreviousServiceFragmentRowBinding
                    ) {
                        val inputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                        val outputDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                        try {
                            val date = inputDateFormat.parse(model.date)
                            val formattedDate = outputDateFormat.format(date)

                            dataBinding.tvDate.setText("" + formattedDate)
                        } catch (e: Exception) {
                            dataBinding.tvDate.setText("" + model.date)
                            println("Error parsing or formatting date: ${e.message}")
                        }


                    }

                    override val layoutResId: Int
                        get() = R.layout.previous_service_fragment_row
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

    private fun showCustomDialog(userScheduleInfo: SavedInterviewInfo) {
        val inflater = LayoutInflater.from(requireContext())
        val bindingDig: PreviousServiceFragmentInterviewDetailsBinding =
            PreviousServiceFragmentInterviewDetailsBinding.inflate(inflater, null, false)
        try {
            bindingDig.tvBenefName.setText("" + beneficiary.benefName)
            bindingDig.tvBeneficiaryCode.setText("" + beneficiary.benefCode)
            bindingDig.tvServiceType.setText(
                "" + userScheduleInfo.questionnarieTitle + " " + (activity?.resources?.getString(
                    R.string.services
                ))
            )

            bindingDig.mobileNo.setText("" + userScheduleInfo.date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val algoList = ArrayList<String>()
        algoList.add(userScheduleInfo.getQuestionnarieName())

        val qAnsJson: String = userScheduleInfo.questionAnswerJson
        val qJson: String = userScheduleInfo.questionJson
        val qusDataList = JSONParser.getJsonArray(JSONObject(qJson), "questionnaireDetails")
        var ansDataList:JSONArray = JSONArray();
        try {
            ansDataList = JSONParser.getJsonArray(JSONObject(qAnsJson), "questions")
        }catch (e:Exception){
            e.printStackTrace()
        }

        //        for (QuestionAnswer qa : qaArrList) {
//            if (qa.getAnswerList() != null) {
//                StringBuilder sbAnswer = new StringBuilder();
//                sbAnswer.append(TextUtils.join("|", qa.getAnswerList()));
//
//                if (qa.getType().equalsIgnoreCase(QUESTION_TYPE.BINARY) && sbAnswer.toString() != null && sbAnswer.toString().trim().length() > 0) {
//                    img = FileOperaion.decodeImageFile(sbAnswer.toString(), 100);
//                    continue;
//                }
//            }}
        val sb = StringBuilder()
        var outputAnsStrRaw: String = ""


//        var d = 1
        var precisionJsonArray: JSONArray = JSONArray();
        for (i in 0 until ansDataList.length()) {
            val dataInterviewDetails = ansDataList.getJSONObject(i)
            val qId = JSONParser.getString(dataInterviewDetails, "qkey").toInt()
            val isShowAbleQus: Long = getQusShowAbleOutputbyQusId(qusDataList, qId).toLong()
            val qName: String = getQusNamebyQusId(qusDataList, qId)
            val qType: String = getQusTypebyQusId(qusDataList, qId)
            val answer = JSONParser.getString(dataInterviewDetails, "answer")
//                    && qName != Constants.SYS_SOURCE_INTERVIEW
            if (isShowAbleQus == 1L) {
                if (!answer.isEmpty() || answer != null) {
                    var outputAnsStr: String


                    if ("Medicine".toLowerCase().contains(qType.toLowerCase())) {
                        var prescriptionMedicine = ""
                        if (answer.contains("|")) {
                            val medicineReplaceSplitCharacter = answer.replace("|", ";")
                            var prescriptionForMutipleMedicine = ""
                            val arrayOfMutipleMedicine = medicineReplaceSplitCharacter.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            for (m in arrayOfMutipleMedicine) {
                                precisionJsonArray.put(JSONObject(m))
                                Log.d(TAG, "showCustomDialog: ..........multi med" + m)
                                prescriptionForMutipleMedicine += Utility.humanReadablePrescription(
                                    m
                                ) + "\n"
                            }
                            prescriptionMedicine += prescriptionForMutipleMedicine
                        } else {
                            try {
                                precisionJsonArray.put(JSONObject(answer))
                                prescriptionMedicine += Utility.humanReadablePrescription(answer)
                            }catch (e:Exception){e.printStackTrace()}

                            
                        }

//                                      outputAnsStr=Utility.humanReadablePrescription(JSONParser.getString(dataInterviewDetails,
//                                              "outputAns"));

                        outputAnsStr = prescriptionMedicine

                    } else {

                        outputAnsStr = answer.replace("_", " ")
                    }

                    val tableRow = LayoutInflater.from(activity)
                        .inflate(R.layout.row_table_layout, null)
                    val tvValue1 = tableRow.findViewById<View>(R.id.tvValue1) as TextView
                    val tvValue2 = tableRow.findViewById<View>(R.id.tvValue2) as TextView
                    tvValue1.visibility = View.VISIBLE
                    tvValue2.visibility = View.VISIBLE
                    val wv = tableRow.findViewById<WebView>(R.id.wv_webview)

                    val mimeType = "text/html"
                    val encoding = "UTF-8"
                    val webSettings = wv.settings
                    webSettings.javaScriptEnabled = true
                    wv.settings.defaultZoom = WebSettings.ZoomDensity.FAR
                    wv.settings.loadWithOverviewMode = true
                    wv.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
                    wv.settings.useWideViewPort = true

                    //                                  TableRow tableRow = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_card_table_layout, null);
//                                  CardButton tvValue1= (CardButton) tableRow.findViewById(R.id.tvValue1);
//                                  CardButton tvValue2= (CardButton) tableRow.findViewById(R.id.tvValue2);
//                                  tvValue1.setVisibility(View.VISIBLE);
//                                  tvValue2.setVisibility(View.VISIBLE);
                    tvValue1.text = qName.replace("_", " ")
                    if ("REFERRAL_CENTER".equals(qName, ignoreCase = true)) {
                        try {
                            val referId = outputAnsStr.toLong()
                            val longCode = App.getContext().getAppSettings().getLanguage();
                            val refCenterName: String =
                                App.getContext().getDB().getRefcenterNameById(referId, longCode)
                            tvValue2.text = refCenterName
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else if ("Medicine".equals("" + qName)) {
                        tvValue2.visibility = View.GONE
                        if (precisionJsonArray.length() > 0) {

                            sb.append(
                                "<!DOCTYPE html>" +
                                        "<html>" +
                                        "<head>" +
                                        "</head>" +
                                        " <style>" +
                                        "        table {" +
                                        "            border-collapse: collapse;" +
                                        "            width: 100%;" +
                                        "            margin: auto;" +
                                        "        }" +
                                        "        th, td {" +
                                        "            border: 1px solid black;" +
                                        "            padding: 8px;" +
                                        "            text-align: center;" +
                                        "             font-size: 28px;" +
                                        "        }" +
                                        "    </style>" +
                                        "<body>"
                            )
                            sb.append(
                                """ <table>
                                <tr>
                                     <th class="large-font">Name</th>
                                     <th class="large-font"> Quantity</th>
                                     <th class="large-font">Doses</th>
                                     <th class="large-font">Days</th>
                                     <th class="large-font">Taking Time</th>
                                </tr>
                            """
                            )
                            for (i in 0 until precisionJsonArray.length()) {
                                val jsonObject: JSONObject = precisionJsonArray.getJSONObject(i)
                                val name = JSONParser.getString(jsonObject, "MED_NAME")
                                Log.d(TAG, "showAllAnswer: ....the json obj is $jsonObject")
                                sb.append("<tr>")
                                sb.append("<td>")
                                sb.append(name)
                                sb.append("</td>")
                                val medQuantity = JSONParser.getString(jsonObject, "MED_QTY")
                                sb.append("<td>")
                                sb.append(medQuantity)
                                sb.append("</td>")
                                val doses = JSONParser.getString(jsonObject, "DOSES")
                                if (doses != "null") {
                                    Log.d(TAG, "showAllAnswer: ......the does is $doses")
                                    sb.append("<td>")
                                    sb.append(doses)
                                    sb.append("</td>")
                                } else {
                                    // Log.d(TAG, "showAllAnswer: ......the does is "+doses);
                                    sb.append("<td>")
                                    sb.append("")
                                    sb.append("</td>")
                                }
                                val days = JSONParser.getString(jsonObject, "DAYS")
                                if (days != "null") {
                                    sb.append("<td>")
                                    sb.append(days)
                                    sb.append("</td>")
                                } else {
                                    sb.append("<td>")
                                    sb.append("")
                                    sb.append("</td>")
                                }
                                val takingTime = JSONParser.getString(jsonObject, "TAKINGTIME")
                                if (takingTime != "null") {
                                    sb.append("<td>")
                                    sb.append(takingTime)
                                    sb.append("</td>")
                                } else {
                                    sb.append("<td>")
                                    sb.append("")
                                    sb.append("</td>")
                                }
                                sb.append("</tr>")
                            }
                        }

                        sb.append("</table>")
                        sb.append(
                            "</body>" +
                                    "</html>"
                        )
                        // answerShowDilog.show();

                        wv.loadDataWithBaseURL("", sb.toString(), mimeType, encoding, "")

//        sb.append("\n" +
//                "    <p>\n" + sb.toString() + "</p>"+


//                        val medicineList = JSONArray(outputAnsStr)
//
//                        for (j in 0 until medicineList.length()) {
//                            val dlistdata: JSONObject = medicineList.getJSONObject(j)
//                            val value = JSONParser.getString(dlistdata, "value")
//                            Log.d(TAG, "showCustomDialog: .......value is"+value)
//                        }
                    } else {
                        tvValue2.text = outputAnsStr
                    }
//                    if (d % 2 != 0) {
//                        tvValue1.setBackgroundColor(Color.parseColor("#e0e0e0"))
//                        tvValue2.setBackgroundColor(Color.parseColor("#e0e0e0"))
//                    }
                    bindingDig.llContainer.addView(tableRow)
                    // d++
                }
            }
        }


        val builder = AlertDialog.Builder(requireContext())
        builder.setView(bindingDig.root)


        val dialog = builder.create()
        dialog.show()

        bindingDig.tvClear.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun getQusShowAbleOutputbyQusId(qusArray: JSONArray, qusId: Int): Int {
        for (i in 0 until qusArray.length()) {
            try {
                val singleQus = qusArray.getJSONObject(i)
                val qustionId: Int = JSONParser.getInt(singleQus, "QUESTION_ID")
                if (qustionId == qusId) {
                    return JSONParser.getInt(singleQus, "SHOWABLE_OUTPUT")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return 0
    }

    fun getQusNamebyQusId(qusArray: JSONArray, qusId: Int): String {
        for (i in 0 until qusArray.length()) {
            try {
                val singleQus = qusArray.getJSONObject(i)
                val qustionId = JSONParser.getInt(singleQus, "QUESTION_ID")
                if (qustionId == qusId) {
                    return JSONParser.getString(singleQus, "Q_NAME")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return ""
    }

    fun getQusTypebyQusId(qusArray: JSONArray, qusId: Int): String {
        for (i in 0 until qusArray.length()) {
            try {
                val singleQus = qusArray.getJSONObject(i)
                val qustionId = JSONParser.getInt(singleQus, "QUESTION_ID")
                if (qustionId == qusId) {
                    return JSONParser.getString(singleQus, "Q_NAME")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return ""
    }

}