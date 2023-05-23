package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.WorkHistoryStatus
import br.uff.graduatesapi.model.Graduate
import java.util.*

data class GraduateItemDTO(
    val id: UUID,
    val name: String,
    val email: String,
    val status: WorkHistoryStatus,
    var workPlace: WorkHistoryInfoDTO?,
    var position: String?,
)

fun Graduate.toGraduateItemDTO() = GraduateItemDTO(
    id = id,
    name = user.name,
    email = user.email,
    status = getWorkHistoryStatus(),
    workPlace = lastWorkHistory?.toWorkHistoryInfoDTO(),
    position = lastWorkHistory?.position
)

data class ListGraduatesDTO(
    val data: List<GraduateItemDTO>,
    val meta: MetaDTO,
)
