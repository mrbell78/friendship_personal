package ngo.friendship.satellite.ui.stock_manage;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.databinding.ActivityProductRiseBinding;
import ngo.friendship.satellite.databinding.ProductsItemsListBinding;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.ui.notification.NotificationActivity;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.DialogView;

public class StockRequisitionRiseActivity extends BaseActivity implements View.OnClickListener {
    ActivityProductRiseBinding binding;
    ArrayList<MedicineInfo> medicineList=new ArrayList<>();
    ArrayList<MedicineInfo> medicineFilterList = new ArrayList<>();

    String activityPath;
    PopupMenu popupmenu;
    int minteger = 0;
   String parentName = "";
   String activityBase = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductRiseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle b = getIntent().getExtras();
        parentName = b.getString(ActivityDataKey.PARENT_NAME);


        binding.toolbar.tvProductTitle.setText(getResources().getString(R.string.create_requisition));

        binding.toolbar.actionCart.setVisibility(View.VISIBLE);
        binding.toolbar.llCard.setVisibility(View.VISIBLE);

        binding.layoutSync.llPenddingSync.setVisibility(View.GONE);
        binding.layoutSync.viewLine.setVisibility(View.GONE);
        binding.layoutSync.llAllSync.setVisibility(View.GONE);
        binding.layoutSync.viewLine2.setVisibility(View.GONE);

