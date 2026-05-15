package com.example.pelayananbantuangizi.ui.lansia

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pelayananbantuangizi.data.api.model.LansiaDto
import com.example.pelayananbantuangizi.data.repository.LansiaRepository
import com.example.pelayananbantuangizi.util.UiState
import com.example.pelayananbantuangizi.util.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LansiaFormScreen(
    lansiaRepository: LansiaRepository,
    lansiaId: Int?,
    onSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val vm: LansiaFormViewModel = viewModel(factory = ViewModelFactory(lansiaRepository))
    val submitState by vm.submitState.collectAsState()
    val loadState by vm.loadState.collectAsState()
    val context = LocalContext.current

    var nik by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var tanggalLahir by remember { mutableStateOf("") }
    var jenisKelamin by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var rt by remember { mutableStateOf("") }
    var rw by remember { mutableStateOf("") }
    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var existingFotoUrl by remember { mutableStateOf<String?>(null) }
    var prefilled by remember { mutableStateOf(false) }

    val isEditMode = lansiaId != null
    val title = if (isEditMode) "Edit Data Lansia" else "Tambah Lansia"

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> fotoUri = uri }

    LaunchedEffect(lansiaId) {
        if (lansiaId != null) vm.loadForEdit(lansiaId)
    }

    LaunchedEffect(loadState) {
        if (loadState is UiState.Success && !prefilled) {
            val lansia = (loadState as UiState.Success<LansiaDto>).data
            nik = lansia.nik
            nama = lansia.nama
            tanggalLahir = lansia.tanggalLahir
            jenisKelamin = lansia.jenisKelamin
            alamat = lansia.alamat
            rt = lansia.rt ?: ""
            rw = lansia.rw
            existingFotoUrl = lansia.fotoKtp
            prefilled = true
        }
    }

    LaunchedEffect(submitState) {
        if (submitState is UiState.Success) {
            onSuccess()
            vm.resetSubmitState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (isEditMode && loadState is UiState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nik,
                onValueChange = { if (it.length <= 16) nik = it },
                label = { Text("NIK (16 digit)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("${nik.length}/16") }
            )

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Lengkap") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tanggalLahir,
                onValueChange = { tanggalLahir = it },
                label = { Text("Tanggal Lahir (YYYY-MM-DD)") },
                placeholder = { Text("1950-01-01") },
                singleLine = true,
                trailingIcon = { Icon(Icons.Default.DateRange, null) },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Jenis Kelamin", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("L" to "Laki-laki", "P" to "Perempuan").forEach { (value, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = jenisKelamin == value,
                            onClick = { jenisKelamin = value }
                        )
                        Text(label)
                    }
                }
            }

            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rt,
                    onValueChange = { rt = it },
                    label = { Text("RT (opsional)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = rw,
                    onValueChange = { rw = it },
                    label = { Text("RW") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            // Foto KTP section
            Text("Foto KTP (opsional)", style = MaterialTheme.typography.labelLarge)

            val fotoDisplayUrl: Any? = fotoUri ?: existingFotoUrl
            if (fotoDisplayUrl != null) {
                AsyncImage(
                    model = fotoDisplayUrl,
                    contentDescription = "Foto KTP",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clickable { imagePicker.launch("image/*") },
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedButton(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    when {
                        fotoUri != null -> "Ganti Foto KTP"
                        existingFotoUrl != null -> "Ganti Foto KTP"
                        else -> "Pilih Foto KTP"
                    }
                )
            }

            if (fotoUri != null) {
                Text(
                    "Foto baru dipilih — akan diupload saat simpan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (submitState is UiState.Error) {
                Text(
                    text = (submitState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    vm.submit(
                        lansiaId, nik, nama, tanggalLahir, jenisKelamin, alamat, rt, rw,
                        fotoUri, context.contentResolver
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = submitState !is UiState.Loading
            ) {
                if (submitState is UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (isEditMode) "Simpan Perubahan" else "Simpan")
                }
            }
        }
    }
}
