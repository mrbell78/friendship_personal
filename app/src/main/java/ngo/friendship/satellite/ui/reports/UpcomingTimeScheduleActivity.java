package ngo.friendship.satellite.ui.reports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;

import ngo.friendship.satellite.App;
import ngo.friendship.satellite.LanguageContextWrapper;
import ngo.friendship.satellite.R;
import ngo.friendship.satellite.constants.ActivityDataKey;
import ngo.friendship.satellite.constants.KEY;
import ngo.friendship.satellite.databinding.UpcomingTimeScheduleLayoutBinding;
import ngo.friendship.satellite.model.AppSettings;
import ngo.friendship.satellite.utility.AppPreference;
import ngo.friendship.satellite.utility.SystemUtility;

// TODO: Auto-generated Javadoc

/**
 * The Class UpcomingTimeScheduleActivity.
 */
public class UpcomingTimeScheduleActivity extends AppCompatActivity implements OnClickListener {

    String activityPath;
    UpcomingTimeScheduleLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        App.loadApplicationData(this);
        binding = UpcomingTimeScheduleLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("" + getResources().getString(R.string.upcoming_schedule));


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.btnTomorrowTimeSchedule.setOnClickListener(this);
        binding.btnNext7DaysTimeSchedule.setOnClickListener(this);
        binding.btnUndoneTimeSchedule.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (App.getContext().getAppSettings() == null)
            App.getContext().readAppSettings(this);


        if (!SystemUtility.isAutoTimeEnabled(this)) {
            SystemUtility.openDateTimeSettingsActivity(this);
        }


        // activityPath = Utility.setActivityPath(this, R.string.upcoming_schedule);
    }


    /**
     * Start activity.
     *
     * @param cls is the activity class
     */
    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(ActivityDataKey.ACTIVITY_PATH, activityPath);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tomorrow_time_schedule:
                openActivity(TomorrowsTimeScheduleActivity.class);
                break;

            case R.id.btn_next_7_days_time_schedule:
                openActivity(Next30DaysTimeScheduleListActivity.class);
                break;

            case R.id.btn_undone_time_schedule:
                openActivity(UnDoneTimeScheduleListActivity.class);
                break;

            default:
                break;
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


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(LanguageContextWrapper.wrap(context, AppPreference.getString(context, KEY.LANGUAGE, AppSettings.DEFAULT_LANGUAGE)));
    }


}
