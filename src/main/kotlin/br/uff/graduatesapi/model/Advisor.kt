package br.uff.graduatesapi.model

import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*


@Entity
class Advisor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @OneToOne(optional = false)
    var user: PlatformUser? = null

    @OneToMany(mappedBy = "advisor")
    var courses: List<Course>? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
}