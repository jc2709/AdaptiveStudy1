# Documento técnico — Adaptive Study 2.0

## 1. Descripción de la aplicación
Adaptive Study 2.0 es una aplicación Android de apoyo al estudio basada en un quiz de 10 preguntas. La aplicación modifica automáticamente su interfaz, frecuencia de actualización y dificultad de las preguntas según cambios detectados en el contexto del dispositivo y en la interacción del usuario.

## 2. Variables de contexto
La aplicación utiliza cuatro señales de contexto:
- **Luminosidad ambiental**, obtenida mediante `Sensor.TYPE_LIGHT`.
- **Nivel y estado de batería**, mediante `ACTION_BATTERY_CHANGED`.
- **Orientación del dispositivo**, obtenida desde `Configuration`.
- **Rendimiento del usuario**, medido mediante rachas de respuestas correctas o incorrectas.

## 3. Comportamiento adaptativo
- Luz < 20 lux: modo nocturno.
- Luz > 800 lux: alto contraste.
- Batería <= 20% y sin cargar: modo ahorro, ocultando información secundaria y reduciendo la frecuencia de actualización del contexto de 15 s a 45 s.
- Orientación horizontal: pregunta y alternativas en dos columnas; vertical: disposición apilada.
- Dos aciertos consecutivos: aumenta la dificultad del quiz; dos errores consecutivos: disminuye cuando sea posible.

Todas las reglas principales están centralizadas en `config/AdaptationRules.kt`.

## 4. Pipeline adaptativo
**CONTEXTO → PROCESAMIENTO → DECISIÓN → ADAPTACIÓN**

1. **Contexto:** `BatteryContextProvider` y `LightContextProvider` capturan datos reales del dispositivo. La orientación se obtiene desde la configuración de Android y el rendimiento desde las respuestas del quiz.
2. **Procesamiento:** `ContextManager` reúne la información, promedia las últimas cinco lecturas de luz y actualiza el contexto de forma periódica.
3. **Decisión:** `AdaptationEngine` determina modo visual, ahorro y distribución de interfaz. `QuizEngine` decide la dificultad según las rachas del usuario.
4. **Adaptación:** `MainActivity` aplica colores, visibilidad, distribución y contenido del quiz. Los cambios de dificultad modifican automáticamente el nivel de las siguientes preguntas.

## 5. Arquitectura

```text
BatteryContextProvider ─┐
LightContextProvider ───┼─> ContextManager ─> AdaptationEngine ─┐
Orientación ────────────┘                                      ├─> MainActivity
Respuestas del usuario ───────────────> QuizEngine ─────────────┘
                                  ↑
                         AdaptationRules
```

La separación de responsabilidades evita código monolítico y permite modificar reglas sin cambiar los componentes de captura.

## 6. Tecnologías
- Kotlin
- Android SDK 35
- SensorManager
- BatteryManager / Broadcast de batería
- SharedPreferences para progreso local
- Gradle y JDK 17
- GitHub Actions para compilación automática del APK

## 7. Código relevante

| Función | Archivo |
|---|---|
| Reglas configurables | `config/AdaptationRules.kt` |
| Batería | `context/BatteryContextProvider.kt` |
| Luminosidad | `context/LightContextProvider.kt` |
| Contexto consolidado | `processing/ContextManager.kt` |
| Decisión ambiental | `decision/AdaptationEngine.kt` |
| Quiz adaptativo | `quiz/QuizEngine.kt` |
| Interfaz y adaptación observable | `ui/MainActivity.kt` |
