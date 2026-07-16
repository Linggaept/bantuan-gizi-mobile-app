package com.example.pelayananbantuangizi.ui.pemeriksaan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pelayananbantuangizi.data.api.model.MonitoringEntryDto
import com.example.pelayananbantuangizi.data.repository.PemeriksaanRepository
import com.example.pelayananbantuangizi.util.UiState
import com.example.pelayananbantuangizi.util.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MonitoringViewModel(private val repository: PemeriksaanRepository) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<MonitoringEntryDto>>>(UiState.Loading)
    val state: StateFlow<UiState<List<MonitoringEntryDto>>> = _state.asStateFlow()

    fun load(lansiaId: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = repository.getMonitoring(lansiaId)
            _state.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Gagal memuat monitoring") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoringScreen(
    pemeriksaanRepository: PemeriksaanRepository,
    lansiaId: Int,
    namaLansia: String,
    onNavigateToTambah: (Int) -> Unit,
    onBack: () -> Unit
) {
    val vm: MonitoringViewModel = viewModel(factory = ViewModelFactory(pemeriksaanRepository))
    val state by vm.state.collectAsState()

    LaunchedEffect(lansiaId) { vm.load(lansiaId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitoring Kesehatan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToTambah(lansiaId) }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Pemeriksaan")
            }
        }
    ) { padding ->
        when (state) {
            is UiState.Loading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is UiState.Error -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { vm.load(lansiaId) }) { Text("Coba lagi") }
                }
            }

            is UiState.Success -> {
                val entries = (state as UiState.Success<List<MonitoringEntryDto>>).data
                if (entries.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text("Belum ada data monitoring")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        item {
                            Text(
                                "Riwayat Kesehatan — $namaLansia",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        items(entries, key = { "${it.periodeTahun}-${it.periodeBulan}" }) { entry ->
                            MonitoringCard(entry)
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
private fun MonitoringCard(entry: MonitoringEntryDto) {
    val (trendText, trendColor) = when (entry.trend) {
        "membaik" -> "↑ Membaik" to Color(0xFF2E7D32)
        "menurun" -> "↓ Menurun" to MaterialTheme.colorScheme.error
        "tetap" -> "→ Tetap" to MaterialTheme.colorScheme.onSurfaceVariant
        else -> "" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(entry.label, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Text("Tanggal: ${entry.tanggalPeriksa}", style = MaterialTheme.typography.bodySmall)
            entry.beratBadan?.let { Text("Berat badan: $it kg", style = MaterialTheme.typography.bodySmall) }
            entry.tinggiBadan?.let { Text("Tinggi badan: $it cm", style = MaterialTheme.typography.bodySmall) }
            entry.tekananDarah?.let { Text("Tekanan darah: $it", style = MaterialTheme.typography.bodySmall) }
            entry.gulaDarah?.let { Text("Gula darah: $it mg/dL", style = MaterialTheme.typography.bodySmall) }
            entry.kolesterol?.let { Text("Kolesterol: $it mg/dL", style = MaterialTheme.typography.bodySmall) }
            entry.asamUrat?.let { Text("Asam urat: $it mg/dL", style = MaterialTheme.typography.bodySmall) }
            entry.catatan?.let { Text("Catatan: $it", style = MaterialTheme.typography.bodySmall) }
            if (trendText.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(trendText, color = trendColor, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
