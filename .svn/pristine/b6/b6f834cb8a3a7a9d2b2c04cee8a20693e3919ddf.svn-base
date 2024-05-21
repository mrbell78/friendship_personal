package ngo.friendship.satellite.ui

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ngo.friendship.satellite.App
import ngo.friendship.satellite.R
import ngo.friendship.satellite.base.BaseActivity
import ngo.friendship.satellite.service.ManualSyncService

// TODO: Auto-generated Javadoc
/**
 * The Class NotificationShowActivity.
 */
class NotificationShowActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
        App.loadApplicationData(this)
        val b = intent.extras
        val notifId = b!!.getInt("NOTIF_ID")
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.cancel(notifId)
        if (ManualSyncService.notificationItemList != null && ManualSyncService.notificationItemList.size > 0) {
            showNotifItem()
        } else {
            finish()
        }
    }

    /**
     * Display activity exit prompt dialog.
     */
    private fun showNotifItem() {
        val builder = AlertDialog.Builder(this)
        val view = View.inflate(this, R.layout.notification_dialog_layout, null)
        builder.setView(view)
        val tvDialogTitle = view.findViewById<TextView>(R.id.tv_dialog_title)
        tvDialogTitle.setText(R.string.dialog_title)
        tvDialogTitle.setTextColor(Color.BLACK)
        val imgIcon = view.findViewById<ImageView>(R.id.img_dialog_title)
        imgIcon.setImageResource(R.drawable.warning)

        /*
		 * Display notification item in a list
		 */
        val llRowContainer = view.findViewById<LinearLayout>(R.id.ll_row_container)
        var prevType: String? = null
        for (item in ManualSyncService.notificationItemList) {
            /*
			 * Check if different type found. If so then display the type in title
			 */
            Log.e("NOTIFICATION SHOW", item.toString() + "")
            if (prevType == null || !prevType.equals(item.notificationType, ignoreCase = true)) {
                prevType = item.notificationType
                val tv = TextView(this@NotificationShowActivity)
                tv.textSize = 20f
                tv.setBackgroundColor(Color.rgb(200, 200, 200))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                tv.layoutParams = params
                tv.setPadding(5, 5, 5, 5)
                tv.setTextColor(Color.BLACK)
                llRowContainer.addView(tv)
                tv.text = item.notificationType
            }

            /*
			 * Add item row with data.
			 */
            val rowView = View.inflate(this, R.layout.notification_dialog_row, null)
            val tvTitle = rowView.findViewById<TextView>(R.id.tv_title)
            tvTitle.text = Html.fromHtml(item.title)
            val tvDate = rowView.findViewById<TextView>(R.id.tv_date)
            if (item.date != null && !item.date.equals("", ignoreCase = true)) {
                tvDate.visibility = View.VISIBLE
                tvDate.text = item.date
            } else {
                tvDate.visibility = View.GONE
            }
            llRowContainer.addView(rowView)
        }
        val dialog = builder.create()
        builder.setCancelable(false)
        val btnYes = view.findViewById<Button>(R.id.btn_ok)
        btnYes.setText(R.string.btn_yes)
        btnYes.setOnClickListener {
            dialog.dismiss()
            if (!App.getContext().isActivityRunning) {
                startActivity(Intent(this@NotificationShowActivity, LauncherActivity::class.java))
            }
            finish()
        }
        val btnNo = view.findViewById<Button>(R.id.btn_close)
        btnNo.setText(R.string.btn_close)
        btnNo.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    public override fun onStart() {
        super.onStart()
        App.getContext().onStartActivity(this)
    }

    public override fun onStop() {
        super.onStop()
        App.getContext().onStartActivity(this)
    }
}