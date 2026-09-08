package pe.edu.uni.adaptivestudy.processing

import android.content.Context
import pe.edu.uni.adaptivestudy.context.BatteryContextProvider
import pe.edu.uni.adaptivestudy.context.ContextSnapshot
import pe.edu.uni.adaptivestudy.context.LightContextProvider

class ContextManager(
    context: Context,
    private val onContextChanged: (ContextSnapshot) -> Unit
) {
    private val batteryProvider = BatteryContextProvider(context)
    private val lightSamples = ArrayDeque<Float>()
    private var lastLux: Float? = null
    private var current = ContextSnapshot()

    private val lightProvider = LightContextProvider(context) { lux ->
        lastLux = smooth(lux)
        refresh()
    }

    fun start() {
        refresh()
        lightProvider.start()
    }

    fun stop() {
        lightProvider.stop()
    }

    fun refresh() {
        val (battery, charging) = batteryProvider.read()
        current = ContextSnapshot(
            batteryPercent = battery,
            isCharging = charging,
            ambientLux = lastLux
        )
        onContextChanged(current)
    }

    private fun smooth(value: Float?): Float? {
        if (value == null) return null
        lightSamples.addLast(value)
        while (lightSamples.size > 5) lightSamples.removeFirst()
        return lightSamples.average().toFloat()
    }
}
