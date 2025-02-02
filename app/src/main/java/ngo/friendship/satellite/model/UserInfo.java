package ngo.friendship.satellite.model;

import android.content.Context;

import java.io.IOException;
import java.io.Serializable;

import ngo.friendship.satellite.constants.Column;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.Base64;

// TODO: Auto-generated Javadoc

/**
 * The Class UserInfo.
 */
public class UserInfo implements Serializable {
	
	/** The userName. */
	private String userName="";
	
	/** The profile pic url. */
	private String profilePicUrl;
	
	/** The profile pic local path. */
	private String profilePicLocalPath;
	
	/** The location id. */
	private long locationId=1;

	/** The fcm id. */
	private long userId=-1;
	
	/** The location userName. */
	private String locationName="";
	private String locationCode;

	/** The userCode. */
	private String userCode="";
	
	private long targetHh;
	private long state;

	private String token = "";

	
	/** The profile pic in string. */
	private String profilePicInString="";
	
	 private String otherDetails="";

	 private String password="";

	private long orgId=-1;
	private String orgCode="";
	private String orgName="";
	private String orgDesc="";
	private String orgAddress="";
	private String orgCountry="";
	private String headerSmallLogoPath="";
	private String loginImagePathMobile="";
	private String titleLogoPathMobile="";
	private String appTitleMobile="";
	private boolean selected;
	private String usrSLNo="000";

	public String getUsrSLNo() {
		return usrSLNo;
	}

	public void setUsrSLNo(String usrSLNo) {
		this.usrSLNo = usrSLNo;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Sets the profile pic in string.
	 *
	 * @param profilePicInString the new profile pic in string
	 */
	public void setProfilePicInString(String profilePicInString) {
		this.profilePicInString = profilePicInString;
	}
	
	/**
	 * Gets the profile pic in string.
	 *
	 * @return the profile pic in string
	 */
	public String getProfilePicInString() {
		return profilePicInString;
	}
	
	/**
	 * Sets the location userName.
	 *
	 * @param locationName the new location userName
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	
	/**
	 * Gets the location userName.
	 *
	 * @return the location userName
	 */
	public String getLocationName() {
		return locationName;
	}
	
	/**
	 * Sets the userCode.
	 *
	 * @param userCode the new userCode
	 */
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	
	/**
	 * Gets the userCode.
	 *
	 * @return the userCode
	 */
	public String getUserCode() {
		return userCode;
	}
	
	/**
	 * Sets the fcm id.
	 *
	 * @param userId the new fcm id
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	/**
	 * Gets the fcm id.
	 *
	 * @return the fcm id
	 */
	public long getUserId() {
		return userId;
	}
	
	/**
	 * Gets the userName.
	 *
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Sets the userName.
	 *
	 * @param userName the new userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Gets the profile pic url.
	 *
	 * @return the profile pic url
	 */
	public String getProfilePicUrl() {
		return profilePicUrl;
	}
	
	/**
	 * Sets the profile pic url.
	 *
	 * @param profilePicUrl the new profile pic url
	 */
	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}
	
	/**
	 * Gets the profile pic local path.
	 *
	 * @return the profile pic local path
	 */
	public String getProfilePicLocalPath() {
		return profilePicLocalPath;
	}
	
	/**
	 * Sets the profile pic local path.
	 *
	 * @param profilePicLocalPath the new profile pic local path
	 */
	public void setProfilePicLocalPath(String profilePicLocalPath) {
		this.profilePicLocalPath = profilePicLocalPath;
	}
	
	/**
	 * Gets the location id.
	 *
	 * @return the location id
	 */
	public long getLocationId() {
		return locationId;
	}
	
	/**
	 * Sets the location id.
	 *
	 * @param locationId the new location id
	 */
	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
	
	public void setTargetHh(long targetHh) {
		this.targetHh = targetHh;
	}
	public long getTargetHh() {
		return targetHh;
	}
	
	public void setOtherDetails(String otherDetails) {
		this.otherDetails = otherDetails;
	}
	
	
	public String getOtherDetails() {
		return otherDetails;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgDesc() {
		return orgDesc;
	}

	public void setOrgDesc(String orgDesc) {
		this.orgDesc = orgDesc;
	}

	public String getOrgAddress() {
		return orgAddress;
	}

	public void setOrgAddress(String orgAddress) {
		this.orgAddress = orgAddress;
	}

	public String getOrgCountry() {
		return orgCountry;
	}

	public void setOrgCountry(String orgCountry) {
		this.orgCountry = orgCountry;
	}

	public String getHeaderSmallLogoPath() {
		return headerSmallLogoPath;
	}

	public void setHeaderSmallLogoPath(String headerSmallLogoPath) {
		this.headerSmallLogoPath = headerSmallLogoPath;
	}

	public String getLoginImagePathMobile() {
		return loginImagePathMobile;
	}

	public void setLoginImagePathMobile(String loginImagePathMobile) {
		this.loginImagePathMobile = loginImagePathMobile;
	}

	public String getTitleLogoPathMobile() {
		return titleLogoPathMobile;
	}

	public void setTitleLogoPathMobile(String titleLogoPathMobile) {
		this.titleLogoPathMobile = titleLogoPathMobile;
	}

	public String getAppTitleMobile() {
		return appTitleMobile;
	}

	public void setAppTitleMobile(String appTitleMobile) {
		this.appTitleMobile = appTitleMobile;
	}

	public boolean isNewUser(){
		return this.orgCode.trim().isEmpty()||
				this.userCode.trim().isEmpty();
	}

	public boolean isDifferentUser(String orgCode,String userLoginId){
		return  !(this.orgCode.equals(orgCode)&&
				this.userCode.equals(userLoginId));
	}

	public boolean isActive(){
		return  !this.orgCode.trim().isEmpty()&&
				!this.userCode.trim().isEmpty()&&
				this.state==1;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setState(long state) {
		this.state = state;
	}

	public long getState() {
		return state;
	}

	public String getHeaderSmallLogoPathDir(){
		return orgCode.trim()+"_"+ Column.HEADER_SMALL_LOGO_PATH+".png";
	}

	public String getTitleLogoPathMobileDir(){
		return orgCode.trim()+"_"+ Column.TITLE_LOGO_PATH_MOBILE+".png";
	}

	public String getLoginImagePathMobileDir(){
		return orgCode.trim()+"_"+ Column.LOGIN_IMAGE_PATH_MOBILE+".png";
	}


	public byte[] getKey(Context context){

		try {
			return Base64.decode(AppPreference.getString(context,Column.USER_KEY,""));
		} catch (IOException e) {
			return null;
		}

	}

}
