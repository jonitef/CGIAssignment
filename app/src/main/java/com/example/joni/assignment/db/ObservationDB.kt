package com.example.joni.assignment.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = [(Observation::class)], version = 1)
@TypeConverters(Converter::class)
abstract class ObservationDB: RoomDatabase() {

    abstract fun observationDao(): ObservationDao

    companion object {
        private var sInstance: ObservationDB? = null
        @Synchronized
        fun get(context: Context): ObservationDB {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.applicationContext, ObservationDB::class.java, "observation.db").allowMainThreadQueries().build()
            }
            return sInstance!!
        }
    }

}