package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LaserOrange
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.SlateBorder

@Composable
fun FitnessRingChart(
    calorieProgress: Float, // 0.0f to 1.0f+
    waterProgress: Float,   // 0.0f to 1.0f+
    stepsProgress: Float,   // 0.0f to 1.0f+
    modifier: Modifier = Modifier,
    centerValueText: String = ""
) {
    // Animate sweep angles for satisfying fluid load-ins
    val animatedCalories by animateFloatAsState(
        targetValue = calorieProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "Calories"
    )
    val animatedWater by animateFloatAsState(
        targetValue = waterProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "Water"
    )
    val animatedSteps by animateFloatAsState(
        targetValue = stepsProgress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000),
        label = "Steps"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val center = Offset(width / 2f, height / 2f)
            val strokeWidth = 14.dp.toPx()

            // Outer ring: Calories (Neon Green)
            val outerRadius = (width / 2f) - (strokeWidth / 2f)
            drawArc(
                color = Color(0x3300E676),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(outerRadius * 2f, outerRadius * 2f),
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = NeonGreen,
                startAngle = -90f,
                sweepAngle = animatedCalories * 360f,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius, center.y - outerRadius),
                size = Size(outerRadius * 2f, outerRadius * 2f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Middle ring: Steps (Laser Orange)
            val middleRadius = outerRadius - strokeWidth - 10.dp.toPx()
            drawArc(
                color = Color(0x33FF9100),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - middleRadius, center.y - middleRadius),
                size = Size(middleRadius * 2f, middleRadius * 2f),
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = LaserOrange,
                startAngle = -90f,
                sweepAngle = animatedSteps * 360f,
                useCenter = false,
                topLeft = Offset(center.x - middleRadius, center.y - middleRadius),
                size = Size(middleRadius * 2f, middleRadius * 2f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Inner ring: Water (Electric Blue)
            val innerRadius = middleRadius - strokeWidth - 10.dp.toPx()
            drawArc(
                color = Color(0x332979FF),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                size = Size(innerRadius * 2f, innerRadius * 2f),
                style = Stroke(width = strokeWidth)
            )
            drawArc(
                color = ElectricBlue,
                startAngle = -90f,
                sweepAngle = animatedWater * 360f,
                useCenter = false,
                topLeft = Offset(center.x - innerRadius, center.y - innerRadius),
                size = Size(innerRadius * 2f, innerRadius * 2f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = centerValueText,
                color = NeonGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
