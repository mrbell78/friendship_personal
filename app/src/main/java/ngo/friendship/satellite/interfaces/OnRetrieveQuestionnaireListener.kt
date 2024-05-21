package ngo.friendship.satellite.interfaces

import ngo.friendship.satellite.model.QuestionnaireList

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving onRetrieveQuestionnaire events.
 * The class that is interested in processing a onRetrieveQuestionnaire
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's `addOnRetrieveQuestionnaireListener` method. When
 * the onRetrieveQuestionnaire event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OnRetrieveQuestionnaireEvent
`` */
interface OnRetrieveQuestionnaireListener {
    /**
     * On retrieve questionnaire finished.
     *
     * @param questionnaireList the questionnaire list
     */
    fun onRetrieveQuestionnaireFinished(questionnaireList: QuestionnaireList?)
}