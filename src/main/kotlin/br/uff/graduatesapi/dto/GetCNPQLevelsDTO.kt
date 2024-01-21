package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.CNPQLevel
import java.util.*

data class GetCNPQLevelsDTO(
  val id: UUID,
  val name: String,
)

fun CNPQLevel.toGetCNPQLevelsDTO() = GetCNPQLevelsDTO(
  id = id,
  name = name,
)
