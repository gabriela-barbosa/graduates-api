package br.uff.graduatesapi.model

import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*

@Entity
class Graduate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @OneToOne(optional = true)
    var historyStatus: HistoryStatus? = null

    @OneToOne(optional = false)
    var user: PlatformUser? = null

    @OneToMany(mappedBy = "graduate")
    var courses: List<Course>?  = null

    @OneToMany(mappedBy = "graduate")
    var workHistory: List<WorkHistory>? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
}