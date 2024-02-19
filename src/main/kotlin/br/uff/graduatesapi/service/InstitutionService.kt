package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CreateInstitutionDTO
import br.uff.graduatesapi.dto.GetInstitutionsDTO
import br.uff.graduatesapi.entity.InstitutionFilters
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.repository.InstitutionRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class InstitutionService(
    private val institutionRepository: InstitutionRepository,
    private val institutionTypeService: InstitutionTypeService,
) {
    fun findById(id: UUID): ResponseResult<Institution> = institutionRepository.findByIdOrNull(id)
        ?.let { ResponseResult.Success(it) }
        ?: ResponseResult.Error(Errors.INSTITUTION_NOT_FOUND)

    fun deleteInstitution(id: UUID): ResponseResult<Nothing?> {
        return try {
            institutionRepository.deleteById(id)
            return ResponseResult.Success(null)
        } catch (ex: Exception) {
            ResponseResult.Error(Errors.INVALID_DATA)
        }
    }

    fun findAllByFilters(filters: InstitutionFilters, pageable: Pageable): ResponseResult<GetInstitutionsDTO> {
        return try {
            val resultInstitution =
                institutionRepository.findAllByFilters(filters, pageable)
            ResponseResult.Success(resultInstitution)
        } catch (ex: Exception) {
            ResponseResult.Error(Errors.INVALID_DATA)
        }
    }

    fun findByNameAndType(name: String, type: UUID): ResponseResult<Institution> {
        val resultInstitution = institutionRepository.findByNameAndTypeId(name, type)
            ?: return ResponseResult.Error(Errors.INSTITUTION_NOT_FOUND)
        return ResponseResult.Success(resultInstitution)
    }

    fun findByName(name: String): ResponseResult<Institution> {
        val resultInstitution = institutionRepository.findFirstByNameIgnoreCase(name)
            ?: return ResponseResult.Error(Errors.INSTITUTION_NOT_FOUND)
        return ResponseResult.Success(resultInstitution)
    }

    fun createInstitution(institutionDTO: CreateInstitutionDTO): ResponseResult<Institution> {

        val institutionFound = if (institutionDTO.id != null) {
            findById(institutionDTO.id)
        } else {
            findByNameAndType(institutionDTO.name, institutionDTO.typeId)
        }.takeIf { it is ResponseResult.Success }

        if (institutionFound != null) return ResponseResult.Success(institutionFound.data!!)

        return try {
            val institutionType = when (val resultLevel = institutionTypeService.findById(institutionDTO.typeId)) {
                is ResponseResult.Success -> resultLevel.data!!
                is ResponseResult.Error -> return ResponseResult.Error(Errors.INSTITUTION_TYPE_NOT_FOUND, errorCode = HttpStatus.UNPROCESSABLE_ENTITY )
            }
            val institution = Institution(
                name = institutionDTO.name,
                type = institutionType
            )
            val respInstitution = institutionRepository.save(institution)
            ResponseResult.Success(respInstitution)
        } catch (err: Exception) {
            ResponseResult.Error(Errors.CANT_CREATE_INSTITUTION, errorData = institutionDTO.name)
        }
    }

    fun updateInstitution(createInstitutionDTO: CreateInstitutionDTO, id: UUID): ResponseResult<Nothing?> {
        return try {

            val institution = when (val result = this.findById(id)) {
                is ResponseResult.Success -> result.data!!
                is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)

            }
            institution.name = createInstitutionDTO.name

            if (institution.type.id != createInstitutionDTO.typeId) {
                val institutionType =
                    when (val resultLevel = institutionTypeService.findById(createInstitutionDTO.typeId)) {
                        is ResponseResult.Success -> resultLevel.data!!
                        is ResponseResult.Error -> return ResponseResult.Error(Errors.INSTITUTION_TYPE_NOT_FOUND)
                    }
                institution.type = institutionType
            }

            institutionRepository.save(institution)
            ResponseResult.Success(null)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_UPDATE_INSTITUTION_TYPE)
        }
    }

}