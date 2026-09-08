# Documento técnico — Adaptive Study

## 1. Descripción de la aplicación
Adaptive Study es una aplicación Android de apoyo al estudio que modifica automáticamente su interfaz en función del contexto real del dispositivo. Su objetivo es demostrar adaptación automática sin que el usuario tenga que cambiar configuraciones manualmente.

## 2. Contexto utilizado
La aplicación detecta dos variables reales:
- **Luminosidad ambiental**, mediante el sensor `TYPE_LIGHT`.
- **Nivel y estado de batería**, mediante `ACTION_BATTERY_CHANGED`.

## 3. Comportamiento adaptativo
Según la iluminación, la interfaz cambia automáticamente entre modo nocturno, normal y alto contraste. Cuando la batería es menor o igual a 20% y el dispositivo no está cargando, se activa un modo ahorro que reduce contenido visual secundario.

## 4. Pipeline adaptativo
**Entrada → Procesamiento → Decisión → Adaptación**

- Entrada: lecturas de batería y sensor de luz.
- Procesamiento: `ContextManager` reúne el contexto y suaviza las últimas lecturas de luz.
- Decisión: `AdaptationEngine` aplica reglas sobre lux y batería.
- Adaptación: `MainActivity` cambia colores, contraste y visibilidad de elementos.

## 5. Arquitectura / componentes principales

```text
BatteryContextProvider ─┐
                        ├─> ContextManager ─> AdaptationEngine ─> MainActivity
LightContextProvider ───┘
```

La separación permite modificar reglas sin tocar la captura de sensores ni reescribir la interfaz completa.

## 6. Tecnologías utilizadas
- Kotlin
- Android SDK
- SensorManager
- BatteryManager / ACTION_BATTERY_CHANGED
- Gradle
- GitHub Actions para compilación del APK

## 7. Ubicación del código relevante

| Elemento | Archivo / clase |
|---|---|
| Captura de batería | `context/BatteryContextProvider.kt` |
| Captura de luminosidad | `context/LightContextProvider.kt` |
| Procesamiento | `processing/ContextManager.kt` |
| Decisión | `decision/AdaptationEngine.kt` |
| Adaptación | `ui/MainActivity.kt` |
