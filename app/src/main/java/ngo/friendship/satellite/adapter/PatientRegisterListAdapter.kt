package ngo.friendship.satellite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import ngo.friendship.satellite.databinding.BenefPatientRegisterRowBinding
import ngo.friendship.satellite.jsonoperation.JSONParser
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat


class PatientRegisterListAdapter(var ctx: Context, private var jsonArray: JSONArray) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private val fullJsonArray: JSONArray = jsonArray

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = BenefPatientRegisterRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        // return itemView
        return ViewHolderProduct(binding, ctx)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = jsonArray.getJSONObject(position)
        val viewHolder = holder as ViewHolderProduct
        viewHolder.bind(item)
    }

    override fun getItemCount(): Int {
        return jsonArray.length()
    }

    fun filterList(filteredList: JSONArray) {
        jsonArray = filteredList
        notifyDataSetChanged()
    }

    class ViewHolderProduct(
        private val binding: BenefPatientRegisterRowBinding,
        private val ctx: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(singleRowData: JSONObject) {
            binding.apply {
                val benefCode = JSONParser.getString(singleRowData, "BENEF_CODE")
                val sexInfo = JSONParser.getString(singleRowData, "SEX")

                val SERVICE = JSONParser.getString(singleRowData, "SERVICE")
                val NAME = JSONParser.getString(singleRowData, "NAME")
                val MEDICINE_LIST = JSONParser.getString(singleRowData, "MEDICINE_LIST")
                val DISEASE = JSONParser.getString(singleRowData, "DISEASE")
                val AGE_IN_DAY = JSONParser.getString(singleRowData, "AGE_IN_DAY")
                var ADDRESS = JSONParser.getString(singleRowData, "ADDRESS")
                var USER_NAME = JSONParser.getString(singleRowData, "USER_NAME")
                try {
                    val parts = ADDRESS.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    ADDRESS = "" + parts[0]
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                tvLocation.text = ADDRESS
                var roundedAge = 0;
                try {
                    val ages = AGE_IN_DAY.toLong()
                    val calAge = ages / 365.25
                    roundedAge = Math.floor(calAge).toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                var count = position;
                count++

                tvSL.text = "" + count

//                tvServiceDate.setText("" + formattedDate)
//                tvBenefCode.setText("" + BENEF_CODE)
//
//                tvBenefName.text = "" + NAME
                tvBenefInfo.text = "$NAME"
                tvBenefOtherInfo.text = "$benefCode " + "-" + "$sexInfo/ $roundedAge y"
                tvService.text = "" + SERVICE
                // Split the string based on comma
                val parts = MEDICINE_LIST.split(",")

                // Create a new string with each part on a new line
                val result = parts.joinToString(",\n")
                tvMedicine.text = "" + result
                tvDisease.text = "" + DISEASE
                val serviceDate = JSONParser.getString(singleRowData, "SERVICE_DATE")
                tvFcmInfo.text = "FCM: " + USER_NAME

                try {
                    val isNewDate: Boolean =
                        JSONParser.getString(singleRowData, "IS_NEW_DATE").toBoolean()
                    if (isNewDate) {
                        tvDate.visibility = View.VISIBLE
                        var formattedDate = ""
                        try {
                            val serviceDate = JSONParser.getString(singleRowData, "SERVICE_DATE")
                            val inputFormatter = SimpleDateFormat("yyyy-MM-dd")
                            val date = inputFormatter.parse(serviceDate)

                            // Define the desired date format
                            val outputFormatter = SimpleDateFormat("dd-MMM-yyyy")

                            // Format the date to the desired format
                            formattedDate = outputFormatter.format(date)
                            tvDate.text = "Date: " + formattedDate
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        tvDate.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }



    interface ItemInterface {
        fun onIteEdit(item: JSONObject)
        fun onItemDelete(item: JSONObject)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString = charSequence.toString()
                val filterResults = FilterResults()

                if (charString.isEmpty()) {
                    filterResults.values = fullJsonArray
                } else {
                    val newArray = JSONArray()
                    val ln = fullJsonArray.length()

                    for (i in 0 until ln) {
                        try {
                            val obj = fullJsonArray.getJSONObject(i)
                            if (JSONParser.getString(obj, "SERVICE").toLowerCase().contains(charString.toLowerCase())
                                || JSONParser.getString(obj, "DISEASE").toLowerCase().contains(charString.toLowerCase())
                                || JSONParser.getString(obj, "USER_NAME").toLowerCase().contains(charString.toLowerCase())
                                || JSONParser.getString(obj, "MEDICINE_LIST").toLowerCase().contains(charString.toLowerCase()
                                )
                            ) {
                                newArray.put(obj)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    filterResults.values = newArray
                }

                return filterResults
            }

            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
                jsonArray = filterResults?.values as JSONArray
                notifyDataSetChanged()
            }
        }
    }

}