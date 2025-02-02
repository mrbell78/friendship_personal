package ngo.friendship.satellite.model;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

import ngo.friendship.satellite.App;

// TODO: Auto-generated Javadoc

/**
 * The Class QuestionnaireInfo.
 */
public class QuestionnaireInfo implements Serializable{
	
	/** The questionnaire title. */
	private String questionnaireTitle;
	
	/** The id. */
	private long id;
	
	/** The questionnaire json. */
	private String questionnaireJSON;
	
	/** The is selected. */
	private boolean isSelected;
	
	/** The category id. */
	long categoryId;
	
	/** The questionnaire name. */
	private String questionnaireName;
	
	/** The state. */
	private int state;
	
	/** The is highlight. */
	private boolean isHighlight = false;
	private long visibileInCategory;
	/** The lang code. */
	private String langCode;
	
	private String icon;

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	private String param1;

	private String param2;
	
	//SORT_ORDER
	private long sortOrder;
	
	private long serviceCategoryId;
	
	private ArrayList<QuestionnaireDetail> questionnaireDetails;

	private  String singlePgFormView="1";

	public String getIsSinglePgFormView() {
		return singlePgFormView;
	}

	public void setSinglePgFormView(String singlePgFormView) {
		this.singlePgFormView = singlePgFormView;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setPromptForBeneficiary(long promptForBeneficiary) {
        this.promptForBeneficiary = promptForBeneficiary;
    }

    public long getPromptForBeneficiary() {
        return promptForBeneficiary;
    }
    
    private long promptForBeneficiary;
    private String benefSelectionCriteria;
    
    private Long maternalServiceId;
    
    public void setMaternalServiceId(Long maternalServiceId) {
		this.maternalServiceId = maternalServiceId;
	}
    public Long getMaternalServiceId() {
		return maternalServiceId;
	}
    
	/**
	 * Sets the lang code.
	 *
	 * @param langCode the new lang code
	 */
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	
	/**
	 * Gets the lang code.
	 *
	 * @return the lang code
	 */
	public String getLangCode() {
		return langCode;
	}	
	
	/**
	 * Checks if is highlight.
	 *
	 * @return true, if is highlight
	 */
	public boolean isHighlight() {
		return isHighlight;
	}
	
	/**
	 * Sets the highlight.
	 *
	 * @param isHighlight the new highlight
	 */
	public void setHighlight(boolean isHighlight) {
		this.isHighlight = isHighlight;
	}
	
	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	
	/** The followup questionnaire list. */
	private ArrayList<FollowupQuestionnaireInfo> followupQuestionnaireList;
	
	/**
	 * Sets the followup questionnaire list.
	 *
	 * @param followupQuestionnaireList the new followup questionnaire list
	 */
	public void setFollowupQuestionnaireList(
			ArrayList<FollowupQuestionnaireInfo> followupQuestionnaireList) {
		this.followupQuestionnaireList = followupQuestionnaireList;
	}
	
	/**
	 * Gets the followup questionnaire list.
	 *
	 * @return the followup questionnaire list
	 */
	public ArrayList<FollowupQuestionnaireInfo> getFollowupQuestionnaireList() {
		return followupQuestionnaireList;
	}

	/**
	 * Sets the questionnaire name.
	 *
	 * @param questionnaireName the new questionnaire name
	 */
	public void setQuestionnaireName(String questionnaireName) {
		this.questionnaireName = questionnaireName;
	}

	/**
	 * Gets the questionnaire name.
	 *
	 * @return the questionnaire name
	 */
	public String getQuestionnaireName() {
		return questionnaireName;
	}
	
	/**
	 * Sets the category id.
	 *
	 * @param categoryId the new category id
	 */
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	
	/**
	 * Gets the category id.
	 *
	 * @return the category id
	 */
	public long getCategoryId() {
		return categoryId;
	}
	
	/**
	 * Checks if is selected.
	 *
	 * @return true, if is selected
	 */
	public boolean isSelected() {
		return isSelected;
	}
	
	/**
	 * Sets the selected.
	 *
	 * @param isSelected the new selected
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	/**
	 * Sets the questionnaire json.
	 *
	 * @param questionnaireJSON the new questionnaire json
	 */
	public void setQuestionnaireJSON(String questionnaireJSON) {
		this.questionnaireJSON = questionnaireJSON;
	}
	
	/**
	 * Gets the questionnaire json.
	 *
	 * @return the questionnaire json
	 */
	public String getQuestionnaireJSON() {
		return questionnaireJSON;
	}


	public String getQuestionnaireJSON(Context context) {
		byte[] key= App.getContext().getUserInfo().getKey(context);
		try {
			//return new String(SecurityUtil.decrypt(key, Base64.decode(questionnaireJSON)));
			return new String(questionnaireJSON);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	
	public void setSortOrder(long sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public long getSortOrder() {
		return sortOrder;
	}
	
	/**
	 * Sets the questionnaire title.
	 *
	 * @param questionnaireTitle the new questionnaire title
	 */
	public void setQuestionnaireTitle(String questionnaireTitle) {
		this.questionnaireTitle = questionnaireTitle;
	}
	
	public void setVisibileInCategory(long visibileInCategory) {
		this.visibileInCategory = visibileInCategory;
	}
	public long getVisibileInCategory() {
		return visibileInCategory;
	}
	/**
	 * Gets the questionnaire title.
	 *
	 * @return the questionnaire title
	 */
	public String getQuestionnaireTitle() {
		return questionnaireTitle;
	}
	public String getBenefSelectionCriteria() {
		return benefSelectionCriteria;
	}
	public void setBenefSelectionCriteria(String benefSelectionCriteria) {
		this.benefSelectionCriteria = benefSelectionCriteria;
	}
	
	public ArrayList<QuestionnaireDetail> getQuestionnaireDetails() {
		return questionnaireDetails;
	}
	public void setQuestionnaireDetails(ArrayList<QuestionnaireDetail> questionnaireDetails) {
		this.questionnaireDetails = questionnaireDetails;
	}
	
	public void setServiceCategoryId(long serviceCategoryId) {
		this.serviceCategoryId = serviceCategoryId;
	}
	
	public long getServiceCategoryId() {
		return serviceCategoryId;
	}
}
