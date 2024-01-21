package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.Role

data class UpdateCurrentRoleDTO(
  val currentRole: Role,
)
