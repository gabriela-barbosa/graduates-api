package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enums.InstitutionType

data class WorkHistoryDTO(
    val email: String,
    val newEmail: String?,
    val knownWorkPlace: Boolean,
    var position: String?,
    val institutionType: InstitutionType?,
    val institutionName: String?,
    val cnpqScholarship: Int?,
    val postDoctorate: String?,
    var finishedDoctorateOnUFF: Boolean,
)
