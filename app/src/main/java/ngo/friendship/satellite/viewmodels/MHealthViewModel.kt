package ngo.friendship.satellite.viewmodels

import androidx.lifecycle.LiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ngo.friendship.satellite.model.demo.Product
import ngo.friendship.satellite.repository.NetworkRepository
import javax.inject.Inject

@HiltViewModel
class MHealthViewModel @Inject constructor(private val repository: NetworkRepository) : ViewModel() {

    val productsLiveData : LiveData<Product>
        get() = repository.products
    init {
        viewModelScope.launch {
            delay(10000)
            repository.getProducts()
        }
    }
}