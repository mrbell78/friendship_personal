package ngo.friendship.satellite.storage;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.model.ImmunaizationBeneficiary;
import ngo.friendship.satellite.model.ImmunizableBeneficiary;
import ngo.friendship.satellite.model.ImmunizationFollowup;
import ngo.friendship.satellite.model.ImmunizationInfo;
import ngo.friendship.satellite.model.ImmunizationService;
import ngo.friendship.satellite.model.ImmunizationTarget;
import ngo.friendship.satellite.utility.Utility;

public  class ImmunizationDao {
	    private SQLiteDatabase db;
	    private SatelliteCareDatabaseManager parent;
	    private static ImmunizationDao instance = null;
	    private ImmunizationDao(){}

    public static ImmunizationDao getInstance(SatelliteCareDatabaseManager manager) {
	      if(instance == null)instance = new ImmunizationDao();
	      instance.parent=manager;
	      instance.db=manager.getDb();
	      return instance;
	   }
	  
	    
	

		public void saveImmunizableBeneficiaryList(ArrayList<ImmunizableBeneficiary> rows,JSONObject param)
		{
			ContentValues cv = new ContentValues();
			long count=0;
			try {
				if (db != null) {
					db.beginTransaction();
					for(ImmunizableBeneficiary beneficiary : rows)
					{
						cv.put(Column.BENEF_ID, beneficiary.getBenefId());
						cv.put(Column.BENEF_CODE, beneficiary.getBenefCode());
						cv.put(Column.AGE_DAY_WHEN_DETECTED, beneficiary.getAgeDayWhenDetected());
						cv.put(Column.CREATE_DATE, beneficiary.getCreateDate());
						cv.put(Column.IMMU_BENEF_ID, beneficiary.getImmuBenefId());
						cv.put(Column.IMMU_TYPE, beneficiary.getImmuType());
						cv.put(Column.IMMU_COMPLETE_DATE, beneficiary.getImmuCompleteDate());
						cv.put(Column.REASON_ID, beneficiary.getReasonId());
						db.replace(DBTable.IMMUNIZABLE_BENEFICIARY,null, cv);
						cv.clear();
					    count++;
					}
					
				    parent.updateDataVersion(DBTable.IMMUNIZABLE_BENEFICIARY,rows.size(),count,param, KEY.VERSION_NO_IMMUNIZABLE_BENEFICIARY);

					db.setTransactionSuccessful();
				} 
			}finally {
				if (db != null && db.inTransaction()) {
					db.endTransaction();
				}
			}
		
		
		}
		
		public void addTTInfo(ImmunaizationBeneficiary beneficiary){

			String eventSQL = "SELECT SCHED_DATE FROM user_schedule "
					+ "WHERE TYPE='TT' "
					+ "AND SCHED_DATE > strftime('%s','now')*1000 "
					+ "ORDER BY SCHED_DATE ASC LIMIT 1";
			
			Log.e("SQL(ADD TT INFO)", eventSQL);
			Cursor cursor = db.rawQuery(eventSQL, null);
			beneficiary.setNextImmunizationDateInMillis(0);
			if(cursor.moveToFirst())
			{
				beneficiary.setNextImmunizationDateInMillis(cursor.getLong(cursor.getColumnIndex("SCHED_DATE")));
			}
			cursor.close();
		}
		public void saveImmunizationFollowupList(ArrayList<ImmunizationFollowup> rows,JSONObject param)
		{
			ContentValues cv = new ContentValues();
			long count=0;
			try {
				if (db != null) {
					db.beginTransaction();
					for(ImmunizationFollowup followup : rows)
					{
						cv.put(Column.BENEF_ID, followup.getBenefId());
						cv.put(Column.BENEF_CODE, followup.getBenefCode());
						cv.put(Column.CREATE_DATE, followup.getCreateDate());
						cv.put(Column.FOLLOWUP_DATE, followup.getFollowupDate());
						cv.put(Column.FOLLOWUP_TYPE, followup.getFollowupType());
						cv.put(Column.IMMU_FOLLOWUP_ID , followup.getImmuFollowupId());
						cv.put(Column.IMMU_TYPE, followup.getImmuType());
						cv.put(Column.INTERVIEW_ID, followup.getInterviewId());
						cv.put(Column.REASON_ID, followup.getReasonId());
						cv.put(Column.TRANS_REF, followup.getTransRef());
						db.replace(DBTable.IMMUNIZATION_FOLLOWUP,null, cv);
						cv.clear();
						   count++;
					}
					parent.updateDataVersion(DBTable.IMMUNIZATION_FOLLOWUP,rows.size(),count, param, KEY.VERSION_NO_IMMUNIZATION_FOLLOWUP);
					db.setTransactionSuccessful();
				} 
			}finally {
				if (db != null && db.inTransaction()) {
					db.endTransaction();
				}
			}
		
		
		}


