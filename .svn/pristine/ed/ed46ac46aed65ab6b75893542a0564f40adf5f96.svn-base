package ngo.friendship.satellite;

import android.util.Log;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionBranch;

// TODO: Auto-generated Javadoc

/**
 * 
 * Description: Manage question list. Return current, next, previous question
 * Created date: 19th Jan 2014
 * Last Update: 21st Oct 2014
 * @author: Kayum Hossan
 **/



public class QuestionManager {

	//	private int questionStackIndex;
	/** The question key stack. */
	private Stack<String> questionKeyStack;
	
	/** The question list. */
	private ArrayList<Question> questionList;
	
	/** The current question. */
	private Question currentQuestion;
	
	/** The first question key. */
	private String firstQuestionKey;
	
	/** The previous key. */
	private String previousKey;
	
	/** The current key. */
	private String currentKey;

	public ArrayList<Question> getQuestionList() {
		return questionList;
	}

	public void setQuestionList(ArrayList<Question> questionList) {
		this.questionList = questionList;
	}

	public String getCurrentKey() {
		return currentKey;
	}
	public void setCurrentKey(String currentKey) {
		this.currentKey = currentKey;
	}
	
	
	
	public String getPreviousKey() {
		return previousKey;
	}
	
	public void setPreviousKey(String previousKey) {
		this.previousKey = previousKey;
	}
	
	public void setFirstQuestionKey(String firstQuestionKey) {
		this.firstQuestionKey = firstQuestionKey;
	}
	
	public String getFirstQuestionKey() {
		return firstQuestionKey;
	}
	
	
	public Stack<String> getQuestionKeyStack() {
		return questionKeyStack;
	}
	
	public void setQuestionKeyStack(Stack<String> questionKeyStack) {
		this.questionKeyStack = questionKeyStack;
	}
	
	
	/**
	 * Constructor .
	 *
	 * @param questionList Question list
	 * @param firstQuestionKey First question key
	 */
	public QuestionManager(ArrayList<Question> questionList, String firstQuestionKey)
	{
		this.questionList = questionList;
		this.firstQuestionKey = firstQuestionKey;
		questionKeyStack = new Stack<String>();
		//		questionStackIndex = 0;
	}

	public QuestionManager()
	{
	}


	/**
	 * Get question object by question key.
	 *
	 * @param key is the question key
	 * @return Question object
	 */
	public Question getQuestionByKey(String key)
	{
		if(key == null)
			return null;

		for(int i=0;i<questionList.size();i++)
		{
			if(questionList.get(i).getKey().equalsIgnoreCase(key))
				return questionList.get(i);
		}

		return null;
	}
	
	public void setCurrentQuestion(Question currentQuestion) {
		this.currentQuestion = currentQuestion;
	}
	

	/**
	 * Get next question key by current question's answer.
	 *
	 * @param val is the current question answer
	 * @return next question key if any
	 */
	private String getNextQuestionKeyByValue(String val)
	{

		if(currentQuestion == null )
			return null;

		ArrayList<QuestionBranch> branchList = currentQuestion.getBranchList();

		boolean matched = false;
		int matchedIndex=-1;
		int questionWithoutValIndex = -1;
		
		for(int i=0;i<branchList.size();i++)
		{
			if((val == null || val.equalsIgnoreCase("")))
			{
				/**
				 * Check if any brunch rule has any as rule value
				 */
				if(branchList.get(i).getRule().equalsIgnoreCase("any"))
					return branchList.get(i).getNextQuestionKey();
			}
			else
			{
				if(branchList.get(i).getValue() == null || branchList.get(i).getValue().equalsIgnoreCase(""))
				{
					questionWithoutValIndex = i;
					continue;
				}

				/**
				 * Check if value matched with branch rule value
				 */
				if(branchList.get(i).getValue().trim().equalsIgnoreCase(val.trim()))
				{
					matched = true;
					matchedIndex = i;
					break;
				}

			}
		}

		if(matched)
		{
			/**
			 * Value match found at matchedIndex so return the next question key 
			 */
			return branchList.get(matchedIndex).getNextQuestionKey();
		}
		else if(questionWithoutValIndex >= 0)
		{
			return branchList.get(questionWithoutValIndex).getNextQuestionKey();
		}
		else
			return null;
	}


	/**
	 * get the first question.
	 *
	 * @return Object of first question
	 */
	public Question getFirstQuestion()
	{
		Question question = getQuestionByKey(firstQuestionKey);

		if(question != null)
			currentQuestion = question;

		currentKey = firstQuestionKey;
		previousKey = null;
		return question;
	}
	
//	public Question getQuestion(String key)
//	{
//		Question question = getQuestionByKey(key);
//
//		if(question != null)
//			currentQuestion = question;
//
//		currentKey = key;
//		previousKey = null;
//		return question;
//	}

