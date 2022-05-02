package br.uff.graduatesapi.dto

data class WorkHistoryDTO(
    val email: String,
    val newEmail: String?,
    val knownWorkPlace: Boolean,
    var position: String?,
    var institution: InstitutionDTO?,
    val cnpqLevelId: Int?,
    val postDoctorate: InstitutionDTO?,
    var finishedDoctorateOnUFF: Boolean?,
) {

}
