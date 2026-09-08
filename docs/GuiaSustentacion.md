# Guía rápida de sustentación

## Demostración sugerida

1. Abrir Adaptive Study en un celular Android.
2. Mostrar batería, estado de carga y luminosidad en tiempo real.
3. Cubrir el sensor de luz hasta bajar de 20 lux: la interfaz cambia automáticamente a modo nocturno.
4. Iluminar el sensor hasta superar 800 lux: la interfaz cambia automáticamente a alto contraste.
5. Explicar el pipeline: CONTEXTO → PROCESAMIENTO → DECISIÓN → ADAPTACIÓN.

## Archivos clave para explicar

- `context/LightContextProvider.kt`: captura la luminosidad real.
- `context/BatteryContextProvider.kt`: captura batería y estado de carga.
- `processing/ContextManager.kt`: reúne y suaviza el contexto.
- `decision/AdaptationEngine.kt`: contiene los umbrales y reglas.
- `ui/MainActivity.kt`: aplica visualmente la adaptación.

## Cambios rápidos para un reto técnico

- Cambiar `20f` por `50f` para modificar el umbral del modo nocturno.
- Cambiar `800f` para modificar el umbral de alto contraste.
- Cambiar `20` por `30` para activar el modo ahorro con 30% de batería.
