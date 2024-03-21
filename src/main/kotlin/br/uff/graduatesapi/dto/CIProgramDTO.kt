package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.CIProgram
import java.util.*

data class CIProgramDTO(
    val id: UUID? = null,
    val initials: String,
)

fun CIProgram.toDTO() = CIProgramDTO(
    id = id,
    initials = initials,
)
