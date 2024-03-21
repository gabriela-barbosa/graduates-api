package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.CIProgram
import br.uff.graduatesapi.model.Course
import br.uff.graduatesapi.model.Graduate
import java.time.LocalDate
import java.util.*


data class RegisterCourseDTO(
    val id: UUID? = null,
    val program: UUID,
    val defenseMinute: String,
    val titleDate: LocalDate,
    val advisor: UUID? = null,
    val graduate: UUID? = null,
)

fun RegisterCourseDTO.toCourse(program: CIProgram, advisor: Advisor, graduate: Graduate) = Course(
    program = program,
    defenseMinute = defenseMinute,
    titleDate = titleDate,
    advisor = advisor,
    graduate = graduate,
)

fun RegisterCourseDTO.isEqual(course: Course) =
    course.id == id
            && course.program.id == program
            && course.defenseMinute == defenseMinute
            && course.titleDate == titleDate
            && course.advisor.id == advisor
            && course.graduate.id == graduate

data class RegisterUserDTO(
    val id: UUID? = null,
    val name: String,
    val email: String,
    val roles: List<RoleEnum>,
)

data class RegisterGraduateDTO(
    val id: UUID? = null,
    val courses: List<RegisterCourseDTO>,
)

data class RegisterAdvisorDTO(
    val id: UUID? = null,
    val courses: List<RegisterCourseDTO>,
)

data class RegisterDTO(
    val user: RegisterUserDTO,
    val graduate: RegisterGraduateDTO? = null,
    val advisor: RegisterAdvisorDTO? = null,
)
