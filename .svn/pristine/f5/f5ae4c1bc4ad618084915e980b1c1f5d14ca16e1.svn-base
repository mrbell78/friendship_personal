package ngo.friendship.satellite.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.R;

/*
 * Author: Md. Shahadat Sarker
 * created: 04 Feb 2018
 * modified: 22 Feb 2018
 * */

public class CardButton extends LinearLayout {


    Object mdIcon, textId;
    String orientation;

    boolean cardView = false;
    Context context;
    private TextView tv;
    private MdiTextView mdi_tv;
    private ImageView imageView;

    public CardButton(Context ctx) {
        super(ctx);
        this.context = ctx;
        initLayout();
    }

    public CardButton(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        this.context = ctx;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardButton);
        this.mdIcon = a.getString(R.styleable.CardButton_mdIcon);
        this.textId = a.getString(R.styleable.CardButton_text);
        this.orientation = a.getString(R.styleable.CardButton_orientation);
        initButton(mdIcon, textId, orientation);
        a.recycle();
    }

    public CardButton(Context ctx, Object mdIcon, Object text, String orientation) {
        super(ctx);
        this.mdIcon = mdIcon;
        this.textId = text;
        this.context = ctx;
        this.orientation = orientation;
        initButton(mdIcon, textId, orientation);
    }


    private void initLayout() {
        tv = new TextView(context);
//		ColorStateList textColorstateList = null;
//		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//			textColorstateList = context.getResources().getColorStateList(R.color.text_color, context.getTheme());
//		}else{
//			textColorstateList = context.getResources().getColorStateList(R.color.text_color);
//		}

        tv.setTextColor(context.getResources().getColor(R.color.text_color));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_medium));
        tv.setDuplicateParentStateEnabled(true);

        imageView = new ImageView(context);
        imageView.setDuplicateParentStateEnabled(true);


        if (orientation.contains("VERTICAL")) {
            mdi_tv = (MdiTextView) View.inflate(context, R.layout.card_button_vicon, null);
        } else {
            mdi_tv = (MdiTextView) View.inflate(context, R.layout.card_button_hicon, null);
        }

