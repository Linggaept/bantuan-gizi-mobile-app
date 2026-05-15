# Design Spec: Pelayanan Bantuan Gizi Mobile App

**Date:** 2026-05-15  
**Stack:** Kotlin + Jetpack Compose + Retrofit + MVVM  
**Backend:** Laravel/Sanctum, Base URL `http://127.0.0.1:8000/api/v1`

---

## Architecture

MVVM + Repository pattern. No Hilt — manual ViewModelFactory. Online-only (no offline/Room).

### Package Structure

```
com.example.pelayananbantuangizi/
├── data/
│   ├── api/
│   │   ├── ApiService.kt           # all Retrofit endpoints
│   │   ├── ApiClient.kt            # OkHttp + AuthInterceptor + Retrofit instance
│   │   └── model/                  # request/response data classes
│   └── repository/
│       ├── AuthRepository.kt
│       ├── LansiaRepository.kt
│       └── PemeriksaanRepository.kt
├── local/
│   └── TokenDataStore.kt           # DataStore Preferences for Bearer token
├── ui/
│   ├── auth/
│   │   ├── LoginScreen.kt
│   │   └── LoginViewModel.kt
│   ├── lansia/
│   │   ├── LansiaListScreen.kt
│   │   ├── LansiaListViewModel.kt
│   │   ├── LansiaDetailScreen.kt
│   │   ├── LansiaDetailViewModel.kt
│   │   ├── LansiaFormScreen.kt     # shared tambah + edit
│   │   └── LansiaFormViewModel.kt
│   ├── pemeriksaan/
│   │   ├── PemeriksaanListScreen.kt
│   │   ├── PemeriksaanListViewModel.kt
│   │   ├── TambahPemeriksaanScreen.kt
│   │   └── TambahPemeriksaanViewModel.kt
│   └── theme/                      # existing
├── navigation/
│   └── AppNavGraph.kt
└── util/
    └── ViewModelFactory.kt
```

---

## Screens & Navigation

```
Login
  └─► Dashboard (LansiaList)
        ├─► LansiaDetail
        │     ├─► LansiaForm (edit mode)
        │     ├─► UploadKTP (integrated in Detail)
        │     ├─► PemeriksaanList
        │     │     └─► TambahPemeriksaan
        │     └─► StatusBantuan (section in Detail)
        └─► LansiaForm (tambah mode)
```

### Screen Breakdown

| Screen | Features |
|---|---|
| Login | email + password form, error from 422, save token on 200 |
| LansiaList | paginated list, search by nama, filter RW/kondisi/status_bantuan, FAB tambah |
| LansiaDetail | semua field lansia, foto KTP preview + upload button, status bantuan section, link ke pemeriksaan |
| LansiaForm | shared form tambah/edit, validasi client-side match API rules |
| PemeriksaanList | list riwayat per lansia |
| TambahPemeriksaan | form tanggal_periksa, berat_badan, tekanan_darah, hasil_periksa, catatan |

---

## Data Layer

### ApiService (Retrofit)

```
POST   /auth/login
POST   /auth/logout

GET    /lansia                          # ?nama, rw, kondisi_kesehatan, status_bantuan, page
GET    /lansia/{id}
POST   /lansia
PUT    /lansia/{id}
DELETE /lansia/{id}
POST   /lansia/{id}/foto-ktp           # multipart
GET    /lansia/{id}/status-bantuan
GET    /lansia/{id}/pemeriksaan
POST   /lansia/{id}/pemeriksaan
```

### AuthInterceptor
Reads token from DataStore synchronously via `runBlocking`, injects `Authorization: Bearer {token}` on every request except `/auth/login`.

### UI State Pattern
Each ViewModel exposes `StateFlow<UiState>`:
```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

---

## Error Handling

| HTTP Code | Handling |
|---|---|
| 401 | Clear token, navigate to Login |
| 403 | Show snackbar "Tidak punya akses" |
| 404 | Show snackbar "Data tidak ditemukan" |
| 422 | Parse `errors` map, show per-field error in form |
| Network error | Show snackbar "Tidak ada koneksi internet" |

---

## Dependencies to Add

```toml
# gradle/libs.versions.toml additions
retrofit = "2.11.0"
okhttp = "4.12.0"
datastore = "1.1.1"
navigationCompose = "2.8.4"
lifecycleViewModel = "2.8.7"
coroutines = "1.9.0"
coil = "2.7.0"          # image loading for foto KTP
```

---

## Reference Layouts

- `refrence-layout/login.png` → LoginScreen
- `refrence-layout/dahboard-pendataan.png` → LansiaListScreen
- `refrence-layout/kelola-lansia.png` → LansiaDetailScreen + LansiaFormScreen
- `refrence-layout/input-lansia.png` → LansiaFormScreen
- `refrence-layout/laporan-pendataan.png` → StatusBantuan section
