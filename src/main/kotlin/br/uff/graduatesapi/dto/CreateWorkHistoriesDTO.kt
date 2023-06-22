package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.Institution
import java.util.*

data class CreateInstitutionDTO(
  val typeId: UUID,
  val name: String,
)

data class CreateWorkHistoryDTO(
  var id: UUID?,
  var institution: CreateInstitutionDTO,
  var startedAt: String,
  var endedAt: String? = null,
  var position: String? = null,
)

data class CreatePostDoctorateDTO(
  var id: UUID?,
  var name: String,
  var institution: CreateInstitutionDTO,
  var startedAt: String,
  var endedAt: String? = null,
)

data class CreatePosDoctorateDTO(
  val institution: InstitutionDTO,

  )

data class CreateWorkHistoriesDTO(
  val graduateName: String,
  val email: String,
  val workHistories: List<CreateWorkHistoryDTO>? = null,
  val cnpqLevels: List<CNPQScholarshipDTO>? = null,
  val isPostDoctorateKnown: Boolean,
  val postDoctorate: CreatePostDoctorateDTO? = null,
//  val isHasFinishedDoctorateOnUFF: Boolean,
//  val isHasFinishedMasterDegreeOnUFF: Boolean,
  val hasFinishedDoctorateOnUFF: Boolean? = null,
  val hasFinishedMasterDegreeOnUFF: Boolean? = null,
  val successCase: String? = null
)

fun Institution.toCreateWorkHistoriesInstitutionDTO() = CreateInstitutionDTO(
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
