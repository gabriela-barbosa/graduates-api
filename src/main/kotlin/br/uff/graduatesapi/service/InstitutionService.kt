package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CreateInstitutionDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.repository.InstitutionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class InstitutionService(
  private val institutionRepository: InstitutionRepository,
  private val institutionTypeService: InstitutionTypeService,
) {
  fun findById(id: UUID): Institution? {
    return institutionRepository.findByIdOrNull(id)
  }

  fun deleteInstitution(id: UUID): ResponseResult<Nothing?> {
    return try {
      institutionRepository.deleteById(id)
      return ResponseResult.Success(null)
    } catch (ex: Exception) {
      ResponseResult.Error(Errors.INVALID_DATA)
    }
  }

  fun findByNameAndType(name: String, type: UUID): ResponseResult<Institution> {
    val resultInstitution = institutionRepository.findByNameAndTypeId(name, type)
      ?: return ResponseResult.Error(Errors.INSTITUTION_NOT_FOUND)
    return ResponseResult.Success(resultInstitution)
  }

  fun createInstitution(institution: Institution): ResponseResult<Institution> {
    val resultInstitution = findByNameAndType(institution.name, institution.type.id)
    if (resultInstitution is ResponseResult.Success) return resultInstitution
    return try {
      val respInstitution = institutionRepository.save(institution)
      ResponseResult.Success(respInstitution)
    } catch (err: Exception) {
      ResponseResult.Error(Errors.CANT_CREATE_INSTITUTION)
    }
  }

  fun createInstitutionByInstitutionDTO(newInstitution: CreateInstitutionDTO): ResponseResult<Institution> {

    val institutionFound =
      findByNameAndType(newInstitution.name, newInstitution.typeId).takeIf { it is ResponseResult.Success }

    if (institutionFound != null) return ResponseResult.Success(institutionFound.data!!)

    val institutionType = when (val resultLevel = institutionTypeService.findById(newInstitution.typeId)) {
      is ResponseResult.Success -> resultLevel.data!!
      is ResponseResult.Error -> return ResponseResult.Error(Errors.INSTITUTION_TYPE_NOT_FOUND)
    }

    val institution = Institution(
      name = newInstitution.name,
      type = institutionType
    )

    val institutionSaved = createInstitution(institution)
    if (institutionSaved is ResponseResult.Error) return ResponseResult.Error(institutionSaved.errorReason)
    return institutionSaved
  }
}