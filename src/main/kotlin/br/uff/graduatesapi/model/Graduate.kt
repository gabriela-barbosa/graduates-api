package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Graduate(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int,

    @OneToOne(optional = true)
    var historyStatus: HistoryStatus? = null,

    @JsonIgnore
    @OneToOne(optional = false)
    var user: PlatformUser,
) {
    @OneToMany(mappedBy = "graduate")
    var cnpqScholarship: List<CNPQScholarship>? = null

    @OneToMany(mappedBy = "graduate")
    var courses: List<Course>?  = null

    @ManyToOne(optional=true)
    var postDoctorate: Institution? = null

    @OneToMany(mappedBy = "graduate")
    var workHistory: List<WorkHistory>? = null

    @Column(name = "finished_doctorate_on_uff")
    var hasFinishedDoctorateOnUFF: Boolean? = false

    @Column(name = "finished_master_degree_on_uff")
    var hasFinishedMasterDegreeOnUFF: Boolean? = false

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
}