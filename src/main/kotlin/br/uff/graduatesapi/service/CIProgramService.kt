package br.uff.graduatesapi.service

import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.CIProgram
import br.uff.graduatesapi.repository.CIProgramRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CIProgramService(
    private val programRepository: CIProgramRepository,
) {
    fun findById(id: Int): ResponseResult<CIProgram> {
        val result = programRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.CNPQSCHOLARSHIP_NOT_FOUND)
        return ResponseResult.Success(result)
    }

    fun findPrograms(): ResponseResult<List<CIProgram>> {
        return try {
            val result = programRepository.findAll()
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_CNPQ_LEVELS)
        }
    }

    fun deleteProgram (id: Int): ResponseResult<Nothing?> {
        return try {
            programRepository.deleteById(id)
            ResponseResult.Success(null)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_DELETE_CIPROGRAM)
        }
    }
}