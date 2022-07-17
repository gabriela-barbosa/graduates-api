package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CreateInstitutionTypeDTO
import br.uff.graduatesapi.dto.InstitutionTypeDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.InstitutionType
import br.uff.graduatesapi.repository.InstitutionTypeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class InstitutionTypeService(
  private val institutionTypeRepository: InstitutionTypeRepository,
) {
  fun findActiveTypes(): ResponseResult<List<InstitutionTypeDTO>> {
    return try {
      val result = institutionTypeRepository.findAllActives()
      ResponseResult.Success(result)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_RETRIEVE_INSTITUTION_TYPE)
    }
  }

  fun deleteType(id: Int): ResponseResult<Nothing?> {
    return try {
      institutionTypeRepository.deleteById(id)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_DELETE_INSTITUTION_TYPE)
    }
  }

  fun createType(createInstitutionTypeDTO: CreateInstitutionTypeDTO): ResponseResult<Nothing?> {
    val program = InstitutionType(name = createInstitutionTypeDTO.name)
    return try {
      institutionTypeRepository.save(program)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_CREATE_INSTITUTION_TYPE)
    }
  }

  fun findById(id: Int): ResponseResult<InstitutionType> {
    val result =
      institutionTypeRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.INSTITUTION_TYPE_NOT_FOUND)
    return ResponseResult.Success(result)
  }

  fun editType(createInstitutionTypeDTO: CreateInstitutionTypeDTO, id: Int): ResponseResult<Nothing?> {
    return try {
      val result = this.findById(id)
      if (result is ResponseResult.Error)
        return ResponseResult.Error(result.errorReason)
      val type = result.data!!
      type.name = createInstitutionTypeDTO.name
      institutionTypeRepository.save(type)
      ResponseResult.Success(null)
    } catch (err: Error) {
      ResponseResult.Error(Errors.CANT_UPDATE_INSTITUTION_TYPE)
    }
  }
}