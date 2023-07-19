package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.CNPQScholarship
import java.util.*

data class GetCNPQScholarshipDTO(
  val id: UUID?,
  val levelId: UUID,
  val startedAt: String,
  val endedAt: String? = null,
)

fun CNPQScholarship.toGetCNPQScholarshipDTO() = GetCNPQScholarshipDTO(
  id = id,
  levelId = level.id,
  startedAt = startedAt.toString(),
  endedAt = endedAt.toString(),
)
