package br.uff.graduatesapi.model

import br.uff.graduatesapi.enum.EmailDispatchStatusEnum
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
class EmailDispatch(

    @ManyToOne
    var user: PlatformUser,

    @ManyToOne
    var emailContent: Email,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status")
    var status: EmailDispatchStatusEnum? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true, updatable = true)
    var updatedAt: LocalDateTime? = null,
) {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    var id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime
}