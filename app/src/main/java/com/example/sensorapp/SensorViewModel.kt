package com.example.sensorapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds all sensor state exposed to the UI.
 */
data class SensorState(
    // Accelerometer (Motion)
    val accelerometerX: Float = 0f,
    val accelerometerY: Float = 0f,
    val accelerometerZ: Float = 0f,
    // Derived ball position on canvas
    val ballOffsetX: Float = 0f,
    val ballOffsetY: Float = 0f,
    // Light Sensor (Environment)
    val lux: Float = -1f,
    val isDarkTheme: Boolean = false
)

class SensorViewModel : ViewModel() {

    private val _state = MutableStateFlow(SensorState())
    val state = _state.asStateFlow()

    /**
     * Called when the Accelerometer fires a new event.
     * Maps gravity components to ball offset on screen.
     *
     * Accelerometer axes (phone held upright, portrait):
     *   +X → left tilt    |  +Y → forward/up tilt  |  +Z → towards user
     *
     * Ball should roll opposite to tilt direction (like a marble on a tilted plane).
     */
    fun onAccelerometerChanged(x: Float, y: Float, z: Float) {
        _state.update { current ->
            current.copy(
                accelerometerX = x,
                accelerometerY = y,
                accelerometerZ = z,
                ballOffsetX = (-x * 25f).coerceIn(-280f, 280f),
                ballOffsetY = (y * 25f).coerceIn(-450f, 450f)
            )
        }
    }

    /**
     * Called when the Light Sensor fires a new event.
     * Switches dark/light theme at 30 lux threshold.
     */
    fun onLightChanged(lux: Float) {
        _state.update { current ->
            current.copy(
                lux = lux,
                isDarkTheme = lux < 30f
            )
        }
    }
}
