package ngo.friendship.satellite.model;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * The Class QuestionBranch.
 */
public class QuestionBranch implements Serializable{
	
	/** The rule. */
	private String rule;
	
	/** The value. */
	private String value;
	
	/** The next question key. */
	private String nextQuestionKey;
	
	/** The calc value. */
	private String calcValue;
	
	
	/**
	 * Sets the calc value.
	 *
	 * @param calcValue the new calc value
	 */
	public void setCalcValue(String calcValue) {
		this.calcValue = calcValue;
	}
	
	/**
	 * Gets the calc value.
	 *
	 * @return the calc value
	 */
	public String getCalcValue() {
		return calcValue;
	}
	
	/**
	 * Sets the next question key.
	 *
	 * @param nextQuestionKey the new next question key
	 */
	public void setNextQuestionKey(String nextQuestionKey) {
		this.nextQuestionKey = nextQuestionKey;
	}
	
	/**
	 * Gets the next question key.
	 *
	 * @return the next question key
	 */
	public String getNextQuestionKey() {
		return nextQuestionKey;
	}
	
	/**
	 * Sets the rule.
	 *
	 * @param rule the new rule
	 */
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	/**
	 * Gets the rule.
	 *
	 * @return the rule
	 */
	public String getRule() {
		return rule;
	}
	
	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
