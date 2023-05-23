package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.CNPQScholarship
import java.time.LocalDateTime
import java.util.*

data class CNPQScholarshipDTO(
  val levelId: UUID,
  val startedAt: LocalDateTime,
  val endedAt: LocalDateTime? = null,
)

fun CNPQScholarship.toCNPQScholarshipDTO() = CNPQScholarshipDTO(
  levelId = level.id,
  startedAt = startedAt,
  endedAt = endedAt,
)
