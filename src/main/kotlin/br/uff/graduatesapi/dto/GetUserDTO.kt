package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.Course
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.PlatformUser
import java.time.LocalDate
import java.util.*

data class GetAuthenticatedUser(
	var user: GetUserDTO,
	var token: String,
)


data class GetUserDTO(
	var id: UUID,
	var name: String,
	var email: String,
	var roles: List<RoleEnum>,
	var currentRole: RoleEnum,
)

data class GetUserLeanDTO(
	var id: UUID,
	var name: String,
	var email: String,
)

data class GetGraduateDTO(
	val courses: List<GetCourseDTO>,
	val postDoctorate: PostDoctorateDTO? = null,
	val hasFinishedDoctorateOnUFF: Boolean? = null,
	val hasFinishedMasterDegreeOnUFF: Boolean? = null,
	val successCase: String? = null,
	val cnpqScholarships: List<GetCNPQScholarshipDTO>,
	val workHistories: List<WorkHistoryDTO>,
)

data class GetCourseDTO(
	val id: UUID? = null,
	val program: CIProgramDTO,
	val graduate: GetUserLeanDTO,
	val defenseMinute: String,
	val titleDate: LocalDate,
	val advisor: GetUserLeanDTO,
)

data class GetAdvisorDTO(
	val courses: List<GetCourseDTO>,
)

data class GetUserInfoDTO(
	val user: GetUserDTO,
	val graduate: GetGraduateDTO?,
	val advisor: GetAdvisorDTO?,
)

fun PlatformUser.toGetUserDTO() = GetUserDTO(
	id = id,
	name = name,
	email = email,
	roles = roles,
	currentRole = currentRole ?: roles.sorted()[0]
)

fun PlatformUser.toGetUserLeanDTO() = GetUserLeanDTO(
	id = id,
	name = name,
	email = email,
)

fun GetUserDTO.toGetAuthenticatedUser(token: String) = GetAuthenticatedUser(
	user = this,
	token = token
)

fun Graduate.toGetGraduateDTO() = GetGraduateDTO(
	postDoctorate = postDoctorate?.toDTO(),
	hasFinishedDoctorateOnUFF = hasFinishedDoctorateOnUFF,
	hasFinishedMasterDegreeOnUFF = hasFinishedMasterDegreeOnUFF,
	successCase = successCase,
	courses = courses.map { it.toGetCourseDTO() },
	cnpqScholarships = cnpqScholarships.map { it.toGetCNPQScholarshipDTO() },
	workHistories = workHistories.map { it.toWorkHistoryDTO() },
)

fun PlatformUser.toGetUserInfoDTO() = GetUserInfoDTO(
	user = toGetUserDTO(),
	graduate = graduate?.toGetGraduateDTO(),
	advisor = advisor?.toGetAdvisorDTO(),
)

fun Course.toGetCourseDTO() = GetCourseDTO(
	program = program.toDTO(),
	graduate = graduate.user.toGetUserLeanDTO(),
	defenseMinute = defenseMinute,
	titleDate = titleDate,
	advisor = advisor.user.toGetUserLeanDTO(),
)

fun Advisor.toGetAdvisorDTO() = GetAdvisorDTO(
	courses = courses.map { it.toGetCourseDTO() },
)

