package com.example.pelayananbantuangizi.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object LansiaList : Screen("lansia_list")
    object LansiaDetail : Screen("lansia_detail/{lansiaId}") {
        fun createRoute(id: Int) = "lansia_detail/$id"
    }
    object LansiaForm : Screen("lansia_form?lansiaId={lansiaId}") {
        fun createRoute(id: Int? = null) =
            if (id != null) "lansia_form?lansiaId=$id" else "lansia_form"
    }
    object PemeriksaanList : Screen("pemeriksaan_list/{lansiaId}") {
        fun createRoute(id: Int) = "pemeriksaan_list/$id"
    }
    object TambahPemeriksaan : Screen("tambah_pemeriksaan/{lansiaId}") {
        fun createRoute(id: Int) = "tambah_pemeriksaan/$id"
    }
}
