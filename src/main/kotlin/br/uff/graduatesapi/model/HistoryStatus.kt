package br.uff.graduatesapi.model

import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*

@Entity
class HistoryStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "known_workplace", nullable = false)
    var knownWorkplace: Boolean? = null

    @OneToOne(mappedBy = "historyStatus", optional = false)
    var graduate: Graduate? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
}