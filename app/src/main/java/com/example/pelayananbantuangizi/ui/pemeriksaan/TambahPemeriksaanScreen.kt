package com.example.pelayananbantuangizi.ui.pemeriksaan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pelayananbantuangizi.data.repository.PemeriksaanRepository
import com.example.pelayananbantuangizi.util.UiState
import com.example.pelayananbantuangizi.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahPemeriksaanScreen(
    pemeriksaanRepository: PemeriksaanRepository,
    lansiaId: Int,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val vm: TambahPemeriksaanViewModel = viewModel(factory = ViewModelFactory(pemeriksaanRepository))
    val state by vm.state.collectAsState()

    var tanggalPeriksa by remember { mutableStateOf("") }
    var beratBadan by remember { mutableStateOf("") }
    var tekananDarah by remember { mutableStateOf("") }
    var hasilPeriksa by remember { mutableStateOf("") }
    var catatan by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is UiState.Success) {
            onSuccess()
            vm.reset()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Pemeriksaan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = tanggalPeriksa,
                onValueChange = { tanggalPeriksa = it },
                label = { Text("Tanggal Periksa (YYYY-MM-DD)") },
                placeholder = { Text("2026-05-15") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = beratBadan,
                onValueChange = { beratBadan = it },
                label = { Text("Berat Badan (kg, opsional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tekananDarah,
                onValueChange = { tekananDarah = it },
                label = { Text("Tekanan Darah (opsional, contoh: 120/80)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Hasil Periksa", style = MaterialTheme.typography.labelLarge)
            listOf("baik" to "Baik", "sedang" to "Sedang", "buruk" to "Buruk").forEach { (value, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { hasilPeriksa = value }
                ) {
                    RadioButton(selected = hasilPeriksa == value, onClick = { hasilPeriksa = value })
                    Text(label)
                }
            }

            OutlinedTextField(
                value = catatan,
                onValueChange = { catatan = it },
                label = { Text("Catatan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            if (state is UiState.Error) {
                Text(
                    text = (state as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { vm.submit(lansiaId, tanggalPeriksa, beratBadan, tekananDarah, hasilPeriksa, catatan) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = state !is UiState.Loading
            ) {
                if (state is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan")
                }
            }
        }
    }
}
