package com.example.fud.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fud.model.Cart
import com.example.fud.model.Favorite

@Database(entities = [Cart::class,Favorite::class], version = 1 , exportSchema = false)
abstract class CartDatabase:RoomDatabase() {

    abstract fun orderDao(): CartDao
    abstract fun favoriteDao():FavoriteDao

    companion object{
        @Volatile
        private var INSTANCE: CartDatabase?=null

        fun getDatabase(context: Context): CartDatabase {
            val tempInstance= INSTANCE
            if (tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    CartDatabase::class.java,
                    "fud_database_6"
                ).build()
                INSTANCE =instance
                return instance
            }
        }
    }
}