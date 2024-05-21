package ngo.friendship.satellite.jsonoperation;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.QUESTION_TYPE;
import ngo.friendship.satellite.constants.QuestionnaireName;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.ScheduleStatus;
import ngo.friendship.satellite.error.ErrorCode;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.model.AppVersionHistory;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.BeneficiaryRegistrationState;
import ngo.friendship.satellite.model.CCSStatus;
import ngo.friendship.satellite.model.ConsumptionDetailsModel;
import ngo.friendship.satellite.model.DataVersionInfo;
import ngo.friendship.satellite.model.DiagnosisInfo;
import ngo.friendship.satellite.model.EventInfo;
import ngo.friendship.satellite.model.FollowupQuestionnaireInfo;
import ngo.friendship.satellite.model.HealthCareReportInfo;
import ngo.friendship.satellite.model.Household;
import ngo.friendship.satellite.model.ImmunizableBeneficiary;
import ngo.friendship.satellite.model.ImmunizationFollowup;
import ngo.friendship.satellite.model.ImmunizationInfo;
import ngo.friendship.satellite.model.ImmunizationService;
import ngo.friendship.satellite.model.ImmunizationTarget;
import ngo.friendship.satellite.model.IndividualSalesInfo;
import ngo.friendship.satellite.model.InputValidation;
import ngo.friendship.satellite.model.LocationModel;
import ngo.friendship.satellite.model.MedicineAdjustmentDetailModel;
import ngo.friendship.satellite.model.MedicineAdjustmentInfoModel;
import ngo.friendship.satellite.model.MedicineConsumptionInfoModel;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.MedicineRcvSaleInfo;
import ngo.friendship.satellite.model.MedicineReceiveModel;
import ngo.friendship.satellite.model.MedicineReceivedDetailModel;
import ngo.friendship.satellite.model.MyData;
import ngo.friendship.satellite.model.NotificationItem;
import ngo.friendship.satellite.model.PatientInterviewDetail;
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedback;
import ngo.friendship.satellite.model.PatientInterviewMaster;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionBranch;
import ngo.friendship.satellite.model.QuestionList;
import ngo.friendship.satellite.model.QuestionOption;
import ngo.friendship.satellite.model.QuestionnaireCategoryInfo;
import ngo.friendship.satellite.model.QuestionnaireDetail;
import ngo.friendship.satellite.model.QuestionnaireInfo;
import ngo.friendship.satellite.model.QuestionnaireList;
import ngo.friendship.satellite.model.ReferralCenterInfo;
import ngo.friendship.satellite.model.RequisitionInfo;
import ngo.friendship.satellite.model.RequisitionMedicineInfo;
import ngo.friendship.satellite.model.SatelliteSessionChwarModel;
import ngo.friendship.satellite.model.SatelliteSessionModel;
import ngo.friendship.satellite.model.ScheduleInfo;
import ngo.friendship.satellite.model.TextRef;
import ngo.friendship.satellite.model.UserInfo;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.model.maternal.MaternalAbortion;
import ngo.friendship.satellite.model.maternal.MaternalBabyInfo;
import ngo.friendship.satellite.model.maternal.MaternalCareInfo;
import ngo.friendship.satellite.model.maternal.MaternalDelivery;
import ngo.friendship.satellite.model.maternal.MaternalInfo;
import ngo.friendship.satellite.model.maternal.MaternalService;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.Utility;


public class JSONParser {

    /**
     * Parses the questionnaire json.
     *
     * @param data the data
     * @return the questionnaire list
     * @throws JSONException the JSON exception
     */
    public static QuestionnaireList parseQuestionnaireJSON(String data) throws MhealthException {
        QuestionnaireList questionerList = new QuestionnaireList();

        try {

            JSONObject jObj = new JSONObject(data);

            if (!jObj.getString("responseCode").equalsIgnoreCase("01")) {
                questionerList.setIsError(true);

                questionerList.setErrorCode(jObj.getInt("errorCode"));
                questionerList.setErrorDesc(jObj.getString("errorDesc"));

                return questionerList;
            }

            questionerList.setIsError(false);

            JSONObject jDataObj = jObj.getJSONObject("data");
            JSONArray jQuestionerArr = jDataObj.getJSONArray("questionnaire");

            ArrayList<QuestionnaireInfo> allQuestioner = new ArrayList<QuestionnaireInfo>();
            for (int i = 0; i < jQuestionerArr.length(); i++) {
                JSONObject jQuestionObj = jQuestionerArr.getJSONObject(i);

                QuestionnaireInfo questionerInfo = new QuestionnaireInfo();
                questionerInfo.setId(jQuestionObj.getInt("id"));
                questionerInfo.setQuestionnaireTitle(jQuestionObj.getString("title"));

                allQuestioner.add(questionerInfo);
            }

            questionerList.setAllQuestionnaire(allQuestioner);
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return questionerList;
    }

    public static QuestionList parseQuestionListJSON(String data) throws MhealthException {

        QuestionList questionList = new QuestionList();
        try {
            JSONObject jObj = new JSONObject(data);
            questionList.setQuestionnaireId(jObj.getInt(KEY.QUESTIONNAIRE_ID));
            questionList.setQuestionnaireTitle(jObj.getString(KEY.TITLE));
            questionList.setQuestionnaireName(jObj.getString("NAME"));
            JSONObject jDataObj = jObj.getJSONObject("QUESTIONNAIRE_DATA");

            questionList.setFirstQuestionKey(jDataObj.getString(KEY.PARENT));


            JSONObject jQuetionnaireObj = jDataObj.getJSONObject(KEY.QUESTIONNAIRE);

            Iterator<String> keyList = jQuetionnaireObj.keys();


            ArrayList<Question> allQuestion = new ArrayList<Question>();
            while (keyList.hasNext()) {
                String key = keyList.next();

                Question question = new Question();
                question.setKey(key);

                JSONObject jQuestionObj = jQuetionnaireObj.getJSONObject(key);
                question.setName(jQuestionObj.getString(KEY.QNAME));
                question.setType(jQuestionObj.getString(KEY.QTYPE));

                question.setCaption(jQuestionObj.getString(KEY.CAPTION));
                question.setHint(jQuestionObj.getString(KEY.HINT));
                question.setReadonly(jQuestionObj.getBoolean(KEY.READONLY));
                question.setRequired(jQuestionObj.getBoolean(KEY.REQUIRED));

                if (jQuestionObj.has(KEY.EXPRESSION))
                    question.setExpression(jQuestionObj.getString(KEY.EXPRESSION));

                if (jQuestionObj.has(KEY.HIDDEN)) {
                    question.setHidden(jQuestionObj.getBoolean(KEY.HIDDEN));
                } else if (questionList.getQuestionnaireName().equals(QuestionnaireName.BENEFICIARY_REGISTRATION)  // for old question
                        && question.getName().equals("HH_NO")) {
                    question.setHidden(true);
                }


                if (jQuestionObj.has(KEY.ADD_MEDICINE))
                    question.setAddMedicine(jQuestionObj.getBoolean(KEY.ADD_MEDICINE));

                if (jQuestionObj.has(KEY.MULTI_LINE))
                    question.setMultiLine(jQuestionObj.getBoolean(KEY.MULTI_LINE));

                if (jQuestionObj.has(KEY.SUGGESTION))
                    question.setSuggestion(jQuestionObj.getBoolean(KEY.SUGGESTION));

                if (jQuestionObj.has(KEY.SUGGESTION_TYPE))
                    question.setSuggestionType(jQuestionObj.getString(KEY.SUGGESTION_TYPE));


                if (jQuestionObj.has(KEY.SAVABLE))
                    question.setSavable(jQuestionObj.getBoolean(KEY.SAVABLE));

                if (jQuestionObj.has(KEY.SCALE)) {
                    question.setScaleTo(Utility.parseLong(jQuestionObj.getString(KEY.SCALE)));
                } else {
                    question.setScaleTo(0);
                }

                if (jQuestionObj.has(KEY.QUALITY)) {
                    question.setQuality(Utility.parseLong(jQuestionObj.getString(KEY.QUALITY)));
                } else {
                    question.setQuality(100);
                }

                if (jQuestionObj.has(KEY.FIRST_VISIBLE))
                    question.setFirstVisible(jQuestionObj.getBoolean(KEY.FIRST_VISIBLE));
                if (jQuestionObj.has(KEY.LAST_VISIBLE))
                    question.setLastVisible(jQuestionObj.getBoolean(KEY.LAST_VISIBLE));
                if (jQuestionObj.has(KEY.MAYBE_LAST_VISIBLE))
                    question.setMaybeLastVisible(jQuestionObj.getBoolean(KEY.MAYBE_LAST_VISIBLE));

                if (jQuestionObj.has(KEY.MEDIA_TYPE))
                    question.setMediaType(jQuestionObj.getString(KEY.MEDIA_TYPE));

                if (jQuestionObj.has(KEY.LOCATION_TYPE))
                    question.setLocationType(jQuestionObj.getString(KEY.LOCATION_TYPE));

                if (jQuestionObj.has(KEY.ACCURACY))
                    question.setAccuracy(Utility.parseLong(jQuestionObj.getString(KEY.ACCURACY)));

                if (jQuestionObj.has(KEY.DELAY))
                    question.setDelay(Utility.parseLong(jQuestionObj.getString(KEY.DELAY)));

                if (jQuestionObj.has(KEY.LOCATION_REQ_INTERVAL))
                    question.setLocationReqInterval(Utility.parseLong(jQuestionObj.getString(KEY.LOCATION_REQ_INTERVAL)));


                if (jQuestionObj.has(KEY.DEFAULT_VAL)) {
                    String defaultValStr = jQuestionObj.getString(KEY.DEFAULT_VAL);
                    Log.e(jQuestionObj.getString(KEY.QNAME), "Default Value: " + defaultValStr);
                    if (!defaultValStr.equalsIgnoreCase("")) {
                        ArrayList<String> defaultValList = new ArrayList<String>();
                        if (question.getType().equals(QUESTION_TYPE.MULTI_SELECT)) {
                            String[] defaultValStrArr = defaultValStr.split(" ");

                            for (int i = 0; i < defaultValStrArr.length; i++) {
                                defaultValList.add(defaultValStrArr[i]);
                            }
                        } else {
                            defaultValList.add(defaultValStr);
                        }
                        question.setDefaultValue(defaultValList);
                    }
                }

                if (jQuestionObj.has(KEY.USER_INPUT)) {
                    String userInputStr = jQuestionObj.getString(KEY.USER_INPUT);


                    if (!userInputStr.equalsIgnoreCase("")) {
                        String[] userInputStrArr = userInputStr.split("\\|");

                        ArrayList<String> userInputList = new ArrayList<String>();
                        if (!question.getType().equals(QUESTION_TYPE.LIST)) {
                            for (int i = 0; i < userInputStrArr.length; i++) {
                                userInputList.add(userInputStrArr[i]);
                            }
                        }
                        question.setUserInput(userInputList);
                    }
                }

                if (jQuestionObj.has(KEY.NUM_TYPE))
                    question.setNumType(jQuestionObj.getString(KEY.NUM_TYPE));

                if (jQuestionObj.has(KEY.TIME_FORMAT))
                    question.setTimeFormat(jQuestionObj.getString(KEY.TIME_FORMAT));

                ///// Retrieve Validation array
                if (jQuestionObj.has(KEY.VALIDATIONS)) {
                    JSONArray jValidationArr = jQuestionObj.getJSONArray(KEY.VALIDATIONS);

                    ArrayList<InputValidation> validationList = new ArrayList<InputValidation>();

                    for (int i = 0; i < jValidationArr.length(); i++) {
                        InputValidation inputValidation = new InputValidation();
                        JSONObject jValidationObj = jValidationArr.getJSONObject(i);

                        inputValidation.setValidationType(jValidationObj.getString(KEY.VALIDATION_TYPE));
                        inputValidation.setValue(jValidationObj.getString(KEY.VALUE));
                        if (jValidationObj.has(KEY.VALIDATION_BASE_DATE)) {
                            inputValidation.setBaseDate(jValidationObj.getString(KEY.VALIDATION_BASE_DATE));
                        }
                        validationList.add(inputValidation);

                    }

                    question.setValidationList(validationList);
                }
                if (jQuestionObj.has(KEY.VALIDATION_MSG))
                    question.setValidationMsg(jQuestionObj.getString(KEY.VALIDATION_MSG));
                ///////////////////

                /////////// Retrieve Options array

                if (jQuestionObj.has(KEY.OPTIONS) && !question.getType().equals(QUESTION_TYPE.REFERRAL_CENTER)) {
                    JSONArray jOptionArr = jQuestionObj.getJSONArray(KEY.OPTIONS);

                    ArrayList<QuestionOption> optionList = new ArrayList<QuestionOption>();
                    ArrayList<MedicineInfo> prescription = new ArrayList<MedicineInfo>();

                    for (int i = 0; i < jOptionArr.length(); i++) {
                        JSONObject jOptionObj = jOptionArr.getJSONObject(i);
                        if (question.getType().equals(QUESTION_TYPE.PRESCRIPTION)) {
                            try {
                                MedicineInfo medicineInfo = new MedicineInfo();
                                JSONObject mJson = new JSONObject(jOptionObj.getString(KEY.VALUE));
                                medicineInfo.setMedId(Integer.parseInt(mJson.getString("MED_ID")));
                                medicineInfo.setMedicineType(mJson.getString("MED_TYPE"));
                                medicineInfo.setGenericName(mJson.getString("GEN_NAME"));
                                medicineInfo.setBrandName(mJson.getString("MED_NAME"));
                                medicineInfo.setRequiredQuantity(mJson.getString("MED_QTY"));
                                medicineInfo.setSoldQuantity(mJson.getString("SALE_QTY"));

                                if (mJson.has("MED_DURATION"))
                                    medicineInfo.setDoseDuration(mJson.getString("MED_DURATION"));
                                if (mJson.has("MTR"))
                                    medicineInfo.setMedicineTakingRule(mJson.getString("MTR"));
                                if (mJson.has("MTR_LBL"))
                                    medicineInfo.setMedicineTakingRuleLabel(mJson.getString("MTR_LBL"));
                                if (mJson.has("MTR_SF"))
                                    medicineInfo.setSmsFormatmedicineTakingRule(mJson.getString("MTR_SF"));
                                if (mJson.has("AFM"))
                                    medicineInfo.setAdviceForMedicine(mJson.getString("AFM"));
                                if (mJson.has("AFM_SF"))
                                    medicineInfo.setSmsFormatadviceForMedicine(mJson.getString("AFM_SF"));
                                if (mJson.has("SF"))
                                    medicineInfo.setSmsFormat(mJson.getString("SF"));
                                if (mJson.has("DAYS")) {
                                    medicineInfo.setDays(mJson.getString("DAYS"));
                                }
                                if (mJson.has("DOSES")) {
                                    medicineInfo.setDoses(mJson.getString("DOSES"));
                                }
                                if (mJson.has("TAKINGTIME")) {
                                    medicineInfo.setTakingTime(mJson.getString("TAKINGTIME"));
                                }


                                prescription.add(medicineInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        QuestionOption option = new QuestionOption();

                        option.setCaption(jOptionObj.getString(KEY.CAPTION));
                        option.setId(Integer.parseInt(jOptionObj.getString(KEY.ID)));
                        option.setValue(jOptionObj.getString(KEY.VALUE));
                        option.setOptionName("option" + option.getId());

                        optionList.add(option);

                    }

                    question.setOptionList(optionList);
                    question.setPrescription(prescription);
                }

                if (question.getType().equals(QUESTION_TYPE.REFERRAL_CENTER)) {
                    ArrayList<ReferralCenterInfo> referralCenterList = App.getContext().getDB().getReferralCenterList("STATE=1");
                    question.setReferralCenterList(referralCenterList);
                }
                /////////////////////////////////

                /////////// Retrieve Branch Item array

                if (jQuestionObj.has(KEY.BRANCH_ITEMS)) {
                    JSONArray jBreanchItemArr = jQuestionObj.getJSONArray(KEY.BRANCH_ITEMS);

                    ArrayList<Integer> branchItemList = new ArrayList<Integer>();

                    for (int i = 0; i < jBreanchItemArr.length(); i++) {

                        branchItemList.add(jBreanchItemArr.getInt(i));
                    }

                    question.setBranchItemList(branchItemList);
                }
                /////////////////////////////////

                //////// Retrieve Branch Information

                if (jQuestionObj.has(KEY.BRANCHES)) {
                    JSONArray jBranchArr = jQuestionObj.getJSONArray(KEY.BRANCHES);

                    ArrayList<QuestionBranch> branchList = new ArrayList<QuestionBranch>();

                    for (int i = 0; i < jBranchArr.length(); i++) {
                        QuestionBranch branch = new QuestionBranch();
                        JSONObject jBranchObj = jBranchArr.getJSONObject(i);

                        if (jBranchObj.has(KEY.CALCVAL))
                            branch.setCalcValue(jBranchObj.getString(KEY.CALCVAL));
                        branch.setNextQuestionKey(jBranchObj.getString(KEY.NEXT_Q));
                        branch.setRule(jBranchObj.getString(KEY.RULE));

                        if (jBranchObj.has(KEY.VALUE)) {
                            branch.setValue(jBranchObj.getString(KEY.VALUE));
                        }

                        branchList.add(branch);
                    }
                    question.setBranchList(branchList);
                }

                ////////////////////////////////////

                allQuestion.add(question);
            }

            questionList.setQuestionList(allQuestion);

        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }


        return questionList;
    }

    public static ResponseData parseLoginResponse(String data) throws MhealthException {

        ResponseData webResponseDataInfo = new ResponseData();
        try {
            JSONObject jObj = new JSONObject(data);

            webResponseDataInfo.setResponseType(jObj.getString("responseType"));
            webResponseDataInfo.setResponseName(jObj.getString("responseName"));
            webResponseDataInfo.setResponseCode(jObj.getString("responseCode"));
            //		webResponseInfo.setResponseTime(jObj.getString("responseTime"));
            webResponseDataInfo.setExecTime(jObj.getString("execTime"));

            if (webResponseDataInfo.getResponseCode().equalsIgnoreCase("00")) {
                webResponseDataInfo.setErrorCode(jObj.getString("errorCode"));
                webResponseDataInfo.setErrorDesc(jObj.getString("errorDesc"));
            } else {
                webResponseDataInfo.setDataLength(jObj.getInt("dataLength"));
                webResponseDataInfo.setData(jObj.getString("data"));
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return webResponseDataInfo;
    }

    public static MyData parseMyDataJSON(Context context, String jsonStr, String requestName) throws MhealthException {

        MyData myData = new MyData();
        try {
            ResponseData responseData = new ResponseData();


            JSONObject jObj = new JSONObject(jsonStr);

            responseData.setResponseType(jObj.getString("responseType"));
            responseData.setResponseName(jObj.getString("responseName"));
            responseData.setResponseCode(jObj.getString("responseCode"));
            //		webResponseInfo.setResponseTime(jObj.getString("responseTime"));
            responseData.setExecTime(jObj.getString("execTime"));

            if (responseData.getResponseCode().equalsIgnoreCase("00")) {
                responseData.setErrorCode(jObj.getString("errorCode"));
                responseData.setErrorDesc(jObj.getString("errorDesc"));
            } else {
                responseData.setDataLength(jObj.getInt("dataLength"));
                responseData.setData(jObj.getString("data"));
            }
            myData.setResponseInfo(responseData);

            if (responseData.getData() != null && !responseData.getData().equalsIgnoreCase("")) {
                JSONObject jDataObj = jObj.getJSONObject("data");

                /////// Referral center list///////////////
                if (requestName.equals(RequestName.MY_DATA) || requestName.equals(RequestName.REFERRAL_CENTERS))
                    myData.setReferraCenterList(jDataObj.getJSONArray("referralCenters"));
                //////////////////////////////////

                /////// Questionnaire List//////
                if (requestName.equals(RequestName.MY_DATA) || requestName.equals(RequestName.QUESTIONNAIRES)) {
                    byte[] key = App.getContext().getUserInfo().getKey(context);
                    myData.setQuestionnaireList(parseQuestionnaireListJSON(key, jDataObj.getJSONArray("questionnaires")));
                }

                /////////////////////

                //////////// Questionnaire category list/////////////////
                if (requestName.equals(RequestName.MY_DATA) || requestName.equals(RequestName.CATEGORIES))
                    myData.setQuestionnaireCategoryList(parseQuestionnaireCategoryListJSON(jDataObj.getJSONArray("categories")));
                //////////////////////////////////

                ///////// Beneficiary List///////////////
                if (requestName.equals(RequestName.MY_DATA) || requestName.equals(RequestName.BENEFICIARIES))
                    myData.setBeneficiaryList(parseBeneficiaryListJSON(context, jDataObj.getJSONArray("beneficiaries")));
                ///////////////////////////////////////

                ///////// Beneficiary List///////////////
                if (requestName.equals(RequestName.MY_DATA)) {
                    myData.setMaternalCareInfos(parseMaternalCareList(jDataObj.getJSONArray("maternalCareList")));
                    myData.setDiagnosisInfos(parseDiagnosisInfoList(jDataObj.getJSONArray("diagnosisInfo")));

                }
                ///////////////////////////////////////

                ///// Medicine List//////////////
                if (requestName.equals(RequestName.MY_DATA) || requestName.equals(RequestName.MEDICINES))
                    myData.setMedicineList(parseMedicineListJSON(jDataObj.getJSONArray("medicines")));
                //////////////////////////////////

                /////////// FCM Profile/////////////
                if (requestName.equals(RequestName.MY_DATA) || requestName.equals(RequestName.FCM_PROFILE))
                    myData.setUserInfo(parseFCMProfileJSON(jDataObj.getJSONObject("fcmProfile"), context));
                ///////////////////////////

                /////////// App Version History /////////////
                if (requestName.equals(RequestName.APP_VERSION_HISTORY))
                    myData.setAppVersionHistory(parseAppVersionHistoryJSON(jDataObj.getJSONObject("APP_VERSION_HISTORY")));
                ///////////////////////////

                /////////// Schedule List /////////////
                if (requestName.equals(RequestName.MY_DATA) || requestName.equals(RequestName.UNDONE_SCHEDULE))
                    myData.setScheduleList(parseScheduleListJSON(jDataObj.getJSONArray("schedList")));
                ///////////////////////////


                /////////// CCS Status/ CCS reason/////////
                if (requestName.equals(RequestName.MY_DATA)) {
                    myData.setCcsStatusList(parseCCSStatusListJSON(jDataObj.getJSONArray("ccsStatuses")));
                    myData.setTextRefList(parseTextRefListJSON(jDataObj.getJSONArray("textRef")));
                    myData.setFcmComfiguration(jDataObj.getJSONArray("fcmConfigurations").toString());
                    myData.setImmunizationList(parseImmunizationListJSON(jDataObj.getJSONArray("immunizationInfo")));
                }

                /////////// growth_measurement_param List /////////////
                if (requestName.equals(RequestName.MY_DATA))
                    myData.setGrowthMeasurementParams(jDataObj.getJSONArray("growthMeasurementParams"));
                ///////////////////////////////////////
                if (jObj.has(KEY.PARAM1)) {
                    JSONObject jParamObj = jObj.getJSONObject(KEY.PARAM1);
                    ArrayList<DataVersionInfo> versionList = new ArrayList<DataVersionInfo>();
                    if (jParamObj.has(KEY.VERSION_NO_BENEFICIARY)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(DBTable.BENEFICIARY);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_BENEFICIARY));
                        versionList.add(dvi);
                    }

                    if (jParamObj.has(KEY.VERSION_NO_CATEGORIES)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(DBTable.QUESTIONNAIRE_CATEGORY);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_CATEGORIES));
                        versionList.add(dvi);
                    }

                    if (jParamObj.has(KEY.VERSION_NO_QUESTIONNAIRE)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(DBTable.QUESTIONNAIRE);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_QUESTIONNAIRE));
                        versionList.add(dvi);
                    }

                    if (jParamObj.has(KEY.VERSION_NO_REFERRAL_CENTER)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(DBTable.REFERRAL_CENTER);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_REFERRAL_CENTER));
                        versionList.add(dvi);
                    }

                    if (jParamObj.has(KEY.VERSION_NO_USER_SCHEDULE)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(DBTable.USER_SCHEDULE);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_USER_SCHEDULE));
                        versionList.add(dvi);
                    }

