package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.CNPQScholarship
import java.time.LocalDate
import java.util.*

data class CreateCNPQScholarshipDTO(
  val levelId: UUID,
  val startedAt: LocalDate,
  val endedAt: LocalDate? = null,
)

fun CNPQScholarship.toCreateCNPQScholarshipDTO() = CreateCNPQScholarshipDTO(
  levelId = level.id,
  startedAt = startedAt,
  endedAt = endedAt,
)
