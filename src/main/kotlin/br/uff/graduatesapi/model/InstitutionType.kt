package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "institution_type")
class InstitutionType(
    @Column(name = "name", nullable = false)
    var name: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    lateinit var id: UUID

    @JsonIgnore
    @OneToMany(mappedBy = "type")
    var institutions: List<Institution> = emptyList()

    @Column(name = "active", nullable = false, updatable = true, columnDefinition = "BOOLEAN DEFAULT true")
    val active: Boolean = true

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDate
}