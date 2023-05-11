package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution
import java.time.LocalDate

data class CreateWorkHistoryDTO(
  val email: String,
  val knownWorkPlace: Boolean?,
  var position: String? = null,
  var startedAt: LocalDate,
  var finishedAt: LocalDate? = null,
  var institution: InstitutionDTO,
  val cnpqLevels: List<CreateCNPQScholarshipDTO>,
  var postDoctorate: InstitutionDTO? = null,
  var hasFinishedDoctorateOnUFF: Boolean? = null,
  var hasFinishedMasterDegreeOnUFF: Boolean? = null,
  var successCase: String? = null
) {
  fun addInstitutionInfo(institution: Institution) {
    this.institution = InstitutionDTO(institution.id, institution.type.id, institution.name)
  }

  fun addPostDoctorate(institution: Institution) {
    this.postDoctorate = InstitutionDTO(institution.id, institution.type.id, institution.name)
  }
}
