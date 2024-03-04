package br.uff.graduatesapi.dto

import br.uff.graduatesapi.model.ResetPasswordCode


data class GetResetPasswordCodeDTO(
    val userEmail: String,
    val isExpired: Boolean,
)

fun ResetPasswordCode.toGetResetPasswordCodeDTO() = GetResetPasswordCodeDTO(
    userEmail = user.email,
    isExpired = isExpired(),
)
