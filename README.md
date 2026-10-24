# AgroNutri

Aplicación Android para registrar datos agronómicos en terreno y calcular un programa de fertirriego sin conexión a internet. Los datos se guardan en el teléfono y se sincronizan con un servidor cuando hay red.

## Qué hace

- Registra predios con su cuartel, cultivo y etapa fenológica.
- Guarda análisis de suelo, agua y foliar (pH, conductividad eléctrica, N, P, K, Ca y Mg).
- Toma y guarda fotos foliares con una nota, usando la cámara del dispositivo.
- Calcula un programa de fertirriego a partir del cultivo, la etapa y el análisis de suelo.
- Guarda un historial de programas por predio y permite compartirlos como texto.
- Sincroniza los datos con el servidor cuando hay conexión y avisa con una notificación.

## Perfiles

- **Agricultor:** registra predios, análisis y fotos, y genera programas. Cada programa queda pendiente de validación.
- **Asesor:** verifica los análisis y valida los programas, con una observación opcional.

El perfil se elige al abrir la app por primera vez y se puede cambiar en Ajustes.

## Cálculo del programa

- Cultivos disponibles: palto, cítricos, tomate, uva de mesa y cerezo.
- Etapas disponibles: floración, engorde y maduración.
- Cada cultivo tiene una dosis base de N, P, K, Ca y Mg (kg/ha), que se multiplica por un factor según la etapa.
- Según el análisis de suelo, el nivel de cada nutriente se clasifica como bajo, adecuado o alto. Si es bajo la dosis sube 20 %, si es alto baja 20 %. Si no hay dato, se usa la dosis base.
- La dosis del nutriente se convierte a kg de fertilizante según su riqueza: urea (N), fosfato monoamónico (P), nitrato de potasio (K), nitrato de calcio (Ca) y sulfato de magnesio (Mg).
- El total se reparte en una aplicación semanal durante 4, 8 o 4 semanas, según la etapa.
- Genera alertas si el pH del suelo es menor a 5,5 o mayor a 7,5, o si la conductividad eléctrica del suelo o del agua supera 2,0 dS/m.

## Tecnologías

| Uso | Librería |
|---|---|
| Interfaz | Jetpack Compose, Material 3, Navigation Compose |
| Base de datos local | Room (SQLite) |
| API REST | Retrofit, OkHttp, Gson |
| Sincronización en segundo plano | WorkManager |
| Cámara | CameraX |
| Imágenes | Coil |

Lenguaje: Kotlin. Arquitectura: MVVM. Versión mínima de Android: 13 (API 33).

## Estructura del proyecto

```
app/src/main/java/cl/duoc/agronutri/
  data/          Entidades Room, DAO, repositorio y preferencias
  data/remote/   ApiService, ApiClient y SyncRequest (Retrofit)
  sync/          SyncWorker, SyncScheduler, Conectividad y Notificaciones
  domain/        Catalogo y CalculadoraFertirriego
  ui/            Una pantalla por archivo y un ViewModel por pantalla
```

Las pruebas de la calculadora están en `app/src/test`.

## Sincronización con el servidor

La app espera un servidor (por ejemplo, Spring Boot) con estas rutas:

| Método | Ruta | Cuerpo |
|---|---|---|
| POST | `/api/sync` | JSON: `{ "planes": [...], "analisis": [...], "programas": [...] }` |
| POST | `/api/fotos` | multipart: `archivo` (jpg), `id`, `planId`, `nota` |

Ambas deben responder 200 si todo salió bien. El servidor debe hacer upsert por `id`, ya que un registro puede reenviarse.


## Cómo ejecutarlo

1. Abrir la carpeta del proyecto en Android Studio.
2. Esperar a que Gradle sincronice las dependencias.
3. Ejecutar en un emulador o dispositivo con Android 13 o superior.

