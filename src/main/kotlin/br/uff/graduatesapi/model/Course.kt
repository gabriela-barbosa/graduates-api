package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
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

    @Column(name = "minute_defense", nullable = false)
    var minuteDefense: Int,
) {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime
}