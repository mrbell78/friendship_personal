package ngo.friendship.satellite.model

import java.io.Serializable
import java.util.ArrayList

// TODO: Auto-generated Javadoc
/**
 * The Class MedicineSellInfo.
 */
class MedicineSellInfo : Serializable {
    /**
     * Gets the consumption id.
     *
     * @return the consumption id
     */
    /**
     * Sets the consumption id.
     *
     * @param consumptionId the new consumption id
     */
    /** The consumption id.  */
    var consumptionId: Long = 0
    /**
     * Gets the medicine list.
     *
     * @return the medicine list
     */
    /**
     * Sets the medicine list.
     *
     * @param medicineList the new medicine list
     */
    /** The medicine list.  */
    var medicineList: ArrayList<MedicineInfo>? = null
}