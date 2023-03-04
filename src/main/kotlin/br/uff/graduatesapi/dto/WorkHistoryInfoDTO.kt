package br.uff.graduatesapi.dto

import java.util.*

data class WorkHistoryInfoDTO(
    val id: UUID,
    val name: String?,
    val type: UUID?,
)