	/**
	 * Get Next question based on the answer of current question.
	 *
	 * @param val The answer of current question
	 * @return next question object
	 */
	public Question getNextQuestion(String val)
	{
		previousKey = currentKey;
		currentKey = getNextQuestionKeyByValue(val);
		Question question = getQuestionByKey(currentKey);
		currentQuestion = question;

		pushQuestionKey(previousKey);
		return currentQuestion;
	}

	/**
	 * Get next question key by current question's answer. 
	 * Mainly used for those question which has multiple answers (Radio, check box, date field).
	 * Custom branching rule(Minimum, maximum, and, or, greater, less etc) is checked in this method.
	 * @param valList is the current question's answer list
	 * @return next question key if any
	 */
	private String getNextQuestionKeyByValue(ArrayList<String> valList)
	{
		if(currentQuestion == null )
			return null;

		ArrayList<QuestionBranch> branchList = currentQuestion.getBranchList();
		if(branchList == null) 	return null;

		//// Searching for value field//////



		for(int i=0;i<branchList.size() && valList != null;i++)
		{
			/**
			 * Check if brunch has some value in value field
			 */
			if(branchList.get(i).getValue() != null && !branchList.get(i).getValue().equalsIgnoreCase(""))
			{
//				Log.e("Rule", branchList.get(i).getRule());
				double conditionVal = -1;
				String conditionRul =branchList.get(i).getRule();
				try
				{
					conditionVal = Double.parseDouble(branchList.get(i).getValue());
				}
				catch (NumberFormatException e)
				{}

				/**
				 * Check for greater or less value branching 
				 */
				for(int j=0;valList!= null && j<valList.size();j++)
				{

					/**
					 * Check less/greater branching 
					 */
					if(branchList.get(i).getRule().equalsIgnoreCase("less")) // for number 
					{
						try {

							if(Double.parseDouble(valList.get(j)) < conditionVal){
								return branchList.get(i).getNextQuestionKey();
							}		
						} catch (Exception e) {
							// TODO: handle exception
						}
					}	
					else if(branchList.get(i).getRule().equalsIgnoreCase("greater")) // for number 
					{
						
						try {

							if(Double.parseDouble(valList.get(j)) > conditionVal){
								return branchList.get(i).getNextQuestionKey();
							}		
						} catch (Exception e) {
							// TODO: handle exception
						}
						
					}else if(branchList.get(i).getRule().equalsIgnoreCase("is")) // for number  and string 
					{
						if(branchList.get(i).getValue().trim().equalsIgnoreCase(valList.get(j).trim())){
								return branchList.get(i).getNextQuestionKey();
					    }else if(branchList.get(i).getValue().trim().equals("<EMPTY>") && valList.get(j).trim().equals("")){
					    	return branchList.get(i).getNextQuestionKey();
					    }
					}
					else if(branchList.get(i).getRule().equalsIgnoreCase("not")) // for number  and string
					{
						if(!branchList.get(i).getValue().trim().equalsIgnoreCase(valList.get(j).trim())){
								return branchList.get(i).getNextQuestionKey();
						}else if(branchList.get(i).getValue().trim().equals("<EMPTY>") && !valList.get(j).trim().equals("") ){
							return branchList.get(i).getNextQuestionKey();
					    }		
						
					}
					else if(branchList.get(i).getValue().equalsIgnoreCase(valList.get(j)))
					{
						return branchList.get(i).getNextQuestionKey();
					}
				}
			}
		}

		/**
		 * Check for maximum, minimum, exact number of selection. And OR condition also checked here
		 */
		for(int i=0;i<branchList.size() && valList != null;i++)
		{

			/**
			 * Check if the branch rule is calc type. This is set if the question has custom calculation type rule
			 */
			if(branchList.get(i).getRule().equalsIgnoreCase("calc") &&   branchList.get(i).getCalcValue()!=null)
			{

				String calcVal = branchList.get(i).getCalcValue().toUpperCase().trim();

				/*
				 * Check for minimum number of selection
				 */
				if(calcVal.startsWith("MIN_SEL")) 
				{
					try
					{
						/// Get the minimum number
						int minSel = Integer.parseInt(calcVal.split("=")[1].trim());
						if(valList.size() >= minSel)
						{
							return branchList.get(i).getNextQuestionKey();
						}
					}
					catch(Exception e)
					{
						return null;
					}
				}
				/**
				 * Check of exact number of selection
				 */
				else if(calcVal.startsWith("NUM_SEL"))
				{
					try
					{
						/// Get the exact number
						int exactSel = Integer.parseInt(calcVal.split("=")[1].trim());
						if(valList.size() == exactSel)
						{
							return branchList.get(i).getNextQuestionKey();
						}
					}
					catch(Exception e)
					{
						return null;
					}
				}
				/*
				 * Check for maximum number of selection
				 */
				else if(calcVal.startsWith("MAX_SEL"))
				{
					try
					{
						/// Get the exact number
						int maxSel = Integer.parseInt(calcVal.split("=")[1].trim());
						if(valList.size() <= maxSel)
						{
							return branchList.get(i).getNextQuestionKey();
						}
					}
					catch(Exception e)
					{
						return null;
					}
				}
				/**
				 * Check of multiple specific item selecion
				 */
				else if(calcVal.startsWith("AND"))
				{
					calcVal = calcVal.split("=")[1].trim();
					String [] conditionValues = calcVal.split("&&");
					boolean matched = true;
					for(int j=0;j<conditionValues.length && matched;j++)
					{
						String conditionValue = conditionValues[j].toUpperCase().trim();

						/* Find condition value in user selected value list. Initialize matched by false 
						 * for each condition value. if matched remain false after following loop execution 
						 * this loop will be stopped since matched must be true for this loop to be continue.
						 */
						matched = false;
						for(int k=0; k<valList.size(); k++)
						{
							if(conditionValue.equalsIgnoreCase(valList.get(k).toUpperCase().trim()))
							{
								matched = true;
								break;
							}
						}
					}
					if(matched)
						return  branchList.get(i).getNextQuestionKey();
				}
				/**
				 * 
				 */
				else if(calcVal.startsWith("OR"))
				{


					calcVal = calcVal.split("=")[1].trim();
					String [] conditionValues = calcVal.split(Pattern.quote("||"));
					boolean matched = false;
					for(int j=0;j<conditionValues.length && !matched;j++)
					{

						String conditionValue = conditionValues[j].toUpperCase().trim();
						Log.e("Next Question","CalcVal: "+calcVal+" User Input: "+valList.get(0)+" ConditionValu: "+conditionValue);
						/* Find condition value in user selected value list. Initialize matched by false 
						 * for each condition value. if matched remain false after following loop execution 
						 * this loop will be stopped since matched must be true for this loop to be continue.
						 */
						matched = false;
						for(int k=0; k < valList.size(); k++)
						{
							//Log.e("Next Question","CalcVal: "+calcVal+" User input: "+valList.get(k));
							if(valList.get(k) != null && conditionValue.equalsIgnoreCase(valList.get(k).toUpperCase().trim()))
							{
								matched = true;
								break;
							}
						}
					}
					if(matched)
						return  branchList.get(i).getNextQuestionKey();
				}
			}
		}

		/**
		 *  No value matched or number of selection item found so check for any
		 * 
		 */
		for(int i=0;i<branchList.size();i++)
		{
			if(branchList.get(i).getRule().equalsIgnoreCase("any"))
				return  branchList.get(i).getNextQuestionKey();
		}
		return null;
	}

