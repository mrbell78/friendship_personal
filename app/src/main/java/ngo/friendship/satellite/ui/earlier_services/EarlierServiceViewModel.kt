package ngo.friendship.satellite.ui.earlier_services

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EarlierServiceViewModel : ViewModel() {
    val selected = MutableLiveData<String>()

    fun selectedItem(item: String) {
        selected.value = item
    }

}