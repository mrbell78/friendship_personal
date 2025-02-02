package ngo.friendship.satellite.ui.stock_manage;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

import dagger.hilt.android.AndroidEntryPoint;
import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.databinding.ActivityProductAdjustmentBinding;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AdjustmentMedicineInfo;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.ui.notification.NotificationActivity;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.DialogView;

@AndroidEntryPoint
public class AdjustmentRequestActivity extends BaseActivity implements View.OnClickListener {
    ActivityProductAdjustmentBinding binding;
    ArrayList<MedicineInfo> medicineList;
    private ArrayList<AdjustmentMedicineInfo> toBeAdjustedMedicinList;
    Dialog dialog;
    String activityPath;

    int minteger = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductAdjustmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.layout2.setVisibility(View.GONE);
        dialog = new Dialog(this);
       // binding.toolbar.btnBack.setOnC
        binding.toolbar.actionCart.setVisibility(View.VISIBLE);
        binding.toolbar.llCard.setVisibility(View.VISIBLE);
        binding.toolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.toolbar.tvProductTitle.setText(getResources().getString(R.string.my_products));
        binding.toolbar.llCard.setOnClickListener(view -> {
            readDataFromView();
            if (toBeAdjustedMedicinList.size() == 0) {
                showOneButtonDialog(getResources().getString(R.string.empty_adjust_medicine), R.drawable.error, Color.RED, "");
                return;
            }
            Intent intent = new Intent(this, AdjustmentConfirmActivity.class);
            intent.putExtra(ActivityDataKey.DATA, toBeAdjustedMedicinList);
            intent.putExtra(ActivityDataKey.PARENT_NAME, this.getClass().getName());
            intent.putExtra(ActivityDataKey.ACTIVITY_PATH, activityPath);
            startActivity(intent);
        });
//        binding.toolbar.ivSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopMenuDisplay();
//            }
//
//            public void PopMenuDisplay() {
//
//                popupmenu = new PopupMenu(ProductAdjustmentRequestActivity.this, actionInvoiceList);
//
//                popupmenu.getMenuInflater().inflate(R.menu.invoice_popup, popupmenu.getMenu());
//
//                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        Intent dataActivity = new Intent(ProductAdjustmentRequestActivity.this, InvoiceListActivity.class);
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

        binding.toolbar.productPageNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent notificationIntent = new Intent(AdjustmentRequestActivity.this, NotificationActivity.class);
                startActivity(notificationIntent);
                Toast.makeText(AdjustmentRequestActivity.this, "No notification", Toast.LENGTH_LONG).show();
            }
        });
        medicineList = App.getContext().getDB().getCurrentMedicineStock(App.getContext().getUserInfo().getUserId(), false);
        showMedicineList();

//        setUpRecyclerView();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
              //  adapter.getFilter().filter(newText);
                return false;
            }
        });

//        binding.mFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopMenuDisplay();
//            }
//
//            public void PopMenuDisplay() {
//
//                popupmenu = new PopupMenu(ProductAdjustmentRequestActivity.this, binding.mFilter);
//
//                popupmenu.getMenuInflater().inflate(R.menu.popup, popupmenu.getMenu());
//
//                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//
//                    public boolean onMenuItemClick(MenuItem item) {
//
//                        Toast.makeText(ProductAdjustmentRequestActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
//
//                        return true;
//                    }
//                });
//
//                popupmenu.show();
//
//            }
//        });


        //..................................................................


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_product_menu, menu);
        return true;
    }

//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == android.R.id.home) {
//            onBackPressed();
//            return true;
//        } else if (id == R.id.nav_product_rise) {
//            // finish the activity
//
//            startActivity(new Intent(this, RiseListActivity.class));
//            return true;
//        } else if (id == R.id.nav_product_adjust) {
//            startActivity(new Intent(this, ProductAdjustmentActivity.class));
//            return true;
//        } else if (id == R.id.all_invoice) {
//            startActivity(new Intent(this, InvoiceListActivity.class));
//            return true;
//        }
//
//
//        return super.onOptionsItemSelected(item);
//    }

    private void showMedicineList() {
        binding.toolbar.tvTotalMedicinePrice.setText("");

        if (medicineList.size() > 0) {
            binding.dataNotFound.serviceNotFound.setVisibility(View.GONE);
        } else {
            binding.dataNotFound.serviceNotFound.setVisibility(View.VISIBLE);
        }

        binding.llMedicineRowContainer.removeAllViews();
        if (medicineList != null) {
            int i = 0;
            for (MedicineInfo medicineInfo : medicineList) {
                View view = View.inflate(this, R.layout.adjustment_products_items_list, null);
                binding.llMedicineRowContainer.addView(view);

                TextView tv = view.findViewById(R.id.tv_name);
                tv.setText(medicineInfo.toString());

                tv = view.findViewById(R.id.tv_type);
                tv.setText(medicineInfo.getMedicineType());

                tv = view.findViewById(R.id.tv_info_left);
                tv.setText(TextUtility.format("%.2f", medicineInfo.getUnitSalesPrice()));

                tv = view.findViewById(R.id.tv_info_right);
                tv.setText(TextUtility.format("%d", medicineInfo.getCurrentStockQuantity()));


                TextView etQty = view.findViewById(R.id.et_quantity);
                CardView plus = view.findViewById(R.id.increase);
                CardView minus = view.findViewById(R.id.decrease);
                CardView plus2 = findViewById(R.id.increaseBlue);
                TextView tvCartLabel = findViewById(R.id.tvCartLabel);
                tvCartLabel.setText("Qty");
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String m = String.valueOf(0);
                        int qty = Integer.parseInt(etQty.getText().toString().trim());

                        m = String.valueOf(qty + 1);
                        etQty.setText(m);

                    }
                });
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String m = String.valueOf(0);
                        int qty = Integer.parseInt(etQty.getText().toString().trim());
                        m = String.valueOf(qty - 1);
                        etQty.setText(m);
                        if (qty <= 0) {
                            showOneButtonDialog(getResources().getString(R.string.empty_medicine), R.drawable.error, Color.RED, "");
                            return;
                        }

                    }
                });
                etQty.setTag(i);
                i++;


                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                            int p = Integer.parseInt(etQty.getText().toString());
                        minteger = minteger + 1;
                        etQty.setText(minteger);