	/**
	 * Get Next question based on the answer of current question.
	 *
	 * @param valList is the answer list of current question
	 * @return next question object
	 */
	public Question getNextQuestion(ArrayList<String> valList)
	{
		previousKey = currentKey;
		currentKey = getNextQuestionKeyByValue(valList);
		Question question = getQuestionByKey(currentKey);
		currentQuestion = question;

		pushQuestionKey(previousKey);
		return currentQuestion;
	}

	/**
	 * Get previous question.
	 *
	 * @return Previous question object
	 */
	public Question getPrevQuestion()
	{
		currentKey = popQuestionKey();
		Question question = getQuestionByKey(currentKey);
		currentQuestion = question;
		return currentQuestion;
	}

	/**
	 * Push the key into stack.
	 *
	 * @param key is the question key
	 */

	private void pushQuestionKey(String key)
	{
		questionKeyStack.push(key);
	}

	/**
	 * Get the current key from stack.
	 *
	 * @return current question key if the stack size is greater that 1. Otherwise key of first question
	 */
	private String popQuestionKey()
	{
		if(questionKeyStack.size()>0)
			return questionKeyStack.pop();
		else
			return firstQuestionKey;
	}

	/**
	 * Get  remaining questions after current question. Current question also included in the list.
	 * Maintain a stack of question to get all child node of question
	 * @return The question list
	 */
	public ArrayList<Question> getRemainingQuestionList()
	{
		ArrayList<Question> questionList = new ArrayList<Question>();

		Stack<String> questionKeyStack = new Stack<String>();
		addNextQuestionsKeyToStack(currentQuestion, questionKeyStack);

		while(!questionKeyStack.isEmpty())
		{
			/**
			 * Get question key
			 */
			String questionKey = questionKeyStack.pop();
			
			/**
			 * Get question object by key
			 */
			Question question = getQuestionByKey(questionKey);
			if(question != null)
			{
				questionList.add(question);
				addNextQuestionsKeyToStack(question, questionKeyStack);
			}
		}

		return questionList;
	}
	
