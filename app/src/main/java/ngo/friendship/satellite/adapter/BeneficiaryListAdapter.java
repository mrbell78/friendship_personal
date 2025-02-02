package ngo.friendship.satellite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.utility.FileOperaion;
import ngo.friendship.satellite.utility.Utility;


public class BeneficiaryListAdapter extends BaseAdapter implements Filterable {

//    ArrayList<Beneficiary> beneficiaryList;
    private ArrayList<Beneficiary> mBeneficiaryOriginalList; // Original Values
    private ArrayList<Beneficiary> mBeneficiaryFilterList;    // Values to be displayed

    private static LayoutInflater inflater = null;
    Context ctx;

    public BeneficiaryListAdapter(ArrayList<Beneficiary> beneficiaryList, Context context) {
        this.mBeneficiaryOriginalList = beneficiaryList;
        this.mBeneficiaryFilterList = beneficiaryList;
        this.ctx = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mBeneficiaryFilterList == null)
            return 0;

        return this.mBeneficiaryFilterList.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mBeneficiaryFilterList = (ArrayList<Beneficiary>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Beneficiary> filteredArrList = new ArrayList<Beneficiary>();

                if (mBeneficiaryOriginalList == null) {
                    mBeneficiaryOriginalList = new ArrayList<Beneficiary>(mBeneficiaryFilterList); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mBeneficiaryOriginalList.size();
                    results.values = mBeneficiaryOriginalList;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mBeneficiaryOriginalList.size(); i++) {
                        String benefName = mBeneficiaryOriginalList.get(i).getBenefName();
                        String hhNumber = ""+mBeneficiaryOriginalList.get(i).getBenefCode();
                        if (benefName.toLowerCase().trim().contains(constraint.toString())) {
                            filteredArrList.add(mBeneficiaryOriginalList.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = filteredArrList.size();
                    results.values = filteredArrList;
                }
                return results;
            }
        };
        return filter;
    }

    public static class ViewHolder {

        /**
         * The tv beneficiary code.
         */
        public TextView tvBeneficiaryCode;

        /**
         * The tv beneficiary name.
         */
        public TextView tvBeneficiaryName;

        /**
         * The tvbeneficiary age.
         */
        public TextView tvbeneficiaryAge;

        /**
         * The tvbeneficiary gender.
         */
        public TextView tvbeneficiaryGender;

        /**
         * The iv beneficiary.
         */
        public ImageView ivBeneficiary;

    }

    /**
     * **** Depends upon data size called for each row , Create each ListView row ****.
     *
     * @param position    the position
     * @param convertView the convert view
     * @param parent      the parent
     * @return the view
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.beneficiary_row, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.tvBeneficiaryCode = vi.findViewById(R.id.tv_beneficiary_code);
            holder.tvBeneficiaryName = vi.findViewById(R.id.tv_beneficiary_name);


            holder.tvbeneficiaryAge = vi.findViewById(R.id.tv_beneficiary_age);
            holder.tvbeneficiaryGender = vi.findViewById(R.id.tv_beneficiary_gender);
            holder.ivBeneficiary = vi.findViewById(R.id.iv_beneficiary);


            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        boolean isShowBenefNumber = Boolean.parseBoolean(Utility.getFcmConfigurationValue(ctx, "list_show", "show.hh.number.in.benef.list", "false"));
        if (isShowBenefNumber) {
            holder.tvBeneficiaryName.setText(mBeneficiaryFilterList.get(position).getBenefName());
            holder.tvBeneficiaryCode.setText(mBeneficiaryFilterList.get(position).getBenefCode());

         }else{
            holder.tvBeneficiaryCode.setText(mBeneficiaryFilterList.get(position).getBenefName());
            holder.tvBeneficiaryName.setText("");

        }

        if (mBeneficiaryFilterList.get(position).getBenefCode().equals("00000")) {
            holder.tvbeneficiaryAge.setText("");
            holder.tvbeneficiaryGender.setText("");
        } else {
            holder.tvbeneficiaryAge.setText(mBeneficiaryFilterList.get(position).getAge());
            holder.tvbeneficiaryGender.setText(mBeneficiaryFilterList.get(position).getGender());
        }

        if (FileOperaion.isExist(mBeneficiaryFilterList.get(position).getBenefImagePath())) {
            holder.ivBeneficiary.setImageBitmap(FileOperaion.decodeImageFile(mBeneficiaryFilterList.get(position).getBenefImagePath(), 60));
        } else {
            holder.ivBeneficiary.setImageResource(R.drawable.account_card_details);
        }

        return vi;
    }

}
