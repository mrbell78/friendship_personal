package ngo.friendship.satellite.model;

import java.io.Serializable;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class QuestionAnswer.
 */
public class QuestionAnswer implements Serializable {
	
	/** The question key. */
	private String questionKey;
	
	/** The question name. */
	private String questionName;
	
	/** The answer list. */
	private ArrayList<String> answerList;
	
	/** The type. */
	private String type;
	
	/** The media type. */
	private String mediaType;
	
	/** The question caption. */
	private String questionCaption;
	
	/** The hidden. */
	private boolean hidden;
	
	private boolean added;
	
	private boolean savable;
	
	private int order;
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	
	/**
	 * Sets the question caption.
	 *
	 * @param questionCaption the new question caption
	 */
	public void setQuestionCaption(String questionCaption) {
		this.questionCaption = questionCaption;
	}
	
	/**
	 * Gets the question caption.
	 *
	 * @return the question caption
	 */
	public String getQuestionCaption() {
		return questionCaption;
	}
	
	/**
	 * Sets the media type.
	 *
	 * @param mediaType the new media type
	 */
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
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
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
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
	 * Gets the question key.
	 *
	 * @return the question key
	 */
	public String getQuestionKey() {
		return questionKey;
	}
	
	/**
	 * Sets the question key.
	 *
	 * @param questionKey the new question key
	 */
	public void setQuestionKey(String questionKey) {
		this.questionKey = questionKey;
	}
	
	/**
	 * Gets the answer list.
	 *
	 * @return the answer list
	 */
	public ArrayList<String> getAnswerList() {
		return answerList;
	}
	
	/**
	 * Sets the answer list.
	 *
	 * @param answerList the new answer list
	 */
	public void setAnswerList(ArrayList<String> answerList) {
		this.answerList = answerList;
	}
	
	/**
	 * Gets the question name.
	 *
	 * @return the question name
	 */
	public String getQuestionName() {
		return questionName;
	}
	
	/**
	 * Sets the question name.
	 *
	 * @param questionName the new question name
	 */
	public void setQuestionName(String questionName) {
		this.questionName = questionName;
	}
	
	/**
	 * Checks if is hidden.
	 *
	 * @return true, if is hidden
	 */
	public boolean isHidden() {
		return hidden;
	}
	
	/**
	 * Sets the hidden.
	 *
	 * @param hidden the new hidden
	 */
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	public void setAdded(boolean added) {
		this.added = added;
	}
	
	public boolean isAdded() {
		return added;
	}
	
	public void setSavable(boolean savable) {
		this.savable = savable;
	}

	public boolean isSavable() {
		return savable;
	}
	
}
