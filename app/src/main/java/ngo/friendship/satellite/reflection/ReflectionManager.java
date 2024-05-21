package ngo.friendship.satellite.reflection;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import ngo.friendship.satellite.model.Beneficiary;
import ngo.friendship.satellite.model.CCSBeneficiary;
import ngo.friendship.satellite.model.ImmunaizationBeneficiary;
import ngo.friendship.satellite.model.QuestionAnswer;
import ngo.friendship.satellite.model.ReferralCenterInfo;
import ngo.friendship.satellite.model.maternal.MaternalDelivery;
import ngo.friendship.satellite.model.maternal.MaternalInfo;
import ngo.friendship.satellite.model.maternal.MaternalService;
import ngo.friendship.satellite.ui.InterviewActivity;
import ngo.friendship.satellite.utility.TextUtility;


// TODO: Auto-generated Javadoc

/**
 * Manage java reflection.
 *
 * @author Mohammed Jubayer
 * @created 10th Nov 2014
 * @modified 10th Nov 2014
 */
public class ReflectionManager {

    /**
     * The expression.
     */
    private String expression;

    /**
     * The beneficiary.
     */
    private Beneficiary beneficiary;

    /**
     * The question answer map.
     */
    private HashMap<String, QuestionAnswer> questionAnswerMap;

    /**
     * The beneficiary type.
     */
    private String beneficiaryType;

    /**
     * The class name.
     */
    private String className = "";

    /**
     * The method name.
     */
    private String methodName = "";

    /**
     * The parameters.
     */
    private String[] parameters;

    /**
     * The ans.
     */
    private Object ans;

    /**
     * The ans list.
     */
    ArrayList<String> ansList = new ArrayList<String>();

    private InterviewActivity activity;


    /**
     * Instantiates a new reflection manager.
     *
     * @param expression        the expression
     * @param beneficiary       the beneficiary
//     * @param beneficiaryType   the beneficiary type
     * @param questionAnswerMap the question answer map
     */
    public ReflectionManager(InterviewActivity activity, String expression, Beneficiary beneficiary, HashMap<String, QuestionAnswer> questionAnswerMap) {
        super();
        this.expression = expression.trim();
        this.beneficiary = beneficiary;
        this.activity = activity;
        this.questionAnswerMap = questionAnswerMap;
    }

    /**
     * Gets the expression answer.
     *
     * @return the expression answer
     * @throws ClassNotFoundException    the class not found exception
     * @throws IllegalArgumentException  the illegal argument exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws InvocationTargetException the invocation target exception
     * @throws NoSuchMethodException     the no such method exception
     * @throws InstantiationException    the instantiation exception
     */
    public ArrayList<String> getExpressionAnswer() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        className = TextUtility.getClassName(expression);
        methodName = TextUtility.getMethodName(expression);
        parameters = TextUtility.getParameters(expression);

        Log.e("RE EXClassName: ", className);
        Log.e("RE EXmethodName: ", methodName);
//        for (int x = 1; x <= parameters.length; x++) {
//            Log.e("RE EX MANAGER parameters " + x + " : ", parameters[x - 1]);
//        }

