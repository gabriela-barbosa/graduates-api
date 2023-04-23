package br.uff.graduatesapi.controller

import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.service.CourseService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/v1")
class CourseController(private val courseService: CourseService) {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("courses")
    fun getCourses(): ResponseEntity<Any> =
        when (val result = this.courseService.findCourses()) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> ResponseEntity.status(result.errorReason!!.errorCode)
                .body(result.errorReason.responseMessage)
        }
}