package ngo.friendship.satellite.ui;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;

import com.google.gson.Gson;
import com.logic27.components.Component;
import com.logic27.components.ComponentFectory;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.MainActivity;
import ngo.friendship.satellite.QuestionManager;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.MHealthTask;
import ngo.friendship.satellite.asynctask.Task;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.asynctask.UploadDoctorFeedbackTask;
import ngo.friendship.satellite.communication.APICommunication;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.FollowupType;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.Priority;
import ngo.friendship.satellite.constants.QUESTION_TYPE;
import ngo.friendship.satellite.constants.QuestionnaireName;
import ngo.friendship.satellite.constants.RequestAction;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.constants.ScheduleStatus;
import ngo.friendship.satellite.databinding.DoctorFeedbackFailedLayoutBinding;
import ngo.friendship.satellite.databinding.InterviewActivityBinding;
import ngo.friendship.satellite.databinding.SaveInterviewLayoutBinding;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.interfaces.OnDialogDismissListener;
import ngo.friendship.satellite.interfaces.OnInterviewUploadListener;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.BeneficiaryMigration;
import ngo.friendship.satellite.model.CCSBeneficiary;
import ngo.friendship.satellite.model.CourtyardMeeting;
import ngo.friendship.satellite.model.Household;
import ngo.friendship.satellite.model.ImmunaizationBeneficiary;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.PatientInterviewDetail;
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedback;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionAnswer;
import ngo.friendship.satellite.model.QuestionBranch;
import ngo.friendship.satellite.model.QuestionList;
import ngo.friendship.satellite.model.QuestionOption;
import ngo.friendship.satellite.model.ReferralCenterInfo;
import ngo.friendship.satellite.model.ScheduleInfo;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.model.maternal.MaternalAbortion;
import ngo.friendship.satellite.model.maternal.MaternalBabyInfo;
import ngo.friendship.satellite.model.maternal.MaternalDelivery;
import ngo.friendship.satellite.model.maternal.MaternalInfo;
import ngo.friendship.satellite.model.maternal.MaternalService;
import ngo.friendship.satellite.reflection.ReflectionManager;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.FileOperaion;
import ngo.friendship.satellite.utility.GPSUtility;
import ngo.friendship.satellite.utility.ModelProvider;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;
import ngo.friendship.satellite.views.AppToast;
import ngo.friendship.satellite.views.AudioRecorderView;
import ngo.friendship.satellite.views.AutoCompleteEditTextView;
import ngo.friendship.satellite.views.CameraView;
import ngo.friendship.satellite.views.DateView;
import ngo.friendship.satellite.views.DialogView;
import ngo.friendship.satellite.views.EditTextView;
import ngo.friendship.satellite.views.ListCheckGroupView;
import ngo.friendship.satellite.views.QuestionView;
import ngo.friendship.satellite.views.QuestionViewFactory;
import ngo.friendship.satellite.views.RadioGroupView;
import ngo.friendship.satellite.views.ReferralCenterView;


public class InterviewActivity extends AppCompatActivity implements OnClickListener, OnGestureListener, LocationListener, OnInterviewUploadListener {
    boolean flagForupdateFeedbackFromDetails = false;
    PatientInterviewDoctorFeedback pIDCFeedbackFromDoctorReffereal = new PatientInterviewDoctorFeedback();
    PatientInterviewDoctorFeedback doctorFeedbackUpdatedObject = new PatientInterviewDoctorFeedback();

    SaveInterviewLayoutBinding binding;
    MediaPlayer mpAudio;
    Vibrator vibrator;
    LocationManager lm;
    private GestureDetectorCompat mDetector;
    float maxDist = 0;
    ProgressDialog dlog;
    private QuestionView questionView;
    ArrayList<String> helpLineNoList = new ArrayList<>();
    InterviewActivityBinding interviewBinding;
    String interviewRecordDate = "";
    long interviewDateinimis = 0;
    private HashMap<String, QuestionAnswer> questionAnswerListForUpload = new HashMap<String, QuestionAnswer>(); //The question answer list. Use to create server json


    Question currentQuestion; //Keep the current question.
    String qJson; //Keep the questionnaire JSON.
    QuestionList qList = null; //Keep the question list which are retrieved from questionnaire JSON.
    boolean isQuestionLayoutDisplaying = false;//The is question layout displaying.

    String beneficiaryCode = null;
    String pendingSyncPage = "";
    long beneficiaryId = -1;
    String householdNumber = null;

    /**
     * The interview start time.
     */
    private long interviewStartTime; /// Will store time in millisecond
    private HashMap<String, QuestionAnswer> questionAnswerList = new HashMap<String, QuestionAnswer>(); //The question answer list. Use to create server json
    ArrayList<String> prescription = null;//To keep the prescription, will be used when answer saved into database
    boolean needDoctorReferral = false; //Flag to determine whether doctor feedback is required.
    boolean isDoctorFeedback = false; //Flag to determine whether the server response is from doctor feedback.
    String interviewType = ActivityDataKey.NEW;
    String SINGLE_PG_FROM_VIEW = "1";
    String householdWithBeneficiaryReg = "";
    long doctorFeedbackRequestStartTime;
    long nextFollowupDate;
    int referralCenterId;
    String activityPath;
    long parentInterviewId;
    Beneficiary beneficiaryInfo;
    ArrayList<Question> questionsToBeAdded;
    Stack<Question> questionsListRendering = new Stack<>();
    QuestionList currentQuestionList = null;
    private String SCHE_TYPE = FollowupType.PATIENT_VISIT;

    HashMap<String, String> filesMap = new HashMap<String, String>();

    String systemMessage = "";

    /**
     * The request number.
     */
    int requestNumber;


    /**
     * The interview upload response.
     */
    private ResponseData interviewUploadResponse;

    /**
     * The doctor feedback request interval.
     */

    long doctorFeedbackRequestInterval = 30 * 1000;/// in millisecond
    long doctorFeedbackRequestwaittime = 10 * 60 * 1000;/// in millisecond

    private ArrayList<String> entryParams;
    private boolean isGust = false;

    /**
     * The interview id.
     */
    long interviewId = -1;

    int questionOrder = 1;

    JSONObject questionAnswerJson; //Store the question answer JSON which will be sent to server.
    boolean isRemairPartInterview = false;
    boolean isDoctorFeedbackUploded = false;


    private final int DIALOG_GENERAL = 0;
    private final int DIALOG_DOCTOR_REFERRAL = 1;
    private final int DIALOG_DATA_UPLOAD_SUCCESSFULL = 2;
    private final int EXIT_ON_OK = 3;
    private final int UPLOAD_USER_REGISTRATION = 4;

    private long maternalServiceId = 0;
    Location location;
    Stack<Question> questionsListChanged = new Stack<>();
    String serverStatus = "";

    private void initializeVariables() {
        needDoctorReferral = false;
        isDoctorFeedback = false;


        prescription = null;

        isQuestionLayoutDisplaying = false;
        qList = null;

        questionAnswerJson = null;
        doctorFeedbackRequestStartTime = 0;

        nextFollowupDate = -1;
        referralCenterId = -1;

    }

