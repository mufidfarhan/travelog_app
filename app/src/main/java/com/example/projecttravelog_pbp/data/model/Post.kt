package com.example.projecttravelog_pbp.data.model

import java.util.*

data class Post(
    var id : String? = null,
    val user : String? = null,
    val tujuan : String? = null,
    val gambar : String? = null,
    val tanggal_mulai : String? = null,
    val tanggal_akhir : String? = null,
    val timestamp: Date? = null,
    val deskripsi : String? = null,
)
