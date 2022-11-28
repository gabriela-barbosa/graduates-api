package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CreateEmailDTO
import br.uff.graduatesapi.dto.GetEmailsDTO
import br.uff.graduatesapi.dto.MetaDTO
import br.uff.graduatesapi.dto.UpdateEmailDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Email
import br.uff.graduatesapi.model.EmailFilters
import br.uff.graduatesapi.model.OffsetLimit
import br.uff.graduatesapi.repository.EmailRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class EmailService(
  private val emailRepository: EmailRepository,
) {
  fun findAll(
    emailFilters: EmailFilters,
    pageConfig: OffsetLimit,
  ): ResponseResult<GetEmailsDTO> {
    return try {
      val result = emailRepository.getAll(emailFilters, pageConfig)
      val count = emailRepository.getAllCount(emailFilters, pageConfig)
      val meta = MetaDTO(pageConfig.page, result.count(), count)
      val resp = GetEmailsDTO(
        data = result,
        meta = meta,
      )
      ResponseResult.Success(resp)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_RETRIEVE_EMAILS)
    }
  }

  fun deleteEmail(
    id: Int
  ): ResponseResult<Nothing?> {
    return try {
      val result = this.findEmail(id)

      if (result is ResponseResult.Error)
        return ResponseResult.Error(Errors.INVALID_DATA)

      val data = result.data

      if (data!!.active)
        return ResponseResult.Error(Errors.CANT_DELETE_AN_ACTIVE_EMAIL)
      emailRepository.delete(data)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_DELETE_EMAIL)
    }
  }

  fun findEmailById(id: Int): ResponseResult<Email> {
    val email = emailRepository.findByIdOrNull(id)
    return if (email == null) {
      ResponseResult.Error(Errors.EMAIL_NOT_FOUND)
    } else {
      ResponseResult.Success(email)
    }
  }

  fun findEmail(id: Int): ResponseResult<Email?> {
    val result = emailRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.EMAIL_NOT_FOUND)
    return ResponseResult.Success(result)
  }

  fun createEmail(id: Int): ResponseResult<Nothing?> {
    return try {
      emailRepository.deleteById(id)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_DELETE_CI_PROGRAM)
    }
  }

  fun createEmail(createEmailDTO: CreateEmailDTO): ResponseResult<Nothing?> {
    val email = Email(
      title = createEmailDTO.title,
      name = createEmailDTO.name,
      content = createEmailDTO.content,
      buttonText = createEmailDTO.buttonText,
      buttonURL = createEmailDTO.buttonURL,
      active = createEmailDTO.active,
      isGraduateEmail = createEmailDTO.isGraduateEmail,
    )
    return try {
      if(createEmailDTO.active)
        emailRepository.deactivateEmails(createEmailDTO.isGraduateEmail)
      emailRepository.save(email)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_CREATE_EMAIL)
    }
  }

  fun deactivateEmails(isGraduateEmail: Boolean): ResponseResult<Nothing?> {
    return try {
      emailRepository.deactivateEmails(isGraduateEmail)
      ResponseResult.Success(null)
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.CANT_DEACTIVATE_EMAILS)
    }
  }

  fun editEmail(updateEmailDTO: UpdateEmailDTO, id: Int): ResponseResult<Nothing?> {
    return try {
      val result = this.findEmail(id)
      if (result is ResponseResult.Error)
        return ResponseResult.Error(Errors.INVALID_DATA)
      val email = result.data!!
      updateEmailDTO.title?.let { email.title = it }
      updateEmailDTO.content?.let { email.content = it }
      updateEmailDTO.buttonText?.let { email.buttonText = it }
      updateEmailDTO.buttonURL?.let { email.buttonURL = it }
      updateEmailDTO.active?.let {
        if (it && !email.active) {
          deactivateEmails(email.isGraduateEmail)
          email.active = true
        }
      }
      emailRepository.save(email)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_UPDATE_CI_PROGRAM)
    }
  }
}