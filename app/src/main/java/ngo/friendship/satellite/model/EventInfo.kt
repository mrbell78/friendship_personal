package ngo.friendship.satellite.model

import java.io.Serializable

/**
 *
 * @author Mohammed Jubayer
 */
// Table(name = "event_info")
class EventInfo : Serializable {
    // Column(name = "EVENT_ID") id
    var eventId: Long = 0

    //Column(name = "EVENT_NAME")
    var eventName: String? = null

    //Column(name = "EVENT_DESC")
    var eventDesc: String? = null

    //Column(name = "CREATE_DATE")
    var createDate: Long = 0

    //Column(name = "EVENT_DATE")
    var eventDate: Long = 0

    //Column(name = "TYPE")
    var type: String? = null

    //Column(name = "LOCATION_ID")
    var locationId: Long = 0

    //Column(name = "STATE")
    var state: Long = 0

    //Column(name = "CREATED_BY")
    var createdBy: Long = 0

    //Column(name = "VERSION_NO")
    var versionNo: Long = 0

    companion object {
        const val MODEL_NAME = "event_info"
    }
}