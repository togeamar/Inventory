package com.example.inventory.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface itemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Insert()
    suspend fun inserthistory(history: history)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Delete
    suspend fun deletehistory(history: history)

    @Query("SELECT * from items WHERE id = :id")
    fun getItem(id: Int): Flow<Item>

    @Query("SELECT * from items ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * from payhistory")
    fun getAllhistory(): Flow<List<history>>

    @Query("UPDATE items set selectedcount=selectedcount+1 where id=:id")
    fun incselected(id:Int)

    @Query("UPDATE items set selectedcount=selectedcount-1 where id=:id")
    fun remselected(id:Int)

    @Query("UPDATE items set selectedcount=0 ")
    fun zeroselected()

    @Query("update items set quantity=quantity-selectedcount")
    fun updatequantity()

    @Query("update payhistory set paid=1")
    fun updatecolor()

    @Query("update payhistory set paid=1 where id=:id")
    fun updatecolor2(id: Int)




}