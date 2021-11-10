package br.uff.graduatesapi.model

import javax.persistence.*

@Entity
class Confirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "known_workplace", nullable = false)
    var knownWorkplace: Boolean? = null

    @OneToOne(mappedBy = "confirmation", optional = false)
    var advisor: Advisor? = null

    @OneToOne(mappedBy = "confirmation", optional = false)
    var graduate: Graduate? = null
}