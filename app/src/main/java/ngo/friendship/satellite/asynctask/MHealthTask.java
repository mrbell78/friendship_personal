package ngo.friendship.satellite.asynctask;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.asynctask.async.AsyncTask;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.BeneficiaryRegistrationState;
import ngo.friendship.satellite.model.CCSBeneficiary;
import ngo.friendship.satellite.model.HealthCareReportInfo;
import ngo.friendship.satellite.model.Household;
import ngo.friendship.satellite.model.IndividualSalesInfo;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.MedicineRcvSaleInfo;
import ngo.friendship.satellite.model.MyData;
import ngo.friendship.satellite.model.QuestionnaireCategoryInfo;
import ngo.friendship.satellite.model.QuestionnaireInfo;
import ngo.friendship.satellite.model.QuestionnaireList;
import ngo.friendship.satellite.model.Report;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.utility.ModelHandler;
import ngo.friendship.satellite.utility.ModelProvider;
import ngo.friendship.satellite.utility.Utility;

public class MHealthTask extends AsyncTask<Void, String, String> {


    private OnCompleteListener completeListener;
    private Context context;
    private ProgressDialog dialog;
    Message message = Message.obtain();
    Bundle bundle = new Bundle();
    private Task task;
    private Object param[];

    public MHealthTask(Context context, Task task, Object dilogTitle, Object dilogMessage) {
        this.context = context;
        this.message = Message.obtain();
        this.bundle = new Bundle();
        this.bundle.putString(TaskKey.NAME, "MHEALTH_TASK");
        this.task = task;
        this.dialog = new ProgressDialog(context);
        if (dilogTitle instanceof Integer) {
            this.dialog.setTitle((Integer) dilogTitle);
        } else if (dilogTitle instanceof String) {
            this.dialog.setTitle((String) dilogTitle);
        }
        if (dilogMessage instanceof Integer) {
            this.dialog.setMessage(context.getResources().getString((Integer) dilogMessage));
        } else if (dilogMessage instanceof String) {
            this.dialog.setMessage((String) dilogMessage);
        }
    }

    public MHealthTask(Context context, Task task) {
        this.context = context;
        this.message = Message.obtain();
        this.task = task;
        this.bundle = new Bundle();
        this.bundle.putString(TaskKey.NAME, "MHEALTH_TASK");
        this.bundle.putString("NAME", "MHEALTH_TASK");

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (this.dialog != null) {
            this.dialog.show();
            this.dialog.setCancelable(false);
            this.dialog.setCanceledOnTouchOutside(false);
        }
    }

