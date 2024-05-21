package ngo.friendship.satellite.interfaces

import ngo.friendship.satellite.communication.ResponseData

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving onDataSent events.
 * The class that is interested in processing a onDataSent
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's `addOnDataSentListener` method. When
 * the onDataSent event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OnDataSentEvent
`` */
interface OnDataSentListener {
    /**
     * On data sending finished.
     *
     * @param webResponseDataInfo the web response info
     */
    fun onDataSendingFinished(webResponseDataInfo: ResponseData?)
}