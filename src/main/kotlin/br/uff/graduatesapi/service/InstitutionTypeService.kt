package br.uff.graduatesapi.service

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
    fun findById(id: Int): ResponseResult<InstitutionType> {
        val result = institutionTypeRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.INSTITUTION_TYPE_NOT_FOUND)
        return ResponseResult.Success(result)
    }
}