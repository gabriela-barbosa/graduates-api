package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enums.WorkHistoryStatus

data class MetaListGraduatesDTO(
    val page: Int,
    val size: Int,
    val total: Long,
)

data class DataListGraduatesDTO(
    val id: Int,
    val name: String,
    val email: String,
    val status: WorkHistoryStatus,
    var workPlace: WorkHistoryInfoDTO?,
    var position: String?,
)
data class ListGraduatesDTO(
    val data: List<DataListGraduatesDTO>,
    val meta: MetaListGraduatesDTO,
)
