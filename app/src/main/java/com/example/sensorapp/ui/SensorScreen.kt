package com.example.sensorapp.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sensorapp.SensorState

@Composable
fun SensorScreen(state: SensorState) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Title ──────────────────────────────────────────────
            Text(
                text = "🎯 Sensor App",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (state.isDarkTheme) "🌙 Dark Mode aktif (cahaya redup)"
                else "☀️ Light Mode aktif (cahaya terang)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Ball Canvas (Accelerometer visual) ────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

                // Smooth animation for ball movement
                val animatedX by animateFloatAsState(
                    targetValue = state.ballOffsetX,
                    animationSpec = tween(durationMillis = 80),
                    label = "ballX"
                )
                val animatedY by animateFloatAsState(
                    targetValue = state.ballOffsetY,
                    animationSpec = tween(durationMillis = 80),
                    label = "ballY"
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Background grid lines (decorative)
                    val gridColor = surfaceVariant.copy(alpha = 0.4f)
                    for (i in 1..4) {
                        drawLine(
                            color = gridColor,
                            start = Offset(size.width / 5 * i, 0f),
                            end = Offset(size.width / 5 * i, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, size.height / 5 * i),
                            end = Offset(size.width, size.height / 5 * i),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Center crosshair
                    drawLine(
                        color = primaryColor.copy(alpha = 0.2f),
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 1.5.dp.toPx()
                    )
                    drawLine(
                        color = primaryColor.copy(alpha = 0.2f),
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = 1.5.dp.toPx()
                    )

                    // Ball shadow
                    val ballRadius = 40.dp.toPx()
                    val cx = (size.width / 2 + animatedX).coerceIn(ballRadius, size.width - ballRadius)
                    val cy = (size.height / 2 + animatedY).coerceIn(ballRadius, size.height - ballRadius)

                    drawCircle(
                        color = Color.Black.copy(alpha = 0.15f),
                        radius = ballRadius + 4.dp.toPx(),
                        center = Offset(cx + 4f, cy + 4f)
                    )

                    // Ball with gradient
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.9f),
                                primaryColor
                            ),
                            center = Offset(cx - ballRadius * 0.3f, cy - ballRadius * 0.3f),
                            radius = ballRadius
                        ),
                        radius = ballRadius,
                        center = Offset(cx, cy)
                    )

                    // Ball highlight (shine)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = ballRadius * 0.35f,
                        center = Offset(cx - ballRadius * 0.25f, cy - ballRadius * 0.28f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Sensor Info Cards ─────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Accelerometer Card
                SensorInfoCard(
                    modifier = Modifier.weight(1f),
                    title = "📱 Accelerometer",
                    subtitle = "Motion Sensor",
                    values = listOf(
                        "X" to state.accelerometerX,
                        "Y" to state.accelerometerY,
                        "Z" to state.accelerometerZ
                    ),
                    unit = "m/s²"
                )

                // Light Sensor Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "💡 Light Sensor",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Environment Sensor",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (state.lux >= 0f) "${"%.1f".format(state.lux)} lux"
                            else "Mendeteksi...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = when {
                                state.lux < 0 -> "—"
                                state.lux < 10 -> "🌑 Sangat gelap"
                                state.lux < 30 -> "🌒 Redup"
                                state.lux < 200 -> "🌤 Normal"
                                else -> "☀️ Terang"
                            },
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Miringkan HP untuk menggerakkan bola",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SensorInfoCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    values: List<Pair<String, Float>>,
    unit: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            values.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    Text(
                        "${"%.2f".format(value)} $unit",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
