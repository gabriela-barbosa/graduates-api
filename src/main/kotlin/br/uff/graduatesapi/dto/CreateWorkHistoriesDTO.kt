package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution
import java.util.*

data class CreateInstitutionDTO(
  val id: UUID? = null,
  val typeId: UUID,
  val name: String,
)

data class CreateWorkHistoryDTO(
  val id: UUID? = null,
  val institution: CreateInstitutionDTO,
  val startedAt: String? = null,
  val endedAt: String? = null,
  val position: String? = null,
)

data class CreatePostDoctorateDTO(
  val id: UUID? = null,
  val institution: CreateInstitutionDTO,
  val startedAt: String? = null,
  val endedAt: String? = null,
)

data class CreatePosDoctorateDTO(
  val institution: InstitutionDTO,

  )

data class CreateWorkHistoriesDTO(
  val graduateName: String,
  val email: String,
  val successCase: String? = null,
  val workHistories: List<CreateWorkHistoryDTO>? = null,
  val cnpqScholarships: List<CNPQScholarshipDTO>? = null,
  val postDoctorate: CreatePostDoctorateDTO? = null,
  val hasCurrentCNPQScholarship: Boolean? = null,
  val hasPostDoctorate: Boolean? = null,
  val hasCurrentWorkHistory: Boolean? = null,
  val hasFinishedDoctorateOnUFF: Boolean? = null,
  val hasFinishedMasterDegreeOnUFF: Boolean? = null,
)

fun Institution.toCreateInstitutionDTO() = CreateInstitutionDTO(
  typeId = type.id,
  name = name,
)

//fun WorkHistory.toCreateDTO(scholarships: List<CNPQScholarship>, graduate: Graduate) = CreateWorkHistoriesDTO(
//  email = graduate.user.email,
//  position = position,
//  startedAt = startedAt,
//  endedAt = endedAt,
//  institution = institution.toDTO(),
//  cnpqLevels = scholarships.map { it.toCNPQScholarshipDTO() },
//  postDoctorates = graduate.postDoctorate?.toDTO(),
//  hasFinishedDoctorateOnUFF = graduate.hasFinishedDoctorateOnUFF,
//  hasFinishedMasterDegreeOnUFF = graduate.hasFinishedMasterDegreeOnUFF,
//  successCase = graduate.successCase
//)
