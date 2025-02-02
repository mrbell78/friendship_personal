package ngo.friendship.satellite.storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.DBTable;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.error.MhealthException;
import ngo.friendship.satellite.jsonoperation.JSONParser;
import ngo.friendship.satellite.model.CCSBeneficiary;
import ngo.friendship.satellite.model.CCSStatus;
import ngo.friendship.satellite.utility.Utility;

public  class CcsDao {
	    private SQLiteDatabase db;
	    private SatelliteCareDatabaseManager parent;
	    private static CcsDao instance = null;
	    private CcsDao(){}

    public static CcsDao getInstance(SatelliteCareDatabaseManager manager) {
	      if(instance == null)instance = new CcsDao();
	      instance.parent=manager;
	      instance.db=manager.getDb();
	      return instance;
	   }
	  
		
		public void updateCCSEligableList(){

			String minAge="30";
			String maxAge="60";
			try {
				 minAge=JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(),"CCS","min_age");
				 maxAge=JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(),"CCS","max_age");
				
			 } catch (MhealthException e) {
				e.printStackTrace();
			}
			
			try{
				
				StringBuffer  sql = new StringBuffer();
				sql.append(" INSERT INTO ccs_eligible ");
				sql.append("            (benef_id, ");
				sql.append("             benef_code, ");
				sql.append("             age_when_detected, ");
				sql.append("             ELIGIBLE_DATE, ");
				sql.append("             create_date) ");
				sql.append(" SELECT b.benef_id, ");
				sql.append("       b.benef_code, ");
				sql.append("       Cast(Strftime('%Y.%m%d', 'now', 'localtime') - Strftime('%Y.%m%d', b.dob) ");
				sql.append("            AS INT ");
				sql.append("       )    AS AGE, ");
				sql.append("      Strftime('%s', Date('now')) * 1000 AS CURREN_TIME, Strftime('%s', 'now') * 1000 AS CURREN_TIME_MILI ");
				sql.append(" FROM   beneficiary b ");
				sql.append("       LEFT JOIN (SELECT benef_code ");
				sql.append("                  FROM   ccs_eligible ");
				sql.append("                  GROUP  BY benef_code) e ");
				sql.append("              ON b.benef_code = e.benef_code ");
				sql.append(" WHERE  e.benef_code IS NULL ");
				sql.append("       AND gender = 'F' ");
				sql.append("       AND Cast(Strftime('%Y.%m%d', 'now', 'localtime') - ");
				sql.append("                Strftime('%Y.%m%d', dob) AS INT) ");
				sql.append("           BETWEEN "+minAge+" AND "+maxAge);
				
				Log.e("Update CCS Eligable ",sql.toString());
				db.execSQL(sql.toString());
			}
			catch (Exception exception){
				exception.printStackTrace();
			}
		}
		
		
		public void updateCCSHospitalMissingList(){

		
			try{
				String missing_status_name=JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(),"CCS","hospital.schedule.missing.status.name");
				StringBuffer  sql = new StringBuffer();
				sql.append(" UPDATE ccs_treatment ");
				sql.append(" SET  CCS_STATUS_ID = (select STATUS_ID from ccs_status where STATUS_NAME ='"+missing_status_name+"') ");
				sql.append(" WHERE CCS_TR_ID IN (SELECT  ctf1.CCS_TR_ID from ccs_treatment_followup ctf1 ");
				sql.append(" JOIN  (select MAX(FOLLOWUP_ID) FOLLOWUP_ID  FROM   `ccs_treatment_followup`  GROUP  BY CCS_TR_ID ) ctf2 ON ctf1.FOLLOWUP_ID=ctf2.FOLLOWUP_ID ");
				sql.append(" JOIN (SELECT Max(ccs_tr_id) CCS_TR_ID,ELIGIBLE_ID FROM  ccs_treatment  GROUP  BY ELIGIBLE_ID) ct  ON  ct.CCS_TR_ID=ctf1.CCS_TR_ID ");
				sql.append(" JOIN (select * from ccs_eligible where ACTIVITY_STATE=1 and  (Strftime('%s','now')*1000)> ELIGIBLE_DATE ");
				sql.append("        AND (Strftime('%s','now')*1000) > coalesce(ACTIVITY_START_DATE,0) GROUP BY BENEF_CODE   )  ce ");
				sql.append(" ON ce.ELIGIBLE_ID=ct.ELIGIBLE_ID ");
				sql.append(" WHERE ctf1.hospital_going_date>0  AND ( strftime('%s',date( ctf1.hospital_going_date/1000, 'unixepoch','localtime'))- strftime('%s',Date('now'))) < 0)");
				
				Log.e("Update CCS Eligable ",sql.toString());
				db.execSQL(sql.toString());
			}
			catch (Exception exception){
				exception.printStackTrace();
			}
		}
		
		public Map<String, ArrayList<CCSBeneficiary>>  getCCSEligibleList(String hhNumber)
		{	
			Map<String, ArrayList<CCSBeneficiary>> map=new HashMap<String, ArrayList<CCSBeneficiary>>();
			map.put("UNDER_TREATMENT", new ArrayList<CCSBeneficiary>());
			map.put("NO_TREATMENT", new ArrayList<CCSBeneficiary>());
			map.put("HOSPITAL_GOING_DATE", new ArrayList<CCSBeneficiary>());
			
			int beforeDay=15;
			int afterDay=15;
			String followupDetectionFlag="";
			try {
				  
				 beforeDay=Integer.parseInt(JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(),"CCS","before_day"));
				 afterDay=Integer.parseInt( JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(),"CCS","after_day"));
				 followupDetectionFlag=JSONParser.getFcmConfigValue(App.getContext().getAppSettings().getFcmConfigrationJsonArray(),"CCS","followup.detection.flag");
			} catch (MhealthException e) {
				e.printStackTrace();
			}
	             
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT ");
			sql.append("       ce.ELIGIBLE_ID, ");
			sql.append("       COALESCE(ce.ACTIVITY_START_DATE,0) ACTIVITY_START_DATE, ");
			sql.append("       ce.ACTIVITY_STATE, ");
			sql.append("       CASE WHEN  length(trim(b.benef_name_local))>0 THEN  b.benef_name_local  ELSE b.benef_name  END   BENEF_NAME, ");
			sql.append("       b.benef_code               BENEF_CODE, ");
			sql.append("       Substr(b.hh_number, -3, 3) HH_NUMBER, ");
			sql.append("       b.dob                      DOB, ");
			sql.append("       b.benef_image_path         BENEF_IMAGE_PATH, ");
			sql.append("       Cast(Strftime('%Y.%m%d', 'now', 'localtime') - Strftime('%Y.%m%d', b.dob) AS INT ) AGE, ");
			sql.append("       COALESCE( ct.ccs_tr_id ,0)  CCS_TR_ID, ");
			sql.append("       ct.next_followup_date      NEXT_FOLLOWUP_DATE, ");
			sql.append("       COALESCE(ct.treatment_progress,0) TREATMENT_PROGRESS, ");
			sql.append("       ctf.NUMBER_OF_FCM_VISIT        NUMBER_OF_FCM_VISIT, ");
			sql.append("       ctf.NUMBER_OF_FOLLOWUP_VISIT        NUMBER_OF_FOLLOWUP_VISIT, ");
			sql.append("       ctf.followup_id       FOLLOWUP_ID, ");
			sql.append("       ctf.OVER_CAPACITY_FLAG       OVER_CAPACITY_FLAG, ");
			sql.append("       ctf.hospital_going_date    HOSPITAL_GOING_DATE, ");
			sql.append("       rfc.REF_CENTER_NAME_CAPTION    REF_CENTER_NAME_CAPTION, ");
			sql.append("   	  CASE WHEN  ctf.hospital_going_date>0  AND ( strftime('%s',date( ctf.hospital_going_date/1000, 'unixepoch','localtime'))- strftime('%s',Date('now')))>=0 THEN 1 ELSE 0 END  REMAING_TIME,");
			sql.append("       ctf.reason_id  REASON_ID , ");
			sql.append("       cs.STATUS_CAPTION STATUS_CAPTION ");
			sql.append(" FROM  (select * from ccs_eligible where ACTIVITY_STATE=1 and  (Strftime('%s','now')*1000)> ELIGIBLE_DATE AND (Strftime('%s','now')*1000) > coalesce(ACTIVITY_START_DATE,0) GROUP BY BENEF_CODE   )  ce ");
			sql.append(" JOIN beneficiary b ON ce.BENEF_CODE=b.BENEF_CODE  AND b.STATE=1 AND b.GENDER='F' ");
			sql.append(" JOIN household h on b.HH_NUMBER = h.HH_NUMBER and h.STATE=1 and h.HH_NUMBER like '"+hhNumber+"%' ");
			sql.append(" LEFT JOIN (SELECT ct1.* ");
			sql.append("                  FROM   ccs_treatment ct1 ");
			sql.append("                         JOIN (SELECT Max(ccs_tr_id) CCS_TR_ID ");
			sql.append("                               FROM   ccs_treatment ");
			sql.append("                               GROUP  BY ELIGIBLE_ID) ct2 ");
			sql.append("                           ON ct1.ccs_tr_id = ct2.ccs_tr_id) ct ");
			sql.append("              ON ce.ELIGIBLE_ID = ct.ELIGIBLE_ID ");
			sql.append("       LEFT JOIN (SELECT ctf1.* , ctf2.NUMBER_OF_FOLLOWUP_VISIT,ctf2.NUMBER_OF_FCM_VISIT  FROM   ccs_treatment_followup ctf1 ");
			sql.append(" JOIN (SELECT MAX(FOLLOWUP_ID) FOLLOWUP_ID , SUM(CASE  WHEN `FOLLOWUP_VISIT`  in ("+followupDetectionFlag.replace("#", ",")+")  THEN 1 ELSE 0 END) NUMBER_OF_FOLLOWUP_VISIT,SUM(CASE  WHEN `FOLLOWUP_VISIT`=2  THEN 1 ELSE 0 END) NUMBER_OF_FCM_VISIT   FROM   `ccs_treatment_followup` GROUP  BY CCS_TR_ID ) ctf2  ");
			sql.append(" ON ctf1.FOLLOWUP_ID = ctf2.FOLLOWUP_ID) ctf  ON ct.ccs_tr_id = ctf.ccs_tr_id ");
			sql.append("       LEFT JOIN ccs_status cs ON ct.CCS_STATUS_ID = cs.STATUS_ID ");
			sql.append("       LEFT JOIN referral_center rfc ON rfc.REF_CENTER_ID = ct.REF_CENTER_ID ");
			sql.append("      GROUP BY ce.BENEF_CODE ");
			sql.append(" ORDER BY ");
			sql.append(" CASE ");
			sql.append(" WHEN REMAING_TIME>0 THEN REMAING_TIME  ");
			sql.append(" WHEN ACTIVITY_START_DATE>0 THEN NEXT_FOLLOWUP_DATE ");
			sql.append(" ELSE b.BENEF_CODE  ");
			sql.append(" END   ASC " );


			Log.e("SQL(GET CCS ELIGIBLE LIST) : ", sql.toString());
			Cursor cursor = db.rawQuery(sql.toString(), null);

			if(cursor.moveToFirst())
			{
				do {
					CCSBeneficiary ccsInfo = new CCSBeneficiary();
					
					ccsInfo.setEligibleId(cursor.getLong(cursor.getColumnIndex("ELIGIBLE_ID")));
					ccsInfo.setActivityStartDate(cursor.getLong(cursor.getColumnIndex("ACTIVITY_START_DATE")));
					ccsInfo.setActivityState(cursor.getLong(cursor.getColumnIndex("ACTIVITY_STATE")));
					
					ccsInfo.setBenefCode(cursor.getString(cursor.getColumnIndex("BENEF_CODE")));
					ccsInfo.setHhNumber(cursor.getString(cursor.getColumnIndex("HH_NUMBER")));
					ccsInfo.setBenefImagePath(cursor.getString(cursor.getColumnIndex("BENEF_IMAGE_PATH")));
					ccsInfo.setAge(cursor.getString(cursor.getColumnIndex("AGE"))+"y");
					ccsInfo.setDob(cursor.getString(cursor.getColumnIndex("DOB")));
					ccsInfo.setBenefName(cursor.getString(cursor.getColumnIndex("BENEF_NAME")));
					
					 long followupDate = cursor.getLong(cursor.getColumnIndex("NEXT_FOLLOWUP_DATE"));
					 
		             if(followupDate>0){
		                
		            	Calendar fupDate= Calendar.getInstance();
		            	fupDate.setTimeInMillis(followupDate);
		            	fupDate.set(Calendar.HOUR_OF_DAY, 0);
		            	fupDate.set(Calendar.MINUTE, 0);
		            	fupDate.set(Calendar.SECOND, 0);
		            	fupDate.set(Calendar.MILLISECOND, 0);
		            	
		            	
		 				Calendar currentDate = Calendar.getInstance();
		 				currentDate.set(Calendar.HOUR_OF_DAY, 0);
		 				currentDate.set(Calendar.MINUTE, 0);
		 				currentDate.set(Calendar.SECOND, 0);
		 				currentDate.set(Calendar.MILLISECOND, 0);
		 				
		 				Calendar fromDate = Calendar.getInstance();
		 				fromDate.set(Calendar.HOUR_OF_DAY, 0);
		 				fromDate.set(Calendar.MINUTE, 0);
		 				fromDate.set(Calendar.SECOND, 0);
		 				fromDate.set(Calendar.MILLISECOND, 0);
		 				fromDate.add(Calendar.DAY_OF_YEAR,-beforeDay);
		 				
		 				
		 				Calendar toDate = Calendar.getInstance();
		 				toDate.set(Calendar.HOUR_OF_DAY, 0);
		 				toDate.set(Calendar.MINUTE, 0);
		 				toDate.set(Calendar.SECOND, 0);
		 				toDate.set(Calendar.MILLISECOND, 0);
		 				toDate.add(Calendar.DAY_OF_YEAR, afterDay);
		 				
		 				if(fupDate.getTimeInMillis()>=fromDate.getTimeInMillis() && fupDate.getTimeInMillis()<=toDate.getTimeInMillis()){
		 					ccsInfo.setNextFollowupDate(followupDate);
		 					ccsInfo.setRemainingDaysTOFollowup(Utility.getRemainingDays(currentDate,fupDate)); 
		 				}
		             }

					ccsInfo.setFollowupReason(cursor.getString(cursor.getColumnIndex("STATUS_CAPTION")));
					
					
						
					ccsInfo.setReferralCenterName(cursor.getString(cursor.getColumnIndex("REF_CENTER_NAME_CAPTION")));
					ccsInfo.setNumberOfFcmVisit(cursor.getInt(cursor.getColumnIndex("NUMBER_OF_FCM_VISIT")));
					ccsInfo.setNumberOfFollowupVisit(cursor.getInt(cursor.getColumnIndex("NUMBER_OF_FOLLOWUP_VISIT")));
					ccsInfo.setOverCapacity(cursor.getLong(cursor.getColumnIndex("OVER_CAPACITY_FLAG")));

	
					String reasonIdList = cursor.getString(cursor.getColumnIndex("REASON_ID"));
					if(reasonIdList != null)
					{
						String reasonSql = "SELECT TEXT_CAPTION FROM text_ref WHERE TEXT_REF_ID IN ("+reasonIdList+")";
						Log.e("SQL(GET REASON CAPTION)", reasonSql);
						Cursor reasonCur = db.rawQuery(reasonSql, null);

						if(reasonCur.moveToFirst())
						{
							StringBuilder sbReasonCaption = new StringBuilder();
							int i=0;
							do {
								if(i>0)
									sbReasonCaption.append(", ");
								sbReasonCaption.append(reasonCur.getString(reasonCur.getColumnIndex("TEXT_CAPTION")));
								i++;
							} while (reasonCur.moveToNext());

							ccsInfo.setReasonForNotToDoTest(sbReasonCaption.toString());
						}

					}

					if(ccsInfo.getActivityStartDate()>0 ){
						if(ccsInfo.getNextFollowupDate()>0){
							 map.get("UNDER_TREATMENT").add(ccsInfo);
						}
					}else{
					  map.get("NO_TREATMENT").add(ccsInfo);
					}
					
					
					if(cursor.getLong(cursor.getColumnIndex("REMAING_TIME"))==1){
						  ccsInfo.setComittedDateToGoHospital(Utility.getDateFromMillisecond(cursor.getLong(cursor.getColumnIndex("HOSPITAL_GOING_DATE")), Constants.DATE_FORMAT_DD_MM_YY));
						  map.get("HOSPITAL_GOING_DATE").add(ccsInfo);
				    }
					
				} while (cursor.moveToNext());
			}
			cursor.close();
			return map;
		}
		
		public long getCcsTrId(String benefCode,long eligibleId,long statusId){
			long CcsTrId = -1;
			String sql = " select MAX(CCS_TR_ID) CCS_TR_ID from ccs_treatment WHERE ELIGIBLE_ID="+eligibleId+" AND BENEF_CODE='"+benefCode+"' AND  CCS_STATUS_ID="+statusId ;
			Log.e("SQL(GET CCS CCS_TR_ID) : ", sql);
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.moveToFirst())
			{
				CcsTrId = cursor.getLong(cursor.getColumnIndex("CCS_TR_ID"));
			}
			return CcsTrId;
		}
		
		
		public long getCcsTrId(long eligibleId,long index){
			long CcsTrId = -1;
			String sql = " select CCS_TR_ID from ccs_treatment WHERE ELIGIBLE_ID="+eligibleId+" ORDER BY CCS_TR_ID DESC LIMIT "+index+",1 "  ;
			Log.e("SQL(GET CCS CCS_TR_ID) : ", sql);
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.moveToFirst())
			{
				CcsTrId = cursor.getLong(cursor.getColumnIndex("CCS_TR_ID"));
			}
			return CcsTrId;
		}

		public void addCCSInfo(CCSBeneficiary beneficiary){
			StringBuffer  varname1 = new StringBuffer();
			varname1.append(" SELECT ce.eligible_id ELIGIBLE_ID, ");
			varname1.append("        COALESCE(ct.ccs_tr_id, 0) CCS_TR_ID, ");
			varname1.append("        ctf.number_of_visit       NUMBER_OF_VISIT, ");
			varname1.append("        ctf.hospital_going_date   HOSPITAL_GOING_DATE, ct.NEXT_FOLLOWUP_DATE NEXT_FOLLOWUP_DATE , ");
			varname1.append("        cs.status_id      STATUS_ID, ");
			varname1.append("       ctf.OVER_CAPACITY_FLAG       OVER_CAPACITY_FLAG, ");
			varname1.append("        cs.status_name    STATUS_NAME, ");
			varname1.append("        cs.status_caption STATUS_CAPTION ");
			varname1.append(" FROM   (SELECT * ");
			varname1.append("        FROM   ccs_eligible ");
			varname1.append("        WHERE  activity_state = 1 AND BENEF_CODE='"+beneficiary.getBenefCode()+"') ce ");
			varname1.append("        LEFT JOIN (SELECT ct1.* ");
			varname1.append("                  FROM   ccs_treatment ct1 ");
			varname1.append("                         JOIN (SELECT Max(ccs_tr_id) CCS_TR_ID ");
			varname1.append("                               FROM   ccs_treatment ");
			varname1.append("                               GROUP  BY eligible_id) ct2 ");
			varname1.append("                           ON ct1.ccs_tr_id = ct2.ccs_tr_id) ct ");
			varname1.append("              ON ce.eligible_id = ct.eligible_id ");
			varname1.append("       LEFT JOIN (SELECT ctf1.*, ");
			varname1.append("                         ctf2.number_of_visit ");
			varname1.append("                  FROM   ccs_treatment_followup ctf1 ");
			varname1.append("                         JOIN (SELECT Max(followup_id)   FOLLOWUP_ID, ");
			varname1.append("                                      Count(followup_id) NUMBER_OF_VISIT ");
			varname1.append("                               FROM   ccs_treatment_followup ");
			varname1.append("                               GROUP  BY ccs_tr_id) ctf2 ");
			varname1.append("                           ON ctf1.followup_id = ctf2.followup_id) ctf ");
			varname1.append("              ON ct.ccs_tr_id = ctf.ccs_tr_id ");
			varname1.append("       LEFT JOIN ccs_status cs ");
			varname1.append("              ON ct.ccs_status_id = cs.status_id ");
			Log.e("SQL(addCCSInfo)", varname1.toString());
			Cursor cursor = db.rawQuery( varname1.toString(), null);
			if(cursor.moveToFirst())
			{
    			beneficiary.setEligibleId(cursor.getLong(cursor.getColumnIndex("ELIGIBLE_ID")));
				beneficiary.setCcsTreatmentId(cursor.getLong(cursor.getColumnIndex("CCS_TR_ID")));
				beneficiary.setCcsStatusId(cursor.getLong(cursor.getColumnIndex("STATUS_ID")));
				beneficiary.setCcsStatusName(cursor.getString(cursor.getColumnIndex("STATUS_NAME")));
				beneficiary.setCcsStatusCaption(cursor.getString(cursor.getColumnIndex("STATUS_CAPTION")));
				beneficiary.setOverCapacity(cursor.getLong(cursor.getColumnIndex("OVER_CAPACITY_FLAG")));
				beneficiary.setNextFollowupDate(cursor.getLong(cursor.getColumnIndex("NEXT_FOLLOWUP_DATE")));
			}
			cursor.close();
		}

		public long getNumberOfCCSFollowup(long ccsTrId,long type ) {
	        long count = 0;
			String sql = " SELECT COUNT(FOLLOWUP_ID)  COUNT_FOLLOWUP FROM ccs_treatment_followup WHERE CCS_TR_ID="+ccsTrId ; 
			if(type!=-1){
				sql=sql+" AND FOLLOWUP_VISIT="+type;
			}
			Log.e("SQL(countFollowup) : ", sql);
			Cursor cursor = db.rawQuery(sql, null);
			if(cursor.moveToFirst())
			{
				count = cursor.getLong(cursor.getColumnIndex("COUNT_FOLLOWUP"));
			}
			cursor.close();
			return count;
	    }

		/**
		 * Save CCS status list.
		 *
		 * @param ccsStatusList The status list
		 * @param langCode The language code
		 */
		public void saveCCSStatus(ArrayList<CCSStatus> rows,JSONObject param)
		{
			String langCode=App.getContext().getAppSettings().getLanguage();
			long count=0;
			for(CCSStatus ccsStatus : rows)
			{
				ContentValues cv = new ContentValues();
				cv.put("STATUS_ID", ccsStatus.getStatusId());
				cv.put("STATUS_NAME", ccsStatus.getStatusName());
				cv.put("STATUS_CAPTION", ccsStatus.getStatusCaption());
				cv.put("LANG_CODE", langCode);
				db.replace(DBTable.CCS_STATUS, null, cv);
				count++;
			}

			parent.updateDataVersion(DBTable.CCS_STATUS,rows.size(),count,param, KEY.VERSION_NO_CCS_STATUS);

		}
		
		
		public long saveCCSPrimaryScreening(CCSBeneficiary ccsBeneficiary, long userId, String langCode, long interviewId,long interviewStartTime)
		{
			Calendar cal = Calendar.getInstance();
			long curTimeInMillis = cal.getTimeInMillis();
			ContentValues cv = new ContentValues();
			cv.put("TREATMENT_PROGRESS", ccsBeneficiary.getTreatmentProgress());
			cv.put("CCS_STATUS_ID", ccsBeneficiary.getCcsStatusId());
			cv.put("NEXT_FOLLOWUP_DATE", ccsBeneficiary.getNextFollowupDate());
			if(ccsBeneficiary.getReferralCenterId()>0){
				cv.put("REF_CENTER_ID", ccsBeneficiary.getReferralCenterId());
			}
			
			if(ccsBeneficiary.getComittedDateToGoHospital() != null)
			{
				try {
					cv.put("HOSPITAL_GOING_DATE", Utility.getMillisecondFromDate(ccsBeneficiary.getComittedDateToGoHospital(),Constants.DATE_FORMAT_YYYY_MM_DD));
					cv.putNull("REASON_ID");
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else{
				cv.put("REASON_ID", ccsBeneficiary.getReasonForNotToDoTest() );
				cv.putNull("HOSPITAL_GOING_DATE");
			}
			
			long ccsTreatmentId =ccsBeneficiary.getCcsTreatmentId();
			if(ccsTreatmentId > 0)
			{
				db.update("ccs_treatment", cv,"CCS_TR_ID="+ccsTreatmentId, null);
			}
			else
			{
				cv.put("USER_ID", userId);
				cv.put(Column.BENEFICIARY_BENEF_ID, ccsBeneficiary.getBenefId());
				cv.put(Column.BENEFICIARY_BENEF_CODE, ccsBeneficiary.getBenefCode());
				cv.put("ELIGIBLE_ID", ccsBeneficiary.getEligibleId());
				cv.put("TRANS_REF", interviewStartTime);
				ccsTreatmentId = db.insert("ccs_treatment", null, cv);
			}
			
			if(ccsBeneficiary.getActivityStartDate()>0){
				String updateSql="update ccs_eligible set ACTIVITY_START_DATE=Strftime('%s','"+Utility.getDateFromMillisecond(ccsBeneficiary.getActivityStartDate(), Constants.DATE_FORMAT_YYYY_MM_DD)+"','utc') * 1000 where ELIGIBLE_ID="+ccsBeneficiary.getEligibleId();
				db.execSQL(updateSql);
			}
			
			if(ccsTreatmentId > 0)
			{
				ContentValues cvCCSFollowup = new ContentValues();
				cvCCSFollowup.put("CCS_TR_ID", ccsTreatmentId);
				cvCCSFollowup.put("FCM_VISIT_DATE", interviewStartTime);
				cvCCSFollowup.put("INTERVIEW_ID", interviewId);
				cvCCSFollowup.put("REASON_ID", ccsBeneficiary.getReasonForNotToDoTest());
				if(ccsBeneficiary.getFollowUpVisit()!=null && ccsBeneficiary.getFollowUpVisit()>-1){
					cvCCSFollowup.put("FOLLOWUP_VISIT", ccsBeneficiary.getFollowUpVisit());
				}
				
				cvCCSFollowup.put(Column.TRANS_REF,interviewStartTime);
				if(ccsBeneficiary.getComittedDateToGoHospital() != null)
				{
					try {
						cvCCSFollowup.put("HOSPITAL_GOING_DATE", Utility.getMillisecondFromDate(ccsBeneficiary.getComittedDateToGoHospital(),Constants.DATE_FORMAT_YYYY_MM_DD));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				db.insert("ccs_treatment_followup", null, cvCCSFollowup);
			}
			return ccsTreatmentId;
		}
		public void saveCCSFollowup(CCSBeneficiary ccsBeneficiary, long userId, String langCode, long interviewId , long interviewStartTime)
		{
			Calendar cal = Calendar.getInstance();
		
			ContentValues cv = new ContentValues();
			cv.put(Column.BENEFICIARY_BENEF_ID, ccsBeneficiary.getBenefId());
			cv.put(Column.BENEFICIARY_BENEF_CODE, ccsBeneficiary.getBenefCode());
			cv.put("CCS_STATUS_ID", ccsBeneficiary.getCcsStatusId());
			cv.put("USER_ID", userId);
			cv.put("NEXT_FOLLOWUP_DATE", ccsBeneficiary.getNextFollowupDate());
			if(ccsBeneficiary.getReferralCenterId()>0){
				cv.put("REF_CENTER_ID", ccsBeneficiary.getReferralCenterId());
			}
			
			if(ccsBeneficiary.getComittedDateToGoHospital() != null)
			{
				try {
					cv.put("HOSPITAL_GOING_DATE", Utility.getMillisecondFromDate(ccsBeneficiary.getComittedDateToGoHospital(),Constants.DATE_FORMAT_YYYY_MM_DD));
					cv.putNull("REASON_ID");
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else{
				cv.put("REASON_ID", ccsBeneficiary.getReasonForNotToDoTest() );
				cv.putNull("HOSPITAL_GOING_DATE");
			}
			
			long ccsTrId = ccsBeneficiary.getCcsTreatmentId();
			
			

			db.update("ccs_treatment", cv, "CCS_TR_ID="+ccsTrId,null);

			if(ccsTrId > 0)
			{
				ContentValues cvCCSFollowup = new ContentValues();
				cvCCSFollowup.put("CCS_TR_ID", ccsTrId);
				cvCCSFollowup.put("FCM_VISIT_DATE", interviewStartTime);
				cvCCSFollowup.put("INTERVIEW_ID", interviewId);
				if(ccsBeneficiary.getFollowUpVisit()!=null && ccsBeneficiary.getFollowUpVisit()>-1){
					cvCCSFollowup.put("FOLLOWUP_VISIT", ccsBeneficiary.getFollowUpVisit());
				}
				cvCCSFollowup.put("REASON_ID", ccsBeneficiary.getReasonForNotToDoTest() );
				cvCCSFollowup.put(Column.TRANS_REF,interviewStartTime);
				if(ccsBeneficiary.getComittedDateToGoHospital() != null)
				{
					try {
						cvCCSFollowup.put("HOSPITAL_GOING_DATE", Utility.getMillisecondFromDate(ccsBeneficiary.getComittedDateToGoHospital(),Constants.DATE_FORMAT_YYYY_MM_DD));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				db.insert("ccs_treatment_followup", null, cvCCSFollowup);
			}
		}
		public long getCCSStatusID(String statusName)
		{
			long statusID = 1;
			String sql = "SELECT STATUS_ID FROM CCS_STATUS WHERE STATUS_NAME=? LIMIT 1";
			Log.e("SQL(GET CCS STATUS ID) : ", sql);
			Cursor cursor = db.rawQuery(sql, new String []{statusName});
			if(cursor.moveToFirst())
			{
				statusID = cursor.getLong(cursor.getColumnIndex("STATUS_ID"));
			}
			cursor.close();

			return statusID;
		}
		
		public void beneficiaryReEligible(String benefCode ,String date){

			try{
				db.execSQL(" UPDATE ccs_eligible SET ACTIVITY_STATE=0 WHERE BENEF_CODE='"+benefCode+"'");
				StringBuffer  varname1 = new StringBuffer();
				varname1.append(" INSERT INTO ccs_eligible ");
				varname1.append("            (benef_id, ");
				varname1.append("             benef_code, ");
				varname1.append("             age_when_detected,ELIGIBLE_DATE, ");
				varname1.append("             create_date) ");
				varname1.append(" SELECT b.benef_id, ");
				varname1.append("       b.benef_code, ");
				varname1.append("       Cast(Strftime('%Y.%m%d', 'now', 'localtime') - Strftime('%Y.%m%d', b.dob) ");
				varname1.append("            AS INT ");
				varname1.append("       )                            AS AGE, ");
				varname1.append("       Strftime('%s', '"+date+"') * 1000 AS CURREN_TIME_MILI ");
				varname1.append("       Strftime('%s', Date('now')) * 1000 AS CURREN_TIME, ");
				varname1.append(" FROM   beneficiary b ");
				varname1.append(" WHERE  b.benef_code = '"+benefCode+"'");
				
				Log.e("beneficiaryReEligible ",varname1.toString());
				db.execSQL(varname1.toString());
			}
			catch (Exception exception){
				exception.printStackTrace();
			}
		}
		

	    public void saveCCSEligible(JSONArray rows,JSONObject param)
		{ 
			try{
				if(rows !=null && rows.length()>0){
                    long count=0;
					for (int i = 0; i < rows.length(); i++) {
						ContentValues cv= new ContentValues();
						JSONObject  ce= rows.getJSONObject(i);
					
						long eligibleId=ce.getLong("ELIGIBLE_ID");
						long benefId=ce.getLong("BENEF_ID");
						String benefCode=ce.getString("BENEF_CODE");
						long activityState=ce.getLong("ACTIVITY_STATE");
						
						cv.put("ELIGIBLE_ID",eligibleId);
						cv.put("BENEF_ID", benefId);
						cv.put("BENEF_CODE", benefCode);
						
						
						if(ce.has("AGE_WHEN_DETECTED")) cv.put("AGE_WHEN_DETECTED", ce.getLong("AGE_WHEN_DETECTED"));
						if(ce.has("CREATE_DATE")) cv.put("CREATE_DATE", ce.getLong("CREATE_DATE"));
						if(ce.has("ELIGIBLE_DATE")) cv.put("ELIGIBLE_DATE", ce.getLong("ELIGIBLE_DATE"));
						if(ce.has("TREATMENT_START_DATE")) cv.put("TREATMENT_START_DATE", ce.getLong("TREATMENT_START_DATE"));
						
						String uniqueCondition=" BENEF_CODE='"+benefCode+"' AND ACTIVITY_STATE= "+activityState;
						
						if(ce.has("ACTIVITY_START_DATE") && ce.getLong("ACTIVITY_START_DATE")>0) {
							long activityStartDate=ce.getLong("ACTIVITY_START_DATE");
							cv.put("ACTIVITY_START_DATE", activityStartDate);	
							uniqueCondition=uniqueCondition+" AND ACTIVITY_START_DATE="+activityStartDate;
						}else{
							//uniqueCondition=uniqueCondition+" AND ACTIVITY_START_DATE IS NULL ";
						}
						
						cv.put("ACTIVITY_STATE", activityState);
						
						if(parent.isExist(DBTable.CCS_ELIGIBLE,uniqueCondition) ){
						   db.update(DBTable.CCS_ELIGIBLE, cv,uniqueCondition,null);
						}else{
						   db.insert(DBTable.CCS_ELIGIBLE, null, cv);
						}
						cv.clear();
					    count++;
					}

					parent.updateDataVersion(DBTable.CCS_ELIGIBLE,rows.length(),count,param, KEY.VERSION_NO_CCS_ELIGIBLE);

				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		public void saveCCSTreatment(JSONArray rows,JSONObject param)
		{ 
			try{
				if(rows !=null && rows.length()>0){
					long count=0;
					for (int i = 0; i < rows.length(); i++) {
						ContentValues cv= new ContentValues();
						JSONObject  cto= rows.getJSONObject(i);
						
						
						long ccsTrId=cto.getLong("CCS_TR_ID");
						long statusId=cto.getLong("CCS_STATUS_ID");
						long benefId=cto.getLong("BENEF_ID");
						long eligibleId=cto.getLong("ELIGIBLE_ID");
						String benefCode=cto.getString("BENEF_CODE");
						long transRef=cto.getLong("TRANS_REF");
						
						
						
						cv.put("CCS_TR_ID", ccsTrId);
						cv.put("BENEF_ID", benefId);
						cv.put("CCS_STATUS_ID", statusId);
						cv.put("TRANS_REF",transRef);
						cv.put("BENEF_CODE",benefCode );
						cv.put("ELIGIBLE_ID",eligibleId );
						if(cto.has("DIAGNOSED_RESULT"))cv.put("DIAGNOSED_RESULT", cto.getString("DIAGNOSED_RESULT"));
						if(cto.has("TREATMENT_PROGRESS"))cv.put("TREATMENT_PROGRESS", cto.getLong("TREATMENT_PROGRESS"));
						
						if(cto.has("DIAGNOSED_DATE"))cv.put("DIAGNOSED_DATE",cto.getLong("DIAGNOSED_DATE"));
						if(cto.has("NEXT_FOLLOWUP_DATE"))cv.put("NEXT_FOLLOWUP_DATE",cto.getLong("NEXT_FOLLOWUP_DATE"));
						if(cto.has("HOSPITAL_GOING_DATE"))cv.put("HOSPITAL_GOING_DATE",cto.getLong("HOSPITAL_GOING_DATE"));
						if(cto.has("REASON_ID")) cv.put("REASON_ID", cto.getString("REASON_ID"));
						if(cto.has("REF_CENTER_ID"))cv.put("REF_CENTER_ID",cto.getLong("REF_CENTER_ID"));
						if(cto.has("TREATMENT_AT"))cv.put("TREATMENT_AT",cto.getLong("TREATMENT_AT"));
						if(cto.has("DOCTOR_ID") )cv.put("DOCTOR_ID",cto.getLong("DOCTOR_ID"));
						if(cto.has("LAST_FINDINGS"))cv.put("LAST_FINDINGS",cto.getString("LAST_FINDINGS"));
						if(cto.has("CREATE_DATE") )cv.put("CREATE_DATE",cto.getLong("CREATE_DATE"));
						if(cto.has("CCS_CURRENT_STATUS") )cv.put("CCS_CURRENT_STATUS",cto.getLong("CCS_CURRENT_STATUS"));
						
						String uniqueCondition=" BENEF_CODE='"+benefCode+"' AND  ELIGIBLE_ID ="+eligibleId+" AND TRANS_REF="+transRef;
						
						if(parent.isExist(DBTable.CCS_TEREATMENT,uniqueCondition) ){
							Log.e("saveCCSTreatment ON UPDATE :" ,cv.toString()); 
							db.update(DBTable.CCS_TEREATMENT,cv, uniqueCondition, null);
						}else{
							Log.e("saveCCSTreatment ON SAVE :" ,cv.toString());
							db.insert(DBTable.CCS_TEREATMENT, null, cv);
						}
						
						cv.clear();
						if(cto.has("CCS_TEATMENT_FOLLOW_UP")){
							db.delete("ccs_treatment_followup","CCS_TR_ID="+ccsTrId ,null);
							JSONArray tretmentFollowUpList=cto.getJSONArray("CCS_TEATMENT_FOLLOW_UP");
							for (int j = 0; j < tretmentFollowUpList.length(); j++) {
								cv.clear();
								JSONObject ctd=tretmentFollowUpList.getJSONObject(j);
								cv.put("CCS_TR_ID", ccsTrId);
								if(ctd.has("FCM_VISIT_DATE"))cv.put("FCM_VISIT_DATE",ctd.getLong("FCM_VISIT_DATE"));
								if(ctd.has("HOSPITAL_GOING_DATE"))cv.put("HOSPITAL_GOING_DATE",ctd.getLong("HOSPITAL_GOING_DATE"));
								if(ctd.has("REASON_ID")) cv.put("REASON_ID", ctd.getString("REASON_ID"));
								if(ctd.has("FOLLOWUP_VISIT"))cv.put("FOLLOWUP_VISIT", ctd.getLong("FOLLOWUP_VISIT"));
								if(ctd.has("INTERVIEW_ID"))cv.put("INTERVIEW_ID", ctd.getLong("INTERVIEW_ID"));
								if(ctd.has("TRANS_REF") )cv.put("TRANS_REF",ctd.getLong("TRANS_REF"));
								if(ctd.has("OVER_CAPACITY_FLAG") )cv.put("OVER_CAPACITY_FLAG",ctd.getLong("OVER_CAPACITY_FLAG"));
								db.insert("ccs_treatment_followup", null, cv);
								cv.clear();
							}
						}
						count++;
					}

					parent.updateDataVersion(DBTable.CCS_TEREATMENT,rows.length(),count, param, KEY.VERSION_NO_CCS_TREATMENT);

				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public long updateOverCapacityFlag(long transRef ,long flag){
			try {
				ContentValues cv = new ContentValues();
				cv.put("OVER_CAPACITY_FLAG",flag);
				return db.update("ccs_treatment_followup", cv, " TRANS_REF=? ", new String []{Long.toString(transRef) });
			}catch(Exception e){
				e.printStackTrace();
			}
			return 0;
		}

}
