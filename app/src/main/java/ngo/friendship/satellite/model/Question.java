package ngo.friendship.satellite.model;


import com.logic27.components.Component;

import java.io.Serializable;
import java.util.ArrayList;

import ngo.friendship.satellite.views.QuestionView;

// TODO: Auto-generated Javadoc

/**
 * The Class Question.
 */
public class Question implements Serializable{

	/** The key. */
	private String key;

	/** The name. */
	private String name;

	/** The type. */
	private String type;

	/** The caption. */
	private String caption;

	/** The hint. */
	private String hint;

	/** The default value. */
	private ArrayList<String> defaultValue;

	/** The required. */
	private boolean required;

	/** The readonly. */
	private boolean readonly;

	/** The media type. */
	private String mediaType;
	
	

	/** The num type. */
	private String numType;

	private String timeFormat;

	/** The option list. */
	private ArrayList<QuestionOption> optionList;

	/** The branch list. */
	private ArrayList<QuestionBranch> branchList;

	/** The branch item list. */
	private ArrayList<Integer> branchItemList;

	/** The validation list. */
	private ArrayList<InputValidation> validationList;

	/** The validation msg. */
	private String validationMsg;

	/** The ui. */
	private QuestionView UI;

	/** The prescription. */
	ArrayList<MedicineInfo> prescription;

	/** The referral center list. */
	ArrayList<ReferralCenterInfo> referralCenterList;

	
	/** The user input. */
	private ArrayList<String> userInput;

	/** The expression. */
	private String expression;

	/** The hidden. */
	private boolean hidden;

	private boolean savable = true;
	private boolean suggestion=false;
	private String suggestionType="";
	private boolean addMedicine=false;
	private boolean multiLine=false;

	//for media type 
	private long scaleTo;
	private long quality;
	
	private boolean firstVisible = false;
	private boolean lastVisible = false;
	private boolean maybeLastVisible = false;
	
	
	private String locationType;
	private long accuracy=100;
	private long delay=60000;
	private long locationReqInterval=1000;
	

	private Component component;


	/**
	 * Sets the referral center list.
	 * 
	 * @param referralCenterList
	 *            the new referral center list
	 */
	public void setReferralCenterList(
			ArrayList<ReferralCenterInfo> referralCenterList) {
		this.referralCenterList = referralCenterList;
	}

	/**
	 * Gets the referral center list.
	 * 
	 * @return the referral center list
	 */
	public ArrayList<ReferralCenterInfo> getReferralCenterList() {
		return referralCenterList;
	}

	/**
	 * Sets the prescription.
	 * 
	 * @param prescription
	 *            the new prescription
	 */
	public void setPrescription(ArrayList<MedicineInfo> prescription) {
		this.prescription = prescription;
	}

	/**
	 * Gets the prescription.
	 * 
	 * @return the prescription
	 */
	public ArrayList<MedicineInfo> getPrescription() {
		return prescription;
	}

	/**
	 * Sets the ui.
	 * 
	 * @param uI
	 *            the new ui
	 */
	public void setUI(QuestionView uI) {
		UI = uI;
	}

	/**
	 * Gets the ui.
	 * 
	 * @return the ui
	 */
	public QuestionView getUI() {
		return UI;
	}

	/**
	 * Sets the user input.
	 * 
	 * @param userInput
	 *            the new user input
	 */
	public void setUserInput(ArrayList<String> userInput) {
		this.userInput = userInput;
	}

	/**
	 * Gets the user input.
	 * 
	 * @return the user input
	 */
	public ArrayList<String> getUserInput() {
		return userInput;
	}

	/**
	 * Gets the branch item list.
	 * 
	 * @return the branch item list
	 */
	public ArrayList<Integer> getBranchItemList() {
		return branchItemList;
	}

