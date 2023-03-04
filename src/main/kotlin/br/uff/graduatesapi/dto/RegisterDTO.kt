package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.Role
import java.util.*


data class RegisterDTO(
    val id: UUID?,
    val name: String,
    val email: String,
    val role: Role,
    val password: String?,
)
