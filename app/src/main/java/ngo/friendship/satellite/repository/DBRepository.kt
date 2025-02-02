package ngo.friendship.satellite.repository;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ngo.friendship.satellite.App
import ngo.friendship.satellite.model.Beneficiary
import ngo.friendship.satellite.model.MedicineInfo
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedback
import ngo.friendship.satellite.model.RequisitionInfo
import ngo.friendship.satellite.model.SavedInterviewInfo
import ngo.friendship.satellite.model.StockAdjustmentInfo
import ngo.friendship.satellite.model.UserScheduleInfo
import ngo.friendship.satellite.storage.SatelliteCareDatabaseManager
import java.util.ArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DBRepository @Inject constructor(private val db: SatelliteCareDatabaseManager) {


    fun getBeneficiaryList(type: String,hhNumber: String): LiveData<ArrayList<Beneficiary>> {
        val data = MutableLiveData<ArrayList<Beneficiary>>()
        data.value = ArrayList()
        val fcmCode = App.getContext().userInfo.userCode
        val beneficiaryList = db.getBeneficiaryListNew(fcmCode,fcmCode+hhNumber, type)
        data.setValue(beneficiaryList)
        return data
    }
    fun getBeneficiaryList(type: String): LiveData<ArrayList<Beneficiary>> {
        val data = MutableLiveData<ArrayList<Beneficiary>>()
        data.value = ArrayList()
        val userId = App.getContext().userInfo.userId
        val beneficiaryList = db.getBeneficiaryList(type,userId)
        data.setValue(beneficiaryList)
        return data
    }

    fun getBeneficiaryListWithPagination(type: String,offset:Int,limit:Int): ArrayList<Beneficiary> {
//        val data = MutableLiveData<ArrayList<Beneficiary>>()
//        data.value = ArrayList()
        val userId = App.getContext().userInfo.userId
        val beneficiaryList = db.getBeneficiaryListWithPagination(type,userId,offset,limit)
       // data.setValue(beneficiaryList)
        return beneficiaryList
    }

    fun retrievePatientFollowup(code: String?, type: Int): LiveData<ArrayList<UserScheduleInfo>> {
        val data = MutableLiveData<ArrayList<UserScheduleInfo>>()
        data.value = ArrayList()
        var hhCode = App.getContext().userInfo.userCode
        if (code != null && code.trim { it <= ' ' }.length > 0) {
            hhCode = hhCode + code
        }
        var followUpList = ArrayList<UserScheduleInfo>()
        if (type == 0) {
            followUpList = App.getContext().db.getPatientFollowupList(hhCode)
        } else if (type == 1) {
            for (userScheduler in followUpList) {
                if (userScheduler.attendedDate > 0) {
                    followUpList.add(userScheduler)
                }
            }
        }
        if (type == 3) {
            followUpList = App.getContext().db.getPatientFollowupList(hhCode)
        }
        data.postValue(followUpList)
        return data
    }


    fun getPatientInterviewDoctorFeedbackList(
        sendStatus: Long
    ): LiveData<ArrayList<PatientInterviewDoctorFeedback>> {
        val data = MutableLiveData<ArrayList<PatientInterviewDoctorFeedback>>()
        data.value = ArrayList()
        val fcmCode = App.getContext().userInfo.userCode
        val langCode = App.getContext().appSettings.language
        val doctorFeedback = db.getPatientInterviewDoctorFeedbackList(sendStatus, langCode)
        data.setValue(doctorFeedback)
        return data
    }
    fun getCurrentMonthInterviewCount(): LiveData<Long> {
        val data = MutableLiveData<Long>()
        val langCode = App.getContext().appSettings.language
        val doctorFeedback = db.getCurrentMonthInterviewCount(langCode)
        data.setValue(doctorFeedback)
        return data
    }
    fun getCurrentTodayInterviewCount(): LiveData<Long> {
        val data = MutableLiveData<Long>()
        val langCode = App.getContext().appSettings.language
        val doctorFeedback = db.getCurrentTodayInterviewCount(langCode)
        data.setValue(doctorFeedback)
        return data
    }
    fun getMonthlySessionCount(): LiveData<Long> {
        val data = MutableLiveData<Long>()
        val langCode = App.getContext().appSettings.language
        val doctorFeedback = db.getMonthlySessionCount(langCode)
        data.setValue(doctorFeedback)
        return data
    }
    fun getInterviewListByBenefCode(benefCode:String): LiveData<ArrayList<SavedInterviewInfo>> {
        val data = MutableLiveData<ArrayList<SavedInterviewInfo>>()
        val langCode = App.getContext().appSettings.language
        data.value = db.getInterviewListByBenefCode(langCode,benefCode)
        return data
    }
    fun getSingleBenef(benefCode:String): LiveData<Beneficiary> {
        val data = MutableLiveData<Beneficiary>()
        data.value = db.getSingleBenef(benefCode)
        return data
    }

    fun getRequisitionMedicineInfo(requisitionId:Long): LiveData<RequisitionInfo> {
        val data = MutableLiveData<RequisitionInfo>()
        data.value = db.getRequisitionMedicineInfo(requisitionId)
        return data
    }

    fun getAllRequisition(): LiveData<List<RequisitionInfo>> {
        val data = MutableLiveData<List<RequisitionInfo>>()
        data.value = db.getAllRequisition()
        return data
    }

    fun getRequisitionWithMedicineById( status:String,id:String): LiveData<RequisitionInfo> {
        val data = MutableLiveData<RequisitionInfo>()
        data.value = db.getRequisitionWithMedicineById(status,id)
        return data
    }
    fun getStockAdjustmentInfoList(): LiveData<List<StockAdjustmentInfo>> {
        val data = MutableLiveData<List<StockAdjustmentInfo>>()
        data.value = db.getStockAdjustmentInfoList()
        return data
    }

//    fun retrieveCurrentStock(): LiveData<ArrayList<MedicineInfo>> {
//        val data = MutableLiveData<ArrayList<MedicineInfo>>()
//        data.value = db.retrieveCurrentStock()
//        return data
//    }
//    suspend fun getEntriesList(): ResponseData {
//      //  database.
//
//    }

}