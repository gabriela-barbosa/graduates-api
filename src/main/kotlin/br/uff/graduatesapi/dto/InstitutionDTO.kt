package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution
import java.util.*


data class InstitutionDTO(
  val id: UUID?,
  val type: UUID,
  val name: String,
)

fun Institution.toDTO() = InstitutionDTO(
  id = id,
  type = type.id,
  name = name,
)
