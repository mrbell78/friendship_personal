package ngo.friendship.satellite.views;

import java.util.HashMap;


import android.app.Activity;
import android.content.Context;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.QUESTION_TYPE;
import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionAnswer;


public class QuestionViewFactory {

	/**
	 * Instantiates a new question view factory.
	 */
	private QuestionViewFactory() {
	}


	public static QuestionView createQuestionView(Context context, Question question , HashMap<String, QuestionAnswer> questionAnswerMap,Activity activity,String expression) {

		// return new ImmunizationChartView(context, question);
		if (question == null || question.getType() == null|| question.getType().trim().equalsIgnoreCase("") == true) {
			return null;
		}

		if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.STRING)) {

			if (question.getName().equals(Constants.QUESTIONNAIRE_IMAGE_FLAG)) {
				return new CustomImageView(context, question);
			}
			if (question.isSuggestion())
				return new AutoCompleteEditTextView(context, question, App.getContext().getDB().getNameList(question.getSuggestionType()));
			else
				return new EditTextView(context, question);
		} else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.NUMBER)) {
			return new EditTextView(context, question);
		} else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.SELECT)) {
			return new RadioGroupView(context, question);
		} else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.MULTI_SELECT)) {
			if(question.getExpression().equalsIgnoreCase("<POPUP_VIEW>")){
				return new ListCheckGroupView(context, question,questionAnswerMap,activity);
			}else if (question.getName().startsWith("SCHEDULE_CARD"))
				return new ScheduleChardView(context, question);
			else
				return new CheckGroupView(context, question);
		}
		else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.TIME)) {
			return new TimeView(context, question);
		}
		else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.DATE)) {
			return new DateView(context, question, App.getContext().getDB().getMonthList(App.getContext().getAppSettings().getLanguage()));
			//return new DateView(context, question, App.getContext().getDB().getBengaliNameMonthList(App.getContext().getAppSettings().getLanguage()));
		} else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.BARCODE)) {
			return new BarCodeView(context, question);
		} else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.LOCATION)) {
			return new LocationView(context, question);
		} else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.BINARY)) {
			if (question.getMediaType()==null || question.getMediaType().toString().trim().equalsIgnoreCase(""))
				return null;
			if (question.getMediaType().trim().equalsIgnoreCase(QUESTION_TYPE.AUDIO))
				return new AudioRecorderView(context, question);
			else if (question.getMediaType().trim().equalsIgnoreCase(QUESTION_TYPE.VIDEO))
				return new VideoView(context, question);
			else if (question.getMediaType().trim().equalsIgnoreCase(QUESTION_TYPE.IMAGE))
				return new CameraView(context, question);
			return null;
		}
		else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.REFERRAL_CENTER)) {
			return new ReferralCenterView(context, question);
		} else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.LIST)) {
			return new ListInfoView(context, question);
		} else if (question.getType().trim().equalsIgnoreCase(QUESTION_TYPE.PRESCRIPTION)) {
			return new PrescriptionView(context, question,questionAnswerMap,activity);
		}
		return null;
	}
}