	/**
	 * Sets the branch item list.
	 * 
	 * @param branchItemList
	 *            the new branch item list
	 */
	public void setBranchItemList(ArrayList<Integer> branchItemList) {
		this.branchItemList = branchItemList;
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 * 
	 * @param key
	 *            the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the caption.
	 * 
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the caption.
	 * 
	 * @param caption
	 *            the new caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * Gets the hint.
	 * 
	 * @return the hint
	 */
	public String getHint() {
		return hint;
	}

	/**
	 * Sets the hint.
	 * 
	 * @param hint
	 *            the new hint
	 */
	public void setHint(String hint) {
		this.hint = hint;
	}

	/**
	 * Gets the default value.
	 * 
	 * @return the default value
	 */
	public ArrayList<String> getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the default value.
	 * 
	 * @param defaultValue
	 *            the new default value
	 */
	public void setDefaultValue(ArrayList<String> defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Checks if is required.
	 * 
	 * @return true, if is required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 * 
	 * @param required
	 *            the new required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Checks if is readonly.
	 * 
	 * @return true, if is readonly
	 */
	public boolean isReadonly() {
		return readonly;
	}

	/**
	 * Sets the readonly.
	 * 
	 * @param readonly
	 *            the new readonly
	 */
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	/**
	 * Gets the media type.
	 * 
	 * @return the media type
	 */
	public String getMediaType() {
		return mediaType;
	}

	/**
	 * Sets the media type.
	 * 
	 * @param mediaType
	 *            the new media type
	 */
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	/**
	 * Gets the num type.
	 * 
	 * @return the num type
	 */
	public String getNumType() {
		return numType;
	}

	/**
	 * Sets the num type.
	 * 
	 * @param numType
	 *            the new num type
	 */
	public void setNumType(String numType) {
		this.numType = numType;
	}

	/**
	 * Gets the option list.
	 * 
	 * @return the option list
	 */
	public ArrayList<QuestionOption> getOptionList() {
		return optionList;
	}

	/**
	 * Sets the option list.
	 * 
	 * @param optionList
	 *            the new option list
	 */
	public void setOptionList(ArrayList<QuestionOption> optionList) {
		this.optionList = optionList;
	}

	/**
	 * Gets the branch list.
	 * 
	 * @return the branch list
	 */
	public ArrayList<QuestionBranch> getBranchList() {
		return branchList;
	}

	/**
	 * Sets the branch list.
	 * 
	 * @param branchList
	 *            the new branch list
	 */
	public void setBranchList(ArrayList<QuestionBranch> branchList) {
		this.branchList = branchList;
	}

	/**
	 * Gets the validation list.
	 * 
	 * @return the validation list
	 */
	public ArrayList<InputValidation> getValidationList() {
		return validationList;
	}

	/**
	 * Sets the validation list.
	 * 
	 * @param validationList
	 *            the new validation list
	 */
	public void setValidationList(ArrayList<InputValidation> validationList) {
		this.validationList = validationList;
	}

	/**
	 * Gets the validation msg.
	 * 
	 * @return the validation msg
	 */
	public String getValidationMsg() {
		return validationMsg;
	}

	/**
	 * Sets the validation msg.
	 * 
	 * @param validationMsg
	 *            the new validation msg
	 */
	public void setValidationMsg(String validationMsg) {
		this.validationMsg = validationMsg;
	}

	/**
	 * Sets the expression.
	 * 
	 * @param expression
	 *            the new expression
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}

	/**
	 * Gets the expression.
	 * 
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Sets the hidden.
	 * 
	 * @param hidden
	 *            the new hidden
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * Checks if is hidden.
	 * 
	 * @return true, if is hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	public void setSavable(boolean savable) {
		this.savable = savable;
	}

	public boolean isSavable() {
		return savable;
	}

	public void setFirstVisible(boolean firstVisible) {
		this.firstVisible = firstVisible;
	}

	public boolean isFirstVisible() {
		return firstVisible;
	}

	public void setLastVisible(boolean lastVisible) {
		this.lastVisible = lastVisible;
	}

	public boolean isLastVisible() {
		return lastVisible;
	}

	public void setMaybeLastVisible(boolean maybeLastVisible) {
		this.maybeLastVisible = maybeLastVisible;
	}
	public boolean isMaybeLastVisible() {
		return maybeLastVisible;
	}
	public void setTimeFormat(String timeFormat) {
		this.timeFormat = timeFormat;
	}
	public String getTimeFormat() {
		return timeFormat;
	}

	public void setComponent(Component component) {
		this.component = component;
	}
	public Component getComponent() {
		return component;
	}
	
	public void setScaleTo(long scaleTo) {
		this.scaleTo = scaleTo;
	}
	public long getScaleTo() {
		return scaleTo;
	}
	
	public void setQuality(long quality) {
		this.quality = quality;
	}
	
	public long getQuality() {
		return quality;
	}
	
	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}
	
	public String getLocationType() {
		return locationType;
	}
	
	public void setAccuracy(long accuracy) {
		this.accuracy = accuracy;
	}
	public long getAccuracy() {
		return accuracy;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	public long getDelay() {
		return delay;
	}
	
	public void setLocationReqInterval(long locationReqInterval) {
		this.locationReqInterval = locationReqInterval;
	}
	public long getLocationReqInterval() {
		return locationReqInterval;
	}
	
	public void setSuggestion(boolean suggestion) {
		this.suggestion = suggestion;
	}
	
	public void setSuggestionType(String suggestionType) {
		this.suggestionType = suggestionType;
	}
	
	public String getSuggestionType() {
		return suggestionType;
	}
	
	public boolean isSuggestion() {
		return this.suggestion;
	}

	public boolean isAddMedicine(){
		return addMedicine;
	}

	public void setAddMedicine(boolean addMedicine) {
		this.addMedicine = addMedicine;
	}


	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

	public boolean isMultiLine() {
		return this.multiLine;
	}
	
	
}
