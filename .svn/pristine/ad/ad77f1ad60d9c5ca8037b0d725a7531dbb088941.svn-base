package ngo.friendship.satellite.ui.notification;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;

import java.util.ArrayList;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.adapter.BasemHealthAdapter;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.databinding.CommonServiceActivityBinding;
import ngo.friendship.satellite.databinding.CommonServicesItemRowBinding;
import ngo.friendship.satellite.interfaces.FragmentCommunicator;
import ngo.friendship.satellite.interfaces.OnInterviewUploadListener;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.NotificationItem;
import ngo.friendship.satellite.model.QuestionnaireInfo;
import ngo.friendship.satellite.model.SavedInterviewInfo;
import ngo.friendship.satellite.ui.InterviewActivity;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.Utility;

/**
 * @author Md.Yeasin Ali
 * @created 01th Oct 2022
 */
public class NotificationListFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, OnInterviewUploadListener, FragmentCommunicator {
    private CommonServiceActivityBinding binding;
    private String IS_SAVED = "";
    String activityPath;
    ArrayList<SavedInterviewInfo> interviews = new ArrayList<>();
    BasemHealthAdapter<NotificationItem, CommonServicesItemRowBinding> mAdapter;
    ArrayList<NotificationItem> notificationItemList = new ArrayList<>();
    Context ctx;
    int dayOfList = 30;

