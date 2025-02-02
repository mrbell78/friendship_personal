package ngo.friendship.satellite.model;

import android.content.Context;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.utility.Utility;

public class Report implements Serializable ,Cloneable{
	
    public static final int REPORT=1;
    public static final int MENU=2;


    Calendar calendarFrom;
    Calendar calendarTo;

	Long MEDICINE_ID;

	Long user_id;

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	private long repId ;
	private String repName ;
	private String repCaption;
	private String repDescription ;
	private long parentId ;
	private String sqlStr ;
	private JSONArray repParams ;
	private long state ;
	private long sortOrder ;
    private HashMap<String,String> inputParams =new HashMap<String,String>();
	private String icon ;
	private JSONArray data;
	private int type;
	public String getSql() {
		String temp=sqlStr;
		for (String key:inputParams.keySet()) {
			String inputKey="<<"+key+">>";
			temp=temp.replace(inputKey, inputParams.get(key));
		}
	    return temp;
	}


	public Long getMEDICINE_ID() {
		return MEDICINE_ID;
	}

	public void setMEDICINE_ID(Long MEDICINE_ID) {
		this.MEDICINE_ID = MEDICINE_ID;
	}

	public long getRepId() {
		return repId;
	}



	public void setRepId(long repId) {
		this.repId = repId;
	}



	public String getRepName() {
		return repName;
	}



	public void setRepName(String repName) {
		this.repName = repName;
	}



	public String getRepCaption() {
		return repCaption;
	}



	public void setRepCaption(String repCaption) {
		this.repCaption = repCaption;
	}



	public String getRepDescription() {


		return repDescription;
	}
	
   public String getRepDescription(Context context) {

	   if(repDescription!=null && repDescription.trim().length()>0){
		   repDescription=repDescription+"\n";
	   }

	   repDescription=context.getResources().getString(R.string.date) +" :";
	   if(inputParams.containsKey(Constants.FROM_DATE) && inputParams.containsKey(Constants.TO_DATE)){
		   repDescription=repDescription+" "+Utility.getDateTimeFromMillisecond(Utility.parseLong(inputParams.get(Constants.FROM_MILLISECOND)),Constants.DATE_FORMAT_DD_MM_YYYY);
		   repDescription=repDescription+" "+context.getResources().getString(R.string.to);
		   repDescription=repDescription+" "+Utility.getDateTimeFromMillisecond(Utility.parseLong(inputParams.get(Constants.TO_MILLISECOND)),Constants.DATE_FORMAT_DD_MM_YYYY);
	   }
       return repDescription;
   }

	

	public void setRepDescription(String repDescription) {
		this.repDescription = repDescription;
	}



	public long getParentId() {
		return parentId;
	}



	public void setParentId(long parentId) {
		this.parentId = parentId;
	}



	public String getSqlStr() {
		return sqlStr;
	}



	public void setSqlStr(String sqlStr) {
		this.sqlStr = sqlStr;
	}


	public JSONArray getRepParams() {
		return repParams;
	}



	public void setRepParams(JSONArray repParams) {
		this.repParams = repParams;
	}



	public long getState() {
		return state;
	}



	public void setState(long state) {
		this.state = state;
	}



	public long getSortOrder() {
		return sortOrder;
	}



	public void setSortOrder(long sortOrder) {
		this.sortOrder = sortOrder;
	}



	public HashMap<String, String> getInputParams() {
		return inputParams;
	}

	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setData(JSONArray datas) {
		this.data = datas;
	}
	
	public JSONArray getData() {
		return data;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return repId + " "+repCaption;
	}
	public void setCalendarFromAndTo(Calendar calendarFrom,Calendar calendarTo) {
		this.calendarFrom = calendarFrom;
		this.calendarTo = calendarTo;
	}
	public Calendar getCalendarFrom() {
		return calendarFrom;
	}
	public Calendar getCalendarTo() {
		return calendarTo;
	}
	public  HashMap<String,String> resetInputPram(){
		inputParams.clear();
		inputParams.put(Constants.FROM_DATE, Utility.getDateFromMillisecond(calendarFrom.getTimeInMillis(),Constants.DATE_FORMAT_YYYY_MM_DD));
		inputParams.put(Constants.TO_DATE, Utility.getDateFromMillisecond(calendarTo.getTimeInMillis(),Constants.DATE_FORMAT_YYYY_MM_DD));
		inputParams.put(Constants.FROM_MILLISECOND, calendarFrom.getTimeInMillis()+"");
		inputParams.put(Constants.TO_MILLISECOND,calendarTo.getTimeInMillis()+"");
		inputParams.put(Constants.FROM_MONTH,calendarFrom.get(Calendar.MONTH)+1+"");
		inputParams.put(Constants.TO_MONTH,calendarTo.get(Calendar.MONTH)+1+"");
		inputParams.put(Constants.FROM_YEAR, calendarFrom.get(Calendar.YEAR)+"");
		inputParams.put(Constants.TO_YEAR,calendarFrom.get(Calendar.YEAR)+"");
		inputParams.put(Constants.LANG_CODE,App.getContext().getAppSettings().getLanguage());
		inputParams.put(Constants.MED_ID, MEDICINE_ID.toString());
		inputParams.put(Constants.USER_ID, user_id.toString());
		return  inputParams;
	}
	public void resetCalenderFromTo(int day){

		Calendar fromD = Calendar.getInstance();
		fromD.setTimeInMillis(day<0? calendarFrom.getTimeInMillis():calendarTo.getTimeInMillis());
		fromD.add(Calendar.DAY_OF_YEAR, day);
		fromD.set(Calendar.HOUR_OF_DAY, 0);
		fromD.set(Calendar.MINUTE, 0);
		fromD.set(Calendar.SECOND, 0);
		fromD.set(Calendar.MILLISECOND, 0);
		calendarFrom=fromD;

		Calendar toD = Calendar.getInstance();
		toD.setTimeInMillis(day<0? calendarFrom.getTimeInMillis():calendarTo.getTimeInMillis());
		//toD.setTimeInMillis(fromD.getTimeInMillis());
		toD.add(Calendar.DAY_OF_YEAR, day);
		toD.set(Calendar.HOUR_OF_DAY, 23);
		toD.set(Calendar.MINUTE, 59);
		toD.set(Calendar.SECOND, 59);
		toD.set(Calendar.MILLISECOND, 999);
		calendarTo=toD;

	}

	public void resetCalenderForNext(int day){

		Calendar fromD = Calendar.getInstance();
		fromD.setTimeInMillis(day<0? calendarFrom.getTimeInMillis():calendarTo.getTimeInMillis());
		fromD.add(Calendar.DAY_OF_YEAR, day);
		fromD.set(Calendar.HOUR_OF_DAY, 0);
		fromD.set(Calendar.MINUTE, 0);
		fromD.set(Calendar.SECOND, 0);
		fromD.set(Calendar.MILLISECOND, 0);
		calendarFrom=fromD;

		Calendar toD = Calendar.getInstance();
		//toD.setTimeInMillis(day<0? calendarFrom.getTimeInMillis():calendarTo.getTimeInMillis());
		toD.setTimeInMillis(fromD.getTimeInMillis());
		toD.add(Calendar.DAY_OF_YEAR, day);
		toD.set(Calendar.HOUR_OF_DAY, 23);
		toD.set(Calendar.MINUTE, 59);
		toD.set(Calendar.SECOND, 59);
		toD.set(Calendar.MILLISECOND, 999);
		calendarTo=toD;

	}


	public Report clone() throws
			CloneNotSupportedException
	{
		return (Report) super.clone();
	}

}