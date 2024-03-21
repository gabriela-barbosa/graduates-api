package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne


@Entity
class Course(
    @ManyToOne(optional = false)
    var program: CIProgram,

    @JsonIgnore
    @ManyToOne(optional = false)
    var advisor: Advisor,

    @JsonIgnore
    @ManyToOne(optional = false)
    var graduate: Graduate,

    @Column(nullable = false, unique = true)
    var defenseMinute: String,

    @Column(nullable = false)
    var titleDate: LocalDate,
) {
    @Id
    @Column(nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, updatable = true)
    var updatedAt: LocalDateTime? = null

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime
}