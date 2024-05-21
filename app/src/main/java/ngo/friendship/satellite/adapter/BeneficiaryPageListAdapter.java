package ngo.friendship.satellite.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.databinding.CommonServicesItemRowBinding;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.ui.QuestionnaireListActivity;
import ngo.friendship.satellite.ui.beneficiary.profile.BeneficiaryProfileActivity;
import ngo.friendship.satellite.utility.FileOperaion;
import ngo.friendship.satellite.views.AppToast;

/**
 * Created by Yeasin Ali on 5/18/2023.
 * friendship.ngo
 * yeasinali@friendship.ngo
 */
public class BeneficiaryPageListAdapter extends PaginatedAdapterData<Beneficiary, BeneficiaryPageListAdapter.ViewHolder> implements Filterable {
    Context ctx;
    private ArrayList<Beneficiary> mFilteredDataSet = new ArrayList<>(); // Dataset after applying the filter
    private ArrayList<Beneficiary> allBenefList = new ArrayList<>(); // Dataset after applying the filter
    private String mFilterText = ""; // Text used for filtering
    private OnSelectBenfClickListener onSelectBenfClickListener;

    public BeneficiaryPageListAdapter(Context mainActivity) {
        this.ctx = mainActivity;
    }
    public interface OnSelectBenfClickListener {
        void onSelectBenfClick(Beneficiary item);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CommonServicesItemRowBinding binding = CommonServicesItemRowBinding.inflate(inflater, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.render(getItem(position));
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Beneficiary> filteredList = new ArrayList<>();

                // Perform filtering logic here using the constraint (filter text)
                for (Beneficiary beneficiary : allBenefList) {
                    // Customize the filtering criteria based on your requirements
                    String fcmName="";
                    try {
                        fcmName =  beneficiary.getFcmName().toLowerCase();
                    }catch (Exception e){e.printStackTrace();}
                    if (
                            beneficiary.getBenefName().toLowerCase().contains(constraint.toString().toLowerCase())
//                                    || beneficiary.getBenefCode().toLowerCase().contains(constraint.toString().toLowerCase())
                                    || beneficiary.getBenefCode().toLowerCase().contains(constraint.toString().toLowerCase())
//                            || beneficiary.getAddress().toLowerCase().contains(constraint.toString().toLowerCase())
//                            || beneficiary.getLocationName().toLowerCase().contains(constraint.toString().toLowerCase())
//                            || fcmName.contains(constraint.toString().toLowerCase())
//                            || beneficiary.getGender().toLowerCase().contains(constraint.toString().toLowerCase())
//                            )
                    ) {
                        filteredList.add(beneficiary);
                        Log.d("Filter", "performFiltering: ............total filtered list in very begaining"+filteredList.size());
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                Log.d("Filter", "performFiltering: ......total filter data list"+filteredList.size());
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                mFilteredDataSet = (ArrayList<Beneficiary>) results.values;
                Log.d("Filter", "publishResults: ........total filter data list size "+mFilteredDataSet.size());

                submitItemAll(mFilteredDataSet);
            }
        };
    }

    public void setItems(ArrayList<Beneficiary> items) {
        allBenefList = items;
    }

    public void filter(String text) {
        mFilterText = text;
        getFilter().filter(text);
    }


    public void setOnSelectBenfClickListener(OnSelectBenfClickListener listener) {
        this.onSelectBenfClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CommonServicesItemRowBinding dataBinding;

        public ViewHolder(@NonNull CommonServicesItemRowBinding binding) {
            super(binding.getRoot());
            this.dataBinding = binding;
        }

        public void render(Beneficiary model) {
            dataBinding.tvServiceType.setVisibility(View.GONE);
            dataBinding.tvBenefNameOthers.setVisibility(View.VISIBLE);
            dataBinding.tvFcmInfo.setVisibility(View.VISIBLE);
            dataBinding.tvAgeGender.setVisibility(View.GONE);
            dataBinding.viewLine.setVisibility(View.VISIBLE);
            dataBinding.tvBenefName.setText("" + model.getBenefName());
            dataBinding.tvBeneficiaryCode.setText("" + model.getBenefCode());
            dataBinding.tvGuardian.setVisibility(View.VISIBLE);
            dataBinding.tvGuardian.setText("Guardian: " + model.getGuardianName()+" ("+model.getRelationToGurdian()+")");

            if (model.getState()==0){
                dataBinding.llContent.setBackgroundColor(ctx.getResources().getColor(R.color.red_trans));
            }else{
                dataBinding.llContent.setBackgroundColor(ctx.getResources().getColor(R.color.white));
            }
            if (model.getUserId() == App.getContext().getUserInfo().getUserId()) {
                dataBinding.tvFcmInfo.setText("from " + model.getAddress() + "");
            }
            if (model.getUserId() != App.getContext().getUserInfo().getUserId()) {
                dataBinding.tvFcmInfo.setText("FCM: " + model.getFcmName() + " from " + model.getLocationName() + "");
            }


            if (FileOperaion.isExist(model.getBenefImagePath())) {
                try {
                    dataBinding.ivBeneficiary.setImageBitmap(FileOperaion.decodeImageFile(model.getBenefImagePath(), 60));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                try {
                    if (model.getGender().equalsIgnoreCase("F")) {
                        dataBinding.ivBeneficiary.setImageResource(R.drawable.ic_default_woman);
                    } else if (model.getGender().equalsIgnoreCase("M")) {
                        dataBinding.ivBeneficiary.setImageResource(R.drawable.ic_default_man);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            try {
                if (model.getGender().length()>0) {
                    if (model.getGender().equalsIgnoreCase("F")) {
                    dataBinding.tvBenefNameOthers.setText(" Female, " + model.getAge() + "");
                    }else if (model.getGender().equalsIgnoreCase("M")) {
                        dataBinding.tvBenefNameOthers.setText(" Male, " + model.getAge() + "");
                    }else   if (model.getGender().equalsIgnoreCase("O")) {
                        dataBinding.tvBenefNameOthers.setText(" Others, " + model.getAge() + "");
                    }
                } else  {
                    dataBinding.tvBenefNameOthers.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            dataBinding.clServiceBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.getState()==0){
                        try {
                            AppToast.showToastWarnaing(ctx,ctx.getResources().getString(R.string.fcm_info_to_deactive_benef));
                        }catch (Exception e){e.printStackTrace();}

                        //Toast.makeText(ctx,ctx.getResources().getString(R.string.fcm_info_to_deactive_benef) , Toast.LENGTH_SHORT).show();
                    }
                    if (onSelectBenfClickListener != null) {
                        onSelectBenfClickListener.onSelectBenfClick(model);
                    }else{
                        String profileString = new Gson().toJson(model).toString();
                        Intent activity = new Intent(ctx, QuestionnaireListActivity.class);
                        activity.putExtra("BENEF_PROFILE", profileString);
                        ctx.startActivity(activity);
                    }

                }
            });


            dataBinding.btnProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String profileString = new Gson().toJson(model).toString();
                    Intent fcmProfile = new Intent(ctx, BeneficiaryProfileActivity.class);
                    fcmProfile.putExtra("BENEF_PROFILE", profileString);
                    ctx.startActivity(fcmProfile);
                }
            });

        }
    }
}