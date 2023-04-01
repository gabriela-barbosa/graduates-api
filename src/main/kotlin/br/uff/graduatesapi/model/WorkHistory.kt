package br.uff.graduatesapi.model

import br.uff.graduatesapi.enum.WorkHistoryStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "work_history")
class WorkHistory(
    @JsonIgnore
    @ManyToOne(optional = false)
    var graduate: Graduate,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: WorkHistoryStatus = WorkHistoryStatus.PENDING,

    @Column(name = "position", nullable = true)
    var position: String? = null,

    @JsonIgnore
    @ManyToOne(optional = true)
    var institution: Institution? = null,
) {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDate

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, updatable = true)
    var updatedAt: LocalDate? = null
}