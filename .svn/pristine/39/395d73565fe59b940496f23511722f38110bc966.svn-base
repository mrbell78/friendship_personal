package ngo.friendship.satellite.utility;

// TODO: Auto-generated Javadoc

import java.util.Locale;

/**
 * The Class TextUtility.
 */
public class TextUtility {

	/**
	 * Return the empty string if the given String is null otherwise return the
	 * given string.
	 *
	 * @param str            The data string which will be checkd for null
	 * @return empty string or the given string
	 */
	public static String getEmptyStringFromNull(String str) {
		if (str == null)
			return "";
		else
			return str;
	}

	/**
	 * Convert the string into number and return. If the string is empty or not
	 * a number then return 0
	 * 
	 * @param str
	 *            The number in string format
	 * @return 0 or Converted number
	 */
	

	/**
	 * Gets the class name.
	 *
	 * @param text the text
	 * @return the class name
	 */
	public static String getClassName(String text) {
		text = text.trim();
		return text.substring(0, text.indexOf("."));
	}

	/**
	 * Gets the method name.
	 *
	 * @param text the text
	 * @return the method name
	 */
	public static String getMethodName(String text) {
		text = text.trim();
		return text.substring(text.indexOf(".") + 1, text.indexOf("("));
	}

	/**
	 * Gets the parameters.
	 *
	 * @param text the text
	 * @return the parameters
	 */
	public static String[] getParameters(String text) {
		text = text.trim();
		text=text.substring(text.indexOf("(") + 1, text.indexOf(")"));
		if(text.trim().length()>0){
		 return	text.split(",");
		}else {
		 return new String[0];
		}
	}
	/* Gets the method name.
	 *
	 * @param text the text
	 * @return the method name
	 */
	public static String getMethodName(String text, int after) {
        text = text.trim();
        int start=text.indexOf("." ,after) + 1;
        int end =text.indexOf("(",start);
		return text.substring(start,end);
	}

	/**
	 * Gets the string from long number.
	 *
	 * @param number the number
	 * @return the string from long number
	 */
	public static String getStringFromLongNumber(double number)
	{
		if (number == 0.00) {
			return "0.00";
		}
		
		String wholeNumStr = Double.toString(number);
		StringBuilder sb = new StringBuilder();
		sb.append(wholeNumStr.substring(0, wholeNumStr.indexOf(".")));
		sb.append(".");
		sb.append(wholeNumStr.substring(wholeNumStr.indexOf(".")+1, wholeNumStr.indexOf(".")+3));
		sb.append("...");
		sb.append(wholeNumStr.substring(wholeNumStr.length()-3, wholeNumStr.length()));
		
		return sb.toString();
	}
	public static String toCamelCase(String inputString) {
	       String result = "";
	       if (inputString.length() == 0) {
	           return result;
	       }
	       char firstChar = inputString.charAt(0);
	       char firstCharToUpperCase = Character.toUpperCase(firstChar);
	       result = result + firstCharToUpperCase;
	       for (int i = 1; i < inputString.length(); i++) {
	           char currentChar = inputString.charAt(i);
	           char previousChar = inputString.charAt(i - 1);
	           if (previousChar == ' ') {
	               char currentCharToUpperCase = Character.toUpperCase(currentChar);
	               result = result + currentCharToUpperCase;
	           } else {
	               char currentCharToLowerCase = Character.toLowerCase(currentChar);
	               result = result + currentCharToLowerCase;
	           }
	       }
	       return result;
	   }

	public static String format(String format, Object... args) {
		return String.format(Locale.ROOT,format, args);
	}
}
