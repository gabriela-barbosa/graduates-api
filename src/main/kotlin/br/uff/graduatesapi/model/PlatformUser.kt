package br.uff.graduatesapi.model

import br.uff.graduatesapi.enums.Role
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.hibernate.annotations.CreationTimestamp
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDate
import javax.persistence.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class PlatformUser(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null,

    @Column(nullable = false)
    var name: String,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(name = "role", nullable = true)
    var role: Role? = Role.GRADUATE,

    @OneToOne(mappedBy = "user")
    var advisor: Advisor? = null,

    @OneToOne(mappedBy = "user")
    var graduate: Graduate? = null,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDate? = null
) {
    @Column(nullable = false)
    var password = ""
        @JsonIgnore
        get

    fun comparePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}