package br.uff.graduatesapi.model

import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*


@Entity
class CNPQLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "level", nullable = false, updatable = false)
    var level: String = ""

    @OneToMany(mappedBy = "level")
    var cnpqScholarship: List<CNPQScholarship>? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
}