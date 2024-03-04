package br.uff.graduatesapi.controller

import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.toResponseEntity
import br.uff.graduatesapi.service.CourseService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1")
class CourseController(private val courseService: CourseService) {
    @PreAuthorize("isAuthenticated()")
    @GetMapping("courses")
    fun getCourses(): ResponseEntity<Any> =
        when (val result = this.courseService.findCourses()) {
            is ResponseResult.Success -> ResponseEntity.ok(result.data)
            is ResponseResult.Error -> result.toResponseEntity()
        }
}