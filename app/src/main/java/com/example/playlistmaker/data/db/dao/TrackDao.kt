package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Query("DELETE FROM track_table WHERE trackId= :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM track_table ORDER BY id DESC")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId = :id)")
    suspend fun inFavorite(id: Int): Boolean
}