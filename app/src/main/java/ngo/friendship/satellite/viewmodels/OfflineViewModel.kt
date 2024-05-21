package ngo.friendship.satellite.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ngo.friendship.satellite.App
import ngo.friendship.satellite.model.Beneficiary
import ngo.friendship.satellite.model.PatientInterviewDoctorFeedback
import ngo.friendship.satellite.model.RequisitionInfo
import ngo.friendship.satellite.model.SavedInterviewInfo
import ngo.friendship.satellite.model.StockAdjustmentInfo
import ngo.friendship.satellite.repository.DBRepository
import javax.inject.Inject


@HiltViewModel
class OfflineViewModel @Inject constructor(
    private val dbRepository: DBRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    init {
        App.loadApplicationData(context)
    }

    private var pageNumber = 1
    private val pageSize = 30

    private val dataList: MutableLiveData<ArrayList<Beneficiary>?> =
        MutableLiveData<ArrayList<Beneficiary>?>()

    private val myStringLiveData = MutableLiveData<String>()

    fun getMyStringLiveData(): MutableLiveData<String> {
        return myStringLiveData
    }

    fun setMyString(myString: String) {
        myStringLiveData.value = myString
    }

    fun getBeneficiaryList(type: String,hhNumber: String): LiveData<ArrayList<Beneficiary>> {
        return dbRepository.getBeneficiaryList(type,hhNumber)
    }

    fun getBeneficiaryList(type: String): LiveData<ArrayList<Beneficiary>> {
        return dbRepository.getBeneficiaryList(type)
    }


    /*** pagination mechanism start */
    fun getBeneficiaryListWithPagination(type: String,offset:Int,limit:Int) {
         val newData:ArrayList<Beneficiary> = dbRepository.getBeneficiaryListWithPagination(type,offset,limit)
        if(!newData.isEmpty()){
            var currentData: ArrayList<Beneficiary>? = dataList.value
            if (currentData == null) {
                currentData = ArrayList<Beneficiary>()
            }

            currentData.addAll(newData);
            dataList.setValue(currentData);
        }

    }

    fun getData(type: String,offset:Int,limit:Int): MutableLiveData<ArrayList<Beneficiary>?> {
        // Load data for the initial page
        getBeneficiaryListWithPagination(type,offset,limit)
        return dataList
    }

    fun loadNextPage(type: String) {
        // Increment page number and load the next page of data
        pageNumber++
        getBeneficiaryListWithPagination(type,pageNumber*pageSize,pageSize)
    }


    /*** pagination mechanism end */


    fun getPatientInterviewDoctorFeedbackList(
        sendStatus: Long
    ): LiveData<ArrayList<PatientInterviewDoctorFeedback>> {
        return dbRepository.getPatientInterviewDoctorFeedbackList(sendStatus)
    }

    fun getCurrentMonthInterviewCount(): LiveData<Long> {
        return dbRepository.getCurrentMonthInterviewCount()
    }

    fun getCurrentTodayInterviewCount(): LiveData<Long> {
        return dbRepository.getCurrentTodayInterviewCount()
    }
   fun getMonthlySessionCount(): LiveData<Long> {
        return dbRepository.getMonthlySessionCount()
    }
    fun getInterviewListByBenefCode(benefCode:String):  LiveData<java.util.ArrayList<SavedInterviewInfo>> {
        return dbRepository.getInterviewListByBenefCode(benefCode);
    }

    fun getSingleBenef(benefCode:String):  LiveData<Beneficiary> {
        return dbRepository.getSingleBenef(benefCode);
    }

    fun getRequisitionMedicineInfo(requisitionId:Long):LiveData<RequisitionInfo>{
        return dbRepository.getRequisitionMedicineInfo(requisitionId)

    }

    fun getAllRequisition():LiveData<List<RequisitionInfo>>{
        return dbRepository.getAllRequisition()

    }

    fun getRequisitionWithMedicineById(status:String,id:String):LiveData<RequisitionInfo>{
        return dbRepository.getRequisitionWithMedicineById(status,id)

    }

    fun getStockAdjustmentInfoList():LiveData<List<StockAdjustmentInfo>>{
        return dbRepository.getStockAdjustmentInfoList()

    }


}