package ngo.friendship.satellite.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.model.QuestionnaireInfo;
import ngo.friendship.satellite.model.UserScheduleInfo;
import ngo.friendship.satellite.model.maternal.MaternalAbortion;
import ngo.friendship.satellite.model.maternal.MaternalBabyInfo;
import ngo.friendship.satellite.model.maternal.MaternalCareInfo;
import ngo.friendship.satellite.model.maternal.MaternalDelivery;
import ngo.friendship.satellite.model.maternal.MaternalInfo;
import ngo.friendship.satellite.model.maternal.MaternalService;
import ngo.friendship.satellite.utility.Utility;

public  class MaternalDao {
	   private SQLiteDatabase db;
	    private SatelliteCareDatabaseManager parent;
	    private static MaternalDao instance = null;
	    private MaternalDao(){}

    public static MaternalDao getInstance(SatelliteCareDatabaseManager manager) {
	      if(instance == null)instance = new MaternalDao();
	      instance.parent=manager;
	      instance.db=manager.getDb();
	      return instance;
	   }
	    
		public void addMaternalInfo(MaternalInfo beneficiary){
			String sql ="  SELECT  " +
					"  mi1.MATERNAL_ID MATERNAL_ID, " +
					"  mi1.BENEF_CODE BENEF_CODE, " +
					"  mi1.BENEF_ID BENEF_ID, " +
					"  mi1.BMI BMI, " +
					"  mi1.BMI_VALUE BMI_VALUE, " +
					"  mi1.CREATE_DATE CREATE_DATE, " +
					"  mi1.EDD EDD, " +
					"  mi1.HEIGHT_IN_CM HEIGHT_IN_CM, " +
					"  mi1.GRAVIDA GRAVIDA, " +
					"  mi1.HIGH_RISK_INTERVIEW_ID HIGH_RISK_INTERVIEW_ID, " +
					"  mi1.LMP LMP, " +
					"  mi1.MATERNAL_STATUS MATERNAL_STATUS, " +
					"  mi1.PARA PARA, " +
					"  mi1.NO_OF_RISK_ITEM NO_OF_RISK_ITEM, " +
					"  mi1.REG_INTERVIEW_ID REG_INTERVIEW_ID " +
					"  FROM maternal_info mi1 " +
					"  JOIN (SELECT MAX(MATERNAL_ID) MATERNAL_ID , BENEF_CODE   FROM maternal_info WHERE BENEF_CODE='"+beneficiary.getBenefCode()+"' AND MATERNAL_STATUS=1 ) mi2 " +
					"  ON mi1.MATERNAL_ID=mi2.MATERNAL_ID ";
	         
			Log.e("SQL(ADD MATERNAL INFO)", sql);
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.moveToFirst())
			{
				beneficiary.setMaternalId(cursor.getLong(cursor.getColumnIndex("MATERNAL_ID")));
				beneficiary.setBmi(cursor.getString(cursor.getColumnIndex("BMI")));
				beneficiary.setBmiValue(cursor.getDouble(cursor.getColumnIndex("BMI_VALUE")));
				beneficiary.setLmp(cursor.getLong(cursor.getColumnIndex("LMP")));
				beneficiary.setEdd(cursor.getLong(cursor.getColumnIndex("EDD")));
				beneficiary.setPara(cursor.getLong(cursor.getColumnIndex("PARA")));
				beneficiary.setHeightInCm(cursor.getLong(cursor.getColumnIndex("HEIGHT_IN_CM")));
				beneficiary.setMaternalStatus(cursor.getLong(cursor.getColumnIndex("MATERNAL_STATUS")));
				beneficiary.setGravida(cursor.getLong(cursor.getColumnIndex("GRAVIDA")));
				beneficiary.setNoOfRiskItem(cursor.getLong(cursor.getColumnIndex("NO_OF_RISK_ITEM")));
				beneficiary.setCreateDateMaternal(cursor.getLong(cursor.getColumnIndex("CREATE_DATE")));
				beneficiary.setHighRiskInterviewId(cursor.getLong(cursor.getColumnIndex("HIGH_RISK_INTERVIEW_ID")));
				
			}
			cursor.close();

		}

		public void addMaternalDelevery(MaternalInfo beneficiary){
			String sql =" select "
					+ " MATERNAL_DELIVERY_ID,"
					+ " MATERNAL_ID,"
					+ " MOTHER_CONDITION,"
					+ " DELIVERY_DATE, "
					+ " DELIVERY_TIME,"
					+ " DELIVERY_PLACE,"
					+ " DELIVERED_BY_CSBA ,"
					+ " DELIVERY_TYPE,"
					+ " PERSON_DELIVERED ,"
					+ " NO_OF_BABY , "
					+ " DELIVERY_CARE_INTERVIEW_ID,"
					+ " LMP "
					+ " from maternal_delivery where MATERNAL_ID="+beneficiary.getMaternalId();

			Log.e("SQL(ADD MATERNAL DELEVERY INFO)", sql);
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.moveToFirst())
			{
				MaternalDelivery delivery =new MaternalDelivery();
				delivery.setMaternalDeliveryId(cursor.getLong(cursor.getColumnIndex("MATERNAL_DELIVERY_ID")));
				delivery.setMaternalId(cursor.getLong(cursor.getColumnIndex("MATERNAL_ID")));
				delivery.setMotherCondition(cursor.getString(cursor.getColumnIndex("MOTHER_CONDITION")));
				delivery.setDeliveryDate(cursor.getLong(cursor.getColumnIndex("DELIVERY_DATE")));
				delivery.setDeliveryTime(cursor.getString(cursor.getColumnIndex("DELIVERY_TIME")));
				delivery.setDeliveryPlace(cursor.getString(cursor.getColumnIndex("DELIVERY_PLACE")));
				delivery.setDeliveredByCsba(cursor.getString(cursor.getColumnIndex("DELIVERED_BY_CSBA")));
				delivery.setDeliveryType(cursor.getString(cursor.getColumnIndex("DELIVERY_TYPE")));
				delivery.setPersonDelivered(cursor.getString(cursor.getColumnIndex("PERSON_DELIVERED")));
				delivery.setNoOfBaby(cursor.getLong(cursor.getColumnIndex("NO_OF_BABY")));
				delivery.setLmp(cursor.getLong(cursor.getColumnIndex("LMP")));
				delivery.setDeliveryCareInterviewId(cursor.getLong(cursor.getColumnIndex("DELIVERY_CARE_INTERVIEW_ID")));
				beneficiary.setMaternalDelivery(delivery);
			}
			cursor.close();
		}   
		
		
		public boolean isMaternalMotherAndBabyRegistrationComplete(long maternalId){
			String sql = " select NO_OF_BABY from maternal_delivery where MATERNAL_ID="+maternalId;
			Log.e("SQL(IS MATERNAL MOTHER AND BABY REGISTRATION COMPLETE) : ", sql);
			Cursor cursor = db.rawQuery(sql,null);
			if(cursor.moveToFirst())
			{
				if (cursor.getLong(cursor.getColumnIndex("NO_OF_BABY"))>0) return true;
			}
			return false;
		}
		
		public ArrayList<MaternalInfo> getMaternalBeneficiaryList(String fcmCode, String hhNumber)
		{
			db.execSQL(" UPDATE maternal_info set "
					+ "  MATERNAL_STATUS=0,"
					+ " MATERNAL_COMPLETE_DATE="+Calendar.getInstance().getTimeInMillis()+", "
					+ " MATERNAL_COMPLETE_SOURCE=0 "
					+ " where ((strftime('%s', 'now')-(LMP/1000))/(60*60*24))>325 and   MATERNAL_STATUS=1 ");

			ArrayList<MaternalInfo> beneficiaryList = new ArrayList<MaternalInfo>();
			String sql =     " SELECT substr( b.BENEF_CODE, -5, 5 )  BENEF_CODE_SHORT,  " +
					"       b.BENEF_CODE BENEF_CODE, "+
					"       b.BENEF_NAME BENEF_NAME,  " +
					"       b.HH_NUMBER HH_NUMBER,  " +
					"       b.BENEF_NAME_LOCAL BENEF_NAME_LOCAL,  " +
					"       b.DOB DOB,  " +
					"       b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH,  " +
					"       b.GENDER GENDER,  " +
					"       mi.MATERNAL_ID MATERNAL_ID, " +
					"       mi.MATERNAL_STATUS MATERNAL_STATUS,  " +
					"       mi.LMP LMP,  " +
					"       mi.EDD EDD,  " +
					"       mi.GRAVIDA GRAVIDA,  " +
					"       mi.PARA PARA,  " +
					"       mi.BMI_VALUE BMI_VALUE,  " +
					"       mi.BMI BMI,  " +
					"       mi.HEIGHT_IN_CM HEIGHT_IN_CM,  " +
					"       mi.CREATE_DATE CREATE_DATE,  " +
					"       mi.NO_OF_RISK_ITEM  NO_OF_RISK_ITEM " +
					"  FROM beneficiary b  " +
					"  JOIN household h on b.HH_NUMBER = h.HH_NUMBER and h.STATE=1 and h.HH_NUMBER like '"+hhNumber+"%' " +
					"       JOIN (   " +
					"       SELECT  " +
					"       m.MATERNAL_ID , " +
					"       m.BENEF_ID, " +
					"       m.BENEF_CODE,  " +
					"       m.MATERNAL_STATUS,  " +
					"       m.LMP,  " +
					"       m.EDD,  " +
					"       m.CREATE_DATE,  " +
					"       m.GRAVIDA,  " +
					"       m.HEIGHT_IN_CM,  " +
					"       m.PARA, " +
					"       m.BMI_VALUE,  " +
					"       m.BMI,  " +
					"       m.NO_OF_RISK_ITEM  " +
					"        " +
					"      FROM maternal_info m  " +
					"           INNER JOIN (   " +
					"              SELECT max( MATERNAL_ID ) MATERNAL_ID  " +
					"              FROM maternal_info WHERE MATERNAL_STATUS=1  " +
					"              GROUP BY BENEF_CODE   " +
					"        )   " +
					"        mi2  ON m.MATERNAL_ID = mi2.MATERNAL_ID  " +
					" )   " +
					" mi  " +
					"         ON mi.BENEF_CODE = b.BENEF_CODE  " +
					" WHERE b.STATE = '1' AND b.GENDER='F'  " +
					"       AND  " +
					"       substr( b.BENEF_CODE, 1, 9 ) = ?  " +
					" ORDER BY EDD ASC ";

			Log.e("SQL(MATERNAL BENEF LIST)", sql);
			Cursor cursor = db.rawQuery(sql, new String []{fcmCode});

			if(cursor.moveToFirst())
			{
				do
				{
					MaternalInfo beneficiary = new MaternalInfo();
					//beneficiary.setBenefCodeShort(cursor.getString(cursor.getColumnIndex("BENEF_CODE_SHORT")));
					beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE_SHORT")));
					beneficiary.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
					beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));
					beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
					String name = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));

					if(name != null && !name.equalsIgnoreCase("")) beneficiary.setBenefName(name);
					else beneficiary.setBenefName(cursor.getString(cursor.getColumnIndex("BENEF_NAME")));


					try {
						String dob = cursor.getString(cursor.getColumnIndex("DOB"));
						beneficiary.setAge(Utility.getAge(dob));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					beneficiary.setHeightInCm(cursor.getLong(cursor.getColumnIndex("HEIGHT_IN_CM")));
					beneficiary.setEdd(cursor.getLong(cursor.getColumnIndex("EDD")));
					beneficiary.setLmp(cursor.getLong(cursor.getColumnIndex("LMP")));
					beneficiary.setGravida(cursor.getLong(cursor.getColumnIndex("GRAVIDA")));
					beneficiary.setPara(cursor.getLong(cursor.getColumnIndex("PARA")));
					beneficiary.setMaternalId(cursor.getLong(cursor.getColumnIndex("MATERNAL_ID")));
					beneficiary.setMaternalStatus(cursor.getLong(cursor.getColumnIndex("MATERNAL_STATUS")));
					beneficiary.setBmiValue(cursor.getDouble(cursor.getColumnIndex("BMI_VALUE")));
					beneficiary.setBmi(cursor.getString(cursor.getColumnIndex("BMI")));
					beneficiary.setCreateDateMaternal(cursor.getLong(cursor.getColumnIndex("CREATE_DATE")));
					beneficiary.setNoOfRiskItem(cursor.getLong(cursor.getColumnIndex("NO_OF_RISK_ITEM")));
					beneficiaryList.add(beneficiary);
				}while(cursor.moveToNext());
			}
			cursor.close();

			for (MaternalInfo maternalInfo :beneficiaryList) {
				sql ="SELECT MIN(us.SCHED_DATE) NEXT_VISIT_DATE, us.INTERVIEW_ID INTERVIEW_ID FROM user_schedule us WHERE us.ATTENDED_DATE < 1 AND (us.TYPE = 'ANC' OR us.TYPE = 'PNC' ) AND us.BENEF_CODE='"+fcmCode+maternalInfo.getBenefCode()+"'";
				Log.e("SQL(NEXT_VISIT_DATE) ", sql);
				cursor = db.rawQuery(sql, null);

				if(cursor.moveToFirst())
				{
					ArrayList<UserScheduleInfo> userScheduleInfos=new ArrayList<UserScheduleInfo>();
					UserScheduleInfo scheduleInfo=new UserScheduleInfo();
					scheduleInfo.setScheduleDate(cursor.getLong(cursor.getColumnIndex("NEXT_VISIT_DATE")));
					scheduleInfo.setInterviewId(cursor.getLong(cursor.getColumnIndex("INTERVIEW_ID")));
					userScheduleInfos.add(scheduleInfo);
					maternalInfo.setUserScheduleInfos(userScheduleInfos);
				}
				cursor.close();
				sql =" select mc.CARE_NAME CARE_NAME  from maternal_care_info mc JOIN ( "
						+" select ms.MATERNAL_CARE_ID from maternal_service ms "
						+" JOIN (select MAX(MATERNAL_SERVICE_ID) MATERNAL_SERVICE_ID from maternal_service where LMP ="+maternalInfo.getLmp()+" AND BENEF_CODE='"+maternalInfo.getBenefCode()+"' ) "
						+" tmp ON ms.MATERNAL_SERVICE_ID=tmp.MATERNAL_SERVICE_ID) tmp2 ON mc.MATERNAL_CARE_ID=tmp2.MATERNAL_CARE_ID ";
				Log.e("GET MATERNAL CARE NAME ", sql);
				cursor = db.rawQuery(sql, null);



				if(cursor.moveToFirst())
				{
					ArrayList<MaternalService> maternalServices=new ArrayList<MaternalService>();
					maternalInfo.setLastserviceName(cursor.getString(cursor.getColumnIndex("CARE_NAME")));	
				}
				cursor.close();
				
				addMaternalDelevery(maternalInfo);
			}
			return beneficiaryList;
		}

		public void addMaternalServicInfo(MaternalInfo beneficiary){

			String sql = " SELECT  "
					+ "  q.NAME Q_NAME, "
					+ "  ms.*, "
					+ "  mc.CARE_NAME CARE_NAME "
					+ "  FROM maternal_service ms   "
					+ "  LEFT JOIN maternal_care_info mc ON mc.MATERNAL_CARE_ID = ms.MATERNAL_CARE_ID  "
					+ "  LEFT JOIN questionnaire q ON mc.QUESTIONNAIRE_ID= q.QUESTIONNAIRE_ID "
					+ "  WHERE MATERNAL_ID = ?  AND q.LANG_CODE='"+App.getContext().getAppSettings().getLanguage()+"'";

			Log.e("SQL(ADD MATERNAL SERVIC INFO)", sql);
			Cursor cursor = db.rawQuery(sql, new String []{beneficiary.getMaternalId()+""});

			if(cursor.moveToFirst())
			{
				HashMap <String , MaternalService> maternalServicesMap=new HashMap<String, MaternalService>();
				do
				{
					String qName=cursor.getString(cursor.getColumnIndex("Q_NAME"));
					MaternalService maternalService= new MaternalService();

					maternalService.setMaternalServiceId(cursor.getLong(cursor.getColumnIndex("MATERNAL_SERVICE_ID")));
					maternalService.setMaternalId(cursor.getLong(cursor.getColumnIndex("MATERNAL_ID")));
					maternalService.setBmiValue(cursor.getDouble(cursor.getColumnIndex("BMI_VALUE")));
					maternalService.setBmi(cursor.getString(cursor.getColumnIndex("BMI")));
					maternalService.setPulse(cursor.getLong(cursor.getColumnIndex("PULSE")));
					maternalService.setPulseStatus(cursor.getString(cursor.getColumnIndex("PULSE_STATUS")));
					maternalService.setBloodPressure(cursor.getString(cursor.getColumnIndex("BLOOD_PRESSURE")));
					maternalService.setBloodPressureType(cursor.getString(cursor.getColumnIndex("BLOOD_PRESSURE_TYPE")));

					maternalService.setTemperature(cursor.getDouble(cursor.getColumnIndex("TEMPERATURE")));
					maternalService.setTemperatureType(cursor.getString(cursor.getColumnIndex("TEMPERATURE_TYPE")));
					
					
					maternalService.setWeight(cursor.getDouble(cursor.getColumnIndex("WEIGHT")));
					maternalService.setWeeklyWeightGain(cursor.getDouble(cursor.getColumnIndex("WEEKLY_WEIGHT_GAIN")));
					maternalService.setAnaemia(cursor.getString(cursor.getColumnIndex("ANAEMIA")));
					maternalService.setOedema(cursor.getString(cursor.getColumnIndex("OEDEMA")));
					maternalService.setVomiting(cursor.getString(cursor.getColumnIndex("VOMITING")));
					maternalService.setJaundice(cursor.getString(cursor.getColumnIndex("JAUNDICE")));
					maternalService.setProteinOfUrine(cursor.getString(cursor.getColumnIndex("PROTEIN_OF_URINE")));
					maternalService.setJaundice(cursor.getString(cursor.getColumnIndex("JAUNDICE")));
					maternalService.setSugarOfUrine(cursor.getString(cursor.getColumnIndex("SUGAR_OF_URINE")));
					maternalService.setVomiting(cursor.getString(cursor.getColumnIndex("VOMITING")));
					maternalService.setSugarOfUrine(cursor.getString(cursor.getColumnIndex("SUGAR_OF_URINE")));
					maternalService.setHeightOfUterus(cursor.getString(cursor.getColumnIndex("HEIGHT_OF_UTERUS")));
					maternalService.setHeightInCm(cursor.getLong(cursor.getColumnIndex("HEIGHT_IN_CM")));
					maternalService.setProteinOfUrine(cursor.getString(cursor.getColumnIndex("PROTEIN_OF_URINE")));
					maternalService.setFetalMovement(cursor.getString(cursor.getColumnIndex("FETAL_MOVEMENT")));
					maternalService.setFetalMovement(cursor.getString(cursor.getColumnIndex("FETAL_MOVEMENT")));
					maternalService.setFetalHeartRate(cursor.getString(cursor.getColumnIndex("FETAL_HEART_RATE")));
					maternalService.setFetalLie(cursor.getString(cursor.getColumnIndex("FETAL_LIE")));
					maternalService.setFetalPresentation(cursor.getString(cursor.getColumnIndex("FETAL_PRESENTATION")));
					maternalService.setBreastProblem(cursor.getString(cursor.getColumnIndex("BREAST_PROBLEM")));
					maternalService.setRiskState(cursor.getString(cursor.getColumnIndex("RISK_STATE")));
					maternalService.setRiskProp(cursor.getLong(cursor.getColumnIndex("RISK_PROP")));
					maternalService.setCreateDate(cursor.getLong(cursor.getColumnIndex("CREATE_DATE")));
					maternalService.setCareName(cursor.getString(cursor.getColumnIndex("CARE_NAME")));
					maternalService.setInterviewId(cursor.getLong(cursor.getColumnIndex("INTERVIEW_ID")));
					maternalServicesMap.put(maternalService.getCareName(), maternalService);


				}while(cursor.moveToNext());
				beneficiary.setMaternalServices(maternalServicesMap);
			}
			cursor.close();
		}
	    public boolean isExist(String tableName,String whereCondision){
		    	String sql= " SELECT * FROM " +tableName+" WHERE "+ whereCondision;
		    	Log.e("SQL(IS EXIST) : ", sql);
		    	try {
		    		Cursor cursor = db.rawQuery(sql, null);
		    		if(cursor.moveToFirst())
		    		{
		    			return true;
		    		}
				} catch (Exception e) {
			
				}
				return false;
	  }
		
		
		public long saveMaternalAbortion(MaternalAbortion abortion){
			ContentValues cv = new ContentValues();
			cv.put(Column.ABORT_ID, parent.getMaxId(MaternalAbortion.MODEL_NAME, Column.ABORT_ID)+1);
			cv.put(Column.ABORT_BY_ID, abortion.getAbortById());
			cv.put(Column.ABORT_PLACE_ID, abortion.getAbortPlaceId());
			cv.put(Column.BENEF_CODE,abortion.getBenefCode());
			cv.put(Column.BENEF_ID,abortion.getBenefId());
			cv.put(Column.ABORT_DATE,abortion.getAbortDate());
			cv.put(Column.UNUSUAL_OUTCOME_TYPE,abortion.getUnusalOutcomeType());
			cv.put(Column.PREGNANCY_WEEK,abortion.getPregnancyWeek());
			cv.put(Column.INTERVIEW_ID,abortion.getInterviewId());
			if(abortion.getMaternalId()>0){
				cv.put(Column.MATERNAL_ID,abortion.getMaternalId());
			}
			Log.e("saveMaternalAbortion", cv.toString());
			return db.insert(MaternalAbortion.MODEL_NAME, null, cv);		
		}
		public void saveMaternalAbortions(ArrayList<MaternalAbortion> abortions){
			ContentValues cv = new ContentValues();
			for(MaternalAbortion abortion : abortions){
				cv.put(Column.ABORT_ID, abortion.getAbortId());
				cv.put(Column.ABORT_BY_ID, abortion.getAbortById());
				cv.put(Column.ABORT_PLACE_ID, abortion.getAbortPlaceId());
				cv.put(Column.BENEF_CODE,abortion.getBenefCode());
				cv.put(Column.BENEF_ID,abortion.getBenefId());
				cv.put(Column.UNUSUAL_OUTCOME_TYPE,abortion.getUnusalOutcomeType());
				cv.put(Column.ABORT_DATE,abortion.getAbortDate());
				cv.put(Column.PREGNANCY_WEEK,abortion.getPregnancyWeek());
				cv.put(Column.INTERVIEW_ID,abortion.getInterviewId());
				cv.put(Column.TRANS_REF,abortion.getTransRef());
				db.replace(MaternalAbortion.MODEL_NAME, null, cv);	
				cv.clear();
			}
			
		}
		public void saveMaternalBabyInfos(ArrayList<MaternalBabyInfo> babyInfos){
			ContentValues cv = new ContentValues();
			for(MaternalBabyInfo babyInfo: babyInfos){
				try{
					cv.put(Column.MATERNAL_BABY_ID, babyInfo.getMaternalBabyId());
					cv.put(Column.MATERNAL_ID,babyInfo.getMaternalId());
					cv.put(Column.BABY_STATE,babyInfo.getBabyState());
					cv.put(Column.GENDER,babyInfo.getGender());
					cv.put(Column.CHILD_BENEF_CODE,babyInfo.getChildBenefCode());
					cv.put(Column.BENEF_CODE,babyInfo.getBenefCode());
					cv.put(Column.LMP,babyInfo.getLmp());
					cv.put(Column.TRANS_REF,babyInfo.getTransRef());
					db.replace(MaternalBabyInfo.MODEL_NAME, null, cv);
					cv.clear();
				}catch (Exception e){
					e.printStackTrace();
				}
				 
			}
			
		}
		public void saveMaternalBabyInfos(MaternalBabyInfo babyInfo , String benefCode, long lmp){
			ContentValues cv = new ContentValues();
			cv.put(Column.MATERNAL_BABY_ID, parent.getMaxId(MaternalBabyInfo.MODEL_NAME,Column.MATERNAL_BABY_ID)+1);
			cv.put(Column.BABY_STATE,babyInfo.getBabyState());
			cv.put(Column.GENDER,babyInfo.getGender());
			cv.put(Column.MATERNAL_ID,babyInfo.getMaternalId());
			cv.put(Column.CHILD_BENEF_CODE,babyInfo.getBenefCode());
			cv.put(Column.BENEF_CODE,benefCode);
			cv.put(Column.LMP,lmp);
			db.insert(MaternalBabyInfo.MODEL_NAME, null, cv);
		}
		public void saveMaternalCareInfoList(ArrayList<MaternalCareInfo> rows,JSONObject param)
		{
			long count=0;
			for(MaternalCareInfo maternalCareInfo : rows)
			{
				ContentValues cv = new ContentValues();
				cv.put(Column.MATERNAL_CARE_ID, maternalCareInfo.getMaternalCareId());
				cv.put(Column.CARE_NAME, maternalCareInfo.getCareName());
				cv.put(Column.CARE_DESC, maternalCareInfo.getCareDesc());
				cv.put(Column.CARE_TYPE, maternalCareInfo.getCareType());
				cv.put(Column.SCHED_RANGE_START_DAY, maternalCareInfo.getSchedRangeStartDay());
				cv.put(Column.SCHED_RANGE_END_DAY, maternalCareInfo.getSchedRangeEndDay());
				cv.put(Column.QUESTIONNAIRE_ID, maternalCareInfo.getQuestionnaireId());
				db.replace(DBTable.MATERNAL_CARE_INFO, null, cv);
				cv.clear();
			    count++;
			}
			parent.updateDataVersion(DBTable.MATERNAL_CARE_INFO, rows.size(),count, param, KEY.VERSION_NO_MATERNAL_CARE_INFO);
		}
		public void saveMaternalDeliveries(ArrayList<MaternalDelivery> deliveries) {
			ContentValues cv = new ContentValues();
			for(MaternalDelivery  delivery: deliveries){
				try{
					cv.put(Column.MATERNAL_DELIVERY_ID, delivery.getMaternalDeliveryId());
					cv.put(Column.MATERNAL_ID, delivery.getMaternalId());
					cv.put(Column.MOTHER_CONDITION,delivery.getMotherCondition());
					cv.put(Column.DELIVERY_DATE, delivery.getDeliveryDate());
					cv.put(Column.DELIVERY_TIME, delivery.getDeliveryTime());
					cv.put(Column.DELIVERY_PLACE, delivery.getDeliveryPlace());
					cv.put(Column.DELIVERED_BY_CSBA,delivery.getDeliveredByCsba());
					cv.put(Column.DELIVERY_TYPE, delivery.getDeliveryType());
					cv.put(Column.PERSON_DELIVERED,delivery.getPersonDelivered());
					cv.put(Column.NO_OF_BABY, delivery.getNoOfBaby());
					if(delivery.getDeliveryCareInterviewId()>0){
						cv.put(Column.DELIVERY_CARE_INTERVIEW_ID,delivery.getDeliveryCareInterviewId());
					}
					cv.put(Column.LMP, delivery.getLmp());
					cv.put(Column.BENEF_CODE, delivery.getBenefCode());
					cv.put(Column.TRANS_REF,delivery.getTransRef());
					db.replace(MaternalDelivery.MODEL_NAME,null,cv);
					cv.clear();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		
		}
		public void saveMaternalDelivery(MaternalDelivery delivery){
			ContentValues cv = new ContentValues();
		
			cv.put(Column.MATERNAL_ID,delivery.getMaternalId());
			if(delivery.getMotherCondition()!=null) cv.put(Column.MOTHER_CONDITION ,delivery.getMotherCondition());
			if(delivery.getDeliveredByCsba()!=null) cv.put(Column.DELIVERED_BY_CSBA ,delivery.getDeliveredByCsba());
			if(delivery.getDeliveryPlace()!=null) cv.put(Column.DELIVERY_PLACE,delivery.getDeliveryPlace());
			if(delivery.getDeliveryDate()!=null) cv.put(Column.DELIVERY_DATE ,delivery.getDeliveryDate());
			if(delivery.getMotherCondition()!=null) cv.put(Column.MOTHER_CONDITION,delivery.getMotherCondition());
			if(delivery.getDeliveryTime()!=null) cv.put(Column.DELIVERY_TIME,delivery.getDeliveryTime() );
			if(delivery.getDeliveryType()!=null) cv.put(Column.DELIVERY_TYPE,delivery.getDeliveryType());
			if(delivery.getPersonDelivered()!=null) cv.put(Column.PERSON_DELIVERED,delivery.getPersonDelivered());
			if(delivery.getNoOfBaby()!=null) cv.put(Column.NO_OF_BABY ,delivery.getNoOfBaby());
		
			if(delivery.getDeliveryCareInterviewId()!=null &&delivery.getDeliveryCareInterviewId()>0 ) 
			{
				cv.put(Column.DELIVERY_CARE_INTERVIEW_ID,delivery.getDeliveryCareInterviewId());
			}
		
			int affectedRow = db.update(MaternalDelivery.MODEL_NAME, cv, " BENEF_CODE=? and LMP=?  ", new String []{delivery.getBenefCode(),Long.toString(delivery.getLmp())});
		
			if(affectedRow == 0){
				cv.put(Column.MATERNAL_DELIVERY_ID, parent.getMaxId(MaternalDelivery.MODEL_NAME,Column.MATERNAL_DELIVERY_ID)+1);
				cv.put(Column.LMP,delivery.getLmp());
				cv.put(Column.BENEF_CODE ,delivery.getBenefCode()); 
				db.insert(MaternalDelivery.MODEL_NAME, null, cv);	
			}
		
			Log.e(" MaternalDelivery CV : ", cv.toString());
		}
		public long saveMaternalInfo(MaternalInfo maternalInfo){
			ContentValues cv = new ContentValues();
			long matId= parent.getMaxId(MaternalInfo.MODEL_NAME,Column.MATERNAL_ID)+1;
			cv.put(Column.MATERNAL_ID,matId);
			cv.put(Column.CREATE_DATE, Calendar.getInstance().getTimeInMillis());
			cv.put(Column.GRAVIDA,maternalInfo.getGravida() );
			cv.put(Column.LMP,maternalInfo.getLmp());
			cv.put(Column.CALAULATED_LMP,maternalInfo.getCalculateLmp());
			cv.put(Column.EDD,maternalInfo.getEdd());
			cv.put(Column.PARA,maternalInfo.getPara() );
			cv.put(Column.BENEF_CODE,maternalInfo.getBenefCode());
			cv.put(Column.BENEF_ID,maternalInfo.getBenefId());
			if(maternalInfo.getRegInterviewId()>0){
				cv.put(Column.REG_INTERVIEW_ID,maternalInfo.getRegInterviewId());	
			}
			cv.put(Column.MATERNAL_STATUS,1);
			Log.e("Save Maternal Info : ", cv.toString());
			db.insert(MaternalInfo.MODEL_NAME, null, cv);
			return matId;
		}
		public long updateMaternalHighrisk(long maternalId,long riskCount ,long interviewId){
			try {
				ContentValues cv = new ContentValues();
				cv.put(Column.HIGH_RISK_INTERVIEW_ID,interviewId);
				cv.put(Column.NO_OF_RISK_ITEM,riskCount);
				return db.update(MaternalInfo.MODEL_NAME, cv, "MATERNAL_ID=?", new String []{Long.toString(maternalId)});
		
			}catch(Exception e){
				e.printStackTrace();
			}
			return 0;
		}
		public long updateMaternalInfosBmi(long  maternalId, Double bmiValues ,String  bmi,Long hight){
			ContentValues cv = new ContentValues();
			cv.put(Column.BMI_VALUE,bmiValues);
			cv.put(Column.BMI,bmi);
			cv.put(Column.HEIGHT_IN_CM,hight);
			return db.update(MaternalInfo.MODEL_NAME, cv," MATERNAL_ID=? ", new String []{Long.toString(maternalId)});
		}
		public long updateMaternalStatus(MaternalInfo  maternalInfo,long interviewID){
		
			ContentValues cv = new ContentValues();
			cv.put(Column.MATERNAL_STATUS,0);
			cv.put(Column.MATERNAL_COMPLETE_DATE,Calendar.getInstance().getTimeInMillis());
			cv.put(Column.MATERNAL_COMPLETE_SOURCE,interviewID);
			return db.update(MaternalInfo.MODEL_NAME, cv," MATERNAL_ID=? ", new String []{Long.toString(maternalInfo.getMaternalId())});
		}
		public void saveMaternalInfos(ArrayList<MaternalInfo> maternalInfos) {
			ContentValues cv = new ContentValues();
			for (MaternalInfo maternalInfo : maternalInfos) {
				try{
		
					cv.put(Column.MATERNAL_ID, maternalInfo.getMaternalId());
					cv.put(Column.BENEF_ID, maternalInfo.getBenefId());
					cv.put(Column.LMP, maternalInfo.getLmp());
					cv.put(Column.EDD, maternalInfo.getEdd());
					cv.put(Column.PARA, maternalInfo.getPara());
					cv.put(Column.GRAVIDA, maternalInfo.getGravida());
					cv.put(Column.BMI_VALUE, maternalInfo.getBmiValue());
					cv.put(Column.BMI, maternalInfo.getBmi());
					cv.put(Column.HEIGHT_IN_CM, maternalInfo.getHeightInCm());
					cv.put(Column.NO_OF_RISK_ITEM, maternalInfo.getNoOfRiskItem());
					cv.put(Column.REG_INTERVIEW_ID,maternalInfo.getRegInterviewId());
					if(maternalInfo.getHighRiskInterviewId()>0){
						cv.put(Column.HIGH_RISK_INTERVIEW_ID,maternalInfo.getHighRiskInterviewId());
					}
					cv.put(Column.MATERNAL_STATUS,maternalInfo.getMaternalStatus());
					cv.put(Column.CREATE_DATE, maternalInfo.getCreateDateMaternal());
					cv.put(Column.MATERNAL_COMPLETE_DATE, maternalInfo.getCompleteDate());
					cv.put(Column.MATERNAL_COMPLETE_SOURCE, maternalInfo.getCompleteSource());
					cv.put(Column.BENEF_CODE, maternalInfo.getBenefCode());
					cv.put(Column.TRANS_REF,maternalInfo.getTransRef());
					
					String unqueCondition=" BENEF_CODE='"+maternalInfo.getBenefCode()+"' and LMP="+maternalInfo.getLmp();
					if(isExist(MaternalInfo.MODEL_NAME, unqueCondition)){
						db.update(MaternalInfo.MODEL_NAME,cv,unqueCondition,null);
					}else{
						db.insert(MaternalInfo.MODEL_NAME, null, cv);
				    }
					cv.clear();	 
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		public void  saveMaternalService(MaternalService srvice , MaternalInfo maternalInfo){
			ContentValues cv = new ContentValues();
			cv.put(Column.MATERNAL_SERVICE_ID,parent.getMaxId(MaternalService.MODEL_NAME, Column.MATERNAL_SERVICE_ID)+1);
			cv.put(Column.MATERNAL_ID,srvice.getMaternalId());
			cv.put(Column.BMI_VALUE,srvice.getBmiValue());
			cv.put(Column.BMI,srvice.getBmi());
			cv.put(Column.PULSE,srvice.getPulse());
			cv.put(Column.BLOOD_PRESSURE,srvice.getBloodPressure());
			cv.put(Column.BLOOD_PRESSURE_TYPE,srvice.getBloodPressureType());
			cv.put(Column.TEMPERATURE,srvice.getTemperature());
			cv.put(Column.WEIGHT,srvice.getWeight());
			cv.put(Column.HEIGHT_IN_CM,srvice.getHeightInCm());
			cv.put(Column.WEEKLY_WEIGHT_GAIN,srvice.getWeeklyWeightGain());
			cv.put(Column.ANAEMIA,srvice.getAnaemia());
			cv.put(Column.JAUNDICE,srvice.getJaundice());
			cv.put(Column.OEDEMA,srvice.getOedema());
			cv.put(Column.VOMITING,srvice.getVomiting());
			cv.put(Column.SUGAR_OF_URINE,srvice.getSugarOfUrine());
			cv.put(Column.PROTEIN_OF_URINE,srvice.getProteinOfUrine());
			cv.put(Column.HEIGHT_OF_UTERUS,srvice.getHeightOfUterus());
			cv.put(Column.FETAL_MOVEMENT,srvice.getFetalMovement());
			cv.put(Column.FETAL_HEART_RATE,srvice.getFetalHeartRate());
			cv.put(Column.FETAL_LIE,srvice.getFetalLie());
			cv.put(Column.FETAL_PRESENTATION,srvice.getFetalPresentation());
			cv.put(Column.BREAST_PROBLEM,srvice.getBreastProblem());
			cv.put(Column.VITAMIN_A ,srvice.getVitaminA());
			cv.put(Column.RISK_STATE,srvice.getRiskState());
			cv.put(Column.PULSE_STATUS, srvice.getPulseStatus());
			cv.put(Column.TEMPERATURE_TYPE, srvice.getTemperatureType());
			cv.put(Column.RISK_PROP,srvice.getRiskProp());
			cv.put(Column.MATERENAL_STATUS,srvice.getMaterenalStatus());
			cv.put(Column.MATERNAL_CARE_ID,srvice.getMaternalCareId());
			cv.put(Column.INTERVIEW_ID,srvice.getInterviewId());
			cv.put(Column.CREATE_DATE,srvice.getCreateDate());
			cv.put(Column.USER_ID,srvice.getUserId());
			cv.put(Column.BENEF_CODE,maternalInfo.getBenefCode());
			cv.put(Column.LMP,maternalInfo.getLmp());
			long serviceId= db.insert("maternal_service", null, cv);
		
			if(srvice.getBmi()!=null ||srvice.getBmiValue()!=null){
				cv.clear();
				if(srvice.getBmi()!=null)
					cv.put(Column.BMI, srvice.getBmi());
				if(srvice.getBmiValue()!=null)
					cv.put(Column.BMI_VALUE,srvice.getBmiValue() );
				db.update(MaternalInfo.MODEL_NAME, cv," BENEF_CODE=? AND LMP=? ", new String []{maternalInfo.getBenefCode(),Long.toString(maternalInfo.getLmp())});
			}
		
		}
		public void saveMaternalServices(ArrayList<MaternalService> services) {
			ContentValues cv = new ContentValues();
			for (MaternalService service : services) {
		
				try{
					cv.put(Column.MATERNAL_SERVICE_ID, service.getMaternalServiceId());
					cv.put(Column.MATERNAL_ID, service.getMaternalId());
					cv.put(Column.BMI_VALUE, service.getBmiValue());
					cv.put(Column.BMI, service.getBmi());
					cv.put(Column.PULSE, service.getPulse());
					cv.put(Column.BLOOD_PRESSURE, service.getBloodPressure());
					cv.put(Column.BLOOD_PRESSURE_TYPE,service.getBloodPressureType());
					cv.put(Column.TEMPERATURE, service.getTemperature());
					cv.put(Column.WEIGHT, service.getWeight());
					cv.put(Column.WEEKLY_WEIGHT_GAIN,service.getWeeklyWeightGain());
					cv.put(Column.ANAEMIA, service.getAnaemia());
					cv.put(Column.JAUNDICE, service.getJaundice());
					cv.put(Column.OEDEMA, service.getOedema());
					cv.put(Column.VOMITING, service.getVomiting());
					cv.put(Column.SUGAR_OF_URINE, service.getSugarOfUrine());
					cv.put(Column.PROTEIN_OF_URINE, service.getProteinOfUrine());
					cv.put(Column.HEIGHT_OF_UTERUS, service.getHeightOfUterus());
					cv.put(Column.FETAL_MOVEMENT, service.getFetalMovement());
					cv.put(Column.FETAL_HEART_RATE, service.getFetalHeartRate());
					cv.put(Column.FETAL_LIE, service.getFetalLie());
					cv.put(Column.FETAL_PRESENTATION,service.getFetalPresentation());
					cv.put(Column.BREAST_PROBLEM, service.getBreastProblem());
					cv.put(Column.VITAMIN_A, service.getVitaminA());
					cv.put(Column.HEIGHT_IN_CM, service.getHeightInCm());

					cv.put(Column.RISK_STATE, service.getRiskState());
					cv.put(Column.RISK_PROP, service.getRiskProp());
					cv.put("PULSE_STATUS", service.getPulseStatus());
					cv.put("TEMPERATURE_TYPE", service.getTemperatureType());
					
					//cv.put(Column.MATERENAL_STATUS,service.getMaterenalStatus());
					cv.put(Column.MATERNAL_CARE_ID, service.getMaternalCareId());
					cv.put(Column.INTERVIEW_ID, service.getInterviewId());
					cv.put(Column.CREATE_DATE, service.getCreateDate());
					cv.put(Column.USER_ID, service.getUserId());
					cv.put(Column.BENEF_CODE, service.getBenefCode());
					cv.put(Column.LMP, service.getLmp());
					cv.put(Column.TRANS_REF,service.getTransRef());
					db.replace("maternal_service", null, cv);
					cv.clear();
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		
		}
		public long deleteMaternalAbortion(MaternalAbortion abortion){
			return db.delete(MaternalAbortion.MODEL_NAME," ABORT_DATE=? AND BENEF_CODE=? ",  new String []{Long.toString(abortion.getAbortDate() ),abortion.getBenefCode()});
		}
		public long deleteMaternalService(long id){
			return db.delete(MaternalService.MODEL_NAME ," MATERNAL_SERVICE_ID=? ",  new String []{Long.toString(id) });
		}
		public long getMaternalCareId(long questionnaireId){
			String sql ="select MATERNAL_CARE_ID from maternal_care_info where QUESTIONNAIRE_ID="+questionnaireId;
			Log.e("SQL(GET MATERNAL CARE ID) : ", sql);
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.moveToFirst())
			{
				return cursor.getLong(cursor.getColumnIndex("MATERNAL_CARE_ID"));
			}
			return -1;
		}
		public ArrayList<QuestionnaireInfo> getMaternalCareQuestionnaireList(String benefCode ,long maternalId,long createDate,String langCode )
			{ 
				ArrayList<QuestionnaireInfo> followupQuestionnaireList = new ArrayList<QuestionnaireInfo>();
				String sql =  " SELECT q2.QUESTIONNAIRE_ID  QUESTIONNAIRE_ID, " +
						"       q2.NAME  NAME, " +
						"       q2.QUESTIONNAIRE_TITLE QUESTIONNAIRE_TITLE, " +
						"       q2.QUESTIONNAIRE_JSON QUESTIONNAIRE_JSON, " +
						"       ms.MATERNAL_SERVICE_ID MATERNAL_SERVICE_ID, " +
						"       qf.SORT_ORDER SORT_ORDER,q2.ICON, " +
						"       CASE " +
						"            WHEN max( pim.INTERVIEW_ID ) = pim.INTERVIEW_ID THEN '1'  " +
						"            ELSE 0  " +
						"       END AS lastInterview " +
						"  FROM questionnaire q " +
						"       LEFT JOIN questionnaire_followup_relation qf " +
						"              ON q.QUESTIONNAIRE_ID = qf.QUESTIONNAIRE_ID " +
						"       LEFT JOIN questionnaire q2 " +
						"              ON q2.QUESTIONNAIRE_ID = qf.FOLLOWUP_QUESTIONNAIRE_ID " +
						"        " +
						"       LEFT JOIN maternal_care_info mci " +
						"              ON mci.QUESTIONNAIRE_ID = q2.QUESTIONNAIRE_ID " +
						"       LEFT JOIN maternal_service ms " +
						"              ON ms.MATERNAL_CARE_ID = mci.MATERNAL_CARE_ID  " +
						" AND ms.MATERNAL_ID = " +maternalId+
						" LEFT JOIN patient_interview_master pim " +
						"              ON pim.QUESTIONNAIRE_ID = q2.QUESTIONNAIRE_ID AND pim.BENEF_CODE = '"+benefCode+"' " +
						"               " +
						" and pim.CREATE_DATE > " +createDate+
						" WHERE q.NAME = 'MATERNAL_PREGNANT_MOTHER_REGISTRATION' and q.LANG_CODE = '"+langCode+"' " +
						"        AND       q2.LANG_CODE = '"+langCode+"' " +
						" GROUP BY q2.QUESTIONNAIRE_ID " +
						" ORDER BY qf.SORT_ORDER ";
				
				
		//		StringBuffer  sql = new StringBuffer();
		//		sql.append(" SELECT ");
		//		sql.append("  q2.QUESTIONNAIRE_ID QUESTIONNAIRE_ID, ");
		//		sql.append("  q2.NAME NAME, ");
		//		sql.append("  q2.QUESTIONNAIRE_TITLE QUESTIONNAIRE_TITLE, ");
		//		sql.append("  q2.QUESTIONNAIRE_JSON QUESTIONNAIRE_JSON, ");
		//		sql.append("  ms.MATERNAL_SERVICE_ID MATERNAL_SERVICE_ID, ");
		//		sql.append("  qf.SORT_ORDER SORT_ORDER, ");
		//		sql.append("  CASE ");
		//		sql.append("    WHEN MAX(pim.INTERVIEW_ID) = pim.INTERVIEW_ID THEN '1' ");
		//		sql.append("    ELSE 0 ");
		//		sql.append("  END AS lastInterview ");
		//		sql.append(" FROM questionnaire q ");
		//		sql.append(" LEFT JOIN questionnaire_followup_relation qf ");
		//		sql.append("  ON q.QUESTIONNAIRE_ID = qf.QUESTIONNAIRE_ID ");
		//		sql.append(" LEFT JOIN questionnaire q2 ");
		//		sql.append("  ON q2.QUESTIONNAIRE_ID = qf.FOLLOWUP_QUESTIONNAIRE_ID ");
		//		sql.append(" LEFT JOIN maternal_care_info mci ");
		//		sql.append("  ON mci.QUESTIONNAIRE_ID = q2.QUESTIONNAIRE_ID ");
		//		sql.append(" LEFT JOIN maternal_service ms ");
		//		sql.append("  ON ms.MATERNAL_CARE_ID = mci.MATERNAL_CARE_ID ");
		//		sql.append("  AND ms.MATERNAL_ID ="+maternalId);
		//		sql.append(" LEFT join maternal_info mi ");
		//		sql.append("  on mi.MATERNAL_ID = ms.MATERNAL_ID ");
		//		sql.append(" LEFT JOIN patient_interview_master pim ");
		//		sql.append("  ON pim.QUESTIONNAIRE_ID = q2.QUESTIONNAIRE_ID ");
		//		sql.append("  AND pim.BENEF_CODE = '"+benefCode+"' ");
		//		sql.append("  AND pim.TRANS_REF > mi.TRANS_REF ");
		//		sql.append(" WHERE q.NAME = 'MATERNAL_PREGNANT_MOTHER_REGISTRATION' ");
		//		sql.append(" AND q.LANG_CODE = '"+langCode+"' ");
		//		sql.append(" AND q2.LANG_CODE = '"+langCode+"' ");
		//		sql.append(" GROUP BY q2.QUESTIONNAIRE_ID ");
		//		sql.append(" ORDER BY qf.SORT_ORDER");
		
		
				Log.e("SQL(MATERNAL CARE QUESTIONNAIRE LIST) : ", sql.toString());
				Cursor cursor = db.rawQuery(sql.toString(),null);
		
				boolean isHighlightFound =true;
				if(cursor.moveToFirst())
				{
					do {
						QuestionnaireInfo qi = new QuestionnaireInfo();
						qi.setId(cursor.getLong(cursor.getColumnIndex("QUESTIONNAIRE_ID")));
						qi.setQuestionnaireName(cursor.getString(cursor.getColumnIndex("NAME")));
						qi.setQuestionnaireTitle(cursor.getString(cursor.getColumnIndex("QUESTIONNAIRE_TITLE")));
						qi.setQuestionnaireJSON(cursor.getString(cursor.getColumnIndex("QUESTIONNAIRE_JSON")));
						qi.setMaternalServiceId(cursor.getLong(cursor.getColumnIndex("MATERNAL_SERVICE_ID")));
						qi.setIcon(cursor.getString(cursor.getColumnIndex("ICON")));

						if(cursor.getInt(cursor.getColumnIndex("lastInterview"))==1){
							isHighlightFound=true;
							for(QuestionnaireInfo info: followupQuestionnaireList){
								info.setHighlight(false);
							}
						}else if(isHighlightFound){
							qi.setHighlight(true);
							isHighlightFound = false;
						}
						followupQuestionnaireList.add(qi);
		
					} while (cursor.moveToNext());
				}
		
				cursor.close();
		
				return followupQuestionnaireList;
			}
		public MaternalCareInfo getMaternelCareInfo(long questionnaireId)
		{
			MaternalCareInfo maternalCareInfo =null;
			String sql = " SELECT * FROM " 
					+ MaternalCareInfo.MODEL_NAME 
					+ " where "
					+ Column.QUESTIONNAIRE_ID +"="+questionnaireId;
			Log.e("SQL(GET MATERNEL CARE INFO):", sql);
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.moveToFirst())
			{
				maternalCareInfo = new MaternalCareInfo();
				maternalCareInfo.setMaternalCareId(cursor.getInt(cursor.getColumnIndex(Column.MATERNAL_CARE_ID)));
				maternalCareInfo.setCareName(cursor.getString(cursor.getColumnIndex(Column.CARE_NAME)));
				maternalCareInfo.setCareDesc(cursor.getString(cursor.getColumnIndex(Column.CARE_DESC)));
				maternalCareInfo.setCareType(cursor.getString(cursor.getColumnIndex(Column.CARE_TYPE)));
				maternalCareInfo.setQuestionnaireId(cursor.getLong(cursor.getColumnIndex(Column.QUESTIONNAIRE_ID)));
				maternalCareInfo.setSchedRangeStartDay(cursor.getLong(cursor.getColumnIndex(Column.SCHED_RANGE_START_DAY)));
				maternalCareInfo.setSchedRangeEndDay(cursor.getLong(cursor.getColumnIndex(Column.SCHED_RANGE_END_DAY)));	
			}
			cursor.close();
			return maternalCareInfo;
		}
		public ArrayList<MaternalCareInfo> getMaternelCareInfoList(String type)
		{
		
			ArrayList<MaternalCareInfo> maternalCareInfos = new ArrayList<MaternalCareInfo>();
		
			String sql = "SELECT * FROM " 
					+ MaternalCareInfo.MODEL_NAME 
					+ " where "
					+ Column.CARE_TYPE +"='"+type+"'";
			
			Log.e("SQL(GET MATERNEL CARE INFO LIST):", sql);
			Cursor cursor = db.rawQuery(sql, null);
		
			if(cursor.moveToFirst())
			{
				do
				{
					MaternalCareInfo maternalCareInfo = new MaternalCareInfo();
					maternalCareInfo.setMaternalCareId(cursor.getInt(cursor.getColumnIndex(Column.MATERNAL_CARE_ID)));
					maternalCareInfo.setCareName(cursor.getString(cursor.getColumnIndex(Column.CARE_NAME)));
					maternalCareInfo.setCareDesc(cursor.getString(cursor.getColumnIndex(Column.CARE_DESC)));
					maternalCareInfo.setCareType(cursor.getString(cursor.getColumnIndex(Column.CARE_TYPE)));
					maternalCareInfo.setQuestionnaireId(cursor.getLong(cursor.getColumnIndex(Column.QUESTIONNAIRE_ID)));
					maternalCareInfo.setSchedRangeStartDay(cursor.getLong(cursor.getColumnIndex(Column.SCHED_RANGE_START_DAY)));
					maternalCareInfo.setSchedRangeEndDay(cursor.getLong(cursor.getColumnIndex(Column.SCHED_RANGE_END_DAY)));	
					maternalCareInfos.add(maternalCareInfo);
				}while(cursor.moveToNext());
			}
			cursor.close();
			return maternalCareInfos;
		}
	    
		
	
}
