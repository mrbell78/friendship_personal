package ngo.friendship.satellite.model

import kotlin.Throws
import ngo.friendship.satellite.error.MhealthException
import org.json.JSONObject
import ngo.friendship.satellite.App
import ngo.friendship.satellite.error.ErrorCode
import org.json.JSONException
import java.io.Serializable

class Household : Serializable {
    var hhId: Long = 0
    var hhNumber: String? = null
    var hhName: String? = null
    var locationId: Long = 0
    var hhGrade: String? = null
    var createDate: Long = 0
    var latitude = 0.0
    var longitude = 0.0
    var refDataId: Long = 0
    var state: Long = 0
    var noOfFamilyMember: Long = 0
    var regDate: Long = 0
    var sent: Long = 0
    var monthlyFamilyExpenditure: String? = null
    var hhAdultWomen: Long = 0
    var hhCharacter: String? = null
    var householdHeadName: String? = null
    var householdHeadCode: String? = null
    var householdHeadAge: String? = null
    var householdHeadImagePath: String? = null
    var householdHeadGender: String? = null
    var householdHeadOccupation: String? = null
    var householdHeadMobile: String? = null
    var isHasLocation = false
    var numberOfBeneficiary: Long = 0
    var latitudeStr: String? = null
    var longitudeStr: String? = null
    var updateHistory: String? = null
    var fullHouseHoldNumber: String? = null
    var interviewDate: String? = null

    @Throws(MhealthException::class)
    fun toJson(): JSONObject {
        return try {
            val jHouseholdInfoObj = JSONObject()
            jHouseholdInfoObj.put("HH_NUMBER", App.getContext().userInfo.userCode + hhNumber)
            jHouseholdInfoObj.put("NO_OF_FAMILY_MEMBER", noOfFamilyMember)
            jHouseholdInfoObj.put("MONTHLY_FAMILY_EXPENDITURE", monthlyFamilyExpenditure)
            jHouseholdInfoObj.put("LATITUDE", latitude)
            jHouseholdInfoObj.put("LONGITUDE", longitude)
            jHouseholdInfoObj.put("REG_DATE", regDate)
            jHouseholdInfoObj.put("STATE", state)
            jHouseholdInfoObj.put("UPDATE_HISTORY", updateHistory)
            jHouseholdInfoObj.put("HH_CHARACTER", hhCharacter)
            jHouseholdInfoObj.put("HH_ADULT_WOMEN", hhAdultWomen)
            jHouseholdInfoObj.put("HH_NAME", hhName)
            jHouseholdInfoObj
        } catch (e: JSONException) {
            e.printStackTrace()
            throw MhealthException(ErrorCode.JSON_EXCEPTION, "JSON_EXCEPTION")
        }
    }

    companion object {
        const val MODEL_NAME = "household"
    }
}