package com.example.pelayananbantuangizi.ui.pemeriksaan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    var beratBadan by remember { mutableStateOf("") }
    var tinggiBadan by remember { mutableStateOf("") }
    var tekananDarah by remember { mutableStateOf("") }
    var gulaDarah by remember { mutableStateOf("") }
    var kolesterol by remember { mutableStateOf("") }
    var asamUrat by remember { mutableStateOf("") }
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Tanggal periksa otomatis (hari ini)", style = MaterialTheme.typography.bodySmall)
                    Text("Hasil periksa otomatis dari BB/TB, tensi, gula, kolesterol & asam urat", style = MaterialTheme.typography.bodySmall)
                }
            }

            OutlinedTextField(
                value = beratBadan,
                onValueChange = { beratBadan = it },
                label = { Text("Berat Badan (kg, opsional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tinggiBadan,
                onValueChange = { tinggiBadan = it },
                label = { Text("Tinggi Badan (cm, opsional)") },
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

            OutlinedTextField(
                value = gulaDarah,
                onValueChange = { gulaDarah = it },
                label = { Text("Gula Darah (mg/dL, opsional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = kolesterol,
                onValueChange = { kolesterol = it },
                label = { Text("Kolesterol (mg/dL, opsional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = asamUrat,
                onValueChange = { asamUrat = it },
                label = { Text("Asam Urat (mg/dL, opsional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

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
                onClick = { vm.submit(lansiaId, beratBadan, tinggiBadan, tekananDarah, gulaDarah, kolesterol, asamUrat, catatan) },
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
