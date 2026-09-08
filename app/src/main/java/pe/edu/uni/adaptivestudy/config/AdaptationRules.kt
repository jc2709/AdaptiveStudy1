package pe.edu.uni.adaptivestudy.config

object AdaptationRules {
    const val LOW_LIGHT_LUX = 20f
    const val HIGH_LIGHT_LUX = 800f
    const val LOW_BATTERY_PERCENT = 20

    const val NORMAL_REFRESH_MS = 15_000L
    const val ECO_REFRESH_MS = 45_000L

    const val STREAK_FOR_LEVEL_CHANGE = 2
    const val SESSION_QUESTION_COUNT = 10
}
