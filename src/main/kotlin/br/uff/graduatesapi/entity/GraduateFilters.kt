package br.uff.graduatesapi.entity

import br.uff.graduatesapi.model.Advisor
import java.util.*


data class GraduateFilters(
    val name: String?,
    val institutionName: String?,
    val institutionType: UUID?,
    var advisor: Advisor? = null,
)
