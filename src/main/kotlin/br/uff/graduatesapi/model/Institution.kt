package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
class Institution(
    @Column(name = "name", nullable = false)
    var name: String = "",

    @ManyToOne(optional = false)
    var type: InstitutionType? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int = 0

    @JsonIgnore
    @OneToMany(mappedBy = "postDoctorate")
    var graduate: List<Graduate>? = null

    @OneToMany(mappedBy = "institution")
    var workHistory: List<WorkHistory>? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDate? = null
}