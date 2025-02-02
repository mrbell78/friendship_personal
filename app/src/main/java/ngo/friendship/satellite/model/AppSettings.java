package ngo.friendship.satellite.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class AppSettings  implements Serializable{
	public static final String DEFAULT_LANGUAGE="en";
	private String hostAddress;
	private String alternateHostAddress;
    private String language;
	private boolean isGPSStartOnAppStart;

	private String fcmConfigration;
    private int ccsAutometicFCMFollowupInterval;
	private int ccsNumberOfMaximumFCMFollowup;
	private int epiMaxAge;
	private int epiMinAge;
	private int ttMaxAge;
	private int ttMinAge;
	private int immunizationMissGapDate;
	private int scheduleDayBeforToday;
	private int scheduleDayAfterToday;
	private String isImageShow;
	private boolean useNetworkProvidedTime;
	
	private long followUpDayBeforToday;
	private long followUpDayAfterToday;
    
    private String followUpType;


	public String getIsSplashTextShow() {
		return isSplashTextShow;
	}

	public void setIsSplashTextShow(String isSplashTextShow) {
		this.isSplashTextShow = isSplashTextShow;
	}

	private String isSplashTextShow;
	
	public void setFcmConfigration(String fcmConfigration) {
		this.fcmConfigration = fcmConfigration;
	}
	
	
	public String getFcmConfigration() {
		return fcmConfigration;
	}
	public JSONArray getFcmConfigrationJsonArray() {
		try {
			return new JSONArray(fcmConfigration);
		} catch (JSONException e) {
			return new JSONArray();
		}
	}
	
	public int getCcsAutometicFCMFollowupInterval() {
		return ccsAutometicFCMFollowupInterval;
	}

	public void setCcsAutometicFCMFollowupInterval(
			int ccsAutometicFCMFollowupInterval) {
		this.ccsAutometicFCMFollowupInterval = ccsAutometicFCMFollowupInterval;
	}

	public int getCcsNumberOfMaximumFCMFollowup() {
		return ccsNumberOfMaximumFCMFollowup;
	}

	public void setCcsNumberOfMaximumFCMFollowup(int ccsNumberOfMaximumFCMFollowup) {
		this.ccsNumberOfMaximumFCMFollowup = ccsNumberOfMaximumFCMFollowup;
	}

	public int getEpiMaxAge() {
		return epiMaxAge;
	}

	public void setEpiMaxAge(int epiMaxAge) {
		this.epiMaxAge = epiMaxAge;
	}

	public int getEpiMinAge() {
		return epiMinAge;
	}

	public void setEpiMinAge(int epiMinAge) {
		this.epiMinAge = epiMinAge;
	}

	public int getTtMaxAge() {
		return ttMaxAge;
	}

	public void setTtMaxAge(int ttMaxAge) {
		this.ttMaxAge = ttMaxAge;
	}

	public int getTtMinAge() {
		return ttMinAge;
	}

	public void setTtMinAge(int ttMinAge) {
		this.ttMinAge = ttMinAge;
	}

	public int getScheduleDayBeforToday() {
		return scheduleDayBeforToday;
	}

	public void setScheduleDayBeforToday(int scheduleDayBeforToday) {
		this.scheduleDayBeforToday = scheduleDayBeforToday;
	}

	public int getScheduleDayAfterToday() {
		return scheduleDayAfterToday;
	}

	public void setScheduleDayAfterToday(int scheduleDayAfterToday) {
		this.scheduleDayAfterToday = scheduleDayAfterToday;
	}

	/** The vibrate on doctor feedback. */
	private boolean vibrateOnDoctorFeedback;
	

	
	
	

	
	

	/**
	 * Checks if is enable collect household basic data.
	 *
	 * @return true, if is enable collect household basic data
	 */

	
	public String getHostAddress() {
		return hostAddress;
	}
	
	/**
	 * Sets the host address.
	 *
	 * @param hostAddress the new host address
	 */
	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}
	
	/**
	 * Gets the alternate host address.
	 *
	 * @return the alternate host address
	 */
	public String getAlternateHostAddress() {
		return alternateHostAddress;
	}
	
	/**
	 * Sets the alternate host address.
	 *
	 * @param alternateHostAddress the new alternate host address
	 */
	public void setAlternateHostAddress(String alternateHostAddress) {
		this.alternateHostAddress = alternateHostAddress;
	}
	


	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 *
	 * @param language the new language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
	/**
	 * Checks if is GPS start on app start.
	 *
	 * @return true, if is GPS start on app start
	 */
	public boolean isGPSStartOnAppStart() {
		return isGPSStartOnAppStart;
	}
	
	/**
	 * Sets the GPS start on app start.
	 *
	 * @param isGPSStartOnAppStart the new GPS start on app start
	 */
	public void setGPSStartOnAppStart(boolean isGPSStartOnAppStart) {
		this.isGPSStartOnAppStart = isGPSStartOnAppStart;
	}
	

	
	public void setIsImageShow(String isImageShow) {
		this.isImageShow = isImageShow;
	}
	
	public String getIsImageShow() {
		return isImageShow;
	}
	

    public void setImmunizationMissGapDate(int immunizationMissGapDate) {
	this.immunizationMissGapDate = immunizationMissGapDate;
    }
    public int getImmunizationMissGapDate() {
		return immunizationMissGapDate;
	}
    
    public void setFollowUpDayAfterToday(long followUpDayAfterToday) {
		this.followUpDayAfterToday = followUpDayAfterToday;
	}
     public void setFollowUpDayBeforToday(long followUpDayBeforToday) {
		this.followUpDayBeforToday = followUpDayBeforToday;
	}
     
     public long getFollowUpDayAfterToday() {
		return followUpDayAfterToday;
	}
     
     public long getFollowUpDayBeforToday() {
		return followUpDayBeforToday;
	  }
     
     public void setFollowUpType(String folloeUpType) {
		this.followUpType = folloeUpType;
	}
     
    public String getFollowUpType() {
		return followUpType;
	}


	public boolean isUseNetworkProvidedTime() {
		return useNetworkProvidedTime;
	}


	public void setUseNetworkProvidedTime(boolean useNetworkProvidedTime) {
		this.useNetworkProvidedTime = useNetworkProvidedTime;
	}
     
    
	private JSONObject cloneInfo;
	public void setCloneInfo(JSONObject cloneInfo) {
		this.cloneInfo = cloneInfo;
	}
	
	public JSONObject getCloneInfo() {
		return cloneInfo;
	}


}
