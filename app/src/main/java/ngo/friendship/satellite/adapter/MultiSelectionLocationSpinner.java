package ngo.friendship.satellite.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.FcmEvent;
import ngo.friendship.satellite.model.LocationEvent;
import ngo.friendship.satellite.model.LocationHandler;
import ngo.friendship.satellite.model.LocationModel;
import ngo.friendship.satellite.model.UserInfo;
import ngo.friendship.satellite.utility.BusProvider;

/**
 * Created by Yeasin Ali on 4/18/2023.
 * friendship.ngo
 * yeasinali@friendship.ngo
 */
public class MultiSelectionLocationSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    ArrayList<LocationModel> items = null;
    boolean[] selection = null;

    LocationHandler myCallback;
    
    
    ArrayAdapter adapter;

    public MultiSelectionLocationSpinner(Context context) {
        super(context);

        adapter = new ArrayAdapter(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }

    public MultiSelectionLocationSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);


        adapter = new ArrayAdapter(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(adapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (selection != null && which < selection.length) {
            selection[which] = isChecked;

            adapter.clear();
            adapter.add(buildSelectedItemString());

        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String[] itemNames = new String[items.size()];
        String[] itemValues = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            itemNames[i] = items.get(i).getLocationName();
            itemValues[i] = items.get(i).getLocationId() + ",";
        }

        builder.setMultiChoiceItems(itemNames, selection, this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
               BusProvider.getBus().postSticky(new LocationEvent(getSelectedItemsString()));
                //myCallback.onCallback(getSelectedItems());
            }
        });
        builder.show();

        return true;
    }

    public void performTaskWithCallback(LocationHandler callback) {
        // Perform some task and obtain a result
        String result = "Task completed successfully";

        // Call the callback method
        callback.onCallback(getSelectedItems());
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {

//        throw new RuntimeException("setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(ArrayList<LocationModel> items) {
        this.items = items;
        selection = new boolean[this.items.size()];
        adapter.clear();
        adapter.add(getContext().getString(R.string.tap_to_select_location));
        Arrays.fill(selection, false);
    }

    public void setSelection(ArrayList<LocationModel> selection) {
        if (this.selection != null) {
            for (int i = 0; i < this.selection.length; i++) {
                this.selection[i] = false;
            }
        }


        for (LocationModel sel : selection) {
            for (int j = 0; j < items.size(); ++j) {
                if (items.get(j).getLocationId() == sel.getLocationId()) {
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

                    sb.append(items.get(i).getLocationName());
                }
            }
        }

        return sb.toString();
    }

    public ArrayList<LocationModel> getSelectedItems() {
        ArrayList<LocationModel> selectedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); ++i) {
            if (selection[i]) {
                selectedItems.add(items.get(i));
            }
        }
        return selectedItems;
    }

    public String getSelectedItemsString() {
        String selectedItemsString = "";
        StringBuilder selectedItems = new StringBuilder();
        try {
            for (int i = 0; i < items.size(); ++i) {
                if (selection[i]) {
                    selectedItems.append("'");
                    selectedItems.append(items.get(i).getLocationId() + "");
                    selectedItems.append("',");
                }
            }

            selectedItemsString = selectedItems.toString().substring(0, selectedItems.toString().length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedItemsString;
    }
}