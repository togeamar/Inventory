package com.example.inventory.ui.history

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.data.Item
import com.example.inventory.data.history
import com.example.inventory.ui.AppViewModelProvider
import com.example.inventory.ui.bill.formatedPrice
import com.example.inventory.ui.bill.historystate

import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


object PayHistoryDestination : NavigationDestination {
    override val route = "payhistory"
    override val titleRes = R.string.cbill
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun payhistory(viewModel:historyviewmodel= androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider.Factory)){
    val historyliststate by viewModel.historyliststate.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = PayHistoryDestination.route,
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                navigateUp = {}
            )
        },
    ){innerpadding ->
        HomeBody(itemList = historyliststate.historylist, onItemswipe = {coroutineScope.launch { viewModel.deletehistory(it) }},
            onItemswiperight = {coroutineScope.launch { viewModel.updatecolor2(it) }}, modifier = Modifier.padding(innerpadding))
    }
}

@Composable
private fun HomeBody(
    itemList: List<history>, onItemswipe: (history) -> Unit,onItemswiperight: (Int) -> Unit, modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (itemList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_item_description)+" in home page",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        } else {
            HistoryList(
                itemList = itemList,
                onItemswipe = {onItemswipe(it)},
                onItemswiperight = {onItemswiperight(it.id)},
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryList(itemList: List<history>, onItemswipe: (history) -> Unit,onItemswiperight:(history) ->Unit,modifier: Modifier){
        LazyColumn(modifier = modifier) {
            items(items = itemList, key = { it.id }) { history ->
                val dismissState = rememberDismissState(confirmValueChange ={if (it==DismissValue.DismissedToStart){
                    onItemswipe(history)
                }else if (it==DismissValue.DismissedToEnd){
                    onItemswiperight(history)
                }
                    true })
                SwipeToDismiss(state = dismissState, directions = setOf(DismissDirection.StartToEnd,DismissDirection.EndToStart),
                    background = {val backgroundColor by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.DismissedToStart -> Color.Black.copy(alpha = 0.8f)
                            DismissValue.DismissedToEnd ->Color.Green
                            else -> Color.White
                        }, label = ""
                    )
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(color = backgroundColor)
                                .padding(end = 16.dp), // inner padding
                        contentAlignment = Alignment.CenterEnd // place the icon at the end (left)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete",
                                tint = Color.White
                            )
                        } }, dismissContent ={
                        HistoryItem(item = history,
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.padding_small))
                                .clickable { })
                    } )
            }
        }
}

@Composable
private fun HistoryItem(
    item: history, modifier: Modifier = Modifier
) {
    var color by remember {
        mutableStateOf(Color.Unspecified)
    }
    if (item.paid){
        color= Color.Green
    }
    else{
        color= Color.Red
    }
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
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
                Text(
                    text = item.formatedPrice(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
