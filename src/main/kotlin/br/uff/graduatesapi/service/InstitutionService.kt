package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.InstitutionDTO
import br.uff.graduatesapi.enums.InstitutionType
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.Institution
import br.uff.graduatesapi.repository.InstitutionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class InstitutionService(
    private val institutionRepository: InstitutionRepository,
) {
    fun findById(id: Int): Institution? {
        return institutionRepository.findByIdOrNull(id)
    }

    fun findByNameAndType(name: String, type: InstitutionType): ResponseResult<Institution> {
        val resultInstitution = institutionRepository.findByNameAndType(name, type)
            ?: return ResponseResult.Error(Errors.INSTITUTION_NOT_FOUND)
        return ResponseResult.Success(resultInstitution)
    }

    fun createInstitution(institution: Institution): ResponseResult<Institution> {
        val resultInstitution = findByNameAndType(institution.name, institution.type)
        if (resultInstitution is ResponseResult.Success) return resultInstitution
        return try {
            val respInstitution = institutionRepository.save(institution)
            ResponseResult.Success(respInstitution)
        } catch (err: java.lang.Exception) {
            ResponseResult.Error(Errors.CANT_CREATE_INSTITUTION)
        }
    }

    fun createInstitutionByInstitutionDTO(newInstitution: InstitutionDTO): ResponseResult<Institution> {
        val institution = Institution(
            name = newInstitution.name,
            type = newInstitution.type
        )
        val institutionSaved = this.createInstitution(institution)
        if (institutionSaved is ResponseResult.Error) return ResponseResult.Error(institutionSaved.errorReason)
        return institutionSaved
    }
}