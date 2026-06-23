# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Android app (Kotlin + Jetpack Compose) for **Pelayanan Bantuan Gizi** — kader/operator RW records elderly (lansia) data, health checks (pemeriksaan), aid status.

- Package: `com.example.pelayananbantuangizi`
- minSdk 24, targetSdk/compileSdk 36, Kotlin 2.0.21, AGP 8.12.3, Compose BOM 2024.09.00

## Build & Run

```bash
./gradlew assembleDebug         # build debug APK
./gradlew installDebug          # install to connected device
./gradlew test                  # unit tests
./gradlew connectedAndroidTest  # instrumented tests (needs device)
./gradlew test --tests "com.example.pelayananbantuangizi.ExampleUnitTest"  # single test
```

## Architecture

Layered, no DI framework. Manual wiring lives in `navigation/AppNavGraph.kt` — it constructs `TokenDataStore`, initializes `ApiClient`, instantiates repositories, then passes them down into each screen composable. ViewModels are created via `util/ViewModelFactory` (reflection-based vararg constructor) inside the screens.

**Layers:**
- `data/api/` — Retrofit `ApiService` + singleton `ApiClient`. Auth header injected by OkHttp interceptor that reads token from `TokenDataStore` via `runBlocking { ... .first() }`. Base URL is **hardcoded** in `ApiClient.BASE_URL` (currently a LAN IP for dev backend) — update when backend host changes.
- `data/repository/` — `AuthRepository`, `LansiaRepository`, `PemeriksaanRepository`. Repositories own all API calls and return domain results; screens never touch `ApiService` directly.
- `data/api/model/ApiModels.kt` — all request/response DTOs in one file.
- `local/TokenDataStore.kt` — Preferences DataStore for Bearer token persistence; observed as `Flow` in `AppNavGraph` to decide start destination (Login vs LansiaList).
- `ui/<feature>/` — one folder per feature (`auth`, `lansia`, `pemeriksaan`, `theme`). Each feature: `XxxScreen.kt` + `XxxViewModel.kt`. UI state uses `util/UiState` sealed class.
- `navigation/Screen.kt` — typed route definitions with `createRoute(id)` helpers; `AppNavGraph.kt` wires composables + `NavType.IntType` args.

**Auth/session flow:** Login writes token via `AuthRepository` → `TokenDataStore`. `AppNavGraph` collects token flow; non-null → `LansiaList`, null → `Login`. Logout calls `authRepo.logout()` then nav `popUpTo(0)`.

**Note:** `MainActivity` is now just the Compose host that mounts `AppNavGraph`; the old `Greeting` placeholder is gone.

## API Surface (see `docs/mobile-operator-api.md`)

Backend: Laravel + Sanctum. All endpoints except `POST /login` require `Authorization: Bearer <token>`.

| Domain | Endpoints |
|---|---|
| Auth | `POST /login`, `POST /logout` |
| Lansia | `GET /lansia`, `GET /lansia/{id}`, `POST /lansia`, `PUT /lansia/{id}`, `DELETE /lansia/{id}` |
| Foto KTP | `POST /lansia/{id}/foto-ktp` (multipart, max 2MB jpg/png/jpeg) |
| Status bantuan | `GET /lansia/{id}/status-bantuan` |
| Pemeriksaan | `GET /lansia/{id}/pemeriksaan`, `POST /lansia/{id}/pemeriksaan` |

## Key Files

- `navigation/AppNavGraph.kt` — composition root; add new screens + repo wiring here
- `data/api/ApiClient.kt` — Retrofit/OkHttp setup, base URL, auth interceptor
- `data/api/ApiService.kt` + `model/ApiModels.kt` — endpoints + DTOs
- `util/ViewModelFactory.kt` — reflection vararg factory; pass repo deps positionally
- `util/UiState.kt` — Loading/Success/Error sealed class used by ViewModels
- `gradle/libs.versions.toml` — version catalog (Retrofit, OkHttp, DataStore, Nav-Compose, Lifecycle-VM, Coroutines, Coil)
- `docs/mobile-operator-api.md` — full API spec with examples
- `docs/task.md` — task tracking
- `refrence-layout/` — UI reference screenshots (login, dashboard, kelola-lansia, input-lansia, laporan)

## Dependencies

Retrofit 2.11 + Gson, OkHttp 4.12 (logging interceptor enabled at `BODY` level), DataStore Preferences 1.1.1, Navigation Compose 2.8.4, Lifecycle ViewModel Compose 2.8.7, Coroutines 1.9.0, Coil 2.7.0, Compose Material3. No Hilt, no Room.
