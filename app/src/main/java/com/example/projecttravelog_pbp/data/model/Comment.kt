package com.example.projecttravelog_pbp.data.model

import java.util.*

data class Comment(
    val user : String? = null,
    var id_perjalanan : String? = null,
    val timestamp: Date? = null,
    val komentar : String? = null,
)
