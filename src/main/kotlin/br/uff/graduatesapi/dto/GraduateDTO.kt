package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.PlatformUser
import java.util.*

data class GraduateDTO(
  val id: UUID,
  val user: PlatformUser,
)

fun Graduate.toDTO() = GraduateDTO(
  id = id,
  user = user
)
