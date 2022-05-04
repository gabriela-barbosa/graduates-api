package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.hibernate.annotations.CreationTimestamp
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*
import javax.persistence.*

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class PlatformUser(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int,

    @Column(nullable = false)
    var name: String,

    @Column(unique = true, nullable = false)
    var email: String,

    @OneToOne(mappedBy = "user")
    var advisor: Advisor? = null,

    @OneToOne(mappedBy = "user")
    var graduate: Graduate? = null,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Date
) {
    @Column(nullable = false)
    var password = ""
        @JsonIgnore
        get

    fun comparePassword(password: String): Boolean {
        return BCryptPasswordEncoder().matches(password, this.password)
    }
}