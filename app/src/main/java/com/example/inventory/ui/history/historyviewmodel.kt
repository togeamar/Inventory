package com.example.inventory.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import com.example.inventory.data.history
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import java.text.NumberFormat

class historyviewmodel(val itemsRepository: ItemsRepository):ViewModel() {

    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val historyliststate:StateFlow<Historyliststate> =itemsRepository.getallhistorystream().map {Historyliststate(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = Historyliststate()
    )

    suspend fun updatecolor(){
        withContext(Dispatchers.IO){
            itemsRepository.updatecolor()
        }
    }
    suspend fun updatecolor2(id:Int){
        withContext(Dispatchers.IO){
            itemsRepository.updatecolor2(id)
        }
    }
    suspend fun deletehistory(history: history){
        withContext(Dispatchers.IO){
            itemsRepository.deletehistory(history)
        }
    }


}


data class Historyliststate(val historylist:List<history> = listOf())

