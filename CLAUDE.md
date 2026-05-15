# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Android app (Kotlin + Jetpack Compose) for **Pelayanan Bantuan Gizi** — used by kader/operator RW to record elderly (lansia) data, health checks, and aid status.

- Package: `com.example.pelayananbantuangizi`
- minSdk 24, targetSdk/compileSdk 36, Kotlin 2.0.21, AGP 8.12.3

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run single unit test class
./gradlew test --tests "com.example.pelayananbantuangizi.ExampleUnitTest"
```

## Architecture

Project is in early scaffolding stage — only `MainActivity` exists with a placeholder `Greeting` composable. No navigation, no ViewModel, no networking layer yet.

**Planned per API docs (`docs/mobile-operator-api.md`):**
- Backend: Laravel + Sanctum, Base URL `http://127.0.0.1:8000/api/v1`
- Auth: Bearer token stored after login, required on all endpoints except login
- Offline-first note: docs recommend Room/SQLite for local storage, sync to server when online

## API Surface (from `docs/mobile-operator-api.md`)

| Domain | Endpoints |
|---|---|
| Auth | `POST /login`, `POST /logout` |
| Lansia (elderly) | `GET /lansia`, `GET /lansia/{id}`, `POST /lansia`, `PUT /lansia/{id}`, `DELETE /lansia/{id}` |
| Foto KTP | `POST /lansia/{id}/foto-ktp` (multipart, max 2MB jpg/png/jpeg) |
| Status bantuan | `GET /lansia/{id}/status-bantuan` |
| Pemeriksaan kesehatan | `GET /lansia/{id}/pemeriksaan`, `POST /lansia/{id}/pemeriksaan` |
| Monitoring | reuses `/lansia` and `/lansia/{id}/status-bantuan` |

## Key Files

- `app/src/main/java/com/example/pelayananbantuangizi/MainActivity.kt` — single entry point
- `app/src/main/AndroidManifest.xml` — single activity, no deep links
- `gradle/libs.versions.toml` — version catalog for all dependencies
- `docs/mobile-operator-api.md` — full API spec with request/response examples
- `refrence-layout/` — UI reference screenshots (dahboard-pendataan, kelola-lansia)

## Dependencies (current)

Only Compose BOM 2024.09.00 + Material3. No Retrofit, Hilt, Room, or Navigation added yet — these need to be added to `gradle/libs.versions.toml` and `app/build.gradle.kts` before implementing features.
