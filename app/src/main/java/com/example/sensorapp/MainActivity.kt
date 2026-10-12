package com.example.sensorapp

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sensorapp.ui.SensorScreen
import com.example.sensorapp.ui.theme.SensorAppTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lightSensor: Sensor? = null

    private val viewModel: SensorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get sensor manager and individual sensors
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor  = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            val state = viewModel.state.collectAsStateWithLifecycle()

            SensorAppTheme(darkTheme = state.value.isDarkTheme) {
                SensorScreen(state = state.value)
            }
        }
    }

    /**
     * Register sensors when the activity is visible.
     * Uses SENSOR_DELAY_GAME for accelerometer (smooth) and
     * SENSOR_DELAY_NORMAL for light (no need for high frequency).
     */
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    /**
     * Unregister ALL sensors when the activity is paused to save battery.
     */
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> viewModel.onAccelerometerChanged(
                x = event.values[0],
                y = event.values[1],
                z = event.values[2]
            )
            Sensor.TYPE_LIGHT -> viewModel.onLightChanged(lux = event.values[0])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this app
    }
}
