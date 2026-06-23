package com.example.pelayananbantuangizi.ui.lansia

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pelayananbantuangizi.data.api.model.LansiaDto
import com.example.pelayananbantuangizi.data.api.model.StatusBantuanDto
import com.example.pelayananbantuangizi.data.repository.LansiaRepository
import com.example.pelayananbantuangizi.util.UiState
import com.example.pelayananbantuangizi.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LansiaDetailScreen(
    lansiaRepository: LansiaRepository,
    lansiaId: Int,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToPemeriksaan: (Int) -> Unit,
    onNavigateToMonitoring: (Int, String) -> Unit,
    onDeleted: () -> Unit,
    onBack: () -> Unit
) {
    val vm: LansiaDetailViewModel = viewModel(factory = ViewModelFactory(lansiaRepository))
    val lansiaState by vm.lansiaState.collectAsState()
    val statusBantuanState by vm.statusBantuanState.collectAsState()
    val uploadState by vm.uploadState.collectAsState()
    val deleteState by vm.deleteState.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { vm.uploadFoto(lansiaId, it, context.contentResolver) }
    }

    LaunchedEffect(lansiaId) { vm.load(lansiaId) }

    LaunchedEffect(uploadState) {
        when (uploadState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Foto KTP berhasil diupload")
                vm.resetUploadState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar((uploadState as UiState.Error).message)
                vm.resetUploadState()
            }
            else -> {}
        }
    }

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is UiState.Success -> {
                onDeleted()
                vm.resetDeleteState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar((deleteState as UiState.Error).message)
                vm.resetDeleteState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Lansia") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (lansiaState is UiState.Success) {
                        IconButton(onClick = { onNavigateToEdit(lansiaId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (lansiaState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((lansiaState as UiState.Error).message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { vm.load(lansiaId) }) { Text("Coba lagi") }
                    }
                }
            }
            is UiState.Success -> {
                val lansia = (lansiaState as UiState.Success<LansiaDto>).data
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailSection(title = "Data Pribadi") {
                        DetailRow("NIK", lansia.nik)
                        DetailRow("Nama", lansia.nama)
                        DetailRow("Tanggal Lahir", lansia.tanggalLahir)
                        DetailRow("Usia", "${lansia.usia} tahun")
                        DetailRow("Jenis Kelamin", if (lansia.jenisKelamin == "L") "Laki-laki" else "Perempuan")
                        DetailRow("Alamat", lansia.alamat)
                        DetailRow("RT/RW", "${lansia.rt ?: "-"}/${lansia.rw}")
                    }

                    // Foto KTP
                    DetailSection(title = "Foto KTP") {
                        if (lansia.fotoKtp != null) {
                            AsyncImage(
                                model = lansia.fotoKtp,
                                contentDescription = "Foto KTP",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(8.dp))
                        } else {
                            Text("Belum ada foto KTP", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                        }
                        OutlinedButton(
                            onClick = { imagePicker.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uploadState !is UiState.Loading
                        ) {
                            if (uploadState is UiState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Text(if (lansia.fotoKtp != null) "Ganti Foto KTP" else "Upload Foto KTP")
                            }
                        }
                    }

                    // Status Bantuan
                    DetailSection(title = "Status Bantuan") {
                        when (statusBantuanState) {
                            is UiState.Success -> {
                                val status = (statusBantuanState as UiState.Success<StatusBantuanDto>).data
                                if (status.statusPenerima != null) {
                                    val statusText = if (status.statusPenerima == "penerima") "Penerima Bantuan" else "Bukan Penerima"
                                    val statusColor = if (status.statusPenerima == "penerima")
                                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    Text(statusText, color = statusColor, style = MaterialTheme.typography.titleMedium)
                                    status.periodeBulan?.let { DetailRow("Periode", periodeLabel(it, status.periodeTahun)) }
                                } else {
                                    Text("Belum ada data bantuan", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            else -> Text("Data bantuan tidak tersedia", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Pemeriksaan button
                    OutlinedButton(
                        onClick = { onNavigateToPemeriksaan(lansiaId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Lihat Riwayat Pemeriksaan")
                    }
                    OutlinedButton(
                        onClick = {
                            val nama = (lansiaState as? UiState.Success)?.data?.nama ?: ""
                            onNavigateToMonitoring(lansiaId, nama)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Monitoring Kesehatan")
                    }
                }
            }
            else -> {}
        }
    }

    if (showDeleteDialog) {
        val nama = (lansiaState as? UiState.Success<LansiaDto>)?.data?.nama ?: ""
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Data") },
            text = { Text("Hapus data $nama? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteLansia(lansiaId)
                    showDeleteDialog = false
                }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

private fun periodeLabel(bulan: Int, tahun: Int?): String {
    val (q, range) = when (bulan) {
        1 -> "Q1" to "Jan–Mar"
        4 -> "Q2" to "Apr–Jun"
        7 -> "Q3" to "Jul–Sep"
        10 -> "Q4" to "Okt–Des"
        else -> "Q?" to "-"
    }
    return "$q ${tahun ?: "?"} ($range)"
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label, modifier = Modifier.width(120.dp), style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}
