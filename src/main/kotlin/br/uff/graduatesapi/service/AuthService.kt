package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.GetAuthenticatedUser
import br.uff.graduatesapi.dto.LoginDTO
import br.uff.graduatesapi.dto.toGetAuthenticatedUser
import br.uff.graduatesapi.dto.toGetUserDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.passError
import br.uff.graduatesapi.security.JWTUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
	private val userService: UserService,
	private val jwtUtil: JWTUtil,
	private val passwordEncoder: BCryptPasswordEncoder,
	private val resetPasswordCodeService: ResetPasswordCodeService,
	@Value("\${frontend.url:default}")
	private val frontendUrl: String = "",
	private val emailSenderService: EmailSenderService,
) {
	fun login(loginDTO: LoginDTO): ResponseResult<GetAuthenticatedUser> {


		val user = when (val resultUser = this.userService.findByEmail(loginDTO.email)) {
			is ResponseResult.Success -> resultUser.data!!
			is ResponseResult.Error -> return ResponseResult.Error(Errors.UNAUTHORIZED)
		}

		if (!user.comparePassword(loginDTO.password)) {
			return ResponseResult.Error(Errors.UNAUTHORIZED)
		}

		val userDTO = user.toGetUserDTO(user.currentRole ?: user.roles.sorted()[0])


		val issuer = user.id.toString()
		val token = jwtUtil.generateJWT(issuer)

		val result = userDTO.toGetAuthenticatedUser(token)

		return ResponseResult.Success(result)
	}

	fun sendResetPasswordEmail(email: String): ResponseResult<Nothing?> {
		val passwordCode = when (val result = resetPasswordCodeService.createPasswordCodeByEmail(email)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return result.passError()
		}

		val text =
			"Acesse o link para redefinir sua senha: $frontendUrl/reset-password/${passwordCode.id}. Esse link expira em 24 horas."
		return emailSenderService.sendEmail("Redefinição de senha", text, email)
	}
}