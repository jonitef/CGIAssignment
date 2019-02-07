package com.example.joni.assignment.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface ObservationDao {

    @Query("SELECT * FROM observation ORDER BY datetime(orderDate) DESC")
    fun getAllOrderedByDate(): LiveData<List<Observation>>

    @Query("SELECT * FROM observation WHERE rarity == :rarity ORDER BY datetime(orderDate) DESC")
    fun getAllOrderedByDateWithRarity(rarity: String): LiveData<List<Observation>>

    @Query("SELECT * FROM observation ORDER BY species ASC")
    fun getAllOrderedBySpecies(): LiveData<List<Observation>>

    @Query("SELECT * FROM observation WHERE rarity == :rarity ORDER BY species ASC")
    fun getAllOrderedBySpeciesWithRarity(rarity: String): LiveData<List<Observation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(observation: Observation)
}