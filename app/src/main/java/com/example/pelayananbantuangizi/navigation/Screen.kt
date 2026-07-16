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
    object TambahPemeriksaan : Screen("tambah_pemeriksaan/{lansiaId}") {
        fun createRoute(id: Int) = "tambah_pemeriksaan/$id"
    }
    object Monitoring : Screen("monitoring/{lansiaId}/{namaLansia}") {
        fun createRoute(id: Int, nama: String) = "monitoring/$id/${java.net.URLEncoder.encode(nama, "UTF-8")}"
    }
}