    @Override
    protected String doInBackground(Void params) throws Exception {
        try {

            String fcmCode = App.getContext().getUserInfo().getUserCode();
            switch (task) {
                case MYDATA:
                    bundle.putSerializable(TaskKey.DATA0, processMydata(param == null ? null : (ResponseData) param[0]));
                    break;

                case MY_DATA_PARAMEDIC:
                    bundle.putSerializable(TaskKey.DATA0, processMydata(param == null ? null : (ResponseData) param[0]));
                    break;
                case TODAYS_MEDICINE_SALES_REPOR:
                    bundle.putSerializable(TaskKey.DATA0, getTodaysMedicineSalesReport(param == null ? null : (ResponseData) param[0]));
                    break;
                case LAST_30_DAYS_RECEIVE_SALES:
                    bundle.putSerializable(TaskKey.DATA0, getLast30daysReceiveSales(param == null ? null : (ResponseData) param[0]));
                    break;
                case LAST_30DAYS_SALES_LIST:
                    bundle.putSerializable(TaskKey.DATA0, getlast30DaysSalesListReport(param == null ? null : (ResponseData) param[0]));
                    break;
                case HEALTH_CARER_EPORT:
                    bundle.putSerializable(TaskKey.DATA0, getHealthCareRepor(param == null ? null : (ResponseData) param[0]));
                    break;
                case REGISTRATION_STATS:
                    bundle.putSerializable(TaskKey.DATA0, getBeneficiaryRegistrationReport(param == null ? null : (ResponseData) param[0]));
                    break;
                case RETRIEVE_SCHEDULE_LIST:

                    if (param != null && param[1] instanceof String) {
                        fcmCode = fcmCode + (param != null ? ((String) param[1]).trim() : "");
                    }

                    bundle.putSerializable(TaskKey.DATA0, getScheduleListFromDB((Integer) param[0], fcmCode));
                    break;
                case REPORT_SCHEDULE_LIST:
                    processScheduleList((ResponseData) param[0]);
                    bundle.putSerializable(TaskKey.DATA0, getScheduleListFromDB((Integer) param[1], ""));
                    break;
                case RETERIEVE_BENEFICIARY_LIST:
                    bundle.putSerializable(TaskKey.DATA0, getBeneficiaryList(param == null ? null : param[0]));
                    break;

                case RETRIEVE_CATEGORY_LIST:
                    bundle.putSerializable(TaskKey.DATA0, getRetrieveCategoryList(param == null ? null : (ResponseData) param[0]));
                    break;
                case RETRIEVE_CCSBENEFICIARY_LIST:

                    if (param != null && param[0] instanceof ResponseData) {
                        processMydata((ResponseData) param[0]);
                        bundle.putString(TaskKey.DATA3, "RT");
                    } else {
                        bundle.putString(TaskKey.DATA3, "DB");
                    }

                    if (param != null && param[0] instanceof String) {
                        fcmCode = fcmCode + (param != null ? ((String) param[0]).trim() : "");
                    } else {
                        App.getContext().getDB().getCcsDao().updateCCSEligableList();
                        App.getContext().getDB().getCcsDao().updateCCSHospitalMissingList();
                    }


                    Map<String, ArrayList<CCSBeneficiary>> map = App.getContext().getDB().getCcsDao().getCCSEligibleList(fcmCode);
                    bundle.putSerializable(TaskKey.DATA0, map.get("NO_TREATMENT"));
                    bundle.putSerializable(TaskKey.DATA1, map.get("UNDER_TREATMENT"));
                    bundle.putSerializable(TaskKey.DATA2, map.get("HOSPITAL_GOING_DATE"));

                    break;

                case PROCESS_EMMUNIZATION_AND_RETRIEVE_BENEFICIARY:

                    if (param != null && param.length == 4 && param[3] instanceof ResponseData) {
                        processMydata((ResponseData) param[3]);
                    }
                    if (param != null && param.length == 4 && param[3] instanceof String) {
                        fcmCode = fcmCode + (param != null ? ((String) param[3]).trim() : "");
                    }

                    if (param[2].equals("UPDATE")) {
                        App.getContext().getDB().getImmunizationDao().updateImmunizableBeneficiary((String) param[0]);
                        App.getContext().getDB().getImmunizationDao().updateImmunaizationTarget((Activity) context, (String) param[0], (Long) param[1]);
                    }
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getDB().getImmunizationDao().getImmunizationBeneficiaryList((String) param[0], false, fcmCode));
                    bundle.putSerializable(TaskKey.DATA1, App.getContext().getDB().getImmunizationDao().getImmunizationBeneficiaryList((String) param[0], true, fcmCode));
                    break;

                case RETRIEVE_FCM_PERSONAL_DATA:
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getUserInfo());
                    break;

                case RETRIEVE_SAVED_INTERVIEW_LIST:

                    if (param != null && param[1] instanceof String) {
                        fcmCode = fcmCode + (param != null ? ((String) param[1]).trim() : "");
                    }

                    Log.d("limittest", "doInBackground: offset is "+param[2]);
                    Log.d("limittest", "doInBackground: limit is "+param[3]);
                    long fromDate =-1;
                    long toDatee =-1;
                    if(param[4]!=null && param[5]!=null){
                        fromDate =Long.parseLong(param[4].toString());
                        toDatee =Long.parseLong(param[5].toString());
                    }

                    String lng = App.getContext().getAppSettings().getLanguage();
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getDB().getInterviewListSyncUnsync((String) param[0], lng, fcmCode,  Constants.INTERVIEW_ALL, Integer.parseInt((String) param[2]),Integer.parseInt((String) param[3]),fromDate,toDatee));
                    break;

                case RETRIEVE_TIME_SCHEDULE:
                    bundle.putSerializable(TaskKey.DATA0, retrieveTodaysTimeSchedule(param == null ? null : param[0]));
                    break;


                case RETRIEVE_PATIENT_FOLLOWUP:
                    bundle.putSerializable(TaskKey.DATA0, retrievePatientFollowup(param == null ? null : (String) param[0]));
                    break;

                case RETRIEVE_QUESTIONNAIRE_LIST:
                    if (param != null && param.length == 2) {
                        bundle.putSerializable(TaskKey.DATA0, getQuestionnaireList((Integer) param[0], (String) param[1], null));
                    } else if (param != null && param.length == 3) {
                        bundle.putSerializable(TaskKey.DATA0, getQuestionnaireList((Integer) param[0], (String) param[1], (ResponseData) param[2]));
                    }
                    break;

