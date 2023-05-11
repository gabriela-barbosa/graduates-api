package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution

data class UpdateWorkHistoryDTO(
    val email: String,
    val knownWorkPlace: Boolean?,
    var position: String? = null,
    var institution: InstitutionDTO? = null,
    val cnpqLevelId: Int? = null,
    var postDoctorate: InstitutionDTO? = null,
    var hasFinishedDoctorateOnUFF: Boolean? = null,
    var hasFinishedMasterDegreeOnUFF: Boolean? = null,
    var successCase: String? = null
) {
    fun addInstitutionInfo(institution: Institution) {
        this.institution = InstitutionDTO(institution.id, institution.type!!.id!!, institution.name)
    }
    fun addPostDoctorate(institution: Institution) {
        this.postDoctorate = InstitutionDTO(institution.id, institution.type!!.id!!, institution.name)
    }
}
