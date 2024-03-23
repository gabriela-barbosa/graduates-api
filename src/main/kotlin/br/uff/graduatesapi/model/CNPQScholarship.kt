package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JoinFormula
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
class CNPQScholarship(
    @ManyToOne(optional = false)
    var level: CNPQLevel,

    @JsonIgnore
    @ManyToOne(optional = false)
    var graduate: Graduate,

    @CreationTimestamp
    @Column(name = "started_at", nullable = true, updatable = true)
    var startedAt: LocalDateTime? = null,

    @Column(name = "ended_at", nullable = true, updatable = true)
    var endedAt: LocalDateTime? = null
) {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    fun isCurrent(): Boolean {
        return endedAt == null
    }
}