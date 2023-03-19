package br.uff.graduatesapi.model

import br.uff.graduatesapi.enum.Role
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.hibernate.annotations.CreationTimestamp
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDate
import java.util.UUID
import javax.persistence.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class PlatformUser(

    @Column(nullable = false)
    var name: String,

    @Column(unique = true, nullable = false)
    var email: String,

    @ElementCollection(targetClass = Role::class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="platform_user_role")
    @Column(name="role")
    var roles: List<Role> = mutableListOf(),

    @OneToOne(mappedBy = "user")
    var advisor: Advisor? = null,

    @OneToOne(mappedBy = "user")
    var graduate: Graduate? = null,

) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    lateinit var id: UUID

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDate

    @Column(nullable = false)
    var password = ""
        @JsonIgnore
        get

    fun comparePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}