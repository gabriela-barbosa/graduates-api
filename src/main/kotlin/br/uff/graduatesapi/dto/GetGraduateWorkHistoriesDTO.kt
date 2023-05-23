package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.WorkHistory
import java.time.LocalDateTime
import java.util.*

data class WorkHistoryDTO(
  var institution: InstitutionDTO,
  var startedAt: LocalDateTime,
  var endedAt: LocalDateTime? = null,
  var position: String? = null,
)

data class GraduateWorkHistoriesDTO(
  val graduateId: UUID,
  val email: String,
  var postDoctorate: InstitutionDTO? = null,
  var hasFinishedDoctorateOnUFF: Boolean? = null,
  var hasFinishedMasterDegreeOnUFF: Boolean? = null,
  var successCase: String? = null,
  val cnpqLevels: List<CNPQScholarshipDTO>,
  val workHistories: List<WorkHistoryDTO>
)

fun Graduate.toGraduateWorkHistoriesDTO(workHistoriesDTO: List<WorkHistoryDTO>) = GraduateWorkHistoriesDTO(
  graduateId = id,
  email = user.email,
  postDoctorate = postDoctorate?.toDTO(),
  hasFinishedDoctorateOnUFF = hasFinishedDoctorateOnUFF,
  hasFinishedMasterDegreeOnUFF = hasFinishedMasterDegreeOnUFF,
  successCase = successCase,
  cnpqLevels = cnpqScholarships.map { it.toCNPQScholarshipDTO() },
  workHistories = workHistoriesDTO
)

fun WorkHistory.toWorkHistoryDTO() = WorkHistoryDTO(
  position = position,
  startedAt = startedAt,
  endedAt = endedAt,
  institution = institution.toDTO()
)
