package br.uff.graduatesapi.entity

import java.util.*


data class GraduateFilters(
    val name: String?,
    val institutionName: String?,
    val institutionType: UUID?,
    var advisorId: UUID? = null,
    val advisorName: String? = null,
    val position: String? = null,
)
