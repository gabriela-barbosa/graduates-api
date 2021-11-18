package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enums.InstitutionType

data class WorkPlaceDTO(
    val name: String,
    val type: InstitutionType,
)
