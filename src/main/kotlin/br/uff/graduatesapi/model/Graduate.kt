package br.uff.graduatesapi.model

import br.uff.graduatesapi.enum.WorkHistoryStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JoinFormula
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
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
  var cnpqScholarship: List<CNPQScholarship> = emptyList()

  @OneToMany(mappedBy = "graduate")
  var courses: List<Course> = emptyList()

  @ManyToOne(optional = true)
  var postDoctorate: Institution? = null

  @OneToMany(mappedBy = "graduate")
  var workHistories: List<WorkHistory> = emptyList()

  @ManyToMany
  @JoinFormula("(SELECT w.id FROM work_history w WHERE w.graduate_id = id AND w.finished_at is null ORDER BY w.started_at DESC)")
  val currentWorkHistories: List<WorkHistory> = emptyList()

  @Column(name = "finished_doctorate_on_uff", nullable = true)
  var hasFinishedDoctorateOnUFF: Boolean? = null

  @Column(name = "finished_master_degree_on_uff", nullable = true)
  var hasFinishedMasterDegreeOnUFF: Boolean? = null

  @Column(name = "success_case")
  var successCase: String? = null

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  lateinit var createdAt: LocalDate

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = true, updatable = true)
  var updatedAt: LocalDate? = null

  fun getWorkHistoryStatus(): WorkHistoryStatus {
    val isPending = this.currentWorkHistories.any {
      it.updatedAt == null || it.updatedAt!!.year < LocalDate.now().year
    }
    if (isPending || currentWorkHistories.isEmpty()) return WorkHistoryStatus.PENDING

    val wasUpdatedPartiallyWorkHistories = this.currentWorkHistories.any {
      it.position == null
    }
    if (wasUpdatedPartiallyWorkHistories || this.hasFinishedDoctorateOnUFF == null || this.hasFinishedMasterDegreeOnUFF == null) return WorkHistoryStatus.UPDATED_PARTIALLY

    return WorkHistoryStatus.UPDATED
  }
}