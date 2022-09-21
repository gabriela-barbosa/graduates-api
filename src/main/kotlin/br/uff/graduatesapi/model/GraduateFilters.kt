package br.uff.graduatesapi.model

import br.uff.graduatesapi.model.Institution

data class GraduateFilters(
    val name: String?,
    val institutionName: String?,
    val institutionType: Int?,
    var advisor: Advisor? = null,
)
