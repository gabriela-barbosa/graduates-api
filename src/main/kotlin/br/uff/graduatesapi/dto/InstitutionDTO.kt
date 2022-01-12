package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enums.InstitutionType

data class InstitutionDTO(
    val id: Int?,
    val type: InstitutionType?,
    val name: String?,
)
