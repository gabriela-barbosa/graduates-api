package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
class Institution(
    @Column(name = "name", nullable = false)
    var name: String,

    @ManyToOne(optional = false)
    var type: InstitutionType,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    lateinit var id: UUID

    @JsonIgnore
    @OneToMany(mappedBy = "postDoctorate")
    var graduate: List<Graduate> = emptyList()

    @OneToMany(mappedBy = "institution")
    var workHistory: List<WorkHistory> = emptyList()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDate
}