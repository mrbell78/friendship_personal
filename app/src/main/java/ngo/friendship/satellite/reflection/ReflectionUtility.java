package ngo.friendship.satellite.reflection;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.BuildConfig;
import ngo.friendship.satellite.constants.BeneficiaryType;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.QUESTION_TYPE;
import ngo.friendship.satellite.jsonoperation.JSONCreateor;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.ImmunaizationBeneficiary;
import ngo.friendship.satellite.model.QuestionAnswer;
import ngo.friendship.satellite.model.ReferralCenterInfo;
import ngo.friendship.satellite.model.ScheduleInfo;
import ngo.friendship.satellite.model.TextRef;
import ngo.friendship.satellite.utility.GPSUtility;
import ngo.friendship.satellite.utility.Utility;


public class ReflectionUtility {

    private String ERROR = "ERROR";

    public String countOptions(String[] params,
                               HashMap<String, QuestionAnswer> questionAnswerMap) {
        int count = 0;
        for (String param : params) {
            if (param.split(":").length == 2) {
                String question = param.split(":")[0];
                String answer = param.split(":")[1];
                QuestionAnswer questionAnswer = questionAnswerMap.get(question);
                if (questionAnswer != null
                        && questionAnswer.getAnswerList() != null) {
                    for (String optionValue : questionAnswer.getAnswerList()) {
                        if (optionValue.equals(answer)) {
                            count++;
                            break;
                        }
                    }

                }

            }

        }
        return count + "";
    }


