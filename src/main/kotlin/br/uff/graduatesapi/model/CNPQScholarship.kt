package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
class CNPQScholarship(
    @JsonIgnore
    @ManyToOne(optional = false)
    var level: CNPQLevel,

    @JsonIgnore
    @ManyToOne(optional = false)
    var graduate: Graduate
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    lateinit var id: UUID

    @CreationTimestamp
    @Column(name = "start_year", nullable = false, updatable = false)
    lateinit var startYear: LocalDate

    @Column(name = "end_year", nullable = true, updatable = false)
    var endYear: LocalDate? = null
}