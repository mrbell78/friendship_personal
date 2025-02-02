package ngo.friendship.satellite.storage;

import static android.content.ContentValues.TAG;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.Priority;
import ngo.friendship.satellite.constants.QuestionnaireName;
import ngo.friendship.satellite.constants.RequisitionStatus;
import ngo.friendship.satellite.constants.ScheduleStatus;
import ngo.friendship.satellite.constants.StockAdjustmentRequestStatus;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.loadname.NameInfo;
import ngo.friendship.satellite.model.AdjustmentMedicineInfo;
import ngo.friendship.satellite.model.AppVersionHistory;
import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.BeneficiaryRegistrationState;
import ngo.friendship.satellite.model.CCSBeneficiary;
import ngo.friendship.satellite.model.ConsumptionDetailsModel;
import ngo.friendship.satellite.model.Couple;
import ngo.friendship.satellite.model.CourtyardMeeting;
import ngo.friendship.satellite.model.DiagnosisInfo;
import ngo.friendship.satellite.model.EventInfo;
import ngo.friendship.satellite.model.Household;
import ngo.friendship.satellite.model.InterviewInfoSyncUnsync;
import ngo.friendship.satellite.model.LocationModel;
import ngo.friendship.satellite.model.MedicineAdjustmentDetailModel;
import ngo.friendship.satellite.model.MedicineAdjustmentInfoModel;
import ngo.friendship.satellite.model.MedicineConsumptionInfoModel;
import ngo.friendship.satellite.model.MedicineReceiveModel;
import ngo.friendship.satellite.model.MedicineReceivedDetailModel;
import ngo.friendship.satellite.model.MedicineSellInfo;
import ngo.friendship.satellite.model.NotificationItem;
import ngo.friendship.satellite.model.QuestionList;
import ngo.friendship.satellite.model.SatelliteSessionChwarModel;
import ngo.friendship.satellite.model.SatelliteSessionModel;
import ngo.friendship.satellite.model.TextRef;
import ngo.friendship.satellite.model.UserInfo;
import ngo.friendship.satellite.model.FollowupQuestionnaireInfo;
import ngo.friendship.satellite.model.HealthCareReportInfo;
import ngo.friendship.satellite.model.ImmunaizationBeneficiary;
import ngo.friendship.satellite.model.IndividualSalesInfo;
import ngo.friendship.satellite.model.LocalMonthInfo;
import ngo.friendship.satellite.model.MedicineInfo;
import ngo.friendship.satellite.model.MedicineRcvSaleInfo;
import ngo.friendship.satellite.model.PatientInterviewDetail;
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedback;
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedbackList;
import ngo.friendship.satellite.model.PatientInterviewMaster;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionnaireCategoryInfo;
import ngo.friendship.satellite.model.QuestionnaireDetail;
import ngo.friendship.satellite.model.QuestionnaireInfo;
import ngo.friendship.satellite.model.ReferralCenterInfo;
import ngo.friendship.satellite.model.Report;
import ngo.friendship.satellite.model.RequisitionInfo;
import ngo.friendship.satellite.model.RequisitionMedicineInfo;
import ngo.friendship.satellite.model.SavedInterviewInfo;
import ngo.friendship.satellite.model.ScheduleInfo;
import ngo.friendship.satellite.model.StockAdjustmentInfo;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.model.maternal.MaternalAbortion;
import ngo.friendship.satellite.model.maternal.MaternalInfo;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class SatelliteCareDatabaseManager extends SQLiteOpenHelper {
    /**
     * The db path.
     */
    private String DB_PATH;
    /**
     * The db name.
     */
    private static String DB_NAME = "mh_data.sqlite";
    // private String DB_TITLE = "mHealth Medicine Inventory";
    /**
     * The db.
     */
    private SQLiteDatabase db;
    /**
     * The context.
     */
    private Context context;
    private String UPDATE_SQL = "";
    /**
     * The db version.
     */
    private int DB_VERSION = 24;

    /**
     * Instantiates a new m health database manager.
     *
     * @param context the context
     */
    public SatelliteCareDatabaseManager(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        DB_PATH = App.getContext().getAppDataDir(context);

        if (!checkDataBase()) {
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public CcsDao getCcsDao() {
        return CcsDao.getInstance(this);
    }

    public ImmunizationDao getImmunizationDao() {
        return ImmunizationDao.getInstance(this);
    }

    public MaternalDao getMaternalDao() {
        return MaternalDao.getInstance(this);
    }

    /**
     * Connect to database.
     *
     * @return true if connection successful. false otherwise
     */
    public boolean initializeDatabase() {
        if (DB_PATH == null) return false;
        try {
            db = SQLiteDatabase.openDatabase(DB_PATH + "/" + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

            String forainsql = " PRAGMA foreign_keys=ON; ";
            db.execSQL(forainsql);

            String SQL[] = UPDATE_SQL.split(";");
            for (String sql : SQL) {
                if (sql.length() > 0) {
                    try {
                        Log.e("SQL", sql.toString());
                        db.execSQL(sql);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            UPDATE_SQL = "";

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public void deleteDb() {
        String myPath = DB_PATH + "/" + DB_NAME;
        if (new File(myPath).exists()) {
            new File(myPath).delete();
        }
    }

    public File getDump() {
        String myPath = DB_PATH + "/" + DB_NAME;
        if (new File(myPath).exists()) {
            return new File(myPath);
        }
        return null;
    }

    /**
     * Check if the database file exist.
     *
     * @return true is database exist, false otherwise
     */
    private boolean checkDataBase() {
        int appVersionCode = 0;

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int dbVersion = AppPreference.getInt(context, "DB_VERSION", 0);

        String myPath = DB_PATH + "/" + DB_NAME;
        if (new File(myPath).exists()) {
            if (dbVersion < DB_VERSION) {
                UPDATE_SQL = (new UpdateDB()).getSchema(context, dbVersion, DB_VERSION);
                AppPreference.putInt(context, "DB_VERSION", DB_VERSION);
            }
            return true;
        } else return false;
    }

    // /sas

    /**
     * Copy database from application asset to device storage.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void copyDataBase() throws IOException {
        // Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + "/" + DB_NAME;

        // Open the empty db as the output stream
        new File(outFileName).createNewFile();
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        AppPreference.putInt(context, "DB_VERSION", DB_VERSION);
    }

    /*
     * Get the list of medicines which have the same generic name as the
     * medicine in prescription.
     *
     * @param prescription is the list of medicine which represent the
     * prescription
     *
     * @return The list of medicines
     */


    @SuppressLint("Range")
    public ArrayList<MedicineInfo> getMedicineList() {
        ArrayList<MedicineInfo> medList = new ArrayList<MedicineInfo>();

        StringBuilder sql = new StringBuilder();
        sql.append("   SELECT m.MEDICINE_ID, m.TYPE,m.GENERIC_NAME, m.BRAND_NAME,  ");
        sql.append("   ROUND(m.UNIT_SALES_PRICE,2 )  UNIT_SALES_PRICE, ");
        sql.append("    m.STRENGTH,m.MEASURE_UNIT,m.MANUF_ID, ROUND(m.UNIT_PURCHASE_PRICE,2) as UNIT_PURCHASE_PRICE,  ");
        sql.append("   IFNULL(ms.`CURRENT_STOCK_QTY`, 0) AS CURRENT_STOCK_QTY  ");
        sql.append("   from medicine m  ");
        sql.append("   LEFT JOIN medicine_stock ms  ");
        sql.append("       ON m.MEDICINE_ID = ms.MEDICINE_ID  ");
        sql.append("       AND USER_ID =" + App.getContext().getUserInfo().getUserId());
        sql.append("   where m.ACCESS_TYPE='FCM'    and m.state=1 ");
        sql.append("    ORDER BY m.TYPE, m.BRAND_NAME   ");


        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setMedId(cursor.getInt(cursor.getColumnIndex(Column.MEDICINE_ID)));
                medInfo.setBrandName(cursor.getString(cursor.getColumnIndex(Column.BRAND_NAME)));
                medInfo.setGenericName(cursor.getString(cursor.getColumnIndex(Column.GENERIC_NAME)));
                medInfo.setMedicineType(cursor.getString(cursor.getColumnIndex(Column.MEDICINE_TYPE)));

                medInfo.setUnitPurchasePrice(cursor.getDouble(cursor.getColumnIndex(Column.UNIT_PURCHASE_PRICE)));
                medInfo.setUnitSalesPrice(cursor.getDouble(cursor.getColumnIndex(Column.UNIT_SALES_PRICE)));
                medInfo.setStrength(cursor.getFloat(cursor.getColumnIndex(Column.STRENGTH)));
                medInfo.setMeasureUnit(cursor.getString(cursor.getColumnIndex(Column.MEASURE_UNIT)));
                medInfo.setManufId(cursor.getInt(cursor.getColumnIndex(Column.MANUF_ID)));
                medInfo.setAvailableQuantity(cursor.getInt(cursor.getColumnIndex(Column.CURRENT_STOCK_QTY)));
                medList.add(medInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return medList;
    }

    @SuppressLint("Range")
    public MedicineInfo getMedicine(long id) {
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        sql.append("TYPE, ");
        sql.append("MEDICINE_ID, ");
        sql.append("BRAND_NAME, ");
        sql.append("GENERIC_NAME, ");
        sql.append("UNIT_PURCHASE_PRICE, ");
        sql.append("UNIT_SALES_PRICE, ");
        sql.append("STRENGTH, ");
        sql.append("MEASURE_UNIT, ");
        sql.append("MANUF_ID, ");
        sql.append("BOX_SIZE, ");
        sql.append("STATE, ");
        sql.append("UNIT_TYPE, ");
        sql.append("ACCESS_TYPE ");
        sql.append(" from medicine where  MEDICINE_ID=" + id);

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), null);
        MedicineInfo medInfo = null;
        if (cursor.moveToFirst()) {

            medInfo = new MedicineInfo();
            medInfo.setMedId(cursor.getInt(cursor.getColumnIndex(Column.MEDICINE_ID)));
            medInfo.setBrandName(cursor.getString(cursor.getColumnIndex(Column.BRAND_NAME)));
            medInfo.setGenericName(cursor.getString(cursor.getColumnIndex(Column.GENERIC_NAME)));
            medInfo.setMedicineType(cursor.getString(cursor.getColumnIndex(Column.MEDICINE_TYPE)));
            medInfo.setUnitPurchasePrice(cursor.getDouble(cursor.getColumnIndex(Column.UNIT_PURCHASE_PRICE)));
            medInfo.setUnitSalesPrice(cursor.getDouble(cursor.getColumnIndex(Column.UNIT_SALES_PRICE)));
            medInfo.setStrength(cursor.getFloat(cursor.getColumnIndex(Column.STRENGTH)));
            medInfo.setMeasureUnit(cursor.getString(cursor.getColumnIndex(Column.MEASURE_UNIT)));
            medInfo.setManufId(cursor.getInt(cursor.getColumnIndex(Column.MANUF_ID)));
        }

        cursor.close();

        return medInfo;
    }

    /**
     * Get all medicines with all information.
     *
     * @return Medicine list
     */
    public ArrayList<MedicineInfo> getAllMedicine(String accessType) {
        ArrayList<MedicineInfo> medicineList = new ArrayList<MedicineInfo>();

        String sql = "SELECT * FROM medicine where STATE=1 AND  ACCESS_TYPE='" + accessType + "'  ORDER BY TYPE, BRAND_NAME ";
        Cursor cursor = db.rawQuery(sql, null);
        Log.e("SQL", sql.toString());
        if (cursor.moveToFirst()) {
            do {
                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setMedId(cursor.getInt(cursor.getColumnIndex(Column.MEDICINE_ID)));
                medInfo.setBrandName(cursor.getString(cursor.getColumnIndex(Column.BRAND_NAME)));
                medInfo.setGenericName(cursor.getString(cursor.getColumnIndex(Column.GENERIC_NAME)));
                medInfo.setMedicineType(cursor.getString(cursor.getColumnIndex(Column.MEDICINE_TYPE)));
                medInfo.setUnitPurchasePrice(cursor.getDouble(cursor.getColumnIndex(Column.UNIT_PURCHASE_PRICE)));
                medInfo.setUnitSalesPrice(cursor.getDouble(cursor.getColumnIndex(Column.UNIT_SALES_PRICE)));
                medInfo.setStrength(cursor.getFloat(cursor.getColumnIndex(Column.STRENGTH)));
                medInfo.setMeasureUnit(cursor.getString(cursor.getColumnIndex(Column.MEASURE_UNIT)));
                medInfo.setManufId(cursor.getInt(cursor.getColumnIndex(Column.MANUF_ID)));

                medicineList.add(medInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medicineList;
    }

    public String getDignosisIds(String names) {
        names = names.replaceAll("##", "','");
        String ids = "#";
        String sql = " SELECT DIAG_ID FROM diagnosis_info where DIAG_NAME in ('" + names + "')";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                ids = ids + "," + cursor.getInt(cursor.getColumnIndex("DIAG_ID"));
            } while (cursor.moveToNext());
            ids = ids.replace("#,", "");
        }
        cursor.close();
        return ids;
    }

    /**
     * Get the code of the beneficiary in houseHold with name if the beneficiary
     * already resigtered.
     *
     * @param name      is the name of the beneficiary
     * @param houseHold is the household number
     * @return the beneficiary code. null if the beneficiary does not exist
     */
    public String getBeneficiaryCodeIfExist(String name, String houseHold) {
        String beneficiaryCode = null;
        String sql = " SELECT BENEF_CODE  FROM  beneficiary" + " WHERE substr(BENEF_CODE,0,length('" + houseHold + "')+1)='" + houseHold + "' and BENEF_NAME='" + name + "'";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            beneficiaryCode = cursor.getString(cursor.getColumnIndex("BENEF_CODE"));

        }
        cursor.close();
        return beneficiaryCode;
    }

    /**
     * Generate the beneficiary code from householdNumber.
     *
     * @param householdNumber is the household number of the beneficiary
     * @return The beneficiary code
     */
    public String generateBeneficiaryCodeFromHHNumber(String householdNumber, int incrementBy) {
        String benefCode = householdNumber + "01";

        String sql = String.format("SELECT CASE WHEN MAX(`BENEF_CODE`) IS NULL THEN '%s' || '01' " + "ELSE '%s' || substr('000' || cast(substr(MAX(`BENEF_CODE`),-2,2)+" + incrementBy + " as text), -2,2) END AS beneficiaryCode " + "FROM `beneficiary` WHERE substr(BENEF_CODE,0,length('%s')+1)='%s' ", householdNumber, householdNumber, householdNumber, householdNumber);
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            benefCode = cursor.getString(cursor.getColumnIndex("beneficiaryCode"));
        }

        return benefCode;
    }


    public String getRangeChildGrowth(String mesereType, double ageInMonth, String gender) {

        String sql = "SELECT " + " RANGE_START||'-'||RANGE_END GROWTH_RANGE " + " FROM " + " growth_measurement_param  " + " WHERE " + " GENDER = '" + gender + "'  " + " AND " + " MEASURE_TYPE ='" + mesereType + "' " + " AND " + " AGE_IN_MONTH=" + ageInMonth;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("GROWTH_RANGE")).trim();
        }
        return "";
    }


    public String getRangeChildGrowthWhitResult(String mesereType, double ageInMonth, String gender, String inputValue) {

        String sql = "SELECT OUTPUT_RESULT " + " FROM  growth_measurement_param   WHERE " + inputValue + " BETWEEN RANGE_START  and RANGE_END  AND " + " GENDER = '" + gender + "' AND  MEASURE_TYPE ='" + mesereType + "'   AND  AGE_IN_MONTH=" + ageInMonth + " AND IMPL_VERSION >0";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("OUTPUT_RESULT")).trim();
        }
        return "";
    }

    public void saveQuestionnaireList(Context context, ArrayList<QuestionnaireInfo> rows, JSONObject param) {
        long count = 0;
        for (QuestionnaireInfo questionnaireInfo : rows) {
            ContentValues cv = new ContentValues();
            cv.put(Column.QUESTIONNAIRE_ID, questionnaireInfo.getId());
            cv.put(Column.QUESTIONNAIRE_TITLE, questionnaireInfo.getQuestionnaireTitle());
            cv.put(Column.QUESTIONNAIRE_PROMPT_FOR_BENEFICIARY, questionnaireInfo.getPromptForBeneficiary());
            cv.put(Column.QUESTIONNAIRE_BENEF_SELECTION_CRITERIA, questionnaireInfo.getBenefSelectionCriteria());
            cv.put(Column.VISIBILE_IN_CATEGORY, questionnaireInfo.getVisibileInCategory());
            cv.put(Column.QUESTIONNAIRE_JSON, questionnaireInfo.getQuestionnaireJSON());
            cv.put(Column.CATEGORY_ID, questionnaireInfo.getCategoryId());
            cv.put(Column.QUESTIONNAIRE_NAME, questionnaireInfo.getQuestionnaireName());
            cv.put("SORT_ORDER", questionnaireInfo.getSortOrder());
            cv.put(Column.STATE, questionnaireInfo.getState());
            cv.put(Column.ICON, questionnaireInfo.getIcon());
            cv.put(Column.LANG_CODE, questionnaireInfo.getLangCode());
            cv.put(Column.SERVICE_CATEGORY_ID, questionnaireInfo.getServiceCategoryId());
            cv.put(Column.SINGLE_PG_FORM_VIEW, questionnaireInfo.getIsSinglePgFormView());

            cv.put(Column.PARAM_1, questionnaireInfo.getParam1());

            cv.put(Column.PARAM_2, questionnaireInfo.getParam2());

            int numberOfRowAffected = db.update(DBTable.QUESTIONNAIRE, cv, Column.QUESTIONNAIRE_ID + "=? AND " + Column.LANG_CODE + "=?", new String[]{Long.toString(questionnaireInfo.getId()), questionnaireInfo.getLangCode()});

            if (numberOfRowAffected <= 0) db.insert(DBTable.QUESTIONNAIRE, null, cv);
            // // Delete all follow up questionnaire relation from relation
            // table
            db.delete(DBTable.QUESTIONNAIRE_FOLLOWUP_RELATION, "QUESTIONNAIRE_ID=?", new String[]{Long.toString(questionnaireInfo.getId())});

            // // Insert follow up relation information into relation table
            if (questionnaireInfo.getFollowupQuestionnaireList() != null && questionnaireInfo.getFollowupQuestionnaireList().size() > 0) {
                for (FollowupQuestionnaireInfo fqi : questionnaireInfo.getFollowupQuestionnaireList()) {
                    ContentValues fqiCv = new ContentValues();
                    fqiCv.put("QUESTIONNAIRE_ID", fqi.getParentQuestionnaireId());
                    fqiCv.put("FOLLOWUP_QUESTIONNAIRE_ID", fqi.getFollowupQuestionnaireId());
                    fqiCv.put("SORT_ORDER", fqi.getSortOrder());
                    db.insert(DBTable.QUESTIONNAIRE_FOLLOWUP_RELATION, null, fqiCv);
                }
            }

            if (questionnaireInfo.getQuestionnaireDetails() != null) {
                for (QuestionnaireDetail questionnaireDetail : questionnaireInfo.getQuestionnaireDetails()) {
                    ContentValues cvDtl = new ContentValues();
                    cvDtl.put(Column.Q_ID, questionnaireDetail.getQId());
                    cvDtl.put(Column.QUESTIONNAIRE_ID, questionnaireDetail.getQuestionnaireId());
                    cvDtl.put(Column.QUESTION_ID, questionnaireDetail.getQuestionId());
                    cvDtl.put(Column.Q_NAME, questionnaireDetail.getQName());
                    cvDtl.put(Column.Q_TITLE, questionnaireDetail.getqTitle());
                    cvDtl.put(Column.Q_TYPE, questionnaireDetail.getqType());
                    cvDtl.put(Column.Q_STATUS, questionnaireDetail.getqStatus());
                    cvDtl.put(Column.SHOWABLE_OUTPUT, questionnaireDetail.getShowableOutput());
                    db.replace(QuestionnaireDetail.MODEL_NAME, null, cvDtl);
                }
            }
            // Check if there is any prescription and save into database

            try {
                QuestionList qList = JSONParser.parseQuestionListJSON(questionnaireInfo.getQuestionnaireJSON(context));
                for (int j = 0; j < qList.getQuestionList().size(); j++) {
                    Question question = qList.getQuestionList().get(j);

                    if (question.getPrescription() != null && question.getPrescription().size() > 0) {
                        ArrayList<MedicineInfo> prescription = question.getPrescription();
                        App.getContext().getDB().savePrescription(questionnaireInfo.getId(), prescription);
                    }
                }
            } catch (MhealthException e) {
                // myData.setResponseInfo(Utility.createErrorWebResponse(context,
                // R.string.error_json_parse_exception, e));
                e.printStackTrace();
            }

            count++;
        }

        updateDataVersion(DBTable.QUESTIONNAIRE, rows.size(), count, param, KEY.VERSION_NO_QUESTIONNAIRE);

    }


    public void saveQuestionnaireCategoryList(ArrayList<QuestionnaireCategoryInfo> rows, JSONObject param) {

        long count = 0;
        for (QuestionnaireCategoryInfo categoryInfo : rows) {
            ContentValues cv = new ContentValues();
            cv.put(Column.CATEGORY_ID, categoryInfo.getCategoryId());
            cv.put(Column.CATEGORY_CAPTION, categoryInfo.getCategoryCaption());
            cv.put(Column.STATE, categoryInfo.getState());
            cv.put(Column.ICON, categoryInfo.getIcon());
            cv.put(Column.CATEGORY_NAME, categoryInfo.getCategoryName());
            if (categoryInfo.getSortOrder() != null) {
                cv.put(Column.SORT_ORDER, categoryInfo.getSortOrder());
            }

            cv.put(Column.LANG_CODE, App.getContext().getAppSettings().getLanguage());
            db.replace(DBTable.QUESTIONNAIRE_CATEGORY, null, cv);
            count++;
        }

        updateDataVersion(DBTable.QUESTIONNAIRE_CATEGORY, rows.size(), count, param, KEY.VERSION_NO_CATEGORIES);

    }


    public void saveScheduleList(ArrayList<ScheduleInfo> rows, JSONObject param) {
        if (rows == null) return;
        long count = 0;

        ContentValues cvSched = new ContentValues();
        for (ScheduleInfo schedInfo : rows) {
            try {

                long interviewId = schedInfo.getInterviewId();
                long serverInterviewId = schedInfo.getInterviewId();
                long fcmLocalInterviewId = schedInfo.getFcmInterviewId();
                String benefCode = schedInfo.getBeneficiaryCode();
                String serverInterviewIdWhereClause = Column.INTERVIEW_ID + " =" + serverInterviewId + " and " + Column.BENEF_CODE + " ='" + benefCode + "'";
                String localInterviewIdwhereClause = Column.INTERVIEW_ID + " =" + fcmLocalInterviewId + " and " + Column.BENEF_CODE + " ='" + benefCode + "'";

                if (App.getContext().getDB().isExist(DBTable.PATIENT_INTERVIEW_MASTER, localInterviewIdwhereClause)) {
                    interviewId = schedInfo.getFcmInterviewId();
                } else if (App.getContext().getDB().isExist(DBTable.PATIENT_INTERVIEW_MASTER, serverInterviewIdWhereClause)) {
                    interviewId = schedInfo.getInterviewId();

                }


                cvSched.clear();
                cvSched.put(Column.SCHED_DESC, schedInfo.getScheduleDesc());
                cvSched.put(Column.CREATE_DATE, schedInfo.getCreatedDate());
                cvSched.put(Column.ATTENDED_DATE, schedInfo.getAttendedDate());
                cvSched.put(Column.SCHED_DATE, schedInfo.getScheduleDate());
                cvSched.put(Column.TYPE, schedInfo.getScheduleType());
                cvSched.put(Column.REFERENCE_ID, schedInfo.getReferenceId());
                cvSched.put(Column.SCHED_STATUS, schedInfo.getSchedStatus());
                cvSched.put(Column.STATE, schedInfo.getState());
                cvSched.put(Column.INTERVIEW_ID, interviewId);
                cvSched.put(Column.TRANS_REF, schedInfo.getTransRef());


                if (schedInfo.getEventId() != null && schedInfo.getEventId() > 0) {
                    String sql = " SELECT SCHED_ID FROM user_schedule WHERE EVENT_ID=" + schedInfo.getEventId();
                    String scheId = getId(sql);
                    if (scheId != null && Utility.parseLong(scheId.trim()) > 0) {
                        db.update(DBTable.USER_SCHEDULE, cvSched, "SCHED_ID=" + scheId, null);
                    } else {
                        cvSched.put(Column.EVENT_ID, schedInfo.getEventId());
                        db.insert(DBTable.USER_SCHEDULE, null, cvSched);
                    }
                } else if (schedInfo.getInterviewId() > 0 && schedInfo.getTransRef() > 0) {

                    if (schedInfo.getBeneficiaryCode() != null && schedInfo.getBeneficiaryCode().length() > 0) {
                        cvSched.put(Column.BENEFICIARY_BENEF_CODE, schedInfo.getBeneficiaryCode());
                    }


//                    cvSched.put(Column.INTERVIEW_ID, schedInfo.getInterviewId());
                    /** Check if patient visit already exist */
                    StringBuilder sql = new StringBuilder();
                    sql.append("  SELECT us.SCHED_ID FROM user_schedule us ");
                    sql.append("  INNER JOIN patient_interview_master pim");
                    sql.append("  ON us.INTERVIEW_ID = pim.INTERVIEW_ID ");
                    sql.append("  WHERE us.BENEF_CODE= '" + schedInfo.getBeneficiaryCode() + "' ");
                    sql.append(" AND us.TRANS_REF=" + schedInfo.getTransRef());
                    sql.append(" AND us.TYPE='" + schedInfo.getScheduleType() + "' ");
                    if (schedInfo.getReferenceId() > 0) {
                        sql.append(" AND us.REFERENCE_ID=" + schedInfo.getReferenceId());
                    }
                    sql.append(" AND pim.QUESTIONNAIRE_ID=" + schedInfo.getQuestionnaireId());
                    sql.append(" LIMIT 1");
                    Log.e("SQL", sql.toString());
                    String scheId = getId(sql.toString());

                    if (scheId != null && scheId.trim().length() > 0) {

                        db.update(DBTable.USER_SCHEDULE, cvSched, "SCHED_ID=" + scheId, null);

                    } else {

                        ContentValues cvInterview = new ContentValues();
                        cvInterview.put(Column.DATA_SENT, "Y");
                        cvInterview.put(Column.NEXT_FOLLOWUP_DATE, schedInfo.getScheduleDate());
                        cvInterview.put(Column.BENEFICIARY_BENEF_CODE, schedInfo.getBeneficiaryCode());
                        cvInterview.put(Column.CREATE_DATE, schedInfo.getCreatedDate());
                        cvInterview.put(Column.QUESTIONNAIRE_ID, schedInfo.getQuestionnaireId());
                        cvInterview.put(Column.INTERVIEW_ID, interviewId);

                        // cvInterview.put(Column.FCM_INTERVIEW_ID,  schedInfo.getFcmInterviewId());
                        //   db.update("patient_interview_master",cvInterview, "INTERVIEW_ID=" + schedInfo.getInterviewId(), null);
//                        if(isExist("patient_interview_master", "FCM_INTERVIEW_ID =" + schedInfo.getFcmInterviewId()+" AND ")){
//                            db.update("patient_interview_master",cvInterview, "INTERVIEW_ID=" + schedInfo.getInterviewId(), null);
//                        }
//                        else if(isExist("patient_interview_master", "INTERVIEW_ID=" + schedInfo.getInterviewId())){
//                            db.update("patient_interview_master",cvInterview, "INTERVIEW_ID=" + schedInfo.getInterviewId(), null);
//                        }


                        if (isExist("patient_interview_master", "INTERVIEW_ID=" + interviewId + " and " + Column.BENEF_CODE + " ='" + benefCode + "'")) {
                            db.update("patient_interview_master", cvInterview, "INTERVIEW_ID=" + interviewId + " and " + Column.BENEF_CODE + " ='" + benefCode + "'", null);
                        }
                        db.insert(DBTable.USER_SCHEDULE, null, cvSched);
                    }

                } else if (schedInfo.getInterviewId() <= 0 && schedInfo.getTransRef() > 0 && schedInfo.getBeneficiaryCode() != null && schedInfo.getBeneficiaryCode().length() > 0) {

                    cvSched.put(Column.BENEFICIARY_BENEF_CODE, schedInfo.getBeneficiaryCode());

                    cvSched.put("SYSTEM_CHANGED_DATE", schedInfo.getSystemChangedDate());

                    String sql = " SELECT SCHED_ID FROM user_schedule " + " WHERE BENEF_CODE='" + schedInfo.getBeneficiaryCode() + "' " + " AND TYPE='" + schedInfo.getScheduleType() + "' AND TRANS_REF=" + schedInfo.getTransRef();
                    String schedId = getId(sql);

                    if (schedId != null && schedId.trim().length() > 0) {

                        db.update(DBTable.USER_SCHEDULE, cvSched, "SCHED_ID=" + schedId, null);
                    } else {
                        db.insert(DBTable.USER_SCHEDULE, null, cvSched);
                    }

                }
                count++;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        updateDataVersion(DBTable.USER_SCHEDULE, rows.size(), count, param, KEY.VERSION_NO_USER_SCHEDULE);

    }


    public void savePrescription(long questionnaireId, ArrayList<MedicineInfo> prescription) {

        if (prescription == null) return;

        // /// Delete old record/////
        String sql = TextUtility.format("SELECT %s FROM prescription_for_questionnaire WHERE %s=?", Column.PRESCRIPTION_ID, Column.QUESTIONNAIRE_ID);
        Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(questionnaireId)});
        if (cursor.moveToFirst()) {
            do {
                db.delete("prescription_detail", Column.PRESCRIPTION_ID + "=?", new String[]{Integer.toString(cursor.getInt(cursor.getColumnIndex(Column.PRESCRIPTION_ID)))});
            } while (cursor.moveToNext());
        }
        cursor.close();

        db.delete("prescription_for_questionnaire", Column.QUESTIONNAIRE_ID + "=?", new String[]{Long.toString(questionnaireId)});
        // /////////////

        ContentValues cv = new ContentValues();
        cv.put(Column.QUESTIONNAIRE_ID, questionnaireId);

        // / Insert new record into prescription_for_questionnaire table
        long prescId = db.insert("prescription_for_questionnaire", null, cv);

        for (int i = 0; i < prescription.size(); i++) {
            MedicineInfo medInfo = prescription.get(i);

            cv = new ContentValues();
            cv.put(Column.PRESCRIPTION_ID, prescId);
            cv.put(Column.MEDICINE_ID, medInfo.getMedId());
            cv.put(Column.QTY, medInfo.getRequiredQuantity());
            cv.put(Column.DURATION_DAY, medInfo.getDoseDuration());
            cv.put(Column.TAKING_RULE, medInfo.getMedicineTakingRule());
            cv.put(Column.REMARKS, medInfo.getSmsFormat());

            // / Insert medicine info into prescription_detail
            db.insert("prescription_detail", null, cv);
        }
    }

    /**
     * Get all questionnaire of category whose ID is categoryId.
     *
     * @param categoryId is the category ID
     * @param langCode   the lang code
     * @return The list of questionnaires
     */
    public ArrayList<QuestionnaireInfo> getQuestionnaireList(int categoryId, String langCode) {
        ArrayList<QuestionnaireInfo> questionnaireList = new ArrayList<QuestionnaireInfo>();

        String sql;
        if (categoryId < 0)
            sql = "SELECT * FROM questionnaire WHERE STATE=1 AND LANG_CODE='" + langCode + "' AND NAME !='BENEFICIARY_REGISTRATION' ORDER BY  SORT_ORDER  ";
        else
            sql = "SELECT * FROM questionnaire WHERE CATEGORY_ID=" + categoryId + " AND STATE=1 AND LANG_CODE='" + langCode + "' ORDER BY SORT_ORDER , QUESTIONNAIRE_TITLE ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                QuestionnaireInfo questionnaireInfo = new QuestionnaireInfo();
                questionnaireInfo.setId(cursor.getLong(cursor.getColumnIndex(Column.QUESTIONNAIRE_ID)));
                questionnaireInfo.setPromptForBeneficiary(cursor.getLong(cursor.getColumnIndex(Column.QUESTIONNAIRE_PROMPT_FOR_BENEFICIARY)));
                questionnaireInfo.setQuestionnaireTitle(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_TITLE)));
                questionnaireInfo.setBenefSelectionCriteria(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_BENEF_SELECTION_CRITERIA)));
                questionnaireInfo.setQuestionnaireJSON(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_JSON)));
                questionnaireInfo.setQuestionnaireName(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_NAME)));
                questionnaireInfo.setVisibileInCategory(cursor.getLong(cursor.getColumnIndex(Column.VISIBILE_IN_CATEGORY)));
                questionnaireInfo.setIcon(cursor.getString(cursor.getColumnIndex(Column.ICON)));
                questionnaireInfo.setSinglePgFormView(cursor.getString(cursor.getColumnIndex(Column.SINGLE_PG_FORM_VIEW)));

                questionnaireList.add(questionnaireInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return questionnaireList;
    }


    public QuestionnaireInfo getQuestionnaire(long questionnaireId) {
        String sql = " SELECT * FROM questionnaire WHERE QUESTIONNAIRE_ID=" + questionnaireId + " and LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "' ";
        return getQuestionnaireInfo(sql);
    }

    public QuestionnaireInfo getQuestionnaire(String questionnaireName) {
        String sql = " SELECT * FROM questionnaire WHERE NAME='" + questionnaireName + "' and LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "' ";
        return getQuestionnaireInfo(sql);
    }

    private QuestionnaireInfo getQuestionnaireInfo(String sql) {
        QuestionnaireInfo questionnaireInfo = null; //BENEFICIARY_REGISTRATION
        Log.e("SQL", sql);
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            questionnaireInfo = new QuestionnaireInfo();
            questionnaireInfo.setId(cursor.getLong(cursor.getColumnIndex(Column.QUESTIONNAIRE_ID)));
            questionnaireInfo.setPromptForBeneficiary(cursor.getLong(cursor.getColumnIndex(Column.QUESTIONNAIRE_PROMPT_FOR_BENEFICIARY)));
            questionnaireInfo.setQuestionnaireTitle(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_TITLE)));
            questionnaireInfo.setBenefSelectionCriteria(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_BENEF_SELECTION_CRITERIA)));
            questionnaireInfo.setQuestionnaireJSON(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_JSON)));
            questionnaireInfo.setQuestionnaireName(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_NAME)));
            questionnaireInfo.setVisibileInCategory(cursor.getLong(cursor.getColumnIndex(Column.VISIBILE_IN_CATEGORY)));
        }
        cursor.close();
        return questionnaireInfo;
    }

    public long getMaxInterviewStartTime() {
        long startTime = 1;
        String sql = " SELECT MAX(pim.START_TIME) START_TIME FROM patient_interview_master pim  " + " JOIN questionnaire q ON q.QUESTIONNAIRE_ID=pim.QUESTIONNAIRE_ID  " + " JOIN questionnaire_category  qc ON q.CATEGORY_ID= qc.CATEGORY_ID " + " AND qc.CATEGORY_NAME='OBSTETRICAL_CARE' ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            startTime = cursor.getLong(cursor.getColumnIndex("START_TIME"));
        }
        cursor.close();
        return startTime;
    }

    public void savePatientInterviewMasterList(ArrayList<PatientInterviewMaster> pims) {
        ContentValues cv = new ContentValues();
        for (PatientInterviewMaster pim : pims) {
            String whereClose = " BENEF_CODE='" + pim.getBenefCode() + "' AND TRANS_REF=" + pim.getTransRef() + " AND QUESTIONNAIRE_ID= " + Long.toString(pim.getQuestionnaireId());
            if (!isExist("patient_interview_master", whereClose)) {
                cv.clear();
                cv.put(Column.INTERVIEW_ID, pim.getInterviewId());
                cv.put(Column.QUESTIONNAIRE_ID, pim.getQuestionnaireId());
                cv.put(Column.BENEFICIARY_BENEF_CODE, pim.getBenefCode());
                cv.put(Column.USER_ID, pim.getUserId());
                cv.put(Column.START_TIME, pim.getStartTime());
                cv.put(Column.PRIORITY, pim.getPriority());
                cv.put(Column.QUESTION_ANSWER_JSON, pim.getQuestionAnsJson());

                if (pim.getBenefName() != null && pim.getBenefName().trim().length() > 0) {
                    cv.put(Column.BENEF_NAME, pim.getBenefName());
                }
                if (pim.getAgeInDay() > 0) {
                    cv.put(Column.AGE_IN_DAY, pim.getAgeInDay());
                }

                if (pim.getGender() != null && pim.getGender().trim().length() > 0) {
                    cv.put(Column.GENDER, pim.getGender());
                }

                if (pim.getBenefAddress() != null && pim.getBenefAddress().trim().length() > 0) {
                    cv.put(Column.BENEF_ADDRESS, pim.getBenefAddress());
                }

                if (pim.getParentInterviewId() > 0) {
                    cv.put(Column.PARENT_INTERVIEW_ID, pim.getParentInterviewId());
                }

                if (pim.getRefCenterId() > 0) {
                    cv.put(Column.REF_CENTER_ID, pim.getRefCenterId());
                }

                if (pim.getFcmInterviewId() > 0) {
                    // long
                    cv.put(Column.FCM_INTERVIEW_ID, pim.getFcmInterviewId());
                }
                cv.put(Column.CREATE_DATE, pim.getStartTime());
                cv.put(Column.RECORD_DATE, pim.getRecordDate());
                cv.put(Column.DATA_SENT, "Y");
                cv.put(Column.TRANS_REF, pim.getTransRef());
                cv.put("IS_FEEDBACK", pim.getFeedBack());
                long flag = db.insert("patient_interview_master", null, cv);
                cv.clear();
                if (flag > 0 && pim.getDetails() != null && !pim.getDetails().isEmpty()) {
                    for (PatientInterviewDetail pid : pim.getDetails()) {
                        cv.clear();
                        cv.put(Column.INTERVIEW_DTL_ID, pid.getInterviewDtlId());
                        cv.put(Column.INTERVIEW_ID, pid.getInterviewId());
                        cv.put(Column.Q_ID, pid.getqId());
                        cv.put(Column.ANSWER, pid.getAnswer());
                        cv.put(Column.TRANS_REF, pid.getTransRef());
                        db.replace(PatientInterviewDetail.MODEL_NAME, null, cv);
                        cv.clear();
                    }
                }
            }

        }

    }

    public void savePatientInterviewDetail(ArrayList<PatientInterviewDetail> patientInterviewDetails, long transRef) {
        if (patientInterviewDetails != null && !patientInterviewDetails.isEmpty()) {
            db.delete(PatientInterviewDetail.MODEL_NAME, "INTERVIEW_ID=" + patientInterviewDetails.get(0).getInterviewId(), null);
            ContentValues cv = new ContentValues();
            for (PatientInterviewDetail pid : patientInterviewDetails) {
                cv.put(Column.INTERVIEW_ID, pid.getInterviewId());
                cv.put(Column.Q_ID, pid.getqId());
                cv.put(Column.ANSWER, pid.getAnswer());
                cv.put(Column.TRANS_REF, transRef);
                db.replace(PatientInterviewDetail.MODEL_NAME, null, cv);
                cv.clear();
            }
        }
    }

    public long saveInterview(int questionnaireId, long parentInterviewId, long userId, String beneficiaryCode, int referralCenterId, long nextFollowupDate, String filePath, String jsonData, long interviewDate, long interviewStartTime, long interviewId, boolean needDoctorReferral, Beneficiary beneficiary, String recordDate) {
        long currentDateInMilli = interviewDate;
        ContentValues cv = new ContentValues();
        cv.put(Column.QUESTIONNAIRE_ID, questionnaireId);
        cv.put(Column.BENEFICIARY_BENEF_CODE, beneficiaryCode);
        cv.put(Column.USER_ID, userId);
        cv.put(Column.FILE_PATH, filePath);
        cv.put(Column.CREATE_DATE, currentDateInMilli);
        cv.put(Column.QUESTION_ANSWER_JSON, jsonData);
        cv.put(Column.RECORD_DATE, recordDate);

        if (needDoctorReferral) {
            cv.put(Column.PRIORITY, Priority.DOCTOR_FEEDBACK);
        }

        if (referralCenterId > 0) cv.put(Column.REF_CENTER_ID, referralCenterId);

        if (nextFollowupDate > 0) cv.put(Column.NEXT_FOLLOWUP_DATE, nextFollowupDate);


        if (beneficiary.getBenefName() != null && beneficiary.getBenefName().trim().length() > 0) {
            cv.put(Column.BENEF_NAME, beneficiary.getBenefName());
        }

        if (beneficiary.getAddress() != null && beneficiary.getAddress().trim().length() > 0) {
            cv.put(Column.BENEF_ADDRESS, beneficiary.getAddress());
        }
        if (beneficiary.getGender() != null && beneficiary.getGender().trim().length() > 0) {
            cv.put(Column.GENDER, beneficiary.getGender());
        }
        try {
            if (beneficiary.getAgeInDay().length() > 0) {
                cv.put(Column.AGE_IN_DAY, beneficiary.getAgeInDay());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (parentInterviewId > 0) {
            cv.put(Column.PARENT_INTERVIEW_ID, parentInterviewId);
            ScheduleInfo scheduleInfo = App.getContext().getDB().getScheduleInfo(parentInterviewId, questionnaireId);
            if (scheduleInfo != null) {

                ContentValues cvUserSched = new ContentValues();
                cvUserSched.put("SCHED_STATUS", ScheduleStatus.ATTENDED);
                cvUserSched.put("ATTENDED_DATE", interviewDate);
                cvUserSched.put("DATA_SENT", "N");
                db.update("user_schedule", cvUserSched, "SCHED_ID=?", new String[]{Long.toString(scheduleInfo.getScheduleId())});
            }
        }


        if (interviewId > 0) {
            db.update("patient_interview_master", cv, Column.INTERVIEW_ID + "=?", new String[]{Long.toString(interviewId)});
            return interviewId;
        }


        int n = db.update("patient_interview_master", cv, Column.TRANS_REF + "=?", new String[]{Long.toString(interviewStartTime)});

        if (n == 0) {
            cv.put("TRANS_REF", interviewStartTime);
            cv.put(Column.START_TIME, interviewStartTime);
            long id = db.insert("patient_interview_master", null, cv);
            return id;
        } else {
            String sql = " SELECT " + Column.INTERVIEW_ID + " FROM patient_interview_master WHERE " + Column.TRANS_REF + "=?";
            Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(interviewStartTime)});

            long inId = -1;
            if (cursor.moveToFirst())
                inId = cursor.getLong(cursor.getColumnIndex(Column.INTERVIEW_ID));
            cursor.close();

            return inId;

        }

    }

    /**
     * Update the interview whether it is sent or not.
     *
     * @param interviewId       is the interview ID
     * @param parentInterviewId the parent interview id
     * @param sent              is the sent string (Y or N)
     */
    public void updateInterviewSent(long interviewId, long parentInterviewId, String sent, long affectedRow) {
        ContentValues cv = new ContentValues();
        cv.put("DATA_SENT", sent);
        if (affectedRow > 0) {
            // cv.put(DBTableColumnTitle.INTERVIEW_ID, affectedRow);
        }
        db.update("patient_interview_master", cv, Column.INTERVIEW_ID + "=?", new String[]{Long.toString(interviewId)});
        cv.clear();

        if (parentInterviewId > 0) {
            cv.put("DATA_SENT", sent);
            db.update("user_schedule", cv, Column.INTERVIEW_ID + "=?", new String[]{Long.toString(parentInterviewId)});
        }
    }

    public void updateInterviewSentService(long interviewId, long parentInterviewId, String sent, long affectedRow) {
        ContentValues cv = new ContentValues();
        cv.put("DATA_SENT", sent);
        cv.put("UPDATED_ON", Calendar.getInstance().getTimeInMillis());
        if (affectedRow > 0) {
            // cv.put(DBTableColumnTitle.INTERVIEW_ID, affectedRow);
        }
        db.update("patient_interview_master", cv, Column.INTERVIEW_ID + "=?", new String[]{Long.toString(interviewId)});
        cv.clear();

        if (parentInterviewId > 0) {
            cv.put("DATA_SENT", sent);
            db.update("user_schedule", cv, Column.INTERVIEW_ID + "=?", new String[]{Long.toString(parentInterviewId)});
        }
    }

    public void updateInterviewMasterFcmInterviewId(long interviewId) {
        ContentValues cv = new ContentValues();
        cv.put("FCM_INTERVIEW_ID", interviewId);

        db.update("patient_interview_master", cv, Column.INTERVIEW_ID + "=?", new String[]{Long.toString(interviewId)});
        cv.clear();
    }

    public void saveFileBank(String name, String path, long transRef, long isSend) {
        ContentValues cv = new ContentValues();
        cv.put("NAME", name);
        cv.put("PATH", path);
        cv.put("IS_SEND", isSend);
        cv.put("TRANS_REF", transRef);

        String whereCondition = " NAME='" + name + "' TRANS_REF='" + transRef + "' ";
        if (isExist("file_bank", whereCondition)) {
            db.update("file_bank", cv, whereCondition, null);
            Log.e("CV", cv.toString());
        } else {
            db.insert("file_bank", null, cv);
        }
        cv.clear();
    }

    /**
     * Update the beneficiary name in local language.
     *
     * @param benefCode is the beneficiary code
     * @param localName is the beneficiary name in local language
     */
    public void updateBenefLocalName(String benefCode, String localName) {

        // / Update BENEF_NAME_LOCAL column
        String sql = "UPDATE beneficiary SET BENEF_NAME_LOCAL='" + localName + "' WHERE BENEF_NAME IN (SELECT BENEF_NAME FROM beneficiary WHERE BENEF_CODE='" + benefCode + "')";
        db.execSQL(sql);

        // / Update GUARDIAN_NAME_LOCAL column
        sql = "UPDATE beneficiary SET GUARDIAN_NAME_LOCAL='" + localName + "' WHERE GUARDIAN_NAME IN (SELECT BENEF_NAME FROM beneficiary WHERE BENEF_CODE='" + benefCode + "')";
        db.execSQL(sql);

    }

    public void updateBeneficiaryState(String benefCode, int state) {
        // / Update BENEF_NAME_LOCAL column
        String sql = " UPDATE beneficiary SET STATE=" + state + " WHERE BENEF_CODE='" + benefCode + "'";
        db.execSQL(sql);

    }

    public void saveDeathRegistration(ContentValues cv) {

        db.replace(DBTable.DEATH_REGISTRATION, null, cv);

    }

    public void updateBeneficiaryHouseholdNumberAndCode(String oldBenefCode, String newBenefCode, String houseHoldNumber) {
        // / Update BENEF_NAME_LOCAL column
        String sql = " UPDATE beneficiary SET  STATE=1 , HH_NUMBER='" + houseHoldNumber + "' , BENEF_CODE='" + newBenefCode + "'  WHERE BENEF_CODE='" + oldBenefCode + "'";
        db.execSQL(sql);

    }

    public void updateBenefeciaryMaretalStatus(String benefCode, String maretalStatus) {

        String sql = "UPDATE beneficiary SET MARITAL_STATUS='" + maretalStatus + "' WHERE BENEF_CODE='" + benefCode + "' ";
        db.execSQL(sql);
    }

    /**
     * Update the guardian name in local language.
     *
     * @param benefCode is the beneficiary code
     * @param localName is the beneficiary name in local language
     */
    public void updateGuardianLocalName(String benefCode, String localName) {

        // / Update GUARDIAN_NAME_LOCAL column
        String sql = "UPDATE beneficiary SET GUARDIAN_NAME_LOCAL='" + localName + "' WHERE GUARDIAN_NAME IN (SELECT GUARDIAN_NAME FROM beneficiary WHERE BENEF_CODE='" + benefCode + "')";
        db.execSQL(sql);

        // / Update BENEF_NAME_LOCAL column
        sql = "UPDATE beneficiary SET BENEF_NAME_LOCAL='" + localName + "' WHERE BENEF_NAME IN (SELECT GUARDIAN_NAME FROM beneficiary WHERE BENEF_CODE='" + benefCode + "')";
        db.execSQL(sql);
    }

    /**
     * Reduce the medicine stock table .
     *
     * @param prescription    is the list of medicine which represent the prescription
     * @param interviewId     is the interview id
     * @param userId          is the user id
     * @param beneficiaryCode is the beneficiary Code
     */
    public void consumeMedicine(ArrayList<String> prescription, long interviewId, long userId, String beneficiaryCode, long interviewStartTime/*
     * , int
     * locationId
     */) {

        if (prescription == null) // / Questionnaire doesn't suggest any
        // prescription
        {

            String sqlConsumpId = "SELECT " + Column.MED_CONSUMP_ID + " FROM medicine_consumption_master WHERE " + Column.INTERVIEW_ID + "=?";
            Cursor cursorConsumpId = db.rawQuery(sqlConsumpId, new String[]{Long.toString(interviewId)});

            if (cursorConsumpId.moveToFirst()) {
                long consumpId = cursorConsumpId.getLong(cursorConsumpId.getColumnIndex(Column.MED_CONSUMP_ID));

                deleteOldDataFromConsumpDetailTable(consumpId, userId, interviewStartTime/*
                 * ,
                 * locationId
                 */);

                db.delete("medicine_consumption_master", Column.INTERVIEW_ID + "=?", new String[]{Long.toString(interviewId)});
            }
            cursorConsumpId.close();

            // update prescription flag patient interview master set 0
            updatePatientInterviewMaster("PRESC_ID", 0 + "", interviewId);

            return;
        }

        ArrayList<MedicineInfo> medList = new ArrayList<MedicineInfo>();

        StringBuilder sbIn = new StringBuilder();
        StringBuilder sbUnion = new StringBuilder();

        // / Parse prescription and get med id and qty and build IN and UNION
        // SQL
        for (int i = 0; i < prescription.size(); i++) {
            MedicineInfo medicineInfo = new MedicineInfo();
            try {
                JSONObject jsonObject = new JSONObject(prescription.get(i));
                medicineInfo.setMedId(Integer.parseInt(jsonObject.getString("MED_ID")));
                medicineInfo.setSoldQuantity(Integer.parseInt(jsonObject.getString("SALE_QTY")) + "");
                medList.add(medicineInfo);
            } catch (Exception exception) {

            }
            if (i > 0) sbIn.append(",");
            sbIn.append(medicineInfo.getMedId());

            sbUnion.append(" UNION SELECT " + medicineInfo.getMedId() + ",0");
        }

        // // SQL to retrieve Medicine sales price
        String sql = "SELECT medTable.MEDICINE_ID MEDICINE_ID, max(medTable.UNIT_SALES_PRICE) as UNIT_SALES_PRICE " + "FROM (" + "SELECT MEDICINE_ID, UNIT_SALES_PRICE " + "FROM medicine " + "WHERE MEDICINE_ID IN (" + sbIn.toString() + ") " + sbUnion.toString() + " )medTable " + "GROUP BY MEDICINE_ID";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        // // Retrieve sales price and store it into Map by MEDICINE_ID key
        Map<Long, Double> salesPriceList = new HashMap<Long, Double>();
        if (cursor.moveToFirst()) {
            do {
                salesPriceList.put(cursor.getLong(cursor.getColumnIndex("MEDICINE_ID")), cursor.getDouble(cursor.getColumnIndex("UNIT_SALES_PRICE")));
            } while (cursor.moveToNext());
        }
        cursor.close();

        // // Calculate single medicine's total price and prescription's total
        // price
        double prescriptionTotalPrice = 0;
        for (int i = 0; i < medList.size(); i++) {

            Double salesPrice = salesPriceList.get(medList.get(i).getMedId());
            Log.e("SQL", sql.toString());
            if (salesPrice == null) {

                continue;
            }

            medList.get(i).setUnitSalesPrice(salesPrice);
            double medTotalPrice = Utility.parseInt(medList.get(i).getSoldQuantity()) * salesPrice;

            medList.get(i).setTotalPrice(medTotalPrice);

            prescriptionTotalPrice += medTotalPrice;

        }

        // /// Prepare data from UPSERT into medicine_consumption_master table
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        ContentValues cv = new ContentValues();

        cv.put(Column.INTERVIEW_ID, interviewId);
        cv.put(Column.BENEFICIARY_BENEF_CODE, beneficiaryCode);
        cv.put(Column.USER_ID, userId);
        cv.put(Column.LOCATION_ID, App.getContext().getUserInfo().getLocationId());
        cv.put(Column.TOTAL_PRICE, prescriptionTotalPrice);
        cv.put(Column.CONSUMP_DATE, currentTimeInMillis);
        cv.put(Column.REMARKS, "Patient interview");
        cv.put(Column.DATA_SENT, "N");

        int numberOfRowsAffected = db.update("medicine_consumption_master", cv, Column.INTERVIEW_ID + "=?", new String[]{Long.toString(interviewId)});

        long consumpId = -1;
        if (numberOfRowsAffected == 0) {
            // // Row is not present in table. So insert it and get the
            // MED_CONSUMP_ID
            consumpId = db.insert("medicine_consumption_master", null, cv);
        } else {
            // // Row updated, so get the MED_CONSUM_ID
            String sqlConsumpId = "SELECT " + Column.MED_CONSUMP_ID + " FROM medicine_consumption_master WHERE " + Column.INTERVIEW_ID + "=?";
            Cursor cursorConsumpId = db.rawQuery(sqlConsumpId, new String[]{Long.toString(interviewId)});

            if (cursorConsumpId.moveToFirst()) {
                consumpId = cursorConsumpId.getLong(cursorConsumpId.getColumnIndex(Column.MED_CONSUMP_ID));

            }
            cursorConsumpId.close();
        }
        deleteOldDataFromConsumpDetailTable(consumpId, userId, interviewStartTime/* , locationId */);

        for (int i = 0; i < medList.size(); i++) {


            if (Utility.parseInt(medList.get(i).getSoldQuantity()) <= 0) continue;
            /* Prepare data to insert into medicine_consumption_detail table */
            ContentValues cvMedDetail = new ContentValues();
            cvMedDetail.put(Column.MED_CONSUMP_ID, consumpId);
            cvMedDetail.put(Column.MEDICINE_ID, medList.get(i).getMedId());
            cvMedDetail.put(Column.QTY, medList.get(i).getSoldQuantity());
            cvMedDetail.put(Column.PRICE, medList.get(i).getTotalPrice());

            db.insert("medicine_consumption_detail", null, cvMedDetail);

            // / Update Stock table
            updateStockTable(medList.get(i).getMedId(), userId/* , locationId */, -Utility.parseInt(medList.get(i).getSoldQuantity()), interviewStartTime);
        }

        // update prescription flag patient interview master set 1
        updatePatientInterviewMaster("PRESC_ID", 1 + "", interviewId);
    }

    public void updatePatientInterviewMaster(String colomnName, String value, long interviewId) {

        ContentValues cvPatienInterviewMaster = new ContentValues();
        cvPatienInterviewMaster.put(colomnName, value);
        db.update(DBTable.PATIENT_INTERVIEW_MASTER, cvPatienInterviewMaster, "INTERVIEW_ID=?", new String[]{Long.toString(interviewId)});
        cvPatienInterviewMaster.clear();
    }

    /**
     * Update medicine consumption status.
     *
     * @param interviewId the interview id
     * @param consumpId   the consump id
     * @param status      the status
     */
    public void updateMedicineConsumptionStatus(long interviewId, long consumpId, String status) {

        ContentValues cv = new ContentValues();
        cv.put(Column.DATA_SENT, status);
        if (interviewId > 0) {
            db.update("medicine_consumption_master", cv, Column.INTERVIEW_ID + "=?", new String[]{Long.toString(interviewId)});
        } else {
            db.update("medicine_consumption_master", cv, Column.MED_CONSUMP_ID + "=?", new String[]{Long.toString(consumpId)});
        }
    }

    /**
     * Delete row from medicine_consumption_detail table and update stock table
     * accordingly.
     *
     * @param medConsupmId is the medicine consumption ID
     * @param userId       is the FCM ID
     */
    private void deleteOldDataFromConsumpDetailTable(long medConsupmId, long userId, long interviewStartTime) {
        String sql = TextUtility.format("SELECT %s,%s FROM medicine_consumption_detail WHERE %s=?", Column.MEDICINE_ID, Column.QTY, Column.MED_CONSUMP_ID);
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(medConsupmId)});

        if (cursor.moveToFirst()) {
            do {
                long medicineId = cursor.getLong(cursor.getColumnIndex(Column.MEDICINE_ID));
                long medicineQty = cursor.getLong(cursor.getColumnIndex(Column.QTY));

                // / Row from medicine_consumption_detail table will be deleted,
                // so update the stock table
                updateStockTable(medicineId, userId/* , locationId */, medicineQty, interviewStartTime);

            } while (cursor.moveToNext());
        }
        cursor.close();

        // // Delete row from medicine_consumption_detail table based on
        // MED_CONSUMP_ID
        db.delete("medicine_consumption_detail", Column.MED_CONSUMP_ID + "=?", new String[]{Long.toString(medConsupmId)});

    }


    /**
     * Update Stock table with new medicine quantity.
     *
     * @param medicineId  is the medicine ID
     * @param userId      is the FCM ID
     * @param medicineQty is the number of medicine which will be added(if quantity is
     *                    positive)/deducted(if quantity is negative) from current
     *                    medicine quantity.
     */


    public void updateStockTable(long medicineId, long userId, long medicineQty, long updateTimeMili) {
        String updateSql = " UPDATE medicine_stock SET  LAST_UPDATE_ON =" + updateTimeMili + ",CURRENT_STOCK_QTY =CURRENT_STOCK_QTY+" + medicineQty + " WHERE " + Column.MEDICINE_ID + "=" + medicineId + " AND " + Column.USER_ID + "=" + userId;
        db.execSQL(updateSql);
    }


    public void addStockTable(long medicineId, long userId, long medicineQty, long updateTimeMili) {
        ContentValues cv = new ContentValues();
        cv.put("USER_ID", userId);
        cv.put("MEDICINE_ID", medicineId);
        cv.put("CURRENT_STOCK_QTY", medicineQty);
        cv.put("LAST_UPDATE_ON", updateTimeMili);
        db.insert("medicine_stock", null, cv);
    }

    /**
     * Save the newly registered beneficiary or update the value of previously
     * registered beneficiary.
     *
     * @param beneficiaryId  the beneficiary id
     * @param beneficiary    is the beneficiary object which contains all the necessary
     *                       data
     * @param familyHeadCode is code of family head
     */
    public long saveBeneficiary(long beneficiaryId, Beneficiary beneficiary, String familyHeadCode) {
        if (familyHeadCode != null && beneficiary.getFamilyHead() == 1) {
            ContentValues cvFmCode = new ContentValues();
            cvFmCode.put(Column.BENEFICIARY_FAMILY_HEAD, 0);
            db.update("beneficiary", cvFmCode, Column.HH_NUMBER + "=?", new String[]{familyHeadCode.substring(0, 12)});
        }

        ContentValues cv = new ContentValues();
        cv.put(Column.BENEFICIARY_BENEF_CODE, beneficiary.getBenefCode());

        if (beneficiary.getBenefImagePath() != null) {
            cv.put(Column.BENEFICIARY_BENEF_IMAGE_PATH, beneficiary.getBenefImagePath());
        }

        cv.put(Column.BENEFICIARY_BENEF_NAME, beneficiary.getBenefName());
        if (beneficiary.getBenefLocalName() != null)
            cv.put(Column.BENEFICIARY_BENEF_NAME_LOCAL, beneficiary.getBenefLocalName());
        cv.put(Column.BENEFICIARY_CREATE_DATE, Constants.DATE_FORMAT_YYYY_MM_DD.format(Calendar.getInstance().getTime()));
        cv.put(Column.BENEFICIARY_DOB, beneficiary.getDob());
        cv.put(Column.BENEFICIARY_EDU_LEVEL, beneficiary.getEduLevel());
        cv.put(Column.USER_ID, beneficiary.getUserId());
        cv.put(Column.BENEFICIARY_FAMILY_HEAD, beneficiary.getFamilyHead());
        cv.put(Column.BENEFICIARY_GENDER, beneficiary.getGender());
        cv.put(Column.BENEFICIARY_HH_ID, beneficiary.getHhId());
        cv.put(Column.BENEFICIARY_MOBILE_COMM, beneficiary.getMobileComm());
        cv.put(Column.BENEFICIARY_MOBILE_NUMBER, beneficiary.getMobileNumber());
        cv.put(Column.BENEFICIARY_RELATION_GUARDIAN, beneficiary.getRelationToGurdian());
        cv.put(Column.GUARDIAN_NAME, beneficiary.getGuardianName());
        cv.put(Column.GUARDIAN_NAME_LOCAL, beneficiary.getGuardianLocalName());
        cv.put(Column.BENEFICIARY_REF_DATA_ID, beneficiary.getRefDataId());
        cv.put(Column.HH_NUMBER, beneficiary.getHhNumber());
        cv.put(Column.OCCUPATION, beneficiary.getOccupation());
        cv.put(Column.OCCUPATION_HER_HUSBAND, beneficiary.getOccupationHerHusband());
        cv.put(Column.AGREED_MOBILE_COMM, beneficiary.getAgreedMobileComm());
        cv.put(Column.MOBILE_COMM_LANG, beneficiary.getMobileCommLang());
        cv.put(Column.MARITAL_STATUS, beneficiary.getMaritalStatus());
        cv.put(Column.RELIGION, beneficiary.getReligion());
        cv.put(Column.RELIGION_OTHER_SPECIFIC, beneficiary.getReligionOtherSpecofic());
        cv.put(Column.STATE, beneficiary.getState());
        cv.put(Column.NATIONAL_ID, beneficiary.getNationalIdNumber());
        cv.put(Column.BIRTH_REG_NUMBER, beneficiary.getBirthCertificateNumber());
        cv.put(Column.ADDRESS, beneficiary.getAddress());


        long count = db.update("beneficiary", cv, Column.BENEFICIARY_BENEF_ID + "=?", new String[]{Long.toString(beneficiaryId)});

        if (count == 0) {
            try {
                Log.e("CV", cv.toString());
                long bid = db.insert("beneficiary", null, cv);
                return bid;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        } else {
            return beneficiaryId;
        }
    }


    /**
     * Get the code of the head of house.
     *
     * @param householNumber is the household number
     * @param benefCode      is the beneficiary code
     * @return The Family head code. null if there is no head in the family or
     * the code is equal to benefCode
     */
    public String getFamilyHeadCodeIfExist(String householNumber, String benefCode) {
        String familyHeadCode = null;
        if (householNumber != null && benefCode != null) {
            String sql = "SELECT BENEF_CODE FROM beneficiary WHERE HH_NUMBER =? AND FAMILY_HEAD =? AND STATE=1 AND BENEF_CODE !=?";
            Cursor cursor = db.rawQuery(sql, new String[]{householNumber, "1", benefCode});
            if (cursor.moveToFirst()) {
                familyHeadCode = cursor.getString(cursor.getColumnIndex("BENEF_CODE"));
            }
        }
        return familyHeadCode;
    }

    /**
     * Save the list of beneficiaries.
     *
     * @param beneficiaryList is the beneficiary list
     */
    public void saveBeneficiaryList(ArrayList<Beneficiary> beneficiaryList, JSONObject param) {
        if (beneficiaryList == null) return;
        long count = 0;
        for (Beneficiary beneficiary : beneficiaryList) {

            ContentValues cv = new ContentValues();
            cv.put(Column.USER_ID, beneficiary.getUserId());
            cv.put(Column.BENEFICIARY_BENEF_CODE, beneficiary.getBenefCode());
            cv.put(Column.BENEFICIARY_BENEF_IMAGE_PATH, beneficiary.getBenefImagePath());
            cv.put(Column.BENEFICIARY_BENEF_NAME, beneficiary.getBenefName());
            cv.put(Column.BENEFICIARY_BENEF_NAME_LOCAL, beneficiary.getBenefLocalName());
            cv.put(Column.BENEFICIARY_CREATE_DATE, beneficiary.getCreateDate());
            cv.put(Column.BENEFICIARY_DOB, beneficiary.getDob());
            cv.put(Column.BENEFICIARY_EDU_LEVEL, beneficiary.getEduLevel());
            cv.put(Column.BENEFICIARY_FAMILY_HEAD, beneficiary.getFamilyHead());
            cv.put(Column.BENEFICIARY_GENDER, beneficiary.getGender());
            cv.put(Column.BENEFICIARY_HH_ID, beneficiary.getHhId());
            cv.put(Column.BENEFICIARY_MOBILE_COMM, beneficiary.getMobileComm());
            cv.put(Column.BENEFICIARY_MOBILE_NUMBER, beneficiary.getMobileNumber());
            cv.put(Column.MARITAL_STATUS, beneficiary.getMaritalStatus());
            cv.put(Column.OCCUPATION, beneficiary.getOccupation());
            cv.put(Column.OCCUPATION_HER_HUSBAND, beneficiary.getOccupationHerHusband());
            cv.put(Column.NATIONAL_ID, beneficiary.getNationalIdNumber());
            cv.put(Column.RELIGION, beneficiary.getReligion());
            cv.put(Column.RELIGION_OTHER_SPECIFIC, beneficiary.getReligionOtherSpecofic());
            cv.put(Column.AGREED_MOBILE_COMM, beneficiary.getAgreedMobileComm());
            cv.put(Column.MOBILE_COMM_LANG, beneficiary.getMobileCommLang());
            cv.put(Column.BIRTH_REG_NUMBER, beneficiary.getBirthCertificateNumber());
            cv.put(Column.BENEFICIARY_RELATION_GUARDIAN, beneficiary.getRelationToGurdian());
            cv.put(Column.GUARDIAN_NAME, beneficiary.getGuardianName());
            cv.put(Column.GUARDIAN_NAME_LOCAL, beneficiary.getGuardianLocalName());
            cv.put(Column.BENEFICIARY_REF_DATA_ID, beneficiary.getRefDataId());
            cv.put(Column.HH_NUMBER, beneficiary.getHhNumber());
            cv.put(Column.FILE_KEY, beneficiary.getFileKey());
            cv.put(Column.BENEFICIARY_BENEF_IMAGE_PATH, beneficiary.getBenefImagePath());
            cv.put(Column.ADDRESS, beneficiary.getAddress());
            cv.put("STATE", beneficiary.getState());
            int affectedRow = db.update(DBTable.BENEFICIARY, cv, Column.BENEFICIARY_BENEF_CODE + "=?", new String[]{beneficiary.getBenefCode()});
            if (affectedRow == 0) {
                db.insert(DBTable.BENEFICIARY, null, cv);
            }

            count++;
        }

        //  updateDataVersion(DBTable.BENEFICIARY, beneficiaryList.size(), count, param, KEY.VERSION_NO_BENEFICIARY);
    }


    public void saveDignosisInfo(ArrayList<DiagnosisInfo> rows, JSONObject param) {

        ContentValues cv = new ContentValues();
        long count = 0;
        try {
            db.beginTransaction();
            for (DiagnosisInfo info : rows) {
                cv.put(Column.DIAG_ID, info.getDiagId());
                cv.put(Column.DIAG_NAME, info.getDiagName());
                cv.put(Column.CODE_ICD10, info.getCodeIcd10());
                cv.put(Column.CODE_SNOMED, info.getCodeSnomed());
                cv.put(Column.STATE, info.getState());
                db.replace(DBTable.DIAGNOSIS_INFO, null, cv);
                cv.clear();
                count++;
            }
            updateDataVersion(DBTable.DIAGNOSIS_INFO, rows.size(), count, param, KEY.VERSION_NO_DIAGNOSIS_INFO);
            db.setTransactionSuccessful();
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    /**
     * Get beneficiary name list in eng_name(local_name) format.
     *
     * @return The name list
     */
    public ArrayList<String> getNameList(String type) {

        StringBuilder sb = new StringBuilder();

        if (KEY.NAME.toUpperCase().equalsIgnoreCase(type)) {
            sb.append(" SELECT b.benef_name ||case when length(trim(b.benef_name_local))>0 then  ' ('||b.benef_name_local||')' ELSE '' END text ");
            sb.append(" FROM (SELECT benef_name, benef_name_local  ");
            sb.append(" FROM beneficiary where substr( benef_name, 0,8 )!='Baby of'  ");
            sb.append(" UNION SELECT eng_name, local_name FROM suggestion_text where type='" + type + "'  AND STATE=1   ");
            sb.append(" UNION SELECT guardian_name, guardian_name_local FROM beneficiary where substr( guardian_name, 0,8 )!='Baby of' ) b  ");
            sb.append(" group by b.benef_name ");
        } else {
            sb.append(" SELECT eng_name text FROM suggestion_text where type='" + type + "' AND STATE=1  ");

        }


        Log.e("SQL", sb.toString());
        Cursor cursor = db.rawQuery(sb.toString(), null);

        ArrayList<String> nameList = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                String text = cursor.getString(cursor.getColumnIndex("text"));
                if (text.trim().length() > 0) {
                    nameList.add(text.trim());
                }
            } while (cursor.moveToNext());
        }

        return nameList;
    }

    /**
     * Save referral center list.
     *
     * @param referralCenterList
     *            is the referral center list
     */


    /**
     * Get all referral centers.
     *
     * @return The referral center list along with their details
     */
    public ArrayList<ReferralCenterInfo> getReferralCenterList(String whereCondition) {
        String language = App.getContext().getAppSettings().getLanguage();
        ArrayList<ReferralCenterInfo> referralCenterList = new ArrayList<ReferralCenterInfo>();

        if (whereCondition != null && whereCondition.trim().length() > 0) {
            whereCondition = " AND " + whereCondition;
        } else {
            whereCondition = "";
        }

        String sqlTamp = "SELECT  " + " rc.REF_CENTER_ID REF_CENTER_ID, " + " rc.REF_CENTER_NAME_CAPTION REF_CENTER_NAME_CAPTION ," + " rc.ADDRESS ADDRESS , rc.DESCRIPTION DESCRIPTION, " + " rc.CONTACT_NAME CONTACT_NAME,  rc.CONTACT_NUMBER CONTACT_NUMBER , " + " rcca.DISTANCE DISTANCE, rcca.TIME_NEED TIME_NEED, rcca.TRANSPORT_WAY TRANSPORT_WAY " + " FROM (select * from referral_center where LANG_CODE='" + language + "' %s) AS rc  " + " left join referral_center_char_assignment AS rcca  " + " ON rc.REF_CENTER_ID=rcca.REF_CENTER_ID ";

        String sql = TextUtility.format(sqlTamp, whereCondition);

        Cursor cursor = db.rawQuery(sql, null);
        Log.e("SQL", sql.toString());
        if (cursor.moveToFirst()) {
            do {
                ReferralCenterInfo referralCenterInfo = new ReferralCenterInfo();
                referralCenterInfo.setCaptionName(cursor.getString(cursor.getColumnIndex("REF_CENTER_NAME_CAPTION")));
                referralCenterInfo.setContactName(cursor.getString(cursor.getColumnIndex("CONTACT_NAME")));
                referralCenterInfo.setContactNumber(cursor.getString(cursor.getColumnIndex("CONTACT_NUMBER")));
                referralCenterInfo.setDescription(cursor.getString(cursor.getColumnIndex("DESCRIPTION")));
                referralCenterInfo.setDistance(cursor.getInt(cursor.getColumnIndex("DISTANCE")));
                referralCenterInfo.setID(cursor.getInt(cursor.getColumnIndex("REF_CENTER_ID")));
                referralCenterInfo.setTimeNeed(cursor.getInt(cursor.getColumnIndex("TIME_NEED")));
                referralCenterInfo.setTransportWay(cursor.getString(cursor.getColumnIndex("TRANSPORT_WAY")));
                referralCenterInfo.setAddress(cursor.getString(cursor.getColumnIndex("ADDRESS")));
                referralCenterList.add(referralCenterInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return referralCenterList;
    }


    public void saveMedicineList(ArrayList<MedicineInfo> rows, JSONObject param) {

        long userId = App.getContext().getUserInfo().getUserId();
        long count = 0;
        for (MedicineInfo medicineInfo : rows) {

            ContentValues cv = new ContentValues();
            cv.put(Column.MEDICINE_ID, medicineInfo.getMedId());
            cv.put(Column.MEDICINE_TYPE, medicineInfo.getMedicineType());
            cv.put(Column.GENERIC_NAME, medicineInfo.getGenericName());
            cv.put(Column.BRAND_NAME, medicineInfo.getBrandName());
            cv.put(Column.STRENGTH, medicineInfo.getStrength());

            cv.put(Column.MEASURE_UNIT, medicineInfo.getMeasureUnit());
            cv.put(Column.UNIT_SALES_PRICE, medicineInfo.getUnitSalesPrice());
            cv.put(Column.UNIT_PURCHASE_PRICE, medicineInfo.getUnitPurchasePrice());

            cv.put("STATE", medicineInfo.getState());
            cv.put("BOX_SIZE", medicineInfo.getBoxSize());
            cv.put("ACCESS_TYPE", medicineInfo.getAccessType());
            cv.put("UNIT_TYPE", medicineInfo.getUnitType());

            String whereCon = " MEDICINE_ID=" + medicineInfo.getMedId();

            if (isExist(DBTable.MEDICINE, whereCon)) {
                db.update(DBTable.MEDICINE, cv, whereCon, null);
            } else {
                db.insert(DBTable.MEDICINE, null, cv);
            }


            cv = new ContentValues();
            cv.put(Column.MEDICINE_ID, medicineInfo.getMedId());
            cv.put(Column.USER_ID, userId);
            cv.put(Column.LOCATION_ID, userId);
            cv.put(Column.MIN_QUANTITY, medicineInfo.getMinimumStockQuantity());


            whereCon = " MEDICINE_ID=" + medicineInfo.getMedId() + " AND USER_ID=" + userId;
            if (isExist("medicine_minimum_bar", whereCon)) {
                db.update("medicine_minimum_bar", cv, whereCon, null);
            } else {
                db.insert("medicine_minimum_bar", null, cv);
            }

            count++;
        }

        updateDataVersion(DBTable.MEDICINE, rows.size(), count, param, KEY.VERSION_NO_MEDICINE);

    }


    public void saveMedicineStock(JSONArray rows) {
        if (rows == null) return;
        ContentValues cv = new ContentValues();
        JSONObject row;
        long count = 0;
        for (int i = 0; i < rows.length(); i++) {
            try {
                row = rows.getJSONObject(i);
                long locationId = row.getLong("LOCATION_ID");
                long userId = row.getLong("USER_ID");
                long medeicineId = row.getLong("MEDICINE_ID");
                cv.put("LOCATION_ID", locationId);
                cv.put("USER_ID", userId);
                cv.put("MEDICINE_ID", medeicineId);
                cv.put("CURRENT_STOCK_QTY", row.getDouble("CURRENT_STOCK_QTY"));
                cv.put("LAST_UPDATE_ON", row.getDouble("LAST_UPDATED_ON"));
                String whereCon = "LOCATION_ID=" + locationId + " AND USER_ID=" + userId + " AND MEDICINE_ID=" + medeicineId;
                if (isExist("medicine_stock", whereCon)) {
                    db.update("medicine_stock", cv, whereCon, null);
                } else {
                    db.insert("medicine_stock", null, cv);
                }
                cv.clear();
                count++;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public void saveGrowthMeasurementParam(JSONArray rows, JSONObject param) {
        if (rows == null) return;
        ContentValues cv = new ContentValues();
        JSONObject row;
        long count = 0;
        for (int i = 0; i < rows.length(); i++) {
            try {
                row = rows.getJSONObject(i);
                cv.put("AGE_IN_MONTH", row.getDouble("AGE_IN_MONTH"));
                cv.put("MEASURE_TYPE", row.getString("MEASURE_TYPE"));
                cv.put("GENDER", row.getString("GENDER"));
                cv.put("RANGE_START", row.getDouble("RANGE_START"));
                cv.put("RANGE_END", row.getDouble("RANGE_END"));
                cv.put("OUTPUT_RESULT", row.getString("OUTPUT_RESULT"));
                cv.put("IMPL_VERSION", row.getInt("IMPL_VERSION"));
                cv.put("ID", row.getString("ID"));
                db.replace(DBTable.GROWTH_MEASUREMENT_PARAM, null, cv);
                cv.clear();
                count++;
            } catch (JSONException e) {
            }

        }

        updateDataVersion(DBTable.GROWTH_MEASUREMENT_PARAM, rows.length(), count, param, KEY.VERSION_NO_GROWTH_MEASUREMENT_PARAM);
    }

    /**
     * Save FCM profle information.
     *
     * @param userInfo is the FCM profile information
     */
    public void saveUserInfo(UserInfo userInfo, JSONObject param) {
        if (userInfo == null) return;
        ContentValues cv = new ContentValues();
        cv.put(Column.USER_ID, userInfo.getUserId());
        cv.put(Column.USER_LOGIN_ID, userInfo.getUserCode());
        cv.put(Column.USER_NAME, userInfo.getUserName());
        cv.put(Column.TARGET_HH, userInfo.getTargetHh());
        cv.put(Column.LOCATION_NAME, userInfo.getLocationName());
        cv.put(Column.LOCATION_ID, userInfo.getLocationId());
        cv.put(Column.LOCATION_CODE, userInfo.getLocationCode());

        cv.put(Column.ORG_ID, userInfo.getOrgId());
        cv.put(Column.ORG_CODE, userInfo.getOrgCode());
        cv.put(Column.ORG_NAME, userInfo.getOrgName());
        cv.put(Column.ORG_DESC, userInfo.getOrgDesc());
        cv.put(Column.ORG_ADDRESS, userInfo.getOrgAddress());
        cv.put(Column.ORG_COUNTRY, userInfo.getOrgCountry());
        cv.put(Column.HEADER_SMALL_LOGO_PATH, userInfo.getHeaderSmallLogoPath());
        cv.put(Column.LOGIN_IMAGE_PATH_MOBILE, userInfo.getLoginImagePathMobile());
        cv.put(Column.TITLE_LOGO_PATH_MOBILE, userInfo.getTitleLogoPathMobile());
        cv.put(Column.APP_TITLE_MOBILE, userInfo.getAppTitleMobile());

        cv.put(Column.OTHER_DETAILS, userInfo.getOtherDetails());
        if (userInfo.getProfilePicInString() != null && userInfo.getProfilePicInString().length() > 100) {
            cv.put(Column.PROFILE_IMAGE, userInfo.getProfilePicInString());
        }
        long affectedRow = db.update("user_info", cv, Column.USER_LOGIN_ID + "=? AND ORG_ID=" + userInfo.getOrgId(), new String[]{userInfo.getUserCode()});
        if (affectedRow == 0) {
            db.delete(DBTable.USER_INFO, null, null);
            db.insert(DBTable.USER_INFO, null, cv);
        }

        if (param != null) {

            try {
                param.put(DBTable.USER_INFO, 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateDataVersion(DBTable.USER_INFO, 1, 1, param, KEY.VERSION_NO_USER_INFO);
        }

    }

    /**
     * Get FCM profile information.
     *
     * @return FCM profile information
     */
    public UserInfo getUserInfo(String userLoginId, String password, long orgId, long state) {
        UserInfo userInfo = null;

        String sql = "SELECT * FROM user_info where ORG_ID=" + orgId + " and USER_LOGIN_ID='" + userLoginId + "' ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            userInfo = new UserInfo();
            userInfo.setPassword(password);
            userInfo.setPassword(password);
            userInfo.setState(state);
            userInfo.setUserId(cursor.getLong(cursor.getColumnIndex(Column.USER_ID)));
            userInfo.setUserCode(cursor.getString(cursor.getColumnIndex(Column.USER_LOGIN_ID)));
            userInfo.setUserName(cursor.getString(cursor.getColumnIndex(Column.USER_NAME)));
            userInfo.setOtherDetails(cursor.getString(cursor.getColumnIndex(Column.OTHER_DETAILS)));
            userInfo.setTargetHh(cursor.getLong(cursor.getColumnIndex(Column.TARGET_HH)));
            userInfo.setLocationId(cursor.getLong(cursor.getColumnIndex(Column.LOCATION_ID)));
            userInfo.setLocationCode(cursor.getString(cursor.getColumnIndex(Column.LOCATION_CODE)));
            userInfo.setLocationName(cursor.getString(cursor.getColumnIndex(Column.LOCATION_NAME)));
            userInfo.setProfilePicInString(cursor.getString(cursor.getColumnIndex(Column.PROFILE_IMAGE)));
            userInfo.setOrgCode(cursor.getString(cursor.getColumnIndex(Column.ORG_CODE)));
            userInfo.setOrgId(cursor.getLong(cursor.getColumnIndex(Column.ORG_ID)));
            userInfo.setOrgName(cursor.getString(cursor.getColumnIndex(Column.ORG_NAME)));
            userInfo.setOrgDesc(cursor.getString(cursor.getColumnIndex(Column.ORG_DESC)));
            userInfo.setOrgAddress(cursor.getString(cursor.getColumnIndex(Column.ORG_ADDRESS)));
            userInfo.setOrgCountry(cursor.getString(cursor.getColumnIndex(Column.ORG_COUNTRY)));
            userInfo.setHeaderSmallLogoPath(cursor.getString(cursor.getColumnIndex(Column.HEADER_SMALL_LOGO_PATH)));
            userInfo.setLoginImagePathMobile(cursor.getString(cursor.getColumnIndex(Column.LOGIN_IMAGE_PATH_MOBILE)));
            userInfo.setTitleLogoPathMobile(cursor.getString(cursor.getColumnIndex(Column.TITLE_LOGO_PATH_MOBILE)));
            userInfo.setAppTitleMobile(cursor.getString(cursor.getColumnIndex(Column.APP_TITLE_MOBILE)));
        }
        cursor.close();
        return userInfo;
    }

    public ArrayList<UserInfo> getAllUserInfo(long orgId, String loginId, long state, String filter) {
        ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();
        String sql = "";
        if (filter.length() > 0) {
            sql = "SELECT * FROM user_info where USER_LOGIN_ID !='" + loginId + "' and LOCATION_ID in(" + filter + ") and state =1 ORDER BY USER_NAME ASC";
        } else {
            sql = "SELECT * FROM user_info where USER_LOGIN_ID !='" + loginId + "' and state =1 ORDER BY USER_NAME ASC";
        }
        // String sql = "SELECT * FROM user_info where ORG_ID=" + orgId + "  ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                UserInfo userInfo = new UserInfo();
                userInfo = new UserInfo();
//                userInfo.setState(state);
                userInfo.setUserId(cursor.getLong(cursor.getColumnIndex(Column.USER_ID)));
                userInfo.setUserCode(cursor.getString(cursor.getColumnIndex(Column.USER_LOGIN_ID)));
                userInfo.setUserName(cursor.getString(cursor.getColumnIndex(Column.USER_NAME)));
//                userInfo.setOtherDetails(cursor.getString(cursor.getColumnIndex(Column.OTHER_DETAILS)));
//                userInfo.setTargetHh(cursor.getLong(cursor.getColumnIndex(Column.TARGET_HH)));
                userInfo.setLocationId(cursor.getInt(cursor.getColumnIndex(Column.LOCATION_ID)));
//                userInfo.setProfilePicInString(cursor.getString(cursor.getColumnIndex(Column.PROFILE_IMAGE)));
//                userInfo.setOrgCode(cursor.getString(cursor.getColumnIndex(Column.ORG_CODE)));
//                userInfo.setOrgId(cursor.getLong(cursor.getColumnIndex(Column.ORG_ID)));
//                userInfo.setOrgName(cursor.getString(cursor.getColumnIndex(Column.ORG_NAME)));
//                userInfo.setOrgDesc(cursor.getString(cursor.getColumnIndex(Column.ORG_DESC)));
//                userInfo.setOrgAddress(cursor.getString(cursor.getColumnIndex(Column.ORG_ADDRESS)));
//                userInfo.setOrgCountry(cursor.getString(cursor.getColumnIndex(Column.ORG_COUNTRY)));
//                userInfo.setHeaderSmallLogoPath(cursor.getString(cursor.getColumnIndex(Column.HEADER_SMALL_LOGO_PATH)));
//                userInfo.setLoginImagePathMobile(cursor.getString(cursor.getColumnIndex(Column.LOGIN_IMAGE_PATH_MOBILE)));
//                userInfo.setTitleLogoPathMobile(cursor.getString(cursor.getColumnIndex(Column.TITLE_LOGO_PATH_MOBILE)));
//                userInfo.setAppTitleMobile(cursor.getString(cursor.getColumnIndex(Column.APP_TITLE_MOBILE)));
                userInfoList.add(userInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return userInfoList;
    }

    public ArrayList<LocationModel> getLocationList() {
        ArrayList<LocationModel> userInfoList = new ArrayList<LocationModel>();

        String sql = "SELECT * FROM location";
        // String sql = "SELECT * FROM user_info where ORG_ID=" + orgId + "  ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                LocationModel userInfo = new LocationModel();
                userInfo.setLocationId(cursor.getLong(cursor.getColumnIndex(LocationModel.LOCATION_ID)));
                userInfo.setLocationCode(cursor.getString(cursor.getColumnIndex(LocationModel.LOCATION_CODE)));
                userInfo.setLocationName(cursor.getString(cursor.getColumnIndex(LocationModel.LOCATION_NAME)));
                userInfo.setParentLocationId(cursor.getInt(cursor.getColumnIndex(LocationModel.PARENT_LOCATION_ID)));
                userInfoList.add(userInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return userInfoList;
    }


    public ArrayList<LocationModel> getLocationByCode() {
        ArrayList<LocationModel> userInfoList = new ArrayList<LocationModel>();

        String sql = "SELECT * FROM location";
        // String sql = "SELECT * FROM user_info where ORG_ID=" + orgId + "  ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                LocationModel userInfo = new LocationModel();
                userInfo.setLocationId(cursor.getLong(cursor.getColumnIndex(LocationModel.LOCATION_ID)));
                userInfo.setLocationCode(cursor.getString(cursor.getColumnIndex(LocationModel.LOCATION_CODE)));
                userInfo.setLocationName(cursor.getString(cursor.getColumnIndex(LocationModel.LOCATION_NAME)));
                userInfo.setParentLocationId(cursor.getInt(cursor.getColumnIndex(LocationModel.PARENT_LOCATION_ID)));
                userInfoList.add(userInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return userInfoList;
    }


    public ArrayList<Household> getHouseholdList(String code) {
        ArrayList<Household> householdList = new ArrayList<Household>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ");
        sql.append(" substr(h.HH_NUMBER, -3, 3 ) HH_NUMBER,  ");
        sql.append(" h.HH_NUMBER FULL_HH_NUMBER,  ");
        sql.append(" ifnull(h.LATITUDE ,0) LATITUDE, ");
        sql.append(" ifnull(h.LONGITUDE ,0) LONGITUDE, ");
        sql.append(" ifnull(h.MONTHLY_FAMILY_EXPENDITURE ,0) MONTHLY_FAMILY_EXPENDITURE,  ");
        sql.append(" h.HH_CHARACTER  HH_CHARACTER,  ");
        sql.append(" ifnull(h.HH_ADULT_WOMEN ,0) HH_ADULT_WOMEN, ");
        sql.append(" ifnull(h.HH_NAME ,'') HH_NAME, ");
        sql.append(" h.NO_OF_FAMILY_MEMBER  NO_OF_FAMILY_MEMBER,  ");
        sql.append(" h.STATE  STATE,  ");
        sql.append(" h.UPDATE_HISTORY  UPDATE_HISTORY,  ");
        sql.append(" ifnull(sum( b.countbenef ),0 )COUNT_REG_BENF,  ");
        sql.append(" substr(b.FH_CODE , -5, 5 ) FH_CODE, ");
        sql.append(" b.BENEF_NAME  BENEF_NAME,  ");
        sql.append(" b.FH_CODE  BENEF_CODE,  ");
        sql.append(" b.BENEF_NAME_LOCAL  BENEF_NAME_LOCAL, ");
        sql.append(" b.BENEF_IMAGE_PATH  BENEF_IMAGE_PATH,  ");
        sql.append(" b.GENDER  GENDER,  ");
        sql.append(" b.DOB  DOB,  ");
        sql.append(" ifnull( b.OCCUPATION ,'') OCCUPATION, ");
        sql.append(" ifnull( b.MOBILE_NUMBER ,'') MOBILE_NUMBER, ");
        sql.append("  strftime('%d-%m-%Y', pim.TRANS_REF / 1000,'unixepoch','localtime') as interviewDate");
        sql.append(" FROM household h ");
        sql.append(" INNER JOIN (  ");
        sql.append(" SELECT HH_NUMBER,  ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_CODE ELSE NULL END FH_CODE , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_CODE ELSE NULL END BENEF_CODE , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_NAME_LOCAL ELSE NULL END BENEF_NAME_LOCAL , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_NAME ELSE NULL END BENEF_NAME , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_IMAGE_PATH ELSE NULL END BENEF_IMAGE_PATH , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN GENDER ELSE NULL END GENDER , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN DOB ELSE NULL END DOB , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN OCCUPATION ELSE NULL END OCCUPATION , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN MOBILE_NUMBER ELSE NULL END MOBILE_NUMBER , ");
        sql.append(" 1 countbenef  FROM beneficiary WHERE STATE=1 ) as b on h.HH_NUMBER=b.HH_NUMBER  ");
        sql.append("left join PATIENT_INTERVIEW_MASTER pim on  pim.BENEF_CODE = b.BENEF_CODE");
        sql.append(" where  h.HH_NUMBER like '" + code + "%' AND  (h.STATE=1 OR h.STATE=10)   and substr(h.HH_NUMBER,10,3)!='000'  GROUP BY h.HH_NUMBER ");
        sql.append(" ORDER BY substr( h.HH_NUMBER, -3, 3 ) ASC; ");


        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                Household household = new Household();
                household.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                household.setHouseholdHeadCode(cursor.getString(cursor.getColumnIndex("FH_CODE")));
                household.setMonthlyFamilyExpenditure(cursor.getString(cursor.getColumnIndex("MONTHLY_FAMILY_EXPENDITURE")));
                household.setHhAdultWomen(cursor.getLong(cursor.getColumnIndex("HH_ADULT_WOMEN")));
                household.setHhCharacter(cursor.getString(cursor.getColumnIndex("HH_CHARACTER")));

                household.setFullHouseHoldNumber(cursor.getString(cursor.getColumnIndex("FULL_HH_NUMBER")));

                household.setHouseholdHeadImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));
                household.setHouseholdHeadOccupation(cursor.getString(cursor.getColumnIndex("OCCUPATION")));
                household.setHouseholdHeadMobile(cursor.getString(cursor.getColumnIndex("MOBILE_NUMBER")));

                String updateHistroyStr = cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY"));
                if (updateHistroyStr == null || updateHistroyStr.equals("")) {
                    household.setUpdateHistory("0");
                } else {
                    household.setUpdateHistory(cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY")));
                }
//				household.setUpdateHistory(cursor.getString(cursor
////						.getColumnIndex("UPDATE_HISTORY")));

                household.setState(cursor.getLong(cursor.getColumnIndex("STATE")));

                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                household.setHouseholdHeadName(Utility.formatTextBylanguage(nameOriginal, nameLocal));


                household.setNumberOfBeneficiary(cursor.getInt(cursor.getColumnIndex("COUNT_REG_BENF")));

                String dob = cursor.getString(cursor.getColumnIndex("DOB"));

                try {
                    household.setHouseholdHeadAge(Utility.getAge(dob));
                } catch (Exception e) {
                    household.setHouseholdHeadAge("");
                }

                household.setHouseholdHeadGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                household.setInterviewDate(cursor.getString(cursor.getColumnIndex("interviewDate")));
                Double lon = 0.0;
                Double lat = 0.0;
                try {
                    lon = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                    lat = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (lon != null && lat != null && (lon + lat) > 0.0) {
                    household.setHasLocation(true);
                    household.setLongitude(lon);
                    household.setLatitude(lat);
                } else {
                    household.setHasLocation(false);
                    household.setLongitude(0);
                    household.setLatitude(0);
                }

                Long hhbdNumberOfMember = cursor.getLong(cursor.getColumnIndex("NO_OF_FAMILY_MEMBER"));
                if (hhbdNumberOfMember != null) {
                    household.setNoOfFamilyMember(hhbdNumberOfMember);
                } else {
                    household.setNoOfFamilyMember((long) 0);
                }

                householdList.add(household);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return householdList;
    }

    public ArrayList<Household> getHouseholdListAutoSelected() {
        ArrayList<Household> householdList = new ArrayList<Household>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ");
        sql.append(" substr(h.HH_NUMBER, -3, 3 ) HH_NUMBER,  ");
        sql.append(" h.HH_NUMBER FULL_HH_NUMBER,  ");
        sql.append(" ifnull(h.LATITUDE ,0) LATITUDE, ");
        sql.append(" ifnull(h.LONGITUDE ,0) LONGITUDE, ");
        sql.append(" ifnull(h.MONTHLY_FAMILY_EXPENDITURE ,0) MONTHLY_FAMILY_EXPENDITURE,  ");
        sql.append(" h.HH_CHARACTER  HH_CHARACTER,  ");
        sql.append(" ifnull(h.HH_ADULT_WOMEN ,0) HH_ADULT_WOMEN, ");
        sql.append(" h.NO_OF_FAMILY_MEMBER  NO_OF_FAMILY_MEMBER,  ");
        sql.append(" ifnull(h.HH_NAME,'') HH_NAME, ");
        sql.append(" h.STATE  STATE,  ");
        sql.append(" h.UPDATE_HISTORY  UPDATE_HISTORY,  ");
        sql.append(" ifnull(sum( b.countbenef ),0 )COUNT_REG_BENF,  ");
        sql.append(" substr(b.FH_CODE , -5, 5 ) FH_CODE, ");
        sql.append(" b.BENEF_NAME  BENEF_NAME,  ");
        sql.append(" b.BENEF_NAME_LOCAL  BENEF_NAME_LOCAL, ");
        sql.append(" b.BENEF_IMAGE_PATH  BENEF_IMAGE_PATH,  ");
        sql.append(" b.GENDER  GENDER,  ");
        sql.append(" b.DOB  DOB  ");
        sql.append(" FROM household h ");
        sql.append(" LEFT JOIN (  ");
        sql.append(" SELECT HH_NUMBER,  ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_CODE ELSE NULL END FH_CODE , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_NAME_LOCAL ELSE NULL END BENEF_NAME_LOCAL , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_NAME ELSE NULL END BENEF_NAME , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_IMAGE_PATH ELSE NULL END BENEF_IMAGE_PATH , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN GENDER ELSE NULL END GENDER , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN DOB ELSE NULL END DOB , ");
        sql.append(" 1 countbenef  FROM beneficiary WHERE STATE=1 ) as b on h.HH_NUMBER=b.HH_NUMBER  ");
        sql.append(" where  (h.STATE=1 OR h.STATE=10)   and substr(h.HH_NUMBER,10,3)!='000'  GROUP BY h.HH_NUMBER ");
        sql.append(" ORDER BY substr( h.HH_NUMBER, -3, 3 ) ASC; ");


        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                Household household = new Household();
                household.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                household.setHouseholdHeadCode(cursor.getString(cursor.getColumnIndex("FH_CODE")));
                household.setMonthlyFamilyExpenditure(cursor.getString(cursor.getColumnIndex("MONTHLY_FAMILY_EXPENDITURE")));
                household.setHhAdultWomen(cursor.getLong(cursor.getColumnIndex("HH_ADULT_WOMEN")));
                household.setHhCharacter(cursor.getString(cursor.getColumnIndex("HH_CHARACTER")));

                household.setHhName(cursor.getString(cursor.getColumnIndex("HH_NAME")));

                household.setFullHouseHoldNumber(cursor.getString(cursor.getColumnIndex("FULL_HH_NUMBER")));

                household.setHouseholdHeadImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));

                String updateHistroyStr = cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY"));
                if (updateHistroyStr == null || updateHistroyStr.equals("")) {
                    household.setUpdateHistory("0");
                } else {
                    household.setUpdateHistory(cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY")));
                }
//				household.setUpdateHistory(cursor.getString(cursor
////						.getColumnIndex("UPDATE_HISTORY")));

                household.setState(cursor.getLong(cursor.getColumnIndex("STATE")));

                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                household.setHouseholdHeadName(Utility.formatTextBylanguage(nameOriginal, nameLocal));


                household.setNumberOfBeneficiary(cursor.getInt(cursor.getColumnIndex("COUNT_REG_BENF")));

                String dob = cursor.getString(cursor.getColumnIndex("DOB"));

                try {
                    household.setHouseholdHeadAge(Utility.getAge(dob));
                } catch (Exception e) {
                    household.setHouseholdHeadAge("");
                }

                household.setHouseholdHeadGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                Double lon = 0.0;
                Double lat = 0.0;
                try {
                    lon = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                    lat = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (lon != null && lat != null && (lon + lat) > 0.0) {
                    household.setHasLocation(true);
                    household.setLongitude(lon);
                    household.setLatitude(lat);
                } else {
                    household.setHasLocation(false);
                    household.setLongitude(0);
                    household.setLatitude(0);
                }

                Long hhbdNumberOfMember = cursor.getLong(cursor.getColumnIndex("NO_OF_FAMILY_MEMBER"));
                if (hhbdNumberOfMember != null) {
                    household.setNoOfFamilyMember(hhbdNumberOfMember);
                } else {
                    household.setNoOfFamilyMember((long) 0);
                }

                householdList.add(household);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return householdList;
    }

    public ArrayList<Household> getDeactivatedHouseholdList(String code) {
        ArrayList<Household> householdList = new ArrayList<Household>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ");
        sql.append(" substr(h.HH_NUMBER, -3, 3 ) HH_NUMBER,  ");
        sql.append(" h.HH_NUMBER FULL_HH_NUMBER,  ");
        sql.append(" ifnull(h.LONGITUDE ,0) LONGITUDE, ");
        sql.append(" ifnull(h.LATITUDE ,0) LATITUDE,  ");
        sql.append(" h.NO_OF_FAMILY_MEMBER  NO_OF_FAMILY_MEMBER,  ");
        sql.append(" h.STATE  STATE,  ");
        sql.append(" h.UPDATE_HISTORY  UPDATE_HISTORY,  ");
        sql.append(" ifnull(sum( b.countbenef ),0 )COUNT_REG_BENF,  ");
        sql.append(" substr(b.FH_CODE , -5, 5 ) FH_CODE, ");
        sql.append(" b.BENEF_NAME  BENEF_NAME,  ");
        sql.append(" b.BENEF_NAME_LOCAL  BENEF_NAME_LOCAL, ");
        sql.append(" b.BENEF_IMAGE_PATH  BENEF_IMAGE_PATH,  ");
        sql.append(" b.GENDER  GENDER,  ");
        sql.append(" b.DOB  DOB  ");
        sql.append(" FROM household h ");
        sql.append("  JOIN (  ");
        sql.append(" SELECT HH_NUMBER,  ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_CODE ELSE NULL END FH_CODE , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_NAME_LOCAL ELSE NULL END BENEF_NAME_LOCAL , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_NAME ELSE NULL END BENEF_NAME , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_IMAGE_PATH ELSE NULL END BENEF_IMAGE_PATH , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN GENDER ELSE NULL END GENDER , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN DOB ELSE NULL END DOB , ");
        sql.append(" 1 countbenef  FROM beneficiary WHERE STATE=1 ) as b on h.HH_NUMBER=b.HH_NUMBER  ");
        sql.append(" where  h.HH_NUMBER like '" + code + "%' AND h.STATE != 1 and substr(h.HH_NUMBER,10,3)!='000'  GROUP BY h.HH_NUMBER ");
        sql.append(" ORDER BY substr( h.HH_NUMBER, -3, 3 ) ASC; ");


        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                Household household = new Household();
                household.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                household.setHouseholdHeadCode(cursor.getString(cursor.getColumnIndex("FH_CODE")));

                household.setFullHouseHoldNumber(cursor.getString(cursor.getColumnIndex("FULL_HH_NUMBER")));

                household.setHouseholdHeadImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));

                String updateHistroyStr = cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY"));
                if (updateHistroyStr == null || updateHistroyStr.equals("")) {
                    household.setUpdateHistory("0");
                } else {
                    household.setUpdateHistory(cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY")));
                }
//				household.setUpdateHistory(cursor.getString(cursor
////						.getColumnIndex("UPDATE_HISTORY")));

                household.setState(cursor.getLong(cursor.getColumnIndex("STATE")));

                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                household.setHouseholdHeadName(Utility.formatTextBylanguage(nameOriginal, nameLocal));


                household.setNumberOfBeneficiary(cursor.getInt(cursor.getColumnIndex("COUNT_REG_BENF")));

                String dob = cursor.getString(cursor.getColumnIndex("DOB"));

                try {
                    household.setHouseholdHeadAge(Utility.getAge(dob));
                } catch (Exception e) {
                    household.setHouseholdHeadAge("");
                }

                household.setHouseholdHeadGender(cursor.getString(cursor.getColumnIndex("GENDER")));

                Double lon = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                Double lat = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));


                if (lon != null && lat != null && (lon + lat) > 0.0) {
                    household.setHasLocation(true);
                    household.setLongitude(lon);
                    household.setLatitude(lat);
                } else {
                    household.setHasLocation(false);
                    household.setLongitude(0);
                    household.setLatitude(0);
                }

                Long hhbdNumberOfMember = cursor.getLong(cursor.getColumnIndex("NO_OF_FAMILY_MEMBER"));
                if (hhbdNumberOfMember != null) {
                    household.setNoOfFamilyMember(hhbdNumberOfMember);
                } else {
                    household.setNoOfFamilyMember((long) 0);
                }

                householdList.add(household);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return householdList;
    }

    public boolean isHouseholdExist(String hhNumber) {
        String sql = " SELECT HH_NUMBER FROM household where (STATE=1 OR STATE=10) AND HH_NUMBER='" + hhNumber + "'";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount() >= 1;
    }

    public int getNumberOfMemberInHH(String hhNumber) {
        String sql = null;
        sql = " SELECT " + "HH_NUMBER " + " FROM beneficiary  " + " WHERE HH_NUMBER=? AND STATE=1 ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{hhNumber});
        return cursor.getCount();
    }

    public ArrayList<Beneficiary> getHouseholdMemberList(String householdNumber, boolean withOutfamilyHead, String gender, String maritalStatus, Integer maternalStatus, String ageType, int start, int end, int isguestBenefAllowed) {
        ArrayList<Beneficiary> memberList = new ArrayList<Beneficiary>();
        String sql = " SELECT substr( b.BENEF_CODE, -5, 5 )  BENEF_CODE, " + "  b.BENEF_CODE BENEF_CODE_FULL , " + "       b.BENEF_NAME BENEF_NAME, " + "       b.BENEF_NAME_LOCAL BENEF_NAME_LOCAL, " + "       b.DOB DOB, " + "       b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH, " + "       b.GENDER GENDER " + " FROM beneficiary b ";

        if (maternalStatus != null) {
            sql += " left JOIN (SELECT BENEF_CODE,MATERNAL_STATUS " + " FROM maternal_info m inner join (SELECT max(MATERNAL_ID) MATERNAL_ID FROM maternal_info GROUP BY BENEF_CODE) mi2 " + " on m.MATERNAL_ID = mi2.MATERNAL_ID) mi " + " ON mi.BENEF_CODE=b.BENEF_CODE  ";
        }

        // for mHealth
//        if (isguestBenefAllowed == 1) {
//            sql += " WHERE substr( b.BENEF_CODE, -5, 5 )='00000' OR (b.HH_NUMBER='" + householdNumber.trim() + "'  AND b.STATE=1 ";
//
//        } else {
//            sql += " WHERE (b.HH_NUMBER='" + householdNumber.trim() + "'  AND b.STATE=1 ";
//        }

        //for satellite care
        sql += " WHERE (b.BENEF_CODE='" + householdNumber.trim() + "' ";


        if (gender != null) {
            sql += " AND b.GENDER='" + gender.trim() + "'";
        }

        if (withOutfamilyHead) {
            sql += " AND b.FAMILY_HEAD !=1 ";
        }

        if (ageType != null && start > -1 && end > -1) {
            if (ageType.trim().equals("day")) {
                sql += " AND round((julianday(Date('now')) - julianday(b.DOB))+1) BETWEEN " + start + " AND " + end;
            } else if (ageType.trim().equals("month")) {
                sql += " AND round((julianday(Date('now')) - julianday(b.DOB))/30) BETWEEN " + start + " AND " + end;
            } else if (ageType.trim().equals("year")) {
                sql += " AND cast(strftime('%Y.%m%d', 'now', 'localtime') - strftime('%Y.%m%d', b.DOB) as int) BETWEEN " + start + " AND " + end;
            }
        }
        if (maritalStatus != null && maritalStatus.trim().length() > 0) {
            if (maritalStatus.contains(",")) {
                maritalStatus = maritalStatus.replaceAll(",", "','");
            }
            sql += " AND b.MARITAL_STATUS in ('" + maritalStatus.trim() + "') ";
        }

        if (maternalStatus != null && maternalStatus == 1) {
            sql += " and mi.MATERNAL_STATUS==1";
        } else if (maternalStatus != null && maternalStatus == 0) {
            sql += " and (mi.MATERNAL_STATUS !=1 or mi.MATERNAL_STATUS is null)";
        }

        sql += " ) ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                beneficiary.setBenefCodeFull(cursor.getString(cursor.getColumnIndex("BENEF_CODE_FULL")));
                beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));


                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));


                String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                try {
                    beneficiary.setAge(Utility.getAge(dob));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                memberList.add(beneficiary);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return memberList;
    }

    public boolean hasEventDate(String immuType) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        String sql = " SELECT ei.EVENT_ID,ei.EVENT_DATE FROM event_info ei " + " JOIN (SELECT MIN(EVENT_DATE) EVENT_DATE , TYPE FROM  event_info WHERE STATE=1 AND EVENT_DATE > " + calendar.getTimeInMillis() + " AND TYPE='" + immuType + "' ) ei2 " + " ON ei.EVENT_DATE=ei2.EVENT_DATE AND ei.TYPE=ei2.TYPE ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        return cursor.getCount() >= 1;
    }

    public boolean hasUnregistarImmunizableBeneficiary(String immuType) {
        String sql = "";
        if (immuType.equals("EPI")) {
            sql = " SELECT b.BENEF_ID,b.BENEF_CODE,date('now'), cast(julianday()- julianday(b.DOB) as INTEGER) DEF_DOB ,'" + immuType + "' " + " FROM beneficiary b WHERE  cast(julianday()- julianday(DOB) as INTEGER)/30 BETWEEN " + App.getContext().getAppSettings().getEpiMinAge() + " AND " + App.getContext().getAppSettings().getEpiMaxAge() + " AND " + " b.BENEF_CODE NOT IN (SELECT  BENEF_CODE FROM  immunizable_beneficiary where IMMU_TYPE='" + immuType + "' ) ";
        } else if (immuType.equals("TT")) {
            sql = " SELECT b.BENEF_ID,b.BENEF_CODE,date('now'), cast(julianday()- julianday(b.DOB) as INTEGER) DEF_DOB ,'" + immuType + "'   " + " FROM beneficiary b WHERE  cast(julianday()- julianday(DOB) as INTEGER)/30  BETWEEN (12*" + App.getContext().getAppSettings().getTtMinAge() + ") AND (12*" + App.getContext().getAppSettings().getTtMaxAge() + ")  " + " AND b.GENDER='F' AND b.BENEF_CODE NOT IN (SELECT  BENEF_CODE FROM  immunizable_beneficiary  where IMMU_TYPE='" + immuType + "')";
        }

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount() >= 1;
    }

    public ArrayList<Beneficiary> getHouseholdMemberListForUpdate(Context context, String householdNumber) {

        ArrayList<Beneficiary> memberList = new ArrayList<Beneficiary>();
        String sql = null;

        sql = " SELECT " + " b.BENEF_ID BENEF_ID, " + " substr(b.BENEF_CODE,-5,5) BENEF_CODE, " + " b.BENEF_CODE BENEF_FULL_CODE,  " + " b.BENEF_NAME BENEF_NAME, " + " b.BENEF_NAME_LOCAL BENEF_NAME_LOCAL, " + " b.DOB DOB, " + " b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH,  " + " b.GENDER GENDER, " + " IFNULL(b.FILE_KEY,'') FILE_KEY " + " FROM beneficiary b " + " JOIN household as h on h.HH_NUMBER=b.HH_NUMBER  " + " WHERE b.HH_NUMBER=? AND b.STATE=1 AND h.STATE=1 ";

        Log.e("SQL", householdNumber + "___ " + sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{householdNumber});

        if (cursor.moveToFirst()) {
            do {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));


                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));

                String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                try {
                    try {
                        beneficiary.setAge(Utility.getAge(dob));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                beneficiary.setBenefId(cursor.getLong(cursor.getColumnIndex("BENEF_ID")));
                beneficiary.setBenefCodeFull(cursor.getString(cursor.getColumnIndex("BENEF_FULL_CODE")));
                memberList.add(beneficiary);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return memberList;
    }


    public ArrayList<Beneficiary> getBeneficiaryListNew(String fcmCode, String hhNumber, String type) {
        ArrayList<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();

        String sql = " SELECT " + "  substr( b.BENEF_CODE, " + "  -5, " + "  5 ) BENEF_CODE_SHORT," + "  b.*, " + "  mi.MATERNAL_ID MATERNAL_ID, " + "  mi.MATERNAL_STATUS MATERNAL_STATUS, " + "  mi.LMP LMP, " + "  mi.EDD EDD, " + "  mi.GRAVIDA GRAVIDA, " + "  mi.PARA PARA, " + "  mi.BMI_VALUE BMI_VALUE, " + "  mi.BMI BMI, " + "  mi.HEIGHT_IN_CM HEIGHT_IN_CM, " + "  mi.CREATE_DATE CREATE_DATE, " + "  mi.NO_OF_RISK_ITEM NO_OF_RISK_ITEM, " + "  pim.LAST_SERVICE_DATE, " + "  usbenef.REMAINING_DAYS, " + "  pim.LAST_SERVICE_NAME, " + "  IFNULL(CASE WHEN  	mi.MATERNAL_ID IS NULL THEN " + "  CASE WHEN  	benfChild.BENEF_CODE IS NOT NULL THEN " + "  '" + Constants.CHILD_U_5_BENEFICIARY + "' " + "  end " + "  ELSE " + "  '" + Constants.MY_BENEFICIARY + "' " + "  end,'" + Constants.mHEALTH_BENEFICIARY + "') as BENEF_TYPE " + "  FROM " + "  beneficiary b " + "  JOIN household h on " + "  b.HH_NUMBER = h.HH_NUMBER " + "  and h.STATE = 1 " + "  and h.HH_NUMBER like '%' " + "  " + "  LEFT JOIN ( " + "  select max ( CASE   WHEN julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  ) = CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) " + "  THEN CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000,'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) " + "     ELSE 1 + CAST ( ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  )  AS int ) " + "  END )  REMAINING_DAYS  ,us.BENEF_CODE from user_schedule  us group by   us.BENEF_CODE " + "  ) usbenef ON  " + "  usbenef.BENEF_CODE = b.BENEF_CODE " + "  LEFT JOIN (select BENEF_CODE, strftime('%d-%m-%Y', date( MAX(pim.TRANS_REF)/1000, 'unixepoch','localtime')) LAST_SERVICE_DATE ,q.QUESTIONNAIRE_TITLE as LAST_SERVICE_NAME " + " from patient_interview_master pim JOIN questionnaire q on pim.QUESTIONNAIRE_ID=q.QUESTIONNAIRE_ID group by BENEF_CODE) pim  on   pim.BENEF_CODE =b.BENEF_CODE " + "  LEFT JOIN ( " + "  select BENEF_CODE,DOB from beneficiary  where  (strftime('%s',date('now'))-strftime('%s',trim(DOB)))/(60*60*24*30) between 0 AND 60 " + "  ) benfChild ON  " + "  benfChild.BENEF_CODE = b.BENEF_CODE " + "  LEFT JOIN ( " + "  SELECT  m.MATERNAL_ID , m.BENEF_ID, m.BENEF_CODE, m.MATERNAL_STATUS, m.LMP, m.EDD, m.CREATE_DATE, m.GRAVIDA,   m.HEIGHT_IN_CM, m.PARA, m.BMI_VALUE, m.BMI, m.NO_OF_RISK_ITEM " + "  FROM " + "  maternal_info m " + "  INNER JOIN ( " + "  SELECT  max(MATERNAL_ID) MATERNAL_ID  FROM  maternal_info  WHERE  MATERNAL_STATUS = 1 GROUP BY  BENEF_CODE ) mi2 ON " + "  m.MATERNAL_ID = mi2.MATERNAL_ID ) mi ON " + "  mi.BENEF_CODE = b.BENEF_CODE " + "  WHERE " + "  b.STATE = '1' " + "  AND substr( b.BENEF_CODE, 	 1,   9 ) = ? and h.HH_NUMBER like '" + hhNumber + "%'  " + "   ";

        if (type.equalsIgnoreCase(Constants.BENEFICIARY_FEMALE) || type.equalsIgnoreCase(Constants.BENEFICIARY_MALE)) {
            sql += "  AND GENDER  = '" + type + "'";
        }
        if (type.equalsIgnoreCase(Constants.CHILD_U_5_BENEFICIARY) || type.equalsIgnoreCase(Constants.MY_BENEFICIARY)) {
            sql += "  AND BENEF_TYPE  = '" + type + "'";
        }

        sql += "  ORDER BY substr(b.BENEF_CODE,-5,5) ASC";

        Log.e("SQL", sql.toString());
        Cursor cursor = null;
        try {
            try {
                cursor = db.rawQuery(sql, new String[]{fcmCode});
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Beneficiary beneficiary = new Beneficiary();
                        beneficiary.setBenefCodeShort(cursor.getString(cursor.getColumnIndex("BENEF_CODE_SHORT")));
                        beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                        beneficiary.setBenefType(cursor.getString(cursor.getColumnIndex("BENEF_TYPE")));
                        beneficiary.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                        String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                        String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                        beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));
                        beneficiary.setMaternalID(cursor.getString(cursor.getColumnIndex("MATERNAL_ID")));
                        beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));
                        beneficiary.setMobileNumber(cursor.getString(cursor.getColumnIndex("MOBILE_NUMBER")));
                        beneficiary.setEduLevel(cursor.getString(cursor.getColumnIndex("EDU_LEVEL")));
                        beneficiary.setCreateDate(cursor.getString(cursor.getColumnIndex("CREATE_DATE")));
                        beneficiary.setReligion(cursor.getString(cursor.getColumnIndex("RELIGION")));
                        beneficiary.setMaritalStatus(cursor.getString(cursor.getColumnIndex("MARITAL_STATUS")));
                        beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                        beneficiary.setDob(cursor.getString(cursor.getColumnIndex("DOB")));


                        String lastService = cursor.getString(cursor.getColumnIndex("LAST_SERVICE_NAME"));
                        beneficiary.setLastServiceName(lastService);
                        String lastServiceDate = cursor.getString(cursor.getColumnIndex("LAST_SERVICE_DATE"));
                        beneficiary.setLastServiceDate(lastServiceDate);
                        try {
                            long edd = cursor.getLong(cursor.getColumnIndex("EDD"));
                            beneficiary.setEdd(edd);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                        try {
                            beneficiary.setAge(Utility.getAge(dob));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                        beneficiaryList.add(beneficiary);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return beneficiaryList;
    }

    /**
     * Get all beneficiaries.
     *
     * @param fcmCode the fcm code
     * @return The beneficiary list
     */
    public ArrayList<Beneficiary> getBeneficiaryList(String fcmCode, String hhNumber) {
        ArrayList<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
        String sql = "SELECT substr(b.BENEF_CODE,-5,5) BENEF_CODE, " + " b.BENEF_NAME, " + " b.BENEF_NAME_LOCAL, " + " b.DOB, " + " b.BENEF_IMAGE_PATH," + " b.GENDER " + " FROM beneficiary b " + " JOIN household h on b.HH_NUMBER = h.HH_NUMBER  and h.HH_NUMBER like '" + hhNumber + "%'  " + " WHERE  substr(b.BENEF_CODE, 1, 9)=? " + " ORDER BY substr(b.BENEF_CODE,-5,5) ASC";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{fcmCode});

        if (cursor.moveToFirst()) {
            do {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));


                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));


                String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                try {
                    beneficiary.setAge(Utility.getAge(dob));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                beneficiaryList.add(beneficiary);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return beneficiaryList;
    }

    public ArrayList<Beneficiary> getBeneficiaryList(String type, long userId) {
        boolean active = false;
        try {
            JSONArray configArry = new JSONArray(AppPreference.getString(context.getApplicationContext(), KEY.FCM_CONFIGURATION, "[]"));
            String val = JSONParser.getFcmConfigValue(configArry, "INACTIVE_BENEF_SHOW", "inactive.benef.show");
            active = Boolean.parseBoolean(val);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sql = "";
        ArrayList<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
        if (active) {
            sql = " SELECT * FROM (SELECT substr(b.BENEF_CODE,-5,5) BENEF_CODE,b.BENEF_CODE as BENEF_CODE_FULL,b.USER_ID as USER_ID,b.GUARDIAN_NAME as GUARDIAN_NAME ,b.RELATION_GUARDIAN as RELATION_GUARDIAN,b.STATE as STATE,b.BENEF_ID as BENEF_ID,b.ADDRESS as ADDRESS,b.HH_NUMBER as HH_NUMBER,ui.USER_NAME as USER_NAME,L.LOCATION_NAME as LOCATION_NAME, b.BENEF_NAME as BENEF_NAME, b.BENEF_NAME_LOCAL as BENEF_NAME_LOCAL, b.DOB as DOB,  b.BENEF_IMAGE_PATH as BENEF_IMAGE_PATH, b.GENDER as GENDER FROM beneficiary b  left join household h on b.HH_NUMBER = h.HH_NUMBER  LEFT JOIN USER_INFO ui on ui.USER_ID =b.USER_ID LEFT JOIN  LOCATION L ON L.LOCATION_ID = UI.LOCATION_ID   ORDER BY substr(b.BENEF_CODE,-5,5) DESC ) as tb where tb.BENEF_CODE != '00000' ";
        } else {
            sql = " SELECT * FROM (SELECT substr(b.BENEF_CODE,-5,5) BENEF_CODE,b.BENEF_CODE as BENEF_CODE_FULL,b.USER_ID as USER_ID,b.GUARDIAN_NAME as GUARDIAN_NAME ,b.RELATION_GUARDIAN as RELATION_GUARDIAN,b.STATE as STATE,b.BENEF_ID as BENEF_ID,b.ADDRESS as ADDRESS,b.HH_NUMBER as HH_NUMBER,ui.USER_NAME as USER_NAME,L.LOCATION_NAME as LOCATION_NAME, b.BENEF_NAME as BENEF_NAME, b.BENEF_NAME_LOCAL as BENEF_NAME_LOCAL, b.DOB as DOB,  b.BENEF_IMAGE_PATH as BENEF_IMAGE_PATH, b.GENDER as GENDER FROM beneficiary b  left join household h on b.HH_NUMBER = h.HH_NUMBER  LEFT JOIN USER_INFO ui on ui.USER_ID =b.USER_ID LEFT JOIN  LOCATION L ON L.LOCATION_ID = UI.LOCATION_ID  WHERE b.STATE='1' ORDER BY substr(b.BENEF_CODE,-5,5) DESC ) as tb where tb.BENEF_CODE != '00000' ";
        }

        if (type.equalsIgnoreCase(Constants.BENEFICIARY_LIST_mHEALTH)) {
            sql += " and tb.USER_ID  !=" + userId + " ";
        } else if (type.equalsIgnoreCase(Constants.BENEFICIARY_LIST_PARAMEDIC)) {
            sql += "  and tb.USER_ID  =" + userId + " ";
        }
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                beneficiary.setUserId(cursor.getLong(cursor.getColumnIndex("USER_ID")));
                beneficiary.setBenefId(cursor.getLong(cursor.getColumnIndex("BENEF_ID")));
                beneficiary.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                beneficiary.setBenefCodeFull(cursor.getString(cursor.getColumnIndex("BENEF_CODE_FULL")));
                beneficiary.setFcmName(cursor.getString(cursor.getColumnIndex("USER_NAME")));
                beneficiary.setLocationName(cursor.getString(cursor.getColumnIndex("LOCATION_NAME")));
                beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));
                beneficiary.setGuardianName(cursor.getString(cursor.getColumnIndex("GUARDIAN_NAME")));
                beneficiary.setRelationToGurdian(cursor.getString(cursor.getColumnIndex("RELATION_GUARDIAN")));
                beneficiary.setState(cursor.getInt(cursor.getColumnIndex("STATE")));


                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));
                try {
                    String address = "";
                    if (userId == beneficiary.getUserId()) {
                        address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                    }
                    if (userId != beneficiary.getUserId()) {
                        address = cursor.getString(cursor.getColumnIndex("LOCATION_NAME"));
                    }
                    beneficiary.setAddress(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                try {
                    beneficiary.setAge(Utility.getAge(dob));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                beneficiaryList.add(beneficiary);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return beneficiaryList;
    }

    public ArrayList<Beneficiary> getBeneficiaryListWithPagination(String type, long userId, int ofsset, int limitt) {
        String sql = "";
        ArrayList<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
        sql = " SELECT * FROM (SELECT substr(b.BENEF_CODE,-5,5) BENEF_CODE,b.BENEF_CODE as BENEF_CODE_FULL,b.USER_ID as USER_ID,b.BENEF_ID as BENEF_ID,b.ADDRESS as ADDRESS,b.HH_NUMBER as HH_NUMBER,ui.USER_NAME,L.LOCATION_NAME, b.BENEF_NAME, b.BENEF_NAME_LOCAL, b.DOB,  b.BENEF_IMAGE_PATH, b.GENDER  FROM beneficiary b  left join household h on b.HH_NUMBER = h.HH_NUMBER  LEFT JOIN USER_INFO ui on ui.USER_ID =b.USER_ID LEFT JOIN  LOCATION L ON L.LOCATION_ID = UI.LOCATION_ID WHERE b.STATE='1'  ORDER BY substr(b.BENEF_CODE,-5,5) ASC ) as tb where tb.BENEF_CODE != '00000' ";
        //  Log.d(TAG, "getBeneficiaryListWithPagination: sql: "+sql);
        if (type.equalsIgnoreCase(Constants.BENEFICIARY_LIST_mHEALTH)) {
            sql += " and tb.USER_ID  !=" + userId + " ";
        } else if (type.equalsIgnoreCase(Constants.BENEFICIARY_LIST_PARAMEDIC)) {
            sql += "  and tb.USER_ID  =" + userId + " ";
        }
        sql += "LIMIT " + limitt + " OFFSET " + ((ofsset));
        Log.e("SQL", sql.toString());
        Log.d(TAG, "getBeneficiaryListWithPagination: ..the limit is:  " + limitt + "  the offset is " + ((ofsset)));
        Cursor cursor = db.rawQuery(sql, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                beneficiary.setUserId(cursor.getLong(cursor.getColumnIndex("USER_ID")));
                beneficiary.setBenefId(cursor.getLong(cursor.getColumnIndex("BENEF_ID")));
                beneficiary.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                beneficiary.setBenefCodeFull(cursor.getString(cursor.getColumnIndex("BENEF_CODE_FULL")));
                beneficiary.setFcmName(cursor.getString(cursor.getColumnIndex("USER_NAME")));
                beneficiary.setLocationName(cursor.getString(cursor.getColumnIndex("LOCATION_NAME")));
                beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));


                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));
                try {
                    String address = "";
                    if (userId == beneficiary.getUserId()) {
                        address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                    }
                    if (userId != beneficiary.getUserId()) {
                        address = cursor.getString(cursor.getColumnIndex("LOCATION_NAME"));
                    }
                    beneficiary.setAddress(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                try {
                    beneficiary.setAge(Utility.getAge(dob));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                beneficiaryList.add(beneficiary);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return beneficiaryList;
    }

    public Beneficiary getBeneficiaryById(String benefCode) {

        String sql = "";
        ArrayList<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
        sql = " SELECT * FROM (SELECT substr(b.BENEF_CODE,-5,5) BENEF_CODE,b.BENEF_CODE as BENEF_CODE_FULL,b.HH_NUMBER as HH_NUMBER,ui.USER_NAME,ui.USER_ID as USER_ID,L.LOCATION_NAME, b.BENEF_NAME, b.BENEF_NAME_LOCAL, b.DOB,  b.BENEF_IMAGE_PATH, b.GENDER  FROM beneficiary b  left join household h on b.HH_NUMBER = h.HH_NUMBER  JOIN USER_INFO ui on ui.USER_ID =b.USER_ID LEFT JOIN  LOCATION L ON L.LOCATION_ID = UI.LOCATION_ID WHERE b.STATE='1'  ORDER BY substr(b.BENEF_CODE,-5,5) ASC )  where BENEF_CODE_FULL = '" + benefCode + "' ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                beneficiary.setUserId(cursor.getLong(cursor.getColumnIndex("USER_ID")));
                beneficiary.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                beneficiary.setBenefCodeFull(cursor.getString(cursor.getColumnIndex("BENEF_CODE_FULL")));
                beneficiary.setFcmName(cursor.getString(cursor.getColumnIndex("USER_NAME")));
                beneficiary.setLocationName(cursor.getString(cursor.getColumnIndex("LOCATION_NAME")));
                beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));


                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));


                String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                try {
                    beneficiary.setAge(Utility.getAge(dob));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                return beneficiary;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return null;
    }

    public ArrayList<Beneficiary> getBeneficiaryList(int limit) {
        ArrayList<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
        String sql = " SELECT * FROM (SELECT substr(b.BENEF_CODE,-5,5) BENEF_CODE,b.BENEF_CODE as BENEF_CODE_FULL,ui.USER_NAME,L.LOCATION_NAME, b.BENEF_NAME, b.BENEF_NAME_LOCAL, b.DOB,  b.BENEF_IMAGE_PATH, b.GENDER  FROM beneficiary b " + " JOIN household h on b.HH_NUMBER = h.HH_NUMBER  JOIN USER_INFO ui on ui.USER_LOGIN_ID =substr(b.BENEF_CODE,1,9) LEFT JOIN  LOCATION L ON L.LOCATION_ID = UI.LOCATION_ID  " + " ORDER BY substr(b.BENEF_CODE,-5,5) ASC )  where BENEF_CODE != '00000' ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{});

        if (cursor.moveToFirst()) {
            do {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                beneficiary.setBenefCodeFull(cursor.getString(cursor.getColumnIndex("BENEF_CODE_FULL")));
                beneficiary.setFcmName(cursor.getString(cursor.getColumnIndex("USER_NAME")));
                beneficiary.setLocationName(cursor.getString(cursor.getColumnIndex("LOCATION_NAME")));
                beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));


                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));


                String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                try {
                    beneficiary.setAge(Utility.getAge(dob));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                beneficiaryList.add(beneficiary);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return beneficiaryList;
    }

    public ArrayList<Beneficiary> getChildBeneficiaryList(String fcmCode, String hhNumber) {
        ArrayList<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();

        try {
            int minAge = Integer.parseInt(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "CHILD_HEALTHCARE_LIST", "min_age"));

            int maxAge = Integer.parseInt(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(), "CHILD_HEALTHCARE_LIST", "max_age"));

            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT substr(b.BENEF_CODE,-5,5) BENEF_CODE,b.BENEF_CODE BENEF_CODE_FULL, b.BENEF_NAME, ");
            sql.append("   b.BENEF_NAME_LOCAL, b.DOB, b.BENEF_IMAGE_PATH, b.GENDER , ");
            sql.append("   (strftime('%s',date('now'))-strftime('%s',trim(b.DOB)))/(60*60*24*30)  AGE_IN_MONTH ,pim.LAST_SERVICE_DATE ");
            sql.append(" FROM beneficiary b ");
            sql.append(" JOIN household h on b.HH_NUMBER = h.HH_NUMBER  and   h.HH_NUMBER like  '" + hhNumber + "%' ");
            sql.append(" LEFT JOIN (select BENEF_CODE, ");
            sql.append(" strftime('%d/%m/%Y', date( MAX(pim.TRANS_REF)/1000, 'unixepoch','localtime')) ");
            sql.append(" LAST_SERVICE_DATE from patient_interview_master pim ");
            sql.append(" JOIN questionnaire q on pim.QUESTIONNAIRE_ID=q.QUESTIONNAIRE_ID  ");
            sql.append(" WHERE trim(q.NAME)='CHILD_GROWTH_ASSESSMENT' GROUP BY BENEF_CODE ) pim  ");
            sql.append("  on pim.BENEF_CODE=b.BENEF_CODE ");
            sql.append(" WHERE b.STATE='1' AND substr(b.BENEF_CODE, 1, 9)=? ");
            sql.append(" AND  (strftime('%s',date('now'))-strftime('%s',trim(b.DOB)))/(60*60*24*30) between 0 AND 12 ");
            sql.append(" ORDER BY substr(b.BENEF_CODE,-5,5) ASC ");

            Log.e("SQL", sql.toString());
            Cursor cursor = db.rawQuery(sql.toString(), new String[]{fcmCode});

            if (cursor.moveToFirst()) {
                do {
                    Beneficiary beneficiary = new Beneficiary();
                    beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                    beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                    beneficiary.setBenefCodeFull(cursor.getString(cursor.getColumnIndex("BENEF_CODE_FULL")));
                    beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));


                    String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                    String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                    beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));

                    String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                    try {
                        beneficiary.setAge(Utility.getAge(dob));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                    beneficiary.setCreateDate(cursor.getString(cursor.getColumnIndex("LAST_SERVICE_DATE")));
                    beneficiaryList.add(beneficiary);
                } while (cursor.moveToNext());
            }
            cursor.close();

        } catch (MhealthException e) {
            e.printStackTrace();
        }
        return beneficiaryList;
    }

    /***
     * Get different type of beneficiary (Ex:
     * EPIBeneficiary,TTBeneficiary,CCSBeneficiary,MaternalInfo) depend on
     * questionnaire name.
     *
     * @param beneCode
     *            beneficiary code
     * @param questionnaireName
     *            name of interview
     * @return Beneficiary or null if not exist
     */
    public Beneficiary getBeneficiaryInfo(String beneCode, String questionnaireName) {

        Beneficiary beneficiary = getBeneficiaryBasicInfoForQuestionnaire(beneCode, questionnaireName);
        if (beneficiary instanceof ImmunaizationBeneficiary) {
            if (questionnaireName.equals(QuestionnaireName.TT) || questionnaireName.equals(QuestionnaireName.TT_FOLLOWUP)) {
                getImmunizationDao().addTTInfo((ImmunaizationBeneficiary) beneficiary);
            } else if (questionnaireName.equals(QuestionnaireName.EPI) || questionnaireName.equals(QuestionnaireName.EPI_FOLLOWUP)) {
                getImmunizationDao().addEPIInfo((ImmunaizationBeneficiary) beneficiary);
            }
        }
        if (beneficiary instanceof CCSBeneficiary) {
            getCcsDao().addCCSInfo((CCSBeneficiary) beneficiary);
        }
        if (beneficiary instanceof MaternalInfo) {
            if (!questionnaireName.equals(QuestionnaireName.MATERNAL_PREGNANT_MOTHER_REGISTRATION)) {
                getMaternalDao().addMaternalInfo((MaternalInfo) beneficiary);
                getMaternalDao().addMaternalServicInfo((MaternalInfo) beneficiary);
                getMaternalDao().addMaternalDelevery((MaternalInfo) beneficiary);
            }
        }
        return beneficiary;
    }

    /***
     * Get different type of beneficiary (Ex:
     * EPIBeneficiary,TTBeneficiary,CCSBeneficiary,MaternalInfo) with only
     * common information depend on questionnaire name.
     *
     * @param beneCode
     *            beneficiary code
     * @param questionnaireName
     *            name of interview
     * @return Beneficiary or null if not exist
     */
    public Beneficiary getBeneficiaryBasicInfoForQuestionnaire(String beneCode, String questionnaireName) {

        Beneficiary beneficiary = null;
        if (questionnaireName.startsWith("CERVICAL_CANCER")) beneficiary = new CCSBeneficiary();
        else if (questionnaireName.startsWith("EPI") || questionnaireName.startsWith("TT"))
            beneficiary = new ImmunaizationBeneficiary();
        else if (questionnaireName.startsWith("MATERNAL_")) beneficiary = new MaternalInfo();
        else beneficiary = new Beneficiary();

        String sql = " SELECT  " + " b.STATE,  " + " b.HH_NUMBER,  " + " b.BENEF_ID,  " + " b.ADDRESS,  " + " b.USER_ID,  " + " b.FAMILY_HEAD,  " + " b.MOBILE_NUMBER,  " + " b.MOBILE_COMM,  " + " b.EDU_LEVEL,  " + " b.RELATION_GUARDIAN,  " + " b.GUARDIAN_NAME,  " + " b.GUARDIAN_NAME_LOCAL,  " + " b.RELIGION,  " + " b.RELIGION_OTHER_SPECIFIC,  " + " b.AGREED_MOBILE_COMM,  " + " b.MOBILE_COMM_LANG,  " + " b.MARITAL_STATUS,  " + " b.OCCUPATION,  " + " b.OCCUPATION_HER_HUSBAND,  " + " b.NATIONAL_ID,  " + " b.BIRTH_REG_NUMBER,  " + " b.BENEF_CODE,  " + " substr( b.BENEF_CODE, -5, 5 )  BENEF_CODE_SHORT, " + " b.BENEF_NAME,  " + " b.BENEF_NAME_LOCAL,  " + " b.DOB,  " + " b.BENEF_IMAGE_PATH,  " + " b.GENDER, " + " ifnull(h.LATITUDE,0) ||'##'|| ifnull(h.LONGITUDE,0)  LOCATION , " + " IFNULL(mi.LMP,0) LMP, " + " IFNULL(mi2.MAX_MATERNAL_COMPLETE_DATE,0) MAX_MATERNAL_COMPLETE_DATE, " + " IFNULL(mi.MATERNAL_STATUS,-1 ) MATERNAL_STATUS " + " FROM beneficiary b " + " LEFT JOIN (select * from household where HH_NUMBER =substr('" + beneCode + "',0,13) ) h  on h.HH_NUMBER=substr(b.BENEF_CODE,0,13) " + " LEFT JOIN maternal_info mi ON b.BENEF_CODE=mi.BENEF_CODE " + " LEFT JOIN (select MAX(MATERNAL_COMPLETE_DATE) MAX_MATERNAL_COMPLETE_DATE , BENEF_CODE from maternal_info where BENEF_CODE ='" + beneCode + "'  ) mi2 ON mi2.BENEF_CODE=b.BENEF_CODE " + " WHERE  b.BENEF_CODE=?  ORDER BY mi.MATERNAL_ID DESC LIMIT 1 ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{beneCode});
        if (cursor.moveToFirst()) {
            beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
            beneficiary.setBenefCodeShort(cursor.getString(cursor.getColumnIndex("BENEF_CODE_SHORT")));
            beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));
            beneficiary.setBenefName(cursor.getString(cursor.getColumnIndex("BENEF_NAME")));
            beneficiary.setBenefLocalName(cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL")));
            String dob = cursor.getString(cursor.getColumnIndex("DOB"));
            try {
                beneficiary.setDob(dob);
                try {
                    beneficiary.setAge(Utility.getAge(dob));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
            beneficiary.setBenefId(cursor.getInt(cursor.getColumnIndex("BENEF_ID")));
            beneficiary.setBirthCertificateNumber(cursor.getString(cursor.getColumnIndex("BIRTH_REG_NUMBER")));
            beneficiary.setEduLevel(cursor.getString(cursor.getColumnIndex("EDU_LEVEL")));
            beneficiary.setFamilyHead(cursor.getInt(cursor.getColumnIndex("FAMILY_HEAD")));
            beneficiary.setGuardianName(cursor.getString(cursor.getColumnIndex("GUARDIAN_NAME")));
            beneficiary.setGuardianLocalName(cursor.getString(cursor.getColumnIndex("GUARDIAN_NAME_LOCAL")));
            beneficiary.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
            beneficiary.setMaritalStatus(cursor.getString(cursor.getColumnIndex("MARITAL_STATUS")));
            beneficiary.setMobileComm(cursor.getString(cursor.getColumnIndex("MOBILE_COMM")));
            beneficiary.setMobileNumber(cursor.getString(cursor.getColumnIndex("MOBILE_NUMBER")));
            beneficiary.setNationalIdNumber(cursor.getString(cursor.getColumnIndex("NATIONAL_ID")));
            beneficiary.setAgreedMobileComm(cursor.getString(cursor.getColumnIndex("AGREED_MOBILE_COMM")));
            beneficiary.setMobileCommLang(cursor.getString(cursor.getColumnIndex("MOBILE_COMM_LANG")));
            beneficiary.setOccupation(cursor.getString(cursor.getColumnIndex("OCCUPATION")));
            beneficiary.setOccupationHerHusband(cursor.getString(cursor.getColumnIndex("OCCUPATION_HER_HUSBAND")));
            beneficiary.setRelationToGurdian(cursor.getString(cursor.getColumnIndex("RELATION_GUARDIAN")));
            beneficiary.setReligion(cursor.getString(cursor.getColumnIndex("RELIGION")));
            beneficiary.setReligionOtherSpecofic(cursor.getString(cursor.getColumnIndex("RELIGION_OTHER_SPECIFIC")));
            beneficiary.setMaternalStatus(cursor.getLong(cursor.getColumnIndex("MATERNAL_STATUS")));
            beneficiary.setLmp(cursor.getLong(cursor.getColumnIndex("LMP")));
            beneficiary.setMaxMaternalCompleteDate(cursor.getLong(cursor.getColumnIndex("MAX_MATERNAL_COMPLETE_DATE")));
            beneficiary.setState(cursor.getInt(cursor.getColumnIndex("STATE")));
            beneficiary.setHouseholdLocation(cursor.getString(cursor.getColumnIndex("LOCATION")));
            try {
                beneficiary.setUserId(cursor.getLong(cursor.getColumnIndex("USER_ID")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                String address = "";
                if (beneficiary.getUserId() == App.getContext().getUserInfo().getUserId()) {
                    address = cursor.getString(cursor.getColumnIndex("ADDRESS"));
                } else {
                    address = getAddressByLocationId(beneficiary.getUserId());
                }
                beneficiary.setAddress(address);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            beneficiary = null;
        }
        cursor.close();
        return beneficiary;
    }

    /**
     * Get all categories of questionnaire.
     *
     * @param langCode the lang code
     * @return The questionnaire category list
     */
    public ArrayList<QuestionnaireCategoryInfo> getQuestionnaireCategoryList(String langCode) {
        ArrayList<QuestionnaireCategoryInfo> categoryList = new ArrayList<QuestionnaireCategoryInfo>();

        String sql = " SELECT * FROM (SELECT qc.CATEGORY_ID CATEGORY_ID, " + " qc.CATEGORY_CAPTION CATEGORY_CAPTION, " + " qc.CATEGORY_NAME CATEGORY_NAME, qc.ICON ICON, " + " SUM(CASE  WHEN q.QUESTIONNAIRE_ID ISNULL THEN 0 ELSE 1 END) NUM_OF_QUESTIONNAIRE " + " FROM questionnaire_category qc  " + " LEFT JOIN questionnaire q ON q.CATEGORY_ID = qc.CATEGORY_ID AND q.STATE=1  AND q.LANG_CODE='" + langCode + "' " + " WHERE qc.STATE=1 AND qc.LANG_CODE='" + langCode + "' " + " GROUP BY qc.CATEGORY_ID ORDER BY  qc.SORT_ORDER ) where NUM_OF_QUESTIONNAIRE>0 ";

        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                QuestionnaireCategoryInfo categoryInfo = new QuestionnaireCategoryInfo();
                categoryInfo.setCategoryId(cursor.getInt(cursor.getColumnIndex("CATEGORY_ID")));
                categoryInfo.setCategoryCaption(cursor.getString(cursor.getColumnIndex("CATEGORY_CAPTION")));
                categoryInfo.setCategoryName(cursor.getString(cursor.getColumnIndex("CATEGORY_NAME")));
                categoryInfo.setIcon(cursor.getString(cursor.getColumnIndex("ICON")));
                categoryList.add(categoryInfo);
            } while (cursor.moveToNext());

        }
        cursor.close();

        return categoryList;
    }

    /**
     * Check if a beneficiary exist.
     *
     * @param beneficiaryCode is the beneficiary code which will checked
     * @return true if the beneficiary exist. false otherwise
     */
    public boolean isBeneficiaryExist(String beneficiaryCode) {
        String sql = " SELECT BENEF_CODE FROM beneficiary WHERE BENEF_CODE='" + beneficiaryCode + "' LIMIT 1";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        return cursor.getCount() > 0;
    }


    public void saveUserSchedule(ArrayList<UserScheduleInfo> scheduleInfos, long interviewStarttime) {
        for (UserScheduleInfo scheduleInfo : scheduleInfos) {
            try {

                ContentValues cv = new ContentValues();

                cv.put(Column.SCHED_DESC, scheduleInfo.getDescription());
                cv.put(Column.SCHED_DATE, scheduleInfo.getScheduleDate());
                cv.put(Column.SCHED_STATUS, scheduleInfo.getScheduleStatus());
                cv.put(Column.USER_ID, scheduleInfo.getUserId());
                cv.put(Column.TYPE, scheduleInfo.getScheduleType());
                cv.put(Column.ATTENDED_DATE, scheduleInfo.getAttendedDate());
                cv.put(Column.REFERENCE_ID, scheduleInfo.getReferenceId());
                cv.put(Column.EVENT_ID, scheduleInfo.getEventId());
                cv.put(Column.BENEFICIARY_BENEF_CODE, scheduleInfo.getBeneficiaryCode());
                cv.put(Column.INTERVIEW_ID, scheduleInfo.getInterviewId());
                int affectedRow = db.update("user_schedule", cv, " INTERVIEW_ID = ? and SCHED_DESC=? ", new String[]{Long.toString(scheduleInfo.getInterviewId()), scheduleInfo.getDescription()});
                if (affectedRow == 0) {
                    cv.put(Column.CREATE_DATE, interviewStarttime);
                    cv.put(Column.TRANS_REF, interviewStarttime);
                    Log.e("CV", cv.toString());
                    db.insertOrThrow("user_schedule", null, cv);
                }
            } catch (Exception exception) {

                exception.printStackTrace();
            }
        }
    }

    public void inactivePrivousUndoneUserSchedule(UserScheduleInfo us, long questionnaireId) {
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" update user_schedule set STATE=0 ");
            sql.append(" WHERE BENEF_CODE='" + us.getBeneficiaryCode() + "' ");
            sql.append(" AND TYPE ='" + us.getScheduleType() + "' ");

            if (!"CCS".equals(us.getScheduleType())) {
                sql.append(" AND SCHED_DATE=" + us.getScheduleDate() + " AND SCHED_STATUS=0  ");
                sql.append(" AND INTERVIEW_ID in ");
                sql.append("  (select INTERVIEW_ID  from patient_interview_master where QUESTIONNAIRE_ID=" + questionnaireId + " and BENEF_CODE='" + us.getBeneficiaryCode() + "' )");
                Log.e("SQL", sql.toString());
            }
            db.execSQL(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getUserScheId(long interviewId, String type) {
        long reasonId = -1;
        String sql = " select   SCHED_ID from user_schedule WHERE INTERVIEW_ID=" + interviewId + " and TYPE='" + type + "' ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            reasonId = cursor.getLong(cursor.getColumnIndex("SCHED_ID"));
        }
        return reasonId;
    }


    public void updateUserSchedule(String whereBenefCode, String whereType, long whereScheDate, long attendedDate) {
        ContentValues cv = new ContentValues();
        cv.put("ATTENDED_DATE", attendedDate);
        cv.put("SCHED_STATUS", 1);
        String whereStr = "BENEF_CODE='" + whereBenefCode + "' AND TYPE='" + whereType + "'  AND SCHED_STATUS=0 AND SCHED_DATE=" + whereScheDate;
        db.update("user_schedule", cv, whereStr, null);
    }

    public void deleteUserSchedule(long scheId) {
        db.delete("user_schedule", " SCHED_ID =" + scheId, null);
    }

    public void deleteAllBenef(long userId) {
        db.delete("beneficiary", " USER_ID !=" + userId, null);
        db.delete("couple_registration", null, null);
    }

    public String getInterViewServerStatus(long INTERIVEW_ID) {

        String sql = "Select DATA_SENT FROM patient_interview_master  WHERE INTERVIEW_ID = " + INTERIVEW_ID;
        Cursor cr = db.rawQuery(sql.toString(), new String[]{});
        // Cursor cr = db.rawQuery(sql.toString(), new String[]{lang});
        String date = "";

        InterviewInfoSyncUnsync si = new InterviewInfoSyncUnsync();
        if (cr.moveToFirst()) {
            do {
                try {
                    si.setSentStatus(cr.getString(cr.getColumnIndex("DATA_SENT")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cr.moveToNext());

            return si.getSentStatus();
        } else return "";

    }

    public void deleteService(long interviewId, long userId, JSONArray prescription, int timiMili) {


        for (int i = 0; i < prescription.length(); i++) {
            JSONObject jsonDosesObject = new JSONObject();
            try {
                jsonDosesObject = prescription.getJSONObject(i);
                String medicineId = JSONParser.getString(jsonDosesObject, "MED_ID");
                String medicineQuantity = JSONParser.getString(jsonDosesObject, "MED_QTY");
                updateStockTable(Long.parseLong(medicineId), userId, Long.parseLong(medicineQuantity), timiMili);


                String consumSql = "SELECT " + Column.MED_CONSUMP_ID + " FROM medicine_consumption_master WHERE " + Column.INTERVIEW_ID + "=?";
                Cursor consumpCorsor = db.rawQuery(consumSql, new String[]{Long.toString(interviewId)});

                if (consumpCorsor.moveToFirst()) {
                    long consumpId = consumpCorsor.getLong(consumpCorsor.getColumnIndex(Column.MED_CONSUMP_ID));

                    String insertIntomedicine_consumption_detailTAble = "INSERT INTO medicine_consumption_detail_delete SELECT * FROM medicine_consumption_detail where MED_CONSUMP_ID =" + consumpId;
                    db.execSQL(insertIntomedicine_consumption_detailTAble);


                    db.delete("medicine_consumption_detail", Column.MED_CONSUMP_ID + "=?", new String[]{Long.toString(consumpId)});

                }
                consumpCorsor.close();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        String insertIntopatient_interview_detailTAble = "INSERT INTO patient_interview_detail_delete SELECT * FROM patient_interview_detail where  INTERVIEW_ID =" + interviewId;
        db.execSQL(insertIntopatient_interview_detailTAble);
        String insertIntopatient_interview_masterDuplicateTAble = "INSERT INTO patient_interview_master_delete SELECT * FROM patient_interview_master where  INTERVIEW_ID =" + interviewId;
        db.execSQL(insertIntopatient_interview_masterDuplicateTAble);
        String insertIntomedicine_consumption_masterDuplicateTAble = "INSERT INTO medicine_consumption_master_delete SELECT * FROM medicine_consumption_master where  INTERVIEW_ID =" + interviewId;
        db.execSQL(insertIntomedicine_consumption_masterDuplicateTAble);

        db.delete("patient_interview_detail", " INTERVIEW_ID =" + interviewId, null);
        db.delete("patient_interview_master", " INTERVIEW_ID =" + interviewId, null);
        db.delete("medicine_consumption_master", " INTERVIEW_ID =" + interviewId, null);

    }

    public void saveUserSchedule(UserScheduleInfo scheduleInfo) {
        ContentValues cv = new ContentValues();
        cv.put(Column.SCHED_DESC, scheduleInfo.getDescription());
        cv.put(Column.SCHED_DATE, scheduleInfo.getScheduleDate());
        cv.put(Column.SCHED_STATUS, scheduleInfo.getScheduleStatus());
        cv.put(Column.USER_ID, scheduleInfo.getUserId());
        cv.put(Column.TYPE, scheduleInfo.getScheduleType());
        cv.put(Column.ATTENDED_DATE, scheduleInfo.getAttendedDate());
        cv.put(Column.REFERENCE_ID, scheduleInfo.getReferenceId());
        cv.put(Column.EVENT_ID, scheduleInfo.getEventId());
        cv.put(Column.BENEFICIARY_BENEF_CODE, scheduleInfo.getBeneficiaryCode());
        cv.put(Column.INTERVIEW_ID, scheduleInfo.getInterviewId());
        cv.put(Column.STATE, 1);
        cv.put(Column.CREATE_DATE, scheduleInfo.getCreatedDate());
        cv.put(Column.TRANS_REF, scheduleInfo.getCreatedDate());
        db.insert("user_schedule", null, cv);
    }

    /**
     * Remove user schedule from schedule table for specific interview ID.
     *
     * @param interviewId The interview ID
     */
    public void deleteSchedule(long interviewId) {
        db.delete("user_schedule", Column.INTERVIEW_ID + "=?", new String[]{Long.toString(interviewId)});
    }

    /**
     * Get the saved interview list based on dataSent.
     *
     * @param dataSent is the flag to determine whether the interview is sent to
     *                 server or not. The value can be 'Y' or 'N'
     * @param lang     the lang
     * @return The interview list
     */
//  public ArrayList<SavedInterviewList> getSavedInterviewListOld(String dataSent,
//          String lang) {
//      ArrayList<SavedInterviewList> allInterview = new ArrayList<SavedInterviewList>();
//
//      ArrayList<String> answerFileNameList = FileOperaion
//              .getFileList(new File(App.getContext()
//                      .getQuestionnaireJSONDir()));
//
//      StringBuilder sbInSql = new StringBuilder();
//      sbInSql.append("(");
//      for (int i = 0; i < answerFileNameList.size(); i++) {
//          String fileKey = answerFileNameList.get(i).split("_")[0];
//
//          if (i > 0)
//              sbInSql.append(",");
//          sbInSql.append("'" + fileKey + "'");
//      }
//      sbInSql.append(")");
//      Cursor dateCursor = null;
//
//      String dateListSql = "";
//      if (dataSent.equals("N")) {
//          dateListSql = "SELECT strftime('%d-%m-%Y',TRANS_REF/1000, 'unixepoch', 'localtime') as interviewDate"
//                  + " FROM patient_interview_master"
//                  + " WHERE DATA_SENT=? AND FILE_KEY in "
//                  + sbInSql.toString()
//                  + " GROUP BY strftime('%d-%m-%Y',TRANS_REF/1000, 'unixepoch', 'localtime') ORDER BY interviewDate ASC ";
//      } else if (dataSent.equals("Y")) {
//          dateListSql = "SELECT strftime('%d-%m-%Y',TRANS_REF/1000, 'unixepoch', 'localtime') as interviewDate"
//                  + " FROM patient_interview_master"
//                  + " WHERE DATA_SENT=? AND FILE_KEY in "
//                  + sbInSql.toString()
//                  + " GROUP BY strftime('%d-%m-%Y',TRANS_REF/1000, 'unixepoch', 'localtime') ORDER BY interviewDate DESC ";
//      }
//
//      dateCursor = db.rawQuery(dateListSql, new String[] { dataSent });
//      // dateCursor = db.rawQuery(dateListSql,null);
//
//      if (dateCursor.moveToFirst()) {
//          do {
//              String interviewDate = dateCursor.getString(dateCursor
//                      .getColumnIndex("interviewDate"));
//
//              // // Retrieve interview based on date
//              String  interviewSql = " SELECT strftime('%d-%m-%Y',pim.TRANS_REF/1000, 'unixepoch', 'localtime') as interviewDate, "
//                      + " pim.TRANS_REF TRANS_REF, "
//                      + " b.benef_id BENEF_ID, "
//                      + " b.benef_name BENEF_NAME,"
//                      + " b.benef_name_local BENEF_NAME_LOCAL, "
//                      + " b.benef_image_path BENEF_IMAGE_PATH, "
//                      + " b.benef_code BENEF_CODE, "
//                      + " substr(b.benef_code,-5,3) HH_NUMBER, "
//                      + " CASE WHEN CAST(strftime('%H', pim.TRANS_REF/1000, 'unixepoch', 'localtime') AS INTEGER) < 12 "
//                      + " then strftime('%H:%M', pim.TRANS_REF/1000, 'unixepoch','localtime') || ' AM' "
//                      + " ELSE   strftime('%H:%M', pim.TRANS_REF/1000, '-12 Hours', 'unixepoch', 'localtime') || ' PM' END as INTERVIEW_TIME , "
//                      + " q.questionnaire_title QUESTIONNAIRE_TITLE, "
//                      + "  q.name QUESTIONNAIRE_NAME , "
//                      + " pim.file_path FILE_PATH, "
//                      + " pim.question_answer_json QUESTION_ANSWER_JSON, "
//                      + " pim.interview_id INTERVIEW_ID, "
//                      + " pim.parent_interview_id PARENT_INTERVIEW_ID, "
//                      + " pim.file_key FILE_KEY"
//                      + " FROM patient_interview_master pim "
//                      + " left join beneficiary b on pim.benef_code = b.benef_code  "
//                      + " inner join questionnaire q on pim.questionnaire_id = q.questionnaire_id "
//                      + " where pim.file_key in "
//                      + sbInSql.toString()
//                      + " AND pim.DATA_SENT=? AND strftime('%d-%m-%Y',pim.TRANS_REF/1000, 'unixepoch', 'localtime')=? AND q.LANG_CODE=?";

//
//              Cursor interviewCursor = db.rawQuery(interviewSql,
//                      new String[] { dataSent, interviewDate, lang });
//
//              if (interviewCursor.moveToFirst()) {
//                  SavedInterviewList savedInterviewList = new SavedInterviewList();
//                  savedInterviewList.setInterviewDate(interviewDate);
//
//                  ArrayList<SavedInterviewInfo> interviewList = new ArrayList<SavedInterviewInfo>();
//                  do {
//                      SavedInterviewInfo savedInterviewInfo = new SavedInterviewInfo();
//
//                      savedInterviewInfo.setBeneficiaryId(interviewCursor
//                              .getLong(interviewCursor
//                                      .getColumnIndex("BENEF_ID")));
//                      savedInterviewInfo.setBeneficiaryCode(interviewCursor
//                              .getString(interviewCursor
//                                      .getColumnIndex("BENEF_CODE")));
//                      savedInterviewInfo.setInterviewId(interviewCursor
//                              .getLong(interviewCursor
//                                      .getColumnIndex("INTERVIEW_ID")));
//                      savedInterviewInfo
//                              .setParentInterviewId(interviewCursor.getLong(interviewCursor
//                                      .getColumnIndex("PARENT_INTERVIEW_ID")));
//                      savedInterviewInfo.setBenefImagePath(interviewCursor
//                              .getString(interviewCursor
//                                      .getColumnIndex("BENEF_IMAGE_PATH")));
//
//                      savedInterviewInfo.setTransRef(interviewCursor
//                              .getLong(interviewCursor
//                                      .getColumnIndex("TRANS_REF")));
//
//
//
//                      String nameOriginal = interviewCursor.getString(interviewCursor.getColumnIndex("BENEF_NAME"));
//                      String nameLocal = interviewCursor.getString(interviewCursor.getColumnIndex("BENEF_NAME_LOCAL"));
//                      savedInterviewInfo.setBenefName(Utility.formatTextBylanguage(nameOriginal,nameLocal));
//
//
//
//                      savedInterviewInfo.setDate(interviewDate);
//                      savedInterviewInfo.setHouseholdNumber(interviewCursor
//                              .getString(interviewCursor
//                                      .getColumnIndex("HH_NUMBER")));
//                      savedInterviewInfo
//                              .setInputBinaryFilePathList(interviewCursor
//                                      .getString(interviewCursor
//                                              .getColumnIndex("FILE_PATH")));
//
//                      savedInterviewInfo
//                              .setQuestionAnswerJson(interviewCursor.getString(interviewCursor
//                                      .getColumnIndex("QUESTION_ANSWER_JSON")));
//                      savedInterviewInfo
//                              .setQuestionnarieTitle(interviewCursor.getString(interviewCursor
//                                      .getColumnIndex("QUESTIONNAIRE_TITLE")));
//                      savedInterviewInfo.setQuestionnarieName(interviewCursor
//                              .getString(interviewCursor
//                                      .getColumnIndex("QUESTIONNAIRE_NAME")));
//                      savedInterviewInfo.setTime(interviewCursor
//                              .getString(interviewCursor
//                                      .getColumnIndex("INTERVIEW_TIME")));
//
//                      for (int i = 0; i < answerFileNameList.size(); i++) {
//                          if (answerFileNameList.get(i).contains(
//                                  interviewCursor.getString(interviewCursor
//                                          .getColumnIndex("FILE_KEY")))) {
//                              savedInterviewInfo
//                                      .setFileFullName(answerFileNameList
//                                              .get(i));
//                              break;
//                          }
//                      }
//
//                      if (savedInterviewInfo.getParentInterviewId() > 0) {
//                          savedInterviewInfo.setScheduleInfo(getScheduleInfo(
//                                  savedInterviewInfo.getParentInterviewId(),
//                                  0));
//                      }
//                      interviewList.add(savedInterviewInfo);
//
//                  } wgetBeneficiaryCodehile (interviewCursor.moveToNext());
//
//                  savedInterviewList.setAllInterview(interviewList);
//                  allInterview.add(savedInterviewList);
//              }
//
//              interviewCursor.close();
//
//          } while (dateCursor.moveToNext());
//      }
//
//      dateCursor.close();
//
//      return allInterview;
//  }
    public ArrayList<InterviewInfoSyncUnsync> getInterviewListSyncUnsync(String dataSent, String lang, String hhCode, String interviewType, int offset, int limit, long fromDate, long toDate) {
        int dataSentforFeedback = 0;
        if (dataSent.equalsIgnoreCase("Y")) {
            dataSentforFeedback = 1;
        } else {
            dataSentforFeedback = 0;
        }
        String fromDateFormat = "";
        String toDateFormat = "";
        try {
            Date fromDateDate = new Date(fromDate);
            Date toDateDate = new Date(toDate);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            fromDateFormat = outputDateFormat.format(fromDateDate);
            toDateFormat = outputDateFormat.format(toDateDate);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<InterviewInfoSyncUnsync> interviewList = new ArrayList<InterviewInfoSyncUnsync>();

        StringBuilder sql = new StringBuilder();

        sql.append(" select * from ( ");
        sql.append("   SELECT null SEND_STATUS,null DOC_FOLLOWUP_ID, null IS_FEEDBACK_ON_TIME,null FEEDBACK_DATE,null FEEDBACK_SOURCE,null  ");
        sql.append(" INVES_ADVICE,null INVES_RESULT,null  INVES_STATUS,null FEEDBACK_RECEIVE_TIME,null  DOCTOR_FINDINGS,null  ");
        sql.append(" REF_CENTER_ID,null PRESCRIBED_MEDICINE,null NEXT_FOLLOWUP_DATE,null MESSAGE_TO_FCM,null UPDATE_BY,null  ");
        sql.append(" UPDATE_ON,pim.questionnaire_id QUESTIONNAIRE_ID, strftime('%d-%m-%Y', pim.TRANS_REF / 1000,'unixepoch','localtime') as interviewDate , ");
        sql.append(" pim.TRANS_REF TRANS_REF,pim.CREATE_DATE CREATE_DATE,pim.RECORD_DATE RECORD_DATE,   b.benef_id BENEF_ID,   IFNULL(  b.DOB,'' ) DOB,  IFNULL( ");
        sql.append(" b.GENDER,'' ) GENDER, IFNULL(  b.ADDRESS,'' ) ADDRESS, IFNULL(  pim.GENDER,'' ) PIM_GENDER,IFNULL(  pim.BENEF_NAME,'' ) PIM_BENEF_NAME,IFNULL(  pim.BENEF_ADDRESS,'' )  PIM_BENEF_ADDRESS,IFNULL(  pim.AGE_IN_DAY,0 )  AGE_IN_DAY,  case when  length(trim(b.BENEF_NAME))>0 THEN b.BENEF_NAME   when length(trim( ");
        sql.append(" b.benef_name_local))>0 then b.benef_name_local else b.benef_name END  BENEF_NAME,  b.benef_image_path  ");
        sql.append(" BENEF_IMAGE_PATH,  IFNULL( b.benef_code,'' ) BENEF_CODE,  IFNULL( pim.benef_code,'' ) PIM_BENEF_CODE ,   substr(b.benef_code,-5,3) HH_NUMBER,substr(pim.benef_code,-5,3) PIM_HH_NUMBER,   CASE WHEN CAST( ");
        sql.append(" strftime('%H', pim.TRANS_REF/1000, 'unixepoch', 'localtime') AS INTEGER) < 12   then strftime('%H:%M',  ");
        sql.append(" pim.TRANS_REF/1000, 'unixepoch','localtime') || ' AM'   ELSE   strftime('%H:%M',time(strftime('%H:%M',  ");
        sql.append(" pim.TRANS_REF/1000, 'unixepoch', 'localtime'),'-12 hours')) || ' PM' END as INTERVIEW_TIME ,   IFNULL( ");
        sql.append(" q.questionnaire_title,q.name) QUESTIONNAIRE_TITLE,   q.name QUESTIONNAIRE_NAME ,q.ICON as ICON,  pim.file_path FILE_PATH,    ");
        sql.append(" case when 'N'=UPPER('" + dataSent + "') THEN pim.question_answer_json ELSE  pim.question_answer_json END QUESTION_ANSWER_JSON,  null   ");
        sql.append(" QUESTION_ANSWER_JSON2, pim.USER_ID USER_ID, pim.interview_id INTERVIEW_ID,   pim.parent_interview_id  ");
        sql.append(" PARENT_INTERVIEW_ID,   pim.file_key FILE_KEY  FROM patient_interview_master pim   left join beneficiary b on  ");
        sql.append(" pim.benef_code = b.benef_code    inner join questionnaire q on pim.questionnaire_id = q.questionnaire_id  AND  ");
        sql.append(" q.LANG_CODE='" + lang + "'  where   pim.DATA_SENT='" + dataSent + "' and pim.TRANS_REF >0  and  ");
        sql.append(" pim.USER_ID IS NOT  NULL  ");
        if (fromDate > 0 && toDate > 0) {
            sql.append(" AND pim.RECORD_DATE  BETWEEN  '" + fromDateFormat + "'  AND '" + toDateFormat + "'");
        }
        sql.append("  UNION ALL ");
        sql.append("  SELECT pidf.SEND_STATUS SEND_STATUS ,pidf.DOC_FOLLOWUP_ID DOC_FOLLOWUP_ID,pidf.IS_FEEDBACK_ON_TIME IS_FEEDBACK_ON_TIME,pidf.FEEDBACK_DATE  ");
        sql.append(" FEEDBACK_DATE, pidf.FEEDBACK_SOURCE FEEDBACK_SOURCE,pidf.INVES_ADVICE INVES_ADVICE,pidf.INVES_RESULT  ");
        sql.append(" INVES_RESULT,pidf.INVES_STATUS INVES_STATUS,pidf.FEEDBACK_RECEIVE_TIME  ");
        sql.append(" FEEDBACK_RECEIVE_TIME,pidf.DOCTOR_FINDINGS DOCTOR_FINDINGS,pidf.REF_CENTER_ID  ");
        sql.append(" REF_CENTER_ID,pidf.PRESCRIBED_MEDICINE PRESCRIBED_MEDICINE,pidf.NEXT_FOLLOWUP_DATE  ");
        sql.append(" NEXT_FOLLOWUP_DATE,pidf.MESSAGE_TO_FCM MESSAGE_TO_FCM, pidf.UPDATE_BY  ");
        sql.append(" UPDATE_BY,pidf.UPDATE_ON UPDATE_ON,pim.questionnaire_id QUESTIONNAIRE_ID, strftime('%d-%m-%Y', pidf.TRANS_REF / 1000,'unixepoch','localtime') as interviewDate, ");
        sql.append(" pidf.TRANS_REF TRANS_REF,pim.CREATE_DATE CREATE_DATE,pim.RECORD_DATE RECORD_DATE,  b.benef_id BENEF_ID,  IFNULL(  b.DOB,'' ) DOB,  IFNULL(b.GENDER,'' )  ");
        sql.append(" GENDER, IFNULL(  b.ADDRESS,'' ) ADDRESS, IFNULL(  pim.GENDER,'' ) PIM_GENDER,IFNULL(  pim.BENEF_NAME,'' ) PIM_BENEF_NAME,IFNULL(  pim.BENEF_ADDRESS,'' )  PIM_BENEF_ADDRESS,IFNULL(  pim.AGE_IN_DAY,0 )  AGE_IN_DAY,  case when  length(trim(b.BENEF_NAME))>0 THEN b.BENEF_NAME   when length(trim( ");
        sql.append(" b.benef_name_local))>0 then b.benef_name_local else b.benef_name END  BENEF_NAME,  b.benef_image_path  ");
        sql.append(" BENEF_IMAGE_PATH,  IFNULL( b.benef_code,'' ) BENEF_CODE,  IFNULL( pim.benef_code,'' ) PIM_BENEF_CODE,   substr(b.benef_code,-5,3) HH_NUMBER,substr(pim.benef_code,-5,3) PIM_HH_NUMBER,   CASE WHEN CAST( ");
        sql.append(" strftime('%H', pidf.TRANS_REF/1000, 'unixepoch', 'localtime') AS INTEGER) < 12   then strftime('%H:%M',  ");
        sql.append(" pidf.TRANS_REF/1000, 'unixepoch','localtime') || ' AM'   ELSE   strftime('%H:%M',time(strftime('%H:%M',  ");
        sql.append(" pidf.TRANS_REF/1000, 'unixepoch', 'localtime'),'-12 hours')) || ' PM' END as INTERVIEW_TIME ,   IFNULL( ");
        sql.append(" q.questionnaire_title,q.name) QUESTIONNAIRE_TITLE,   q.name QUESTIONNAIRE_NAME ,q.ICON as ICON,   pim.file_path FILE_PATH,   ");
        sql.append(" pidf.QUESTION_ANSWER_JSON QUESTION_ANSWER_JSON,  pidf.QUESTION_ANSWER_JSON2  ");
        sql.append(" QUESTION_ANSWER_JSON2,pidf.USER_ID USER_ID,  pim.interview_id INTERVIEW_ID,   pim.parent_interview_id  ");
        sql.append(" PARENT_INTERVIEW_ID,   pim.file_key FILE_KEY  FROM patient_interview_master pim   left join beneficiary b on  ");
        sql.append(" pim.benef_code = b.benef_code       inner join questionnaire q on pim.questionnaire_id =  ");
        sql.append(" q.questionnaire_id   join patient_interview_doctor_feedback pidf on pidf.INTERVIEW_ID = pim.INTERVIEW_ID AND  ");
        sql.append(" q.LANG_CODE='" + lang + "'  where    pidf.SEND_STATUS = " + dataSentforFeedback + " and pidf.TRANS_REF >0  and  ");

        //sql.append(" q.LANG_CODE=?  where  pim.benef_code like '" + hhCode + "%' and  pidf.SEND_STATUS =" + dataSentforFeedback + " and pidf.TRANS_REF >0  and  ");
        sql.append(" pim.USER_ID IS NOT  NULL ");

        sql.append(") ORDER BY TRANS_REF DESC ");


//        sql.append(" LIMIT "+ limit+ " OFFSET " +offset);

        Log.e("SQL", sql.toString());
        Cursor cr = db.rawQuery(sql.toString(), new String[]{});
        // Cursor cr = db.rawQuery(sql.toString(), new String[]{lang});
        String date = "";
        if (cr.moveToFirst()) {
            do {
                try {
                    InterviewInfoSyncUnsync si = new InterviewInfoSyncUnsync();
                    si.setBeneficiaryId(cr.getLong(cr.getColumnIndex("BENEF_ID")));
                    si.setUpdateOn(cr.getLong(cr.getColumnIndex("UPDATE_ON")));
                    si.setInterviewId(cr.getLong(cr.getColumnIndex("INTERVIEW_ID")));
                    si.setParentInterviewId(cr.getLong(cr.getColumnIndex("PARENT_INTERVIEW_ID")));
                    si.setBenefImagePath(cr.getString(cr.getColumnIndex("BENEF_IMAGE_PATH")));
                    si.setTransRef(cr.getLong(cr.getColumnIndex("TRANS_REF")));
                    //  si.setBenefName(cr.getString(cr.getColumnIndex("BENEF_NAME")));
                    String dates = cr.getString(cr.getColumnIndex("interviewDate"));
                    si.setDate(cr.getString(cr.getColumnIndex("interviewDate")));

                    si.setInputBinaryFilePathList(cr.getString(cr.getColumnIndex("FILE_PATH")));
                    si.setQuestionAnswerJson(cr.getString(cr.getColumnIndex("QUESTION_ANSWER_JSON")));
                    si.setQuestionnarieTitle(cr.getString(cr.getColumnIndex("QUESTIONNAIRE_TITLE")));
                    si.setQuestionnarieName(cr.getString(cr.getColumnIndex("QUESTIONNAIRE_NAME")));
                    si.setQuestionnaireIcon(cr.getString(cr.getColumnIndex("ICON")));
                    si.setQuestionnaireId(cr.getLong(cr.getColumnIndex("QUESTIONNAIRE_ID")));

                    si.setTime(cr.getString(cr.getColumnIndex("INTERVIEW_TIME")));
                    //----------------------for new param
                    try {
                        String gender = cr.getString(cr.getColumnIndex("GENDER"));
                        if (gender == null || gender.isEmpty()) {
                            gender = cr.getString(cr.getColumnIndex("PIM_GENDER"));
                        }
                        si.setBeneficiarGender(gender);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        String benefCode = cr.getString(cr.getColumnIndex("BENEF_CODE"));
                        if (benefCode == null || benefCode.isEmpty()) {
                            benefCode = cr.getString(cr.getColumnIndex("PIM_BENEF_CODE"));
                        }
                        si.setBeneficiaryCode(benefCode);
                        si.setBenefCode(benefCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        String HH_NUMBER = cr.getString(cr.getColumnIndex("HH_NUMBER"));

                        if (HH_NUMBER == null || HH_NUMBER.isEmpty()) {
                            HH_NUMBER = cr.getString(cr.getColumnIndex("PIM_HH_NUMBER"));
                        }
                        si.setHouseholdNumber(HH_NUMBER);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        String benefName = cr.getString(cr.getColumnIndex("BENEF_NAME"));

                        if (benefName == null || benefName.isEmpty()) {
                            benefName = cr.getString(cr.getColumnIndex("PIM_BENEF_NAME"));
                        }
                        si.setBenefName(benefName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        String address = cr.getString(cr.getColumnIndex("ADDRESS"));
                        if (address == null || address.isEmpty()) {
                            address = cr.getString(cr.getColumnIndex("PIM_BENEF_ADDRESS"));
                        }
                        si.setBenefAddress(address);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        String dob = cr.getString(cr.getColumnIndex("DOB"));
                        if (dob.isEmpty()) {
                            long ageInDay = cr.getLong(cr.getColumnIndex("AGE_IN_DAY"));
                            if (ageInDay > 0) {
                                long age = ageInDay / 365;
                                si.setAge("" + age);
                            }
                        } else {
                            try {
                                si.setBeneficiarAge(Utility.getAge(dob));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //----------------------for new param


                    si.setFileKey(cr.getString(cr.getColumnIndex("FILE_KEY")));


                    si.setIsFeedbackOnTime(cr.getLong(cr.getColumnIndex(Column.IS_FEEDBACK_ON_TIME)));
                    si.setFeedbackSource(cr.getString(cr.getColumnIndex(Column.FEEDBACK_SOURCE)));

                    si.setInvesAdvice(cr.getString(cr.getColumnIndex(Column.INVES_ADVICE)));
                    si.setCreateDate(cr.getLong(cr.getColumnIndex(Column.CREATE_DATE)));
                    si.setInvesResult(cr.getString(cr.getColumnIndex(Column.INVES_RESULT)));
                    si.setInvesStatus(cr.getString(cr.getColumnIndex(Column.INVES_STATUS)));
                    si.setDocFollowupId(cr.getLong(cr.getColumnIndex(Column.DOC_FOLLOWUP_ID)));
                    si.setInterviewTime(cr.getString(cr.getColumnIndex("INTERVIEW_TIME")));

                    si.setQuestionAnsJson2(cr.getString(cr.getColumnIndex(Column.QUESTION_ANSWER_JSON2)));

                    si.setFeedbackDate(cr.getLong(cr.getColumnIndex(Column.FEEDBACK_DATE)));

                    si.setFeedbackReceiveTime(cr.getLong(cr.getColumnIndex(Column.FEEDBACK_RECEIVE_TIME)));


                    si.setUserId(cr.getLong(cr.getColumnIndex(Column.USER_ID)));

                    si.setRefCenterId(cr.getLong(cr.getColumnIndex(Column.REF_CENTER_ID)));

                    si.setNextFollowupDate(cr.getLong(cr.getColumnIndex(Column.NEXT_FOLLOWUP_DATE)));

                    si.setDoctorFindings(cr.getString(cr.getColumnIndex(Column.DOCTOR_FINDINGS)));

                    si.setPrescribedMedicine(cr.getString(cr.getColumnIndex(Column.PRESCRIBED_MEDICINE)));

                    si.setUpdateBy(cr.getLong(cr.getColumnIndex(Column.UPDATE_BY)));

                    si.setUpdateOn(cr.getLong(cr.getColumnIndex(Column.UPDATE_ON)));
                    si.setRecordDate(cr.getString(cr.getColumnIndex(Column.RECORD_DATE)));

                    if (!cr.isNull(cr.getColumnIndex(Column.SEND_STATUS))) {
                        si.setSendStatus(cr.getInt(cr.getColumnIndex(Column.SEND_STATUS)));
                    } else {
                        Log.e("sdf", "s");
                    }

                    if (!date.equals(si.getRecordDate())) {
                        date = si.getRecordDate();
                        si.setNewDate(true);
                    }

//                        if (si.getParentInterviewId() > 0) {
//                          si.setScheduleInfo(getScheduleInfo(si.getParentInterviewId(),0));
//                      }
                    interviewList.add(si);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return interviewList;

//        if (interviewType.equalsIgnoreCase(Constants.INTERVIEW_SERVICE)) {
//            ArrayList<InterviewInfoSyncUnsync> interviewServiceList = new ArrayList<InterviewInfoSyncUnsync>();
//            for (InterviewInfoSyncUnsync interviewInfo : interviewList) {
//
//
//            }
//
//            return interviewServiceList;
//        } else if (interviewType.equalsIgnoreCase(Constants.INTERVIEW_DOCTOR_FEEDBACK)) {
//            ArrayList<InterviewInfoSyncUnsync> interviewDoctorFeedbackList = new ArrayList<InterviewInfoSyncUnsync>();
//            for (InterviewInfoSyncUnsync interviewInfo : interviewList) {
//
//
//            }
//            return interviewDoctorFeedbackList;
//        } else {
//            return interviewList;
//        }
    }


    @SuppressLint("Range")
    public ArrayList<SavedInterviewInfo> getInterviewList(String dataSent, String lang, String hhCode) {

        ArrayList<SavedInterviewInfo> interviewList = new ArrayList<SavedInterviewInfo>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT pim.questionnaire_id QUESTIONNAIRE_ID, strftime('%d-%m-%Y',pim.TRANS_REF/1000, 'unixepoch', 'localtime') as interviewDate,  ");
        sql.append(" pim.TRANS_REF TRANS_REF,  ");
        sql.append(" b.benef_id BENEF_ID,  ");
        sql.append(" case when  length(trim(pim.BENEF_NAME))>0 THEN pim.BENEF_NAME   when length(trim(b.benef_name_local))>0 then b.benef_name_local else b.benef_name END  BENEF_NAME, ");
        sql.append(" b.benef_image_path BENEF_IMAGE_PATH,  ");
        sql.append(" b.benef_code BENEF_CODE,  ");
        sql.append(" substr(b.benef_code,-5,3) HH_NUMBER,  ");
        sql.append(" CASE WHEN CAST(strftime('%H', pim.TRANS_REF/1000, 'unixepoch', 'localtime') AS INTEGER) < 12  ");
        sql.append(" then strftime('%H:%M', pim.TRANS_REF/1000, 'unixepoch','localtime') || ' AM'  ");
        sql.append(" ELSE   strftime('%H:%M',time(strftime('%H:%M', pim.TRANS_REF/1000, 'unixepoch', 'localtime'),'-12 hours')) || ' PM' END as INTERVIEW_TIME ,  ");
        sql.append(" IFNULL(q.questionnaire_title,q.name) QUESTIONNAIRE_TITLE,  ");
        sql.append(" q.name QUESTIONNAIRE_NAME ,  ");
        sql.append(" pim.file_path FILE_PATH,  ");
        sql.append(" case when 'N'=UPPER(?) THEN pim.question_answer_json ELSE '' END QUESTION_ANSWER_JSON,  ");
        sql.append(" pim.interview_id INTERVIEW_ID,  ");
        sql.append(" pim.parent_interview_id PARENT_INTERVIEW_ID,  ");
        sql.append(" pim.file_key FILE_KEY ");
        sql.append(" FROM patient_interview_master pim  ");
        sql.append(" left join beneficiary b on pim.benef_code = b.benef_code   ");
        sql.append(" inner join questionnaire q on pim.questionnaire_id = q.questionnaire_id  AND q.LANG_CODE=? ");
        sql.append(" where  pim.DATA_SENT=? and pim.TRANS_REF >0  and pim.USER_ID IS NOT  NULL order by pim.TRANS_REF DESC");

        Log.e("SQL", sql.toString());
        Cursor cr = db.rawQuery(sql.toString(), new String[]{dataSent, lang, dataSent});
        String date = "";
        if (cr.moveToFirst()) {

            do {
                SavedInterviewInfo si = new SavedInterviewInfo();
                si.setBeneficiaryId(cr.getLong(cr.getColumnIndex("BENEF_ID")));
                si.setBeneficiaryCode(cr.getString(cr.getColumnIndex("BENEF_CODE")));
                si.setInterviewId(cr.getLong(cr.getColumnIndex("INTERVIEW_ID")));
                si.setParentInterviewId(cr.getLong(cr.getColumnIndex("PARENT_INTERVIEW_ID")));
                si.setBenefImagePath(cr.getString(cr.getColumnIndex("BENEF_IMAGE_PATH")));
                si.setTransRef(cr.getLong(cr.getColumnIndex("TRANS_REF")));
                si.setBenefName(cr.getString(cr.getColumnIndex("BENEF_NAME")));
                si.setDate(cr.getString(cr.getColumnIndex("interviewDate")));
                si.setHouseholdNumber(cr.getString(cr.getColumnIndex("HH_NUMBER")));
                si.setInputBinaryFilePathList(cr.getString(cr.getColumnIndex("FILE_PATH")));
                si.setQuestionAnswerJson(cr.getString(cr.getColumnIndex("QUESTION_ANSWER_JSON")));
                si.setQuestionnarieTitle(cr.getString(cr.getColumnIndex("QUESTIONNAIRE_TITLE")));
                si.setQuestionnarieName(cr.getString(cr.getColumnIndex("QUESTIONNAIRE_NAME")));
                si.setQuestionnaireId(cr.getLong(cr.getColumnIndex("QUESTIONNAIRE_ID")));

                si.setTime(cr.getString(cr.getColumnIndex("INTERVIEW_TIME")));
                si.setFileKey(cr.getString(cr.getColumnIndex("FILE_KEY")));
                if (!date.equals(si.getDate())) {
                    date = si.getDate();
                    si.setNewDate(true);
                }
//                        if (si.getParentInterviewId() > 0) {
//                          si.setScheduleInfo(getScheduleInfo(si.getParentInterviewId(),0));
//                      }
                interviewList.add(si);
            } while (cr.moveToNext());
        }
        cr.close();
        return interviewList;
    }

    public long getInterviewListCount(String dataSent, String lang, String hhCode) {
        int dataSentforFeedback = 0;
        if (dataSent.equalsIgnoreCase("Y")) {
            dataSentforFeedback = 1;
        } else {
            dataSentforFeedback = 0;
        }
        long count = 0;
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" * ");
        sql.append(" from ");
        sql.append(" ( ");
        sql.append(" SELECT ");
        sql.append(" null SEND_STATUS,  pim.interview_id INTERVIEW_ID FROM ");
        sql.append(" patient_interview_master pim ");
        sql.append(" left join beneficiary b on ");
        sql.append(" pim.benef_code = b.benef_code ");
        sql.append(" inner join questionnaire q on ");
        sql.append(" pim.questionnaire_id = q.questionnaire_id ");
        sql.append(" AND q.LANG_CODE ='" + lang + "' ");
        sql.append(" where ");
//        sql.append(" pim.benef_code like '" + hhCode + "%' and");
        sql.append("  pim.DATA_SENT ='" + dataSent + "' ");
        sql.append(" and pim.TRANS_REF >0 ");
        sql.append(" and pim.USER_ID IS NOT NULL ");
        sql.append(" UNION ALL ");
        sql.append(" SELECT ");
        sql.append(" null SEND_STATUS,  pim.interview_id INTERVIEW_ID ");
        sql.append(" FROM ");
        sql.append(" patient_interview_master pim ");
        sql.append(" left join beneficiary b on ");
        sql.append(" pim.benef_code = b.benef_code ");
        sql.append(" inner join questionnaire q on ");
        sql.append(" pim.questionnaire_id = q.questionnaire_id ");
        sql.append(" join patient_interview_doctor_feedback pidf on ");
        sql.append(" pidf.INTERVIEW_ID = pim.INTERVIEW_ID ");
        sql.append(" AND q.LANG_CODE ='" + lang + "' ");
        sql.append(" where ");
//        sql.append(" pim.benef_code like '" + hhCode + "%' and");
        sql.append("  pidf.SEND_STATUS = " + dataSentforFeedback + " ");
        sql.append(" and pidf.TRANS_REF >0 ");
        sql.append(" and pim.USER_ID IS NOT NULL ) ");
        Log.e("SQL", sql.toString());
        try {
            Cursor cursor = db.rawQuery(sql.toString(), null);
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    public long getInterviewListAllCount(String lang) {
        int dataSentforFeedback = 0;

        long count = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("  SELECT pim.interview_id INTERVIEW_ID FROM ");
        sql.append(" patient_interview_master pim ");
        sql.append(" left join beneficiary b on ");
        sql.append(" pim.benef_code = b.benef_code ");
        sql.append(" inner join questionnaire q on ");
        sql.append(" pim.questionnaire_id = q.questionnaire_id ");
        sql.append(" AND q.LANG_CODE ='" + lang + "' ");
        sql.append(" where ");
        sql.append("  pim.TRANS_REF >0 ");
        sql.append(" and pim.USER_ID IS NOT NULL ");
        Log.e("SQL", sql.toString());
        try {
            Cursor cursor = db.rawQuery(sql.toString(), null);
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    public long getCurrentMonthInterviewCount(String lang) {
        long count = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pim.interview_id AS INTERVIEW_ID ");
        sql.append("FROM patient_interview_master pim ");
        sql.append("LEFT JOIN beneficiary b ON pim.benef_code = b.benef_code ");
        sql.append("INNER JOIN questionnaire q ON pim.questionnaire_id = q.questionnaire_id AND q.LANG_CODE = '" + lang + "'  ");
        sql.append("WHERE strftime('%Y-%m', pim.RECORD_DATE) = strftime('%Y-%m', 'now') ");
        sql.append("AND q.NAME !='BENEFICIARY_REGISTRATION' AND pim.TRANS_REF > 0 AND pim.USER_ID IS NOT NULL");
        Log.e("SQL", sql.toString());
        try {
            Cursor cursor = db.rawQuery(sql.toString(), null);
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    public long getCurrentTodayInterviewCount(String lang) {
        long count = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pim.interview_id AS INTERVIEW_ID ");
        sql.append("FROM patient_interview_master pim ");
        sql.append("LEFT JOIN beneficiary b ON pim.benef_code = b.benef_code ");
        sql.append("INNER JOIN questionnaire q ON pim.questionnaire_id = q.questionnaire_id AND q.LANG_CODE = '" + lang + "'  ");
        sql.append("WHERE  strftime('%Y-%m-%d', pim.RECORD_DATE)  = strftime('%Y-%m-%d', 'now') ");
        sql.append("AND  q.NAME !='BENEFICIARY_REGISTRATION' AND pim.TRANS_REF > 0 AND pim.USER_ID IS NOT NULL");
        Log.e("SQL", sql.toString());
        try {
            Cursor cursor = db.rawQuery(sql.toString(), null);
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }


    public long getMonthlySessionCount(String lang) {
        long count = 0;
        StringBuilder sql = new StringBuilder();
        sql.append("select DISTINCT ( strftime('%Y-%m-%d', RECORD_DATE)) as RECORD_DATE   from patient_interview_master WHERE ( strftime('%Y-%m', RECORD_DATE))  = strftime('%Y-%m', 'now')");
        Log.e("SQL", sql.toString());
        try {
            Cursor cursor = db.rawQuery(sql.toString(), null);
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Check if any incomplete medicine requisition exist.
     *
     * @return <b>true</b> if exist. <b>false</b> otherwise
     */
    public boolean isIncompletedMedicineRequisitionExist() {
        boolean status = false;
        String sql = " SELECT REQ_ID FROM medicine_requisition_master WHERE REQ_STATUS=? OR REQ_STATUS=? ";
        Cursor cursor = db.rawQuery(sql, new String[]{RequisitionStatus.Initiated.toString(), RequisitionStatus.Accepted.toString()});
        Log.e("SQL", sql.toString());
        status = cursor.getCount() > 0;
        cursor.close();
        return status;
    }


    public int getIncompletedMedicineRequisitionId() {
        int id = -1;
        String sql = " SELECT REQ_ID FROM medicine_requisition_master WHERE REQ_STATUS=? OR REQ_STATUS=? order by REQ_ID desc ";
        Cursor cursor = db.rawQuery(sql, new String[]{RequisitionStatus.Initiated.toString(), RequisitionStatus.Accepted.toString()});
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex("REQ_ID"));
        }
        cursor.close();
        return id;
    }

    /**
     * Check if any unsent medicine received request exist.
     *
     * @return <b>true</b> if exist. <b>false</b> otherwise
     */
    public boolean isUnsentMedicineReceivedExist() {
        boolean isExist = true;

        String sql = " SELECT REQ_NO FROM medicine_receive_master WHERE DATA_SENT='N' ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        isExist = cursor.getCount() > 0;

        cursor.close();
        return isExist;
    }

    /**
     * Get the requisition number based on status.
     *
     * @param status The requisition status
     * @return The requisition number
     */
    public String getReqNumber(String status) {
        String reqNo = null;
        String sql = "SELECT REQ_NO FROM medicine_requisition_master WHERE REQ_STATUS=? LIMIT 1";
        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, new String[]{status});

        if (cursor.moveToFirst()) {
            reqNo = cursor.getString(cursor.getColumnIndex("REQ_NO"));
        }
        cursor.close();
        return reqNo;
    }

    public String getSendFiles(long transref) {
        String SEND_FILES = " ";
        String sql = " select  ifnull(GROUP_CONCAT(NAME),' ') SEND_FILES " + " from file_bank where IS_SEND=1 AND TRANS_REF=" + transref;
        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            SEND_FILES = cursor.getString(cursor.getColumnIndex("SEND_FILES"));
        }
        cursor.close();

        return SEND_FILES;
    }

    public long getRequisitionId(String REQ_NO) {
        long reqId = 0;
        String sql = "SELECT REQ_ID FROM medicine_requisition_master WHERE REQ_NO=? LIMIT 1";
        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, new String[]{REQ_NO});

        if (cursor.moveToFirst()) {
            reqId = cursor.getLong(cursor.getColumnIndex("REQ_ID"));
        }
        cursor.close();

        return reqId;
    }

    /**
     * Save medicine requisition and return the row id.
     *
     * @param medicineList The medicine list
     * @param userId       The FCM ID who made the requisition
     * @return The requisition item id
     */
    public long saveMedicineRequisition(ArrayList<MedicineInfo> medicineList, long userId) {
        // Get uncompleted requisition ID if any and delete corresponding row
        // from master and detail table

        String idSql = " SELECT REQ_ID FROM medicine_requisition_master WHERE REQ_STATUS=? OR REQ_STATUS=?";
        Log.e("SQL", idSql.toString());
        Cursor idCursor = db.rawQuery(idSql, new String[]{RequisitionStatus.Initiated.toString(), RequisitionStatus.Accepted.toString()});


        if (idCursor.moveToFirst()) {
            do {
                long itemId = idCursor.getLong(idCursor.getColumnIndex("REQ_ID"));

                // Delete old uncompleted requisition from master table
                db.delete("medicine_requisition_master", "REQ_ID=?", new String[]{Long.toString(itemId)});

                // Delete old medicine data from detail table
                db.delete("medicine_requisition_detail", "REQ_ID=?", new String[]{Long.toString(itemId)});
            } while (idCursor.moveToNext());
        }
        idCursor.close();

        long currentDate = Calendar.getInstance().getTimeInMillis();

        // Calculate Medicine total price
        double totalMedicinePrice = 0;
        for (MedicineInfo medicineInfo : medicineList) {
            if (Utility.parseInt(medicineInfo.getRequiredQuantity()) > 0) {
                totalMedicinePrice += medicineInfo.getUnitPurchasePrice() * Utility.parseInt(medicineInfo.getRequiredQuantity());
            }
        }

        // / Insert Data into master table
        ContentValues cv = new ContentValues();
        cv.put(Column.REQ_DATE, currentDate);
        cv.put(Column.REQ_STATUS, RequisitionStatus.Initiated.toString());
        cv.put(Column.REQ_USER_ID, userId);
        cv.put(Column.TOTAL_PRICE, totalMedicinePrice);

        long reqID = db.insert("medicine_requisition_master", null, cv);

        if (reqID > 0) {
            for (MedicineInfo medicineInfo : medicineList) {
                if (Utility.parseInt(medicineInfo.getRequiredQuantity()) > 0) {
                    // // Insert data into detail table
                    ContentValues cvMed = new ContentValues();
                    cvMed.put(Column.REQ_ID, reqID);
                    cvMed.put(Column.MEDICINE_ID, medicineInfo.getMedId());
                    cvMed.put(Column.QTY, medicineInfo.getRequiredQuantity());
                    cvMed.put(Column.PRICE, Utility.parseInt(medicineInfo.getRequiredQuantity()) * medicineInfo.getUnitPurchasePrice());
                    db.insert("medicine_requisition_detail", null, cvMed);
                }
            }
        }
        return reqID;
    }

    /**
     * <strong><i>public void updateRequisitionMasterTable(long itemId, long
     * reqNumber, String reqStatus)</i></strong> <br>
     * <br>
     * Update the requisition master table by new requisition number and
     * requisition status.
     *
     * @param itemId        The row ID which will be updated
     * @param reqNumber     The requisition number get from server
     * @param reqStatus     The Requisition status
     * @param completedDate The Requisition complete date
     */
    public void updateRequisitionMasterTable(long itemId, long reqNumber, String reqStatus, long completedDate) {
        ContentValues cv = new ContentValues();
        if (reqNumber > 0) cv.put(Column.REQ_NO, reqNumber);

        if (reqStatus != null) cv.put(Column.REQ_STATUS, reqStatus);
        if (completedDate > 0) cv.put(Column.COMPLETE_DATE, completedDate);

        db.update("medicine_requisition_master", cv, Column.REQ_ID + "=?", new String[]{Long.toString(itemId)});
    }


    /**
     * Gets the incomplete requisition.
     *
     * @return the incomplete requisition
     */
    public RequisitionInfo getIncompleteRequisition() {
        RequisitionInfo medReqInfo = new RequisitionInfo();

        String sql = "SELECT " + " rm.REQ_NO REQ_NO, " + " rm.`REQ_ID` REQ_ID, " + " rm.TOTAL_PRICE_DISPATCH," + " rd.`MEDICINE_ID` MEDICINE_ID, " + " m.GENERIC_NAME GENERIC_NAME, " + " m.`TYPE` TYPE, " + " m.`BRAND_NAME` BRAND_NAME," + " m.`STRENGTH` STRENGTH ," + " m.MEASURE_UNIT MEASURE_UNIT , " + " m.UNIT_SALES_PRICE UNIT_SALES_PRICE, " + " m.UNIT_PURCHASE_PRICE UNIT_PURCHASE_PRICE, " + " rd.`QTY` MEDICINE_QTY, " + " rd.`QTY_DISPATCH` QTY_DISPATCH , rd.PRICE ," + " rd.`PRICE_DISPATCH` PRICE_DISPATCH " + " FROM medicine_requisition_master rm " + " INNER JOIN medicine_requisition_detail rd on rm.`REQ_ID` = rd.`REQ_ID` " + " INNER JOIN medicine m on rd.`MEDICINE_ID` = m.`MEDICINE_ID` " + " WHERE rm.`REQ_STATUS` IN ('" + RequisitionStatus.Accepted.toString() + "') AND ifnull (rm.REQ_NO,-1)<>-1  ORDER BY m.`TYPE` , m.`BRAND_NAME` ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            medReqInfo.setRequisitionNo(cursor.getLong(cursor.getColumnIndex("REQ_NO")));
            medReqInfo.setRequisitionId(cursor.getInt(cursor.getColumnIndex("REQ_ID")));
            ArrayList<MedicineInfo> medicineList = new ArrayList<MedicineInfo>();
            do {
                MedicineInfo medicineInfo = new MedicineInfo();
                medicineInfo.setMedId(cursor.getInt(cursor.getColumnIndex("MEDICINE_ID")));
                medicineInfo.setBrandName(cursor.getString(cursor.getColumnIndex("BRAND_NAME")));
                medicineInfo.setGenericName(cursor.getString(cursor.getColumnIndex("GENERIC_NAME")));
                medicineInfo.setMedicineType(cursor.getString(cursor.getColumnIndex("TYPE")));
                medicineInfo.setStrength(cursor.getFloat(cursor.getColumnIndex("STRENGTH")));
                medicineInfo.setMeasureUnit(cursor.getString(cursor.getColumnIndex("MEASURE_UNIT")));
                medicineInfo.setRequiredQuantity(cursor.getString(cursor.getColumnIndex("MEDICINE_QTY")));
                medicineInfo.setUnitPurchasePrice(cursor.getDouble(cursor.getColumnIndex("UNIT_PURCHASE_PRICE")));
                medicineInfo.setUnitSalesPrice(cursor.getDouble(cursor.getColumnIndex("UNIT_SALES_PRICE")));
                medicineInfo.setQtyDispatch(cursor.getLong(cursor.getColumnIndex("QTY_DISPATCH")));
                medicineInfo.setPriceDispatch(cursor.getDouble(cursor.getColumnIndex("PRICE_DISPATCH")));
                medicineInfo.setPrice(cursor.getDouble(cursor.getColumnIndex("PRICE")));
                medicineList.add(medicineInfo);
            } while (cursor.moveToNext());
            medReqInfo.setMedicineList(medicineList);
        }
        cursor.close();
        return medReqInfo;
    }

    /**
     * Save medicine received information.
     *
     * @param medicineList The medicine list
     * @param userId       The FCM ID
     * @param reqNo        The requisition number
     * @param dataSent     the data sent
     * @param medReceiveId the med receive id
     */
    public void saveMedicineReceived(ArrayList<MedicineInfo> medicineList, long userId, long reqNo, String dataSent, long medReceiveId, long currentTimeInMillis) {

        // // Insert data into medicine_receive_master
        ContentValues cv = new ContentValues();

        cv.put(Column.DATA_SENT, dataSent);
        if (medReceiveId > 0) {
            db.update("medicine_receive_master", cv, Column.MED_RECEIVED_ID + "=?", new String[]{Long.toString(medReceiveId)});
        } else {
            cv.put(Column.USER_ID, userId);
            cv.put(Column.REQ_NO, reqNo);
            cv.put(Column.RECEIVED_DATE, currentTimeInMillis);
            medReceiveId = db.insert("medicine_receive_master", null, cv);

            double totalPurchasePrice = 0;
            for (MedicineInfo medicineInfo : medicineList) {
                // // Insert data into medicine_receive_detail
                ContentValues cvReceiveDetail = new ContentValues();
                cvReceiveDetail.put(Column.MED_RECEIVED_ID, medReceiveId);
                cvReceiveDetail.put(Column.MEDICINE_ID, medicineInfo.getMedId());
                cvReceiveDetail.put(Column.PRICE, medicineInfo.getTotalPrice());
                cvReceiveDetail.put(Column.QTY, medicineInfo.getQtyReceived());

                int affectedRow = db.update("medicine_receive_detail", cvReceiveDetail, Column.MED_RECEIVED_ID + "=? AND " + Column.MEDICINE_ID + "=?", new String[]{Long.toString(medReceiveId), Long.toString(medicineInfo.getMedId())});
                if (affectedRow <= 0) {
                    db.insert("medicine_receive_detail", null, cvReceiveDetail);
                    // /Update Stock table

                    if (isExist("medicine_stock", " MEDICINE_ID=" + medicineInfo.getMedId() + " AND  USER_ID=" + userId)) {
                        updateStockTable(medicineInfo.getMedId(), userId, medicineInfo.getQtyReceived(), currentTimeInMillis);

                    } else {
                        addStockTable(medicineInfo.getMedId(), userId, medicineInfo.getQtyReceived(), currentTimeInMillis);

                    }

                }

                totalPurchasePrice += medicineInfo.getTotalPrice();

                // /Update medicine table
                updateMedicinePrice(medicineInfo.getMedId(), medicineInfo.getUnitPurchasePrice(), medicineInfo.getUnitSalesPrice());
            }

            cv = new ContentValues();
            cv.put(Column.TOTAL_PRICE, totalPurchasePrice);
            db.update("medicine_receive_master", cv, Column.MED_RECEIVED_ID + "=?", new String[]{Long.toString(medReceiveId)});
        }


    }


    /**
     * Save medicine received information.
     *
     * @param medicineList The medicine list
     * @param userId       The FCM ID
     * @param reqNo        The requisition number
     * @param dataSent     the data sent
     * @param medReceiveId the med receive id
     */
    public void saveMedicineReceivedFromServer(ArrayList<MedicineInfo> medicineList, long userId, long reqNo, String dataSent, long medReceiveId, long currentTimeInMillis) {

        // // Insert data into medicine_receive_master
        ContentValues cv = new ContentValues();

        cv.put(Column.DATA_SENT, dataSent);
        if (medReceiveId > 0) {
            db.update("medicine_receive_master", cv, Column.MED_RECEIVED_ID + "=?", new String[]{Long.toString(medReceiveId)});
        } else {
            cv.put(Column.USER_ID, userId);
            cv.put(Column.REQ_NO, reqNo);
            cv.put(Column.RECEIVED_DATE, currentTimeInMillis);
            medReceiveId = db.insert("medicine_receive_master", null, cv);

            double totalPurchasePrice = 0;
            for (MedicineInfo medicineInfo : medicineList) {
                // // Insert data into medicine_receive_detail
                ContentValues cvReceiveDetail = new ContentValues();
                cvReceiveDetail.put(Column.MED_RECEIVED_ID, medReceiveId);
                cvReceiveDetail.put(Column.MEDICINE_ID, medicineInfo.getMedId());
                cvReceiveDetail.put(Column.PRICE, medicineInfo.getTotalPrice());
                cvReceiveDetail.put(Column.QTY, medicineInfo.getQtyReceived());

                int affectedRow = db.update("medicine_receive_detail", cvReceiveDetail, Column.MED_RECEIVED_ID + "=? AND " + Column.MEDICINE_ID + "=?", new String[]{Long.toString(medReceiveId), Long.toString(medicineInfo.getMedId())});
                if (affectedRow <= 0) {
                    db.insert("medicine_receive_detail", null, cvReceiveDetail);
                    // /Update Stock table

                    if (isExist("medicine_stock", " MEDICINE_ID=" + medicineInfo.getMedId() + " AND  USER_ID=" + userId)) {
                        updateStockTable(medicineInfo.getMedId(), userId, medicineInfo.getQtyReceived(), currentTimeInMillis);

                    } else {
                        addStockTable(medicineInfo.getMedId(), userId, medicineInfo.getQtyReceived(), currentTimeInMillis);

                    }

                }

                totalPurchasePrice += medicineInfo.getTotalPrice();

                // /Update medicine table
                updateMedicinePrice(medicineInfo.getMedId(), medicineInfo.getUnitPurchasePrice(), medicineInfo.getUnitSalesPrice());
            }

            cv = new ContentValues();
            cv.put(Column.TOTAL_PRICE, totalPurchasePrice);
            db.update("medicine_receive_master", cv, Column.MED_RECEIVED_ID + "=?", new String[]{Long.toString(medReceiveId)});
        }


    }

    /**
     * Update medicines prices.
     *
     * @param medicineId        The medicine id
     * @param unitPurchasePrice The new unit purchase price
     * @param unitSalesPrice    The new unit sales price
     */
    private void updateMedicinePrice(long medicineId, double unitPurchasePrice, double unitSalesPrice) {
        if (unitPurchasePrice <= 0 || unitSalesPrice <= 0) return;

        ContentValues cv = new ContentValues();
        cv.put(Column.UNIT_PURCHASE_PRICE, unitPurchasePrice);
        cv.put(Column.UNIT_SALES_PRICE, unitSalesPrice);

        db.update("medicine", cv, Column.MEDICINE_ID + "=?", new String[]{Long.toString(medicineId)});
    }

    /**
     * Get the current medicine stock.
     *
     * @param userId The FCM ID
     * @return The medicine current stock
     */
    public ArrayList<MedicineInfo> getCurrentMedicineStock(long userId, boolean onlyStockMedicine) {
        ArrayList<MedicineInfo> medicineList = null;


        StringBuilder sql = new StringBuilder();


        sql.append(" SELECT  ");
        sql.append(" m.MEDICINE_ID MEDICINE_ID,  ");
        sql.append(" m.TYPE TYPE ,m.GENERIC_NAME ,m.BRAND_NAME BRAND_NAME , ");
        sql.append(" m.STRENGTH STRENGTH ,m.MEASURE_UNIT MEASURE_UNIT,  ");
        sql.append(" ms.current_stock_qty CURRENT_QTY,  ");
        sql.append(" mmb.min_qty MINIMUM_QTY,  ");
        sql.append(" m.unit_purchase_price UNIT_PURCHASE_PRICE,  ");
        sql.append(" m.unit_sales_price UNIT_SALES_PRICE  ");
        sql.append(" FROM medicine m  ");
        if (!onlyStockMedicine) {
            sql.append(" LEFT ");
        }
        sql.append("  JOIN medicine_stock ms ON m.medicine_id = ms.medicine_id AND ms.USER_ID =" + userId);
        sql.append(" LEFT JOIN medicine_minimum_bar mmb ON m.medicine_id= mmb.medicine_id AND mmb.USER_ID=" + userId);
        sql.append(" WHERE m.STATE=1 AND m.ACCESS_TYPE='FCM' ");
        sql.append(" GROUP BY m.MEDICINE_ID  ");
        sql.append(" ORDER BY m.TYPE, m.BRAND_NAME   ");

        Log.e("SQLSTOCK", sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            medicineList = new ArrayList<MedicineInfo>();
            do {
                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setMedId(cursor.getInt(cursor.getColumnIndex("MEDICINE_ID")));
                medInfo.setBrandName(cursor.getString(cursor.getColumnIndex(Column.BRAND_NAME)));
                medInfo.setGenericName(cursor.getString(cursor.getColumnIndex(Column.GENERIC_NAME)));
                medInfo.setMedicineType(cursor.getString(cursor.getColumnIndex(Column.MEDICINE_TYPE)));
                medInfo.setStrength(cursor.getFloat(cursor.getColumnIndex(Column.STRENGTH)));
                medInfo.setMeasureUnit(cursor.getString(cursor.getColumnIndex(Column.MEASURE_UNIT)));
                medInfo.setCurrentStockQuantity(cursor.getInt(cursor.getColumnIndex("CURRENT_QTY")));
                medInfo.setMinimumStockQuantity(cursor.getInt(cursor.getColumnIndex("MINIMUM_QTY")));
                medInfo.setUnitPurchasePrice(cursor.getDouble(cursor.getColumnIndex("UNIT_PURCHASE_PRICE")));
                medInfo.setUnitSalesPrice(cursor.getDouble(cursor.getColumnIndex("UNIT_SALES_PRICE")));
                medicineList.add(medInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medicineList;
    }

    /**
     * Save the medicine stock adjustment request and return the row id.
     *
     * @param medicineList The medicine list which will be adjusted
     * @param userId       The FCM ID
     * @return The inserted row id in adjustment master table
     */
    public long saveStockAdjustmentRequest(ArrayList<AdjustmentMedicineInfo> medicineList, long userId, String requestNumber, String status) {
//        db.delete("medicine_adjustment_master", null, null);
//        db.delete("medicine_adjustment_detail", null, null);

        ContentValues cv = new ContentValues();
        cv.put(Column.USER_ID, userId);
        cv.put("REQUEST_DATE", Calendar.getInstance().getTimeInMillis());
        cv.put("STATE", status);
        if (requestNumber != null) cv.put("REQUEST_NUMBER", requestNumber);
        long itemId = db.insert("medicine_adjustment_master", null, cv);

        for (AdjustmentMedicineInfo medicineInfo : medicineList) {
            ContentValues cvAdjustmentDetail = new ContentValues();
            cvAdjustmentDetail.put("MEDICINE_ADJUST_ID", itemId);
            cvAdjustmentDetail.put("MEDICINE_ID", medicineInfo.getMedicineId());
            cvAdjustmentDetail.put("ADJUST_QTY", medicineInfo.getAdjustQuantity());
            cvAdjustmentDetail.put("PRE_RQST_QTY", medicineInfo.getCurrentStockQty());
            cvAdjustmentDetail.put("RQST_QTY", medicineInfo.getInputQty());
            db.insert("medicine_adjustment_detail", null, cvAdjustmentDetail);
        }

        return itemId;
    }

    /**
     * Update adjustment request table with request number(if any) and request
     * status. If medicine list is not null then update the stock table
     *
     * @param userId        The FCM ID
     * @param itemId        the item id
     * @param requestNumber The stock adjustment request number
     * @param requestStatus The request status
     * @param medicineList  The medicine list
     */
    public void updateStockAdjustment(long userId, long itemId, String requestNumber, StockAdjustmentRequestStatus requestStatus, ArrayList<MedicineInfo> medicineList) {
        ContentValues cv = new ContentValues();
        if (requestNumber != null) cv.put("REQUEST_NUMBER", requestNumber);

        if (requestStatus != null) cv.put("STATE", requestStatus.toString());

        db.update("medicine_adjustment_master", cv, "MEDICINE_ADJUST_ID=?", new String[]{Long.toString(itemId)});

        if (medicineList != null) {
//          for (MedicineInfo medInfo : medicineList) {
//              // / Here availableQuanity represent the actual medicine
//              // quantity
//              updateStockTable(medInfo.getMedId(), userId,
//                      (medInfo.getAvailableQuantity() - medInfo
//                              .getCurrentStockQuantity()));
//          }
        }
    }

    /**
     * Check if there is any incomplete adjustment request.
     *
     * @return <b>true</b> if exist. <b>false</b> otherwise
     */
    public boolean isInCompleteAdjustmentRequestExist() {
        boolean status = false;
        String sql = " SELECT MEDICINE_ADJUST_ID FROM medicine_adjustment_master WHERE STATE=? OR STATE=?  ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{StockAdjustmentRequestStatus.OPEN.toString(), StockAdjustmentRequestStatus.RECOMMENDED.toString()});

        status = cursor.getCount() > 0;

        cursor.close();

        return status;
    }

    public String getAdjustmentRequestNumber() {
        String number = "";
        String sql = " SELECT IFNULL(REQUEST_NUMBER,'' ) REQUEST_NUMBER FROM medicine_adjustment_master WHERE STATE=? OR STATE=? order by MEDICINE_ADJUST_ID DESC limit 1 ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{StockAdjustmentRequestStatus.OPEN.toString(), StockAdjustmentRequestStatus.RECOMMENDED.toString()});

        if (cursor.moveToFirst()) {
            number = cursor.getString(cursor.getColumnIndex("REQUEST_NUMBER"));
        }

        cursor.close();

        return number;
    }


    public String getRequsitionRequestNumber() {
        String number = "";
        String sql = " SELECT IFNULL(REQ_NO,'' ) REQUEST_NUMBER FROM medicine_requisition_master WHERE REQ_STATUS=? or REQ_STATUS=?  order by REQ_ID DESC limit 1 ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{RequisitionStatus.Initiated.toString(), RequisitionStatus.Accepted.toString()});
        if (cursor.moveToFirst()) {
            number = cursor.getString(cursor.getColumnIndex("REQUEST_NUMBER"));
        }
        cursor.close();
        return number;
    }

    /**
     * Get the last OPEN stock adjustment request information.
     *
     * @return Last OPEN stock adjustment request information if any otherwise
     * <b>null</b>
     */
    public StockAdjustmentInfo getStockAdjustmentInfo() {
        StockAdjustmentInfo stockAdjustmentInfo = null;
        String sql = "SELECT  m.TYPE||' '||m.BRAND_NAME||' '||m.STRENGTH||m.MEASURE_UNIT MEDICINE_NAME, REQUEST_NUMBER, mam.MEDICINE_ADJUST_ID MEDICINE_ADJUST_ID," + " ADJUST_QTY,mad.PRE_RQST_QTY,mad.RQST_QTY, mad.MEDICINE_ID MEDICINE_ID,mam.STATE STATE FROM medicine_adjustment_master mam " + "INNER JOIN medicine_adjustment_detail mad ON mam.MEDICINE_ADJUST_ID = mad.MEDICINE_ADJUST_ID " + "INNER JOIN medicine m ON mad.MEDICINE_ID = m.MEDICINE_ID " + "WHERE mam.STATE IN ('Open','Recommended') ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            stockAdjustmentInfo = new StockAdjustmentInfo();
            stockAdjustmentInfo.setAdjustmentId(cursor.getInt(cursor.getColumnIndex("MEDICINE_ADJUST_ID")));
            stockAdjustmentInfo.setRequestNumber(cursor.getString(cursor.getColumnIndex("REQUEST_NUMBER")));
            stockAdjustmentInfo.setState(cursor.getString(cursor.getColumnIndex("STATE")));

            ArrayList<AdjustmentMedicineInfo> medList = new ArrayList<AdjustmentMedicineInfo>();
            do {
                AdjustmentMedicineInfo medInfo = new AdjustmentMedicineInfo();
                medInfo.setMedicineId(cursor.getInt(cursor.getColumnIndex("MEDICINE_ID")));

                medInfo.setAdjustQuantity(cursor.getInt(cursor.getColumnIndex("ADJUST_QTY")));
                medInfo.setMedicineName(cursor.getString(cursor.getColumnIndex("MEDICINE_NAME")));
                medInfo.setCurrentStockQty(cursor.getInt(cursor.getColumnIndex("PRE_RQST_QTY")));
                medInfo.setInputQty(cursor.getInt(cursor.getColumnIndex("RQST_QTY")));
                medList.add(medInfo);
            } while (cursor.moveToNext());
            stockAdjustmentInfo.setMedicineList(medList);

        }
        cursor.close();

        return stockAdjustmentInfo;
    }

    public ArrayList<StockAdjustmentInfo> getStockAdjustmentInfoList() {
        ArrayList<StockAdjustmentInfo> stockAdjustmentInfoList = new ArrayList<>();
        String sql = "SELECT  m.TYPE||' '||m.BRAND_NAME||' '||m.STRENGTH||m.MEASURE_UNIT MEDICINE_NAME, REQUEST_NUMBER, mam.MEDICINE_ADJUST_ID MEDICINE_ADJUST_ID, mam.REQUEST_DATE REQUEST_DATE," + " ADJUST_QTY,mad.PRE_RQST_QTY,mad.RQST_QTY, mad.MEDICINE_ID MEDICINE_ID,mam.STATE STATE FROM medicine_adjustment_master mam " + "INNER JOIN medicine_adjustment_detail mad ON mam.MEDICINE_ADJUST_ID = mad.MEDICINE_ADJUST_ID " + "INNER JOIN medicine m ON mad.MEDICINE_ID = m.MEDICINE_ID " + "WHERE mam.STATE IN ('Open','Recommended') ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            StockAdjustmentInfo stockAdjustmentInfo = new StockAdjustmentInfo();
            stockAdjustmentInfo.setAdjustmentId(cursor.getInt(cursor.getColumnIndex("MEDICINE_ADJUST_ID")));
            stockAdjustmentInfo.setRequestNumber(cursor.getString(cursor.getColumnIndex("REQUEST_NUMBER")));
            stockAdjustmentInfo.setState(cursor.getString(cursor.getColumnIndex("STATE")));
            stockAdjustmentInfo.setRequisitionDate(cursor.getString(cursor.getColumnIndex("REQUEST_DATE")));

            ArrayList<AdjustmentMedicineInfo> medList = new ArrayList<AdjustmentMedicineInfo>();
            do {
                AdjustmentMedicineInfo medInfo = new AdjustmentMedicineInfo();
                medInfo.setMedicineId(cursor.getInt(cursor.getColumnIndex("MEDICINE_ID")));

                medInfo.setAdjustQuantity(cursor.getInt(cursor.getColumnIndex("ADJUST_QTY")));
                medInfo.setMedicineName(cursor.getString(cursor.getColumnIndex("MEDICINE_NAME")));
                medInfo.setCurrentStockQty(cursor.getInt(cursor.getColumnIndex("PRE_RQST_QTY")));
                medInfo.setInputQty(cursor.getInt(cursor.getColumnIndex("RQST_QTY")));
                medList.add(medInfo);
            } while (cursor.moveToNext());
            stockAdjustmentInfo.setMedicineList(medList);
            stockAdjustmentInfoList.add(stockAdjustmentInfo);
        }
        cursor.close();

        return stockAdjustmentInfoList;
    }


    /**
     * Parse the medicineListJson. Get medicine information list from database
     * based on medicine id get from medicinelistJson and return the list
     *
     * @param jMedicineArr the j medicine arr
     * @return The medicine list get from database
     * @throws JSONException the JSON exception
     */
    public ArrayList<AdjustmentMedicineInfo> getMedicineList(JSONArray jMedicineArr) throws JSONException {
        ArrayList<AdjustmentMedicineInfo> medicineList = new ArrayList<AdjustmentMedicineInfo>();

        for (int i = 0; i < jMedicineArr.length(); i++) {
            JSONObject jMedicineObj = jMedicineArr.getJSONObject(i);

            int medicineId = jMedicineObj.getInt("MEDICINE_ID");
            int adjustQty = jMedicineObj.getInt("ADJUST_QTY");
            long updatedOn = jMedicineObj.getLong("UPDATED_ON");

            AdjustmentMedicineInfo medicineInfo = new AdjustmentMedicineInfo();
            medicineInfo.setMedicineId(medicineId);
            medicineInfo.setAdjustQuantity(adjustQty);
            medicineInfo.setUpdatedOn(updatedOn);

            String sql = "SELECT  TYPE||' '||BRAND_NAME||' '||STRENGTH||MEASURE_UNIT MEDICINE_NAME " + "FROM medicine " + "WHERE MEDICINE_ID=?";
            Log.e("SQL", sql.toString());
            Cursor cursor = db.rawQuery(sql, new String[]{Integer.toString(medicineId)});

            if (cursor.moveToFirst()) {
                medicineInfo.setMedicineName(cursor.getString(cursor.getColumnIndex("MEDICINE_NAME")));
                medicineList.add(medicineInfo);
            }
            cursor.close();
        }

        return medicineList;
    }

    /**
     * Retrieve last seven requisition from database and return.
     *
     * @return Last seven requisitions
     */


    public ArrayList<RequisitionInfo> getLast7Requisition() {
        String sql = "SELECT mrqm.REQ_NO REQ_NO, " + "IFNULL(mrqm.TOTAL_PRICE,0.00) AS REQ_PRICE, " + "IFNULL(mrcm.TOTAL_PRICE, 0.00) AS REC_PRICE, " + "strftime('%d-%m-%Y',mrqm.REQ_DATE/1000, 'unixepoch', 'localtime') REQ_DATE, " + "mrqm.REQ_STATUS REQ_STATUS, " + "mrqm.REQ_ID REQ_ID " + "FROM medicine_requisition_master mrqm " + "LEFT JOIN medicine_receive_master mrcm ON mrqm.REQ_NO = mrcm.REQ_NO " + "ORDER BY mrqm.REQ_DATE DESC LIMIT 7";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<RequisitionInfo> requisitionList = new ArrayList<RequisitionInfo>();
        if (cursor.moveToFirst()) {

            do {
                RequisitionInfo reqInfo = new RequisitionInfo();
                reqInfo.setRequisitionId(cursor.getInt(cursor.getColumnIndex("REQ_ID")));
                reqInfo.setRequisitionNo(cursor.getLong(cursor.getColumnIndex("REQ_NO")));
                reqInfo.setReceiveMedicinePrice(cursor.getDouble(cursor.getColumnIndex("REC_PRICE")));
                reqInfo.setRequisitionMedicinePrice(cursor.getDouble(cursor.getColumnIndex("REQ_PRICE")));
                reqInfo.setRequisitionDate(cursor.getString(cursor.getColumnIndex("REQ_DATE")));
                reqInfo.setRequisitionStatus(cursor.getString(cursor.getColumnIndex("REQ_STATUS")));

                requisitionList.add(reqInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return requisitionList;
    }

    public ArrayList<RequisitionInfo> getAllRequisition() {
        String sql = "SELECT mrqm.REQ_NO REQ_NO, " + "IFNULL(mrqm.TOTAL_PRICE,0.00) AS REQ_PRICE, " + "IFNULL(mrcm.TOTAL_PRICE, 0.00) AS REC_PRICE, " + "strftime('%d-%m-%Y',mrqm.REQ_DATE/1000, 'unixepoch', 'localtime') REQ_DATE, " + "mrqm.REQ_STATUS REQ_STATUS, " + "mrqm.REQ_ID REQ_ID " + "FROM medicine_requisition_master mrqm " + "LEFT JOIN medicine_receive_master mrcm ON mrqm.REQ_NO = mrcm.REQ_NO " + "  ORDER BY mrqm.REQ_DATE,REQ_NO DESC ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<RequisitionInfo> requisitionList = new ArrayList<RequisitionInfo>();
        if (cursor.moveToFirst()) {

            do {
                RequisitionInfo reqInfo = new RequisitionInfo();
                reqInfo.setRequisitionId(cursor.getInt(cursor.getColumnIndex("REQ_ID")));
                reqInfo.setRequisitionNo(cursor.getLong(cursor.getColumnIndex("REQ_NO")));
                reqInfo.setReceiveMedicinePrice(cursor.getDouble(cursor.getColumnIndex("REC_PRICE")));
                reqInfo.setRequisitionMedicinePrice(cursor.getDouble(cursor.getColumnIndex("REQ_PRICE")));
                reqInfo.setRequisitionDate(cursor.getString(cursor.getColumnIndex("REQ_DATE")));
                reqInfo.setRequisitionStatus(cursor.getString(cursor.getColumnIndex("REQ_STATUS")));

                requisitionList.add(reqInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return requisitionList;
    }


    /**
     * Clear Adjustment master and detail table.
     */
    public void removeAdjustmentRequest() {
        db.delete("medicine_adjustment_master", null, null);
        db.delete("medicine_adjustment_detail", null, null);
    }


    public void removeRequisitionRequest(long reqId) {
        db.delete("medicine_requisition_master", "REQ_ID=" + reqId, null);
        db.delete("medicine_requisition_master", "REQ_ID=" + reqId, null);
    }


    /**
     * Save requisition list into database.
     *
     * @param requisitionList the requisition list
     * @param userId          the user id
     */
    public void saveRequisitionList(ArrayList<RequisitionInfo> requisitionList, long userId, JSONObject param) {
        long count = 0;
        for (RequisitionInfo requisitionInfo : requisitionList) {

            ContentValues cvRequsition = new ContentValues();
            cvRequsition.put("REQ_NO", requisitionInfo.getRequisitionNo());

            try {
                long dateInMillis = Utility.getMillisecondFromDate(requisitionInfo.getRequisitionDate(), Constants.DATE_FORMAT_YYYY_MM_DD);
                cvRequsition.put("REQ_DATE", dateInMillis);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            cvRequsition.put("REQ_STATUS", requisitionInfo.getRequisitionStatus());
            try {
                long dateInMili = Utility.getMillisecondFromDate(requisitionInfo.getCompleteDate(), Constants.DATE_FORMAT_YYYY_MM_DD);
                cvRequsition.put("COMPLETE_DATE", dateInMili);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            cvRequsition.put("TOTAL_PRICE", requisitionInfo.getRequisitionMedicinePrice());
            cvRequsition.put("REQ_USER_ID", userId);

            String whereCond = "REQ_NO=" + requisitionInfo.getRequisitionNo();
            if (isExist("medicine_requisition_master", whereCond)) {
                db.update("medicine_requisition_master", cvRequsition, whereCond, null);
            } else {
                long reqId = db.insert("medicine_requisition_master", null, cvRequsition);
                for (RequisitionMedicineInfo medInfo : requisitionInfo.getRequisitionMedicineList()) {
                    ContentValues cvMedReq = new ContentValues();
                    cvMedReq.put("REQ_ID", reqId);
                    cvMedReq.put("MEDICINE_ID", medInfo.getMedId());
                    cvMedReq.put("QTY", medInfo.getRequisitionQuantity());
                    cvMedReq.put("PRICE", medInfo.getRequisitionPrice());
                    db.insert("medicine_requisition_detail", null, cvMedReq);
                }
            }

            // / Check if requisition is received
            if (requisitionInfo.getRequisitionStatus() != null && requisitionInfo.getRequisitionStatus().equalsIgnoreCase(RequisitionStatus.Completed.toString())) {
                ContentValues cvReceive = new ContentValues();
                cvReceive.put("REQ_NO", requisitionInfo.getRequisitionNo());
                cvReceive.put("USER_ID", userId);
                cvReceive.put("TOTAL_PRICE", requisitionInfo.getReceiveMedicinePrice());
                try {
                    long dateInMillis = Utility.getMillisecondFromDate(requisitionInfo.getReceiveDate(), Constants.DATE_FORMAT_YYYY_MM_DD);
                    cvReceive.put("RECEIVED_DATE", dateInMillis);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                cvReceive.put("DATA_SENT", "Y");
                if (!isExist("medicine_receive_master", whereCond)) {
                    long recId = db.insert("medicine_receive_master", null, cvReceive);
                    for (RequisitionMedicineInfo medInfo : requisitionInfo.getRequisitionMedicineList()) {
                        ContentValues cvMedRec = new ContentValues();
                        cvMedRec.put("MED_RECEIVED_ID", recId);
                        cvMedRec.put("MEDICINE_ID", medInfo.getMedId());
                        cvMedRec.put("QTY", medInfo.getReceiveQuantity());
                        cvMedRec.put("PRICE", medInfo.getReceivePrice());
                        db.insert("medicine_receive_detail", null, cvMedRec);
                    }
                }
            }
            if (requisitionInfo.getRequisitionAcceptedDetail().length() > 0) {

                try {
                    JSONObject getRequisitionAcceptedDetail = new JSONObject(requisitionInfo.getRequisitionAcceptedDetail());
                    Double tpd = JSONParser.getDouble(getRequisitionAcceptedDetail, "TOTAL_PRICE_DISPATCH");
                    String reqStatus = JSONParser.getString(getRequisitionAcceptedDetail, Column.REQ_STATUS);
                    JSONArray requisitionDetail = JSONParser.getJsonArray(getRequisitionAcceptedDetail, "medicine_requisition_detail");
                    App.getContext().getDB().updateRequisition("" + requisitionInfo.getRequisitionNo(), reqStatus, tpd, requisitionDetail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            count++;
        }
        updateDataVersion("medicine_requisition_master", requisitionList.size(), count, param, KEY.VERSION_NO_MEDICINE_REQUISITION);
    }


    /**
     * Save requisition list into database.
     *
     * @param consumptionlist the consumption list
     * @param userId          the user id
     */
    public void saveConsumptionList(ArrayList<MedicineConsumptionInfoModel> medicineConsumptionInfoModelList, long userId) {
        for (MedicineConsumptionInfoModel medicineConsumptionInfoModel : medicineConsumptionInfoModelList) {

            ContentValues cvConsumption = new ContentValues();
            cvConsumption.put("INTERVIEW_ID", medicineConsumptionInfoModel.getInterviewId());

            try {
                long dateInMillis = Utility.getMillisecondFromDate(medicineConsumptionInfoModel.getConsumptionDate(), Constants.DATE_FORMAT_YYYY_MM_DD);
                cvConsumption.put("CONSUMP_DATE", dateInMillis);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            cvConsumption.put("BENEF_CODE", medicineConsumptionInfoModel.getBenefCode());
            cvConsumption.put("MED_CONSUMP_ID", medicineConsumptionInfoModel.getMedConsumpId());
            cvConsumption.put("LOCATION_ID", medicineConsumptionInfoModel.getLocationId());
            cvConsumption.put("USER_ID", medicineConsumptionInfoModel.getLocationId());
            cvConsumption.put("TOTAL_PRICE", medicineConsumptionInfoModel.getTotalPrice());
            cvConsumption.put("REMARKS", medicineConsumptionInfoModel.getRemarks());
            cvConsumption.put("DATA_SENT", medicineConsumptionInfoModel.getDataSent());
            cvConsumption.put("VERSION_NO", medicineConsumptionInfoModel.getVersionNo());


            String whereCond = "MED_CONSUMP_ID=" + medicineConsumptionInfoModel.getMedConsumpId();
            if (isExist("medicine_consumption_master", whereCond)) {
                db.update("medicine_consumption_master", cvConsumption, whereCond, null);
            } else {
                long consumpId = db.insert("medicine_consumption_master", null, cvConsumption);
                for (ConsumptionDetailsModel conDetail : medicineConsumptionInfoModel.getConsumptionDetails()) {
                    ContentValues cvConDetails = new ContentValues();
                    cvConDetails.put("MED_CONSUMP_ID", conDetail.getMed_consump_id());
                    cvConDetails.put("MEDICINE_ID", conDetail.getMed_id());

                    cvConDetails.put("QTY", conDetail.getQty());
                    cvConDetails.put("PRICE", conDetail.getPrice());
                    db.insert("medicine_consumption_detail", null, cvConDetails);
                }
            }
        }
    }


    /**
     * Save requisition list into database.
     *
     * @param consumptionlist the consumption list
     * @param userId          the user id
     */
    public void saveAdjustmentList(ArrayList<MedicineAdjustmentInfoModel> medicineAdjustmentInfoModelslist, long userId) {
        for (MedicineAdjustmentInfoModel medicineAdjustmentInfoModelSingle : medicineAdjustmentInfoModelslist) {

            ContentValues cvAdjustmentInfo = new ContentValues();
            cvAdjustmentInfo.put("MEDICINE_ADJUST_ID", medicineAdjustmentInfoModelSingle.getMedicine_adjust_id());

            try {
                long dateInMillis = Utility.getMillisecondFromDate(medicineAdjustmentInfoModelSingle.getRequest_date(), Constants.DATE_FORMAT_YYYY_MM_DD);
                cvAdjustmentInfo.put("REQUEST_DATE", dateInMillis);


                long dateInMillisApproved = Utility.getMillisecondFromDate(medicineAdjustmentInfoModelSingle.getApproved_on(), Constants.DATE_FORMAT_YYYY_MM_DD);
                cvAdjustmentInfo.put("APPROVED_ON", dateInMillisApproved);


//
//                long dateInMillisRecordDate = Utility.getMillisecondFromDate(medicineAdjustmentInfoModelSingle.getRecordDate(), Constants.DATE_FORMAT_YYYY_MM_DD);
//                cvAdjustmentInfo.put("RECORD_DATE", dateInMillisRecordDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            cvAdjustmentInfo.put("USER_ID", medicineAdjustmentInfoModelSingle.getUser_id());
            cvAdjustmentInfo.put("REQUEST_NUMBER", medicineAdjustmentInfoModelSingle.getRequest_number());
//
            cvAdjustmentInfo.put("APPROVED_BY", medicineAdjustmentInfoModelSingle.getApproved_by());
            cvAdjustmentInfo.put("VERSION_NO", medicineAdjustmentInfoModelSingle.getVersionNo());
//            cvAdjustmentInfo.put("FCM_FETCH_ON", medicineAdjustmentInfoModelSingle.getFcm_fetch_on());
//            cvAdjustmentInfo.put("STATE", medicineAdjustmentInfoModelSingle.getState());


            String whereCond = "MEDICINE_ADJUST_ID=" + medicineAdjustmentInfoModelSingle.getMedicine_adjust_id();
            if (isExist("medicine_adjustment_master", whereCond)) {
                db.update("medicine_adjustment_master", cvAdjustmentInfo, whereCond, null);
            } else {
                long adjustId = db.insert("medicine_adjustment_master", null, cvAdjustmentInfo);
                for (MedicineAdjustmentDetailModel adDetail : medicineAdjustmentInfoModelSingle.getAdjustmentDetailModels()) {
                    ContentValues cvAdjustDetails = new ContentValues();
                    cvAdjustDetails.put("MEDICINE_ADJUST_DTL_ID", adDetail.getMedicine_adjust_dtl_id());
                    cvAdjustDetails.put("MEDICINE_ADJUST_ID", adDetail.getMedicine_adjust_id());
                    cvAdjustDetails.put("MEDICINE_ID", adDetail.getMedicine_id());
                    cvAdjustDetails.put("ADJUST_QTY", adDetail.getAdjust_qty());
                    cvAdjustDetails.put("PRE_RQST_QTY", adDetail.getPre_rqst_qty());
                    cvAdjustDetails.put("RQST_QTY", adDetail.getRqst_qty());
                    cvAdjustDetails.put("RCMND_QTY", adDetail.getRcmnd_qty());
                    cvAdjustDetails.put("APPVD_QTY", adDetail.getApproved_qty());
                    db.insert("medicine_adjustment_detail", null, cvAdjustDetails);
                }
            }


        }
    }


    /**
     * Save requisition list into database.
     *
     * @param consumptionlist the consumption list
     * @param userId          the user id
     */
    public void saveMedicineReceivedList(ArrayList<MedicineReceiveModel> medicineReceiveList, long userId) {
        for (MedicineReceiveModel medicineReceiveModel : medicineReceiveList) {

            ContentValues cvConsumption = new ContentValues();
            cvConsumption.put("TOTAL_PRICE", medicineReceiveModel.getTotal_price());

            try {
                long dateInMillis = Utility.getMillisecondFromDate(medicineReceiveModel.getMedicine_receive_date(), Constants.DATE_FORMAT_YYYY_MM_DD);
                cvConsumption.put("RECEIVED_DATE", dateInMillis);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            cvConsumption.put("MED_RECEIVED_ID", medicineReceiveModel.getMedicine_received_id());
            cvConsumption.put("LOCATION_ID", medicineReceiveModel.getLocation_id());
            cvConsumption.put("SUPPLIER_ID", medicineReceiveModel.getSupplier_id());
            cvConsumption.put("USER_ID", medicineReceiveModel.getUser_id());
            cvConsumption.put("VERSION_NO", medicineReceiveModel.getVersionNo());
            cvConsumption.put("DATA_SENT", "Y");


            String whereCond = "MED_RECEIVED_ID=" + medicineReceiveModel.getMedicine_received_id();
            if (isExist("medicine_receive_master", whereCond)) {
                db.update("medicine_receive_master", cvConsumption, whereCond, null);
            } else {
                long consumpId = db.insert("medicine_receive_master", null, cvConsumption);
                for (MedicineReceivedDetailModel rcvDetail : medicineReceiveModel.getMedicineReceivedDetailList()) {
                    ContentValues cvConDetails = new ContentValues();
                    cvConDetails.put("PRICE", rcvDetail.getPrice());
                    cvConDetails.put("MED_RECEIVED_DTL_ID", rcvDetail.getMedicineReceivedDtlId());
                    cvConDetails.put("MED_RECEIVED_ID", rcvDetail.getMedicineReceivedId());
                    cvConDetails.put("MEDICINE_ID", rcvDetail.getMedicineId());
                    cvConDetails.put("QTY", rcvDetail.getQty());
                    db.insert("medicine_receive_detail", null, cvConDetails);
                }
            }


        }
    }


    /**
     * Retrieve requisition medicine information list from database based in
     * requisition ID.
     *
     * @param requisitionId The requisition ID
     * @return The requisition information based in requisition ID
     */
    public RequisitionInfo getRequisitionMedicineInfo(long requisitionId) {
        RequisitionInfo requisitionInfo = null;
        String sql = "SELECT  mrqm.REQ_STATUS,  " + " mrqm.REQ_ID REQ_ID,mrqm.REQ_DATE REQ_DATE," + " IFNULL(mrqm.REQ_NO,0) REQ_NO," + " mrqm.TOTAL_PRICE REQ_PRICE," + " mrqd.medicine_id REQ_MED_ID," + " m.GENERIC_NAME GENERIC_NAME, " + " m.`TYPE` TYPE, " + " m.`BRAND_NAME` BRAND_NAME," + " m.`STRENGTH` STRENGTH ," + " m.MEASURE_UNIT MEASURE_UNIT , " + " mrqd.QTY REQ_QTY," + " mrqd.PRICE REQ_MED_PRICE," + " IFNULL( mrcm.TOTAL_PRICE,0) REC_PRICE," + " mrcd.medicine_id REC_MED_ID," + " mrcd.QTY REC_QTY," + " IFNULL(mrcd.PRICE,0) REC_MED_PRICE "

                + " FROM medicine_requisition_master mrqm " + " INNER JOIN medicine_requisition_detail mrqd ON mrqd.REQ_ID=mrqm.REQ_ID " + " LEFT JOIN medicine_receive_master mrcm ON mrcm.REQ_NO=mrqm.REQ_NO " + " LEFT JOIN medicine_receive_detail mrcd ON mrcm.MED_RECEIVED_ID=mrcd.MED_RECEIVED_ID AND mrqd.medicine_id = mrcd.medicine_id " + " INNER JOIN medicine m ON mrqd.medicine_id = m.medicine_id " + " WHERE mrqm.REQ_ID=? ORDER BY m.TYPE, m.BRAND_NAME ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(requisitionId)});

        if (cursor.moveToFirst()) {
            requisitionInfo = new RequisitionInfo();
            requisitionInfo.setRequisitionId(requisitionId);


            requisitionInfo.setRequisitionNo(Utility.parseLong(cursor.getString(cursor.getColumnIndex("REQ_NO"))));

            requisitionInfo.setRequisitionMedicinePrice(cursor.getDouble(cursor.getColumnIndex("REQ_PRICE")));
            requisitionInfo.setRequisitionStatus(cursor.getString(cursor.getColumnIndex("REQ_STATUS")));
            requisitionInfo.setRequisitionDate(cursor.getString(cursor.getColumnIndex("REQ_DATE")));

            requisitionInfo.setReceiveMedicinePrice(Utility.parseDouble(cursor.getString(cursor.getColumnIndex("REC_PRICE"))));

            ArrayList<RequisitionMedicineInfo> medList = new ArrayList<RequisitionMedicineInfo>();

            do {
                RequisitionMedicineInfo reqMedInfo = new RequisitionMedicineInfo();
                reqMedInfo.setMedId(cursor.getLong(cursor.getColumnIndex("REQ_MED_ID")));

                reqMedInfo.setBrandName(cursor.getString(cursor.getColumnIndex("BRAND_NAME")));
                reqMedInfo.setGenericName(cursor.getString(cursor.getColumnIndex("GENERIC_NAME")));
                reqMedInfo.setMedicineType(cursor.getString(cursor.getColumnIndex("TYPE")));
                reqMedInfo.setStrength(cursor.getFloat(cursor.getColumnIndex("STRENGTH")));
                reqMedInfo.setMeasureUnit(cursor.getString(cursor.getColumnIndex("MEASURE_UNIT")));


                reqMedInfo.setRequisitionQuantity(cursor.getInt(cursor.getColumnIndex("REQ_QTY")));
                int recQty = Utility.parseInt(cursor.getString(cursor.getColumnIndex("REC_QTY")));
                reqMedInfo.setReceiveQuantity(recQty);

                reqMedInfo.setRequisitionPrice(cursor.getDouble(cursor.getColumnIndex("REQ_MED_PRICE")));

                reqMedInfo.setReceivePrice(Utility.parseDouble(cursor.getString(cursor.getColumnIndex("REC_MED_PRICE"))));

                medList.add(reqMedInfo);

            } while (cursor.moveToNext());
            requisitionInfo.setRequisitionMedicineList(medList);
        }

        cursor.close();

        return requisitionInfo;
    }

    /**
     * Check if basic information of a household with <b>householdNumber</b> has
     * already been taken.
     *
     * @param householdNumber The household number
     * @return <b>true</b> if the information has already been taken.
     * <b>false</b> otherwise
     */
    public boolean isHouseholdNumberExist(String householdNumber) {
        String sql = "SELECT HH_NUMBER FROM household WHERE HH_NUMBER=?";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{householdNumber});

        int numberOfRow = cursor.getCount();
        cursor.close();
        return numberOfRow > 0;
    }

    /**
     * Count number of householdbasic data
     *
     * @return long
     */
    public long countHouseHoldData() {
        String sql = " SELECT DISTINCT(HH_NUMBER) FROM household where state > 0  " + " and substr(HH_NUMBER,10,3)!='000' ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        int numberOfRow = cursor.getCount();
        cursor.close();
        if (numberOfRow > 0) return numberOfRow;
        else return 0;
    }

    public long countHouseHoldData(int stateValue) {
        String sql = null;
        if (stateValue > 0) {
            sql = " SELECT DISTINCT(HH_NUMBER) FROM household where state > 0  " + " and substr(HH_NUMBER,10,3)!='000' ";
        } else {
            sql = " SELECT DISTINCT(HH_NUMBER) FROM household where state = 0  " + " and substr(HH_NUMBER,10,3)!='000' ";
        }

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        int numberOfRow = cursor.getCount();
        cursor.close();
        if (numberOfRow > 0) return numberOfRow;
        else return 0;
    }

    /**
     * Count beneficiary from householdbasic data
     *
     * @return long
     */
    public long countBeneficiaryFromHouseHold() {
        long count = 0;
        String sql = " SELECT SUM(NO_OF_FAMILY_MEMBER)  MEMBER FROM household  where state > 0  " + " and substr(HH_NUMBER,10,3)!='000' ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            count = cursor.getLong(cursor.getColumnIndex("MEMBER"));
        }
        cursor.close();
        return count;
    }

    public long countBeneficiaryFromHouseHold(int stateValue) {
        long count = 0;
        String sql = null;
        if (stateValue > 0) {
            sql = " SELECT SUM(NO_OF_FAMILY_MEMBER)  MEMBER FROM household  where state > 0  " + " and substr(HH_NUMBER,10,3)!='000' ";
        } else {
            sql = " SELECT SUM(NO_OF_FAMILY_MEMBER)  MEMBER FROM household  where state = 0  " + " and substr(HH_NUMBER,10,3)!='000' ";
        }

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            count = cursor.getLong(cursor.getColumnIndex("MEMBER"));
        }
        cursor.close();
        return count;
    }


    public void saveHousehold(Household household) {

        ContentValues cv = new ContentValues();
        cv.put(Column.HH_NUMBER, household.getHhNumber());
        cv.put(Column.HH_NAME, household.getHhName());
        cv.put(Column.NO_OF_FAMILY_MEMBER, household.getNoOfFamilyMember());
        cv.put(Column.UPDATE_HISTORY, household.getUpdateHistory());
        cv.put(Column.MONTHLY_FAMILY_EXPENDITURE, household.getMonthlyFamilyExpenditure());
        cv.put(Column.HH_CHARACTER, household.getHhCharacter());
        cv.put(Column.HH_ADULT_WOMEN, household.getHhAdultWomen());

        if (household.getLongitude() != 0) {
            cv.put(Column.LONGITUDE, household.getLongitude());
        }
        if (household.getLatitude() != 0) {
            cv.put(Column.LATITUDE, household.getLatitude());
        }

        cv.put(Column.SENT, 0);
        if (household.getState() == 2) {
            household.setHhNumber(household.getFullHouseHoldNumber());
            cv.put(Column.HH_NUMBER, household.getHhNumber());
            cv.put(Column.STATE, 2);
        } else if (household.getState() == 3) {
            household.setHhNumber(household.getFullHouseHoldNumber());
            cv.put(Column.HH_NUMBER, household.getHhNumber());
            cv.put(Column.STATE, 3);
            cv.put(Column.SENT, 1);
        } else {
            cv.put(Column.STATE, 1);
        }


        if (isHouseholdExist(household.getHhNumber())) {
            try {
                db.update("household", cv, "HH_NUMBER=?", new String[]{household.getHhNumber()});
            } catch (Exception e) {

            }

//			db.update("household", cv, "HH_NUMBER='" + householdNumber
//					+ "'", null);
        } else {
            cv.put("REG_DATE", household.getRegDate());
            db.insert("household", null, cv);
        }
    }

    public void saveHouseholds(ArrayList<Household> households, JSONObject param) {
        ContentValues cv = new ContentValues();
        long count = 0;
        for (Household household : households) {
            cv.put(Column.HH_ID, household.getHhId());
            cv.put(Column.HH_NUMBER, household.getHhNumber());
            cv.put(Column.LOCATION_ID, household.getLocationId());
            cv.put(Column.NO_OF_FAMILY_MEMBER, household.getNoOfFamilyMember());
            cv.put(Column.MONTHLY_FAMILY_EXPENDITURE, household.getMonthlyFamilyExpenditure());
            cv.put(Column.HH_CHARACTER, household.getHhCharacter());
            cv.put(Column.HH_ADULT_WOMEN, household.getHhAdultWomen());
            cv.put(Column.HH_NAME, household.getHhName());
            cv.put(Column.HH_GRADE, household.getHhGrade());
            cv.put(Column.CREATE_DATE, household.getCreateDate());
            cv.put(Column.REG_DATE, household.getRegDate());
            cv.put(Column.LATITUDE, household.getLatitudeStr());
            cv.put(Column.LONGITUDE, household.getLongitudeStr());
            cv.put(Column.REF_DATA_ID, household.getRefDataId());
            cv.put(Column.STATE, household.getState());
            cv.put(Column.UPDATE_HISTORY, household.getUpdateHistory());
            cv.put(Column.SENT, 1);
            db.replace(DBTable.HOUSEHOLD, null, cv);
            cv.clear();
            count++;
        }
        //updateDataVersion(DBTable.HOUSEHOLD, households.size(), count, param, KEY.VERSION_NO_HOUSEHOLD);
    }

    /**
     * Get all household basic information list.
     *
     * @return The information list
     */

    public ArrayList<Household> getHouseholdBasicDataList() {
        ArrayList<Household> householdList = null;
        String sql = "SELECT substr(HH_NUMBER,-3,3) HH_NUMBER, " + " NO_OF_FAMILY_MEMBER, MONTHLY_FAMILY_EXPENDITURE, HH_CHARACTER,  ifnull(HH_ADULT_WOMEN,0) HH_ADULT_WOMEN,  LONGITUDE,  LATITUDE, " + " REG_DATE, SENT,   STATE,   UPDATE_HISTORY " + " FROM household WHERE STATE > 0  AND substr(HH_NUMBER,10,3)!='000'  ORDER BY SENT ASC";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            householdList = new ArrayList<Household>();
            do {
                Household household = new Household();
                household.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                household.setNoOfFamilyMember(cursor.getInt(cursor.getColumnIndex("NO_OF_FAMILY_MEMBER")));
                household.setMonthlyFamilyExpenditure(cursor.getString(cursor.getColumnIndex("MONTHLY_FAMILY_EXPENDITURE")));
                household.setHhAdultWomen(cursor.getLong(cursor.getColumnIndex("HH_ADULT_WOMEN")));
                household.setHhCharacter(cursor.getString(cursor.getColumnIndex("HH_CHARACTER")));
                household.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
                household.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));
                household.setRegDate(cursor.getLong(cursor.getColumnIndex("REG_DATE")));
                household.setSent(cursor.getLong(cursor.getColumnIndex("SENT")));
                household.setState(cursor.getLong(cursor.getColumnIndex("STATE")));
                household.setUpdateHistory(cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY")));
                householdList.add(household);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return householdList;
    }

    public ArrayList<Household> getHouseholdBasicDataListUnsend() {
        ArrayList<Household> householdList = new ArrayList<>();
        String sql = "SELECT substr(HH_NUMBER,-3,3) HH_NUMBER, " + " NO_OF_FAMILY_MEMBER," + " MONTHLY_FAMILY_EXPENDITURE," + " HH_NAME," + " HH_CHARACTER," + " ifnull(HH_ADULT_WOMEN,0) HH_ADULT_WOMEN ," + " LONGITUDE, " + " LATITUDE, " + " REG_DATE, " + " SENT,  " + " STATE, " + " UPDATE_HISTORY " + " FROM household  WHERE  STATE > 0  AND SENT !=1 ORDER BY SENT ASC";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            householdList = new ArrayList<Household>();
            do {
                Household household = new Household();
                household.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                household.setNoOfFamilyMember(cursor.getInt(cursor.getColumnIndex("NO_OF_FAMILY_MEMBER")));

                household.setHhName(cursor.getString(cursor.getColumnIndex("HH_NAME")));


                household.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
                household.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));
                household.setRegDate(cursor.getLong(cursor.getColumnIndex("REG_DATE")));
                household.setSent(cursor.getLong(cursor.getColumnIndex("SENT")));

                household.setState(cursor.getLong(cursor.getColumnIndex("STATE")));
                household.setUpdateHistory(cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY")));
                household.setMonthlyFamilyExpenditure(cursor.getString(cursor.getColumnIndex("MONTHLY_FAMILY_EXPENDITURE")));
                household.setHhCharacter(cursor.getString((cursor.getColumnIndex("HH_CHARACTER"))));
                household.setHhAdultWomen(cursor.getLong((cursor.getColumnIndex("HH_ADULT_WOMEN"))));
                householdList.add(household);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return householdList;
    }

    public ArrayList<QuestionnaireDetail> getQuestionnaireDetail(long questionaryId, ArrayList<String> questionIds) {
        ArrayList<QuestionnaireDetail> questionnaireDetails = new ArrayList<QuestionnaireDetail>();
        String sql = " select  Q_ID,QUESTION_ID FROM questionnaire_detail where QUESTIONNAIRE_ID=" + questionaryId + " and QUESTION_ID In (" + TextUtils.join(",", questionIds) + ")";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                QuestionnaireDetail dtl = new QuestionnaireDetail();
                dtl.setQId(cursor.getLong(cursor.getColumnIndex("Q_ID")));
                dtl.setQuestionId(cursor.getLong(cursor.getColumnIndex("QUESTION_ID")));
                questionnaireDetails.add(dtl);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return questionnaireDetails;
    }

    /**
     * Get household basic information .
     *
     * @return The information list
     */

    public Household getHousehold(String houseHoldNumber) {
        String sql = "SELECT HH_ID, substr(HH_NUMBER,-3,3) HH_NUMBER , " + " NO_OF_FAMILY_MEMBER, " + " LONGITUDE, " + " LATITUDE, " + " MONTHLY_FAMILY_EXPENDITURE, " + "ifnull(HH_ADULT_WOMEN ,0) HH_ADULT_WOMEN,  " + " HH_CHARACTER, " + " SENT,  " + " REG_DATE, " + " STATE, " + " UPDATE_HISTORY " + " FROM household   where  STATE > 0 and substr(HH_NUMBER,-3,3)='" + houseHoldNumber + "'";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        Household household = null;
        if (cursor.moveToFirst()) {
            household = new Household();
            household.setHhId(cursor.getLong(cursor.getColumnIndex("HH_ID")));
            household.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
            household.setNoOfFamilyMember(cursor.getInt(cursor.getColumnIndex("NO_OF_FAMILY_MEMBER")));
            household.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
            household.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));
            household.setSent(cursor.getLong(cursor.getColumnIndex("SENT")));
            household.setMonthlyFamilyExpenditure(cursor.getString(cursor.getColumnIndex("MONTHLY_FAMILY_EXPENDITURE")));
            household.setHhCharacter(cursor.getString(cursor.getColumnIndex("HH_CHARACTER")));
            household.setHhAdultWomen(cursor.getLong(cursor.getColumnIndex("HH_ADULT_WOMEN")));
            household.setRegDate(cursor.getLong(cursor.getColumnIndex("REG_DATE")));
            household.setState(cursor.getLong(cursor.getColumnIndex("STATE")));
            household.setUpdateHistory(cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY")));
        }
        cursor.close();
        return household;
    }

    /**
     * Consume medicine that has been sold.
     *
     * @param medicineList The sold medicine list
     * @param userId       The FCM ID
     * @return the long
     */
    public long cosumeMedicineToSell(ArrayList<MedicineInfo> medicineList, long userId) {
        // Insert data into master table
        ContentValues cvCMaster = new ContentValues();
        cvCMaster.put(Column.CONSUMP_DATE, medicineList.get(0).getUpdateTime());
        cvCMaster.put(Column.REMARKS, "Direct Sell");
        cvCMaster.put(Column.USER_ID, userId);
        cvCMaster.put(Column.DATA_SENT, "N");

        long consumpId = db.insert("medicine_consumption_master", null, cvCMaster);

        double totalPrice = 0;

        // / Insert data into detail table and update stock table
        for (MedicineInfo medicineInfo : medicineList) {
            ContentValues cvCDetail = new ContentValues();

            double medTotalPrice = Utility.parseInt(medicineInfo.getSoldQuantity()) * medicineInfo.getUnitSalesPrice();

            totalPrice += medTotalPrice;
            cvCDetail.put(Column.MED_CONSUMP_ID, consumpId);
            cvCDetail.put(Column.MEDICINE_ID, medicineInfo.getMedId());
            cvCDetail.put(Column.QTY, medicineInfo.getSoldQuantity());
            cvCDetail.put(Column.PRICE, medTotalPrice);

            db.insert("medicine_consumption_detail", null, cvCDetail);

            updateStockTable(medicineInfo.getMedId(), userId, -Utility.parseInt(medicineInfo.getSoldQuantity()), medicineInfo.getUpdateTime());
        }

        // Update total price of master table
        cvCMaster = new ContentValues();
        cvCMaster.put(Column.TOTAL_PRICE, totalPrice);
        db.update("medicine_consumption_master", cvCMaster, Column.MED_CONSUMP_ID + "=?", new String[]{Long.toString(consumpId)});

        return consumpId;

    }

    /**
     * Get today's medicine sales report from database.
     *
     * @return The medicine list which are sold today
     */
    public ArrayList<MedicineInfo> getTodaysSalesReport() {
        ArrayList<MedicineInfo> medicineList = new ArrayList<MedicineInfo>();

        String sql = "SELECT m.`TYPE`|| ' '|| m.`BRAND_NAME`|| ' '|| m.`STRENGTH` || m.MEASURE_UNIT as MEDICINE_NAME," + " sum(mcd.QTY) QTY," + " sum(mcd.PRICE) TOTAL_PRICE " + " FROM medicine_consumption_master mcm " + " INNER JOIN medicine_consumption_detail mcd ON mcm.MED_CONSUMP_ID = mcd.MED_CONSUMP_ID  AND strftime('%Y-%m-%d',mcm.CONSUMP_DATE/1000, 'unixepoch', 'localtime')=date('now') " + " INNER JOIN medicine m ON mcd.MEDICINE_ID = m.MEDICINE_ID " + " GROUP BY mcd.MEDICINE_ID " + "ORDER BY  m.TYPE, m.BRAND_NAME   ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setBrandName(cursor.getString(cursor.getColumnIndex("MEDICINE_NAME")));
                medInfo.setSoldQuantity(cursor.getInt(cursor.getColumnIndex("QTY")) + "");
                medInfo.setTotalPrice(cursor.getDouble(cursor.getColumnIndex("TOTAL_PRICE")));
                medicineList.add(medInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return medicineList;
    }

    /**
     * Retrieve last 30 days sales information.
     *
     * @return The last 30day sales information list
     */
    public ArrayList<IndividualSalesInfo> getLast30DaysSales() {
        ArrayList<IndividualSalesInfo> salesList = new ArrayList<IndividualSalesInfo>();
        String sql = "SELECT strftime('%d-%m-%Y',CONSUMP_DATE/1000, 'unixepoch', 'localtime') CONSUMP_DATE, sum(TOTAL_PRICE) TOTAL_PRICE " + "FROM medicine_consumption_master " + "WHERE strftime('%Y-%m-%d',CONSUMP_DATE/1000, 'unixepoch', 'localtime') between date('now','-30 day')  and date('now') " + "GROUP BY strftime('%d-%m-%Y',CONSUMP_DATE/1000, 'unixepoch', 'localtime')";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                IndividualSalesInfo salesInfo = new IndividualSalesInfo();

                salesInfo.setDate(cursor.getString(cursor.getColumnIndex("CONSUMP_DATE")));
                salesInfo.setSaleAmount(cursor.getDouble(cursor.getColumnIndex("TOTAL_PRICE")));

                salesList.add(salesInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return salesList;
    }

    /**
     * Retrieve last 30 days medicine Receives/sale report and return.
     *
     * @return Last 30 days medicine receive/sale report
     */
    public ArrayList<MedicineRcvSaleInfo> getLast30DaysReceivceSaleList() {
        ArrayList<MedicineRcvSaleInfo> medicineRcvSaleList = new ArrayList<MedicineRcvSaleInfo>();

        String sql = "SELECT m.`TYPE`|| ' '|| m.`BRAND_NAME`|| ' '|| m.`STRENGTH` || m.MEASURE_UNIT AS MEDICINE_NAME, " + "sum(medDetail.TOTAL_RCV_QTY) RCV_QTY, " + "sum(medDetail.TOTAL_SALE_PRICE) TOTAL_SALE_PRICE," + "sum(medDetail.TOTAL_CONSUMP_QTY) CONSUMP_QTY " + "FROM " + "(SELECT " + "mrcd.MEDICINE_ID MEDICINE_ID, " + "SUM(mrcd.qty) TOTAL_RCV_QTY," + "0 as TOTAL_SALE_PRICE, " + "0 AS TOTAL_CONSUMP_QTY " + "FROM medicine_receive_master mrcm " + "INNER JOIN medicine_receive_detail mrcd ON mrcm.MED_RECEIVED_ID=mrcd.MED_RECEIVED_ID " + "WHERE strftime('%Y-%m-%d',mrcm.RECEIVED_DATE/1000, 'unixepoch', 'localtime') between date('now','-30 day')  and date('now')  " + "GROUP BY mrcd.MEDICINE_ID " + "UNION ALL " + "SELECT  " + "mcd.MEDICINE_ID MEDICINE_ID, " + "0 as TOTAL_RCV_QTY, " + "SUM(mcd.PRICE) TOTAL_SALE_PRICE, " + "SUM(mcd.QTY) TOTAL_CONSUMP_QTY " + "FROM medicine_consumption_master mcm " + "INNER JOIN medicine_consumption_detail mcd ON mcm.MED_CONSUMP_ID = mcd.MED_CONSUMP_ID " + "WHERE strftime('%Y-%m-%d',mcm.CONSUMP_DATE/1000, 'unixepoch', 'localtime') between date('now','-30 day')  and date('now')  " + "GROUP BY mcd.MEDICINE_ID) as medDetail " + "INNER JOIN medicine m ON m.MEDICINE_ID = medDetail.MEDICINE_ID " + "GROUP BY medDetail.MEDICINE_ID  ORDER BY m.TYPE , m.BRAND_NAME ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                MedicineRcvSaleInfo medicineRcvSaleInfo = new MedicineRcvSaleInfo();
                medicineRcvSaleInfo.setMedicineName(cursor.getString(cursor.getColumnIndex("MEDICINE_NAME")));
                medicineRcvSaleInfo.setReceiveQuantity(Integer.toString(cursor.getInt(cursor.getColumnIndex("RCV_QTY"))));
                medicineRcvSaleInfo.setSaleQuantity(Integer.toString(cursor.getInt(cursor.getColumnIndex("CONSUMP_QTY"))));
                medicineRcvSaleInfo.setTotalSalePrice(TextUtility.format("%.2f", cursor.getDouble(cursor.getColumnIndex("TOTAL_SALE_PRICE"))));

                medicineRcvSaleList.add(medicineRcvSaleInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return medicineRcvSaleList;
    }

    /**
     * Update household basic data sent.
     */
    public void updateHouseholdBasicDataSent() {
        ContentValues cv = new ContentValues();
        cv.put("SENT", 1);
        db.update("household", cv, null, null);
    }

    /**
     * Retrieve current and last month health care statistics.
     *
     * @return The health care report
     */
    public ArrayList<HealthCareReportInfo> getHealthCareReport() {

        ArrayList<HealthCareReportInfo> report = new ArrayList<HealthCareReportInfo>();
        String sql = "SELECT q.questionnaire_title HEALTH_CARE_TITLE, " + "SUM(CASE WHEN  strftime('%Y-%m-%d',pim.CREATE_DATE/1000, 'unixepoch', 'localtime') BETWEEN date('now','start of month') AND date('now') " + "THEN 1 " + "ELSE 0 " + "END) CURRENT_MONTH_QTY, " + "SUM(CASE WHEN  strftime('%Y-%m-%d',pim.CREATE_DATE/1000, 'unixepoch', 'localtime') BETWEEN date('now','start of month','-1 month') AND date('now','start of month','-1 day') " + "THEN 1 " + "ELSE 0 " + "END) LAST_MONTH_QTY " + "FROM patient_interview_master pim " + "INNER JOIN questionnaire q ON q.QUESTIONNAIRE_ID= pim.QUESTIONNAIRE_ID and q.LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "' " + " WHERE q.name != 'BENEFICIARY_REGISTRATION' AND strftime('%Y-%m-%d',pim.CREATE_DATE/1000, 'unixepoch', 'localtime') BETWEEN date('now','start of month','-1 month') AND date('now') " + "GROUP BY pim.questionnaire_id";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                HealthCareReportInfo healthCareInfo = new HealthCareReportInfo();

                healthCareInfo.setCurrentMonthQuantity(cursor.getInt(cursor.getColumnIndex("CURRENT_MONTH_QTY")));
                healthCareInfo.setLastMonthQuantity(cursor.getInt(cursor.getColumnIndex("LAST_MONTH_QTY")));
                healthCareInfo.setHealthCareTitle(cursor.getString(cursor.getColumnIndex("HEALTH_CARE_TITLE")));

                report.add(healthCareInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return report;
    }

    /**
     * Retrieve monthly beneficiary registration report.
     *
     * @return Monthly beneficiary registration report.
     */
    public ArrayList<BeneficiaryRegistrationState> getBeneficiaryRegistrationReport() {
        ArrayList<BeneficiaryRegistrationState> report = new ArrayList<BeneficiaryRegistrationState>();
        String sql = "SELECT  " + "((case strftime('%m',pim.CREATE_DATE/1000, 'unixepoch', 'localtime') " + "when '01' then 'January' " + "when '02' then 'February' " + "when '03' then 'March' " + "when '04' then 'April' " + "when '05' then 'May' " + "when '06' then 'June' " + "when '07' then 'July' " + "when '08' then 'August' " + "when '09' then 'September' " + "when '10' then 'October' " + "when '11' then 'November' " + "when '12' then 'December' " + "end) ||' '||strftime('%Y',pim.CREATE_DATE/1000, 'unixepoch', 'localtime')) MONTH, " + "COUNT(pim.questionnaire_id) QTY " + "FROM patient_interview_master pim " + "INNER JOIN questionnaire q ON q.QUESTIONNAIRE_ID= pim.QUESTIONNAIRE_ID and q.LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "' " + "WHERE q.name = 'BENEFICIARY_REGISTRATION'  " + "GROUP BY strftime('%Y-%m',pim.CREATE_DATE/1000, 'unixepoch', 'localtime') " + "ORDER BY strftime('%Y-%m',pim.CREATE_DATE/1000, 'unixepoch', 'localtime') DESC;";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                BeneficiaryRegistrationState registrationState = new BeneficiaryRegistrationState();
                registrationState.setMonth(cursor.getString(cursor.getColumnIndex("MONTH")));
                registrationState.setQuantity(cursor.getInt(cursor.getColumnIndex("QTY")));

                report.add(registrationState);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return report;
    }


    public void updateDataVersion(String tableName, long dataSize, long dataInsertedSize, JSONObject param, String versionKey) {
        try {
            if (dataSize == dataInsertedSize) {

                ContentValues cv = new ContentValues();
                cv.put("TABLE_NAME", tableName);
                cv.put("LANG_CODE", App.getContext().getAppSettings().getLanguage());

                if (param.has(versionKey) && param.has(tableName)) {
                    long versionNumber = param.getLong(versionKey);
                    long isComplete = param.getLong(tableName);
                    cv.put("CURRENT_VERSION", versionNumber);
                    cv.put("IS_DOWNLOADED", isComplete);
                    db.replace("data_version", null, cv);
                } else if (param.has(versionKey) && !param.has(tableName)) {
                    long versionNumber = param.getLong(versionKey);
                    cv.put("CURRENT_VERSION", versionNumber);
                    cv.put("IS_DOWNLOADED", 1);
                    db.replace("data_version", null, cv);

                } else if (!param.has(versionKey) && param.has(tableName)) {
                    long isComplete = param.getLong(tableName);
                    cv.put("IS_DOWNLOADED", isComplete);
                    String whereCon = "TABLE_NAME='" + tableName + "' AND LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'";

                    if (isExist(DBTable.DATA_VERSION, whereCon)) {
                        db.update(DBTable.DATA_VERSION, cv, whereCon, null);
                    } else {
                        cv.put("CURRENT_VERSION", 0);
                        db.replace("data_version", null, cv);
                    }
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public Map<String, Long> getVersionList() {
        Map<String, Long> versionList = new HashMap<String, Long>();
        String sql = "SELECT TABLE_NAME, CURRENT_VERSION FROM data_version where LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                versionList.put(cursor.getString(cursor.getColumnIndex("TABLE_NAME")), cursor.getLong(cursor.getColumnIndex("CURRENT_VERSION")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return versionList;
    }


    public int getMandatoryDataDownloadedStatus(Context context) {

        long terget = 19;
        long downloded = 0;
        if (AppPreference.contains(context, KEY.FCM_CONFIGURATION)) {
            downloded++;
        }
        String sql = " SELECT COUNT(*)  DOWNLOADED  FROM data_version where " + " IS_DOWNLOADED=1 " + " AND TABLE_NAME IN ('user_info','questionnaire_category','questionnaire','text_ref','growth_measurement_param','courtyard_meeting_topic_month','topic_info','diagnosis_info','medicine','referral_center_category','referral_center','ccs_status','immunization_info','maternal_care_info','suggestion_text','report_asst','questionnaire_service_category','questionnaire_broad_category', 'patient_interview_doctor_feedback', 'notification_master' )" + " AND LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            downloded = downloded + cursor.getLong(cursor.getColumnIndex("DOWNLOADED"));
        }
        cursor.close();


        return Math.round(((float) downloded / (float) terget) * 100);
    }


    public ArrayList<MedicineInfo> getRequsitionData(long reqId) {
        ArrayList<MedicineInfo> medicineList = new ArrayList<MedicineInfo>();

        String sql = "SELECT  MEDICINE_ID MEDICINE_ID, QTY FROM medicine_requisition_detail WHERE REQ_ID=?";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(reqId)});

        if (cursor.moveToFirst()) {
            do {
                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setMedId(cursor.getLong(cursor.getColumnIndex("MEDICINE_ID")));
                medInfo.setRequiredQuantity(cursor.getInt(cursor.getColumnIndex("QTY")) + "");

                medicineList.add(medInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return medicineList;
    }

    /**
     * REtrieve unsent requisition id.
     *
     * @return The unsent requisition id
     */
    public RequisitionInfo getUnSentRequisition() {
        RequisitionInfo requisitionInfo = null;
        String sql = " SELECT REQ_ID,REQ_NO FROM medicine_requisition_master WHERE  REQ_STATUS='Initiated' AND ifnull(REQ_NO,-1)<1 ORDER BY REQ_ID DESC  LIMIT 1 ";
        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            requisitionInfo = new RequisitionInfo();
            requisitionInfo.setRequisitionId(cursor.getLong(cursor.getColumnIndex("REQ_ID")));
            requisitionInfo.setRequisitionNo(cursor.getLong(cursor.getColumnIndex("REQ_NO")));
        }
        cursor.close();

        return requisitionInfo;
    }


    public JSONArray getServicelastSentStatus(String currentdate, String day) {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append(" select  date(TRANS_REF/1000, 'unixepoch', 'localtime') SERVICE_DATE, ");
        sql.append("  ifnull(MAX(TRANS_REF),-1) LAST_SERVIC_TIME , COUNT(*) NO_SERVICE,  ");
        sql.append(" COUNT(CASE WHEN DATA_SENT='Y' THEN 1 END ) NO_SENT,COUNT(CASE WHEN DATA_SENT='N' THEN 1 END ) NO_UNSENT  ");
        sql.append(" from patient_interview_master  ");
        sql.append(" where date(TRANS_REF/1000, 'unixepoch', 'localtime')  between date('" + currentdate + "','-" + day + " day') and date('now')  ");
        sql.append(" GROUP BY  date(TRANS_REF/1000, 'unixepoch', 'localtime') order by SERVICE_DATE  ");

        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    /**
     * Retrieve unsent medicine sale information.
     *
     * @return The unsent medicine sale information
     */
    public ArrayList<MedicineSellInfo> getMedicineConsumptionList() {
        ArrayList<MedicineSellInfo> medSellList = new ArrayList<MedicineSellInfo>();
        String sql = "SELECT mcm.CONSUMP_DATE,mcm.MED_CONSUMP_ID MED_CONSUMP_ID, mcd.MEDICINE_ID MEDICINE_ID, mcd.QTY QTY " + "FROM medicine_consumption_master mcm " + "INNER JOIN medicine_consumption_detail mcd ON mcm.MED_CONSUMP_ID=mcd.MED_CONSUMP_ID " + "WHERE mcm.interview_id is null AND mcm.DATA_SENT='N'";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        long consumpId = -1;
        if (cursor.moveToFirst()) {
            MedicineSellInfo medSellInfo = null;
            ArrayList<MedicineInfo> medList = null;

            do {
                long id = cursor.getLong(cursor.getColumnIndex("MED_CONSUMP_ID"));
                if (consumpId != id) {
                    consumpId = id;
                    medSellInfo = new MedicineSellInfo();
                    medSellInfo.setConsumptionId(consumpId);
                    medList = new ArrayList<MedicineInfo>();
                    medSellInfo.setMedicineList(medList);
                    medSellList.add(medSellInfo);
                }

                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setMedId(cursor.getLong(cursor.getColumnIndex("MEDICINE_ID")));
                medInfo.setSoldQuantity(cursor.getInt(cursor.getColumnIndex("QTY")) + "");
                medInfo.setUpdateTime(cursor.getLong(cursor.getColumnIndex("CONSUMP_DATE")));
                medList.add(medInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return medSellList;
    }

    /**
     * Get unsent medicine receive list.
     *
     * @return The Unsent medicine receive list
     */
    public ArrayList<RequisitionInfo> getUnsentMedicneReceivedList() {
        ArrayList<RequisitionInfo> unsentReceivedList = new ArrayList<RequisitionInfo>();
        String sql = "SELECT mrcm.MED_RECEIVED_ID MED_RECEIVED_ID, " + "mrcm.REQ_NO REQ_NO, " + "mrcd.MEDICINE_ID MEDICINE_ID, " + "mrcm.RECEIVED_DATE," + "mrcd.QTY QTY " + "FROM medicine_receive_master mrcm " + "INNER JOIN medicine_receive_detail mrcd ON mrcd.MED_RECEIVED_ID=mrcm.MED_RECEIVED_ID " + "WHERE mrcm.DATA_SENT='N'";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        long reqNo = -1;
        if (cursor.moveToFirst()) {
            RequisitionInfo reqInfo = null;
            ArrayList<MedicineInfo> medicineList = null;

            do {
                long newReqNo = cursor.getLong(cursor.getColumnIndex("REQ_NO"));
                if (newReqNo != reqNo) {
                    reqInfo = new RequisitionInfo();
                    medicineList = new ArrayList<MedicineInfo>();

                    reqInfo.setMedicineList(medicineList);

                    unsentReceivedList.add(reqInfo);

                    reqNo = newReqNo;
                    reqInfo.setRequisitionNo(newReqNo);
                    reqInfo.setReceiveDate(cursor.getColumnIndex("RECEIVED_DATE") + "");
                    reqInfo.setReceivedId(cursor.getLong(cursor.getColumnIndex("MED_RECEIVED_ID")));
                }

                MedicineInfo medInfo = new MedicineInfo();
                medInfo.setMedId(cursor.getLong(cursor.getColumnIndex("MEDICINE_ID")));
                medInfo.setQtyReceived(cursor.getLong(cursor.getColumnIndex("QTY")));
                medicineList.add(medInfo);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return unsentReceivedList;
    }


    @SuppressLint("Range")
    public ArrayList<UserScheduleInfo> getPatientFollowupList(String hhNumber) {

        String type = App.getContext().getAppSettings().getFollowUpType();
        ArrayList<UserScheduleInfo> patientFollowupList = new ArrayList<UserScheduleInfo>();

        Calendar current = Calendar.getInstance();
        current.set(Calendar.HOUR, 0);
        current.set(Calendar.MINUTE, 0);
        current.set(Calendar.SECOND, 0);
        current.set(Calendar.HOUR_OF_DAY, 0);
        current.set(Calendar.MILLISECOND, 0);

        long startTime = current.getTimeInMillis();
        long endTime = startTime;

        long beforMillis = App.getContext().getAppSettings().getFollowUpDayBeforToday() * 24 * 60 * 60 * 1000;
        long afterMillis = App.getContext().getAppSettings().getFollowUpDayAfterToday() * 24 * 60 * 60 * 1000;
        startTime = startTime - beforMillis;
        endTime = endTime + afterMillis;

        String sql = "SELECT ( CASE " + "WHEN julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  ) = CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) THEN CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) " + "ELSE 1 + CAST ( ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  )  AS int ) " + "END )  REMAINING_DAYS, " + "substr(us.BENEF_CODE,-5,5) BENEF_CODE," + "b.BENEF_ID BENEF_ID,  " + "b.benef_name BENEF_NAME, " + "b.GENDER GENDER, " + "b.DOB DOB, " + "b.benef_name_local BENEF_NAME_LOCAL, " + "b.benef_image_path BENEF_IMAGE_PATH, " + "us.INTERVIEW_ID INTERVIEW_ID, " + "us.SCHED_DESC SCHED_DESC, " + "us.ATTENDED_DATE ATTENDED_DATE, " + "pim.QUESTIONNAIRE_ID QUESTIONNAIRE_ID " + "FROM user_schedule us " + " INNER JOIN beneficiary b ON b.benef_code = us.benef_code " + " INNER JOIN household h on b.HH_NUMBER = h.HH_NUMBER and h.STATE=1 and h.HH_NUMBER like '" + hhNumber + "%'  " + " INNER JOIN patient_interview_master pim ON pim.INTERVIEW_ID = us.INTERVIEW_ID " + "WHERE  us.SCHED_DATE between " + startTime + " AND " + endTime + " AND us.TYPE in (" + type + ")  AND  us.SCHED_STATUS=? AND us.STATE=1 ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{Integer.toString(ScheduleStatus.NEW)});
        Log.e("SQL", sql.toString());
        if (cursor.moveToFirst()) {
            do {
                UserScheduleInfo scheduleInfo = new UserScheduleInfo();
                scheduleInfo.setBeneficiaryCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                scheduleInfo.setBeneficiaryId(cursor.getLong(cursor.getColumnIndex("BENEF_ID")));
                scheduleInfo.setBeneficiaryImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));

                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                scheduleInfo.setBeneficiaryName(Utility.formatTextBylanguage(nameOriginal, nameLocal));

                scheduleInfo.setRemainingDays(cursor.getInt(cursor.getColumnIndex("REMAINING_DAYS")));
                scheduleInfo.setInterviewId(cursor.getLong(cursor.getColumnIndex("INTERVIEW_ID")));
                scheduleInfo.setQuestionnaireId(cursor.getLong(cursor.getColumnIndex("QUESTIONNAIRE_ID")));
                scheduleInfo.setDescription(cursor.getString(cursor.getColumnIndex("SCHED_DESC")));
                scheduleInfo.setDescription(cursor.getString(cursor.getColumnIndex("SCHED_DESC")));
                scheduleInfo.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                scheduleInfo.setDob(cursor.getString(cursor.getColumnIndex("DOB")));

                patientFollowupList.add(scheduleInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return patientFollowupList;

    }


    public ArrayList<UserScheduleInfo> getScheduleList(int day, String hhNumber) {
        ArrayList<UserScheduleInfo> patientFollowupList = new ArrayList<UserScheduleInfo>();

        String sql = "SELECT ( CASE " + "WHEN julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  ) = CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) THEN CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) " + "ELSE 1 + CAST ( abs( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  )  AS int ) " + "END )  REMAINING_DAYS, " + "substr(us.BENEF_CODE,-5,5) BENEF_CODE, " + "us.TYPE TYPE, " + "us.SYSTEM_CHANGED_DATE SYSTEM_CHANGED_DATE, " + "b.benef_name BENEF_NAME, " + "b.benef_name_local BENEF_NAME_LOCAL, " + "b.benef_image_path BENEF_IMAGE_PATH, " + "us.INTERVIEW_ID INTERVIEW_ID, " + "us.SCHED_DESC SCHED_DESC, " + "strftime( '%d-%m-%Y', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' ) SCHED_DATE, " + "pim.QUESTIONNAIRE_ID QUESTIONNAIRE_ID " + "FROM user_schedule us " + "LEFT JOIN beneficiary b ON b.benef_code = us.benef_code " + "LEFT JOIN patient_interview_master pim ON pim.INTERVIEW_ID = us.INTERVIEW_ID " + "WHERE us.SCHED_STATUS=? AND us.STATE=1  and  us.BENEF_CODE like '" + hhNumber + "%' ";

        if (day > 0) {
            sql = sql + " AND strftime('%Y-%m-%d',us.SCHED_DATE/1000, 'unixepoch', 'localtime') BETWEEN date('now','+1 day') AND date('now', '+" + day + " day') ";
        } else {
            sql = sql + " AND strftime('%Y-%m-%d',us.SCHED_DATE/1000, 'unixepoch', 'localtime') BETWEEN  date('now', '" + day + " day') AND date('now','-1 day')  ";
        }
        // sql = sql+
        // " ORDER BY strftime( '%d-%m-%Y', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' ) ASC";

        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, new String[]{Integer.toString(ScheduleStatus.NEW)});

        if (cursor.moveToFirst()) {
            do {
                UserScheduleInfo scheduleInfo = new UserScheduleInfo();
                scheduleInfo.setBeneficiaryCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                scheduleInfo.setBeneficiaryImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));

                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                scheduleInfo.setBeneficiaryName(Utility.formatTextBylanguage(nameOriginal, nameLocal));

                scheduleInfo.setRemainingDays(cursor.getInt(cursor.getColumnIndex("REMAINING_DAYS")));
                scheduleInfo.setInterviewId(cursor.getLong(cursor.getColumnIndex("INTERVIEW_ID")));
                scheduleInfo.setQuestionnaireId(cursor.getLong(cursor.getColumnIndex("QUESTIONNAIRE_ID")));
                scheduleInfo.setDescription(cursor.getString(cursor.getColumnIndex("SCHED_DESC")));
                scheduleInfo.setScheduleType(cursor.getString(cursor.getColumnIndex("TYPE")));
                scheduleInfo.setScheduleDateStr(cursor.getString(cursor.getColumnIndex("SCHED_DATE")));
                scheduleInfo.setSystemChangedDate(cursor.getLong(cursor.getColumnIndex("SYSTEM_CHANGED_DATE")));

                patientFollowupList.add(scheduleInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return patientFollowupList;

    }

    public ArrayList<ScheduleInfo> getScheduleInfoListForMaternal(String date, String type) {
        ArrayList<ScheduleInfo> scheduleInfos = new ArrayList<ScheduleInfo>();

        StringBuffer varname1 = new StringBuffer();
        varname1.append(" SELECT   tmp.maternal_care_id MATERNAL_CARE_ID, ");
        varname1.append("          tmp.sched_name       SCHED_NAME, ");
        varname1.append("          tmp.questionnaire_id QUESTIONNAIRE_ID, ");
        varname1.append("          tmp.QUESTIONNAIRE_TITLE       SCHED_DESC, ");
        varname1.append("          CASE ");
        varname1.append("                    WHEN MIN(us.sched_date) NOT null THEN MIN(us.sched_date) ");
        varname1.append("                    WHEN tmp.start_day >= date('now') THEN tmp.start_day ");
        varname1.append("                    ELSE tmp.end_day ");
        varname1.append("          END           SCHED_DATE, ");
        varname1.append("          tmp.start_day start_day, ");
        varname1.append("          tmp.end_day   end_day ");
        varname1.append(" FROM      ( ");
        varname1.append("                 SELECT mci.maternal_care_id maternal_care_id, ");
        varname1.append("                        mci.care_name        sched_name, ");
        varname1.append("                        mci.care_desc        sched_desc, ");
        varname1.append("                        mci.questionnaire_id questionnaire_id, ");
        varname1.append("                        q.QUESTIONNAIRE_TITLE QUESTIONNAIRE_TITLE, ");
        varname1.append("                        date('" + date + "', '+' ");
        varname1.append("                               || mci.sched_range_start_day ");
        varname1.append("                               || ' days') start_day, ");
        varname1.append("                        date('" + date + "', '+' ");
        varname1.append("                               || mci.sched_range_end_day ");
        varname1.append("                               || ' days') end_day ");
        varname1.append("                 FROM   maternal_care_info mci  join questionnaire  q on q.questionnaire_id=mci.questionnaire_id and q.LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'");
        varname1.append("                 WHERE  mci.care_type = '" + type + "') AS tmp ");
        varname1.append(" LEFT JOIN ");
        varname1.append("          ( ");
        varname1.append("                 SELECT date(sched_date/1000,'unixepoch','localtime') sched_date ");
        varname1.append("                 FROM   user_schedule ");
        varname1.append("                 WHERE  type ='SATELLITE_CLINIC' AND STATE=1 ) us ");
        varname1.append(" ON     us.sched_date BETWEEN start_day AND       end_day ");
        varname1.append(" AND    us.sched_date >= date('now') ");
        varname1.append(" WHERE  date(tmp.end_day) >= date('now') ");
        varname1.append(" GROUP BY  tmp.maternal_care_id;");

        Log.e("SQL", varname1.toString());

        Cursor cursor = db.rawQuery(varname1.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                ScheduleInfo scheduleInfo = new ScheduleInfo();
                scheduleInfo.setScheduleId(cursor.getLong(cursor.getColumnIndex("MATERNAL_CARE_ID")));
                scheduleInfo.setScheduleDesc(cursor.getString(cursor.getColumnIndex("SCHED_DESC")));
                scheduleInfo.setScheduleName(cursor.getString(cursor.getColumnIndex("SCHED_NAME")));
                scheduleInfo.setReferenceId(cursor.getLong(cursor.getColumnIndex("QUESTIONNAIRE_ID")));
                scheduleInfo.setScheduleType(type);
                try {
                    scheduleInfo.setScheduleDate(Utility.getMillisecondFromDate(cursor.getString(cursor.getColumnIndex("SCHED_DATE")), Constants.DATE_FORMAT_YYYY_MM_DD));
                } catch (ParseException e) {
                    scheduleInfo.setScheduleDate(0);
                }
                scheduleInfos.add(scheduleInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return scheduleInfos;

    }

    public ArrayList<ScheduleInfo> getScheduleInfoListForImmunization(String benfCode, String immuType, String langCode) {
        ArrayList<ScheduleInfo> scheduleInfos = new ArrayList<ScheduleInfo>();
        String sql = " SELECT " + " IMMU_NAME , " + " CASE WHEN DESC_ORG_CARD IS NULL THEN ' ' ELSE DESC_ORG_CARD END DESC_ORG_CARD, " + " CASE WHEN IMMU_DATE IS NULL THEN 0 ELSE IMMU_DATE END IMMU_DATE, " + " imi.IMMU_ID IMMU_ID " + " FROM immunization_info imi " + " LEFT JOIN immunization_service ims ON imi.IMMU_ID=ims.IMMU_ID AND ims.BENEF_CODE='" + benfCode + "' " + " WHERE imi.IMMU_TYPE=? AND imi.LANG_CODE=? ORDER BY imi.SORT_ORDER ASC ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{immuType, langCode});
        if (cursor.moveToFirst()) {
            do {
                ScheduleInfo scheduleInfo = new ScheduleInfo();
                scheduleInfo.setScheduleId(cursor.getLong(cursor.getColumnIndex("IMMU_ID")));
                scheduleInfo.setScheduleName(cursor.getString(cursor.getColumnIndex("IMMU_NAME")));
                scheduleInfo.setScheduleType(immuType);
                scheduleInfo.setScheduleDesc(cursor.getString(cursor.getColumnIndex("DESC_ORG_CARD")));
                scheduleInfo.setScheduleDate(cursor.getLong(cursor.getColumnIndex("IMMU_DATE")));
                scheduleInfos.add(scheduleInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return scheduleInfos;
    }


    @SuppressLint("Range")
    public ArrayList<UserScheduleInfo> getTodaysScheduleList(String schedStatus) {
        ArrayList<UserScheduleInfo> patientFollowupList = new ArrayList<UserScheduleInfo>();
        String code = App.getContext().getUserInfo().getUserCode();


        int form = App.getContext().getAppSettings().getScheduleDayBeforToday();
        int to = App.getContext().getAppSettings().getScheduleDayAfterToday();

//        sql.append(" LEFT JOIN (select BENEF_CODE, ");
//        sql.append(" strftime('%d/%m/%Y', date( MAX(pim.TRANS_REF)/1000, 'unixepoch','localtime')) ");
//        sql.append(" LAST_SERVICE_DATE from patient_interview_master pim ");
        String sql = " SELECT " + "( CASE " + "WHEN julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  ) = CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) THEN CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) " + "ELSE 1 + CAST ( ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  )  AS int ) " + "END )  REMAINING_DAYS, " + " us.SCHED_ID SCHED_ID, " + " substr(us.BENEF_CODE,-5,5) BENEF_CODE, " + " us.TYPE TYPE, " + " pim.LAST_SERVICE_DATE as LAST_SERVICE_DATE , " + " b.benef_name BENEF_NAME, " + " b.BENEF_ID BENEF_ID,  " + " b.GENDER  GENDER,  " + " b.benef_name_local BENEF_NAME_LOCAL, " + " b.benef_image_path BENEF_IMAGE_PATH, " + " us.INTERVIEW_ID INTERVIEW_ID, " + " us.SCHED_DESC SCHED_DESC, " + " strftime( '%d-%m-%Y', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' ) SCHED_DATE_STR, " + " us.SCHED_DATE SCHED_DATE, " + " us.EVENT_ID EVENT_ID, " + " us.SYSTEM_CHANGED_DATE SYSTEM_CHANGED_DATE, " + " strftime( '%d-%m-%Y', us.ATTENDED_DATE / 1000, 'unixepoch', 'localtime' ) ATTENDED_DATE_STR, " + " us.ATTENDED_DATE ATTENDED_DATE, " + " us.SCHED_STATUS SCHED_STATUS " + " FROM user_schedule us " + " LEFT JOIN beneficiary b ON b.benef_code = us.benef_code " + " LEFT JOIN (select INTERVIEW_ID, strftime('%d/%m/%Y %H:%M', date( TRANS_REF/1000, 'unixepoch','localtime')) LAST_SERVICE_DATE  from patient_interview_master ) pim ON  pim.INTERVIEW_ID=us.INTERVIEW_ID " + " WHERE   " + " ( (us.SCHED_STATUS= " + schedStatus + " and strftime('%Y-%m-%d',us.SCHED_DATE/1000, 'unixepoch', 'localtime') BETWEEN date('now','-" + form + " day') AND date('now', '+" + to + " day')) or " + "  (us.SCHED_STATUS=" + schedStatus + " and strftime('%Y-%m-%d',us.ATTENDED_DATE/1000, 'unixepoch', 'localtime')=date('now')) )  " + " AND  us.STATE=1 " + " ORDER BY us.SCHED_STATUS, strftime( '%d-%m-%Y', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )";

        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                UserScheduleInfo scheduleInfo = new UserScheduleInfo();
                scheduleInfo.setBeneficiaryCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                scheduleInfo.setBeneficiaryImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));

                String name = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                if (name == null || name.equalsIgnoreCase("")) {
                    name = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                }
                scheduleInfo.setBeneficiaryName(name);
                scheduleInfo.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                scheduleInfo.setInterviewId(cursor.getLong(cursor.getColumnIndex("INTERVIEW_ID")));
                scheduleInfo.setScheduleID(cursor.getLong(cursor.getColumnIndex("SCHED_ID")));
                scheduleInfo.setBeneficiaryId(cursor.getLong(cursor.getColumnIndex("BENEF_ID")));


                scheduleInfo.setDescription(cursor.getString(cursor.getColumnIndex("SCHED_DESC")));
                scheduleInfo.setScheduleType(cursor.getString(cursor.getColumnIndex("TYPE")));
                scheduleInfo.setScheduleDateStr(cursor.getString(cursor.getColumnIndex("SCHED_DATE_STR")));
                scheduleInfo.setScheduleDate(cursor.getLong(cursor.getColumnIndex("SCHED_DATE")));
                scheduleInfo.setAttendedDateStr(cursor.getString(cursor.getColumnIndex("ATTENDED_DATE_STR")));
                scheduleInfo.setScheduleStatus(cursor.getInt(cursor.getColumnIndex("SCHED_STATUS")));
                scheduleInfo.setAttendedDate(cursor.getLong(cursor.getColumnIndex("ATTENDED_DATE")));
                scheduleInfo.setSystemChangedDate(cursor.getLong(cursor.getColumnIndex("SYSTEM_CHANGED_DATE")));


                scheduleInfo.setLastServiceDate(cursor.getString(cursor.getColumnIndex("LAST_SERVICE_DATE")));
                scheduleInfo.setRemainingDays(cursor.getInt(cursor.getColumnIndex("REMAINING_DAYS")));

                patientFollowupList.add(scheduleInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return patientFollowupList;
    }

    public ArrayList<UserScheduleInfo> getCompeleteScheduleList(String hhNumber) {
        ArrayList<UserScheduleInfo> patientFollowupList = new ArrayList<UserScheduleInfo>();
        String code = App.getContext().getUserInfo().getUserCode() + (hhNumber != null ? hhNumber.trim() : "");


        int form = App.getContext().getAppSettings().getScheduleDayBeforToday();
        int to = App.getContext().getAppSettings().getScheduleDayAfterToday();
        String sql = " SELECT " + "( CASE " + "WHEN julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  ) = CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) THEN CAST ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  AS int ) " + "ELSE 1 + CAST ( ( julianday( strftime( '%Y-%m-%d', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )  ) - julianday(  )  )  AS int ) " + "END )  REMAINING_DAYS, " + " us.SCHED_ID SCHED_ID, " + " substr(us.BENEF_CODE,-5,5) BENEF_CODE, " + " us.TYPE TYPE, " + " pim.LAST_SERVICE_DATE, " + " b.benef_name BENEF_NAME, " + " b.BENEF_ID BENEF_ID,  " + " b.benef_name_local BENEF_NAME_LOCAL, " + " b.benef_image_path BENEF_IMAGE_PATH, " + " us.INTERVIEW_ID INTERVIEW_ID, " + " us.SCHED_DESC SCHED_DESC, " + " strftime( '%d-%m-%Y', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' ) SCHED_DATE_STR, " + " us.SCHED_DATE SCHED_DATE, " + " us.SYSTEM_CHANGED_DATE SYSTEM_CHANGED_DATE, " + " strftime( '%d-%m-%Y', us.ATTENDED_DATE / 1000, 'unixepoch', 'localtime' ) ATTENDED_DATE_STR, " + " us.ATTENDED_DATE ATTENDED_DATE, " + " us.SCHED_STATUS SCHED_STATUS " + " FROM user_schedule us " + " LEFT JOIN beneficiary b ON b.benef_code = us.benef_code " + "  LEFT JOIN (select INTERVIEW_ID, strftime('%d/%m/%Y', date( TRANS_REF/1000, 'unixepoch','localtime')) LAST_SERVICE_DATE  from patient_interview_master ) pim ON  pim.INTERVIEW_ID=us.INTERVIEW_ID " + " WHERE  us.benef_code like '" + code + "%' AND " + " ( (us.SCHED_STATUS=0 and strftime('%Y-%m-%d',us.SCHED_DATE/1000, 'unixepoch', 'localtime') BETWEEN date('now','-" + form + " day') AND date('now', '+" + to + " day')) " + "  or " + "  (us.SCHED_STATUS=1 and strftime('%Y-%m-%d',us.ATTENDED_DATE/1000, 'unixepoch', 'localtime')=date('now')) )  " + " AND  us.STATE=1  AND us.SCHED_STATUS=1 " + " ORDER BY us.SCHED_STATUS, strftime( '%d-%m-%Y', us.SCHED_DATE / 1000, 'unixepoch', 'localtime' )";

        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                UserScheduleInfo scheduleInfo = new UserScheduleInfo();
                scheduleInfo.setBeneficiaryCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                scheduleInfo.setBeneficiaryImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));

                String name = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                if (name == null || name.equalsIgnoreCase("")) {
                    name = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                }
                scheduleInfo.setBeneficiaryName(name);

                scheduleInfo.setRemainingDays(cursor.getInt(cursor.getColumnIndex("REMAINING_DAYS")));
                scheduleInfo.setInterviewId(cursor.getLong(cursor.getColumnIndex("INTERVIEW_ID")));
                scheduleInfo.setScheduleID(cursor.getLong(cursor.getColumnIndex("SCHED_ID")));
                scheduleInfo.setBeneficiaryId(cursor.getLong(cursor.getColumnIndex("BENEF_ID")));
                scheduleInfo.setDescription(cursor.getString(cursor.getColumnIndex("SCHED_DESC")));
                scheduleInfo.setScheduleType(cursor.getString(cursor.getColumnIndex("TYPE")));
                scheduleInfo.setScheduleDateStr(cursor.getString(cursor.getColumnIndex("SCHED_DATE_STR")));
                scheduleInfo.setScheduleDate(cursor.getLong(cursor.getColumnIndex("SCHED_DATE")));
                scheduleInfo.setAttendedDateStr(cursor.getString(cursor.getColumnIndex("ATTENDED_DATE_STR")));
                scheduleInfo.setScheduleStatus(cursor.getInt(cursor.getColumnIndex("SCHED_STATUS")));
                scheduleInfo.setAttendedDate(cursor.getLong(cursor.getColumnIndex("ATTENDED_DATE")));
                scheduleInfo.setSystemChangedDate(cursor.getLong(cursor.getColumnIndex("SYSTEM_CHANGED_DATE")));

                patientFollowupList.add(scheduleInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return patientFollowupList;
    }


    /**
     * Retrieve follow up questionnaire list and return.
     *
     * @param parentInterviewId The parent interview Id
     * @param langCode          the lang code
     * @return The follow up questionnaire list
     */
    public ArrayList<QuestionnaireInfo> getFollowupQuestionnaireList(long parentInterviewId, String langCode) {
        ArrayList<QuestionnaireInfo> followupQuestionnaireList = new ArrayList<QuestionnaireInfo>();
//		String sql = " SELECT "
//				+ "qfr2.`FOLLOWUP_QUESTIONNAIRE_ID` QUESTIONNAIRE_ID,  "
//				+ "q.`QUESTIONNAIRE_TITLE` QUESTIONNAIRE_TITLE, "
//				+ "q.NAME NAME,q.ICON ICON, "
//				+ "q.QUESTIONNAIRE_JSON QUESTIONNAIRE_JSON, "
//				+ "CASE "
//				+ "WHEN qfr2.SORT_ORDER > "
//				+ "CASE WHEN pim.QUESTIONNAIRE_ID = qfr2.`FOLLOWUP_QUESTIONNAIRE_ID` "
//				+ "THEN qfr2.`SORT_ORDER` "
//				+ "ELSE 0 "
//				+ "END "
//				+ "THEN 1 "
//				+ "ELSE 0 "
//				+ "END AS NEXT_INTERVIEW "
//				+ "FROM  (SELECT "
//				+ "IFNULL(QF.`FOLLOWUP_QUESTIONNAIRE_ID`, Q.`QUESTIONNAIRE_ID`) AS RELATED_QUESTIONNAIRE_ID,"
//				+ "IFNULL(QF.`QUESTIONNAIRE_ID`, Q.`QUESTIONNAIRE_ID`) AS MainQuestionnaireID "
//				+ "FROM `questionnaire` Q "
//				+ "LEFT JOIN `questionnaire_followup_relation` QF ON Q.`QUESTIONNAIRE_ID` =  QF.`FOLLOWUP_QUESTIONNAIRE_ID` "
//				+ "WHERE Q.LANG_CODE= '"
//				+ langCode
//				+ "' AND Q.STATE=1 ) qfr  "
//				+ "LEFT JOIN `patient_interview_master` pim ON qfr.`RELATED_QUESTIONNAIRE_ID` = pim.`QUESTIONNAIRE_ID` "
//				+ "LEFT JOIN `questionnaire_followup_relation` qfr2 ON qfr.`MainQuestionnaireID` = qfr2.`QUESTIONNAIRE_ID`  "
//				+ "LEFT JOIN `questionnaire` q ON qfr2.FOLLOWUP_QUESTIONNAIRE_ID = q.`QUESTIONNAIRE_ID` "
//				+ "WHERE pim.`INTERVIEW_ID`=? AND q.STATE=? AND q.LANG_CODE=? "
//				+ " GROUP BY qfr2.FOLLOWUP_QUESTIONNAIRE_ID  "
//				+ "ORDER BY qfr2.`SORT_ORDER`";

        String sql = " SELECT " + "qfr2.`FOLLOWUP_QUESTIONNAIRE_ID` QUESTIONNAIRE_ID,  " + "IFNULL(q.`QUESTIONNAIRE_TITLE`, q.NAME) QUESTIONNAIRE_TITLE,  " + "q.NAME NAME,q.ICON ICON, " + "q.QUESTIONNAIRE_JSON QUESTIONNAIRE_JSON, " + "CASE " + "WHEN qfr2.SORT_ORDER > " + "CASE WHEN pim.QUESTIONNAIRE_ID = qfr2.`FOLLOWUP_QUESTIONNAIRE_ID` " + "THEN qfr2.`SORT_ORDER` " + "ELSE 0 " + "END " + "THEN 1 " + "ELSE 0 " + "END AS NEXT_INTERVIEW " + "FROM  (SELECT " + "IFNULL(QF.`FOLLOWUP_QUESTIONNAIRE_ID`, Q.`QUESTIONNAIRE_ID`) AS RELATED_QUESTIONNAIRE_ID," + "IFNULL(QF.`QUESTIONNAIRE_ID`, Q.`QUESTIONNAIRE_ID`) AS MainQuestionnaireID " + "FROM `questionnaire` Q " + "LEFT JOIN `questionnaire_followup_relation` QF ON Q.`QUESTIONNAIRE_ID` =  QF.`FOLLOWUP_QUESTIONNAIRE_ID` " + "WHERE Q.LANG_CODE= '" + langCode + "' AND Q.STATE=1 ) qfr  " + "LEFT JOIN `patient_interview_master` pim ON qfr.`MainQuestionnaireID` = pim.`QUESTIONNAIRE_ID` " + "LEFT JOIN `questionnaire_followup_relation` qfr2 ON qfr.`MainQuestionnaireID` = qfr2.`QUESTIONNAIRE_ID`  " + "LEFT JOIN `questionnaire` q ON qfr2.FOLLOWUP_QUESTIONNAIRE_ID = q.`QUESTIONNAIRE_ID` " + "WHERE pim.`INTERVIEW_ID`=? AND q.STATE=? AND q.LANG_CODE=? " + " GROUP BY qfr2.FOLLOWUP_QUESTIONNAIRE_ID  " + "ORDER BY qfr2.`SORT_ORDER`";
        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(parentInterviewId), "1", langCode});

        boolean isHighlightFound = false;
        if (cursor.moveToFirst()) {
            do {
                QuestionnaireInfo qi = new QuestionnaireInfo();
                qi.setId(cursor.getLong(cursor.getColumnIndex("QUESTIONNAIRE_ID")));
                qi.setQuestionnaireName(cursor.getString(cursor.getColumnIndex("NAME")));
                qi.setQuestionnaireTitle(cursor.getString(cursor.getColumnIndex("QUESTIONNAIRE_TITLE")));
                qi.setQuestionnaireJSON(cursor.getString(cursor.getColumnIndex("QUESTIONNAIRE_JSON")));

                qi.setIcon(cursor.getString(cursor.getColumnIndex("ICON")));

                if (!isHighlightFound) {
                    boolean isHighlight = cursor.getInt(cursor.getColumnIndex("NEXT_INTERVIEW")) == 1;

                    qi.setHighlight(isHighlight);
                    isHighlightFound = isHighlight;
                }

                if (cursor.getInt(cursor.getColumnIndex("NEXT_INTERVIEW")) == 0) {
                    isHighlightFound = false;
                    for (QuestionnaireInfo info : followupQuestionnaireList) {
                        info.setHighlight(isHighlightFound);
                    }
                }

                followupQuestionnaireList.add(qi);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return followupQuestionnaireList;
    }

    public String getInterviewFinding(long interviewId) {
        String sql = " select " + " (CASE  " + " WHEN IFNULL(REF_CENTER_ID,0)>0 AND  IFNULL(PRESC_ID,0)>0 AND IFNULL(ADVICE_FLAG,0)>0 THEN 'Treatment_referred' " + " WHEN IFNULL(REF_CENTER_ID,0)>0 AND   IFNULL(PRESC_ID,0)>0 AND IFNULL(ADVICE_FLAG,0)<=0 THEN 'Treatment_referred'" + " WHEN IFNULL(REF_CENTER_ID,0)<=0 AND  IFNULL(PRESC_ID,0)>0 AND IFNULL(ADVICE_FLAG,0)>0 THEN  'Treatment' " + " WHEN IFNULL(REF_CENTER_ID,0)>0 AND   IFNULL(PRESC_ID,0)<=0 AND IFNULL(ADVICE_FLAG,0)>0 THEN 'Referred_advice'" + " WHEN IFNULL(REF_CENTER_ID,0)<=0 AND  IFNULL(PRESC_ID,0)<=0 AND IFNULL(ADVICE_FLAG,0)>0 THEN 'Advised'" + " WHEN IFNULL(REF_CENTER_ID,0)<=0 AND  IFNULL( PRESC_ID,0)>0 AND IFNULL(ADVICE_FLAG,0)<=0 THEN 'Treatment'" + " WHEN IFNULL(REF_CENTER_ID,0)>0 AND  IFNULL( PRESC_ID,0)<=0 AND IFNULL(ADVICE_FLAG,0)<=0 THEN 'Referred' " + " ELSE 'no finding' END) FINDING from patient_interview_master  where INTERVIEW_ID= " + interviewId;

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            String ans = cursor.getString(cursor.getColumnIndex("FINDING"));
            cursor.close();
            return ans;
        }
        return "";
    }


    public String getInterviewTransRef(long interviewId) {
        String sql = " select TRANS_REF from patient_interview_master where INTERVIEW_ID=" + interviewId;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String ans = cursor.getString(cursor.getColumnIndex("TRANS_REF"));
            cursor.close();
            return ans;
        }
        return "";
    }


    public ScheduleInfo getScheduleInfo(long interviewId, long referenceId) {
        ScheduleInfo scheduleInfo = null;

        String sql = " SELECT us.SCHED_ID SCHED_ID, us.CREATE_DATE CREATE_DATE, us.SCHED_DATE SCHED_DATE, us.ATTENDED_DATE ATTENDED_DATE, pim.QUESTIONNAIRE_ID QUESTIONNAIRE_ID  " + " FROM user_schedule us INNER JOIN patient_interview_master pim ON pim.INTERVIEW_ID=us.INTERVIEW_ID " + " WHERE  CASE  " + " WHEN us.REFERENCE_ID>0  THEN  us.REFERENCE_ID=" + referenceId + " AND us.`INTERVIEW_ID`=" + interviewId + " WHEN us.`REFERENCE_ID`=0  THEN us.`INTERVIEW_ID`=" + interviewId + " ELSE 0 " + " END ";

        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            scheduleInfo = new ScheduleInfo();
            scheduleInfo.setScheduleId(cursor.getLong(cursor.getColumnIndex("SCHED_ID")));
            scheduleInfo.setAttendedDate(cursor.getLong(cursor.getColumnIndex("ATTENDED_DATE")));
            scheduleInfo.setCreatedDate(cursor.getLong(cursor.getColumnIndex("CREATE_DATE")));
            scheduleInfo.setScheduleDate(cursor.getLong(cursor.getColumnIndex("SCHED_DATE")));
            scheduleInfo.setQuestionnaireId(cursor.getLong(cursor.getColumnIndex("QUESTIONNAIRE_ID")));
        }
        cursor.close();

        return scheduleInfo;
    }

    /**
     * Update requisition status.
     *
     * @param reqNo  the req no
     * @param status the status
     */
    public void updateRequisition(String reqNo, String status, Double dispatchPrice, JSONArray requisitionDtls) {
        long reqId = getRequisitionId(reqNo);
        ContentValues cv = new ContentValues();
        cv.put(Column.REQ_STATUS, status);
        if (dispatchPrice != null) {
            cv.put("TOTAL_PRICE_DISPATCH", dispatchPrice);
        }
        db.update("medicine_requisition_master", cv, Column.REQ_NO + "=?", new String[]{reqNo});

        try {
            if (requisitionDtls != null) {
                for (int i = 0; i < requisitionDtls.length(); i++) {
                    JSONObject reqDtl = requisitionDtls.getJSONObject(i);

                    // // Insert data into detail table
                    ContentValues dtlCv = new ContentValues();
                    dtlCv.put("QTY_DISPATCH", reqDtl.getLong("QTY_DISPATCH"));
                    dtlCv.put("PRICE_DISPATCH", reqDtl.getDouble("PRICE_DISPATCH"));
                    db.update("medicine_requisition_detail", dtlCv, "MEDICINE_ID=" + reqDtl.getLong("MEDICINE_ID") + " AND REQ_ID=" + reqId, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Update the schedule to attended.
     *
     * @param scheduleList The schedule list
     * @param dataSent     The Tag to determine whether the date is sent to server or not
     */
    public void updateEventScheduleList(ArrayList<UserScheduleInfo> scheduleList, String dataSent) {
        for (UserScheduleInfo scheduleInfo : scheduleList) {
            ContentValues cv = new ContentValues();
            cv.put("ATTENDED_DATE", scheduleInfo.getAttendedDate());
            cv.put("DATA_SENT", dataSent);
            cv.put("SCHED_STATUS", ScheduleStatus.ATTENDED);

            db.update("user_schedule", cv, "SCHED_DATE=? AND SCHED_DESC=? AND TYPE=?", new String[]{Long.toString(scheduleInfo.getScheduleDate()), scheduleInfo.getDescription(), scheduleInfo.getScheduleType()});
        }
    }

    /**
     * Save beneficiary name list.
     *
     * @param nameList The name list which will be saved
     */
    public void saveNameList(ArrayList<NameInfo> nameList) {
        for (int i = 0; i < nameList.size(); i++) {
            NameInfo name = nameList.get(i);

            String sql = "SELECT eng_name FROM name WHERE eng_name=?";
            Log.e("SQL", sql.toString());
            Cursor cursor = db.rawQuery(sql, new String[]{name.getEngName()});
            if (cursor.getCount() > 0) {
                cursor.close();
                continue;
            }
            cursor.close();

            ContentValues cv = new ContentValues();
            cv.put("eng_name", name.getEngName());
            cv.put("local_name", name.getLocalName());
            db.insert("name", null, cv);
        }
    }

    /**
     * Gets the month list.
     *
     * @param localCode the local code
     * @return the month list
     */
    public ArrayList<LocalMonthInfo> getMonthList(String localCode) {
        ArrayList<LocalMonthInfo> monthList = new ArrayList<LocalMonthInfo>();

        String sql = "SELECT NAME, NUMBER_OF_DAY, START_DAY, START_MONTH, END_DAY, END_MONTH " + "FROM month " + "WHERE LANG=? " + "ORDER BY ID";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{localCode});
        if (cursor.moveToFirst()) {
            do {
                LocalMonthInfo mi = new LocalMonthInfo();
                mi.setEndDay(cursor.getInt(cursor.getColumnIndex("END_DAY")));
                mi.setEndMonthIndex(cursor.getInt(cursor.getColumnIndex("END_MONTH")));
                mi.setName(cursor.getString(cursor.getColumnIndex("NAME")));
                mi.setNumberOfDays(cursor.getInt(cursor.getColumnIndex("NUMBER_OF_DAY")));
                mi.setStartDay(cursor.getInt(cursor.getColumnIndex("START_DAY")));
                mi.setStartMonthIndex(cursor.getInt(cursor.getColumnIndex("START_MONTH")));
                monthList.add(mi);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return monthList;
    }


    public ArrayList<LocalMonthInfo> getBengaliNameMonthList(String localCode) {
        ArrayList<LocalMonthInfo> monthList = new ArrayList<LocalMonthInfo>();

        String sql = "SELECT NAME, NUMBER_OF_DAY, START_DAY, START_MONTH, END_DAY, END_MONTH " + "FROM month_bn " + "WHERE LANG=? " + "ORDER BY ID";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{localCode});
        if (cursor.moveToFirst()) {
            do {
                LocalMonthInfo mi = new LocalMonthInfo();
                mi.setEndDay(cursor.getInt(cursor.getColumnIndex("END_DAY")));
                mi.setEndMonthIndex(cursor.getInt(cursor.getColumnIndex("END_MONTH")));
                mi.setName(cursor.getString(cursor.getColumnIndex("NAME")));
                mi.setNumberOfDays(cursor.getInt(cursor.getColumnIndex("NUMBER_OF_DAY")));
                mi.setStartDay(cursor.getInt(cursor.getColumnIndex("START_DAY")));
                mi.setStartMonthIndex(cursor.getInt(cursor.getColumnIndex("START_MONTH")));
                monthList.add(mi);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return monthList;
    }

    /**
     * Get unsent event schedule list.
     *
     * @param lang The Application language
     * @return unsent event schedule list
     */
    public ArrayList<UserScheduleInfo> getUnsentEventScheduleList(String lang) {
        // juba
        ArrayList<UserScheduleInfo> schedList = new ArrayList<UserScheduleInfo>();
        String sql = "SELECT " + "SCHED_ID, " + "TYPE TYPE, " + "SCHED_DATE, " + "ATTENDED_DATE " + "FROM user_schedule " + "WHERE  LANG=? AND INTERVIEW_ID=? AND SCHED_STATUS=? AND DATA_SENT=?";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{lang, "0", Integer.toString(ScheduleStatus.ATTENDED), "N"});
        if (cursor.moveToFirst()) {
            do {
                UserScheduleInfo usi = new UserScheduleInfo();
                usi.setScheduleID(cursor.getLong(cursor.getColumnIndex("SCHED_ID")));
                usi.setScheduleType(cursor.getString(cursor.getColumnIndex("TYPE")));
                usi.setScheduleDate(cursor.getLong(cursor.getColumnIndex("SCHED_DATE")));
                usi.setAttendedDate(cursor.getLong(cursor.getColumnIndex("ATTENDED_DATE")));
                schedList.add(usi);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return schedList;
    }

    /**
     * Get the eligible CCS beneficiary list.
     *
     * @return The beneficiary list
     */


    public long saveCourtYardMeeting(CourtyardMeeting meeting, long transRef) {
        if (meeting != null) {
            ContentValues cv = new ContentValues();
            cv.put("MEETING_DATE", meeting.getMeetingDate());
            cv.put("TOPIC_ID", meeting.getTopicId());
            cv.put("TOTAL_ATTENDENT", meeting.getTotalAttendent());
            cv.put("TOTAL_MALE_ATTENDANT", meeting.getTotalMaleAttendent());
            cv.put("TOTAL_FEMALE_ATTENDANT", meeting.getTotalFemaleAttendent());
            cv.put("MEETING_DURATION", meeting.getMeetingDuration());
            cv.put("INTERVIEW_ID", meeting.getInterviewId());
            cv.put("HH_NUMBER", meeting.getHhNumber());
            cv.put("TRANS_REF", transRef);
            return db.insert("courtyard_meeting", null, cv);
        }
        return -1;
    }

    public int updateCourtyardMeetingTopicMonth(long endDate, long topicId) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endDate);
        ContentValues cv = new ContentValues();
        cv.put("END_DATE", endDate);
        String whereClose = " TOPIC_ID=" + topicId + " AND MONTH_NO=" + cal.get(Calendar.MONTH);
        return db.update("courtyard_meeting_topic_month", cv, whereClose, null);
    }

    public void saveTopicInfo(JSONArray rows, JSONObject param) {
        if (rows == null) return;
        ContentValues cv = new ContentValues();
        JSONObject row;
        long count = 0;
        for (int i = 0; i < rows.length(); i++) {
            try {
                row = rows.getJSONObject(i);
                if (row.has("TOPIC_ID") && row.has("TOPIC_NAME")) {
                    cv.put("TOPIC_ID", row.getLong("TOPIC_ID"));
                    cv.put("TOPIC_NAME", row.getString("TOPIC_NAME"));
                    if (row.has("TOPIC_TITLE")) cv.put("TOPIC_TITLE", row.getString("TOPIC_TITLE"));
                    if (row.has("TOPIC_CODE")) cv.put("TOPIC_CODE", row.getString("TOPIC_CODE"));
                    if (row.has("STATE")) cv.put("STATE", row.getInt("STATE"));
                    cv.put("LANG_CODE", App.getContext().getAppSettings().getLanguage());
                    db.replace(DBTable.TOPIC_INFO, null, cv);
                    cv.clear();
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        updateDataVersion(DBTable.TOPIC_INFO, rows.length(), count, param, KEY.VERSION_NO_TOPIC_INFO);

    }

    public void saveCourtyardMeetingTopicMonth(JSONArray rows, JSONObject param) {
        if (rows == null) return;
        ContentValues cv = new ContentValues();
        JSONObject row;
        long count = 0;
        for (int i = 0; i < rows.length(); i++) {
            try {
                row = rows.getJSONObject(i);
                if (row.has("TOPIC_ID")) {
                    long mappingId = row.getLong("MAPPING_ID");
                    cv.put("MAPPING_ID", mappingId);
                    cv.put("TOPIC_ID", row.getLong("TOPIC_ID"));

                    if (row.has("MONTH_NO")) cv.put("MONTH_NO", row.getLong("MONTH_NO"));
                    if (row.has("ASSIGNED_BY")) cv.put("ASSIGNED_BY", row.getLong("ASSIGNED_BY"));
                    try {
                        if (row.has("START_DATE"))
                            cv.put("START_DATE", Utility.getMillisecondFromDate(row.getString("START_DATE"), Constants.DATE_FORMAT_DD_MM_YY));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (row.has("END_DATE"))
                            cv.put("END_DATE", Utility.getMillisecondFromDate(row.getString("END_DATE"), Constants.DATE_FORMAT_DD_MM_YY));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (row.has("ASSIGNED_DATE"))
                            cv.put("ASSIGNED_DATE", Utility.getMillisecondFromDate(row.getString("ASSIGNED_DATE"), Constants.DATE_FORMAT_DD_MM_YY));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String whereClose = "MAPPING_ID=" + mappingId;
                    if (isExist("courtyard_meeting_topic_month", whereClose)) {
                        db.update(DBTable.COURTYARD_MEETING_TOPIC_MONTH, cv, whereClose, null);
                    } else {
                        db.insert(DBTable.COURTYARD_MEETING_TOPIC_MONTH, null, cv);
                    }
                    count++;
                    cv.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        updateDataVersion(DBTable.COURTYARD_MEETING_TOPIC_MONTH, rows.length(), count, param, KEY.VERSION_NO_COURTYARD_MEETING_TOPIC_MONTH);

    }


    public void saveTextRef(ArrayList<TextRef> rows, JSONObject param) {
        String langCode = App.getContext().getAppSettings().getLanguage();
        long count = 0;
        for (TextRef textRef : rows) {
            ContentValues cv = new ContentValues();
            cv.put("TEXT_REF_ID", textRef.getTextRefId());
            cv.put("TEXT_NAME", textRef.getTextName());
            cv.put("TEXT_CAPTION", textRef.getTextCaption());
            cv.put("TEXT_CATEGORY", textRef.getTextCateogry());
            cv.put("SORT_ORDER", textRef.getSortOrder());
            cv.put("LANG_CODE", langCode);
            db.replace(DBTable.TEXT_REF, null, cv);
            count++;
        }

        updateDataVersion(DBTable.TEXT_REF, rows.size(), count, param, KEY.VERSION_NO_TEXT_REF);

    }

    public void saveEventInfoList(ArrayList<EventInfo> rows, JSONObject param) {
        ContentValues cv = new ContentValues();
        long count = 0;
        try {
            if (db != null) {
                db.beginTransaction();
                for (EventInfo info : rows) {
                    cv.put(Column.EVENT_ID, info.getEventId());
                    cv.put(Column.CREATE_DATE, info.getCreateDate());
                    cv.put(Column.CREATED_BY, info.getCreatedBy());
                    cv.put(Column.EVENT_DATE, info.getEventDate());
                    cv.put(Column.EVENT_DESC, info.getEventDesc());
                    cv.put(Column.EVENT_NAME, info.getEventName());
                    cv.put(Column.LOCATION_ID, info.getLocationId());
                    cv.put(Column.STATE, info.getState());
                    cv.put(Column.TYPE, info.getType());

                    db.replace(DBTable.EVENT_INFO, null, cv);
                    cv.clear();
                    count++;
                }

                updateDataVersion(DBTable.EVENT_INFO, rows.size(), count, param, KEY.VERSION_NO_EVENT_INFO);
                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    public void voidDataVersion(String language) {
        ContentValues cv = new ContentValues();
        cv.put("CURRENT_VERSION", 0);
        int affectedRow = db.update("data_version", cv, "LANG_CODE=?", new String[]{language});
    }


    public long getTextRefId(String textName, String txtCatagory) {
        long reasonId = -1;

        String sql = " SELECT TEXT_REF_ID FROM text_ref WHERE TEXT_NAME='" + textName + "' AND TEXT_CATEGORY='" + txtCatagory + "' AND LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            reasonId = cursor.getLong(cursor.getColumnIndex("TEXT_REF_ID"));
        }
        return reasonId;
    }

    public long getDignosisId(String textName) {
        long reasonId = -1;
        String sql = " SELECT DIAG_ID FROM diagnosis_info where upper(trim(DIAG_NAME))='" + textName.trim().toUpperCase() + "'";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            reasonId = cursor.getLong(cursor.getColumnIndex("DIAG_ID"));
        }
        return reasonId;
    }

    public long getTopicId(String topicName) {
        long reasonId = -1;

        String sql = " SELECT TOPIC_ID FROM topic_info WHERE TOPIC_NAME='" + topicName + "'  AND LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            reasonId = cursor.getLong(cursor.getColumnIndex("TOPIC_ID"));
        }
        return reasonId;
    }


    public ArrayList<String> getTextCaptions(ArrayList<String> textNames, String txtCatagory) {
        ArrayList<String> captions = new ArrayList<String>();
        if (textNames == null) return captions;
        String sql = " SELECT TEXT_CAPTION FROM text_ref WHERE  TEXT_CATEGORY='" + txtCatagory + "' AND TEXT_NAME in ('" + TextUtils.join("','", textNames) + "')  AND LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "' ORDER BY SORT_ORDER ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                captions.add(cursor.getString(cursor.getColumnIndex("TEXT_CAPTION")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return captions;
    }

    public String getTextCaption(String textName, String txtCatagory) {
        if (textName == null) return "";
        String sql = " SELECT TEXT_CAPTION FROM text_ref WHERE TEXT_NAME='" + textName + "' AND TEXT_CATEGORY='" + txtCatagory + "' AND LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        String caption = "";

        if (cursor.moveToFirst()) {
            caption = cursor.getString(cursor.getColumnIndex("TEXT_CAPTION"));
        }
        cursor.close();
        return caption;
    }

    public ArrayList<TextRef> getTextRef(String txtCatagory) {
        String sql = " SELECT TEXT_REF_ID,TEXT_NAME,TEXT_CAPTION,LANG_CODE,TEXT_CATEGORY  FROM text_ref" + "  WHERE  TEXT_CATEGORY='" + txtCatagory + "' AND LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "' ORDER BY SORT_ORDER  ASC";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<TextRef> txtRefs = new ArrayList<TextRef>();
        if (cursor.moveToFirst()) {
            do {
                TextRef textRef = new TextRef();
                textRef.setTextRefId(cursor.getLong(cursor.getColumnIndex("TEXT_REF_ID")));
                textRef.setTextName(cursor.getString(cursor.getColumnIndex("TEXT_NAME")));
                textRef.setTextCaption(cursor.getString(cursor.getColumnIndex("TEXT_CAPTION")));
                textRef.setTextCateogry(cursor.getString(cursor.getColumnIndex("TEXT_CATEGORY")));
                txtRefs.add(textRef);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return txtRefs;
    }

    public JSONArray getServiceList(String benefCode, long numberOfService) {

        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT Strftime('%d-%m-%Y', pim.trans_ref / 1000, 'unixepoch') ");
        sql.append("       || ' - ' ");
        sql.append("       || q.questionnaire_title TITLE, ");
        sql.append("       pim.trans_ref            VALUE ");
        sql.append(" FROM   patient_interview_master pim ");
        sql.append("       JOIN questionnaire q ");
        sql.append("         ON q.questionnaire_id = pim.questionnaire_id ");
        sql.append("            AND q.lang_code = 'bn' ");
        sql.append(" WHERE  pim.benef_code = '" + benefCode + "' ");
        sql.append("       AND q.name != 'BENEFICIARY_REGISTRATION' ");
        sql.append(" ORDER  BY pim.trans_ref DESC ");
        sql.append(" LIMIT  " + numberOfService);

        Cursor cursor = db.rawQuery(sql.toString(), null);
        JSONArray jsonRows = new JSONArray();
        try {
            jsonRows = Utility.toJsonArray(cursor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cursor.close();
        return jsonRows;
    }

    public String getInterviewQuestionnaireTitle(String transref) {
        String title = "";
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT q.questionnaire_title TITLE ");
        sql.append(" FROM   patient_interview_master pim ");
        sql.append("       JOIN questionnaire q ");
        sql.append("         ON q.questionnaire_id = pim.questionnaire_id ");
        sql.append("            AND q.lang_code = '" + App.getContext().getAppSettings().getLanguage() + "' ");
        sql.append(" WHERE   pim.trans_ref=" + transref);

        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            title = cursor.getString(cursor.getColumnIndex("TITLE"));

        }
        cursor.close();
        return title;
    }

    public ArrayList<TextRef> getTopicInfo(long monthNo) {
        String sql = " select t.TOPIC_ID TEXT_REF_ID ,t.TOPIC_NAME TEXT_NAME ,t.TOPIC_TITLE TEXT_CAPTION from topic_info t " + " join courtyard_meeting_topic_month c " + " on t.TOPIC_ID=c.TOPIC_ID " + " where  " + " c.END_DATE IS NULL  AND " + "  c.MONTH_NO=" + monthNo + " and t.STATE=1 " + " and t.LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'" + " ORDER BY TEXT_REF_ID ASC";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        ArrayList<TextRef> txtRefs = new ArrayList<TextRef>();
        if (cursor.moveToFirst()) {
            do {
                TextRef textRef = new TextRef();
                textRef.setTextRefId(cursor.getLong(cursor.getColumnIndex("TEXT_REF_ID")));
                textRef.setTextName(cursor.getString(cursor.getColumnIndex("TEXT_NAME")));
                textRef.setTextCaption(cursor.getString(cursor.getColumnIndex("TEXT_CAPTION")));
                txtRefs.add(textRef);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return txtRefs;
    }

    /**
     * Save PatientInterviewDoctorFeedback
     *
     * @param doctorFeedback instance of PatientInterviewDoctorFeedback
     */
    public void savePatientInterviewDoctorFeedback(PatientInterviewDoctorFeedback doctorFeedback) {
        ContentValues cv = new ContentValues();
        cv.put(Column.DOC_FOLLOWUP_ID, doctorFeedback.getDocFollowupId());
        cv.put(Column.INTERVIEW_ID, doctorFeedback.getInterviewId());
        cv.put(Column.FEEDBACK_DATE, doctorFeedback.getFeedbackDate());
        cv.put(Column.REF_CENTER_ID, doctorFeedback.getRefCenterId());
        cv.put(Column.PRESCRIBED_MEDICINE, doctorFeedback.getPrescribedMedicine());
        cv.put(Column.NEXT_FOLLOWUP_DATE, doctorFeedback.getNextFollowupDate());
        cv.put(Column.SEND_STATUS, 0);
        cv.put(Column.MESSAGE_TO_FCM, doctorFeedback.getMessageToFCM());
        cv.put(Column.IS_FEEDBACK_ON_TIME, doctorFeedback.getIsFeedbackOnTime());
        cv.put(Column.FEEDBACK_SOURCE, doctorFeedback.getFeedbackSource());
        cv.put(Column.INVES_ADVICE, doctorFeedback.getInvesAdvice());
        cv.put(Column.INVES_RESULT, doctorFeedback.getInvesResult());
        cv.put(Column.INVES_STATUS, doctorFeedback.getInvesStatus());
        cv.put(Column.NOTIFICATION_STATUS, doctorFeedback.getNotificationStatus());
        cv.put(Column.TRANS_REF, doctorFeedback.getTransRef());

        int numberOfRowAffected = db.update(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, cv, Column.INTERVIEW_ID + "=? AND " + Column.TRANS_REF + "<? AND " + Column.DOC_FOLLOWUP_ID + "=? ", new String[]{Long.toString(doctorFeedback.getInterviewId()), Long.toString(doctorFeedback.getTransRef()), Long.toString(doctorFeedback.getDocFollowupId())});

        if (numberOfRowAffected <= 0)
            db.replace(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, null, cv);
        cv.clear();

        //db.insert("patient_interview_doctor_feedback", null, cv);
    }

    public void savePatientInterviewDoctorFeedbackFromInstantDoctorCenter(PatientInterviewDoctorFeedback doctorFeedback, long oldDocFollowupId, long oldInterviewId) {
        ContentValues cv = new ContentValues();
        cv.put(Column.DOC_FOLLOWUP_ID, doctorFeedback.getDocFollowupId());
        //cv.put(Column.INTERVIEW_ID, doctorFeedback.getInterviewId());
        //cv.put(Column.FEEDBACK_DATE, doctorFeedback.getFeedbackDate());
        cv.put(Column.REF_CENTER_ID, doctorFeedback.getRefCenterId());
        cv.put(Column.DOCTOR_FINDINGS, doctorFeedback.getDoctorFindings());
        cv.put(Column.PRESCRIBED_MEDICINE, doctorFeedback.getPrescribedMedicine());
        cv.put(Column.NEXT_FOLLOWUP_DATE, doctorFeedback.getNextFollowupDate());
        cv.put(Column.SEND_STATUS, 0);
        cv.put(Column.MESSAGE_TO_FCM, doctorFeedback.getMessageToFCM());
        cv.put(Column.IS_FEEDBACK_ON_TIME, doctorFeedback.getIsFeedbackOnTime());
        cv.put(Column.FEEDBACK_SOURCE, doctorFeedback.getFeedbackSource());
        cv.put(Column.INVES_ADVICE, doctorFeedback.getInvesAdvice());
        cv.put(Column.INVES_RESULT, doctorFeedback.getInvesResult());
        cv.put(Column.INVES_STATUS, doctorFeedback.getInvesStatus());
        cv.put(Column.NOTIFICATION_STATUS, 1);
        //cv.put(Column.TRANS_REF, 0);


        int numberOfRowAffected = db.update(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, cv, Column.INTERVIEW_ID + "=? AND " + Column.DOC_FOLLOWUP_ID + "=? ", new String[]{Long.toString(oldInterviewId), Long.toString(oldDocFollowupId)});

        if (numberOfRowAffected <= 0)
            db.replace(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, null, cv);
        cv.clear();
    }

    /**
     * Update PatientInterviewDoctorFeedback
     * REF_CENTER_ID,PRESCRIBED_MEDICINE,NEXT_FOLLOWUP_DATE,MESSAGE_TO_FCM
     *
     * @param doctorFeedback instance of PatientInterviewDoctorFeedback
     */
    public int updatePatientInterviewDoctorFeedback(PatientInterviewDoctorFeedback doctorFeedback) {
        ContentValues cv = new ContentValues();
        cv.put(Column.REF_CENTER_ID, doctorFeedback.getRefCenterId());
        cv.put(Column.PRESCRIBED_MEDICINE, doctorFeedback.getPrescribedMedicine());
        cv.put(Column.SEND_STATUS, 0);
        cv.put(Column.NEXT_FOLLOWUP_DATE, doctorFeedback.getNextFollowupDate());
        cv.put(Column.MESSAGE_TO_FCM, doctorFeedback.getMessageToFCM());
        cv.put(Column.IS_FEEDBACK_ON_TIME, doctorFeedback.getIsFeedbackOnTime());
        cv.put(Column.FEEDBACK_SOURCE, doctorFeedback.getFeedbackSource());
        cv.put(Column.INVES_ADVICE, doctorFeedback.getInvesAdvice());
        cv.put(Column.INVES_RESULT, doctorFeedback.getInvesResult());
        cv.put(Column.INVES_STATUS, doctorFeedback.getInvesStatus());
        cv.put(Column.NOTIFICATION_STATUS, doctorFeedback.getNotificationStatus());
        cv.put(Column.TRANS_REF, doctorFeedback.getTransRef());
        cv.put(Column.UPDATE_BY, doctorFeedback.getUpdateBy());
        cv.put(Column.UPDATE_ON, doctorFeedback.getUpdateOn());

        return db.update("patient_interview_doctor_feedback", cv, "INTERVIEW_ID=? AND " + Column.DOC_FOLLOWUP_ID + "=? ", new String[]{Long.toString(doctorFeedback.getInterviewId()), Long.toString(doctorFeedback.getDocFollowupId())});
    }

    public int updatePatientInterviewDoctorFeedback(long interviewId, int sentStatus, String questionAnsJson) {
        ContentValues cv = new ContentValues();
        cv.put("SEND_STATUS", sentStatus);
        cv.put("QUESTION_ANSWER_JSON", questionAnsJson);


        return db.update("patient_interview_doctor_feedback", cv, "INTERVIEW_ID=?", new String[]{Long.toString(interviewId)});
    }


    public int updatePatientInterviewDoctorFeedbackSent(long docFeedBack, int status) {
        ContentValues cv = new ContentValues();
        cv.put("SEND_STATUS", status);
        return db.update("patient_interview_doctor_feedback", cv, "DOC_FOLLOWUP_ID=?", new String[]{Long.toString(docFeedBack)});
    }

    public int updatePatientInterviewDoctorFeedbacksInterviewID(long docFollowupId, long fcmInterviewId) {
        ContentValues cv = new ContentValues();
        cv.put(Column.INTERVIEW_ID, fcmInterviewId);
        return db.update("patient_interview_doctor_feedback", cv, "DOC_FOLLOWUP_ID=?", new String[]{Long.toString(docFollowupId)});
    }


    public int updatePatientInterViewMaster(long interviewId, int isFeedBack) {
        ContentValues cv = new ContentValues();
        cv.put("IS_FEEDBACK", isFeedBack);
        return db.update("patient_interview_master", cv, "INTERVIEW_ID=?", new String[]{Long.toString(interviewId)});
    }

    public void savePatientInterviewDoctorFeedbacks(ArrayList<PatientInterviewDoctorFeedback> patientInterviewDoctorFeedbacks, JSONObject param) {
        ContentValues cv = new ContentValues();
        long count = 0;
        for (PatientInterviewDoctorFeedback patientInterviewDoctorFeedback : patientInterviewDoctorFeedbacks) {

            String whereClose = " DOC_FOLLOWUP_ID=" + patientInterviewDoctorFeedback.getDocFollowupId() + " AND TRANS_REF= " + patientInterviewDoctorFeedback.getTransRef() + " AND NOTIFICATION_STATUS = -1";


            long interviewId = patientInterviewDoctorFeedback.getInterviewId();
            long serverInterviewId = patientInterviewDoctorFeedback.getInterviewId();
            long fcmLocalInterviewId = patientInterviewDoctorFeedback.getFcmInterviewId();
            String benefCode = patientInterviewDoctorFeedback.getBenefCode();
            String serverInterviewIdWhereClause = Column.INTERVIEW_ID + " =" + serverInterviewId + " and " + Column.BENEF_CODE + " ='" + benefCode + "'";
            String localInterviewIdwhereClause = Column.INTERVIEW_ID + " =" + fcmLocalInterviewId + " and " + Column.BENEF_CODE + " ='" + benefCode + "'";

            if (App.getContext().getDB().isExist(DBTable.PATIENT_INTERVIEW_MASTER, localInterviewIdwhereClause)) {
                interviewId = patientInterviewDoctorFeedback.getFcmInterviewId();
            } else if (App.getContext().getDB().isExist(DBTable.PATIENT_INTERVIEW_MASTER, serverInterviewIdWhereClause)) {
                interviewId = patientInterviewDoctorFeedback.getInterviewId();

            }

            if (!isExist("patient_interview_doctor_feedback", whereClose)) {

                cv.put(Column.DOC_FOLLOWUP_ID, patientInterviewDoctorFeedback.getDocFollowupId());
                cv.put(Column.INTERVIEW_ID, interviewId);
                cv.put("FEEDBACK_DATE", patientInterviewDoctorFeedback.getFeedbackDate());
                cv.put("USER_ID", patientInterviewDoctorFeedback.getUserId());
                if (patientInterviewDoctorFeedback.getRefCenterId() > 0) {
                    cv.put("REF_CENTER_ID", patientInterviewDoctorFeedback.getRefCenterId());
                }
                cv.put("PRESCRIBED_MEDICINE", TextUtility.getEmptyStringFromNull(patientInterviewDoctorFeedback.getPrescribedMedicine()));
                cv.put("NEXT_FOLLOWUP_DATE", patientInterviewDoctorFeedback.getNextFollowupDate());
                cv.put("SEND_STATUS", 1);
                cv.put("MESSAGE_TO_FCM", TextUtility.getEmptyStringFromNull(patientInterviewDoctorFeedback.getMessageToFCM()));
                cv.put("DOCTOR_FINDINGS", TextUtility.getEmptyStringFromNull(patientInterviewDoctorFeedback.getDoctorFindings()));

                cv.put("IS_FEEDBACK_ON_TIME", patientInterviewDoctorFeedback.getIsFeedbackOnTime());
                cv.put("FEEDBACK_SOURCE", TextUtility.getEmptyStringFromNull(patientInterviewDoctorFeedback.getFeedbackSource()));
                cv.put("INVES_ADVICE", TextUtility.getEmptyStringFromNull(patientInterviewDoctorFeedback.getInvesAdvice()));
                cv.put("INVES_RESULT", TextUtility.getEmptyStringFromNull(patientInterviewDoctorFeedback.getInvesResult()));
                cv.put("INVES_STATUS", TextUtility.getEmptyStringFromNull(patientInterviewDoctorFeedback.getInvesStatus()));
                cv.put("TRANS_REF", patientInterviewDoctorFeedback.getTransRef());
                cv.put("QUESTION_ANSWER_JSON", patientInterviewDoctorFeedback.getQuestionAnsJson());
                cv.put("QUESTION_ANSWER_JSON2", patientInterviewDoctorFeedback.getQuestionAnsJson2());
                if (patientInterviewDoctorFeedback.getNotificationStatus() == -1) {
                    cv.put(Column.NOTIFICATION_STATUS, patientInterviewDoctorFeedback.getNotificationStatus());
                }

                cv.put(Column.FEEDBACK_RECEIVE_TIME, patientInterviewDoctorFeedback.getFeedbackReceiveTime());
                cv.put(Column.BENEF_CODE, patientInterviewDoctorFeedback.getBenefCode());
                cv.put(Column.UPDATE_ON, patientInterviewDoctorFeedback.getUpdateOn());
                cv.put(Column.UPDATE_BY, patientInterviewDoctorFeedback.getUpdateBy());

                int numberOfRowAffected = db.update(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, cv, Column.DOC_FOLLOWUP_ID + "=? AND "
//                                + Column.INTERVIEW_ID + "=? AND "
                        + Column.TRANS_REF + "<?", new String[]{Long.toString(patientInterviewDoctorFeedback.getDocFollowupId()),
//                                Long.toString(patientInterviewDoctorFeedback.getInterviewId()),
                        Long.toString(patientInterviewDoctorFeedback.getTransRef())});

                if (numberOfRowAffected <= 0) {
                    db.replace(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, null, cv);
                }

                //db.replace(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, null, cv);
                cv.clear();
                count++;

            }
        }
        updateDataVersion(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, patientInterviewDoctorFeedbacks.size(), count, param, KEY.TRANS_REF_PATIENT_INTERVIEW_DOCTOR_FEEDBACK);
    }

    public ArrayList<PatientInterviewDoctorFeedback> getPatientInterviewDoctorFeedbackList(long sendStatus, String langCode) {
        ArrayList<PatientInterviewDoctorFeedback> doctorFeedbackList = new ArrayList<>();
        String sql = " SELECT date( pidf.FEEDBACK_DATE / 1000, 'unixepoch', 'localtime' )  FEEDBACK_DATE, " + " pidf.DOC_FOLLOWUP_ID DOC_FOLLOWUP_ID, " + " pidf.INTERVIEW_ID INTERVIEW_ID, " + " pidf.IS_FEEDBACK_ON_TIME IS_FEEDBACK_ON_TIME, " + " pidf.FEEDBACK_SOURCE FEEDBACK_SOURCE, " + " pidf.FEEDBACK_DATE FEEDBACK_DATE, " + " pidf.INVES_ADVICE INVES_ADVICE, " + " pidf.INVES_RESULT INVES_RESULT, " + " pidf.INVES_STATUS INVES_STATUS, " + " pidf.NOTIFICATION_STATUS NOTIFICATION_STATUS, " + " pidf.FEEDBACK_RECEIVE_TIME FEEDBACK_RECEIVE_TIME, " + " q.QUESTIONNAIRE_TITLE INTERVIEW_NAME, " + " pidf.QUESTION_ANSWER_JSON QUESTION_ANSWER_JSON," + " pidf.QUESTION_ANSWER_JSON2 QUESTION_ANSWER_JSON2," + " pidf.USER_ID USER_ID," + " pidf.DOCTOR_FINDINGS DOCTOR_FINDINGS," + " pidf.REF_CENTER_ID REF_CENTER_ID," + " pidf.PRESCRIBED_MEDICINE PRESCRIBED_MEDICINE," + " pidf.NEXT_FOLLOWUP_DATE NEXT_FOLLOWUP_DATE," + " pidf.MESSAGE_TO_FCM MESSAGE_TO_FCM," + " pidf.TRANS_REF TRANS_REF," + " pidf.UPDATE_BY UPDATE_BY," + " pidf.UPDATE_ON UPDATE_ON," + " b.BENEF_NAME BENEF_NAME, " + " b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH," + " b.BENEF_CODE BENEF_CODE, " + " substr(b.BENEF_CODE, " + "  -5, " + "  5 ) BENEF_CODE_SHORT, " + " b.BENEF_ID BENEF_ID, " + " b.GENDER GENDER, " + " b.DOB DOB, " + " q.NAME Q_NAME, " + " pidf.SEND_STATUS SEND_STATUS," + " substr( b.benef_code, -5, 3 )  HH_NUMBER," + " CASE " + "      WHEN CAST ( strftime( '%H', pidf.TRANS_REF / 1000, 'unixepoch', 'localtime' )  AS INTEGER ) < 12 THEN strftime( '%H:%M', pidf.TRANS_REF / 1000, 'unixepoch', 'localtime' ) || ' AM' " + "     ELSE  strftime('%H:%M',time(strftime('%H:%M', pidf.TRANS_REF/1000, 'unixepoch', 'localtime'),'-12 hours'))  || ' PM' " + " END AS INTERVIEW_TIME" + " FROM patient_interview_doctor_feedback pidf" + " LEFT JOIN patient_interview_master pim" + "        ON pim.INTERVIEW_ID = pidf.INTERVIEW_ID " + " LEFT JOIN questionnaire q" + "       ON pim.QUESTIONNAIRE_ID = q.QUESTIONNAIRE_ID AND q.LANG_CODE='" + langCode + "'" + "  JOIN beneficiary b" + "       ON pim.BENEF_CODE = b.BENEF_CODE " + " WHERE pidf.SEND_STATUS=" + sendStatus + " AND CASE WHEN pidf.QUESTION_ANSWER_JSON ISNULL AND pidf.SEND_STATUS=0 THEN 0 ELSE 1 END " + " GROUP BY pidf.DOC_FOLLOWUP_ID " + " ORDER BY FEEDBACK_DATE DESC ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        String pdifDate = "";
        if (cursor.moveToFirst()) {
//            patientInterviewDoctorFeedbackLists = new ArrayList<PatientInterviewDoctorFeedbackList>();
//            String pdifDate = "";
            do {
                PatientInterviewDoctorFeedback patientInterviewDoctorFeedback = new PatientInterviewDoctorFeedback();
                if (!pdifDate.trim().equals(cursor.getString(cursor.getColumnIndex("FEEDBACK_DATE")).trim())) {
                    pdifDate = cursor.getString(cursor.getColumnIndex("FEEDBACK_DATE"));
                    patientInterviewDoctorFeedback.setNewDate(true);
                }
//                if (!pdifDate
//                        .trim()
//                        .equals(cursor.getString(
//                                cursor.getColumnIndex("INTERVIEW_DATE")).trim())) {
//                    pdifDate = cursor.getString(cursor
//                            .getColumnIndex("INTERVIEW_DATE"));
//                    doctorFeedbackList = new PatientInterviewDoctorFeedbackList();
//                    doctorFeedbackList
//                            .setDoctorFeedBack(new ArrayList<PatientInterviewDoctorFeedback>());
//                    doctorFeedbackList.setInterviewDate(pdifDate);
////                    patientInterviewDoctorFeedbackLists.add(doctorFeedbackList);
//                }

                patientInterviewDoctorFeedback.setInterviewId(cursor.getLong(cursor.getColumnIndex(Column.INTERVIEW_ID)));
                patientInterviewDoctorFeedback.setIsFeedbackOnTime(cursor.getLong(cursor.getColumnIndex(Column.IS_FEEDBACK_ON_TIME)));
                patientInterviewDoctorFeedback.setFeedbackSource(cursor.getString(cursor.getColumnIndex(Column.FEEDBACK_SOURCE)));
                patientInterviewDoctorFeedback.setInvesAdvice(cursor.getString(cursor.getColumnIndex(Column.INVES_ADVICE)));
                patientInterviewDoctorFeedback.setInvesResult(cursor.getString(cursor.getColumnIndex(Column.INVES_RESULT)));
                patientInterviewDoctorFeedback.setInvesStatus(cursor.getString(cursor.getColumnIndex(Column.INVES_STATUS)));
                patientInterviewDoctorFeedback.setDocFollowupId(cursor.getLong(cursor.getColumnIndex(Column.DOC_FOLLOWUP_ID)));
                patientInterviewDoctorFeedback.setInterviewName(cursor.getString(cursor.getColumnIndex("INTERVIEW_NAME")));
                patientInterviewDoctorFeedback.setBenefName(cursor.getString(cursor.getColumnIndex(Column.BENEF_NAME)));
                patientInterviewDoctorFeedback.setHouseHoldNumber(cursor.getString(cursor.getColumnIndex(Column.HH_NUMBER)));
                patientInterviewDoctorFeedback.setSendStatus(cursor.getInt(cursor.getColumnIndex(Column.SEND_STATUS)));
                patientInterviewDoctorFeedback.setBenefImagePath(cursor.getString(cursor.getColumnIndex(Column.BENEFICIARY_BENEF_IMAGE_PATH)));
                patientInterviewDoctorFeedback.setInterviewTime(cursor.getString(cursor.getColumnIndex("INTERVIEW_TIME")));
                patientInterviewDoctorFeedback.setQuestionAnsJson(cursor.getString(cursor.getColumnIndex(Column.QUESTION_ANSWER_JSON)));

                patientInterviewDoctorFeedback.setQuestionAnsJson2(cursor.getString(cursor.getColumnIndex(Column.QUESTION_ANSWER_JSON2)));
                patientInterviewDoctorFeedback.setNotificationStatus(cursor.getInt(cursor.getColumnIndex(Column.NOTIFICATION_STATUS)));

                patientInterviewDoctorFeedback.setFeedbackDate(cursor.getLong(cursor.getColumnIndex(Column.FEEDBACK_DATE)));

                patientInterviewDoctorFeedback.setFeedbackReceiveTime(cursor.getLong(cursor.getColumnIndex(Column.FEEDBACK_RECEIVE_TIME)));


                patientInterviewDoctorFeedback.setUserId(cursor.getLong(cursor.getColumnIndex(Column.USER_ID)));

                patientInterviewDoctorFeedback.setRefCenterId(cursor.getLong(cursor.getColumnIndex(Column.REF_CENTER_ID)));

                patientInterviewDoctorFeedback.setNextFollowupDate(cursor.getLong(cursor.getColumnIndex(Column.NEXT_FOLLOWUP_DATE)));

                patientInterviewDoctorFeedback.setTransRef(cursor.getLong(cursor.getColumnIndex(Column.TRANS_REF)));

                patientInterviewDoctorFeedback.setDoctorFindings(cursor.getString(cursor.getColumnIndex(Column.DOCTOR_FINDINGS)));

                patientInterviewDoctorFeedback.setPrescribedMedicine(cursor.getString(cursor.getColumnIndex(Column.PRESCRIBED_MEDICINE)));

                patientInterviewDoctorFeedback.setqName(cursor.getString(cursor.getColumnIndex("Q_NAME")));
                patientInterviewDoctorFeedback.setBenefCode(cursor.getString(cursor.getColumnIndex(Column.BENEF_CODE)));

                patientInterviewDoctorFeedback.setBenefId(cursor.getLong(cursor.getColumnIndex(Column.BENEF_ID)));

                patientInterviewDoctorFeedback.setUpdateBy(cursor.getLong(cursor.getColumnIndex(Column.UPDATE_BY)));

                patientInterviewDoctorFeedback.setUpdateOn(cursor.getLong(cursor.getColumnIndex(Column.UPDATE_ON)));

                String dob = "";
                try {
                    dob = cursor.getString(cursor.getColumnIndex("DOB"));
                    if (!dob.equalsIgnoreCase("")) {
                        patientInterviewDoctorFeedback.setDob(Utility.getAge(dob));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                patientInterviewDoctorFeedback.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));

                patientInterviewDoctorFeedback.setBenefCodeShort(cursor.getString(cursor.getColumnIndex("BENEF_CODE_SHORT")));


                doctorFeedbackList.add(patientInterviewDoctorFeedback);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return doctorFeedbackList;
    }

    /**
     * Get the PatientInterviewDoctorFeedback List
     *
     * @return ArrayList<PatientInterviewDoctorFeedback>
     */

//    copy date by 01/02/2023
//    public ArrayList<PatientInterviewDoctorFeedbackList> getPatientInterviewDoctorFeedbackList(
//            long sendStatus, String langCode) {
//        ArrayList<PatientInterviewDoctorFeedbackList> patientInterviewDoctorFeedbackLists = null;
//        String sql = " SELECT date( pidf.FEEDBACK_DATE / 1000, 'unixepoch', 'localtime' )  INTERVIEW_DATE, "
//                + " pidf.DOC_FOLLOWUP_ID DOC_FOLLOWUP_ID, "
//                + " pidf.INTERVIEW_ID INTERVIEW_ID, "
//                + " pidf.IS_FEEDBACK_ON_TIME IS_FEEDBACK_ON_TIME, "
//                + " pidf.FEEDBACK_SOURCE FEEDBACK_SOURCE, "
//                + " pidf.FEEDBACK_DATE FEEDBACK_DATE, "
//                + " pidf.INVES_ADVICE INVES_ADVICE, "
//                + " pidf.INVES_RESULT INVES_RESULT, "
//                + " pidf.INVES_STATUS INVES_STATUS, "
//                + " pidf.NOTIFICATION_STATUS NOTIFICATION_STATUS, "
//                + " pidf.FEEDBACK_RECEIVE_TIME FEEDBACK_RECEIVE_TIME, "
//                + " q.QUESTIONNAIRE_TITLE INTERVIEW_NAME, "
//                + " pidf.QUESTION_ANSWER_JSON QUESTION_ANSWER_JSON,"
//                + " pidf.QUESTION_ANSWER_JSON2 QUESTION_ANSWER_JSON2,"
//                + " pidf.USER_ID USER_ID,"
//                + " pidf.DOCTOR_FINDINGS DOCTOR_FINDINGS,"
//                + " pidf.REF_CENTER_ID REF_CENTER_ID,"
//                + " pidf.PRESCRIBED_MEDICINE PRESCRIBED_MEDICINE,"
//                + " pidf.NEXT_FOLLOWUP_DATE NEXT_FOLLOWUP_DATE,"
//                + " pidf.MESSAGE_TO_FCM MESSAGE_TO_FCM,"
//                + " pidf.TRANS_REF TRANS_REF,"
//                + " pidf.UPDATE_BY UPDATE_BY,"
//                + " pidf.UPDATE_ON UPDATE_ON,"
//                + " b.BENEF_NAME BENEF_NAME, "
//                + " b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH,"
//                + " b.BENEF_CODE BENEF_CODE, "
//                + " substr(b.BENEF_CODE, "
//                + "  -5, "
//                + "  5 ) BENEF_CODE_SHORT, "
//                + " b.BENEF_ID BENEF_ID, "
//                + " b.GENDER GENDER, "
//                + " b.DOB DOB, "
//                + " q.NAME Q_NAME, "
//                + " pidf.SEND_STATUS SEND_STATUS,"
//                + " substr( b.benef_code, -5, 3 )  HH_NUMBER,"
//                + " CASE "
//                + "      WHEN CAST ( strftime( '%H', pidf.TRANS_REF / 1000, 'unixepoch', 'localtime' )  AS INTEGER ) < 12 THEN strftime( '%H:%M', pidf.TRANS_REF / 1000, 'unixepoch', 'localtime' ) || ' AM' "
//                + "     ELSE  strftime('%H:%M',time(strftime('%H:%M', pidf.TRANS_REF/1000, 'unixepoch', 'localtime'),'-12 hours'))  || ' PM' "
//                + " END AS INTERVIEW_TIME"
//                + " FROM patient_interview_doctor_feedback pidf"
//                + " LEFT JOIN patient_interview_master pim"
//                + "        ON pim.INTERVIEW_ID = pidf.INTERVIEW_ID "
//                + " LEFT JOIN questionnaire q"
//                + "       ON pim.QUESTIONNAIRE_ID = q.QUESTIONNAIRE_ID AND q.LANG_CODE='"
//                + langCode
//                + "'"
//                + "  JOIN beneficiary b"
//                + "       ON pim.BENEF_CODE = b.BENEF_CODE "
//                + " WHERE pidf.SEND_STATUS=" + sendStatus
//                + " AND CASE WHEN pidf.QUESTION_ANSWER_JSON ISNULL AND pidf.SEND_STATUS=0 THEN 0 ELSE 1 END "
//                + " GROUP BY pidf.DOC_FOLLOWUP_ID "
//                + " ORDER BY FEEDBACK_DATE DESC ";
//
//        Log.e("SQL", sql.toString());
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) {
//            patientInterviewDoctorFeedbackLists = new ArrayList<PatientInterviewDoctorFeedbackList>();
//            String pdifDate = "";
//            PatientInterviewDoctorFeedbackList doctorFeedbackList = new PatientInterviewDoctorFeedbackList();
//            doctorFeedbackList
//                    .setDoctorFeedBack(new ArrayList<PatientInterviewDoctorFeedback>());
//            do {
//                if (!pdifDate
//                        .trim()
//                        .equals(cursor.getString(
//                                cursor.getColumnIndex("INTERVIEW_DATE")).trim())) {
//                    pdifDate = cursor.getString(cursor
//                            .getColumnIndex("INTERVIEW_DATE"));
//                    doctorFeedbackList = new PatientInterviewDoctorFeedbackList();
//                    doctorFeedbackList
//                            .setDoctorFeedBack(new ArrayList<PatientInterviewDoctorFeedback>());
//                    doctorFeedbackList.setInterviewDate(pdifDate);
//                    patientInterviewDoctorFeedbackLists.add(doctorFeedbackList);
//                }
//                PatientInterviewDoctorFeedback patientInterviewDoctorFeedback = new PatientInterviewDoctorFeedback();
//                patientInterviewDoctorFeedback.setInterviewId(cursor
//                        .getLong(cursor.getColumnIndex(Column.INTERVIEW_ID)));
//                patientInterviewDoctorFeedback.setIsFeedbackOnTime(cursor
//                        .getLong(cursor.getColumnIndex(Column.IS_FEEDBACK_ON_TIME)));
//                patientInterviewDoctorFeedback.setFeedbackSource(cursor
//                        .getString(cursor.getColumnIndex(Column.FEEDBACK_SOURCE)));
//                patientInterviewDoctorFeedback.setInvesAdvice(cursor
//                        .getString(cursor.getColumnIndex(Column.INVES_ADVICE)));
//                patientInterviewDoctorFeedback.setInvesResult(cursor
//                        .getString(cursor.getColumnIndex(Column.INVES_RESULT)));
//                patientInterviewDoctorFeedback.setInvesStatus(cursor
//                        .getString(cursor.getColumnIndex(Column.INVES_STATUS)));
//                patientInterviewDoctorFeedback.setDocFollowupId(cursor
//                        .getLong(cursor.getColumnIndex(Column.DOC_FOLLOWUP_ID)));
//                patientInterviewDoctorFeedback.setInterviewName(cursor
//                        .getString(cursor.getColumnIndex("INTERVIEW_NAME")));
//                patientInterviewDoctorFeedback.setBenefName(cursor
//                        .getString(cursor.getColumnIndex(Column.BENEF_NAME)));
//                patientInterviewDoctorFeedback.setHouseHoldNumber(cursor
//                        .getString(cursor.getColumnIndex(Column.HH_NUMBER)));
//                patientInterviewDoctorFeedback.setSendStatus(cursor
//                        .getInt(cursor.getColumnIndex(Column.SEND_STATUS)));
//                patientInterviewDoctorFeedback.setBenefImagePath(cursor
//                        .getString(cursor.getColumnIndex(Column.BENEFICIARY_BENEF_IMAGE_PATH)));
//                patientInterviewDoctorFeedback.setInterviewTime(cursor
//                        .getString(cursor.getColumnIndex("INTERVIEW_TIME")));
//                patientInterviewDoctorFeedback.setQuestionAnsJson(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.QUESTION_ANSWER_JSON)));
//
//                patientInterviewDoctorFeedback.setQuestionAnsJson2(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.QUESTION_ANSWER_JSON2)));
//                patientInterviewDoctorFeedback.setNotificationStatus(cursor
//                        .getInt(cursor
//                                .getColumnIndex(Column.NOTIFICATION_STATUS)));
//
//                patientInterviewDoctorFeedback.setFeedbackDate(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.FEEDBACK_DATE)));
//
//                patientInterviewDoctorFeedback.setFeedbackReceiveTime(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.FEEDBACK_RECEIVE_TIME)));
//
//
//                patientInterviewDoctorFeedback.setUserId(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.USER_ID)));
//
//                patientInterviewDoctorFeedback.setRefCenterId(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.REF_CENTER_ID)));
//
//                patientInterviewDoctorFeedback.setNextFollowupDate(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.NEXT_FOLLOWUP_DATE)));
//
//                patientInterviewDoctorFeedback.setTransRef(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.TRANS_REF)));
//
//                patientInterviewDoctorFeedback.setDoctorFindings(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.DOCTOR_FINDINGS)));
//
//                patientInterviewDoctorFeedback.setPrescribedMedicine(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.PRESCRIBED_MEDICINE)));
//
//                patientInterviewDoctorFeedback.setqName(cursor
//                        .getString(cursor
//                                .getColumnIndex("Q_NAME")));
//                patientInterviewDoctorFeedback.setBenefCode(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.BENEF_CODE)));
//
//                patientInterviewDoctorFeedback.setBenefId(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.BENEF_ID)));
//
//                patientInterviewDoctorFeedback.setUpdateBy(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.UPDATE_BY)));
//
//                patientInterviewDoctorFeedback.setUpdateOn(cursor.getLong(cursor.getColumnIndex(Column.UPDATE_ON)));
//
//                String dob = "";
//                try {
//                    dob = cursor.getString(cursor.getColumnIndex("DOB"));
//                    if (!dob.equalsIgnoreCase("")) {
//                        patientInterviewDoctorFeedback.setDob(Utility.getAge(dob));
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                patientInterviewDoctorFeedback.setGender(cursor.getString(cursor
//                        .getColumnIndex("GENDER")));
//
//                patientInterviewDoctorFeedback.setBenefCodeShort(cursor.getString(cursor
//                        .getColumnIndex("BENEF_CODE_SHORT")));
//
//
//                doctorFeedbackList.getDoctorFeedBack().add(
//                        patientInterviewDoctorFeedback);
//
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return patientInterviewDoctorFeedbackLists;
//    }
//    public ArrayList<PatientInterviewDoctorFeedbackList> getPatientInterviewDoctorFeedbackList(
//            long sendStatus, String langCode) {
//        ArrayList<PatientInterviewDoctorFeedbackList> patientInterviewDoctorFeedbackLists = null;
//        String sql = " SELECT date( pidf.FEEDBACK_DATE / 1000, 'unixepoch', 'localtime' )  INTERVIEW_DATE, "
//                + " pidf.DOC_FOLLOWUP_ID DOC_FOLLOWUP_ID, "
//                + " pidf.INTERVIEW_ID INTERVIEW_ID, "
//                + " pidf.IS_FEEDBACK_ON_TIME IS_FEEDBACK_ON_TIME, "
//                + " pidf.FEEDBACK_SOURCE FEEDBACK_SOURCE, "
//                + " pidf.FEEDBACK_DATE FEEDBACK_DATE, "
//                + " pidf.INVES_ADVICE INVES_ADVICE, "
//                + " pidf.INVES_RESULT INVES_RESULT, "
//                + " pidf.INVES_STATUS INVES_STATUS, "
//                + " pidf.NOTIFICATION_STATUS NOTIFICATION_STATUS, "
//                + " pidf.FEEDBACK_RECEIVE_TIME FEEDBACK_RECEIVE_TIME, "
//                + " q.QUESTIONNAIRE_TITLE INTERVIEW_NAME, "
//                + " pidf.QUESTION_ANSWER_JSON QUESTION_ANSWER_JSON,"
//                + " pidf.QUESTION_ANSWER_JSON2 QUESTION_ANSWER_JSON2,"
//                + " pidf.USER_ID USER_ID,"
//                + " pidf.DOCTOR_FINDINGS DOCTOR_FINDINGS,"
//                + " pidf.REF_CENTER_ID REF_CENTER_ID,"
//                + " pidf.PRESCRIBED_MEDICINE PRESCRIBED_MEDICINE,"
//                + " pidf.NEXT_FOLLOWUP_DATE NEXT_FOLLOWUP_DATE,"
//                + " pidf.MESSAGE_TO_FCM MESSAGE_TO_FCM,"
//                + " pidf.TRANS_REF TRANS_REF,"
//                + " pidf.UPDATE_BY UPDATE_BY,"
//                + " pidf.UPDATE_ON UPDATE_ON,"
//                + " b.BENEF_NAME BENEF_NAME, "
//                + " b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH,"
//                + " b.BENEF_CODE BENEF_CODE, "
//                + " b.BENEF_ID BENEF_ID, "
//                + " q.NAME Q_NAME, "
//                + " pidf.SEND_STATUS SEND_STATUS,"
//                + " substr( b.benef_code, -5, 3 )  HH_NUMBER,"
//                + " CASE "
//                + "      WHEN CAST ( strftime( '%H', pidf.TRANS_REF / 1000, 'unixepoch', 'localtime' )  AS INTEGER ) < 12 THEN strftime( '%H:%M', pidf.TRANS_REF / 1000, 'unixepoch', 'localtime' ) || ' AM' "
//                + "     ELSE  strftime('%H:%M',time(strftime('%H:%M', pidf.TRANS_REF/1000, 'unixepoch', 'localtime'),'-12 hours'))  || ' PM' "
//                + " END AS INTERVIEW_TIME"
//                + " FROM patient_interview_doctor_feedback pidf"
//                + " LEFT JOIN patient_interview_master pim"
//                + "        ON pim.INTERVIEW_ID = pidf.INTERVIEW_ID "
//                + " LEFT JOIN questionnaire q"
//                + "       ON pim.QUESTIONNAIRE_ID = q.QUESTIONNAIRE_ID AND q.LANG_CODE='"
//                + langCode
//                + "'"
//                + " LEFT JOIN beneficiary b"
//                + "       ON pim.BENEF_CODE = b.BENEF_CODE "
//                + " WHERE pidf.SEND_STATUS=" + sendStatus
//                + " AND CASE WHEN pidf.QUESTION_ANSWER_JSON ISNULL AND pidf.SEND_STATUS=0 THEN 0 ELSE 1 END "
//                + " GROUP BY pidf.DOC_FOLLOWUP_ID "
//                + " ORDER BY FEEDBACK_DATE DESC ";
//
//        Log.e("SQL", sql.toString());
//        Cursor cursor = db.rawQuery(sql, null);
//        if (cursor.moveToFirst()) {
//            patientInterviewDoctorFeedbackLists = new ArrayList<PatientInterviewDoctorFeedbackList>();
//            String pdifDate = "";
//            PatientInterviewDoctorFeedbackList doctorFeedbackList = new PatientInterviewDoctorFeedbackList();
//            doctorFeedbackList
//                    .setDoctorFeedBack(new ArrayList<PatientInterviewDoctorFeedback>());
//            do {
//                if (!pdifDate
//                        .trim()
//                        .equals(cursor.getString(
//                                cursor.getColumnIndex("INTERVIEW_DATE")).trim())) {
//                    pdifDate = cursor.getString(cursor
//                            .getColumnIndex("INTERVIEW_DATE"));
//                    doctorFeedbackList = new PatientInterviewDoctorFeedbackList();
//                    doctorFeedbackList
//                            .setDoctorFeedBack(new ArrayList<PatientInterviewDoctorFeedback>());
//                    doctorFeedbackList.setInterviewDate(pdifDate);
//                    patientInterviewDoctorFeedbackLists.add(doctorFeedbackList);
//                }
//                PatientInterviewDoctorFeedback patientInterviewDoctorFeedback = new PatientInterviewDoctorFeedback();
//                patientInterviewDoctorFeedback.setInterviewId(cursor
//                        .getLong(cursor.getColumnIndex(Column.INTERVIEW_ID)));
//                patientInterviewDoctorFeedback.setIsFeedbackOnTime(cursor
//                        .getLong(cursor.getColumnIndex(Column.IS_FEEDBACK_ON_TIME)));
//                patientInterviewDoctorFeedback.setFeedbackSource(cursor
//                        .getString(cursor.getColumnIndex(Column.FEEDBACK_SOURCE)));
//                patientInterviewDoctorFeedback.setInvesAdvice(cursor
//                        .getString(cursor.getColumnIndex(Column.INVES_ADVICE)));
//                patientInterviewDoctorFeedback.setInvesResult(cursor
//                        .getString(cursor.getColumnIndex(Column.INVES_RESULT)));
//                patientInterviewDoctorFeedback.setInvesStatus(cursor
//                        .getString(cursor.getColumnIndex(Column.INVES_STATUS)));
//                patientInterviewDoctorFeedback.setDocFollowupId(cursor
//                        .getLong(cursor.getColumnIndex(Column.DOC_FOLLOWUP_ID)));
//                patientInterviewDoctorFeedback.setInterviewName(cursor
//                        .getString(cursor.getColumnIndex("INTERVIEW_NAME")));
//                patientInterviewDoctorFeedback.setBenefName(cursor
//                        .getString(cursor.getColumnIndex(Column.BENEF_NAME)));
//                patientInterviewDoctorFeedback.setHouseHoldNumber(cursor
//                        .getString(cursor.getColumnIndex(Column.HH_NUMBER)));
//                patientInterviewDoctorFeedback.setSendStatus(cursor
//                        .getInt(cursor.getColumnIndex(Column.SEND_STATUS)));
//                patientInterviewDoctorFeedback.setBenefImagePath(cursor
//                        .getString(cursor.getColumnIndex(Column.BENEFICIARY_BENEF_IMAGE_PATH)));
//                patientInterviewDoctorFeedback.setInterviewTime(cursor
//                        .getString(cursor.getColumnIndex("INTERVIEW_TIME")));
//                patientInterviewDoctorFeedback.setQuestionAnsJson(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.QUESTION_ANSWER_JSON)));
//
//                patientInterviewDoctorFeedback.setQuestionAnsJson2(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.QUESTION_ANSWER_JSON2)));
//                patientInterviewDoctorFeedback.setNotificationStatus(cursor
//                        .getInt(cursor
//                                .getColumnIndex(Column.NOTIFICATION_STATUS)));
//
//                patientInterviewDoctorFeedback.setFeedbackDate(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.FEEDBACK_DATE)));
//
//                patientInterviewDoctorFeedback.setFeedbackReceiveTime(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.FEEDBACK_RECEIVE_TIME)));
//
//
//                patientInterviewDoctorFeedback.setUserId(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.USER_ID)));
//
//                patientInterviewDoctorFeedback.setRefCenterId(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.REF_CENTER_ID)));
//
//                patientInterviewDoctorFeedback.setNextFollowupDate(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.NEXT_FOLLOWUP_DATE)));
//
//                patientInterviewDoctorFeedback.setTransRef(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.TRANS_REF)));
//
//                patientInterviewDoctorFeedback.setDoctorFindings(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.DOCTOR_FINDINGS)));
//
//                patientInterviewDoctorFeedback.setPrescribedMedicine(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.PRESCRIBED_MEDICINE)));
//
//                patientInterviewDoctorFeedback.setqName(cursor
//                        .getString(cursor
//                                .getColumnIndex("Q_NAME")));
//                patientInterviewDoctorFeedback.setBenefCode(cursor
//                        .getString(cursor
//                                .getColumnIndex(Column.BENEF_CODE)));
//
//                patientInterviewDoctorFeedback.setBenefId(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.BENEF_ID)));
//
//                patientInterviewDoctorFeedback.setUpdateBy(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.UPDATE_BY)));
//
//                patientInterviewDoctorFeedback.setUpdateOn(cursor
//                        .getLong(cursor
//                                .getColumnIndex(Column.UPDATE_ON)));
//
//
//                doctorFeedbackList.getDoctorFeedBack().add(
//                        patientInterviewDoctorFeedback);
//
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//        return patientInterviewDoctorFeedbackLists;
//    }
    public ArrayList<PatientInterviewDoctorFeedbackList> getPatientInterviewDoctorFeedbackListForUpdate(long sendStatus, String langCode, String searchText) {
        ArrayList<PatientInterviewDoctorFeedbackList> patientInterviewDoctorFeedbackLists = null;
        String sql = " SELECT date( pidf.FEEDBACK_DATE / 1000, 'unixepoch', 'localtime' )  INTERVIEW_DATE, " + " pidf.DOC_FOLLOWUP_ID DOC_FOLLOWUP_ID, " + " pidf.INTERVIEW_ID INTERVIEW_ID, " + " pidf.IS_FEEDBACK_ON_TIME IS_FEEDBACK_ON_TIME, " + " pidf.FEEDBACK_SOURCE FEEDBACK_SOURCE, " + " pidf.FEEDBACK_DATE FEEDBACK_DATE, " + " pidf.INVES_ADVICE INVES_ADVICE, " + " pidf.INVES_RESULT INVES_RESULT, " + " pidf.INVES_STATUS INVES_STATUS, " + " pidf.NOTIFICATION_STATUS NOTIFICATION_STATUS, " + " pidf.UPDATE_BY UPDATE_BY, " + " pidf.UPDATE_ON UPDATE_ON, " + " pidf.FEEDBACK_RECEIVE_TIME FEEDBACK_RECEIVE_TIME, " + " q.QUESTIONNAIRE_TITLE INTERVIEW_NAME, " + " pidf.QUESTION_ANSWER_JSON QUESTION_ANSWER_JSON," + " pidf.QUESTION_ANSWER_JSON2 QUESTION_ANSWER_JSON2," + " pidf.USER_ID USER_ID," + " pidf.DOCTOR_FINDINGS DOCTOR_FINDINGS," + " IFNULL(pidf.REF_CENTER_ID,0) REF_CENTER_ID," + " pidf.PRESCRIBED_MEDICINE PRESCRIBED_MEDICINE," + " pidf.NEXT_FOLLOWUP_DATE NEXT_FOLLOWUP_DATE," + " pidf.MESSAGE_TO_FCM MESSAGE_TO_FCM," + " pidf.TRANS_REF TRANS_REF," + " b.BENEF_NAME BENEF_NAME, " + " b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH," + " b.BENEF_CODE BENEF_CODE, " + " b.BENEF_ID BENEF_ID, " + " pim.FCM_INTERVIEW_ID FCM_INTERVIEW_ID, " + " q.NAME Q_NAME, " + " pidf.SEND_STATUS SEND_STATUS," + " substr( b.benef_code, -5, 3 )  HH_NUMBER," + " CASE " + "      WHEN CAST ( strftime( '%H', pidf.TRANS_REF / 1000, 'unixepoch', 'localtime' )  AS INTEGER ) < 12 THEN strftime( '%H:%M', pidf.TRANS_REF / 1000, 'unixepoch', 'localtime' ) || ' AM' " + "     ELSE  strftime('%H:%M',time(strftime('%H:%M', pidf.TRANS_REF/1000, 'unixepoch', 'localtime'),'-12 hours'))  || ' PM' " + " END AS INTERVIEW_TIME" + " FROM patient_interview_doctor_feedback pidf" + " LEFT JOIN patient_interview_master pim" + "        ON pim.INTERVIEW_ID = pidf.INTERVIEW_ID " + " LEFT JOIN questionnaire q" + "       ON pim.QUESTIONNAIRE_ID = q.QUESTIONNAIRE_ID AND q.LANG_CODE='" + langCode + "'" + " LEFT JOIN beneficiary b" + "       ON pim.BENEF_CODE = b.BENEF_CODE " + " WHERE " + " q.NAME NOT IN ( 'DIABETES_SCREENING','Visit date collection','DIABETES_DOCTOR_FFEDBACK', 'HYPERTENSION_SCREENING','HYPERTENSION_DOCTOR_FFEDBACK' )  "
                //+ " AND pidf.SEND_STATUS="+ sendStatus
                + " AND CASE WHEN pidf.QUESTION_ANSWER_JSON ISNULL AND pidf.SEND_STATUS=0 THEN 0 ELSE 1 END ";
        if (searchText != null && !searchText.isEmpty()) {
            sql += " AND substr( b.benef_code, -5, 3 )='" + searchText + "' ";
        }
        sql += " GROUP BY pidf.DOC_FOLLOWUP_ID ORDER BY FEEDBACK_DATE DESC ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            patientInterviewDoctorFeedbackLists = new ArrayList<PatientInterviewDoctorFeedbackList>();
            String pdifDate = "";
            PatientInterviewDoctorFeedbackList doctorFeedbackList = new PatientInterviewDoctorFeedbackList();
            doctorFeedbackList.setDoctorFeedBack(new ArrayList<PatientInterviewDoctorFeedback>());
            do {
                if (!pdifDate.trim().equals(cursor.getString(cursor.getColumnIndex("INTERVIEW_DATE")).trim())) {
                    pdifDate = cursor.getString(cursor.getColumnIndex("INTERVIEW_DATE"));
                    doctorFeedbackList = new PatientInterviewDoctorFeedbackList();
                    doctorFeedbackList.setDoctorFeedBack(new ArrayList<PatientInterviewDoctorFeedback>());
                    doctorFeedbackList.setInterviewDate(pdifDate);
                    patientInterviewDoctorFeedbackLists.add(doctorFeedbackList);
                }
                PatientInterviewDoctorFeedback patientInterviewDoctorFeedback = new PatientInterviewDoctorFeedback();
                patientInterviewDoctorFeedback.setInterviewId(cursor.getLong(cursor.getColumnIndex(Column.INTERVIEW_ID)));
                patientInterviewDoctorFeedback.setIsFeedbackOnTime(cursor.getLong(cursor.getColumnIndex(Column.IS_FEEDBACK_ON_TIME)));
                patientInterviewDoctorFeedback.setFeedbackSource(cursor.getString(cursor.getColumnIndex(Column.FEEDBACK_SOURCE)));
                patientInterviewDoctorFeedback.setInvesAdvice(cursor.getString(cursor.getColumnIndex(Column.INVES_ADVICE)));
                patientInterviewDoctorFeedback.setInvesResult(cursor.getString(cursor.getColumnIndex(Column.INVES_RESULT)));
                patientInterviewDoctorFeedback.setInvesStatus(cursor.getString(cursor.getColumnIndex(Column.INVES_STATUS)));
                patientInterviewDoctorFeedback.setDocFollowupId(cursor.getLong(cursor.getColumnIndex(Column.DOC_FOLLOWUP_ID)));
                patientInterviewDoctorFeedback.setInterviewName(cursor.getString(cursor.getColumnIndex("INTERVIEW_NAME")));
                patientInterviewDoctorFeedback.setBenefName(cursor.getString(cursor.getColumnIndex(Column.BENEF_NAME)));
                patientInterviewDoctorFeedback.setHouseHoldNumber(cursor.getString(cursor.getColumnIndex(Column.HH_NUMBER)));
                patientInterviewDoctorFeedback.setSendStatus(cursor.getInt(cursor.getColumnIndex(Column.SEND_STATUS)));
                patientInterviewDoctorFeedback.setBenefImagePath(cursor.getString(cursor.getColumnIndex(Column.BENEFICIARY_BENEF_IMAGE_PATH)));
                patientInterviewDoctorFeedback.setInterviewTime(cursor.getString(cursor.getColumnIndex("INTERVIEW_TIME")));
                patientInterviewDoctorFeedback.setQuestionAnsJson(cursor.getString(cursor.getColumnIndex(Column.QUESTION_ANSWER_JSON)));

                patientInterviewDoctorFeedback.setQuestionAnsJson2(cursor.getString(cursor.getColumnIndex(Column.QUESTION_ANSWER_JSON2)));
                patientInterviewDoctorFeedback.setNotificationStatus(cursor.getInt(cursor.getColumnIndex(Column.NOTIFICATION_STATUS)));

                patientInterviewDoctorFeedback.setFeedbackDate(cursor.getLong(cursor.getColumnIndex(Column.FEEDBACK_DATE)));

                patientInterviewDoctorFeedback.setFeedbackReceiveTime(cursor.getLong(cursor.getColumnIndex(Column.FEEDBACK_RECEIVE_TIME)));


                patientInterviewDoctorFeedback.setUserId(cursor.getLong(cursor.getColumnIndex(Column.USER_ID)));

                patientInterviewDoctorFeedback.setRefCenterId(cursor.getLong(cursor.getColumnIndex(Column.REF_CENTER_ID)));

                patientInterviewDoctorFeedback.setNextFollowupDate(cursor.getLong(cursor.getColumnIndex(Column.NEXT_FOLLOWUP_DATE)));

                patientInterviewDoctorFeedback.setTransRef(cursor.getLong(cursor.getColumnIndex(Column.TRANS_REF)));

                patientInterviewDoctorFeedback.setDoctorFindings(cursor.getString(cursor.getColumnIndex(Column.DOCTOR_FINDINGS)));

                patientInterviewDoctorFeedback.setPrescribedMedicine(cursor.getString(cursor.getColumnIndex(Column.PRESCRIBED_MEDICINE)));

                patientInterviewDoctorFeedback.setqName(cursor.getString(cursor.getColumnIndex("Q_NAME")));
                patientInterviewDoctorFeedback.setBenefCode(cursor.getString(cursor.getColumnIndex(Column.BENEF_CODE)));

                patientInterviewDoctorFeedback.setBenefId(cursor.getLong(cursor.getColumnIndex(Column.BENEF_ID)));

                patientInterviewDoctorFeedback.setFcmInterviewId(cursor.getLong(cursor.getColumnIndex(Column.FCM_INTERVIEW_ID)));

                patientInterviewDoctorFeedback.setUpdateOn(cursor.getLong(cursor.getColumnIndex(Column.UPDATE_ON)));

                patientInterviewDoctorFeedback.setUpdateBy(cursor.getLong(cursor.getColumnIndex(Column.UPDATE_BY)));

                doctorFeedbackList.getDoctorFeedBack().add(patientInterviewDoctorFeedback);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return patientInterviewDoctorFeedbackLists;
    }

    // mohammed jubayer
    public AppVersionHistory getVersionHistory(long versionNumber, String langCode) {

        String sql = "  SELECT VERSION_ID, " + "       VERSION_NAME, " + "       VERSION_NUMBER, " + "       UPDATE_NOTIFICATION, " + "       UPDATE_DESC, " + "       VERSION_DESC, " + "       APP_NAME, " + "       INSTALL_DATE , " + "       OPEN_DATE , " + "       APP_PATH_LOCAL , " + "       SEND_FLAG , " + "        APP_PATH , " + "       INSTALL_FLAG " + "  FROM app_version_history " + "  WHERE  " + "  VERSION_NUMBER >= " + versionNumber + "  AND " + "  LANG_CODE = '" + langCode + "' ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        AppVersionHistory appVersionHistory = null;
        if (cursor.moveToFirst()) {
            appVersionHistory = new AppVersionHistory();
            appVersionHistory.setVersionId(cursor.getLong(cursor.getColumnIndex("VERSION_ID")));
            appVersionHistory.setVersionName(cursor.getString(cursor.getColumnIndex("VERSION_NAME")));
            appVersionHistory.setVersionNumber(cursor.getLong(cursor.getColumnIndex("VERSION_NUMBER")));
            appVersionHistory.setVersionDesc(cursor.getString(cursor.getColumnIndex("VERSION_DESC")));
            appVersionHistory.setAppName(cursor.getString(cursor.getColumnIndex("APP_NAME")));
            appVersionHistory.setAppPath(cursor.getString(cursor.getColumnIndex("APP_PATH")));
            appVersionHistory.setInstallFlag(cursor.getString(cursor.getColumnIndex("INSTALL_FLAG")));
            appVersionHistory.setInstallDate(cursor.getLong(cursor.getColumnIndex("INSTALL_DATE")));
            appVersionHistory.setOpenDate(cursor.getLong(cursor.getColumnIndex("OPEN_DATE")));
            appVersionHistory.setAppPathLocal(cursor.getString(cursor.getColumnIndex("APP_PATH_LOCAL")));
            appVersionHistory.setSendFlag(cursor.getLong(cursor.getColumnIndex("SEND_FLAG")));
            appVersionHistory.setUpdateNotification(cursor.getString(cursor.getColumnIndex("UPDATE_NOTIFICATION")));
            appVersionHistory.setUpdateDesc(cursor.getString(cursor.getColumnIndex("UPDATE_DESC")));
            Log.e("SQL", sql.toString());
        }
        cursor.close();
        return appVersionHistory;
    }

    // mohammed jubayer
    public void saveAppVersionHistoryOnReceived(AppVersionHistory versionHistory) {
        db.delete(AppVersionHistory.MODEL_NAME, null, null);
        ContentValues cv = new ContentValues();
        cv.put(AppVersionHistory.VERSION_ID, versionHistory.getVersionId());
        cv.put(AppVersionHistory.VERSION_NAME, versionHistory.getVersionName());
        cv.put(AppVersionHistory.VERSION_DESC, versionHistory.getVersionDesc());
        cv.put(AppVersionHistory.VERSION_NUMBER, versionHistory.getVersionNumber());
        cv.put(AppVersionHistory.APP_NAME, versionHistory.getAppName());
        cv.put(AppVersionHistory.LANG_CODE, versionHistory.getLangCode());
        cv.put(AppVersionHistory.APP_PATH, versionHistory.getAppPath());
        cv.put(AppVersionHistory.RELEASE_DATE, versionHistory.getReleaseDate());
        cv.put(AppVersionHistory.UPDATE_DESC, versionHistory.getUpdateDesc());
        cv.put(AppVersionHistory.UPDATE_NOTIFICATION, versionHistory.getUpdateNotification());
        cv.put(AppVersionHistory.INSTALL_FLAG, AppVersionHistory.FLAG_RECEIVED);
        long versionId = db.insert(AppVersionHistory.MODEL_NAME, null, cv);
        Log.e("CV", cv.toString());
    }

    public boolean isLmpExist(String modelName, String benefCode, long lmp) {
        boolean isComplete = false;
        String sql = "SELECT LMP FROM " + modelName + " WHERE BENEF_CODE='" + benefCode + "' AND LMP=" + lmp;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        isComplete = cursor.getCount() > 0;
        cursor.close();
        return isComplete;
    }

    public void clearBabyInfoIfexist(long maternalId) {
        String sql = " SELECT CHILD_BENEF_CODE FROM  maternal_baby_info WHERE MATERNAL_ID =" + maternalId + " and CHILD_BENEF_CODE is not null";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        String benefIds = "'0'";

        if (cursor.moveToFirst()) {
            do {
                benefIds = benefIds + ",'" + cursor.getString(cursor.getColumnIndex("CHILD_BENEF_CODE")) + "'";
            } while (cursor.moveToNext());
        }
        cursor.close();

        sql = " UPDATE beneficiary SET STATE=0 WHERE BENEF_CODE IN (" + benefIds + ")";
        db.execSQL(sql);
        sql = " DELETE FROM `maternal_baby_info` WHERE MATERNAL_ID=" + maternalId;
        db.execSQL(sql);
    }

    public long getMaxId(String tableName, String columnName) {
        long id = 0;
        String sql = "SELECT MAX(" + columnName + ") ID FROM " + tableName;
        Log.e("SQL", sql.toString());

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndex("ID"));
        }
        cursor.close();
        return id;
    }

    public boolean isMaternalAbortionComplete(MaternalAbortion abortion) {
        String sql = " select BENEF_CODE from maternal_abortion where ABORT_DATE = ? and BENEF_CODE =? ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(abortion.getAbortDate()), abortion.getBenefCode()});
        return cursor.getCount() >= 1;
    }

    public boolean isMaternalDeleveryComplete(long maternalId) {
        String sql = " select MATERNAL_ID from maternal_delivery where MATERNAL_ID =" + maternalId;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        return cursor.getCount() >= 1;
    }

    public boolean isMaternalHighriskComplete(long maternalId) {
        String sql = " select NO_OF_RISK_ITEM from maternal_info where MATERNAL_ID=" + maternalId;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            if (cursor.getLong(cursor.getColumnIndex("NO_OF_RISK_ITEM")) > 0) return true;
        }
        return false;
    }


    // Mohammed jubayer
    public int updateAppVersionHistory(AppVersionHistory versionHistory) {
        ContentValues cv = new ContentValues();
        cv.put(AppVersionHistory.INSTALL_FLAG, versionHistory.getInstallFlag());
        if (versionHistory.getInstallFlag().equals(AppVersionHistory.FLAG_INSTALLED)) {
            cv.put(AppVersionHistory.INSTALL_DATE, versionHistory.getInstallDate() + "");
            cv.put(AppVersionHistory.SEND_FLAG, 0);
        } else if (versionHistory.getInstallFlag().equals(AppVersionHistory.FLAG_OPENED)) {
            cv.put(AppVersionHistory.OPEN_DATE, versionHistory.getOpenDate() + "");
            cv.put(AppVersionHistory.APP_PATH_LOCAL, versionHistory.getAppPathLocal() + "");
            cv.put(AppVersionHistory.SEND_FLAG, 0);
        }
        return db.update(AppVersionHistory.MODEL_NAME, cv, "VERSION_ID=?", new String[]{Long.toString(versionHistory.getVersionId())});
    }

    public int updateAppVersionHistorySendFlag(long versionId) {
        ContentValues cv = new ContentValues();
        cv.put(AppVersionHistory.SEND_FLAG, 1);
        return db.update(AppVersionHistory.MODEL_NAME, cv, "VERSION_ID=?", new String[]{Long.toString(versionId)});
    }

    /**
     * Close db.
     */
    public void closeDB() {
        if (db.isOpen()) {

            db.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long getChildDob(String benefCode) {
        String sql = "select md.DELIVERY_DATE DELIVERY_DATE,md.DELIVERY_TIME DELIVERY_TIME from maternal_baby_info mbi " + " LEFT JOIN maternal_delivery md ON md.MATERNAL_ID =mbi.MATERNAL_ID  " + " where mbi.CHILD_BENEF_CODE='" + benefCode + "'";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String time = "";
            long dateMili = cursor.getLong(cursor.getColumnIndex("DELIVERY_DATE"));
            time = cursor.getString(cursor.getColumnIndex("DELIVERY_TIME"));
            String[] hhMMArray = time.trim().split(":");
            if (dateMili > 0 && hhMMArray.length >= 2) {
                int hh = Integer.parseInt(hhMMArray[0]);
                int mm = Integer.parseInt(hhMMArray[1]);

                Date date = new Date(dateMili);
                date.setHours(hh);
                date.setMinutes(mm);

                return date.getTime();
            }
        }

        return -1;
    }

    public ArrayList<String> getAnswers(Long interviewId, String questionName) {
        ArrayList<String> ansList = new ArrayList<String>();
        if (interviewId == null || questionName == null) return ansList;

        String ans = "";
        String sql = "select pid.ANSWER ANSWER from " + " patient_interview_master pim  " + " JOIN patient_interview_detail pid ON pid.INTERVIEW_ID=pim.INTERVIEW_ID  " + " JOIN questionnaire_detail qd on qd.Q_ID=pid.Q_ID  " + " WHERE pim.INTERVIEW_ID=" + interviewId + " and upper(qd.Q_NAME) =upper('" + questionName + "')";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            ans = cursor.getString(cursor.getColumnIndex("ANSWER"));
            String[] ansArr = ans.split("\\|");

            if (ansArr != null && ansArr.length > 0) {
                for (String part : ansArr) {
                    if (part.trim().length() > 0) {
                        ansList.add(part);
                    }

                }
            }
        }
        cursor.close();

        return ansList;
    }

    public long countChild(Long maternalId, String status) {
        long numberOfbaby = 0;
        if (maternalId == null || status == null) return 0;
        String sql = " select count(MATERNAL_BABY_ID) NO_OF_BABY FROM maternal_baby_info where MATERNAL_ID=" + maternalId + " and BABY_STATE='" + status + "'";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            numberOfbaby = cursor.getLong(cursor.getColumnIndex("NO_OF_BABY"));
        }
        cursor.close();

        return numberOfbaby;
    }

    public long getId(String tableName, String ID, String whereCondision) {
        String sql = "SELECT " + ID + " AS ID FROM " + tableName + " WHERE " + whereCondision;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(cursor.getColumnIndex("ID"));
        }
        return -1;
    }

    public boolean isExist(String tableName, String whereCondision) {
        String sql = " SELECT * FROM " + tableName + " WHERE " + whereCondision;
        Log.e("SQL", sql.toString());
        Log.d(" SQLdatacheck", sql.toString());

        if (db.isOpen()) {
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, null);
                if (cursor.moveToFirst()) {
                    return true;
                }
            } catch (Exception e) {
                Log.e("loadClassKids", e.getMessage());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

        }
        return false;
    }


    public String getId(String sql) {
        Log.e("SQL", sql.toString());
        try {
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public long getMax(String tableName, String culumnName) {
        String sql = " SELECT MAX(" + culumnName + ")  MAX FROM " + tableName;
        try {
            Cursor cursor = db.rawQuery(sql, null);
            Log.e("SQL", sql.toString());
            if (cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndex("MAX"));
            }

        } catch (Exception e) {

        }
        return 0;
    }


    public boolean isBeneficiaryStoredInServer(String benefCode) {
        String sql = " select p.INTERVIEW_ID from patient_interview_master p JOIN questionnaire q ON p.QUESTIONNAIRE_ID=q.QUESTIONNAIRE_ID " + " WHERE q.NAME='BENEFICIARY_REGISTRATION' AND p.BENEF_CODE='" + benefCode + "' AND p.DATA_SENT='N' ";
        Log.e("SQL", sql.toString());
        try {
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                return false;
            }
        } catch (Exception e) {

        }
        return true;
    }

    public Report getReportResult(Report report) {

        try {
            String sql = report.getSql();
            Log.e("SQL", sql.toString());
            Cursor cursor = db.rawQuery(sql, null);
            report.setData(toJsonArray(cursor));
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return report;
    }

    private JSONArray toJsonArray(Cursor cursor) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        if (cursor.moveToFirst()) {
            do {
                int totalColumn = cursor.getColumnCount();
                JSONObject jsonObject = new JSONObject();
                for (int columnIndex = 0; columnIndex < totalColumn; columnIndex++) {
                    switch (cursor.getType(1)) {
                        case Cursor.FIELD_TYPE_NULL:
                            jsonObject.put(cursor.getColumnName(columnIndex), cursor.getString(columnIndex));
                            break;
                        default:
                            jsonObject.put(cursor.getColumnName(columnIndex), cursor.getString(columnIndex));
                            break;
                    }
                }
                jsonArray.put(jsonObject);

            } while (cursor.moveToNext());
        }
        return jsonArray;
    }


    public void saveQuestionnaireBroadCategory(JSONArray rows, JSONObject param) {
        ContentValues cv = new ContentValues();
        long count = 0;
        try {
            if (db != null) {
                db.beginTransaction();

                String lang = App.getContext().getAppSettings().getLanguage();
                for (int index = 0; index < rows.length(); index++) {

                    try {
                        cv.clear();
                        JSONObject row = rows.getJSONObject(index);
                        if (row.has(Column.BROAD_CAT_ID))
                            cv.put(Column.BROAD_CAT_ID, row.get(Column.BROAD_CAT_ID) + "");
                        if (row.has(Column.BROAD_CAT_NAME))
                            cv.put(Column.BROAD_CAT_NAME, row.get(Column.BROAD_CAT_NAME) + "");
                        if (row.has(Column.STATE)) cv.put(Column.STATE, row.get(Column.STATE) + "");
                        if (row.has(Column.SORT_ORDER))
                            cv.put(Column.SORT_ORDER, row.get(Column.SORT_ORDER) + "");
                        cv.put(Column.LANG_CODE, lang);
                        db.replace(DBTable.QUESTIONNAIRE_BROAD_CATEGORY, null, cv);
                        cv.clear();
                        count++;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                updateDataVersion(DBTable.QUESTIONNAIRE_BROAD_CATEGORY, rows.length(), count, param, KEY.VERSION_NO_QUESTIONNAIRE_BROAD_CATEGORY);
                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }


    public void saveQuestionnaireServiceCategory(JSONArray rows, JSONObject param) {
        ContentValues cv = new ContentValues();
        long count = 0;
        try {
            if (db != null) {
                db.beginTransaction();

                String lang = App.getContext().getAppSettings().getLanguage();
                for (int index = 0; index < rows.length(); index++) {

                    try {
                        cv.clear();
                        JSONObject row = rows.getJSONObject(index);
                        if (row.has(Column.SERVICE_CATEGORY_ID))
                            cv.put(Column.SERVICE_CATEGORY_ID, row.get(Column.SERVICE_CATEGORY_ID) + "");
                        if (row.has(Column.SERVICE_CATEGORY_NAME))
                            cv.put(Column.SERVICE_CATEGORY_NAME, row.get(Column.SERVICE_CATEGORY_NAME) + "");
                        if (row.has(Column.BROAD_CAT_ID))
                            cv.put(Column.BROAD_CAT_ID, row.get(Column.BROAD_CAT_ID) + "");
                        if (row.has(Column.STATE)) cv.put(Column.STATE, row.get(Column.STATE) + "");
                        if (row.has(Column.SORT_ORDER))
                            cv.put(Column.SORT_ORDER, row.get(Column.SORT_ORDER) + "");
                        cv.put(Column.LANG_CODE, lang);
                        db.replace(DBTable.QUESTIONNAIRE_SERVICE_CATEGORY, null, cv);
                        cv.clear();
                        count++;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }


                updateDataVersion(DBTable.QUESTIONNAIRE_SERVICE_CATEGORY, rows.length(), count, param, KEY.VERSION_NO_QUESTIONNAIRE_SERVICE_CATEGORY);

                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }


    public void saveCourtyardMeeting(JSONArray rows) {
        for (int index = 0; index < rows.length(); index++) {
            try {
                ContentValues cv = new ContentValues();
                JSONObject row = rows.getJSONObject(index);
                if (row.has("MEETING_ID")) cv.put("MEETING_ID", row.get("MEETING_ID") + "");
                if (row.has("HH_NUMBER")) cv.put("HH_NUMBER", row.get("HH_NUMBER") + "");
                if (row.has("TRANS_REF")) cv.put("TRANS_REF", row.get("TRANS_REF") + "");
                if (row.has("TOPIC_ID")) cv.put("TOPIC_ID", row.get("TOPIC_ID") + "");
                if (row.has("MEETING_DATE")) cv.put("MEETING_DATE", row.get("MEETING_DATE") + "");
                if (row.has("TOTAL_ATTENDENT"))
                    cv.put("TOTAL_ATTENDENT", row.get("TOTAL_ATTENDENT") + "");
                if (row.has("TOTAL_MALE_ATTENDANT"))
                    cv.put("TOTAL_MALE_ATTENDANT", row.get("TOTAL_MALE_ATTENDANT") + "");
                if (row.has("TOTAL_FEMALE_ATTENDANT"))
                    cv.put("TOTAL_FEMALE_ATTENDANT", row.get("TOTAL_FEMALE_ATTENDANT") + "");
                if (row.has("MEETING_DURATION"))
                    cv.put("MEETING_DURATION", row.get("MEETING_DURATION") + "");
                if (row.has("INTERVIEW_ID")) cv.put("INTERVIEW_ID", row.get("INTERVIEW_ID") + "");
                db.replace("courtyard_meeting", null, cv);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    public void saveCourtyardMeetingTarget(JSONArray rows, JSONObject param) {
        ContentValues cv = new ContentValues();
        long count = 0;
        try {
            if (db != null) {
                db.beginTransaction();

                String lang = App.getContext().getAppSettings().getLanguage();
                for (int index = 0; index < rows.length(); index++) {

                    try {
                        cv.clear();
                        JSONObject row = rows.getJSONObject(index);
                        if (row.has("TARGET_ID")) cv.put("TARGET_ID", row.get("TARGET_ID") + "");
                        if (row.has("MONTH")) cv.put("MONTH", row.get("MONTH") + "");
                        if (row.has("YEAR")) cv.put("YEAR", row.get("YEAR") + "");
                        if (row.has("USER_ID")) cv.put("USER_ID", row.get("USER_ID") + "");
                        if (row.has("MEETING_TARGET"))
                            cv.put("MEETING_TARGET", row.get("MEETING_TARGET") + "");
                        if (row.has("CREATE_DATE"))
                            cv.put("CREATE_DATE", row.get("CREATE_DATE") + "");
                        db.replace(DBTable.COURTYARD_MEETING_TARGET, null, cv);
                        cv.clear();
                        count++;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }


                updateDataVersion(DBTable.COURTYARD_MEETING_TARGET, rows.length(), count, param, KEY.VERSION_NO_COURTYARD_MEETING_TARGET);
                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }


    public void saveReportAsst(JSONArray rows, JSONObject param) {
        ContentValues cv = new ContentValues();
        long count = 0;
        try {
            if (db != null) {
                db.beginTransaction();

                String lang = App.getContext().getAppSettings().getLanguage();
                for (int index = 0; index < rows.length(); index++) {

                    try {
                        cv.clear();
                        JSONObject row;
                        row = rows.getJSONObject(index);
                        if (row.has(Column.REP_ID))
                            cv.put(Column.REP_ID, row.get(Column.REP_ID) + "");
                        if (row.has(Column.REP_NAME))
                            cv.put(Column.REP_NAME, row.get(Column.REP_NAME) + "");
                        if (row.has(Column.REP_CAPTION))
                            cv.put(Column.REP_CAPTION, row.get(Column.REP_CAPTION) + "");
                        if (row.has(Column.REP_DESCRIPTION))
                            cv.put(Column.REP_DESCRIPTION, row.get(Column.REP_DESCRIPTION) + "");
                        if (row.has(Column.PARENT_ID))
                            cv.put(Column.PARENT_ID, row.get(Column.PARENT_ID) + "");
                        if (row.has(Column.SQL_STR))
                            cv.put(Column.SQL_STR, row.get(Column.SQL_STR) + "");
                        if (row.has(Column.MODULE_ID))
                            cv.put(Column.MODULE_ID, row.get(Column.MODULE_ID) + "");
                        if (row.has(Column.STATE)) cv.put(Column.STATE, row.get(Column.STATE) + "");
                        if (row.has(Column.SORT_ORDER))
                            cv.put(Column.SORT_ORDER, row.get(Column.SORT_ORDER) + "");
                        if (row.has(Column.REP_PARAMS))
                            cv.put(Column.REP_PARAMS, row.get(Column.REP_PARAMS) + "");
                        if (row.has(Column.ICON)) cv.put(Column.ICON, row.get(Column.ICON) + "");

                        cv.put(Column.LANG_CODE, lang);
                        db.replace(DBTable.REPORT_ASST, null, cv);
                        cv.clear();
                        count++;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

                updateDataVersion(DBTable.REPORT_ASST, rows.length(), count, param, KEY.VERSION_NO_REPORT_ASST);

                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }


    public void saveReferralCenterList(JSONArray rows, JSONObject param) {

        long count = 0;
        try {
            if (db != null) {
                db.beginTransaction();

                for (int index = 0; index < rows.length(); index++) {

                    try {
                        JSONObject row = rows.getJSONObject(index);
                        ContentValues cv = new ContentValues();
                        if (row.has((Column.REF_CENTER_ID)))
                            cv.put(Column.REF_CENTER_ID, row.getLong(Column.REF_CENTER_ID));
                        if (row.has((Column.REF_CENTER_NAME_CAPTION)))
                            cv.put(Column.REF_CENTER_NAME_CAPTION, row.getString(Column.REF_CENTER_NAME_CAPTION));
                        if (row.has((Column.DESCRIPTION)))
                            cv.put(Column.DESCRIPTION, row.getString(Column.DESCRIPTION));
                        if (row.has((Column.ADDRESS)))
                            cv.put(Column.ADDRESS, row.getString(Column.ADDRESS));
                        if (row.has((Column.CONTACT_NUMBER)))
                            cv.put(Column.CONTACT_NUMBER, row.getString(Column.CONTACT_NUMBER));
                        if (row.has((Column.CONTACT_NAME)))
                            cv.put(Column.CONTACT_NAME, row.getString(Column.CONTACT_NAME));
                        if (row.has((Column.STATE))) cv.put(Column.STATE, row.getInt(Column.STATE));
                        if (row.has((Column.CAPACITY_DAILY_CCS)))
                            cv.put(Column.CAPACITY_DAILY_CCS, row.getInt(Column.CAPACITY_DAILY_CCS));
                        cv.put(Column.LANG_CODE, App.getContext().getAppSettings().getLanguage());

                        if (row.has((Column.CONTACT_EMAIL)))
                            cv.put(Column.CONTACT_EMAIL, row.getString(Column.CONTACT_EMAIL));
                        if (row.has((Column.LATITUDE)))
                            cv.put(Column.LATITUDE, row.getDouble(Column.LATITUDE));
                        if (row.has((Column.LONGITUDE)))
                            cv.put(Column.LONGITUDE, row.getDouble(Column.LONGITUDE));
                        if (row.has((Column.LOCATION_ID)))
                            cv.put(Column.LOCATION_ID, row.getLong(Column.LOCATION_ID));
                        if (row.has((Column.REF_CENTER_CODE)))
                            cv.put(Column.REF_CENTER_CODE, row.getString(Column.REF_CENTER_CODE));
                        if (row.has((Column.REF_CENTER_NAME)))
                            cv.put(Column.REF_CENTER_NAME, row.getString(Column.REF_CENTER_NAME));
                        if (row.has((Column.REF_CENTER_TYPE)))
                            cv.put(Column.REF_CENTER_TYPE, row.getString(Column.REF_CENTER_TYPE));
                        if (row.has((Column.REF_CENTER_CAT_ID)))
                            cv.put(Column.REF_CENTER_CAT_ID, row.getLong(Column.REF_CENTER_CAT_ID));
                        db.replace(DBTable.REFERRAL_CENTER, null, cv);

                        if (row.has("RCCA_REF_CENTER_ID") && row.getLong("RCCA_REF_CENTER_ID") > 0) {
                            cv.clear();
                            cv.put(Column.REF_CENTER_ID, row.getLong("RCCA_REF_CENTER_ID"));
                            if (row.has((Column.DISTANCE)))
                                cv.put(Column.DISTANCE, row.getInt((Column.DISTANCE)));
                            if (row.has(Column.TIME_NEED))
                                cv.put(Column.TIME_NEED, row.getInt(Column.TIME_NEED));
                            if (row.has(Column.TRANSPORT_WAY))
                                cv.put(Column.TRANSPORT_WAY, row.getString(Column.TRANSPORT_WAY));
                            if (row.has("RCCA_STATE"))
                                cv.put(Column.STATE, row.getString("RCCA_STATE"));
                            db.replace("referral_center_char_assignment", null, cv);
                        }
                        count++;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }


                updateDataVersion(DBTable.REFERRAL_CENTER, rows.length(), count, param, KEY.VERSION_NO_REFERRAL_CENTER);

                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    public void saveReferralCenterCategory(JSONArray rows, JSONObject param) {
        ContentValues cv = new ContentValues();
        long count = 0;
        try {
            if (db != null) {
                db.beginTransaction();
                String lang = App.getContext().getAppSettings().getLanguage();
                for (int index = 0; index < rows.length(); index++) {
                    try {
                        cv.clear();
                        JSONObject row;
                        row = rows.getJSONObject(index);
                        if (row.has(Column.REF_CENTER_CAT_ID))
                            cv.put(Column.REF_CENTER_CAT_ID, row.get(Column.REF_CENTER_CAT_ID) + "");
                        if (row.has(Column.REF_CENTER_CAT_CODE))
                            cv.put(Column.REF_CENTER_CAT_CODE, row.get(Column.REF_CENTER_CAT_CODE) + "");
                        if (row.has(Column.REF_CENTER_CAT_NAME))
                            cv.put(Column.REF_CENTER_CAT_NAME, row.get(Column.REF_CENTER_CAT_NAME) + "");
                        if (row.has(Column.SORT_ORDER))
                            cv.put(Column.SORT_ORDER, row.get(Column.SORT_ORDER) + "");
                        cv.put(Column.LANG_CODE, lang);
                        db.replace(DBTable.REFERRAL_CENTER_CATEGORY, null, cv);
                        cv.clear();
                        count++;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                updateDataVersion(DBTable.REFERRAL_CENTER_CATEGORY, rows.length(), count, param, KEY.VERSION_NO_REFERRAL_CENTER_CATEGORY);
                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }


    public void saveDeathRegistration(JSONArray rows) {
        for (int index = 0; index < rows.length(); index++) {

            try {
                ContentValues cv = new ContentValues();
                JSONObject row = rows.getJSONObject(index);
                if (row.has(Column.DEATH_REG_ID))
                    cv.put(Column.DEATH_REG_ID, row.get(Column.DEATH_REG_ID) + "");
                if (row.has(Column.BENEF_ID))
                    cv.put(Column.BENEF_ID, row.get(Column.BENEF_ID) + "");
                if (row.has(Column.BENEF_CODE))
                    cv.put(Column.BENEF_CODE, row.get(Column.BENEF_CODE) + "");
                if (row.has(Column.DATE_OF_DEATH))
                    cv.put(Column.DATE_OF_DEATH, row.get(Column.DATE_OF_DEATH) + "");
                if (row.has(Column.AGE)) cv.put(Column.AGE, row.get(Column.AGE) + "");
                if (row.has(Column.AGE_IN_DAY))
                    cv.put(Column.AGE_IN_DAY, row.get(Column.AGE_IN_DAY) + "");
                if (row.has(Column.DEATH_REASON_ID))
                    cv.put(Column.DEATH_REASON_ID, row.get(Column.DEATH_REASON_ID) + "");
                if (row.has(Column.CREATE_DATE))
                    cv.put(Column.CREATE_DATE, row.get(Column.CREATE_DATE) + "");
                if (row.has(Column.REG_DATE))
                    cv.put(Column.REG_DATE, row.get(Column.REG_DATE) + "");
                if (row.has(Column.INTERVIEW_ID))
                    cv.put(Column.INTERVIEW_ID, row.get(Column.INTERVIEW_ID) + "");
                if (row.has(Column.TRANS_REF))
                    cv.put(Column.TRANS_REF, row.get(Column.TRANS_REF) + "");
                db.replace(DBTable.DEATH_REGISTRATION, null, cv);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    public Report getSingleReportAssts(long repId) {
        Report report = new Report();
        String sql = " select REP_ID,REP_NAME,REP_CAPTION,REP_DESCRIPTION," + " PARENT_ID,SQL_STR,REP_PARAMS,ICON, case when length(trim(SQL_STR))>0 THEN 1 ELSE 2 END TYPE " + " from report_asst  " + " where REP_ID = " + repId + " AND state=1 AND  LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'  ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {

            report.setRepId(cursor.getLong(cursor.getColumnIndex(Column.REP_ID)));
            report.setRepName(cursor.getString(cursor.getColumnIndex(Column.REP_NAME)));
            report.setRepCaption(cursor.getString(cursor.getColumnIndex(Column.REP_CAPTION)));
            report.setRepDescription(cursor.getString(cursor.getColumnIndex(Column.REP_DESCRIPTION)));
            report.setParentId(cursor.getLong(cursor.getColumnIndex(Column.PARENT_ID)));
            report.setSqlStr(cursor.getString(cursor.getColumnIndex(Column.SQL_STR)));
            report.setType(cursor.getInt(cursor.getColumnIndex(Column.TYPE)));
            try {
                report.setRepParams(new JSONArray(cursor.getString(cursor.getColumnIndex(Column.REP_PARAMS))));
            } catch (Exception e) {
            }
            report.setIcon(cursor.getString(cursor.getColumnIndex("ICON")));

        }
        return report;
    }

    public ArrayList<Report> getReportAssts(Long repId, Long parentId) {
        ArrayList<Report> reportList = new ArrayList<Report>();

        String sql = " select REP_ID,REP_NAME,REP_CAPTION,REP_DESCRIPTION," + " PARENT_ID,SQL_STR,REP_PARAMS,ICON, case when length(trim(SQL_STR))>0 THEN 1 ELSE 2 END TYPE " + " from report_asst  " + " where  state=1 AND  LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'  ";


        if (parentId != null) {
            sql = sql + " AND PARENT_ID=" + parentId + "  order by SORT_ORDER asc ";
        } else if (repId != null) {
            sql = sql + " AND REP_ID=" + repId;
        }

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Report report = new Report();
                report.setRepId(cursor.getLong(cursor.getColumnIndex(Column.REP_ID)));
                report.setRepName(cursor.getString(cursor.getColumnIndex(Column.REP_NAME)));
                report.setRepCaption(cursor.getString(cursor.getColumnIndex(Column.REP_CAPTION)));
                report.setRepDescription(cursor.getString(cursor.getColumnIndex(Column.REP_DESCRIPTION)));
                report.setParentId(cursor.getLong(cursor.getColumnIndex(Column.PARENT_ID)));
                report.setSqlStr(cursor.getString(cursor.getColumnIndex(Column.SQL_STR)));
                report.setType(cursor.getInt(cursor.getColumnIndex(Column.TYPE)));
                try {
                    report.setRepParams(new JSONArray(cursor.getString(cursor.getColumnIndex(Column.REP_PARAMS))));
                } catch (Exception e) {
                }
                report.setIcon(cursor.getString(cursor.getColumnIndex("ICON")));
                reportList.add(report);

            } while (cursor.moveToNext());
        }
        return reportList;
    }

    public void removeTransitional(long transRef) {
        ContentValues cv = new ContentValues();
        long count = 0;
        try {
            if (db != null) {
                db.beginTransaction();
                db.execSQL(" delete from ccs_treatment_followup where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from ccs_treatment where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from courtyard_meeting where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from death_registration where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from file_bank where  is_send=1 and  ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from immunization_followup where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from immunization_service where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from maternal_abortion where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from maternal_baby_info where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from maternal_delivery where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from maternal_service where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from maternal_info where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from user_schedule where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from patient_interview_doctor_feedback where send_status=1 and ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from patient_interview_detail where ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.execSQL(" delete from patient_interview_master where  data_sent='Y' and ifnull(trans_ref,0)>0 and  trans_ref<" + transRef);
                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }


    public void saveSuggestionText(JSONArray rows, JSONObject param) {
        ContentValues cv = new ContentValues();
        long count = 0;
        try {
            if (db != null) {
                db.beginTransaction();

                for (int index = 0; index < rows.length(); index++) {

                    try {
                        cv.clear();
                        JSONObject row;
                        row = rows.getJSONObject(index);
                        if (row.has(Column.ID)) cv.put(Column.ID, row.get(Column.ID) + "");
                        if (row.has(Column.ENG_NAME))
                            cv.put(Column.ENG_NAME, row.get(Column.ENG_NAME) + "");
                        if (row.has(Column.LOCAL_NAME))
                            cv.put(Column.LOCAL_NAME, row.get(Column.LOCAL_NAME) + "");
                        if (row.has(Column.CODE)) cv.put(Column.CODE, row.get(Column.CODE) + "");
                        if (row.has(Column.TYPE)) cv.put(Column.TYPE, row.get(Column.TYPE) + "");
                        if (row.has(Column.VERSION_NO))
                            cv.put(Column.VERSION_NO, row.get(Column.VERSION_NO) + "");
                        if (row.has(Column.STATE)) cv.put(Column.STATE, row.get(Column.STATE) + "");
                        db.replace(DBTable.SUGGESTION_TEXT, null, cv);
                        cv.clear();
                        count++;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }


                updateDataVersion(DBTable.SUGGESTION_TEXT, rows.length(), count, param, "NA");


                db.setTransactionSuccessful();
            }
        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }
    }

    public void saveNotification(ArrayList<NotificationItem> pims, JSONObject param) {
        long count = 0;
        for (NotificationItem notificationItem : pims) {
            ContentValues cv = new ContentValues();
            cv.put(Column.CONTENT, notificationItem.getContent());
            cv.put(Column.CREATE_DATE, notificationItem.getCreateDate());
            cv.put(Column.TITLE, notificationItem.getTitle());
            cv.put(Column.TYPE, notificationItem.getNotificationType());
            cv.put(Column.VIEW_TIME, notificationItem.getViewTime());
            cv.put(Column.NOTIFICATION_STATUS, notificationItem.getNotificationStatus());
            cv.put(Column.S_NOTIFICATION_ID, notificationItem.getNotificationId());
            cv.put(Column.ORG_ID, notificationItem.getOrgId());
            cv.put(Column.VERSION_NO, notificationItem.getVersionNo());
            cv.put(Column.NOTIFICATION_TIME, notificationItem.getNotificationTime());
            cv.put(Column.STATE, notificationItem.getState());
            cv.put(Column.UPDATE_SOURCE, notificationItem.getUpdateSource());
            cv.put(Column.DESTINATION_PATH, notificationItem.getDestinationPath());
            cv.put(Column.DESTINATION_KEY, notificationItem.getDestinationKey());
            cv.put(Column.BENEF_CODE, notificationItem.getBenefCode());
            cv.put(Column.NOTIFICATION_ICON, notificationItem.getNotificationIcon());
            if (notificationItem.getViewTime() != null) {
                cv.put(Column.SEND_STATUS, 1);
            }
            String whereCon = " S_NOTIFICATION_ID=" + notificationItem.getNotificationId();

            if (!isExist(DBTable.NOTIFICATION_MASTER, whereCon)) {
                db.insert(DBTable.NOTIFICATION_MASTER, null, cv);
            }
            cv.clear();
            count++;

            //db.insert("patient_interview_doctor_feedback", null, cv);
        }
        updateDataVersion(DBTable.NOTIFICATION_MASTER, pims.size(), count, param, KEY.VERSION_NO_NOTIFICATION);


    }


    public PatientInterviewMaster getInterviewById(long interviewId) {
        PatientInterviewMaster patientInterviewMaster = new PatientInterviewMaster();
        String sql = " select pim.INTERVIEW_ID,pim.BENEF_CODE,b.BENEF_NAME,pim.FCM_INTERVIEW_ID from patient_interview_master pim" + " LEFT JOIN beneficiary b ON pim.BENEF_CODE=b.BENEF_CODE" + " WHERE pim.FCM_INTERVIEW_ID=" + interviewId;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            patientInterviewMaster.setInterviewId(cursor.getLong(cursor.getColumnIndex(Column.INTERVIEW_ID)));
            patientInterviewMaster.setBenefCode(cursor.getString(cursor.getColumnIndex(Column.BENEF_CODE)));
            patientInterviewMaster.setBenefName(cursor.getString(cursor.getColumnIndex(Column.BENEF_NAME)));
            patientInterviewMaster.setFcmInterviewId(cursor.getLong(cursor.getColumnIndex(Column.FCM_INTERVIEW_ID)));

        }
        return patientInterviewMaster;
    }

    public JSONArray getAlgorithWisePatientList(LinkedList<String> invesStatusList, ArrayList<String> algoList, String searchText) {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append(" SELECT * from (select NAME as Q_NAME ,BENEF_IMAGE_PATH, HOUSEHOLD_NUMBER, BENEF_CODE, BENEF_CODE_FULL, BENEF_NAME, " + "IFNULL(case when instr(GROUP_CONCAT(INVES_STATUS),',') > 0 " + "then substr(GROUP_CONCAT(INVES_STATUS), 0, instr(GROUP_CONCAT(INVES_STATUS),',')) " + "else GROUP_CONCAT(INVES_STATUS) end, '" + Constants.STATUS_PENDING + "')  INVES_STATUS " + "from ( " + "select  q.NAME, b.BENEF_IMAGE_PATH,substr( pim.BENEF_CODE, -5, 3 ) HOUSEHOLD_NUMBER,b.BENEF_ID, " + " substr( pim.BENEF_CODE, -5, 5 ) BENEF_CODE, pim.BENEF_CODE BENEF_CODE_FULL, b.BENEF_NAME, " + " IFNULL(pidf.INVES_STATUS,'" + Constants.STATUS_PENDING + "') INVES_STATUS,pim.INTERVIEW_ID, pim.FCM_INTERVIEW_ID " + "  from patient_interview_master pim " +
//				"  LEFT JOIN patient_interview_doctor_feedback pidf on pim.INTERVIEW_ID=pidf.INTERVIEW_ID " +
                "  LEFT JOIN patient_interview_doctor_feedback pidf on  pim.INTERVIEW_ID=pidf.INTERVIEW_ID OR pim.FCM_INTERVIEW_ID=pidf.INTERVIEW_ID " + "  INNER JOIN beneficiary b ON pim.BENEF_CODE = b.BENEF_CODE " + "  INNER JOIN questionnaire q ON pim.QUESTIONNAIRE_ID = q.QUESTIONNAIRE_ID " + "  INNER JOIN user_info ui ON pim.USER_ID = ui.USER_ID " +
                //"  WHERE q.NAME IN ( 'DIABETES_SCREENING','Visit date collection','DIABETES_DOCTOR_FFEDBACK' )  " +
                "  WHERE q.NAME IN ( ");
        for (int i = 0; i < algoList.size(); i++) {
            if (i == (algoList.size() - 1)) {
                sql.append("'" + algoList.get(i) + "'");
            } else {
                sql.append("'" + algoList.get(i) + "' ,");
            }
        }

        sql.append(")  ");
        sql.append(" ORDER BY b.BENEF_CODE ASC, pim.INTERVIEW_ID DESC, pidf.DOC_FOLLOWUP_ID DESC) tmp ");
        sql.append(" group by tmp.BENEF_CODE ) toptemp ");

        if (invesStatusList != null && invesStatusList.size() > 0) {
            sql.append(" WHERE toptemp.INVES_STATUS IN ( ");
            for (int i = 0; i < invesStatusList.size(); i++) {
                if (i == (invesStatusList.size() - 1)) {
                    sql.append("'" + invesStatusList.get(i) + "'");
                } else {
                    sql.append("'" + invesStatusList.get(i) + "' ,");
                }
            }
        }
        sql.append(")  ");

        if (searchText != null && !searchText.equals("")) {
            sql.append(" AND toptemp.HOUSEHOLD_NUMBER='" + searchText + "' ");
        }


        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public JSONArray getAlgorithWisePatientListOldQuery(LinkedList<String> invesStatusList, ArrayList<String> algoList, String searchText) {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append(" SELECT * from (select NAME as Q_NAME ,BENEF_IMAGE_PATH, HOUSEHOLD_NUMBER, BENEF_CODE, BENEF_CODE_FULL, BENEF_NAME, " + "IFNULL(case when instr(GROUP_CONCAT(INVES_STATUS),',') > 0 " + "then substr(GROUP_CONCAT(INVES_STATUS), 0, instr(GROUP_CONCAT(INVES_STATUS),',')) " + "else GROUP_CONCAT(INVES_STATUS) end, '')  INVES_STATUS " + "from ( " + "select  q.NAME, b.BENEF_IMAGE_PATH,substr( pim.BENEF_CODE, -5, 3 ) HOUSEHOLD_NUMBER,b.BENEF_ID, " + " substr( pim.BENEF_CODE, -5, 5 ) BENEF_CODE, pim.BENEF_CODE BENEF_CODE_FULL, b.BENEF_NAME, " + " NULLIF(pidf.INVES_STATUS,'') INVES_STATUS,pim.INTERVIEW_ID, pim.FCM_INTERVIEW_ID " + "  from patient_interview_master pim " +
//				"  LEFT JOIN patient_interview_doctor_feedback pidf on pim.INTERVIEW_ID=pidf.INTERVIEW_ID " +
                "  LEFT JOIN patient_interview_doctor_feedback pidf on  pim.INTERVIEW_ID=pidf.INTERVIEW_ID OR pim.FCM_INTERVIEW_ID=pidf.INTERVIEW_ID " + "  INNER JOIN beneficiary b ON pim.BENEF_CODE = b.BENEF_CODE " + "  INNER JOIN questionnaire q ON pim.QUESTIONNAIRE_ID = q.QUESTIONNAIRE_ID " + "  INNER JOIN user_info ui ON pim.USER_ID = ui.USER_ID " +
                //"  WHERE q.NAME IN ( 'DIABETES_SCREENING','Visit date collection','DIABETES_DOCTOR_FFEDBACK' )  " +
                "  WHERE q.NAME IN ( ");
        for (int i = 0; i < algoList.size(); i++) {
            if (i == (algoList.size() - 1)) {
                sql.append("'" + algoList.get(i) + "'");
            } else {
                sql.append("'" + algoList.get(i) + "' ,");
            }
        }

        sql.append(")  ");
        sql.append(" ORDER BY b.BENEF_CODE ASC, pim.INTERVIEW_ID DESC, pidf.DOC_FOLLOWUP_ID DESC) tmp ");
        sql.append(" group by tmp.BENEF_CODE ) toptemp ");

        if (invesStatusList != null && invesStatusList.size() > 0) {
            sql.append(" WHERE toptemp.INVES_STATUS IN ( ");
            for (int i = 0; i < invesStatusList.size(); i++) {
                if (i == (invesStatusList.size() - 1)) {
                    sql.append("'" + invesStatusList.get(i) + "'");
                } else {
                    sql.append("'" + invesStatusList.get(i) + "' ,");
                }
            }
        }
        sql.append(")  ");

        if (searchText != null && !searchText.equals("")) {
            sql.append(" AND toptemp.HOUSEHOLD_NUMBER='" + searchText + "' ");
        }


        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public JSONArray getInterviewListbyBenefCode(String benefCode, ArrayList<String> algoList) {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append("select pidf.QUESTION_ANSWER_JSON FEEDBACK_QUESTION_ANSWER_JSON ,pim.QUESTION_ANSWER_JSON, pim.INTERVIEW_ID, pidf.INTERVIEW_ID FEEDBACK_INTERVIEW_ID, q.NAME,q.QUESTIONNAIRE_TITLE," + "substr( pim.BENEF_CODE, -5, 5 ) BENEF_CODE, pim.BENEF_CODE BENEF_CODE_FULL," + " b.BENEF_NAME, pidf.INVES_STATUS from patient_interview_master pim " + "LEFT JOIN patient_interview_doctor_feedback pidf on pim.INTERVIEW_ID=pidf.INTERVIEW_ID " + "INNER JOIN beneficiary b ON pim.BENEF_CODE = b.BENEF_CODE " + "INNER JOIN questionnaire q ON pim.QUESTIONNAIRE_ID = q.QUESTIONNAIRE_ID " + "INNER JOIN user_info ui ON pim.USER_ID = ui.USER_ID " + "  WHERE q.NAME IN ( ");
        for (int i = 0; i < algoList.size(); i++) {
            if (i == (algoList.size() - 1)) {
                sql.append("'" + algoList.get(i) + "'");
            } else {
                sql.append("'" + algoList.get(i) + "' ,");
            }
        }

        sql.append(")  ");
        sql.append(" AND pim.BENEF_CODE='" + benefCode + "'");
        sql.append(" ORDER BY b.BENEF_NAME ASC");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public JSONArray getPatientInterviewbyBenefCode(String benefCode, ArrayList<String> algoList) {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append("SELECT" + " PIM.INTERVIEW_ID," + " PIM.FCM_INTERVIEW_ID," + " b.BENEF_CODE," + " q.QUESTIONNAIRE_ID," + " q.QUESTIONNAIRE_TITLE," + " REPLACE(q.NAME, '_', ' ') QUESTIONNAIRE_NAME," + " q.NAME RAW_QUESTIONNAIRE_NAME," + " PIM.QUESTION_ANSWER_JSON MASTER_INTERVIEW_QUESTION_ANSWER_JSON," + " PIM.START_TIME," + " Q.NAME," + " IFNULL(rc.REF_CENTER_NAME, '') REF_CENTER_NAME" + " FROM patient_interview_master PIM" + " INNER JOIN beneficiary b" + " ON PIM.BENEF_CODE = b.BENEF_CODE" + " INNER JOIN questionnaire q" + " ON PIM.QUESTIONNAIRE_ID = q.QUESTIONNAIRE_ID" + " INNER JOIN user_info ui" + " ON PIM.USER_ID = ui.USER_ID" + " LEFT JOIN referral_center rc" + " ON PIM.REF_CENTER_ID = rc.REF_CENTER_ID" + " WHERE q.NAME IN ( ");

        for (int i = 0; i < algoList.size(); i++) {
            if (i == (algoList.size() - 1)) {
                sql.append("'" + algoList.get(i) + "'");
            } else {
                sql.append("'" + algoList.get(i) + "' ,");
            }
        }
        sql.append(")  ");
        sql.append(" AND b.BENEF_CODE='" + benefCode + "'");
        sql.append(" ORDER BY PIM.INTERVIEW_ID ASC");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public JSONArray getPatientInterviewDetailsbyInterviewIdBenefCode(Long interviewId, String benefCode) {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append("SELECT" + " m.BENEF_CODE," + " pid.INTERVIEW_ID," + " pid.INTERVIEW_DTL_ID," + " pid.Q_ID PID_QID," + " pid.TRANS_REF transRef," + " q.Q_ID,qm.QUESTIONNAIRE_ID," + " IFNULL(qm.QUESTIONNAIRE_TITLE, qm.NAME) QUESTIONNAIRE_TITLE," + " q.SHOWABLE_OUTPUT AS bySystem," + " pid.ANSWER ansr," + " q.Q_NAME rawQname, " + "CASE " + " WHEN q.Q_NAME IS NOT NULL" + " THEN REPLACE(q.Q_NAME, '_', ' ')" + " ELSE q.Q_NAME " + " END outputQname, " + "CASE " + " WHEN q.Q_TITLE <> ''" + " THEN q.Q_TITLE" + " ELSE REPLACE(q.Q_NAME, '_', ' ') " + " END outputQtitle, " +

                "CASE " + " WHEN pid.ANSWER='null' " + "THEN NULL " + "ELSE CASE " + " WHEN (q.Q_NAME='REFERRAL_CENTER')" + " THEN rc.REF_CENTER_NAME" + " ELSE pid.ANSWER " + " END " + " END outputAns " + " FROM" + " patient_interview_master m" + " JOIN patient_interview_detail pid ON m.INTERVIEW_ID=pid.INTERVIEW_ID" + " INNER JOIN questionnaire_detail q ON pid.Q_ID = q.Q_ID" + " INNER JOIN questionnaire qm ON q.QUESTIONNAIRE_ID = qm.QUESTIONNAIRE_ID " + " LEFT JOIN referral_center rc ON rc.REF_CENTER_ID=m.REF_CENTER_ID ");
        sql.append(" WHERE m.INTERVIEW_ID =" + interviewId);
        sql.append(" AND m.BENEF_CODE='" + benefCode + "'");
//		sql.append(" AND q.Q_TYPE != 'prescription' ");
//		sql.append(" AND q.Q_TYPE != 'referralCenter' ");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public JSONArray getInterviewDetailsbyInterviewIdBenefCode(Long interviewId, String benefCode) {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append("select qd.Q_STATUS, pid.INTERVIEW_DTL_ID,pid.INTERVIEW_ID PID_INTERVIEW_ID,pid.Q_ID,qd.Q_NAME,pid.ANSWER,pid.TRANS_REF,pim.INTERVIEW_ID,pim.PARENT_INTERVIEW_ID,pim.BENEF_CODE,pim.QUESTION_ANSWER_JSON PIM_QUESTION_ANSWER_JSON," + " pim.PRESC_ID,pim.ADVICE_FLAG,pim.IS_FEEDBACK FROM patient_interview_detail pid" + " LEFT JOIN patient_interview_master pim ON pid.INTERVIEW_ID=pim.INTERVIEW_ID " + " LEFT JOIN questionnaire_detail qd ON qd.Q_ID=pid.Q_ID ");
        sql.append(" WHERE pid.INTERVIEW_ID=" + interviewId);
        sql.append(" AND pim.BENEF_CODE='" + benefCode + "'");
        sql.append(" ORDER BY pid.INTERVIEW_DTL_ID ASC ");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public JSONArray getInterviewDoctorFeedbackbyInterviewIdBenefCode(Long interviewId, String benefCode) {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append("select b.BENEF_ID,pidf.QUESTION_ANSWER_JSON2, pidf.TRANS_REF, pim.INTERVIEW_ID PARENT_INTERVIEW_ID, b.BENEF_ID, pim.BENEF_CODE, pidf.DOC_FOLLOWUP_ID, b.BENEF_NAME," + " pidf.USER_ID,pidf.QUESTION_ANSWER_JSON,pidf.NEXT_FOLLOWUP_DATE,pidf.PRESCRIBED_MEDICINE, pidf.DOCTOR_FINDINGS," + " pidf.MESSAGE_TO_FCM,pidf.INVES_ADVICE,pidf.INVES_RESULT,pidf.INVES_STATUS,pidf.FEEDBACK_DATE,pidf.FEEDBACK_SOURCE," + " pidf.REF_CENTER_ID, pidf.NOTIFICATION_STATUS from patient_interview_doctor_feedback pidf LEFT JOIN " + " (Select * from patient_interview_master  where FCM_INTERVIEW_ID=" + interviewId + " AND " + " TRANS_REF=(Select min(TRANS_REF) from patient_interview_master where FCM_INTERVIEW_ID=" + interviewId + " )) " + " pim \n" + " ON pim.FCM_INTERVIEW_ID=pidf.INTERVIEW_ID " + " LEFT JOIN beneficiary b ON b.BENEF_CODE=pim.BENEF_CODE");
        sql.append(" WHERE pidf.INTERVIEW_ID=" + interviewId);
        //sql.append(" AND pim.BENEF_CODE='"+benefCode+"'");
        sql.append(" AND pidf.BENEF_CODE='" + benefCode + "'");
        //sql.append(" AND pidf.FEEDBACK_SOURCE !='"+Constants.AUTO_GENERATED_FEEDBACK_SOURCE+"'");
        sql.append(" AND (pidf.FEEDBACK_SOURCE <> '" + Constants.AUTO_GENERATED_FEEDBACK_SOURCE + "' ");
        sql.append(" OR pidf.FEEDBACK_SOURCE IS NULL )");
        sql.append(" ORDER BY pidf.DOC_FOLLOWUP_ID ASC ");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public JSONArray getInterviewDoctorFeedbackForNotification(int notificationStatus) {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append("select pidf.*,b.BENEF_NAME from patient_interview_doctor_feedback pidf LEFT JOIN beneficiary b ON b.BENEF_CODE=pidf.BENEF_CODE   ");
        sql.append(" WHERE pidf.NOTIFICATION_STATUS= " + notificationStatus);
        sql.append(" ORDER BY pidf.DOC_FOLLOWUP_ID ASC ");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public int updatePatientInterviewDoctorFeedback(long doctorFoolowupId, int notificationStatus) {
        ContentValues cv = new ContentValues();
        cv.put("NOTIFICATION_STATUS", notificationStatus);
        return db.update("patient_interview_doctor_feedback", cv, "DOC_FOLLOWUP_ID=?", new String[]{Long.toString(doctorFoolowupId)});
    }

    public int updatePatientInterviewDoctorFeedback(long doctorFollowupId, long interviewId) {
        ContentValues cv = new ContentValues();
        cv.put(Column.DOC_FOLLOWUP_ID, doctorFollowupId);
        int numberOfRowAffected = db.update(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, cv, "INTERVIEW_ID=? AND " + Column.DOC_FOLLOWUP_ID + "=? ", new String[]{Long.toString(interviewId), Long.toString(0)});
        return numberOfRowAffected;
    }


    public String getPARAM1ValueFromQuestionInfo(String qName) {
        String param1Value = null;
        StringBuilder sql = new StringBuilder();
        sql.append("select q.PARAM_1 from questionnaire q where q.NAME='" + qName + "'");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            param1Value = cursor.getString(cursor.getColumnIndex("PARAM_1"));
        }
        cursor.close();
        return param1Value;
    }

    public SavedInterviewInfo getPatientInterviewMasterInfo(String benefCode, String questionnaireName, String lang, String dataSent) {
        //PatientInterviewMaster patientInterviewMaster =new PatientInterviewMaster();
        //SavedInterviewInfo savedInterviewInfo=new SavedInterviewInfo();
        SavedInterviewInfo si = new SavedInterviewInfo();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT pim.questionnaire_id QUESTIONNAIRE_ID, strftime('%d-%m-%Y',pim.TRANS_REF/1000, 'unixepoch', 'localtime') as interviewDate,  ");
        sql.append(" pim.TRANS_REF TRANS_REF,  ");
        sql.append(" b.benef_id BENEF_ID,  ");
        sql.append(" case when  length(trim(pim.BENEF_NAME))>0 THEN pim.BENEF_NAME   when length(trim(b.benef_name_local))>0 then b.benef_name_local else b.benef_name END  BENEF_NAME, ");
        sql.append(" b.benef_image_path BENEF_IMAGE_PATH,  ");
        sql.append(" b.benef_code BENEF_CODE,  ");
        sql.append(" substr(b.benef_code,-5,3) HH_NUMBER,  ");
        sql.append(" CASE WHEN CAST(strftime('%H', pim.TRANS_REF/1000, 'unixepoch', 'localtime') AS INTEGER) < 12  ");
        sql.append(" then strftime('%H:%M', pim.TRANS_REF/1000, 'unixepoch','localtime') || ' AM'  ");
        sql.append(" ELSE   strftime('%H:%M', pim.TRANS_REF/1000, '-12 Hours', 'unixepoch', 'localtime') || ' PM' END as INTERVIEW_TIME ,  ");
        sql.append(" q.questionnaire_title QUESTIONNAIRE_TITLE,  ");
        sql.append(" q.name QUESTIONNAIRE_NAME ,  ");
        sql.append(" pim.file_path FILE_PATH,  ");
        sql.append(" case when 'N'=UPPER(?) THEN pim.question_answer_json ELSE '' END QUESTION_ANSWER_JSON,  ");
        sql.append(" pim.interview_id INTERVIEW_ID,  ");
        sql.append(" pim.parent_interview_id PARENT_INTERVIEW_ID,  ");
        sql.append(" pim.file_key FILE_KEY ");
        sql.append(" FROM patient_interview_master pim  ");
        sql.append(" left join beneficiary b on pim.benef_code = b.benef_code   ");
        sql.append(" inner join questionnaire q on pim.questionnaire_id = q.questionnaire_id  AND q.LANG_CODE=? ");
        sql.append(" where  pim.benef_code= '" + benefCode + "' and pim.TRANS_REF >0 ");

        Log.e("SQL", sql.toString());
        Cursor cr = db.rawQuery(sql.toString(), new String[]{dataSent, lang});
        String date = "";
        if (cr.moveToFirst()) {

            do {

                si.setBeneficiaryId(cr.getLong(cr.getColumnIndex("BENEF_ID")));
                si.setBeneficiaryCode(cr.getString(cr.getColumnIndex("BENEF_CODE")));
                si.setInterviewId(cr.getLong(cr.getColumnIndex("INTERVIEW_ID")));
                si.setParentInterviewId(cr.getLong(cr.getColumnIndex("PARENT_INTERVIEW_ID")));
                si.setBenefImagePath(cr.getString(cr.getColumnIndex("BENEF_IMAGE_PATH")));
                si.setTransRef(cr.getLong(cr.getColumnIndex("TRANS_REF")));
                si.setBenefName(cr.getString(cr.getColumnIndex("BENEF_NAME")));
                si.setDate(cr.getString(cr.getColumnIndex("interviewDate")));
                si.setHouseholdNumber(cr.getString(cr.getColumnIndex("HH_NUMBER")));
                si.setInputBinaryFilePathList(cr.getString(cr.getColumnIndex("FILE_PATH")));
                si.setQuestionAnswerJson(cr.getString(cr.getColumnIndex("QUESTION_ANSWER_JSON")));
                si.setQuestionnarieTitle(cr.getString(cr.getColumnIndex("QUESTIONNAIRE_TITLE")));
                si.setQuestionnarieName(cr.getString(cr.getColumnIndex("QUESTIONNAIRE_NAME")));
                si.setQuestionnaireId(cr.getLong(cr.getColumnIndex("QUESTIONNAIRE_ID")));

                si.setTime(cr.getString(cr.getColumnIndex("INTERVIEW_TIME")));
                si.setFileKey(cr.getString(cr.getColumnIndex("FILE_KEY")));
                if (!date.equals(si.getDate())) {
                    date = si.getDate();
                    si.setNewDate(true);
                }
//                        if (si.getParentInterviewId() > 0) {
//                          si.setScheduleInfo(getScheduleInfo(si.getParentInterviewId(),0));
//                      }

            } while (cr.moveToNext());
        }
        cr.close();

        return si;

    }

    public JSONArray getInterviewDoctorFeedbackForUpdate() {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append(" select pidf.DOC_FOLLOWUP_ID,pim.FCM_INTERVIEW_ID PIM_FCM_INTERVIEW_ID,pim.INTERVIEW_ID PIM_INTERVIEW_ID from patient_interview_doctor_feedback pidf INNER JOIN patient_interview_master pim " + " ON pidf.INTERVIEW_ID=pim.INTERVIEW_ID ORDER BY pidf.DOC_FOLLOWUP_ID DESC ");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public int updatePatientInterviewDoctorFeedbackBasedOnFCMInterviewId(long doctorFollowupId, long fcmInterviewId) {
        ContentValues cv = new ContentValues();
        cv.put(Column.INTERVIEW_ID, fcmInterviewId);
        int numberOfRowAffected = db.update(DBTable.PATIENT_INTERVIEW_DOCTOR_FEEDBACK, cv, Column.DOC_FOLLOWUP_ID + "=? ", new String[]{Long.toString(doctorFollowupId)});
        return numberOfRowAffected;
    }

    public String getRefcenterNameById(long refCenterId, String langCode) {
        String refCentreName = null;
        StringBuilder sql = new StringBuilder();

        if (langCode.equals("bn")) {
            sql.append("SELECT rf.REF_CENTER_NAME_CAPTION  from referral_center rf where rf.REF_CENTER_ID=" + refCenterId);
            Cursor cursor = db.rawQuery(sql.toString(), null);
            if (cursor.moveToFirst()) {
                refCentreName = cursor.getString(cursor.getColumnIndex(Column.REF_CENTER_NAME_CAPTION));
            }
            cursor.close();
        } else {
            sql.append("SELECT rf.REF_CENTER_NAME  from referral_center rf where rf.REF_CENTER_ID=" + refCenterId);
            Cursor cursor = db.rawQuery(sql.toString(), null);
            if (cursor.moveToFirst()) {
                refCentreName = cursor.getString(cursor.getColumnIndex(Column.REF_CENTER_NAME));
            }
            cursor.close();
        }
        return refCentreName;
    }

    public int unreadNotificationRowCount() {
        int count = 0;
        try {
            String countQuery = "SELECT  * FROM notification_master where VIEW_TIME IS NULL  and datetime('now', 'localtime')>datetime(NOTIFICATION_TIME) ";
            Cursor cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        // return count
        return count;
    }

    public int updateNotificationStatus(long notificationId, int notificationStatus) {

        ContentValues cv = new ContentValues();
        cv.put("NOTIFICATION_STATUS", notificationStatus);
        return db.update("notification_master", cv, "S_NOTIFICATION_ID=?", new String[]{Long.toString(notificationId)});
    }

    public int updateSendStatusNotification(long notificationId, int status) {

        ContentValues cv = new ContentValues();
        cv.put("SEND_STATUS", status);
        return db.update("notification_master", cv, "S_NOTIFICATION_ID=?", new String[]{Long.toString(notificationId)});
    }

    public int updateNotificationViewTimeUpdate(long notificationId, String viewTime) {

        ContentValues cv = new ContentValues();
        cv.put("VIEW_TIME", viewTime);
        return db.update("notification_master", cv, "S_NOTIFICATION_ID=?", new String[]{Long.toString(notificationId)});
    }

    public ArrayList<NotificationItem> getNotificationList(int day) {
        ArrayList<NotificationItem> notificationItemList = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        try {
            sql.append("select * from notification_master  where datetime('now', 'localtime')>datetime(NOTIFICATION_TIME)  and   datetime(NOTIFICATION_TIME)  BETWEEN  datetime('now', 'localtime', '-" + day + " days') AND datetime('now', 'localtime')   ORDER by strftime('%s',NOTIFICATION_TIME)*1000 DESC");
            Cursor cursor = db.rawQuery(sql.toString(), null);
            if (cursor.moveToFirst()) {
                do {
                    NotificationItem notificationItem = new NotificationItem();
                    notificationItem.setNotificationId(cursor.getLong(cursor.getColumnIndex(Column.NOTIFICATION_ID)));
                    notificationItem.setContent(cursor.getString(cursor.getColumnIndex(Column.CONTENT)));
                    notificationItem.setTitle(cursor.getString(cursor.getColumnIndex(Column.TITLE)));
                    notificationItem.setNotificationType(cursor.getString(cursor.getColumnIndex(Column.TYPE)));
                    notificationItem.setNotificationIcon(cursor.getString(cursor.getColumnIndex(Column.NOTIFICATION_ICON)));
                    notificationItem.setSNotificationId(cursor.getLong(cursor.getColumnIndex(Column.S_NOTIFICATION_ID)));
                    notificationItem.setBenefCode(cursor.getString(cursor.getColumnIndex(Column.BENEF_CODE)));
                    notificationItem.setNotificationTime(cursor.getString(cursor.getColumnIndex(Column.NOTIFICATION_TIME)));
                    notificationItemList.add(notificationItem);
                } while (cursor.moveToNext());
            }
            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return notificationItemList;
    }

    public ArrayList<NotificationItem> getUnseenNotification(int day, int topRow) {
        ArrayList<NotificationItem> notificationItemList = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        try {
            sql.append("select * from notification_master  where datetime('now', 'localtime')>datetime(NOTIFICATION_TIME)  and   datetime(NOTIFICATION_TIME)  BETWEEN  datetime('now', 'localtime', '-" + day + " days')  AND  VIEW_TIME IS NULL AND datetime('now', 'localtime')   ORDER by strftime('%s',NOTIFICATION_TIME)*1000 DESC");
            if (topRow > 0) {
                sql.append("  LIMIT " + topRow);
            }

            Cursor cursor = db.rawQuery(sql.toString(), null);
            if (cursor.moveToFirst()) {
                do {
                    NotificationItem notificationItem = new NotificationItem();
                    notificationItem.setNotificationId(cursor.getLong(cursor.getColumnIndex(Column.NOTIFICATION_ID)));
                    notificationItem.setContent(cursor.getString(cursor.getColumnIndex(Column.CONTENT)));
                    notificationItem.setTitle(cursor.getString(cursor.getColumnIndex(Column.TITLE)));
                    notificationItem.setNotificationType(cursor.getString(cursor.getColumnIndex(Column.TYPE)));
                    notificationItem.setNotificationIcon(cursor.getString(cursor.getColumnIndex(Column.NOTIFICATION_ICON)));
                    notificationItem.setSNotificationId(cursor.getLong(cursor.getColumnIndex(Column.S_NOTIFICATION_ID)));
                    notificationItem.setBenefCode(cursor.getString(cursor.getColumnIndex(Column.BENEF_CODE)));
                    notificationItem.setNotificationTime(cursor.getString(cursor.getColumnIndex(Column.NOTIFICATION_TIME)));
                    notificationItemList.add(notificationItem);
                } while (cursor.moveToNext());
            }
            cursor.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return notificationItemList;
    }


    public JSONArray getAllUnsendNotification() {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append("select  S_NOTIFICATION_ID,VIEW_TIME, SEND_STATUS  from  notification_master  where VIEW_TIME IS NOT NULL and SEND_STATUS = -1 and  datetime('now', 'localtime')>datetime(NOTIFICATION_TIME) ");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public JSONArray getUnNotifyNotificationList() {
        StringBuilder sql = new StringBuilder();
        JSONArray object = new JSONArray();
        sql.append("select *  from  notification_master  where NOTIFICATION_STATUS=0 and datetime('now', 'localtime')>datetime(NOTIFICATION_TIME)");
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            try {
                object = toJsonArray(cursor);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return object;
    }

    public long getReferralCenterIdByCode(String refCenterCode) {
        long reasonId = -1;

        String sql = " SELECT REF_CENTER_ID FROM referral_center WHERE REF_CENTER_CODE='" + refCenterCode + "'  AND LANG_CODE='" + App.getContext().getAppSettings().getLanguage() + "'";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            reasonId = cursor.getLong(cursor.getColumnIndex("REF_CENTER_ID"));
        }
        return reasonId;
    }

    public String getNumberOfInterview(String benefCode, String algorithmName, long targetMili, long currentMili) {

        String sql = "SELECT count(*) as NUMBER_OF_INTERVIEW FROM " + " patient_interview_master pim join questionnaire q2 on pim.QUESTIONNAIRE_ID = q2.QUESTIONNAIRE_ID " + " where pim.BENEF_CODE = '" + benefCode + "' and q2.NAME = '" + algorithmName + "' and  pim.CREATE_DATE BETWEEN " + targetMili + " and " + currentMili;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("NUMBER_OF_INTERVIEW")).trim();
        }
        return "";
    }

    @SuppressLint("Range")
    public int getLastHouseHoldNumberWithIncrement() {
        String houseHoldId = "0";
        String sql = "SELECT  substr(HH_NUMBER,10,3) HH_NUMBER  from household order by  substr(HH_NUMBER,10,3)  DESC limit 1 ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            try {
                houseHoldId = cursor.getString(cursor.getColumnIndex("HH_NUMBER"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        cursor.close();
        return Integer.parseInt("" + houseHoldId) + 1;
    }

    public QuestionnaireInfo getQuestionnaireCallByName(String questionName, String langCode) {

        String sql = "SELECT * FROM questionnaire WHERE NAME= '" + questionName + "' AND STATE=1 AND LANG_CODE='" + langCode + "' ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        QuestionnaireInfo questionnaireInfo = new QuestionnaireInfo();
        if (cursor.moveToFirst()) {
            do {

                questionnaireInfo.setId(cursor.getLong(cursor.getColumnIndex(Column.QUESTIONNAIRE_ID)));
                questionnaireInfo.setPromptForBeneficiary(cursor.getLong(cursor.getColumnIndex(Column.QUESTIONNAIRE_PROMPT_FOR_BENEFICIARY)));
                questionnaireInfo.setQuestionnaireTitle(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_TITLE)));
                questionnaireInfo.setBenefSelectionCriteria(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_BENEF_SELECTION_CRITERIA)));
                questionnaireInfo.setQuestionnaireJSON(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_JSON)));
                questionnaireInfo.setQuestionnaireName(cursor.getString(cursor.getColumnIndex(Column.QUESTIONNAIRE_NAME)));
                questionnaireInfo.setVisibileInCategory(cursor.getLong(cursor.getColumnIndex(Column.VISIBILE_IN_CATEGORY)));
                questionnaireInfo.setIcon(cursor.getString(cursor.getColumnIndex(Column.ICON)));
                questionnaireInfo.setSinglePgFormView(cursor.getString(cursor.getColumnIndex(Column.SINGLE_PG_FORM_VIEW)));

                return questionnaireInfo;
            } while (cursor.moveToNext());
        }

        cursor.close();
        return questionnaireInfo;
    }


    public ArrayList<Household> getVisitMoreHouseholds() {
        ArrayList<Household> householdList = null;
        String sql = "SELECT substr(h.HH_NUMBER,-3,3) HH_NUMBER, " + " h.NO_OF_FAMILY_MEMBER, h.MONTHLY_FAMILY_EXPENDITURE, h.HH_CHARACTER,  ifnull(h.HH_ADULT_WOMEN,0) HH_ADULT_WOMEN,  h.LONGITUDE,  h.LATITUDE, " + " h.REG_DATE, h.SENT,   h.STATE,   h.UPDATE_HISTORY " + "  from household h left join (select  substr(BENEF_CODE,10,3) HH_NO   from " + "patient_interview_master group by substr(BENEF_CODE,10,3)) pim  on pim.HH_NO = substr(h.HH_NUMBER,10,3)   WHERE  pim.HH_NO IS NULL and  STATE > 0  AND substr(HH_NUMBER,10,3)!='000'  ORDER BY SENT ASC ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            householdList = new ArrayList<Household>();
            do {
                Household household = new Household();
                household.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                household.setNoOfFamilyMember(cursor.getInt(cursor.getColumnIndex("NO_OF_FAMILY_MEMBER")));
                household.setMonthlyFamilyExpenditure(cursor.getString(cursor.getColumnIndex("MONTHLY_FAMILY_EXPENDITURE")));
                household.setHhAdultWomen(cursor.getLong(cursor.getColumnIndex("HH_ADULT_WOMEN")));
                household.setHhCharacter(cursor.getString(cursor.getColumnIndex("HH_CHARACTER")));
                household.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
                household.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));
                household.setRegDate(cursor.getLong(cursor.getColumnIndex("REG_DATE")));
                household.setSent(cursor.getLong(cursor.getColumnIndex("SENT")));
                household.setState(cursor.getLong(cursor.getColumnIndex("STATE")));
                household.setUpdateHistory(cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY")));
                householdList.add(household);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return householdList;
    }

    public ArrayList<Household> getVisitedHouseholds() {
        ArrayList<Household> householdList = new ArrayList<Household>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT  ");
        sql.append(" substr(h.HH_NUMBER, -3, 3 ) HH_NUMBER,  ");
        sql.append(" h.HH_NUMBER FULL_HH_NUMBER,  ");
        sql.append(" ifnull(h.LATITUDE ,0) LATITUDE, ");
        sql.append(" ifnull(h.LONGITUDE ,0) LONGITUDE, ");
        sql.append(" ifnull(h.MONTHLY_FAMILY_EXPENDITURE ,0) MONTHLY_FAMILY_EXPENDITURE,  ");
        sql.append(" h.HH_CHARACTER  HH_CHARACTER,  ");
        sql.append(" ifnull(h.HH_ADULT_WOMEN ,0) HH_ADULT_WOMEN, ");
        sql.append(" ifnull(h.HH_NAME ,'') HH_NAME, ");
        sql.append(" h.NO_OF_FAMILY_MEMBER  NO_OF_FAMILY_MEMBER,  ");
        sql.append(" h.STATE  STATE,  ");
        sql.append(" h.UPDATE_HISTORY  UPDATE_HISTORY,  ");
        sql.append(" ifnull(sum( b.countbenef ),0 )COUNT_REG_BENF,  ");
        sql.append(" substr(b.FH_CODE , -5, 5 ) FH_CODE, ");
        sql.append(" b.BENEF_NAME  BENEF_NAME,  ");
        sql.append(" b.BENEF_NAME_LOCAL  BENEF_NAME_LOCAL, ");
        sql.append(" b.BENEF_IMAGE_PATH  BENEF_IMAGE_PATH,  ");
        sql.append(" b.GENDER  GENDER,  ");
        sql.append(" b.DOB  DOB  ");
        sql.append(" FROM household h ");
        sql.append(" LEFT JOIN (  ");
        sql.append(" SELECT HH_NUMBER,  ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_CODE ELSE NULL END FH_CODE , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_NAME_LOCAL ELSE NULL END BENEF_NAME_LOCAL , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_NAME ELSE NULL END BENEF_NAME , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN BENEF_IMAGE_PATH ELSE NULL END BENEF_IMAGE_PATH , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN GENDER ELSE NULL END GENDER , ");
        sql.append(" CASE WHEN FAMILY_HEAD = 1 THEN DOB ELSE NULL END DOB , ");
        sql.append(" 1 countbenef  FROM beneficiary WHERE STATE=1 ) as b on h.HH_NUMBER=b.HH_NUMBER  ");
        sql.append(" inner join (select  substr(BENEF_CODE,10,3) HH_NO   from patient_interview_master group by substr(BENEF_CODE,10,3)) pim  on pim.HH_NO = substr(h.HH_NUMBER,10,3)   WHERE   STATE > 0  AND substr(h.HH_NUMBER,10,3)!='000'   GROUP BY h.HH_NUMBER ");
        sql.append(" ORDER BY substr( h.HH_NUMBER, -3, 3 ) ASC; ");


        Log.e("SQL-11", sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                Household household = new Household();
                household.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                household.setHouseholdHeadCode(cursor.getString(cursor.getColumnIndex("FH_CODE")));
                household.setMonthlyFamilyExpenditure(cursor.getString(cursor.getColumnIndex("MONTHLY_FAMILY_EXPENDITURE")));
                household.setHhAdultWomen(cursor.getLong(cursor.getColumnIndex("HH_ADULT_WOMEN")));
                household.setHhCharacter(cursor.getString(cursor.getColumnIndex("HH_CHARACTER")));

                household.setFullHouseHoldNumber(cursor.getString(cursor.getColumnIndex("FULL_HH_NUMBER")));

                household.setHouseholdHeadImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));

                String updateHistroyStr = cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY"));
                if (updateHistroyStr == null || updateHistroyStr.equals("")) {
                    household.setUpdateHistory("0");
                } else {
                    household.setUpdateHistory(cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY")));
                }
//				household.setUpdateHistory(cursor.getString(cursor
////						.getColumnIndex("UPDATE_HISTORY")));

                household.setState(cursor.getLong(cursor.getColumnIndex("STATE")));

                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                household.setHouseholdHeadName(Utility.formatTextBylanguage(nameOriginal, nameLocal));


                household.setNumberOfBeneficiary(cursor.getInt(cursor.getColumnIndex("COUNT_REG_BENF")));

                String dob = cursor.getString(cursor.getColumnIndex("DOB"));

                try {
                    household.setHouseholdHeadAge(Utility.getAge(dob));
                } catch (Exception e) {
                    household.setHouseholdHeadAge("");
                }

                household.setHouseholdHeadGender(cursor.getString(cursor.getColumnIndex("GENDER")));
                Double lon = 0.0;
                Double lat = 0.0;
                try {
                    lon = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                    lat = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));

                } catch (Exception e) {
                    e.printStackTrace();
                }


                if (lon != null && lat != null && (lon + lat) > 0.0) {
                    household.setHasLocation(true);
                    household.setLongitude(lon);
                    household.setLatitude(lat);
                } else {
                    household.setHasLocation(false);
                    household.setLongitude(0);
                    household.setLatitude(0);
                }

                Long hhbdNumberOfMember = cursor.getLong(cursor.getColumnIndex("NO_OF_FAMILY_MEMBER"));
                if (hhbdNumberOfMember != null) {
                    household.setNoOfFamilyMember(hhbdNumberOfMember);
                } else {
                    household.setNoOfFamilyMember((long) 0);
                }

                householdList.add(household);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return householdList;
    }

    public ArrayList<Household> getVisitedHousehssolds() {
        ArrayList<Household> householdList = null;
        String sql = "SELECT substr(h.HH_NUMBER,-3,3) HH_NUMBER, " + " h.NO_OF_FAMILY_MEMBER, h.MONTHLY_FAMILY_EXPENDITURE, h.HH_CHARACTER,  ifnull(h.HH_ADULT_WOMEN,0) HH_ADULT_WOMEN,  h.LONGITUDE,  h.LATITUDE, " + " h.REG_DATE, h.SENT,   h.STATE,   h.UPDATE_HISTORY " + "  from household h inner join (select  substr(BENEF_CODE,10,3) HH_NO   from patient_interview_master group by substr(BENEF_CODE,10,3)) pim  on pim.HH_NO = substr(h.HH_NUMBER,10,3)   WHERE   STATE > 0  AND substr(HH_NUMBER,10,3)!='000'  ORDER BY SENT ASC ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            householdList = new ArrayList<Household>();
            do {
                Household household = new Household();
                household.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
                household.setNoOfFamilyMember(cursor.getInt(cursor.getColumnIndex("NO_OF_FAMILY_MEMBER")));
                household.setMonthlyFamilyExpenditure(cursor.getString(cursor.getColumnIndex("MONTHLY_FAMILY_EXPENDITURE")));
                household.setHhAdultWomen(cursor.getLong(cursor.getColumnIndex("HH_ADULT_WOMEN")));
                household.setHhCharacter(cursor.getString(cursor.getColumnIndex("HH_CHARACTER")));
                household.setLatitude(cursor.getDouble(cursor.getColumnIndex("LATITUDE")));
                household.setLongitude(cursor.getDouble(cursor.getColumnIndex("LONGITUDE")));
                household.setRegDate(cursor.getLong(cursor.getColumnIndex("REG_DATE")));
                household.setSent(cursor.getLong(cursor.getColumnIndex("SENT")));
                household.setState(cursor.getLong(cursor.getColumnIndex("STATE")));
                household.setUpdateHistory(cursor.getString(cursor.getColumnIndex("UPDATE_HISTORY")));
                householdList.add(household);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return householdList;
    }

    public void saveUserInfoList(ArrayList<UserInfo> userInfoList, JSONObject param, long userId) {
        if (userInfoList == null) return;
        for (UserInfo userInfo : userInfoList) {
            ContentValues cv = new ContentValues();
            cv.put(Column.USER_ID, userInfo.getUserId());
            cv.put(Column.USER_LOGIN_ID, userInfo.getUserCode());
            cv.put(Column.USER_NAME, userInfo.getUserName());
            cv.put(Column.TARGET_HH, userInfo.getTargetHh());
            cv.put(Column.LOCATION_NAME, userInfo.getLocationName());

            cv.put(Column.ORG_ID, userInfo.getOrgId());
            cv.put(Column.ORG_CODE, userInfo.getOrgCode());
            cv.put(Column.ORG_NAME, userInfo.getOrgName());
            cv.put(Column.ORG_DESC, userInfo.getOrgDesc());
            cv.put(Column.ORG_ADDRESS, userInfo.getOrgAddress());
            cv.put(Column.LOCATION_ID, userInfo.getLocationId());
            cv.put(Column.LOCATION_CODE, userInfo.getLocationCode());
            cv.put(Column.ORG_COUNTRY, userInfo.getOrgCountry());
            cv.put(Column.HEADER_SMALL_LOGO_PATH, userInfo.getHeaderSmallLogoPath());
            cv.put(Column.LOGIN_IMAGE_PATH_MOBILE, userInfo.getLoginImagePathMobile());
            cv.put(Column.TITLE_LOGO_PATH_MOBILE, userInfo.getTitleLogoPathMobile());
            cv.put(Column.APP_TITLE_MOBILE, userInfo.getAppTitleMobile());
            cv.put(Column.STATE, userInfo.getState());

            cv.put(Column.OTHER_DETAILS, userInfo.getOtherDetails());
            if (userInfo.getProfilePicInString() != null && userInfo.getProfilePicInString().length() > 100) {
                cv.put(Column.PROFILE_IMAGE, userInfo.getProfilePicInString());
            }
            long affectedRow = db.update("user_info", cv, Column.USER_LOGIN_ID + "=? AND ORG_ID=" + userInfo.getOrgId(), new String[]{userInfo.getUserCode()});
            if (affectedRow == 0) {
                db.insert(DBTable.USER_INFO, null, cv);
            }
        }


        if (param != null) {

            try {
                param.put(DBTable.USER_INFO, 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateDataVersion(DBTable.USER_INFO, 1, 1, param, KEY.VERSION_NO_USER_INFO);
        }

    }

    public void saveParamadicWiseLocationList(ArrayList<LocationModel> userInfoList, JSONObject param) {
        if (userInfoList == null) return;
        for (LocationModel locationModel : userInfoList) {
            ContentValues cv = new ContentValues();
            cv.put(LocationModel.PARENT_LOCATION_ID, locationModel.getParentLocationId());
            cv.put(LocationModel.LOCATION_ID, locationModel.getLocationId());
            cv.put(LocationModel.LOCATION_NAME, locationModel.getLocationName());
            cv.put(LocationModel.LOCATION_CODE, locationModel.getLocationCode());

            long affectedRow = db.update("location", cv, LocationModel.LOCATION_ID + "=? ", new String[]{String.valueOf(locationModel.getLocationId())});
            if (affectedRow == 0) {

                db.insert("location", null, cv);
            }
        }


        if (param != null) {

            try {
                param.put(DBTable.USER_INFO, 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateDataVersion(DBTable.USER_INFO, 1, 1, param, KEY.VERSION_NO_USER_INFO);
        }

    }

//    ------------Satelite--------------

    public long getFcmCoverageCount() {
        String sql = " SELECT DISTINCT(" + "" + "" + ") FROM household where state > 0  " + " and substr(HH_NUMBER,10,3)!='000' ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        int numberOfRow = cursor.getCount();
        cursor.close();
        if (numberOfRow > 0) return numberOfRow;
        else return 0;
    }

    public String generateBeneficiaryCodeFromParamedic(String loginId) {
        String benefCode = "";

        String sql = String.format("SELECT COALESCE(  CASE    WHEN max_benef_code IS NULL THEN '" + loginId + "' ||'00001'   ELSE '" + loginId + "' || SUBSTR((1000000000000 + CAST(SUBSTR(max_benef_code, 1, 5) AS INTEGER) + 1), 9, 14)   END,   '00001') AS BENEF_CODE FROM (   SELECT MAX(SUBSTR(BENEF_CODE, 10, 14)) AS max_benef_code   FROM beneficiary   WHERE HH_NUMBER IS NULL OR HH_NUMBER = '') ;");
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            benefCode = cursor.getString(cursor.getColumnIndex("BENEF_CODE"));
        }

        return benefCode;
    }

    public String generateBeneficiaryCodeFromParamedicCopy(String upzilaCode, String usrSlNo) {
        String benefCode = "";

        String sql = String.format("SELECT COALESCE( " + "  CASE " + "    WHEN max_benef_code IS NULL THEN '" + upzilaCode + "'||'" + usrSlNo + "'||'0000001' " + "    ELSE '" + upzilaCode + "'||'" + usrSlNo + "' || SUBSTR((10000000 + CAST(SUBSTR(max_benef_code, 5, 14) AS INTEGER) + 1), 2, 11) " + "  END, " + "  '0000001' " + ") AS BENEF_CODE " + "FROM ( " + "  SELECT MAX(SUBSTR(BENEF_CODE, 5, 14)) AS max_benef_code " + "  FROM beneficiary " + "  WHERE HH_NUMBER IS NULL OR HH_NUMBER = ''" + ") ;");
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            benefCode = cursor.getString(cursor.getColumnIndex("BENEF_CODE"));
        }

        return benefCode;
    }

    public ArrayList<SavedInterviewInfo> getInterviewListByBenefCode(String lang, String benefCode) {

        ArrayList<SavedInterviewInfo> interviewList = new ArrayList<SavedInterviewInfo>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT pim.questionnaire_id QUESTIONNAIRE_ID, strftime('%d-%m-%Y',pim.TRANS_REF/1000, 'unixepoch', 'localtime') as interviewDate,  ");
        sql.append(" pim.TRANS_REF TRANS_REF,  ");
        sql.append(" CASE WHEN CAST(strftime('%H', pim.TRANS_REF/1000, 'unixepoch', 'localtime') AS INTEGER) < 12  ");
        sql.append(" then strftime('%H:%M', pim.TRANS_REF/1000, 'unixepoch','localtime') || ' AM'  ");
        sql.append(" ELSE   strftime('%H:%M',time(strftime('%H:%M', pim.TRANS_REF/1000, 'unixepoch', 'localtime'),'-12 hours')) || ' PM' END as INTERVIEW_TIME ,  ");
        sql.append(" IFNULL(q.questionnaire_title,q.name) QUESTIONNAIRE_TITLE,  ");
        sql.append(" q.name QUESTIONNAIRE_NAME ,  ");
        sql.append(" pim.file_path FILE_PATH,  ");
        sql.append(" q.QUESTIONNAIRE_JSON, ");
        sql.append(" QUESTION_ANSWER_JSON, ");
        sql.append(" pim.interview_id INTERVIEW_ID,  ");
        sql.append(" pim.file_key FILE_KEY ");
        sql.append(" FROM patient_interview_master pim  ");
        sql.append(" inner join questionnaire q on pim.questionnaire_id = q.questionnaire_id  AND q.LANG_CODE=? ");
        sql.append(" where q.NAME !='BENEFICIARY_REGISTRATION' and  pim.benef_code = '" + benefCode + "' and pim.TRANS_REF >0  and pim.USER_ID IS NOT  NULL order by pim.TRANS_REF DESC");


        Log.e("SQL", sql.toString());
        Cursor cr = db.rawQuery(sql.toString(), new String[]{lang});
        String date = "";
        if (cr.moveToFirst()) {

            do {
                SavedInterviewInfo si = new SavedInterviewInfo();
                si.setInterviewId(cr.getLong(cr.getColumnIndex("INTERVIEW_ID")));
                si.setTransRef(cr.getLong(cr.getColumnIndex("TRANS_REF")));
                si.setDate(cr.getString(cr.getColumnIndex("interviewDate")));
                si.setInputBinaryFilePathList(cr.getString(cr.getColumnIndex("FILE_PATH")));
                si.setQuestionAnswerJson(cr.getString(cr.getColumnIndex("QUESTION_ANSWER_JSON")));
                si.setQuestionJson(cr.getString(cr.getColumnIndex("QUESTIONNAIRE_JSON")));
                si.setQuestionnarieTitle(cr.getString(cr.getColumnIndex("QUESTIONNAIRE_TITLE")));
                si.setQuestionnarieName(cr.getString(cr.getColumnIndex("QUESTIONNAIRE_NAME")));
                si.setQuestionnaireId(cr.getLong(cr.getColumnIndex("QUESTIONNAIRE_ID")));
                if (!date.equals(si.getDate())) {
                    date = si.getDate();
                    si.setNewDate(true);
                }
//                        if (si.getParentInterviewId() > 0) {
//                          si.setScheduleInfo(getScheduleInfo(si.getParentInterviewId(),0));
//                      }
                interviewList.add(si);
            } while (cr.moveToNext());
        }
        cr.close();
        return interviewList;
    }


    public Beneficiary getSingleBenef(String beneCode) {

        Beneficiary beneficiary = new Beneficiary();

        String sql = " SELECT  * from  beneficiary where BENEF_CODE=? ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, new String[]{beneCode});
        if (cursor.moveToFirst()) {
            beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
            beneficiary.setBenefName(cursor.getString(cursor.getColumnIndex("BENEF_NAME")));
            String dob = cursor.getString(cursor.getColumnIndex("DOB"));
            try {
                beneficiary.setDob(dob);
                try {
                    beneficiary.setAge(Utility.getAge(dob));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
            beneficiary.setUserId(cursor.getLong(cursor.getColumnIndex("USER_ID")));
            beneficiary.setBenefCodeFull(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
            beneficiary.setEduLevel(cursor.getString(cursor.getColumnIndex("EDU_LEVEL")));
            beneficiary.setBenefId(cursor.getInt(cursor.getColumnIndex("BENEF_ID")));
            beneficiary.setBirthCertificateNumber(cursor.getString(cursor.getColumnIndex("BIRTH_REG_NUMBER")));
            beneficiary.setGuardianName(cursor.getString(cursor.getColumnIndex("GUARDIAN_NAME")));
            beneficiary.setGuardianLocalName(cursor.getString(cursor.getColumnIndex("GUARDIAN_NAME_LOCAL")));
            beneficiary.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
            beneficiary.setMaritalStatus(cursor.getString(cursor.getColumnIndex("MARITAL_STATUS")));
            beneficiary.setMobileComm(cursor.getString(cursor.getColumnIndex("MOBILE_COMM")));
            beneficiary.setMobileNumber(cursor.getString(cursor.getColumnIndex("MOBILE_NUMBER")));
            beneficiary.setNationalIdNumber(cursor.getString(cursor.getColumnIndex("NATIONAL_ID")));
            beneficiary.setAgreedMobileComm(cursor.getString(cursor.getColumnIndex("AGREED_MOBILE_COMM")));
            beneficiary.setMobileCommLang(cursor.getString(cursor.getColumnIndex("MOBILE_COMM_LANG")));
            beneficiary.setOccupation(cursor.getString(cursor.getColumnIndex("OCCUPATION")));
            beneficiary.setOccupationHerHusband(cursor.getString(cursor.getColumnIndex("OCCUPATION_HER_HUSBAND")));
            beneficiary.setRelationToGurdian(cursor.getString(cursor.getColumnIndex("RELATION_GUARDIAN")));
            beneficiary.setReligion(cursor.getString(cursor.getColumnIndex("RELIGION")));
            beneficiary.setReligionOtherSpecofic(cursor.getString(cursor.getColumnIndex("RELIGION_OTHER_SPECIFIC")));
            beneficiary.setState(cursor.getInt(cursor.getColumnIndex("STATE")));
            beneficiary.setCreateDate(cursor.getString(cursor.getColumnIndex("CREATE_DATE")));
            try {
                beneficiary.setAddress(cursor.getString(cursor.getColumnIndex("ADDRESS")));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            beneficiary = null;
        }
        cursor.close();
        return beneficiary;
    }


    public RequisitionInfo getRequisitionWithMedicineById(String status, String id) {
        RequisitionInfo medReqInfo = new RequisitionInfo();

        String sql = "SELECT " + " rm.REQ_NO REQ_NO, " + " rm.`REQ_ID` REQ_ID, " + " rm.TOTAL_PRICE_DISPATCH," + " rd.`MEDICINE_ID` MEDICINE_ID, " + " m.GENERIC_NAME GENERIC_NAME, " + " m.`TYPE` TYPE, " + " m.`BRAND_NAME` BRAND_NAME," + " m.`STRENGTH` STRENGTH ," + " m.MEASURE_UNIT MEASURE_UNIT , " + " m.UNIT_SALES_PRICE UNIT_SALES_PRICE, " + " m.UNIT_PURCHASE_PRICE UNIT_PURCHASE_PRICE, " + " rd.`QTY` MEDICINE_QTY, " + " rd.`QTY_DISPATCH` QTY_DISPATCH , rd.PRICE ," + " rd.`PRICE_DISPATCH` PRICE_DISPATCH " + " FROM medicine_requisition_master rm " + " INNER JOIN medicine_requisition_detail rd on rm.`REQ_ID` = rd.`REQ_ID` " + " INNER JOIN medicine m on rd.`MEDICINE_ID` = m.`MEDICINE_ID` " + " WHERE rm.`REQ_STATUS` IN ('" + status + "') AND rm.`REQ_ID` IN ('" + id + "')  ORDER BY m.`TYPE` , m.`BRAND_NAME` ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            medReqInfo.setRequisitionNo(cursor.getLong(cursor.getColumnIndex("REQ_NO")));
            medReqInfo.setRequisitionId(cursor.getInt(cursor.getColumnIndex("REQ_ID")));

            ArrayList<MedicineInfo> medicineList = new ArrayList<MedicineInfo>();
            do {
                MedicineInfo medicineInfo = new MedicineInfo();
                medicineInfo.setMedId(cursor.getInt(cursor.getColumnIndex("MEDICINE_ID")));


                medicineInfo.setBrandName(cursor.getString(cursor.getColumnIndex("BRAND_NAME")));
                medicineInfo.setGenericName(cursor.getString(cursor.getColumnIndex("GENERIC_NAME")));
                medicineInfo.setMedicineType(cursor.getString(cursor.getColumnIndex("TYPE")));
                medicineInfo.setStrength(cursor.getFloat(cursor.getColumnIndex("STRENGTH")));
                medicineInfo.setMeasureUnit(cursor.getString(cursor.getColumnIndex("MEASURE_UNIT")));

                medicineInfo.setRequiredQuantity(cursor.getString(cursor.getColumnIndex("MEDICINE_QTY")));
                medicineInfo.setUnitPurchasePrice(cursor.getDouble(cursor.getColumnIndex("UNIT_PURCHASE_PRICE")));
                medicineInfo.setUnitSalesPrice(cursor.getDouble(cursor.getColumnIndex("UNIT_SALES_PRICE")));
                medicineInfo.setQtyDispatch(cursor.getLong(cursor.getColumnIndex("QTY_DISPATCH")));
                medicineInfo.setPriceDispatch(cursor.getDouble(cursor.getColumnIndex("PRICE_DISPATCH")));
                medicineInfo.setPrice(cursor.getDouble(cursor.getColumnIndex("PRICE")));
                medicineList.add(medicineInfo);
            } while (cursor.moveToNext());
            medReqInfo.setMedicineList(medicineList);
        }
        cursor.close();

        return medReqInfo;
    }

    public String getAddressByLocationId(long userId) {
        String sql = "select  lo.LOCATION_NAME  as LOCATION_NAME  from user_info ui join LOCATION lo on ui.LOCATION_ID = lo.LOCATION_ID  where USER_ID=" + userId;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("LOCATION_NAME")).trim();
        }
        return "";
    }

    public String getUserName(String userCode) {
        String sql = "select  USER_NAME FROM  user_info where  USER_LOGIN_ID='" + userCode + "'";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("USER_NAME")).trim();
        }
        return "";
    }

    public String getUserName(long userId) {
        String sql = "select  USER_NAME FROM  user_info where  USER_ID=" + userId;
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("USER_NAME")).trim();
        }
        return "";
    }


    public long saveCoupleRegistration(Couple couple, ContentValues contentValues, String benefCode, String interviewType) {
        long sent = -1;
        long couId = 0;

//        String coupleId = getCoupleCodeByBenefCodeIfExist(couple.getHusbandBenefCode(), couple.getWifeBenefCode());
//
//
//        try {
//            couId = Long.parseLong(coupleId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (interviewType.equalsIgnoreCase(ActivityDataKey.COUPLE_DATA)) {
            return updateCoupleRegistration(couple);
        } else {
            try {
                Log.e("CV", contentValues.toString());
                long bid = db.insert(DBTable.COUPLE_REGISTRATION, null, contentValues);
                return bid;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }

        }


    }

    public long updateCoupleRegistration(Couple couple) {
        try {
            String sql = " UPDATE couple_registration SET  " +
                    " MARRIAGE_DATE= '" + couple.getMarriageDate() + "' " +
                    " , TT_INFO= '" + couple.getTtInfo() + "' " +
                    " , WIFE_BENEF_CODE= '" + couple.getWifeBenefCode() + "' " +
                    " , HUSBAND_BENEF_CODE= '" + couple.getHusbandBenefCode() + "' " +
                    " , LIVING_CHILDREN= '" + couple.getLivingChildren() + "' " +
                    " , AGE_FIRST_PREGNANCY= '" + couple.getAgeFirstPregnacy() + "' " +
                    " WHERE COUPLE_CODE ='" + couple.getCoupleCode() + "' and STATE = 1";
            db.execSQL(sql);
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long updateCoupleSentStatus(long coupleId, String sentStatus, int state) {
        if (coupleId > 0) {
            try {
                String sql = " UPDATE couple_registration SET  UPDATE_DATA_SENT= '" + sentStatus + "' , STATE = " + state + " WHERE COUPLE_ID =" + coupleId + " and STATE = 1";
                db.execSQL(sql);
                return 1;
            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return -1;
    }

    public void saveCoupleRegistrationList(JSONArray rows, JSONObject param) {
        long count = 0;
        try {
            db.beginTransaction();
            for (int index = 0; index < rows.length(); index++) {

                try {
                    ContentValues cv = new ContentValues();
                    JSONObject row = rows.getJSONObject(index);
                    if (row.has(Column.COUPLE_ID))
                        cv.put(Column.COUPLE_ID, row.get(Column.COUPLE_ID) + "");
                    if (row.has(Column.COUPLE_CODE))
                        cv.put(Column.COUPLE_CODE, row.get(Column.COUPLE_CODE) + "");
                    if (row.has(Column.BENEF_ID))
                        cv.put(Column.BENEF_ID, row.get(Column.BENEF_ID) + "");
                    if (row.has(Column.BENEF_CODE))
                        cv.put(Column.BENEF_CODE, row.get(Column.BENEF_CODE) + "");
                    if (row.has(Column.HUSBAND_BENEF_CODE))
                        cv.put(Column.HUSBAND_BENEF_CODE, row.get(Column.HUSBAND_BENEF_CODE) + "");
                    if (row.has(Column.WIFE_BENEF_CODE))
                        cv.put(Column.WIFE_BENEF_CODE, row.get(Column.WIFE_BENEF_CODE) + "");
                    if (row.has(Column.MARRIAGE_DATE))
                        cv.put(Column.MARRIAGE_DATE, row.get(Column.MARRIAGE_DATE) + "");
                    if (row.has(Column.TT_INFO))
                        cv.put(Column.TT_INFO, row.get(Column.TT_INFO) + "");
                    if (row.has(Column.LIVING_CHILDREN))
                        cv.put(Column.LIVING_CHILDREN, row.get(Column.LIVING_CHILDREN) + "");
                    if (row.has(Column.AGE_FIRST_PREGNANCY))
                        cv.put(Column.AGE_FIRST_PREGNANCY, row.get(Column.AGE_FIRST_PREGNANCY) + "");

                    if (row.has(Column.CREATE_DATE))
                        cv.put(Column.CREATE_DATE, row.get(Column.CREATE_DATE) + "");
                    if (row.has(Column.REG_DATE))
                        cv.put(Column.REG_DATE, row.get(Column.REG_DATE) + "");
                    if (row.has(Column.TRANS_REF))
                        cv.put(Column.TRANS_REF, row.get(Column.TRANS_REF) + "");
                    if (row.has(Column.VERSION_NO))
                        cv.put(Column.VERSION_NO, row.get(Column.VERSION_NO) + "");
                    if (row.has(Column.STATE))
                        cv.put(Column.STATE, row.get(Column.STATE) + "");

                    db.replace(DBTable.COUPLE_REGISTRATION, null, cv);
                    cv.clear();
                    count++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            updateDataVersion(DBTable.COUPLE_REGISTRATION, rows.length(), count, param, KEY.VERSION_NO_COUPLES);


            db.setTransactionSuccessful();

        } finally {
            if (db != null && db.inTransaction()) {
                db.endTransaction();
            }
        }

    }

    public String getCoupleCodeByBenefCodeIfExist(String HUSBAND_BENEF_CODE, String WIFE_BENEF_CODE) {
        String sql = "select COUPLE_CODE  from couple_registration" +
                " where HUSBAND_BENEF_CODE = '" + HUSBAND_BENEF_CODE + "' and WIFE_BENEF_CODE = '" + WIFE_BENEF_CODE + "' ";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("COUPLE_CODE"))
                    .trim();
        }
        String sql2 = "select COUPLE_CODE  from couple_registration" +
                " where  WIFE_BENEF_CODE = '" + WIFE_BENEF_CODE + "'";
        Log.e("SQL2", sql2.toString());
        Cursor cursor2 = db.rawQuery(sql2, null);
        if (cursor2.moveToFirst()) {
            return cursor2.getString(cursor2.getColumnIndex("COUPLE_CODE"))
                    .trim();
        }
        return "";
    }

    public String getCoupleIdByBenefCode(String benefCode) {
        String sql = "select MAX(COUPLE_ID)  COUPLE_ID,COUPLE_CODE  from couple_registration" +
                " where HUSBAND_BENEF_CODE = '" + benefCode + "' or WIFE_BENEF_CODE = '" + benefCode + "' and STATE =1";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("COUPLE_CODE")).trim();
        }
        return "";
    }

    public String getCoupleIdByBenefCodeIfExist(String WIFE_BENEF_CODE) {
        String sql = "select COUPLE_CODE  from couple_registration" +
                " where  WIFE_BENEF_CODE = '" + WIFE_BENEF_CODE + "'";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("COUPLE_CODE"))
                    .trim();
        }
        return "";
    }


    @SuppressLint("Range")
    public ArrayList<Couple> getCoupleBenefUnsentList() {
        ArrayList<Couple> coupleRegList = new ArrayList<Couple>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from  couple_registration   where  UPDATE_DATA_SENT = 'N'  ORDER BY substr(BENEF_CODE, -5, 3 )  ASC");
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), null);

        if (cursor.moveToFirst()) {
            do {
                Couple coupleReg = new Couple();
                coupleReg.setCoupleCode(cursor.getString(cursor.getColumnIndex("COUPLE_CODE")));
                coupleReg.setHusbandBenefCode(cursor.getString(cursor.getColumnIndex("HUSBAND_BENEF_CODE")));
                coupleReg.setWifeBenefCode(cursor.getString(cursor.getColumnIndex("WIFE_BENEF_CODE")));
                coupleReg.setBenefId(cursor.getLong(cursor.getColumnIndex("BENEF_ID")));
                coupleReg.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
                coupleRegList.add(coupleReg);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return coupleRegList;
    }

    public String getHusbandOrWifeCodeByCoupleCode(String COUPLE_CODE, String WHO) {
        if (WHO.toUpperCase().equals("HUSBAND")) {
            String sql = "select HUSBAND_BENEF_CODE   from couple_registration" +
                    " where COUPLE_CODE = '" + COUPLE_CODE + "' ";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("HUSBAND_BENEF_CODE")).trim();
            }

        } else if (WHO.toUpperCase().equals("WIFE")) {
            String sql = "select WIFE_BENEF_CODE   from couple_registration" +
                    " where COUPLE_CODE = '" + COUPLE_CODE + "' ";
            Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("WIFE_BENEF_CODE")).trim();
            }
        }


        return "ERROR";
    }

    public String getNameByBenefCode(String benefCode) {
        String sql = "select BENEF_NAME,BENEF_NAME_LOCAL  from beneficiary" +
                " where BENEF_CODE = '" + benefCode + "'  ";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
            String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
            return Utility.formatTextBylanguage(nameOriginal, nameLocal);
        }

        return "";

    }

    public String generateCoupleCodeFromHHNumber(String householdNumber) {
        String coupleCode = "";
        String sql = "SELECT '" + householdNumber + "'|| IFNULL(MAX(SUBSTR(COUPLE_CODE, 13))+1,'1')  AS COUPLE_CODE  " +
                " FROM couple_registration WHERE substr(COUPLE_CODE,1,12) ='" + householdNumber + "'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex("COUPLE_CODE")).trim();
        }
        Log.e("SQL", sql.toString());


        return coupleCode;
    }

    public ArrayList<Beneficiary> getHouseholdMemberListForCouple(String householdNumber, String gender, String ageType, int start, int end) {
        ArrayList<Beneficiary> memberList = new ArrayList<Beneficiary>();
        String sql = " SELECT substr( b.BENEF_CODE, -5, 5 )  BENEF_CODE, "
                + "  b.BENEF_CODE BENEF_CODE_FULL , "
                + "       b.BENEF_NAME BENEF_NAME, "
                + "       b.BENEF_NAME_LOCAL BENEF_NAME_LOCAL, "
                + "       b.DOB DOB, "
                + "       b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH, "
                + "       SUBSTR(b.BENEF_CODE, 1, 12) AS HH_NUMBER, "
                + "    CASE "
                + "        WHEN cup.HUSBAND_BENEF_CODE = b.BENEF_CODE THEN 1 "
                + "        WHEN cup.WIFE_BENEF_CODE = b.BENEF_CODE THEN 1 "
                + "        ELSE 0 "
                + "    END AS is_exists, "
                + "       b.GENDER GENDER " + " FROM beneficiary b  LEFT JOIN couple_registration cup ON cup.HUSBAND_BENEF_CODE = b.BENEF_CODE OR cup.WIFE_BENEF_CODE = b.BENEF_CODE"
                + " WHERE (b.HH_NUMBER='" + householdNumber.trim() + "'  AND b.STATE=1 ";
        if (gender != null) {
            sql += " AND b.GENDER='" + gender.trim() + "'";
        }


        if (ageType != null && start > -1 && end > -1) {
            if (ageType.trim().equals("day")) {
                sql += " AND round((julianday(Date('now')) - julianday(b.DOB))+1) BETWEEN "
                        + start + " AND " + end;
            } else if (ageType.trim().equals("month")) {
                sql += " AND round((julianday(Date('now')) - julianday(b.DOB))/30) BETWEEN "
                        + start + " AND " + end;
            } else if (ageType.trim().equals("year")) {
                sql += " AND cast(strftime('%Y.%m%d', 'now', 'localtime') - strftime('%Y.%m%d', b.DOB) as int) BETWEEN "
                        + start + " AND " + end;
            }
        }
        sql += " and is_exists = 0 ) ";

        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Beneficiary beneficiary = new Beneficiary();
                beneficiary.setBenefCode(cursor.getString(cursor
                        .getColumnIndex("BENEF_CODE")));
                beneficiary.setBenefCodeFull(cursor.getString(cursor
                        .getColumnIndex("BENEF_CODE_FULL")));
                beneficiary.setBenefImagePath(cursor.getString(cursor
                        .getColumnIndex("BENEF_IMAGE_PATH")));


                String nameOriginal = cursor.getString(cursor.getColumnIndex("BENEF_NAME"));
                String nameLocal = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
                beneficiary.setBenefName(Utility.formatTextBylanguage(nameOriginal, nameLocal));


                String dob = cursor.getString(cursor.getColumnIndex("DOB"));
                try {
                    beneficiary.setAge(Utility.getAge(dob));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                beneficiary.setGender(cursor.getString(cursor
                        .getColumnIndex("GENDER")));
                memberList.add(beneficiary);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return memberList;
    }

    public boolean isBeneficiaryNameExist(String beneficiaryName) {
        String sql = " SELECT UPPER(BENEF_NAME) as BENEF_NAME  FROM beneficiary WHERE UPPER(BENEF_NAME)='"
                + beneficiaryName.toUpperCase() + "' LIMIT 1";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        return cursor.getCount() > 0;
    }

    public int getQuestionnaireIDByQNameAndQDetailsName(long questionaryId, String questionDetailsName) {
        int qid = -1;
        String sql = " select QUESTION_ID FROM questionnaire_detail where QUESTIONNAIRE_ID=" + questionaryId + " and Q_NAME = ('" + questionDetailsName + "')";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            qid = cursor.getInt(cursor.getColumnIndex("QUESTION_ID"));
        }
        cursor.close();
        return qid;
    }

    /**
     * Get all session with all information.
     * author noor
     *
     * @return session  list
     */
    public ArrayList<SatelliteSessionModel> getSessionListUnsent(long userId, String dataSent) {
        ArrayList<SatelliteSessionModel> sessionList = new ArrayList<>();
        String sql = "SELECT * FROM satellite_session ss where USER_ID=" + userId + " AND SS.DATA_SENT = '" + dataSent + "' ORDER BY SAT_SESSION_DATE ";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            SatelliteSessionModel sessionModel = new SatelliteSessionModel();
            sessionModel.setSatelliteSessionId(cursor.getInt(cursor.getColumnIndex(Column.SAT_SESSION_ID)));
            sessionModel.setSatSessionDate(cursor.getLong(cursor.getColumnIndex(Column.SAT_SESSION_DATE)));
            sessionModel.setUserId(cursor.getLong(cursor.getColumnIndex(Column.USER_ID)));
            sessionModel.setSatSessionLocationId(cursor.getLong(cursor.getColumnIndex(Column.SAT_SESSION_LOCATION_ID)));
            sessionModel.setCreateDate(cursor.getLong(cursor.getColumnIndex(Column.CREATE_DATE)));
            sessionModel.setSatelliteSessionChwarDetailsList(getSatelliteSessionChar(cursor.getInt(cursor.getColumnIndex(Column.SAT_SESSION_ID))));
            sessionList.add(sessionModel);
        }
        while (cursor.moveToNext()) ;
        cursor.close();
        return sessionList;
    }


    /**
     * save satellite session
     *
     * @author noor
     */
    public void saveSatelliteSession(long satSessionDate, long userID, long satSessionLocationId, List<UserInfo> fcmlList, List<LocationModel> locationModelList, long satSessionId) {
     ContentValues cv = new ContentValues();
        cv.put(Column.DATA_SENT, "N");
        if (satSessionLocationId > 0) {
            cv.put(Column.SAT_SESSION_LOCATION_ID, satSessionLocationId);
        }

        cv.put(Column.STATE, 1);
        Date date = new Date(satSessionDate);


        // Define a date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Log.d(TAG, "saveSatelliteSession: ..........session date is "+satSessionDate+ "data exist is ... "+isExist("satellite_session", "strftime ( '%Y-%m-%d' ,SAT_SESSION_DATE / 1000 ,'unixepoch' ,'localtime' ) = " + satSessionDate + "  AND STATE = 1 AND  USER_ID=" + userID + ""));
//

        if (isExist("satellite_session ss", "strftime ( '%Y-%m-%d' ,ss.SAT_SESSION_DATE / 1000 ,'unixepoch' ,'localtime' ) = Date( '" +sdf.format(date) + " ' ) AND STATE = 1 AND  USER_ID=" + userID + "")) {
            //cv.put(Column.SAT_SESSION_ID,satSessionId);
            cv.put(Column.UPDATE_DATE, Calendar.getInstance().getTimeInMillis());
            db.update("satellite_session", cv, Column.SAT_SESSION_ID + "=?", new String[]{Long.toString(satSessionId)});
            db.delete("satellite_session_chw", "SAT_SESSION_ID=?", new String[]{Long.toString(satSessionId)});
            if (locationModelList.size() >= fcmlList.size()) {
                for (LocationModel lm : locationModelList) {
                    ContentValues cvFcmdetail = new ContentValues();
                    cvFcmdetail.put(Column.SAT_SESSION_ID, satSessionId);
                    for (UserInfo userInfo : fcmlList) {
                        if (userInfo.getLocationId() == lm.getLocationId()) {
                            cvFcmdetail.put(Column.USER_ID, userInfo.getUserId());
                        }

                    }

                    cvFcmdetail.put(Column.LOCATION_ID, lm.getLocationId());
                    db.insert("satellite_session_chw", null, cvFcmdetail);
                }
            } else {

                for (UserInfo userInfo : fcmlList) {
                    // // Insert data into detail table
                    ContentValues cvFcmdetail = new ContentValues();
                    cvFcmdetail.put(Column.SAT_SESSION_ID, satSessionId);
                    for (LocationModel lm : locationModelList) {
                        if (userInfo.getLocationId() == lm.getLocationId()) {
                            cvFcmdetail.put(Column.USER_ID, userInfo.getUserId());
                        }
                    }
                    cvFcmdetail.put(Column.LOCATION_ID, userInfo.getLocationId());
                    db.insert("satellite_session_chw", null, cvFcmdetail);
                }

            }

        } else {
            cv.put(Column.SAT_SESSION_DATE, satSessionDate);
            cv.put(Column.USER_ID, userID);
            cv.put(Column.CREATE_DATE, Calendar.getInstance().getTimeInMillis());
            long createSessionId = db.insert("satellite_session", null, cv);
            if (createSessionId > 0) {
                if (locationModelList.size() >= fcmlList.size()) {
                    for (LocationModel lm : locationModelList) {
                        ContentValues cvFcmdetail = new ContentValues();
                        cvFcmdetail.put(Column.SAT_SESSION_ID, createSessionId);
                        for (UserInfo userInfo : fcmlList) {
                            if (userInfo.getLocationId() == lm.getLocationId()) {
                                cvFcmdetail.put(Column.USER_ID, userInfo.getUserId());
                            }
                        }
                        cvFcmdetail.put(Column.LOCATION_ID, lm.getLocationId());
                        db.insert("satellite_session_chw", null, cvFcmdetail);
                    }
                } else {

                    for (UserInfo userInfo : fcmlList) {
                        // // Insert data into detail table
                        ContentValues cvFcmdetail = new ContentValues();
                        cvFcmdetail.put(Column.SAT_SESSION_ID, createSessionId);


                        for (LocationModel lm : locationModelList) {
                            if (userInfo.getLocationId() == lm.getLocationId()) {
                                cvFcmdetail.put(Column.USER_ID, userInfo.getUserId());
                            }

                        }

                        cvFcmdetail.put(Column.LOCATION_ID, userInfo.getLocationId());
                        db.insert("satellite_session_chw", null, cvFcmdetail);
                    }

                }


            }

        }
    }


    public SatelliteSessionModel getSessionDateWise(long datemilis, long userId) {
        SatelliteSessionModel sessionModel = new SatelliteSessionModel();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(datemilis);
        String formattedDate = dateFormat.format(date);
        String sql = "SELECT * FROM satellite_session ss where  strftime ( '%Y-%m-%d' , ss.SAT_SESSION_DATE/ 1000 , 'unixepoch' , 'localtime' ) = '" + formattedDate + "' AND  USER_ID=" + userId + " AND STATE = 1  ORDER BY SAT_SESSION_DATE ";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            sessionModel.setSatelliteSessionId(cursor.getInt(cursor.getColumnIndex(Column.SAT_SESSION_ID)));
            sessionModel.setSatSessionDate(cursor.getLong(cursor.getColumnIndex(Column.SAT_SESSION_DATE)));
            sessionModel.setUserId(cursor.getLong(cursor.getColumnIndex(Column.USER_ID)));
            sessionModel.setSatSessionLocationId(cursor.getLong(cursor.getColumnIndex(Column.SAT_SESSION_LOCATION_ID)));
            sessionModel.setCreateDate(cursor.getLong(cursor.getColumnIndex(Column.CREATE_DATE)));
            sessionModel.setDataSent(cursor.getString(cursor.getColumnIndex(Column.DATA_SENT)));
        }
        cursor.close();
        return sessionModel;
    }

    /**
     * Get all satelliteChwar with all information.
     * author noor
     *
     * @return satellitesessionchar list
     */

    public ArrayList<SatelliteSessionChwarModel> getSatelliteSessionChar(long satSessionId) {
        ArrayList<SatelliteSessionChwarModel> sessionChwarModelArrayList = new ArrayList<>();
        String sql = "SELECT * FROM satellite_session_chw  where   SAT_SESSION_ID= '" + satSessionId + "'";
        Cursor cursor = db.rawQuery(sql, null);
        Log.e("SQL", sql.toString());
        if (cursor.moveToFirst()) {
            do {
                SatelliteSessionChwarModel sessionModelChar = new SatelliteSessionChwarModel();
                sessionModelChar.setSAT_SESSION_CHW_ID(cursor.getInt(cursor.getColumnIndex(Column.SAT_SESSION_CHW_ID)));
                sessionModelChar.setSAT_SESSION_ID(cursor.getLong(cursor.getColumnIndex(Column.SAT_SESSION_ID)));
                sessionModelChar.setUSER_ID(cursor.getLong(cursor.getColumnIndex(Column.USER_ID)));
                sessionModelChar.setLOCATION_ID(cursor.getInt(cursor.getColumnIndex(Column.LOCATION_ID)));
                sessionChwarModelArrayList.add(sessionModelChar);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return sessionChwarModelArrayList;
    }


    /**
     * Save saveSessionList.
     */
    public void saveSessionList(ArrayList<SatelliteSessionModel> sessionModelArrayList, JSONObject param) {
        long count = 0;
        for (SatelliteSessionModel sessionModel : sessionModelArrayList) {
            ContentValues cvSessionMaster = new ContentValues();
            cvSessionMaster.put(Column.SAT_SESSION_DATE, sessionModel.getSatSessionDate());
            cvSessionMaster.put(Column.USER_ID, sessionModel.getUserId());
            cvSessionMaster.put(Column.SAT_SESSION_LOCATION_ID, sessionModel.getSatSessionLocationId());
            cvSessionMaster.put(Column.CREATE_DATE, sessionModel.getCreateDate());
            cvSessionMaster.put(Column.DATA_SENT, "Y");
            cvSessionMaster.put(Column.STATE, sessionModel.getState());
            cvSessionMaster.put(Column.VERSION_NO, sessionModel.getVersion());
            Date date = new Date(sessionModel.getSatSessionDate());
            // Define a date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String whereCond = "strftime ( '%Y-%m-%d' ,SAT_SESSION_DATE / 1000 ,'unixepoch' ,'localtime' )= Date('" + sdf.format(date)+"')";
            Log.d(TAG, "saveSessionList: .........sessionDateis   "+whereCond + "data exist is "+isExist(DBTable.SATELLITE_SESSION, whereCond));
            if (isExist(DBTable.SATELLITE_SESSION, whereCond)) {
                db.update(DBTable.SATELLITE_SESSION, cvSessionMaster, whereCond, null);
            } else {
                long satSessionId = db.insert(DBTable.SATELLITE_SESSION, null, cvSessionMaster);
                for (SatelliteSessionChwarModel sessionChwarModel : sessionModel.getSatelliteSessionChwarDetailsList()) {
                    ContentValues cvConDetails = new ContentValues();
                    cvConDetails.put(Column.SAT_SESSION_ID, satSessionId);
                    cvConDetails.put(Column.USER_ID, sessionChwarModel.getUSER_ID());
                    cvConDetails.put(Column.LOCATION_ID, sessionChwarModel.getLOCATION_ID());
                    db.insert(DBTable.SATELLITE_SESSION_CHW, null, cvConDetails);
                }
            }
            count++;
        }
        updateDataVersion(DBTable.SATELLITE_SESSION, sessionModelArrayList.size(), count, param, KEY.VERSION_NO_SESSION);
    }


    public LocationModel getLocationById(long locId) {
        LocationModel location = new LocationModel();

        String sql = "SELECT * FROM location  where  LOCATION_ID = " + locId;
        // String sql = "SELECT * FROM user_info where ORG_ID=" + orgId + "  ";
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                location.setLocationId(cursor.getLong(cursor.getColumnIndex(LocationModel.LOCATION_ID)));
                location.setLocationCode(cursor.getString(cursor.getColumnIndex(LocationModel.LOCATION_CODE)));
                location.setLocationName(cursor.getString(cursor.getColumnIndex(LocationModel.LOCATION_NAME)));
                location.setParentLocationId(cursor.getInt(cursor.getColumnIndex(LocationModel.PARENT_LOCATION_ID)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return location;
    }

    public void updateDataSentStatusForSatSession(long sessionId, String dataSentStatus) {
        ContentValues cv = new ContentValues();
        cv.put(Column.DATA_SENT, dataSentStatus);
        db.update("satellite_session", cv, Column.SAT_SESSION_ID + "=?", new String[]{Long.toString(sessionId)});
    }


    /**
     * copy Stock table data to stock_history table
     *
     * @author noor
     */
    public void saveCurrentStock() {
        String[] columns = {"LOCATION_ID", "USER_ID", "MEDICINE_ID", "CURRENT_STOCK_QTY", "LAST_UPDATE_ON"};
        String selectQuery = "SELECT " + TextUtils.join(",", columns) + " FROM medicine_stock";

        // Execute the query and get a cursor
        Cursor cursor = db.rawQuery(selectQuery, null);
        Calendar calendar = Calendar.getInstance();
        long currentTimeMillisFromCalendar = calendar.getTimeInMillis();
        while (cursor.moveToNext()) {
            ContentValues values = new ContentValues();
            for (String column : columns) {
                int columnIndex = cursor.getColumnIndex(column);
                values.put(column, cursor.getString(columnIndex));
                values.put("LAST_SAVING_ON", currentTimeMillisFromCalendar);
            }
            db.insert("medicine_stock_history", null, values);
        }
        cursor.close();
    }
    public Boolean isExitMedicineStockHistory(String currentDate) {
        boolean isExit = false;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  * FROM medicine_stock_history msh where  strftime ( '%Y-%m-%d' , msh.LAST_SAVING_ON/ 1000 , 'unixepoch' , 'localtime' ) ='"+ currentDate + "' limit 1");
        Log.e("SQL", sql.toString());
        Cursor cursor = db.rawQuery(sql.toString(), null);
        if (cursor.moveToFirst()) {
            isExit = true;
        }
        cursor.close();
        return isExit;
    }


}
