package com.example

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Workout
import com.example.ui.AdMobBanner
import com.example.ui.AdMobManager
import com.example.ui.FitnessRingChart
import com.example.ui.theme.AccentRed
import com.example.ui.theme.BrightCyan
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LaserOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.OffWhite
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateSurface
import com.example.viewmodel.FitnessViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Ads SDK with our static production manager
        AdMobManager.init(this)

        setContent {
            MyApplicationTheme {
                val fitnessViewModel: FitnessViewModel = viewModel()
                MainAppScreen(viewModel = fitnessViewModel, activity = this)
            }
        }
    }
}

enum class Screen {
    Dashboard, LogWorkout, History, AdsPlayground
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: FitnessViewModel,
    activity: Activity
) {
    var currentScreen by remember { mutableStateOf(Screen.Dashboard) }

    // Collect variables with lifecycle awareness
    val waterIntake by viewModel.waterIntakeMl.collectAsStateWithLifecycle()
    val waterGoal = viewModel.waterGoalMl
    val stepCount by viewModel.stepCount.collectAsStateWithLifecycle()
    val stepGoal = viewModel.stepGoal
    val points by viewModel.adPoints.collectAsStateWithLifecycle()

    val calorieProgressTarget = viewModel.calorieGoal
    val activeCalorieBurn by viewModel.todaysCaloriesBurned.collectAsStateWithLifecycle()

    val workouts by viewModel.allWorkouts.collectAsStateWithLifecycle()
    val initStatus by AdMobManager.isInitialized.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SlateBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FITNESS TRACKER",
                                color = OffWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (initStatus) NeonGreen else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (initStatus) "AdMob Active" else "AdMob Loading...",
                                    color = if (initStatus) NeonGreen else Color.LightGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Premium ad points badge
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF2E1A47).copy(alpha = 0.5f))
                                .border(1.dp, LaserOrange.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .clickable { currentScreen = Screen.AdsPlayground }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Active Points",
                                tint = LaserOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$points pts",
                                color = LaserOrange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SlateSurface,
                    titleContentColor = OffWhite
                )
            )
        },
        bottomBar = {
            Column {
                // Persistent AdMob adaptive Banner ad on bottom
                AdMobBanner(
                    adUnitId = AdMobManager.BANNER_ID,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admob_banner_bottom")
                )

                NavigationBar(
                    containerColor = SlateSurface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.Dashboard,
                        onClick = { currentScreen = Screen.Dashboard },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreen,
                            selectedTextColor = NeonGreen,
                            indicatorColor = SlateCard,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.LogWorkout,
                        onClick = { currentScreen = Screen.LogWorkout },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Log Workout") },
                        label = { Text("Log") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreen,
                            selectedTextColor = NeonGreen,
                            indicatorColor = SlateCard,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_log")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.History,
                        onClick = { currentScreen = Screen.History },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "History") },
                        label = { Text("History") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreen,
                            selectedTextColor = NeonGreen,
                            indicatorColor = SlateCard,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_history")
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.AdsPlayground,
                        onClick = { currentScreen = Screen.AdsPlayground },
                        icon = { Icon(Icons.Default.Build, contentDescription = "Ads Console") },
                        label = { Text("Ads") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonGreen,
                            selectedTextColor = NeonGreen,
                            indicatorColor = SlateCard,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_ads")
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentScreen == Screen.Dashboard) {
                FloatingActionButton(
                    onClick = { currentScreen = Screen.LogWorkout },
                    containerColor = NeonGreen,
                    contentColor = SlateBackground,
                    modifier = Modifier.testTag("add_workout_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Workout Model")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SlateBackground)
        ) {
            when (currentScreen) {
                Screen.Dashboard -> DashboardScreen(
                    viewModel = viewModel,
                    activeCalorieBurn = activeCalorieBurn,
                    calorieGoal = calorieProgressTarget,
                    waterIntake = waterIntake,
                    waterGoal = waterGoal,
                    stepCount = stepCount,
                    stepGoal = stepGoal,
                    activity = activity
                )
                Screen.LogWorkout -> LogWorkoutScreen(
                    viewModel = viewModel,
                    activity = activity,
                    onNavigateToDashboard = { currentScreen = Screen.Dashboard }
                )
                Screen.History -> HistoryScreen(
                    viewModel = viewModel,
                    workouts = workouts,
                    activity = activity
                )
                Screen.AdsPlayground -> AdsPlaygroundScreen(
                    activity = activity,
                    viewModel = viewModel
                )
            }
        }
    }
}

// ---------------------------
// DASHBOARD VIEW
// ---------------------------
@Composable
fun DashboardScreen(
    viewModel: FitnessViewModel,
    activeCalorieBurn: Double,
    calorieGoal: Double,
    waterIntake: Int,
    waterGoal: Int,
    stepCount: Int,
    stepGoal: Int,
    activity: Activity
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header info
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStrokeModifier(SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "VITALITY DASHBOARD",
                        color = NeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Keep burning and earn booster logs!",
                        color = OffWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Concentric Rings Graphic + Quick Stats Panel
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Custom Canvas Ring
                FitnessRingChart(
                    calorieProgress = (activeCalorieBurn / calorieGoal).toFloat(),
                    waterProgress = (waterIntake.toFloat() / waterGoal.toFloat()),
                    stepsProgress = (stepCount.toFloat() / stepGoal.toFloat()),
                    modifier = Modifier
                        .weight(1.1f)
                        .padding(end = 8.dp),
                    centerValueText = "${activeCalorieBurn.toInt()} / ${calorieGoal.toInt()}\nkcal"
                )

                // Quick parameters breakdown
                Column(
                    modifier = Modifier.weight(0.9f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricIndicatorCard(
                        title = "Active Energy",
                        value = "${activeCalorieBurn.toInt()} kcal",
                        color = NeonGreen,
                        icon = Icons.Default.Star
                    )
                    MetricIndicatorCard(
                        title = "Hydration",
                        value = "$waterIntake / $waterGoal ml",
                        color = ElectricBlue,
                        icon = Icons.Default.Favorite
                    )
                    MetricIndicatorCard(
                        title = "Steps Walked",
                        value = "$stepCount / $stepGoal",
                        color = LaserOrange,
                        icon = Icons.Default.Favorite
                    )
                }
            }
        }

        // Quick log widget for fast tracking hydration and steps
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStrokeModifier(SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUICK ACTIVITY LOGGER",
                        color = OffWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.addWater(250) },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, ElectricBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .testTag("quick_water_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Favorite, contentDescription = "Water", tint = ElectricBlue)
                                Text("+250ml Water", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.addSteps(1000) },
                            colors = ButtonDefaults.buttonColors(containerColor = LaserOrange.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, LaserOrange.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .testTag("quick_steps_btn"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Favorite, contentDescription = "Steps", tint = LaserOrange)
                                Text("+1K Steps", color = LaserOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Sponsored Ad Booster call-out
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                border = BorderStrokeModifier(LaserOrange.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .clickable {
                            AdMobManager.showRewarded(activity) { reward ->
                                viewModel.addAdPoints(reward)
                                Toast.makeText(activity, "Added +$reward Booster Points!", Toast.LENGTH_LONG).show()
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(LaserOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Reward", tint = LaserOrange)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTIVE BOOSTER ADS",
                            color = LaserOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Enjoy video ad to earn +100pts tracker boosters",
                            color = OffWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricIndicatorCard(
    title: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = BorderStrokeModifier(SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                Text(text = value, color = OffWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ---------------------------
// PROGRESS WORKOUT INSERTER
// ---------------------------
@Composable
fun LogWorkoutScreen(
    viewModel: FitnessViewModel,
    activity: Activity,
    onNavigateToDashboard: () -> Unit
) {
    // Workout types details
    val workoutTypes = listOf(
        WorkoutTypeData("Running", "🏃", 11.5, true),
        WorkoutTypeData("Cycling", "🚴", 8.5, true),
        WorkoutTypeData("Strength", "🏋️", 6.0, false),
        WorkoutTypeData("Swimming", "🏊", 9.8, true),
        WorkoutTypeData("Walking", "🚶", 4.5, true),
        WorkoutTypeData("Yoga", "🧘", 3.2, false),
        WorkoutTypeData("Cardio", "⚡", 8.0, false)
    )

    var selectedType by remember { mutableStateOf(workoutTypes[0]) }
    var durationMinutes by remember { mutableStateOf(30) }
    var workoutNotes by remember { mutableStateOf("") }
    var userDistanceKm by remember { mutableStateOf("") }
    var calorieOverride by remember { mutableStateOf("") }

    // Computations
    val expectedCalories = (durationMinutes * selectedType.calorieMultiplier)
    val displayCalories = if (calorieOverride.isEmpty()) expectedCalories else calorieOverride.toDoubleOrNull() ?: expectedCalories

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "CHOOSE EXERCISE CATEGORY",
                color = NeonGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        // Gorgeous grid list of exercise chips
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(workoutTypes.size) { index ->
                    val type = workoutTypes[index]
                    val isSelected = selectedType.name == type.name
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) NeonGreen.copy(alpha = 0.15f) else SlateSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = type }
                            .testTag("type_chip_${type.name.lowercase()}"),
                        border = BorderStrokeModifier(if (isSelected) NeonGreen else SlateBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = type.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = type.name,
                                color = if (isSelected) NeonGreen else OffWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Duration Slider Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStrokeModifier(SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Workout Duration", color = OffWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "$durationMinutes minutes",
                            color = NeonGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Slider(
                        value = durationMinutes.toFloat(),
                        onValueChange = { durationMinutes = it.toInt() },
                        valueRange = 5f..120f,
                        steps = 23,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonGreen,
                            activeTrackColor = NeonGreen,
                            inactiveTrackColor = SlateBorder
                        ),
                        modifier = Modifier.testTag("duration_slider")
                    )
                }
            }
        }

        // Distance & Burn Card Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStrokeModifier(SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Optional Distance Field (for relevant modes)
                    if (selectedType.hasDistance) {
                        Text(
                            text = "Distance Coverage (Km)",
                            color = OffWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = userDistanceKm,
                            onValueChange = { userDistanceKm = it },
                            placeholder = { Text("e.g. 5.2", color = Color.Gray) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = OffWhite),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = SlateBorder,
                                focusedContainerColor = SlateBackground,
                                unfocusedContainerColor = SlateBackground
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("distance_input")
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Calorie Output + Overrider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Calories Burn Output", color = OffWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Formula: ${selectedType.calorieMultiplier} kcal/min",
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = "${expectedCalories.toInt()} kcal",
                            color = LaserOrange,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = calorieOverride,
                        onValueChange = { calorieOverride = it },
                        label = { Text("Override Calories Burn (Optional)", color = Color.Gray) },
                        textStyle = androidx.compose.ui.text.TextStyle(color = OffWhite),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LaserOrange,
                            unfocusedBorderColor = SlateBorder,
                            focusedContainerColor = SlateBackground,
                            unfocusedContainerColor = SlateBackground
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calories_input")
                    )
                }
            }
        }

        // Workout notes input
        item {
            OutlinedTextField(
                value = workoutNotes,
                onValueChange = { workoutNotes = it },
                label = { Text("Workout Session Notes", color = Color.Gray) },
                placeholder = { Text("Write feelings, targets reached...", color = Color.Gray) },
                textStyle = androidx.compose.ui.text.TextStyle(color = OffWhite),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = SlateBorder,
                    focusedContainerColor = SlateSurface,
                    unfocusedContainerColor = SlateSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .testTag("notes_input")
            )
        }

        // Action submit logging bottom buttons
        item {
            Button(
                onClick = {
                    val finalDistance = userDistanceKm.toDoubleOrNull()
                    viewModel.insertWorkout(
                        type = selectedType.name,
                        duration = durationMinutes,
                        calories = displayCalories,
                        distance = finalDistance,
                        notes = workoutNotes.trim()
                    )

                    // REAL AD INTENT: Save triggers the Yloverlay transient ad!
                    AdMobManager.showYloverlay(activity) {
                        Toast.makeText(activity, "Successfully saved workout!", Toast.LENGTH_SHORT).show()
                    }

                    // reset fields
                    durationMinutes = 30
                    workoutNotes = ""
                    userDistanceKm = ""
                    calorieOverride = ""

                    onNavigateToDashboard()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_workout_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = SlateBackground)
                Spacer(modifier = Modifier.width(8.dp))
                Text("SAVE WORKOUT & TRIGGER YLOVERLAY AD", color = SlateBackground, fontWeight = FontWeight.Bold)
            }
        }
    }
}

data class WorkoutTypeData(
    val name: String,
    val emoji: String,
    val calorieMultiplier: Double,
    val hasDistance: Boolean
)

// ---------------------------
// HISTORIC RECORDS LIST
// ---------------------------
@Composable
fun HistoryScreen(
    viewModel: FitnessViewModel,
    workouts: List<Workout>,
    activity: Activity
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // High level stats aggregate card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            border = BorderStrokeModifier(SlateBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            val totalCal = workouts.sumOf { it.caloriesBurned }
            val totalDuration = workouts.sumOf { it.durationMinutes }
            val totalDistance = workouts.mapNotNull { it.distanceKm }.sum()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Workouts", color = Color.Gray, fontSize = 11.sp)
                    Text("${workouts.size}", color = OffWhite, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Burned", color = Color.Gray, fontSize = 11.sp)
                    Text("${totalCal.toInt()} kcal", color = NeonGreen, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Distance", color = Color.Gray, fontSize = 11.sp)
                    Text(String.format("%.1f km", totalDistance), color = ElectricBlue, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "EXERCISE LOG RECORDS HISTORY",
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (workouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Empty",
                        tint = SlateBorder,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No saved workouts recorded",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(workouts) { item ->
                    WorkoutItemRow(
                        workout = item,
                        onDeleteClick = {
                            // REAL AD INTENT: Deleting a workout triggers the Interstitial ad!
                            AdMobManager.showInterstitial(activity) {
                                viewModel.deleteWorkout(item)
                                Toast.makeText(activity, "Log deleted.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
                item {
                    // Reset all button to clean sandbox
                    Button(
                        onClick = {
                            viewModel.clearAllWorkouts()
                            Toast.makeText(activity, "Cleared history.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed.copy(alpha = 0.15f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, AccentRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .testTag("clear_history_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Clear", tint = AccentRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("RESET STATISTICS LOGGER DATA", color = AccentRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutItemRow(
    workout: Workout,
    onDeleteClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        border = BorderStrokeModifier(SlateBorder),
        modifier = Modifier.fillMaxWidth().testTag("workout_item_${workout.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon slot
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SlateCard),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (workout.type) {
                        "Running" -> "🏃"
                        "Cycling" -> "🚴"
                        "Strength" -> "🏋️"
                        "Swimming" -> "🏊"
                        "Walking" -> "🚶"
                        "Yoga" -> "🧘"
                        else -> "⚡"
                    },
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = workout.type, color = OffWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${workout.caloriesBurned.toInt()} kcal",
                        color = LaserOrange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Duration: ${workout.durationMinutes} min" + if (workout.distanceKm != null) " • ${workout.distanceKm} km" else "",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatMillisToDate(workout.dateMillis),
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }

                if (workout.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${workout.notes}\"",
                        color = Color.LightGray.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.testTag("delete_workout_btn_${workout.id}")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AccentRed)
            }
        }
    }
}

fun formatMillisToDate(millis: Long): String {
    val formatter = java.text.SimpleDateFormat("MMM dd, hh:mm a", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(millis))
}

// Helper to provide simple visual Border Strokes in modifier chaining
@Composable
fun BorderStrokeModifier(color: Color): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(1.dp, color)
}

// ---------------------------
// AD INTEGRATION CENTER / PLAYGROUND
// ---------------------------
@Composable
fun AdsPlaygroundScreen(
    activity: Activity,
    viewModel: FitnessViewModel
) {
    val isInit by AdMobManager.isInitialized.collectAsState()
    val interState by AdMobManager.interstitialState.collectAsState()
    val rewardState by AdMobManager.rewardedState.collectAsState()
    val overlayState by AdMobManager.yloverlayState.collectAsState()
    val logs by AdMobManager.adLogs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStrokeModifier(SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Ads", tint = NeonGreen, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "REAL-TIME ADMOB CONTROL BOARD",
                            color = NeonGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This terminal connects your designated production AdMob keys directly. Double click to test instant callbacks.",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Live States Table
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                border = BorderStrokeModifier(SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CONNECTION METRICS STATUS",
                        color = OffWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    StatusItem(label = "MobileAds Initialized", statusStr = if (isInit) "READY" else "PENDING", isReady = isInit)
                    StatusItem(label = "Interstitial Ad Loaded", statusStr = getAdStateText(interState), isReady = interState == AdMobManager.AdState.Loaded)
                    StatusItem(label = "Rewarded Ad Loaded", statusStr = getAdStateText(rewardState), isReady = rewardState == AdMobManager.AdState.Loaded)
                    StatusItem(label = "Yloverlay Ad Loaded", statusStr = getAdStateText(overlayState), isReady = overlayState == AdMobManager.AdState.Loaded)
                }
            }
        }

        // Quick Show Activators
        item {
            Text(
                text = "INTERACTIVE TRIGGER ACTIVATORS",
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        AdMobManager.showInterstitial(activity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                    modifier = Modifier.weight(1f).testTag("trigger_interstitial_btn")
                ) {
                    Text("Trigger Interstitial", fontSize = 11.sp, color = SlateBackground, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        AdMobManager.showRewarded(activity) { reward ->
                            viewModel.addAdPoints(reward)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LaserOrange),
                    modifier = Modifier.weight(1f).testTag("trigger_rewarded_btn")
                ) {
                    Text("Trigger Rewarded", fontSize = 11.sp, color = SlateBackground, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        AdMobManager.showYloverlay(activity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier.weight(1f).testTag("trigger_yloverlay_btn")
                ) {
                    Text("Trigger Yloverlay", fontSize = 11.sp, color = OffWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Ad SDK Logs console terminal
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStrokeModifier(SlateBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "REAL-TIME ADMOB LOGGER CONSOLE",
                        color = BrightCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(SlateBorder))
                    Spacer(modifier = Modifier.height(6.dp))

                    if (logs.isEmpty()) {
                        Text(
                            text = "Initializing buffer streams...",
                            color = Color.Green,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(logs) { logMsg ->
                                Text(
                                    text = logMsg,
                                    color = if (logMsg.contains("failed") || logMsg.contains("Error")) AccentRed else Color.Green,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Developer Info footer
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Application configured using Google AdMob release SDK version 23.6.0",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun StatusItem(
    label: String,
    statusStr: String,
    isReady: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.LightGray, fontSize = 12.sp)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (isReady) NeonGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f))
                .border(
                    1.dp,
                    if (isReady) NeonGreen.copy(alpha = 0.4f) else Color.Red.copy(alpha = 0.4f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = statusStr,
                color = if (isReady) NeonGreen else AccentRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

fun getAdStateText(state: AdMobManager.AdState): String {
    return when (state) {
        AdMobManager.AdState.Unloaded -> "UNLOADED"
        AdMobManager.AdState.Loading -> "LOADING..."
        AdMobManager.AdState.Loaded -> "LOADED"
        is AdMobManager.AdState.Error -> "FAILED"
    }
}
