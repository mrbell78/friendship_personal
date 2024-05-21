package ngo.friendship.satellite.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.BuildConfig;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.model.Question;


public class CameraView extends QuestionView {

	private Button btnCapture;
	private ImageView imageView;	
	private  String URI = null;
	public static String tempPath=null;
	Activity activity;

	
	public CameraView(Context context, Question question) {
		super(context, question);
		activity = (Activity)context;
		// TODO Auto-generated constructor stub

		init();
		addCaptionField();
		addHintField();
		addInputField();
	}

	@Override
	protected void addInputField() {
		
		ll_body.setGravity(Gravity.CENTER);
		btnCapture = new Button(context);
		imageView  = new ImageView(context);
		btnCapture.setPadding(5, 5, 5, 5);
		btnCapture.setText(R.string.button_camera_caption);

		LayoutParams layoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		btnCapture.setLayoutParams(layoutParams);


		layoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(layoutParams);	
		imageView.setId(R.id.question_media_camera_view);
		imageView.setScaleType(ScaleType.FIT_START);
	

		btnCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
  
                tempPath = App.getContext().getBeneficiaryImageDir(activity)+File.separator+"img_tamp.jpg";
                File file = new File(tempPath);

				Uri outputFileUri=null;
				if (Build.VERSION.SDK_INT >= 24) {
					outputFileUri= FileProvider.getUriForFile(activity,
							BuildConfig.APPLICATION_ID + ".provider",
							file);
				}else{

					outputFileUri = Uri.fromFile(file);
				}




				Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
				CameraView.this.activity.startActivityForResult(captureIntent, Constants.REQUEST_CODE_CAPTURE_PHOTO);
			}
		});

		ll_body.addView(this.btnCapture);
		ll_body.addView(this.imageView);

		if(question.getUserInput() != null && question.getUserInput().size()>0)
			setInputData(question.getUserInput().get(0));

	}

	@Override
	public String getInputData() {
		return URI;
	}
	
    public  String makeUri(){
    	deleteFile();
        long timeInMilli = Calendar.getInstance().getTimeInMillis();
		String imageFileName = Constants.IMAGE_PATH+App.getContext().getUserInfo().getOrgCode()+"_"+App.getContext().getUserInfo().getUserCode()+"_"+timeInMilli + ".jpg";
		URI = App.getContext().getBeneficiaryImageDir(activity)+File.separator+imageFileName;
		
        return URI;
    }
    
    private void deleteFile(){
        try {
        	if(URI==null || URI.length()<=10){
             	File file =new File(URI);
             	file.delete();
     	     }
		} catch (Exception e) {
			// TODO: handle exception
		} 
    }

	@Override
	public boolean isValid(boolean isSingleForm) {
		if(question.isRequired()){
			if(URI==null || URI.length()<=0)
			{
				if (!isSingleForm){
					AppToast.showToast(context, R.string.input_required);
				}

				return false;
			}else{
				return true;
			}
		}else{
			
			return true;
		}
		
		
	}


	@Override
	public ArrayList<String> getInputDataList() {
		return null;
	}


	@Override
	public void setInputData(String imagePath) {
		if (imagePath != null && imagePath.length()>0) {
            URI = imagePath;
            imageView.setImageURI(null);
			imageView.setImageURI(Uri.fromFile(new File(URI)));
		}
	}

	@Override
	public View getInputView() {
	
		return null;
	} 

	@Override
	public void replaceBody(Object data) {
		
	}

}
