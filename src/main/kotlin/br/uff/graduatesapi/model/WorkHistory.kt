package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*
import javax.persistence.*

@Entity(name = "work_history")
class WorkHistory(
    @Column(name = "position", nullable = true)
    var position: String? = null,

    @JsonIgnore
    @ManyToOne(optional = true)
    var institution: Institution? = null,

    @JsonIgnore
    @ManyToOne(optional = false)
    var graduate: Graduate,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = true, updatable = true)
    var updatedAt: Date? = null
}