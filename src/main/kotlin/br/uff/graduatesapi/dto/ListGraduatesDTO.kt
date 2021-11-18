package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enums.WorkHistoryStatus

data class ListGraduatesDTO(
    val name: String,
    val status: WorkHistoryStatus,
    val workPlace: WorkPlaceDTO?,
    val position: String?,
)
