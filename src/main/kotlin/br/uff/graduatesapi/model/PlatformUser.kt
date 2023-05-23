package br.uff.graduatesapi.model

import br.uff.graduatesapi.enum.Role
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime
import java.util.*
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
    @CollectionTable(name="platform_user_role", )
    @Column(name="role")
    var roles: List<Role> = mutableListOf(),

    @OneToOne(mappedBy = "user")
    var advisor: Advisor? = null,

    @OneToOne(mappedBy = "user")
    var graduate: Graduate? = null,

) {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, updatable = true)
    var updatedAt: LocalDateTime? = null

    @Column(nullable = false)
    var password = ""
        @JsonIgnore
        get

    fun comparePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}