package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CNPQLevelDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.CNPQLevel
import br.uff.graduatesapi.repository.CNPQLevelRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CNPQLevelService(
    private val cnpqLevelRepository: CNPQLevelRepository
) {
    fun findById(id: Int): ResponseResult<CNPQLevel> {
        val result = cnpqLevelRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.CNPQ_LEVEL_NOT_FOUND)
        return ResponseResult.Success(result)
    }

    fun findCNPQLevels(): ResponseResult<List<CNPQLevel>> {
        return try {
            val result = cnpqLevelRepository.findAllActives()
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_CNPQ_LEVELS)
        }
    }
    fun deleteCNPQLevel (id: Int): ResponseResult<Nothing?> {
        return try {
            cnpqLevelRepository.deleteById(id)
            ResponseResult.Success(null)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_DELETE_CNPQ_LEVEL)
        }
    }

    fun createLevel(levelDTO: CNPQLevelDTO): ResponseResult<Nothing?> {
        val level = CNPQLevel(level = levelDTO.level)
        return try {
            cnpqLevelRepository.save(level)
            ResponseResult.Success(null)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_CREATE_CNPQ_LEVEL)
        }
    }

    fun editLevel(levelDTO: CNPQLevelDTO, id: Int): ResponseResult<Nothing?> {
        return try {
            val result = this.findById(id)
            if (result is ResponseResult.Error)
                return ResponseResult.Error(result.errorReason)
            val level = result.data!!
            level.level = levelDTO.level
            cnpqLevelRepository.save(level)
            ResponseResult.Success(null)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_UPDATE_CNPQ_LEVEL)
        }
    }
}