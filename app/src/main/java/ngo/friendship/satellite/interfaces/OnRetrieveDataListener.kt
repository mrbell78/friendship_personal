package ngo.friendship.satellite.interfaces

import ngo.friendship.satellite.communication.ResponseData

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving onRetrieveData events.
 * The class that is interested in processing a onRetrieveData
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's `addOnRetrieveDataListener` method. When
 * the onRetrieveData event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OnRetrieveDataEvent
`` */
interface OnRetrieveDataListener {
    /**
     * On data retrieve finished.
     *
     * @param webResponseDataInfo the web response info
     */
    fun onDataRetrieveFinished(webResponseDataInfo: ResponseData?)
}