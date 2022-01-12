package br.uff.graduatesapi.model

import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*


@Entity
class CNPQScholarship {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @ManyToOne(optional = false)
    var level: CNPQLevel? = null

    @ManyToOne(optional = false)
    var graduate: Graduate? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var startYear: Date? = null

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var endYear: Date? = null
}