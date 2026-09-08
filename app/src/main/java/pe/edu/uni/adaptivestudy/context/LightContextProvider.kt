package pe.edu.uni.adaptivestudy.context

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class LightContextProvider(
    context: Context,
    private val onLuxChanged: (Float?) -> Unit
) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    fun start() {
        if (lightSensor == null) {
            onLuxChanged(null)
            return
        }
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val lux = event?.values?.firstOrNull() ?: return
        onLuxChanged(lux)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