    // ReflectionUtility.countSelectedItems(q1,q2,q3) return integer
    public String countSelectedItems(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        int count = 0;
        for (String question : params) {

            QuestionAnswer questionAnswer = questionAnswerMap.get(question.trim());
            if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
                for (String optionValue : questionAnswer.getAnswerList()) {
                    count++;
                }
            }
        }
        return count + "";
    }


    public String ccsFcmNextFollowupDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, App.getContext().getAppSettings()
                .getCcsAutometicFCMFollowupInterval());
        return Utility.getDateFromMillisecond(cal.getTimeInMillis(),
                Constants.DATE_FORMAT_YYYY_MM_DD);
    }

    public String nextImmunizationDate(Beneficiary beneficiary, String type) {

        long nextEPIDateInMillis = new Date().getTime();
        if (type != null && type.equals(BeneficiaryType.EPI)) {
            nextEPIDateInMillis = ((ImmunaizationBeneficiary) beneficiary)
                    .getNextImmunizationDateInMillis();
        } else if (type != null && type.equals(BeneficiaryType.TT)) {
            nextEPIDateInMillis = ((ImmunaizationBeneficiary) beneficiary)
                    .getNextImmunizationDateInMillis();
        }
        return Utility.getDateFromMillisecond(nextEPIDateInMillis,
                Constants.DATE_FORMAT_YYYY_MM_DD);
    }


    class KeyValue {
        public String key = null;
        public String value = null;
    }


    public String concatAAA(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        String answer = "";
        for (String pram : params) {
            String[] parts = pram.trim().split(":");
            for (String part : parts) {
                if (part.contains("q")) {

                    QuestionAnswer questionAnswer = questionAnswerMap.get(part
                            .trim());
                    if (questionAnswer != null
                            && questionAnswer.getAnswerList() != null) {
                        ArrayList<String> ansList = questionAnswer
                                .getAnswerList();
                        String tempAns = "";
                        for (String ans : ansList) {
                            tempAns = tempAns + "," + ans;
                        }
                        if (tempAns.indexOf(",") == 0) {
                            tempAns = tempAns.substring(1);
                        }
                        part = tempAns;
                    } else {
                        part = "";
                    }
                }
                answer = answer + part.trim();
            }
        }
        return answer;
    }

    private boolean isQuestion(String part) {
        return part.startsWith("q") && Utility.isNumaric(part.replace("q", ""));
    }

    private String getAnswer(HashMap<String, QuestionAnswer> questionAnswerMap, String question) {
        QuestionAnswer questionAnswer = questionAnswerMap.get(question.trim());
        String ans = "";
        if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
            ans = TextUtils.join(",", questionAnswer.getAnswerList());
        } else {
            ans = "";
        }
        return ans;
    }

    //	 ReflectionUtility.concat(q id: separator, q id: separator,…)
    //	 Description: concat answers of the given questions by given separator
    //	 Expression Example: ReflectionUtility.concat(q10:/,q12:-,q13)
    //	 Return: 10/Male-No (if answer of q10 is “10” , q12 is “Male” and q13 is “No”)
    //   Return: 10/No (if answer of q10 is “10” , q12 is null or empty and q13 is “No”)
    //	 Return: Male-No (if answer of q10 is null or empty , q12 is  “Male” and q13 is “No”)

    public String concat(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {


        String concatedString = "";
        String baseSeperator;
        String rightPart;
        String[] leftParam;

        // replace question by answer
        for (int index = 0; index < params.length; index++) {
            String qKey = params[index].split(":")[0];
            if (isQuestion(qKey)) {
                params[index] = params[index].replace(qKey, getAnswer(questionAnswerMap, qKey));
            }
        }

        String leftPart = params[0];
        for (int i = 1; i < params.length; i++) {
            leftParam = leftPart.split(":");
            //If left part contains no value, make full left part blank
            if (leftParam.length == 0 || leftParam[0].equals("") || leftParam[0] == null) {
                leftPart = "";
            }
            //Reinitializing leftParam qualifying whether it carries any value
            leftParam = leftPart.split(":");


            rightPart = params[i];

            //IF right part contains no value,leave here
            if (rightPart.split(":").length < 1 || rightPart.split(":")[0].equals("")) {
                //If this is last parameter, compile result
                if (i == params.length - 1) {
                    leftPart = leftParam[0];
                }
                continue;
            }
            //If this is last parameter, remove its seperator
            if (i == params.length - 1) {
                rightPart = rightPart.split(":").length > 0 ? rightPart.split(":")[0] : "";
            }
            //Seperator is taken from left side
            baseSeperator = leftParam.length == 2 ? leftParam[1] : "";


            leftPart = leftParam[0] + baseSeperator + rightPart;//"01:|,02:/,03:-,04"
        }
        concatedString = leftPart;
        return concatedString;
    }

    //	measureBP(question number:higher range/lower range=output:higher range/lower range=output.....)
    //	example: measureBP(q8:90-119/60-79=output:140-900/90-125=Hypotension:....)
    public String measureBP(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length != 1) return "";
        String[] datas = params[0].trim().split(":");
        if (datas.length < 2) return "";
        QuestionAnswer questionAnswer = questionAnswerMap.get(datas[0].trim());
        if (questionAnswer == null || questionAnswer.getAnswerList() == null || questionAnswer.getAnswerList().size() == 0 || questionAnswer.getAnswerList().size() != 1)
            return "";
        ///
        for (int i = 1; i < datas.length; i++) {
            String rng = datas[i].trim().split("=")[0].trim();
            String output = datas[i].trim().split("=")[1].trim();
            String[] ranges = rng.split("/");
            if (ranges.length != 2)
                return "";
            String[] rangeHighBP = ranges[0].trim().split("-");
            String[] rangeLowBP = ranges[1].trim().split("-");
            if (rangeHighBP.length != 2 || rangeLowBP.length != 2) return "";

            int highMinRange = Integer.parseInt(rangeHighBP[0]);
            int highMaxRange = Integer.parseInt(rangeHighBP[1]);

            int lowMinRange = Integer.parseInt(rangeLowBP[0]);
            int lowMaxRange = Integer.parseInt(rangeLowBP[1]);

            String[] qBP = questionAnswer.getAnswerList().get(0).trim().split("/");
            if (qBP.length != 2) return "";

            int maxBP = Integer.parseInt(qBP[0]);
            int minBP = Integer.parseInt(qBP[1]);
            if ((maxBP >= highMinRange && maxBP <= highMaxRange) || (minBP >= lowMinRange && minBP <= lowMaxRange)) {
                return output;
            }
        }
        return "";
    }


    //	compareRangeEx(question number: low range (-/to) high range=output:low range (-/to ) high range=output:.....)
    //	example: ReflectionUtility.compareRangeEx(q8:60-79=output:-140to-900.8=Hypotension:....)
    public String compareRangeEx(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        try {
            if (params.length != 1) return "ERROR";
            String[] datas = params[0].trim().split(":");
            if (datas.length < 2) return "ERROR";
            QuestionAnswer questionAnswer = questionAnswerMap.get(datas[0].trim());
            if (questionAnswer == null || questionAnswer.getAnswerList() == null || questionAnswer.getAnswerList().size() == 0 || questionAnswer.getAnswerList().size() != 1)
                return "ERROR";
            ///
            for (int i = 1; i < datas.length; i++) {
                String range = datas[i].trim().split("=")[0].trim();
                String output = datas[i].trim().split("=")[1].trim();

                double lRange;
                double hRange;

                if (range.trim().split("to").length == 2) {
                    lRange = Utility.parseDouble(range.trim().split("to")[0]);
                    hRange = Utility.parseDouble(range.trim().split("to")[1]);
                } else if (range.trim().split("-").length == 2) {

                    lRange = Utility.parseDouble(range.trim().split("-")[0]);
                    hRange = Utility.parseDouble(range.trim().split("-")[1]);
                } else {
                    return "ERROR";
                }

                double value = Utility.parseDouble(questionAnswer.getAnswerList().get(0).trim());

                if ((value <= hRange && value >= lRange)) {
                    return output;
                }
            }
            return "NOT IN RANGE";
        } catch (Exception exception) {
            return "ERROR";
        }

    }

    public String averageBP(String[] params,
                            HashMap<String, QuestionAnswer> questionAnswerMap) {
        long lbp = 0, hbp = 0;
        long count = 0;
        for (String question : params) {
            QuestionAnswer questionAnswer = questionAnswerMap.get(question);
            if (questionAnswer != null
                    && questionAnswer.getAnswerList() != null
                    && questionAnswer.getAnswerList().size() > 0) {
                String[] bp = questionAnswer.getAnswerList().get(0).trim()
                        .split("/");
                lbp = lbp + Utility.parseLong(bp[0]);
                hbp = hbp + Utility.parseLong(bp[1]);
                count++;
            }
        }
        return Math.round(lbp / count) + "/" + Math.round(hbp / count);
    }

    public String sum(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        double ans = 0;
        for (String question : params) {

            if (Utility.isNumaric(question)) {
                ans = ans + Utility.parseDouble(question.trim());
            } else {
                QuestionAnswer questionAnswer = questionAnswerMap.get(question.trim());
                if (questionAnswer != null
                        && questionAnswer.getAnswerList() != null
                        && questionAnswer.getAnswerList().size() > 0) {
                    ans = ans + Utility.parseDouble(questionAnswer.getAnswerList().get(0));
                }
            }
        }

        return ans + "";
    }

    //ReflectionUtility.subtract(q5,q6) //ReflexionUtility.subtraction(q5,15) prams length 2
    public String subtract(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length != 2) return ERROR;
        double ans = 0;
        try {
            ans = ans + Double.parseDouble(params[0].trim());
        } catch (NumberFormatException nfe) {
            QuestionAnswer questionAnswer = questionAnswerMap.get(params[0].trim());
            if (questionAnswer != null
                    && questionAnswer.getAnswerList() != null
                    && questionAnswer.getAnswerList().size() > 0) {
                ans = ans
                        + Utility.parseLong(questionAnswer.getAnswerList().get(0));
            } else {
                return ERROR;
            }
        }


        try {
            ans = ans - Double.parseDouble(params[1].trim());
        } catch (NumberFormatException nfe) {
            QuestionAnswer questionAnswer = questionAnswerMap.get(params[1].trim());
            if (questionAnswer != null
                    && questionAnswer.getAnswerList() != null
                    && questionAnswer.getAnswerList().size() > 0) {
                ans = ans - Utility.parseLong(questionAnswer.getAnswerList().get(0));
            } else {
                return ERROR;
            }
        }

        if (((long) ans) == ans) {
            return ((long) ans) + "";
        } else {
            return ans + "";
        }
    }

    // ReflectionUtility.getValues(q10);
    public ArrayList<String> getValues(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> ansList = new ArrayList<String>();
        if (params.length > 0) {
            for (String question : params) {
                QuestionAnswer answer = questionAnswerMap.get(question);
                if (answer != null && answer.getAnswerList() != null && answer.getAnswerList().size() > 0) {
                    ansList.addAll(answer.getAnswerList());
                }
            }

        }
        return ansList;
    }

    // ReflectionUtility.compareRange(q13:1.8-2.5:Less.Normal.High)
    //Or ReflectionUtility.compareRange(q13:1.8to-2.5:Less.Normal.High) for negative
    public String compareRange(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        String ans = "";
        try {
            if (params != null && params.length == 1 && params[0].trim().split(":").length == 3) {
                String qKey = params[0].trim().split(":")[0].trim();
                String range = params[0].trim().split(":")[1].trim();
                String[] rangeAns = params[0].trim().split(":")[2].trim().split("\\.");

                // //////////// LOG//////////////
                Log.e("REEXMANAGERgetP.s.qkey:", qKey);
                Log.e("RE EX MANAGER range: ", range);

                // //////////////////////
                double lRange;
                double hRange;

                if (range.trim().split("to").length == 2) {
                    lRange = Utility.parseDouble(range.trim().split("to")[0]);
                    hRange = Utility.parseDouble(range.trim().split("to")[1]);
                } else if (range.trim().split("-").length == 2) {

                    lRange = Utility.parseDouble(range.trim().split("-")[0]);
                    hRange = Utility.parseDouble(range.trim().split("-")[1]);
                    Log.e("REEXMANAGERgetPulse:", "lRange " + lRange + " hRange " + hRange);
                } else {
                    return ans;
                }

                QuestionAnswer questionAnswer = questionAnswerMap.get(qKey);
                if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                    qKey = questionAnswer.getAnswerList().get(0);
                }

                double qAns = Utility.parseDouble(qKey);
                Log.e("REEXMANAGERgetPulseS.:", "question " + qKey
                        + " ans " + qAns);
                if (qAns < lRange) {
                    ans = rangeAns[0];
                } else if (qAns > hRange) {
                    ans = rangeAns[2];
                } else {
                    ans = rangeAns[1];
                }

            }
        } catch (Exception e) {

        }
        return ans;
    }

    //ReflectionUtility.getComplementSet(q10:q12)
    //ReflectionUtility.getComplementSet(A##B:A##B##D)
    public ArrayList<String> getComplementSet(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        Set optionSet = new LinkedHashSet();
        if (params != null && params.length == 1 && params[0].trim().split(":").length == 2) {
            ArrayList<String> left = new ArrayList<String>();
            ArrayList<String> right = new ArrayList<String>();
            String qKey1 = params[0].trim().split(":")[0].trim();
            String qKey2 = params[0].trim().split(":")[1].trim();

            QuestionAnswer qa1 = questionAnswerMap.get(qKey1);
            QuestionAnswer qa2 = questionAnswerMap.get(qKey2);

            if (qa1 != null) {
                if (qa1.getAnswerList() != null) {
                    left = qa1.getAnswerList();
                }
            } else {
                left = Utility.split(qKey1, "##");
            }

            if (qa2 != null) {
                if (qa2.getAnswerList() != null) {
                    right = qa2.getAnswerList();
                }
            } else {
                right = Utility.split(qKey2, "##");
            }

            optionSet.addAll(left);
            optionSet.removeAll(right);
        }
        return new ArrayList<String>(optionSet);
    }

    public String compare(String[] params,
                          HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params != null && params.length == 1
                && params[0].trim().split(":").length == 2) {
            String qKey = params[0].trim().split(":")[0].trim();
            double value = Utility.parseDouble(params[0].trim().split(":")[1]
                    .trim());
            QuestionAnswer questionAnswer = questionAnswerMap.get(qKey);
            if (questionAnswer != null
                    && questionAnswer.getAnswerList() != null
                    && questionAnswer.getAnswerList().size() == 1) {
                double ans = Utility.parseDouble(questionAnswer.getAnswerList()
                        .get(0));
                if (ans > value) {
                    return "HIGH";
                } else if (ans == value) {
                    return "EQUAL";
                } else {
                    return "LOW";
                }
            }
        }

        return "";

    }

    public ArrayList<String> getReferenceValueList(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        Set optionSet = new LinkedHashSet();
        for (String param : params) {
            if (param.trim().split(":").length == 3) {
                String[] data = param.trim().split(":");
                String qKey = data[0].trim();
                String value = data[1].trim();
                String[] options = data[2].trim().split("#");
                QuestionAnswer questionAnswer = questionAnswerMap.get(qKey);
                if (questionAnswer != null
                        && questionAnswer.getAnswerList() != null) {
                    for (String ans : questionAnswer.getAnswerList()) {
                        if (ans.trim().equals(value)) {
                            for (String option : options) {
                                optionSet.add(option.trim());
                            }
                        }
                    }

                }

            }

        }
        return new ArrayList<String>(optionSet);
    }

    public String getPulseStatus(String[] params,
                                 HashMap<String, QuestionAnswer> questionAnswerMap) {
        return compareRange(params, questionAnswerMap);

    }

    public String calculateEDD(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params != null) {
            String question = params[0];
            QuestionAnswer questionAnswer = questionAnswerMap.get(question);
            if (questionAnswer != null
                    && questionAnswer.getType().equals("date")
                    && questionAnswer.getAnswerList() != null) {
                Date date;
                try {
                    date = Constants.DATE_FORMAT_YYYY_MM_DD.parse(questionAnswer
                            .getAnswerList().get(0).trim());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int x = cal.get(Calendar.MONTH);
                    int y = cal.get(Calendar.DAY_OF_MONTH);

                    if (Utility.isLeapYear(cal.get(Calendar.YEAR))
                            && cal.get(Calendar.MONTH) < 2) {
                        cal.add(Calendar.DAY_OF_YEAR, 281);
                    } else if (Utility.isLeapYear(cal.get(Calendar.YEAR) + 1)
                            && cal.get(Calendar.MONTH) == 4
                            && cal.get(Calendar.DAY_OF_MONTH) > 24) {
                        cal.add(Calendar.DAY_OF_YEAR, 281);
                    } else if (Utility.isLeapYear(cal.get(Calendar.YEAR) + 1)
                            && cal.get(Calendar.MONTH) > 4) {
                        cal.add(Calendar.DAY_OF_YEAR, 281);
                    } else {
                        cal.add(Calendar.DAY_OF_YEAR, 280);
                    }

                    return Constants.DATE_FORMAT_YYYY_MM_DD.format(cal.getTime());
                } catch (ParseException e) {

                }

            }

        }
        return "";
    }

    // ReflectionUtility.getSchedules( TYPE:ANC,CODE:q6,REF_DATE:q12) //TYPE=ANC/PNC/EPI/TT
    public String getSchedules(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {

        String type = null;
        String code = null;
        String refDate = null;
        String dateInputRule = "0";

        for (String part : params) {
            part = part.trim();
            if (part.split(":")[0].equals("TYPE")) {
                type = part.split(":")[1].trim();
            } else if (part.split(":")[0].equals("CODE")) {
                code = part.split(":")[1].trim();
                QuestionAnswer qAns = questionAnswerMap.get(code);
                if (qAns != null && qAns.getAnswerList().size() == 1) {
                    code = qAns.getAnswerList().get(0);
                } else {
                    code = null;
                }
            } else if (part.split(":")[0].equals("REF_DATE")) {
                refDate = part.split(":")[1].trim();
            } else if (part.split(":")[0].equals("DATE_INPUT_RULE")) {
                dateInputRule = part.split(":")[1].trim();
            }
        }

        ArrayList<ScheduleInfo> scheduleInfos = null;

        if (type != null) {
            if (type.equals("ANC") || type.equals("PNC")) {
                if (refDate != null) {
                    QuestionAnswer qAns = questionAnswerMap.get(refDate);
                    if (qAns != null && qAns.getAnswerList().size() == 1) {
                        String date = qAns.getAnswerList().get(0);
                        scheduleInfos = App.getContext().getDB().getScheduleInfoListForMaternal(date, type);
                    }
                }
            } else if (type.equals("EPI") && code != null && code.length() > 0) {

                scheduleInfos = App.getContext().getDB().getScheduleInfoListForImmunization(code, "EPI", App.getContext().getAppSettings().getLanguage());
            } else if (type.equals("TT") && code != null && code.length() > 0) {
                scheduleInfos = App.getContext().getDB().getScheduleInfoListForImmunization(code, "TT", App.getContext().getAppSettings().getLanguage());
            }

            if (scheduleInfos != null) {
                try {
                    return JSONCreateor.createScheduleInfosJson(scheduleInfos, dateInputRule);
                } catch (JSONException e) {
                    return "";
                }

            } else {
                return "";
            }

        } else {
            return "";
        }

    }

    //ReflectionUtility.measureChildGrowth(WEIGHT:10/q5,HEIGHT:5/q6,AGE_IN_MONTH:15/q10,GENDER:Male/q12,OUTPUT:Less.Normal.High)
    public String measureChildGrowth(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        String MESURE_VALUE = null;
        String AGE_IN_MONTH = null;
        String GENDER = null;
        String OUTPUT = null;
        String masureType = "";
        double ageInMonth = -1;
        String ans = "";
        try {
            for (String part : params) {
                part = part.trim();
                if (part.split(":")[0].equals("WEIGHT")) {
                    MESURE_VALUE = part.split(":")[1].trim();
                    masureType = "Weight";
                } else if (part.split(":")[0].equals("HEIGHT")) {
                    MESURE_VALUE = part.split(":")[1].trim();
                    masureType = "Height";
                }
                if (part.split(":")[0].equals("AGE_IN_MONTH")) {
                    AGE_IN_MONTH = part.split(":")[1].trim();
                    if (AGE_IN_MONTH.startsWith("q")) {
                        QuestionAnswer questionAnswer = questionAnswerMap.get(AGE_IN_MONTH.trim());
                        if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                            AGE_IN_MONTH = questionAnswer.getAnswerList().get(0);
                        }
                    }
                    ageInMonth = Utility.parseLong(AGE_IN_MONTH);
                }

                if (part.split(":")[0].equals("GENDER")) {
                    GENDER = part.split(":")[1].trim();
                    if (GENDER.startsWith("q")) {
                        QuestionAnswer questionAnswer = questionAnswerMap.get(GENDER.trim());
                        if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                            GENDER = questionAnswer.getAnswerList().get(0);
                        }
                    }
                }

                if (part.split(":")[0].equals("OUTPUT"))
                    OUTPUT = part.split(":")[1].trim();

            }

            String rang = App.getContext().getDB().getRangeChildGrowth(masureType, ageInMonth, GENDER);
            String[] valueRangeOutput = {MESURE_VALUE.trim() + ":" + rang + ":" + OUTPUT};
            ans = compareRange(valueRangeOutput, questionAnswerMap);
        } catch (Exception e) {

        }
        return ans;
    }


    //
    // ReflectionUtility.calculateBMI(height,weight) // height in centimeter and weight in kg
    // ReflectionUtility.calculateBMI(50.8,20)
    // ReflectionUtility.calculateBMI(q5,q10)
    // ReflectionUtility.calculateBMI(40,q10)

    public String calculateBMI(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length != 2) return ERROR;
        double height = 0;
        try {
            height = Double.parseDouble(params[0].trim());
        } catch (NumberFormatException nfe) {
            QuestionAnswer questionAnswer = questionAnswerMap.get(params[0].trim());
            if (questionAnswer != null
                    && questionAnswer.getAnswerList() != null
                    && questionAnswer.getAnswerList().size() > 0) {
                height = Utility.parseDouble(questionAnswer.getAnswerList().get(0));
            } else {
                return ERROR;
            }
        }


        double weight = 0;

        try {
            weight = Double.parseDouble(params[1].trim());
        } catch (NumberFormatException nfe) {
            QuestionAnswer questionAnswer = questionAnswerMap.get(params[1].trim());
            if (questionAnswer != null
                    && questionAnswer.getAnswerList() != null
                    && questionAnswer.getAnswerList().size() > 0) {
                weight = Utility.parseDouble(questionAnswer.getAnswerList().get(0));
            } else {
                return ERROR;
            }
        }

        if (height > 0 && weight > 0) {
            return new DecimalFormat("#.##").format(weight / ((height * 0.01) * (height * 0.01)));
        } else {
            return "ERROR";
        }
    }

    //ReflectionUtility.getNextBenefCode(q7,1);
    public String getNextBenefCode(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length == 2) {
            String houseHoldnumber = "";
            int incrementBy = 0;
            QuestionAnswer questionAnswer = questionAnswerMap.get(params[0].trim());
            if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
                houseHoldnumber = questionAnswer.getAnswerList().get(0);
            }

            questionAnswer = null;
            questionAnswer = questionAnswerMap.get(params[1].trim());
            if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
                incrementBy = (int) Double.parseDouble(questionAnswer.getAnswerList().get(0));
            } else {
                try {
                    incrementBy = (int) Utility.parseDouble(params[1]);
                } catch (Exception exception) {
                }
            }
            if (houseHoldnumber.length() > 0 && incrementBy > 0) {
                return App.getContext().getDB().generateBeneficiaryCodeFromHHNumber(houseHoldnumber, incrementBy);
            }
            return "";
        }
        return "";
    }

    // ReflectionUtility.getAgeInHour(q7); //question must Contain benefCode
    public String getAgeInHour(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> ansList = new ArrayList<String>();
        if (params.length == 1) {

            QuestionAnswer answer = questionAnswerMap.get(params[0].trim());
            if (answer != null && answer.getAnswerList() != null) {

                String benefCode = answer.getAnswerList().get(0);
                long mili = App.getContext().getDB().getChildDob(benefCode);

                if (mili > 0) {

                    long currentMili = Calendar.getInstance().getTimeInMillis();
                    return "" + ((currentMili - mili) / 1000) / 60 / 60;
                }
            }
        }
        return "";
    }


    // ReflectionUtility.contains(q7:"TEXT"); // return YES if answer of q7 contains "TEXT" else return NO , if any exception occurred return "ERROR"
    public String contains(String[] params,
                           HashMap<String, QuestionAnswer> questionAnswerMap) {
        int count = 0;
        if (params.length == 1 && params[0].split(":").length == 2) {
            String left = params[0].split(":")[0];
            String right = params[0].split(":")[1];
            QuestionAnswer questionAnswerLeft = questionAnswerMap.get(left);
            QuestionAnswer questionAnswerRight = questionAnswerMap.get(right);
            if (questionAnswerRight != null && questionAnswerRight.getAnswerList() != null) {
                right = questionAnswerRight.getAnswerList().get(0);
            }
            if (questionAnswerLeft != null && questionAnswerLeft.getAnswerList() != null) {
                for (String optionValue : questionAnswerLeft.getAnswerList()) {
                    if (optionValue.contains(right)) {
                        return "YES";
                    }
                }
            }
            return "NO";
        } else {
            return "ERROR";
        }
    }


    //ReflectionUtility validateHouseholdNumber(q10)  if the value of q10 is valid household number then  return 1 else 0 if any error occur  return ERROR
    public String validateHouseholdNumber(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (params.length == 1) {
            try {
                String question = params[0];
                QuestionAnswer questionAnswer = questionAnswerMap.get(question);
                if (App.getContext().getDB().isHouseholdExist(questionAnswer.getAnswerList().get(0))) {
                    return 1 + "";
                }
                return 0 + "";
            } catch (Exception e) {
                return "ERROR";
            }
        } else {
            return "ERROR";
        }

    }


    public String getNumberOfMemberInHH(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (params.length == 1) {
            try {
                String question = params[0];
                QuestionAnswer questionAnswer = questionAnswerMap.get(question);
                int number = App.getContext().getDB().getNumberOfMemberInHH(questionAnswer.getAnswerList().get(0));
                return number + "";
            } catch (Exception e) {
                return "ERROR";
            }
        } else {
            return "ERROR";
        }

    }

    public ArrayList<String> getBeneficiaryListInHouse(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (params.length == 2) {
            try {
                String question = params[0];
                String formatType = params[1].trim();
                QuestionAnswer questionAnswer = questionAnswerMap.get(question);
                ArrayList<Beneficiary> beneficiaries = App.getContext().getDB().getHouseholdMemberList(questionAnswer.getAnswerList().get(0), false, null, null, null, null, -1, -1, -1);

                for (Beneficiary beneficiary : beneficiaries) {
                    String caption = beneficiary.getBenefCode() + " ";
                    if (beneficiary.getGender().equals("F")) {
                        caption = caption + "♀";
                    } else if (beneficiary.getGender().equals("M")) {
                        caption = caption + "♂";
                    } else {
                        caption = caption + "⚥";
                    }
                    caption = caption + " " + beneficiary.getAge() + " " + beneficiary.getBenefName();
                    String value = beneficiary.getBenefCodeFull();
                    arrayList.add(Utility.getFormatedData(caption, value, formatType));
                }
                return arrayList;
            } catch (Exception e) {
                arrayList = new ArrayList<String>();
                arrayList.add("ERROR");
                return arrayList;
            }
        } else {
            arrayList = new ArrayList<String>();
            arrayList.add("ERROR");
            return arrayList;
        }

    }


    public String substring(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (params.length == 1) {
            try {
                String str = params[0].substring(0, params[0].lastIndexOf("."));
                QuestionAnswer questionAnswer = questionAnswerMap.get(str);
                if (questionAnswer != null) {
                    str = questionAnswer.getAnswerList().get(0);
                    String parameter = params[0].substring(params[0].lastIndexOf(".") + 1);
                    Integer start = Integer.parseInt(parameter.split(":")[0]);
                    Integer length = Integer.parseInt(parameter.split(":")[1]);
                    if (str != null && str.length() > 0 && start != null && length != null) {
                        return str.substring(start, length);
                    } else {
                        return "ERROR";
                    }
                } else {
                    return "ERROR";
                }


            } catch (Exception e) {
                return "ERROR";
            }
        } else {

            return "ERROR";
        }

    }


    //	ReflectionUtility.getTextList(TEXT_CATEGORY, FORMATTYPE)
    //	ReflectionUtility.getTextList(q10,JSON)
    //	ReflectionUtility.getTextList(q10,XML)
    //	ReflectionUtility.getTextList(q10,CSV)

    public ArrayList<String> getTextList(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (params.length == 2) {
            try {
                String question = params[0];
                String formatType = params[1].trim();
                QuestionAnswer questionAnswer = questionAnswerMap.get(question);
                String category = "";
                if (questionAnswer != null && questionAnswer.getAnswerList() != null && questionAnswer.getAnswerList().size() > 0) {
                    category = questionAnswer.getAnswerList().get(0);
                } else {
                    category = question;
                }

                ArrayList<TextRef> textRefs = App.getContext().getDB().getTextRef(category);
                for (TextRef textRef : textRefs) {
                    String caption = textRef.getTextCaption();
                    String value = textRef.getTextName() + "";
                    arrayList.add(Utility.getFormatedData(caption, value, formatType));
                }
                return arrayList;
            } catch (Exception e) {
                arrayList = new ArrayList<String>();
                arrayList.add("ERROR");
                return arrayList;
            }
        } else {
            arrayList = new ArrayList<String>();
            arrayList.add("ERROR");
            return arrayList;
        }
    }

    // ReflectionUtility.getTextRefId(CATEGORY:REFTEXT);
    // ReflectionUtility.getTextRefId(EPI:q8);
    // return id list of ref text that contain q8 and category EPI , if any exception occurred return "ERROR"
    public ArrayList<String> getTextRefId(String[] params,
                                          HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> ids = new ArrayList<String>();
        if (params.length == 1 && params[0].split(":").length == 2) {
            String catagory = params[0].split(":")[0];
            String refText = params[0].split(":")[1];

            QuestionAnswer questionAnswer = questionAnswerMap.get(refText);
            if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
                for (String text : questionAnswer.getAnswerList()) {
                    long id = App.getContext().getDB().getTextRefId(text, catagory);
                    if (id > 0) {
                        ids.add(id + "");
                    }
                }
            } else {
                long id = App.getContext().getDB().getTextRefId(refText, catagory);
                if (id > 0) {
                    ids.add(id + "");
                }
            }
        } else {
            ids.add("ERROR");
        }

        return ids;
    }


    // ReflectionUtility.getDiagnosisId(diag_name or question); //ignore case
    // ReflectionUtility.getDiagnosisId(q8);
    // ReflectionUtility.getDiagnosisId(diag_name);
    // return id list of diag_name that contain q8 , if any exception occurred return "ERROR"
    //* here diag_name not case sensitive

    public ArrayList<String> getDiagnosisId(String[] params,
                                            HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> ids = new ArrayList<String>();
        if (params.length == 1) {
            String refText = params[0];

            QuestionAnswer questionAnswer = questionAnswerMap.get(refText);
            if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
                for (String text : questionAnswer.getAnswerList()) {
                    long id = App.getContext().getDB().getDignosisId(text);
                    if (id > 0) {
                        ids.add(id + "");
                    }
                }
            } else {
                long id = App.getContext().getDB().getDignosisId(refText);
                if (id > 0) {
                    ids.add(id + "");
                }
            }
        } else {
            ids.add("ERROR");
        }
        return ids;
    }

    /**
     * ReflectionUtility.replaceAll(CONTENT:REGULAR_EXPRESSION:REPLACMENT);
     * CONTENT: May question id or String
     * Replaces all matches for REGULAR_EXPRESSION within this CONTENT( answer list of the question) with the given
     * REPLACMENT
     * See {@link Pattern} for regular expression syntax.
     * If any error occur return ERROR
     * Example : ReflectionUtility.replaceAll(q10:##:,);
     */

    public ArrayList<String> replaceAll(String[] params,
                                        HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> ans = new ArrayList<String>();
        if (params.length == 1 && params[0].split(":").length == 3) {
            String content = params[0].split(":")[0];
            String replaceTo = params[0].split(":")[1];
            String replaceBy = params[0].split(":")[2];

            QuestionAnswer questionAnswer = questionAnswerMap.get(content);
            if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
                for (String text : questionAnswer.getAnswerList()) {
                    text = text.replaceAll(replaceTo, replaceBy);
                    ans.add(text);
                }
            } else {
                content = content.replaceAll(replaceTo, replaceBy);
                ans.add(content);
            }
        } else {
            ans.add("ERROR");
        }
        return ans;
    }

    //ReflectionUtility.addComponent(Name=STOPWATCH:Title=Stopwatch:Position=BOTTOM:Key=K12:Caption=START-Start.STOP-Stop)
    public ArrayList<String> addComponent(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> ans = new ArrayList<String>();
        if (params.length == 1 && params[0].split(":")[0].trim().startsWith("NAME") && params[0].split(":")[0].trim().length() > "NAME".length()) {
            ans.add(params[0]);
        } else if (params.length == 4
                && params[0].split(":")[1].trim().equals(QUESTION_TYPE.IMAGE)
                && params[1].split(":")[0].trim().equals(Constants.ALGORITHM_IMAGE_ALIGN)
                && params[2].split(":")[0].trim().equals(Constants.ALGORITHM_IMAGE_URL)
                && params[3].split(":")[0].trim().equals(Constants.ALGORITHM_IMAGE_FILENAME)
        ) {
            ans.add(params[0].split(":")[1]);
            ans.add(params[1].split(":")[1]);
            ans.add(params[2].split(":", 2)[1]);
            ans.add(params[3].split(":")[1]);
        } else {
            ans.add("ERROR");
        }
        return ans;
    }


    //ReflectionUtility.getMillisecondFromDate(DATE:FORMAT)
    // ReflectionUtility.getMillisecondFromDate(05-06-2015:dd-MM-yyyy)
    //ReflectionUtility.getMillisecondFromDate(q5:q10)
    public String getMillisecondFromDate(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length == 1) {
            try {
                String date = params[0].split(":")[0];
                String formate = params[0].split(":")[1];
                QuestionAnswer questionAnswerDate = questionAnswerMap.get(date);
                QuestionAnswer questionAnswerFormate = questionAnswerMap.get(formate);

                if (questionAnswerDate != null) {
                    date = questionAnswerDate.getAnswerList().get(0);
                }

                if (questionAnswerFormate != null) {
                    formate = questionAnswerFormate.getAnswerList().get(0);
                }
                return Utility.getMillisecondFromDate(date, new SimpleDateFormat(formate, Locale.ENGLISH)) + "";
            } catch (Exception e) {
                return "ERROR";
            }
        } else {
            return "ERROR";
        }

    }

    // ReflectionUtility.getCourtYardMeetingTopic(DATE:DATE_FORMAT:OUTPUT_FORMAT)
    public ArrayList<String> getCourtYardMeetingTopic(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (params.length == 1) {
            try {
                String dateStr = params[0].split(":")[0];
                String dateFormate = params[0].split(":")[1];
                String formatType = params[0].split(":")[2];
                QuestionAnswer questionAnswerDate = questionAnswerMap.get(dateStr);
                QuestionAnswer questionAnswerFormate = questionAnswerMap.get(dateFormate);

                if (questionAnswerDate != null) {
                    dateStr = questionAnswerDate.getAnswerList().get(0);
                }

                if (questionAnswerFormate != null) {
                    dateFormate = questionAnswerFormate.getAnswerList().get(0);
                }
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(Utility.getMillisecondFromDate(dateStr, new SimpleDateFormat(dateFormate, Locale.ENGLISH)));
                ArrayList<TextRef> textRefs = App.getContext().getDB().getTopicInfo(date.get(Calendar.MONTH));
                if (textRefs.size() > 0) {
                    arrayList = new ArrayList<String>();
                    for (TextRef textRef : textRefs) {
                        String caption = textRef.getTextCaption();
                        String value = textRef.getTextName() + "";
                        arrayList.add(Utility.getFormatedData(caption, value, formatType));
                    }
                } else {
                    arrayList.add(Utility.getFormatedData("No Topics Found", "NO_TOPICS_FOUND", formatType));
                }

                return arrayList;
            } catch (Exception e) {
                arrayList = new ArrayList<String>();
                arrayList.add("ERROR");
                return arrayList;
            }
        } else {
            arrayList = new ArrayList<String>();
            arrayList.add("ERROR");
            return arrayList;
        }

    }


    // ReflectionUtility.addDate(DATE:q,DAYS:number/q,FORMAT:date_format)
    //  ReflectionUtility.addDate(DATE:q15,DAYS:q13,FORMAT:yyyy-MM-dd)
    //  ReflectionUtility.addDate(DATE:q15,DAYS:11,FORMAT:yyyy-MM-dd)
    public String addDate(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {


        String date = null;
        String day = null;
        String formate = null;
        for (String part : params) {
            part = part.trim();
            if (part.split(":")[0].equals("DATE")) {
                date = part.split(":")[1].trim();
                QuestionAnswer qAns = questionAnswerMap.get(date);
                if (qAns != null && qAns.getAnswerList().size() == 1) {
                    date = qAns.getAnswerList().get(0);
                } else {
                    date = null;
                }
            } else if (part.split(":")[0].equals("DAYS")) {
                day = part.split(":")[1].trim();
                QuestionAnswer qAns = questionAnswerMap.get(day);
                if (qAns != null && qAns.getAnswerList().size() == 1) {
                    day = qAns.getAnswerList().get(0);
                }
            } else if (part.split(":")[0].equals("FORMAT")) {
                formate = part.split(":")[1].trim();
            }
        }

        try {
            Date dt = Constants.DATE_FORMAT_YYYY_MM_DD.parse(date);
            Calendar calender = Calendar.getInstance();
            calender.setTime(dt);
            calender.add(Calendar.DAY_OF_YEAR, Integer.parseInt(day));
            return (new SimpleDateFormat(formate)).format(calender.getTime());
        } catch (ParseException ex) {
            return "ERROR";
        }
    }

    // ReflectionUtility.getCCSTreatmentId(ELIGIBLE_ID:q/NUMBER,INDEX:q/NUMBER)
    // ReflectionUtility.getCCSTreatmentId(ELIGIBLE_ID:q5,INDEX:q10)
    // ReflectionUtility.getCCSTreatmentId(ELIGIBLE_ID:554,INDEX:0)
    // ReflectionUtility.getCCSTreatmentId(ELIGIBLE_ID:554,INDEX:q12)

    public String getCCSTreatmentId(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        String eligibleId = null;
        String index = null;

        for (String part : params) {
            part = part.trim();
            if (part.split(":")[0].equals("ELIGIBLE_ID")) {
                eligibleId = part.split(":")[1].trim();
                QuestionAnswer qAns = questionAnswerMap.get(eligibleId);
                if (qAns != null && qAns.getAnswerList().size() == 1) {
                    eligibleId = qAns.getAnswerList().get(0);
                }
            } else if (part.split(":")[0].equals("INDEX")) {
                index = part.split(":")[1].trim();
                QuestionAnswer qAns = questionAnswerMap.get(index);
                if (qAns != null && qAns.getAnswerList().size() == 1) {
                    index = qAns.getAnswerList().get(0);
                }
            }
        }

        try {
            return App.getContext().getDB().getCcsDao().getCcsTrId(Long.parseLong(eligibleId), Long.parseLong(index)) + "";
        } catch (Exception ex) {
            return "ERROR";
        }
    }


    // ReflectionUtility.getNumberOfCCSFollowup(CCS_TR_ID:q/NUMBER,TYPE:q/NUMBER)
    // ReflectionUtility.getNumberOfCCSFollowup(CCS_TR_ID:q5,TYPE:q10)
    // ReflectionUtility.getNumberOfCCSFollowup(CCS_TR_ID:554,TYPE:0)
    // ReflectionUtility.getNumberOfCCSFollowup(CCS_TR_ID:554,TYPE:q12)

    public String getNumberOfCCSFollowup(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        String ccsTrId = null;
        String type = null;

        for (String part : params) {
            part = part.trim();
            if (part.split(":")[0].equals("CCS_TR_ID")) {
                ccsTrId = part.split(":")[1].trim();
                QuestionAnswer qAns = questionAnswerMap.get(ccsTrId);
                if (qAns != null && qAns.getAnswerList().size() == 1) {
                    ccsTrId = qAns.getAnswerList().get(0);
                }
            } else if (part.split(":")[0].equals("TYPE")) {
                type = part.split(":")[1].trim();
                QuestionAnswer qAns = questionAnswerMap.get(type);
                if (qAns != null && qAns.getAnswerList().size() == 1) {
                    type = qAns.getAnswerList().get(0);
                }
            }
        }

        try {
            return App.getContext().getDB().getCcsDao().getNumberOfCCSFollowup(Long.parseLong(ccsTrId), Long.parseLong(type)) + "";
        } catch (Exception ex) {
            return "ERROR";
        }
    }


    // ReflectionUtility.getTopicId(TOPIC_NAME)
    // ReflectionUtility.getTopicId(q8)
    public ArrayList<String> getTopicId(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> ids = new ArrayList<String>();
        if (params.length == 1) {
            String refText = params[0];
            QuestionAnswer questionAnswer = questionAnswerMap.get(refText);
            if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
                for (String text : questionAnswer.getAnswerList()) {
                    long id = App.getContext().getDB().getTopicId(text);
                    if (id > 0) {
                        ids.add(id + "");
                    }
                }
            } else {
                long id = App.getContext().getDB().getTopicId(refText);
                if (id > 0) {
                    ids.add(id + "");
                }
            }
        } else {
            ids.add("ERROR");
        }

        return ids;
    }

    //ReflectionUtility.subtract(q5,q6) //ReflexionUtility.subtraction(q5,15) prams length 2
    public String getLocationDistance(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length != 2) return ERROR;
        double ans = 0;

        try {
            String[] location1 = null;
            String[] location2 = null;

            QuestionAnswer questionAnswer = questionAnswerMap.get(params[0].trim());
            if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
                location1 = questionAnswer.getAnswerList().get(0).split("##");
            } else {
                location1 = params[0].split("##");
            }

            QuestionAnswer questionAnswer2 = questionAnswerMap.get(params[1].trim());
            if (questionAnswer2 != null && questionAnswer2.getAnswerList() != null) {
                location2 = questionAnswer2.getAnswerList().get(0).split("##");
            } else {
                location2 = params[1].split("##");
            }

            double lat1 = Double.parseDouble(location1[0]);
            double lon1 = Double.parseDouble(location1[1]);
            double lat2 = Double.parseDouble(location2[0]);
            double lon2 = Double.parseDouble(location2[1]);
            double dis = GPSUtility.distance(lat1, lon1, lat2, lon2);
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            return decimalFormat.format(dis);
        } catch (Exception e) {
            return ERROR;

        }
    }


    //	ReflectionUtility.getReferralCenterList(WHERE_CONDITION, FORMATTYPE)
    //	ReflectionUtility.getReferralCenterList(q10,JSON)
    //	ReflectionUtility.getReferralCenterList(q10,XML)
    //	ReflectionUtility.getReferralCenterList(LOCATION=1,CSV)
    // in where condition  use "^" as an alternative  ","
