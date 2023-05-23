package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.CNPQScholarship
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.WorkHistory
import java.time.LocalDateTime

data class CreateWorkHistoryDTO(
  val email: String,
  var position: String? = null,
  var startedAt: LocalDateTime,
  var endedAt: LocalDateTime? = null,
  var institution: InstitutionDTO,
  val cnpqLevels: List<CNPQScholarshipDTO>,
  var postDoctorate: InstitutionDTO? = null,
  var hasFinishedDoctorateOnUFF: Boolean? = null,
  var hasFinishedMasterDegreeOnUFF: Boolean? = null,
  var successCase: String? = null
)

fun WorkHistory.toCreateDTO(scholarships: List<CNPQScholarship>, graduate: Graduate) = CreateWorkHistoryDTO(
  email = graduate.user.email,
  position = position,
  startedAt = startedAt,
  endedAt = endedAt,
  institution = institution.toDTO(),
  cnpqLevels = scholarships.map { it.toCNPQScholarshipDTO() },
  postDoctorate = graduate.postDoctorate?.toDTO(),
  hasFinishedDoctorateOnUFF = graduate.hasFinishedDoctorateOnUFF,
  hasFinishedMasterDegreeOnUFF = graduate.hasFinishedMasterDegreeOnUFF,
  successCase = graduate.successCase
)
