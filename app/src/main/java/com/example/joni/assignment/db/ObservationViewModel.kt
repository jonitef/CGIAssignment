package com.example.joni.assignment.db

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class ObservationViewModel(application: Application): AndroidViewModel(application){

    fun getObservations(sort: Int, rarity: String) = when (sort){
        1 -> {
            if (rarity == "*") ObservationDB.get(getApplication()).observationDao().getAllOrderedByDate()
            else ObservationDB.get(getApplication()).observationDao().getAllOrderedByDateWithRarity(rarity)
        }
        else -> {
            if (rarity == "*") ObservationDB.get(getApplication()).observationDao().getAllOrderedBySpecies()
            else ObservationDB.get(getApplication()).observationDao().getAllOrderedBySpeciesWithRarity(rarity)
        }
    }
}