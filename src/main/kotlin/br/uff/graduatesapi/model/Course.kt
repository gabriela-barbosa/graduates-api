package br.uff.graduatesapi.model

import br.uff.graduatesapi.enums.CIPrograms
import br.uff.graduatesapi.enums.InstitutionType
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*


@Entity
class Course{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "program", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    var program: CIPrograms? = null

    @Column(name = "minute_defense", nullable = false)
    var minuteDefense: Int? = null

    @JsonIgnore
    @ManyToOne(optional = false)
    var advisor: Advisor? = null

    @JsonIgnore
    @ManyToOne(optional = false)
    var graduate: Graduate? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null
}