//
                        Toast.makeText(AdjustmentRequestActivity.this, "p", Toast.LENGTH_SHORT).show();
//                        increaseNumber();


                    }
                });


                etQty.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                        // TODO Auto-generated method stub


                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        try {


                            int sellQty = Utility.parseInt(arg0.toString().trim());
                            int rowIndex = (Integer) etQty.getTag();
                            int currentStock = medicineList.get(rowIndex).getCurrentStockQuantity();
                            JSONArray configArry = new JSONArray(AppPreference.getString(AdjustmentRequestActivity.this, KEY.FCM_CONFIGURATION, "[]"));
                            String val = JSONParser.getFcmConfigValue(configArry, "INVENTORY", "allow.0.inventory.product.consumption");
                            Log.e("valData", val);


                            if (!val.equalsIgnoreCase("YES")) {
                                if (sellQty > currentStock) {
                                    arg0.delete(arg0.length() - 1, arg0.length());
                                    return;
                                }
                            }

                            binding.toolbar.tvTotalMedicinePrice.setText("Qty:"+getMedicineTotalPrice());

                         //   binding.toolbar.tvTotalMedicinePrice.setText(""+getMedicineTotalPrice());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }


                });


            }
        }
    }

    private int getMedicineTotalQty() {
        int totalPrice = 0;
        for (int i = 0; i < binding.llMedicineRowContainer.getChildCount(); i++) {

            View view = binding.llMedicineRowContainer.getChildAt(i);
            EditText etQty = view.findViewById(R.id.et_quantity);
            String qtyStr = etQty.getText().toString().trim();
            if (Utility.parseLong(qtyStr) <= 0)
                continue;
            totalPrice +=Utility.parseLong(qtyStr);
            //totalPrice += medicineList.get(i).getUnitSalesPrice() * Utility.parseLong(qtyStr);
        }
        return totalPrice;

    }
    private int getMedicineTotalPrice() {
        int totalPrice = 0;
        for (int i = 0; i < binding.llMedicineRowContainer.getChildCount(); i++) {

            View view = binding.llMedicineRowContainer.getChildAt(i);
            EditText etQty = view.findViewById(R.id.et_quantity);
            String qtyStr = etQty.getText().toString().trim();
            if (Utility.parseLong(qtyStr) <= 0)
                continue;
            totalPrice +=Utility.parseLong(qtyStr);
            //totalPrice += medicineList.get(i).getUnitSalesPrice() * Utility.parseLong(qtyStr);
        }
        if (totalPrice>0){
            binding.toolbar.ivBackArrowAni.setVisibility(View.VISIBLE);
            binding.toolbar.ivBackArrow.setVisibility(View.GONE);

        }else{
            binding.toolbar.ivBackArrowAni.setVisibility(View.GONE);
            binding.toolbar.ivBackArrow.setVisibility(View.VISIBLE);
        }

        return totalPrice;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                this.finish();
                break;

            default:
                break;
        }

    }


    private void showOneButtonDialog(String msg, final int imageId, int messageColor, final String type) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView exitDialog = new DialogView(this, R.string.dialog_title, msg, messageColor, imageId, buttonMap);
        exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {
                if (type.equals("DATA_SEND")) {
                    AdjustmentRequestActivity.this.finish();
                }

            }
        });
        exitDialog.show();
    }

    private void readDataFromView() {
        toBeAdjustedMedicinList = new ArrayList<AdjustmentMedicineInfo>();
        //long timeMilisecond = Calendar.getInstance().getTimeInMillis();
      //  soldMedicineList = new ArrayList<MedicineInfo>();
        for (int i = 0; i < binding.llMedicineRowContainer.getChildCount(); i++) {

            View view = binding.llMedicineRowContainer.getChildAt(i);
            EditText etQty = view.findViewById(R.id.et_quantity);
            String qtyStr = etQty.getText().toString().trim();
//            if (Utility.parseLong(qtyStr) <= 0)
//                continue;
            if (etQty.getText().toString().matches(""))
                continue;

            medicineList.get(i).setSoldQuantity(Utility.parseInt(qtyStr) + "");
            AdjustmentMedicineInfo medInfo = new AdjustmentMedicineInfo();
            medInfo.setMedicineName( medicineList.get(i).toString() );
            medInfo.setMedicineId(medicineList.get(i).getMedId());
            medInfo.setInputQty(Utility.parseInt(qtyStr));
            medInfo.setAdjustQuantity( Utility.parseInt(qtyStr) - medicineList.get(i).getCurrentStockQuantity());
            medInfo.setCurrentStockQty(medicineList.get(i).getCurrentStockQuantity());
            toBeAdjustedMedicinList.add(medInfo);
            Log.d(TAG, "readDataFromView: ...adj list: "+toBeAdjustedMedicinList);

//            medicineList.get(i).setUpdateTime(timeMilisecond);
//            soldMedicineList.add(medicineList.get(i));


        }
    }

}