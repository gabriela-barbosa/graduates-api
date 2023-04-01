package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "email")
class Email(
    @Column(name = "title", nullable = false, updatable = true)
    var title: String,

    @Column(name = "name", nullable = false, updatable = false, unique = true)
    var name: String,

    @Column(name = "content", nullable = false, updatable = true)
    var content: String,

    @Column(name = "button_text", nullable = false, updatable = true)
    var buttonText: String,

    @Column(name = "button_url", nullable = false, updatable = true)
    var buttonURL: String,

    @Column(name = "is_graduate_email", nullable = false, updatable = true)
    val isGraduateEmail: Boolean,

    @Column(name = "active", nullable = false, updatable = true)
    var active: Boolean = true,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, updatable = true)
    var updatedAt: LocalDate? = null,
) {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDate
}