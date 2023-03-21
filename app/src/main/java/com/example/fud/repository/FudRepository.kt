package com.example.fud.repository

import com.example.fud.model.Cart
import com.example.fud.model.Favorite
import com.example.fud.room.CartDao
import com.example.fud.room.FavoriteDao

class FudRepository(private val cartDao: CartDao,private val favoriteDao: FavoriteDao) {

    suspend fun addCart(cart: Cart)=cartDao.addCart(cart)

    suspend fun updateCart(cart: Cart)=cartDao.updateCart(cart)

    suspend fun deleteCart(cart: Cart)=cartDao.deleteCart(cart)

    suspend fun deleteAll()=cartDao.deleteAllCart()

    val cart = cartDao.getCart()

    val favorites=favoriteDao.getFavorites()

    suspend fun addFavorite(favorite: Favorite)=favoriteDao.addFavorite(favorite)

    suspend fun deleteFavorite(favorite: Favorite)=favoriteDao.deleteFavorite(favorite)
}