package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JoinFormula
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Graduate(
    @JsonIgnore
    @OneToOne(optional = false)
    var user: PlatformUser,

    @OneToMany(mappedBy = "graduate")
    var cnpqScholarships: List<CNPQScholarship> = emptyList(),

    @OneToMany(mappedBy = "graduate")
    var courses: List<Course> = emptyList(),

    @OneToOne(mappedBy = "graduate", optional = true)
    var postDoctorate: PostDoctorate? = null,

    @OneToMany(mappedBy = "graduate")
    var workHistories: List<WorkHistory> = emptyList(),

    @Column(name = "finished_doctorate_on_uff", nullable = true)
    var hasFinishedDoctorateOnUFF: Boolean? = null,

    @Column(name = "finished_master_degree_on_uff", nullable = true)
    var hasFinishedMasterDegreeOnUFF: Boolean? = null,

    @Column(name = "success_case")
    var successCase: String? = null

) {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @OneToMany(mappedBy = "graduate")
    var historyStatus: List<HistoryStatus> = emptyList()

    @ManyToOne
    @JoinFormula("(SELECT w.id FROM work_history w WHERE w.graduate_id = id ORDER BY w.ended_at DESC, w.started_at DESC, w.created_at DESC limit 1)")
    val lastWorkHistory: WorkHistory? = null

    @ManyToOne
    @JoinFormula("(SELECT h.id FROM history_status h WHERE h.graduate_id = id AND extract(year from h.created_at) = extract(year from now())  ORDER BY h.created_at DESC limit 1)")
    val currentHistoryStatus: HistoryStatus? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, updatable = true)
    var updatedAt: LocalDateTime? = null
}