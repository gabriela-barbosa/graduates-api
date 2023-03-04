package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JoinFormula
import java.time.LocalDate
import java.util.*
import javax.persistence.*


@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Graduate(
    @OneToOne(optional = true)
    var historyStatus: HistoryStatus? = null,

    @JsonIgnore
    @OneToOne(optional = false)
    var user: PlatformUser,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    lateinit var id: UUID

    @OneToMany(mappedBy = "graduate")
    var cnpqScholarship: List<CNPQScholarship> = emptyList()

    @OneToMany(mappedBy = "graduate")
    var courses: List<Course> = emptyList()

    @ManyToOne(optional = true)
    var postDoctorate: Institution? = null

    @OneToMany(mappedBy = "graduate")
    var workHistories: List<WorkHistory> = emptyList()

    @ManyToOne
    @JoinFormula("(SELECT w.id FROM work_history w WHERE w.graduate_id = id and EXTRACT(YEAR FROM w.created_at) = EXTRACT(YEAR FROM CURRENT_DATE) ORDER BY w.created_at DESC LIMIT 1)")
    val currentWorkHistory: WorkHistory? = null

    @Column(name = "finished_doctorate_on_uff", nullable = true)
    var hasFinishedDoctorateOnUFF: Boolean? = null

    @Column(name = "finished_master_degree_on_uff", nullable = true)
    var hasFinishedMasterDegreeOnUFF: Boolean? = null

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDate
}