package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution
import java.util.*

data class WorkHistoryDTO(
    val email: String,
    val newEmail: String? = "",
    val knownWorkPlace: Boolean?,
    var position: String? = "",
    var institution: InstitutionDTO? = null,
    val cnpqLevelId: UUID? = null,
    var postDoctorate: InstitutionDTO? = null,
    var hasFinishedDoctorateOnUFF: Boolean? = null,
    var hasFinishedMasterDegreeOnUFF: Boolean? = null,
) {
    fun addInstitutionInfo(institution: Institution) {
        this.institution = InstitutionDTO(institution.id, institution.type.id, institution.name)
    }
    fun addPostDoctorate(institution: Institution) {
        this.postDoctorate = InstitutionDTO(institution.id, institution.type.id, institution.name)
    }
}