    public static NotificationListFragment newInstance(String isSaved) {
        NotificationListFragment homePage = new NotificationListFragment();
        Bundle args = new Bundle();
        args.putString("IS_SAVED", isSaved);
        homePage.setArguments(args);
        return homePage;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = CommonServiceActivityBinding.inflate(inflater, container, false);
        App.loadApplicationData(getActivity());
        if (getArguments() != null) {
            IS_SAVED = getArguments().getString("IS_SAVED", "");
        }


        JSONArray notificationList = new JSONArray();
        try {
            String confiData = AppPreference.getString(getActivity(), KEY.FCM_CONFIGURATION, "[]");
            JSONArray configArry = new JSONArray(confiData);
            dayOfList = Integer.parseInt(JSONParser.getFcmConfigValue(configArry, "Notification_Service", "notificaiton_list_of_day"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.notificationItemList = App.getContext().getDB().getUnseenNotification(dayOfList, 0);
            Log.e("Size", "" + notificationList.length());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getItemAdapter();
        mAdapter.addItems(this.notificationItemList);
        return binding.getRoot();
    }

    private void retrieveInterview(String data) {
//
//        MHealthTask tsk = new MHealthTask(getActivity(), Task.RETRIEVE_SAVED_INTERVIEW_LIST, R.string.retrieving_data, R.string.please_wait);
//        tsk.setParam(IS_SAVED ? "Y" : "N", data);
//        tsk.setCompleteListener(new OnCompleteListener() {
//            @Override
//            public void onComplete(Message msg) {
//                if (msg.getData().getString(TaskKey.NAME).equals(TaskKey.MHEALTH_TASK)) {
//                    if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
//                        String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
//                        App.showMessageDisplayDialog(getActivity(), getResources().getString(R.string.saving_error), R.drawable.error, Color.RED);
//                    } else {
//                        interviews = (ArrayList<SavedInterviewInfo>) msg.getData().getSerializable(TaskKey.DATA0);
//                        if (interviews.size() > 0) {
//                            binding.serviceItemNotFound.serviceNotFound.setVisibility(View.GONE);
//                        } else {
//                            binding.serviceItemNotFound.serviceNotFound.setVisibility(View.VISIBLE);
//                        }
//                        mAdapter.addItems(interviews);
//                        Log.e("Inviews", "" + interviews.size());
//                    }
//                }
//            }
//        });
//        tsk.execute();

    }

    private void getItemAdapter() {
        mAdapter = new BasemHealthAdapter<>() {
            @Override
            public void onItemLongClick(@NonNull View view, NotificationItem model, int position) {

            }

            @Override
            public void onItemClick(@NonNull View view, NotificationItem savedInterviewInfo, int position, CommonServicesItemRowBinding dataBinding) {
                //   openQuestionnair(position);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onBindData(NotificationItem model, int position, CommonServicesItemRowBinding dataBinding) {
                dataBinding.clServiceBody.setOnClickListener(v -> {

                });

                dataBinding.tvBenefName.setText("" + model.getTitle());
                dataBinding.tvAgeGender.setVisibility(View.GONE);
                dataBinding.tvBeneficiaryCode.setText("" + model.getBenefCode().substring(model.getBenefCode().length() - 5));
//                dataBinding.tvLastVisitedTime.setText("Update : ");
//                dataBinding.llService.setVisibility(View.GONE);
                try {
                    long createDate = Utility.getMillisecondFromDate("" + model.getNotificationTime(), Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);

                    //  dataBinding.tvDateTime.setText("" + Utility.getDateTimeFromMillisecond(createDate, Constants.dd_MMM_HH_MM));
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (IS_SAVED) {
//                    dataBinding.cvCheck.setVisibility(View.GONE);
//                }
//                if (FileOperaion.isExist(model.getBenefImagePath())) {
//                    dataBinding.ivBeneficiary.setImageBitmap(FileOperaion.decodeImageFile(
//                            model.getBenefImagePath(), 50));
//                } else {
//                    if (model.getBeneficiarGender().equalsIgnoreCase("F")) {
//                        dataBinding.ivBeneficiary.setImageResource(R.drawable.ic_default_woman);
//                        dataBinding.ivBeneficiary.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_female));
//                    } else {
//                        dataBinding.ivBeneficiary.setImageResource(R.drawable.ic_default_man);
//                        dataBinding.ivBeneficiary.setColorFilter(ContextCompat.getColor(getContext(), R.color.app_color_100));
//                    }
//
//                }

//                dataBinding.tvQuestionnaireIcon.setText("");
//                dataBinding.tvQuestionnaireTitle.setText("" + model.getQuestionnaireTitle());
            }

            @Override

            public int getLayoutResId() {
                return R.layout.common_services_item_row;
            }
        };

        int[] ATTRS = new int[]{android.R.attr.listDivider};
        TypedArray a = getActivity().obtainStyledAttributes(ATTRS);
        Drawable divider = a.getDrawable(0);
        InsetDrawable insetDivider = new InsetDrawable(divider, 8, 16, 8, 16);
        a.recycle();
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(insetDivider);
        binding.rvService.addItemDecoration(itemDecoration);

        binding.rvService.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.rvService.setLayoutManager(mLayoutManager);
        binding.rvService.setItemAnimator(new DefaultItemAnimator());

        binding.rvService.setAdapter(mAdapter);

    }

    public void editInterView(SavedInterviewInfo info) {
        String jsonStr = "";
        QuestionnaireInfo questionnaireInfo = App.getContext().getDB().getQuestionnaire(info.getQuestionnaireId());
        if (questionnaireInfo != null) {
            jsonStr = questionnaireInfo.getQuestionnaireJSON(getActivity());
            jsonStr = JSONParser.preapreQuestionearWithAns(jsonStr, info.getQuestionAnswerJson());

            ArrayList<String> entryParams = new ArrayList<>();
            entryParams.add(info.getHouseholdNumber());

            Intent intent = new Intent(getActivity(), InterviewActivity.class);
            intent.putExtra(ActivityDataKey.QUESTIONNAIRE_JSON, jsonStr);
            intent.putExtra(ActivityDataKey.BENEFICIARY_CODE, info.getBeneficiaryCode());
            intent.putExtra(ActivityDataKey.BENEFICIARY_ID, info.getBeneficiaryId());
            intent.putExtra(ActivityDataKey.PARENT_INTERVIEW_ID, info.getParentInterviewId());
            intent.putExtra(ActivityDataKey.PARAMS, entryParams);
            intent.putExtra(ActivityDataKey.INTERVIEW_TYPE, ActivityDataKey.UPDATE);
            intent.putExtra(ActivityDataKey.TRANS_REF, info.getTransRef());
            intent.putExtra("EDIT", true);
            startActivity(intent);

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.ctx = context;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (App.getContext().getAppSettings() == null) {
            App.getContext().readAppSettings(getActivity());
        }
        if (!SystemUtility.isAutoTimeEnabled(getActivity())) {
            SystemUtility.openDateTimeSettingsActivity(getActivity());
        }
//        if(IS_SAVED){
//            activityPath= Utility.setActivityPath(getActivity(),R.string.saved_interview);
//        }else{
//            activityPath=Utility.setActivityPath(getActivity(),R.string.sent_interview);
//        }

        retrieveInterview("");
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onInterviewUploadFinished(String message) {

    }

    @Override
    public void passData(@Nullable String name) {
        Toast.makeText(ctx, "" + name, Toast.LENGTH_SHORT).show();
    }
}