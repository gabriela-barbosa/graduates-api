package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.PostDoctorate
import br.uff.graduatesapi.model.WorkHistory
import java.util.*

data class WorkHistoryDTO(
  val id: UUID,
  val institution: InstitutionDTO,
  val startedAt: String,
  val endedAt: String? = null,
  val position: String? = null,
)

data class PostDoctorateDTO(
  val id: UUID,
  val institution: InstitutionDTO,
  val startedAt: String,
  val endedAt: String? = null,
)

fun PostDoctorate.toDTO() = PostDoctorateDTO(
  id = id,
  institution = institution.toDTO(),
  startedAt = startedAt.toString(),
  endedAt = endedAt.toString(),
)


data class GraduateWorkHistoriesDTO(
  val graduateId: UUID,
  val graduateName: String,
  val email: String,
  val postDoctorate: PostDoctorateDTO?,
  val hasFinishedDoctorateOnUFF: Boolean? = null,
  val hasFinishedMasterDegreeOnUFF: Boolean? = null,
  val successCase: String? = null,
  val cnpqScholarships: List<GetCNPQScholarshipDTO>,
  val workHistories: List<WorkHistoryDTO>,
  val pendingFields: List<String>,
  val emptyFields: List<String>,
)

fun Graduate.toGraduateWorkHistoriesDTO(workHistories: List<WorkHistory>) = GraduateWorkHistoriesDTO(
  graduateId = id,
  graduateName = user.name,
  email = user.email,
  postDoctorate = postDoctorate?.toDTO(),
  hasFinishedDoctorateOnUFF = hasFinishedDoctorateOnUFF,
  hasFinishedMasterDegreeOnUFF = hasFinishedMasterDegreeOnUFF,
  successCase = successCase,
  cnpqScholarships = cnpqScholarships.map { it.toGetCNPQScholarshipDTO() },
  workHistories = workHistories.map { it.toWorkHistoryDTO() },
  pendingFields = currentHistoryStatus?.pendingFields?.split(",") ?: emptyList(),
  emptyFields = currentHistoryStatus?.emptyFields?.split(",") ?: emptyList(),
)

fun WorkHistory.toWorkHistoryDTO() = WorkHistoryDTO(
  id = id,
  position = position,
  startedAt = startedAt.toString(),
  endedAt = endedAt.toString(),
  institution = institution.toDTO()
)
