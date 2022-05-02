package br.uff.graduatesapi.model

import br.uff.graduatesapi.enums.WorkHistoryStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
class HistoryStatus(
    @Column(name = "known_workplace", nullable = false)
    var knownWorkplace: Boolean? = null,

    @JsonIgnore
    @OneToOne(mappedBy = "historyStatus", optional = false)
    var graduate: Graduate? = null,

    @Column(name = "status", nullable = false)
    var status: WorkHistoryStatus = WorkHistoryStatus.PENDING
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
}