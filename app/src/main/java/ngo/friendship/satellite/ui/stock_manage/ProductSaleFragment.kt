package ngo.friendship.satellite.ui.stock_manage

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.constants.KEY
import ngo.friendship.satellite.databinding.FragmentProductSaleBinding
import ngo.friendship.satellite.interfaces.OnDialogButtonClick
import ngo.friendship.satellite.jsonoperation.JSONParser
import ngo.friendship.satellite.model.MedicineInfo
import ngo.friendship.satellite.utility.AppPreference
import ngo.friendship.satellite.utility.TextUtility
import ngo.friendship.satellite.utility.Utility
import ngo.friendship.satellite.views.DialogView
import org.json.JSONArray
import java.util.Calendar

class ProductSaleFragment : Fragment() {
    private lateinit var binding: FragmentProductSaleBinding;
    var medicineList: ArrayList<MedicineInfo>? = null
    var soldMedicineList: ArrayList<MedicineInfo>? = null
    var popupmenu: PopupMenu? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductSaleBinding.inflate(
            layoutInflater, container, false
        )
        //        binding.toolbar.tvProductTitle.setText(getResources().getString(R.string.product_sale));
//        binding.toolbar.llCard.setVisibility(View.VISIBLE);
//        binding.toolbar.actionCart.setVisibility(View.VISIBLE);
//        binding.toolbar.tvTotalMedicinePrice.setText("");
        medicineList =
            App.getContext().db.getCurrentMedicineStock(App.getContext().userInfo.userId, false)
        showMedicineList()
        binding.mFilter.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                PopMenuDisplay()
            }

            fun PopMenuDisplay() {
                popupmenu = PopupMenu(context, binding.mFilter)
                popupmenu!!.menuInflater.inflate(R.menu.popup, popupmenu!!.menu)
                popupmenu!!.setOnMenuItemClickListener { item: MenuItem ->
                    Toast.makeText(context, item.title, Toast.LENGTH_LONG).show()
                    true
                }
                popupmenu!!.show()
            }
        })
        //        binding.toolbar.llCard.setOnClickListener(view -> {
//            readDataFromView();
//            if (soldMedicineList.size() == 0) {
//                showOneButtonDialog(getResources().getString(R.string.empty_sold_medicine), R.drawable.error, Color.RED, "");
//                return;
//            }
//
//            Intent intent = new Intent(getActivity(), MedicineSaleConfirmActivity.class);
//            intent.putExtra(ActivityDataKey.DATA, soldMedicineList);
//            intent.putExtra(ActivityDataKey.PARENT_NAME, this.getClass().getName());
////            intent.putExtra(ActivityDataKey.ACTIVITY_PATH, activityPath);
//            startActivity(intent);
//        });

