package br.uff.graduatesapi.service

import br.uff.graduatesapi.Utils
import br.uff.graduatesapi.dto.CreatePostDoctorateDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.PostDoctorate
import br.uff.graduatesapi.repository.PostDoctorateRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class PostDoctorateService(
  private val postDoctorateRepository: PostDoctorateRepository,
  private val institutionService: InstitutionService
) {
  fun findById(id: UUID): ResponseResult<PostDoctorate> {
    val result =
      postDoctorateRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.POST_DOCTORATE_NOT_FOUND)
    return ResponseResult.Success(result)
  }

  fun updatePostDoctorate(
    id: UUID,
    postDoctorateDTO: CreatePostDoctorateDTO,
  ): ResponseResult<PostDoctorate> {

    val oldPostDoctorate = when (val result = findById(id)) {
      is ResponseResult.Success -> result.data!!
      is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
    }

    val institution =
      when (val result = institutionService.createInstitutionByInstitutionDTO(postDoctorateDTO.institution)) {
        is ResponseResult.Success -> result.data!!
        is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
      }

    oldPostDoctorate.institution = institution
    oldPostDoctorate.updatedAt = LocalDateTime.now()
    if (oldPostDoctorate.startedAt.toString() != postDoctorateDTO.startedAt)
      oldPostDoctorate.startedAt = Utils.parseUTCToLocalDateTime(postDoctorateDTO.startedAt)
    if (oldPostDoctorate.endedAt?.toString() != postDoctorateDTO.endedAt)
      oldPostDoctorate.endedAt = postDoctorateDTO.endedAt?.let { Utils.parseUTCToLocalDateTime(it) }

    return try {
      ResponseResult.Success(postDoctorateRepository.save(oldPostDoctorate))
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.INVALID_DATA)
    }
  }

  fun createPostDoctorate(
    graduate: Graduate,
    postDoctorateDTO: CreatePostDoctorateDTO,
  ): ResponseResult<PostDoctorate> {

    val institution =
      when (val result = institutionService.createInstitutionByInstitutionDTO(postDoctorateDTO.institution)) {
        is ResponseResult.Success -> result.data!!
        is ResponseResult.Error -> return ResponseResult.Error(result.errorReason)
      }

    val postDoctorate = PostDoctorate(
      graduate = graduate,
      institution = institution,
      startedAt = Utils.parseUTCToLocalDateTime(postDoctorateDTO.startedAt),
      endedAt = postDoctorateDTO.endedAt?.let { Utils.parseUTCToLocalDateTime(it) },
      updatedAt = LocalDateTime.now(),
    )

    return try {
      ResponseResult.Success(postDoctorateRepository.save(postDoctorate))
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.INVALID_DATA)
    }
  }
}