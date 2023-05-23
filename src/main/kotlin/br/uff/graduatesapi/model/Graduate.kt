package br.uff.graduatesapi.model

import br.uff.graduatesapi.enum.WorkHistoryStatus
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
) {
  @Id
  @Column(name = "id", nullable = false, unique = true)
  var id: UUID = UUID.randomUUID()

  @OneToMany(mappedBy = "graduate")
  var cnpqScholarships: List<CNPQScholarship> = emptyList()

  @OneToMany(mappedBy = "graduate")
  var courses: List<Course> = emptyList()

  @ManyToOne(optional = true)
  var postDoctorate: Institution? = null

  @OneToMany(mappedBy = "graduate")
  var workHistories: List<WorkHistory> = emptyList()

  @ManyToMany
  @JoinFormula("(SELECT c.id FROM CNPQScholarship c WHERE c.graduate_id = id AND c.ended_at is null ORDER BY w.started_at DESC)")
  val currentCNPQScholarships: List<CNPQScholarship> = emptyList()

  @ManyToOne
  @JoinFormula("(SELECT w.id FROM work_history w WHERE w.graduate_id = id ORDER BY w.ended_at DESC, started_at DESC, created_at DESC limit 1)")
  val lastWorkHistory: WorkHistory? = null

  @Column(name = "finished_doctorate_on_uff", nullable = true)
  var hasFinishedDoctorateOnUFF: Boolean? = null

  @Column(name = "finished_master_degree_on_uff", nullable = true)
  var hasFinishedMasterDegreeOnUFF: Boolean? = null

  @Column(name = "success_case")
  var successCase: String? = null

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  lateinit var createdAt: LocalDateTime

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = true, updatable = true)
  var updatedAt: LocalDateTime? = null

  fun getWorkHistoryStatus(): WorkHistoryStatus {
    val isPending = this.lastWorkHistory == null || this.lastWorkHistory.updatedAt == null|| this.lastWorkHistory.updatedAt!!.year < LocalDateTime.now().year
    if (isPending) return WorkHistoryStatus.PENDING

    val wasUpdatedPartiallyWorkHistories = this.lastWorkHistory?.position == null
    if (wasUpdatedPartiallyWorkHistories || this.hasFinishedDoctorateOnUFF == null || this.hasFinishedMasterDegreeOnUFF == null) return WorkHistoryStatus.UPDATED_PARTIALLY

    return WorkHistoryStatus.UPDATED
  }
}