package br.uff.graduatesapi.model

import br.uff.graduatesapi.enum.WorkHistoryStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
class HistoryStatus(
    @Column(name = "known_workplace", nullable = true)
    var knownWorkplace: Boolean?,

    @JsonIgnore
    @OneToOne(mappedBy = "historyStatus", optional = false)
    var graduate: Graduate,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: WorkHistoryStatus = WorkHistoryStatus.PENDING,
    ) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    lateinit var id: UUID

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDate
}