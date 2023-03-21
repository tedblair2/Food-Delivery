package com.example.fud.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fud.model.Cart

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCart(cart: Cart)

    @Update
    suspend fun updateCart(cart: Cart)

    @Delete
    suspend fun deleteCart(cart: Cart)

    @Query("SELECT * FROM cart_table ORDER BY id ASC")
    fun getCart():LiveData<List<Cart>>

    @Query("DELETE FROM cart_table")
    suspend fun deleteAllCart()
}