        if (className.equalsIgnoreCase("InterviewActivity")) {
            if (parameters != null && parameters.length > 0 && parameters[0].trim().length() > 0) {
                ans = activity.getClass().getMethod(methodName, new Class[]{String[].class, HashMap.class}).invoke(activity, parameters, questionAnswerMap);
            } else {
                ans = activity.getClass().getMethod(methodName, null).invoke(activity, null);
            }
        } else if (className.equals("ReflectionUtility")) {
            Class refClass = Class.forName("ngo.friendship.satellite.reflection.ReflectionUtility");
            Object obj = refClass.newInstance();
            ans = refClass.getMethod(methodName, new Class[]{String[].class, HashMap.class}).invoke(obj, parameters, questionAnswerMap);

        } else if (className.equalsIgnoreCase("Beneficiary")) {
            if (beneficiary instanceof CCSBeneficiary) {
                ans = ((CCSBeneficiary) beneficiary).getClass().getMethod(methodName, null).invoke(beneficiary, null);
            } else if (beneficiary instanceof ImmunaizationBeneficiary) {
                ans = ((ImmunaizationBeneficiary) beneficiary).getClass().getMethod(methodName, null).invoke(beneficiary, null);
            } else if (beneficiary instanceof MaternalInfo) {
                if (methodName.equals("getWeightGainPerWeek")) {
                    ans = ((MaternalInfo) beneficiary).getClass().getMethod(methodName, new Class[]{String[].class, HashMap.class}).invoke(beneficiary, parameters, questionAnswerMap);
                } else if (methodName.equals("getMaternalServices")) { // beneficiary.getMaternalServices(ANC1).getProteinOfUrine()
                    String propertyName = TextUtility.getMethodName(expression, expression.indexOf(methodName) + 10);
                    String serviceName = parameters[0];
                    MaternalService servic = ((MaternalInfo) beneficiary).getMaternalServices().get(serviceName);
                    ans = servic.getClass().getMethod(propertyName, null).invoke(servic, null);
                } else if (methodName.equals("getMaternalDelivery")) {
                    String method_name = TextUtility.getMethodName(expression, expression.indexOf(methodName) + 10);
                    MaternalDelivery delivery = ((MaternalInfo) beneficiary).getMaternalDelivery();
                    ans = delivery.getClass().getMethod(method_name, null).invoke(delivery, null);
                } else if (methodName.equals("getMaternalBabyInfos")) {
                    String property = TextUtility.getMethodName(expression, expression.indexOf(methodName) + 10);
                    ans = ((MaternalInfo) beneficiary).getClass().getMethod(methodName, new Class[]{String.class, String.class}).invoke(beneficiary, parameters, property);
                } else {
                    ans = ((MaternalInfo) beneficiary).getClass().getMethod(methodName, null).invoke(beneficiary, null);
                }
            } else {
                if (parameters != null && parameters.length > 0 && parameters[0].trim().length() > 0) {
                    ans = beneficiary.getClass().getMethod(methodName, new Class[]{String[].class, HashMap.class}).invoke(beneficiary, parameters, questionAnswerMap);
                } else {
                    ans = beneficiary.getClass().getMethod(methodName, null).invoke(beneficiary, null);
                }
            }
        }
        if (ans != null && ans instanceof String || ans instanceof Integer || ans instanceof Long || ans instanceof Float || ans instanceof Double || ans instanceof Character) {
            ansList.add(ans + "");
        } else if (ans != null && ans instanceof ArrayList<?> && ((ArrayList<String>) ans).size() > 0 && ((ArrayList<String>) ans).get(0) instanceof String) {
            ansList = (ArrayList<String>) ans;
        }
        Log.e("RE EX MANAGER ANS: ", ansList.toString());
        return ansList;
    }

    public ArrayList<ReferralCenterInfo> getExpressionAnswerForReferralCenter() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        ArrayList<ReferralCenterInfo> ansList = new ArrayList<ReferralCenterInfo>();
        className = TextUtility.getClassName(expression);
        methodName = TextUtility.getMethodName(expression);
        parameters = TextUtility.getParameters(expression);

        Log.e("REEXMANAGERClassName: ", className);
        Log.e("REEXMANAGERmethodName: ", methodName);
//        for (int x = 1; x <= parameters.length; x++) {
//            Log.e("RE EX MANAGER parameters " + x + " : ", parameters[x - 1]);
//        }

        if (className.equals("ReflectionUtility")) {
            Class refClass = Class.forName("ngo.friendship.satellite.reflection.ReflectionUtility");
            Object obj = refClass.newInstance();
            ans = refClass.getMethod(methodName, new Class[]{String[].class, HashMap.class}).invoke(obj, parameters, questionAnswerMap);

        }
        if (ans != null && ans instanceof ArrayList<?>) {
            ansList = (ArrayList<ReferralCenterInfo>) ans;
        }
        Log.e("RE EX MANAGER ANS: ", ansList.toString());
        return ansList;
    }

}
