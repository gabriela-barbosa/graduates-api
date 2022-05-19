package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CIProgramDTO
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
    fun findPrograms(): ResponseResult<List<CIProgram>> {
        return try {
            val result = programRepository.findAllActives()
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_CI_PROGRAMS)
        }
    }

    fun deleteProgram (id: Int): ResponseResult<Nothing?> {
        return try {
            programRepository.deleteById(id)
            ResponseResult.Success(null)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_DELETE_CI_PROGRAM)
        }
    }

    fun createProgram(ciProgramDTO: CIProgramDTO): ResponseResult<Nothing?> {
        val program = CIProgram(initials = ciProgramDTO.initials)
        return try {
            programRepository.save(program)
            ResponseResult.Success(null)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_CREATE_CI_PROGRAM)
        }
    }
}