                    if (jParamObj.has(KEY.VERSION_NO_USER_INFO)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(DBTable.USER_INFO);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_USER_INFO));
                        versionList.add(dvi);
                    }

                    if (jParamObj.has(KEY.VERSION_NO_MEDICINE)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(DBTable.MEDICINE);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_MEDICINE));
                        versionList.add(dvi);
                    }
                    if (jParamObj.has(KEY.VERSION_NO_DIAGNOSIS_INFO)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(DBTable.DIAGNOSIS_INFO);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_DIAGNOSIS_INFO));
                        versionList.add(dvi);
                    }

                    if (jParamObj.has(KEY.VERSION_NO_GROWTH_MEASUREMENT_PARAM)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(DBTable.GROWTH_MEASUREMENT_PARAM);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_GROWTH_MEASUREMENT_PARAM));
                        versionList.add(dvi);
                    }

                    if (jParamObj.has(KEY.VERSION_NO_MATERNAL_CARE_INFO)) {
                        DataVersionInfo dvi = new DataVersionInfo();
                        dvi.setTableName(MaternalCareInfo.MODEL_NAME);
                        dvi.setVersion(jParamObj.getLong(KEY.VERSION_NO_MATERNAL_CARE_INFO));
                        versionList.add(dvi);
                    }

                    myData.setVersionList(versionList);
                }
            }

        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return myData;
    }


    public static ArrayList<CCSStatus> parseCCSStatusListJSON(JSONArray jCCSStatusArr) throws MhealthException {
        ArrayList<CCSStatus> ccsStatusList = new ArrayList<CCSStatus>();

        try {
            for (int i = 0; i < jCCSStatusArr.length(); i++) {
                JSONObject ccsStatusObj = jCCSStatusArr.getJSONObject(i);

                CCSStatus ccsStatusInfo = new CCSStatus();
                ccsStatusInfo.setStatusCaption(ccsStatusObj.getString("STATUS_CAPTION"));
                ccsStatusInfo.setStatusId(ccsStatusObj.getInt("STATUS_ID"));
                ccsStatusInfo.setStatusName(ccsStatusObj.getString("STATUS_NAME"));

                ccsStatusList.add(ccsStatusInfo);

            }

        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return ccsStatusList;
    }


    public static String getFcmConfigValue(JSONArray jconfigArr, String name, String key) throws MhealthException {
        try {
            for (int index = 0; index < jconfigArr.length(); index++) {
                JSONObject jConfigObj = jconfigArr.getJSONObject(index);

                if (jConfigObj.has("name") && jConfigObj.getString("name").trim().equals(name.trim())) {
                    JSONArray jItemArr = jConfigObj.getJSONArray("items");
                    for (int i = 0; i < jItemArr.length(); i++) {
                        JSONObject jItemObj = jItemArr.getJSONObject(i);
                        if (jItemObj.has("key") && jItemObj.getString("key").equals(key)) {
                            return jItemObj.getString("value");
                        }
                    }

                }
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION KEY NOT FOUND");
    }

    /**
     * Parse Immunization information array and  parse information.
     *
     * @param jImmuArr The JSON array
     * @return The immnunization list
     * @throws JSONException the JSON exception
     */
    public static ArrayList<ImmunizationInfo> parseImmunizationListJSON(JSONArray jImmuArr) throws MhealthException {
        ArrayList<ImmunizationInfo> immuList = new ArrayList<ImmunizationInfo>();
        try {
            for (int i = 0; i < jImmuArr.length(); i++) {
                JSONObject jImmuObj = jImmuArr.getJSONObject(i);
                ImmunizationInfo immuInfo = new ImmunizationInfo();
                immuInfo.setName(jImmuObj.getString("IMMU_NAME"));
                immuInfo.setType(jImmuObj.getString("IMMU_TYPE"));
                immuInfo.setStartIndex(jImmuObj.getInt("START_INDEX"));
                immuInfo.setNumberOfDose(jImmuObj.getInt("NO_OF_DOSE"));
                immuInfo.setId(jImmuObj.getLong("IMMU_ID"));
                immuInfo.setSortOrder(jImmuObj.getLong("SORT_ORDER"));
                if (jImmuObj.has("DESC_ORG_CARD"))
                    immuInfo.setDescFromOrg(jImmuObj.getString("DESC_ORG_CARD"));
                if (jImmuObj.has("DESC_GOVT_CARD"))
                    immuInfo.setDescFromGov(jImmuObj.getString("DESC_GOVT_CARD"));

                immuList.add(immuInfo);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return immuList;
    }


    public static ArrayList<ImmunizationFollowup> parseImmunizationFollowUpList(JSONArray jImmuArr) throws MhealthException {
        ArrayList<ImmunizationFollowup> followups = new ArrayList<ImmunizationFollowup>();
        try {
            for (int i = 0; i < jImmuArr.length(); i++) {
                JSONObject jImmuObj = jImmuArr.getJSONObject(i);
                ImmunizationFollowup followup = new ImmunizationFollowup();
                followup.setBenefId(jImmuObj.getInt(Column.BENEF_ID));
                followup.setBenefCode(jImmuObj.getString(Column.BENEF_CODE));

                followup.setFollowupType(jImmuObj.getInt(Column.FOLLOWUP_TYPE));
                try {
                    String dateStr = jImmuObj.getString(Column.CREATE_DATE);
                    followup.setCreateDate(Utility.getMillisecondFromDate(dateStr, Constants.DATE_FORMAT_YYYY_MM_DD));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (jImmuObj.has("TRANS_REF")) followup.setTransRef(jImmuObj.getLong("TRANS_REF"));


                if (jImmuObj.has(Column.FOLLOWUP_DATE))
                    followup.setFollowupDate(jImmuObj.getInt(Column.FOLLOWUP_DATE));

                followup.setImmuFollowupId(jImmuObj.getInt(Column.IMMU_FOLLOWUP_ID));
                followup.setImmuType(jImmuObj.getString(Column.IMMU_TYPE));
                if (jImmuObj.has(Column.REASON_ID)) {
                    followup.setReasonId(jImmuObj.getInt(Column.REASON_ID));
                }

                followups.add(followup);
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return followups;
    }

    public static ArrayList<ImmunizableBeneficiary> parseImmunizableBeneficiaryList(JSONArray jImmuArr) throws MhealthException {
        ArrayList<ImmunizableBeneficiary> beneficiaries = new ArrayList<ImmunizableBeneficiary>();
        try {
            for (int i = 0; i < jImmuArr.length(); i++) {
                JSONObject jImmuObj = jImmuArr.getJSONObject(i);
                ImmunizableBeneficiary beneficiary = new ImmunizableBeneficiary();
                beneficiary.setBenefId(jImmuObj.getLong(Column.BENEF_ID));
                beneficiary.setBenefCode(jImmuObj.getString(Column.BENEF_CODE));
                beneficiary.setAgeDayWhenDetected(jImmuObj.getLong(Column.AGE_DAY_WHEN_DETECTED));
                beneficiary.setCreateDate(jImmuObj.getString(Column.CREATE_DATE));
                beneficiary.setImmuBenefId(jImmuObj.getLong(Column.IMMU_BENEF_ID));
                beneficiary.setImmuType(jImmuObj.getString(Column.IMMU_TYPE));
                if (jImmuObj.has(Column.IMMU_COMPLETE_DATE)) {
                    String dateStr = jImmuObj.getString(Column.IMMU_COMPLETE_DATE);
                    beneficiary.setImmuCompleteDate(Utility.getMillisecondFromDate(dateStr, Constants.DATE_FORMAT_YYYY_MM_DD));
                }
                if (jImmuObj.has(Column.REASON_ID)) {
                    beneficiary.setReasonId(jImmuObj.getLong(Column.REASON_ID));
                }
                beneficiaries.add(beneficiary);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.PARSE_EXCEPTION, "PARSE EXCEPTION", e);
        }
        return beneficiaries;
    }


    public static ArrayList<ImmunizationTarget> parseImmunizationTargetList(JSONArray jImmuArr) throws MhealthException {
        ArrayList<ImmunizationTarget> targets = new ArrayList<ImmunizationTarget>();
        try {
            for (int i = 0; i < jImmuArr.length(); i++) {
                JSONObject jImmuObj = jImmuArr.getJSONObject(i);
                ImmunizationTarget target = new ImmunizationTarget();
                target.setBenefId(jImmuObj.getLong(Column.BENEF_ID));
                target.setBenefCode(jImmuObj.getString(Column.BENEF_CODE));
                if (jImmuObj.has(Column.EVENT_ID)) {
                    target.setEventId(jImmuObj.getLong(Column.EVENT_ID));
                }
                target.setImmuType(jImmuObj.getString(Column.IMMU_TYPE));

                String dateStr = jImmuObj.getString(Column.SESSION_DATE);
                target.setSessionDate(Utility.getMillisecondFromDate(dateStr, Constants.DATE_FORMAT_YYYY_MM_DD));
                targets.add(target);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.PARSE_EXCEPTION, "PARSE EXCEPTION", e);

        }
        return targets;
    }

    public static ArrayList<EventInfo> parseEventInfoList(JSONArray jImmuArr) throws MhealthException {
        ArrayList<EventInfo> infos = new ArrayList<EventInfo>();
        try {
            for (int i = 0; i < jImmuArr.length(); i++) {
                JSONObject jImmuObj = jImmuArr.getJSONObject(i);
                EventInfo info = new EventInfo();
                info.setEventId(jImmuObj.getLong(Column.EVENT_ID));


                if (jImmuObj.has(Column.CREATED_BY))
                    info.setCreatedBy(jImmuObj.getLong(Column.CREATED_BY));

                if (jImmuObj.has(Column.EVENT_DATE))
                    info.setEventDate(Utility.getMillisecondFromDate(jImmuObj.getString(Column.EVENT_DATE), Constants.DATE_FORMAT_YYYY_MM_DD));

                if (jImmuObj.has(Column.EVENT_DESC))
                    info.setEventDesc(jImmuObj.getString(Column.EVENT_DESC));

                if (jImmuObj.has(Column.EVENT_NAME))
                    info.setEventName(jImmuObj.getString(Column.EVENT_NAME));

                if (jImmuObj.has(Column.LOCATION_ID))
                    info.setLocationId(jImmuObj.getLong(Column.LOCATION_ID));

                if (jImmuObj.has(Column.STATE)) info.setState(jImmuObj.getLong(Column.STATE));

                if (jImmuObj.has(Column.TYPE)) info.setType(jImmuObj.getString(Column.TYPE));

                if (jImmuObj.has(Column.CREATE_DATE))
                    info.setCreateDate(Utility.getMillisecondFromDate(jImmuObj.getString(Column.CREATE_DATE), Constants.DATE_FORMAT_YYYY_MM_DD));

                infos.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.PARSE_EXCEPTION, "PARSE EXCEPTION", e);

        }
        return infos;
    }


    public static ArrayList<Household> parseHouseholdList(JSONArray jImmuArr) throws MhealthException {
        ArrayList<Household> households = new ArrayList<Household>();
        try {
            for (int i = 0; i < jImmuArr.length(); i++) {
                JSONObject jImmuObj = jImmuArr.getJSONObject(i);
                Household household = new Household();

                if (jImmuObj.has(Column.HH_ID)) household.setHhId(jImmuObj.getLong(Column.HH_ID));

                if (jImmuObj.has(Column.HH_NUMBER))
                    household.setHhNumber(jImmuObj.getString(Column.HH_NUMBER));


                if (jImmuObj.has(Column.LOCATION_ID))
                    household.setLocationId(jImmuObj.getLong(Column.LOCATION_ID));

                if (jImmuObj.has(Column.HH_GRADE))
                    household.setHhGrade(jImmuObj.getString(Column.HH_GRADE));

                if (jImmuObj.has(Column.NO_OF_FAMILY_MEMBER))
                    household.setNoOfFamilyMember(jImmuObj.getLong(Column.NO_OF_FAMILY_MEMBER));

                if (jImmuObj.has(Column.MONTHLY_FAMILY_EXPENDITURE)) {
                    household.setMonthlyFamilyExpenditure(jImmuObj.getString(Column.MONTHLY_FAMILY_EXPENDITURE));
                }
                if (jImmuObj.has(Column.HH_ADULT_WOMEN)) {
                    household.setHhAdultWomen(jImmuObj.getLong(Column.HH_ADULT_WOMEN));
                }
                if (jImmuObj.has(Column.HH_CHARACTER)) {
                    household.setHhCharacter(jImmuObj.getString(Column.HH_CHARACTER));
                }

                if (jImmuObj.has(Column.CREATE_DATE))
                    household.setCreateDate(Utility.getMillisecondFromDate(jImmuObj.getString(Column.CREATE_DATE), Constants.DATE_FORMAT_YYYY_MM_DD));

                if (jImmuObj.has(Column.REG_DATE))
                    household.setRegDate(Utility.getMillisecondFromDate(jImmuObj.getString(Column.REG_DATE), Constants.DATE_FORMAT_YYYY_MM_DD));


                if (jImmuObj.has(Column.LATITUDE))
                    household.setLatitudeStr(jImmuObj.getString(Column.LATITUDE));

                if (jImmuObj.has(Column.LONGITUDE))
                    household.setLongitudeStr(jImmuObj.getString(Column.LONGITUDE));


                if (jImmuObj.has(Column.REF_DATA_ID))
                    household.setRefDataId(jImmuObj.getLong(Column.REF_DATA_ID));

                if (jImmuObj.has(Column.STATE)) household.setState(jImmuObj.getLong(Column.STATE));

                if (jImmuObj.has(Column.UPDATE_HISTORY))
                    household.setUpdateHistory(jImmuObj.getString(Column.UPDATE_HISTORY));

                households.add(household);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.PARSE_EXCEPTION, "PARSE EXCEPTION", e);

        }
        return households;
    }


    public static ArrayList<ImmunizationService> parseImmunizationServiceList(JSONArray jImmuArr) throws MhealthException {
        ArrayList<ImmunizationService> services = new ArrayList<ImmunizationService>();
        try {
            for (int i = 0; i < jImmuArr.length(); i++) {
                JSONObject jImmuObj = jImmuArr.getJSONObject(i);
                ImmunizationService service = new ImmunizationService();
                service.setBenefId(jImmuObj.getLong(Column.BENEF_ID));
                service.setBenefCode(jImmuObj.getString(Column.BENEF_CODE));
                service.setAgeDayWhenImmunized(jImmuObj.getLong(Column.AGE_DAY_WHEN_IMMUNIZED));
                String dateStr = jImmuObj.getString(Column.IMMU_DATE);
                service.setImmuDate(Utility.getMillisecondFromDate(dateStr, Constants.DATE_FORMAT_YYYY_MM_DD));
                service.setImmuId(jImmuObj.getLong(Column.IMMU_ID));
                service.setImmuServId(jImmuObj.getLong(Column.IMMU_SERV_ID));
                service.setImmuType(jImmuObj.getString(Column.IMMU_TYPE));
                service.setInterviewId(jImmuObj.getLong(Column.INTERVIEW_ID));
                if (jImmuObj.has("TRANS_REF")) service.setTransRef(jImmuObj.getLong("TRANS_REF"));
                services.add(service);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.PARSE_EXCEPTION, "PARSE EXCEPTION", e);
        }
        return services;
    }


    public static ArrayList<PatientInterviewMaster> parsePatientInterviewMasterList(JSONArray jImmuArr) throws MhealthException {
        ArrayList<PatientInterviewMaster> pims = new ArrayList<PatientInterviewMaster>();
        try {
            for (int i = 0; i < jImmuArr.length(); i++) {
                JSONObject jImmuObj = jImmuArr.getJSONObject(i);
                PatientInterviewMaster pim = new PatientInterviewMaster();
                pim.setInterviewId(jImmuObj.getLong("INTERVIEW_ID"));
                pim.setUserId(jImmuObj.getLong("USER_ID"));
                pim.setBenefCode(jImmuObj.getString("BENEF_CODE"));
                pim.setQuestionnaireId(jImmuObj.getLong("QUESTIONNAIRE_ID"));
                pim.setStartTime(Utility.getMillisecondFromDate(jImmuObj.getString("START_TIME"), Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
                pim.setParentInterviewId(jImmuObj.getLong("PARENT_INTERVIEW_ID"));
                pim.setRecordDate(JSONParser.getString(jImmuObj,"RECORD_DATE"));

                if (jImmuObj.has("FCM_INTERVIEW_ID")) {
                    pim.setFcmInterviewId(jImmuObj.getLong("FCM_INTERVIEW_ID"));
                }
                if (jImmuObj.has("BENEF_NAME")) {
                    pim.setBenefName(jImmuObj.getString("BENEF_NAME"));
                }
                if (jImmuObj.has("AGE_IN_DAY")) {
                    pim.setAgeInDay(jImmuObj.getLong("AGE_IN_DAY"));
                }
                if (jImmuObj.has("BENEF_ADDRESS")) {
                    pim.setBenefAddress(jImmuObj.getString("BENEF_ADDRESS"));
                }
                if (jImmuObj.has("GENDER")) {
                    pim.setGender(jImmuObj.getString("GENDER"));
                }



                if (jImmuObj.has("QUESTION_ANSWER_JSON"))
                    pim.setQuestionAnsJson(jImmuObj.getString("QUESTION_ANSWER_JSON"));

                if (jImmuObj.has("TRANS_REF")) pim.setTransRef(jImmuObj.getLong("TRANS_REF"));

                if (jImmuObj.has("PRIORITY")) {
                    pim.setPriority(jImmuObj.getInt("PRIORITY"));
                }

                if (jImmuObj.has("REF_CENTER_ID"))
                    pim.setRefCenterId(jImmuObj.getInt("REF_CENTER_ID"));
                if (jImmuObj.has("IS_FEEDBACK")) pim.setFeedBack(jImmuObj.getInt("IS_FEEDBACK"));

                if (jImmuObj.has(Column.BENEF_NAME))
                    pim.setBenefName(jImmuObj.getString(Column.BENEF_NAME));

                if (jImmuObj.has("patientInterviewDetails")) {
                    JSONArray qDtlJsonArray = jImmuObj.getJSONArray("patientInterviewDetails");

                    ArrayList<PatientInterviewDetail> pids = new ArrayList<PatientInterviewDetail>();
                    for (int j = 0; j < qDtlJsonArray.length(); j++) {
                        JSONObject dtlJsonOb = qDtlJsonArray.getJSONObject(j);
                        PatientInterviewDetail pid = new PatientInterviewDetail();
                        pid.setInterviewDtlId(dtlJsonOb.getLong(Column.INTERVIEW_DTL_ID));
                        pid.setInterviewId(dtlJsonOb.getLong(Column.INTERVIEW_ID));
                        pid.setqId(dtlJsonOb.getLong(Column.Q_ID));
                        pid.setAnswer(dtlJsonOb.getString(Column.ANSWER));
                        if (dtlJsonOb.has(Column.TRANS_REF))
                            pid.setTransRef(dtlJsonOb.getLong(Column.TRANS_REF));

                        pids.add(pid);
                    }
                    pim.setDetails(pids);
                }

                pims.add(pim);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", e);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.PARSE_EXCEPTION, "PARSE EXCEPTION", e);
        }
        return pims;
    }


    public static ArrayList<NotificationItem> parseNotificationMasterList(JSONArray jImmuArr) throws MhealthException {
        ArrayList<NotificationItem> pims = new ArrayList<NotificationItem>();

        for (int i = 0; i < jImmuArr.length(); i++) {
            try {
                JSONObject jImmuObj = jImmuArr.getJSONObject(i);
                NotificationItem pim = new NotificationItem();
                pim.setNotificationId(jImmuObj.getLong("NOTIFICATION_ID"));
                if (jImmuObj.has("TITLE")) pim.setTitle(jImmuObj.getString("TITLE"));
                if (jImmuObj.has("CONTENT")) pim.setContent(jImmuObj.getString("CONTENT"));
                if (jImmuObj.has("NOTIFICATION_TYPE"))
                    pim.setNotificationType(jImmuObj.getString("NOTIFICATION_TYPE"));
                if (jImmuObj.has("NOTIFICATION_STATUS"))
                    pim.setNotificationStatus(jImmuObj.getLong("NOTIFICATION_STATUS"));
                if (jImmuObj.has("VIEW_TIME"))
                    pim.setViewTime(JSONParser.getString(jImmuObj, "VIEW_TIME", ""));
                if (jImmuObj.has("BENEF_CODE")) pim.setBenefCode(jImmuObj.getString("BENEF_CODE"));
                if (jImmuObj.has("ORG_ID")) pim.setOrgId(jImmuObj.getLong("ORG_ID"));
                if (jImmuObj.has("NOTIFICATION_TIME"))
                    pim.setNotificationTime(jImmuObj.getString("NOTIFICATION_TIME"));
                if (jImmuObj.has("STATE")) pim.setState(jImmuObj.getInt("STATE"));
                if (jImmuObj.has("UPDATE_SOURCE"))
                    pim.setUpdateSource(jImmuObj.getString("UPDATE_SOURCE"));
                if (jImmuObj.has("DESTINATION_PATH"))
                    pim.setDestinationPath(jImmuObj.getString("DESTINATION_PATH"));
                if (jImmuObj.has("DESTINATION_KEY"))
                    pim.setDestinationKey(jImmuObj.getString("DESTINATION_KEY"));
                if (jImmuObj.has("NOTIFICATION_ICON"))
                    pim.setNotificationIcon(jImmuObj.getString("NOTIFICATION_ICON"));
                if (jImmuObj.has("VERSION_NO")) pim.setVersionNo(jImmuObj.getLong("VERSION_NO"));
                pims.add(pim);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", e);
            }
        }

        return pims;
    }

    // TEXT_REF

    /**
     * Parses the reason list json.
     *
     * @param jCCSReasonArr the j ccs reason arr
     * @return the array list
     * @throws JSONException the JSON exception
     */
    public static ArrayList<TextRef> parseTextRefListJSON(JSONArray jCCSReasonArr) throws MhealthException {
        ArrayList<TextRef> textRefs = new ArrayList<TextRef>();
        try {
            for (int i = 0; i < jCCSReasonArr.length(); i++) {
                JSONObject ccsStatusObj = jCCSReasonArr.getJSONObject(i);
                TextRef textRef = new TextRef();
                textRef.setTextRefId(ccsStatusObj.getInt("TEXT_REF_ID"));
                textRef.setTextName(ccsStatusObj.getString("TEXT_NAME"));
                textRef.setTextCaption(ccsStatusObj.getString("TEXT_CAPTION"));
                textRef.setTextCateogry(ccsStatusObj.getString("TEXT_CATEGORY"));
                if (ccsStatusObj.has("SORT_ORDER")) {
                    textRef.setSortOrder(ccsStatusObj.getLong("SORT_ORDER"));
                }
                textRefs.add(textRef);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return textRefs;
    }

    /**
     * Parse questionnaire category JSON array and prepare questionnaire category list.
     *
     * @param jQuestionnaireCategoryArr is the JSON array which will be parsed to questionnaire category list
     * @return The questionnaire category list
     * @throws JSONException the JSON exception
     */
    public static ArrayList<QuestionnaireCategoryInfo> parseQuestionnaireCategoryListJSON(JSONArray jQuestionnaireCategoryArr) throws MhealthException {
        ArrayList<QuestionnaireCategoryInfo> categoryList = new ArrayList<QuestionnaireCategoryInfo>();
        try {
            for (int i = 0; i < jQuestionnaireCategoryArr.length(); i++) {
                JSONObject jQuestionnaireCategoryObj = jQuestionnaireCategoryArr.getJSONObject(i);

                QuestionnaireCategoryInfo categoryInfo = new QuestionnaireCategoryInfo();
                categoryInfo.setCategoryCaption(jQuestionnaireCategoryObj.getString("CATEGORY_CAPTION"));
                categoryInfo.setCategoryId(jQuestionnaireCategoryObj.getInt("CATEGORY_ID"));
                categoryInfo.setState(jQuestionnaireCategoryObj.getInt("STATE"));

                if (jQuestionnaireCategoryObj.has("SORT_ORDER")) {
                    categoryInfo.setSortOrder(jQuestionnaireCategoryObj.getLong("SORT_ORDER"));
                }

                categoryInfo.setCategoryName(jQuestionnaireCategoryObj.getString("CATEGORY_NAME"));

                categoryInfo.setIcon(jQuestionnaireCategoryObj.getString("ICON"));

                categoryList.add(categoryInfo);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return categoryList;
    }

    /**
     * Parse questionnaire JSON array and prepare questionnaire list.
     *
     * @param jQuestionnaireArr is the JSON array which will be parsed to questionnaire list
     * @return The questionnaire list
     */
    public static ArrayList<QuestionnaireInfo> parseQuestionnaireListJSON(byte[] key, JSONArray jQuestionnaireArr) throws MhealthException {


        ArrayList<QuestionnaireInfo> questionnaireList = new ArrayList<QuestionnaireInfo>();
        try {
            for (int i = 0; i < jQuestionnaireArr.length(); i++) {
                String encodeddata = jQuestionnaireArr.getString(i);
                // for Encryption to Decryption
                //JSONObject plainObj=new JSONObject(new String(SecurityUtil.decrypt(key, Base64.decode(encodeddata))));
                // without Encryption
                JSONObject plainObj = new JSONObject(encodeddata);
                //JSONObject plainObj=jQuestionnaireArr.getJSONObject(i);
                QuestionnaireInfo questionnaireInfo = new QuestionnaireInfo();
                questionnaireInfo.setCategoryId(plainObj.getInt("CATEGORY_ID"));
                questionnaireInfo.setId(plainObj.getInt("QUESTIONNAIRE_ID"));
                questionnaireInfo.setQuestionnaireTitle(plainObj.getString("TITLE"));
                questionnaireInfo.setPromptForBeneficiary(plainObj.getLong("P_F_B"));
                questionnaireInfo.setBenefSelectionCriteria(plainObj.getString("BENEF_SELECTION_CRITERIA"));
                questionnaireInfo.setQuestionnaireName(plainObj.getString("NAME"));
                if (plainObj.has(KEY.SINGLE_PG_FORM_VIEW)) {
                    questionnaireInfo.setSinglePgFormView(plainObj.getString(KEY.SINGLE_PG_FORM_VIEW));
                }

                //questionnaireInfo.setQuestionnaireJSON(jQuestionnaireArr.getJSONObject(i).getString("QUESTIONNAIRE_DATA"));
                questionnaireInfo.setQuestionnaireJSON(encodeddata);
                questionnaireInfo.setState(plainObj.getInt("STATE"));
                questionnaireInfo.setSortOrder(plainObj.getLong("SORT_ORDER"));
                questionnaireInfo.setVisibileInCategory(plainObj.getInt("VISIBILE_IN_CATEGORY"));
                questionnaireInfo.setLangCode(plainObj.getString("LANG_CODE"));

                questionnaireInfo.setIcon(plainObj.getString("ICON"));

                if (plainObj.has(Column.SERVICE_CATEGORY_ID)) {
                    questionnaireInfo.setServiceCategoryId(plainObj.getLong(Column.SERVICE_CATEGORY_ID));
                }

                if (plainObj.has(Column.PARAM_1) && plainObj.getString(Column.PARAM_1) != null) {
                    questionnaireInfo.setParam1(plainObj.getString(Column.PARAM_1));
                }

                if (plainObj.has(Column.PARAM_2) && plainObj.getString(Column.PARAM_2) != null) {
                    questionnaireInfo.setParam2(plainObj.getString(Column.PARAM_2));
                }

                if (plainObj.has("followupQuestionnaires")) {
                    //Log.e("Questionnaire", jQuestionnaireArr.getJSONObject(i).getJSONArray("followupQuestionnaires").toString());
                    JSONArray jFollowupQuestionnaireArr = plainObj.getJSONArray("followupQuestionnaires");

                    ArrayList<FollowupQuestionnaireInfo> followupQuestionnaireList = new ArrayList<FollowupQuestionnaireInfo>();
                    for (int j = 0; j < jFollowupQuestionnaireArr.length(); j++) {
                        JSONObject jFollowupQuestionnaireObj = jFollowupQuestionnaireArr.getJSONObject(j);

                        FollowupQuestionnaireInfo followupQuestionnaireInfo = new FollowupQuestionnaireInfo();

                        followupQuestionnaireInfo.setParentQuestionnaireId(questionnaireInfo.getId());
                        followupQuestionnaireInfo.setFollowupQuestionnaireId(jFollowupQuestionnaireObj.getLong("FOLLOWUP_QUESTIONNAIRE_ID"));
                        followupQuestionnaireInfo.setSortOrder(jFollowupQuestionnaireObj.getInt("SORT_ORDER"));

                        followupQuestionnaireList.add(followupQuestionnaireInfo);
                    }
                    questionnaireInfo.setFollowupQuestionnaireList(followupQuestionnaireList);
                }


                if (plainObj.has("questionnaireDetails")) {
                    JSONArray qDtlJsonArray = plainObj.getJSONArray("questionnaireDetails");

                    ArrayList<QuestionnaireDetail> qDtls = new ArrayList<QuestionnaireDetail>();
                    for (int j = 0; j < qDtlJsonArray.length(); j++) {
                        JSONObject dtlJsonOb = qDtlJsonArray.getJSONObject(j);
                        QuestionnaireDetail qDtl = new QuestionnaireDetail();
                        qDtl.setQId(dtlJsonOb.getLong(Column.Q_ID));
                        qDtl.setQuestionnaireId(questionnaireInfo.getId());
                        qDtl.setQuestionId(dtlJsonOb.getLong(Column.QUESTION_ID));
                        qDtl.setqType(dtlJsonOb.getString(Column.Q_TYPE));
                        qDtl.setQName(dtlJsonOb.getString(Column.Q_NAME));
                        qDtl.setqTitle(dtlJsonOb.getString(Column.Q_TITLE));
                        qDtl.setqStatus(dtlJsonOb.getInt(Column.Q_STATUS));
                        qDtl.setShowableOutput(dtlJsonOb.getInt(Column.SHOWABLE_OUTPUT));
                        qDtls.add(qDtl);
                    }
                    questionnaireInfo.setQuestionnaireDetails(qDtls);
                }

                questionnaireList.add(questionnaireInfo);

            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
//		} catch (IOException e) {
//			throw new MhealthException(ErrorCode.IO_EXCEPTION,"IO EXCEPTION", e);
        } catch (Exception e) {
            throw new MhealthException(ErrorCode.IO_EXCEPTION, "IO EXCEPTION", e);
        }

        return questionnaireList;
    }


    /**
     * Parse medicine list JSON array and prepare medicine list.
     *
     * @param jMedicineArr is the JSON array which will be parsed to medicine list
     * @return The medicine list
     * @throws JSONException the JSON exception
     */
    public static ArrayList<MedicineInfo> parseMedicineListJSON(JSONArray jMedicineArr) throws MhealthException {
        ArrayList<MedicineInfo> medicineList = new ArrayList<MedicineInfo>();
        try {
            for (int i = 0; i < jMedicineArr.length(); i++) {
                JSONObject jMedObj = jMedicineArr.getJSONObject(i);
                MedicineInfo medicineInfo = new MedicineInfo();
                medicineInfo.setMedId(jMedObj.getInt("MEDICINE_ID"));

                medicineInfo.setGenericName(jMedObj.getString("GENERIC_NAME"));
                medicineInfo.setBrandName(jMedObj.getString("BRAND_NAME"));
                medicineInfo.setMeasureUnit(jMedObj.getString("MEASURE_UNIT"));
                medicineInfo.setUnitPurchasePrice(jMedObj.getDouble("UNIT_PURCHASE_PRICE"));
                medicineInfo.setUnitSalesPrice(jMedObj.getDouble("UNIT_SALES_PRICE"));
                medicineInfo.setMinimumStockQuantity(jMedObj.getInt("MIN_QTY"));
                medicineInfo.setMedicineType(jMedObj.getString("TYPE"));


                medicineInfo.setState(jMedObj.getLong("STATE"));

                if (jMedObj.has("STRENGTH")) {
                    medicineInfo.setStrength((float) jMedObj.getDouble("STRENGTH"));
                }

                if (jMedObj.has("BOX_SIZE")) {
                    medicineInfo.setBoxSize(jMedObj.getLong("BOX_SIZE"));
                }
                if (jMedObj.has("ACCESS_TYPE")) {
                    medicineInfo.setAccessType(jMedObj.getString("ACCESS_TYPE"));
                }
                if (jMedObj.has("UNIT_TYPE")) {
                    medicineInfo.setUnitType(jMedObj.getString("UNIT_TYPE"));
                }

                // medicineInfo.setCurrentStockQuantity(400);

                medicineList.add(medicineInfo);

                //			Log.e("Medicine", medicineInfo.getBrandName());
            }
        } catch (JSONException exception) {
            exception.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return medicineList;
    }


    public static UserInfo parseFCMProfileJSON(JSONObject data, Context context) throws MhealthException {
        UserInfo userInfo = new UserInfo();
        try {
            if (data.length() > 0) {

                userInfo.setUserId(data.getLong(Column.USER_ID));
                userInfo.setUserName(data.getString(Column.USER_NAME));
                userInfo.setUserCode(data.getString(Column.USER_LOGIN_ID));

                userInfo.setOrgId(data.getLong(Column.ORG_ID));
                userInfo.setOrgCode(data.getString(Column.ORG_CODE));
                userInfo.setOrgName(JSONParser.getString(data, Column.ORG_NAME));
                userInfo.setOrgDesc(JSONParser.getString(data, Column.ORG_DESC));
                userInfo.setOrgAddress(JSONParser.getString(data, Column.ORG_ADDRESS));
                userInfo.setOrgCountry(JSONParser.getString(data, Column.ORG_COUNTRY));
                userInfo.setHeaderSmallLogoPath(JSONParser.getString(data, Column.HEADER_SMALL_LOGO_PATH));
                userInfo.setLoginImagePathMobile(JSONParser.getString(data, Column.LOGIN_IMAGE_PATH_MOBILE));
                userInfo.setTitleLogoPathMobile(JSONParser.getString(data, Column.TITLE_LOGO_PATH_MOBILE));
                userInfo.setAppTitleMobile(JSONParser.getString(data, Column.APP_TITLE_MOBILE));

                if (data.has(Column.USER_KEY)) {
                    AppPreference.putString(context, Column.USER_KEY, data.getString(Column.USER_KEY));
                }

                if (data.has(Column.LOCATION_NAME)) {
                    try {
                        userInfo.setLocationName(JSONParser.getString(data, Column.LOCATION_NAME));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (data.has(Column.LOCATION_CODE)) {
                    try {
                        userInfo.setLocationCode(data.getString(Column.LOCATION_CODE));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (data.has(Column.LOCATION_ID)) {
                    try {
                        userInfo.setLocationId(data.getLong(Column.LOCATION_ID));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (data.has("OTHER_DETAILS")) {
                    userInfo.setOtherDetails(data.getString("OTHER_DETAILS"));
                }
                if (data.has("TARGET_HH")) {
                    userInfo.setTargetHh(data.getLong("TARGET_HH"));
                }
                if (data.has("PROFILE_IMAGE")) {
                    userInfo.setProfilePicInString(data.getString("PROFILE_IMAGE"));
                }
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return userInfo;
    }

    public static ArrayList<UserInfo> parseFCMProfileListJSON(JSONArray jUserInfoArr) {
        ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

        for (int i = 0; i < jUserInfoArr.length(); i++) {
            try {
                JSONObject data = jUserInfoArr.getJSONObject(i);
                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(JSONParser.getLong(data, Column.USER_ID));
                userInfo.setUserName(JSONParser.getString(data, Column.USER_NAME));
                userInfo.setUserCode(JSONParser.getString(data, Column.USER_LOGIN_ID));
                userInfo.setOrgId(JSONParser.getLong(data, Column.ORG_ID));


                if (data.has(Column.LOCATION_NAME)) {
                    try {
                        userInfo.setLocationName(JSONParser.getString(data, Column.LOCATION_NAME));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (data.has(Column.LOCATION_CODE)) {
                    try {
                        userInfo.setLocationCode(data.getString(Column.LOCATION_CODE));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (data.has(Column.LOCATION_ID)) {
                    try {
                        userInfo.setLocationId(data.getLong(Column.LOCATION_ID));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    boolean state = data.getBoolean(Column.STATE);
                    if (state == true) {
                        userInfo.setState(1);
                    } else {
                        userInfo.setState(0);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                userInfoList.add(userInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }


//                userInfo.setOrgCode(data.getString(Column.ORG_CODE));
//                userInfo.setOrgName(JSONParser.getString(data, Column.ORG_NAME));
//                userInfo.setOrgDesc(JSONParser.getString(data, Column.ORG_DESC));
//                userInfo.setOrgAddress(JSONParser.getString(data, Column.ORG_ADDRESS));
//                userInfo.setOrgCountry(JSONParser.getString(data, Column.ORG_COUNTRY));
//                userInfo.setHeaderSmallLogoPath(JSONParser.getString(data, Column.HEADER_SMALL_LOGO_PATH));
//                userInfo.setLoginImagePathMobile(JSONParser.getString(data, Column.LOGIN_IMAGE_PATH_MOBILE));
//                userInfo.setTitleLogoPathMobile(JSONParser.getString(data, Column.TITLE_LOGO_PATH_MOBILE));
//                userInfo.setAppTitleMobile(JSONParser.getString(data, Column.APP_TITLE_MOBILE));

//                userInfo.setLocationName(data.getString("LOCATION_NAME"));
//                if (data.has("OTHER_DETAILS")) {
//                    userInfo.setOtherDetails(data.getString("OTHER_DETAILS"));
//                }
//                if (data.has("TARGET_HH")) {
//                    userInfo.setTargetHh(data.getLong("TARGET_HH"));
//                }
//                if (data.has("PROFILE_IMAGE")) {
//                    userInfo.setProfilePicInString(data.getString("PROFILE_IMAGE"));
//                }


        }
        return userInfoList;
    }

    public static ArrayList<LocationModel> parseLocationListJSON(JSONArray jUserInfoArr) {
        ArrayList<LocationModel> userInfoList = new ArrayList<LocationModel>();
        try {
            for (int i = 0; i < jUserInfoArr.length(); i++) {
                JSONObject data = jUserInfoArr.getJSONObject(i);
                LocationModel locationModel = new LocationModel();
                locationModel.setLocationId(data.getInt(LocationModel.LOCATION_ID));
                locationModel.setParentLocationId(data.getInt(LocationModel.PARENT_LOCATION_ID));
                locationModel.setLocationCode(data.getString(LocationModel.LOCATION_CODE));
                locationModel.setLocationName(data.getString(LocationModel.LOCATION_NAME));
                userInfoList.add(locationModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfoList;
    }

    public static AppVersionHistory parseAppVersionHistoryJSON(JSONObject jSONObject) throws MhealthException {


        AppVersionHistory versionHistory = new AppVersionHistory();
        try {
            if (jSONObject.length() > 0) {

                versionHistory.setVersionId(jSONObject.getLong(AppVersionHistory.VERSION_ID));
                versionHistory.setVersionDesc(jSONObject.getString(AppVersionHistory.VERSION_DESC));
                versionHistory.setVersionNumber(jSONObject.getLong(AppVersionHistory.VERSION_NUMBER));
                versionHistory.setVersionName(jSONObject.getString(AppVersionHistory.VERSION_NAME));
                versionHistory.setAppName(jSONObject.getString(AppVersionHistory.APP_NAME));
                versionHistory.setAppPath(jSONObject.getString(AppVersionHistory.APP_PATH));
                versionHistory.setReleaseDate(jSONObject.getLong(AppVersionHistory.RELEASE_DATE));
                versionHistory.setLangCode(jSONObject.getString(AppVersionHistory.LANG_CODE));
                versionHistory.setUpdateDesc(jSONObject.getString(AppVersionHistory.UPDATE_DESC));
                versionHistory.setUpdateNotification(jSONObject.getString(AppVersionHistory.UPDATE_NOTIFICATION));

            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return versionHistory;
    }


    /**
     * Parse undone Schedule list JSON array and prepare schedule list.
     *
     * @param jSchedListArr is the JSON array which will be parsed to schedule list
     * @return The schedule list
     * @throws JSONException the JSON exception
     */
    public static ArrayList<ScheduleInfo> parseScheduleListJSON(JSONArray jSchedListArr) throws MhealthException {
        ArrayList<ScheduleInfo> schedList = new ArrayList<ScheduleInfo>();
        try {
            for (int i = 0; i < jSchedListArr.length(); i++) {
                JSONObject jSchedObj = jSchedListArr.getJSONObject(i);


                ScheduleInfo schedInfo = new ScheduleInfo();
                schedInfo.setCreatedDate(jSchedObj.getLong("CREATE_DATE"));
                schedInfo.setScheduleDate(jSchedObj.getLong("SCHED_DATE"));

                if (jSchedObj.has("BENEF_CODE")) {
                    schedInfo.setBeneficiaryCode(App.getContext().getUserInfo().getUserCode() + jSchedObj.getString("BENEF_CODE"));
                }

                if (jSchedObj.has("SCHED_DESC")) {
                    schedInfo.setScheduleDesc(jSchedObj.getString("SCHED_DESC"));
                } else {
                    schedInfo.setScheduleDesc("");
                }

                if (jSchedObj.has("REFERENCE_ID")) {
                    schedInfo.setReferenceId(jSchedObj.getLong("REFERENCE_ID"));
                }

                if (jSchedObj.has("INTERVIEW_ID")) {
                    schedInfo.setInterviewId(jSchedObj.getLong("INTERVIEW_ID"));
                }

                if (jSchedObj.has("FCM_INTERVIEW_ID")) {
                    long fcmInterviewId = jSchedObj.getLong("FCM_INTERVIEW_ID");
                    schedInfo.setFcmInterviewId(fcmInterviewId);
                }

                if (jSchedObj.has("TRANS_REF"))
                    schedInfo.setTransRef(jSchedObj.getLong("TRANS_REF"));

                if (jSchedObj.has("TYPE")) {
                    schedInfo.setScheduleType(jSchedObj.getString("TYPE"));
                } else schedInfo.setScheduleType("");

                if (jSchedObj.has("QUESTIONNAIRE_ID")) {
                    schedInfo.setQuestionnaireId(jSchedObj.getLong("QUESTIONNAIRE_ID"));
                } else {
                    schedInfo.setQuestionnaireId(-1);
                }

                if (jSchedObj.has("SCHED_STATUS")) {
                    schedInfo.setSchedStatus(jSchedObj.getInt("SCHED_STATUS"));
                } else {
                    schedInfo.setSchedStatus(ScheduleStatus.NEW);
                }

                if (jSchedObj.has("ATTENDED_DATE")) {
                    schedInfo.setAttendedDate(jSchedObj.getLong("ATTENDED_DATE"));
                } else {
                    schedInfo.setAttendedDate(0);
                }

                if (jSchedObj.has("CHANGED_DATE")) {
                    schedInfo.setSystemChangedDate(jSchedObj.getLong("CHANGED_DATE"));
                } else {
                    schedInfo.setSystemChangedDate(0);
                }


                if (jSchedObj.has("STATE")) {
                    schedInfo.setState(jSchedObj.getLong("STATE"));
                }


                if (jSchedObj.has("EVENT_ID")) {
                    schedInfo.setEventId(jSchedObj.getLong("EVENT_ID"));
                }

                schedList.add(schedInfo);
            }

        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return schedList;
    }


    public static ArrayList<DiagnosisInfo> parseDiagnosisInfoList(JSONArray jdigonosisInfo) throws MhealthException {
        ArrayList<DiagnosisInfo> diagnosisInfos = new ArrayList<DiagnosisInfo>();
        try {
            for (int i = 0; i < jdigonosisInfo.length(); i++) {
                JSONObject jMaternalCare = jdigonosisInfo.getJSONObject(i);
                DiagnosisInfo info = new DiagnosisInfo();
                info.setDiagId((jMaternalCare.getLong(Column.DIAG_ID)));
                info.setDiagName((jMaternalCare.getString(Column.DIAG_NAME)));
                info.setCodeSnomed((jMaternalCare.getString(Column.CODE_SNOMED)));
                info.setCodeIcd10((jMaternalCare.getString(Column.CODE_ICD10)));
                info.setState((jMaternalCare.getLong(Column.STATE)));
                diagnosisInfos.add(info);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return diagnosisInfos;
    }

    /**
     * Parse beneficiary list JSON array and prepare beneficiary list.
     *
     * @param jBeneficiaryArr is the JSON array which will be parsed to beneficiary list
     * @return The beneficiary list
     * @throws JSONException the JSON exception
     */
    public static ArrayList<Beneficiary> parseBeneficiaryListJSON(Context context, JSONArray jBeneficiaryArr) throws MhealthException {
        ArrayList<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
        try {
            for (int i = 0; i < jBeneficiaryArr.length(); i++) {
                JSONObject jBeneficiaryObj = jBeneficiaryArr.getJSONObject(i);
                Beneficiary beneficiaryInfo = new Beneficiary();

                beneficiaryInfo.setBenefName(jBeneficiaryObj.getString(Column.BENEFICIARY_BENEF_NAME));
                beneficiaryInfo.setBenefLocalName(jBeneficiaryObj.getString(Column.BENEFICIARY_BENEF_NAME_LOCAL));
                beneficiaryInfo.setDob(jBeneficiaryObj.getString(Column.BENEFICIARY_DOB));
                beneficiaryInfo.setFamilyHead(jBeneficiaryObj.getInt(Column.BENEFICIARY_FAMILY_HEAD));
                beneficiaryInfo.setHhId(jBeneficiaryObj.getInt(Column.BENEFICIARY_HH_ID));
                beneficiaryInfo.setBenefId(jBeneficiaryObj.getInt(Column.BENEFICIARY_BENEF_ID));
                //beneficiaryInfo.setRelationToFamilyHead(jBeneficiaryObj.getString("RELATION_FAMILY_HEAD"));
                beneficiaryInfo.setRelationToGurdian(jBeneficiaryObj.getString(Column.BENEFICIARY_RELATION_GUARDIAN));
                beneficiaryInfo.setGuardianName(jBeneficiaryObj.getString(Column.GUARDIAN_NAME));
                beneficiaryInfo.setGuardianLocalName(jBeneficiaryObj.getString(Column.GUARDIAN_NAME_LOCAL));
                beneficiaryInfo.setBenefCode(jBeneficiaryObj.getString(Column.BENEFICIARY_BENEF_CODE));
                if (jBeneficiaryObj.has(Column.HH_NUMBER)) {
                    beneficiaryInfo.setHhNumber(jBeneficiaryObj.getString(Column.HH_NUMBER));
                }
                if (jBeneficiaryObj.has(Column.BENEF_ADDRESS)) {
                    beneficiaryInfo.setAddress(jBeneficiaryObj.getString(Column.BENEF_ADDRESS));
                }

                beneficiaryInfo.setState(jBeneficiaryObj.getInt(Column.STATE));

                if (jBeneficiaryObj.has(Column.BENEFICIARY_GENDER))
                    beneficiaryInfo.setGender(jBeneficiaryObj.getString(Column.BENEFICIARY_GENDER));

                if (jBeneficiaryObj.has(Column.BENEFICIARY_EDU_LEVEL))
                    beneficiaryInfo.setEduLevel(jBeneficiaryObj.getString(Column.BENEFICIARY_EDU_LEVEL));

                if (jBeneficiaryObj.has(Column.USER_ID))
                    beneficiaryInfo.setUserId(jBeneficiaryObj.getLong(Column.USER_ID));

                if (jBeneficiaryObj.has(Column.BENEFICIARY_MOBILE_NUMBER))
                    beneficiaryInfo.setMobileNumber(jBeneficiaryObj.getString(Column.BENEFICIARY_MOBILE_NUMBER));

                if (jBeneficiaryObj.has(Column.BENEFICIARY_MOBILE_COMM))
                    beneficiaryInfo.setMobileComm(jBeneficiaryObj.getString(Column.BENEFICIARY_MOBILE_COMM));


                if (jBeneficiaryObj.has(Column.BENEFICIARY_BENEF_IMAGE_PATH)) {
                    String imagePath = jBeneficiaryObj.getString(Column.BENEFICIARY_BENEF_IMAGE_PATH);
                    if (imagePath != null && !imagePath.equalsIgnoreCase("")) {
                        imagePath = App.getContext().getBeneficiaryImageDir(context) + "/" + imagePath;
                    }
                    beneficiaryInfo.setBenefImagePath(imagePath);
                }


                if (jBeneficiaryObj.has(Column.OCCUPATION))
                    beneficiaryInfo.setOccupation(jBeneficiaryObj.getString(Column.OCCUPATION));

                if (jBeneficiaryObj.has(Column.OCCUPATION_HER_HUSBAND))
                    beneficiaryInfo.setOccupationHerHusband(jBeneficiaryObj.getString(Column.OCCUPATION_HER_HUSBAND));

                if (jBeneficiaryObj.has(Column.MONTHLY_FAMILY_EXPENDITURE)) {
                    beneficiaryInfo.setMonthlyFamilyExpenditure(jBeneficiaryObj.getString(Column.MONTHLY_FAMILY_EXPENDITURE));
                }


                if (jBeneficiaryObj.has(Column.RELIGION)) {
                    beneficiaryInfo.setReligion(jBeneficiaryObj.getString(Column.RELIGION));
                }

                if (jBeneficiaryObj.has(Column.RELIGION_OTHER_SPECIFIC)) {
                    beneficiaryInfo.setReligionOtherSpecofic(jBeneficiaryObj.getString(Column.RELIGION_OTHER_SPECIFIC));
                }

                if (jBeneficiaryObj.has(Column.MARITAL_STATUS))
                    beneficiaryInfo.setMaritalStatus(jBeneficiaryObj.getString(Column.MARITAL_STATUS));

                if (jBeneficiaryObj.has(Column.NATIONAL_ID))
                    beneficiaryInfo.setNationalIdNumber(jBeneficiaryObj.getString(Column.NATIONAL_ID));

                if (jBeneficiaryObj.has(Column.BIRTH_REG_NUMBER))
                    beneficiaryInfo.setBirthCertificateNumber(jBeneficiaryObj.getString(Column.BIRTH_REG_NUMBER));

                if (jBeneficiaryObj.has("FILE_KEY"))
                    beneficiaryInfo.setFileKey(jBeneficiaryObj.getString("FILE_KEY"));

                if (jBeneficiaryObj.has("REG_FORM_PATH"))
                    beneficiaryInfo.setRegFormPath(jBeneficiaryObj.getString("REG_FORM_PATH"));

                if (jBeneficiaryObj.has(Column.CREATE_DATE))
                    beneficiaryInfo.setCreateDate(jBeneficiaryObj.getString(Column.CREATE_DATE));


                beneficiaryList.add(beneficiaryInfo);

            }
        } catch (JSONException exception) {
            exception.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return beneficiaryList;
    }

    /**
     * Parse the response JSON get from multipart upload request and prepare WebResponseInfo object.
     *
     * @param jsonData is the JSON string get from multipart upload request
     * @return The WebResponseInfo object which contains data JSON, parameter JSON, and other necessary information
     * @throws JSONException the JSON exception
     */
    public static ResponseData parseMultipartUploadResponse(String jsonData) throws MhealthException {
        //		JSONObject jObj = new JSONObject(jsonData);

        ResponseData webResponseDataInfo = new ResponseData();
        try {
            JSONObject jObj = new JSONObject(jsonData);

            webResponseDataInfo.setResponseType(jObj.getString("responseType"));
            webResponseDataInfo.setResponseName(jObj.getString("responseName"));
            webResponseDataInfo.setResponseCode(jObj.getString("responseCode"));
            //		webResponseInfo.setResponseTime(jObj.getString("responseTime"));
            webResponseDataInfo.setExecTime(jObj.getString("execTime"));

            if (webResponseDataInfo.getResponseCode().equalsIgnoreCase("00")) {
                webResponseDataInfo.setErrorCode(jObj.getString("errorCode"));
                webResponseDataInfo.setErrorDesc(jObj.getString("errorDesc"));
            } else {
                webResponseDataInfo.setDataLength(jObj.getInt("dataLength"));
                webResponseDataInfo.setData(jObj.getString("data"));
            }

            if (jObj.has(KEY.PARAM1)) {
                webResponseDataInfo.setParam(jObj.getString(KEY.PARAM1));
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return webResponseDataInfo;
    }

    /**
     * Parse the medicine received data JSON and return the medicine list.
     *
     * @param jsonData The data JSON string get from medicine received request
     * @return The parsed medicine list
     * @throws JSONException the JSON exception
     */
    public static ArrayList<MedicineInfo> parseMedicineReceivedJSON(String jsonData) throws MhealthException {
        ArrayList<MedicineInfo> medicineList = new ArrayList<MedicineInfo>();
        try {
            JSONObject jObj = new JSONObject(jsonData);
            JSONArray jMedArray = jObj.getJSONArray("medicineList");

            for (int i = 0; i < jMedArray.length(); i++) {
                MedicineInfo medicineInfo = new MedicineInfo();
                JSONObject jMedObj = jMedArray.getJSONObject(i);

                medicineInfo.setMedId(jMedObj.getInt(Column.MEDICINE_ID));
                medicineInfo.setQtyReceived(jMedObj.getInt(Column.QTY));
                medicineInfo.setUnitPurchasePrice(jMedObj.getDouble(Column.UNIT_PURCHASE_PRICE));
                medicineInfo.setUnitSalesPrice(jMedObj.getDouble(Column.UNIT_SALES_PRICE));

                medicineInfo.setTotalPrice(medicineInfo.getQtyReceived() * medicineInfo.getUnitPurchasePrice());

                medicineList.add(medicineInfo);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return medicineList;
    }

    /**
     * Parse the requisition list json data and return requisition list.
     *
     * @param jsonData The json String data which will be parsed
     * @return Requisition list with medicine information
     * @throws JSONException the JSON exception
     */
    public static ArrayList<RequisitionInfo> pasreRequisitionListJSON(String jsonData) throws MhealthException {
        ArrayList<RequisitionInfo> requisitionList = new ArrayList<RequisitionInfo>();

        try {
            JSONObject jObj = new JSONObject(jsonData);

            if (jObj.has("requisitionList")) {
                JSONArray jRequisitionArr = jObj.getJSONArray("requisitionList");
                for (int i = 0; i < jRequisitionArr.length(); i++) {
                    RequisitionInfo requisitionInfo = new RequisitionInfo();

                    JSONObject jRequisitionObj = jRequisitionArr.getJSONObject(i);

                    requisitionInfo.setRequisitionNo(jRequisitionObj.getLong("REQ_NO"));

                    if (jRequisitionObj.has("COMPLETE_DATE"))
                        requisitionInfo.setCompleteDate(jRequisitionObj.getString("COMPLETE_DATE"));

                    requisitionInfo.setRequisitionDate(jRequisitionObj.getString("REQ_DATE"));
                    requisitionInfo.setRequisitionStatus(jRequisitionObj.getString("REQ_STATUS"));
                    requisitionInfo.setRequisitionMedicinePrice(jRequisitionObj.getDouble("REQ_TOTAL_PRICE"));
                    requisitionInfo.setReceiveMedicinePrice(jRequisitionObj.getDouble("REC_TOTAL_PRICE"));

                    if (jRequisitionObj.has("REC_DATE"))
                        requisitionInfo.setReceiveDate(jRequisitionObj.getString("REC_DATE"));

                    // Parse Medicine list
                    JSONArray jMedicineArr = jRequisitionObj.getJSONArray("medicineList");
                    ArrayList<RequisitionMedicineInfo> medList = new ArrayList<RequisitionMedicineInfo>();

                    for (int j = 0; j < jMedicineArr.length(); j++) {
                        JSONObject jMedObj = jMedicineArr.getJSONObject(j);

                        RequisitionMedicineInfo medInfo = new RequisitionMedicineInfo();

                        medInfo.setMedId(jMedObj.getLong("MEDICINE_ID"));
                        medInfo.setReceivePrice(jMedObj.getDouble("REC_PRICE"));
                        medInfo.setRequisitionPrice(jMedObj.getDouble("REQ_PRICE"));
                        medInfo.setReceiveQuantity(jMedObj.getInt("REC_QTY"));
                        medInfo.setRequisitionQuantity(jMedObj.getInt("REQ_QTY"));

                        medList.add(medInfo);
                    }

                    requisitionInfo.setRequisitionMedicineList(medList);
                    requisitionList.add(requisitionInfo);


                    if (jRequisitionObj.has("requisition_accepted_detail")) {
                        requisitionInfo.setRequisitionAcceptedDetail(jRequisitionObj.getJSONObject("requisition_accepted_detail").toString());
                    }

                }


            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return requisitionList;
    }

    /**
     * Parse the requisition list json data and return requisition list.
     *
     * @param jsonData The json String data which will be parsed
     * @return Requisition list with medicine information
     * @throws JSONException the JSON exception
     */
    public static ArrayList<MedicineConsumptionInfoModel> parseMedicineConsumptionListJSON(JSONArray jRequisitionArr) throws MhealthException {
        ArrayList<MedicineConsumptionInfoModel> medicineConsumptionInfoModelList = new ArrayList<MedicineConsumptionInfoModel>();

        try {

            //   JSONArray jRequisitionArr = jarray;
            for (int i = 0; i < jRequisitionArr.length(); i++) {
                MedicineConsumptionInfoModel medicineConsumptionInfoModel = new MedicineConsumptionInfoModel();

                JSONObject jsonObjectConsumption = jRequisitionArr.getJSONObject(i);

                medicineConsumptionInfoModel.setInterviewId(jsonObjectConsumption.getLong("PATIENT_INTERVIEW_ID"));

                if (jsonObjectConsumption.has("CREATE_DATE"))
                    medicineConsumptionInfoModel.setConsumptionDate(jsonObjectConsumption.getString("CREATE_DATE"));

                medicineConsumptionInfoModel.setBenefCode(jsonObjectConsumption.getLong("BENEF_ID"));
                medicineConsumptionInfoModel.setMedConsumpId(jsonObjectConsumption.getLong("MED_CONSUMP_ID"));
                medicineConsumptionInfoModel.setLocationId(jsonObjectConsumption.getLong("LOCATION_ID"));
                medicineConsumptionInfoModel.setUserId(jsonObjectConsumption.getLong("USER_ID"));
                medicineConsumptionInfoModel.setTotalPrice(jsonObjectConsumption.getLong("TOTAL_PRICE"));
                medicineConsumptionInfoModel.setDataSent("Y");

                if (jsonObjectConsumption.has("VERSION_NO")) {
                    medicineConsumptionInfoModel.setVersionNo(jsonObjectConsumption.getString("VERSION_NO"));
                }


                // Parse Medicine list
                JSONArray jsonArrayConsumpDetailslist = jsonObjectConsumption.getJSONArray("consumptionDetailList");
                ArrayList<ConsumptionDetailsModel> consumpList = new ArrayList<ConsumptionDetailsModel>();

                for (int j = 0; j < jsonArrayConsumpDetailslist.length(); j++) {
                    JSONObject jsonObjectConDetail = jsonArrayConsumpDetailslist.getJSONObject(j);

                    ConsumptionDetailsModel conDetail = new ConsumptionDetailsModel();

                    conDetail.setMed_consump_dtl_id(jsonObjectConDetail.getLong("MED_CONSUMP_DTL_ID"));
                    conDetail.setMed_consump_id(jsonObjectConDetail.getLong("MED_CONSUMP_ID"));
                    conDetail.setMed_id(jsonObjectConDetail.getLong("MEDICINE_ID"));
                    conDetail.setQty(jsonObjectConDetail.getDouble("QTY"));
                    conDetail.setPrice(jsonObjectConDetail.getDouble("PRICE"));
                    consumpList.add(conDetail);
                }

                medicineConsumptionInfoModel.setConsumptionDetails(consumpList);
                medicineConsumptionInfoModelList.add(medicineConsumptionInfoModel);


            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return medicineConsumptionInfoModelList;
    }


    /**
     * Parse the requisition list json data and return requisition list.
     *
     * @param jsonData The json String data which will be parsed
     * @return Requisition list with medicine information
     * @throws JSONException the JSON exception
     */
    public static ArrayList<SatelliteSessionModel> parseSessionListJson(JSONArray jRequisitionArr) throws MhealthException {
        ArrayList<SatelliteSessionModel> satelliteSessionModelArrayList = new ArrayList<SatelliteSessionModel>();

        try {

            //   JSONArray jRequisitionArr = jarray;
            for (int i = 0; i < jRequisitionArr.length(); i++) {
                SatelliteSessionModel sessionModel = new SatelliteSessionModel();

                JSONObject jsonObjectSession = jRequisitionArr.getJSONObject(i);

                long dateInMiliSession = Utility.getMillisecondFromDate(jsonObjectSession.getString("sat_session_date"), Constants.DATE_FORMAT_YYYY_MM_DD);
                sessionModel.setSatSessionDate(dateInMiliSession);
                sessionModel.setUserId(jsonObjectSession.getLong("user_id"));
                try {
                    boolean sates =jsonObjectSession.getBoolean("state");
                    if (sates){
                        sessionModel.setState("1");
                    }else{
                        sessionModel.setState("0");
                    }
                }catch (Exception e){
                    e.printStackTrace();

                }



                sessionModel.setOrg_id(jsonObjectSession.getLong("org_id"));
                sessionModel.setSatSessionLocationId(jsonObjectSession.getLong("sat_session_location_id"));
                sessionModel.setSatelliteSessionId(jsonObjectSession.getLong("sat_session_id"));
                long dateInMiliCreateDate = Utility.getMillisecondFromDate(jsonObjectSession.getString("create_date"), Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
                sessionModel.setCreateDate(dateInMiliCreateDate);
                sessionModel.setDataSent("Y");
                if (jsonObjectSession.has("version_no")) {
                    sessionModel.setVersion(""+jsonObjectSession.getLong("version_no"));
                }


                // Parse Medicine list
                JSONArray jsonArraySatelliteSessionChwardetaisllist = jsonObjectSession.getJSONArray("satelliteSessionChwarDetailsList");
                ArrayList<SatelliteSessionChwarModel> satelliteSessionChwarList = new ArrayList<SatelliteSessionChwarModel>();

                for (int j = 0; j < jsonArraySatelliteSessionChwardetaisllist.length(); j++) {
                    JSONObject jsonObjectSessionChwDetail = jsonArraySatelliteSessionChwardetaisllist.getJSONObject(j);

                    SatelliteSessionChwarModel satelliteSessionChwarModel = new SatelliteSessionChwarModel();

                    satelliteSessionChwarModel.setSAT_SESSION_ID(jsonObjectSessionChwDetail.getLong("sat_session_id"));
                    satelliteSessionChwarModel.setLOCATION_ID(jsonObjectSessionChwDetail.getLong("location_id"));
                    satelliteSessionChwarModel.setUSER_ID(jsonObjectSessionChwDetail.getLong("user_id"));
                    satelliteSessionChwarList.add(satelliteSessionChwarModel);
                }

                sessionModel.setSatelliteSessionChwarDetailsList(satelliteSessionChwarList);
                satelliteSessionModelArrayList.add(sessionModel);


            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new MhealthException(ErrorCode.PARSE_EXCEPTION, "PARSE EXCEPTION", e);

        }
        return satelliteSessionModelArrayList;
    }


    /**
     * Parse the requisition list json data and return requisition list.
     *
     * @param jsonData The json String data which will be parsed
     * @return Requisition list with medicine information
     * @throws JSONException the JSON exception
     */
    public static ArrayList<MedicineAdjustmentInfoModel> parseMedicineAdjustListJSON(JSONArray adjustArray) throws MhealthException {
        ArrayList<MedicineAdjustmentInfoModel> medicineAdjustmentInfoModelsList = new ArrayList<MedicineAdjustmentInfoModel>();

        try {

            for (int i = 0; i < adjustArray.length(); i++) {
                MedicineAdjustmentInfoModel medicineAdjustmentInfoModel = new MedicineAdjustmentInfoModel();

                JSONObject jsonObjectAdjust = adjustArray.getJSONObject(i);

                medicineAdjustmentInfoModel.setMedicine_adjust_id(jsonObjectAdjust.getLong("MEDICINE_ADJUST_ID"));
                medicineAdjustmentInfoModel.setUser_id(jsonObjectAdjust.getLong("USER_ID"));

                if (jsonObjectAdjust.has("REQUEST_DATE"))
                    medicineAdjustmentInfoModel.setRequest_date(jsonObjectAdjust.getString("REQUEST_DATE"));

//                if (jsonObjectAdjust.has("RECORD_DATE"))
//                    medicineAdjustmentInfoModel.setRecordDate(jsonObjectAdjust.getString("RECORD_DATE"));(this feild is not avail able on adjustmentmaster on local db but data exist)


                if (jsonObjectAdjust.has("APPROVED_ON")) {
                    medicineAdjustmentInfoModel.setApproved_on(jsonObjectAdjust.getString("APPROVED_ON"));
                }
                if (jsonObjectAdjust.has("VERSION_NO")) {
                    medicineAdjustmentInfoModel.setVersionNo(jsonObjectAdjust.getString("VERSION_NO"));
                }


                medicineAdjustmentInfoModel.setRequest_number(jsonObjectAdjust.getLong("REQUEST_NUMBER"));
                medicineAdjustmentInfoModel.setApproved_by(jsonObjectAdjust.getLong("REQUEST_NUMBER"));

//                medicineAdjustmentInfoModel.setLocation_id(jsonObjectAdjust.getLong("LOCATION_ID")); (this feild is not available on local db but exist)

                // Parse adjustment details list
                JSONArray jsonArrayAdjustmentDetail = jsonObjectAdjust.getJSONArray("adjustmentDetailList");
                ArrayList<MedicineAdjustmentDetailModel> adjustmentDetailModelsList = new ArrayList<MedicineAdjustmentDetailModel>();

                for (int j = 0; j < jsonArrayAdjustmentDetail.length(); j++) {
                    JSONObject jsonObjectAdDetail = jsonArrayAdjustmentDetail.getJSONObject(j);

                    MedicineAdjustmentDetailModel adDetail = new MedicineAdjustmentDetailModel();

                    adDetail.setMedicine_adjust_dtl_id(jsonObjectAdDetail.getLong("MEDICINE_ADJUST_DTL_ID"));
                    adDetail.setMedicine_adjust_id(jsonObjectAdDetail.getLong("MEDICINE_ADJUST_ID"));
                    adDetail.setMedicine_id(jsonObjectAdDetail.getLong("MEDICINE_ID"));
                    adDetail.setAdjust_qty(jsonObjectAdDetail.getDouble("ADJUST_QTY"));
                    adDetail.setPre_rqst_qty(jsonObjectAdDetail.getDouble("PRE_RQST_QTY"));
                    adDetail.setRqst_qty(jsonObjectAdDetail.getDouble("RQST_QTY"));
                    adDetail.setRcmnd_qty(jsonObjectAdDetail.getDouble("RCMND_QTY"));
                    adDetail.setApproved_qty(jsonObjectAdDetail.getDouble("APPVD_QTY"));

                    adjustmentDetailModelsList.add(adDetail);
                }

                medicineAdjustmentInfoModel.setAdjustmentDetailModels(adjustmentDetailModelsList);
                medicineAdjustmentInfoModelsList.add(medicineAdjustmentInfoModel);


            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return medicineAdjustmentInfoModelsList;
    }


    /**
     * Parse the requisition list json data and return requisition list.
     *
     * @param jsonData The json String data which will be parsed
     * @return Requisition list with medicine information
     * @throws JSONException the JSON exception
     */
    public static ArrayList<MedicineReceiveModel> parseMedicineReceivetListJSON(JSONArray medicineReceiveModelList) throws MhealthException {
        ArrayList<MedicineReceiveModel> medicineReceiveModelsList = new ArrayList<MedicineReceiveModel>();

        try {

            for (int i = 0; i < medicineReceiveModelList.length(); i++) {
                MedicineReceiveModel medicineReceiveSingle = new MedicineReceiveModel();

                JSONObject jsonObjectAdjust = medicineReceiveModelList.getJSONObject(i);

                medicineReceiveSingle.setTotal_price(jsonObjectAdjust.getLong("TOTAL_PRICE"));
                medicineReceiveSingle.setMedicine_received_id(jsonObjectAdjust.getLong("MED_RECEIVED_ID"));

                if (jsonObjectAdjust.has("RECEIVED_DATE"))
                    medicineReceiveSingle.setMedicine_receive_date(jsonObjectAdjust.getString("RECEIVED_DATE"));

                medicineReceiveSingle.setLocation_id(jsonObjectAdjust.getLong("LOCATION_ID"));
                medicineReceiveSingle.setSupplier_id(jsonObjectAdjust.getLong("SUPP_ID"));
                medicineReceiveSingle.setUser_id(jsonObjectAdjust.getLong("USER_ID"));
                if (jsonObjectAdjust.has("VERSION_NO")) {
                    medicineReceiveSingle.setVersionNo(jsonObjectAdjust.getString("VERSION_NO"));
                }

                // Parse receive details list
                JSONArray jsonArrayMedReciveDetail = jsonObjectAdjust.getJSONArray("receiveDetailList");
                ArrayList<MedicineReceivedDetailModel> receiveDetails = new ArrayList<MedicineReceivedDetailModel>();

                for (int j = 0; j < jsonArrayMedReciveDetail.length(); j++) {
                    JSONObject jsonObjectRvDetail = jsonArrayMedReciveDetail.getJSONObject(j);

                    MedicineReceivedDetailModel medDetail = new MedicineReceivedDetailModel();

                    medDetail.setPrice(jsonObjectRvDetail.getLong("PRICE"));
                    medDetail.setMedicineReceivedDtlId(jsonObjectRvDetail.getLong("MED_RECEIVED_DTL_ID"));
                    medDetail.setMedicineId(jsonObjectRvDetail.getLong("MEDICINE_ID"));
                    medDetail.setQty(jsonObjectRvDetail.getLong("QTY"));
                    medDetail.setMedicineReceivedId(jsonObjectRvDetail.getLong("MED_RECEIVED_ID"));


                    receiveDetails.add(medDetail);
                }

                medicineReceiveSingle.setMedicineReceivedDetailList(receiveDetails);
                medicineReceiveModelsList.add(medicineReceiveSingle);


            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return medicineReceiveModelsList;
    }


//
//    /**
//     * Parse the requisition list json data and return requisition list.
//     *
//     * @param jsonData The json String data which will be parsed
//     * @return Requisition list with medicine information
//     * @throws JSONException the JSON exception
//     */
//    public static ArrayList<MedicineAdjustmentInfoModel> parseMedicineAdjustListJSON(JSONArray requisitionList) throws MhealthException {
//        ArrayList<MedicineAdjustmentInfoModel> medicineAdjustmentInfoModelsList = new ArrayList<MedicineAdjustmentInfoModel>();
//
//        try {
//
//            for (int i = 0; i < requisitionList.length(); i++) {
//                RequisitionInfo requisitionInfo = new RequisitionInfo();
//
//                JSONObject jsonObjectreqInfo = requisitionList.getJSONObject(i);
//
//                requisitionInfo.setRequisitionMedicinePrice(jsonObjectreqInfo.getLong("REC_TOTAL_PRICE"));
//
//                if (jsonObjectreqInfo.has("REC_DATE"))
//                    requisitionInfo.setRequisitionDate(jsonObjectreqInfo.getString("REC_DATE"));
//
//
//                medicineAdjustmentInfoModel.setUser_id(jsonObjectAdjust.getLong("USER_ID"));
//                medicineAdjustmentInfoModel.setRequest_number(jsonObjectAdjust.getLong("MED_CONSUMP_ID"));
//                medicineAdjustmentInfoModel.setRecommended_by(jsonObjectAdjust.getString("RECOMMENDED_BY"));
//                medicineAdjustmentInfoModel.setRecommende_on(jsonObjectAdjust.getString("RECOMMENDED_ON"));
//                medicineAdjustmentInfoModel.setApproved_by(jsonObjectAdjust.getString("APPROVED_BY"));
//                medicineAdjustmentInfoModel.setApproved_on(jsonObjectAdjust.getString("APPROVED_ON"));
//                medicineAdjustmentInfoModel.setFcm_fetch_on(jsonObjectAdjust.getString("FCM_FETCH_ON"));
//                medicineAdjustmentInfoModel.setState(jsonObjectAdjust.getString("STATE"));
//
//                // Parse adjustment details list
//                JSONArray jsonArrayAdjustmentDetail = jsonObjectAdjust.getJSONArray("consumptionDetailList");
//                ArrayList<MedicineAdjustmentDetailModel> adjustmentDetailModelsList = new ArrayList<MedicineAdjustmentDetailModel>();
//
//                for (int j = 0; j < jsonArrayAdjustmentDetail.length(); j++) {
//                    JSONObject jsonObjectAdDetail = jsonArrayAdjustmentDetail.getJSONObject(j);
//
//                    MedicineAdjustmentDetailModel adDetail = new MedicineAdjustmentDetailModel();
//
//                    adDetail.setMedicine_adjust_dtl_id(jsonObjectAdDetail.getLong("MEDICINE_ADJUST_DTL_ID"));
//                    adDetail.setMedicine_adjust_id(jsonObjectAdDetail.getLong("MEDICINE_ADJUST_ID"));
//                    adDetail.setMedicine_id(jsonObjectAdDetail.getLong("MEDICINE_ID"));
//                    adDetail.setAdjust_qty(jsonObjectAdDetail.getDouble("ADJUST_QTY"));
//                    adDetail.setPre_rqst_qty(jsonObjectAdDetail.getDouble("PRE_RQST_QTY"));
//                    adDetail.setRqst_qty(jsonObjectAdDetail.getDouble("RQST_QTY"));
//                    adDetail.setRcmnd_qty(jsonObjectAdDetail.getDouble("RCMND_QTY"));
//                    adDetail.setApproved_qty(jsonObjectAdDetail.getDouble("APPVD_QTY"));
//
//                    adjustmentDetailModelsList.add(adDetail);
//                }
//
//                medicineAdjustmentInfoModel.setAdjustmentDetailModels(adjustmentDetailModelsList);
//                medicineAdjustmentInfoModelsList.add(medicineAdjustmentInfoModel);
//
//
//            }
//        } catch (JSONException exception) {
//            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
//        }
//        return medicineAdjustmentInfoModelsList;
//    }


    /**
     * Parse the FCM's today's sales report to prepare the medicine list and return.
     *
     * @param jsonData The JSON string which will be parsed
     * @return The sold medicine list
     * @throws JSONException the JSON exception
     */
    public static ArrayList<MedicineInfo> parseTodaysSalesReportJson(String jsonData) throws MhealthException {
        ArrayList<MedicineInfo> medicineList = new ArrayList<MedicineInfo>();
        try {
            JSONObject jObj = new JSONObject(jsonData);
            JSONArray jMedArr = jObj.getJSONArray("REPORT");
            for (int i = 0; i < jMedArr.length(); i++) {
                JSONObject jMedObj = jMedArr.getJSONObject(i);

                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setBrandName(jMedObj.getString("MEDICINE_NAME"));
                medInfo.setSoldQuantity(jMedObj.getInt("TOTAL_QTY") + "");
                medInfo.setTotalPrice(jMedObj.getDouble("TOTAL_PRICE"));
                medicineList.add(medInfo);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return medicineList;
    }

    /**
     * Parse Last 30 days medicine sales summary report JSON.
     *
     * @param jsonData The JSON data in string
     * @return Last 30 days medicine sales summary
     * @throws JSONException the JSON exception
     */
    public static ArrayList<IndividualSalesInfo> parseLast30DaysSalesReportJson(String jsonData) throws MhealthException {
        ArrayList<IndividualSalesInfo> summaryList = new ArrayList<IndividualSalesInfo>();
        try {
            JSONObject jObj = new JSONObject(jsonData);
            JSONArray jMedArr = jObj.getJSONArray("REPORT");

            for (int i = 0; i < jMedArr.length(); i++) {
                JSONObject jMedObj = jMedArr.getJSONObject(i);

                IndividualSalesInfo salesInfo = new IndividualSalesInfo();
                salesInfo.setDate(jMedObj.getString("CONSUMPTION_DATE"));
                salesInfo.setSaleAmount(jMedObj.getDouble("TOTAL_PRICE"));

                summaryList.add(salesInfo);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return summaryList;
    }

    /**
     * Parse Last 30 days medicine receive/sales summary report JSON.
     *
     * @param jsonData The JSON data in string
     * @return Last 30 days medicine receive/sales summary
     * @throws JSONException the JSON exception
     */
    public static ArrayList<MedicineRcvSaleInfo> parseLast30DaysRcvSalesReportJson(String jsonData) throws MhealthException {
        ArrayList<MedicineRcvSaleInfo> summaryList = new ArrayList<MedicineRcvSaleInfo>();

        try {
            JSONObject jObj = new JSONObject(jsonData);
            JSONArray jReportArr = jObj.getJSONArray("REPORT");

            for (int i = 0; i < jReportArr.length(); i++) {
                JSONObject jReportObj = jReportArr.getJSONObject(i);

                MedicineRcvSaleInfo rcvSalesInfo = new MedicineRcvSaleInfo();
                rcvSalesInfo.setMedicineName(jReportObj.getString("MEDICINE_NAME"));
                rcvSalesInfo.setReceiveQuantity(jReportObj.getString("RECEIVE_QTY"));
                rcvSalesInfo.setSaleQuantity(jReportObj.getString("SALE_QTY"));
                rcvSalesInfo.setTotalSalePrice(jReportObj.getString("SALE_PRICE"));

                summaryList.add(rcvSalesInfo);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return summaryList;
    }

    /**
     * Parse health care summary report JSON.
     *
     * @param jsonData The JSON data in string
     * @return Health care summary
     * @throws JSONException the JSON exception
     */
    public static ArrayList<HealthCareReportInfo> parseHealthCareReportJson(String jsonData) throws MhealthException {
        ArrayList<HealthCareReportInfo> summaryList = new ArrayList<HealthCareReportInfo>();
        try {
            JSONObject jObj = new JSONObject(jsonData);
            JSONArray jReportArr = jObj.getJSONArray("REPORT");

            for (int i = 0; i < jReportArr.length(); i++) {
                JSONObject jReportObj = jReportArr.getJSONObject(i);

                HealthCareReportInfo reportInfo = new HealthCareReportInfo();
                reportInfo.setCurrentMonthQuantity(jReportObj.getInt("CURRENT_MONTH_QTY"));
                reportInfo.setLastMonthQuantity(jReportObj.getInt("LAST_MONTH_QTY"));
                reportInfo.setHealthCareTitle(jReportObj.getString("HEALTH_CARE_TYPE"));

                summaryList.add(reportInfo);
            }

        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return summaryList;
    }

    /**
     * Parse beneficiary registration summary report JSON.
     *
     * @param jsonData The JSON data in string
     * @return Beneficiary registration summary
     * @throws JSONException the JSON exception
     */
    public static ArrayList<BeneficiaryRegistrationState> parseBenefRegReportJson(String jsonData) throws MhealthException {
        ArrayList<BeneficiaryRegistrationState> summaryList = new ArrayList<BeneficiaryRegistrationState>();
        try {
            JSONObject jObj = new JSONObject(jsonData);
            JSONArray jReportArr = jObj.getJSONArray("REPORT");

            for (int i = 0; i < jReportArr.length(); i++) {
                JSONObject jReportObj = jReportArr.getJSONObject(i);

                BeneficiaryRegistrationState reportInfo = new BeneficiaryRegistrationState();

                reportInfo.setMonth(jReportObj.getString("MONTH_NAME"));
                reportInfo.setQuantity(jReportObj.getInt("REGISTRATION_QTY"));

                summaryList.add(reportInfo);
            }

        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return summaryList;
    }

    public static ArrayList<ScheduleInfo> parseScheduleInfos(String jsonData) throws MhealthException {
        ArrayList<ScheduleInfo> scheduleInfos = new ArrayList<ScheduleInfo>();

        try {
            JSONObject jObj = new JSONObject(jsonData);
            JSONArray jArr = jObj.getJSONArray("schedList");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject jReportObj = jArr.getJSONObject(i);
                ScheduleInfo scheduleInfo = new ScheduleInfo();
                scheduleInfo.setScheduleId(jReportObj.getLong("SCHED_ID"));
                scheduleInfo.setScheduleName(jReportObj.getString("SCHED_NAME"));
                scheduleInfo.setScheduleDate(jReportObj.getLong("SCHED_DATE"));
                scheduleInfo.setScheduleDesc(jReportObj.getString("SCHED_DESC"));
                scheduleInfo.setScheduleType(jReportObj.getString("SCHED_TYPE"));
                if (jReportObj.has("REFERENCE_ID"))
                    scheduleInfo.setReferenceId(jReportObj.getLong("REFERENCE_ID"));
                if (jReportObj.has("DATE_INPUT_RULE"))
                    scheduleInfo.setDateInputRule(jReportObj.getString("DATE_INPUT_RULE"));
                scheduleInfos.add(scheduleInfo);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return scheduleInfos;
    }


    public static ArrayList<MaternalInfo> parseMaternalInfos(JSONArray array) throws MhealthException {
        ArrayList<MaternalInfo> maternalInfos = new ArrayList<MaternalInfo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                MaternalInfo maternalInfo = new MaternalInfo();
                maternalInfo.setMaternalId(o.getLong(Column.MATERNAL_ID));
                maternalInfo.setBenefId(o.getLong(Column.BENEF_ID));
                maternalInfo.setLmp(Utility.getMillisecondFromDate(o.getString(Column.LMP), Constants.DATE_FORMAT_YYYY_MM_DD));
                maternalInfo.setEdd(Utility.getMillisecondFromDate(o.getString(Column.EDD), Constants.DATE_FORMAT_YYYY_MM_DD));
                maternalInfo.setCreateDateMaternal(Utility.getMillisecondFromDate(o.getString(Column.CREATE_DATE), Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
                maternalInfo.setPara(o.getLong(Column.PARA));
                maternalInfo.setGravida(o.getLong(Column.GRAVIDA));
                maternalInfo.setBmiValue(o.getDouble(Column.BMI_VALUE));
                maternalInfo.setBmi(o.getString(Column.BMI));
                maternalInfo.setNoOfRiskItem(o.getLong(Column.NO_OF_RISK_ITEM));
                maternalInfo.setRegInterviewId(o.getLong(Column.REG_INTERVIEW_ID));
                maternalInfo.setHighRiskInterviewId(o.getLong(Column.HIGH_RISK_INTERVIEW_ID));
                maternalInfo.setHeightInCm(o.getLong(Column.HEIGHT_IN_CM));
                if (o.has(Column.TRANS_REF)) maternalInfo.setTransRef(o.getLong(Column.TRANS_REF));


                try {

                    if (o.getInt(Column.MATERNAL_STATUS) == 1) {
                        maternalInfo.setMaternalStatus(1);
                    } else {
                        maternalInfo.setMaternalStatus(0);
                    }
                } catch (Exception e) {
                    if (o.getBoolean(Column.MATERNAL_STATUS)) {
                        maternalInfo.setMaternalStatus(1);
                    } else {
                        maternalInfo.setMaternalStatus(0);
                    }
                }

                if (o.has(Column.MATERNAL_COMPLETE_DATE)) {
                    maternalInfo.setCompleteDate(Utility.getMillisecondFromDate(o.getString(Column.MATERNAL_COMPLETE_DATE), Constants.DATE_FORMAT_YYYY_MM_DD));
                }
                maternalInfo.setCompleteSource(o.getLong(Column.MATERNAL_COMPLETE_SOURCE));

                maternalInfo.setBenefCode(o.getString(Column.BENEF_CODE));
                maternalInfos.add(maternalInfo);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return maternalInfos;
    }

    public static ArrayList<MaternalCareInfo> parseMaternalCareList(JSONArray jMaternalCareArr) throws MhealthException {
        ArrayList<MaternalCareInfo> maternalCareInfos = new ArrayList<MaternalCareInfo>();
        try {
            for (int i = 0; i < jMaternalCareArr.length(); i++) {
                JSONObject jMaternalCare = jMaternalCareArr.getJSONObject(i);

                MaternalCareInfo careInfo = new MaternalCareInfo();

                careInfo.setMaternalCareId((jMaternalCare.getLong(Column.MATERNAL_CARE_ID)));
                careInfo.setCareName((jMaternalCare.getString(Column.CARE_NAME)));
                careInfo.setCareDesc((jMaternalCare.getString(Column.CARE_DESC)));
                careInfo.setCareType((jMaternalCare.getString(Column.CARE_TYPE)));
                careInfo.setSchedRangeStartDay((jMaternalCare.getLong(Column.SCHED_RANGE_START_DAY)));
                careInfo.setSchedRangeEndDay((jMaternalCare.getLong(Column.SCHED_RANGE_END_DAY)));
                careInfo.setQuestionnaireId((jMaternalCare.getLong(Column.QUESTIONNAIRE_ID)));
                maternalCareInfos.add(careInfo);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return maternalCareInfos;
    }

    public static ArrayList<MaternalService> parseMaternalServices(JSONArray array) throws MhealthException {
        ArrayList<MaternalService> services = new ArrayList<MaternalService>();

        try {

            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                MaternalService service = new MaternalService();
                service.setMaternalServiceId(o.getLong(Column.MATERNAL_SERVICE_ID));
                service.setMaternalId(o.getLong(Column.MATERNAL_ID));
                service.setBmiValue(o.getDouble(Column.BMI_VALUE));
                service.setBmi(o.getString(Column.BMI));
                service.setPulse(o.getLong(Column.PULSE));
                service.setPulseStatus(o.getString(Column.PULSE_STATUS));
                service.setBloodPressure(o.getString(Column.BLOOD_PRESSURE));
                service.setBloodPressureType(o.getString(Column.BLOOD_PRESSURE_TYPE));
                service.setTemperature(o.getDouble(Column.TEMPERATURE));
                service.setTemperatureType(o.getString(Column.TEMPERATURE_TYPE));
                service.setWeight(o.getDouble(Column.WEIGHT));
                service.setHeightInCm(o.getLong(Column.HEIGHT_IN_CM));
                service.setWeeklyWeightGain(o.getDouble(Column.WEEKLY_WEIGHT_GAIN));
                service.setAnaemia(o.getString(Column.ANAEMIA));
                service.setJaundice(o.getString(Column.JAUNDICE));
                service.setOedema(o.getString(Column.OEDEMA));
                service.setVomiting(o.getString(Column.VOMITING));
                service.setSugarOfUrine(o.getString(Column.SUGAR_OF_URINE));
                service.setProteinOfUrine(o.getString(Column.PROTEIN_OF_URINE));
                service.setHeightOfUterus(o.getString(Column.HEIGHT_OF_UTERUS));
                service.setFetalMovement(o.getString(Column.FETAL_MOVEMENT));
                service.setFetalHeartRate(o.getString(Column.FETAL_HEART_RATE));
                service.setFetalLie(o.getString(Column.FETAL_LIE));
                service.setFetalPresentation(o.getString(Column.FETAL_PRESENTATION));
                service.setBreastProblem(o.getString(Column.BREAST_PROBLEM));
                service.setVitaminA(o.getString(Column.VITAMIN_A));
                service.setRiskState(o.getString(Column.RISK_STATE));
                service.setRiskProp(o.getLong(Column.RISK_PROP));
                //	service.setMaterenalStatus(o.getString(Column.MATERENAL_STATUS));
                service.setMaternalCareId(o.getLong(Column.MATERNAL_CARE_ID));
                service.setInterviewId(o.getLong(Column.INTERVIEW_ID));
                service.setCreateDate(Utility.getMillisecondFromDate(o.getString(Column.CREATE_DATE), Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
                service.setUserId(o.getLong(Column.USER_ID));
                service.setBenefCode(o.getString(Column.BENEF_CODE));
                service.setLmp(Utility.getMillisecondFromDate(o.getString(Column.LMP), Constants.DATE_FORMAT_YYYY_MM_DD));
                if (o.has(Column.TRANS_REF)) service.setTransRef(o.getLong(Column.TRANS_REF));
                services.add(service);
            }
        } catch (Exception exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return services;
    }

    public static ArrayList<MaternalBabyInfo> parseMaternalBabyInfos(JSONArray array) throws MhealthException {
        ArrayList<MaternalBabyInfo> babyInfos = new ArrayList<MaternalBabyInfo>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);

                MaternalBabyInfo babyInfo = new MaternalBabyInfo();
                babyInfo.setMaternalBabyId(o.getLong(Column.MATERNAL_BABY_ID));
                babyInfo.setBabyState(o.getString(Column.BABY_STATE));
                babyInfo.setGender(o.getString(Column.GENDER));
                babyInfo.setMaternalId(o.getLong(Column.MATERNAL_ID));
                babyInfo.setChildBenefCode(o.getString(Column.CHILD_BENEF_CODE));
                babyInfo.setBenefCode(o.getString(Column.BENEF_CODE));
                if (o.has(Column.TRANS_REF)) babyInfo.setTransRef(o.getLong(Column.TRANS_REF));
                babyInfo.setLmp(Utility.getMillisecondFromDate(o.getString(Column.LMP), Constants.DATE_FORMAT_YYYY_MM_DD));
                babyInfos.add(babyInfo);
            }
        } catch (Exception exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return babyInfos;
    }

    public static ArrayList<MaternalDelivery> parseMaternalDeliveries(JSONArray array) throws MhealthException {
        ArrayList<MaternalDelivery> deliveries = new ArrayList<MaternalDelivery>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                MaternalDelivery delivery = new MaternalDelivery();
                delivery.setMaternalDeliveryId(o.getLong(Column.MATERNAL_DELIVERY_ID));
                delivery.setMaternalId(o.getLong(Column.MATERNAL_ID));
                delivery.setMotherCondition(o.getString(Column.MOTHER_CONDITION));


                if (o.has(Column.DELIVERY_DATE)) {
                    try {
                        delivery.setDeliveryDate(Utility.getMillisecondFromDate(o.getString(Column.DELIVERY_DATE), Constants.DATE_FORMAT_YYYY_MM_DD));
                    } catch (Exception e) {
                    }
                }

                if (o.has(Column.DELIVERY_TIME)) {
                    delivery.setDeliveryTime(o.getString(Column.DELIVERY_TIME));
                }

                if (o.has(Column.DELIVERY_PLACE)) {
                    delivery.setDeliveryPlace(o.getString(Column.DELIVERY_PLACE));
                }
                if (o.has(Column.TRANS_REF)) delivery.setTransRef(o.getLong(Column.TRANS_REF));
                delivery.setDeliveredByCsba(o.getString(Column.DELIVERED_BY_CSBA));
                delivery.setDeliveryType(o.getString(Column.DELIVERY_TYPE));
                delivery.setPersonDelivered(o.getString(Column.PERSON_DELIVERED));
                delivery.setNoOfBaby(o.getLong(Column.NO_OF_BABY));
                delivery.setDeliveryCareInterviewId(o.getLong(Column.DELIVERY_CARE_INTERVIEW_ID));
                delivery.setLmp(Utility.getMillisecondFromDate(o.getString(Column.LMP), Constants.DATE_FORMAT_YYYY_MM_DD));
                delivery.setBenefCode(o.getString(Column.BENEF_CODE));
                deliveries.add(delivery);
            }
        } catch (Exception exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return deliveries;
    }

    public static ArrayList<MaternalAbortion> parseMaternalAbortions(JSONArray array) throws MhealthException {
        ArrayList<MaternalAbortion> abortions = new ArrayList<MaternalAbortion>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                MaternalAbortion abortion = new MaternalAbortion();

                abortion.setAbortId(o.getLong(Column.ABORT_ID));
                abortion.setAbortById(o.getLong(Column.ABORT_BY_ID));
                abortion.setAbortPlaceId(o.getLong(Column.ABORT_PLACE_ID));
                abortion.setBenefCode(o.getString(Column.BENEF_CODE));
                abortion.setBenefId(o.getLong(Column.BENEF_ID));
                abortion.setUnusalOutcomeType(o.getString(Column.UNUSUAL_OUTCOME_TYPE));
                abortion.setAbortDate(Utility.getMillisecondFromDate(o.getString(Column.ABORT_DATE), Constants.DATE_FORMAT_YYYY_MM_DD));
                abortion.setPregnancyWeek(o.getLong(Column.PREGNANCY_WEEK));
                abortion.setInterviewId(o.getLong(Column.INTERVIEW_ID));
                if (o.has(Column.TRANS_REF)) abortion.setTransRef(o.getLong(Column.TRANS_REF));
                abortions.add(abortion);
            }
        } catch (Exception exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }

        return abortions;
    }


    public static ArrayList<UserScheduleInfo> parseScheduleInfosReport(JSONObject jObj) throws MhealthException {
        ArrayList<UserScheduleInfo> scheduleInfos = new ArrayList<UserScheduleInfo>();
        if (jObj != null && jObj.has("SCHEDULE_INFO")) {

            try {
                JSONArray jArr = jObj.getJSONArray("SCHEDULE_INFO");
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject jReportObj = jArr.getJSONObject(i);
                    UserScheduleInfo scheduleInfo = new UserScheduleInfo();

                    if (jReportObj.has("CODE")) {
                        scheduleInfo.setBeneficiaryCode(jReportObj.getString("CODE").trim());
                    }

                    if (jReportObj.has("NAME")) {
                        scheduleInfo.setBeneficiaryName(jReportObj.getString("NAME").trim());
                    }

                    if (jReportObj.has("TYPE")) {
                        scheduleInfo.setScheduleType(jReportObj.getString("TYPE"));
                        scheduleInfo.setDescription(jReportObj.getString("TYPE"));
                    }

                    if (jReportObj.has("SCHED_DATE")) {
                        scheduleInfo.setScheduleDateStr(jReportObj.getString("SCHED_DATE"));
                    }

                    scheduleInfos.add(scheduleInfo);
                }
            } catch (JSONException exception) {
                throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
            }

        }

        return scheduleInfos;
    }

    public static String preapreRegirtrationQuestionear(String jsondata, Beneficiary beneficiary) {
        try {
            JSONObject jObj = new JSONObject(jsondata);
            JSONObject jDataObj = jObj.getJSONObject("QUESTIONNAIRE_DATA");
            JSONObject jQuetionnaireObj = jDataObj.getJSONObject(KEY.QUESTIONNAIRE);
            Iterator<String> keyList = jQuetionnaireObj.keys();
            ArrayList<Question> allQuestion = new ArrayList<Question>();
            while (keyList.hasNext()) {
                String key = keyList.next();
                JSONObject jQuestionObj = jQuetionnaireObj.getJSONObject(key);
                if (beneficiary.getHhNumber() != null && "HH_NO".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getHhNumber().replace(App.getContext().getUserInfo().getUserCode(), "").trim());
                } else if (beneficiary.getBenefName() != null && "B_NAME".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, getNameWithLocalName(beneficiary.getBenefName(), beneficiary.getBenefLocalName()));
                } else if (beneficiary.getRelationToGurdian() != null && "B_GUARDIAN".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getRelationToGurdian());
                } else if (beneficiary.getGuardianName() != null && jQuestionObj.getString(KEY.QNAME).startsWith("B_GUARDIAN_NAME")) {
                    jQuestionObj.put(KEY.USER_INPUT, getNameWithLocalName(beneficiary.getGuardianName(), beneficiary.getGuardianLocalName()));
                } else if (beneficiary.getOccupation() != null && "B_OCCUPATION".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getOccupation());
                } else if (beneficiary.getOccupationHerHusband() != null && "B_OCCUPATION_HER_HUSBAND".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getOccupationHerHusband());
                } else if (beneficiary.getMonthlyFamilyExpenditure() != null && "B_Monthly_family_expenditure".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getMonthlyFamilyExpenditure());
                } else if (beneficiary.getMaritalStatus() != null && "B_MARITAL_STATUS".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getMaritalStatus());
                } else if (beneficiary.getGender() != null && "B_SEX".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getGender());
                } else if (beneficiary.getGender() != null && "B_DOB".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getDob());
                } else if (beneficiary.getBenefImagePath() != null && "B_IMG".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getBenefImagePath());
                } else if (beneficiary.getReligion() != null && "B_RELIGION".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getReligion());
                } else if (beneficiary.getNationalIdNumber() != null && "B_NATIONAL_ID".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getNationalIdNumber());
                } else if (beneficiary.getBirthCertificateNumber() != null && "B_BIRTH_REG_ID".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getBirthCertificateNumber());
                } else if ("B_RELA".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getFamilyHead() == 1 ? "Self" : "Member");
                } else if (beneficiary.getMobileComm() != null && jQuestionObj.getString(KEY.QNAME).startsWith("B_MOB_COM")) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getMobileComm());
                } else if (beneficiary.getMobileNumber() != null && "B_MOB".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getMobileNumber().replace("+88", ""));
                } else if ("B_EDU".equals(jQuestionObj.getString(KEY.QNAME))) {
                    jQuestionObj.put(KEY.USER_INPUT, beneficiary.getEduLevel());
                }
            }
            return jObj.toString();
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return "";
    }


    public static String preapreQuestionearWithAns(String jsondata, String jsonAns) {
        try {
            Map<String, String> ansMap = new HashMap<>();

            JSONObject jObj = new JSONObject(jsondata);
            JSONObject jDataObj = jObj.getJSONObject("QUESTIONNAIRE_DATA");
            JSONObject jQuetionnaireObj = jDataObj.getJSONObject(KEY.QUESTIONNAIRE);
            Iterator<String> keyList = jQuetionnaireObj.keys();


            JSONArray ansList = (new JSONObject(jsonAns)).getJSONArray("questions");
            for (int i = 0; i < ansList.length(); i++) {
                ansMap.put("question" + ansList.getJSONObject(i).getString("qkey"), ansList.getJSONObject(i).getString("answer"));
            }

            while (keyList.hasNext()) {
                String key = keyList.next();
                JSONObject jQuestionObj = jQuetionnaireObj.getJSONObject(key);

                if (ansMap.containsKey(key)) {
                    if (jQuestionObj.getString(KEY.QTYPE).equals(QUESTION_TYPE.PRESCRIPTION)) {
                        JSONArray options = new JSONArray();
                        String[] ans = ansMap.get(key).split("\\|");
                        int x = 0;
                        for (String a : ans) {
                            JSONObject object = new JSONObject();
                            object.put(KEY.ID, ++x);
                            object.put(KEY.CAPTION, a);
                            object.put(KEY.VALUE, a);
                            options.put(object);
                        }
                        if (options.length() > 0) {
                            jQuestionObj.put(KEY.OPTIONS, options);
                        }


                    }

                    jQuestionObj.put(KEY.USER_INPUT, ansMap.get(key));

                }

            }
            return jObj.toString();
        } catch (JSONException exception) {
        }
        return jsondata;
    }

    public static String getNameWithLocalName(String name, String localName) {
        if (localName != null && localName.trim().length() > 0) {
            return name + " (" + localName + ")";
        }
        return name;
    }

    public static String getString(JSONObject data, String key) {
        try {
            return data.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public static String getString(JSONObject data, String key, String defaultValue) {
        try {
            return data.getString(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static Long getLong(JSONObject data, String key) {
        try {
            return data.getLong(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public static Long getLongNullAllow(JSONObject data, String key) {
        try {
            return data.getLong(key);
        } catch (Exception e) {
            return 0L;
        }
    }

    public static int getInt(JSONObject data, String key) {
        try {
            return data.getInt(key);
        } catch (JSONException e) {
            return -1;
        }
    }

    public static Double getDouble(JSONObject data, String key) {
        try {
            return data.getDouble(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public static JSONArray getJsonArray(JSONObject data, String key) {
        try {
            return data.getJSONArray(key);
        } catch (JSONException e) {
            return null;
        }
    }

    public static ArrayList<PatientInterviewDoctorFeedback> parsePatientInterviewDoctorFeedbackList(JSONArray jCCSReasonArr) throws MhealthException {
        ArrayList<PatientInterviewDoctorFeedback> patientInterviewDoctorFeedbacks = new ArrayList<PatientInterviewDoctorFeedback>();
        try {
            for (int i = 0; i < jCCSReasonArr.length(); i++) {
                JSONObject ccsStatusObj = jCCSReasonArr.getJSONObject(i);
                PatientInterviewDoctorFeedback patientInterviewDoctorFeedback = new PatientInterviewDoctorFeedback();
                patientInterviewDoctorFeedback.setDocFollowupId(ccsStatusObj.getLong("DOC_FOLLOWUP_ID"));
                patientInterviewDoctorFeedback.setBenefCode(ccsStatusObj.getString(Column.BENEF_CODE));
                patientInterviewDoctorFeedback.setInterviewId(ccsStatusObj.getLong(Column.INTERVIEW_ID));
                patientInterviewDoctorFeedback.setFcmInterviewId(ccsStatusObj.getLong(Column.FCM_INTERVIEW_ID));
                patientInterviewDoctorFeedback.setUserId(ccsStatusObj.getLong(Column.USER_ID));

                try {
                    patientInterviewDoctorFeedback.setFeedbackDate(Utility.getMillisecondFromDate(ccsStatusObj.getString("FEEDBACK_DATE"), Constants.DATE_FORMAT_YYYY_MM_DD));
                } catch (ParseException e) {
                    patientInterviewDoctorFeedback.setFeedbackDate(0);
                    e.printStackTrace();
                }

                //patientInterviewDoctorFeedback.setFeedbackDate(0);
                patientInterviewDoctorFeedback.setDoctorFindings(ccsStatusObj.getString("DOCTOR_FINDINGS"));
                patientInterviewDoctorFeedback.setRefCenterId(ccsStatusObj.getLong("REF_CENTER_ID"));
                patientInterviewDoctorFeedback.setPrescribedMedicine(ccsStatusObj.getString("PRESCRIBED_MEDICINE"));

                try {
                    if (ccsStatusObj.getString(Column.NEXT_FOLLOWUP_DATE).isEmpty() || ccsStatusObj.getString(Column.NEXT_FOLLOWUP_DATE) == null) {
                        patientInterviewDoctorFeedback.setNextFollowupDate(0);
                    } else {
                        patientInterviewDoctorFeedback.setNextFollowupDate(Utility.getMillisecondFromDate(ccsStatusObj.getString("NEXT_FOLLOWUP_DATE"), Constants.DATE_FORMAT_YYYY_MM_DD));
                    }

                } catch (ParseException e) {
                    patientInterviewDoctorFeedback.setNextFollowupDate(0);
                    e.printStackTrace();
                }

                //patientInterviewDoctorFeedback.setNextFollowupDate(0);
                try {
                    patientInterviewDoctorFeedback.setMessageToFCM(ccsStatusObj.getString("MESSAGE_TO_FCM"));
                } catch (Exception e) {
                    e.printStackTrace();
                    patientInterviewDoctorFeedback.setMessageToFCM(e.getMessage());
                }

                patientInterviewDoctorFeedback.setIsFeedbackOnTime(ccsStatusObj.getLong("IS_FEEDBACK_ON_TIME"));
                patientInterviewDoctorFeedback.setFeedbackSource(ccsStatusObj.getString("FEEDBACK_SOURCE"));
                patientInterviewDoctorFeedback.setInvesAdvice(ccsStatusObj.getString("INVES_ADVICE"));
                patientInterviewDoctorFeedback.setInvesResult(ccsStatusObj.getString("INVES_RESULT"));
                patientInterviewDoctorFeedback.setInvesStatus(ccsStatusObj.getString("INVES_STATUS"));
                patientInterviewDoctorFeedback.setTransRef(ccsStatusObj.getLong("TRANS_REF"));
                if (ccsStatusObj.getString("QUESTION_ANSWER_JSON") == null) {
                    patientInterviewDoctorFeedback.setQuestionAnsJson("");
                } else {
                    patientInterviewDoctorFeedback.setQuestionAnsJson(ccsStatusObj.getString("QUESTION_ANSWER_JSON"));
                }
                if (ccsStatusObj.getString("QUESTION_ANSWER_JSON2") == null) {
                    patientInterviewDoctorFeedback.setQuestionAnsJson2("");
                } else {
                    patientInterviewDoctorFeedback.setQuestionAnsJson2(ccsStatusObj.getString("QUESTION_ANSWER_JSON2"));
                }

                //patientInterviewDoctorFeedback.setNotificationStatus(ccsStatusObj.getInt(Column.NOTIFICATION_STATUS));

                if (ccsStatusObj.getInt("NOTIFICATION_STATUS") == -1) {
                    patientInterviewDoctorFeedback.setNotificationStatus(-1);
                }

                if (ccsStatusObj.getString("FEEDBACK_RECEIVE_TIME") == null) {
                    patientInterviewDoctorFeedback.setFeedbackReceiveTime(0);
                } else {
                    try {
                        patientInterviewDoctorFeedback.setFeedbackReceiveTime(Utility.getMillisecondFromDate(ccsStatusObj.getString("FEEDBACK_RECEIVE_TIME"), Constants.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    patientInterviewDoctorFeedback.setUpdateBy(ccsStatusObj.getLong(Column.UPDATE_BY));
                } catch (Exception e) {
                    patientInterviewDoctorFeedback.setUpdateBy(0);
                    e.printStackTrace();
                }
                try {
                    patientInterviewDoctorFeedback.setUpdateOn(ccsStatusObj.getLong(Column.UPDATE_ON));
                } catch (Exception e) {
                    patientInterviewDoctorFeedback.setUpdateOn(0);
                    e.printStackTrace();
                }
                patientInterviewDoctorFeedbacks.add(patientInterviewDoctorFeedback);
            }
        } catch (JSONException exception) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION, "JSON EXCEPTION", exception);
        }
        return patientInterviewDoctorFeedbacks;
    }


}




