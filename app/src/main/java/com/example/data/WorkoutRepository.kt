package com.example.data

import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    val allWorkouts: Flow<List<Workout>> = workoutDao.getAllWorkouts()

    suspend fun insertWorkout(workout: Workout) {
        workoutDao.insertWorkout(workout)
    }

    suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout)
    }

    suspend fun clearAll() {
        workoutDao.clearAll()
    }
}