	/**
	 * Add all child questions to stack.
	 *
	 * @param question The question whose child will be searched
	 * @param questionKeyStack The question stack in which child will be inserted
	 */
	private void addNextQuestionsKeyToStack(Question question, Stack<String> questionKeyStack)
	{
		/**
		 * Search the branch of question to get child list and insert question key into stack
		 */
		for(int i=0;i<question.getBranchList().size();i++)
		{
			questionKeyStack.push(question.getBranchList().get(i).getNextQuestionKey());
		}
	}

	/**
	 * Append questions after question list.
	 *
	 * @param questionsToBeAppended Questions to be added
	 */
	public void appendQuestionList(ArrayList<Question> questionsToBeAppended)
	{
		String firstQuestionkey = questionsToBeAppended.get(0).getKey();

		for(int i=0;i<questionList.size();i++)
		{
			ArrayList<QuestionBranch> branchList = questionList.get(i).getBranchList();
			for(int j=0; j<branchList.size(); j++)
			{
				if(getQuestionByKey(branchList.get(j).getNextQuestionKey())==null)
				{
					branchList.get(j).setNextQuestionKey(firstQuestionkey);
				}
			}
		}
		questionList.addAll(questionList.size(), questionsToBeAppended);
	}

	public String getNextQuestionKeyByValueForSingleForm(Question qtn, String val)
	{
		if(qtn == null )
			return null;

		ArrayList<QuestionBranch> branchList = qtn.getBranchList();

		boolean matched = false;
		int matchedIndex=-1;
		int questionWithoutValIndex = -1;

		for(int i=0;i<branchList.size();i++)
		{
			if((val == null || val.equalsIgnoreCase("")))
			{
				/**
				 * Check if any brunch rule has any as rule value
				 */
				if(branchList.get(i).getRule().equalsIgnoreCase("any"))
					return branchList.get(i).getNextQuestionKey();
			}
			else
			{
				if(branchList.get(i).getValue() == null || branchList.get(i).getValue().equalsIgnoreCase(""))
				{
					questionWithoutValIndex = i;
					continue;
				}

				/**
				 * Check if value matched with branch rule value
				 */
				if(branchList.get(i).getValue().trim().equalsIgnoreCase(val.trim()))
				{
					matched = true;
					matchedIndex = i;
					break;
				}

			}
		}

		if(matched)
		{
			/**
			 * Value match found at matchedIndex so return the next question key
			 */
			return branchList.get(matchedIndex).getNextQuestionKey();
		}
		else if(questionWithoutValIndex >= 0)
		{
			return branchList.get(questionWithoutValIndex).getNextQuestionKey();
		}
		else
			return null;
	}


	public String getNextQuestionKeyByReferValueForSingleForm(Question qtn, String val)
	{
		if(qtn == null )
			return null;

		ArrayList<QuestionBranch> branchList = qtn.getBranchList();

		boolean matched = false;
		int matchedIndex=-1;
		int questionWithoutValIndex = -1;

		for(int i=0;i<branchList.size();i++)
		{
			if((val == null || val.equalsIgnoreCase("")))
			{
				/**
				 * Check if any brunch rule has any as rule value
				 */
				if(branchList.get(i).getRule().equalsIgnoreCase("any"))
					return branchList.get(i).getNextQuestionKey();
			}
			else
			{
				if(branchList.get(i).getValue() == null || branchList.get(i).getValue().equalsIgnoreCase(""))
				{
					questionWithoutValIndex = i;
					continue;
				}

				/**
				 * Check if value matched with branch rule value
				 */
				if(branchList.get(i).getRule().trim().equalsIgnoreCase("option"+val.trim()))
				{
					matched = true;
					matchedIndex = i;
					break;
				}

			}
		}

		if(matched)
		{
			/**
			 * Value match found at matchedIndex so return the next question key
			 */
			return branchList.get(matchedIndex).getNextQuestionKey();
		}
		else if(questionWithoutValIndex >= 0)
		{
			return branchList.get(questionWithoutValIndex).getNextQuestionKey();
		}
		else
			return null;
	}

	public Question getQuestionByKeyForSingleForm(ArrayList<Question> questionLists,String key)
	{
		if(key == null)
			return null;

		for(int i=0;i<questionLists.size();i++)
		{
			if(questionLists.get(i).getKey().equalsIgnoreCase(key))
				return questionLists.get(i);
		}

		return null;
	}

}
