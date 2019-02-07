package com.example.joni.assignment.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.time.OffsetDateTime

@Entity
data class Observation(
        @PrimaryKey(autoGenerate = true)
        val observationid: Long,
        val species: String,
        val rarity: String,
        val note: String,
        val date: String,
        val time: String,
        val image: String?,
        val orderDate: OffsetDateTime) {
    override fun toString(): String = "$observationid $species $rarity $note $date $time $image"
}