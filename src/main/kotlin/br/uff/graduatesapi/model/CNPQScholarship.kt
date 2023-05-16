package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
class CNPQScholarship(
  @ManyToOne(optional = false)
  var level: CNPQLevel,

  @JsonIgnore
  @ManyToOne(optional = false)
  var graduate: Graduate,

  @CreationTimestamp
  @Column(name = "started_at", nullable = false, updatable = false)
  var startedAt: LocalDate,

  @Column(name = "ended_at", nullable = true, updatable = false)
  var endedAt: LocalDate? = null
) {
  @Id
  @Column(name = "id", nullable = false, unique = true)
  var id: UUID = UUID.randomUUID()

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  lateinit var createdAt: LocalDate
}