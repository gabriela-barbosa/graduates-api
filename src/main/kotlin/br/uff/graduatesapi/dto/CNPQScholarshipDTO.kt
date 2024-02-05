package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.CNPQScholarship
import java.time.LocalDateTime
import java.util.*

data class CNPQScholarshipDTO(
    val id: UUID?,
    val levelId: UUID,
    val startedAt: LocalDateTime? = null,
    val endedAt: LocalDateTime? = null,
)

fun CNPQScholarship.toCNPQScholarshipDTO() = CNPQScholarshipDTO(
    id = id,
    levelId = level.id,
    startedAt = startedAt,
    endedAt = endedAt,
)
