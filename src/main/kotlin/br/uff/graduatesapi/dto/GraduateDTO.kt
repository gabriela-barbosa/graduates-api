package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.PlatformUser

data class GraduateDTO(
  val id: Int,
  val user: PlatformUser,
)
