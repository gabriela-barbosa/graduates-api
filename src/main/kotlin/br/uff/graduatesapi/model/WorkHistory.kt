package br.uff.graduatesapi.model

import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity(name = "work_history")
class WorkHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "position", nullable = false)
    var position: String? = null

    @ManyToOne(optional = true)
    var institution: Institution? = null

    @ManyToOne(optional = false)
    var graduate: Graduate? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
}