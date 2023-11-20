package com.example.inventory.data

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Entity data class represents a single row in the database.
 */

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val quantity: Int,
    val selectedcount:Int=0
)

@Entity(tableName = "payhistory")
data class history(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val name:String,
    val bill:Int,
    val paid:Boolean=false
)