                case RETRIEVE_HOUSEHOLD_BASICDATA_INFORMATION_LIST:
                    bundle.putSerializable(TaskKey.DATA0, getHouseholdBasicdataInformationList(param == null ? null : (ResponseData) param[0]));
                    break;
                case HOUSE_HOLD_DATA_SAVE:
                    App.getContext().getDB().saveHousehold((Household) param[0]);
                    break;
                case RETRIEVE_HOUSEHOLD_MEMBER_LIST_ACTIVE:
                    bundle.putSerializable(TaskKey.DATA0, retrieveHouseholdMemberListActive(param == null ? " " : (String) param[0]));
                    break;

                case RETRIVE_HOUSEHOLD_LIST:
                    long countBeneficiary = 0;
                    String hhcode = fcmCode + (param != null ? ((String) param[0]).trim() : "");
                    int status = (Integer) param[1];

                    ArrayList<Household> householdList;
                    if (status == 1) {
                        householdList = App.getContext().getDB().getHouseholdList(hhcode);
                    } else {
                        householdList = App.getContext().getDB().getDeactivatedHouseholdList(hhcode);
                    }

                    for (Household h : householdList) {
                        countBeneficiary = countBeneficiary + h.getNumberOfBeneficiary();
                    }
                    bundle.putSerializable(TaskKey.DATA0, householdList);
                    bundle.putLong(TaskKey.DATA1, App.getContext().getDB().countHouseHoldData());
                    bundle.putLong(TaskKey.DATA2, App.getContext().getDB().countBeneficiaryFromHouseHold());
                    bundle.putSerializable(TaskKey.DATA3, App.getContext().getUserInfo());
                    bundle.putLong(TaskKey.DATA4, countBeneficiary);

                    break;

//				case RETRIVE_DEACTIVATED_HOUSEHOLD_LIST:
//					long countBeneficiaryDeactivatedHH=0;
//					String hhcodeDeactivatedHH=fcmCode+(param!=null?((String)param[0]).trim():"");
//					ArrayList<Household> deactivatedHouseholdList=App.getContext().getDB().getDeactivatedHouseholdList(hhcodeDeactivatedHH);
//					for(Household h: deactivatedHouseholdList){
//						countBeneficiaryDeactivatedHH=countBeneficiaryDeactivatedHH+h.getNumberOfBeneficiary();
//					}
//					bundle.putSerializable(TaskKey.DATA0,deactivatedHouseholdList);
//					bundle.putLong(TaskKey.DATA1,App.getContext().getDB().countHouseHoldData(0));
//					bundle.putLong(TaskKey.DATA2,App.getContext().getDB().countBeneficiaryFromHouseHold(0));
//					bundle.putSerializable(TaskKey.DATA3,App.getContext().getUserInfo());
//					bundle.putLong(TaskKey.DATA4,countBeneficiaryDeactivatedHH);
//					break;

                case RETERIEVE_MATERNAL_BENEFICIARY_LIST:
                    String hhNumber = fcmCode + (param != null ? ((String) param[0]).trim() : "");
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getDB().getMaternalDao().getMaternalBeneficiaryList(fcmCode, hhNumber));
                    break;
                case RETERIEVE_CHILD_BENEFICIARY_LIST:

                    String hn = fcmCode + (param != null ? ((String) param[0]).trim() : "");
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getDB().getChildBeneficiaryList(fcmCode, hn));
                    break;

                case RETRIEVE_REFERRAL_CENTER_LIST:
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getDB().getReferralCenterList("STATE=1"));
                    break;
                case RETRIEVE_DOCTOR_FEEDBACK_LIST:
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getDB().getPatientInterviewDoctorFeedbackList(0, App.getContext().getAppSettings().getLanguage()));
                    break;

                case RETRIEVE_CURRENT_STOCK_MEDICINE_LIST:
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getDB().getCurrentMedicineStock(App.getContext().getUserInfo().getUserId(), false));
                    break;

                case RETRIEVE_ONLY_STOCK_MEDICINE_LIST:
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getDB().getCurrentMedicineStock(App.getContext().getUserInfo().getUserId(), true));
                    break;

                case RETRIEVE_REPORT:
                    bundle.putSerializable(TaskKey.DATA0, App.getContext().getDB().getReportResult((Report) param[0]));
                    break;
                case MEDICINE_STOCK:
                    bundle.putSerializable(TaskKey.DATA0, updateCurrentStock(param == null ? null : (ResponseData) param[0]));
                    break;
                case DOWNLOAD_FILE:
                    Utility.downloadCommonFile(context, (String) param[0], (String) param[1]);
                    break;
            }
        } catch (MhealthException e) {
            e.printStackTrace();
            bundle.putSerializable(TaskKey.ERROR_MSG, e.getMessage());
        }
        return null;
    }


