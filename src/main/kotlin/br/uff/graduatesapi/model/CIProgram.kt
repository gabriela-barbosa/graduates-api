package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "ci_program")
class CIProgram(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @Column(name = "initials", nullable = false, updatable = false)
    var initials: String = "",

    @JsonIgnore
    @OneToMany(mappedBy = "program")
    var courses: List<Course>? = null,

    @Column(name = "active", nullable = false, updatable = true)
    val active: Boolean = true,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null,
)