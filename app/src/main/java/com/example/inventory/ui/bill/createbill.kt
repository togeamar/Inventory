package com.example.inventory.ui.bill

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.item.ItemEntryBody
import com.example.inventory.ui.item.ItemEntryDestination
import com.example.inventory.ui.item.formatedPrice
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

object Createbilldestination : NavigationDestination {
    override val route = "cbill"
    override val titleRes = R.string.cbill
}


@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun createbill(
    modifier: Modifier=Modifier,
    viewModel: billViewModel  ,
    onpaydebit: () -> Unit)
{
    val coststate by viewModel.cost.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val homeUiState by viewModel.homeUiState.collectAsState()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(id = Createbilldestination.titleRes),
                canNavigateBack = false,
                navigateUp = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.show()},
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large), bottom = 70.dp)
            ){
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = stringResource(R.string.item_entry_title)
                )
            }
        },
    ) { innerPadding ->
        val historystate=viewModel.historyState
        if (coststate.show){
            alertdialog(cost = coststate.totalcost, ondismiss = {viewModel.hide()},historystate=historystate, onpaydebit = {onpaydebit()
            viewModel.update(2)
            viewModel.hide()
            coroutineScope.launch {  viewModel.updatequantity()
            viewModel.zeroselected()
            viewModel.inserthistory()
            viewModel.updatecolor()}
            },onpaycredit = {onpaydebit()
                viewModel.update(2)
                viewModel.hide()
                coroutineScope.launch {  viewModel.updatequantity()
                    viewModel.zeroselected()
                    viewModel.inserthistory() }
            })
        }
        billbody(totalcosti = {int->
            coroutineScope.launch {
                viewModel.totalcosti(int)
            }
        },onincrease = {itemid ->
            coroutineScope.launch{
            viewModel.incselected(itemid)
        }},ondecrease = {itemid ->
            coroutineScope.launch{
                viewModel.remselected(itemid)
            }},
            itemList = homeUiState.itemList,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        )

    }
}



@Composable
fun billbody(totalcosti:(Int)->Unit,ondecrease:(Int)->Unit,onincrease:(Int)->Unit,itemList: List<Item>,modifier: Modifier=Modifier){

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (itemList.isEmpty()) {
            Text(
                text = "No Items in inventory \n" +
                        "add them first",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            InventoryList(ondecrease = ondecrease,onincrease = onincrease, itemList = itemList, onItemClick ={} )
            var totalcost =0
            itemList.forEach {item->
                totalcost=totalcost+(item.selectedcount*item.price).toInt()
            }
            Text(text = "$totalcost$")
            totalcosti(totalcost)
        }
    }

}


@Composable
private fun InventoryList(ondecrease:(Int)->Unit,onincrease:(Int)->Unit,
    itemList: List<Item>, onItemClick: (Item) -> Unit, modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier) {
        items(items = itemList, key = { it.id }) { item ->
            InventoryItem(ondecrease = ondecrease, onincrease = onincrease,item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .clickable { onItemClick(item) })
        }
    }

    
}




@Composable
private fun InventoryItem(ondecrease:(Int)->Unit,onincrease:(Int)->Unit,
    item: Item, modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.weight(1f))
                Icon(Icons.Filled.Close, contentDescription ="",modifier=Modifier.clickable { if (item.selectedcount>0){ondecrease(item.id)} })
                Text(
                    text = item.selectedcount.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                val context = LocalContext.current
                Icon(Icons.Filled.Add, contentDescription ="",modifier=Modifier.clickable { if (item.quantity>0 &&item.quantity>item.selectedcount){onincrease(item.id)}
                else{Toast.makeText(context,"add ${item.name} to the inventory",Toast.LENGTH_SHORT).show()} } )
            }
            Row(modifier=Modifier.fillMaxWidth() ) {
                Text(
                    text = stringResource(R.string.in_stock, item.quantity),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = item.formatedPrice(),
                    style = MaterialTheme.typography.titleMedium
                )

            }

        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun alertdialog(cost:Int,ondismiss:()->Unit,historystate: historystate,onpaydebit:()->Unit,onpaycredit: () -> Unit){
    var edit by remember {
        mutableStateOf(historystate.name)}
    AlertDialog(onDismissRequest = { ondismiss()}) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value =edit ,
                onValueChange = {edit=it
                    historystate.name=it

                },
                placeholder = {
                    Text(text = "First name")
                }
            )
            TextField(
                value = cost.toString(),
                onValueChange = {
                },
                placeholder = {
                    Text(text = "Last name")
                }
            )
            Row {
                Button(onClick = {
                    historystate.bill=cost
                    onpaydebit()
                },colors = ButtonDefaults.buttonColors(Color.Green)) {
                    Text(text = "pay now")
                }
                Button(onClick = {
                    historystate.bill=cost
                    onpaycredit()
                }, colors = ButtonDefaults.buttonColors(Color.Red)){
                    Text(text = "pay later")
                }

            }

        }
    }
}