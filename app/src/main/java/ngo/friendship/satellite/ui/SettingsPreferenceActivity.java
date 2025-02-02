package ngo.friendship.satellite.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.asynctask.CommiunicationTask;
import ngo.friendship.satellite.asynctask.TaskKey;
import ngo.friendship.satellite.asynctask.async.AsyncTask;
import ngo.friendship.satellite.communication.RequestData;
import ngo.friendship.satellite.communication.ResponseData;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.Constants;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.constants.RequestName;
import ngo.friendship.satellite.constants.RequestType;
import ngo.friendship.satellite.interfaces.OnCompleteListener;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.model.MyPreferenceCategory;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.SystemUtility;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.WebServerCommunication;
import ngo.friendship.satellite.views.DialogView;

public class SettingsPreferenceActivity extends PreferenceActivity implements OnPreferenceChangeListener {

	private final int VOID_DATA_VERSION=1;
	private final int REMOVE_LOCAL_DATA=2;
	private final int UPLOAD_LOCAL_DATABASE=3;
	ProgressDialog dlog;

	String prevLangCode;

	/** The prev host. */
	String prevHost ;
	/** The prev alter host. */
	String prevAlterHost ;
	/** The cur host. */
	String curHost ;
	/** The cur alter host. */
	String curAlterHost ;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.loadApplicationData(this);


		setContentView(R.layout.preference_layout);
		setPreferenceScreen(createPreferenceHierarchy());
		getListView().setCacheColorHint(Color.TRANSPARENT);

		setTitle("" + getResources().getString(R.string.upcoming_schedule));





		/*
		 * Change the preference screen color to white background and black text
		 */
		setTheme(R.style.BlackText);
		getListView().setBackgroundColor(Color.WHITE);



		prevLangCode = App.getContext().getAppSettings().getLanguage();
		prevHost = App.getContext().getAppSettings().getHostAddress();
		prevAlterHost = App.getContext().getAppSettings().getAlternateHostAddress();
		curHost = App.getContext().getAppSettings().getHostAddress();
		curAlterHost = App.getContext().getAppSettings().getAlternateHostAddress();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (App.getContext().getAppSettings() == null)
			App.getContext().readAppSettings(this);
   

		

		if(!SystemUtility.isAutoTimeEnabled(this)){
			SystemUtility.openDateTimeSettingsActivity(this);
		}
	
	}





	
	/**
	 * Show Gate way dialog.
	 */
	private void showMHealthGateWayDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		View view = View.inflate(this, R.layout.mhealth_gateway_dialog_layout,null);
		builder.setView(view);
		final AlertDialog dialog = builder.create();

		final EditText etUrl1 = view.findViewById(R.id.et_url_1);
		final EditText etUrl2 = view.findViewById(R.id.et_url_2);

		App.loadApplicationData(this);
		
		etUrl1.setText(App.getContext().getAppSettings().getHostAddress());
		etUrl2.setText(App.getContext().getAppSettings().getAlternateHostAddress());

		Button btnCancel = view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});

		Button btnSave = view.findViewById(R.id.btn_save);
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				/*
				 * Save user input
				 */
				String hostAddr = etUrl1.getText().toString().trim();
				String alternateAddr = etUrl2.getText().toString().trim();

				App.getContext().getAppSettings().setHostAddress(hostAddr);
				App.getContext().getAppSettings().setAlternateHostAddress(alternateAddr);

				AppPreference.putString(SettingsPreferenceActivity.this, KEY.MHEALTH_SERVER, hostAddr);
				AppPreference.putString(SettingsPreferenceActivity.this, KEY.ALTERNATIVE_SERVER,alternateAddr);
				curHost = hostAddr;
				curAlterHost = alternateAddr;
				dialog.dismiss();

				
			}
		});

		Button btnCheckUrl1 = view.findViewById(R.id.btn_check_url_1);
		btnCheckUrl1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				checkURL(etUrl1.getText().toString().trim());
			}
		});

		Button btnCheckUrl2 = view.findViewById(R.id.btn_check_url_2);
		btnCheckUrl2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkURL(etUrl2.getText().toString().trim());
			}
		});
		dialog.setCancelable(false);
		dialog.show();
	}

	/**
	 * Check the gate way if it is working.
	 * 
	 * @param url
	 *            is the gate way URL
	 */
	private void checkURL(String url) {
		if (SystemUtility
				.isConnectedToInternet(SettingsPreferenceActivity.this)) {

			if (url.equalsIgnoreCase("")) {
				showOneButtonDialog(R.string.dialog_title, "Empty URL field",
						Color.RED, R.string.btn_close, R.drawable.error);
			} else if (!URLUtil.isValidUrl(url)) {
				showOneButtonDialog(R.string.dialog_title, "Invalid URL",
						Color.RED, R.string.btn_close, R.drawable.error);
			} else {
				dlog = ProgressDialog.show(SettingsPreferenceActivity.this,
						"Checking server", "Please wait...");
				new CheckUrlTask(url).execute();
			}
		} else {
			SystemUtility
			.openInternetSettingsActivity(SettingsPreferenceActivity.this);
		}
	}

	
