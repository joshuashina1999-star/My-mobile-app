package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_table")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: String,
    val durationMinutes: Int,
    val caloriesBurned: Double,
    val distanceKm: Double?,
    val dateMillis: Long = System.currentTimeMillis(),
    val notes: String = ""
)
