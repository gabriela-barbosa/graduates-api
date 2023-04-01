package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany


@Entity
class CNPQLevel(

  @Column(name = "level", nullable = false, updatable = false)
  var level: String,

  @Column(name = "active", nullable = false, updatable = true)
  val active: Boolean = true,

  ) {
  @Id
  @Column(name = "id", nullable = false, unique = true)
  var id: UUID = UUID.randomUUID()

  @JsonIgnore
  @OneToMany(mappedBy = "level")
  var cnpqScholarship: List<CNPQScholarship> = emptyList()

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  lateinit var createdAt: LocalDate
}