package ngo.friendship.satellite.ui.notification

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.constants.Constants
import ngo.friendship.satellite.databinding.ActivityNotificationBinding
import ngo.friendship.satellite.utility.SystemUtility

/**
 * @author Md.Yeasin Ali
 * @created 01th Oct 2022
 */
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding;
    lateinit var ctx: Context;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)

        ctx = this;
        title = getString(R.string.txt_notification_list)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        App.loadApplicationData(this)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fvNotification,
                NotificationListFragment.newInstance(Constants.ALL_NOTIFICATION)
            ).commit()


//
        binding.llTabDoctorFeedback.updatePadding(top = 5, bottom = 5);
        binding.llTabGeneralNotification.updatePadding(top = 5, bottom = 5);
//        binding.llTabAll.updatePadding(top = 5, bottom = 5);
//        binding.llTabAll.setOnClickListener {
//
//            binding.llTabAll.setBackground(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.border_rounded_corner,
//                    null
//                )
//            )
//            binding.tvTabAll.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.white,
//                    null
//                )
//            )
//            binding.mdiTabAll.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.white,
//                    null
//                )
//            )
//
//
//            binding.tvTabDoctorFeedback.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.mdiTabDoctorFeedbackIcon.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.llTabDoctorFeedback.setBackground(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.border_rounded_corner_white,
//                    null
//                )
//            )
//
//
//            binding.tvTabNotifiation.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.mdiTabNotifiation.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.llTabGeneralNotification.setBackground(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.border_rounded_corner_white,
//                    null
//                )
//            )
//
//
//            supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.fvNotification,
//                    NotificationListFragment.newInstance(Constants.ALL_NOTIFICATION)
//                )
//                .commit()
//        }
//        binding.llTabGeneralNotification.setOnClickListener {
//            binding.llTabGeneralNotification.setBackground(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.border_rounded_corner,
//                    null
//                )
//            )
//            binding.tvTabNotifiation.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.white,
//                    null
//                )
//            )
//            binding.mdiTabNotifiation.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.white,
//                    null
//                )
//            )
//
//
//            binding.tvTabDoctorFeedback.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.mdiTabDoctorFeedbackIcon.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.llTabDoctorFeedback.setBackground(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.border_rounded_corner_white,
//                    null
//                )
//            )
//
//            binding.tvTabAll.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.mdiTabAll.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.llTabAll.setBackground(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.border_rounded_corner_white,
//                    null
//                )
//            )
//
//
//            supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.fvNotification,
//                    NotificationListFragment.newInstance(Constants.GENERAL_NOTIFICAITON)
//                )
//                .commit()
//        }
//
//        binding.llTabDoctorFeedback.setOnClickListener {
//            binding.llTabDoctorFeedback.setBackground(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.border_rounded_corner,
//                    null
//                )
//            )
//            binding.tvTabDoctorFeedback.setTextColor(
//                ResourcesCompat.getColor(resources, R.color.white, null)
//            )
//            binding.mdiTabDoctorFeedbackIcon.setTextColor(
//                ResourcesCompat.getColor(resources, R.color.white, null)
//            )
//
//
//            binding.tvTabNotifiation.setTextColor(
//                ResourcesCompat.getColor(resources, R.color.black, null)
//            )
//            binding.mdiTabNotifiation.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.llTabGeneralNotification.setBackground(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.border_rounded_corner_white,
//                    null
//                )
//            )
//
//            binding.tvTabAll.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.mdiTabAll.setTextColor(
//                ResourcesCompat.getColor(
//                    resources,
//                    R.color.black,
//                    null
//                )
//            )
//            binding.llTabAll.setBackground(
//                ResourcesCompat.getDrawable(
//                    resources,
//                    R.drawable.border_rounded_corner_white,
//                    null
//                )
//            )
//
//
//            supportFragmentManager.beginTransaction()
//                .replace(
//                    R.id.fvNotification,
//                    NotificationListFragment.newInstance(Constants.DOCTOR_FEEDBACK_NOTIFICATION)
//                )
//                .commit()
//        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.common_menu_search, menu)
        val item = menu?.findItem(R.id.action_search);
        val searchView = item?.actionView as SearchView
        // search queryTextChange Listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (!query.equals("")) {
//                    fragmentCommunicator.passData("Yeasin")
                }

                return true
            }
        })


        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (App.getContext().appSettings == null) {
            App.getContext().readAppSettings(this@NotificationActivity)
        }
        if (!SystemUtility.isAutoTimeEnabled(this@NotificationActivity)) {
            SystemUtility.openDateTimeSettingsActivity(this@NotificationActivity)
        }
    }
}