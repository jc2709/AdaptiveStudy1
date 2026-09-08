package pe.edu.uni.adaptivestudy.processing

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import pe.edu.uni.adaptivestudy.config.AdaptationRules
import pe.edu.uni.adaptivestudy.context.BatteryContextProvider
import pe.edu.uni.adaptivestudy.context.ContextSnapshot
import pe.edu.uni.adaptivestudy.context.LightContextProvider

class ContextManager(
    context: Context,
    private val onContextChanged: (ContextSnapshot) -> Unit
) {
    private val appContext = context.applicationContext
    private val batteryProvider = BatteryContextProvider(appContext)
    private val lightSamples = ArrayDeque<Float>()
    private val handler = Handler(Looper.getMainLooper())

    private var lastLux: Float? = null
    private var current = ContextSnapshot()
    private var running = false

    private val lightProvider = LightContextProvider(appContext) { lux ->
        lastLux = smooth(lux)
        refresh()
    }

    private val refreshRunnable = object : Runnable {
        override fun run() {
            if (!running) return
            refresh()
            scheduleNextRefresh()
        }
    }

    fun start() {
        if (running) return
        running = true
        refresh()
        lightProvider.start()
        scheduleNextRefresh()
    }

    fun stop() {
        running = false
        handler.removeCallbacks(refreshRunnable)
        lightProvider.stop()
    }

    fun refresh() {
        val (battery, charging) = batteryProvider.read()
        val landscape = appContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        current = ContextSnapshot(
            batteryPercent = battery,
            isCharging = charging,
            ambientLux = lastLux,
            isLandscape = landscape
        )
        onContextChanged(current)
    }

    private fun scheduleNextRefresh() {
        handler.removeCallbacks(refreshRunnable)
        val eco = current.batteryPercent <= AdaptationRules.LOW_BATTERY_PERCENT && !current.isCharging
        val delay = if (eco) AdaptationRules.ECO_REFRESH_MS else AdaptationRules.NORMAL_REFRESH_MS
        handler.postDelayed(refreshRunnable, delay)
    }

    private fun smooth(value: Float?): Float? {
        if (value == null) return null
        lightSamples.addLast(value)
        while (lightSamples.size > 5) lightSamples.removeFirst()
        return lightSamples.average().toFloat()
    }
}
