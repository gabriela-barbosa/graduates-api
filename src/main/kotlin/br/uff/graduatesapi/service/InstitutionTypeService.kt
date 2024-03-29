package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CreateInstitutionTypeDTO
import br.uff.graduatesapi.dto.InstitutionTypeDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.InstitutionType
import br.uff.graduatesapi.repository.InstitutionTypeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class InstitutionTypeService(
    private val institutionTypeRepository: InstitutionTypeRepository,
) {
    fun findActiveTypes(): ResponseResult<List<InstitutionTypeDTO>> {
        return try {
            val result = institutionTypeRepository.findAllActives()
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_INSTITUTION_TYPES)
        }
    }

    fun deleteType(id: UUID): ResponseResult<Nothing?> {
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

    fun findById(id: UUID): ResponseResult<InstitutionType> {
        val result =
            institutionTypeRepository.findByIdOrNull(id)
                ?: return ResponseResult.Error(Errors.INSTITUTION_TYPE_NOT_FOUND)
        return ResponseResult.Success(result)
    }

    fun updateType(createInstitutionTypeDTO: CreateInstitutionTypeDTO, id: UUID): ResponseResult<Nothing?> {
        return try {

            val type = when (val result = this.findById(id)) {
                is ResponseResult.Success -> result.data!!
                is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
            }
            type.name = createInstitutionTypeDTO.name
            institutionTypeRepository.save(type)
            ResponseResult.Success(null)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_UPDATE_INSTITUTION_TYPE)
        }
    }

    fun findByName(name: String): ResponseResult<InstitutionType> {
        try {
            val institutionType =
                institutionTypeRepository.findByNameContainingIgnoreCase(name)
                    ?: return ResponseResult.Error(Errors.INSTITUTION_TYPE_NOT_FOUND, errorData = name)
            return ResponseResult.Success(institutionType)
        } catch (err: Error) {
            return ResponseResult.Error(Errors.CANT_RETRIEVE_INSTITUTION_TYPE)
        }
    }
}