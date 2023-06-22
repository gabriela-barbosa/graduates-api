package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution
import java.util.*


data class InstitutionDTO(
  val typeId: UUID,
  val typeName: String,
  val name: String,
)

fun Institution.toDTO() = InstitutionDTO(
  typeId = type.id,
  typeName = type.name,
  name = name,
)
