package ngo.friendship.satellite.ui.beneficiary_filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;



import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import ngo.friendship.satellite.adapter.BeneficiaryPageListAdapter;
import ngo.friendship.satellite.adapter.PaginatedAdapterData;
import ngo.friendship.satellite.databinding.ActivityCommonBeneficiaryBinding;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.viewmodels.OfflineViewModel;

@AndroidEntryPoint
public class CommonBeneficiaryActivity extends DialogFragment {

    private OnDialogBeneficiaryButtonClickListener beneficiaryButtonClickListener;

    public interface OnDialogBeneficiaryButtonClickListener {
        void onBeneficiaryButtonClick(Beneficiary beneficiary);
    }
    ActivityCommonBeneficiaryBinding binding;
    ArrayList<Beneficiary> beneficiaryList = new ArrayList<>();
    ArrayList<Beneficiary> filterBeneficiaryList = new ArrayList<>();
    OfflineViewModel mainViewModel;

   // EarlierServiceViewModel CommonViewModel;
   CommonServiceViewModel commonViewModel;
    BeneficiaryPageListAdapter adapter;
    AlertDialog showdia;
    int pageSize = 15;
    private int currentPage = 0;
    AlertDialog.Builder builder ;

    public static CommonBeneficiaryActivity newInstance() {
        CommonBeneficiaryActivity fragment = new CommonBeneficiaryActivity();
        return fragment;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityCommonBeneficiaryBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        mainViewModel = new ViewModelProvider(this).get(OfflineViewModel.class);
        commonViewModel = new ViewModelProvider(this).get(CommonServiceViewModel.class);

        mainViewModel.getBeneficiaryList("").observe(this, beneficiariesList -> {
            beneficiaryList = beneficiariesList;

            getbenefListAdapter();
        });
        //this.setFinishOnTouchOutside(false);
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.x =-20;
//        params.height = displayMetrics.heightPixels-200;
//        params.width = displayMetrics.widthPixels-100;
//        params.y =-10;
//        this.getWindow().setAttributes(params);

        binding.imgCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
//                if (builder.)
//                onBackPressed();
            }
        });


        binding.etBenefSearchCommon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                adapter.filter(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//
//               adapter.filter(s.toString());
//
////                filterBeneficiaryList.clear();
////                for (Beneficiary item : beneficiaryList) {
////                    if (item.toString().toLowerCase().contains(s.toLowerCase())) {
////                        filterBeneficiaryList.add(item);
////                    }
////                }
////                adapter.submitItems(filterBeneficiaryList);
//                return true;
//            }
//        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setView(binding.getRoot());

        return builder.create();
    }
    public void setOnDialogButtonClickListener(OnDialogBeneficiaryButtonClickListener listener) {
        this.beneficiaryButtonClickListener = listener;
        dismiss();
    }
    private void getbenefListAdapter() {
        adapter = new BeneficiaryPageListAdapter(getActivity());
//        adapter.setDefaultRecyclerView(getActivity(),R.id.rvCommonBenefList);
        adapter.setStartPage(1); //set first page of data. default value is 1.
        adapter.setPageSize(pageSize); //set page data size. default value is 10.

        adapter.setOnSelectBenfClickListener(item -> {
            if (beneficiaryButtonClickListener != null) {
                beneficiaryButtonClickListener.onBeneficiaryButtonClick(item);
            }
//            AppToast.showToast(getActivity(), item.getBenefName());
//            commonViewModel.selectedItem(item);
        });

        adapter.setOnPaginationListener(new PaginatedAdapterData.OnPaginationListener() {
            @Override
            public void onCurrentPage(int page) {
                //Toast.makeText(MainActivity.this, "Page " + page + " loaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNextPage(int page) {
                getNewItems(page);
            }

            @Override
            public void onFinish() {
                // Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_SHORT).show();
            }
        });

//        onGetDate(beneficiaryList);
        binding.rvCommonBenefList.setAdapter(adapter);
        getNewItems(adapter.getStartPage());

        int[] ATTRS = new int[]{android.R.attr.listDivider};
        TypedArray a = getActivity().obtainStyledAttributes(ATTRS);

//        Drawable divider = a.getDrawable(0);
//        InsetDrawable insetDivider = new InsetDrawable(divider, 0, 16, 0, 4);
//        a.recycle();
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(insetDivider);
//        binding.rvCommonBenefList.addItemDecoration(itemDecoration);
        binding.rvCommonBenefList.setHasFixedSize(true);
        binding.rvCommonBenefList.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvCommonBenefList.setItemAnimator(new DefaultItemAnimator());
        adapter.setItems(beneficiaryList);
    }

    private void getNewItems(final int page) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ArrayList<Beneficiary> users = new ArrayList<>();
                int start = page * pageSize - pageSize;
                int end = page * pageSize;
                for (int i = start; i < end; i++) {
                    if (i < beneficiaryList.size()) {
                        users.add(beneficiaryList.get(i));
                    }
                }
                onGetDate(users);
            }
        }, 500);
    }

    public void onGetDate(ArrayList<Beneficiary> users) {
        adapter.submitItems(users);

    }

    @Override
    public void onResume() {
        super.onResume();
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }
}