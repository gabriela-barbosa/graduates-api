package br.uff.graduatesapi.model

import br.uff.graduatesapi.enums.WorkHistoryStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
class HistoryStatus(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @Column(name = "known_workplace", nullable = true)
    var knownWorkplace: Boolean?,

    @JsonIgnore
    @OneToOne(mappedBy = "historyStatus", optional = false)
    var graduate: Graduate,

    @Column(name = "status", nullable = false)
    var status: WorkHistoryStatus = WorkHistoryStatus.PENDING,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
)