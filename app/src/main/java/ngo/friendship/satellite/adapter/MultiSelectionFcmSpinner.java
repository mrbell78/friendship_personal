package ngo.friendship.satellite.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;

import java.util.ArrayList;
import java.util.Arrays;

import hilt_aggregated_deps._dagger_hilt_android_internal_modules_ApplicationContextModule;
import ngo.friendship.satellite.App;
import ngo.friendship.satellite.MainActivity;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.FcmEvent;
import ngo.friendship.satellite.model.MessageEvent;
import ngo.friendship.satellite.model.UserInfo;
import ngo.friendship.satellite.utility.BusProvider;

/**
 * Created by Yeasin Ali on 4/18/2023.
 * friendship.ngo
 * yeasinali@friendship.ngo
 */
public class MultiSelectionFcmSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    ArrayList<UserInfo> items = null;
    boolean[] selection = null;
    ArrayAdapter adapter;
    Context context;
    Activity activity;

    public MultiSelectionFcmSpinner(Context context) {
        super(context);
        this.context=context;
        adapter = new ArrayAdapter(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }

    public MultiSelectionFcmSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        adapter = new ArrayAdapter(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }



    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (selection != null && which < selection.length) {
            if(getSelectedItems().size()<6){
                selection[which] = isChecked;
                adapter.clear();
                adapter.add(buildSelectedItemString());
            }else{
                App.showMessageDisplayDialog(this.activity,"you can't select more than 5 items. Only first 5 selection items will be counted ", R.drawable.error, Color.RED);
                selection[which] = false;


            }
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        try {
            String[] itemNames = new String[items.size()];
            String[] itemValues = new String[items.size()];

            for (int i = 0; i < items.size(); i++) {
                itemNames[i] = items.get(i).getUserName() + " (" + items.get(i).getUserCode() + ")";
                itemValues[i] = items.get(i).getUserCode() + ",";
            }
            int lastIndex = items.size() - 1;
            //itemValues[lastIndex] = itemValues[lastIndex].substring(0, itemValues[lastIndex].length() - 1);
            builder.setMultiChoiceItems(itemNames, selection, this);
        } catch (Exception e) {
        }


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
//                BusProvider.getBus().postSticky(getSelectedItemsString());
//                String[] itemNames = new String[items.size()];
//                Boolean[] itemValues = new Boolean[items.size()];
//                for (int i = 0; i < items.size(); i++) {
//                    itemNames[i] = items.get(i).getUserName() + " (" + items.get(i).getUserCode() + ")";
//
//                    Log.d("TAG", "onClick: canot select more than 5 items at a time "+items.get(i).isSelected());
//                    if(items.get(i).isSelected()){
//                        itemValues[i] = items.get(i).isSelected();
//                        Log.d("TAG", "onClick: canot select more than 5 items at a time "+items.get(i).isSelected());
//                    }
//                }

            }
        });

        builder.show();

        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {

//        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(ArrayList<UserInfo> items) {
        this.items = items;

        selection = new boolean[this.items.size()];
        adapter.clear();
        adapter.add("Select FCM");
        Arrays.fill(selection, false);

    }

    public void setSelection(ArrayList<UserInfo> selection,Activity activity) {
        this.activity = activity;
        if (this.selection != null) {
            for (int i = 0; i < this.selection.length; i++) {
                this.selection[i] = false;
            }
        }


        for (UserInfo sel : selection) {
            for (int j = 0; j < items.size(); ++j) {
                if (items.get(j).getUserCode().equalsIgnoreCase(sel.getUserCode())) {
                    this.selection[j] = true;
                }
            }
        }

        adapter.clear();
        adapter.add(buildSelectedItemString());
    }

    public String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;
        if (this.selection != null) {
            for (int i = 0; i < items.size(); ++i) {
                if (selection[i]) {
                    if (foundOne) {
                        sb.append(", ");
                    }
                    foundOne = true;
                    sb.append(items.get(i).getUserName());
                }
            }
        }

        return sb.toString();
    }

    public ArrayList<UserInfo> getSelectedItems() {
        ArrayList<UserInfo> selectedItems = new ArrayList<>();
        try {
            if (items.size() > 0) {
                for (int i = 0; i < items.size(); ++i) {
                    if (selection[i]) {
                        selectedItems.add(items.get(i));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedItems;
    }

    public String getSelectedItemsString() {
        String selectedItemsData = "";
        StringBuilder selectedItems = new StringBuilder();

        for (int i = 0; i < items.size(); ++i) {
            if (selection[i]) {
                selectedItems.append("'");
                selectedItems.append(items.get(i).getUserCode() + "");
                selectedItems.append("',");
            }
        }
        try {
            selectedItemsData = selectedItems.toString().substring(0, selectedItems.toString().length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedItemsData;
    }
}