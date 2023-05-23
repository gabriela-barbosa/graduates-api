package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.WorkHistory
import java.util.*

data class WorkHistoryInfoDTO(
    val id: UUID,
    val name: String?,
    val type: String?,
)

fun WorkHistory.toWorkHistoryInfoDTO() = WorkHistoryInfoDTO(
    id = id,
    name = institution.name,
    type = institution.type.name
)
