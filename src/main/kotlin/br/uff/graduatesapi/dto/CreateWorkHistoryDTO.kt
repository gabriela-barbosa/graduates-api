package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution
import java.time.LocalDate

data class CreateWorkHistoryDTO(
  val email: String,
  var position: String? = null,
  var startedAt: LocalDate,
  var finishedAt: LocalDate? = null,
  var institution: InstitutionDTO,
  val cnpqLevels: List<CreateCNPQScholarshipDTO>,
  var postDoctorate: InstitutionDTO? = null,
  var hasFinishedDoctorateOnUFF: Boolean? = null,
  var hasFinishedMasterDegreeOnUFF: Boolean? = null,
  var successCase: String? = null
)
