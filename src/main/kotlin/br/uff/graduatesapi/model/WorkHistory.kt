package br.uff.graduatesapi.model

import br.uff.graduatesapi.enums.WorkHistoryStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import javax.persistence.*

@Entity(name = "work_history")
class WorkHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @Column(name = "position", nullable = true)
    var position: String? = null,

    @JsonIgnore
    @ManyToOne(optional = true)
    var institution: Institution? = null,

    @JsonIgnore
    @ManyToOne(optional = false)
    var graduate: Graduate,

    @Column(name = "status", nullable = false)
    var status: WorkHistoryStatus = WorkHistoryStatus.PENDING
) {
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDate? = null

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, updatable = true)
    var updatedAt: LocalDate? = null
}