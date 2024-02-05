package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.model.*
import java.util.*

data class GetUserGraduateDTO(
  val id: UUID,
  val cnpqScholarship: List<CNPQScholarship>,
  val courses: List<Course>,
  val postDoctorate: Institution? = null,
  val workHistories: List<WorkHistory>,
  val currentWorkHistory: WorkHistory,
  val hasFinishedDoctorateOnUFF: Boolean? = null,
  val hasFinishedMasterDegreeOnUFF: Boolean? = null
)

data class GetAuthenticatedUser(
  var user: GetUserDTO,
  var token: String,
)

fun GetUserDTO.toGetAuthenticatedUser(token: String) = GetAuthenticatedUser(
  user = this,
  token = token
)

data class GetUserDTO(
  var id: UUID,
  var name: String,
  var email: String,
  var roleEnums: List<RoleEnum>,
  var currentRoleEnum: RoleEnum,
)

fun PlatformUser.toGetUserDTO(currentRoleEnum: RoleEnum) = GetUserDTO(
  id = id,
  name = name,
  email = email,
  roleEnums = roles,
  currentRoleEnum = currentRoleEnum
)