//	class CheckUrlTask extends AsyncTask<Void, Integer, String> {
//
//
//		String url;
//
//		int colorId, imageId;
//
//		public CheckUrlTask(String url) {
//			this.url = url;
//		}
//
//		@Override
//		protected String doInBackground(Void unused) throws Exception {
//			int statusCode = -1;
//			try {
//				statusCode = WebServerCommunication.checkGateway(url);
//			} catch (HttpHostConnectException e) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("Application unavailable.");
//				sb.append("\n");
//				sb.append(e.getMessage());
//
//				colorId = Color.RED;
//				imageId = R.drawable.error;
//				e.printStackTrace();
//				return sb.toString();
//			} catch (ConnectTimeoutException e) {
//
//				StringBuilder sb = new StringBuilder();
//				sb.append("Server unreachable.");
//				sb.append("\n");
//				sb.append(e.getMessage());
//
//				colorId = Color.RED;
//				imageId = R.drawable.error;
//				e.printStackTrace();
//				return sb.toString();
//			} catch (SocketTimeoutException e) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("Server unreachable.");
//				sb.append("\n");
//				sb.append(e.getMessage());
//
//				colorId = Color.RED;
//				imageId = R.drawable.error;
//				e.printStackTrace();
//				return sb.toString();
//			} catch (Exception e) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("Uncategorize exception");
//				sb.append("\n");
//				sb.append(e.getMessage());
//
//				colorId = Color.RED;
//				imageId = R.drawable.error;
//				e.printStackTrace();
//				return sb.toString();
//			}
//
//			if (statusCode > 0) {
//				if (statusCode == 200) {
//					colorId = Color.BLACK;
//					imageId = R.drawable.information;
//					return "200 - Server is working";
//				} else {
//					colorId = Color.RED;
//					imageId = R.drawable.error;
//					return statusCode + " - Something is wrong at the server";
//				}
//			} else {
//				colorId = Color.RED;
//				imageId = R.drawable.error;
//				return "Server not found";
//			}
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			super.onPostExecute(result);
//			dlog.dismiss();
//			showOneButtonDialog(R.string.dialog_title, result, colorId,
//					R.string.btn_close, imageId);
//
//		}
//
//		@Override
//		protected void onBackgroundError(Exception e) {
//
//		}
//
////	    public void execute(){
////		    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
////					super.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
////				}else {
////					super.execute();
////			    }
////	    }
//
//	}

	class CheckUrlTask extends AsyncTask<Void, Void, String> {

		String url;

		int colorId, imageId;

		public CheckUrlTask(String url) {
			this.url = url;
		}

		@Override
		protected String doInBackground(Void params) {
			int statusCode = -1;
			try {
				statusCode = WebServerCommunication.checkGateway(url);
			} catch (HttpHostConnectException e) {
				StringBuilder sb = new StringBuilder();
				sb.append("Application unavailable.");
				sb.append("\n");
				sb.append(e.getMessage());

				colorId = Color.RED;
				imageId = R.drawable.error;
				e.printStackTrace();
				return sb.toString();
			} catch (ConnectTimeoutException e) {

				StringBuilder sb = new StringBuilder();
				sb.append("Server unreachable.");
				sb.append("\n");
				sb.append(e.getMessage());

				colorId = Color.RED;
				imageId = R.drawable.error;
				e.printStackTrace();
				return sb.toString();
			} catch (SocketTimeoutException e) {
				StringBuilder sb = new StringBuilder();
				sb.append("Server unreachable.");
				sb.append("\n");
				sb.append(e.getMessage());

				colorId = Color.RED;
				imageId = R.drawable.error;
				e.printStackTrace();
				return sb.toString();
			} catch (Exception e) {
				StringBuilder sb = new StringBuilder();
				sb.append("Uncategorize exception");
				sb.append("\n");
				sb.append(e.getMessage());

				colorId = Color.RED;
				imageId = R.drawable.error;
				e.printStackTrace();
				return sb.toString();
			}

			if (statusCode > 0) {
				if (statusCode == 200) {
					colorId = Color.BLACK;
					imageId = R.drawable.information;
					return "200 - Server is working";
				} else {
					colorId = Color.RED;
					imageId = R.drawable.error;
					return statusCode + " - Something is wrong at the server";
				}
			} else {
				colorId = Color.RED;
				imageId = R.drawable.error;
				return "Server not found";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dlog.dismiss();
			showOneButtonDialog(R.string.dialog_title, result, colorId,
			R.string.btn_close, imageId);

		}

		@Override
		protected void onBackgroundError(Exception e) {

		}

	}


	private void showOneButtonDialog(int titleId, String msg, int msgColorId,
			int buttonTextId, int imageId) {
		HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
		buttonMap.put(1, buttonTextId);
		DialogView dialog = new DialogView(this, titleId, msg,
				msgColorId, imageId, buttonMap);
		dialog.show();
	}

	/**
	 * Create preference screen.
	 * 
	 * @return the preference screen
	 */
	private PreferenceScreen createPreferenceHierarchy() {


		PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
		MyPreferenceCategory generalCat = new MyPreferenceCategory(this);
		generalCat.setTitle("General");
		root.addPreference(generalCat);

		/*
		 * Mhealth server
		 */
		PreferenceScreen mHealthServer = getPreferenceManager().createPreferenceScreen(this);
		mHealthServer.setTitle("Satellite Clinic Server");
		mHealthServer.setSummary("FCM Gateway URL");
		mHealthServer.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference arg0) {
				showMHealthGateWayDialog();
				return false;
			}
		});
		generalCat.addPreference(mHealthServer);

		/*
		 * FCM code
		 */
		PreferenceScreen fcmCode = getPreferenceManager().createPreferenceScreen(this);
		fcmCode.setTitle("FCM Code");

		if (App.getContext().getUserInfo().getUserCode().isEmpty())
			fcmCode.setSummary("Unknown");
		else
			fcmCode.setSummary(App.getContext().getUserInfo().getUserCode());

		generalCat.addPreference(fcmCode);

		
		/*
		 * Household data collection
		 */



		/*
		 * Household data collection
		 */

		CheckBoxPreference useNetworkprovidedTimePref = new CheckBoxPreferenceMultiLineTitle(this);
		useNetworkprovidedTimePref.setTitle("Use network provided time");
		useNetworkprovidedTimePref.setSummary("Automatic date time and time zone must be enable.");
		useNetworkprovidedTimePref.setDefaultValue(App.getContext().getAppSettings().isUseNetworkProvidedTime());
		useNetworkprovidedTimePref.setChecked(false);//App.getContext().getAppSettings().isUseNetworkProvidedTime());
		useNetworkprovidedTimePref.setKey(KEY.USE_NETWORK_PROVIDED_TIME);
		useNetworkprovidedTimePref.setOnPreferenceChangeListener(this);
		generalCat.addPreference(useNetworkprovidedTimePref);




		/*
		 * language
		 */
		MyPreferenceCategory lngCat = new MyPreferenceCategory(this);
		lngCat.setTitle("Language & Data");
		root.addPreference(lngCat);
		
		/*
		 * Language preference
		 */
		ListPreference languagePref = new ListPreference(this);
		languagePref.setTitle("Application Language");
		languagePref.setEntries(R.array.entries_language);
		languagePref.setEntryValues(R.array.entrievalues_language);
		languagePref.setDefaultValue(AppSettings.DEFAULT_LANGUAGE);
		int langIndex = languagePref.findIndexOfValue(App.getContext().getAppSettings().getLanguage());
		String langentry = (String) languagePref.getEntries()[langIndex];

		languagePref.setSummary("Changing language voids data version \n"+langentry);
		languagePref.setDialogTitle("Application Language");
		languagePref.setKey(KEY.LANGUAGE);
		languagePref.setOnPreferenceChangeListener(this);
		lngCat.addPreference(languagePref);



		PreferenceScreen clearversion = getPreferenceManager().createPreferenceScreen(this);
		clearversion.setTitle("Void data version");
		clearversion.setSummary("Press here for remove data version.");
		clearversion.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showConfermationAndExecute("Make sure you want to void data version ?",VOID_DATA_VERSION);
				return false;
			}
		});
		
		lngCat.addPreference(clearversion);

		PreferenceScreen removeLocalData = getPreferenceManager().createPreferenceScreen(this);
		removeLocalData.setTitle("Remove local data");
		removeLocalData.setSummary("Press here for remove local data.");
		removeLocalData.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {

                 showConfermationAndExecute("Make sure you want to remove local data ?",REMOVE_LOCAL_DATA);

				return false;
			}
		});
		lngCat.addPreference(removeLocalData);



		PreferenceScreen uploadLocalDatabase = getPreferenceManager().createPreferenceScreen(this);
		uploadLocalDatabase.setTitle("Upload local database");
		uploadLocalDatabase.setSummary("Press here for upload local database.");
		uploadLocalDatabase.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {

				showConfermationAndExecute("Make sure you want to upload local database ?",UPLOAD_LOCAL_DATABASE);

				return false;
			}
		});
		lngCat.addPreference(uploadLocalDatabase);


		/*
		 * Doctor's feedback
		 */
		MyPreferenceCategory dfCat = new MyPreferenceCategory(this);
		dfCat.setTitle("Doctor's feedback");
		root.addPreference(dfCat);



		boolean vodf= AppPreference.getBoolean(this, KEY.VIBRATE_ON_DOCROT_FEEDBACK, true);

		CheckBoxPreference vibrateOnDocFeedbackPref = new CheckBoxPreferenceMultiLineTitle(this);
		vibrateOnDocFeedbackPref.setTitle("Vibrate on feedback");
		vibrateOnDocFeedbackPref.setSummary("Vibrate device when doctor's feedback arrived");
		vibrateOnDocFeedbackPref.setDefaultValue(vodf);
		vibrateOnDocFeedbackPref.setChecked(vodf);
		vibrateOnDocFeedbackPref.setKey(KEY.VIBRATE_ON_DOCROT_FEEDBACK);
		vibrateOnDocFeedbackPref.setOnPreferenceChangeListener(this);
		dfCat.addPreference(vibrateOnDocFeedbackPref);

		
	

		
		
		
		if (App.getContext().getAppSettings().getFcmConfigration()!= null && !App.getContext().getAppSettings().getFcmConfigration().equals("")) {
			try{
				JSONArray jconfigArr=new JSONArray(App.getContext().getAppSettings().getFcmConfigration());
				for(int index=0 ;index<jconfigArr.length();index++)
				{
					JSONObject jConfigObj = jconfigArr.getJSONObject(index);
					if(jConfigObj.has("name")){
						MyPreferenceCategory cat = new MyPreferenceCategory(this);
						cat.setTitle(TextUtility.toCamelCase(jConfigObj.getString("name").replace("_"," ")));
						root.addPreference(cat);
						
						JSONArray jItemArr = jConfigObj.getJSONArray("items");
						for(int i=0; i<jItemArr.length(); i++)
						{
							JSONObject jItemObj = jItemArr.getJSONObject(i);
							if(jItemObj.has("caption") && jItemObj.has("value"))
							{
								Preference ps =new PreferenceScreenMultiLineTitle(this);
								ps.setTitle(jItemObj.getString("caption"));
								ps.setSummary(jItemObj.getString("value"));
								cat.addPreference(ps);
							}
						}
						
					}
				}
			}
			catch(JSONException exception){
				
			}
		}

		return root;
	}


	private void showConfermationAndExecute(final String message , final int type){

		HashMap<Integer, Object> buttonMap = new HashMap<Integer, Object>();
		buttonMap.put(1, R.string.btn_ok);
		buttonMap.put(2, R.string.btn_close);
		DialogView dialogView = new DialogView(this, R.string.dialog_title,message, R.color.warning, R.drawable.information, buttonMap);
		dialogView.setOnDialogButtonClick(new OnDialogButtonClick() {

			@Override
			public void onDialogButtonClick(View view) {
				switch (view.getId()) {
					case 1:
						if(type==REMOVE_LOCAL_DATA){
                            removeLocalDatabase();
						} else if(type==VOID_DATA_VERSION){
							App.getContext().getDB().voidDataVersion(App.getContext().getAppSettings().getLanguage());
						} else if(type==UPLOAD_LOCAL_DATABASE){
							uploadLocalDatabase();
						}
						break;
				}
			}
		});
		dialogView.show();
	}

	private void uploadLocalDatabase(){
		File dbFile=App.getContext().getDB().getDump();
		if(dbFile==null) return;
		RequestData request =new RequestData(RequestType.USER_GATE, RequestName.FILE_UPLOAD, Constants.MODULE_BUNCH_PUSH);
        request.addFile(dbFile);
		CommiunicationTask commiunicationTask=new CommiunicationTask(SettingsPreferenceActivity.this, request, R.string.retrieving_data, R.string.please_wait);
		commiunicationTask.setCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(Message msg) {
				if (msg.getData().containsKey(TaskKey.ERROR_MSG)) {
					String errorMsg = (String) msg.getData().getSerializable(TaskKey.ERROR_MSG);
					App.showMessageDisplayDialog(SettingsPreferenceActivity.this, getResources().getString(R.string.network_error), R.drawable.error, Color.RED);
				} else {
					ResponseData response=(ResponseData) msg.getData().getSerializable(TaskKey.DATA0);
					if(response.getResponseCode().equalsIgnoreCase("00")){
						App.showMessageDisplayDialog(SettingsPreferenceActivity.this, response.getErrorCode()+"-"+response.getErrorDesc(), R.drawable.error, Color.RED);
					}else{
						App.showMessageDisplayDialog(SettingsPreferenceActivity.this,"Successfully Upload" , R.drawable.success,Color.GREEN);
					}
				}
			}
		});
		commiunicationTask.execute();
	}

	private void removeLocalDatabase(){
		AppPreference.putString(SettingsPreferenceActivity.this, ActivityDataKey.SELECTED_FCM, "");
		AppPreference.putString(SettingsPreferenceActivity.this, ActivityDataKey.SELECTED_LOCATION_AREA, "");
		AppPreference.putString(SettingsPreferenceActivity.this, ActivityDataKey.SELECTED_FCM_STRING, "");
		AppPreference.putString(SettingsPreferenceActivity.this, ActivityDataKey.SELECTED_LOCATION_NAME, "");
		App.getContext().getDB().deleteDb();
		App.getContext().deleteDir(new File(App.getContext().getQuestionnaireJSONDir(SettingsPreferenceActivity.this)));
		App.getContext().deleteDir(new File(App.getContext().getBeneficiaryImageDir(SettingsPreferenceActivity.this)));
		App.getContext().deleteDir(new File(App.getContext().getVoiceFileDir(SettingsPreferenceActivity.this)));

		App.getContext().setDBManager(null);
		App.getContext().setAppSettings(null);
		App.getContext().loadApplicationData(SettingsPreferenceActivity.this);
	}



	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		

		if (pref instanceof ListPreference) {
			String value=newValue.toString();
			ListPreference listPref = (ListPreference) pref;
			String title = (String) listPref.getEntries()[listPref.findIndexOfValue(value)];
		
			if (pref.getKey().equals(KEY.LANGUAGE) && !prevLangCode.equals(value)) {
				Log.e("LANGUAGE CHANGE",value);
				listPref.setSummary("Changing language voids data version \n"+title);
				AppPreference.putString(this, KEY.LANGUAGE,value);
				App.getContext().getAppSettings().setLanguage(value);
				LanguageContextWrapper.wrap(this, AppPreference.getString(this, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE));
			//	App.getContext().setLocal(this,App.getContext().getAppSettings().getLanguage());
				App.getContext().getDB().voidDataVersion(App.getContext().getAppSettings().getLanguage());

			}
		}

		if (pref instanceof CheckBoxPreference) {
			CheckBoxPreference cbPref = (CheckBoxPreference) pref;
			boolean isChecked =(Boolean)newValue;
			if (cbPref.getKey().equalsIgnoreCase(KEY.GPS_START)) {
				AppPreference.putBoolean(this, KEY.GPS_START,isChecked);
				App.getContext().getAppSettings().setGPSStartOnAppStart(isChecked);
			}

			
			if (cbPref.getKey().equalsIgnoreCase(KEY.USE_NETWORK_PROVIDED_TIME)) {
				AppPreference.putBoolean(this, KEY.USE_NETWORK_PROVIDED_TIME,isChecked);
				App.getContext().getAppSettings().setUseNetworkProvidedTime(isChecked);
			}
			
			if (cbPref.getKey().equalsIgnoreCase(KEY.VIBRATE_ON_DOCROT_FEEDBACK)) {
				AppPreference.putBoolean(this, KEY.VIBRATE_ON_DOCROT_FEEDBACK,isChecked);
			}
		}
		
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if ((prevHost != null && !prevHost.equalsIgnoreCase("") && !prevHost
				.equalsIgnoreCase(curHost))
				|| (prevAlterHost != null && !prevHost.equalsIgnoreCase("") && !prevAlterHost
				.equalsIgnoreCase(curAlterHost))) {
			Log.e("Password", "reset");
			AppPreference.putString(this,KEY.PASSWORD, "");

		}

		if(getIntent().getExtras().containsKey(ActivityDataKey.ACTIVITY)){
			getIntent().getExtras().getString(ActivityDataKey.ACTIVITY);
			try {
				Intent intent = new Intent(this, Class.forName(getIntent().getExtras().getString(ActivityDataKey.ACTIVITY)));
				startActivity(intent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		}
		this.finish();
	}

    
    
    public class PreferenceScreenMultiLineTitle extends Preference {

    	
    	  public PreferenceScreenMultiLineTitle(Context ctx) {
    	    super(ctx);
    	  }

    	 @Override
    	 protected void onBindView(View view) {
    	    super.onBindView(view);

    	    TextView textView = view.findViewById(android.R.id.title);
    	    if (textView != null) {
    	        textView.setSingleLine(false);
    	        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
    	      }
    	  }
    	}
    
     public class CheckBoxPreferenceMultiLineTitle extends CheckBoxPreference {

  	  public CheckBoxPreferenceMultiLineTitle(Context ctx) {
  	    super(ctx);
  	  }

  	 @Override
  	 protected void onBindView(View view) {
  	    super.onBindView(view);

  	    TextView textView = view.findViewById(android.R.id.title);
  	    if (textView != null) {
  	        textView.setSingleLine(false);
  	      }
  	  }
  	}




	@Override
	public void onStart() {
		super.onStart();
		App.getContext().onStartActivity(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		App.getContext().onStartActivity(this);
	}




	@Override
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(LanguageContextWrapper.wrap(context, AppPreference.getString(context, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)));
	}



}
