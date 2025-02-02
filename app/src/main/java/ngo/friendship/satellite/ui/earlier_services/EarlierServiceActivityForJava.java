package ngo.friendship.satellite.ui.earlier_services;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.base.BaseActivity;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.databinding.ActivityEarlierServicesBinding;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnInterviewUploadListener;
import ngo.friendship.satellite.model.Household;
import ngo.friendship.satellite.model.InterviewInfoSyncUnsync;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.views.AppToast;
import ngo.friendship.satellite.views.MdiTextView;

public class EarlierServiceActivityForJava extends BaseActivity implements OnInterviewUploadListener, OnCompleteListener {
    ArrayList<InterviewInfoSyncUnsync> interviewList = new ArrayList<>();
    private ActivityEarlierServicesBinding binding;
    private EarlierServiceViewModel viewModel;
    String pageType  = "";

    ProgressDialog dlog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEarlierServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle(getResources().getString(R.string.earlier_services));
        enableBackButton();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pageType = bundle.getString("PAGE_TYPE").toString();
        }
        viewModel = new ViewModelProvider(this).get(EarlierServiceViewModel.class);
        if ("SERVER_TO_SENT".equals(pageType)) {
            isTabActiveDeactive(
                    binding.llTabSyncd,
                    binding.tvSyncdSyncd,
                    binding.mdiSyncd,
                    binding.llTabPending,
                    binding.tvTabPending,
                    binding.mdiTabPending
            );

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PendingSyncServiceFragment.Companion.newInstance(false))
                    .commit();
        } else {
            isTabActiveDeactive(
                    binding.llTabPending,
                    binding.tvTabPending,
                    binding.mdiTabPending,
                    binding.llTabSyncd,
                    binding.tvSyncdSyncd,
                    binding.mdiSyncd
            );

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PendingSyncServiceFragment.Companion.newInstance(true))
                    .commit();
        }

        binding.llTabPending.setOnClickListener(v -> {
            isTabActiveDeactive(
                    binding.llTabPending,
                    binding.tvTabPending,
                    binding.mdiTabPending,
                    binding.llTabSyncd,
                    binding.tvSyncdSyncd,
                    binding.mdiSyncd
            );

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PendingSyncServiceFragment.Companion.newInstance(true))
                    .commit();
        });


        binding.llTabSyncd.setOnClickListener(v -> {
            isTabActiveDeactive(
                    binding.llTabSyncd,
                    binding.tvSyncdSyncd,
                    binding.mdiSyncd,
                    binding.llTabPending,
                    binding.tvTabPending,
                    binding.mdiTabPending
            );

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, PendingSyncServiceFragment.Companion.newInstance(false))
                    .commit();
        });


    }

    @Override
    public void onComplete(Message msg) {

    }
    private void isTabActiveDeactive(
            LinearLayout llTabActiveName,
            TextView tvTabActiveName,
            MdiTextView mdiActive,
            LinearLayout llTabDeactiveName,
            TextView tvTabDeactiveName,
            MdiTextView mdiDeactive
    ) {
        llTabActiveName.setBackground(ContextCompat.getDrawable(this, R.drawable.border_rounded_corner));
        tvTabActiveName.setTextColor(ContextCompat.getColor(this, R.color.white));
        mdiActive.setTextColor(ContextCompat.getColor(this, R.color.white));

        llTabDeactiveName.setBackground(ContextCompat.getDrawable(this, R.drawable.border_rounded_corner_white));
        tvTabDeactiveName.setTextColor(ContextCompat.getColor(this, R.color.black));
        mdiDeactive.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    public void showInterview(ArrayList<InterviewInfoSyncUnsync> interview) {
        interviewList = interview;
        actionMenuHiddenShow();
    }

    public void actionMenuHiddenShow() {
        invalidateOptionsMenu();
    }

    @Override
    public void onInterviewUploadFinished(String message) {
        if (dlog != null) {
            dlog.dismiss();
        }
        tabCount();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, PendingSyncServiceFragment.Companion.newInstance(true))
                .commit();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu_search_sync, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem itemSync = menu.findItem(R.id.action_sync);

        if (item != null) {
            item.setVisible(true);
        }

        if (itemSync != null) {
            if (interviewList.size() > 0) {
                itemSync.setVisible(true);
                if (item != null) {
                    item.setVisible(false);
                }
            } else {
                itemSync.setVisible(false);
            }
        }

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")) {
                    // Perform actions on query submission
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.equals("")) {
                    viewModel.selectedItem(query);
                } else {
                    viewModel.selectedItem("");
                }
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_sync) {
            uploadUnSyncData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void tabCount() {
        long interviewListUnsendCount = App.getContext().getDB().getInterviewListCount(
                "N",
                App.getContext().getAppSettings().getLanguage(),
                App.getContext().getUserInfo().getUserCode()
        );
        long interviewListSendCount = App.getContext().getDB().getInterviewListCount(
                "Y",
                App.getContext().getAppSettings().getLanguage(),
                App.getContext().getUserInfo().getUserCode()
        );
        binding.tvSyncdSyncd.setText(getString(R.string.syncd_data_list) + " ( " + interviewListSendCount + ")");
        binding.tvTabPending.setText(getString(R.string.unsync_list) + " ( " + interviewListUnsendCount + ")");
    }
    private void uploadUnSyncData() {
        if (interviewList.size() == 0) {
            AppToast.showToast(this, R.string.no_selected_interview);
            return;
        }
        ArrayList<Household> householdList = App.getContext().getDB().getHouseholdBasicDataListUnsend();
        if (householdList.size() > 0) {
            if (SystemUtility.isConnectedToInternet(this)) {
                try {
                    RequestData request = new RequestData(RequestType.TRANSACTION, RequestName.HOUSEHOLD_DATA_ENTRY, Constants.MODULE_BUNCH_PUSH
                    );
                    JSONArray jHouseholdListArr = new JSONArray();
                    for (Household householdInfo : householdList) {
                        if (householdInfo.getSent() == 1L) continue;
                        jHouseholdListArr.put(householdInfo.toJson());
                    }
                    request.getData().put("householdList", jHouseholdListArr);
                    CommiunicationTask commiunicationTask = new CommiunicationTask(
                            this,
                            request,
                            R.string.uploading_data,
                            R.string.please_wait
                    );
                    commiunicationTask.setCompleteListener(msg -> {
                        ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                        MHealthTask tsk = new MHealthTask(
                                this,
                                Task.RETRIEVE_HOUSEHOLD_BASICDATA_INFORMATION_LIST,
                                R.string.retrieving_data,
                                R.string.please_wait
                        );
                        tsk.setParam(response);
                        tsk.setCompleteListener(taskMsg -> {});
                        tsk.execute();
                        dlog = ProgressDialog.show(
                                this,
                                getResources().getString(R.string.uploading_data),
                                getResources().getString(R.string.please_wait)
                        );
                    });
                    commiunicationTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MhealthException e) {
                    e.printStackTrace();
                }
            } else {
                SystemUtility.openInternetSettingsActivity(this);
            }
        } else {
            dlog = ProgressDialog.show(
                    this,
                    getResources().getString(R.string.uploading_data),
                    getResources().getString(R.string.please_wait)
            );
            ArrayList<InterviewInfoSyncUnsync> interviewListService = new ArrayList<>();
            ArrayList<InterviewInfoSyncUnsync> interviewListDoctorFeedback = new ArrayList<>();
            for (InterviewInfoSyncUnsync interview : interviewList) {
                if (interview.getDocFollowupId() > 0) {
                    // Add logic for patientInterviewDoctorFeedbackList
                    interviewListDoctorFeedback.add(interview);
                } else {
                    interviewListService.add(interview);
                }
            }
            if (interviewListService.size() > 0) {
                // Execute UploadInterviewTask for interviewListService
            }

            if (interviewListDoctorFeedback.size() > 0) {
                // Execute UploadDoctorFeedbackTask for interviewListDoctorFeedback
            }
        }
    }

}