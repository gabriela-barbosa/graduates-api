package br.uff.graduatesapi.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table
class ResetPasswordCode(
    @OneToOne(mappedBy = "resetPasswordCode")
    var user: PlatformUser,

    @Column(name = "code", nullable = false, updatable = false, unique = false)
    var code: String,
) {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime
}