package br.uff.graduatesapi.dto

import java.util.*

data class ChangePasswordDTO(
	val code: UUID,
	val newPassword: String,
)
