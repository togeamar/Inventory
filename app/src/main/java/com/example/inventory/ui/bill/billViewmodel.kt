package com.example.inventory.ui.bill

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import com.example.inventory.data.history
import com.example.inventory.ui.item.ItemUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

import kotlinx.coroutines.withContext
import java.text.NumberFormat


class billViewModel(val itemsRepository: ItemsRepository) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    val homeUiState: StateFlow<HomeUiState> = itemsRepository.getAllItemsStream().map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    suspend fun incselected(id:Int){withContext(Dispatchers.IO) {
        itemsRepository.incselected(id) }
    }

    suspend fun remselected(id:Int) {
        withContext(Dispatchers.IO) {
            itemsRepository.remselected(id)
        }
    }
    suspend fun updatequantity(){
        withContext(Dispatchers.IO){
            itemsRepository.updatequantity()
        }
    }
    suspend fun updatecolor(){
        withContext(Dispatchers.IO){
            itemsRepository.updatecolor()
        }
    }
    suspend fun zeroselected(){
        withContext(Dispatchers.IO){
            itemsRepository.zeroselected()
        }
    }
    var historyState by mutableStateOf(historystate())
        private set
    suspend fun inserthistory(){
        itemsRepository.inserthistory(historyState.toItem())
    }
    val _cost= MutableStateFlow(totalcoststate(0,false))
    val cost:StateFlow<totalcoststate> =_cost.asStateFlow()
    fun totalcosti(totalcost: Int){
        _cost.update {state ->
            state.copy(totalcost=totalcost)
        }
    }
    fun show(){
        _cost.update {state ->
            state.copy(show = true)
        }
    }
    fun hide(){
        _cost.update {state ->
            state.copy(show = false)
        }
    }

    val _selecteditemxindex= MutableStateFlow(selecteditemsindex(0))
    val selecteditemindex: StateFlow<selecteditemsindex> =_selecteditemxindex.asStateFlow()

    fun update(index:Int){
        _selecteditemxindex.update {state ->
            state.copy(index = index)
        }
        Log.d("changed",selecteditemindex.value.index.toString())
    }

}


data class historystate(
    val id: Int=0,
    var name:String="",
    var bill:Int=0,
    var paid:Boolean=false
)

fun historystate.toItem():history= history(
    id = id,
    name = name,
    bill = bill,
    paid=paid
)
fun history.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(bill)
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val itemList: List<Item> = listOf())

data class totalcoststate(val totalcost:Int,val show:Boolean)

data class selecteditemsindex(val index:Int)
