package com.example.pelayananbantuangizi.ui.lansia

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pelayananbantuangizi.data.api.model.LansiaDto
import com.example.pelayananbantuangizi.data.repository.AuthRepository
import com.example.pelayananbantuangizi.data.repository.LansiaRepository
import com.example.pelayananbantuangizi.util.UiState
import com.example.pelayananbantuangizi.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LansiaListScreen(
    lansiaRepository: LansiaRepository,
    authRepository: AuthRepository,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToForm: (Int?) -> Unit,
    onLogout: () -> Unit
) {
    val vm: LansiaListViewModel = viewModel(factory = ViewModelFactory(lansiaRepository))
    val state by vm.state.collectAsState()
    val deleteState by vm.deleteState.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<LansiaDto?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(searchText) {
        kotlinx.coroutines.delay(300)
        vm.search(searchText)
    }

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is UiState.Success -> {
                snackbarHostState.showSnackbar("Data lansia berhasil dihapus")
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
                title = { Text("Data Lansia") },
                actions = {
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Filter")
                    }
                    IconButton(onClick = { onLogout() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Cari nama lansia...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            // Tambah button — fixed below search
            Button(
                onClick = { onNavigateToForm(null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tambah Lansia")
            }

            // Content
            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.error!!, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { vm.loadLansia() }) { Text("Coba lagi") }
                        }
                    }
                }
                state.items.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada data lansia")
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(state.items, key = { it.lansiaId }) { lansia ->
                            LansiaListItem(
                                lansia = lansia,
                                onClick = { onNavigateToDetail(lansia.lansiaId) },
                                onDelete = { showDeleteDialog = lansia }
                            )
                            HorizontalDivider()
                        }
                    }
                    // Pagination
                    state.meta?.let { meta ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { vm.prevPage() },
                                enabled = meta.currentPage > 1
                            ) { Text("< Sebelumnya") }
                            Text("${meta.currentPage} / ${meta.lastPage}")
                            TextButton(
                                onClick = { vm.nextPage() },
                                enabled = meta.currentPage < meta.lastPage
                            ) { Text("Berikutnya >") }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { lansia ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Hapus Data") },
            text = { Text("Hapus data ${lansia.nama}?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteLansia(lansia.lansiaId)
                    showDeleteDialog = null
                }) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Batal") }
            }
        )
    }

    // Filter dialog
    if (showFilterDialog) {
        FilterDialog(
            currentRw = vm.filterRw,
            currentKondisi = vm.filterKondisi,
            currentStatus = vm.filterStatus,
            onApply = { rw, kondisi, status ->
                vm.applyFilter(rw, kondisi, status)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
private fun LansiaListItem(
    lansia: LansiaDto,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(lansia.nama) },
        supportingContent = {
            Text("NIK: ${lansia.nik} | RW ${lansia.rw} | Usia ${lansia.usia} th")
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun FilterDialog(
    currentRw: String,
    currentKondisi: String,
    currentStatus: String,
    onApply: (rw: String, kondisi: String, status: String) -> Unit,
    onDismiss: () -> Unit
) {
    var rw by remember { mutableStateOf(currentRw) }
    var kondisi by remember { mutableStateOf(currentKondisi) }
    var status by remember { mutableStateOf(currentStatus) }

    val kondisiOptions = listOf("", "sehat", "sakit")
    val statusOptions = listOf("", "penerima", "tidak_penerima")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Data") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rw,
                    onValueChange = { rw = it },
                    label = { Text("RW (contoh: 001)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Kondisi Kesehatan:", style = MaterialTheme.typography.labelMedium)
                kondisiOptions.forEach { opt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { kondisi = opt }
                    ) {
                        RadioButton(selected = kondisi == opt, onClick = { kondisi = opt })
                        Text(if (opt.isEmpty()) "Semua" else opt.replaceFirstChar { it.uppercase() })
                    }
                }
                Text("Status Bantuan:", style = MaterialTheme.typography.labelMedium)
                statusOptions.forEach { opt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { status = opt }
                    ) {
                        RadioButton(selected = status == opt, onClick = { status = opt })
                        Text(if (opt.isEmpty()) "Semua" else opt.replace("_", " ").replaceFirstChar { it.uppercase() })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(rw, kondisi, status) }) { Text("Terapkan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
