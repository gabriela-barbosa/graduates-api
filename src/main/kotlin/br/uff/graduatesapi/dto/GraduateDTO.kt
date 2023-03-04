package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.PlatformUser
import java.util.*

data class GraduateDTO(
  val id: UUID,
  val user: PlatformUser,
)