//		ColorStateList iconColorstateList = null;
//		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//			iconColorstateList = context.getResources().getColorStateList(R.color.icon_color, context.getTheme());
//		}else{
//			iconColorstateList = context.getResources().getColorStateList(R.color.icon_color);
//		}
        mdi_tv.setTextColor(context.getResources().getColor(R.color.icon_color));
        mdi_tv.setDuplicateParentStateEnabled(true);


        imageView.setVisibility(View.GONE);
        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(param);
        this.setOrientation(LinearLayout.VERTICAL);
    }

    private void initButton(Object mdIcon, Object text, String orientation) {
        initLayout();
        LayoutParams param;

        LinearLayout contantLayout = new LinearLayout(context);

        if (orientation.contains("VERTICAL")) {
            contantLayout.setOrientation(LinearLayout.VERTICAL);
            param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        } else {
            contantLayout.setOrientation(LinearLayout.HORIZONTAL);
            param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }


        contantLayout.setLayoutParams(param);
        contantLayout.setPadding(8, 10, 8, 10);

        if (orientation.contains("VERTICAL")) {
            contantLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        } else {
            contantLayout.setGravity(Gravity.CENTER_VERTICAL);
        }


        contantLayout.setBackgroundResource(R.drawable.square_layout);


        if (true || (App.getContext().getAppSettings().getIsImageShow() != null && App.getContext().getAppSettings().getIsImageShow().equals("YES"))) {
            if (mdIcon instanceof Integer)
                contantLayout.addView(getMdiTextView(mdIcon));
            else
                contantLayout.addView(getMdiTextView(mdIcon));


            contantLayout.addView(getImageView(R.drawable.ic_biohazard));
        }


        if (text instanceof Integer) {
            contantLayout.addView(getTextView(text));
        } else {
            contantLayout.addView(getTextView(text));
        }
        this.addView(contantLayout);


    }

    public void setMdiIcon(Object iconText) {
        if (iconText instanceof Integer)
            mdi_tv.setText((Integer) iconText);
        else
            mdi_tv.setText((String) iconText);

    }

    public void setIcon(String iconName) {
        imageView.setVisibility(View.GONE);
        mdi_tv.setVisibility(View.GONE);
        mdi_tv.setText("");
        Log.e("ICON NAME", iconName + "");

        if (iconName == null || iconName.trim().length() == 0) {
            mdi_tv.setVisibility(View.VISIBLE);
            mdi_tv.setText(R.string.mdi_question_comment);

        } else if (iconName.equalsIgnoreCase("ic_biohazard")) {
            imageView.setVisibility(View.VISIBLE);
            imageView = getImageView(R.drawable.ic_biohazard);

        } else if (iconName.equalsIgnoreCase("ic_lungs")) {
            imageView.setVisibility(View.VISIBLE);
            imageView = getImageView(R.drawable.ic_lungs);

        } else if (iconName.equalsIgnoreCase("ic_breast")) {
            imageView.setVisibility(View.VISIBLE);
            imageView = getImageView(R.drawable.ic_breast);

        } else if (iconName.equalsIgnoreCase("ic_crying_baby")) {
            imageView.setVisibility(View.VISIBLE);
            imageView = getImageView(R.drawable.ic_crying_baby);

        } else if (iconName.equalsIgnoreCase("ic_mouth_infection")) {
            imageView.setVisibility(View.VISIBLE);
            imageView = getImageView(R.drawable.ic_mouth_infection);

        } else if (iconName.equalsIgnoreCase("ic_stomach")) {
            imageView.setVisibility(View.VISIBLE);
            imageView = getImageView(R.drawable.ic_stomach);

        } else if (iconName.equalsIgnoreCase("ic_diabetes")) {
            imageView.setVisibility(View.VISIBLE);
            imageView = getImageView(R.drawable.ic_diabetes);

        } else if (iconName.equalsIgnoreCase("ic_mite")) {
            imageView.setVisibility(View.VISIBLE);
            imageView = getImageView(R.drawable.ic_mite);

        } else {
            mdi_tv.setVisibility(View.VISIBLE);
            try {
                //	String icons =String.valueOf((char) Integer.decode(iconName).intValue());
                mdi_tv.setText(Html.fromHtml("&#x" + iconName));
            } catch (Exception ex) {
                mdi_tv.setText(R.string.mdi_question_comment);
            }
        }

        imageView.setMinimumWidth(imageView.getMeasuredWidth());
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(5, 5, 5, 5);
        imageView.setLayoutParams(lp);

    }

    public void setText(Object text) {
        if (text instanceof Integer)
            tv.setText((Integer) text);
        else
            tv.setText((String) text);
    }

    public void setTextColor(Object textColor) {
        if (textColor instanceof Integer)
            tv.setTextColor((Integer) textColor);

    }

    private ImageView getImageView(Object img) {
        LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if (img instanceof Drawable) {
            imageView.setImageDrawable((Drawable) img);
        } else if (img instanceof Integer) {
            imageView.setImageResource((Integer) img);
        } else if (img instanceof String) {
            imageView.setImageBitmap(BitmapFactory.decodeFile((String) img));
        }

        if (orientation.contains("VERTICAL")) {
            param.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

        } else {
            param.gravity = Gravity.CENTER_VERTICAL | Gravity.START | Gravity.LEFT;
        }


        imageView.setLayoutParams(param);
        return imageView;
    }

    private MdiTextView getMdiTextView(Object resId) {
        LayoutParams param = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mdi_tv.setLayoutParams(param);

        if (resId instanceof Integer) {
            mdi_tv.setText((Integer) resId);
        } else if (resId instanceof String) {
            mdi_tv.setText((String) resId);
        }

        if (orientation.contains("VERTICAL")) {
            mdi_tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        } else {
            mdi_tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.START | Gravity.LEFT);

        }

        return mdi_tv;
    }


    private TextView getTextView(Object resId) {
        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(param);

        if (resId instanceof Integer) {
            tv.setText((Integer) resId);
        } else if (resId instanceof String) {
            tv.setText((String) resId);
        }


        if (orientation.contains("VERTICAL")) {
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        } else {
            tv.setPadding(5, 1, 5, 1);
            tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.START | Gravity.LEFT);

        }

        return tv;
    }


}
