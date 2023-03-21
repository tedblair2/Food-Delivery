package com.example.fud.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.fud.model.Favorite

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorite_table ORDER BY foodId ASC")
    fun getFavorites():LiveData<List<Favorite>>
}