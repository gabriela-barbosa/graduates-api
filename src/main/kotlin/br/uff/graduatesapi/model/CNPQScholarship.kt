package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*


@Entity
class CNPQScholarship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @JsonIgnore
    @ManyToOne(optional = false)
    var level: CNPQLevel? = null

    @JsonIgnore
    @ManyToOne(optional = false)
    var graduate: Graduate? = null

    @CreationTimestamp
    @Column(name = "start_year", nullable = false, updatable = false)
    var startYear: LocalDate? = null

    @Column(name = "end_year", nullable = true, updatable = false)
    var endYear: LocalDate? = null
}