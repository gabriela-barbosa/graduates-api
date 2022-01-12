package br.uff.graduatesapi.model

import br.uff.graduatesapi.enums.InstitutionType
import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int = 0

    @Column(name = "name", nullable = false)
    var name: String = ""

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    var type: InstitutionType = InstitutionType.OTHER

    @OneToMany(mappedBy = "institution")
    var workHistory: List<WorkHistory>? = null

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date? = null

}