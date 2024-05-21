package ngo.friendship.satellite.model;

/**
 * The Class AppVersionHistory.
 *
 * @author Mohammed Juabyer
 * Data structure to hold the application version history
 * Created Date: 28/12/2014
 * Last Update: 29/12/2014
 */

import java.io.Serializable;

//Table(name = "app_version_history")
public class AppVersionHistory  implements Serializable{

	// Column(name = "VERSION_ID")
	private long versionId;
	// Column(name = "APP_NAME")
	private String appName;
	// Column(name = "VERSION_NUMBER")
	private long versionNumber;
	// Column(name = "VERSION_NAME")
	private String versionName;
	// Column(name = "VERSION_DESC")
	private String versionDesc;
	// Column(name = "RELEASE_DATE")
	// Temporal(TemporalType.TIMESTAMP)
	private long releaseDate;
	// Column(name = "APP_PATH")
	private String appPath;

	// Temporal(TemporalType.TIMESTAMP)
	// Column(name = "OPEN_DATE")
	private long openDate;
	// Column(name = "INSTALL_DATE")
	// Temporal(TemporalType.TIMESTAMP)
	private long installDate;

	private String updateNotification;
	private String updateDesc;

	// OneToMany(cascade = CascadeType.ALL, mappedBy = "versionId")

	private String langCode;
	private String installFlag;
	
	
	private String appPathLocal;
	private long sendFlag;
	

	public long getVersionId() {
		return versionId;
	}

	public void setVersionId(long versionId) {
		this.versionId = versionId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public long getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(long versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getVersionDesc() {
		return versionDesc;
	}

	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}

	public void setReleaseDate(long releaseDate) {
		this.releaseDate = releaseDate;
	}

	public long getReleaseDate() {
		return releaseDate;
	}

	public String getAppPath() {
		return appPath;
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public void setUpdateDesc(String updateDesc) {
		this.updateDesc = updateDesc;
	}

	public String getUpdateDesc() {
		return updateDesc;
	}

	public void setUpdateNotification(String updateNotification) {
		this.updateNotification = updateNotification;
	}

	public String getUpdateNotification() {
		return updateNotification;
	}

	public void setInstallFlag(String installFlag) {
		this.installFlag = installFlag;
	}

	public String getInstallFlag() {
		return installFlag;
	}

	public long getInstallDate() {
		return installDate;
	}

	public void setInstallDate(long installDate) {
		this.installDate = installDate;
	}

	public long getOpenDate() {
		return openDate;
	}

	public void setOpenDate(long openDate) {
		this.openDate = openDate;
	}
	
	
	public void setAppPathLocal(String appPathLocal) {
		this.appPathLocal = appPathLocal;
	}
	public String getAppPathLocal() {
		return appPathLocal;
	}
	
	public void setSendFlag(long sendFlag) {
		this.sendFlag = sendFlag;
	}
	public long getSendFlag() {
		return sendFlag;
	}

	public static final String MODEL_NAME = "app_version_history";
	public static final String VERSION_ID = "VERSION_ID";
	public static final String APP_NAME = "APP_NAME";
	public static final String VERSION_NUMBER = "VERSION_NUMBER";
	public static final String VERSION_NAME = "VERSION_NAME";
	public static final String VERSION_DESC = "VERSION_DESC";
	public static final String RELEASE_DATE = "RELEASE_DATE";
	public static final String APP_PATH = "APP_PATH";
	public static final String UPDATE_NOTIFICATION = "UPDATE_NOTIFICATION";
	public static final String UPDATE_DESC = "UPDATE_DESC";
	public static final String INSTALL_FLAG = "INSTALL_FLAG";
	public static final String LANG_CODE = "LANG_CODE";
	public static final String OPEN_DATE = "OPEN_DATE";
	public static final String INSTALL_DATE = "INSTALL_DATE";
	
	public static final String APP_PATH_LOCAL ="APP_PATH_LOCAL";
	public static final String SEND_FLAG ="SEND_FLAG";//
	     

	public static final String VERSION_NOTIFICATION = "VERSION_NOTIFICATION";

	

	@Override
	public String toString() {
		return "AppVersionHistory [versionId=" + versionId + ", appName="
				+ appName + ", versionNumber=" + versionNumber
				+ ", versionName=" + versionName + ", versionDesc="
				+ versionDesc + ", releaseDate=" + releaseDate + ", appPath="
				+ appPath + ", openDate=" + openDate + ", installDate="
				+ installDate + ", updateNotification=" + updateNotification
				+ ", updateDesc=" + updateDesc + ", langCode=" + langCode
				+ ", installFlag=" + installFlag + ", appPathLocal="
				+ appPathLocal 
				+ ", openSendFlag=" + sendFlag + "]";
	}

	public static final String FLAG_RECEIVED = "RECEIVED";
	public static final String FLAG_OPENED = "OPENED";
	public static final String FLAG_INSTALLED = "INSTALLED";
	public static final String FLAG_REJECTED = "REJECTED";

}
