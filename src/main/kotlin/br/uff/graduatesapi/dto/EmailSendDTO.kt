package br.uff.graduatesapi.dto

import java.util.*

data class EmailSendDTO(
    val subject: String?,
    val targetEmail: String?,
    val emailContentId: UUID
)
