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
    @Column(name = "id", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDate
}