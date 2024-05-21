package ngo.friendship.satellite.model;

import java.io.Serializable;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class QuestionnaireList.
 */
public class QuestionnaireList implements Serializable {
	
	/** The is error. */
	private boolean isError;
	
	/** The error code. */
	private int errorCode;
	
	/** The error desc. */
	private String errorDesc;
	
	/** The all questionnaire. */
	ArrayList<QuestionnaireInfo> allQuestionnaire;
	
	/**
	 * Sets the checks if is error.
	 *
	 * @param isError the new checks if is error
	 */
	public void setIsError(boolean isError) {
		this.isError = isError;
	}
	
	/**
	 * Gets the checks if is error.
	 *
	 * @return the checks if is error
	 */
	public boolean getIsError()
	{
		return isError;
	}
	
	/**
	 * Sets the error code.
	 *
	 * @param errorCode the new error code
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public int getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Sets the error desc.
	 *
	 * @param errorDesc the new error desc
	 */
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	
	/**
	 * Gets the error desc.
	 *
	 * @return the error desc
	 */
	public String getErrorDesc() {
		return errorDesc;
	}
	
	/**
	 * Sets the all questionnaire.
	 *
	 * @param allQuestionnaire the new all questionnaire
	 */
	public void setAllQuestionnaire(ArrayList<QuestionnaireInfo> allQuestionnaire) {
		this.allQuestionnaire = allQuestionnaire;
	}
	
	/**
	 * Gets the all questionnaire.
	 *
	 * @return the all questionnaire
	 */
	public ArrayList<QuestionnaireInfo> getAllQuestionnaire() {
		return allQuestionnaire;
	}
}
