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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pelayananbantuangizi.data.api.model.PemeriksaanDto
import com.example.pelayananbantuangizi.data.repository.PemeriksaanRepository
import com.example.pelayananbantuangizi.util.UiState
import com.example.pelayananbantuangizi.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PemeriksaanListScreen(
    pemeriksaanRepository: PemeriksaanRepository,
    lansiaId: Int,
    onNavigateToTambah: (Int) -> Unit,
    onBack: () -> Unit
) {
    val vm: PemeriksaanListViewModel = viewModel(factory = ViewModelFactory(pemeriksaanRepository))
    val state by vm.state.collectAsState()

    LaunchedEffect(lansiaId) { vm.load(lansiaId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Pemeriksaan") },
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
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((state as UiState.Error).message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { vm.load(lansiaId) }) { Text("Coba lagi") }
                    }
                }
            }
            is UiState.Success -> {
                val list = (state as UiState.Success<List<PemeriksaanDto>>).data
                if (list.isEmpty()) {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text("Belum ada riwayat pemeriksaan")
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        items(list, key = { it.pemeriksaanId }) { item ->
                            PemeriksaanItem(item)
                            HorizontalDivider()
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun PemeriksaanItem(item: PemeriksaanDto) {
    val hasilColor = when (item.hasilPeriksa) {
        "baik" -> MaterialTheme.colorScheme.primary
        "sedang" -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }
    ListItem(
        headlineContent = { Text(item.tanggalPeriksa) },
        supportingContent = {
            Column {
                item.beratBadan?.let { Text("Berat badan: ${it} kg") }
                item.tekananDarah?.let { Text("Tekanan darah: $it") }
                item.catatan?.let { Text("Catatan: $it") }
            }
        },
        trailingContent = {
            Text(
                item.hasilPeriksa.replaceFirstChar { it.uppercase() },
                color = hasilColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    )
}
