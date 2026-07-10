# Panduan Test Kondisi Kesehatan (Sehat / Sakit / Sakit Parah)

Hasil akhir = **tingkat terparah dari semua field**. Satu field "parah" → langsung `sakit_parah`.
Field kosong diabaikan (tidak memberatkan).

## Ambang per parameter

| Parameter | Sehat | Sakit | Sakit Parah |
|---|---|---|---|
| **Gula Darah** | 70–99 | 100–125 | **≥126** |
| **Kolesterol** | <200 | 200–239 | **≥240** |
| **Asam Urat** | 3.4–7.0 | *(tidak ada)* | **>7.0** |
| **Tekanan Darah** | 90–139 / 60–89 | sistol 140–159 atau diastol 90–99 | **sistol ≥160 / <90, atau diastol ≥100 / <60** |
| **IMT** (dari BB & TB) | 18.5–24.9 | 25–29.9 atau <18.5 | **≥30 atau <15** |

## Contoh isian siap-tempel (TB = 160 cm)

### → hasil "Sehat" (semua normal)
```
Berat Badan: 56    Tinggi Badan: 160
Tekanan Darah: 120/80
Gula Darah: 90    Kolesterol: 180    Asam Urat: 5
```

### → hasil "Sakit" (ada yang sakit, tidak ada yang parah)
```
Berat Badan: 56    Tinggi Badan: 160
Tekanan Darah: 145/85
Gula Darah: 110    Kolesterol: 220    Asam Urat: 6
```

### → hasil "Sakit Parah" (minimal 1 parah)
```
Berat Badan: 56    Tinggi Badan: 160
Tekanan Darah: 120/80
Gula Darah: 200    Kolesterol: 180    Asam Urat: 5
```
*(gula 200 ≥126 → langsung parah, walau yang lain normal)*

## Test cepat (isi 1 field, sisanya kosong)

- Gula Darah `90` → Sehat
- Gula Darah `110` → Sakit
- Gula Darah `200` → Sakit Parah

Setelah Simpan, cek di **Monitoring** — badge status & warnanya harus sesuai.

## Cek tembus ke DB (dari sisi web)

```bash
php artisan tinker --execute 'dump(App\Models\PemeriksaanKesehatan::latest("pemeriksaan_id")->first()->only("gula_darah","kolesterol","asam_urat","tinggi_badan","hasil_periksa"));'
```

Sumber ambang: `web/app/Services/HealthClassifier.php`