    //Initialize media player to play sound when doctor feedback arrived.
    private void initializeMediaPlayer() {
        mpAudio = new MediaPlayer();
        mpAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
        AssetFileDescriptor afd;
        try {
            afd = getAssets().openFd("mp3/doctor_feedback.mp3");
            mpAudio.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mpAudio.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initialize Vibrator.
    private void initializeVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Play audio when doctor feedback arrived
     * and vibrate the device if active from settings.
     */
    private void playDoctorFeedAudio() {
        vibrateDevice();
        mpAudio.start();
    }

    /**
     * Vibrate device.
     */
    private void vibrateDevice() {
        boolean vodf = AppPreference.getBoolean(this, KEY.VIBRATE_ON_DOCROT_FEEDBACK, true);
        if (vodf) {
            long pattern[] = {0, 500, 300, 500, 300, 400};
            vibrator.vibrate(pattern, -1);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mpAudio != null) {
            mpAudio.stop();
            mpAudio.release();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeMediaPlayer();
        initializeVibrator();

        App.loadApplicationData(this);
        mDetector = new GestureDetectorCompat(this, this); // Initialize Gesture
        // Check if the activity created first time
        // Initialize location service
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            doctorFeedbackRequestInterval = Long.parseLong(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "DOCTOR_CENTER", "doctor_feedback_internal_request_frequency_gap")) * 1000;
        } catch (MhealthException e) {
            e.printStackTrace();
        }

        try {
            doctorFeedbackRequestwaittime = Long.parseLong(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "DOCTOR_CENTER", "doctor_feedback_wait_time")) * 60 * 1000;
        } catch (MhealthException e) {
            e.printStackTrace();
        }

        if (savedInstanceState == null) {
            Log.e("InterviewActivity", "MAIN");
            initializeVariables();
            dlog = ProgressDialog.show(this, "", getString(R.string.please_wait));

            // Get necessary data from activity bundle
            qJson = getIntent().getExtras().getString(ActivityDataKey.QUESTIONNAIRE_JSON);
            serverStatus = getIntent().getExtras().getString("server");

            Log.d(TAG, "onCreate: ....service name is ..." + getIntent().getExtras().getString("server"));
            beneficiaryCode = getIntent().getExtras().getString(ActivityDataKey.BENEFICIARY_CODE);
            try {

                pendingSyncPage = getIntent().getExtras().getString(ActivityDataKey.PENDING_SYNC_PAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            beneficiaryId = getIntent().getExtras().getLong(ActivityDataKey.BENEFICIARY_ID);
            parentInterviewId = getIntent().getExtras().getLong(ActivityDataKey.PARENT_INTERVIEW_ID);

            if (getIntent().getExtras().containsKey(ActivityDataKey.INTERVIEW_TYPE)) {
                interviewType = getIntent().getExtras().getString(ActivityDataKey.INTERVIEW_TYPE);
            }
            if (getIntent().getExtras().containsKey(ActivityDataKey.SINGLE_PG_FROM_VIEW)) {
                SINGLE_PG_FROM_VIEW = getIntent().getExtras().getString(ActivityDataKey.SINGLE_PG_FROM_VIEW);
                if (SINGLE_PG_FROM_VIEW == null) {
                    SINGLE_PG_FROM_VIEW = "1";
                }
            }
            if (getIntent().getExtras().containsKey(ActivityDataKey.NEW_HOUSEHOLD_BENEFICIARY_REG)) {
                householdWithBeneficiaryReg = getIntent().getExtras().getString(ActivityDataKey.NEW_HOUSEHOLD_BENEFICIARY_REG);
            }
            if (getIntent().getExtras().containsKey(ActivityDataKey.MATERNAL_SERVICE_ID)) {
                maternalServiceId = getIntent().getExtras().getLong(ActivityDataKey.MATERNAL_SERVICE_ID);
            }

            if (getIntent().getExtras().containsKey(ActivityDataKey.IS_GUST)) {
                isGust = getIntent().getExtras().getBoolean(ActivityDataKey.IS_GUST);
            }

            if (getIntent().getExtras().containsKey(ActivityDataKey.PARAMS)) {
                entryParams = (ArrayList<String>) getIntent().getExtras().getSerializable(ActivityDataKey.PARAMS);
            } else {
                entryParams = new ArrayList<String>();
            }

            if (getIntent().getExtras().containsKey(ActivityDataKey.TRANS_REF)) {
                interviewStartTime = getIntent().getExtras().getLong(ActivityDataKey.TRANS_REF);
            } else {
                interviewStartTime = Calendar.getInstance().getTimeInMillis();
            }

            if (beneficiaryCode != null) {   //Get household number from beneficiary code
                householdNumber = beneficiaryCode.substring(beneficiaryCode.length() - 5, beneficiaryCode.length() - 2);
            }
            new ParseQuestionnaireJSONTask().execute();
        } else {
            Log.e("InterviewActivity", "RESTORE");

            String currentKey = savedInstanceState.getString("currentKey");
            String previousKey = savedInstanceState.getString("previousKey");
            String firstQuestionKey = savedInstanceState.getString("firstQuestionKey");
            Stack<String> questionKeyStack = new Stack<String>();
            ArrayList<String> questionKeyStackArrayList = savedInstanceState.getStringArrayList("questionKeyStack");
            if (questionKeyStackArrayList != null) {
                for (String string : questionKeyStackArrayList) {
                    questionKeyStack.push(string);
                }
            }

            entryParams = (ArrayList<String>) savedInstanceState.getSerializable("entryParams");
            questionAnswerList = (HashMap<String, QuestionAnswer>) savedInstanceState.getSerializable("questionAnswerList");
            prescription = (ArrayList<String>) savedInstanceState.getSerializable("prescription");
            beneficiaryInfo = (Beneficiary) savedInstanceState.getSerializable("beneficiaryInfo");
            interviewUploadResponse = (ResponseData) savedInstanceState.getSerializable("interviewUploadResponse");

            qJson = savedInstanceState.getString("qJson");

            isQuestionLayoutDisplaying = savedInstanceState.getBoolean("isQuestionLayoutDisplaying");
            beneficiaryCode = savedInstanceState.getString("beneficiaryCode");
            beneficiaryId = savedInstanceState.getLong("beneficiaryId");
            householdNumber = savedInstanceState.getString("householdNumber");

            interviewStartTime = savedInstanceState.getLong("interviewStartTime");
            maternalServiceId = savedInstanceState.getLong("maternalServiceId");
            needDoctorReferral = savedInstanceState.getBoolean("needDoctorReferral");
            isDoctorFeedback = savedInstanceState.getBoolean("isDoctorFeedback");
            interviewType = savedInstanceState.getString("interviewType");
            try {
                SINGLE_PG_FROM_VIEW = savedInstanceState.getString(ActivityDataKey.SINGLE_PG_FROM_VIEW);
            } catch (Exception e) {
                e.printStackTrace();
            }

            doctorFeedbackRequestStartTime = savedInstanceState.getLong("doctorFeedbackRequestStartTime");
            nextFollowupDate = savedInstanceState.getLong("nextFollowupDate");
            referralCenterId = savedInstanceState.getInt("referralCenterId");
            activityPath = savedInstanceState.getString("activityPath");
            parentInterviewId = savedInstanceState.getLong("parentInterviewId");
            requestNumber = savedInstanceState.getInt("requestNumber");

            isGust = savedInstanceState.getBoolean("isGust");
            interviewId = savedInstanceState.getLong("interviewId");
            questionOrder = savedInstanceState.getInt("questionOrder");

            isRemairPartInterview = savedInstanceState.getBoolean("isRemairPartInterview");
            isDoctorFeedbackUploded = savedInstanceState.getBoolean("isDoctorFeedbackUploded");

            if (savedInstanceState.containsKey("tmpPath")) {
                CameraView.tempPath = savedInstanceState.getString("tmpPath");
            }


            try {
                qList = JSONParser.parseQuestionListJSON(qJson);
            } catch (MhealthException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (isDoctorFeedback) {
                qList.setQuestionnaireId(currentQuestionList.getQuestionnaireId());
                qList.setQuestionnaireName(currentQuestionList.getQuestionnaireName());
                qList.setQuestionnaireTitle(currentQuestionList.getQuestionnaireTitle());
            } else if (qList != null) {
                currentQuestionList = qList;

            } else {
                AppToast.showToast(InterviewActivity.this, R.string.error_retrieving_questionnaire);
                InterviewActivity.this.finish();
            }

            setQuestionLayoutContainer();

            QuestionManager qManager = new QuestionManager(qList.getQuestionList(), qList.getFirstQuestionKey());
            qManager.setCurrentKey(currentKey);
            qManager.setPreviousKey(previousKey);
            qManager.setFirstQuestionKey(firstQuestionKey);
            qManager.setQuestionKeyStack(questionKeyStack);
            currentQuestion = qManager.getQuestionByKey(currentKey);
            qManager.setCurrentQuestion(currentQuestion);


            App.getContext().setQuestionManager(qManager);
            showQuestion(currentQuestion, true);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (App.getContext().getAppSettings() == null)
            App.getContext().readAppSettings(this);


        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        try {
            if (App.getContext().getQuestionManager().getCurrentKey() != null) {
                outState.putString("currentKey", App.getContext().getQuestionManager().getCurrentKey());
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        outState.putString("previousKey", App.getContext().getQuestionManager().getPreviousKey());
        outState.putString("firstQuestionKey", App.getContext().getQuestionManager().getFirstQuestionKey());
        outState.putStringArrayList("questionKeyStack", new ArrayList<String>(App.getContext().getQuestionManager().getQuestionKeyStack()));
        outState.putSerializable("questionAnswerList", questionAnswerList);
        outState.putSerializable("prescription", prescription);
        outState.putSerializable("beneficiaryInfo", beneficiaryInfo);
        outState.putSerializable("interviewUploadResponse", interviewUploadResponse);
        outState.putSerializable("entryParams", entryParams);

        try {
            outState.putString("qJson", JSONCreateor.createQuestionnaireJson(qList));

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        outState.putBoolean("isQuestionLayoutDisplaying", isQuestionLayoutDisplaying);
        outState.putString("beneficiaryCode", beneficiaryCode);
        outState.putLong("beneficiaryId", beneficiaryId);
        outState.putString("householdNumber", householdNumber);

        outState.putLong("interviewStartTime", interviewStartTime);

        outState.putBoolean("needDoctorReferral", needDoctorReferral);
        outState.putBoolean("isDoctorFeedback", isDoctorFeedback);
        outState.putString("interviewType", interviewType);
        outState.putLong("doctorFeedbackRequestStartTime", doctorFeedbackRequestStartTime);
        outState.putLong("maternalServiceId", maternalServiceId);

        outState.putLong("nextFollowupDate", nextFollowupDate);
        outState.putInt("referralCenterId", referralCenterId);
        outState.putString("activityPath", activityPath);
        outState.putLong("parentInterviewId", parentInterviewId);

        outState.putInt("requestNumber", requestNumber);

        outState.putBoolean("isGust", isGust);
        outState.putLong("interviewId", interviewId);
        outState.putInt("questionOrder", questionOrder);
        outState.putBoolean("isRemairPartInterview", isRemairPartInterview);
        outState.putBoolean("isDoctorFeedbackUploded", isDoctorFeedbackUploded);

        if (questionView instanceof CameraView) {
            outState.putString("tmpPath", CameraView.tempPath);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        lm.removeUpdates(this);
    }

    // Set Question display layout to screen.
    private void setQuestionLayoutContainer() {
        // set the layout which will display question view
        interviewBinding = InterviewActivityBinding.inflate(getLayoutInflater());
        setContentView(interviewBinding.getRoot());
        App.getContext().setTraningBanner(this);
        PopupMenu dropDownMenu = new PopupMenu(this, interviewBinding.headerLayout.ivLogo);
        final Menu menu = dropDownMenu.getMenu();
        dropDownMenu.getMenuInflater().inflate(R.menu.question_display_menu, menu);
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_close:
                        if (questionAnswerJson == null)
                            showExitPromptDialog();
                        else
                            finish();
                        return true;
                }
                return false;
            }
        });


        interviewBinding.headerLayout.ivLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });
        // set activity path
        try {
            if (beneficiaryCode != null) {
                Utility.setActivityPath(this, qList.getQuestionnaireTitle() + " (" + beneficiaryInfo.getBenefCodeShort() + " - " + beneficiaryInfo.getBenefName() + " - " + beneficiaryInfo.getGender() + "/" + beneficiaryInfo.getAge() + ")");
            } else {
                Utility.setActivityPath(this, qList.getQuestionnaireTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utility.setActivityPath(this, qList.getQuestionnaireTitle());
        }


        interviewBinding.btnNext.setOnClickListener(this);


        interviewBinding.btnPrev.setOnClickListener(this);

        //Initialize the layout in which the question is displayed and remove all child of this view

        interviewBinding.llQuestionView.removeAllViews();

        isQuestionLayoutDisplaying = true;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        App.getContext().onStartActivity(this);
    }

    // Set interview saved layout to screen.
    private void setQuestionSaveLayout() {
        binding = SaveInterviewLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Utility.setActivityPath(this, qList.getQuestionnaireTitle());
        App.getContext().setTraningBanner(this);

        binding.btnSaveQuestion.setOnClickListener(this);
        if (needDoctorReferral) {
            binding.btnSaveQuestion.setVisibility(View.GONE);
        } else {
            binding.btnSaveQuestion.setVisibility(View.VISIBLE);
        }
        binding.btnCancelQuestion.setOnClickListener(this);

        binding.btnSaveQuestionAndCall.setOnClickListener(this);


        if (isDoctorFeedback) {
            binding.btnSaveQuestionAndCall.setVisibility(View.GONE);
        } else {
            binding.btnSaveQuestionAndCall.setOnClickListener(this);
        }

        binding.btnUploadQuestion.setOnClickListener(this);


        binding.btnShowAnswer.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);

        isQuestionLayoutDisplaying = false;
        App.getContext().onStartActivity(this);
        Boolean flagHelLine = false;
        List<QuestionAnswer> qaArrList = new ArrayList<QuestionAnswer>();
        for (String key : questionAnswerList.keySet()) {

            qaArrList.add(questionAnswerList.get(key));
        }
        qaArrList = Utility.sortQuestionAnswerList(qaArrList);
        StringBuilder sb = new StringBuilder();
        for (QuestionAnswer qa : qaArrList) {
            if (qa.getAnswerList() != null) {
                StringBuilder sbAnswer = new StringBuilder();
                sbAnswer.append(TextUtils.join("|", qa.getAnswerList()));

                if (sbAnswer.toString().trim().equalsIgnoreCase(""))
                    continue;

                sb.append("[" + qa.getOrder() + "]" + qa.getQuestionCaption());
                sb.append("\n");
                sb.append(sbAnswer.toString());
                if (qa.getQuestionCaption().contains(KEY.CALL_HELPLINE)) {
                    try {
                        ArrayList<Question> questionArrayList = qList.getQuestionList();
                        for (int i = 0; i < questionArrayList.size(); i++) {
                            if (questionArrayList.get(i).getName().startsWith(KEY.CALL_HELPLINE)) {
                                String helpLineNo = "tel:" + questionArrayList.get(i).getDefaultValue().get(0);
                                helpLineNoList.add(helpLineNo);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    flagHelLine = true;
                }

            }
        }

        if (flagHelLine == false) {
            binding.btnSaveQuestionAndCall.setVisibility(View.GONE);
            binding.btnUploadQuestion.setVisibility(View.VISIBLE);

        } else {
            binding.btnSaveQuestionAndCall.setVisibility(View.VISIBLE);
            binding.btnUploadQuestion.setVisibility(View.GONE);
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btn_prev:
                    if (SINGLE_PG_FROM_VIEW.equals("1")) {
                        showExitPromptDialog();
                    } else {
                        gotoPreviousQuestion();
                    }
                    break;
                case R.id.btn_next:
                    StringBuilder builders = new StringBuilder();
                    builders.append(getString(R.string.input_required_belowq) + "\n\n");
                    if (SINGLE_PG_FROM_VIEW.equals("1")) {
//                        setQuestionSaveLayout();
                        StringBuilder builder = new StringBuilder();
                        builder.append(getString(R.string.input_required_belowq) + "\n\n");
                        boolean isAllFildValidation = false;

                        for (Question question : questionsListRendering) {
                            if (question != null) {
                                try {
                                    if (question.getUI() != null) {
                                        if (question.getUI().isValid(true)) {
                                            try {
                                                String questionKey = question.getKey().replace("question", "q");
                                                for (String key : questionAnswerList.keySet()) {
                                                    if (questionKey.matches(key)) {
                                                        if (question.getType().equalsIgnoreCase(QUESTION_TYPE.SELECT) || question.getType().equalsIgnoreCase(QUESTION_TYPE.REFERRAL_CENTER)) {
                                                            ArrayList<String> userInput = new ArrayList<String>();
                                                            userInput.add(question.getUI().getInputData());
                                                            questionAnswerList.get(key).setAnswerList(userInput);
                                                            if (question.getType().equalsIgnoreCase(QUESTION_TYPE.REFERRAL_CENTER)) {
                                                                try {
                                                                    referralCenterId = Integer.parseInt(question.getUI().getInputData());
                                                                } catch (Exception e) {

                                                                }
                                                            }
                                                        } else if (question.getType().equalsIgnoreCase(QUESTION_TYPE.STRING) || question.getType().equalsIgnoreCase(QUESTION_TYPE.NUMBER) || question.getType().equalsIgnoreCase(QUESTION_TYPE.LOCATION)) {
                                                            ArrayList<String> userInput = new ArrayList<String>();
                                                            String val = question.getUI().getInputData();
                                                            userInput.add(val);
                                                            if (question.getName().matches("HH_NO")) {
                                                                ArrayList<String> hhNumber = new ArrayList<String>();
                                                                hhNumber.add(App.getContext().getUserInfo().getUserCode() + val);
                                                                questionAnswerList.get(key).setAnswerList(hhNumber);

                                                            } else {
                                                                questionAnswerList.get(key).setAnswerList(userInput);
                                                            }

                                                        } else if (question.getType().equalsIgnoreCase(QUESTION_TYPE.BINARY)) {
                                                            ArrayList<String> userInput = new ArrayList<String>();
                                                            String userInputStr = question.getUI().getInputData();
                                                            userInput.add(userInputStr);
                                                            questionAnswerList.get(key).setAnswerList(userInput);
                                                        } else if (question.getType().equalsIgnoreCase(QUESTION_TYPE.DATE)) {
                                                            ArrayList<String> userInput = new ArrayList<String>();
                                                            userInput.add(question.getUI().getInputData());
                                                            questionAnswerList.get(key).setAnswerList(userInput);
                                                            if (question.getName().contains("NEXT_FOLLOWUP_DATE")) {
                                                                try {
                                                                    nextFollowupDate = Utility.getMillisecondFromDate(question.getUI().getInputData(), Constants.DATE_FORMAT_YYYY_MM_DD);
                                                                } catch (ParseException e) {
                                                                    // TODO Auto-generated catch block
                                                                    e.printStackTrace();
                                                                }
                                                            }

                                                        } else if (question.getType().equalsIgnoreCase(QUESTION_TYPE.TIME)) {
                                                            ArrayList<String> userInput = new ArrayList<String>();
                                                            userInput.add(question.getUI().getInputData());
                                                            questionAnswerList.get(key).setAnswerList(userInput);
                                                        } else if (question.getType().equalsIgnoreCase(QUESTION_TYPE.PRESCRIPTION) || question.getType().equalsIgnoreCase(QUESTION_TYPE.MULTI_SELECT) || question.getType().equalsIgnoreCase(QUESTION_TYPE.LIST)) {
                                                            final ArrayList<String> userInput = (ArrayList<String>) question.getUI().getInputDataList();
                                                            questionAnswerList.get(key).setAnswerList(userInput);
                                                            prescription = userInput;
                                                        }
                                                        break;
                                                    }
                                                }
                                                Log.e("data", "" + questionAnswerJson.toString());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            isAllFildValidation = true;
                                            builder.append("* " + question.getCaption() + "\n\n");
                                        }

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        }

                        if (!isAllFildValidation) {
                            try {
                                for (String key : questionAnswerList.keySet()) {
                                    //   String value  = questionAnswerJson.getString(key);
                                    if (questionAnswerList.get(key).getQuestionName().equals("RECORD_DATE")) {
                                        for (int i = 0; i < questionAnswerList.get(key).getAnswerList().size(); i++) {
                                            interviewRecordDate = questionAnswerList.get(key).getAnswerList().get(i);
                                            break;
                                        }
                                    }
                                }
                                questionAnswerJson = JSONCreateor.getQuestionAnswerJson(this, App.getContext().getUserInfo().getUserCode(), App.getContext().getUserInfo().getPassword(), beneficiaryCode, interviewStartTime, Calendar.getInstance().getTimeInMillis(), qList.getQuestionnaireId(), 0.0, 0.0, questionAnswerList);
                                //questionAnswerJson = JSONCreateor.getQuestionAnswerJson(this, App.getContext().getUserInfo().getUserCode(), App.getContext().getUserInfo().getPassword(), beneficiaryCode, interviewStartTime, timeInMilli, qList.getQuestionnaireId(), longitude, latitude, questionAnswerListForUpload);
                                Log.e("questionAnswerJson", "" + questionAnswerJson.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            setQuestionSaveLayout();
                        } else {
                            HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
                            buttonMap.put(1, R.string.btn_close);
                            DialogView dialog = new DialogView(this, R.string.dialog_title, builder.toString(), R.drawable.warning, buttonMap);
                            dialog.show();
                        }
                        Log.e("data", "aaabbcc" + builder.toString());

                    } else {
                        gotoNextQuestion();
                    }

                    InterviewActivity.this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                    break;
                case R.id.btn_back:
                    if (SINGLE_PG_FROM_VIEW.equals("1")) {
                        setContentView(interviewBinding.getRoot());
                    } else {
                        gotoPreviousQuestion();
                        InterviewActivity.this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                    }

                    break;

                case R.id.btn_save_question:
                    StringBuilder builderSave = new StringBuilder();
                    builderSave.append(getString(R.string.input_required_belowq) + "\n\n");
                    boolean isAllFildValidationData = false;
                    for (Question question : qList.getQuestionList()) {
                        String questionKey = question.getKey().replace("question", "q");
                        QuestionAnswer questionAnswer = questionAnswerList.get(questionKey);
                        if (question.isRequired()) {
                            if (questionAnswer.getAnswerList() != null) {
                                if (questionAnswer.getAnswerList().size() > 0) {
                                    String data = questionAnswer.getAnswerList().get(0);
                                    if (data == null || data.isEmpty()) {
                                        builderSave.append("* " + question.getCaption() + "\n\n");
                                        isAllFildValidationData = true;
                                    }
//                                    Log.e("eeeeeeeeeee", "dddd");
                                } else {

                                    builderSave.append("* " + question.getCaption() + "\n\n");
                                    isAllFildValidationData = true;
                                }
                            } else {
                                builderSave.append("* " + question.getCaption() + "\n\n");
                                isAllFildValidationData = true;
                            }

                        }
                    }
                    if (isAllFildValidationData) {
                        AppToast.showToastWarnaing(InterviewActivity.this, "" + builderSave.toString());
                    } else {
                        saveInterview(false);
                    }

                    break;

                case R.id.btn_save_question_and_call:
                    StringBuilder buildersd = new StringBuilder();
                    buildersd.append(getString(R.string.input_required_belowq) + "\n\n");
                    boolean isAllFildValidationDataCall = false;
                    for (Question question : qList.getQuestionList()) {
                        String questionKey = question.getKey().replace("question", "q");
                        QuestionAnswer questionAnswer = questionAnswerList.get(questionKey);
                        if (question.isRequired()) {
                            if (questionAnswer.getAnswerList() != null) {
                                if (questionAnswer.getAnswerList().size() > 0) {
                                    String data = questionAnswer.getAnswerList().get(0);
                                    if (data == null || data.isEmpty()) {
                                        buildersd.append("* " + question.getCaption() + "\n\n");
                                        isAllFildValidationDataCall = true;
                                    }
                                } else {

                                    buildersd.append("* " + question.getCaption() + "\n\n");
                                    isAllFildValidationDataCall = true;
                                }
                            } else {
                                buildersd.append("* " + question.getCaption() + "\n\n");
                                isAllFildValidationDataCall = true;
                            }

                        }
                    }
                    if (isAllFildValidationDataCall) {
                        AppToast.showToastWarnaing(InterviewActivity.this, "" + buildersd.toString());
                    } else {
                        try {
                            if (helpLineNoList == null || helpLineNoList.size() <= 0) {
                                //AppToast.showToastError(InterviewActivity.this, "HELP Line no is not set for this case.");

                            } else {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse(helpLineNoList.get(0)));
                                if (ActivityCompat.checkSelfPermission(InterviewActivity.this,
                                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    AppToast.showToastError(InterviewActivity.this, "You doesn't allow phone call permission from this app. Please, Go to your phone settings & allow phone call service.");

                                }
                                startActivity(callIntent);
                                saveInterview(true);
                            }

                        } catch (Exception e) {
                            AppToast.showToastError(InterviewActivity.this, "Error occured in your request.");
                        }
                    }

                    break;

                case R.id.btn_upload_question:
                    StringBuilder builderUpload = new StringBuilder();
                    builderUpload.append(getString(R.string.input_required_belowq) + "\n\n");
                    boolean isAllFildValidationDataUpload = false;
                    for (Question question : qList.getQuestionList()) {
                        String questionKey = question.getKey().replace("question", "q");
                        QuestionAnswer questionAnswer = questionAnswerList.get(questionKey);
                        if (question.isRequired()) {
                            if (questionAnswer.getAnswerList() != null) {
                                if (questionAnswer.getAnswerList().size() > 0) {
                                    String data = questionAnswer.getAnswerList().get(0);
                                    if (data == null || data.isEmpty()) {
                                        builderUpload.append("* " + question.getCaption() + "\n\n");
                                        isAllFildValidationDataUpload = true;
                                    }
                                    Log.e("eeeeeeeeeee", "dddd");
                                } else {

                                    builderUpload.append("* " + question.getCaption() + "\n\n");
                                    isAllFildValidationDataUpload = true;
                                }
                            } else {
                                builderUpload.append("* " + question.getCaption() + "\n\n");
                                isAllFildValidationDataUpload = true;
                            }

                        }
                    }
                    if (isAllFildValidationDataUpload) {
                        AppToast.showToastWarnaing(InterviewActivity.this, "" + builderUpload.toString());
                    } else {
                        if (SystemUtility.isConnectedToInternet(this)) {
                            flagForupdateFeedbackFromDetails = getIntent().getExtras().containsKey("flagForupdateFeedbackFromDetails");
                            if (flagForupdateFeedbackFromDetails) {
                                saveInterviewIntoDatabaseAndLocalFile();
                                ArrayList<PatientInterviewDoctorFeedback> doctorFeedbackLists = new ArrayList<>();
                                doctorFeedbackLists.add(doctorFeedbackUpdatedObject);
                                dlog = ProgressDialog.show(this, getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));
                                UploadDoctorFeedbackTask uit = new UploadDoctorFeedbackTask(this, doctorFeedbackLists, true);
                                uit.setOnInterviewUploadListener(this);
                                uit.execute();
//							showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.data_upload_successfull), R.string.btn_ok, R.drawable.information, Color.BLACK, DIALOG_DATA_UPLOAD_SUCCESSFULL);


                            } else {
                                saveInterview(true);
                            }

                        } else {
                            SystemUtility.openInternetSettingsActivity(this);
                        }
                    }

                    break;

                case R.id.btn_cancel_question:
                    if (questionAnswerJson == null)
                        showExitPromptDialog();
                    else
                        finish();
                    break;

                case R.id.btn_show_answer:
                    showAllAnswer();

                    break;

                default:
                    break;
            }
        } catch (MhealthException e) {
            e.printStackTrace();
        }

    }

    private void renderAnswer() {

    }

    /**
     * Build the question answer text to display in a alert dialog.
     */

    private void showAllAnswer() {

        List<QuestionAnswer> qaArrList = new ArrayList<QuestionAnswer>();
        for (String key : questionAnswerList.keySet()) {
            qaArrList.add(questionAnswerList.get(key));
        }
        qaArrList = Utility.sortQuestionAnswerList(qaArrList);

        Gson data = new Gson();
        String datass = data.toJson(qaArrList);

        Bitmap img = null;
//        for (QuestionAnswer qa : qaArrList) {
//            if (qa.getAnswerList() != null) {
//                StringBuilder sbAnswer = new StringBuilder();
//                sbAnswer.append(TextUtils.join("|", qa.getAnswerList()));
//
//                if (qa.getType().equalsIgnoreCase(QUESTION_TYPE.BINARY) && sbAnswer.toString() != null && sbAnswer.toString().trim().length() > 0) {
//                    img = FileOperaion.decodeImageFile(sbAnswer.toString(), 100);
//                    continue;
//                }
//            }}
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "</head>" +
                " <style>" +
                "        table {" +
                "            border-collapse: collapse;" +
                "            width: 90%;" +
                "            margin: auto;" +
                "        }" +
                "        th, td {" +
                "            border: 1px solid black;" +
                "            padding: 8px;" +
                "            text-align: center;" +
                "             font-size: 35px;" +
                "        }" +
                "    </style>" +
                "<body>");
        sb.append("<center>");
        sb.append("</br>");
        sb.append("</br>");
        sb.append("</br>");
        sb.append("</br>");
        sb.append("</br>");
        sb.append("</br>");
        sb.append("</br>");

        for (QuestionAnswer qa : qaArrList) {
            if (qa.getAnswerList() != null) {

//                if(qa.getQuestionName().equals("Medicine")){
//
//                    if(qa.getAnswerList().size()>0){
//                        for(int i = 0 ; i<qa.getAnswerList().size();i++){
//                            Log.d(TAG, "setQuestionSaveLayout: ........the value is "+ qa.getAnswerList().get(i));
//                        }
//                    }
//                }

                StringBuilder sbAnswer = new StringBuilder();
                sbAnswer.append(TextUtils.join("|", qa.getAnswerList()));


                if (qa.getType().equalsIgnoreCase(QUESTION_TYPE.BINARY) && sbAnswer.toString() != null && sbAnswer.toString().trim().length() > 0) {
                    img = FileOperaion.decodeImageFile(sbAnswer.toString(), 100);
                    continue;
                } else if (qa.getType().equalsIgnoreCase(QUESTION_TYPE.PRESCRIPTION) && sbAnswer.toString() != null && sbAnswer.toString().trim().length() > 0) {

                    sb.append("<font face=\"Arial, sans-serif\" size=\"14\">");
                    sb.append("[" + qa.getOrder() + "]" + qa.getQuestionCaption());
                    sb.append("</font>");
                    sb.append("</br>");
                    sb.append("</br>");
                    sb.append("</br>");
                    sb.append(" <table>\n" +
                            "        <tr>\n" +
                            "            <th class=\"large-font\">Name</th>\n" +
                            "            <th class=\"large-font\"> Quantity</th>\n" +
                            "            <th class=\"large-font\">Doses</th>\n" +
                            "            <th class=\"large-font\">Days</th>\n" +
                            "            <th class=\"large-font\">Taking Time</th>\n" +
                            "        </tr>\n");
                    try {
                        //Log.d(TAG, "showAllAnswer: ...the actual array is "+qa.getAnswerList());
                        JSONArray medicineList = new JSONArray("" + qa.getAnswerList());

                        // JSONObject jsonObject=array.getJSONObject(0);
//                      Log.d(TAG, "showAllAnswer: ...the jsn object is "+jsonObject.getString("MED_NAME"));
//                      String namee=jsonObject.getString("MED_NAME");


                        for (int i = 0; i < medicineList.length(); i++) {
                            JSONObject jsonObject = medicineList.getJSONObject(i);
                            String name = JSONParser.getString(jsonObject, "MED_NAME");
                            Log.d(TAG, "showAllAnswer: ....the json obj is " + jsonObject);
                            sb.append("<tr>");
                            sb.append("<td>");
                            sb.append(name);
                            sb.append("</td>");
                            String medQuantity = JSONParser.getString(jsonObject, "MED_QTY");
                            sb.append("<td>");
                            sb.append(medQuantity);
                            sb.append("</td>");
                            String doses = JSONParser.getString(jsonObject, "DOSES");
                            if (!doses.equals("null")) {
                                Log.d(TAG, "showAllAnswer: ......the does is " + doses);
                                sb.append("<td>");
                                sb.append(doses);
                                sb.append("</td>");

                            } else {
                                // Log.d(TAG, "showAllAnswer: ......the does is "+doses);
                                sb.append("<td>");
                                sb.append("");
                                sb.append("</td>");
                            }


                            String days = JSONParser.getString(jsonObject, "DAYS");
                            if (!days.equals("null")) {
                                sb.append("<td>");
                                sb.append(days);
                                sb.append("</td>");
                            } else {
                                sb.append("<td>");
                                sb.append("");
                                sb.append("</td>");
                            }


                            String takingTime = JSONParser.getString(jsonObject, "TAKINGTIME");
                            if (!takingTime.equals("null")) {
                                sb.append("<td>");
                                sb.append(takingTime);
                                sb.append("</td>");
                            } else {
                                sb.append("<td>");
                                sb.append("");
                                sb.append("</td>");
                            }
                            sb.append("</tr>");
                        }
                    } catch (Exception e) {
                    }
                    sb.append("</table>");
                    sb.append("</br>");
                    sb.append("</br>");
                    sb.append("</br>");
                }


                if (sbAnswer.toString().trim().equalsIgnoreCase(""))
                    continue;

                if (!qa.getType().equalsIgnoreCase(QUESTION_TYPE.PRESCRIPTION) && sbAnswer.toString() != null && sbAnswer.toString().trim().length() > 0) {
                    sb.append("<font face=\"Arial, sans-serif\" size=\"14\" >");
                    sb.append("[" + qa.getOrder() + "]" + qa.getQuestionCaption());
                    sb.append("</font>");
                    sb.append("</br>");
                    sb.append("<font face=\"Arial, sans-serif\" size=\"10\" color=\"#808080\">");
                    sb.append(sbAnswer.toString());
                    sb.append("</font>");
                    sb.append("</br>");
                    sb.append("</br>");
                    sb.append("</br>");

                }
                //  sb.append("\n");

                if (qa.getQuestionName().equals("B_DOB")) {
                    try {
                        sb.append("<font face=\"Arial, sans-serif\" size=\"10\" color=\"#808080\">");
                        sb.append(" (" + Utility.getAge(sbAnswer.toString()) + ")");
                        sb.append("</font>");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //  sb.append("\n\n");

            }
        }
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView answerShowDilog = new DialogView(this, R.string.filled_answer, sb.toString(), img, buttonMap);
        // answerShowDilog.show();

//        sb.append("\n" +
//                "    <p>\n" + sb.toString() + "</p>"+
        sb.append("</center>");
        sb.append("</body>" +
                "</html>");

        answerShowDilog.setMessage(sb.toString());
        answerShowDilog.showAnswerWebView(this);
        //  answerShowDilog.setMessage("<p>"+sb.toString()+"</p>");
        // answerShowDilog.showWebView();
    }


    /**
     * Get next question view depend on the answer and set it to question view layout.
     */
    private void gotoNextQuestion() {
        if (currentQuestion == null) {
            return;
        }

        // if current question contain component view get its output and put in questionAnswerList by component key
        if (currentQuestion.getComponent() != null && currentQuestion.getComponent().getValues() != null) {
            QuestionAnswer qa = new QuestionAnswer();
            qa.setQuestionKey(currentQuestion.getComponent().getKey());
            qa.setType("component");
            qa.setQuestionName(currentQuestion.getComponent().getName());
            qa.setQuestionCaption(currentQuestion.getComponent().getTitle());
            qa.setOrder(questionOrder);
            qa.setSavable(false);
            qa.setAnswerList(currentQuestion.getComponent().getValues());
            questionOrder++;
            questionAnswerList.put(currentQuestion.getComponent().getKey(), qa);
        }

        if (questionView.isValid(false)) {
            interviewBinding.btnNext.setEnabled(false);

            final QuestionAnswer questionAnswer = new QuestionAnswer();
            questionAnswer.setQuestionKey(currentQuestion.getKey().replace("question", ""));
            questionAnswer.setType(currentQuestion.getType());
            questionAnswer.setQuestionName(currentQuestion.getName());
            questionAnswer.setMediaType(currentQuestion.getMediaType());
            questionAnswer.setQuestionCaption(currentQuestion.getCaption());
            questionAnswer.setHidden(currentQuestion.isHidden());
            questionAnswer.setSavable(currentQuestion.isSavable());
            questionAnswer.setAdded(false);
            questionAnswer.setOrder(questionOrder);
            questionOrder++;

            if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.SELECT) || currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.REFERRAL_CENTER)) {
                /*
                 *  Get User data from view and save to question object
                 */
                ArrayList<String> userInput = new ArrayList<String>();
                userInput.add(questionView.getInputData());

                currentQuestion.setUserInput(userInput);
                questionAnswer.setAnswerList(userInput);

                if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.REFERRAL_CENTER)) {
                    try {
                        referralCenterId = Integer.parseInt(questionView.getInputData());
                    } catch (Exception e) {

                    }

                }

                if (dlog != null && dlog.isShowing()) dlog.dismiss();
                dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                new GetAndShowQuestionTask(userInput, questionAnswer).execute();
            } else if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.STRING) || currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.NUMBER) || currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.LOCATION)) {
                String val = questionView.getInputData();
                ArrayList<String> userInput = new ArrayList<String>();

                userInput.add(val);
                currentQuestion.setUserInput(userInput);

                /*
                 *  Concat FCM code before hh number
                 */
                if (currentQuestion.getName().equals("HH_NO")) {
                    ArrayList<String> hhNumber = new ArrayList<String>();
                    hhNumber.add(App.getContext().getUserInfo().getUserCode() + val);
                    questionAnswer.setAnswerList(hhNumber);
                } else {
                    questionAnswer.setAnswerList(userInput);
                }

                try {
                    InputMethodManager inputManager = (InputMethodManager) InterviewActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(questionView.getInputView().getWindowToken(), 0);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }


                // close interview if user input is <CANCEL_INTERVIEW>
                if (userInput.get(0).equals("<CANCEL_INTERVIEW>")) {
                    finish();
                }
                if (!currentQuestion.isHidden()) {
                    dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                }
                // dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                if (currentQuestion.getName().equals(Constants.QUESTIONNAIRE_IMAGE_FLAG)) {
                    new GetAndShowQuestionTask("", questionAnswer).execute();
                } else {
                    new GetAndShowQuestionTask(userInput, questionAnswer).execute();
                }

                //new GetAndShowQuestionTask(userInput, questionAnswer).execute();
            } else if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.BINARY)) {
                ArrayList<String> userInput = new ArrayList<String>();

                String userInputStr = questionView.getInputData();
                userInput.add(userInputStr);
                currentQuestion.setUserInput(userInput);
                questionAnswer.setAnswerList(userInput);

                dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                new GetAndShowQuestionTask("", questionAnswer).execute();
            } else if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.DATE)) {
                Log.e("Date", questionView.getInputData());

                ArrayList<String> userInput = new ArrayList<String>();
                userInput.add(questionView.getInputData());
                currentQuestion.setUserInput(userInput);
                questionAnswer.setAnswerList(userInput);

                if (currentQuestion.getName().contains("NEXT_FOLLOWUP_DATE")) {
                    try {
                        nextFollowupDate = Utility.getMillisecondFromDate(questionView.getInputData(), Constants.DATE_FORMAT_YYYY_MM_DD);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                new GetAndShowQuestionTask("", questionAnswer).execute();
            } else if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.TIME)) {
                Log.e("Time", questionView.getInputData());
                ArrayList<String> userInput = new ArrayList<String>();
                userInput.add(questionView.getInputData());
                currentQuestion.setUserInput(userInput);
                questionAnswer.setAnswerList(userInput);

                dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                new GetAndShowQuestionTask("", questionAnswer).execute();
            } else if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.PRESCRIPTION)) {
                final ArrayList<String> userInput = (ArrayList<String>) questionView.getInputDataList();
                currentQuestion.setUserInput(userInput);
                questionAnswer.setAnswerList(userInput);

                if (!currentQuestion.isHidden()) {
                    HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
                    buttonMap.put(1, R.string.btn_ok);
                    buttonMap.put(2, R.string.btn_previous_question);
                    DialogView dialog = new DialogView(this, R.string.dialog_title, "", R.drawable.information, buttonMap);
                    dialog.setOnDialogButtonClick(new OnDialogButtonClick() {

                        @Override
                        public void onDialogButtonClick(View view) {
                            switch (view.getId()) {
                                case 1:

                                    dlog = ProgressDialog.show(InterviewActivity.this, "", getResources().getString(R.string.please_wait));

                                    /*
                                     *  Prescription. Do not pass any user input just change the value of option item by user input
                                     */
                                    prescription = userInput;

                                    if (currentQuestion.isAddMedicine()) {
                                        ArrayList<QuestionOption> options = new ArrayList<QuestionOption>();
                                        for (int l = 0; l < currentQuestion.getUserInput().size(); l++) {
                                            QuestionOption qo = new QuestionOption();
                                            qo.setId(l + 1);
                                            qo.setValue(currentQuestion.getUserInput().get(l));
                                            qo.setCaption(currentQuestion.getUserInput().get(l));
                                            qo.setOptionName("option" + l + 1);
                                            options.add(qo);
                                        }
                                        currentQuestion.setOptionList(options);
                                    } else {
                                        ArrayList<QuestionOption> options = currentQuestion.getOptionList();
                                        for (int l = 0; l < currentQuestion.getUserInput().size(); l++) {
                                            options.get(l).setValue(currentQuestion.getUserInput().get(l));
                                        }

                                    }

                                    new GetAndShowQuestionTask("", questionAnswer).execute();
                                    break;
                                case 2:
                                    InterviewActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    break;
                            }
                        }
                    });
                    dialog.showPrescriptionConfermation(userInput);

                } else {

                    new GetAndShowQuestionTask("", questionAnswer).execute();
                }


            } else if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.MULTI_SELECT)) {

                ArrayList<String> userInput = (ArrayList<String>) questionView.getInputDataList();
                currentQuestion.setUserInput(userInput);
                questionAnswer.setAnswerList(userInput);


                if (currentQuestion.getName().contains("DOCTOR_REFERRAL")) {
                    dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                    needDoctorReferral = true;
                    questionsToBeAdded = App.getContext().getQuestionManager().getRemainingQuestionList();
                    showQuestion(null, true);
                    dlog.dismiss();
                } else {
                    dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                    new GetAndShowQuestionTask(userInput, questionAnswer).execute();
                }
            } else if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.LIST)) {
                ArrayList<String> userInput = (ArrayList<String>) questionView.getInputDataList();
                currentQuestion.setUserInput(userInput);
                questionAnswer.setAnswerList(userInput);

                if (currentQuestion.getName().contains("DOCTOR_REFERRAL")) {
                    dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                    needDoctorReferral = true;
                    questionsToBeAdded = App.getContext().getQuestionManager().getRemainingQuestionList();
                    showQuestion(null, true);
                    dlog.dismiss();
                } else {
                    dlog = ProgressDialog.show(this, "", getResources().getString(R.string.please_wait));
                    new GetAndShowQuestionTask("", questionAnswer).execute();
                }
            }
            //Log.e("Question Name", questionAnswer.getQuestionName()+"  "+currentQuestion.getKey());

            interviewBinding.btnNext.setEnabled(true);
        }
    }


    /**
     * go to previous question.
     */
    private void gotoPreviousQuestion() {

        /*
         *  Set data to invalid state because user cancel the question by go to previous question
         */
        if (currentQuestion != null && currentQuestion.getType().equals(QUESTION_TYPE.PRESCRIPTION)) {
            prescription = null;
        } else if (currentQuestion != null && currentQuestion.getName().contains("DOCTOR_REFERRAL")) {
            needDoctorReferral = false;
        } else if (currentQuestion != null && currentQuestion.getType().equals(QUESTION_TYPE.REFERRAL_CENTER)) {
            referralCenterId = -1;
        } else if (currentQuestion.getName().contains("NEXT_FOLLOWUP_DATE")) {
            nextFollowupDate = -1;
        } else if (currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.STRING) || currentQuestion.getType().equalsIgnoreCase(QUESTION_TYPE.NUMBER)) {

            try {
                InputMethodManager inputManager = (InputMethodManager) InterviewActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(questionView.getInputView().getWindowToken(), 0);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }
        Question question = App.getContext().getQuestionManager().getPrevQuestion();
        showQuestion(question, false);
    }


    private void showQuestion(Question question, boolean isFromNext) {
        // in the case of null question interview is complete,so set interview saved layout to screen.
        if (question == null) {
            setQuestionSaveLayout();
            return;
        }


        // Do the need to show question view if user wants to see previous question from first question view
        if (currentQuestion != null && currentQuestion.getKey().equalsIgnoreCase(question.getKey()) && isQuestionLayoutDisplaying && !isFromNext)
            return;

        // for first time when interview start set question layout container
        if (!isQuestionLayoutDisplaying)
            setQuestionLayoutContainer();

        /**
         * if this question is last then button next is red
         * if this question is either last or not that depend on user input then button next is yellow
         * or any other case next button is
         */
        if (question.isMaybeLastVisible()) {
            interviewBinding.btnPrev.setBackgroundResource(R.drawable.btn_bg_yellow);
        } else if (question.isLastVisible()) {
            interviewBinding.btnNext.setBackgroundResource(R.drawable.btn_bg_red);
        } else {
            interviewBinding.btnNext.setBackgroundResource(R.drawable.app_button);
        }


        // for expression part
        String expression = question.getExpression();
        boolean isCacheable = false;
        boolean isreplaceBody = false;


        if (expression != null && !expression.trim().equalsIgnoreCase("")) {
            expression = expression.trim();
            if (expression.trim().startsWith("<CACHE>")) {
                expression = expression.replace("<CACHE>", "");
                isCacheable = true;
            } else if (expression.trim().startsWith("<REPLACE_BODY>")) {
                expression = expression.replace("<REPLACE_BODY>", "");
                isreplaceBody = true;
            }


            ReflectionManager reflectionManager = new ReflectionManager(this, expression, beneficiaryInfo, questionAnswerList);
            try {
                if (isCacheable && question.getUserInput() != null && question.getUserInput().size() > 0) {
                    if (question.getName().startsWith("SCHEDULE_CARD")) {
                        question.setUserInput(reflectionManager.getExpressionAnswer());
                    }
                } else if (expression.contains(".addComponent(")) {
                    /*
                     * if the method name of reflectionUtils is "addComponent" and it's answer contains value
                     * then get component from  ComponentFectory by answer
                     * set component to question
                     * */

                    if (reflectionManager.getExpressionAnswer() != null && reflectionManager.getExpressionAnswer().size() == 1) {
                        Component component = ComponentFectory.getComponentByExpression(reflectionManager.getExpressionAnswer().get(0), this);
                        if (component != null) {
                            question.setComponent(component);
                        }
                    } else if (reflectionManager.getExpressionAnswer() != null && reflectionManager.getExpressionAnswer().size() == 4
                            && reflectionManager.getExpressionAnswer().get(0).equals(QUESTION_TYPE.IMAGE)) {
                        question.setDefaultValue(reflectionManager.getExpressionAnswer());
                    }
                } else {
                    if (question.getType().equalsIgnoreCase(QUESTION_TYPE.REFERRAL_CENTER)) {
                        String methodName = TextUtility.getMethodName(expression);
                        if (methodName.equalsIgnoreCase("getReferralCenterList")) {
                            question.setReferralCenterList(reflectionManager.getExpressionAnswerForReferralCenter());
                        }
                    }
                    question.setUserInput(reflectionManager.getExpressionAnswer());
                }

                ///  use for replace body
                if (isreplaceBody && question.getUserInput() != null && question.getUserInput().size() > 0) {
                    replaceBody(question);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isCacheable = true;
        }

        // if already question view created then not need to recreate
        if (question.getUI() != null && isCacheable) {
            questionView = question.getUI();
        } else {
            questionView = QuestionViewFactory.createQuestionView(this, question, questionAnswerList, InterviewActivity.this, expression);
            question.setUI(questionView);
        }


        // set question as current question
        currentQuestion = question;

        // remove all view under question container
        interviewBinding.llQuestionView.removeAllViews();

        // remove parent if exist
        LinearLayout ll = (LinearLayout) questionView.getParent();
        if (ll != null) ll.removeView(questionView);


        // remove question answer if it already added.
        if (questionAnswerList.containsKey(currentQuestion.getKey().replace("question", "q"))) {
            questionOrder--;
            questionAnswerList.remove(currentQuestion.getKey().replace("question", "q"));
        }


        /**
         * if any visible question before this question then previous button  is blue and enable it
         * otherwise previous button is gray  and disable  it
         */
        if (!Utility.haveAnyVisibleQuestion(questionAnswerList)) {
            interviewBinding.btnPrev.setEnabled(false);
            interviewBinding.btnPrev.setBackgroundResource(R.drawable.btn_bg_gray);
        } else if (isDoctorFeedback && currentQuestion.isFirstVisible()) {
            interviewBinding.btnPrev.setEnabled(false);
            interviewBinding.btnPrev.setBackgroundResource(R.drawable.btn_bg_gray);
        } else {
            interviewBinding.btnPrev.setBackgroundResource(R.drawable.app_button);
            interviewBinding.btnPrev.setEnabled(true);
        }

        // in the case of not hidden add the question into question container and open keyboard when question is text or number input type
        // otherwise either goto next question when next button click or goto previous question when previous button click
        if (!currentQuestion.isHidden()) {
            questionView.setTag(question.getKey());
            interviewBinding.llQuestionView.addView(questionView);

            if ((questionView instanceof EditTextView || questionView instanceof AutoCompleteEditTextView) && !currentQuestion.isReadonly()) {
                View view = questionView.getInputView();
                view.requestFocus();
                try {
                    InputMethodManager inputManager = (InputMethodManager) InterviewActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(view, 0);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } else {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            if (isFromNext) {
                gotoNextQuestion();
            } else {
                gotoPreviousQuestion();
            }
        }
    }

    private void showQuestionSingleForm(Question question, String replaceName) {

        final QuestionAnswer questionAnswer = new QuestionAnswer();
        questionAnswer.setQuestionKey(question.getKey().replace("question", ""));
        questionAnswer.setType(question.getType());
        questionAnswer.setQuestionName(question.getName());
        questionAnswer.setMediaType(question.getMediaType());
        questionAnswer.setQuestionCaption(question.getCaption());
        questionAnswer.setHidden(question.isHidden());
        questionAnswer.setSavable(question.isSavable());
        questionAnswer.setAdded(false);
        questionAnswer.setOrder(questionOrder);
        questionOrder++;
        questionAnswerList.put(question.getKey().replace("question", "q"), questionAnswer);

        // for expression part
        String expression = question.getExpression();
        boolean isCacheable = false;
        boolean isreplaceBody = false;
        if (expression != null && !expression.trim().equalsIgnoreCase("")) {
            expression = expression.trim();
            if (expression.trim().startsWith("<CACHE>")) {
                expression = expression.replace("<CACHE>", "");
                isCacheable = true;
            } else if (expression.trim().startsWith("<REPLACE_BODY>")) {
                expression = expression.replace("<REPLACE_BODY>", "");
                isreplaceBody = true;
            }
            ReflectionManager reflectionManager = new ReflectionManager(this, expression, beneficiaryInfo, questionAnswerList);
            if (!expression.contains(".addComponent(")) {
                try {
                    questionAnswer.setAnswerList(reflectionManager.getExpressionAnswer());
                    questionAnswerList.put(question.getKey().replace("question", "q"), questionAnswer);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

                questionAnswerList.put(question.getKey().replace("question", "q"), questionAnswer);
            }
            reflectionManager = new ReflectionManager(this, expression, beneficiaryInfo, questionAnswerList);
            try {
                if (isCacheable && question.getUserInput() != null && question.getUserInput().size() > 0) {
                    if (question.getName().startsWith("SCHEDULE_CARD")) {
                        question.setUserInput(reflectionManager.getExpressionAnswer());
                    }
                } else if (expression.contains(".addComponent(")) {
                    /*
                     * if the method name of reflectionUtils is "addComponent" and it's answer contains value
                     * then get component from  ComponentFectory by answer
                     * set component to question
                     * */

                    if (reflectionManager.getExpressionAnswer() != null && reflectionManager.getExpressionAnswer().size() == 1) {
                        Component component = ComponentFectory.getComponentByExpression(reflectionManager.getExpressionAnswer().get(0), this);
                        if (component != null) {
                            question.setComponent(component);
                        }
                    } else if (reflectionManager.getExpressionAnswer() != null && reflectionManager.getExpressionAnswer().size() == 4
                            && reflectionManager.getExpressionAnswer().get(0).equals(QUESTION_TYPE.IMAGE)) {
                        question.setDefaultValue(reflectionManager.getExpressionAnswer());
                    }
                } else {
                    if (question.getType().equalsIgnoreCase(QUESTION_TYPE.REFERRAL_CENTER)) {
                        String methodName = TextUtility.getMethodName(expression);
                        if (methodName.equalsIgnoreCase("getReferralCenterList")) {
                            question.setReferralCenterList(reflectionManager.getExpressionAnswerForReferralCenter());
                        }
                    }
                    question.setUserInput(reflectionManager.getExpressionAnswer());

                }

                ///  use for replace body
                if (isreplaceBody && question.getUserInput() != null && question.getUserInput().size() > 0) {
                    replaceBody(question);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isCacheable = true;
        }


        // if already question view created then not need to recreate
        if (question.getUI() != null && isCacheable) {
            questionView = question.getUI();
        } else {
            questionView = QuestionViewFactory.createQuestionView(this, question, questionAnswerList, InterviewActivity.this, "");
            question.setUI(questionView);
        }
//        questionView = QuestionViewFactory.createQuestionView(this, question, questionAnswerList);
//        question.setUI(questionView);

        // set question as current question
        //    currentQuestion = question;

        // remove all view under question container
//        interviewBinding.llQuestionView.removeAllViews();

        // remove parent if exist
        LinearLayout ll = (LinearLayout) questionView.getParent();
        if (ll != null) ll.removeView(questionView);


        // remove question answer if it already added.
//        if (questionAnswerList.containsKey(currentQuestion.getKey().replace("question", "q"))) {
//            questionOrder--;
//            questionAnswerList.remove(currentQuestion.getKey().replace("question", "q"));
//        }
        if (!question.isHidden()) {
            interviewBinding.llQuestionView.addView(questionView);

//            if (replaceName.equalsIgnoreCase("")){
//                interviewBinding.llQuestionView.addView(questionView);
//            }else{
//                int childCount = interviewBinding.llQuestionView.getChildCount();
//                for (int i = 0; i < childCount; i++) {
//                    View childView = interviewBinding.llQuestionView.getChildAt(i);
//                    Object tag = childView.getTag(); // Retrieve the tag associated with the child view
//                    if (tag != null) {
//                        String tagString = tag.toString();
//                        // Do something with the tag, e.g., compare it or use it for some other logic
//                        if (tagString.equals("specificTag")) {
//                            // Perform actions for the specific tag
//                        }
//                    }
//
//                }
//
////                View oldView =findViewWithTag(interviewBinding.llQuestionView,question.getName());
////                int index = interviewBinding.llQuestionView.indexOfChild(oldView);
////                interviewBinding.llQuestionView.removeView(oldView);
//
//               // interviewBinding.llQuestionView.addView(questionView, 2);
//            }

//            if ((questionView instanceof EditTextView || questionView instanceof AutoCompleteEditTextView) && !question.isReadonly()) {
//                View view = questionView.getInputView();
//                view.requestFocus();
//                try {
//                    InputMethodManager inputManager = (InputMethodManager) InterviewActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputManager.showSoftInput(view, 0);
//                } catch (Exception exception) {
//                    exception.printStackTrace();
//                }
//            } else {
//                this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//            }
        }
        if ((question.getUI() instanceof RadioGroupView)) {
            RadioGroup button = (RadioGroup) question.getUI().getInputView();
            button.setOnCheckedChangeListener((group, checkedId) -> {
                try {
                    ArrayList<QuestionBranch> list = question.getBranchList();
                    if (list.size() > 1) {
                        interviewBinding.svBody.fullScroll(ScrollView.FOCUS_DOWN);
//                        currentQuestion = question;
//                       Question result = App.getContext().getQuestionManager().getNextQuestion((String) question.getUI().getInputData());
                        String currentKey = new QuestionManager().getNextQuestionKeyByValueForSingleForm(question, (String) question.getUI().getInputData());

                        for (QuestionBranch qb : list) {
                            if (!qb.getNextQuestionKey().equalsIgnoreCase(currentKey)) {
                                Question question1 = new QuestionManager().getQuestionByKeyForSingleForm(App.getContext().getQuestionManager().getQuestionList(), qb.getNextQuestionKey());
                                interviewBinding.llQuestionView.removeView(question1.getUI());
                                questionsListChanged.remove(question1);
                            } else {
                                Question questions = new QuestionManager().getQuestionByKeyForSingleForm(App.getContext().getQuestionManager().getQuestionList(), currentKey);

//                                    for (Question q:questionsListChanged) {
//                                        interviewBinding.llQuestionView.removeView(q.getUI());
//                                        questionsListChanged.remove(question);
//                                    }
                                questionsListChanged.add(questions);
                                showQuestionSingleForm(questions, "");
//                                    for (Question qtn : App.getContext().getQuestionManager().getQuestionList()) {
//                                        Question question = new QuestionManager().getQuestionByKeyForSingleForm(App.getContext().getQuestionManager().getQuestionList(), currentKey);
//                                        currentQuestion =question;
//                                        showQuestionSingleForm( App.getContext().getQuestionManager().getNextQuestion(""));
//
//                                    }


                            }

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        } else if ((question.getUI() instanceof ReferralCenterView)) {
            RadioGroup button = (RadioGroup) question.getUI().getInputView();
            button.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    ArrayList<QuestionBranch> list = question.getBranchList();
                    if (list.size() > 1) {
                        interviewBinding.svBody.fullScroll(ScrollView.FOCUS_DOWN);
//                        currentQuestion = question;
//                       Question result = App.getContext().getQuestionManager().getNextQuestion((String) question.getUI().getInputData());
                        String ctr = (String) question.getUI().getInputData();
                        String currentKey = new QuestionManager().getNextQuestionKeyByReferValueForSingleForm(question, ctr);

                        for (QuestionBranch qb : list) {
                            if (!qb.getNextQuestionKey().equalsIgnoreCase(currentKey)) {
                                Question question = new QuestionManager().getQuestionByKeyForSingleForm(App.getContext().getQuestionManager().getQuestionList(), qb.getNextQuestionKey());
                                interviewBinding.llQuestionView.removeView(question.getUI());
                                questionsListChanged.remove(question);

                            } else {
                                try {
                                    Question questions = new QuestionManager().getQuestionByKeyForSingleForm(App.getContext().getQuestionManager().getQuestionList(), currentKey);
                                    for (Question q : questionsListChanged) {
                                        interviewBinding.llQuestionView.removeView(q.getUI());
                                        questionsListChanged.remove(question);
                                    }
                                    showQuestionSingleForm(questions, "");
                                    questionsListChanged.add(questions);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                }
            });

        } else if ((question.getUI() instanceof DateView)) {

            if (question.getName().contains("DOB")) {
                Log.e("dob", "data");
            } else if (question.getName().contains("Delivery")) {
                DatePicker datePicker = (DatePicker) question.getUI().getInputView();
//                ArrayList<String> userInputs = new ArrayList<String>();
//                String datess = question.getUI().getInputData();
//                userInputs.add(datess);
//                currentQuestion.setUserInput(userInputs);
//                questionAnswer.setAnswerList(userInputs);
//
//
//                questionAnswerList.put(question.getKey().replace("question", "q"), questionAnswer);
                String currentKeyss = new QuestionManager().getNextQuestionKeyByValueForSingleForm(question, (String) question.getUI().getInputData());
                Question questionsss = new QuestionManager().getQuestionByKeyForSingleForm(App.getContext().getQuestionManager().getQuestionList(), currentKeyss);
                questionsListChanged.add(questionsss);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    datePicker.setOnDateChangedListener((DatePicker.OnDateChangedListener) (view, year, monthOfYear, dayOfMonth) -> {
                        ArrayList<String> userInput = new ArrayList<String>();
                        int dd = datePicker.getDayOfMonth();
                        int mm = datePicker.getMonth();
                        int yy = datePicker.getYear();
                        String dates = TextUtility.format("%d-%02d-%02d", yy, (mm + 1), dd);
                        userInput.add(dates);
                        currentQuestion.setUserInput(userInput);
                        questionAnswer.setAnswerList(userInput);
                        interviewBinding.llQuestionView.getTag();
                        String currentKey = new QuestionManager().getNextQuestionKeyByValueForSingleForm(question, (String) question.getUI().getInputData());
                        Question questions = new QuestionManager().getQuestionByKeyForSingleForm(App.getContext().getQuestionManager().getQuestionList(), currentKey);
                        questionAnswerList.put(question.getKey().replace("question", "q"), questionAnswer);
                        try {
                            for (Question q : questionsListChanged) {
                                interviewBinding.llQuestionView.removeView(q.getUI());
                                questionsListChanged.remove(question);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        showQuestionSingleForm(questions, "");
                    });
                }
            }

        } else if ((question.getUI() instanceof ListCheckGroupView)) {


        }
//        else
//        if (questionView instanceof EditTextView) {
//
//            EditText editTextValue = (EditText) questionView.getInputView();
//            editTextValue.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    questionView.setInputData("" + s.toString());
//                    ArrayList<String> userInput = new ArrayList<String>();
//                    String val = question.getUI().getInputData();
//                    userInput.add(val);
//                    getSelectQuestionAns(question,userInput);
//
//                    questionAnswerList.put(question.getKey().replace("question", "q"), questionAnswer);
////                    AppToast.showToastWarnaing(InterviewActivity.this,""+questionAnswer.getQuestionName());
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//
//
//        } else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.SELECT)) {
//            RadioGroup radioGroup = (RadioGroup) questionView.getInputView();
//            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup group, int checkedId) {
//                    try {
//                        RadioButton radioButton = (RadioButton) findViewById(checkedId);
//                        String caption = (String) radioButton.getText();
//                        String val = question.getUI().getInputData();
//                        question.getUI().setInputData(val);
//                        ArrayList<String> userInput = new ArrayList<String>();
//                        userInput.add(val);
//                        getSelectQuestionAns(question,userInput);
//                        try {
//                            questionAnswer.setAnswerList(userInput);
//                            questionAnswerList.put(question.getKey().replace("question", "q"), questionAnswer);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        AppToast.showToastWarnaing(InterviewActivity.this,""+questionAnswer.getQuestionName());
////                        userInput.add(val);
////                        Question qtn = App.getContext().getQuestionManager().getNextQuestion((String) radioButton.getText());
////                        AppToast.showToastWarnaing(InterviewActivity.this, "dd" + qtn.getCaption());
////                        Log.e("dddddddddddddddddddddddd",""+caption);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            });
//
//        } else if (question.getType().equalsIgnoreCase(QUESTION_TYPE.TIME)) {
//            ArrayList<String> userInput = new ArrayList<String>();
//            userInput.add(question.getUI().getInputData());
//
//            questionAnswerList.put(question.getKey().replace("question", "q"), questionAnswer);
////            questionAnswerList.get(key).setAnswerList(userInput);
//        }

        interviewBinding.llQuestionView.getChildCount();
        //int childCount = interviewBinding.llQuestionView.getChildCount();
        //  AppToast.showToastWarnaing(InterviewActivity.this,""+childCount+1);
//        View childView = interviewBinding.llQuestionView.getChildAt(childCount);
//        String tagValue = "specificTag"; // Set your desired tag value
//        childView.setTag(tagValue);
    }

    private void replaceBody(Question question) {

        question.setOptionList(new ArrayList<QuestionOption>());
        int qoId = 1;
        for (String data : question.getUserInput()) {
            String caption = "";
            String value = "";

            if (Utility.isJson(data)) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    caption = jsonObject.getString("CAPTION");
                    value = jsonObject.getString("VALUE");
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else if (Utility.isXml(data)) {
                //							try{
                //							     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                //							    DocumentBuilder builder = factory.newDocumentBuilder();
                //							    InputSource is = new InputSource(new StringReader(data));
                //							    Document document =builder.parse(is);
                //							    Node node =document.getChildNodes().item(0);
                //							    node.get
                //							}catch (Exception exception){
                //								exception.printStackTrace();
                //							}
            } else {
                caption = data;
                value = data + "___VALUE";
            }

            QuestionOption option = new QuestionOption();
            option.setId(qoId++);
            option.setCaption(caption);
            option.setValue(value);
            option.setOptionName("option" + option.getId());
            question.getOptionList().add(option);
        }

        if (question.getDefaultValue() != null && question.getDefaultValue().size() > 0 && question.getOptionList() != null && question.getOptionList().size() > 0) {
            question.getUserInput().clear();

            for (String dafultVal : question.getDefaultValue()) {
                for (QuestionOption option : question.getOptionList()) {
                    if (dafultVal.equals(option.getOptionName())) {
                        question.getUserInput().add(option.getValue());
                    }
                }
            }
        }

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
                           float velocityY) {

        if (maxDist > Constants.DISTANCE && isQuestionLayoutDisplaying) /// Swipe right to left
        {
            gotoNextQuestion();
        } else if (maxDist < -Constants.DISTANCE) /// Swipe left to right
        {
            if (interviewBinding.btnPrev.isEnabled()) {
                gotoPreviousQuestion();
            }
        }
        maxDist = 0;
        return true;
    }

    private View findViewWithTag(ViewGroup viewGroup, String tag) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals(tag)) {
                return child;
            } else if (child instanceof ViewGroup) {
                View foundView = findViewWithTag((ViewGroup) child, tag);
                if (foundView != null) {
                    return foundView;
                }
            }
        }
        return null;
    }

    public void getSelectQuestionAns(Question question, ArrayList<String> userInput) {
//        Question question = new Question();
        for (Question questions : questionsListRendering) {
            if (question.getName().equalsIgnoreCase(questions.getName())) {
                questions.setUserInput(userInput);
                break;
            }


        }
//        return  question;
    }


    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
//        if (Math.abs(distanceX) > Math.abs(maxDist))
//            maxDist = distanceX;
        return false;
    }


    @Override
    public void onShowPress(MotionEvent e) {

    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAPTURE_PHOTO) {
                if (questionView != null) {
                    String tempPath = CameraView.tempPath;
                    String realpath = ((CameraView) questionView).makeUri();
                    Utility.imageOrientation(tempPath);
                    FileOperaion.decodeAndSaveImageFile(tempPath, realpath, currentQuestion.getScaleTo(), currentQuestion.getQuality());
                    questionView.setInputData(realpath);
                }
            } else if (requestCode == Constants.REQUEST_CODE_RECORD_AUDIO) {
                questionView.setInputData(((AudioRecorderView) questionView).getTempPath());
            } else if (requestCode == 2001 && resultCode == RESULT_OK) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    GPSUtility.requestPermissions(InterviewActivity.this);
                    return;
                }
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
    }

    ///////// GPS Listeners//////////

    @Override
    public void onLocationChanged(Location arg0) {

    }

    @Override
    public void onProviderDisabled(String arg0) {

    }


    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }
    /////////////////////

    /**
     * The handler.
     */
    Handler handler = new Handler();

    /**
     * The runnable.
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new RetrieveDoctorFeedbackTask().execute();
        }
    };


    /**
     * Create referral center question.
     *
     * @return Return a question of type referral center
     */
    private Question createReferralCenterQuestion() {
        Question question = new Question();
        ArrayList<ReferralCenterInfo> referralCenterList = App.getContext().getDB().getReferralCenterList("STATE=1");
        question.setReferralCenterList(referralCenterList);
        question.setName("REFERRAL_CENTER");
        question.setType(QUESTION_TYPE.REFERRAL_CENTER);
        question.setReadonly(false);
        question.setRequired(true);
        question.setHint(getResources().getString(R.string.referral_center_question_hint));
        question.setCaption(getResources().getString(R.string.referral_center_question_title));
        return question;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.question_display_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_close:
                if (questionAnswerJson == null)
                    showExitPromptDialog();
                else
                    finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }

// Screen Off
//	int prevTimeoutTime = -1;
//	// Set screen timeout.
//	private void turnScreenOff()
//	{
//		prevTimeoutTime = Utility.getCurrentScreenTimeoutTime(this);
//		Utility.setScreenOffTimeoutTime(this, 500);
//	}
//
//	// Unlock screen and set screen timeout to previous value.
//	private void turnScreenOn()
//	{
//		playDoctorFeedAudio();
//		Utility.unlockScreen(this);
//		Utility.setScreenOffTimeoutTime(this, prevTimeoutTime);
//	}


    /**
     * Display alert dialog with one button.
     *
     * @param titleId        is the string id of dialog title
     * @param msg            will be displayed in the message section of the dialog
     * @param buttonTextId   is the string ID of the button text
     * @param imageId        is the image drawable id which will be displayed at the dialog's title
     * @param messageColorId the message color id
     * @param dialogType     is the dialog type which is used to take the decision which task will be run on dialog button click
     */
    private void showOneButtonDialog(int titleId, String msg, int buttonTextId, int imageId, int messageColorId, final int dialogType) {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, buttonTextId);
        DialogView exitDialog = new DialogView(this, titleId, systemMessage + msg, messageColorId, imageId, buttonMap);
        exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {

                if (dialogType == DIALOG_DOCTOR_REFERRAL) {
                    startRequestingForDoctorFeedback();
                } else if (dialogType == DIALOG_DATA_UPLOAD_SUCCESSFULL) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    InterviewActivity.this.finish();
                } else if (dialogType == EXIT_ON_OK) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(ActivityDataKey.PENDING_SYNC_PAGE, pendingSyncPage);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    InterviewActivity.this.finish();
                } else if (dialogType == UPLOAD_USER_REGISTRATION) {
                    /*
                     * Upload interview
                     */
                    if (questionAnswerJson != null || questionAnswerJson.length() > 0) {
                        dlog = ProgressDialog.show(InterviewActivity.this, getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));
                        new UploadAnswerJsonTask(questionAnswerJson).execute();
                    }
                }


            }
        });


        if (dialogType == DIALOG_DOCTOR_REFERRAL) {

            long doctorFeedbackDialogWaitTime = 20;
            try {
                doctorFeedbackDialogWaitTime = Long.parseLong(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "DOCTOR_CENTER", "doctor_feedback_dialog_wait_time"));
            } catch (MhealthException e) {
                e.printStackTrace();
            }

            exitDialog.setOnDialogDismissListener(new OnDialogDismissListener() {
                @Override
                public void OnDialogDismiss() {
                    startRequestingForDoctorFeedback();
                }
            });
            exitDialog.showTimerView(doctorFeedbackDialogWaitTime);
        } else {
            exitDialog.show();
        }


    }


    /**
     * Start the request for doctor feedback.
     */
    private void startRequestingForDoctorFeedback() {
        dlog = ProgressDialog.show(InterviewActivity.this, getResources().getString(R.string.retrieve_doctor_feedback), getResources().getString(R.string.please_wait));
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, doctorFeedbackRequestInterval);
        doctorFeedbackRequestStartTime = Calendar.getInstance().getTimeInMillis();
        requestNumber = 0;
    }

    /**
     * Display activity exit prompt dialog.
     */
    private void showExitPromptDialog() {
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_yes);
        buttonMap.put(2, R.string.btn_no);
        DialogView exitDialog = new DialogView(this, R.string.dialog_title, R.string.exit_from_question_display, R.drawable.warning, buttonMap);
        exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {

            @Override
            public void onDialogButtonClick(View view) {
                switch (view.getId()) {
                    case 1:
                        InterviewActivity.this.finish();
                        break;

                    default:
                        break;
                }


            }
        });
        exitDialog.show();
    }

    /*** Display alert dialog with three button.
     * @param titleId is the string ID of dialog title
     * @param msg will be displayed at the dialog message section
     * @param imageId is the drawable id of image which will be displayed at the dialog title
     */
    private void showDoctorFeedBackDialog(int titleId, String msg, int imageId, String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DoctorFeedbackFailedLayoutBinding doctorFailedBinding = DoctorFeedbackFailedLayoutBinding.inflate(getLayoutInflater());
        builder.setView(doctorFailedBinding.getRoot());

        doctorFailedBinding.tvDialogTitle.setText(titleId);
        doctorFailedBinding.tvDialogMessage.setText(msg);
        doctorFailedBinding.tvDialogMessage.setTextColor(Color.BLACK);
        doctorFailedBinding.imgDialogTitle.setImageResource(imageId);
        final AlertDialog dialog = builder.create();
        builder.setCancelable(false);
        doctorFailedBinding.btnTryAgain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

                startRequestingForDoctorFeedback();
            }
        });

        if ("Open".equalsIgnoreCase(status)) {
            doctorFailedBinding.btnReferralCenter.setVisibility(View.GONE);
        } else {
            doctorFailedBinding.btnReferralCenter.setVisibility(View.VISIBLE);
        }

        doctorFailedBinding.btnReferralCenter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();

                String questionnaireName = qList.getQuestionnaireName();
                int questionnaireId = qList.getQuestionnaireId();
                initializeVariables();

                /*
                 * Doctor's feedback not found and FCM select the referral center for patient.
                 * So Create referral center type question and display
                 */


                //	Mohammed Jubayer
                try {

                    isRemairPartInterview = true;
                    isDoctorFeedback = true;

                    String st = "{\"questionnaires\":[{\"QUESTIONNAIRE_DATA\":{\"questionnaire\":{\"question10000\":{\"defaultval\":\"1\",\"qname\":\"DOCTOR_FEEDBACK_LUNCHER\",\"validations\":[],\"hint\":\"\",\"caption\":\"\",\"qtype\":\"string\",\"hidden\":true,\"fvisible\":false,\"required\":false,\"readonly\":true,\"branches\":[{\"nextq\":\"question10005\",\"rule\":\"any\",\"value\":\"\"}],\"pos\":{\"left\":\"\",\"top\":\"\"},\"uinput\":\"\"},\"question10005\":{\"defaultval\":\"6\",\"qname\":\"MISSING_DOCTOR_FEEDBACK_REFERRAL\",\"qtype\":\"referralCenter\",\"caption\":\"" + getResources().getString(R.string.referral_center_question_title) + "\",\"hint\":\"  " + getResources().getString(R.string.referral_center_question_hint) + "  \",\"branchItems\":[1,2],\"required\":true,\"fvisible\":true,\"savable\":true,\"readonly\":false,\"branches\":[{\"calcValue\":\"\",\"nextq\":\"disconnect\",\"rule\":\"any\"}],\"uinput\":\"\",\"pos\":{\"left\":\"679px\",\"top\":\"391px\"},\"options\":[{\"id\":\"1\",\"value\":\"0\",\"caption\":\"0\"},{\"id\":\"2\",\"value\":\"0\",\"caption\":\"0\"}]}},\"parent\":\"question10000\"},\"STATE\":0,\"NAME\":\"Doctor_feedback_ref_center\",\"CATEGORY_ID\":0,\"TITLE\":\"Doctor_feedback_ref_center\",\"QUESTIONNAIRE_ID\":0}]}";
                    JSONObject jObj = new JSONObject(st);
                    JSONArray jQArray = jObj.getJSONArray("questionnaires");
                    qJson = jQArray.getJSONObject(0).toString();
                    new ParseQuestionnaireJSONTask().execute();
                } catch (JSONException e) {

                }


            }
        });

        doctorFailedBinding.btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                InterviewActivity.this.finish();

            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }


    /**
     * AsyncTask Class. Get the next question view from question manager
     */
    class GetAndShowQuestionTask extends AsyncTask<Void, Void, Question> {
        Object userInput;

        /**
         * Instantiates a new gets the and show question task.
         *
         * @param userInput the user input
         */
        public GetAndShowQuestionTask(Object userInput, QuestionAnswer questionAnswer) {
            this.userInput = userInput;
            questionAnswerList.put(currentQuestion.getKey().replace("question", "q"), questionAnswer);
        }

        @Override
        protected Question doInBackground(Void... params) {
            // android.os.Process.setThreadPriority( android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);

            if (userInput instanceof String) {
                return App.getContext().getQuestionManager().getNextQuestion((String) userInput);
            } else {
                return App.getContext().getQuestionManager().getNextQuestion((ArrayList<String>) userInput);
            }
        }


        @Override
        protected void onPostExecute(Question result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dlog.dismiss();
            showQuestion(result, true);
        }


        public void execute() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                super.execute();
            }
        }
    }


    /**
     * AsyncTask Class. Parse questionnaire JSON and set parent question in screen
     */
    class ParseQuestionnaireJSONTask extends AsyncTask<Void, Void, QuestionList> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }


        public void execute() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                super.execute();
            }
        }

        @Override
        protected QuestionList doInBackground(Void... params) {
            try {
                qList = JSONParser.parseQuestionListJSON(qJson);

                if (isDoctorFeedback) {
                    qList.setQuestionnaireId(currentQuestionList.getQuestionnaireId());
                    qList.setQuestionnaireName(currentQuestionList.getQuestionnaireName());
                    qList.setQuestionnaireTitle(currentQuestionList.getQuestionnaireTitle());
                } else if (qList != null) {
                    currentQuestionList = qList;
                }
                if (beneficiaryCode != null) {
                    beneficiaryInfo = App.getContext().getDB().getBeneficiaryInfo(beneficiaryCode, qList.getQuestionnaireName());
                    if (isGust && entryParams.size() >= 2) {
                        String guestInfo = entryParams.get(2);
                        if (guestInfo != null && guestInfo.split("#").length == 3) {
                            beneficiaryInfo.setDob(guestInfo.split("#")[0]);
                            beneficiaryInfo.setGender(guestInfo.split("#")[1]);
                            beneficiaryInfo.setMaritalStatus(guestInfo.split("#")[2]);
                            try {
                                beneficiaryInfo.setAge(Utility.getAge(beneficiaryInfo.getDob()));
                            } catch (ParseException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            Log.e("GUST : ", guestInfo);
                        }
                    }
                }
                // beneficiaryInfo.setUpdated(updateBenef);
            } catch (MhealthException e) {
                e.printStackTrace();
            }
            return qList;
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(QuestionList result) {
            super.onPostExecute(result);

            dlog.dismiss();

            if (result == null) {
                AppToast.showToast(InterviewActivity.this, R.string.error_retrieving_questionnaire);
                InterviewActivity.this.finish();
                return;
            }

            setQuestionLayoutContainer();


            /*
             *  Initialize question manager and create first question
             */
            QuestionManager qManager = new QuestionManager(result.getQuestionList(), result.getFirstQuestionKey());
            App.getContext().setQuestionManager(qManager);
            Log.d(TAG, "onPostExecute: .........the inflated q before add is @"+ App.getContext().getQuestionManager().getQuestionList().size());


            // for doctor referral
            if (questionsToBeAdded != null && questionsToBeAdded.size() > 0) {
                App.getContext().getQuestionManager().appendQuestionList(questionsToBeAdded);
                questionsToBeAdded = null;
            }

            // added by ashraf for update feedback through interview edit in diabetic algorithm
            Boolean isDoctorFeedbackForUpdate = false;
            PatientInterviewDoctorFeedback patientInterviewDoctorFeedback = new PatientInterviewDoctorFeedback();
            if (getIntent().getExtras().containsKey("isDoctorFeedback") &&
                    getIntent().getExtras().containsKey("isDoctorFeedbackForUpdate") &&
                    getIntent().getExtras().containsKey("patientInterviewDoctorFeedback")
            ) {
                isDoctorFeedback = getIntent().getExtras().getBoolean("isDoctorFeedback");
                isDoctorFeedbackForUpdate = getIntent().getExtras().getBoolean("isDoctorFeedbackForUpdate");
                patientInterviewDoctorFeedback = (PatientInterviewDoctorFeedback) getIntent().getSerializableExtra("patientInterviewDoctorFeedback");
            }

            if (isDoctorFeedback) {
                if (isDoctorFeedbackForUpdate == true) {
                    //PatientInterviewDoctorFeedback doctorFeedback = ModelProvider.getPatientInterviewDoctorFeedback(patientInterviewDoctorFeedback, questionAnswerList);
                    //App.getContext().getDB().updatePatientInterviewDoctorFeedback(patientInterviewDoctorFeedback);
                } else {
                    PatientInterviewDoctorFeedback doctorFeedback = ModelProvider.getPatientInterviewDoctorFeedback(new PatientInterviewDoctorFeedback(), questionAnswerList);
                    doctorFeedback.setInterviewId(interviewId);
                    doctorFeedback.setFeedbackDate(interviewStartTime);
                    doctorFeedback.setTransRef(Calendar.getInstance().getTimeInMillis());
                    App.getContext().getDB().savePatientInterviewDoctorFeedback(doctorFeedback);
                    App.getContext().getDB().savePatientInterviewDoctorFeedbackFromInstantDoctorCenter(pIDCFeedbackFromDoctorReffereal, 0, interviewId);
                }
            }
            // Display first question
            if (SINGLE_PG_FROM_VIEW.equals("1")) {
                interviewBinding.btnPrev.setText("" + InterviewActivity.this.getString(R.string.st_cancel));
                questionsListRendering.add(App.getContext().getQuestionManager().getFirstQuestion());
                Log.d(TAG, "onPostExecute: ......raw"+App.getContext().getQuestionManager().getQuestionList().size());
                for (Question qtn : App.getContext().getQuestionManager().getQuestionList()) {
                    currentQuestion = qtn;
                    questionsListRendering.add(App.getContext().getQuestionManager().getNextQuestion(""));



//                    questionsListRendering.add(currentQuestion);

                }

                Log.d(TAG, "onPostExecute: .........the inflated q is @"+questionsListRendering);
                for (Question question : questionsListRendering) {
                    if (question != null) {
                        // if current question contain component view get its output and put in questionAnswerList by component key
                        showQuestionSingleForm(question, "");
                        interviewBinding.svBody.fullScroll(ScrollView.FOCUS_UP);
                    }
                }
            } else {
                showQuestion(App.getContext().getQuestionManager().getFirstQuestion(), true);
            }

        }

    }

    /**
     * AsyncTask Class. Retrieve doctor feedback
     */
    private class RetrieveDoctorFeedbackTask extends AsyncTask<Void, Void, ResponseData> {

        @Override
        protected ResponseData doInBackground(Void... params) {
            String requestData = null;
            Log.e("Doctor Referral", "Requesting: " + requestNumber++);
            try {
                JSONObject jParamObj;
                if (interviewUploadResponse.getParam() != null)
                    jParamObj = new JSONObject(interviewUploadResponse.getParam());
                else
                    jParamObj = new JSONObject();

                if (needDoctorReferral) {
                    jParamObj.put(KEY.PRIORITY, Priority.DOCTOR_FEEDBACK);
                    jParamObj.put(KEY.PATIENT_ID, beneficiaryCode);
                }
                JSONObject jDataObj = new JSONObject();

                if (jParamObj.has(KEY.AFFECTED_ROW))
                    jDataObj.put(KEY.INTERVIEW_ID, jParamObj.get(KEY.AFFECTED_ROW));

                requestData = JSONCreateor.createRequestJson(InterviewActivity.this, RequestType.TRANSACTION, RequestName.DOCTOR_FEEDBACK, Constants.MODULE_BUNCH_PUSH, null, jDataObj, jParamObj);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            ResponseData doctorReferraResponse = null;
            if (requestData != null) {
                /*Make the server request from doctor feedback*/

                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                try {
                    entityBuilder.addBinaryBody("data", requestData.getBytes(HTTP.UTF_8));
                    doctorReferraResponse = APICommunication.makeWebRequest(InterviewActivity.this, entityBuilder, App.getContext().getTransectionAPI());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return doctorReferraResponse;
        }

        public void execute() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                super.execute();
            }
        }

        @Override
        protected void onPostExecute(ResponseData result) {
            super.onPostExecute(result);

            if (result == null) {

                dlog.dismiss();
                showDoctorFeedBackDialog(R.string.dialog_title, getResources().getString(R.string.server_not_available), R.drawable.error, "");
                return;
            }

            if (result.getResponseCode().equalsIgnoreCase("01")) {

                playDoctorFeedAudio();
                dlog.dismiss();
                try {
                    App.getContext().getDB().updatePatientInterViewMaster(interviewId, 1);
                    initializeVariables();

                    isDoctorFeedback = true;
                    Log.e("DoctorFeedback", result.getData());
                    JSONObject jObj = new JSONObject(result.getData());

                    JSONObject jParamObj = new JSONObject(result.getParam());


                    if (jParamObj.has(Column.DOC_FOLLOWUP_ID)) {
                        try {
                            PatientInterviewDoctorFeedback pDcFeedback = new PatientInterviewDoctorFeedback();
                            pDcFeedback.setDocFollowupId(JSONParser.getLong(jParamObj, Column.DOC_FOLLOWUP_ID));
                            pDcFeedback.setInterviewId(JSONParser.getLong(jParamObj, Column.INTERVIEW_ID));
                            pDcFeedback.setTransRef(JSONParser.getLong(jParamObj, Column.TRANS_REF));
                            try {
                                pDcFeedback.setFeedbackDate(Utility
                                        .getMillisecondFromDate(jParamObj.getString(
                                                        Column.FEEDBACK_DATE),
                                                Constants.DATE_FORMAT_YYYY_MM_DD));
                            } catch (ParseException e) {
                                pDcFeedback.setFeedbackDate(0);
                                e.printStackTrace();
                            }
                            pDcFeedback.setRefCenterId(JSONParser.getLong(jParamObj, Column.REF_CENTER_ID));
//							try {
//								if(jParamObj.getString(Column.NEXT_FOLLOWUP_DATE).isEmpty() || jParamObj.getString(Column.NEXT_FOLLOWUP_DATE)==null){
//									pDcFeedback.setNextFollowupDate(0);
//								}
//								else {
//									pDcFeedback.setNextFollowupDate(Utility
//											.getMillisecondFromDate(jParamObj.getString(
//													Column.NEXT_FOLLOWUP_DATE),
//													Constants.DATE_FORMAT_YYYY_MM_DD));
//								}
//
//							} catch (ParseException e) {
//								pDcFeedback.setNextFollowupDate(0);
//								e.printStackTrace();
//							}
                            if (jParamObj.has(Column.USER_ID) && !jParamObj.isNull(Column.USER_ID)) {
                                pDcFeedback.setUserId(JSONParser.getLong(jParamObj, Column.USER_ID));
                            }

//							if(jParamObj.getString(Column.FEEDBACK_RECEIVE_TIME)==null){
//								pDcFeedback.setFeedbackReceiveTime(0);
//							}
//							else {
//								try {
//									pDcFeedback.setFeedbackReceiveTime(Utility.getMillisecondFromDate(jParamObj.getString(Column.FEEDBACK_RECEIVE_TIME),Constants.DATE_FORMAT_YYYY_MM_DD));
//								} catch (ParseException e) {
//									e.printStackTrace();
//								}
//							}
//							pDcFeedback.setIsFeedbackOnTime(JSONParser.getLong(jParamObj,Column.IS_FEEDBACK_ON_TIME));
                            pDcFeedback.setDoctorFindings(JSONParser.getString(jParamObj, Column.DOCTOR_FINDINGS));
                            pDcFeedback.setPrescribedMedicine(JSONParser.getString(jParamObj, Column.PRESCRIBED_MEDICINE));
//							if(jParamObj.getString(Column.QUESTION_ANSWER_JSON)==null){
//								pDcFeedback.setQuestionAnsJson("");
//							}
//							else {
//								pDcFeedback.setQuestionAnsJson(jParamObj.getString(Column.QUESTION_ANSWER_JSON));
//							}
//							if(jParamObj.getString(Column.QUESTION_ANSWER_JSON2)==null){
//								pDcFeedback.setQuestionAnsJson2("");
//							}
//							else {
//								pDcFeedback.setQuestionAnsJson2(jParamObj.getString(Column.QUESTION_ANSWER_JSON2));
//							}
//							pDcFeedback.setFeedbackSource(JSONParser.getString(jParamObj,Column.FEEDBACK_SOURCE));
                            pDcFeedback.setMessageToFCM(JSONParser.getString(jParamObj, Column.MESSAGE_TO_FCM));
//							pDcFeedback.setInvesAdvice(JSONParser.getString(jParamObj,Column.INVES_ADVICE));
//							pDcFeedback.setInvesResult(JSONParser.getString(jParamObj,Column.INVES_RESULT));
//							pDcFeedback.setInvesStatus(JSONParser.getString(jParamObj,Column.INVES_STATUS));
                            pDcFeedback.setNotificationStatus(1);
                            pIDCFeedbackFromDoctorReffereal = pDcFeedback;
                            //App.getContext().getDB().savePatientInterviewDoctorFeedbackFromInstantDoctorCenter(pDcFeedback,0,interviewId);

                        } catch (Exception e) {
                            Log.e("FeedbackParseError", e.getMessage());
                        }


//						long docFollowUpId=JSONParser.getLong(jParamObj,Column.DOC_FOLLOWUP_ID);
//						long followUpId= App.getContext().getDB().updatePatientInterviewDoctorFeedback(docFollowUpId,interviewId);
                    }
                    JSONArray jQArray = jObj.getJSONArray("questionnaires");
                    qJson = jQArray.getJSONObject(0).toString();
                    new ParseQuestionnaireJSONTask().execute();
                } catch (JSONException e) {

                }
            } else if (result.getResponseCode().equalsIgnoreCase("03")) {
                if ((Calendar.getInstance().getTimeInMillis() - doctorFeedbackRequestStartTime) >= doctorFeedbackRequestwaittime) {

                    dlog.dismiss();

                    try {
                        JSONObject param = new JSONObject(result.getParam());
                        String status = param.getString("STATUS");
                        String msg = param.getString("DOCTOR_FEEDBACK_MESSAGE");
                        showDoctorFeedBackDialog(R.string.dialog_title, msg, R.drawable.error, status);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //showDoctorFeedBackDialog(R.string.dialog_title, getResources().getString(R.string.server_busy, ""), R.drawable.error, "");
                        showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.data_upload_successfull), R.string.btn_ok, R.drawable.information, Color.BLACK, DIALOG_DATA_UPLOAD_SUCCESSFULL);


                    }
                } else {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, doctorFeedbackRequestInterval);
                }
            } else {

                dlog.dismiss();
                showDoctorFeedBackDialog(R.string.dialog_title, getResources().getString(R.string.server_not_available), R.drawable.error, "");
            }
        }
    }

    /**
     * AsyncTask Class. Upload Question answer json to server along with binary data(e.g image, audio, video)
     */
    class UploadAnswerJsonTask extends AsyncTask<Void, Void, ResponseData> {
        JSONObject jsonData;

        /**
         * Instantiates a new upload answer json task.
         *
         * @param jsonData the json data
         */
        public UploadAnswerJsonTask(JSONObject jsonData) {
            this.jsonData = jsonData;
        }

        public void execute() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                super.execute();
            }
        }

        @Override
        protected ResponseData doInBackground(Void... params) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            Charset cs = Charset.forName("UTF-8");
            entityBuilder.setCharset(cs);
            filesMap = new HashMap<String, String>();
            String requestData = null;
            try {

                //// Prepare Param1///////////
                JSONObject jParamObj = new JSONObject();

                if (interviewUploadResponse != null && interviewUploadResponse.getParam() != null) {
                    jParamObj = new JSONObject(interviewUploadResponse.getParam());

                    if (jParamObj.has(KEY.AFFECTED_ROW))
                        jParamObj.put(KEY.INTERVIEW_ID, jParamObj.get(KEY.AFFECTED_ROW));
                } else {
                    jParamObj = new JSONObject();
                }


                Log.e("PARENT INTERVIEW ID ", parentInterviewId + "");
                if (parentInterviewId > 0) {
                    /** Get follow up date and attended date and add them to param*/
                    ScheduleInfo scheduleInfo = App.getContext().getDB().getScheduleInfo(parentInterviewId, qList.getQuestionnaireId());
                    if (scheduleInfo != null) {
                        jParamObj.put("SCHED_DATE", scheduleInfo.getScheduleDate());
                        jParamObj.put("ATTENDED_DATE", scheduleInfo.getAttendedDate());
                        jParamObj.put("PARENT_QUESTIONNAIRE_ID", scheduleInfo.getQuestionnaireId());
                    }
                    parentInterviewId = 0;
                    jParamObj.put("QUESTIONNAIRE_TYPE", "FOLLOW_UP");
                }

                if (needDoctorReferral) // If it is doctor referral then set the priority
                {
                    jParamObj.put(KEY.PRIORITY, Priority.DOCTOR_FEEDBACK);
                    jParamObj.put(KEY.PATIENT_ID, beneficiaryCode);

                } else if (referralCenterId > 0) {
                    jParamObj.put(KEY.PRIORITY, Priority.REFERRAL_CENTER);
                }

                // added by ashraf for set CALL HELPLINE PRIORITY
                if (helpLineNoList.size() > 0) // If it is doctor referral then set the priority
                {
                    jParamObj.put(KEY.PRIORITY, Priority.CALL_HELPLINE_PRIORITY);

                }
                // end condition by ashraf
                if (interviewId > 0) {
                    jParamObj.put("FCM_INTERVIEW_ID", interviewId);
                }

                String actionType;
                if (isDoctorFeedback || isRemairPartInterview) {
                    if (getIntent().getExtras().containsKey("isDoctorFeedbackForUpdate") &&
                            getIntent().getExtras().getBoolean("isDoctorFeedbackForUpdate") == true
                    ) {
                        actionType = RequestAction.INSERT;
                    } else {
                        actionType = RequestAction.UPDATE;
                    }


                } else {
                    if (qList.getQuestionnaireName().startsWith("BENEFICIARY_REGISTRATION") && interviewType.equals(ActivityDataKey.UPDATE_BENEF)) {
                        actionType = RequestAction.UPDATE;
                    } else {
                        actionType = RequestAction.INSERT;
                    }
                }

                requestData = JSONCreateor.createRequestJson(InterviewActivity.this, RequestType.TRANSACTION, RequestName.CASE_ENTRY, Constants.MODULE_INTERVIEW_UPLOADER, actionType, jsonData, jParamObj);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }


            try {
                entityBuilder.addBinaryBody("data", requestData.getBytes(HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            /*
             *  Check if there is any binary answer in the answer list
             */
            for (String key : questionAnswerList.keySet()) {
                if (questionAnswerList.get(key).getType().equalsIgnoreCase(QUESTION_TYPE.BINARY)) {
                    String filePath = questionAnswerList.get(key).getAnswerList().get(0);
                    String sendFiles = App.getContext().getDB().getSendFiles(interviewStartTime);
                    if (filePath != null && filePath.trim().length() > 0) {

                        File file = new File(filePath);
                        if (file.exists() && !sendFiles.contains(file.getName())) {
                            filesMap.put(file.getName(), file.getPath());
                            entityBuilder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
                        }
                    }
                }
            }

            ResponseData webResponseInfo = APICommunication.makeWebRequest(InterviewActivity.this, entityBuilder, App.getContext().getTransectionAPI());
            return webResponseInfo;
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ResponseData response) {
            super.onPostExecute(response);
            dlog.dismiss();
            interviewUploadResponse = response;

            if (response == null) {
                if (needDoctorReferral) {
                    showDoctorFeedBackDialog(R.string.dialog_title, getResources().getString(R.string.server_not_available), R.drawable.error, "");
                } else {
                    showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.server_not_available), R.string.btn_close, R.drawable.error, Color.RED, EXIT_ON_OK);
                }
                return;
            }


            if (response.getWebResponseStatusCode() == 200) {
                try {
                    JSONObject param = new JSONObject(response.getParam());
                    if (param != null && param.has("SAVED_FILES")) {
                        JSONArray savedFiles = param.getJSONArray("SAVED_FILES");
                        for (int index = 0; index < savedFiles.length(); index++) {
                            String fileName = savedFiles.getString(index);
                            String filePath = filesMap.get(fileName);
                            App.getContext().getDB().saveFileBank(fileName, filePath, interviewStartTime, 1);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (response.getResponseCode() != null && response.getResponseCode().equals("01")) {
                    /*
                     * Set the flag to indicate the data has been sent to server.
                     */
                    long affectedRow = 0;

                    try {
                        JSONObject pram = new JSONObject(response.getParam());
                        if (pram.has("AFFECTED_ROW")) {
                            affectedRow = pram.getLong("AFFECTED_ROW");
                        }

                        if (pram.has("OVER_CAPACITY_FLAG")) {
                            long overCapacityflag = pram.getLong("OVER_CAPACITY_FLAG");
                            App.getContext().getDB().getCcsDao().updateOverCapacityFlag(interviewStartTime, overCapacityflag);
                        }

                        if (pram.has("SYSTEM_MSG")) {
                            systemMessage = pram.getString("SYSTEM_MSG");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    App.getContext().getDB().updateInterviewSent(interviewId, parentInterviewId, "Y", affectedRow);

                    if (isDoctorFeedback && isDoctorFeedbackUploded) {
                        App.getContext().getDB().updatePatientInterviewDoctorFeedback(interviewId, 1, questionAnswerJson.toString());
                    }

                    if (qList.getQuestionnaireName().startsWith("BENEFICIARY_REGISTRATION")) {
                        try {
                            JSONObject jParamObj = new JSONObject(response.getParam());
                            Log.e("Param", jParamObj.toString());
                            if (jParamObj.has("BENEF_NAME_LOCAL")) {
                                String benefLocalName = jParamObj.getString("BENEF_NAME_LOCAL");
                                App.getContext().getDB().updateBenefLocalName(beneficiaryCode, benefLocalName);
                            }

                            if (jParamObj.has("GUARDIAN_NAME_LOCAL")) {
                                String guardianLocalName = jParamObj.getString("GUARDIAN_NAME_LOCAL");
                                App.getContext().getDB().updateGuardianLocalName(beneficiaryCode, guardianLocalName);
                            }

                            /*
                             *  Update data version
                             */
                            if (jParamObj.has(KEY.VERSION_NO_BENEFICIARY)) {
                                App.getContext().getDB().updateDataVersion(DBTable.BENEFICIARY, 1, 1, jParamObj, KEY.VERSION_NO_BENEFICIARY);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        App.getContext().getDB().updateMedicineConsumptionStatus(interviewId, -1, "Y");
                    }

                    if (needDoctorReferral) {
                        showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.data_upload_successfull) + " " + getResources().getString(R.string.request_for_doctor_feedback), R.string.btn_ok, R.drawable.warning, Color.BLACK, DIALOG_DOCTOR_REFERRAL);
                    } else {
                        if (qList.getQuestionnaireName().startsWith(QuestionnaireName.BENEFICIARY_REGISTRATION)) {
                            showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.data_upload_successfull) + "\n" + getResources().getString(R.string.beneficiary_code) + " " + beneficiaryCode.substring(beneficiaryCode.length() - 5), R.string.btn_ok, R.drawable.information, Color.BLACK, DIALOG_DATA_UPLOAD_SUCCESSFULL);
                        } else {
                            showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.data_upload_successfull), R.string.btn_ok, R.drawable.information, Color.BLACK, DIALOG_DATA_UPLOAD_SUCCESSFULL);
                        }

                    }
                } else {
                    if (needDoctorReferral) {
                        showDoctorFeedBackDialog(R.string.dialog_title, getResources().getString(R.string.server_not_available), R.drawable.error, "");
                    } else {
                        showOneButtonDialog(R.string.dialog_title, response.getErrorCode() + " " + response.getErrorDesc(), R.string.btn_close, R.drawable.error, Color.RED, EXIT_ON_OK);
                    }
                }
            } else {
                if (needDoctorReferral) {
                    showDoctorFeedBackDialog(R.string.dialog_title, getResources().getString(R.string.server_not_available), R.drawable.error, "");
                } else {
                    showOneButtonDialog(R.string.dialog_title, "Status Code: " + response.getWebResponseStatusCode() + " " + response.getErrorDesc() + "\n" + getResources().getString(R.string.interview_data_sending_failed), R.string.btn_close, R.drawable.error, Color.RED, EXIT_ON_OK);
                }

            }
        }
    }

    public String getEntryParam1() {
        try {
            return entryParams.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getEntryParams(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params != null && params.length == 1) {
            try {
                return entryParams.get(Integer.parseInt(params[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getParentInterviewFindings() {
        return App.getContext().getDB().getInterviewFinding(parentInterviewId);
    }

    public String getParentInterviewTransRef() {
        return App.getContext().getDB().getInterviewTransRef(parentInterviewId);
    }

    public String getFollowupDateInMillis() {
        ScheduleInfo scheduleInfo = App.getContext().getDB().getScheduleInfo(parentInterviewId, qList.getQuestionnaireId());
        if (scheduleInfo != null) {
            return scheduleInfo.getScheduleDate() + "";
        }
        return "";
    }


    /**
     * Save interview into database.
     *
     * @param isUpload is used to decide whether data will be upload to server after save into local database
     */

    private void saveInterview(final boolean isUpload) throws MhealthException {
        // Save Beneficiary if Questionnaire is Beneficiary Registration
        if (qList.getQuestionnaireName().startsWith(QuestionnaireName.BENEFICIARY_REGISTRATION)) {
            double longitude = 0;
            double latitude = 0;
            if (!GPSUtility.isGPSEnable(InterviewActivity.this)) {
                AppToast.showToast(this, "Please turn on GPS");
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    GPSUtility.requestPermissions(InterviewActivity.this);
                    return;
                }
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null && location.getTime() >= (Calendar.getInstance().getTimeInMillis() - 1 * 60 * 1000)) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            }

            if (householdWithBeneficiaryReg.equalsIgnoreCase("HOUSEHOLD_BENEFICIARY_REG")) {
                String hhNumber = ModelProvider.getHouseholdModel(new Household(), questionAnswerList).getHhNumber();
                long numberOfMember = ModelProvider.getBeneficiaryModel(new Beneficiary(), questionAnswerList).getHhFamilyMembers();
                String hhCharacter = ModelProvider.getBeneficiaryModel(new Beneficiary(), questionAnswerList).getHhCharacter();
                String hhName = ModelProvider.getBeneficiaryModel(new Beneficiary(), questionAnswerList).getHhName();
                long hhAdultWomen = ModelProvider.getBeneficiaryModel(new Beneficiary(), questionAnswerList).getHhAdultWomen();
                saveHouseHoldWithBeneficiaryRegistration(hhNumber, hhName, numberOfMember, hhCharacter, hhAdultWomen, latitude, longitude, ModelProvider.getBeneficiaryModel(new Beneficiary(), questionAnswerList).getMonthlyFamilyExpenditure(), isUpload);
            } else {
                saveBeneficiaryRegistration(isUpload);
            }


        } else if (beneficiaryCode != null && beneficiaryInfo instanceof MaternalInfo && qList.getQuestionnaireName().startsWith(QuestionnaireName.MATERNAL_PREGNANT_MOTHER_REGISTRATION)) {
            beneficiaryInfo = ModelProvider.getMaternalInfo((MaternalInfo) beneficiaryInfo, questionAnswerList);
            if (App.getContext().getDB().isLmpExist(MaternalInfo.MODEL_NAME, beneficiaryInfo.getBenefCode(), beneficiaryInfo.getLmp())) {
                HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
                buttonMap.put(1, R.string.btn_close);
                DialogView dialog = new DialogView(this, R.string.dialog_title, R.string.maternal_registration_exist, Color.RED, R.drawable.error, buttonMap);
                dialog.show();
            } else {
                saveInterviewIntoDatabaseAndLocalFile();
                ((MaternalInfo) beneficiaryInfo).setRegInterviewId(interviewId);
                saveSchedule();
                saveMaternalPregnantMotherRegistration((MaternalInfo) beneficiaryInfo);
                completeInterviewWithUploadAndMessage(isUpload);
            }
        } else if (beneficiaryCode != null && beneficiaryInfo instanceof MaternalInfo && qList.getQuestionnaireName().startsWith("MATERNAL_SERVICE")) {
            saveInterviewIntoDatabaseAndLocalFile();
//            if (maternalServiceId > 0) {
//                App.getContext().getDB().getMaternalDao().deleteMaternalService(maternalServiceId);
//            }
//            saveMaternalService();
            //  saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);

        } else if (beneficiaryCode != null && beneficiaryInfo instanceof MaternalInfo && qList.getQuestionnaireName().startsWith(QuestionnaireName.MATERNAL_MOTHER_AND_BABY_REGISTRATION)) {
            saveInterviewIntoDatabaseAndLocalFile();
            saveMotherAndBabyRegistration();
            saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);
        } else if (beneficiaryCode != null && beneficiaryInfo instanceof MaternalInfo && qList.getQuestionnaireName().startsWith(QuestionnaireName.MATERNAL_DELIVERY_CARE)) {
            saveInterviewIntoDatabaseAndLocalFile();
            saveMaternalDelivery();
            saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);
        } else if (beneficiaryCode != null && beneficiaryInfo instanceof MaternalInfo && qList.getQuestionnaireName().startsWith(QuestionnaireName.MATERNAL_HIGH_RISK_MOTHER_IDENTIFICATION)) {
            saveInterviewIntoDatabaseAndLocalFile();
            saveMatrnalHighrisk();
            saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);
        } else if (beneficiaryCode != null && qList.getQuestionnaireName().startsWith(QuestionnaireName.MATERNAL_DISCONTINUATION_OF_PREGNANCY)) {
            saveInterviewIntoDatabaseAndLocalFile();
            MaternalAbortion abortion = ModelProvider.getMaternalAbortion((MaternalInfo) beneficiaryInfo, questionAnswerList, interviewId);
            boolean isMaternalAbortionComplete = App.getContext().getDB().isMaternalAbortionComplete(abortion);
            saveMaternalDiscontination(abortion, isMaternalAbortionComplete);
            saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);
        } else if (beneficiaryCode != null && qList.getQuestionnaireName().startsWith(QuestionnaireName.CERVICAL_CANCER)) {
            SCHE_TYPE = FollowupType.CCS;
            saveInterviewIntoDatabaseAndLocalFile();
            saveCCS();
            saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);
        } else if (beneficiaryCode != null && (qList.getQuestionnaireName().startsWith(QuestionnaireName.EPI) || qList.getQuestionnaireName().startsWith(QuestionnaireName.TT))) {
            SCHE_TYPE = FollowupType.IMMUNIZATION;
            saveInterviewIntoDatabaseAndLocalFile();
            saveImmunization();
            saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);
        } else if (beneficiaryCode != null && qList.getQuestionnaireName().startsWith(QuestionnaireName.BENEFICIARY_MIGRATION)) {
            saveInterviewIntoDatabaseAndLocalFile();
            beneficiaryMigration();
            saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);
        } else if (beneficiaryCode != null && qList.getQuestionnaireName().startsWith(QuestionnaireName.DEATH_REGISTRATION)) {
            saveInterviewIntoDatabaseAndLocalFile();
            App.getContext().getDB().saveDeathRegistration(ModelProvider.getDeathRegistration(beneficiaryInfo, questionAnswerList, interviewId, interviewStartTime));
            App.getContext().getDB().updateBeneficiaryState(beneficiaryCode, 0);
            saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);
        } else if (beneficiaryCode != null && qList.getQuestionnaireName().startsWith(QuestionnaireName.COURT_YARD)) {
            questionAnswerListForUpload = questionAnswerList;
            saveInterviewIntoDatabaseAndLocalFile();
            handelCourtYardMeeting();
            saveSchedule();
            questionAnswerList = questionAnswerListForUpload;
            completeInterviewWithUploadAndMessage(isUpload);
        } else if (beneficiaryCode != null) {
            saveInterviewIntoDatabaseAndLocalFile();
            // saveSchedule();
            completeInterviewWithUploadAndMessage(isUpload);
        } else {
            AppToast.showToast(this, "Beneficiary code not found");
            return;
        }

    }

    private void saveBeneficiaryRegistration(final boolean isUpload) throws MhealthException {
        final Beneficiary beneficiary = prepareBeneficiaryForRegistration();
        if (beneficiary != null) {
            if (beneficiary.getFamilyHead() == 1) //Check if the current beneficiary is the family head
            {
                final String familyHeadCode = App.getContext().getDB().getFamilyHeadCodeIfExist(beneficiary.getHhNumber(), beneficiary.getBenefCode());
                if (familyHeadCode != null && familyHeadCode.trim().length() > 0) //Check if there is already a family head in that house
                {
                    String name = beneficiary.getBenefName();
                    if (beneficiary.getBenefLocalName() != null && !beneficiary.getBenefLocalName().equalsIgnoreCase("")) {
                        name = beneficiary.getBenefLocalName();
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(getResources().getString(R.string.family_head_exist_promt_pre) + " ");
                    sb.append("\n");
                    sb.append(name + " ");
                    sb.append(getResources().getString(R.string.family_head_exist_promt_post));

                    HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
                    buttonMap.put(1, R.string.btn_yes);
                    buttonMap.put(2, R.string.btn_no);

                    DialogView exitDialog = new DialogView(this, R.string.dialog_title, sb.toString(), R.drawable.warning, buttonMap);
                    exitDialog.setOnDialogButtonClick(new OnDialogButtonClick() {
                        @Override
                        public void onDialogButtonClick(View view) {
                            switch (view.getId()) {
                                case 1:
                                    App.getContext().getDB().saveBeneficiary(beneficiaryId, beneficiary, familyHeadCode);
                                    beneficiaryCode = beneficiary.getBenefCode();
                                    try {
                                        saveInterviewIntoDatabaseAndLocalFile();
                                        completeInterviewWithUploadAndMessage(isUpload);
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                    break;
                            }
                        }
                    });
                    exitDialog.show();
                    String x = sb.toString();
                } else {
                    App.getContext().getDB().saveBeneficiary(beneficiaryId, beneficiary, null);
                    beneficiaryCode = beneficiary.getBenefCode();
                    saveInterviewIntoDatabaseAndLocalFile();
                    completeInterviewWithUploadAndMessage(isUpload);
                }
            } else {
                App.getContext().getDB().saveBeneficiary(beneficiaryId, beneficiary, null);
                beneficiaryCode = beneficiary.getBenefCode();
                saveInterviewIntoDatabaseAndLocalFile();
                completeInterviewWithUploadAndMessage(isUpload);
            }
        }
    }

    private Beneficiary prepareBeneficiaryForRegistration() throws MhealthException {
        Beneficiary beneficiary = null;
        // Household household = ModelProvider.getHouseholdModel(new Household(), questionAnswerList); // Create household by necessary data
        beneficiary = ModelProvider.getBeneficiaryModel(new Beneficiary(), questionAnswerList); // Create beneficiary by necessary data

//        if (beneficiary != null) {
//
//            if (beneficiaryCode == null) {
//                beneficiary.setHhNumber(household.getHhNumber());
//                String trimmedHouseholNumber = household.getHhNumber().substring(household.getHhNumber().length() - 3, household.getHhNumber().length());
//                beneficiary.setHhId(App.getContext().getDB().getHousehold(trimmedHouseholNumber).getHhId());
//                // New registration. So check if the beneficiary already registered
//                String benefCode = App.getContext().getDB().getBeneficiaryCodeIfExist(beneficiary.getBenefName(), household.getHhNumber());
//                // new registration and beneficiary not exist
//                if (benefCode == null) {
//                    beneficiary.setBenefCode(App.getContext().getDB().generateBeneficiaryCodeFromHHNumber(household.getHhNumber(), 1));
//                } else {
//                    showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.beneficiary_exist), R.string.btn_ok, R.drawable.warning, Color.BLACK, DIALOG_GENERAL);
//                    questionAnswerJson = null;
//                    return null;
//                }
//            } else {
//                // update beneficiary
//                beneficiary.setBenefCode(beneficiaryCode);
//                beneficiary.setHhNumber(beneficiaryCode.substring(0, 12));
//                for (String key : questionAnswerList.keySet()) {
//                    if (questionAnswerList.get(key).getQuestionName().equals("HH_NO")) {
//                        ArrayList<String> hhNumber = new ArrayList<String>();
//                        hhNumber.add(beneficiary.getHhNumber());
//                        questionAnswerList.get(key).setAnswerList(hhNumber);
//                    }
//
//                }
//            }
//        }

        if (beneficiaryCode == null) {
//            beneficiary.setHhNumber("0");
            long userId = App.getContext().getUserInfo().getUserId();
            String userCode = App.getContext().getUserInfo().getUserCode();
            beneficiary.setUserId(userId);
            String locCode = App.getContext().getUserInfo().getLocationCode();
//            String usrSLNo = AppPreference.getString(InterviewActivity.this, KEY.USR_SL_NO, "");

            beneficiary.setBenefCode(App.getContext().getDB().generateBeneficiaryCodeFromParamedic(userCode));
        } else {
            beneficiary.setBenefCode(beneficiaryCode);
            beneficiary.setBenefId(beneficiaryId);
            long userId = App.getContext().getUserInfo().getUserId();
            beneficiary.setUserId(userId);
        }
        return beneficiary;
    }

    private void beneficiaryMigration() throws MhealthException {
        BeneficiaryMigration bm = ModelProvider.getBeneficiaryMigration(questionAnswerList);
        if (bm != null && bm.getMigrationAction() != null && bm.getMigrationAction().equals("REMOVE")) {
            App.getContext().getDB().updateBeneficiaryState(bm.getPrevBenefCode(), 0);
        } else if (bm != null && bm.getMigrationAction() != null && bm.getMigrationAction().equals("MIGRATION")) {
            if (bm.getPrevBenefCode().substring(0, 9).equals(bm.getNewBenefCode().substring(0, 9))) {
                App.getContext().getDB().updateBeneficiaryHouseholdNumberAndCode(bm.getPrevBenefCode(), bm.getNewBenefCode(), bm.getNewBenefCode().substring(0, 12));
            } else {
                App.getContext().getDB().updateBeneficiaryState(bm.getPrevBenefCode(), 0);
            }
        }
    }

    private void saveMaternalService() throws MhealthException {
        long maternalCareId = App.getContext().getDB().getMaternalDao().getMaternalCareId(qList.getQuestionnaireId());
        if (maternalCareId > 0) {
            MaternalService service = new MaternalService();
            service.setMaternalCareId(maternalCareId);
            service.setMaternalId(((MaternalInfo) beneficiaryInfo).getMaternalId());
            service.setInterviewId(interviewId);
            service = ModelProvider.getMaternalService(service, questionAnswerList);

            App.getContext().getDB().getMaternalDao().saveMaternalService(service, (MaternalInfo) beneficiaryInfo);
            if (service.getBmiValue() != null) {
                App.getContext().getDB().getMaternalDao().updateMaternalInfosBmi(service.getMaternalId(), service.getBmiValue(), service.getBmi(), service.getHeightInCm());
            }

            if (service.getStatus() == 0) {
                App.getContext().getDB().getMaternalDao().updateMaternalStatus((MaternalInfo) beneficiaryInfo, interviewId);
            }

        }
    }

    private void saveMotherAndBabyRegistration() throws MhealthException {

        if (((MaternalInfo) beneficiaryInfo).getMaternalId() < 1) {

            String deleveryDateStr = ModelProvider.getAnswer(questionAnswerList, "DELIVERY_DATE");
            if (deleveryDateStr != null) {
                long deleveryDate;
                try {
                    deleveryDate = Utility.getMillisecondFromDate(deleveryDateStr, Constants.DATE_FORMAT_YYYY_MM_DD);
                    long lmp = (deleveryDate - 24192000000L);
                    beneficiaryInfo.setLmp(lmp);
                    beneficiaryInfo.setCalculateLmp(lmp);
                    ((MaternalInfo) beneficiaryInfo).setEdd(deleveryDate);
                    long maternalId = App.getContext().getDB().getMaternalDao().saveMaternalInfo((MaternalInfo) beneficiaryInfo);
                    ((MaternalInfo) beneficiaryInfo).setMaternalId(maternalId);

                    if (beneficiaryInfo.getMaritalStatus().equals("Single")) {
                        App.getContext().getDB().updateBenefeciaryMaretalStatus(beneficiaryInfo.getBenefCode(), "Married");
                    }
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                return;
            }
        }

        MaternalDelivery delivery = ModelProvider.getMaternalDeliveryFromMotherAndChildRegistration((MaternalInfo) beneficiaryInfo, questionAnswerList, interviewId);

        if (delivery.getUserScheduleInfos() != null) {
            App.getContext().getDB().saveUserSchedule(delivery.getUserScheduleInfos(), interviewStartTime);
        }
        App.getContext().getDB().getMaternalDao().saveMaternalDelivery(delivery);
        if (delivery.getBabyInfos() != null) {
            App.getContext().getDB().clearBabyInfoIfexist(delivery.getMaternalId());
            for (String key : delivery.getBabyInfos().keySet()) {
                MaternalBabyInfo baby = delivery.getBabyInfos().get(key);
                if (baby.isAlive()) {
                    App.getContext().getDB().saveBeneficiary(-1, baby, null);
                }
                App.getContext().getDB().getMaternalDao().saveMaternalBabyInfos(baby, delivery.getBenefCode(), delivery.getLmp());
            }
        }
    }

    private void saveMaternalDelivery() throws MhealthException {
        MaternalDelivery maternalDelivery = ModelProvider.getMaternalDeliveryFromMotherDelivery((MaternalInfo) beneficiaryInfo, questionAnswerList, interviewId);
        App.getContext().getDB().getMaternalDao().saveMaternalDelivery(maternalDelivery);
    }

    private void saveMatrnalHighrisk() throws MhealthException {
        long count = ModelProvider.getMaternalHighRiskCount(questionAnswerList);
        App.getContext().getDB().getMaternalDao().updateMaternalHighrisk(((MaternalInfo) beneficiaryInfo).getMaternalId(), count, interviewId);
    }

    private void saveMaternalDiscontination(MaternalAbortion abortion, boolean isMaternalAbortionComplete) throws MhealthException {
        if (isMaternalAbortionComplete) {
            App.getContext().getDB().getMaternalDao().deleteMaternalAbortion(abortion);
        }
        App.getContext().getDB().getMaternalDao().saveMaternalAbortion(abortion);
        if (abortion.getStatus() == 0) {
            App.getContext().getDB().getMaternalDao().updateMaternalStatus((MaternalInfo) beneficiaryInfo, interviewId);
        }
    }

    private void saveCCS() throws MhealthException {
        beneficiaryInfo = ModelProvider.getCCSBeneficiaryModel((CCSBeneficiary) beneficiaryInfo, questionAnswerList);
        long ccsTrId = App.getContext().getDB().getCcsDao().getCcsTrId(beneficiaryInfo.getBenefCode(), ((CCSBeneficiary) beneficiaryInfo).getEligibleId(), ((CCSBeneficiary) beneficiaryInfo).getCcsStatusId());
        ((CCSBeneficiary) beneficiaryInfo).setCcsTreatmentId(ccsTrId);
        if (qList.getQuestionnaireName().startsWith(QuestionnaireName.CERVICAL_CANCER_SCREENING)) {
            App.getContext().getDB().getCcsDao().saveCCSPrimaryScreening((CCSBeneficiary) beneficiaryInfo, App.getContext().getUserInfo().getUserId(), App.getContext().getAppSettings().getLanguage(), interviewId, interviewStartTime);
            nextFollowupDate = ((CCSBeneficiary) beneficiaryInfo).getNextFollowupDate();
        } else if (qList.getQuestionnaireName().startsWith(QuestionnaireName.CERVICAL_CANCER_FOLLOWUP)) {
            if (((CCSBeneficiary) beneficiaryInfo).getReEligibleDate() != null && ((CCSBeneficiary) beneficiaryInfo).getReEligibleDate().trim().length() > 0) {
                App.getContext().getDB().getCcsDao().beneficiaryReEligible(beneficiaryInfo.getBenefCode(), ((CCSBeneficiary) beneficiaryInfo).getReEligibleDate());
            } else {
                App.getContext().getDB().getCcsDao().saveCCSFollowup((CCSBeneficiary) beneficiaryInfo, App.getContext().getUserInfo().getUserId(), App.getContext().getAppSettings().getLanguage(), interviewId, interviewStartTime);
                nextFollowupDate = ((CCSBeneficiary) beneficiaryInfo).getNextFollowupDate();
            }
            // 1=followup visit which disagreed by beneficiary for going to hospital
            if (((CCSBeneficiary) beneficiaryInfo).getFollowUpVisit() != null && ((CCSBeneficiary) beneficiaryInfo).getFollowUpVisit() == 1) {
                App.getContext().getDB().updateUserSchedule(beneficiaryCode, FollowupType.CCS, ((CCSBeneficiary) beneficiaryInfo).getNextFollowupDate(), interviewStartTime);
            }
        }
    }

    private void saveImmunization() throws MhealthException {
        beneficiaryInfo = ModelProvider.getImmueneficiaryModel((ImmunaizationBeneficiary) beneficiaryInfo, questionAnswerList); //Beneficiary is immunization type. So prepare Immunizable beneficiary objec
        if (qList.getQuestionnaireName().equalsIgnoreCase(QuestionnaireName.EPI)) {
            App.getContext().getDB().getImmunizationDao().saveImmunizationService((ImmunaizationBeneficiary) beneficiaryInfo, interviewId, "EPI");
        } else if (qList.getQuestionnaireName().equalsIgnoreCase(QuestionnaireName.EPI_FOLLOWUP)) {
            App.getContext().getDB().getImmunizationDao().saveEmunizationFollowup((ImmunaizationBeneficiary) beneficiaryInfo, interviewId, "EPI");
        } else if (qList.getQuestionnaireName().equalsIgnoreCase(QuestionnaireName.TT)) {
            App.getContext().getDB().getImmunizationDao().saveImmunizationService((ImmunaizationBeneficiary) beneficiaryInfo, interviewId, "TT");
        } else if (qList.getQuestionnaireName().equalsIgnoreCase(QuestionnaireName.TT_FOLLOWUP)) {
            App.getContext().getDB().getImmunizationDao().saveEmunizationFollowup((ImmunaizationBeneficiary) beneficiaryInfo, interviewId, "TT");
        }
    }

    private void saveMaternalPregnantMotherRegistration(MaternalInfo maternalInfo) throws MhealthException {
        App.getContext().getDB().getMaternalDao().saveMaternalInfo(maternalInfo);
        if (maternalInfo.getUserScheduleInfos() != null) {
            App.getContext().getDB().saveUserSchedule(maternalInfo.getUserScheduleInfos(), interviewStartTime);
        }
        if (maternalInfo.getMaritalStatus().equals("Single")) {
            App.getContext().getDB().updateBenefeciaryMaretalStatus(maternalInfo.getBenefCode(), "Married");
        }
    }

    private void handelCourtYardMeeting() throws MhealthException {
        CourtyardMeeting meeting = ModelProvider.getCourtYardMeeting(questionAnswerList, interviewId);
        App.getContext().getDB().saveCourtYardMeeting(meeting, interviewStartTime);
    }


    private void completeInterviewWithUploadAndMessage(boolean isUploded) {

        if (isUploded) {
            try {
                JSONCreateor.createQuestionnaireJson(qList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String currentDateandTime = Constants.DATE_FORMAT_YYYY_MM_DD.format(new Date());
            double longitude = 0;
            double latitude = 0;

            if (!GPSUtility.isGPSEnable(InterviewActivity.this)) {
                AppToast.showToast(this, "Please turn on GPS");
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    GPSUtility.requestPermissions(InterviewActivity.this);
                    return;
                }
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null && location.getTime() >= (Calendar.getInstance().getTimeInMillis() - 1 * 60 * 1000)) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            }
            long timeInMilli = Calendar.getInstance().getTimeInMillis();
            if (beneficiaryCode.length() <= 5)
                beneficiaryCode = App.getContext().getUserInfo().getUserCode() + beneficiaryCode;
            /*
             *  Create question answer json
             */
            try {
                questionAnswerJson = JSONCreateor.getQuestionAnswerJson(this, App.getContext().getUserInfo().getUserCode(), App.getContext().getUserInfo().getPassword(), beneficiaryCode, interviewStartTime, timeInMilli, qList.getQuestionnaireId(), longitude, latitude, questionAnswerList);
                //questionAnswerJson = JSONCreateor.getQuestionAnswerJson(this, App.getContext().getUserInfo().getUserCode(), App.getContext().getUserInfo().getPassword(), beneficiaryCode, interviewStartTime, timeInMilli, qList.getQuestionnaireId(), longitude, latitude, questionAnswerListForUpload);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (questionAnswerJson != null || questionAnswerJson.length() > 0) {
                dlog = ProgressDialog.show(InterviewActivity.this, getResources().getString(R.string.uploading_data), getResources().getString(R.string.please_wait));

                new UploadAnswerJsonTask(questionAnswerJson).execute();
            }
        } else {
            if (qList.getQuestionnaireName().startsWith(QuestionnaireName.BENEFICIARY_REGISTRATION)) {
                showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.data_saved_successfully), R.string.btn_ok, R.drawable.information, Color.BLACK, EXIT_ON_OK);
                //  showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.data_saved_successfully) + "\n" + getResources().getString(R.string.beneficiary_code) + " " + beneficiaryCode.substring(beneficiaryCode.length() - 5), R.string.btn_ok, R.drawable.information, Color.BLACK, EXIT_ON_OK);
            } else {
                showOneButtonDialog(R.string.dialog_title, getResources().getString(R.string.data_saved_successfully), R.string.btn_ok, R.drawable.information, Color.BLACK, EXIT_ON_OK);
            }

        }
    }

    /**
     * Save interview into database and store questionnaire JSON with user input into local file.
     *
     * @throws JSONException the JSON exception
     */


    private void saveInterviewIntoDatabaseAndLocalFile() throws MhealthException {
        ////// Questionnaire JSON///////////
        String questionnaireWithUserDataJson = "";
        try {
            questionnaireWithUserDataJson = JSONCreateor.createQuestionnaireJson(qList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String currentDateandTime = Constants.DATE_FORMAT_YYYY_MM_DD.format(new Date());
        ///////////////////////


        //////// Question/ Answer JSON////////
        double longitude = 0;
        double latitude = 0;

        if (!GPSUtility.isGPSEnable(InterviewActivity.this)) {
            AppToast.showToast(this, "Please turn on GPS");
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                GPSUtility.requestPermissions(InterviewActivity.this);
                return;
            }
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null && location.getTime() >= (Calendar.getInstance().getTimeInMillis() - 1 * 60 * 1000)) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        }
        long timeInMilli = Calendar.getInstance().getTimeInMillis();


//        if (beneficiaryCode.length() <= 5){
//            beneficiaryCode = App.getContext().getUserInfo().getUserCode() + beneficiaryCode;
//        }


        /*
         *  Create question answer json
         */
        try {

            questionAnswerJson = JSONCreateor.getQuestionAnswerJson(this, App.getContext().getUserInfo().getUserCode(), App.getContext().getUserInfo().getPassword(), beneficiaryCode, interviewStartTime, timeInMilli, qList.getQuestionnaireId(), longitude, latitude, questionAnswerList);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // added by ashraf for update feedback through interview edit in diabetic algorithm
        Boolean isDoctorFeedbackForUpdate = false;
        PatientInterviewDoctorFeedback patientInterviewDoctorFeedback = new PatientInterviewDoctorFeedback();
        if (getIntent().getExtras().containsKey("isDoctorFeedback") &&
                getIntent().getExtras().containsKey("isDoctorFeedbackForUpdate") &&
                getIntent().getExtras().containsKey("patientInterviewDoctorFeedback")
        ) {
            isDoctorFeedback = getIntent().getExtras().getBoolean("isDoctorFeedback");
            isDoctorFeedbackForUpdate = getIntent().getExtras().getBoolean("isDoctorFeedbackForUpdate");
            patientInterviewDoctorFeedback = (PatientInterviewDoctorFeedback) getIntent().getSerializableExtra("patientInterviewDoctorFeedback");
        }


        if (isDoctorFeedback) {
            PatientInterviewDoctorFeedback doctorFeedback;
            if (isDoctorFeedbackForUpdate == true) {
                doctorFeedback = ModelProvider.getPatientInterviewDoctorFeedback(new PatientInterviewDoctorFeedback(), questionAnswerList);
                doctorFeedback.setDocFollowupId(patientInterviewDoctorFeedback.getDocFollowupId());
                doctorFeedback.setInterviewId(patientInterviewDoctorFeedback.getInterviewId());
                doctorFeedback.setNotificationStatus(patientInterviewDoctorFeedback.getNotificationStatus());
                doctorFeedback.setTransRef(patientInterviewDoctorFeedback.getTransRef());
                doctorFeedback.setFeedbackDate(patientInterviewDoctorFeedback.getFeedbackDate());
                doctorFeedback.setFeedbackSource(patientInterviewDoctorFeedback.getFeedbackSource());
                doctorFeedback.setInterviewTime(patientInterviewDoctorFeedback.getInterviewTime());
                doctorFeedback.setInvesAdvice(patientInterviewDoctorFeedback.getInvesAdvice());
                doctorFeedback.setInvesResult(patientInterviewDoctorFeedback.getInvesResult());
                doctorFeedback.setInvesStatus(patientInterviewDoctorFeedback.getInvesStatus());
                doctorFeedback.setIsFeedbackOnTime(patientInterviewDoctorFeedback.getIsFeedbackOnTime());
                doctorFeedback.setUpdateBy(App.getContext().getUserInfo().getUserId());
                doctorFeedback.setUpdateOn(Calendar.getInstance().getTimeInMillis());

            } else {
                doctorFeedback = ModelProvider.getPatientInterviewDoctorFeedback(new PatientInterviewDoctorFeedback(), questionAnswerList);
                doctorFeedback.setUpdateBy(App.getContext().getUserInfo().getUserId());
                doctorFeedback.setUpdateOn(Calendar.getInstance().getTimeInMillis());
            }
            doctorFeedbackUpdatedObject = doctorFeedback;
            //PatientInterviewDoctorFeedback doctorFeedback = ModelProvider.getPatientInterviewDoctorFeedback(new PatientInterviewDoctorFeedback(), questionAnswerList);
            if (doctorFeedback != null) {
                long updateStatus = App.getContext().getDB().updatePatientInterviewDoctorFeedback(doctorFeedback);
                if (updateStatus == 0) {
                    App.getContext().getDB().updatePatientInterviewDoctorFeedback(interviewId, 0, questionAnswerJson.toString());
                    isDoctorFeedbackUploded = true;
                }
            }

        }


        /*
         * Build binary file input
         */
        StringBuilder sbFilePath = new StringBuilder();

        for (String key : questionAnswerList.keySet()) {
            if (questionAnswerList.get(key).getType().equalsIgnoreCase(QUESTION_TYPE.BINARY)) {
                if (questionAnswerList.get(key).getAnswerList().get(0) == null)
                    continue;

                if (sbFilePath.length() > 0)
                    sbFilePath.append("|");

                sbFilePath.append(questionAnswerList.get(key).getAnswerList().get(0));
            }
        }


        String benefName = null;
        for (String key : questionAnswerList.keySet()) {
            if (questionAnswerList.get(key).getType().equalsIgnoreCase(QUESTION_TYPE.STRING)
                    && questionAnswerList.get(key).getQuestionName().equals(Column.B_NAME)) {
                benefName = questionAnswerList.get(key).getAnswerList().get(0);
            }
        }
        try {
            if (beneficiaryInfo == null) {
                beneficiaryInfo = new Beneficiary();
                beneficiaryInfo.setBenefName(benefName);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        /*
         *  Save Question answer JSON into database
         */
//		if (getIntent().getExtras().containsKey("isDoctorFeedback") &&
//				getIntent().getExtras().containsKey("isDoctorFeedbackForUpdate") &&
//				getIntent().getExtras().containsKey("patientInterviewDoctorFeedback")
//		) {
//			isDoctorFeedback = getIntent().getExtras().getBoolean("isDoctorFeedback");
//			isDoctorFeedbackForUpdate=getIntent().getExtras().getBoolean("isDoctorFeedbackForUpdate");
//			if(isDoctorFeedbackForUpdate==true){
//				qList.setQuestionnaireId(191);
//				interviewId = App.getContext().getDB().saveInterview(qList.getQuestionnaireId(), parentInterviewId, App.getContext().getUserInfo().getUserId(), beneficiaryCode, referralCenterId, nextFollowupDate, sbFilePath.toString(), questionAnswerJson.toString(), Calendar.getInstance().getTimeInMillis(), interviewStartTime, interviewId, needDoctorReferral, benefName);
//			}else {
//				interviewId = App.getContext().getDB().saveInterview(qList.getQuestionnaireId(), parentInterviewId, App.getContext().getUserInfo().getUserId(), beneficiaryCode, referralCenterId, nextFollowupDate, sbFilePath.toString(), questionAnswerJson.toString(), Calendar.getInstance().getTimeInMillis(), interviewStartTime, interviewId, needDoctorReferral, benefName);
//
//			}
//		}
//		else {
//			interviewId = App.getContext().getDB().saveInterview(qList.getQuestionnaireId(), parentInterviewId, App.getContext().getUserInfo().getUserId(), beneficiaryCode, referralCenterId, nextFollowupDate, sbFilePath.toString(), questionAnswerJson.toString(), Calendar.getInstance().getTimeInMillis(), interviewStartTime, interviewId, needDoctorReferral, benefName);
//
//		}

        interviewId = App.getContext().getDB().saveInterview(qList.getQuestionnaireId(), parentInterviewId, App.getContext().getUserInfo().getUserId(), beneficiaryCode, referralCenterId, nextFollowupDate, sbFilePath.toString(), questionAnswerJson.toString(), Calendar.getInstance().getTimeInMillis(), interviewStartTime, interviewId, needDoctorReferral, beneficiaryInfo, interviewRecordDate);
        // added for feedback tables interview data by ashraf 24th may,2021
        App.getContext().getDB().updateInterviewMasterFcmInterviewId(interviewId);

        //interviewId = App.getContext().getDB().saveInterview(qList.getQuestionnaireId(), parentInterviewId, App.getContext().getUserInfo().getUserId(), beneficiaryCode, referralCenterId, nextFollowupDate, sbFilePath.toString(), questionAnswerJson.toString(), Calendar.getInstance().getTimeInMillis(), interviewStartTime, interviewId, needDoctorReferral, benefName);

        ArrayList<PatientInterviewDetail> interviewDetails = ModelProvider.getPatientInterviewDetail(interviewId, qList.getQuestionnaireId(), questionAnswerList);
        App.getContext().getDB().savePatientInterviewDetail(interviewDetails, interviewStartTime);



        /*
         *  Update consume and stock table based on data from prescription
         */
        App.getContext().getDB().consumeMedicine(prescription, interviewId, App.getContext().getUserInfo().getUserId(), beneficiaryCode, interviewStartTime);
        // Send prescription SMS

        try {
            if (JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "SMS", "send_prescription").equals("YES") && prescription != null && prescription.size() > 0) {
                JSONObject jsonObject = new JSONObject(prescription.get(0));
                String smsText = "";
                if (jsonObject.has("SMS_SF")) {
                    smsText = jsonObject.getString("SMS_SF");
                } else {
                    smsText = getPriescriptionText();
                }
                if (smsText.trim().length() > 0) {
                    Utility.sendSMS(this, beneficiaryInfo.getMobileNumber(), smsText);
                }
            }
        } catch (Exception exception) {

        }

        if (serverStatus != null && serverStatus.equals("remote")) {
            App.getContext().getDB().updateInterviewSent(interviewId, parentInterviewId, "NR", 0);
        } else {
            App.getContext().getDB().updateInterviewSent(interviewId, parentInterviewId, "N", 0);
        }


    }

    public void saveSchedule() {

        if (nextFollowupDate > 0) {
            /*
             *  If there is a follow up date in interview then save it to schedule table
             */
            UserScheduleInfo userScheduleInfo = new UserScheduleInfo();
            userScheduleInfo.setBeneficiaryCode(beneficiaryCode);
            userScheduleInfo.setReferenceId(0);
            userScheduleInfo.setDescription(qList.getQuestionnaireTitle());
            userScheduleInfo.setScheduleDate(nextFollowupDate);
            userScheduleInfo.setScheduleStatus(ScheduleStatus.NEW);
            userScheduleInfo.setScheduleType(SCHE_TYPE);
            userScheduleInfo.setUserId((int) App.getContext().getUserInfo().getUserId());
            userScheduleInfo.setInterviewId(interviewId);
            userScheduleInfo.setCreatedDate(interviewStartTime);

            // delete previous schedule when edit interview
            long scheId = App.getContext().getDB().getUserScheId(interviewId, userScheduleInfo.getScheduleType());
            if (scheId > 0) {
                App.getContext().getDB().deleteUserSchedule(scheId);
            }

            // inactive duplicate schedule
            App.getContext().getDB().inactivePrivousUndoneUserSchedule(userScheduleInfo, qList.getQuestionnaireId());
            App.getContext().getDB().saveUserSchedule(userScheduleInfo);

        } else if (!isDoctorFeedback) {
            App.getContext().getDB().deleteSchedule(interviewId);
        }
    }

    public String getPriescriptionText() {

        StringBuilder sms = new StringBuilder();
        int count = 0;
        try {
            // Rahima, 01020403100402,37y
            sms.append("Rx\n" + beneficiaryInfo.getBenefName() + "," + beneficiaryCode + "," + beneficiaryInfo.getAge() + "\n");

            for (int i = 0; i < prescription.size(); i++) {

                JSONObject jsonObject = new JSONObject(prescription.get(i));
                MedicineInfo mediInfo = App.getContext().getDB().getMedicine(jsonObject.getLong("MED_ID"));
                if (mediInfo != null) {
                    String MED_DURATION = jsonObject.getString("MED_DURATION");
                    String SF = jsonObject.getString("SF");
                    sms.append(mediInfo.toString());
                    if (SF.trim().length() > 0) {
                        sms.append(" " + SF.trim());
                    }
                    if (MED_DURATION.trim().length() > 0) {
                        sms.append(" " + MED_DURATION.trim() + "d");
                    }
                    sms.append("\n");
                    count++;
                }

            }
            sms.append(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "SMS", "sms_footer"));

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (count > 0) {
            return sms.toString();
        } else {
            return "";
        }
    }

    public String getInterviewQuestionnaireTitle(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length == 1) {
            String transRef = "";
            QuestionAnswer answer = questionAnswerMap.get(params[0].trim());
            if (answer != null) {
                transRef = answer.getAnswerList().get(0);
            } else {
                transRef = params[0].trim();
            }
            return App.getContext().getDB().getInterviewQuestionnaireTitle(transRef);
        }
        return "ERROR";
    }


    public String getFcmCode() {
        return App.getContext().getUserInfo().getUserCode();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (GPSUtility.validatePermissionsLocation(InterviewActivity.this)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            GPSUtility.requestPermissions(InterviewActivity.this);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        App.getContext().onStartActivity(this);
    }


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LanguageContextWrapper.wrap(context, AppPreference.getString(context, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)));
    }

    @Override
    public void onInterviewUploadFinished(String message) {
        dlog.hide();
        dlog.cancel();
        dlog.dismiss();
        HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
        buttonMap.put(1, R.string.btn_close);
        DialogView dialog = new DialogView(this, R.string.dialog_title, message, R.drawable.information, buttonMap);
        dialog.setOnDialogButtonClick(new OnDialogButtonClick() {
            @Override
            public void onDialogButtonClick(View view) {
                finish();

            }
        });
        dialog.showWebView();
    }

    void saveHouseHoldWithBeneficiaryRegistration(String houseHoldId, String hhName, long numberOfMember, String hhCharacter, long hhAdultWomen, double latitude, double longitude, String monthlyFamilyExpenditure, boolean isUpload) {
        Household basicData = new Household();
        basicData.setHhNumber(houseHoldId);
        basicData.setNoOfFamilyMember(numberOfMember);
        basicData.setLatitude(latitude);
        basicData.setLongitude(longitude);
        basicData.setHhCharacter(hhCharacter);
        basicData.setHhAdultWomen(hhAdultWomen);
        basicData.setHhName(hhName);

        basicData.setRegDate(Calendar.getInstance().getTimeInMillis());
        basicData.setMonthlyFamilyExpenditure(monthlyFamilyExpenditure);
        MHealthTask tsk = new MHealthTask(this, Task.HOUSE_HOLD_DATA_SAVE, R.string.saving_data, R.string.please_wait);
        tsk.setParam(basicData);
        tsk.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Message msg) {
                if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
                    String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
                    App.showMessageDisplayDialog(InterviewActivity.this, getResources().getString(R.string.saving_error), R.drawable.error, Color.RED);
                } else {

                    if (isUpload) {
                        ArrayList<Household> householdList = App.getContext().getDB().getHouseholdBasicDataListUnsend();
                        if (SystemUtility.isConnectedToInternet(InterviewActivity.this)) {
                            try {
                                RequestData request = new RequestData(RequestType.TRANSACTION, RequestName.HOUSEHOLD_DATA_ENTRY, Constants.MODULE_BUNCH_PUSH);
                                JSONArray jHouseholdListArr = new JSONArray();
                                for (Household householdInfo : householdList) {
                                    if (householdInfo.getSent() == 1) continue;
                                    jHouseholdListArr.put(householdInfo.toJson());
                                }
                                request.getData().put("householdList", jHouseholdListArr);
                                CommiunicationTask commiunicationTask = new CommiunicationTask(InterviewActivity.this, request, R.string.uploading_data, R.string.please_wait);
                                commiunicationTask.setCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(Message msg) {
                                        try {
                                            ResponseData response = (ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
                                            MHealthTask tsk = new MHealthTask(InterviewActivity.this, Task.RETRIEVE_HOUSEHOLD_BASICDATA_INFORMATION_LIST, R.string.retrieving_data, R.string.please_wait);
                                            tsk.setParam(response);
                                            tsk.setCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(Message msg) {

                                                }
                                            });
                                            tsk.execute();

                                            saveBeneficiaryRegistration(isUpload);
                                        } catch (Exception e) {
                                            e.printStackTrace();

                                        }

                                    }
                                });
                                commiunicationTask.execute();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (MhealthException e) {
                                e.printStackTrace();
                            }

                        } else {
                            SystemUtility.openInternetSettingsActivity(InterviewActivity.this);
                        }
                    } else {
                        try {
                            saveBeneficiaryRegistration(isUpload);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                }
            }
        });
        tsk.execute();
    }

}
