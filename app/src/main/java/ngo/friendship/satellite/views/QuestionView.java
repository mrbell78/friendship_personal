package ngo.friendship.satellite.views;

import java.io.Serializable;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.model.Question;

// TODO: Auto-generated Javadoc
/**
 * The Class QuestionView.
 */
public abstract class QuestionView extends LinearLayout implements Serializable{

	/** The context. */
	protected Context context;

	/** The question. */
	protected Question question;

	/** The caption view. */
	protected TextView captionView;

	protected ImageButton radioResetButton;

	/** header container */

	protected LinearLayout ll_header_container;

	/** The hint view. */
	protected TextView hintView;

	/** The ll_header. */
	protected LinearLayout ll_header;

	/** The ll_body. */
	protected LinearLayout ll_body;

	/** The ll_footer. */
	protected LinearLayout ll_footer;

	/** The data set. */
	protected Object dataSet;

	/**
	 * Constructor.
	 *
	 * @param context The application context
	 * @param question The question object
	 */
	public QuestionView(Context context, Question question) {
		super(context);
		this.context = context;
		this.question = question;
	}

	/**
	 * Constructor.
	 *
	 * @param context The application context
	 * @param question The question object
	 * @param dataSet The data list. Which is used in adapter view like AutoCompleteText, Spinner etc.
	 */
	public QuestionView(Context context, Question question, Object dataSet) {
		super(context);
		this.context = context;
		this.question = question;
		this.dataSet = dataSet;
	}

	/**
	 * Initialize the parent layout by header, body footer layout.
	 */
	protected void init() {
		// MAIN LAYOUT
		this.setOrientation(LinearLayout.VERTICAL);
		this.setPadding(5, 10, 5, 10);
		LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		this.setLayoutParams(layoutParams);

		ScrollView scrollView =new ScrollView(context);
		scrollView.setLayoutParams(layoutParams);
		LinearLayout container =new LinearLayout(context);
		container.setLayoutParams(layoutParams);
		container.setOrientation(LinearLayout.VERTICAL);
		scrollView.addView(container);
		this.addView(scrollView);


		// HEADER LAYOUT
		ll_header=new LinearLayout(context);
		ll_header.setId(R.id.question_header);
		ll_header.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams header_params = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		header_params.setMargins(0, 0, 0, 5);
		ll_header.setLayoutParams(layoutParams);
		container.addView(ll_header);

		// header continer


		// BODY LAYOUT
		ll_body=new LinearLayout(context);
		ll_body.setId(R.id.question_body);
		ll_body.setOrientation(LinearLayout.VERTICAL);
		LayoutParams body_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		body_params.setMargins(0, 5, 0, 5);
		ll_body.setLayoutParams(layoutParams);
		if(question.getComponent()!=null){
			if(question.getComponent().getPosition()!=null && question.getComponent().getPosition().equalsIgnoreCase("TOP"))
			{
				container.addView(question.getComponent());
				container.addView(ll_body);
			}else{
				container.addView(ll_body);
				container.addView(question.getComponent());
			}

		}else{
			container.addView(ll_body);
		}




		// FOOTER LAYOUT
		ll_footer=new LinearLayout(context);
		ll_footer.setId(R.id.question_footer);
		ll_footer.setOrientation(LinearLayout.VERTICAL);
		LayoutParams footer_params = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		footer_params.setMargins(0, 5, 0, 0);
		ll_footer.setLayoutParams(layoutParams);
//		View view = new View(context);
//		LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3); // --> horizontal
//		lpView.setMargins(0, 32, 0, 16);
//
//		view.setBackgroundColor(context.getResources().getColor(R.color.ash_gray));
//		view.setLayoutParams(lpView);
//
//		((LinearLayout) ll_footer).addView(view);

		container.addView(ll_footer);



	}

	/**
	 * Set the question caption into view.
	 */
	protected void addCaptionField() {
		if(question.getCaption()==null||question.getCaption().trim().equalsIgnoreCase("")==true)
			return;
		this.captionView = new TextView(context);
		this.captionView.setPadding(5, 5, 5, 5);
		this.captionView.setTextSize(16);
		LayoutParams caption_params = new LayoutParams(0, LayoutParams.WRAP_CONTENT,2.5f);
		this.captionView.setLayoutParams(caption_params);
		this.captionView.setText(question.getCaption().toString());
		this.ll_header.addView(this.captionView);


	}

	/**
	 * Set question hint into view.
	 */
	protected void addHintField() {
		if(question.getHint()==null||question.getHint().trim().equalsIgnoreCase("")==true)
			return;

		this.hintView = new TextView(context);
		this.hintView.setPadding(5, 5,5, 5);
		this.hintView.setTextSize(16);
		this.hintView.setTextColor(Color.rgb(211, 129,0));
		this.hintView.setText(question.getHint().toString());
		this.ll_header.addView(this.hintView);

	}

	/**
	 * Add input view into parent layout.
	 */
	protected abstract void addInputField();

	/**
	 * Read the input view and return the data.
	 *
	 * @return The input data
	 */
	public abstract String getInputData();

	/**
	 * Set the user data to Input view.
	 *
	 * @param data The data to be set into view
	 */
	public abstract void setInputData(String data);

	/**
	 * Read the view and return the data list.
	 * Mostly used for those view which have more than one input (e.g Check Group)
	 * @return The input data object(e.g ArryList, String etc)
	 */
	public abstract Object getInputDataList();

	/**
	 * Check input data validity.
	 *
	 * @return <b>true</b> if data is valid. <b>false</b> otherwise
	 */
	public abstract boolean isValid(boolean isSingleForm);

	public abstract View getInputView();

	public abstract void replaceBody(Object data);
}
