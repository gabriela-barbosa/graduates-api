package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.RoleEnum

data class UpdateCurrentRoleDTO(
    val currentRoleEnum: RoleEnum,
)
