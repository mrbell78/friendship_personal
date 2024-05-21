package ngo.friendship.satellite.interfaces

import ngo.friendship.satellite.model.MyData

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving onRetrieveMyData events.
 * The class that is interested in processing a onRetrieveMyData
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's `addOnRetrieveMyDataListener` method. When
 * the onRetrieveMyData event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OnRetrieveMyDataEvent
`` */
interface OnRetrieveMyDataListener {
    /**
     * On data retrieve finished.
     *
     * @param myData the my data
     */
    fun onDataRetrieveFinished(myData: MyData?)
}