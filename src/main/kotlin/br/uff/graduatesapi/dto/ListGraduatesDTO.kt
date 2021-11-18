package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enums.WorkHistoryStatus

data class ListGraduatesDTO(
    val name: String,
    val status: WorkHistoryStatus,
    var workPlace: WorkPlaceDTO?,
    var position: String?,
)
