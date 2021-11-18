package br.uff.graduatesapi.model

import br.uff.graduatesapi.enums.InstitutionType
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*

@Entity
class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "name", nullable = false)
    var name: String? = null

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    var type: InstitutionType? = null

    @OneToMany(mappedBy = "institution")
    var workHistory: List<WorkHistory>? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null

}