//        binding.toolbar.actionInvoiceList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopMenuDisplay();
//            }
//
//            public void PopMenuDisplay() {
//
//                popupmenu = new PopupMenu(getActivity(), binding.toolbar.actionInvoiceList);
//
//                popupmenu.getMenuInflater().inflate(R.menu.invoice_popup, popupmenu.getMenu());
//
//                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        Intent dataActivity = new Intent(getActivity(), InvoiceListActivity.class);
//                        startActivity(dataActivity);
//
//                        return true;
//                    }
//                });
//
//                popupmenu.show();
//
//            }
//        });
//        binding.toolbar.productPageNotification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent notificationIntent = new Intent(getActivity(), NotificationActivity.class);
//                startActivity(notificationIntent);
//                Toast.makeText(getActivity(), "No notification", Toast.LENGTH_LONG).show();
//            }
//        });
        return binding.root
    }

    private fun readDataFromView() {
        val timeMilisecond = Calendar.getInstance().timeInMillis
        soldMedicineList = ArrayList()
        for (i in 0 until binding.llMedicineRowContainer.childCount) {
            val view = binding.llMedicineRowContainer.getChildAt(i)
            val etQty = view.findViewById<EditText>(R.id.et_quantity)
            val qtyStr = etQty.text.toString().trim { it <= ' ' }
            val plus = view.findViewById<CardView>(R.id.increase)
            if (Utility.parseLong(qtyStr) <= 0) continue
            medicineList!![i].soldQuantity =
                Utility.parseInt(qtyStr).toString() + ""
            medicineList!![i].updateTime = timeMilisecond
            soldMedicineList!!.add(medicineList!![i])
        }
    }

    private fun showMedicineList() {
        binding.llMedicineRowContainer.removeAllViews()
        if (medicineList != null) {
            var i = 0
            for (medicineInfo in medicineList!!) {
                val view = View.inflate(activity, R.layout.products_items_list, null)
                binding.llMedicineRowContainer.addView(view)
                var tv = view.findViewById<TextView>(R.id.tv_name)
                tv.text = medicineInfo.toString()
                tv = view.findViewById(R.id.tv_type)
                tv.text = medicineInfo.medicineType
                tv = view.findViewById(R.id.tv_info_left)
                tv.text = TextUtility.format("%.2f", medicineInfo.unitSalesPrice)
                tv = view.findViewById(R.id.tv_info_right)
                tv.text = TextUtility.format("%d", medicineInfo.currentStockQuantity)
                val etQty = view.findViewById<TextView>(R.id.et_quantity)
                val plus = view.findViewById<CardView>(R.id.increase)
                val minus = view.findViewById<CardView>(R.id.decrease)
                plus.setOnClickListener {
                    var m = 0.toString()
                    val qty = etQty.text.toString().trim { it <= ' ' }.toInt()
                    m = (qty + 1).toString()
                    etQty.text = m
                }
                minus.setOnClickListener(View.OnClickListener {
                    var m = 0.toString()
                    val qty = etQty.text.toString().trim { it <= ' ' }.toInt()
                    m = (qty - 1).toString()
                    etQty.text = m
                    if (qty <= 0) {
                        showOneButtonDialog(
                            resources.getString(R.string.empty_medicine),
                            R.drawable.error,
                            Color.RED,
                            ""
                        )
                        etQty.text = "0"
                        return@OnClickListener
                    }
                })
                etQty.tag = i
                i++


//
                etQty.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(
                        arg0: CharSequence,
                        arg1: Int,
                        arg2: Int,
                        arg3: Int
                    ) {
                        // TODO Auto-generated method stub
                    }

                    override fun beforeTextChanged(
                        arg0: CharSequence, arg1: Int, arg2: Int,
                        arg3: Int
                    ) {
                        // TODO Auto-generated method stub
                    }

                    override fun afterTextChanged(arg0: Editable) {
                        try {
                            val sellQty = Utility.parseInt(arg0.toString().trim { it <= ' ' })
                            val rowIndex = etQty.tag as Int
                            val currentStock = medicineList!![rowIndex].currentStockQuantity
                            val configArry = JSONArray(
                                AppPreference.getString(
                                    activity,
                                    KEY.FCM_CONFIGURATION,
                                    "[]"
                                )
                            )
                            val `val` = JSONParser.getFcmConfigValue(
                                configArry,
                                "INVENTORY",
                                "allow.0.inventory.product.consumption"
                            )
                            Log.e("valData", `val`)
                            if (!`val`.equals("YES", ignoreCase = true)) {
                                if (sellQty > currentStock) {
                                    arg0.delete(arg0.length - 1, arg0.length)
                                    return
                                }
                            }
                            val totalPriceString =
                                TextUtility.format("%s %.2f", "", medicineTotalPrice)
                            // binding.toolbar.tvTotalMedicinePrice.setText(totalPriceString);
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            }
        }
    }

    private fun showOneButtonDialog(msg: String, imageId: Int, messageColor: Int, type: String) {
        val buttonMap = HashMap<Int, Any>()
        buttonMap[1] = R.string.btn_close
        val exitDialog =
            DialogView(activity, R.string.dialog_title, msg, messageColor, imageId, buttonMap)
        exitDialog.setOnDialogButtonClick(object : OnDialogButtonClick {
            override fun onDialogButtonClick(view: View?) {
                if (type == "DATA_SEND") {
                    activity!!.finish()
                }
            }
        })
        exitDialog.show()
    }

    private val medicineTotalPrice: Double
        private get() {
            var totalPrice = 0.0
            for (i in 0 until binding.llMedicineRowContainer.childCount) {
                val view = binding.llMedicineRowContainer.getChildAt(i)
                val etQty = view.findViewById<EditText>(R.id.et_quantity)
                val qtyStr = etQty.text.toString().trim { it <= ' ' }
                if (Utility.parseLong(qtyStr) <= 0) continue
                totalPrice += medicineList!![i].unitSalesPrice * Utility.parseLong(qtyStr)
            }
            return totalPrice
        }

    companion object {
        fun newInstance(): ProductSaleFragment {
            return ProductSaleFragment()
        }
    }
}