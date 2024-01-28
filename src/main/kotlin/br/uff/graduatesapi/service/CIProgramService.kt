package br.uff.graduatesapi.service

import br.uff.graduatesapi.dto.CIProgramDTO
import br.uff.graduatesapi.error.Errors
import br.uff.graduatesapi.error.ResponseResult
import br.uff.graduatesapi.model.CIProgram
import br.uff.graduatesapi.repository.CIProgramRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class CIProgramService(
    private val programRepository: CIProgramRepository,
) {
    fun findPrograms(): ResponseResult<List<CIProgram>> =
        try {
            val result = programRepository.findAllActives()
            ResponseResult.Success(result)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_RETRIEVE_CI_PROGRAMS)
        }


    fun findProgram(id: UUID): ResponseResult<CIProgram?> {
        val result = programRepository.findByIdOrNull(id) ?: return ResponseResult.Error(Errors.CI_PROGRAM_NOT_FOUND)
        return ResponseResult.Success(result)
    }

    fun findProgramByInitials(initials: String): ResponseResult<CIProgram> {
        val result = programRepository.findCIProgramByInitials(initials) ?: return ResponseResult.Error(Errors.CI_PROGRAM_NOT_FOUND)
        return ResponseResult.Success(result)
    }

    fun deleteProgram(id: UUID): ResponseResult<Nothing?> = try {
        programRepository.deleteById(id)
        ResponseResult.Success(null)
    } catch (err: Error) {
        ResponseResult.Error(Errors.CANT_DELETE_CI_PROGRAM)
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

    fun editProgram(ciProgramDTO: CIProgramDTO, id: UUID): ResponseResult<Nothing?> {
        return try {
            val ciProgram = when (val result = this.findProgram(id)) {
                is ResponseResult.Success -> result.data!!
                is ResponseResult.Error -> return ResponseResult.Error(Errors.INVALID_DATA)
            }
            ciProgram.initials = ciProgramDTO.initials
            programRepository.updateInitials(ciProgramDTO.initials, id)
            ResponseResult.Success(null)
        } catch (err: Error) {
            ResponseResult.Error(Errors.CANT_UPDATE_CI_PROGRAM)
        }
    }
}