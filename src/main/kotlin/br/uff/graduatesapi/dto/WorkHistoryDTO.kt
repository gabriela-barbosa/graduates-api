package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution

data class WorkHistoryDTO(
    val email: String,
    val newEmail: String? = null,
    val knownWorkPlace: Boolean,
    var position: String? = null,
    var institution: InstitutionDTO? = null,
    val cnpqLevelId: Int? = null,
    var postDoctorate: InstitutionDTO? = null,
    var finishedDoctorateOnUFF: Boolean? = false,
) {
    fun addInstitutionInfo(institution: Institution) {
        this.institution = InstitutionDTO(institution.id, institution.type, institution.name)
    }
    fun addPostDoctorate(institution: Institution) {
        this.postDoctorate = InstitutionDTO(institution.id, institution.type, institution.name)
    }
}
