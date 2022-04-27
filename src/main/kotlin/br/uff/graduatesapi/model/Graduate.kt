package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Graduate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @OneToOne(optional = true)
    var historyStatus: HistoryStatus? = null

    @JsonIgnore
    @OneToOne(optional = false)
    var user: PlatformUser? = null

    @OneToMany(mappedBy = "graduate")
    var cnpqScholarship: List<CNPQScholarship>? = null

    @OneToMany(mappedBy = "graduate")
    var courses: List<Course>?  = null

    @OneToMany(mappedBy = "graduate")
    var workHistory: List<WorkHistory>? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
}