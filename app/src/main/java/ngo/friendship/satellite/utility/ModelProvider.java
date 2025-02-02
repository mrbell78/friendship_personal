package ngo.friendship.satellite.utility;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.MainActivity;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.ScheduleStatus;
import ngo.friendship.satellite.error.ErrorCode;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.BeneficiaryMigration;
import ngo.friendship.satellite.model.CCSBeneficiary;
import ngo.friendship.satellite.model.CourtyardMeeting;
import ngo.friendship.satellite.model.EnglishMonthInfo;
import ngo.friendship.satellite.model.Household;
import ngo.friendship.satellite.model.ImmunaizationBeneficiary;
import ngo.friendship.satellite.model.ImmunizationInfo;
import ngo.friendship.satellite.model.MedicineConsumptionInfoModel;
import ngo.friendship.satellite.model.MyData;
import ngo.friendship.satellite.model.PatientInterviewDetail;
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedback;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionAnswer;
import ngo.friendship.satellite.model.QuestionList;
import ngo.friendship.satellite.model.QuestionnaireDetail;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.model.maternal.MaternalAbortion;
import ngo.friendship.satellite.model.maternal.MaternalBabyInfo;
import ngo.friendship.satellite.model.maternal.MaternalDelivery;
import ngo.friendship.satellite.model.maternal.MaternalInfo;
import ngo.friendship.satellite.model.maternal.MaternalService;

public class ModelProvider {
    // create response model from response data
    public static ResponseData getResponseModel(ResponseData responseData, String data)
            throws MhealthException {
        try {
            JSONObject jObj = new JSONObject(data);
            responseData.setResponseType(jObj.getString(KEY.RESPONSE_TYPE));
            responseData.setResponseName(jObj.getString(KEY.RESPONSE_NAME));
            responseData.setResponseCode(jObj.getString(KEY.RESPONSE_CODE));
            responseData.setExecTime(jObj.getString(KEY.RESPONSE_EXECT_TIME));
            if (responseData.getResponseCode().equalsIgnoreCase("00")) {
                responseData.setErrorCode(jObj
                        .getString(KEY.RESPONSE_ERROR_CODE));
                responseData.setErrorDesc(jObj
                        .getString(KEY.RESPONSE_ERROR_DESC));

                if (jObj.has(KEY.PARAM1)) {
                    responseData.setParam(jObj.getString(KEY.PARAM1));
                    responseData.setParamJson(jObj.getJSONObject(KEY.PARAM1));

                }
            } else {
                responseData.setDataLength(jObj.getInt(KEY.RESPONSE_DATA_LENGTH));
                responseData.setData(jObj.getString(KEY.RESPONSE_DATA));
                responseData.setDataJson(jObj.getJSONObject(KEY.RESPONSE_DATA));
                if (jObj.has(KEY.PARAM1)) {
                    responseData.setParam(jObj.getString(KEY.PARAM1));
                    responseData.setParamJson(jObj.getJSONObject(KEY.PARAM1));

                }
            }

        } catch (JSONException e) {
            throw new MhealthException(ErrorCode.JSON_EXCEPTION,
                    "JSON_EXCEPTION");
        }
        return responseData;
    }

