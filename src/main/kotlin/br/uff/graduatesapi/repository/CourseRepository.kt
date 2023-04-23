package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Course
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface CourseRepository : JpaRepository<Course, UUID>