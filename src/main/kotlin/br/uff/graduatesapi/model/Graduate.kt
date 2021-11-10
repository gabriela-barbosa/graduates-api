package br.uff.graduatesapi.model

import javax.persistence.*

@Entity
class Graduate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @OneToOne(optional = true)
    var confirmation: Confirmation? = null

    @OneToOne(optional = false)
    var user: PlatformUser? = null

    @ManyToOne(optional = false)
    var advisor: Advisor? = null

    @ManyToMany
    var workHistory: List<WorkHistory>? = null
}