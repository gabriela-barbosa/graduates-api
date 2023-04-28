package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.Role
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

data class GetUserDTO(
    var id: UUID,
    var name: String,
    var email: String,
    var roles: List<Role>,
    var advisor: Advisor? = null,
    var graduate: GetUserGraduateDTO? = null,
)
