package ngo.friendship.satellite.model

import java.io.Serializable

/**
 * @author Md.Yeasin Ali
 * @created 01th Oct 2022
 */
class NotificationItem : Serializable {

    //Column(name = "NOTIFICATION_ID")
    var notificationId: Long = 0
    var sNotificationId: Long = 0

    //Column(name = "CONTENT")
    var content: String? = null

    //Column(name = "TITLE")
    var title: String? = null

    //Column(name = "NOTIFICATION_TYPE")
    var notificationType: String? = null

    //Column(name = "CREATE_DATE")
    var createDate: String? = null

    //Column(name = "NOTIFICATION_STATUS")
    var notificationStatus: Long = 0

    //Column(name = "STATE")
    var state = 0

    //Column(name = "VIEW_TIME")
    var viewTime: String? = null

    //Column(name = "BENEF_CODE")
    var benefCode: String? = null

    //Column(name = "NOTIFICATION_TIME")
    var notificationTime: String? = null

    //Column(name = "UPDATE_SOURCE")
    var updateSource: String? = null

    //Column(name = "NOTIFICATION_ICON")
    var notificationIcon: String? = null

    //Column(name = "DESTINATION_PATH")
    var destinationPath: String? = null

    //Column(name = "DESTINATION_KEY")
    var destinationKey: String? = null
    var date: String? = null
    var versionNo: Long = 0
    var orgId: Long = 0



    constructor()
    constructor(titile:String ,content:String,time:String,benefCode:String )
    companion object {
        const val MODEL_NAME = "notification_master"
    }
}