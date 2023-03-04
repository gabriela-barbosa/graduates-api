package br.uff.graduatesapi.dto

import java.util.*


data class InstitutionDTO(
    val id: UUID?,
    val type: UUID,
    val name: String,
)
