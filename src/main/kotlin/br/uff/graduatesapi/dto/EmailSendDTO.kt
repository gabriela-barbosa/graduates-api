package br.uff.graduatesapi.dto

data class EmailSendDTO(
    val subject: String?,
    val targetEmail: String?,
    val text: String?,
    val name: String?
)
