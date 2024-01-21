package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution
import java.util.*


data class InstitutionDTO(
  val id: UUID? = null,
  val typeId: UUID,
  val typeName: String,
  val name: String,
)

fun Institution.toDTO() = InstitutionDTO(
  id = id,
  typeId = type.id,
  typeName = type.name,
  name = name,
)
