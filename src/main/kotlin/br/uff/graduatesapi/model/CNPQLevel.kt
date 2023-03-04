package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*


@Entity
class CNPQLevel(

  @Column(name = "level", nullable = false, updatable = false)
  var level: String,

  @Column(name = "active", nullable = false, updatable = true)
  val active: Boolean = true,

  ) {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  lateinit var id: UUID

  @JsonIgnore
  @OneToMany(mappedBy = "level")
  var cnpqScholarship: List<CNPQScholarship> = emptyList()

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  lateinit var createdAt: LocalDate
}