		public void addEPIInfo(ImmunaizationBeneficiary beneficiary){
			String eventSQL = "SELECT SCHED_DATE FROM user_schedule "
					+ " WHERE TYPE='EPI' "
					+ " AND SCHED_DATE > strftime('%s','now')*1000 "
					+ " ORDER BY SCHED_DATE ASC LIMIT 1 ";
			Log.e("SQL(ADD EPI INFO)", eventSQL);
			Cursor cursor = db.rawQuery(eventSQL, null);

			beneficiary.setNextImmunizationDateInMillis(0);
			if(cursor.moveToFirst())
			{
				beneficiary.setNextImmunizationDateInMillis(cursor.getLong(cursor.getColumnIndex("SCHED_DATE")));
			}
			cursor.close();
		}

		
		public void saveEmunizationFollowup(ImmunaizationBeneficiary beneficiary, long interviewId, String immuType)
		{
			ContentValues cv = new ContentValues();
			cv.put("BENEF_ID", beneficiary.getBenefId());
			cv.put("BENEF_CODE",beneficiary.getBenefCode());
			cv.put("INTERVIEW_ID", interviewId);
			cv.put("IMMU_TYPE",immuType);
			cv.put("FOLLOWUP_TYPE", 1); // Post epi followup
			cv.put("REASON_ID", Utility.parseLong(beneficiary.getImmunizationMissReasonName()));
			cv.put("FOLLOWUP_DATE", Calendar.getInstance().getTimeInMillis());

			int affectedRow = db.update("immunization_followup", cv, "INTERVIEW_ID=?", new String[]{Long.toString(interviewId)});

			if(affectedRow == 0)
			{
				cv.put("CREATE_DATE", Calendar.getInstance().getTimeInMillis());
				db.insert("immunization_followup", null, cv);
			}

			if(beneficiary.getNextImmunizationDateStr() != null)
			{
				try {

					long nextEpiDate = Utility.getMillisecondFromDate(beneficiary.getNextImmunizationDateStr(), Constants.DATE_FORMAT_YYYY_MM_DD);
					cv.clear();
					cv.put(Column.BENEF_CODE,beneficiary.getBenefCode());
					cv.put(Column.BENEF_ID,beneficiary.getBenefId());
					cv.put(Column.IMMU_TYPE,immuType);
					cv.put(Column.SESSION_DATE,nextEpiDate);
					db.replace(ImmunizationTarget.MODEL_NAME, null, cv);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			if(beneficiary.isComplete()){
				Calendar toDate = Calendar.getInstance();
				toDate.set(Calendar.HOUR_OF_DAY, 0);
				toDate.set(Calendar.MINUTE, 0);
				toDate.set(Calendar.SECOND, 0);
				toDate.set(Calendar.MILLISECOND, 0);
				cv.clear();
				cv.put(Column.IMMU_COMPLETE_DATE,toDate.getTimeInMillis());
				db.update(ImmunizableBeneficiary.MODEL_NAME, cv, " BENEF_CODE=? AND IMMU_TYPE=? ", new String[]{beneficiary.getBenefCode(),immuType});
			}
		}
		
		public void saveImmunizationList(ArrayList<ImmunizationInfo> rows ,JSONObject param)
		{
			 String langCode=App.getContext().getAppSettings().getLanguage();
			 ContentValues cv = new ContentValues();
			 long count=0;
			try {
				if (db != null) {
					db.beginTransaction();
					
					for(ImmunizationInfo immuInfo : rows)
					{
						cv.put("IMMU_NAME", immuInfo.getName());
						cv.put("DESC_GOVT_CARD", immuInfo.getDescFromGov());
						cv.put("DESC_ORG_CARD", immuInfo.getDescFromOrg());
						cv.put("IMMU_TYPE", immuInfo.getType());
						cv.put("START_INDEX", immuInfo.getStartIndex());
						cv.put("NO_OF_DOSE", immuInfo.getNumberOfDose());
						cv.put("LANG_CODE", langCode);
						cv.put("IMMU_ID", immuInfo.getId());
						cv.put("SORT_ORDER", immuInfo.getSortOrder());
						db.replace(DBTable.IMMUNIZATION_INFO,null, cv);
						cv.clear();
						count++;
					}

					parent.updateDataVersion(DBTable.IMMUNIZATION_INFO,rows.size(), count, param, KEY.VERSION_NO_IMMUNIZATION_INFO);
					db.setTransactionSuccessful();
				} 
			}finally {
				if (db != null && db.inTransaction()) {
					db.endTransaction();
				}
			}
		}
		/**
		 * Save epi service.
		 *
		 * @param beneficiary the epi benef
		 * @param interviewId the interview id
		 * @param immuType the immu type
		 */
		public void saveImmunizationService(ImmunaizationBeneficiary beneficiary, long interviewId, String immuType)
		{
			String benefCode = beneficiary.getBenefCode();
			long benefId = beneficiary.getBenefId();
		
			ContentValues cv = new ContentValues();
			String impids="";
			if(beneficiary!=null &&  beneficiary.getImmunizationlist()!=null ){
		
				for(ImmunizationInfo imi : beneficiary.getImmunizationlist())
				{ 
					try {
						impids=impids+","+imi.getId();
						cv.put("IMMU_DATE", imi.getTakenDateInMillis());
		
						Calendar dobCal = Calendar.getInstance();
						dobCal.setTimeInMillis(Utility.getMillisecondFromDate(beneficiary.getDob(), Constants.DATE_FORMAT_YYYY_MM_DD));
		
						Calendar immuDateCal = Calendar.getInstance();
						immuDateCal.setTimeInMillis(Utility.getMillisecondFromDate(imi.getTakenDateStr(),Constants.DATE_FORMAT_YYYY_MM_DD));
		                
						String whereCondition="IMMU_ID="+imi.getId()+" AND BENEF_CODE='"+benefCode+"'";
					    
						 if(parent.isExist("immunization_service", whereCondition)){
							 int affectedRow = db.update("immunization_service", cv,whereCondition, null);
							 cv.clear();
						 }else{
							cv.put("BENEF_CODE", benefCode);
							cv.put("BENEF_ID", benefId);
							cv.put("IMMU_ID", imi.getId());
							cv.put("IMMU_TYPE", immuType);
							cv.put("INTERVIEW_ID", interviewId);
							Log.e("SQL(SAVE IMMUNIZATION SERVICE)",cv.toString());
							db.insert("immunization_service", null, cv);
							cv.clear();
						 }
		
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		
			//  delete immu service that not contain immu service list 
			impids=impids.replaceFirst(",","");
			long deleteCount = db.delete("immunization_service", "BENEF_CODE='"+benefCode+"' AND IMMU_ID NOT IN ("+impids+") ", null);
			Log.e("DELETE IMMU COUNT : ",deleteCount+"");
			///////////////////
			if(beneficiary.getNextImmunizationDateStr() != null)
			{
				try {
					long nextEpiDate = Utility.getMillisecondFromDate(beneficiary.getNextImmunizationDateStr(), Constants.DATE_FORMAT_YYYY_MM_DD);
					cv.clear();
					cv.put(Column.BENEF_CODE,benefCode);
					cv.put(Column.BENEF_ID,benefId);
					cv.put(Column.IMMU_TYPE,immuType);
					cv.put(Column.EVENT_ID,0);
					cv.put(Column.SESSION_DATE,nextEpiDate);
					db.replace(ImmunizationTarget.MODEL_NAME, null, cv);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		
			if(beneficiary.isComplete()){
				Calendar toDate = Calendar.getInstance();
				toDate.set(Calendar.HOUR_OF_DAY, 0);
				toDate.set(Calendar.MINUTE, 0);
				toDate.set(Calendar.SECOND, 0);
				toDate.set(Calendar.MILLISECOND, 0);
				cv.clear();
				cv.put(Column.IMMU_COMPLETE_DATE,toDate.getTimeInMillis());
				int affectedRow = db.update(ImmunizableBeneficiary.MODEL_NAME, cv, " BENEF_CODE=? AND IMMU_TYPE=? ", new String[]{beneficiary.getBenefCode(),immuType});
			}
		}
		public void saveImmunizationServiceList(ArrayList<ImmunizationService> rows,JSONObject param)
		{
			ContentValues cv = new ContentValues();
			long count=0;
			try {
				if (db != null) {
					db.beginTransaction();
					for(ImmunizationService service : rows)
					{
						cv.put(Column.BENEF_ID, service.getBenefId());
						cv.put(Column.BENEF_CODE, service.getBenefCode());
						cv.put(Column.AGE_DAY_WHEN_IMMUNIZED, service.getAgeDayWhenImmunized());
						cv.put(Column.IMMU_DATE, service.getImmuDate());
						cv.put(Column.IMMU_ID, service.getImmuId());
						cv.put(Column.IMMU_SERV_ID , service.getImmuServId());
						cv.put(Column.IMMU_TYPE, service.getImmuType());
						cv.put(Column.INTERVIEW_ID, service.getInterviewId());
						cv.put(Column.TRANS_REF, service.getTransRef());
						db.replace(DBTable.IMMUNIZATION_SERVICE,null, cv);
						cv.clear();
						count++;
					}

					parent.updateDataVersion(DBTable.IMMUNIZATION_SERVICE,rows.size(),count, param, KEY.VERSION_NO_IMMUNIZATION_SERVICE);

					db.setTransactionSuccessful();
				} 
			}finally {
				if (db != null && db.inTransaction()) {
					db.endTransaction();
				}
			}
		}
		public ArrayList<ImmunaizationBeneficiary> getImmunizationBeneficiaryList(String immuType,boolean isMissing,String hhNumber)
		{
		
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		
			String sql="";
			long missingTimeMilisecond=(calendar.getTimeInMillis()-(App.getContext().getAppSettings().getImmunizationMissGapDate()*24*60*60*1000));
			if(isMissing){
				sql = " select  " +
						" b.BENEF_CODE BENEF_CODE,    " +
						" b.benef_id BENEF_ID,    " +
						" b.BENEF_NAME BENEF_NAME,   " +
						" b.BENEF_NAME_LOCAL BENEF_NAME_LOCAL,    " +
						" b.DOB DOB,    " +
						" b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH,    " +
						" b.HH_NUMBER HH_NUMBER,   " +
						" b.GENDER GENDER , " +
						" mi.LMP LMP,    " +
						" it.SESSION_DATE, " +
						" ims.IMMU_DATE, " +
						" ims.DOSE_COUNT " +
						" FROM     " +
						" ( SELECT BENEF_CODE,IMMU_TYPE,MAX( SESSION_DATE )  SESSION_DATE    " +
						"  FROM immunization_target WHERE IMMU_TYPE = '"+immuType+"' GROUP BY BENEF_CODE ) it  " +
						"  JOIN immunizable_beneficiary ib ON it.BENEF_CODE = ib.BENEF_CODE AND ib.IMMU_COMPLETE_DATE < 1   " +
						"  JOIN beneficiary b ON b.BENEF_CODE = ib.BENEF_CODE and  b.STATE='1'  "+
						"  JOIN household h on b.HH_NUMBER = h.HH_NUMBER and h.STATE=1 and h.HH_NUMBER like '"+hhNumber+"%' " +
						"  LEFT JOIN     " +
						" (SELECT IMMU_TYPE,BENEF_CODE,COUNT( IMMU_SERV_ID )  DOSE_COUNT,MAX( IMMU_DATE )  IMMU_DATE    " +
						"   FROM immunization_service WHERE IMMU_TYPE = '"+immuType+"' GROUP BY BENEF_CODE) ims  " +
						"   on it. BENEF_CODE =ims.BENEF_CODE  " +
						" LEFT JOIN ( SELECT m.LMP,m.BENEF_CODE FROM maternal_info m INNER JOIN ( SELECT max( MATERNAL_ID )  MATERNAL_ID FROM maternal_info WHERE MATERNAL_STATUS = 1 GROUP BY BENEF_CODE) mi2  ON m.MATERNAL_ID = mi2.MATERNAL_ID ) mi on b.BENEF_CODE=mi.BENEF_CODE "+
						"  where ";
		
				if(immuType.equals("EPI")){
					sql=sql+" cast(julianday()- julianday(b.DOB) as INTEGER)/30 BETWEEN "+ App.getContext().getAppSettings().getEpiMinAge()+" AND "+ App.getContext().getAppSettings().getEpiMaxAge()+" AND " ;
				}else if(immuType.equals("TT")){
					sql=sql+" cast(julianday()- julianday(b.DOB) as INTEGER)/30 " + " BETWEEN (12*"+App.getContext().getAppSettings().getTtMinAge()+") AND (12*"+App.getContext().getAppSettings().getTtMaxAge()+") AND b.GENDER='F' AND  " ;
				}
				sql=sql+"  it.SESSION_DATE <"+missingTimeMilisecond+" and  it.IMMU_TYPE = '"+immuType+"' AND b.STATE=1  GROUP BY it.BENEF_CODE ORDER BY it.SESSION_DATE DESC ";
			}else{
				sql =   " select   " +
						" b.BENEF_CODE BENEF_CODE,  " +
						" IFNULL(it.SESSION_DATE,-1) SESSION_DATE,  " +
						" b.benef_id BENEF_ID,  " +
						" b.BENEF_NAME BENEF_NAME,  " +
						" b.BENEF_NAME_LOCAL BENEF_NAME_LOCAL, " +
						" b.DOB DOB,  " +
						" b.BENEF_IMAGE_PATH BENEF_IMAGE_PATH,  " +
						" b.HH_NUMBER HH_NUMBER,  " +
						" b.GENDER GENDER,  " +
						" mi.LMP LMP, " +
						" count(im.IMMU_ID) DOSE_COUNT,  " +
						" tr.TEXT_CAPTION REASON_CAPTION " +
						" from  " +
						" ( SELECT BENEF_CODE ,IMMU_TYPE FROM immunizable_beneficiary WHERE IMMU_TYPE='"+immuType+"' AND IMMU_COMPLETE_DATE<1 )  ib " +
						" LEFT JOIN "+
						" ( SELECT BENEF_CODE,IMMU_TYPE,MAX( SESSION_DATE )  SESSION_DATE    " +
						"  FROM immunization_target WHERE IMMU_TYPE = '"+immuType+"' GROUP BY BENEF_CODE ) it  " +
					    " ON ib.BENEF_CODE=it.BENEF_CODE AND ib.IMMU_TYPE=it.IMMU_TYPE " +
						" left join immunization_service im  " +
						" on it.BENEF_CODE = im.BENEF_CODE and im.IMMU_TYPE=ib.IMMU_TYPE " +
						" left join   " +
						" (select targetFmiss.BENEF_CODE, max(targetFmiss.SESSION_DATE) as lastTargetDate  " +
						" from immunization_target targetFmiss  " +
						" where targetFmiss.SESSION_DATE < " +calendar.getTimeInMillis()+
						" group by targetFmiss.BENEF_CODE) targetFmiss  " +
						" on targetFmiss.BENEF_CODE = ib.BENEF_CODE  " +
						" LEFT JOIN immunization_followup imf on it.BENEF_CODE = imf.BENEF_CODE  " +
						" and imf.FOLLOWUP_DATE >= targetFmiss.lastTargetDate  " +
						" left join text_ref tr  " +
						" on tr.TEXT_REF_ID = imf.REASON_ID  " +
						" JOIN beneficiary b  ON b.BENEF_CODE = ib.BENEF_CODE  AND b.STATE=1  " +
						" JOIN household h on b.HH_NUMBER = h.HH_NUMBER and h.STATE=1  and h.HH_NUMBER like '"+hhNumber+"%' " +
						" LEFT JOIN ( SELECT m.LMP,m.BENEF_CODE FROM maternal_info m INNER JOIN ( SELECT max( MATERNAL_ID )  MATERNAL_ID FROM maternal_info WHERE MATERNAL_STATUS = 1 GROUP BY BENEF_CODE) mi2  ON m.MATERNAL_ID = mi2.MATERNAL_ID ) mi on b.BENEF_CODE=mi.BENEF_CODE "+
						" where ";
				if(immuType.equals("EPI")){
					sql=sql+" cast(julianday()- julianday(b.DOB) as INTEGER)/30 BETWEEN "+ App.getContext().getAppSettings().getEpiMinAge()+" AND "+ App.getContext().getAppSettings().getEpiMaxAge()+" AND " ;
				}else if(immuType.equals("TT")){
					sql=sql+" cast(julianday()- julianday(b.DOB) as INTEGER)/30 " + " BETWEEN (12*"+App.getContext().getAppSettings().getTtMinAge()+") AND (12*"+App.getContext().getAppSettings().getTtMaxAge()+") AND b.GENDER='F' AND " ;
				}
				sql=sql+ " IFNULL(it.SESSION_DATE,999999999999999) >="+missingTimeMilisecond+" and ib.IMMU_TYPE = '"+immuType+"' AND b.STATE=1 " +
						" group by ib.BENEF_CODE ORDER BY it.SESSION_DATE ASC ";
			}
		
			Log.e("SQL(IMMUNIZATION BENEFICIARY LIST)", sql);
			Cursor cursor = db.rawQuery(sql, null);
			ArrayList<ImmunaizationBeneficiary> beneficiaries =new ArrayList<ImmunaizationBeneficiary>();
			if(cursor.moveToFirst())
			{
				do
				{ 
					if(immuType.equals("TT") && cursor.getString(cursor.getColumnIndex("LMP"))!=null && cursor.getLong(cursor.getColumnIndex("LMP"))>100){
						long gap =calendar.getTimeInMillis()-cursor.getLong(cursor.getColumnIndex("LMP"));	
						gap=gap/(7*24*60*60*1000);
						if(gap<=20){
							continue;
						}
					}
		
					ImmunaizationBeneficiary beneficiary = new ImmunaizationBeneficiary();
					beneficiary.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
					beneficiary.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));
					beneficiary.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
		
					String name = cursor.getString(cursor.getColumnIndex("BENEF_NAME_LOCAL"));
		
					if(name != null && !name.equalsIgnoreCase(""))
					{
						beneficiary.setBenefName(name);
					}
					else
					{
						beneficiary.setBenefName(cursor.getString(cursor.getColumnIndex("BENEF_NAME")));
					}
		
					String dob = cursor.getString(cursor.getColumnIndex("DOB"));
					try {
						beneficiary.setAge(Utility.getAge(dob));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					beneficiary.setGender(cursor.getString(cursor.getColumnIndex("GENDER")));
					beneficiary.setCountDose(cursor.getLong(cursor.getColumnIndex("DOSE_COUNT")));
					beneficiary.setNextImmunizationDateInMillis(cursor.getLong(cursor.getColumnIndex("SESSION_DATE")));
					if(!isMissing){
						beneficiary.setImmunizationMissReasonCaption(cursor.getString(cursor.getColumnIndex("REASON_CAPTION")));
					}
		
					beneficiaries.add(beneficiary);
		
				}while(cursor.moveToNext());
			}
			cursor.close();
			return beneficiaries;
		}
		public void saveImmunizationTargetList(ArrayList<ImmunizationTarget> rows,JSONObject param)
		{
			ContentValues cv = new ContentValues();
			long count =0;
			try {
				if (db != null) {
					db.beginTransaction();
					for(ImmunizationTarget target : rows)
					{
						cv.put(Column.BENEF_ID, target.getBenefId());
						cv.put(Column.BENEF_CODE, target.getBenefCode());
						cv.put(Column.EVENT_ID, target.getEventId());
						cv.put(Column.IMMU_TYPE, target.getImmuType());
						cv.put(Column.SESSION_DATE, target.getSessionDate());
						db.replace(DBTable.IMMUNIZATION_TARGET,null, cv);
						cv.clear();
						count++;
					}

					parent.updateDataVersion(DBTable.IMMUNIZATION_TARGET,rows.size(),count, param, KEY.VERSION_NO_IMMUNIZATION_TARGET);
					db.setTransactionSuccessful();
				} 
			}finally {
				if (db != null && db.inTransaction()) {
					db.endTransaction();
				}
			}
		}
		public void updateImmunaizationTarget(Activity context,final String immuType,long date){
		
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		
			String sql = " SELECT ei.EVENT_ID,ei.EVENT_DATE FROM event_info ei "+  
					" JOIN (SELECT MIN(EVENT_DATE) EVENT_DATE , TYPE FROM  event_info WHERE STATE=1 AND EVENT_DATE > "+calendar.getTimeInMillis()+" AND TYPE='"+immuType+"' ) ei2 "+  
					" ON ei.EVENT_DATE=ei2.EVENT_DATE AND ei.TYPE=ei2.TYPE ";
		
			Log.e("SQL(UPDATE IMMUNAIZATION TARGET) : ",sql);
			Cursor cursor = db.rawQuery(sql, null);
			long id=0;
			if(cursor.moveToFirst())
			{
				date = cursor.getLong(cursor.getColumnIndex("EVENT_DATE"));
				id = cursor.getLong(cursor.getColumnIndex("EVENT_ID"));
		
			}
		
			calendar.setTimeInMillis(date);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		
		
			sql=" INSERT  INTO immunization_target (BENEF_ID,BENEF_CODE,EVENT_ID,IMMU_TYPE,SESSION_DATE) "+
					" SELECT ib.BENEF_ID , ib.`BENEF_CODE`,"+id+",'"+immuType+"',"+date+" FROM `immunizable_beneficiary` ib "+
					" LEFT JOIN `immunization_target`  it ON ib.`BENEF_ID`= it.`BENEF_ID` AND  ib.IMMU_TYPE=it.IMMU_TYPE "+ 
					" WHERE  ib.IMMU_TYPE='"+immuType+"' AND it.`BENEF_ID` IS NULL AND ib.`IMMU_COMPLETE_DATE`<1";		
		
			db.execSQL(sql);
		}
		public void updateImmunizableBeneficiary(String immuType)
		{   
			try{
				String sql="";
				if(immuType.equals("EPI")){
					sql=" INSERT INTO immunizable_beneficiary ( BENEF_ID ,BENEF_CODE,CREATE_DATE,AGE_DAY_WHEN_DETECTED,IMMU_TYPE,IMMU_COMPLETE_DATE) " +
							" SELECT b.BENEF_ID,b.BENEF_CODE,date('now'), cast(julianday()- julianday(b.DOB) as INTEGER) DEF_DOB ,'"+immuType+"' , 0 " +
							" FROM beneficiary b WHERE cast(julianday()- julianday(DOB) as INTEGER)/30 BETWEEN "
							+ App.getContext().getAppSettings().getEpiMinAge()+" AND "
							+ App.getContext().getAppSettings().getEpiMaxAge()+" AND " +
							" b.BENEF_CODE NOT IN (SELECT  BENEF_CODE FROM  immunizable_beneficiary where IMMU_TYPE='"+immuType+"' ) " ;
				}
				else if(immuType.equals("TT")){
					sql=" INSERT INTO immunizable_beneficiary ( BENEF_ID ,BENEF_CODE,CREATE_DATE,AGE_DAY_WHEN_DETECTED,IMMU_TYPE,IMMU_COMPLETE_DATE)   " +
							" SELECT b.BENEF_ID,b.BENEF_CODE,date('now'), cast(julianday()- julianday(b.DOB) as INTEGER) DEF_DOB ,'"+immuType+"' ,0  " +
							" FROM beneficiary b WHERE  cast(julianday()- julianday(DOB) as INTEGER)/30 "
							+ " BETWEEN (12*"+App.getContext().getAppSettings().getTtMinAge()+") AND"
							+ " (12*"+App.getContext().getAppSettings().getTtMaxAge()+") " +
							" AND b.GENDER='F' AND b.BENEF_CODE NOT IN (SELECT  BENEF_CODE FROM  immunizable_beneficiary  where IMMU_TYPE='"+immuType+"')" ; 
				}
				Log.e("updateImmunizableBeneficiary",sql);
				db.execSQL(sql);
			}
			catch (Exception exception){
				exception.printStackTrace();
			}
		}
	    
}
