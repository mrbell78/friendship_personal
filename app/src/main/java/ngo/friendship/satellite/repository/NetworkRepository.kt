package ngo.friendship.satellite.repository;

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.frindshipassignment.api.EndPoints
import ngo.friendship.satellite.model.demo.Product
import ngo.friendship.satellite.storage.SatelliteCareDatabaseManager
import javax.inject.Inject

class NetworkRepository @Inject constructor (private val mHealthApi: EndPoints, private val db: SatelliteCareDatabaseManager){
    private val _products = MutableLiveData<Product>()

    val products: LiveData<Product>
        get() = _products

    suspend fun getProducts(){
        val result = mHealthApi.getProducts()
        if(result.isSuccessful && result.body() != null){
            _products.postValue(result.body())
        }
    }

}