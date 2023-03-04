package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*


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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    lateinit var id: UUID

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDate
}