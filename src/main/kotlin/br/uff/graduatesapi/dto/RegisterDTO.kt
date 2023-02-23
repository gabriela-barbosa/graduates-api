package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enums.Role


data class RegisterDTO(
    val id: Int?,
    val name: String,
    val email: String,
    val role: Role,
    val password: String?,
)
