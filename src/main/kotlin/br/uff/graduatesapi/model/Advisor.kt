package br.uff.graduatesapi.model

import javax.persistence.*


@Entity
class Advisor{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @OneToOne(optional = true)
    private val confirmation: Confirmation? = null

    @OneToOne(optional = false)
    var user: PlatformUser? = null

    @OneToMany(mappedBy = "advisor")
    var graduate: List<Graduate>? = null
}