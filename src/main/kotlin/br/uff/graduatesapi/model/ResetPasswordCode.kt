package br.uff.graduatesapi.model

import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table
class ResetPasswordCode(
	@OneToOne(optional = false, cascade = [CascadeType.PERSIST])
	var user: PlatformUser,
) {
	@Id
	@Column(name = "id", nullable = false, unique = true)
	var id: UUID = UUID.randomUUID()

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	lateinit var createdAt: LocalDateTime

	fun isExpired(): Boolean {
		val now = LocalDateTime.now()
		val expirationTime = createdAt.plusHours(24)
		return now.isAfter(expirationTime)
	}
}