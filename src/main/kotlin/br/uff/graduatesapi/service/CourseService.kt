package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.RegisterCourseDTO
import br.uff.graduatesapi.dto.isEqual
import br.uff.graduatesapi.dto.toCourse
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.passError
import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.Course
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.repository.CourseRepository
import org.springframework.context.annotation.Lazy
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    @Lazy
    private val userService: UserService,
    private val ciProgramService: CIProgramService,
) {
    fun findCourses(): ResponseResult<List<Course>> =
        try {
            val result = courseRepository.findAll()
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_COURSES)
        }

    fun updateCourses(
        courses: List<RegisterCourseDTO>,
        graduate: Graduate?,
        advisor: Advisor?
    ): ResponseResult<List<Course>> =
        try {
            val coursesToSave = courses.map {
                val currentCourse = courseRepository.findByIdOrNull(it.id!!)
                    ?: return ResponseResult.Error(
                        Errors.COURSE_NOT_FOUND,
                        errorData = it.id.toString(),
                        errorCode = HttpStatus.UNPROCESSABLE_ENTITY
                    )
                if (it.isEqual(currentCourse))
                    currentCourse
                else {
                    if (it.program != currentCourse.program.id) {
                        val programToSave = when (val result = ciProgramService.findProgram(it.program)) {
                            is ResponseResult.Success -> result.data!!
                            is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
                        }
                        currentCourse.program = programToSave
                    }

                    if (graduate != null && it.graduate != currentCourse.graduate.id) {
                        val userAdvisorToSave = when (val result = userService.getById(it.advisor!!)) {
                            is ResponseResult.Success -> result.data!!
                            is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
                        }
                        currentCourse.graduate = graduate
                        currentCourse.advisor = userAdvisorToSave.advisor!!
                    } else if (advisor != null && it.advisor != currentCourse.advisor.id) {
                        val userGraduateToSave = when (val result = userService.getById(it.graduate!!)) {
                            is ResponseResult.Success -> result.data!!
                            is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
                        }
                        currentCourse.advisor = advisor
                        currentCourse.graduate = userGraduateToSave.graduate!!
                    }

                    currentCourse.defenseMinute = it.defenseMinute
                    currentCourse.titleDate = it.titleDate
                    currentCourse
                }
            }
            ResponseResult.Success(courseRepository.saveAll(coursesToSave))
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_UPDATE_COURSES)
        }

    fun createCourses(
        courses: List<RegisterCourseDTO>,
        graduate: Graduate? = null,
        advisor: Advisor? = null
    ): ResponseResult<List<Course>> =
        try {
            val coursesToSave = courses.map {
                val programToSave = when (val result = ciProgramService.findProgram(it.program)) {
                    is ResponseResult.Success -> result.data!!
                    is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
                }
                val newCourse = if (graduate != null) {
                    val userAdvisorToSave = when (val result = userService.getById(it.advisor!!)) {
                        is ResponseResult.Success -> result.data!!
                        is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
                    }
                    it.toCourse(programToSave, userAdvisorToSave.advisor!!, graduate)
                } else if (advisor != null) {
                    val userGraduateToSave = when (val result = userService.getById(it.graduate!!)) {
                        is ResponseResult.Success -> result.data!!
                        is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
                    }
                    it.toCourse(programToSave, advisor, userGraduateToSave.graduate!!)
                } else {
                    return ResponseResult.Error(Errors.CANT_CREATE_COURSE)
                }
                newCourse
            }
            val result = courseRepository.saveAll(coursesToSave)
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_CREATE_COURSES)
        }

    fun createCourse(course: Course): ResponseResult<Course?> =
        try {
            val result = courseRepository.save(course)
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_CREATE_COURSE)
        }
}