//	 public void execute(){
//	    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//				super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//			}else {
//				super.execute();
//		    }
//    }

    public void setParam(Object... param) {
        this.param = param;
    }

    private void dialogClose() {
        if (this.dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

//	@Override
//	protected void onProgressUpdate(Integer... values) {
//		super.onProgressUpdate(values);
//	}

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dialogClose();
        message.setData(bundle);
        if (completeListener != null) {
            completeListener.onComplete(message);
        }
    }

    @Override
    protected void onBackgroundError(Exception e) {

    }

    public void setCompleteListener(OnCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    private boolean processMydata(ResponseData responseData) throws MhealthException {
        if (responseData.getResponseName().contains(RequestName.MY_DATA_WITH_SERVICE)){
            responseData.setResponseName(   RequestName.MY_DATA);
        }
        MyData myData = ModelProvider.getMyData(context, new MyData(), responseData);
        ModelHandler.getInstance(context).handleMydata(myData);
        return true;
    }


    private ArrayList<MedicineInfo> updateCurrentStock(ResponseData responseData) throws MhealthException {
        if (responseData != null) {
            JSONArray medicineList = new JSONArray();
            String medicinelistString = responseData.getData();
            try {
                JSONObject obj = new JSONObject(medicinelistString);
                medicineList = new JSONArray(""+JSONParser.getJsonArray(obj,"medicines_stock"));

            }catch (Exception e){}
             App.getContext().getDB().saveMedicineStock(medicineList);
            return App.getContext().getDB().getCurrentMedicineStock(App.getContext().getUserInfo().getUserId(), false);
        } else {
            return App.getContext().getDB().getCurrentMedicineStock(App.getContext().getUserInfo().getUserId(), false);
        }
    }






    private ArrayList<MedicineInfo> getTodaysMedicineSalesReport(ResponseData responseData) throws MhealthException {
        if (responseData != null) {
            return JSONParser.parseTodaysSalesReportJson(responseData.getData());
        } else {
            return App.getContext().getDB().getTodaysSalesReport();
        }
    }

    private ArrayList<IndividualSalesInfo> getlast30DaysSalesListReport(ResponseData responseData) throws MhealthException {
        if (responseData != null) {
            return JSONParser.parseLast30DaysSalesReportJson(responseData.getData());
        } else {
            return App.getContext().getDB().getLast30DaysSales();
        }
    }

    private ArrayList<MedicineRcvSaleInfo> getLast30daysReceiveSales(ResponseData responseData) throws MhealthException {
        if (responseData != null) {
            return JSONParser.parseLast30DaysRcvSalesReportJson(responseData.getData());
        } else {
            return App.getContext().getDB().getLast30DaysReceivceSaleList();
        }
    }

    private ArrayList<HealthCareReportInfo> getHealthCareRepor(ResponseData responseData) throws MhealthException {
        if (responseData != null) {
            return JSONParser.parseHealthCareReportJson(responseData.getData());
        } else {
            return App.getContext().getDB().getHealthCareReport();
        }
    }

    private ArrayList<BeneficiaryRegistrationState> getBeneficiaryRegistrationReport(ResponseData responseData) throws MhealthException {
        if (responseData != null) {
            return JSONParser.parseBenefRegReportJson(responseData.getData());
        } else {
            return App.getContext().getDB().getBeneficiaryRegistrationReport();
        }
    }

    private ArrayList<UserScheduleInfo> getScheduleListFromDB(Integer data, String code) {
        return App.getContext().getDB().getScheduleList(data, code != null ? code.trim() : "");
    }

    private ArrayList<UserScheduleInfo> processScheduleList(ResponseData data) throws MhealthException {
        return JSONParser.parseScheduleInfosReport(data.getDataJson());
    }

    private ArrayList<Beneficiary> getBeneficiaryList(Object data) throws MhealthException {

        long fcmCode = App.getContext().getUserInfo().getUserId();
        String benefType = "";
        if (data != null && data instanceof ResponseData) {
            processMydata((ResponseData) data);
        } else if (data != null && data instanceof String) {
            benefType = ((String) data).trim();
        }
        ArrayList<Beneficiary> beneficiaryList = App.getContext().getDB().getBeneficiaryList(benefType,fcmCode);



        return beneficiaryList;
    }


    private ArrayList<Beneficiary> getBeneficiaryListWithPagination(Object data,int offset,int limit) throws MhealthException {

        long fcmCode = App.getContext().getUserInfo().getUserId();
        String benefType = "";
        if (data != null && data instanceof ResponseData) {
            processMydata((ResponseData) data);
        } else if (data != null && data instanceof String) {
            benefType = ((String) data).trim();
        }
        ArrayList<Beneficiary> beneficiaryList = App.getContext().getDB().getBeneficiaryListWithPagination(benefType,fcmCode,offset,limit);
        return beneficiaryList;
    }


    private ArrayList<QuestionnaireCategoryInfo> getRetrieveCategoryList(ResponseData responseData) throws MhealthException {
        if (responseData == null) {
            return App.getContext().getDB().getQuestionnaireCategoryList(App.getContext().getAppSettings().getLanguage());
        } else {
            processMydata(responseData);
            return App.getContext().getDB().getQuestionnaireCategoryList(App.getContext().getAppSettings().getLanguage());
        }
    }

    private ArrayList<Beneficiary> retrieveHouseholdMemberListActive(String houseHoldnumber) {
        return App.getContext().getDB().getHouseholdMemberList(houseHoldnumber, true, null, null, null, null, -1, -1, -1);
    }

    private ArrayList<UserScheduleInfo> retrieveTodaysTimeSchedule(Object data) throws MhealthException {
        String schedStatus = "";
        if (data != null) {
            if (data instanceof ArrayList<?>) {
                App.getContext().getDB().updateEventScheduleList((ArrayList<UserScheduleInfo>) data, "Y");
            }
            if (data instanceof ResponseData) {
                processMydata((ResponseData) data);
            }
            if (data instanceof String) {

                schedStatus = (String) data;
            }
        }

        return App.getContext().getDB().getTodaysScheduleList(schedStatus);

    }

    private ArrayList<UserScheduleInfo> retrieveCompleteTimeSchedule(Object data) throws MhealthException {
        String hhNumber = "";
        if (data != null) {
            if (data instanceof ArrayList<?>) {
                App.getContext().getDB().updateEventScheduleList((ArrayList<UserScheduleInfo>) data, "Y");
            }
            if (data instanceof ResponseData) {
                processMydata((ResponseData) data);
            }
            if (data instanceof String) {

                hhNumber = (String) data;
            }
        }

        return App.getContext().getDB().getCompeleteScheduleList(hhNumber);

    }

    private ArrayList<UserScheduleInfo> retrieveAllTimeSchedule(Object data) throws MhealthException {
        String hhNumber = "";
        if (data != null) {
            if (data instanceof ArrayList<?>) {
                App.getContext().getDB().updateEventScheduleList((ArrayList<UserScheduleInfo>) data, "Y");
            }
            if (data instanceof ResponseData) {
                processMydata((ResponseData) data);
            }
            if (data instanceof String) {

                hhNumber = (String) data;
            }
        }

        return App.getContext().getDB().getCompeleteScheduleList(hhNumber);

    }



    public ArrayList<UserScheduleInfo> retrievePatientFollowup(String code) throws MhealthException {
        String hhCode = App.getContext().getUserInfo().getUserCode();
        if (code != null && code.trim().length() > 0) {
            hhCode = hhCode + code;
        }
        return App.getContext().getDB().getPatientFollowupList(hhCode);

    }

    private QuestionnaireList getQuestionnaireList(int categoryId, String langCode, ResponseData responseData) throws MhealthException {
        if (responseData != null) {
            processMydata(responseData);
        }
        ArrayList<QuestionnaireInfo> questionnaireList = App.getContext().getDB().getQuestionnaireList(categoryId, langCode);
        QuestionnaireList allQuestionnaire = new QuestionnaireList();
        allQuestionnaire.setErrorCode(1);
        allQuestionnaire.setAllQuestionnaire(questionnaireList);
        return allQuestionnaire;
    }

    public ArrayList<Household> getHouseholdBasicdataInformationList(ResponseData responseData) {
        if (responseData != null) {
            App.getContext().getDB().updateHouseholdBasicDataSent();
        }
        return App.getContext().getDB().getHouseholdBasicDataList();
    }
}
