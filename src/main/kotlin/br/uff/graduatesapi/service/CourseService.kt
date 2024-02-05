package br.uff.graduatesapi.service

import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Course
import br.uff.graduatesapi.repository.CourseRepository
import org.springframework.stereotype.Service

@Service
class CourseService(
    private val courseService: CourseRepository,
) {
    fun findCourses(): ResponseResult<List<Course>> =
        try {
            val result = courseService.findAll()
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_COURSES)
        }

    fun createCourse(course: Course): ResponseResult<Course?> =
        try {
            val result = courseService.save(course)
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_CREATE_COURSE)
        }
}