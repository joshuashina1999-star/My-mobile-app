package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Workout
import com.example.data.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository
    val allWorkouts: StateFlow<List<Workout>>

    // Daily Goals
    val calorieGoal = 600.0 // Active calorie burn goal
    val waterGoalMl = 2500
    val stepGoal = 10000

    // Water intake & Custom Steps (tracked in memory for fast reactivity, resetable)
    private val _waterIntakeMl = MutableStateFlow(1250) // starts with a filled base
    val waterIntakeMl: StateFlow<Int> = _waterIntakeMl

    private val _stepCount = MutableStateFlow(5420) // starts with some step progress
    val stepCount: StateFlow<Int> = _stepCount

    private val _adPoints = MutableStateFlow(250) // Premium tracker points
    val adPoints: StateFlow<Int> = _adPoints

    init {
        val workoutDao = AppDatabase.getDatabase(application).workoutDao
        repository = WorkoutRepository(workoutDao)
        allWorkouts = repository.allWorkouts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Dynamic state derived from workouts
    val todaysWorkoutsCount = allWorkouts.map { list ->
        list.filter { isToday(it.dateMillis) }.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val todaysCaloriesBurned = allWorkouts.map { list ->
        list.filter { isToday(it.dateMillis) }.sumOf { it.caloriesBurned }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun insertWorkout(type: String, duration: Int, calories: Double, distance: Double?, notes: String) {
        viewModelScope.launch {
            val workout = Workout(
                type = type,
                durationMinutes = duration,
                caloriesBurned = calories,
                distanceKm = distance,
                notes = notes
            )
            repository.insertWorkout(workout)
        }
    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            repository.deleteWorkout(workout)
        }
    }

    fun clearAllWorkouts() {
        viewModelScope.launch {
            repository.clearAll()
            _waterIntakeMl.value = 0
            _stepCount.value = 0
        }
    }

    fun addWater(amount: Int) {
        _waterIntakeMl.value = (_waterIntakeMl.value + amount).coerceAtLeast(0)
    }

    fun addSteps(amount: Int) {
        _stepCount.value = (_stepCount.value + amount).coerceAtLeast(0)
    }

    fun addAdPoints(amount: Int) {
        _adPoints.value = _adPoints.value + amount
    }

    private fun isToday(timeMillis: Long): Boolean {
        val today = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timeMillis }
        return today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }
}