        binding.layoutSync.syncLayoutOpen.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.VISIBLE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.GONE);
        });
        binding.layoutSync.ivCloseSync.setOnClickListener(v -> {
            binding.layoutSync.cvSyncLayout.setVisibility(View.GONE);
            binding.layoutSync.syncLayoutOpen.setVisibility(View.VISIBLE);
        });

        binding.toolbar.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.layoutSync.llGetData.setOnClickListener(v -> {
            if (SystemUtility.isConnectedToInternet(this)) {
                //  showDataRetrieveConfirmationPrompt(getResources().getString(R.string.retrieve_data_confirmation));
            } else {
                SystemUtility.openInternetSettingsActivity(this);
            }

        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                 // adapter.getFilter().filter(s);

                medicineFilterList.clear();
                for (MedicineInfo item : medicineList) {
                    if (item.toString().toLowerCase().contains(s.toLowerCase())) {
                        medicineFilterList.add(item);
                    }
                }
                showMedicineList(medicineFilterList);

                return true;
            }
        });
        binding.toolbar.llCard.setOnClickListener(view -> {

            //readDataFromView();
            if (getSoldMedicinesList().size() == 0) {
                showOneButtonDialog(getResources().getString(R.string.empty_sold_medicine), R.drawable.error, Color.RED, "");
                return;
            }

            Intent intent = new Intent(this, StockRequisitionConfirmActivity.class);
            intent.putExtra(ActivityDataKey.DATA, getSoldMedicinesList());
            intent.putExtra(ActivityDataKey.PARENT_NAME, this.getClass().getName());
            intent.putExtra(ActivityDataKey.ACTIVITY_PATH, activityPath);
            intent.putExtra(ActivityDataKey.ACTIVITY, Constants.PRODUCT_STOCK_REQUISITION);
            startActivity(intent);

        });
        binding.toolbar.ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopMenuDisplay();
            }

            public void PopMenuDisplay() {

                popupmenu = new PopupMenu(StockRequisitionRiseActivity.this, binding.toolbar.ivSetting);

                popupmenu.getMenuInflater().inflate(R.menu.invoice_popup, popupmenu.getMenu());

                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {

//                        Intent dataActivity = new Intent(ProductRiseActivity.this, InvoiceListActivity.class);
//                        startActivity(dataActivity); Intent dataActivity = new Intent(ProductRiseActivity.this, InvoiceListActivity.class);
//                        startActivity(dataActivity);

                        return true;
                    }
                });

                popupmenu.show();

            }
        });

        binding.toolbar.productPageNotification.setOnClickListener(view -> {
            Intent notificationIntent = new Intent(StockRequisitionRiseActivity.this, NotificationActivity.class);
            startActivity(notificationIntent);
            Toast.makeText(StockRequisitionRiseActivity.this, "No notification", Toast.LENGTH_LONG).show();
        });
        retrieveCurrentStock();


        binding.mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopMenuDisplay();
            }

            public void PopMenuDisplay() {

                popupmenu = new PopupMenu(StockRequisitionRiseActivity.this, binding.mFilter);
                popupmenu.getMenuInflater().inflate(R.menu.popup, popupmenu.getMenu());
                popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(StockRequisitionRiseActivity.this, item.getTitle(), Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
                popupmenu.show();
            }
        });
    }

    private void retrieveCurrentStock() {
        MHealthTask tsk = new MHealthTask(this, Task.RETRIEVE_CURRENT_STOCK_MEDICINE_LIST, R.string.retrieving_data, R.string.please_wait);
        tsk.setCompleteListener(msg -> {
            if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                App.showMessageDisplayDialog(StockRequisitionRiseActivity.this, getResources().getString(R.string.retrive_error), R.drawable.error, Color.RED);
            } else {
                medicineList = (ArrayList<MedicineInfo>) msg.getData().getSerializable(TaskKey.DATA0);
                for (MedicineInfo medicineInfo:medicineList){
                    medicineInfo.setSelected(false);
                }
                showMedicineList(medicineList);
            }
        });
        tsk.execute();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_product_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.nav_product_rise) {
            // finish the activity

//            startActivity(new Intent(this, RiseListActivity.class));
            return true;
        } else if (id == R.id.nav_product_adjust) {
            startActivity(new Intent(this, AdjustmentRequestActivity.class));
            return true;
        } else if (id == R.id.all_invoice) {
           // startActivity(new Intent(this, InvoiceListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMedicineList(List<MedicineInfo> medicineLists) {
        final TextView tvlabel= findViewById(R.id.tvCartLabel);
        tvlabel.setVisibility(View.GONE);

        binding.llMedicineRowContainer.removeAllViews();
        if (medicineLists != null) {
            int i = 0;
            for (MedicineInfo medicineInfo : medicineLists) {
                ProductsItemsListBinding productsItemsListBinding = ProductsItemsListBinding.inflate(getLayoutInflater());

                binding.llMedicineRowContainer.addView(productsItemsListBinding.getRoot());
                productsItemsListBinding.tvName.setText(medicineInfo.toString());
                productsItemsListBinding.tvType.setText(medicineInfo.getMedicineType());
                productsItemsListBinding.tvInfoLeft.setText(TextUtility.format("%.2f", medicineInfo.getUnitSalesPrice()));
                productsItemsListBinding.tvInfoRight.setText(TextUtility.format("%d", medicineInfo.getCurrentStockQuantity()));



//                TextView etQty = view.findViewById(R.id.et_quantity);
//                CardView plus = view.findViewById(R.id.increase);
//                CardView minus = view.findViewById(R.id.decrease);
//                CardView plus2 = findViewById(R.id.increaseBlue);

                productsItemsListBinding.increase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int  m = 0;
                        String etQty =productsItemsListBinding.etQuantity.getText().toString().trim();
                       // Log.d(TAG, "onClick: ...............the edt value is .."+etQty);
                        try{
                            if(etQty!=null && etQty!=""){
                                int qty =  Utility.parseInt(etQty);
                                Log.d(TAG, "onClick: ............."+qty);
                                m = qty+1;
                            }
                            Log.d(TAG, "onClick: ...qty is "+m);
                            productsItemsListBinding.etQuantity.setText(""+m);
                        } catch(NumberFormatException ex){ // handle your exception

                            ex.printStackTrace();
                            Log.d(TAG, "onClick: "+ex.toString());
                        }

                        //getMedicineTotalQuantity();
                    }
                });
                productsItemsListBinding.decrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int m = 0;
                        String etQty =productsItemsListBinding.etQuantity.getText().toString().trim();
                        int qty = Integer.parseInt( etQty);
                        if(qty>0){
                            m = qty-1;
                            productsItemsListBinding.etQuantity.setText(""+m);
                        }
                        if (qty <= 0) {
                            showOneButtonDialog(getResources().getString(R.string.empty_medicine), R.drawable.error, Color.RED, "");
                            return;
                        }
                       // getMedicineTotalQuantity();
                    }
                });
                try {
                    MedicineInfo med = medicineIsSelected(medicineInfo.getMedId());
                    if (med!=null){
                        try {
                            productsItemsListBinding.etQuantity.setText(""+med.getRequiredQuantity());
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                productsItemsListBinding.etQuantity.setTag(i);
                i++;
                productsItemsListBinding.etQuantity.addTextChangedListener(new TextWatcher() {
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

                            String data = String.valueOf(arg0);
                            int sellQty = Utility.parseInt(arg0.toString().trim());
                            int rowIndex = (Integer)  productsItemsListBinding.etQuantity.getTag();
////                            Log.d(TAG, "afterTextChanged: ...........the row index is ..."+rowIndex);
//                            int currentStock = medicineLists.get(rowIndex).getCurrentStockQuantity();
//                            JSONArray configArry = new JSONArray(AppPreference.getString(StockRequisitionRiseActivity.this, KEY.FCM_CONFIGURATION, "[]"));
//                            String val = JSONParser.getFcmConfigValue(configArry, "INVENTORY", "allow.0.inventory.product.consumption");
//                            Log.e("valData", val);
////
////
//                            if (!val.equalsIgnoreCase("YES")) {
//                                if (sellQty > currentStock) {
//                                    arg0.delete(arg0.length() - 1, arg0.length());
//                                    return;
//                                }
//                            }
                            MedicineInfo med  =   medicineLists.get(rowIndex);
                            setSelectedMedicine(med.getMedId(),""+sellQty);
                            getMedicineTotalQuantity();


                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }


                });


            }
        }

    }

    private double getMedicineTotalPrice() {
        double totalPrice = 0;
        for (int i = 0; i < binding.llMedicineRowContainer.getChildCount(); i++) {

            View view = binding.llMedicineRowContainer.getChildAt(i);
            EditText etQty = view.findViewById(R.id.et_quantity);
            String qtyStr = etQty.getText().toString().trim();
            if (Utility.parseLong(qtyStr) <= 0)
                continue;

            totalPrice += medicineList.get(i).getUnitSalesPrice() * Utility.parseLong(qtyStr);
        }
        return totalPrice;

    }

    private int getMedicineTotalQuantity() {
        int totalQuantity = 0;


//        for (int i = 0; i < binding.llMedicineRowContainer.getChildCount(); i++) {
//
//            View view = binding.llMedicineRowContainer.getChildAt(i);
//            EditText etQty = view.findViewById(R.id.et_quantity);
//            String qtyStr = etQty.getText().toString().trim();
//            if (Utility.parseLong(qtyStr) <= 0){
//                continue;
//            }else{
//                MedicineInfo med  =   medicineList.get(i);
//                setSelectedMedicine(med.getMedId(),qtyStr);
//
//            }
//            //totalQuantity += Utility.parseLong(qtyStr);
//
//
//        }

        for (MedicineInfo medicineInfo:medicineList){

            if (medicineInfo.isSelected()){
                totalQuantity +=Integer.parseInt("0"+medicineInfo.getRequiredQuantity());
            }

        }

//        String totalPriceString = TextUtility.format("%s", "",totalQuantity);
        binding.toolbar.tvTotalMedicinePrice.setText("Qty: "+totalQuantity);
        //readDataFromView();
try {
    if (totalQuantity>0){
        binding.toolbar.ivBackArrowAni.setVisibility(View.VISIBLE);
        binding.toolbar.ivBackArrow.setVisibility(View.GONE);

    }else{
        binding.toolbar.ivBackArrowAni.setVisibility(View.GONE);
        binding.toolbar.ivBackArrow.setVisibility(View.VISIBLE);
    }
}catch (Exception e){e.printStackTrace();}


        return totalQuantity;

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
                    StockRequisitionRiseActivity.this.finish();
                }

            }
        });
        exitDialog.show();
    }

    private void readDataFromView() {
        long timeMilisecond = Calendar.getInstance().getTimeInMillis();
       // soldMedicineList = new ArrayList<MedicineInfo>();
        for (int i = 0; i < binding.llMedicineRowContainer.getChildCount(); i++) {

            View view = binding.llMedicineRowContainer.getChildAt(i);
            EditText etQty = view.findViewById(R.id.et_quantity);
            String qtyStr = etQty.getText().toString().trim();

            CardView plus = view.findViewById(R.id.increase);

            if (Utility.parseLong(qtyStr) <= 0)
                continue;

            medicineList.get(i).setSoldQuantity(Utility.parseInt(qtyStr) + "");
            medicineList.get(i).setRequiredQuantity(Utility.parseInt(qtyStr) + "");
            medicineList.get(i).setUpdateTime(timeMilisecond);
           // soldMedicineList.add(medicineList.get(i));


        }
        Log.e("Medicine","dd");
    }
    public void setSelectedMedicine(long medId,String qtyStr){
        if (qtyStr.length()>0){
        if (medicineList.size()>0){
            for (MedicineInfo medicineInfo:medicineList){
                if (medicineInfo.getMedId()==medId){
                    long timeMilisecond = Calendar.getInstance().getTimeInMillis();
                    medicineInfo.setSelected(true);
                    medicineInfo.setSoldQuantity(Utility.parseInt(qtyStr) + "");
                    medicineInfo.setRequiredQuantity(Utility.parseInt(qtyStr) + "");
                    medicineInfo.setUpdateTime(timeMilisecond);
                }
            }
        }
        }
        Log.e("sdfsd","sdsfsdf");

    }

    public void setUnSelectedMedicine(long medId){
        if (medicineList.size()>0){
            for (MedicineInfo medicineInfo:medicineList){
                if (medicineInfo.getMedId()==medId){
                    medicineInfo.setSelected(false);
                }
            }
        }

    }
    public MedicineInfo medicineIsSelected(long medId){
        if (medicineList.size()>0){
            for (MedicineInfo medicineInfo:medicineList){
                if (medicineInfo.getMedId()==medId){
                    if (medicineInfo.isSelected()){
                        return medicineInfo;
                    }
                }

            }
        }

        return null;
    }
    public   ArrayList<MedicineInfo> getSoldMedicinesList(){

        ArrayList<MedicineInfo>soldMedicinesList = new ArrayList<>();
        if (medicineList.size()>0){
            for (MedicineInfo medicineInfo:medicineList){
                if (medicineInfo.isSelected()){
                    soldMedicinesList.add(medicineInfo);
                }
            }
        }
        return soldMedicinesList;
    }


}