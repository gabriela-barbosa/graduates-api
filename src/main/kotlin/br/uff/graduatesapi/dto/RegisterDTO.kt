package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.RoleEnum
import java.util.*


data class RegisterDTO(
    val id: UUID? = null,
    val name: String,
    val email: String,
    val roles: List<RoleEnum>,
    val password: String? = null,
)
