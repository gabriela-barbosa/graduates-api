package br.uff.graduatesapi.service

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
        val result = cnpqLevelRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.CNPQSCHOLARSHIP_NOT_FOUND)
        return ResponseResult.Success(result)
    }

    fun findCNPQLevels(): List<CNPQLevel> {
        return cnpqLevelRepository.findAll()
    }
}