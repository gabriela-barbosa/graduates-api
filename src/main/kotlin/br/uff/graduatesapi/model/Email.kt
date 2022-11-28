package br.uff.graduatesapi.model

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import javax.persistence.*


@Entity
@Table(name = "email")
class Email(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  var id: Int? = null,

  @Column(name = "title", nullable = false, updatable = true)
  var title: String = "",

  @Column(name = "name", nullable = false, updatable = false, unique = true)
  var name: String = "",

  @Column(name = "content", nullable = false, updatable = true)
  var content: String = "",

  @Column(name = "button_text", nullable = false, updatable = true)
  var buttonText: String = "",

  @Column(name = "button_url", nullable = false, updatable = true)
  var buttonURL: String = "",

  @Column(name = "active", nullable = false, updatable = true)
  var active: Boolean = true,

  @Column(name = "is_graduate_email", nullable = false, updatable = true)
  val isGraduateEmail: Boolean = true,

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: LocalDate? = null,

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = true, updatable = true)
  var updatedAt: LocalDate? = null,
)