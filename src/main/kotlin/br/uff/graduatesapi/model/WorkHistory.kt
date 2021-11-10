package br.uff.graduatesapi.model

import javax.persistence.*

@Entity(name = "work_history")
class WorkHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    var id: Int? = null

    @Column(name = "position", nullable = false)
    var position: String? = null

    @ManyToOne(optional = true)
    var institution: Institution? = null

    @ManyToMany(mappedBy = "workHistory")
    var graduates: List<Graduate>? = null
}