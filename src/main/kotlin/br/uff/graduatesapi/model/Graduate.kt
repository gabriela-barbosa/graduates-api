package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JoinFormula
import java.time.LocalDate
import javax.persistence.*


@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
class Graduate(
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  val id: Int,

  @JsonIgnore
  @OneToOne(optional = false)
  var user: PlatformUser,
) {
  @OneToMany(mappedBy = "graduate")
  var cnpqScholarship: List<CNPQScholarship>? = null

  @OneToMany(mappedBy = "graduate")
  var courses: List<Course>? = null

  @ManyToOne(optional = true)
  var postDoctorate: Institution? = null

  @OneToMany(mappedBy = "graduate")
  var workHistories: List<WorkHistory>? = null

  @ManyToOne
  @JoinFormula("(SELECT w.id FROM work_history w WHERE w.graduate_id = id and EXTRACT(YEAR FROM w.created_at) = EXTRACT(YEAR FROM CURRENT_DATE) ORDER BY w.created_at DESC LIMIT 1)")
  val currentWorkHistory: WorkHistory? = null

  @Column(name = "finished_doctorate_on_uff")
  var hasFinishedDoctorateOnUFF: Boolean? = false

  @Column(name = "finished_master_degree_on_uff")
  var hasFinishedMasterDegreeOnUFF: Boolean? = false

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  var createdAt: LocalDate? = null
}