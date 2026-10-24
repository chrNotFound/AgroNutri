# AgroNutri · DSY1105

Prototipo Android (Kotlin, Jetpack Compose, Material 3, MVVM) para registrar datos agronómicos
en terreno y estimar programas de fertirriego **sin internet**.

## Librerías usadas
| Para qué | Librería |
|---|---|
| Interfaz | Jetpack Compose + Material 3 + Navigation Compose |
| Base de datos offline | Room (SQLite) |
| API REST | Retrofit + OkHttp + Gson |
| Sincronización automática | WorkManager (corre cuando hay red) |
| Cámara | CameraX |
| Fotos en pantalla | Coil |

## Estructura
- `data/` Entidades Room, DAO, repositorio y preferencias.
- `data/remote/` Retrofit: `ApiService`, `ApiClient`, `SyncRequest`.
- `sync/` `SyncWorker`, `SyncScheduler`, `Conectividad`, `Notificaciones`.
- `domain/` Catálogo y `CalculadoraFertirriego` (con pruebas en `src/test`).
- `ui/` Una pantalla por archivo + un ViewModel por pantalla.

## Perfiles
- **Agricultor**: registra predios, análisis y fotos; guarda el programa como *pendiente de validación*.
- **Asesor**: verifica análisis y valida los programas.

## Contrato de la API (Spring Boot)
La app envía JSON con los campos de las entidades (`data/Entities.kt`).

| Método | Ruta | Cuerpo |
|---|---|---|
| POST | `/api/sync` | `{ "planes": [...], "analisis": [...], "programas": [...] }` → 200 si OK |
| POST | `/api/fotos` | multipart: `archivo` (jpg), `id`, `planId`, `nota` → 200 si OK |

El servidor debe hacer *upsert* por `id` (un registro puede reenviarse). URL base en `ApiClient.URL_BASE`
(`10.0.2.2:8080` = PC visto desde el emulador).

## APK release firmado
1. Crear la llave (una sola vez):
   `keytool -genkeypair -v -keystore agronutri.jks -alias agronutri -keyalg RSA -keysize 2048 -validity 10000`
2. Crear `keystore.properties` en la raíz (no se sube a Git):
   ```
   storeFile=agronutri.jks
   storePassword=TU_CLAVE
   keyAlias=agronutri
   keyPassword=TU_CLAVE
   ```
3. Android Studio: *Build > Generate Signed App Bundle / APK*, o `./gradlew assembleRelease`.

## Limitaciones conocidas
- Los borrados locales no se envían al servidor.
- Umbrales, dosis y fertilizantes son ficticios hasta tener los datos reales del cliente.
- El análisis foliar se guarda pero aún no entra al cálculo.
- Base de datos con `fallbackToDestructiveMigration` (prototipo): escribir migraciones antes de usar datos reales.