//    public ArrayList<String> getReferralCenterList(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
//        ArrayList<String> arrayList = new ArrayList<String>();
//        if (params.length == 2) {
//            try {
//                String question = params[0];
//                String formatType = params[1].trim();
//                QuestionAnswer questionAnswer = questionAnswerMap.get(question);
//                String whereCondition = "";
//                if (questionAnswer != null && questionAnswer.getAnswerList() != null && questionAnswer.getAnswerList().size() > 0) {
//                    whereCondition = questionAnswer.getAnswerList().get(0);
//                } else {
//                    whereCondition = question;
//                }
//                ArrayList<ReferralCenterInfo> referralCenterList = App.getContext().getDB().getReferralCenterList(whereCondition);
//                for (ReferralCenterInfo referralCenter : referralCenterList) {
//                    arrayList.add(Utility.getFormatedData(referralCenter.toString(), referralCenter.getID() + "", formatType));
//                }
//                return arrayList;
//            } catch (Exception e) {
//                arrayList = new ArrayList<String>();
//                arrayList.add("ERROR");
//                return arrayList;
//            }
//        } else {
//            arrayList = new ArrayList<String>();
//            arrayList.add("ERROR");
//            return arrayList;
//        }
//    }


    //	ReflectionUtility.getReferralCenterList(WHERE_CONDITION, FORMATTYPE)
    //	ReflectionUtility.getReferralCenterList(q10,JSON)
    //	ReflectionUtility.getReferralCenterList(q10,XML)
    //	ReflectionUtility.getReferralCenterList(LOCATION=1,CSV)
    // in where condition  use "^" as an alternative  ","
    // where condition not working [ (), ] first braket and comma with model casting -create by md.yeasin ali
    public ArrayList<ReferralCenterInfo> getReferralCenterList(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<ReferralCenterInfo> arrayList = new ArrayList<>();
        if (params.length == 2) {
            try {
                String question = params[0];
                String formatType = params[1].trim();
                QuestionAnswer questionAnswer = questionAnswerMap.get(question);
                String whereCondition = "";
                if (questionAnswer != null && questionAnswer.getAnswerList() != null && questionAnswer.getAnswerList().size() > 0) {
                    whereCondition = questionAnswer.getAnswerList().get(0);
                } else {
                    whereCondition = question;
                }
                ArrayList<ReferralCenterInfo> referralCenterList = App.getContext().getDB().getReferralCenterList(whereCondition);

                return referralCenterList;
            } catch (Exception e) {
                arrayList = new ArrayList<>();
                return arrayList;
            }
        } else {
            arrayList = new ArrayList<>();
            return arrayList;
        }
    }

    public ArrayList<String> getReferralCenterIdByCode(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> ids = new ArrayList<String>();
        if (params.length == 1) {
            String refText = params[0];
            QuestionAnswer questionAnswer = questionAnswerMap.get(refText);
            if (questionAnswer != null && questionAnswer.getAnswerList() != null) {
                for (String text : questionAnswer.getAnswerList()) {
                    long id = App.getContext().getDB().getReferralCenterIdByCode(text);
                    if (id > 0) {
                        ids.add(id + "");
                    }
                }
            } else {
                long id = App.getContext().getDB().getReferralCenterIdByCode(refText);
                if (id > 0) {
                    ids.add(id + "");
                }
            }
        } else {
            ids.add("ERROR");
        }

        return ids;
    }

    //ReflectionUtility.getNumberOfInterview(benef_code:0,algorithm_name:0,time_duration:0) time duration of day
    public String getNumberOfInterview(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {

        String benefCode = "";
        String benefGetCode = "";
        String algorithmName = "";
        String durationDay = "";
        int timeDurationDay = 0;
        try {
            for (String part : params) {
                part = part.trim();
                if (part.split(":")[0].equals("benef_code")) {
                    benefCode = part.split(":")[1].trim();
                    QuestionAnswer qAns = questionAnswerMap.get(benefCode);
                    if (qAns != null && qAns.getAnswerList().size() == 1) {
                        benefGetCode = qAns.getAnswerList().get(0);
                    }
                    Log.e("Thano", benefGetCode);
                } else if (part.split(":")[0].equals("algorithm_name")) {
                    algorithmName = part.split(":")[1].trim();
                }
                if (part.split(":")[0].equals("duration_day")) {
                    durationDay = part.split(":")[1].trim();
                }


            }
            try {
                timeDurationDay = Integer.parseInt(durationDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance(); // this would default to now
            calendar.add(Calendar.DAY_OF_MONTH, -timeDurationDay);


            String countValue = App.getContext().getDB().getNumberOfInterview(benefGetCode, algorithmName, calendar.getTimeInMillis(), Calendar.getInstance().getTimeInMillis());

            return countValue;

        } catch (Exception e) {

        }
        return "-1";
    }

    //ReflectionUtility.measureChildGrowth(WEIGHT:10/q5,HEIGHT:5/q6,AGE_IN_MONTH:15/q10,GENDER:Male/q12,OUTPUT:Less.Normal.High)
    public String measureNutritionStatus(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        String MESURE_VALUE = null;
        String MESURE_INPUT_VALUE = null;
        String AGE_IN_MONTH = null;
        String GENDER = null;
        String OUTPUT = null;
        String masureType = "";
        long ageInMonth = -1;
        String ans = "";
        try {
            for (String part : params) {
                part = part.trim();
                if (part.split(":")[0].equals("WEIGHT")) {
                    MESURE_VALUE = part.split(":")[1].trim();
                    masureType = "Weight";

                } else if (part.split(":")[0].equals("HEIGHT")) {
                    MESURE_VALUE = part.split(":")[1].trim();
                    masureType = "Height";

                }
                if (part.split(":")[0].equals("AGE_IN_MONTH")) {
                    AGE_IN_MONTH = part.split(":")[1].trim();
                    if (AGE_IN_MONTH.startsWith("q")) {
                        QuestionAnswer questionAnswer = questionAnswerMap.get(AGE_IN_MONTH.trim());
                        if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                            AGE_IN_MONTH = questionAnswer.getAnswerList().get(0);
                        }
                    }
                    ageInMonth = Utility.parseLong(AGE_IN_MONTH);
                }

                if (part.split(":")[0].equals("GENDER")) {
                    GENDER = part.split(":")[1].trim();
                    if (GENDER.startsWith("q")) {
                        QuestionAnswer questionAnswer = questionAnswerMap.get(GENDER.trim());
                        if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                            GENDER = questionAnswer.getAnswerList().get(0);
                        }
                    }
                }
                if (MESURE_VALUE.startsWith("q")) {
                    QuestionAnswer questionAnswer = questionAnswerMap.get(MESURE_VALUE.trim());
                    if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                        MESURE_INPUT_VALUE = questionAnswer.getAnswerList().get(0);
                    }
                }

            }

            ans = App.getContext().getDB().getRangeChildGrowthWhitResult(masureType, ageInMonth, GENDER, MESURE_INPUT_VALUE);
        } catch (Exception e) {

        }
        return ans;
    }

    //courrent use
     /* create by md.yeasin ali
     ReflectionUtility.measureChildGrowth(WEIGHT:10/q5,HEIGHT:5/q6,AGE_IN_MONTH:15/q10,GENDER:Male/q12,OUTPUT:Less.Normal.High)
     return version code like 8085 */
    public String measureNutritionStatusEx(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        String MEASURE_TYPE = null;
        String MEASURE_VALUE = null;
        String MESURE_INPUT_VALUE = null;
        String AGE_IN_MONTH = null;
        String GENDER = null;
        String OUTPUT = null;
        String masureType = "";
        double ageInMonth = 0.0;
        String ans = "";
        try {
            for (String part : params) {
                part = part.trim();
                if (part.split(":")[0].equals("MEASURE_TYPE")) {
                    masureType = part.split(":")[1].trim();
                }
                if (part.split(":")[0].equals("MEASURE_VALUE")) {
                    MEASURE_VALUE = part.split(":")[1].trim();
                    if (MEASURE_VALUE.startsWith("q")) {
                        QuestionAnswer questionAnswer = questionAnswerMap.get(MEASURE_VALUE.trim());
                        if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                            MESURE_INPUT_VALUE = questionAnswer.getAnswerList().get(0);
                        }
                    }
                }
                if (part.split(":")[0].equals("AGE_IN_MONTH")) {
                    AGE_IN_MONTH = part.split(":")[1].trim();
                    if (AGE_IN_MONTH.startsWith("q")) {
                        QuestionAnswer questionAnswer = questionAnswerMap.get(AGE_IN_MONTH.trim());
                        if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                            AGE_IN_MONTH = questionAnswer.getAnswerList().get(0);
                        }
                    }
                    ageInMonth = Utility.parseDouble(AGE_IN_MONTH);
                }

                if (part.split(":")[0].equals("GENDER")) {
                    GENDER = part.split(":")[1].trim();
                    if (GENDER.startsWith("q")) {
                        QuestionAnswer questionAnswer = questionAnswerMap.get(GENDER.trim());
                        if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                            GENDER = questionAnswer.getAnswerList().get(0);
                        }
                    }
                }


            }

            ans = App.getContext().getDB().getRangeChildGrowthWhitResult(masureType, ageInMonth, GENDER, MESURE_INPUT_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans;
    }

    /* create by md.yeasin ali
     ReflectionUtility.getVersionNumber()
     return version code like 3085 */
    public int getVersionNumber(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        try {
            int versionCode = BuildConfig.VERSION_CODE;
            return versionCode;
        } catch (Exception e) {

        }
        return -1;
    }

    /* create by md.yeasin ali
        ReflectionUtility.getAgeInYear(Q1)
       ReflectionUtility.getAgeInYear(2021-08-22)
       return version code like 1 */
    public long getAgeInYear(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length == 1) {
            try {
                String date = params[0].split(":")[0];
                QuestionAnswer questionAnswerDate = questionAnswerMap.get(date);
                if (questionAnswerDate != null) {
                    date = questionAnswerDate.getAnswerList().get(0);
                }

                return Utility.getAgeInYear(date);
            } catch (Exception e) {
                return -1;
            }
        } else {
            return -1;
        }

    }


 /* create by md.yeasin ali
     ReflectionUtility.getVersionNumber()
     return version code like 3085 */

    public ArrayList<String> getBeneficiaryListInHouseForCoupleReg(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        ArrayList<String> arrayList = new ArrayList<String>();
        String HH_NUMBER = null;
        String GENDER = null;
        String AGE_IN_MONTH = null;
        String MARITAL_STATUS = null;
        String TYPE_OF_AGE = null;
        int START_AGE = 0;
        int END_AGE = 0;
        String FORMAT_TYPE = null;

        if (params.length > 0) {
            try {
                for (String part : params) {
                    part = part.trim();

                    if (part.split(":")[0].equals("HH_NUMBER")) {
                        HH_NUMBER = part.split(":")[1].trim();
                        if (HH_NUMBER.startsWith("q")) {
                            QuestionAnswer questionAnswer = questionAnswerMap.get(HH_NUMBER.trim());
                            if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                                HH_NUMBER = questionAnswer.getAnswerList().get(0);
                            }
                        }
                    }

                    if (part.split(":")[0].equals("GENDER")) {
                        GENDER = part.split(":")[1].trim();
                        if (GENDER.startsWith("q")) {
                            QuestionAnswer questionAnswer = questionAnswerMap.get(GENDER.trim());
                            if (questionAnswer != null && questionAnswer.getAnswerList().size() == 1) {
                                GENDER = questionAnswer.getAnswerList().get(0);
                            }
                        }
                    }


                    if (part.split(":")[0].equals("TYPE_OF_AGE")) {
                        TYPE_OF_AGE = part.split(":")[1].trim();
                    }
                    if (part.split(":")[0].equals("MARITAL_STATUS")) {
                        MARITAL_STATUS = part.split(":")[1].trim();
                    }

                    if (part.split(":")[0].equals("START_AGE")) {
                        START_AGE = Integer.parseInt(part.split(":")[1].trim());
                    }
                    if (part.split(":")[0].equals("END_AGE")) {
                        END_AGE = Integer.parseInt(part.split(":")[1].trim());
                    }
                    if (part.split(":")[0].equals("FORMAT_TYPE")) {
                        FORMAT_TYPE = part.split(":")[1].trim();
                    }

                }
                ArrayList<Beneficiary> beneficiaries = App.getContext().getDB().getHouseholdMemberListForCouple(HH_NUMBER, GENDER, TYPE_OF_AGE, START_AGE, END_AGE);
                if (beneficiaries.size() > 0) {
                    for (Beneficiary beneficiary : beneficiaries) {
                        String caption = beneficiary.getBenefCode() + " ";
                        if (beneficiary.getGender().equals("F")) {
                            caption = caption + "♀";
                        } else if (beneficiary.getGender().equals("M")) {
                            caption = caption + "♂";
                        } else {
                            caption = caption + "⚥";
                        }
                        caption = caption + " " + beneficiary.getAge() + " " + beneficiary.getBenefName();
                        String value = beneficiary.getBenefCodeFull();
                        arrayList.add(Utility.getFormatedData(caption, value, FORMAT_TYPE));
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        if (App.getContext().getAppSettings().getLanguage().equals("bn")) {
                            jsonObject.put("CAPTION", "এই উপকারভোগীর জন্য দম্পতি নিবন্ধন প্রযোজ্য নয়।");
                        }
                        if (App.getContext().getAppSettings().getLanguage().equals("en")) {
                            jsonObject.put("CAPTION", "Couple registration is not applicable for this beneficiary");
                        }

                        jsonObject.put("VALUE", "COUPLE_IS_NOT_APPLICABLE");
                        arrayList.add(jsonObject.toString());
                    } catch (JSONException es) {
                        es.printStackTrace();
                    }
                }

                return arrayList;
            } catch (Exception e) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("CAPTION", "ERROR");
                    jsonObject.put("VALUE", "ERROR");
                    arrayList.add(jsonObject.toString());
                } catch (JSONException es) {
                    es.printStackTrace();
                }

                return arrayList;
            }
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("CAPTION", "ERROR");
                jsonObject.put("VALUE", "ERROR");
                arrayList.add(jsonObject.toString());
            } catch (JSONException es) {
                es.printStackTrace();
            }

            return arrayList;
        }

    }

    //ReflectionUtility.getCoupleId(benef_code:0)
    public String getCoupleId(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {

        String benefCode = "";
        String benefCodeHBGet = "";
        String benefCodeWFGet = "";
        try {
            for (String part : params) {
                part = part.trim();
                if (part.split(":")[0].equals("hb_benef_code")) {
                    benefCode = part.split(":")[1].trim();
                    QuestionAnswer qAns = questionAnswerMap.get(benefCode);
                    if (qAns != null && qAns.getAnswerList().size() == 1) {
                        benefCodeHBGet = qAns.getAnswerList().get(0);
                    }
                } else if (part.split(":")[0].equals("wf_benef_code")) {
                    benefCode = part.split(":")[1].trim();
                    QuestionAnswer qAns = questionAnswerMap.get(benefCode);
                    if (qAns != null && qAns.getAnswerList().size() == 1) {
                        benefCodeWFGet = qAns.getAnswerList().get(0);
                    }
                }

            }

            String coupleID = App.getContext().getDB().getCoupleCodeByBenefCodeIfExist(benefCodeHBGet, benefCodeWFGet);

            return coupleID;

        } catch (Exception e) {

        }
        return "-1";
    }

    public String getCoupleIdByBenefCode(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {

        String benefCode = "";
        try {
            for (String part : params) {
                part = part.trim();
                if (part.split(":")[0].equals("benef_code")) {
                    benefCode = part.split(":")[1].trim();
                    QuestionAnswer qAns = questionAnswerMap.get(benefCode);
                    if (qAns != null && qAns.getAnswerList().size() == 1) {
                        benefCode = qAns.getAnswerList().get(0);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String coupleCode = App.getContext().getDB().getCoupleIdByBenefCode(benefCode);

        return coupleCode;


    }

    public String getCoupleIdByWifeCode(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {

        String benefCode = "";
        String benefCodeHBGet = "";
        String benefCodeWFGet = "";
        try {
            for (String part : params) {
                part = part.trim();
                if (part.split(":")[0].equals("benef_code")) {
                    benefCode = part.split(":")[1].trim();
                    QuestionAnswer qAns = questionAnswerMap.get(benefCode);
                    if (qAns != null && qAns.getAnswerList().size() == 1) {
                        benefCodeWFGet = qAns.getAnswerList().get(0);
                    }
                }

            }


            String coupleID = App.getContext().getDB().getCoupleIdByBenefCodeIfExist(benefCodeWFGet);

            return coupleID;

        } catch (Exception e) {

        }
        return "-1";
    }

    public String getNextCoupleCode(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {

        String benefCode = "";
        String householdNo = "";
        try {
            for (String part : params) {
                part = part.trim();
                if (part.split(":")[0].equals("household_no")) {
                    benefCode = part.split(":")[1].trim();
                    QuestionAnswer qAns = questionAnswerMap.get(benefCode);
                    if (qAns != null && qAns.getAnswerList().size() == 1) {
                        householdNo = qAns.getAnswerList().get(0);
                    }
                }

            }
            String coupleCode = App.getContext().getDB().generateCoupleCodeFromHHNumber(householdNo);

            return coupleCode;

        } catch (Exception e) {

        }
        return "-1";
    }

    public String getHusbandOrWifeCodeByCoupleCode(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        try {
            if (params.length == 2) {

                String coupleCode = params[0];
                String who = params[1].trim();
                QuestionAnswer questionAnswer = questionAnswerMap.get(coupleCode);
                return App.getContext().getDB().getHusbandOrWifeCodeByCoupleCode(questionAnswer.getAnswerList().get(0), who);
            }


        } catch (Exception e) {

        }
        return "-1";
    }

    //courrent use
    /* create by md.yeasin ali 24-05-2023
   // ReflectionUtility.getDCcontactNumber(q7); //question must Contain location number input like 0211 and output 018.......
     */
    public String getDCcontactNumber(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length == 1) {
            String locationNoKey = "";
            try {
                QuestionAnswer answer = questionAnswerMap.get(params[0].trim());
                if (answer != null && answer.getAnswerList() != null) {
                    locationNoKey = answer.getAnswerList().get(0);
                    return JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "DOCTOR_CENTER_CONTACT", locationNoKey);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "-1";
    }


    public String getNameByBenefCode(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        try {
            if (params.length == 1) {
                QuestionAnswer questionAnswer = questionAnswerMap.get(params[0]);
                return App.getContext().getDB().getNameByBenefCode(questionAnswer.getAnswerList().get(0));
            }


        } catch (Exception e) {

        }
        return "-1";
    }

    public String isBeneficiaryNameExist(String[] params, HashMap<String, QuestionAnswer> questionAnswerMap) {
        if (params.length == 1) {
            String transRef = "";
            QuestionAnswer answer = questionAnswerMap.get(params[0].trim());
            if (answer != null) {
                transRef = answer.getAnswerList().get(0);
            } else {
                transRef = params[0].trim();
            }
            if (!App.getContext().getDB().isBeneficiaryNameExist(transRef)) {
                return "NO";
            }
            return "YES";
        }
        return "ERROR";
    }


}
