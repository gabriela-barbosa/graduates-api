package br.uff.graduatesapi.service

import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.passError
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.model.ResetPasswordCode
import br.uff.graduatesapi.repository.ResetPasswordCodeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.interceptor.TransactionAspectSupport
import java.util.*

@Service
class ResetPasswordCodeService(
	private val resetPasswordCodeRepository: ResetPasswordCodeRepository,
	private val userService: UserService,
) {

	fun deleteResetPasswordCode(id: UUID): ResponseResult<Nothing?> =
		try {
			resetPasswordCodeRepository.deleteById(id)
			ResponseResult.Success(null)
		} catch (ex: Exception) {
			ResponseResult.Error(Errors.CANT_DELETE_RESET_PASSWORD_CODE)
		}

	fun deletePasswordCodeByUserEmail(email: String): ResponseResult<Long> =
		try {
			val deleted = resetPasswordCodeRepository.deleteByUserEmail(email)
			ResponseResult.Success(deleted)
		} catch (ex: Exception) {
			ResponseResult.Error(Errors.CANT_DELETE_PASSWORD_CODE)
		}

	fun createPasswordCode(user: PlatformUser): ResponseResult<ResetPasswordCode> =
		try {
			val resetPasswordCode = ResetPasswordCode(user)
			val created = resetPasswordCodeRepository.save(resetPasswordCode)
			ResponseResult.Success(created)
		} catch (ex: Exception) {
			ResponseResult.Error(Errors.CANT_CREATE_PASSWORD_CODE)
		}

	fun getResetPasswordCodeById(id: UUID): ResponseResult<ResetPasswordCode> =
		try {
			val resetPasswordCode = resetPasswordCodeRepository.findByIdOrNull(id)
			if (resetPasswordCode == null) {
				ResponseResult.Error(Errors.RESET_PASSWORD_CODE_NOT_FOUND)
			} else {
				ResponseResult.Success(resetPasswordCode)
			}
		} catch (ex: Exception) {
			ResponseResult.Error(Errors.CANT_FIND_RESET_PASSWORD_CODE)
		}

	fun createPasswordCodeByEmail(email: String): ResponseResult<ResetPasswordCode> {
		when (val result = userService.findByEmail(email)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
		}

		val codeDeleted = deletePasswordCodeByUserEmail(email)
		val updatedUser = when (val result = userService.findByEmail(email)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
		}

		if (codeDeleted is ResponseResult.Error) return codeDeleted.passError()

		val codeCreated = when (val result = createPasswordCode(updatedUser)) {
			is ResponseResult.Success -> result.data!!
			is ResponseResult.Error -> return result.passError()
		}

		return ResponseResult.Success(codeCreated)
	}

	fun createPasswordCode(email: String): ResponseResult<ResetPasswordCode> {
		val result = createPasswordCodeByEmail(email)
		if (result is ResponseResult.Error) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}
		return result
	}
}