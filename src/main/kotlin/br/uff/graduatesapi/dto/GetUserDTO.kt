package br.uff.graduatesapi.dto

import br.uff.graduatesapi.enum.RoleEnum
import br.uff.graduatesapi.model.*
import java.util.*

data class GetAuthenticatedUser(
	var user: GetUserDTO,
	var token: String,
)

fun GetUserDTO.toGetAuthenticatedUser(token: String) = GetAuthenticatedUser(
	user = this,
	token = token
)

data class GetUserDTO(
	var id: UUID,
	var name: String,
	var email: String,
	var roles: List<RoleEnum>,
	var currentRole: RoleEnum,
)

fun PlatformUser.toGetUserDTO(currentRoleEnum: RoleEnum? = null) = GetUserDTO(
	id = id,
	name = name,
	email = email,
	roles = roles,
	currentRole = currentRoleEnum ?: currentRole ?: roles.first()
)
