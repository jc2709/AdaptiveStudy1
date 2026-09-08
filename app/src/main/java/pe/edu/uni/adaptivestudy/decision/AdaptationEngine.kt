package pe.edu.uni.adaptivestudy.decision

import pe.edu.uni.adaptivestudy.config.AdaptationRules
import pe.edu.uni.adaptivestudy.context.ContextSnapshot

class AdaptationEngine {
    fun decide(context: ContextSnapshot): AdaptationState {
        val visualMode = when {
            context.ambientLux == null -> VisualMode.NORMAL
            context.ambientLux < AdaptationRules.LOW_LIGHT_LUX -> VisualMode.NIGHT
            context.ambientLux > AdaptationRules.HIGH_LIGHT_LUX -> VisualMode.HIGH_CONTRAST
            else -> VisualMode.NORMAL
        }

        val ecoMode = context.batteryPercent <= AdaptationRules.LOW_BATTERY_PERCENT && !context.isCharging
        val layoutMode = if (context.isLandscape) LayoutMode.SPLIT else LayoutMode.STACKED

        val reasons = mutableListOf<String>()
        when (visualMode) {
            VisualMode.NIGHT -> reasons += "Poca luz: modo nocturno"
            VisualMode.HIGH_CONTRAST -> reasons += "Mucha luz: alto contraste"
            VisualMode.NORMAL -> reasons += if (context.ambientLux == null)
                "Sensor de luz no disponible: modo normal" else "Iluminación normal"
        }

        if (ecoMode) {
            reasons += "Batería baja: menos contenido secundario y menor frecuencia de actualización"
        } else if (context.isCharging) {
            reasons += "Equipo cargando: modo ahorro desactivado"
        }

        reasons += if (layoutMode == LayoutMode.SPLIT)
            "Horizontal: distribución en dos columnas" else "Vertical: distribución apilada"

        return AdaptationState(
            visualMode = visualMode,
            ecoMode = ecoMode,
            layoutMode = layoutMode,
            reason = reasons.joinToString(" · ")
        )
    }
}
