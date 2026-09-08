package pe.edu.uni.adaptivestudy.context

data class ContextSnapshot(
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false,
    val ambientLux: Float? = null
)
