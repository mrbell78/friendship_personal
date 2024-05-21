package ngo.friendship.satellite.views;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.interfaces.OnDialogButtonClick;
import ngo.friendship.satellite.interfaces.OnDialogDismissListener;
import ngo.friendship.satellite.model.Household;
import ngo.friendship.satellite.model.MedicineInfo;

import ngo.friendship.satellite.model.Question;
import ngo.friendship.satellite.model.QuestionOption;
import ngo.friendship.satellite.ui.LauncherActivity;
import ngo.friendship.satellite.ui.SettingsPreferenceActivity;
import ngo.friendship.satellite.utility.TextUtility;
import ngo.friendship.satellite.utility.Utility;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class DialogView {
    // public static void showDialog(final Activity activity, Object title , String name ,Object msg ,Object img  , HashMap<Integer, Integer> buttonMap){
    private Activity activity;
    private String title = "";
    private int titleColor = Color.WHITE;
    private String message = "";
    private int messageColor = Color.BLACK;
    private Object icon = null;
    private HashMap<Integer, Object> buttonMap = null;
    private OnDialogButtonClick onDialogButtonClick;
    private OnDialogDismissListener onDialogDismissListener;
    private static boolean isRuning = false;

    private DialogView() {
    }

    public DialogView(Activity activity, String title, int titleColor,
                      String message, int messageColor, Object icon,
                      HashMap<Integer, Object> buttonMap) {
        super();

        this.activity = activity;

        setTitle(title);
        setTitleColor(titleColor);
        setMessage(message);
        setMessageColor(messageColor);
        setIcon(icon);
        setButtonMap(buttonMap);
    }

    public DialogView(Activity activity, Object title,
                      Object message, int messageColor, Object icon,
                      HashMap<Integer, Object> buttonMap) {
        super();
        this.activity = activity;
        setTitle(title);
        setMessage(message);
        setMessageColor(messageColor);
        setIcon(icon);
        setButtonMap(buttonMap);
    }

    public DialogView(Activity activity, Object title,
                      Object message, Object icon,
                      HashMap<Integer, Object> buttonMap) {
        super();
        this.activity = activity;
        setTitle(title);
        setMessage(message);
        setIcon(icon);
        setButtonMap(buttonMap);
    }


    public DialogView(Activity activity, Object title, HashMap<Integer, Object> buttonMap) {
        super();
        this.activity = activity;
        setTitle(title);
        setButtonMap(buttonMap);
    }


    public DialogView(Activity activity,
                      HashMap<Integer, Object> buttonMap) {
        super();
        this.activity = activity;
        setButtonMap(buttonMap);
    }

    public void setTitle(Object title) {
        if (title instanceof String) {
            this.title = (String) title;
        } else if (title instanceof Integer) {
            this.title = (activity.getResources().getString((Integer) title));
        } else {
            this.title = "";
        }
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }


    public void setMessage(Object message) {
        if (message instanceof String) {
            this.message = (String) message;
        } else if (message instanceof Integer) {
            this.message = (activity.getResources().getString((Integer) message));
        } else {
            this.message = "";
        }
    }


    public void setMessageColor(int messageColor) {
        this.messageColor = messageColor;
    }


    public void setIcon(Object icon) {
        this.icon = icon;
    }

    public void setButtonMap(HashMap<Integer, Object> buttonMap) {
        this.buttonMap = buttonMap;
    }

    public void show() {

        if (isRuning) return;

        View view = View.inflate(activity, R.layout.mhealth_dialog, null);
        final AlertDialog dialog = createDialog(view);

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);

        TextView tvMsg = view.findViewById(R.id.tv_dialog_message);
        tvMsg.setText(message);
        tvMsg.setTextColor(messageColor);


        ImageView imgIcon = view.findViewById(R.id.img_dialog_title);

        if (icon instanceof Bitmap) {
            imgIcon.setImageBitmap((Bitmap) icon);
        } else if (icon instanceof Drawable) {
            imgIcon.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Integer) {
            imgIcon.setImageResource((Integer) icon);
        }

        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }

            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (message.contains("0003")) {
                        try {
                            AppToast.showToast(activity, R.string.inactive_user);
                            Intent intent = new Intent(activity, LauncherActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }

                    if (onDialogButtonClick != null) {
                        onDialogButtonClick.onDialogButtonClick(view);
                    }
                }
            });
            buttonContainer.addView(btn);
        }
        dialog.setCancelable(false);
        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }

    public void showTimerView(long second) {
        if (isRuning) return;
        View view = View.inflate(activity, R.layout.mhealth_dialog, null);
        final AlertDialog dialog = createDialog(view);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);

        TextView tvMsg = view.findViewById(R.id.tv_dialog_message);
        tvMsg.setText(message);
        tvMsg.setTextColor(messageColor);


        final TextView tvTime = view.findViewById(R.id.tv_dialog_time);
        tvTime.setVisibility(View.VISIBLE);

        ImageView imgIcon = view.findViewById(R.id.img_dialog_title);

        if (icon instanceof Bitmap) {
            imgIcon.setImageBitmap((Bitmap) icon);
        } else if (icon instanceof Drawable) {
            imgIcon.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Integer) {
            imgIcon.setImageResource((Integer) icon);
        }

        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        final CountDownTimer timer = new CountDownTimer(second * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTime.setText("00:" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                if (onDialogDismissListener != null) {
                    onDialogDismissListener.OnDialogDismiss();
                }
            }
        };

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }

            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    timer.cancel();
                    dialog.dismiss();
                    if (onDialogButtonClick != null) {
                        onDialogButtonClick.onDialogButtonClick(view);
                    }
                }
            });
            buttonContainer.addView(btn);
        }
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                timer.start();
                isRuning = true;
            }
        });
        dialog.show();


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }

    public void showWebView() {
        if (isRuning) return;

        View view = View.inflate(activity, R.layout.mhealth_web_dialog, null);
        WebView wv = view.findViewById(R.id.wv_webview);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.loadDataWithBaseURL("", message, mimeType, encoding, "");

        final AlertDialog dialog = createDialog(view);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        ImageView imgIcon = view.findViewById(R.id.img_dialog_title);

        if (icon instanceof Bitmap) {
            imgIcon.setImageBitmap((Bitmap) icon);
        } else if (icon instanceof Drawable) {
            imgIcon.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Integer) {
            imgIcon.setImageResource((Integer) icon);
        }

        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }

            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (onDialogButtonClick != null) {
                        onDialogButtonClick.onDialogButtonClick(view);
                    }
                }
            });
            buttonContainer.addView(btn);
        }
        dialog.setCancelable(false);
        //dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = dialog.getWindow();
                    lp.copyFrom(window.getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    window.setAttributes(lp);
                    //super.onPageFinished( view, null );
                    dialog.show();

                }
            });

        } else {
            dialog.show();
        }
    }

    public void showAnswerWebView(Context context) {


        if (isRuning) return;

        View view = View.inflate(activity, R.layout.mhealth_web_dialog, null);
        WebView wv = view.findViewById(R.id.wv_webview);

        final String mimeType = "text/html";
        final String encoding = "UTF-8";


        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        wv.getSettings().setUseWideViewPort(true);
        wv.loadDataWithBaseURL("", message, mimeType, encoding, "");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog = builder.create();


        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        ImageView imgIcon = view.findViewById(R.id.img_dialog_title);

        if (icon instanceof Bitmap) {
            imgIcon.setImageBitmap((Bitmap) icon);
        } else if (icon instanceof Drawable) {
            imgIcon.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Integer) {
            imgIcon.setImageResource((Integer) icon);
        }

        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }

            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (onDialogButtonClick != null) {
                        onDialogButtonClick.onDialogButtonClick(view);
                    }
                }
            });
            buttonContainer.addView(btn);
        }
        dialog.setCancelable(false);
        //dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    Window window = dialog.getWindow();
                    lp.copyFrom(window.getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    window.setAttributes(lp);
                    super.onPageFinished(view, null);
                    dialog.show();
                    dialog.getWindow().setLayout(700, 1000);
//                    DisplayMetrics displayMetrics = new DisplayMetrics();
//                    ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//                    layoutParams.copyFrom(dialog.getWindow().getAttributes());
//
//                    // setting width to 90% of display
//                    layoutParams.width = (int) (displayMetrics.widthPixels * 0.9f);
//
//                    // setting height to 90% of display
//                    layoutParams.height = (int) (displayMetrics.heightPixels * 0.9f);
//                    dialog.getWindow().setAttributes(layoutParams);

                }
            });

        } else {
            dialog.show();
        }
    }

    public void showDateView() {


        if (isRuning) return;

        View view = View.inflate(activity, R.layout.mhealth_dialog, null);
        final AlertDialog dialog = createDialog(view);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);

        TextView tvMsg = view.findViewById(R.id.tv_dialog_message);
        tvMsg.setText(message);
        tvMsg.setTextColor(messageColor);

        final DatePicker dateInput = view.findViewById(R.id.dateInput);
        dateInput.setVisibility(View.VISIBLE);

        ImageView imgIcon = view.findViewById(R.id.img_dialog_title);

        if (icon instanceof Bitmap) {
            imgIcon.setImageBitmap((Bitmap) icon);
        } else if (icon instanceof Drawable) {
            imgIcon.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Integer) {
            imgIcon.setImageResource((Integer) icon);
        }

        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }

            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int day = dateInput.getDayOfMonth();
                    int month = dateInput.getMonth();
                    int year = dateInput.getYear();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    view.setTag(calendar.getTimeInMillis());
                    dialog.dismiss();
                    if (onDialogButtonClick != null) {

                        onDialogButtonClick.onDialogButtonClick(view);
                    }
                }
            });
            buttonContainer.addView(btn);
        }
        dialog.setCancelable(false);
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }

    public void showPrescriptionConfermation(ArrayList<String> prescription) {


        if (isRuning) return;

        View view = View.inflate(activity, R.layout.mhealth_dialog, null);
        final AlertDialog dialog = createDialog(view);

        LinearLayout container = view.findViewById(R.id.container);

        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);

        TextView tvMsg = view.findViewById(R.id.tv_dialog_message);
        tvMsg.setText(R.string.details_of_selling_drugs);
        tvMsg.setTextColor(messageColor);

        boolean isHeaderAdded = false;
        double totalPrice = 0.0;
        int count = 0;
        for (int i = 0; i < prescription.size(); i++) {
            try {
                JSONObject jsonObject = new JSONObject(prescription.get(i));
                if (Integer.parseInt(jsonObject.getString("MED_ID")) > 0 && Integer.parseInt(jsonObject.getString("SALE_QTY")) > 0) {

                    if (!isHeaderAdded) {
                        View rowHeader = View.inflate(activity, R.layout.prescription_confermation_row, null);
                        TextView tv1 = rowHeader.findViewById(R.id.tv_med_name);
                        tv1.setText(R.string.med_name);
                        tv1.setTypeface(null, Typeface.BOLD);

                        TextView tv2 = rowHeader.findViewById(R.id.tv_med_qty);
                        tv2.setText(R.string.quantity);
                        tv2.setTypeface(null, Typeface.BOLD);

                        TextView tv3 = rowHeader.findViewById(R.id.tv_med_price);
                        tv3.setText(R.string.prescription_tbl_price);
                        tv3.setTypeface(null, Typeface.BOLD);

                        rowHeader.setBackgroundResource(R.color.group_header);
                        container.addView(rowHeader);
                        isHeaderAdded = true;
                    }

                    View row = View.inflate(activity, R.layout.prescription_confermation_row, null);
                    TextView medName = row.findViewById(R.id.tv_med_name);
                    medName.setText(jsonObject.getString("MED_NAME"));

                    TextView medQty = row.findViewById(R.id.tv_med_qty);
                    int qty = Integer.parseInt(jsonObject.getString("SALE_QTY"));
                    medQty.setText(qty + "");
                    double price = Utility.parseDouble(jsonObject.getString("TOTAL_PRICE"));
                    totalPrice = totalPrice + price;
                    TextView medPrice = row.findViewById(R.id.tv_med_price);
                    medPrice.setText(price + "");
                    if (count % 2 == 1) {
                        row.setBackgroundResource(R.color.loghi_gray);
                    }
                    container.addView(row);
                    count++;
                }


            } catch (Exception exception) {
            }

        }


        if (isHeaderAdded) {
            LinearLayout rowFooter = (LinearLayout) View.inflate(activity, R.layout.prescription_confermation_row, null);
            TextView tv2 = rowFooter.findViewById(R.id.tv_med_qty);
            tv2.setText(R.string.total_medicine_price);
            tv2.setTypeface(null, Typeface.BOLD);

            TextView tv3 = rowFooter.findViewById(R.id.tv_med_price);
            tv3.setText(TextUtility.format("%s", totalPrice));
            tv3.setTypeface(null, Typeface.BOLD);


            rowFooter.setBackgroundResource(R.color.group_header);
            container.addView(rowFooter);
            isHeaderAdded = true;
        } else {
            tvMsg.setText(R.string.no_medications_were_sold);
        }


        ImageView imgIcon = view.findViewById(R.id.img_dialog_title);

        if (icon instanceof Bitmap) {
            imgIcon.setImageBitmap((Bitmap) icon);
        } else if (icon instanceof Drawable) {
            imgIcon.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Integer) {
            imgIcon.setImageResource((Integer) icon);
        }


        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }

            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (onDialogButtonClick != null) {

                        onDialogButtonClick.onDialogButtonClick(view);
                    }
                }
            });
            buttonContainer.addView(btn);
        }
        dialog.setCancelable(false);
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }


    public void setOnDialogButtonClick(OnDialogButtonClick onDialogButtonClick) {
        this.onDialogButtonClick = onDialogButtonClick;
    }

    public void setOnDialogDismissListener(
            OnDialogDismissListener onDialogDismissListener) {
        this.onDialogDismissListener = onDialogDismissListener;
    }

    /**
     * Display household number entry dialog.
     */
    public void showHouseholdNumberInputDialog() {

        if (isRuning) return;

        final Dialog dialog = new Dialog(activity, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(activity, R.layout.household_number_entry_dialog, null);
        dialog.setContentView(view);
        final EditText etHouseholdNumber = view.findViewById(R.id.et_household_number);
        final MdiTextView btnClose = view.findViewById(R.id.btn_close);
        etHouseholdNumber.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imp = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                //only will trigger it if no physical keyboard is open
                imp.showSoftInput(etHouseholdNumber, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 30);

        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);
            if (id == 1) {
                btn.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
                btn.setTextColor(activity.getResources().getColor(R.color.white));
                btn.setBackground(activity.getResources().getDrawable(R.drawable.button_primary));
            }

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (onDialogButtonClick != null) {
                        view.setTag(etHouseholdNumber.getText().toString().trim());
                        onDialogButtonClick.onDialogButtonClick(view);
                    }
                }
            });
            buttonContainer.addView(btn);
        }
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isRuning = false;
            }
        });
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                isRuning = true;
            }
        });
        dialog.show();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        dialog.getWindow().setLayout((int) (metrics.widthPixels * .90), WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public void showHouseholdNumberInputDialogWithSelfCreateHousehold(Context ctx, ArrayList<Household> householdArrayList, String questionType, int questionPosition) {

        if (isRuning) return;

        final Dialog dialog = new Dialog(activity, R.style.CustomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(activity, R.layout.household_number_entry_dialog, null);
        dialog.setContentView(view);
//        final ListView lv = view.findViewById(R.id.lv_household_list);
//        final TextView tvDialogTitle = view.findViewById(R.id.tv_dialog_title);
//        tvDialogTitle.setText(ctx.getResources().getString(R.string.beneficiary_list));
//        HouseholdListForAutoSelectAdapter householdListAdapter = new HouseholdListForAutoSelectAdapter(householdArrayList, activity);
//
//        lv.setAdapter(householdListAdapter);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View v, int p,
//                                    long id) {
//                dialog.dismiss();
//
//                ((QuestionnaireListActivity)activity).householdListClickForSelfCreatedhousehold(householdListAdapter.mHouseholdFilterList.get(p),questionType,questionPosition,p);
//            }
//        });

//        --------------------------------------


        final EditText etHouseholdNumber = view.findViewById(R.id.et_household_number);

        etHouseholdNumber.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imp = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                //only will trigger it if no physical keyboard is open
                imp.showSoftInput(etHouseholdNumber, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 30);


        etHouseholdNumber.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        etHouseholdNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

        etHouseholdNumber.setHint(ctx.getString(R.string.search));
        // Add Text Change Listener to EditText
        etHouseholdNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
//                householdListAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }

            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (onDialogButtonClick != null) {
                        view.setTag(etHouseholdNumber.getText().toString().trim());
                        onDialogButtonClick.onDialogButtonClick(view);
                    }
                }
            });
            buttonContainer.addView(btn);
        }

        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isRuning = false;
            }
        });
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                isRuning = true;
            }
        });
        dialog.show();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        dialog.getWindow().setLayout((int) (metrics.widthPixels * .90), WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public void showGustBeneficiaryDataRetrievalDialog() {


        if (isRuning) return;

        View view = View.inflate(activity, R.layout.mhealth_dialog_gust_benef, null);

        final AlertDialog dialog = createDialog(view);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);

        ImageView imgIcon = view.findViewById(R.id.img_dialog_title);

        if (icon instanceof Bitmap) {
            imgIcon.setImageBitmap((Bitmap) icon);
        } else if (icon instanceof Drawable) {
            imgIcon.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Integer) {
            imgIcon.setImageResource((Integer) icon);
        }


//			LinearLayout ageLayout=new LinearLayout(activity);
//			ageLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//			ageLayout.setOrientation(LinearLayout.HORIZONTAL);
//			TextView tvAge = new TextView(activity);
//			tvAge.setText(R.string.age);
//
//			tvAge.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,1f));
//			ageLayout.addView(tvAge);
//			EditText eTxtName = (EditText) view.findViewById(R.id.tv_dialog_message);
//			eTxtName.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT,3f));
//			ageLayout.addView(eTxtName);
//
//

        final TextView tvDuration = view.findViewById(R.id.tv_duration);
        final DatePicker dateInput = view.findViewById(R.id.dateInput);
        dateInput.init(dateInput.getYear(), dateInput.getMonth(), dateInput.getDayOfMonth(), new OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    String ageStr = Utility.getAge(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    if (ageStr != null) {
                        tvDuration.setTextColor(Color.BLACK);
                        tvDuration.setText(activity.getResources().getText(R.string.age) + " " + ageStr);
                    } else {
                        tvDuration.setTextColor(Color.RED);
                        tvDuration.setText(activity.getResources().getString(R.string.invalid_age));
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        final RadioGroup rdoGroupGender = view.findViewById(R.id.rdo_group_gender);
        final RadioGroup rdoGroupMaternalStatus = view.findViewById(R.id.rdo_group_marital_status);


        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }

            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int day = dateInput.getDayOfMonth();
                    int month = dateInput.getMonth() + 1;
                    int year = dateInput.getYear();

                    String out = year + "-" + month + "-" + day;
                    switch (rdoGroupGender.getCheckedRadioButtonId()) {
                        case R.id.rdo_male:
                            out += "#M";
                            break;
                        case R.id.rdo_female:
                            out += "#F";
                            break;
                        case R.id.rdo_other:
                            out += "#O";
                            break;
                    }

                    switch (rdoGroupMaternalStatus.getCheckedRadioButtonId()) {
                        case R.id.rdo_divorced:
                            out += "#Divorced";
                            break;
                        case R.id.rdo_married:
                            out += "#Married";
                            break;
                        case R.id.rdo_single:
                            out += "#Single";
                            break;
                        case R.id.rdo_widowed:
                            out += "#Widowed";
                            break;
                    }
                    view.setTag(out);
                    dialog.dismiss();
                    if (onDialogButtonClick != null) {
                        onDialogButtonClick.onDialogButtonClick(view);
                    }
                }
            });
            buttonContainer.addView(btn);
        }
        dialog.setCancelable(false);
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }

    public void showDateInputBetweenDialog(long minDate, long maxDate) {

        if (isRuning) return;

        View view = View.inflate(activity, R.layout.dialog_data_between, null);
        final AlertDialog dialog = createDialog(view);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);

        TextView tvMsg = view.findViewById(R.id.tv_dialog_message);
        tvMsg.setText(message);
        tvMsg.setTextColor(messageColor);

        final DatePicker dateInputFrom = view.findViewById(R.id.dateInput);
        dateInputFrom.setMinDate(minDate);
        dateInputFrom.setMaxDate(maxDate);
        dateInputFrom.setVisibility(View.VISIBLE);

        final DatePicker dateInputTo = view.findViewById(R.id.dateInput2);
        dateInputTo.setMinDate(minDate);
        dateInputTo.setMaxDate(maxDate);
        dateInputTo.setVisibility(View.VISIBLE);

        ImageView imgIcon = view.findViewById(R.id.img_dialog_title);

        if (icon instanceof Bitmap) {
            imgIcon.setImageBitmap((Bitmap) icon);
        } else if (icon instanceof Drawable) {
            imgIcon.setImageDrawable((Drawable) icon);
        } else if (icon instanceof Integer) {
            imgIcon.setImageResource((Integer) icon);
        }

        LinearLayout buttonContainer = view.findViewById(R.id.btn_container);
        Button btnDemo = view.findViewById(R.id.btn);
        buttonContainer.removeAllViews();

        Set<Integer> keys = buttonMap.keySet();
        for (Integer id : keys) {
            Button btn = new Button(activity);
            Object buttonCaption = buttonMap.get(id);
            btn.setId(id);

            if (buttonCaption instanceof Integer) {
                btn.setText((Integer) buttonCaption);
            } else if (buttonCaption instanceof String) {
                btn.setText(buttonCaption + "");
            }

            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            btn.setTypeface(btnDemo.getTypeface());
            btn.setLayoutParams(btnDemo.getLayoutParams());
            btn.setOnClickListener(new OnClickListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onClick(View view) {

                    if (view.getId() == 1) {
                        Calendar calendarFrom = Calendar.getInstance();
                        calendarFrom.set(dateInputFrom.getYear(), dateInputFrom.getMonth(), dateInputFrom.getDayOfMonth(), 0, 0, 0);
                        calendarFrom.set(Calendar.MILLISECOND, 0);

                        Calendar calendarTo = Calendar.getInstance();
                        calendarTo.set(dateInputTo.getYear(), dateInputTo.getMonth(), dateInputTo.getDayOfMonth(), 23, 59, 59);
                        calendarTo.set(Calendar.MILLISECOND, 999);

                        if (calendarFrom.getTimeInMillis() <= calendarTo.getTimeInMillis()) {

                            view.setTag(R.id.from, calendarFrom.getTimeInMillis());
                            view.setTag(R.id.to, calendarTo.getTimeInMillis());
                            dialog.dismiss();
                            if (onDialogButtonClick != null) {

                                onDialogButtonClick.onDialogButtonClick(view);
                            }
                        } else {
                            AppToast.showToastWarnaing(activity, R.string.input_invalid);
                        }
                    } else {
                        dialog.dismiss();
                        if (onDialogButtonClick != null) {

                            onDialogButtonClick.onDialogButtonClick(view);
                        }

                    }

                }
            });
            buttonContainer.addView(btn);
        }
        dialog.setCancelable(false);
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


    public void showMedicineListDialog(final ArrayList<MedicineInfo> medicineInfos) {


        if (isRuning) return;

        View view = View.inflate(activity, R.layout.mhealth_dialog, null);
        final AlertDialog dialog = createDialog(view);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        view.findViewById(R.id.tv_dialog_message).setVisibility(View.GONE);
        view.findViewById(R.id.img_dialog_title).setVisibility(View.GONE);


        final LinearLayout itemContainer = view.findViewById(R.id.sv_items);
        itemContainer.removeAllViews();
        itemContainer.setVisibility(View.VISIBLE);


        EditText et_input = view.findViewById(R.id.et_input);
        et_input.setVisibility(View.VISIBLE);
        et_input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemContainer.removeAllViews();
                for (MedicineInfo medicineInfo : medicineInfos) {
                    View rowView = View.inflate(activity, R.layout.medicine_row, null);
                    CheckBox cbMedicineSeleted = rowView.findViewById(R.id.cbSelectMedicine);
                    TextView tvStock = rowView.findViewById(R.id.tv_stock);
                    LinearLayout llMedicineContainer = rowView.findViewById(R.id.ll_medicine_name_container);

                    tvStock.setText(""+medicineInfo.getAvailableQuantity());
                    if (medicineInfo.isSelected()){
                        cbMedicineSeleted.setChecked(true);
                    }else{
                        cbMedicineSeleted.setChecked(false);
                    }

                    if ((medicineInfo.getMedicineType().toUpperCase().contains(s.toString().toUpperCase())
                                    || medicineInfo.getBrandName().toUpperCase().contains(s.toString().toUpperCase()))) {


                        llMedicineContainer.setTag(medicineInfo);
                        llMedicineContainer.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if (medicineInfo.isSelected()){
                                    cbMedicineSeleted.setChecked(false);
                                    medicineInfo.setSelected(false);
                                }else{
                                    cbMedicineSeleted.setChecked(true);
                                    medicineInfo.setSelected(true);
                                }

//                                dialog.dismiss();
//                                if (onDialogButtonClick != null) {
//                                    onDialogButtonClick.onDialogButtonClick(view);
//                                }
                            }
                        });
                        cbMedicineSeleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                            if (isChecked) {
                                medicineInfo.setSelected(true);
                            } else {
                                medicineInfo.setSelected(false);
                            }
                        });
                        TextView tvMedeicineName = rowView.findViewById(R.id.tv_medicine_name);
                        tvMedeicineName.setText(medicineInfo.toString());
                        itemContainer.addView(rowView);
                    }

                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });


        dialog.setCancelable(false);
        dialog.show();

        et_input.setText("");

        Button btn = view.findViewById(R.id.btn);
        btn.setText("DONE");
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onDialogButtonClick != null) {
                    onDialogButtonClick.onDialogButtonClick(view);
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }


    public static void showAdminLoginDialog(final Activity activity) {


        if (isRuning) return;
        View view = View.inflate(activity, R.layout.admin_login_dialog_layout, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        final EditText etSecurityPin = view.findViewById(R.id.et_security_pin);
        Button btnClose = view.findViewById(R.id.btn_cancel);
        btnClose.setText(R.string.btn_cancel_eng);
        btnClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

        Button btnLogin = view.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String securityPin = Utility.getAdminPassword(activity);
                String userEnteredSecurityPin = etSecurityPin.getText().toString();

                // AppToast.showToast(activity,securityPin);

                if (userEnteredSecurityPin.equals(securityPin) || Utility.isIMEIPermitted(Utility.getIMEInumber(activity))) {
                    dialog.dismiss();

                    Intent intent = new Intent(activity, SettingsPreferenceActivity.class);
                    intent.putExtra(ActivityDataKey.ACTIVITY, activity.getClass().getName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                } else {
                    AppToast.showToast(activity, "Wrong security PIN");
                }
            }
        });

        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isRuning = false;
            }
        });
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                isRuning = true;
            }
        });

        dialog.show();
    }


    public static void showDeviceIDDialog(final Activity activity) {


        if (isRuning) return;
        View view = View.inflate(activity, R.layout.show_device_id_dialog_layout, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        final TextView tvDeviceid = view.findViewById(R.id.tv_device_id);
        tvDeviceid.setText("" + Utility.getIMEInumber(activity));

        Button btnClose = view.findViewById(R.id.btn_cancel);
        btnClose.setText(R.string.btn_cancel_eng);
        btnClose.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });


        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isRuning = false;
            }
        });
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                isRuning = true;
            }
        });

        dialog.show();
    }

    private AlertDialog createDialog(View layoutView) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(layoutView);

        AlertDialog dialog = builder.create();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isRuning = false;
            }
        });
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                isRuning = true;
            }
        });

        return dialog;

    }


    public void showCheckGroupListDialog(Context ctx, Question question, final ArrayList<QuestionOption> getOptionList) {


        if (isRuning) return;

        View view = View.inflate(activity, R.layout.mhealth_dialog, null);
        final AlertDialog dialog = createDialog(view);
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(title);
        tvTitle.setTextColor(titleColor);
        view.findViewById(R.id.tv_dialog_message).setVisibility(View.GONE);
        view.findViewById(R.id.img_dialog_title).setVisibility(View.GONE);


        Button btn = view.findViewById(R.id.btn);
        btn.setText("Done");
        final LinearLayout itemContainer = view.findViewById(R.id.sv_items);
        itemContainer.removeAllViews();
        itemContainer.setVisibility(View.VISIBLE);


        EditText et_input = view.findViewById(R.id.et_input);
        et_input.setVisibility(View.VISIBLE);
        et_input.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                itemContainer.removeAllViews();
                int i = 0;

                for (QuestionOption questionOption : getOptionList) {
                    if (questionOption.getCaption().toUpperCase().contains(s.toString().toUpperCase())) {
                        LinearLayout ll = new LinearLayout(ctx);
                        ll.setId(R.id.question_multi_select_view);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ll.setLayoutParams(params);
                        ll.setOrientation(LinearLayout.VERTICAL);

                        CheckBox checkBox = new CheckBox(ctx);
                        checkBox.setId(i);
                        checkBox.setText(questionOption.getCaption());
                        checkBox.setLayoutParams(params);

                        if (questionOption.isSelected()) {
                            checkBox.setChecked(true);
                        } else {
                            checkBox.setChecked(false);
                        }
                        if (i % 2 == 0)
                            checkBox.setBackgroundColor(Color.rgb(225, 225, 225));
                        // Checked item based on user input or Default value
//                        if (question.getUserInput() != null && question.getUserInput().size() > 0) {
//                            for (int j = 0; j < question.getUserInput().size(); j++) {
//                                if (questionOption.getValue().equalsIgnoreCase(question.getUserInput().get(j))) {
//                                    checkBox.setChecked(true);
//                                    break;
//                                }
//                            }
//                        } else if (question.getDefaultValue() != null && question.getDefaultValue().size() > 0) {
//                            for (int j = 0; j < question.getDefaultValue().size(); j++) {
//                                if (("option" + questionOption.getId()).equalsIgnoreCase(question.getDefaultValue().get(j))) {
//                                    checkBox.setChecked(true);
//                                    break;
//                                }
//                            }
//                        }
                        checkBox.setEnabled(!question.isReadonly());


                        checkBox.setOnCheckedChangeListener((cb, isChecked) -> {
                            if (isChecked) {
                                questionOption.setSelected(true);
                            } else {
                                questionOption.setSelected(false);
                            }

                            // Toast.makeText(ctx, "Ceck--"+question.getOptionList().get(cb.getId()).getValue(), Toast.LENGTH_SHORT).show();
//                        if (question.getOptionList().get(cb.getId()).getValue().equalsIgnoreCase("none")) {
//
//                            for (int i1 = 0; i1 < itemContainer.getChildCount(); i1++) {
//                                View v = itemContainer.getChildAt(i1);
//                                if (v instanceof CheckBox) {
//                                    CheckBox checkBox1 = ((CheckBox) v);
//                                    if (!question.getOptionList().get(checkBox1.getId()).getValue().equalsIgnoreCase("none")) {
//                                        if (isChecked) {
//                                            checkBox1.setChecked(false);
//                                            checkBox1.setEnabled(false);
//                                            questionOption.setSelected(false);
//                                        } else {
//                                            checkBox1.setEnabled(true);
//                                            questionOption.setSelected(true);
//                                        }
//                                    }
//                                }
//
//                            }
//
//                        }
                        });
                        ll.addView(checkBox);
                        itemContainer.addView(ll);
                        i++;
                    }
                }


//                for (MedicineInfo medicineInfo : medicineInfos) {
//                    if (!medicineInfo.isSelected() &&
//                            (medicineInfo.getMedicineType().toUpperCase().contains(s.toString().toUpperCase())
//                                    || medicineInfo.getBrandName().toUpperCase().contains(s.toString().toUpperCase()))) {
//
//                        View rowView = View.inflate(activity, R.layout.medicine_row, null);
//                        LinearLayout llMedicineContainer = rowView.findViewById(R.id.ll_medicine_name_container);
//                        llMedicineContainer.setTag(medicineInfo);
//                        llMedicineContainer.setOnClickListener(new OnClickListener() {
//
//                            @Override
//                            public void onClick(View view) {
//                                dialog.dismiss();
//                                if (onDialogButtonClick != null) {
//                                    onDialogButtonClick.onDialogButtonClick(view);
//                                }
//                            }
//                        });
//                        TextView tvMedeicineName = rowView.findViewById(R.id.tv_medicine_name);
//                        tvMedeicineName.setText(medicineInfo.toString());
//                        itemContainer.addView(rowView);
//                    }
//
//                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });


        dialog.setCancelable(false);
        dialog.show();

        et_input.setText("");


        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onDialogButtonClick != null) {
                    view.setTag(getValue(getOptionList));
                    onDialogButtonClick.onDialogButtonClick(view);
                }
                dialog.dismiss();

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }


    public ArrayList<QuestionOption> getValue(final ArrayList<QuestionOption> getOptionList) {
        ArrayList<QuestionOption> values = new ArrayList<QuestionOption>();

        for (QuestionOption questionOption : getOptionList) {
            if (questionOption.isSelected()) {
                values.add(questionOption);
            }
        }

        if (values.size() > 0) {
            return values;
        }
        return null;

    }

}
