package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.WorkHistoryStatus
import java.util.*

data class DataListGraduatesDTO(
    val id: UUID,
    val name: String,
    val email: String,
    val status: WorkHistoryStatus,
    var workPlace: WorkHistoryInfoDTO?,
    var position: String?,
)

data class ListGraduatesDTO(
    val data: List<DataListGraduatesDTO>,
    val meta: MetaDTO,
)
