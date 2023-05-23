package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "work_history")
class WorkHistory(
  @JsonIgnore
  @ManyToOne(optional = false)
  var graduate: Graduate,

  @Column(name = "position", nullable = true)
  var position: String? = null,

  @JsonIgnore
  @ManyToOne(optional = false)
  var institution: Institution,

  @Column(name = "started_at", nullable = false, updatable = true)
  var startedAt: LocalDateTime,

  @Column(name = "ended_at", nullable = true, updatable = true)
  var endedAt: LocalDateTime? = null,

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = true, updatable = true)
  var updatedAt: LocalDateTime? = null
) {
  @Id
  @Column(name = "id", nullable = false, unique = true)
  var id: UUID = UUID.randomUUID()

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  lateinit var createdAt: LocalDateTime


}