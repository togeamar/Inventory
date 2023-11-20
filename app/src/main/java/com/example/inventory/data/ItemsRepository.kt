package com.example.inventory.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */

interface ItemsRepository {

    /**
     * Retrieve all the items from the the given data source.
     */

    fun getAllItemsStream(): Flow<List<Item>>


    fun getallhistorystream():Flow<List<history>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */

    fun getItemStream(id: Int): Flow<Item?>

    /**
     * Insert item in the data source
     */

    suspend fun insertItem(item: Item)

    /**
     * Delete item from the data source
     */

    suspend fun deleteItem(item: Item)

    suspend fun deletehistory(history: history)

    /**
     * Update item in the data source
     */

    suspend fun updateItem(item: Item)

    suspend fun incselected(id: Int)

    suspend fun remselected(id: Int)

    suspend fun updatequantity()

    suspend fun updatecolor()

    suspend fun updatecolor2(id: Int)

    suspend fun zeroselected()

    suspend fun inserthistory(history: history)
}
