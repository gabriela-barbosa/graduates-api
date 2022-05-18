package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*


@Entity
class CNPQLevel(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @Column(name = "level", nullable = false, updatable = false)
    var level: String,

    @JsonIgnore
    @OneToMany(mappedBy = "level")
    var cnpqScholarship: List<CNPQScholarship>? = null,

    @Column(name = "active", nullable = false, updatable = true)
    val active: Boolean = true,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
)