    // create beneficiary model from questionanswer map
    public static Beneficiary getBeneficiaryModel(Beneficiary beneficiary,
                                                  HashMap<String, QuestionAnswer> questionAnswers) {

        if (questionAnswers.size() == 0)
            return null;
        // for (QuestionAnswer questionAnswer:questionAnswers )
        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            /*
             * if (qName != null && qName.trim().equalsIgnoreCase("HH_NO")) {
             * beneficiary.setHhId(Integer.parseInt(qAnswer)); } else
             */
            if (qName != null && qName.trim().equalsIgnoreCase("B_NAME")) {
                String nameList[] = qAnswer.split("\\(");
                String engName = nameList[0].trim();
                String localName = "";
                if (nameList.length > 1) {
                    localName = nameList[1].replace(")", "");
                }
                beneficiary.setBenefName(engName);
                beneficiary.setBenefLocalName(localName);

            } else if (qName != null && qName.trim().equalsIgnoreCase("B_IMG")) {
                beneficiary.setBenefImagePath(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_MOB_COM")) {
                beneficiary.setMobileComm(qAnswer);
            } else if (qName != null && qName.trim().equalsIgnoreCase("B_MOB")) {
                beneficiary.setMobileNumber(qAnswer);
            } else if (qName != null && qName.trim().equalsIgnoreCase("B_SEX")) {
                beneficiary.setGender(qAnswer);
            } else if (qName != null && qName.trim().equalsIgnoreCase("B_EDU")) {
                if (qAnswer == null || qAnswer.equalsIgnoreCase(""))
                    beneficiary.setEduLevel("0");
                else
                    beneficiary.setEduLevel(qAnswer);
            }

            // // Commented out because this question has been removed from
            // questionnaire.
            // App gets the value for family head property from B_RELA question
            /*
             * else if (qName != null &&
             * qName.trim().equalsIgnoreCase("B_TYPE")) {
             * beneficiary.setFamilyHead(Integer.parseInt(qAnswer)); }
             */
            else if (qName != null && qName.trim().equalsIgnoreCase("B_RELA")) {
                // beneficiary.setRelationToFamilyHead(qAnswer);
                if (qAnswer.equalsIgnoreCase("Self")) {
                    beneficiary.setFamilyHead(1);
                } else {
                    beneficiary.setFamilyHead(0);
                }
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_MARITAL_STATUS")) {
                beneficiary.setMaritalStatus(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_OCCUPATION")) {
                beneficiary.setOccupation(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_RELIGION")) {
                beneficiary.setReligion(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_RELIGION_OTHER_SPECIFIC")) {
                beneficiary.setReligionOtherSpecofic(qAnswer);
            } else if (qName != null && qName.trim().equalsIgnoreCase("B_DOB")) {
                beneficiary.setDob(qAnswer);
            } else if (qName != null && qName.trim().equals("B_GUARDIAN")) {
                beneficiary.setRelationToGurdian(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_NATIONAL_ID")) {
                beneficiary.setNationalIdNumber(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_BIRTH_REG_ID")) {
                beneficiary.setBirthCertificateNumber(qAnswer);
            } else if (qName != null && qName.trim().startsWith("B_GUARDIAN_NAME")) {
                beneficiary.setGuardianName(qAnswer);
                beneficiary.setGuardianLocalName(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_OCCUPATION_HER_HUSBAND")) {
                beneficiary.setOccupationHerHusband(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_Monthly_family_expenditure")) {
                beneficiary.setMonthlyFamilyExpenditure(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("HH_FAMILY_MEMBERS")) {
                try {
                    if (qAnswer == null || qAnswer.equalsIgnoreCase("")) {
                        beneficiary.setHhFamilyMembers(0);
                    } else {
                        beneficiary.setHhFamilyMembers(Integer.parseInt(qAnswer));
                    }

                } catch (Exception e) {
                    beneficiary.setHhFamilyMembers(0);
                }


            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("HH_CHARACTER")) {
                beneficiary.setHhCharacter(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("B_ADDRESS")) {
                beneficiary.setAddress(qAnswer);
            } else if (qName != null
                    && qName.trim().equalsIgnoreCase("HH_ADULT_WOMEN")) {
                try {
                    if (qAnswer == null || qAnswer.equalsIgnoreCase("")) {
                        beneficiary.setHhAdultWomen(0);
                    } else {
                        beneficiary.setHhAdultWomen(Long.parseLong(qAnswer));
                    }
                } catch (Exception e) {
                    beneficiary.setHhAdultWomen(0);
                }
            }


            beneficiary.setState(1);
        }
        return beneficiary;
    }

    /**
     * Create the Household model and return.
     *
     * @param household       the household
     * @param questionAnswers
     * @return The Household model
     */
    public static Household getHouseholdModel(Household household,
                                              HashMap<String, QuestionAnswer> questionAnswers) {
        if (questionAnswers.size() == 0)
            return null;
        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            if (questionAnswer.getQuestionName() == null
                    || !(questionAnswer.getQuestionName().trim()
                    .equalsIgnoreCase("HH_NO")))
                continue;
            String qName = questionAnswer.getQuestionName();
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            household.setHhNumber(qAnswer);
            break;
        }
        return household;
    }

    /**
     * Create CCS beneficiary model and return.
     *
     * @param ccsBeneficiary  The CCS beneficiary object which will be initialized
     * @param questionAnswers The question answer list
     * @return the CCS beneficiary model
     */

    public static CCSBeneficiary getCCSBeneficiaryModel(
            CCSBeneficiary ccsBeneficiary,
            HashMap<String, QuestionAnswer> questionAnswers) {

        if (questionAnswers.size() == 0)
            return null;

        ccsBeneficiary.setCcsStatusId(1);
        ccsBeneficiary.setNextFollowupDate(-1);
        ccsBeneficiary.setTreatmentProgress(0);
        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;


            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();


            if (qName.equalsIgnoreCase("NEXT_FOLLOWUP_DATE")) {
                try {
                    ccsBeneficiary.setNextFollowupDate(Utility.getMillisecondFromDate(qAnswer, Constants.DATE_FORMAT_YYYY_MM_DD));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (qName.equalsIgnoreCase("ACTIVITY_START_DATE")) {
                try {
                    ccsBeneficiary.setActivityStartDate(Utility.getMillisecondFromDate(qAnswer, Constants.DATE_FORMAT_YYYY_MM_DD));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (qName.equalsIgnoreCase("RE_ELIGIBLE_DATE")) {
                ccsBeneficiary.setReEligibleDate(qAnswer);
            } else if (qName.equalsIgnoreCase("SYS_REASON_FOR_NOT_TO_GO")) {
                ccsBeneficiary.setReasonForNotToDoTest(qAnswer.replaceAll("##", ","));
            } else if (qName.equalsIgnoreCase("commited_date_to_go")) {
                ccsBeneficiary.setComittedDateToGoHospital(qAnswer);
            } else if (qName.equalsIgnoreCase("REFERRAL_CENTER")) {
                ccsBeneficiary.setReferralCenterId(Utility.parseLong(qAnswer));
            } else if (qName.equalsIgnoreCase("CCS_STATUS")) {
                long ccsStatusId = App.getContext().getDB().getCcsDao().getCCSStatusID(qAnswer);
                ccsBeneficiary.setCcsStatusId(ccsStatusId);
            } else if (qName.equalsIgnoreCase("Pregnancy_Status")) {
                if (qAnswer.trim().equalsIgnoreCase("Pregnant")) {
                    ccsBeneficiary.setPregnant(true);
                }
            } else if (qName.equalsIgnoreCase("FOLLOWUP_VISIT")) {
                try {
                    ccsBeneficiary.setFollowUpVisit(Long.parseLong(qAnswer));
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }


        }

        return ccsBeneficiary;
    }

    /**
     * Gets the immunizable beneficiary model.
     *
     * @param immunaizationBeneficiary the epi beneficiary
     * @param questionAnswers          the question answers
     * @return the immunizable beneficiary model
     */
    public static ImmunaizationBeneficiary getImmueneficiaryModel(
            ImmunaizationBeneficiary immunaizationBeneficiary,
            HashMap<String, QuestionAnswer> questionAnswers) {

        if (questionAnswers.size() == 0)
            return null;

        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName.startsWith("NEXT_IMMUNIZATION_DATE")) {
                immunaizationBeneficiary.setNextImmunizationDateStr(qAnswer);
            } else if (qName.startsWith("COMPLETE_SCHEDULE")) {
                if (qAnswer.equals("YES")) {
                    immunaizationBeneficiary.setComplete(true);
                }
            } else if (qName.trim().startsWith("SCHEDULE_CARD")) {
                ArrayList<ImmunizationInfo> epiList = new ArrayList<ImmunizationInfo>();
                for (String answer : questionAnswer.getAnswerList()) {
                    ImmunizationInfo imi = new ImmunizationInfo();
                    imi.setId(Utility.parseLong(answer.split("#")[0]));
                    String dateStr = answer.split("#")[3];
                    imi.setTakenDateInMillis(Utility.parseLong(dateStr));
                    epiList.add(imi);
                }
                immunaizationBeneficiary.setImmunizationlist(epiList);
            } else if (qName.startsWith("REASON_OF_MISSING_IMMUNIZATION")) {
                immunaizationBeneficiary.setImmunizationMissReasonName(qAnswer);
            } else if (qName.startsWith("TT_VACCINE_CURRENT_STATUS")) {
                immunaizationBeneficiary.setImmunizationCurrentStatus(qAnswer);
                if (qAnswer.equals("Complete_all_doses_of_tt")) {
                    immunaizationBeneficiary.setComplete(true);
                }
            }
        }
        return immunaizationBeneficiary;
    }

    public static PatientInterviewDoctorFeedback getPatientInterviewDoctorFeedback(
            PatientInterviewDoctorFeedback doctorFeedback, QuestionList qList) {

        for (Question question : qList.getQuestionList()) {
            if (question.getName().trim().equals(Constants.NEXT_FOLLOWUP_DATE)) {
                try {
                    doctorFeedback.setNextFollowupDate(Utility
                            .getMillisecondFromDate(question.getDefaultValue()
                                    .get(0), Constants.DATE_FORMAT_YYYY_MM_DD));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (question.getName().trim()
                    .equals(Constants.REFERRAL_CENTER_FROM_DOCTORCENTER)) {
                doctorFeedback.setRefCenterId(Utility.parseLong(question
                        .getDefaultValue().get(0)));
            }
            if (question.getName().trim().equals(Constants.PRESCRIPTION_DOCTORCENTER)) {
                doctorFeedback.setPrescribedMedicine(question.getDefaultValue()
                        .get(0));
            }
            if (question.getName().trim().equals(Constants.MESSAGE_TO_FCM)) {
                doctorFeedback.setMessageToFCM(question.getHint());
            }
//			if (question.getName().trim()
//					.equals("FULL_DOCTOR_FEEDBACK")) {
//				try {
//					JSONObject jsonObject=new JSONObject(question
//							.getDefaultValue().get(0));
//					doctorFeedback.setDocFollowupId(jsonObject.getLong("DOC_FOLLOWUP_ID"));
//					//doctorFeedback.setInterviewId(jsonObject.getLong("INTERVIEW_ID"));
//					doctorFeedback.setDoctorFindings(jsonObject.getString("DOCTOR_FINDINGS"));
//					//doctorFeedback.setPrescribedMedicine(jsonObject.getString("PRESCRIBED_MEDICINE"));
//					//doctorFeedback.setNextFollowupDate(jsonObject.getLong("NEXT_FOLLOWUP_DATE"));
//					//doctorFeedback.setRefCenterId(jsonObject.getLong("REF_CENTER_ID"));
//					doctorFeedback.setUserId(jsonObject.getLong("USER_ID"));
//					doctorFeedback.setIsFeedbackOnTime(jsonObject.getLong("IS_FEEDBACK_ON_TIME"));
//					doctorFeedback.setFeedbackSource(jsonObject.getString("FEEDBACK_SOURCE"));
//					doctorFeedback.setInvesAdvice(jsonObject.getString("INVES_ADVICE"));
//					doctorFeedback.setInvesResult(jsonObject.getString("INVES_RESULT"));
//					doctorFeedback.setInvesStatus(jsonObject.getString("INVES_STATUS"));
//					doctorFeedback.setTransRef(jsonObject.getLong("TRANS_REF"));
//
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//			if (question.getName().trim()
//					.equals("SYS_DOCTOR_FEEDBACK_LUNCHER")) {
//				try {
//					JSONObject jsonObject=new JSONObject(question
//							.getDefaultValue().get(0));
//					doctorFeedback.setDocFollowupId(jsonObject.getLong("DOC_FOLLOWUP_ID"));
//					//doctorFeedback.setInterviewId(jsonObject.getLong("INTERVIEW_ID"));
//					doctorFeedback.setDoctorFindings(jsonObject.getString("DOCTOR_FINDINGS"));
//					//doctorFeedback.setPrescribedMedicine(jsonObject.getString("PRESCRIBED_MEDICINE"));
//					//doctorFeedback.setNextFollowupDate(jsonObject.getLong("NEXT_FOLLOWUP_DATE"));
//					//doctorFeedback.setRefCenterId(jsonObject.getLong("REF_CENTER_ID"));
//					doctorFeedback.setUserId(jsonObject.getLong("USER_ID"));
//					doctorFeedback.setIsFeedbackOnTime(jsonObject.getLong("IS_FEEDBACK_ON_TIME"));
//					doctorFeedback.setFeedbackSource(jsonObject.getString("FEEDBACK_SOURCE"));
//					doctorFeedback.setInvesAdvice(jsonObject.getString("INVES_ADVICE"));
//					doctorFeedback.setInvesResult(jsonObject.getString("INVES_RESULT"));
//					doctorFeedback.setInvesStatus(jsonObject.getString("INVES_STATUS"));
//					doctorFeedback.setTransRef(jsonObject.getLong("TRANS_REF"));
//
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
        }
        return doctorFeedback;
    }

    public static PatientInterviewDoctorFeedback getPatientInterviewDoctorFeedback(
            PatientInterviewDoctorFeedback doctorFeedback,
            HashMap<String, QuestionAnswer> qaMap) {
        if (qaMap.size() == 0)
            return null;

        for (String key : qaMap.keySet()) {
            QuestionAnswer questionAnswer = qaMap.get(key);
            if (questionAnswer.getQuestionName().trim()
                    .equals(Constants.NEXT_FOLLOWUP_DATE)) {
                try {
                    doctorFeedback.setNextFollowupDate(Utility
                            .getMillisecondFromDate(questionAnswer
                                            .getAnswerList().get(0),
                                    Constants.DATE_FORMAT_YYYY_MM_DD));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (questionAnswer.getQuestionName().trim()
                    .equals(Constants.REFERRAL_CENTER_FROM_DOCTORCENTER) ||
                    questionAnswer.getQuestionName().trim()
                            .equals(Constants.SYS_REFERRAL_CENTER_FROM_DOCTORCENTER)) {
                doctorFeedback.setRefCenterId(Utility.parseLong(questionAnswer
                        .getAnswerList().get(0)));
            }

            if (questionAnswer.getQuestionName().trim()
                    .equals(Constants.SYS_MESSAGE_TO_FCM)) {
                doctorFeedback.setMessageToFCM(questionAnswer
                        .getAnswerList().get(0));
            }
            if (questionAnswer.getQuestionName().trim()
                    .equals(Constants.PRESCRIPTION_DOCTORCENTER)) {
                String prescribedmedicine = "";
                for (int a = 0; a < questionAnswer.getAnswerList().size(); a++) {
                    if (a == (questionAnswer.getAnswerList().size() - 1)) {
                        prescribedmedicine += questionAnswer.getAnswerList().get(a);
                    } else {
                        prescribedmedicine += questionAnswer.getAnswerList().get(a) + "|";
                    }

                }
                doctorFeedback.setPrescribedMedicine(prescribedmedicine);

            }
            if (questionAnswer.getQuestionName().trim()
                    .equals(Constants.FULL_DOCTOR_FEEDBACK)) {
                try {
                    JSONObject jsonObject = new JSONObject(questionAnswer
                            .getAnswerList().get(0));
                    doctorFeedback.setDocFollowupId(jsonObject.getLong(Column.DOC_FOLLOWUP_ID));
                    //doctorFeedback.setInterviewId(jsonObject.getLong("INTERVIEW_ID"));
                    doctorFeedback.setDoctorFindings(jsonObject.getString(Column.DOCTOR_FINDINGS));
//					doctorFeedback.setPrescribedMedicine(jsonObject.getString(Column.PRESCRIBED_MEDICINE));
//					doctorFeedback.setNextFollowupDate(jsonObject.getLong(Column.NEXT_FOLLOWUP_DATE));
//					doctorFeedback.setRefCenterId(jsonObject.getLong(Column.REF_CENTER_ID));
                    doctorFeedback.setUserId(jsonObject.getLong(Column.USER_ID));
                    doctorFeedback.setIsFeedbackOnTime(jsonObject.getLong(Column.IS_FEEDBACK_ON_TIME));
                    doctorFeedback.setFeedbackSource(jsonObject.getString(Column.FEEDBACK_SOURCE));
                    doctorFeedback.setInvesAdvice(jsonObject.getString(Column.INVES_ADVICE));
                    doctorFeedback.setInvesResult(jsonObject.getString(Column.INVES_RESULT));
                    doctorFeedback.setInvesStatus(jsonObject.getString(Column.INVES_STATUS));
                    doctorFeedback.setTransRef(jsonObject.getLong(Column.TRANS_REF));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        return doctorFeedback;

    }

    public static MaternalService getMaternalService(MaternalService maternalService, HashMap<String, QuestionAnswer> questionAnswers) {
        if (questionAnswers.size() == 0)
            return null;


        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);

            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName != null && !qName.trim().equalsIgnoreCase("")) {


                if (qName.trim().equalsIgnoreCase("SYS_MATERNAL_STATUS")) {
                    try {
                        maternalService.setStatus(Long.parseLong(qAnswer));
                    } catch (Exception exception) {
                    }
                } else if (qName.trim().equalsIgnoreCase("PULSE")) {
                    maternalService.setPulse(Utility.parseLong(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("pulse_status")) {
                    maternalService.setPulseStatus(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("TYPE_OF_BLOOD_PRESSURE___FINAL")) {
                    maternalService.setBloodPressure(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("TEMPERATURE")) {
                    maternalService.setTemperature(Utility.parseDouble(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("SYS_temperature_type")) {
                    maternalService.setTemperatureType(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("BLOOD_PRESSURE_AFTER_2_MINUTES")) {
                    maternalService.setBloodPressure(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("BLOOD_PRESSURE") && maternalService.getBloodPressure() == null) {
                    maternalService.setBloodPressure(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("WEIGHT")) {
                    maternalService.setWeight(Utility.parseDouble(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("WEEKLY_WEIGHT_GAIN")) {
                    maternalService.setWeeklyWeightGain(Utility.parseDouble(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("ANAEMIA")) {
                    maternalService.setAnaemia(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("OEDEMA")) {
                    maternalService.setOedema(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("HEIGHT_OF_THE_UTERUS")) {
                    maternalService.setHeightOfUterus(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("SUGAR_IN_URINE")) {
                    maternalService.setSugarOfUrine(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("PROTEIN_IN_URINE")) {
                    maternalService.setProteinOfUrine(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("VOMITING")) {
                    maternalService.setVomiting(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("JAUNDICE")) {
                    maternalService.setJaundice(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("BMI_VALUE")) {
                    maternalService.setBmiValue(Utility.parseDouble(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("BMI")) {
                    maternalService.setBmi(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("HEIGHT")) {
                    maternalService.setHeightInCm(Utility.parseLong(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("FETAL_MOVEMENT")) {
                    maternalService.setFetalMovement(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("FETAL_HEART_RATE")) {
                    maternalService.setFetalHeartRate(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("FETAL_LIE")) {
                    maternalService.setFetalLie(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("FETAL_PRESENTATION")) {
                    maternalService.setFetalPresentation(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("RISK_STATE")) {
                    maternalService.setRiskState(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("BREAST_PROBLEM")) {
                    maternalService.setBreastProblem(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("VITAMIN_A_CAPSULE")) {
                    maternalService.setVitaminA(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("SYS_COUNT_RISK")) {
                    maternalService.setRiskProp(Utility.parseLong(qAnswer));
                }

            }
        }
        return maternalService;
    }

    public static MaternalInfo getMaternalInfo(MaternalInfo maternalInfo, HashMap<String, QuestionAnswer> questionAnswers) {

        if (questionAnswers.size() == 0)
            return null;

        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);

            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName != null && !qName.trim().equalsIgnoreCase("") && qAnswer != null && !qAnswer.trim().equalsIgnoreCase("")) {

                if (qName.trim().equalsIgnoreCase("PARA")) {
                    maternalInfo.setPara(Utility.parseLong(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("GRAVIDA")) {
                    maternalInfo.setGravida(Utility.parseLong(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("LMP")) {
                    try {
                        maternalInfo.setLmp(Utility.getMillisecondFromDate(
                                qAnswer, Constants.DATE_FORMAT_YYYY_MM_DD));
                    } catch (ParseException e) {

                    }
                } else if (qName.trim().equalsIgnoreCase("EDD")) {
                    try {
                        maternalInfo.setEdd(Utility.getMillisecondFromDate(
                                qAnswer, Constants.DATE_FORMAT_YYYY_MM_DD));
                    } catch (ParseException e) {

                    }
                } else if (qName.trim().equalsIgnoreCase("SCHEDULE_CARD_ANC_VISIT")) {
                    ArrayList<UserScheduleInfo> userScheduleInfos = new ArrayList<UserScheduleInfo>();
                    for (String string : questionAnswer.getAnswerList()) {
                        String[] sches = string.split("#");
                        UserScheduleInfo userScheduleInfo = new UserScheduleInfo();
                        userScheduleInfo.setBeneficiaryCode(maternalInfo.getBenefCode());
                        userScheduleInfo.setDescription(sches[1]);
                        userScheduleInfo.setScheduleDate(Utility.parseLong(sches[3]));
                        userScheduleInfo.setScheduleStatus(ScheduleStatus.NEW);
                        userScheduleInfo.setScheduleType(sches[2]);
                        userScheduleInfo.setReferenceId(Utility.parseLong(sches[4]));
                        userScheduleInfo.setUserId((int) App.getContext().getUserInfo().getUserId());
                        userScheduleInfos.add(userScheduleInfo);
                    }

                    maternalInfo.setUserScheduleInfos(userScheduleInfos);
                }
            }
        }
        return maternalInfo;
    }

    public static MaternalDelivery getMaternalDeliveryFromMotherAndChildRegistration(MaternalInfo mother, HashMap<String, QuestionAnswer> questionAnswers, long interviewId) {

        if (questionAnswers.size() == 0)
            return null;
        String daliveryDate = "";
        MaternalDelivery maternalDelivery = new MaternalDelivery();
        maternalDelivery.setMaternalId(mother.getMaternalId());
        maternalDelivery.setBenefCode(mother.getBenefCode());
        maternalDelivery.setLmp(mother.getLmp());
        maternalDelivery.setBabyInfos(new HashMap<String, MaternalBabyInfo>());
        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName != null && !qName.trim().equalsIgnoreCase("")) {
                if (qName.trim().equalsIgnoreCase("DELIVERY_DATE")) {
                    try {
                        maternalDelivery.setDeliveryDate(Utility.getMillisecondFromDate(qAnswer, Constants.DATE_FORMAT_YYYY_MM_DD));
                        daliveryDate = qAnswer;
                    } catch (ParseException e) {
                    }
                } else if (qName.trim().equalsIgnoreCase("MOTHER_CONDITION")) {
                    maternalDelivery.setMotherCondition(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("DELIVERY_TIME")) {
                    maternalDelivery.setDeliveryTime(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("PLACE_OF_DELIVERY")) {
                    maternalDelivery.setDeliveryPlace(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("TYPE_OF_DELIVERY")) {
                    maternalDelivery.setDeliveryType(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("PERSON_DELIVERED")) {
                    maternalDelivery.setPersonDelivered(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("NO_OF_BABY")) {
                    maternalDelivery.setNoOfBaby(Utility.parseLong(qAnswer));

                } else if (qName.trim().startsWith("BABY_STATE") || qName.trim().startsWith("GENDER") || qName.trim().startsWith("SYS_BABY_BENEF_CODE")) {
                    String[] status_part = qName.trim().split("___");
                    int index = 1;
                    String babyNameLastPart = "";
                    if (status_part.length == 2) {
                        index = Integer.parseInt(status_part[1]);
                        babyNameLastPart = "___" + index;
                    }
                    MaternalBabyInfo baby = maternalDelivery.getBabyInfos().get(index + "");
                    if (baby == null) {
                        baby = new MaternalBabyInfo();
                        maternalDelivery.getBabyInfos().put(index + "", baby);
                    }
                    baby.setMaternalId(maternalDelivery.getMaternalId());
                    if (qName.trim().startsWith("BABY_STATE") && baby != null) {
                        baby.setSerial(index);
                        baby.setBabyState(qAnswer);
                    } else if (qName.trim().startsWith("GENDER")) {
                        baby.setGender(qAnswer);
                    } else if (qName.trim().startsWith("SYS_BABY_BENEF_CODE")) {
                        baby.setAlive(true);
                        baby.setHhNumber(mother.getHhNumber());
                        baby.setHhId(mother.getHhId());
                        baby.setBenefCode(qAnswer);
                        baby.setBenefName("Baby of " + mother.getBenefName() + babyNameLastPart);
                        baby.setFamilyHead(0);
                        baby.setDob(daliveryDate);
                        baby.setRelationToGurdian("Mother");
                        baby.setGuardianName(mother.getBenefName());
                        baby.setGuardianLocalName(mother.getBenefLocalName());
                        baby.setEduLevel("0");
                        baby.setState(1);
                        baby.setOccupation("Not_Applicable");
                        baby.setMobileComm("family head");
                        baby.setReligion(mother.getReligion());
                        baby.setMaritalStatus("Single");
                        baby.setOccupation("");
                    }

                } else if (qName.trim().equalsIgnoreCase("SCHEDULE_CARD_PNC_VISIT")) {
                    ArrayList<UserScheduleInfo> userScheduleInfos = new ArrayList<UserScheduleInfo>();
                    for (String string : questionAnswer.getAnswerList()) {
                        String[] sches = string.split("#");
                        UserScheduleInfo userScheduleInfo = new UserScheduleInfo();
                        userScheduleInfo.setBeneficiaryCode(maternalDelivery.getBenefCode());
                        userScheduleInfo.setDescription(sches[1]);
                        userScheduleInfo.setScheduleDate(Utility.parseLong(sches[3]));
                        userScheduleInfo.setScheduleStatus(ScheduleStatus.NEW);
                        userScheduleInfo.setScheduleType(sches[2]);
                        userScheduleInfo.setReferenceId(Utility.parseLong(sches[4]));
                        userScheduleInfo.setUserId((int) App.getContext().getUserInfo().getUserId());
                        userScheduleInfo.setInterviewId(interviewId);
                        userScheduleInfos.add(userScheduleInfo);
                    }

                    maternalDelivery.setUserScheduleInfos(userScheduleInfos);
                }

            }
        }

        for (String key : maternalDelivery.getBabyInfos().keySet()) {
            maternalDelivery.getBabyInfos().get(key).setDob(daliveryDate);
        }
        return maternalDelivery;
    }


    public static MaternalDelivery getMaternalDeliveryFromMotherDelivery(MaternalInfo mother, HashMap<String, QuestionAnswer> questionAnswers, long interviewId) {
        if (questionAnswers.size() == 0)
            return null;
        MaternalDelivery maternalDelivery = new MaternalDelivery();
        maternalDelivery.setDeliveryCareInterviewId(interviewId);
        maternalDelivery.setMaternalId(mother.getMaternalId());
        maternalDelivery.setBenefCode(mother.getBenefCode());
        maternalDelivery.setLmp(mother.getLmp());
        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName != null && qName.trim().equalsIgnoreCase("")) {
                if (qName.trim().equalsIgnoreCase("DELIVERY_PLACE")) {
                    maternalDelivery.setDeliveryPlace(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("DELIVERED_BY_CSBA")) {
                    maternalDelivery.setDeliveredByCsba(qAnswer);
                }
            }
        }
        return maternalDelivery;
    }


    public static ContentValues getDeathRegistration(Beneficiary beneficiary, HashMap<String, QuestionAnswer> questionAnswers, long interviewId, long startTime) {
        if (questionAnswers.size() == 0) return null;
        ContentValues cv = new ContentValues();
        cv.put("BENEF_CODE", beneficiary.getBenefCode());
        cv.put("REG_DATE", startTime);
        cv.put("INTERVIEW_ID", interviewId);
        cv.put("TRANS_REF", startTime);

        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName != null && qName.trim().equalsIgnoreCase("")) {
                if (qName.trim().equalsIgnoreCase("SYS_AGE_IN_DAY")) {
                    cv.put("AGE_IN_DAY", qAnswer);
                } else if (qName.trim().equalsIgnoreCase("SYS_CAUSE_OF_DEATH")) {
                    cv.put("DEATH_REASON_ID", qAnswer);
                } else if (qName.trim().equalsIgnoreCase("DATE_OF_DEATH")) {
                    try {
                        cv.put("DATE_OF_DEATH", Utility.getMillisecondFromDate(qAnswer, Constants.DATE_FORMAT_YYYY_MM_DD));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else if (qName.trim().equalsIgnoreCase("BENEFICIARY_AGE")) {
                    cv.put("AGE", qAnswer);
                }
            }
        }
        return cv;
    }


    public static long getMaternalHighRiskCount(HashMap<String, QuestionAnswer> questionAnswers) {
        if (questionAnswers.size() == 0)
            return 0;
        long count = 0;
        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName != null && !qName.trim().equalsIgnoreCase("")) {
                if (qName.trim().equalsIgnoreCase("SYS_NUMBER_OF_RISK_SYMPTOMS_PRESENT")) {

                    try {
                        count = Utility.parseLong(qAnswer);
                    } catch (Exception exception) {
                    }
                }
            }
        }
        return count;
    }

    public static MaternalAbortion getMaternalAbortion(MaternalInfo beneficiary, HashMap<String, QuestionAnswer> questionAnswers, long iterviewId) {

        MaternalAbortion abortion = new MaternalAbortion();
        abortion.setBenefId(beneficiary.getBenefId());
        abortion.setBenefCode(beneficiary.getBenefCode());
        abortion.setInterviewId(iterviewId);
        abortion.setMaternalId(beneficiary.getMaternalId());

        if (questionAnswers.size() == 0)
            return null;

        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }
            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName != null && !qName.trim().equalsIgnoreCase("")
                    && qAnswer != null && !qAnswer.trim().equalsIgnoreCase("")) {
                if (qName.trim().equalsIgnoreCase("DATE_OF_ABORATION")) {
                    try {
                        abortion.setAbortDate(Utility.getMillisecondFromDate(qAnswer, Constants.DATE_FORMAT_YYYY_MM_DD));
                    } catch (ParseException e) {
                    }
                } else if (qName.trim().equalsIgnoreCase("SYS_DURATION_OF_PREGNANCY")) {
                    abortion.setPregnancyWeek(Utility.parseLong(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("PLACE_OF_EXPULSION_ABORTION")) {
                    long textRefId = App.getContext().getDB().getTextRefId(qAnswer, "ABORT_PLACE");
                    if (textRefId > 0) {
                        abortion.setAbortPlaceId(textRefId);
                    }
                } else if (qName.trim().equalsIgnoreCase("EXPULSION_ABORTION_ASSISTED_BY_WHOM")) {
                    long textRefId = App.getContext().getDB().getTextRefId(qAnswer, "ABORT_BY");
                    if (textRefId > 0) {
                        abortion.setAbortById(textRefId);
                    }
                } else if (qName.trim().equalsIgnoreCase("UNUSUAL_OUTCOME_TYPE")) {
                    abortion.setUnusalOutcomeType(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("SYS_MATERNAL_STATUS")) {
                    try {
                        abortion.setStatus(Long.parseLong(qAnswer));
                    } catch (Exception ex) {

                    }
                }
            }
        }
        return abortion;
    }

    public static MyData getMyData(Context context, MyData myData, ResponseData responseData)
            throws MhealthException {
        try {
            if (responseData.getDataJson() != null) {
                myData.setResponseInfo(responseData);
                JSONObject jDataObj = responseData.getDataJson();

                // ///// Referral center list///////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA) || responseData.getResponseName().equals(RequestName.REFERRAL_CENTERS)) {

                    if (jDataObj.has(DBTable.REFERRAL_CENTER_CATEGORY)) {
                        myData.setReferralCenterCategory(jDataObj.getJSONArray(DBTable.REFERRAL_CENTER_CATEGORY));
                    }

                    if (jDataObj.has(DBTable.REFERRAL_CENTER)) {
                        myData.setReferraCenterList(jDataObj.getJSONArray(DBTable.REFERRAL_CENTER));
                    }

                }

                if (responseData.getResponseName().equals(RequestName.MY_DATA_PARAMEDIC)) {
                    if (jDataObj.has(DBTable.COUPLE_REGISTRATION)) {
                        myData.setCoupleRegistration(jDataObj.getJSONArray(DBTable.COUPLE_REGISTRATION));
                    }

                }

                // ////////////////////////////////

                // ///// Questionnaire List//////
                if (responseData.getResponseName().equals(RequestName.MY_DATA) || responseData.getResponseName().equals(RequestName.QUESTIONNAIRES)) {
                    if (jDataObj.has(DBTable.QUESTIONNAIRE)) {

                        // for Encryption
                        byte[] key = App.getContext().getUserInfo().getKey(context);
                        // for not encription
                        //byte[] key=null;
                        //myData.setQuestionnaireList(JSONParser.parseQuestionnaireListJSON(key,jDataObj.getJSONArray(DBTable.QUESTIONNAIRE)));
                        myData.setQuestionnaireList(JSONParser.parseQuestionnaireListJSON(key, jDataObj.getJSONArray(DBTable.QUESTIONNAIRE)));
                    }
                }


                // ///////////////////

                // ////////// Questionnaire category list/////////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA) || responseData.getResponseName().equals(RequestName.CATEGORIES)) {

                    if (jDataObj.has(DBTable.QUESTIONNAIRE_CATEGORY)) {
                        myData.setQuestionnaireCategoryList(JSONParser.parseQuestionnaireCategoryListJSON(jDataObj.getJSONArray(DBTable.QUESTIONNAIRE_CATEGORY)));
                    }
                }


                // ////////////////////////////////

                // /////// Beneficiary List///////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA) || responseData.getResponseName().equals(RequestName.BENEFICIARIES)) {
                    if (jDataObj.has(DBTable.BENEFICIARY)) {
                        myData.setBeneficiaryList(JSONParser.parseBeneficiaryListJSON(context, jDataObj.getJSONArray(DBTable.BENEFICIARY)));
                    }
                }

                if (responseData.getResponseName().equals(RequestName.MY_DATA) || responseData.getResponseName().equals(RequestName.HOUSEHOLDS)) {
                    if (jDataObj.has(DBTable.HOUSEHOLD)) {
                        myData.setHouseholdList(JSONParser.parseHouseholdList(jDataObj.getJSONArray(DBTable.HOUSEHOLD)));
                    }
                }
                if (jDataObj.has(DBTable.HOUSEHOLD)) {
                    try {
                        myData.setHouseholdList(JSONParser.parseHouseholdList(jDataObj.getJSONArray(DBTable.HOUSEHOLD)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (jDataObj.has(DBTable.BENEFICIARY)) {
                    try {
                        myData.setBeneficiaryList(JSONParser.parseBeneficiaryListJSON(context,jDataObj.getJSONArray(DBTable.BENEFICIARY)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                // /////////////////////////////////////

                // ///////// Beneficiary List///////////////
                // if(response.getResponseName().equals(RequestName.MY_DATA)){
                // myData.setMaternalCareInfos(JSONParser.parseMaternalCareList(jDataObj.getJSONArray("maternalCareList")));
                // myData.setDiagnosisInfos(JSONParser.parseDiagnosisInfoList(jDataObj.getJSONArray("diagnosisInfo")));
                //
                // }
                // ///////////////////////////////////////

                // /// Medicine List//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA) || responseData.getResponseName().equals(RequestName.MEDICINES)) {
                    if (jDataObj.has(DBTable.MEDICINE)) {
                        myData.setMedicineList(JSONParser.parseMedicineListJSON(jDataObj.getJSONArray(DBTable.MEDICINE)));
                    }

                    if (jDataObj.has(KEY.MEDICINES_STOCK)) {
                        myData.setMedicineStock(jDataObj.getJSONArray(KEY.MEDICINES_STOCK));
                    }
                }




                // /// consumption list//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA)) {
                    if (jDataObj.has(DBTable.MEDICINE_CONSUMPTION)) {
                       // ArrayList<MedicineConsumptionInfoModel> list = JSONParser.parseMedicineConsumptionListJSON(jDataObj.getJSONArray(DBTable.MEDICINE_CONSUMPTION));
                         myData.setMedicineConsumptionList(JSONParser.parseMedicineConsumptionListJSON(jDataObj.getJSONArray(DBTable.MEDICINE_CONSUMPTION)));
                        Log.d("consumtion", "getMyData: consumption data "+jDataObj.getJSONArray(DBTable.MEDICINE_CONSUMPTION));
                       // App.getContext().getDB().saveConsumptionList(myData.getMedicineConsumptionList(), App.getContext().getUserInfo().getUserId());
                    }

                }


                // /// session list//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA)) {
                    if (jDataObj.has(KEY.SESSION_LIST)) {
                        myData.setSatelliteSessionModelArrayList(JSONParser.parseSessionListJson(jDataObj.getJSONArray(KEY.SESSION_LIST)));
                       // App.getContext().getDB().saveSessionList(myData.getSatelliteSessionModelArrayList(),myData.getVersionNos());
                    }

                }


//
//
//                // /// Adjust list//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA)) {
                    if (jDataObj.has(DBTable.MEDICINE_ADJUSTMENT)) {

                        myData.setMedicineAdjustmentList(JSONParser.parseMedicineAdjustListJSON(jDataObj.getJSONArray(DBTable.MEDICINE_ADJUSTMENT )));
                        //App.getContext().getDB().saveAdjustmentList(myData.getMedicineAdjustmentList(), App.getContext().getUserInfo().getUserId());
                    }
                }
//
//
//                // /// medicine recievie list//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA)) {
                    if (jDataObj.has(DBTable.MEDICINE_RECEIVE)) {
                        myData.setMedicineReceiveList(JSONParser.parseMedicineReceivetListJSON(jDataObj.getJSONArray(DBTable.MEDICINE_RECEIVE )));
                        //App.getContext().getDB().saveMedicineReceivedList(myData.getMedicineReceiveList(), App.getContext().getUserInfo().getUserId());
                    }
                }


                // ////////////////////////////////

                // ///////// FCM Profile/////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA)
                        || responseData.getResponseName().equals(RequestName.FCM_PROFILE)) {
                    if (jDataObj.has(KEY.fcmProfile)) {
                        myData.setUserInfo(JSONParser.parseFCMProfileJSON(jDataObj.getJSONObject(KEY.fcmProfile), context));
                    }
                    if (jDataObj.has(KEY.fcmProfileList)) {
                        myData.setUserInfoList(JSONParser.parseFCMProfileListJSON(jDataObj.getJSONArray(KEY.fcmProfileList)));
                    }
                    if (jDataObj.has(KEY.upzilaWiseLocation)) {
                        myData.setLocationList(JSONParser.parseLocationListJSON(jDataObj.getJSONArray(KEY.upzilaWiseLocation)));
                    }
                }

//
//                if (responseData.getResponseName().equals(RequestName.MY_DATA)
//                        || responseData.getResponseName().equals(RequestName.fcmProfileList)) {
//                    if (jDataObj.has(KEY.fcmProfileList)) {
//                        myData.setUserInfoList(JSONParser.parseFCMProfileListJSON(jDataObj.getJSONArray(KEY.fcmProfileList)));
//                    }
//
//                }
//
//                if (responseData.getResponseName().equals(RequestName.MY_DATA)
//                        || responseData.getResponseName().equals(RequestName.fcmProfileList)) {
//                    if (jDataObj.has(KEY.upzilaWiseLocation)) {
//                        myData.setLocationList(JSONParser.parseLocationListJSON(jDataObj.getJSONArray(KEY.upzilaWiseLocation)));
//                    }
//
//                }

                // /////////////////////////

                // ///////// App Version History /////////////
                if (responseData.getResponseName().equals(RequestName.APP_VERSION_HISTORY)) {
                    myData.setAppVersionHistory(JSONParser.parseAppVersionHistoryJSON(jDataObj.getJSONObject(DBTable.APP_VERSION_HISTORY)));
                }

                // /////////////////////////

                // ///////// Schedule List /////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA) || responseData.getResponseName().equals(RequestName.USER_SCHEDULE)
                        || responseData.getResponseName().equals(RequestName.UNDONE_SCHEDULE)) {
                    if (jDataObj.has(DBTable.USER_SCHEDULE)) {
                        myData.setScheduleList(JSONParser.parseScheduleListJSON(jDataObj.getJSONArray(DBTable.USER_SCHEDULE)));
                    }
                }


                if (responseData.getResponseName().equals(RequestName.MY_DATA) || responseData.getResponseName().equals(RequestName.CCS)) {
                    if (jDataObj.has(DBTable.CCS_STATUS))
                        myData.setCcsStatusList(JSONParser.parseCCSStatusListJSON(jDataObj.getJSONArray(DBTable.CCS_STATUS)));

                    if (jDataObj.has(DBTable.CCS_ELIGIBLE))
                        myData.setCcsEligibleJsonArry(jDataObj.getJSONArray(DBTable.CCS_ELIGIBLE));

                    if (jDataObj.has(DBTable.CCS_TEREATMENT))
                        myData.setCcsTreatmentJsonArry(jDataObj.getJSONArray(DBTable.CCS_TEREATMENT));

                }


                if (responseData.getResponseName().equals(RequestName.COURT_YARD) || responseData.getResponseName().equals(RequestName.MY_DATA)) {
                    if (jDataObj.has(DBTable.COURTYARD_MEETING_TOPIC_MONTH)) {
                        myData.setCourtyardMeetingTopicMonthJsonArray(jDataObj.getJSONArray(DBTable.COURTYARD_MEETING_TOPIC_MONTH));
                    }

                    if (jDataObj.has(DBTable.TOPIC_INFO)) {
                        myData.setTopicInfoJsonArray(jDataObj.getJSONArray(DBTable.TOPIC_INFO));
                    }

                    if (jDataObj.has(DBTable.COURTYARD_MEETING_TARGET)) {
                        myData.setCourtyardMeetingTarget(jDataObj.getJSONArray(DBTable.COURTYARD_MEETING_TARGET));
                    }

                    if (jDataObj.has(DBTable.COURTYARD_MEETING)) {
                        myData.setCourtyardMeeting(jDataObj.getJSONArray(DBTable.COURTYARD_MEETING));
                    }

                }

                // /////////////////////////

                // ///////// CCS Status/ CCS reason/////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA)) {

                    if (jDataObj.has(DBTable.TEXT_REF)) {
                        myData.setTextRefList(JSONParser.parseTextRefListJSON(jDataObj.getJSONArray(DBTable.TEXT_REF)));
                    }


                    if (jDataObj.has(DBTable.DIAGNOSIS_INFO)) {
                        myData.setDiagnosisInfos(JSONParser.parseDiagnosisInfoList(jDataObj.getJSONArray(DBTable.DIAGNOSIS_INFO)));
                    }

                    if (jDataObj.has(KEY.fcmConfigurations)) {
                        myData.setFcmComfiguration(jDataObj.getJSONArray(KEY.fcmConfigurations).toString());
                    }

                    if (jDataObj.has(DBTable.EVENT_INFO)) {
                        myData.setEventInfos(JSONParser.parseEventInfoList(jDataObj.getJSONArray(DBTable.EVENT_INFO)));
                    }


                    if (jDataObj.has(DBTable.PATIENT_INTERVIEW_MASTER)) {
                        myData.setPatientInterviewMasters(JSONParser.parsePatientInterviewMasterList(jDataObj.getJSONArray(DBTable.PATIENT_INTERVIEW_MASTER)));
                    }

                    if (jDataObj.has(DBTable.NOTIFICATION_MASTER)) {
                        myData.setNotificationMasters(JSONParser.parseNotificationMasterList(jDataObj.getJSONArray(DBTable.NOTIFICATION_MASTER)));
                    }


                    if (jDataObj.has(DBTable.HOUSEHOLD)) {
                        myData.setHouseholdList(JSONParser.parseHouseholdList(jDataObj.getJSONArray(DBTable.HOUSEHOLD)));
                    }


                    if (jDataObj.has(DBTable.MATERNAL_CARE_INFO)) {
                        myData.setMaternalCareInfos(JSONParser.parseMaternalCareList(jDataObj.getJSONArray(DBTable.MATERNAL_CARE_INFO)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_INFO)) {
                        myData.setMaternalInfos(JSONParser.parseMaternalInfos(jDataObj.getJSONArray(DBTable.MATERNAL_INFO)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_SERVICE)) {
                        myData.setMaternalServices(JSONParser.parseMaternalServices(jDataObj.getJSONArray(DBTable.MATERNAL_SERVICE)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_BABY_INFO)) {
                        myData.setMaternalBabyInfos(JSONParser.parseMaternalBabyInfos(jDataObj.getJSONArray(DBTable.MATERNAL_BABY_INFO)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_DELIVERY)) {
                        myData.setMaternalDeliveries(JSONParser.parseMaternalDeliveries(jDataObj.getJSONArray(DBTable.MATERNAL_DELIVERY)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_ABORTION)) {
                        myData.setMaternalAbortions(JSONParser.parseMaternalAbortions(jDataObj.getJSONArray(DBTable.MATERNAL_ABORTION)));
                    }

                    if (jDataObj.has(DBTable.DEATH_REGISTRATION)) {
                        myData.setDeathRegistration(jDataObj.getJSONArray(DBTable.DEATH_REGISTRATION));
                    }

                    if (jDataObj.has(DBTable.QUESTIONNAIRE_BROAD_CATEGORY)) {
                        myData.setQuestionnaireBroadCategory(jDataObj.getJSONArray(DBTable.QUESTIONNAIRE_BROAD_CATEGORY));
                    }

                    if (jDataObj.has(DBTable.QUESTIONNAIRE_SERVICE_CATEGORY)) {
                        myData.setQuestionnaireServiceCategory(jDataObj.getJSONArray(DBTable.QUESTIONNAIRE_SERVICE_CATEGORY));
                    }

                    if (jDataObj.has(DBTable.REPORT_ASST)) {
                        myData.setReportAsst(jDataObj.getJSONArray(DBTable.REPORT_ASST));
                    }

                    if (jDataObj.has(DBTable.SUGGESTION_TEXT)) {
                        myData.setSuggestionText(jDataObj.getJSONArray(DBTable.SUGGESTION_TEXT));
                    }

//
                    if (jDataObj.has(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK)) {
                        myData.setPatientInterviewDoctorFeedbacks(JSONParser.parsePatientInterviewDoctorFeedbackList(jDataObj.getJSONArray(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK)));
                    }


                }

                if (responseData.getResponseName().equals(RequestName.MY_DATA) || responseData.getResponseName().equals(RequestName.IMMUNIZATION)) {
                    if (jDataObj.has(DBTable.IMMUNIZATION_INFO))
                        myData.setImmunizationList(JSONParser.parseImmunizationListJSON(jDataObj.getJSONArray(DBTable.IMMUNIZATION_INFO)));
                    if (jDataObj.has(DBTable.IMMUNIZABLE_BENEFICIARY))
                        myData.setImmunizableBeneficiaries(JSONParser.parseImmunizableBeneficiaryList(jDataObj.getJSONArray(DBTable.IMMUNIZABLE_BENEFICIARY)));
                    if (jDataObj.has(DBTable.IMMUNIZATION_FOLLOWUP))
                        myData.setImmunizationFollowups(JSONParser.parseImmunizationFollowUpList(jDataObj.getJSONArray(DBTable.IMMUNIZATION_FOLLOWUP)));
                    if (jDataObj.has(DBTable.IMMUNIZATION_SERVICE))
                        myData.setImmunizationServices(JSONParser.parseImmunizationServiceList(jDataObj.getJSONArray(DBTable.IMMUNIZATION_SERVICE)));
                    if (jDataObj.has(DBTable.IMMUNIZATION_TARGET))
                        myData.setImmunizationTargets(JSONParser.parseImmunizationTargetList(jDataObj.getJSONArray(DBTable.IMMUNIZATION_TARGET)));
                }


                // ///////// growth_measurement_param List /////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA)) {
                    if (jDataObj.has(DBTable.GROWTH_MEASUREMENT_PARAM))
                        myData.setGrowthMeasurementParams(jDataObj.getJSONArray(DBTable.GROWTH_MEASUREMENT_PARAM));
                }

                // /////////////////////////////////////
                myData.setVersionNos(responseData.getParamJson());

            }
        } catch (JSONException exception) {
            exception.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION,
                    "JSON EXCEPTION", exception);
        }
        return myData;
    }

    public static MyData getMyDataAutoSync(Context context, MyData myData, ResponseData responseData)
            throws MhealthException {
        try {
            if (responseData.getDataJson() != null) {
                myData.setResponseInfo(responseData);
                JSONObject jDataObj = responseData.getDataJson();

                // ///// Referral center list///////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC) || responseData.getResponseName().equals(RequestName.REFERRAL_CENTERS)) {

                    if (jDataObj.has(DBTable.REFERRAL_CENTER_CATEGORY)) {
                        myData.setReferralCenterCategory(jDataObj.getJSONArray(DBTable.REFERRAL_CENTER_CATEGORY));
                    }

                    if (jDataObj.has(DBTable.REFERRAL_CENTER)) {
                        myData.setReferraCenterList(jDataObj.getJSONArray(DBTable.REFERRAL_CENTER));
                    }

                }

                if (responseData.getResponseName().equals(RequestName.MY_DATA_PARAMEDIC)) {
                    if (jDataObj.has(DBTable.COUPLE_REGISTRATION)) {
                        myData.setCoupleRegistration(jDataObj.getJSONArray(DBTable.COUPLE_REGISTRATION));
                    }

                }

                // ////////////////////////////////

                // ///// Questionnaire List//////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC) || responseData.getResponseName().equals(RequestName.QUESTIONNAIRES)) {
                    if (jDataObj.has(DBTable.QUESTIONNAIRE)) {

                        // for Encryption
                        byte[] key = App.getContext().getUserInfo().getKey(context);
                        // for not encription
                        //byte[] key=null;
                        //myData.setQuestionnaireList(JSONParser.parseQuestionnaireListJSON(key,jDataObj.getJSONArray(DBTable.QUESTIONNAIRE)));
                        myData.setQuestionnaireList(JSONParser.parseQuestionnaireListJSON(key, jDataObj.getJSONArray(DBTable.QUESTIONNAIRE)));
                    }
                }


                // ///////////////////

                // ////////// Questionnaire category list/////////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC) || responseData.getResponseName().equals(RequestName.CATEGORIES)) {

                    if (jDataObj.has(DBTable.QUESTIONNAIRE_CATEGORY)) {
                        myData.setQuestionnaireCategoryList(JSONParser.parseQuestionnaireCategoryListJSON(jDataObj.getJSONArray(DBTable.QUESTIONNAIRE_CATEGORY)));
                    }
                }


                // ////////////////////////////////

                // /////// Beneficiary List///////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC) || responseData.getResponseName().equals(RequestName.BENEFICIARIES)) {
                    if (jDataObj.has(DBTable.BENEFICIARY)) {
                        myData.setBeneficiaryList(JSONParser.parseBeneficiaryListJSON(context, jDataObj.getJSONArray(DBTable.BENEFICIARY)));
                    }
                }

                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC) || responseData.getResponseName().equals(RequestName.HOUSEHOLDS)) {
                    if (jDataObj.has(DBTable.HOUSEHOLD)) {
                        myData.setHouseholdList(JSONParser.parseHouseholdList(jDataObj.getJSONArray(DBTable.HOUSEHOLD)));
                    }
                }
                if (jDataObj.has(DBTable.HOUSEHOLD)) {
                    try {
                        myData.setHouseholdList(JSONParser.parseHouseholdList(jDataObj.getJSONArray(DBTable.HOUSEHOLD)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (jDataObj.has(DBTable.BENEFICIARY)) {
                    try {
                        myData.setBeneficiaryList(JSONParser.parseBeneficiaryListJSON(context,jDataObj.getJSONArray(DBTable.BENEFICIARY)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


                // /////////////////////////////////////

                // ///////// Beneficiary List///////////////
                // if(response.getResponseName().equals(RequestName.MY_DATA)){
                // myData.setMaternalCareInfos(JSONParser.parseMaternalCareList(jDataObj.getJSONArray("maternalCareList")));
                // myData.setDiagnosisInfos(JSONParser.parseDiagnosisInfoList(jDataObj.getJSONArray("diagnosisInfo")));
                //
                // }
                // ///////////////////////////////////////

                // /// Medicine List//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC) || responseData.getResponseName().equals(RequestName.MEDICINES)) {
                    if (jDataObj.has(DBTable.MEDICINE)) {
                        myData.setMedicineList(JSONParser.parseMedicineListJSON(jDataObj.getJSONArray(DBTable.MEDICINE)));
                    }

                    if (jDataObj.has(KEY.MEDICINES_STOCK)) {
                        myData.setMedicineStock(jDataObj.getJSONArray(KEY.MEDICINES_STOCK));
                    }
                }




                // /// consumption list//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC)) {
                    if (jDataObj.has(DBTable.MEDICINE_CONSUMPTION)) {
                        // ArrayList<MedicineConsumptionInfoModel> list = JSONParser.parseMedicineConsumptionListJSON(jDataObj.getJSONArray(DBTable.MEDICINE_CONSUMPTION));
                        myData.setMedicineConsumptionList(JSONParser.parseMedicineConsumptionListJSON(jDataObj.getJSONArray(DBTable.MEDICINE_CONSUMPTION)));
                        Log.d("consumtion", "getMyData: consumption data "+jDataObj.getJSONArray(DBTable.MEDICINE_CONSUMPTION));
                        // App.getContext().getDB().saveConsumptionList(myData.getMedicineConsumptionList(), App.getContext().getUserInfo().getUserId());
                    }

                }


                // /// session list//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC)) {
                    if (jDataObj.has(KEY.SESSION_LIST)) {
                        myData.setSatelliteSessionModelArrayList(JSONParser.parseSessionListJson(jDataObj.getJSONArray(KEY.SESSION_LIST)));
                        // App.getContext().getDB().saveSessionList(myData.getSatelliteSessionModelArrayList(),myData.getVersionNos());
                    }

                }


//
//
//                // /// Adjust list//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC)) {
                    if (jDataObj.has(DBTable.MEDICINE_ADJUSTMENT)) {

                        myData.setMedicineAdjustmentList(JSONParser.parseMedicineAdjustListJSON(jDataObj.getJSONArray(DBTable.MEDICINE_ADJUSTMENT )));
                        //App.getContext().getDB().saveAdjustmentList(myData.getMedicineAdjustmentList(), App.getContext().getUserInfo().getUserId());
                    }
                }
//
//
//                // /// medicine recievie list//////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC)) {
                    if (jDataObj.has(DBTable.MEDICINE_RECEIVE)) {
                        myData.setMedicineReceiveList(JSONParser.parseMedicineReceivetListJSON(jDataObj.getJSONArray(DBTable.MEDICINE_RECEIVE )));
                        //App.getContext().getDB().saveMedicineReceivedList(myData.getMedicineReceiveList(), App.getContext().getUserInfo().getUserId());
                    }
                }


                // ////////////////////////////////

                // ///////// FCM Profile/////////////
//                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC)
//                        || responseData.getResponseName().equals(RequestName.FCM_PROFILE)) {
//                    if (jDataObj.has(KEY.fcmProfile)) {
//                        myData.setUserInfo(JSONParser.parseFCMProfileJSON(jDataObj.getJSONObject(KEY.fcmProfile), context));
//                    }
//                    if (jDataObj.has(KEY.fcmProfileList)) {
//                        myData.setUserInfoList(JSONParser.parseFCMProfileListJSON(jDataObj.getJSONArray(KEY.fcmProfileList)));
//                    }
//                    if (jDataObj.has(KEY.upzilaWiseLocation)) {
//                        myData.setLocationList(JSONParser.parseLocationListJSON(jDataObj.getJSONArray(KEY.upzilaWiseLocation)));
//                    }
//                }

//
//                if (responseData.getResponseName().equals(RequestName.MY_DATA)
//                        || responseData.getResponseName().equals(RequestName.fcmProfileList)) {
//                    if (jDataObj.has(KEY.fcmProfileList)) {
//                        myData.setUserInfoList(JSONParser.parseFCMProfileListJSON(jDataObj.getJSONArray(KEY.fcmProfileList)));
//                    }
//
//                }
//
//                if (responseData.getResponseName().equals(RequestName.MY_DATA)
//                        || responseData.getResponseName().equals(RequestName.fcmProfileList)) {
//                    if (jDataObj.has(KEY.upzilaWiseLocation)) {
//                        myData.setLocationList(JSONParser.parseLocationListJSON(jDataObj.getJSONArray(KEY.upzilaWiseLocation)));
//                    }
//
//                }

                // /////////////////////////

                // ///////// App Version History /////////////
                if (responseData.getResponseName().equals(RequestName.APP_VERSION_HISTORY)) {
                    myData.setAppVersionHistory(JSONParser.parseAppVersionHistoryJSON(jDataObj.getJSONObject(DBTable.APP_VERSION_HISTORY)));
                }

                // /////////////////////////

                // ///////// Schedule List /////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC) || responseData.getResponseName().equals(RequestName.USER_SCHEDULE)
                        || responseData.getResponseName().equals(RequestName.UNDONE_SCHEDULE)) {
                    if (jDataObj.has(DBTable.USER_SCHEDULE)) {
                        myData.setScheduleList(JSONParser.parseScheduleListJSON(jDataObj.getJSONArray(DBTable.USER_SCHEDULE)));
                    }
                }


                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC) || responseData.getResponseName().equals(RequestName.CCS)) {
                    if (jDataObj.has(DBTable.CCS_STATUS))
                        myData.setCcsStatusList(JSONParser.parseCCSStatusListJSON(jDataObj.getJSONArray(DBTable.CCS_STATUS)));

                    if (jDataObj.has(DBTable.CCS_ELIGIBLE))
                        myData.setCcsEligibleJsonArry(jDataObj.getJSONArray(DBTable.CCS_ELIGIBLE));

                    if (jDataObj.has(DBTable.CCS_TEREATMENT))
                        myData.setCcsTreatmentJsonArry(jDataObj.getJSONArray(DBTable.CCS_TEREATMENT));

                }


                if (responseData.getResponseName().equals(RequestName.COURT_YARD) || responseData.getResponseName().equals(RequestName.MY_DATA)) {
                    if (jDataObj.has(DBTable.COURTYARD_MEETING_TOPIC_MONTH)) {
                        myData.setCourtyardMeetingTopicMonthJsonArray(jDataObj.getJSONArray(DBTable.COURTYARD_MEETING_TOPIC_MONTH));
                    }

                    if (jDataObj.has(DBTable.TOPIC_INFO)) {
                        myData.setTopicInfoJsonArray(jDataObj.getJSONArray(DBTable.TOPIC_INFO));
                    }

                    if (jDataObj.has(DBTable.COURTYARD_MEETING_TARGET)) {
                        myData.setCourtyardMeetingTarget(jDataObj.getJSONArray(DBTable.COURTYARD_MEETING_TARGET));
                    }

                    if (jDataObj.has(DBTable.COURTYARD_MEETING)) {
                        myData.setCourtyardMeeting(jDataObj.getJSONArray(DBTable.COURTYARD_MEETING));
                    }

                }

                // /////////////////////////

                // ///////// CCS Status/ CCS reason/////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC)) {

                    if (jDataObj.has(DBTable.TEXT_REF)) {
                        myData.setTextRefList(JSONParser.parseTextRefListJSON(jDataObj.getJSONArray(DBTable.TEXT_REF)));
                    }


                    if (jDataObj.has(DBTable.DIAGNOSIS_INFO)) {
                        myData.setDiagnosisInfos(JSONParser.parseDiagnosisInfoList(jDataObj.getJSONArray(DBTable.DIAGNOSIS_INFO)));
                    }

                    if (jDataObj.has(KEY.fcmConfigurations)) {
                        myData.setFcmComfiguration(jDataObj.getJSONArray(KEY.fcmConfigurations).toString());
                    }

                    if (jDataObj.has(DBTable.EVENT_INFO)) {
                        myData.setEventInfos(JSONParser.parseEventInfoList(jDataObj.getJSONArray(DBTable.EVENT_INFO)));
                    }


                    if (jDataObj.has(DBTable.PATIENT_INTERVIEW_MASTER)) {
                        myData.setPatientInterviewMasters(JSONParser.parsePatientInterviewMasterList(jDataObj.getJSONArray(DBTable.PATIENT_INTERVIEW_MASTER)));
                    }

                    if (jDataObj.has(DBTable.NOTIFICATION_MASTER)) {
                        myData.setNotificationMasters(JSONParser.parseNotificationMasterList(jDataObj.getJSONArray(DBTable.NOTIFICATION_MASTER)));
                    }


                    if (jDataObj.has(DBTable.HOUSEHOLD)) {
                        myData.setHouseholdList(JSONParser.parseHouseholdList(jDataObj.getJSONArray(DBTable.HOUSEHOLD)));
                    }


                    if (jDataObj.has(DBTable.MATERNAL_CARE_INFO)) {
                        myData.setMaternalCareInfos(JSONParser.parseMaternalCareList(jDataObj.getJSONArray(DBTable.MATERNAL_CARE_INFO)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_INFO)) {
                        myData.setMaternalInfos(JSONParser.parseMaternalInfos(jDataObj.getJSONArray(DBTable.MATERNAL_INFO)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_SERVICE)) {
                        myData.setMaternalServices(JSONParser.parseMaternalServices(jDataObj.getJSONArray(DBTable.MATERNAL_SERVICE)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_BABY_INFO)) {
                        myData.setMaternalBabyInfos(JSONParser.parseMaternalBabyInfos(jDataObj.getJSONArray(DBTable.MATERNAL_BABY_INFO)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_DELIVERY)) {
                        myData.setMaternalDeliveries(JSONParser.parseMaternalDeliveries(jDataObj.getJSONArray(DBTable.MATERNAL_DELIVERY)));
                    }

                    if (jDataObj.has(DBTable.MATERNAL_ABORTION)) {
                        myData.setMaternalAbortions(JSONParser.parseMaternalAbortions(jDataObj.getJSONArray(DBTable.MATERNAL_ABORTION)));
                    }

                    if (jDataObj.has(DBTable.DEATH_REGISTRATION)) {
                        myData.setDeathRegistration(jDataObj.getJSONArray(DBTable.DEATH_REGISTRATION));
                    }

                    if (jDataObj.has(DBTable.QUESTIONNAIRE_BROAD_CATEGORY)) {
                        myData.setQuestionnaireBroadCategory(jDataObj.getJSONArray(DBTable.QUESTIONNAIRE_BROAD_CATEGORY));
                    }

                    if (jDataObj.has(DBTable.QUESTIONNAIRE_SERVICE_CATEGORY)) {
                        myData.setQuestionnaireServiceCategory(jDataObj.getJSONArray(DBTable.QUESTIONNAIRE_SERVICE_CATEGORY));
                    }

                    if (jDataObj.has(DBTable.REPORT_ASST)) {
                        myData.setReportAsst(jDataObj.getJSONArray(DBTable.REPORT_ASST));
                    }

                    if (jDataObj.has(DBTable.SUGGESTION_TEXT)) {
                        myData.setSuggestionText(jDataObj.getJSONArray(DBTable.SUGGESTION_TEXT));
                    }

//
                    if (jDataObj.has(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK)) {
                        myData.setPatientInterviewDoctorFeedbacks(JSONParser.parsePatientInterviewDoctorFeedbackList(jDataObj.getJSONArray(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK)));
                    }


                }

                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC) || responseData.getResponseName().equals(RequestName.IMMUNIZATION)) {
                    if (jDataObj.has(DBTable.IMMUNIZATION_INFO))
                        myData.setImmunizationList(JSONParser.parseImmunizationListJSON(jDataObj.getJSONArray(DBTable.IMMUNIZATION_INFO)));
                    if (jDataObj.has(DBTable.IMMUNIZABLE_BENEFICIARY))
                        myData.setImmunizableBeneficiaries(JSONParser.parseImmunizableBeneficiaryList(jDataObj.getJSONArray(DBTable.IMMUNIZABLE_BENEFICIARY)));
                    if (jDataObj.has(DBTable.IMMUNIZATION_FOLLOWUP))
                        myData.setImmunizationFollowups(JSONParser.parseImmunizationFollowUpList(jDataObj.getJSONArray(DBTable.IMMUNIZATION_FOLLOWUP)));
                    if (jDataObj.has(DBTable.IMMUNIZATION_SERVICE))
                        myData.setImmunizationServices(JSONParser.parseImmunizationServiceList(jDataObj.getJSONArray(DBTable.IMMUNIZATION_SERVICE)));
                    if (jDataObj.has(DBTable.IMMUNIZATION_TARGET))
                        myData.setImmunizationTargets(JSONParser.parseImmunizationTargetList(jDataObj.getJSONArray(DBTable.IMMUNIZATION_TARGET)));
                }


                // ///////// growth_measurement_param List /////////////
                if (responseData.getResponseName().equals(RequestName.MY_DATA_SYNC)) {
                    if (jDataObj.has(DBTable.GROWTH_MEASUREMENT_PARAM))
                        myData.setGrowthMeasurementParams(jDataObj.getJSONArray(DBTable.GROWTH_MEASUREMENT_PARAM));
                }

                // /////////////////////////////////////
                myData.setVersionNos(responseData.getParamJson());

            }
        } catch (JSONException exception) {
            exception.printStackTrace();
            throw new MhealthException(ErrorCode.JSON_EXCEPTION,
                    "JSON EXCEPTION", exception);
        }
        return myData;
    }


    public static ArrayList<PatientInterviewDetail> getPatientInterviewDetail(long interviewId, long questionaryId, HashMap<String, QuestionAnswer> questionAnswerList) {
        ArrayList<PatientInterviewDetail> patientInterviewDetails = new ArrayList<PatientInterviewDetail>();
        boolean adviceFlag = false;
        String adviceAnswer = "";
        ArrayList<String> questionIds = new ArrayList<String>();
        for (String key : questionAnswerList.keySet()) {
            if (questionAnswerList.get(key).isSavable()) {
                questionIds.add(key.replace("q", ""));
            }
        }

        ArrayList<QuestionnaireDetail> questionDtls = App.getContext().getDB().getQuestionnaireDetail(questionaryId, questionIds);
        if (questionDtls != null) {
            for (QuestionnaireDetail qDtl : questionDtls) {
                QuestionAnswer questionAnswer = questionAnswerList.get("q" + qDtl.getQuestionId());
                if (questionAnswer != null && questionAnswer.isSavable() && questionAnswer.getAnswerList() != null && !questionAnswer.getAnswerList().isEmpty()) {

                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                        if (j > 0) sb.append("|");
                        sb.append(questionAnswer.getAnswerList().get(j));
                    }
                    if (questionAnswer.getQuestionName().split("___")[0].equals(Constants.SYS_DIAG_DECISION)) {
                        String st = App.getContext().getDB().getDignosisIds(sb.toString());
                        sb = new StringBuilder();
                        sb.append(st);
                    }

                    PatientInterviewDetail interviewDetail = new PatientInterviewDetail();
                    interviewDetail.setInterviewId(interviewId);
                    interviewDetail.setqId(qDtl.getQId());
                    interviewDetail.setAnswer(sb.toString());
                    patientInterviewDetails.add(interviewDetail);

                    if (questionAnswer.getQuestionName().equals(Constants.ADVICE_FLAG)) {
                        adviceFlag = true;
                        adviceAnswer = sb.toString();
                    }
                }
            }

            if (adviceFlag) {
                App.getContext().getDB().updatePatientInterviewMaster(Constants.ADVICE_FLAG, Utility.parseLong(adviceAnswer) + "", interviewId);
            } else {
                App.getContext().getDB().updatePatientInterviewMaster(Constants.ADVICE_FLAG, "0", interviewId);
            }
        }

        return patientInterviewDetails;
    }


    public static BeneficiaryMigration getBeneficiaryMigration(HashMap<String, QuestionAnswer> questionAnswers) {

        BeneficiaryMigration beneficiaryMigration = new BeneficiaryMigration();
        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }

            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();
            if (qName != null && !qName.trim().equalsIgnoreCase("")) {
                if (qName.trim().equalsIgnoreCase("SYS_EXISTING_BENEF_CODE")) {
                    beneficiaryMigration.setPrevBenefCode(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("SYS_NEW_BENEF_CODE")) {
                    beneficiaryMigration.setNewBenefCode(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("MIGRATION_ACTION")) {
                    beneficiaryMigration.setMigrationAction(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("SYS_MIGRATION_SRC")) {
                    if (!qAnswer.equalsIgnoreCase("OWN")) {
                        return null;
                    }
                }
            }
        }

        return beneficiaryMigration;

    }

    public static CourtyardMeeting getCourtYardMeeting(HashMap<String, QuestionAnswer> questionAnswers, long interviewId) {
        if (questionAnswers.size() == 0) return null;
        CourtyardMeeting meeting = new CourtyardMeeting();
        meeting.setInterviewId(interviewId);
        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }

            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName != null && !qName.trim().equalsIgnoreCase("")) {
                if (qName.trim().equalsIgnoreCase("COURT_YARD_TOPIC_ID")) {
                    meeting.setTopicId(Utility.parseLong(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("SYS_HH_NUMBER")) {
                    meeting.setHhNumber(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("SESSION_DURATION")) {
                    meeting.setMeetingDuration(qAnswer);
                } else if (qName.trim().equalsIgnoreCase("SYS_COURT_YARD_DATE")) {
                    try {
                        meeting.setMeetingDate(Utility.getMillisecondFromDate(qAnswer, Constants.DATE_FORMAT_YYYY_MM_DD));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                } else if (qName.trim().equalsIgnoreCase("TOTAL_NO_OF_PEOPLE")) {
                    meeting.setTotalAttendent(Utility.parseLong(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("TOTAL_NO_OF_MALE_PARTICIPANTS")) {
                    meeting.setTotalMaleAttendent(Utility.parseInt(qAnswer));
                } else if (qName.trim().equalsIgnoreCase("TOTAL_NO_OF_FEMALE_PARTICIPANTS")) {
                    meeting.setTotalFemaleAttendent(Utility.parseInt(qAnswer));
                }
            }
        }
        return meeting;
    }

    public static String getAnswer(HashMap<String, QuestionAnswer> questionAnswers, String questionName) {
        if (questionAnswers.size() == 0) return null;
        for (String key : questionAnswers.keySet()) {
            QuestionAnswer questionAnswer = questionAnswers.get(key);
            if (questionAnswer == null || questionAnswer.getAnswerList() == null) continue;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < questionAnswer.getAnswerList().size(); j++) {
                if (j > 0)
                    sb.append("|");
                sb.append(questionAnswer.getAnswerList().get(j));
            }

            String qAnswer = sb.toString();
            String qName = questionAnswer.getQuestionName();

            if (qName != null && !qName.trim().equalsIgnoreCase("")) {
                if (qName.trim().equalsIgnoreCase(questionName)) {
                    return qAnswer;
                }
            }
        }
        return null;
    }


    public static int getNumberDayInMonth(int monthIndex) {
        return getMonthList().get(monthIndex).getNumberOfDay();
    }


    public static ArrayList<EnglishMonthInfo> getMonthList() {
        ArrayList<EnglishMonthInfo> monthList = new ArrayList<EnglishMonthInfo>();
        EnglishMonthInfo monthInfo = new EnglishMonthInfo();
        monthInfo.setName("January");
        monthInfo.setNumberOfDay(31);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("Fabruary");
        monthInfo.setNumberOfDay(28);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("March");
        monthInfo.setNumberOfDay(31);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("April");
        monthInfo.setNumberOfDay(30);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("May");
        monthInfo.setNumberOfDay(31);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("June");
        monthInfo.setNumberOfDay(30);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("July");
        monthInfo.setNumberOfDay(31);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("August");
        monthInfo.setNumberOfDay(31);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("September");
        monthInfo.setNumberOfDay(30);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("October");
        monthInfo.setNumberOfDay(31);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("November");
        monthInfo.setNumberOfDay(30);
        monthList.add(monthInfo);

        monthInfo = new EnglishMonthInfo();
        monthInfo.setName("December");
        monthInfo.setNumberOfDay(31);
        monthList.add(monthInfo);
        return monthList;
    }

}
