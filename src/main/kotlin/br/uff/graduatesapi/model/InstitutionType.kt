package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import javax.persistence.*


@Entity
@Table(name = "institution_type")
class InstitutionType(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @Column(name = "name", nullable = false)
    var name: String = "",

    @JsonIgnore
    @OneToMany(mappedBy = "type")
    var institutions: List<Institution>? = null,

    @Column(name = "active", nullable = false, updatable = true, columnDefinition="BOOLEAN DEFAULT true")
    val active: Boolean = true,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDate? = null,
)