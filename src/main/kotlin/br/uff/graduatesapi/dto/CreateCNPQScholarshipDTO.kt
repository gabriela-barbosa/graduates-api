package br.uff.graduatesapi.dto

import java.time.LocalDate
import java.util.*

data class CreateCNPQScholarshipDTO(
  val levelId: UUID,
  val startedAt: LocalDate,
  val endedAt: LocalDate? = null,
)
