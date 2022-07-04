package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enums.WorkHistoryStatus

data class ListGraduatesDTO(
    val id: Int,
    val name: String,
    val email: String,
    val status: WorkHistoryStatus,
    var workPlace: WorkHistoryInfoDTO?,
    var position: String?,
)
