package br.uff.graduatesapi.dto

import java.util.*

data class EmailsSendDTO(
    val subject: String?,
    val usersId: List<UUID>,
    val emailContentId: UUID,
)
