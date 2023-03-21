package com.example.fud.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.fud.model.Cart
import com.example.fud.model.Favorite
import com.example.fud.room.CartDatabase
import com.example.fud.repository.FudRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FudViewModel(application: Application):AndroidViewModel(application) {

    val cart:LiveData<List<Cart>>
    val favorites:LiveData<List<Favorite>>
    private val repository:FudRepository

    init {
        val orderDao= CartDatabase.getDatabase(application).orderDao()
        val favoriteDao=CartDatabase.getDatabase(application).favoriteDao()
        repository=FudRepository(orderDao,favoriteDao)
        cart=repository.cart
        favorites=repository.favorites
    }
    fun addFavorite(favorite: Favorite){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFavorite(favorite)
        }
    }
    fun deleteFavorite(favorite: Favorite){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavorite(favorite)
        }
    }

    fun addCart(cart: Cart){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCart(cart)
        }
    }
    fun updateCart(cart: Cart){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCart(cart)
        }
    }
    fun deleteCart(cart: Cart){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCart(cart)
        }
    }
    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }
}