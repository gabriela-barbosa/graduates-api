package br.uff.graduatesapi.model

import br.uff.graduatesapi.enum.HistoryStatusEnum
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
class HistoryStatus(
  @Column(nullable = false)
  var status: HistoryStatusEnum = HistoryStatusEnum.PENDING,

  @Column(nullable = true)
  var pendingFields: String? = null,

  @Column(nullable = true)
  var emptyFields: String? = null,

  @JsonIgnore
  @ManyToOne(optional = false)
  var graduate: Graduate
) {
  @Id
  @Column(name = "id", nullable = false, unique = true)
  var id: UUID = UUID.randomUUID()

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  lateinit var createdAt: LocalDateTime

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = true, updatable = true)
  var updatedAt: LocalDateTime? = null
}