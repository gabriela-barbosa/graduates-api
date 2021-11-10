package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import javax.persistence.*

@Entity
open class PlatformUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int = 0

    @Column(nullable = false)
    var name = ""

    @Column(unique = true, nullable = false)
    var email = ""

    @Column(nullable = false)
    var password = ""
        @JsonIgnore
        get

    @OneToOne(mappedBy = "user")
    var advisor: Advisor? = null

    @OneToOne(mappedBy = "user")
    var graduate: Graduate? = null

    fun comparePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}