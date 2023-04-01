package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ci_program")
class CIProgram(
  @Column(name = "initials", nullable = false, updatable = false)
  var initials: String,

  @Column(name = "active", nullable = false, updatable = true)
  val active: Boolean = true,
) {
  @Id
  @Column(name = "id", nullable = false, unique = true)
  var id: UUID = UUID.randomUUID()

  @JsonIgnore
  @OneToMany(mappedBy = "program")
  var courses: List<Course> = emptyList()

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  lateinit var createdAt: LocalDate
}