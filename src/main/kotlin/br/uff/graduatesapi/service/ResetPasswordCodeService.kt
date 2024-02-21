package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.CreateEmailDTO
import br.uff.graduatesapi.dto.GetEmailsDTO
import br.uff.graduatesapi.dto.MetaDTO
import br.uff.graduatesapi.dto.UpdateEmailDTO
import br.uff.graduatesapi.entity.EmailFilters
import br.uff.graduatesapi.entity.OffsetLimit
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.error.passError
import br.uff.graduatesapi.model.Email
import br.uff.graduatesapi.model.PlatformUser
import br.uff.graduatesapi.model.ResetPasswordCode
import br.uff.graduatesapi.repository.EmailRepository
import br.uff.graduatesapi.repository.ResetPasswordCodeRepository
import br.uff.graduatesapi.security.JWTUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport
import java.util.*

@Service
class ResetPasswordCodeService(
    private val resetPasswordCodeRepository: ResetPasswordCodeRepository,
    private val userService: UserService,
    private val jwtUtil: JWTUtil,
) {

    fun deletePasswordCodeByUserEmail(email: String): ResponseResult<Long> =
        try {
            val deleted = resetPasswordCodeRepository.deleteByUser_Email(email)
            ResponseResult.Success(deleted)
        } catch (ex: Exception) {
            ResponseResult.Error(Errors.CANT_DELETE_PASSWORD_CODE)
        }

    fun createPasswordCode(user: PlatformUser): ResponseResult<ResetPasswordCode> =
        try {
            val code = Utils.getRandomString(5)
            val codeJWT = jwtUtil.generateJWTWithExpiration(code, 5 * 60 * 1000)
            val resetPasswordCode = ResetPasswordCode(user, codeJWT)
            val created = resetPasswordCodeRepository.save(resetPasswordCode)
            ResponseResult.Success(created)
        } catch (ex: Exception) {
            ResponseResult.Error(Errors.CANT_CREATE_PASSWORD_CODE)
        }

    fun createPasswordCodeByEmail(email: String): ResponseResult<ResetPasswordCode> {
        val user = when (val result = userService.findByEmail(email)) {
            is ResponseResult.Success -> result.data!!
            is ResponseResult.Error -> return result.passError(HttpStatus.UNPROCESSABLE_ENTITY)
        }

        val codeDeleted = deletePasswordCodeByUserEmail(email)
        if (codeDeleted is ResponseResult.Error) return codeDeleted.passError()

        val codeCreated = when (val result = createPasswordCode(user)) {
            is ResponseResult.Success -> result.data!!
            is ResponseResult.Error -> return result.passError()
        }

        return ResponseResult.Success(codeCreated)
    }

    @Transactional(rollbackFor = [Exception::class])
    fun createPasswordCode(email: String): ResponseResult<ResetPasswordCode> {
        val result = createPasswordCodeByEmail(email)
        if (result is ResponseResult.Error) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result
    }
}